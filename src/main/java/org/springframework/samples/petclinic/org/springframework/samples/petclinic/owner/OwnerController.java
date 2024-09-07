package org.springframework.samples.petclinic.org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
class OwnerController {
	
	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	
	private final OwnerRepository owners;
	
	public OwnerController(OwnerRepository clinicService) {
		this.owners = clinicService;
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder detaBinder) {
		//クライアント側からの入力でidが不正に書き換えられないようにしている
		detaBinder.setDisallowedFields("id");
	}
	
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		//ownerIdがnullの場合、新しくOwnerエンティティを生成する。nullではない場合、ownerIdを使用してfindByIdを呼び出し、その結果を返却する
		return ownerId == null ? new Owner() : this.owners.findById(ownerId);
	}
	
	@GetMapping("/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		//画面側へOwnerオブジェクトを渡す
		model.put("owner", owner);
		//定数VIEWS_OWNER_CREATE_OR_UPDATE_FORMに定められている文字列を返却する
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}
	
	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		//バリデーションエラーに引っかかった場合
		if (result.hasErrors()) {
			//リダイレクト先に文字列を送る文字列の設定
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			//定数VIEWS_OWNER_CREATE_OR_UPDATE_FORMに定められている文字列を返却する
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		//OwnerRepositoryのsaveを呼び出す
		this.owners.save(owner);
		//リダイレクト先に送る文字列を設定する
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		//リダイレクトを行う
		return "redirect:/owners/" + owner.getId();
	}
	
	@GetMapping("/owners/find")
	public String initFindForm() {
		//findOwners.htmlを返却する
		return "owners/findOwners";
	}
	
	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result, Model model) {
		// OwnerエンティティのLastNameがnullの場合
		if (owner.getLastName() == null) {
			//LastNameに""を設定する
			owner.setLastName("");
		}
		
		//findPaginatedForOwnersLastNameで検索した値がownersResultsに入る
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, owner.getLastName());
		//ownersResultsが空だった場合
		if (ownersResults.isEmpty()) {
			//バリデーションメッセージにキーと紐づけて登録
			result.rejectValue("lastName","notFound", "not found");
			//バリデーションメッセージありの状態で表示する
			return "owners/findOwners";
		}
		
		// 取得した検索結果の要素が1の場合
		if (ownersResults.getTotalElements() == 1) {
			//iterator().next();で最初の要素を取得する。※get(0)等を使わない理由はPage等ではそれで取得が出来ないから
			owner = ownersResults.iterator().next();
			//OwnerエンティティクラスのIDを足してリダイレクトを行う。
			return "redirect:/owners/" + owner.getId();
		}
		// 上記以外はaddPaginationModelを呼び出す
		return addPaginationModel(page, model, ownersResults);
	}
	
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		//PageのコンテンツをLISTとして取得する
		List<Owner> listOwners = paginated.getContent();
		//ページ情報を画面側へ渡す
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}
	
	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		//ページ毎に表示する数
		int pageSize = 5;
		//pageableの設定
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		//OwnerRepositoryのfindByLastNameで検索
		return owners.findByLastName(lastname, pageable);
	}
	
	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		////OwnerRepositoryのfindByIdで検索
		Owner owner = this.owners.findById(ownerId);
		//検索したOwnerエンティティの情報を画面に渡す
		model.addAttribute(owner);
		//定数VIEWS_OWNER_CREATE_OR_UPDATE_FORMに定められている文字列を返却する
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}
	
	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		//バリデーションエラーが発生した場合
		if(result.hasErrors()) {
			//リダイレクト先にメッセージを渡す
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		//Ownerエンティティに引数のownerIdをセットする
		owner.setId(ownerId);
		//OwnerRepositoryクラスのsaveを呼び出す
		this.owners.save(owner);
		//リダイレクト先にメッセージを渡す
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		//OwnerRepositoryのfindByIdで検索
		Owner owner = this.owners.findById(ownerId);
		//画面側に取得した情報を渡す
		mav.addObject(owner);
		return mav;
	}

}

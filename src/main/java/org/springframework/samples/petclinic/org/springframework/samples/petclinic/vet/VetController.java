package org.springframework.samples.petclinic.org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class VetController {
	
	private final VetRepository vetRepository;
	
	public VetController(VetRepository clinicService) {
		this.vetRepository = clinicService;
	}
	
	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		// @RequestParam(defaultValue = "1")デフォルトはpage1が設定されてくる
		Vets vets = new Vets();
		//指定されたページの獣医師情報を取得する
		Page<Vet> paginated = findPaginated(page);
		//取得したpaginatedをLISTに変換してXMLにシリアライズ（変換）する
		vets.getVetList().addAll(paginated.toList());
		return addPaginationModel(page, paginated, model);
	}
	
	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		//PageのコンテンツをLISTとして取得する
		List<Vet> listVets = paginated.getContent();
		//ページの情報を画面側へ渡す
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		return "vets/vetList";
	}
	
	private Page<Vet> findPaginated(int page) {
		//1ページに表示する件数
		int pageSize = 5;
		//pageableの作成。インデックスは0から始めるのでpage-1を行っている
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		//指定されたページの獣医師情報を取得する
		//例：pageに2がセットされた場合、SELECT * FROM vets LIMIT 5 OFFSET 5;
		return vetRepository.findAll(pageable);
	}
	
	@GetMapping({"/vets"})
	public @ResponseBody Vets showResourcesVetList() {
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}

}

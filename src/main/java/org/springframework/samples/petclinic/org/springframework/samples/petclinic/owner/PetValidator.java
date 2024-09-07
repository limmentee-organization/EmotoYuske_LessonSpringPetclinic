package org.springframework.samples.petclinic.org.springframework.samples.petclinic.owner;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PetValidator implements Validator {
	
	private static final String REQUIRED = "required";
	
	@Override
	public void validate(Object obj, Errors errors) {
		Pet pet = (Pet) obj;
		String name = pet.getName();
		//nameにテキストが含まれていない場合
		if (!StringUtils.hasText(name)) {
			//エラーの登録を行う
			errors.rejectValue("name", REQUIRED, REQUIRED);
		}
		//Petクラスのエンティティにidがないかつ、petTypeがnullの場合
		if (pet.isNew() && pet.getType() == null) {
			//エラーの登録を行う
			errors.rejectValue("type", REQUIRED, REQUIRED);
		}
		//PetクラスのエンティティのBirthDateがnullの場合
		if (pet.getBirthDate() == null) {
			//エラーの登録を行う
			errors.rejectValue("birthDate", REQUIRED, REQUIRED);
		}
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		//検証対象（引数で渡されてきたクラス）がPetクラスまたはPetクラスを継承したものかをチェックする
		return Pet.class.isAssignableFrom(clazz);
	}
}

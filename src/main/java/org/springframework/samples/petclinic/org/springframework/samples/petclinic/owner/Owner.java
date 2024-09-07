package org.springframework.samples.petclinic.org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "owners")
public class Owner extends Person {
	
	@Column(name = "address")
	@NotBlank
	private String address;
	
	@Column(name = "city")
	@NotBlank
	private String city;
	
	@Column(name ="telephone")
	@NotBlank
	@Digits(fraction = 0, integer = 10)
	private String telephone;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private List<Pet> pets = new ArrayList<>();
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public List<Pet> getPets() {
		return this.pets;
	}
	
	public void addPet(Pet pet) {
		if (pet.isNew()) {
			getPets().add(pet);
		}
	}
	
	public Pet getPet(String name) {
		return getPet(name, false);
	}
	
	public Pet getPet(Integer id) {
		//PetクラスのListから繰り返し取り出して変数petに入れる
		for (Pet pet : getPets()) {
			//petIdがnullではない場合
			if (!pet.isNew()) {
				//petIdを変数compIdに代入する
				Integer compId = pet.getId();
				//compIdと引数のidが一致する場合
				if (compId.equals(id)) {
					//変数petの値を返却する
					return pet;
				}
			}
		}
		//それ以外はnullを返却する
		return null;
	}
	
	public Pet getPet(String name, boolean ignoreNew) {
		//引数のnameを小文字に変換する
		name = name.toLowerCase();
		//PetクラスのListから繰り返し取り出して変数petに入れる
		for (Pet pet :getPets()) {
			//nameを変数compNameへ代入する
			String compName = pet.getName();
			//compNameがnullではないかつ、compNameとnameが一致する（大文字小文字わけない）場合
			if (compName != null && compName.equalsIgnoreCase(name)) {
				//引数ignoreNewがfalseまたはペットクラスのIdがnullではない場合
				if(!ignoreNew || !pet.isNew()) {
					//変数petの値を返却する
					return pet;
				}
			}
		}
		//それ以外はnullを返却する
		return null;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.getId())
				.append("new", this.isNew())
				.append("lastName", this.getLastName())
				.append("firstName", this.getFirstName())
				.append("address", this.address)
				.append("city",this.city)
				.append("telephone", this.telephone)
				.toString();
	}
	
	public void addVisit(Integer petId, Visit visit) {
		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");
		
		Pet pet = getPet(petId);
		
		Assert.notNull(pet, "Invalid Pet identifier!");
		
		pet.addVisit(visit);
		
	}
}


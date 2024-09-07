package org.springframework.samples.petclinic.org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OwnerRepository extends Repository<Owner, Integer>{
	
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	//PetTypeをnameのアルファベットの昇順で取得する
	@Transactional(readOnly = true)
	//読み取り専用であることを指定
	List<PetType> findPetTypes();
	
	@Query("SELECT DISTINCT owner FROM Owner owner left join owner.pets WHERE owner.lastName LIKE :lastName% ")
	//重複を除外して検索する。petsの情報もあれば取得する。検索条件は引数で渡されるlastNameで始まる文字列のデータを取得する
	@Transactional(readOnly = true)
	//読み取り専用であることを指定
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);
	
	
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	//Ownerエンティティを検索して取得する。petsの情報もあれば一緒のクエリで取得する。検索条件は引数のidと一致するデータを取得する
	@Transactional(readOnly = true)
	//読み取り専用であることを指定
	Owner findById(@Param("id") Integer id);
	
	void save(Owner owner);
}

package com.tablekok.store_service.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_store_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(mappedBy = "categories")
	private List<Store> stores = new ArrayList<>();

	public void addStore(Store store) {
		this.stores.add(store);
		if (!store.getCategories().contains(this)) {
			store.getCategories().add(this);
		}
	}
	
	@Builder(access = AccessLevel.PRIVATE)
	private Category(
		String name) {
		this.name = name;
	}

	public static Category of(String name) {
		return Category.builder()
			.name(name)
			.build();
	}

}

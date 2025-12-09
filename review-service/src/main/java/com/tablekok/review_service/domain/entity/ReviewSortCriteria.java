package com.tablekok.review_service.domain.entity;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortCriteria {

	NEWEST("createdAt", Sort.Direction.DESC),
	OLDEST("createdAt", Sort.Direction.ASC),
	RATING_HIGH("rating", Sort.Direction.DESC),
	RATING_LOW("rating", Sort.Direction.ASC);

	private final String property;
	private final Sort.Direction direction;
}

package com.tablekok.search_service.domain.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {

	NAME("name.keyword"),
	RATING("averageRating"),
	REVIEW("reviewCount");

	private final String value;
}

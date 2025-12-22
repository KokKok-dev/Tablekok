package com.tablekok.search_service.infrastructure.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.document.StoreStatus;

import co.elastic.clients.elasticsearch._types.FieldValue;

@Component
public class StoreSearchQueryFactory {

	public Query createCategorySearchQuery(
		String categoryId,
		SortType sortType,
		Object cursor,
		String cursorId,
		int limit
	) {
		NativeQueryBuilder queryBuilder = NativeQuery.builder();

		// 조건절 구성 (Filter)
		queryBuilder.withQuery(buildFilterQuery(categoryId));

		// 정렬 구성 (Sort)
		queryBuilder.withSort(buildSort(sortType));

		// 커서 구성 (Search After)
		if (cursor != null && cursorId != null) {
			queryBuilder.withSearchAfter(List.of(cursor, cursorId));
		}

		// 페이지 크기
		queryBuilder.withMaxResults(limit);

		return queryBuilder.build();
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query buildFilterQuery(String categoryId) {

		// Enum을 순회하며 검색 가능한 상태만 필터링 -> FieldValue 리스트로 변환
		List<FieldValue> searchableStatuses = Arrays.stream(StoreStatus.values())
			.filter(StoreStatus::isSearchable) // true인 상태만 필터링
			.map(status -> FieldValue.of(status.name())) // String -> FieldValue 변환
			.toList();

		// 쿼리 생성
		return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q
			.bool(b -> b
				.filter(f -> f.term(t -> t.field("categoryIds").value(categoryId)))
				.filter(f -> f.terms(t -> t.field("status")
					.terms(ts -> ts.value(searchableStatuses)) // 변환된 FieldValue 리스트 주입
				))
			)
		);
	}

	private Sort buildSort(SortType sortType) {
		// 별점 높은순
		if (sortType == SortType.RATING) {
			return Sort.by(Sort.Order.desc("averageRating"), Sort.Order.asc("storeId"));
		}
		// 리뷰 많은순
		if (sortType == SortType.REVIEW) {
			return Sort.by(Sort.Order.desc("reviewCount"), Sort.Order.asc("storeId"));
		}
		// 이름순
		String sortField = sortType.getValue();
		if ("name".equals(sortField)) {
			sortField = "name.keyword"; // "name" -> "name.keyword"로 변경
		}

		return Sort.by(Sort.Order.asc(sortField), Sort.Order.asc("storeId"));
	}
}

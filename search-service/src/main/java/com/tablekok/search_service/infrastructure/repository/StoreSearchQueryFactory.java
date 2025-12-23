package com.tablekok.search_service.infrastructure.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.document.StoreStatus;
import com.tablekok.search_service.domain.vo.StoreSearchCriteria;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;

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
			sortField = "name.keyword";
		}

		return Sort.by(Sort.Order.asc(sortField), Sort.Order.asc("storeId"));
	}

	// 검색
	public Query createKeywordSearchQuery(StoreSearchCriteria criteria, int limit) {
		NativeQueryBuilder queryBuilder = NativeQuery.builder();

		queryBuilder.withQuery(buildSearchQuery(criteria.keyword()));

		if (criteria.sortType() != null) {
			queryBuilder.withSort(buildSort(criteria.sortType()));
		}

		CursorRequest<String> cursorRequest = criteria.cursorRequest();

		if (cursorRequest.hasKey()) {
			queryBuilder.withSearchAfter(List.of(
				cursorRequest.cursor(),
				cursorRequest.cursorId()
			));
		}

		queryBuilder.withMaxResults(limit);

		return queryBuilder.build();
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.Query buildSearchQuery(String keyword) {
		return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q -> q
			.bool(b -> b
				// 운영 중인 상태만 필터링 (기존 로직 재사용 권장)
				.filter(f -> f
					.terms(t -> t.field("status")
						.terms(ts -> ts.value(getSearchableStatusList()))
					)
				)
				// 키워드가 있으면 MultiMatch, 없으면 전체 조회(matchAll)
				.must(m -> {
					if (keyword == null || keyword.isBlank()) {
						return m.matchAll(ma -> ma);
					}
					return m.multiMatch(mm -> mm
							.query(keyword)
							// 검색할 필드 목록과 가중치 설정
							.fields(List.of(
								"name^2.0",
								"categories^1.5",
								"description",
								"address"
							))
					);
				})
			)
		);
	}

	private List<FieldValue> getSearchableStatusList() {
		return Arrays.stream(StoreStatus.values())
			.filter(StoreStatus::isSearchable)
			.map(status -> FieldValue.of(status.name()))
			.toList();
	}

	public Query createAutocompleteQuery(String keyword) {
		return NativeQuery.builder()
			.withQuery(q -> q
				.multiMatch(m -> m
					.query(keyword)
					.fields(
						"name.autocomplete",
						"name.autocomplete._2gram",
						"name.autocomplete._3gram"
					)
					.type(TextQueryType.BoolPrefix)
				)
			)
			.withSourceFilter(
				new FetchSourceFilter(true, new String[]{"name"}, new String[]{})
			)
			.withMaxResults(10)
			.build();
	}
}

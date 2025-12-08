package com.tablekok.review_service.application.service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.tablekok.review_service.application.dto.result.CursorResult;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

@Component
public class CursorResultFactory {

	public <E extends Review, T> CursorResult<T> create(
		Page<E> page,
		int size,
		ReviewSortCriteria criteria,
		Function<E, T> mapper
	) {
		List<E> entities = page.getContent();
		boolean hasNext = entities.size() > size;

		List<E> contentEntities = hasNext ? entities.subList(0, size) : entities;

		List<T> resultList = contentEntities.stream()
			.map(mapper)
			.toList();

		UUID nextCursorId = null;
		String nextCursor = null;

		if (!contentEntities.isEmpty()) {
			E lastEntity = contentEntities.get(contentEntities.size() - 1);
			nextCursorId = lastEntity.getId();

			if (criteria == ReviewSortCriteria.RATING_HIGH || criteria == ReviewSortCriteria.RATING_LOW) {
				nextCursor = String.valueOf(lastEntity.getRating());
			}
			if (criteria == ReviewSortCriteria.NEWEST ||  criteria == ReviewSortCriteria.OLDEST) {
				nextCursor = String.valueOf(lastEntity.getCreatedAt());
			}
		}

		return new CursorResult<>(resultList, nextCursorId, nextCursor, hasNext);
	}
}

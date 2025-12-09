package com.tablekok.review_service.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tablekok.review_service.domain.entity.QReview;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQuerydslRepositoryAdapter implements ReviewQuerydslRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final QReview review = QReview.review;

	@Override
	public Page<Review> findReviewsByStoreId(
		UUID storeId,
		ReviewSortCriteria sortBy,
		String cursor,
		UUID cursorId,
		Pageable pageable
	) {
		BooleanExpression cursorCondition = createCursorCondition(sortBy, cursor, cursorId);

		List<Review> contents = jpaQueryFactory.selectFrom(review)
			.where(
				review.storeId.eq(storeId),
				cursorCondition,
				review.deletedAt.isNull()
			)
			.orderBy(getOrderSpecifier(sortBy))
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(contents, pageable, 0);
	}

	@Override
	public Page<Review> findReviewsByUserId(UUID userId,
		String cursor,
		UUID cursorId,
		Pageable pageable
	) {
		ReviewSortCriteria criteria = ReviewSortCriteria.NEWEST;
		BooleanExpression cursorCondition = createCursorCondition(criteria, cursor, cursorId);

		List<Review> contents = jpaQueryFactory.selectFrom(review)
			.where(
				review.userId.eq(userId),
				cursorCondition,
				review.deletedAt.isNull()
			)
			.orderBy(getOrderSpecifier(criteria))
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(contents, pageable, 0);
	}

	private BooleanExpression createCursorCondition(ReviewSortCriteria criteria, String cursor, UUID cursorId) {
		if (cursor == null || cursorId == null) return null;

		if (criteria == ReviewSortCriteria.RATING_HIGH) {
			Double rating = Double.parseDouble(cursor);
			return review.rating.lt(rating).or(review.rating.eq(rating).and(review.id.lt(cursorId))); // 내림차순 가정
		}
		if (criteria == ReviewSortCriteria.RATING_LOW) {
			Double rating = Double.parseDouble(cursor);
			return review.rating.gt(rating).or(review.rating.eq(rating).and(review.id.gt(cursorId)));
		}
		if (criteria == ReviewSortCriteria.OLDEST) {
			LocalDateTime date = LocalDateTime.parse(cursor);
			return review.createdAt.gt(date).or(review.createdAt.eq(date).and(review.id.gt(cursorId)));
		}
		// 최신 순 (기본값 / 내림차순: 작거나 같음)
		// 위 if문들에 해당하지 않으면 기본적으로 NEWEST 로직을 수행합니다.
		LocalDateTime date = LocalDateTime.parse(cursor);
		// createdAt < cursor OR (createdAt == cursor AND id < cursorId)
		return review.createdAt.lt(date)
			.or(review.createdAt.eq(date).and(review.id.lt(cursorId)));
	}

	private OrderSpecifier<?> getOrderSpecifier(ReviewSortCriteria criteria) {
		if (criteria == ReviewSortCriteria.RATING_HIGH) return review.rating.desc();
		if (criteria == ReviewSortCriteria.RATING_LOW) return review.rating.asc();
		if (criteria == ReviewSortCriteria.OLDEST) return review.createdAt.asc();
		return review.createdAt.desc();
	}
}

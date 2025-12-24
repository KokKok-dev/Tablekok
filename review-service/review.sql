DO
$$
    DECLARE
        target_store_id UUID := '8f3c6a5e-9e84-4c3f-9b80-7c2dd1a4e2c1';
        target_user_id  UUID := 'c1270f2d-f1ed-49c5-bc15-452e7c5f4078';
    BEGIN
        -------------------------------------------------------
        -- 랜덤 데이터 생성 (100만 건)
        -------------------------------------------------------
        INSERT INTO p_review (
            review_id, store_id, user_id, reservation_id,
            content, rating, created_at, created_by, updated_at, updated_by
        )
        SELECT
            gen_random_uuid(),               -- Review ID
            gen_random_uuid(),               -- 랜덤 Store ID
            gen_random_uuid(),               -- 랜덤 User ID
            gen_random_uuid(),               -- 랜덤 Reservation ID
            '배경 데이터 ' || i,
            floor(random() * 5) + 1,         -- 1~5점
            -- ✨ 수정된 부분: 365일 기간에 0~1 난수를 곱하는 방식
            NOW() - ('365 days'::interval * random()),
            target_user_id,
            NOW(),
            target_user_id
        FROM generate_series(1, 1000000) AS i; -- 100만 건 생성

        -------------------------------------------------------
        -- 타겟 데이터 생성 (5,000건)
        -- 타겟 가게에 대한 정렬 테스트용 데이터
        -------------------------------------------------------
        INSERT INTO p_review (
            review_id, store_id, user_id, reservation_id,
            content, rating, created_at, created_by, updated_at, updated_by
        )
        SELECT
            gen_random_uuid(),
            target_store_id,                 -- ★ 타겟 가게
            target_user_id,
            gen_random_uuid(),
            '타겟 가게 리뷰 ' || i,
            floor(random() * 5) + 1,
            -- 여기는 순차적인 시간 생성이 목적이므로 정수형 연산 유지
            NOW() - (i || ' minutes')::interval,
            target_user_id,
            NOW(),
            target_user_id
        FROM generate_series(1, 5000) AS i;

    END
$$;
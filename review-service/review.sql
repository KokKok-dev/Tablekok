DO $$
    DECLARE
i INT := 1;
        -- 1. 조회 테스트를 수행할 실제 가게 ID와 유저 ID를 입력하세요.
        target_store_id UUID := '8f3c6a5e-9e84-4c3f-9b80-7c2dd1a4e2c1';
        target_user_id UUID := '641f6c00-6ea3-46dc-875c-aeec53ea8677';
BEGIN
        -- 50개의 리뷰 데이터 생성
        WHILE i <= 50 LOOP
                INSERT INTO p_review (
                    review_id,
                    store_id,
                    user_id,
                    reservation_id,  -- ✨ 중요: 유니크 제약 회피를 위한 랜덤 UUID
                    content,
                    rating,
                    created_at,
                    created_by,
                    updated_at,
                    updated_by,
                    deleted_at
                ) VALUES (
                             gen_random_uuid(),              -- Review ID
                             target_store_id,
                             target_user_id,
                             gen_random_uuid(),              -- ✨ Reservation ID (가짜지만 Unique함)
                             '테스트 리뷰 데이터입니다. 번호: ' || i,
                             (floor(random() * 5) + 1)::double precision,    -- 1.0 ~ 5.0 랜덤 별점
                             NOW() - (i || ' minutes')::interval, -- 정렬 테스트를 위해 시간을 1분씩 과거로
                             NULL,
                             NOW(),
                             NULL,
                             NULL
                         );
                i := i + 1;
END LOOP;
END $$;
-- =============================================================================
-- 인기예약 중복 방지 — DB 최종 안전망 (분산락 + DB 제약 이중 방어)
-- =============================================================================
-- 분산락(Redisson)은 락 만료 / TOCTOU 상황에서 중복을 100% 막지 못한다.
-- 따라서 DB 부분 유니크 인덱스로 "활성 예약"의 슬롯 유일성을 원자적으로 보장한다.
--   - 락       : 요청 단계 경합 감소 (성능)
--   - DB 인덱스 : 어떤 경우에도 뚫리지 않는 최종 펜스 (정합성)
--
-- 활성 예약 = 취소(CANCELED) / 거절(REJECT) / 소프트삭제(deleted_at) 가 아닌 예약
--   → 취소·거절된 슬롯은 재예약이 가능해야 하므로 유일성 판정에서 제외한다.
--
-- 적용 (현재 수동 1회 실행, Flyway 전환은 개선 과제):
--   psql -U postgres -d tk-reservation -f add_active_reservation_unique_index.sql
-- =============================================================================

CREATE UNIQUE INDEX IF NOT EXISTS uq_active_reservation_slot
    ON p_reservation (store_id, reservation_date, reservation_time)
    WHERE deleted_at IS NULL
      AND reservation_status NOT IN ('CANCELED', 'REJECT');

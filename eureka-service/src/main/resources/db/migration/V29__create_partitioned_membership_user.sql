-- 기존 테이블 이름 변경
RENAME TABLE p_membership_user TO p_membership_user_legacy;

-- 파티션 테이블 재생성
CREATE TABLE p_membership_user (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   user_id BIGINT NOT NULL,
                                   membership_id BIGINT NOT NULL,
                                   is_active BOOLEAN DEFAULT TRUE NOT NULL,
                                   is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
                                   deleted_by BIGINT NULL,
                                   deleted_at DATETIME NULL,
                                   created_by BIGINT NULL,
                                   created_at DATETIME NULL,
                                   updated_by BIGINT NULL,
                                   updated_at DATETIME NULL,
                                   season YEAR NOT NULL,
                                   PRIMARY KEY (id, season),
                                   UNIQUE KEY uk_user_season (user_id, season)
)
    PARTITION BY LIST (season) (
    PARTITION p2024 VALUES IN (2024),
    PARTITION p2025 VALUES IN (2025)
);

-- 필요한 데이터 마이그레이션
INSERT INTO p_membership_user (
    id, user_id, membership_id, is_active, is_deleted,
    deleted_by, deleted_at, created_by, created_at,
    updated_by, updated_at, season
)
SELECT
    id, user_id, membership_id, is_active, is_deleted,
    deleted_by, deleted_at, created_by, created_at,
    updated_by, updated_at,
    2025
FROM p_membership_user_legacy;

-- 기존 테이블 삭제
DROP TABLE p_membership_user_legacy;

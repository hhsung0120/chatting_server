drop table permission;
drop table role;
drop table chatting_message;
drop table users;
drop table black_list;
drop table chatting_room_members;
drop table chatting_room;
drop table categories;

-- ---------------------------------------------------------------------------------------------------------------------
-- 카테고리
CREATE TABLE categories (
                            seq               TINYINT     NOT NULL COMMENT '시퀀스', -- 시퀀스
                            category_name     VARCHAR(50) NOT NULL COMMENT '카테고리 이름', -- 카테고리 이름
                            order_number      TINYINT     NOT NULL COMMENT '화면 순서', -- 순서 번호
                            created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                            created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                            modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                            modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '카테고리';

-- 카테고리
ALTER TABLE categories
    ADD CONSTRAINT PK_categories -- 카테고리 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

-- 카테고리 유니크 인덱스
CREATE UNIQUE INDEX UIX_categories
    ON categories ( -- 카테고리
                   category_name ASC -- 카테고리 이름
        );

ALTER TABLE categories
    MODIFY COLUMN seq TINYINT NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE categories
    AUTO_INCREMENT = 0;

-- ---------------------------------------------------------------------------------------------------------------------

-- 채팅방
CREATE TABLE chatting_room (
                               seq                             INTEGER      NOT NULL COMMENT '시퀀스', -- 시퀀스
                               category_seq                    TINYINT      NOT NULL COMMENT '카테고리 시퀀스', -- 카테고리 시퀀스
                               room_title                      VARCHAR(50)  NOT NULL COMMENT '방 제목', -- 방 제목
                               password                        VARCHAR(300) NULL     COMMENT '방 비밀번호', -- 비밀번호
                               secret_mode_use_yn              VARCHAR(1)   NOT NULL COMMENT '사용 : Y, 미사용 : N', -- 비밀모드 사용여부
                               simultaneous_connections_use_yn VARCHAR(1)   NOT NULL COMMENT '사용 : Y, 미사용 : N', -- 동시접속 사용여부
                               use_yn                          VARCHAR(1)   NOT NULL COMMENT '사용 : Y, 미사용 : N', -- 사용여부
                               created_id                      VARCHAR(80)  NOT NULL COMMENT '생성자', -- 생성자
                               created_datetime                DATE         NOT NULL COMMENT '생성일시', -- 생성일시
                               modified_id                     VARCHAR(80)  NULL     COMMENT '수정자', -- 수정자
                               modified_datetime               DATE         NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '채팅방';

-- 채팅방
ALTER TABLE chatting_room
    ADD CONSTRAINT PK_chatting_room -- 채팅방 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

ALTER TABLE chatting_room
    MODIFY COLUMN seq INTEGER NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE chatting_room
    AUTO_INCREMENT = 0;

-- 채팅방
ALTER TABLE chatting_room
    ADD CONSTRAINT FK_categories_TO_chatting_room -- 카테고리 -> 채팅방
        FOREIGN KEY (
                     category_seq -- 카테고리 시퀀스
            )
            REFERENCES categories ( -- 카테고리
                                   seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------

-- 채팅방 유저 리스트
CREATE TABLE chatting_room_members (
                                       seq               INTEGER     NOT NULL COMMENT '시퀀스', -- 시퀀스
                                       room_seq          INTEGER     NOT NULL COMMENT '채팅방 시퀀스', -- 방 시퀀스
                                       user_seq          VARCHAR(80) NOT NULL COMMENT '유저 아이디', -- 유저 시퀀스
                                       user_type         VARCHAR(10) NOT NULL COMMENT 'HOST : 방 주인, GEUST : 손님, ROOM_ADMIN : 방 관리자', -- 유저 타입
                                       connect_ip        VARCHAR(48) NOT NULL COMMENT '접속 IP', -- 접속 아이피
                                       created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                                       created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                                       modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                                       modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '채팅방 유저 리스트';

-- 채팅방 유저 리스트
ALTER TABLE chatting_room_members
    ADD CONSTRAINT PK_chatting_room_members -- 채팅방 유저 리스트 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

ALTER TABLE chatting_room_members
    MODIFY COLUMN seq INTEGER NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE chatting_room_members
    AUTO_INCREMENT = 0;

-- 채팅방 유저 리스트
ALTER TABLE chatting_room_members
    ADD CONSTRAINT FK_chatting_room_TO_chatting_room_members -- 채팅방 -> 채팅방 유저 리스트
        FOREIGN KEY (
                     room_seq -- 방 시퀀스
            )
            REFERENCES chatting_room ( -- 채팅방
                                      seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------
-- 유저
CREATE TABLE users (
                       seq                INTEGER      NOT NULL COMMENT '시퀀스', -- 시퀀스
                       user_id            VARCHAR(80)  NOT NULL COMMENT '유저 아이디', -- 유저아이디
                       password           VARCHAR(300) NOT NULL COMMENT '사용자 비밀번호', -- 비밀번호
                       sign_up_type       VARCHAR(10)  NOT NULL COMMENT 'DEFAULT : 일반, SNS : 소셜', -- 회원가입 구분
                       profile_image_path VARCHAR(200) NULL     COMMENT '프로필 이미지', -- 프로필 이미지 경로
                       use_yn             VARCHAR(1)   NOT NULL COMMENT '계정 사용 : Y, 미사용 : N', -- 사용여부
                       role_seq           TINYINT      NOT NULL COMMENT 'role 시퀀스', -- 역할 시퀀스
                       created_id         VARCHAR(80)  NOT NULL COMMENT '생성자', -- 생성자
                       created_datetime   DATE         NOT NULL COMMENT '생성일시', -- 생성일시
                       modified_id        VARCHAR(80)  NULL     COMMENT '수정자', -- 수정자
                       modified_datetime  DATE         NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '유저';

-- 유저
ALTER TABLE users
    ADD CONSTRAINT PK_users -- 유저 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

-- 유저 유니크 인덱스
CREATE UNIQUE INDEX UIX_users
    ON users ( -- 유저
              user_id ASC -- 유저아이디
        );

ALTER TABLE users
    MODIFY COLUMN seq INTEGER NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE users
    AUTO_INCREMENT = 0;

-- 유저
ALTER TABLE users
    ADD CONSTRAINT FK_role_TO_users -- 유저 역할 -> 유저
        FOREIGN KEY (
                     role_seq -- 역할 시퀀스
            )
            REFERENCES role ( -- 유저 역할
                             seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------

-- 블랙 리스트
CREATE TABLE black_list (
                            seq               INTEGER     NOT NULL COMMENT '시퀀스', -- 시퀀스
                            user_seq          INTEGER     NOT NULL COMMENT '유저 시퀀스', -- 유저 시퀀스
                            room_seq          INTEGER     NOT NULL COMMENT '방 시퀀스', -- 방 시퀀스
                            status            VARCHAR(10) NOT NULL COMMENT '차단 : BLOCK, 정상 : NORMAR', -- 상태
                            created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                            created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                            modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                            modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '블랙 리스트';

-- 블랙 리스트
ALTER TABLE black_list
    ADD CONSTRAINT PK_black_list -- 블랙 리스트 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

ALTER TABLE black_list
    MODIFY COLUMN seq INTEGER NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE black_list
    AUTO_INCREMENT = 0;

-- 블랙 리스트
ALTER TABLE black_list
    ADD CONSTRAINT FK_users_TO_black_list -- 유저 -> 블랙 리스트
        FOREIGN KEY (
                     user_seq -- 유저 시퀀스
            )
            REFERENCES users ( -- 유저
                              seq -- 시퀀스
                );

-- 블랙 리스트
ALTER TABLE black_list
    ADD CONSTRAINT FK_chatting_room_TO_black_list -- 채팅방 -> 블랙 리스트
        FOREIGN KEY (
                     room_seq -- 방 시퀀스
            )
            REFERENCES chatting_room ( -- 채팅방
                                      seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------

-- 채팅 메시지
CREATE TABLE chatting_message (
                                  seq               INTEGER     NOT NULL COMMENT '시퀀스', -- 시퀀스
                                  from_user_seq     INTEGER     NOT NULL COMMENT '보내는사람', -- 보내는사람
                                  to_user_seq       INTEGER     NOT NULL COMMENT '0 : 전체, 0 != 대상', -- 받는사람
                                  message_status    VARCHAR(10) NOT NULL COMMENT 'NORMAL : 일반, WAIT_APPROVAL : 승인 대기, APPROVED : 승인, REJECTED : 승인 거부, BLOCKED : 거부', -- 메시지 상태
                                  created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                                  created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                                  modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                                  modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '채팅 메시지';

-- 채팅 메시지
ALTER TABLE chatting_message
    ADD CONSTRAINT PK_chatting_message -- 채팅 메시지 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

ALTER TABLE chatting_message
    MODIFY COLUMN seq INTEGER NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE chatting_message
    AUTO_INCREMENT = 0;

-- 채팅 메시지
ALTER TABLE chatting_message
    ADD CONSTRAINT FK_users_TO_chatting_message -- 유저 -> 채팅 메시지
        FOREIGN KEY (
                     from_user_seq -- 보내는사람
            )
            REFERENCES users ( -- 유저
                              seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------

-- 유저 역할
CREATE TABLE role (
                      seq               TINYINT     NOT NULL COMMENT '시퀀스', -- 시퀀스
                      role_name         VARCHAR(50) NOT NULL COMMENT 'ADMIN : 슈퍼 관리자, HOST : 방 주인, GUEST : 손님', -- 역할 이름
                      use_yn            VARCHAR(1)  NOT NULL COMMENT '계정 사용 : Y, 미사용 : N', -- 사용여부
                      created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                      created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                      modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                      modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '유저 역할';

-- 유저 역할
ALTER TABLE role
    ADD CONSTRAINT PK_role -- 유저 역할 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

-- ---------------------------------------------------------------------------------------------------------------------

-- 유저 권한
CREATE TABLE permission (
                            seq               TINYINT     NOT NULL COMMENT '시퀀스', -- 시퀀스
                            role_seq          TINYINT     NOT NULL COMMENT '롤 시퀀스', -- 롤 시퀀스
                            permission_name   VARCHAR(50) NOT NULL COMMENT 'DIRECT_MSG : 귓속말 권한, APPROVE : 승인 권한, REJECT : 거부 권한, BLOCK : 블락 권한', -- 권한 이름
                            use_yn            VARCHAR(1)  NOT NULL COMMENT '계정 사용 : Y, 미사용 : N', -- 사용여부
                            created_id        VARCHAR(80) NOT NULL COMMENT '생성자', -- 생성자
                            created_datetime  DATE        NOT NULL COMMENT '생성일시', -- 생성일시
                            modified_id       VARCHAR(80) NULL     COMMENT '수정자', -- 수정자
                            modified_datetime DATE        NULL     COMMENT '수정일시' -- 수정일시
)
    COMMENT '유저 권한';

-- 유저 권한
ALTER TABLE permission
    ADD CONSTRAINT PK_permission -- 유저 권한 기본키
        PRIMARY KEY (
                     seq -- 시퀀스
            );

-- 유저 권한 유니크 인덱스
CREATE UNIQUE INDEX UIX_permission
    ON permission ( -- 유저 권한
                   role_seq ASC,        -- 롤 시퀀스
                   permission_name ASC  -- 권한 이름
        );

ALTER TABLE permission
    MODIFY COLUMN seq TINYINT NOT NULL AUTO_INCREMENT COMMENT '시퀀스';

ALTER TABLE permission
    AUTO_INCREMENT = 0;

-- 유저 권한
ALTER TABLE permission
    ADD CONSTRAINT FK_role_TO_permission -- 유저 역할 -> 유저 권한
        FOREIGN KEY (
                     role_seq -- 롤 시퀀스
            )
            REFERENCES role ( -- 유저 역할
                             seq -- 시퀀스
                );

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

-- ---------------------------------------------------------------------------------------------------------------------

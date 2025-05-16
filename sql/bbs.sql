
-- 게시글 테이블
-- 테이블 이름은 board
CREATE TABLE board (
    board_id      NUMBER PRIMARY KEY,             -- 게시글 ID (기본키, NUMBER 타입)
    title         VARCHAR2(200) NOT NULL,         -- 제목 (VARCHAR2, 필수)
    content       CLOB NOT NULL,                  -- 내용 (CLOB, 필수)
    writer        VARCHAR2(100) NOT NULL,         -- 작성자 이름 (VARCHAR2, 필수) - DAO 코드의 writer 필드와 매핑
    created_at    TIMESTAMP DEFAULT SYSTIMESTAMP, -- 작성일 (TIMESTAMP)
    -- updated_at
    updated_at  TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 게시글 ID를 자동으로 생성해 줄 시퀀스
CREATE SEQUENCE BOARD_BOARD_ID_SEQ
START WITH 1       -- 1부터 시작
INCREMENT BY 1;   -- 1씩 증가

------------------------------------------------------------------------
-- 테스트 데이터 추가 (선택 사항)
------------------------------------------------------------------------

-- 테스트 게시글 데이터 추가
INSERT INTO board (board_id, title, content, writer)
VALUES (
    BOARD_BOARD_ID_SEQ.NEXTVAL, -- 시퀀스로 ID 생성
    'DAO 코드에 맞춘 첫 글',
    '이 글은 새로 만든 DB 구조에 맞춰서 작성되었습니다.',
    '코드맞춤작성자' -- writer 컬럼에 직접 문자열 작성자 이름/닉네임 입력
);

INSERT INTO board (board_id, title, content, writer)
VALUES (
    BOARD_BOARD_ID_SEQ.NEXTVAL,
    '두 번째 테스트 글',
    '데이터가 잘 들어가는지 확인해봅시다!',
    '테스터1'
);

SELECT * FROM board ORDER BY board_id DESC;
CREATE SEQUENCE board_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;
SELECT sequence_name
FROM user_sequences
WHERE sequence_name = 'BOARD_SEQ';




-- Full text index 사용 시, 불용어 사용을 비활성화 하기.
-- 어플리케이션 실행시 최초 한번 실행되고, 다음 DB 재시작까지 유효하다.(별도의 추가 설정 없이 기본적으로 실행됨)
-- 이 설정은 로컬 환경에서만 유효함. AWS RDS(prod 환경)에서는 파라미터 그룹으로 설정해줘야한다.
SET GLOBAL innodb_ft_enable_stopword = OFF;
SET SESSION innodb_ft_enable_stopword = OFF; -- GLOBAL 변수만 설정하면 해당 설정이 새로운 세션에는 적용되지만, 현재 활성 세션에는 즉시 적용되지 않습니다. (ChatGPT)

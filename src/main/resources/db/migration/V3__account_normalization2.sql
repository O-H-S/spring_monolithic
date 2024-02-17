

-- 기존 account 테이블에서 password가 있는 데이터를 local_credential로 이동
INSERT INTO local_credential (password, username, account_id)
SELECT password, username, id
FROM account
WHERE password IS NOT NULL AND password != '';

-- 기존 account 테이블에서 provider_id가 NULL이 아닌 데이터를 oauth2credential로 이동
INSERT INTO oauth2credential (provider, provider_id, account_id)
SELECT provider, provider_id, id
FROM account
WHERE provider_id IS NOT NULL AND provider_id != '';




# 제외된 파일
- application-secret.yml : 민감한 정보들을 보관하기 때문에 로컬에서 관리된다.
  - resources 하위에 위치함.
```yml 
app:
  #어드민 로그인을 위한 패스워드
  admin-key: ~~~ 
  #쿠키 서명을 위한 private key
  jwt-secret: ~~~
server:
    # ssl 인증서
    ssl:
        key-store: classpath:keystore.p12
        key-store-password: ~~~
        key-store-type: PKCS12
        key-alias: tomcat
    spring:
        datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://~~~
        username: ~~~
        password: ~~~
    security:
    oauth2:
    client:
        provider:
            kakao:
                authorization-uri: https://kauth.kakao.com/oauth/authorize
                token-uri: https://kauth.kakao.com/oauth/token
                user-info-uri: https://kapi.kakao.com/v2/user/me
                user-name-attribute: id
        registration:
            kakao:
                client-id: ~~~
                client-secret: ~~~
                client-authentication-method: client_secret_post
                redirect-uri: https://www.ohs.kr/login/oauth2/code/kakao
                authorization-grant-type: authorization_code
                client-name: kakao
                scope:
                    - profile_nickname
                    - account_email
```

- keystore.p12 : ssl 인증서, resources 하위에 위치함.
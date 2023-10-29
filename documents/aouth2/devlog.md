# AOuth2 기능 구현을 위한 branch

- TO-DO
    - 다양한 Provider 구현하기
        - 카카오(진행중)
        - Google
        - Naver
        - ...
    - 관련된 Security Filter와 로직 문서화
    - CustomOAuth2User 구현해야 할 메소드 공부하기.
    - <s>"Name, Username, Nickname, ID" 차이점 정리하기.</s>
    - 닉네임 기능 구현하기
    - "카카오 로그인" 버튼 디자인 변경하기.~~~~
    -
    - ...


- Problem / Error
    - <s>게시글 조회시 에러 발생(Form Object / Validation 관련)</s>
    - Entity 구조 변경으로 인한, 기존 Form 기반 로그인 호환 문제.
        - Test 코드 작성 필요.
    - "카카오 로그인" 버튼 클릭시, form 에러 뜨는 버그
    - 등등..

----
흔히 ID/Password 라고 하지만, 
스프링 시큐리티의 Principal에서는 Name/Password 인 듯하다.

즉, name이 사용자의 고유한 식별자가 된다.

또한,
UserDetails에서는 Name -> Username 으로 명칭이 바뀐다.
OAuth2User에서는 Name -> Name 으로 동일하다.


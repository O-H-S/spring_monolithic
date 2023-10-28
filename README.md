# AOuth2 기능 구현을 위한 branch

- TO-DO 
  - 다양한 Provider 구현하기
    - 카카오(진행중)
    - Google
    - Naver
    - ...
  - 관련된 Security Filter와 로직 문서화
  - CustomOAuth2User 구현해야 할 메소드 공부하기.
  - "Name, Username, Nickname, ID" 차이점 정리하기.
  - "카카오 로그인" 버튼 디자인 변경하기.
  - 
  - ...
    

- Problem / Error
  - <s>게시글 조회시 에러 발생(Form Object / Validation 관련)</s>
  - Entity 구조 변경으로 인한, 기존 Form 기반 로그인 호환 문제.
    - Test 코드 작성 필요.
  - "카카오 로그인" 버튼 클릭시, form 에러 뜨는 버그  
  - 등등..
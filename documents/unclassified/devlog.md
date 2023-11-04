# 아직 분류하지 않은 devlog


- 유저 정보 Entity의 이름이 Account인 것이 바람직한가? (은행 계좌가 떠오름)
  - 후보 : Member, Membership, AppUser
  - User는 스프링 시큐리티의 클래스와 겹치므로 혼란이 생길 것 같음.
- 게스트 기능 구현하기.
  - 스프링 시큐리티와 어떻게 연동할 지?
- 유저 권한 여러개 
- 스프링 시큐리티 권한의 동적 변경 코드 리팩토링 필요 (UserController.java안에 코드 있음.)
- build 관련 파일들을 ignore 대상에 추가시켜서, 보안에 신경쓰자.
- 원격 저장소 branch 제거하기
  - git push <remote 별칭> -d < branch>
  - ex) git push origin -d main
- RESTful 원칙으로 다시 설계하기
  - Board, Account, Post, ...

- 버전 확인하기
  - 서버 실행시 콘솔창에서 대부분 확인 가능.
- 인텔리제이에서 .md 파일 수정시 팁
  - 텍스트를 드래그하면, 간편하게 스타일 변경 가능.
  - Project 탭에 있는 이미지 파일을 드래그하여, 간편하게 추가 가능.
    - 추가된 이미지 태그 왼쪽 버튼을 클릭하면, 사이즈 조절도 가능.
![intelliJ_mdFileTip_imageTag.png](..%2Fimages%2FintelliJ_mdFileTip_imageTag.png)

  - Gradle
    - `runtimeOnly` vs `implementation`  
    ```
    [runtimeOnly]
    컴파일 시점에는 사용 X, 런타임에는 사용 O
    종속성이 다른 모듈에 전파되지 않음.
    
    [implementation]
    컴파일 시점에는 사용 O, 런타임에는 사용 O
    종속성이 다른 모듈에 전파되지 않음.
    ```
    - 종속성이 전파되지 않는다.
    ```
    A, B, C 라는 모듈이 존재한다.
    C는 B
    B는 A 에 의존성을 가진다.
    즉, C -> B -> A 이다.
    이때 C 모듈에서 implmentation(B) 설정하더라도 종속성이 전파되지 않으므로, C는 A에 관해서 모른다.
    
    종속성 전파를 제한함으로써, 
    각 모듈의 인터페이스를 더 깔끔하게 유지하고, 불필요한 의존성을 줄임.
    이는 더 쉬운 유지 보수와 더 나은 빌드 성능을 가능하게 함.
    
    ```

- [Problem / Error]
    

----


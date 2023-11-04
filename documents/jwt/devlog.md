# JWT 

- TO-DO
    - 전체적인 코드는 https://gksdudrb922.tistory.com/217 를 참고하였다.
      - 분석해서 정리하기.
    - **"Default 필터 체인"** 에 속해있는 필터들 공부하기.
    - api 필터체인, web 필터체인 두개를 분리?
    - 필터를 추가하는 작업이므로, 시큐리티 관련된 기존 기능들의 **Test 코드를 작성**하여 호환성 확인 필요.
      - 인증 수단 : Form / AOuth2
      - 인증 관리 : Session(httpSession) / Token(jwt)
      - 요청 : 로그인 / 조회 / 수정 / 로그아웃
      - 위 3 조합의 다양한 경우를 고려하여 테스트코드 짜보기.
    - 클라이언트가 쿠키를 보관하는 방식
      - 헤더 : `Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..`
      - 쿠키(현재) : `jwt-token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`
      - 현재는 개인적으로 더 익숙한 쿠키를 사용하고 있지만, 각 방식의 장/단점은 무엇인지 파악하기.
      

- Problem / Error
    - jwt 관련 요청의 stateless한 특성을 **보장**하려면, 세션 관리 정책이 SessionCreationPolicy.STATELESS 여야한다. (+ CSRF 비활성화 등등) 하지만, 세션 기반 인증과 토큰 기반 인증을 동시에 구현하는게 목적이므로 우선은 SessionCreationPolicy.IF_REQUIRED 설정된 필터체인에 구현함.
      - 두 개의 필터체인을 구성하여, 하나는 토큰 기반 요청을 처리하는 필터체인으로 나머지는 세션 기반 요청을 처리하는 필터체인으로 구성해보기.
    - 
----
* Filter Chain 디버그 하는 법
  * 중단점 설정 후, Debug로 실행.
  ![springFilterchainDebug.png](images%2FspringFilterchainDebug.png)

* SecurityContextPersistenceFilter는 5.7버전 이후로 deprecated -> SecurityContextHolderFilter
  * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/context/SecurityContextPersistenceFilter.html
  * https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html
  * 관련 블로그 : https://tech-monster.tistory.com/206
  * 명시적으로 repo.saveContext()를 호출해줘야 하는 것으로 바뀜.
    * 현재 프로젝트에서는   
    `public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter `,  
    `public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilte`에서 AbstractAuthenticationProcessingFilter가 성공시 saveContext를 호출한다.

* AbstractAuthenticationProcessingFilter의 doFilter 메서드를 살펴보면, 현재의 SecurityContext에서 Authentication 객체를 가져와서 이 객체가 null이 아니고 isAuthenticated() 메서드의 결과가 true인 경우 (즉, 이미 인증된 상태인 경우)에는 필터 체인을 계속 진행하고, 추가적인 인증 처리를 하지 않습니다.
* jwtCookie.setPath("/");로 설정하면, 클라이언트는 도메인의 루트(/)와 그 아래의 모든 경로에 대한 요청에 이 쿠키를 포함하여 서버로 전송하게 됩니다. 다시 말해, 웹 사이트의 모든 페이지에서 이 쿠키가 사용될 수 있습니다.


- TO-DO
    - https://mangkyu.tistory.com/182 (다 읽고 정리하기)
    - 통합 테스트 코드 작성해보기
```java
// template code
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GreetingControllerIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testGetGreeting() {
      ResponseEntity<String> response = restTemplate.getForEntity("/api/greetings/John", String.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertEquals("Hello, John!", response.getBody());
  }
}
  ```

- Problem / Error
    - 

----

[출처 : https://mangkyu.tistory.com/144, https://mangkyu.tistory.com/182]

Test-Driven Development(TDD) : 소프트웨어 개발 방법론, 코드를 작성하기 전에 테스트 케이스를 먼저 작성
"빨강-초록-리팩토링" 세 단계의 반복
  * 빨강 : 구현할 기능의 명세 역할(구현된 코드가 없기 때문에 빨강)
    * "실패하는 코드"는 컴파일 오류를 의미하는 것이 아니라, 구현이 완료되지 않아 테스트가 실패하는 상태를 의미
  * 초록 : 통과하기 위해 최소한의 코드(통과가 목적이기 때문에 품질 신경 X)
  * 리팩토링 : 초록을 유지하면서, 품질을 향상시키기.
  * 
Repository(H2) -> Service(Mockito를 사용해 Repository 계층을 Mock) -> Controller(SpringTest의 MockMvc) 순서로 개발을 진행
* 대부분의 경우 Repository는 의존성이 적기 때문에 먼저 시작

##### 좋은 테스트 특징 (FIRST 규칙)
* Fast: 테스트는 빠르게 동작하여 자주 돌릴 수 있어야 한다.
* Independent: 각각의 테스트는 독립적이며 서로 의존해서는 안된다.
* Repeatable: 어느 환경에서도 반복 가능해야 한다.
* Self-Validating: 테스트는 성공 또는 실패로 bool 값으로 결과를 내어 자체적으로 검증되어야 한다.
* Timely: 테스트는 적시에 즉, 테스트하려는 실제 코드를 구현하기 직전에 구현해야 한다.


##### 관련 라이브러리
* JUnit5: 자바 단위 테스트를 위한 테스팅 프레임워크
* AssertJ: 자바 테스트를 돕기 위해 다양한 문법을 지원하는 라이브러리
JUnit 만으로도 단위 테스트를 충분히 작성할 수 있다. 하지만 JUnit에서 제공하는 assertEquals()와 같은 메소드는 AssertJ가 주는 메소드에 비해 가독성이 떨어진다. 그렇기 때문에 순수 Java 애플리케이션에서 단위 테스트를 위해 JUnit5와 AssertJ 조합이 많이 사용된다.

##### 작성 패턴
* given(준비): 어떠한 데이터가 준비되었을 때
* when(실행): 어떠한 함수를 실행하면
* then(검증): 어떠한 결과가 나와야 한다.

##### Mockito 가짜 객체의 의존성 주입하는 어노테이션

* @Mock: 가짜 객체를 만들어 반환해주는 어노테이션
* @Spy: Stub하지 않은 메소드들은 원본 메소드 그대로 사용하는 어노테이션
* @InjectMocks: @Mock 또는 @Spy로 생성된 가짜 객체를 자동으로 주입시켜주는 어노테이션

##### JUnit과 결합 하기 : @ExtendWith(MockitoExtension.class)


##### Test에서의 설정
main/resources/application.yml  
`override ` test/resources/application.yml (더 높은 우선순위)  
`override ` @ActiveProfiles (더 높은 우선순위)  
`override ` @TestPropertySource (더 높은 우선순위)  

##### @Tag를 사용하여, 테스트 대상에서 제외시키기

```
@Tag("notAutomation")
public class SlowTest {

    @Test
    public void benchmarkTest() {

    }
}

test {
    useJUnitPlatform {
        excludeTags 'notAutomation'
    }
}
```
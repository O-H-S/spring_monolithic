# `Board(게시판)` 개발 기록

## TO-DO
- BoardManageService -> BoardService로 명칭 변경하기
- RESTful 원칙으로 다시 작성하기.
- 최적화
  * 대규모 데이터가 존재하는 상황에서도 지연 없이 동작하도록
  * "만약 대규모 요청이 빠르게 들어온다면?" 이에 대한 최적화도 필요해보임.

  - 대상
    - 게시글 생성
      - bulk insert 
        - ~~테스트 코드~~
    - 전체 게시글 개수 조회
      - 캐싱
        - ~~간단한 로컬 캐시 구현~~ 
        - ConcurrentHashMap<Integer, AtomicLong> 로 바꾸기
    * 게시글(Post) 조회 
      * 페이지네이션
        * ~~테스트 코드~~
      * 댓글수 조회
    - 게시글 작성 / 삭제
    - 게시글 변경

        
        

## Problem / Error
* 테스트 여러번 할수록 시간이 점점 짧아지는 정확한 이유 파악하기

----
대량의 Post 데이터 insert 테스트 코드
- Post Entity는 채번 전략이 IDENTITY이므로, DB에 Insert 쿼리를 보낸 후 생성된 id값을 받아와야한다.(데이터베이스 round-trip을 수행해야 함) (id 값은 영속성 컨텍스트 관리를 위해 사전에 필요하다.) 따라서, 일괄적으로 insert 하는 것이 불가능하다.
  - 만약 id 할당을 미룬다면, 차후에 동일한 entity의 접근이 일어날경우 동작을 예측하기 힘들어진다.
  - IDENTITY에서 MySQL은 AUTO_INCREMENT를 PK 컬럼에 설정한다.
  - SEQUENCE로 바꾸면? -> mysql은 시퀀스 객체를 지원하지 않았다.(8.0이상부터는 지원하지만 hibernate가 아직 구현하지 않았다?)
  - TABLE 은? ->
  - 따라서, saveall을 사용하더라도 배치되어 처리되지 않는다.
  - 단순히, 테스트 데이터를 밀어넣는건데 영속성 관리가 필요한가? -> JDBC를 직접 사용하자.
  
Post count 캐시
  - 분산 구조가 아니므로, 굳이 redis를 사용하지 않고 로컬 캐시로 간단하게 구현
    - ConcurrentHashMap<(BoradID), Long> 
    - 만약 한 게시판에 글 쓰기 요청이 몰린다면? 단순
      - ConcurrentHashMap의 compute 메소드는 key 값마다 lock을 거는게 아니라, bucket단위로 락을 건다. bucket은 여러개의 key를 포함한다.
      - AtomicLong으로 바꾸는 방법 생각해보자. CAS 방식으로 Lock보다 더 빠르다. 
      - 더 자세한 내용 : https://jiwondev.tistory.com/211
  - 만약 부모 트랜잭션에서 이 Post write 트랜잭션을 호출할 때, 변경된 cache 값을 즉시 사용할 수 있도록 우선 증가시켜놓고 롤백 시에 다시 차감한다


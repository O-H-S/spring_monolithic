# OHSite
>**Spring Boot** 학습 목적의 개인 프로젝트입니다.  
> 이 프로젝트는 "점프 투 프로젝트(https://wikidocs.net/160027)" 을 따라하는 것부터 시작하여, 제가 개인적으로 학습한 백엔드 관련 지식들과 접목하여 개선 시키는 방향으로 진행하고 있습니다.  
> 학습한 내용을 실습/복습하는 것이 주 목적이기 때문에, 실행 자체와 관련 없는 여러 실습 코드와 주석들이 포함되어 있음을 양해부탁드립니다.  
> Monolithic 아키텍처에서 다양한 시나리오를 통해 Spring Framework에 익숙해진 뒤, fork하여 MSA로 migration 하는 것을 목표로 하고 있습니다.    
> 
>  현재, 2023-11-03 기준으로 아직 호스팅 서버에 배포하기에는 미흡하여, 도메인 등록만 되어 있는 상황입니다.
> 
![indexPage.png](documents%2Fimages%2FindexPage.png)
>_index page_





## 프로젝트 환경 / 기술 스택

`IntelliJ 2023.2`  
`Java17` `Gradle 8.2.1` `JUnit` `Lombok`   
`SpringBoot 3.1.3`  
`SpringDataJPA` `Hibernate 6.2.7` 
* test : `org.h2.Driver`--- (in-memory) `H2`  
* dev : `mysql-connector-j` --- (localhost:3306)`MySQL 8.0.34` `InnoDB`  
* prod(준비중) : `mysql-connector-j` --- `MySQL` `InnoDB`  

`SpringSecurity 6.1.3` `OAuth2.0` `JWT`   
`Thymeleaf` `Bootstrap`


## Features
각 항목을 클릭하여, 자세한 내용을 확인할 수 있습니다.
<details>
  <summary>인증/인가</summary>

</details>

<details>
  <summary>관리자(Admin)</summary>

[상세 페이지](documents%2Fadmin%2Fintroduction.md)  

Spring Security와 연동되는 관리자 기능입니다.


</details>


<details>
  <summary>게시판(Board)</summary>

</details>

## Remote repository에서 제외된 파일
> 보안에 민감한 일부 파일들은 gitignore에 설정되어 별도로 관리됩니다.  
> (연동 서버 정보, DB 계정, 빌드 파일 등등...)  
> 자세한 내용은 [IgnoredFiles.md](documents%2FIgnoredFiles.md) 에서 확인하실 수 있습니다.








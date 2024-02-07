# OHSite

> **Spring Boot** 학습 목적의 개인 프로젝트입니다.
> 이 프로젝트는 "점프 투 프로젝트(https://wikidocs.net/book/7601)" 을 따라하는 것부터 시작하여, 제가 개인적으로 학습한 백엔드 관련 지식들과 접목하여 개선 시키는 방향으로 진행하고 있습니다.

![indexPage.png](documents%2Fimages%2FindexPage.png)
![postListPage.png](documents%2Fimages%2FpostListPage.png)

## 프로젝트 환경 / 구조

![architecture_brief.png](documents%2Fimages%2Farchitecture_brief.png)

### Stack

`IntelliJ 2023.2` `Java17` `Gradle 8.2.1` `SpringBoot 3.1.3` `SpringDataJPA` `Hibernate`

* test : `org.h2.Driver`--- (in-memory) `H2`
* dev : `mysql-connector-j` --- (localhost:3306)`MySQL 8.0.34` `InnoDB`
* prod(준비중) : `mysql-connector-j` --- `MySQL` `InnoDB`

`SpringSecurity` `OAuth2.0`
`Thymeleaf` `Bootstrap`

## 설계

### Use case diagram

![UsecaseDiagram_overall.png](documents%2Fimages%2FUsecaseDiagram_overall.png)

### ERD

![ERD.png](documents%2Fimages%2FERD.png)

### [REST Docs(클릭하여 이동)](https://www.ohs.kr/docs/index.html)

![restdocs_intro.png](documents%2Fimages%2Frestdocs_intro.png)

## Features

<details>
  <summary>인증</summary>
사용자는 비밀번호를 입력하여 서버에 직접 계정을 생성하거나, 카카오 계정으로 인증할 수 있습니다.
</details>

<details>  
  <summary>Web UI 관리자 기능 </summary>

[상세 페이지](documents%2Fadmin%2Fintroduction.md)

어드민은 브라우저를 통해 인증하여 서버를 관리할 수 있습니다.

</details>

<details>
  <summary>게시판(Board)</summary>

</details>

## 개발 노트

* 대량의 게시글 Pagiantion 최적화
* 게시글, 댓글 추천 기능의 동시성 고려
* 대규모 데이터 Insert 성능 향상
* @Tag를 통한 테스트 코드 분류
* N+1 문제 개선하기

## 주석

### Remote repository에서 제외된 파일

> 보안에 민감한 일부 파일들은 gitignore에 설정되어 별도로 관리됩니다.
> (연동 서버 정보, DB 계정, 빌드 파일 등등...)
> 자세한 내용은 [IgnoredFiles.md](documents%2FIgnoredFiles.md) 에서 확인하실 수 있습니다.

# BackEnd

![메인이미지](https://github.com/OurPortfolio/FE/assets/108606678/32f89718-f610-4b36-829e-8fc9ea5455a4)

<br />

### Index
1. [**서비스 개발 멤버 소개**](#1)
2. [**기술 도입 배경**](#2)

<br/>
<br/>

<div id="1"></div>

### 👩‍👩‍👧‍👧 Backend 멤버 소개
|  멤버 GitHub   |  역할   |
|:---------------|:--------|
|[🚩박지훈](https://github.com/bbakzi)| Leader😈<br/>CI/CD구현,  AWS 인프라 구축,  Https서버 배포/클라이언트 연결<br/>|
|[김민규](https://github.com/kmg0485)| Spring Security 설정,  회원가입/로그인,  이메일 인증,  소셜로그인,  RefreshToken발급 |
|[이재호](https://github.com/spainclub)| 프로젝트 CRUD, 테스트 코드 작성, Jacoco 커버리지 테스트, SonarQube 코드 분석 |
|[최원제](https://github.com/co-ze)| 포트폴리오 CRUD, 검색어 자동 완성, 테스트 코드 작성, Query 최적화, Jmeter 성능 테스트 |


<br/>
<br/>

<div id="2"></div>

### ✨기술 도입 배경

|     기술     | 도입 배경 |
| :-------- |:-------------|
|<img src="https://images.velog.io/images/ewan/post/4d809000-30c0-48ed-b0d7-a0ded326b95d/querydsl.png"  width="300" height="200"/>| - 동적 쿼리 작성 용이.<br/>- JPA, JPQL의 한계 극복.<br/>복잡한 조회 쿼리 작성.<br/>자료가 많고 비교적 진입 장벽이 낮다. |
|<img src="https://blog.kakaocdn.net/dn/U6FVe/btrr9yQanvW/frnXw7eMhPLV3f0Zz3ZUYK/img.png"  width="300" height="200"/>| - 테스트 코드 커버리지 체크를 위해 사용.<br/>- 다른 라이브러리보다 자료가 많아서 적용하기 비교적 쉽다.|
|<img src="https://images.velog.io/images/hamon/post/dceac89a-dbbc-4331-9acc-e3cdfbec312c/sonarqube-logo.png"  width="300" height="200"/>| - 많은 레퍼런스가 존재한다.<br/>- Github Action/Jenkins등 CI/CD툴과 연동하여 자동 정적 분석을 할 수 있다.|
|<img src="https://ps.w.org/redis-cache/assets/banner-1544x500.png?rev=2315420"  width="300" height="200"/>| - 검색어 자동 완성 결과 caching<br/>- 다양한 형식으로 데이터 저장 가능 |
|<img src="https://blog.kakaocdn.net/dn/cWjrmh/btrc3YD3vyN/x4ApFt8gB7yDkBw2oqzca1/img.png"  width="300" height="200"/><img src="https://miro.medium.com/v2/resize:fit:1400/0*e-ELl9qXHd2kI22Q.jpg"  width="300" height="200"/>| - 코드 변경이 생길 때마다 지속적이고 반복적인 배포 작업을 자동화하여 서비스 로직에 집중할 수 있다.<br/>- Github Action은 설치가 필요없고 여러 명이 하나의 Repository에서 작업할 때 편리하다.<br/>- Code Deploy는 롤백이 가능하며 현재 사용 중인 AWS의 EC2,S3 등 다양한 서비스화 호환성이 좋다.|





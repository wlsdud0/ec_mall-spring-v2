# eCommerce Mall 
## 온라인 쇼핑 플랫폼 (캡스톤 디자인)
- 프로젝트 기간 : 2024.03.25 ~ 2024.05.03
- 사용자가 직접 상품을 등록해 쉽고 빠르게 매매할 수 있는 온라인 쇼핑 플랫폼.
- 배포 : [Ecmall 웹사이트](https://ecmall.site)




##  팀원 소개
<br>
<div align="center"> 
<table>
    <tr>
        <td align="center">
            <b>김성운</b>
        </td>
        <td align="center">
            <b>허진영</b>
        </td>
        <td align="center">
            <b>조호진</b>
        </td>
    </tr>
  <tr>
        <td align="center">
            <br>
            - 커뮤니티 기능 구현 <br>
            - 관리자 회원관리 / 공지사항 구현 <br>
            - HTTPS / 도메인 설정 <br>
            <br>
        </td>
        <td align="center">
            <br>
            - 사용자 계정관리 기능 구현 <br>
            - AWS EC2 배포 / AWS RDS MariaDB 구축<br>
            - CI/CD 구축을 통해 배포 자동화 / 무중단 배포 <br>
            <br>
        </td>
        <td align="center">
            <br>
            - 상품관리 / 배송기능 구현 <br>
            - 장바구니 / 주문상태 변경기능 구현 <br>
            - Thymeleaf를 사용해 화면 구현 <br>
            <br>
        </td>
    </tr>
</table>
</div>
<br>

##  기술 스택

<div align="center">
  <p>💾 BACKEND</p>
    <img src="https://img.shields.io/badge/Java 17-FF160B?style=flat-square&logo=java&logoColor=white"/>
    <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
    <img src="https://img.shields.io/badge/Spring_data_jpa-6DB33F?style=flat-the-badge&logo=SpringSecurity&logoColor=white"/>
    <img src="https://img.shields.io/badge/HTTP Interface-008FC7?style=flat-square&logo=jpa&logoColor=white"/>
  <p>🖥️ FRONTEND</p>
    <img src="https://img.shields.io/badge/HTML-E34F26?style=flat-square&logo=html5&logoColor=white"/>
    <img src="https://img.shields.io/badge/CSS-1572B6?style=flat-square&logo=css3&logoColor=white"/>
    <img src="https://img.shields.io/badge/BootStrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white"/>
    <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white"/>
    <img src="https://img.shields.io/badge/JavaSript-F7DF1E?style=flat-square&logo=javascript&logoColor=white"/>
  <p>⚒️ ETC</p>
    <img src="https://img.shields.io/badge/AWS S3-569A31?style=flat-square&logo=amazon s3&logoColor=white"/>
    <img src="https://img.shields.io/badge/SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white"/>
    <img src="https://img.shields.io/badge/MariaDB-003545?style=flat-the-badge&logo=mariadb&logoColor=white"/>
    <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"/>
    <img src="https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white"/>
    <img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white"/>
  <br>
    <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white"/>
    <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=flat-square&logo=amazonrds&logoColor=white"/>
    <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white"/>
    <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>
    <img src="https://img.shields.io/badge/-NGINX-009639?style=flat&logo=nginx&logoColor=white"/>
<br>
</div>

## 설계
### 유스케이스 다이어그램

![유스케이스 다이어그램](./img/유스케이스%20다이어그램.png)

### 시스템 구성 다이어그램

![시스템 구성 다이그램](./img/시스템%20구성%20다이어그램.png)

### ERD

![ERD](./img/ERD.png)

### 팀 규칙
![팀 규칙](./img/팀%20규칙.png)



## 기능 설명

### Unlogin User
- 게시글 조회, 검색, 정렬 순서 선택
- 조회 수, 좋아요, 싫어요 조회
- 상품 조회, 카테고리 별 조회


### Login User
- 회원가입, 로그인
  - 회원가입 시, 아이디 중복 확인 및 이메일 등록
  - 이름과 이메일을 확인 후, 아이디 찾기 
  - 아이디, 이름, 이메일 확인 후, 비밀번호 찾기 
  - 비밀번호 변경
  - 회원 정보 조회 및 수정
  - 로그아웃과 회원 탈퇴
- 게시판
  - 게시글 조회, 검색, 정렬 순서 선택
  - 조회 수, 좋아요, 싫어요 조회
  - 게시글 작성, 수정, 삭제
  - 댓글 작성, 수정, 삭제
- 쇼핑몰
  - 판매자
    - 판매자 등록
      - 관리자 승인이 완료되면 쇼핑몰에 상품 등록 가능
    - 상품 조회, 등록, 수정, 삭제 
    - 등록한 상품 주문현황 조회
    - 주문 상태 변경
      - 주문접수, 배송준비, 배송시작, 환불
  - 소비자
    - 상품 조회, 카테고리 별 조회
    - 장바구니 담기
    - 주문 상태 변경
      - 주문 접수 전 취소, 물품 수령, 환불 요청
    
    
### Admin
- 회원관리
  - 모든 판매자 조회
  - 판매자 등록 대기 조회
  - 판매자 등록 승인
  - 판매자 권환 회수
- 공지사항 
  - 공지사항 작성, 수정, 삭제
- 로그인 회원의 모든 기능
  - 게시글에 관리자 답글 작성, 수정, 삭제
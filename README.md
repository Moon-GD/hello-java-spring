# Chapter 02. Maria DB와 Spring Data JPA

## 2.1 MariaDB 설치

- 크게 GUI, CLI의 2가지 설치 방법이 존재하지만..
- ❗한 가지 중요한 것은 root 계정을 설정하게 되면 비밀번호를 꼭 기억하는 것! ❗️

### 1️⃣ GUI 기반
- <a href="https://mariadb.org/">공식 홈페이지</a>로!

<br />

### 2️⃣ CLI (feat. homebrew)
```bash
# 1. brew 최신화
$ brew update

# 2. MariaDB 설치
$ brew install mariadb

# 3. MariaDB 설치 확인
$ mariadb --version  # mariadb from 11.3.2-MariaDB, ...
$ brew info maridb
```

- 편의성 상승을 위해 GUI 프로그램 설치
    - Windows 환경에서는 `HeidiSQL` 사용하면 되는데, macOS에서는 지원을 안하는 것으로 보임 ㅇㅅㅇ
    - 그래서 `sequel ace` <a href="https://www.sequelpro.com/">다운로드</a>

<br />

### 3️⃣ Sequel Ace 연결

- 프로그램 실행 후 아래와 같이 기입...

<img width="456" alt="스크린샷 2024-02-29 오후 8 26 21" src="https://github.com/Moon-GD/hello-java-spring/assets/74173976/03daa019-3ec2-4316-976b-642fef3f94c4">


- 그리고 Connect 시도하면 `connection error`가 발생!
- macOS에서 mysql.user 테이블을 살펴보면 'root' 사용자에 대한 password가 비어있음 → <a href="https://madplay.github.io/post/mysql-change-root-password-error">먼저 삽질한 분의 블로그</a>
- 그래서 아래와 같은 방식으로 사용하고자 하는 사용자 계정에 password 발급!

```shell
# mysql 실행
$ mysql
```

```mysql
# mysql 테이블 선택
USE mysql;

# mysql.user 테이블이 가상 뷰이기 때문에 직접 업데이트 불가능!
# 아래의 방식으로 사용자 암호 발급 및 변경
SET PASSWORD FOR 'user name'@'db host' = PASSWORD('new password');

# 혹시나 없는 사용자라고 한다면? 아래와 같이 추가
CREATE USER 'user name'@'db host' IDENTIFIED BY 'password';

# 잘못 생성했다면?
DROP USER 'user name'@'db host';
```

- 이후 connection 시도하면...

<img width="1892" alt="스크린샷 2024-02-29 오후 8 39 45" src="https://github.com/Moon-GD/hello-java-spring/assets/74173976/e603d3b0-5bf6-4613-9373-692e18b8b2de">

- 빈 화면이 이렇게 좋기는 오랜만이다... 😭😭

<br />

### 4️⃣ 신규 DB 및 사용자 계정 설정

- GUI 환경의 경우 DB 생성 버튼을 통해, CLI에서는 아래의 쿼리로 DB 생성!

```mysql
# DB 생성
CREATE DATABASE bootex;

# 혹시나 권한이 없다고 뜬다면, 해당 유저에게 권한을 먼저 부여
# 필요에 따라 `WITH GRANT OPTION`도 함께 부여
GRANT ALL PRIVILEGES ON *.* TO '사용자 이름'@'호스트' WITH GRANT OPTION;

# 권한 확인
SHOW GRANTS;

# 혹은 특정 유저의 권한 확인
SHOW GRANTS FOR '사용자 이름'@'호스트';
```

<br />

## 2.2 Spring Data JPA를 이용하는 프로젝트의 생성

### 1️⃣ 프로젝트 생성

- 위에서 설정해준 MariaDB와 연결해주기 위한 스프링 프로젝트 생성
- 책과 같이 Spring Initilaizr에서 아래의 의존성을 추가
    - Spring Boot DevTools
    - Lombok
    - Spring Web
    - Spring Data JPA
    - MariaDB Driver

- 이후, 메인 메서드를 실행해보면 에러 발생!

```shell
Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
Reason: Failed to determine a suitable driver class
```

- 에러 요약: 스프링 부트의 Auto Configuration으로 의존성은 추가해주었으나, MariaDB 드라이버의 datasource url이 작성되지 않아서 발생
- 이를 해결하기 위해서는 아래의 2가지 설정이 필요

| 필요한 설정 | 내용 |
|:-|:-|
| Spring 프로젝트에서 바라볼 JDBC 드라이버 (현재는 MariaDB 전용 JDBC 드라이버) | - Spring Initializr로 의존성 추가해주면서 자동으로 설정됨<br/> - 혹시나 설정이 필요하다면 <a href="https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client">maven 공식 문서</a>가서 필요한 설정 가져오기! |
| MariaDB JDBC 드라이버를 위한 설정 | - 드라이버 이름, 주소, 계정 정보 <br/> - 일반적으로 드라이버의 설정 파일 작성은 application.properties나 별도의 YAML파일을 두어서 관리! |

- `application.properties`에 추가해 준 내용
```
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/bootex
spring.datasource.username=bootuser
spring.datasource.password=bootuser
```

<br />

## 2.3 Spring Data JPA의 소개

### 1️⃣ ORM & JPA

- ORM 개념의 등장
- `객체 지향 구조`가 `관계형 데이터베이스`와 비슷하다는 데에서 착안된 개념

|           | 객체 지향 구조                        | 관계형 데이터베이스                     |
|:----------|:--------------------------------|:-------------------------------|
| 데이터 설계는   | class 설계                        | table 설계                       |
| 데이터 구현체   | instance: 행위와 값을 지닌 객체 (object) | row: 칼럼에 알맞은 값을 가진 개체 (entity) |
| 데이터 표현 방식 | instance 간의 참조를 통해서             | table간의 관계를 통해서                |

- 여기서, 객체 지향을 관계형 데이터베이스에 맞추어 처리해주는 기법을 고안하게 되었고 이것이 ORM을 일컫는다
- ORM은 특정 언어나 데이터베이스의 종류에 국한되는 것이 아니라 **객체 지향과 관계형 사이의 변환 기법**을 의미
- 이 중, JPA는 Java에 맞게 사용되는 스펙이며 해당 스펙을 구현하는 회사마다 프레임워크의 이름이 달라짐 (Hibernates, Eclipselink ...)
  - 스프링 부트는 `Hibernates`를 이용
  - 프로젝트 생성할 때 추가해준 `Spring Data JPA`는 스프링 부트에서 Hibernates를 쉽게 이용할 수 있도록 API 제공

- 흐름을 정리하면...
- Spring Data JPA ↔ Hibernates ↔ JDBC ↔ DB 

<br />

## 2.4 엔티티 클래스와 JpaRepository

- Spring Data JPA 사용 방식을 살펴보기 위해서 간단한 메모 예제 진행
- Memo 클래스 정의

```java
// project/entity/Memo.class

@Entity  // Spring Data JPA에게 해당 클래스가 엔티티 클래스임을 알려주는 것
@Table(name="tbl_memo")  // DB 테이블의 이름, 인덱스, 스키마 등 지정
@ToString  // lombok에서 자동으로 생성해주는 toString 메서드 활용을 위함
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Memo {
    
    @Id  // 테이블의 PK로 사용되는 멤버 변수임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // PK 생성 전략 명시
    private Long mno;
}
```

- @GenerationType의 키 생성 방식

| 값              | 내용                                                                                                 |
|:---------------|:---------------------------------------------------------------------------------------------------|
| AUTO (default) | Hibernates에게 생성 방식을 결정하도록 위임                                                                       |
| IDENTITY       | 사용하게 되는 DB의 키 생성 방식 활용<ul><li>Oracle: 별도 시퀀스 관리</li><li> MySQL, MariaDB : auto increment</li></ul> |
| SEQUENCE       | 데이터베이스의 sequence를 이용 <br /> @SequenceGenreator와 같이 사용                                              |
| TABLE          | 키 전용 생성 테이블을 생성해서 키 생성 <br /> @TableGenreator와 같이 사용                                               |

- 어노테이션 설명
  - 1️⃣ `@Builder`
    - 객체를 생성할 때 빌더 패턴을 사용하여 가독성을 높일 수 있도록 빌더를 생성하는 메서드를 자동 생성
    - 객체 생성이기 때문에 자연스럽게 생성자 관련 어노테이션을 함께 작성해주지 않는다면, 컴파일 에러 발생
    
    ```java
      User user = new User.builder().age(27).name('moon').build();
    ```

  - 2️⃣ `@NoArgsConstructor`, `@AllArgsConstructor`
    - 생성자 메서드를 자동으로 생성
  
    ```java
        public class User {
            int age;
            String name;
    
            // @NoArgsConstructor 사용은 아래와 같다
            public User() {}
            
            // @AllArgsConstructor 사용은 아래와 같다
            public User(int age, String name) {
                this.age = age;
                this.name = name;
            }
        }
    ```
    
  - 3️⃣ `@Getter`
    - 클래스의 멤버 변수에 대해서 getter 메서드를 자동으로 생성
    

- JPA를 사용함에 따라 발생하는 SQL 확인을 위해 `application.properties`에 아래의 내용 추가

```properties
# 애플리케이션이 시작될 때 엔티티 클래스에 따라 데이터베이스의 테이블을 만들거나 수정하도록
## 테이블이 존재하지 않는다면? 생성
## 엔티티 클래스의 변화가 감지되었다면? 테이블의 구조 변경
## 다만, 기존 데이터는 유지 + 추가된 column에 대한 데이터는 데이터베이스의 기본 설정을 따른다 (대부분, null 삽입)
spring.jpa.hibernate.ddl-auto=update

# Hibernates가 발생시키는 sql을 포맷팅시켜서 출력
spring.jpa.properties.hibernate.format_sql=true

# JPA 처리 시에 발생하는 SQL을 보여주도록 명시
spring.jpa.show-sql=true
```

- 애플리케이션을 실행하면 아래의 console 확인 가능!
- 사진 첨부하기

<hr />

#### updated: 2024.03.04 (Mon)

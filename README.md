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
<img width="400" alt="hibernates ddl update 사진" src="https://github.com/Moon-GD/hello-java-spring/assets/74173976/e7fd4ed4-8dfb-4005-bfff-8f4425df519c">

### JPA 활용
- Spring Data JPA를 의존성으로 추가하며 JPA 관련 작업을 별도 코드 없이 처리할 수 있음
- 제공되는 `JpaRepository`를 상속하는 인터페이스를 선언하기만 하면, 스프링 부트에서 CRUD, Paging, Sort 등을 구현한 스프링 빈을 등록!
  - 참고로 상속 순서는 다음과 같다
  - Repository → CrudRepository → PagingAndSortRepository → JpaRepository
 
- JpaRepository를 확장한 인터페이스를 선언할 때는 엔티티와 @Id의 타입 정보를 전달해야 함

```java
// project/repository/MemoRepository.java

// <엔티티, @Id> 전달
public interface MemoRepository extends JpaRepository<Memo, Long> {
}
```

### JPA Repository 지원 기능 (feat. CrudRepository 기능 활용해보기)

| 작업 구분  | 명세                         | CRUD 구분 |
|:-------|:---------------------------|:--------|
| insert | save(엔티티)                  | C       |
| select | findById(키), getOne(키)     | R       |
| update | save(엔티티)                  | U       |
| delete | deleteById(키), delete(엔티티) | D       |

- JPA에는 `EntityManager`가 엔티티를 DB에 접근하고 관리하는 방식이 정의됨
- `Entity Manager`가 메모리 상에서 객체를 비교하고 없다면 추가, 존재한다면 수정하는 방식으로 동작하기 때문에 C와 U 작업에 대해 동일한 save 명세가 붙은 것!

- ※ 참고 ※

- findById, getOne 비교

  | 메소드명        | 반환 값                     | 탐색 실패할 때 상황                | 참고         | 
  |:------------|:-------------------------|:---------------------------|:-----------|
  | findById(키) | DB에서 조회된 실제 엔티티          | Optional.empty() 반환        |            |
  | getOne(키)   | 엔티티에 대한 참조값을 포함하는 프록시 객체 | EntityNotFoundException 반환 | deprecated |

- deleteById, delete 비교

  | 메소드명          | 반환 값 | 탐색 실패할 때 상황                       | 참고                                                       | 
    |:--------------|:-----|:----------------------------------|:---------------------------------------------------------|
  | deleteById(키) | void | EmptyResultDataAccessException 발생 | 내부에 delete가 사용됨 <br/> 로직: 삭제하고자 하는 데이터 조회 → delete(키) 호출 |
  | delete(키)     | void | 아무 일도 일어나지 않는다                    |                                                          |


## 2.5 페이징 / 정렬 처리하기

### 스프링에서 DB와 소통하는 방법

- Hibernates 내부 `Dialect`를 통해 직접 query를 작성하지 않고도 원하는 DB와 소통 가능 → DB 종류에 따라 개발자가 별도 학습을 해야하는 수고로움을 덜어줌!
- JPA는 상위 레포지토리 `PagingAndSortRepository`의 메소드 findAll()을 통해 페이징과 정렬을 처리
- findAll() 메소드는 Page, PageRequest, Sort 객체와 엮여있음

### 페이징 처리하기

```java
// MemoRepositoryTests.java의 일부

import java.awt.print.Pageable;

@Test
public void pageTest() {
  Pageable pageable = PageRequest.of(0, 10);
  Page<Memo> memoPage = memoRepository.findAll(pageable);

  System.out.println("memoPage: ", memoPage);  // memoPage: Page 1 of 10 containing com.hellospring.ex2.entity.Memo instances
}
```

```java
Page<Memo> memoPage = memoRepository.findAll(pageable);
```

- 위의 코드가 흥미로운 이유는, Page 타입으로 반환이 되기 때문!
- Page 타입은 엔티티의 리스트를 담아오는 것뿐만 아니라, 전체 데이터의 개수도 포함해서 가져옴
- 그리고 Page 타입은 아래를 포함한 여러 메소드를 지원한다

| 메소드명               | 내용             |
|:-------------------|:---------------|
| getTotalElements() | 전체 엔티티 반환      |
| getTotalPages()    | 전체 페이지 수 반환    |
| getSize()          | 페이지별 데이터 개수 출력 |
| hasNext()          | 다음 페이지 존재 여부   |
| isFirst()          | 첫 번째 페이지 여부    |

### 정렬 처리하기

- 페이징 처리에 Sort 객체를 통해 정렬 정보를 전달

```java
// Page And Sort 예시

@Test
public void pageAndSortTest() {
  Sort sort = Sort.by("id").descending();
  Pageable pageable = PageRequest.of(0, 10, sort);  // 정렬 객체 정보를 함께 전달
  
  Page<Memo> memoPage = memoRepository.findAll(pageable);
}
```

## 2.6 쿼리 메서드 (Query Methods) 기능과 @Query
### Query Methods 사용해보기
- 다양한 검색 조건을 충족하기 위해 Spring Data JPA의 경우 다음의 방법을 제공

| 방법       | 내용                                                 | 비고                                                                                                                                                                                     |
|:---------|:---------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 쿼리 메서드   | 메서드 이름을 통해 쿼리를 유추하는 흥미로운 방식                        | JpaRepository를 확장한 레포지토리 내부에 메서드를 선언하면 자동으로 SQL문이 유추됨<br/><a href="https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation">공식 문서</a> |
| @Query   | JpaRepository를 확장한 repository 내부에 커스텀으로 쿼리를 작성하는 것 | 쿼리를 직접 작성하는 형태                                                                                                                                                                         |
| Querydsl | JpaReposiotry를 확장할 때 인자 정보로 사용할 동적 쿼리를 전달해주는 것     |                                                                                                                                                                                        |

### Query Methods 사용해보기 - Pageable를 통한 정렬 조건 추가
- Query Method에서 정렬 조건을 추가하면 메서드 이름이 길어지며 가독성이 떨어짐!
- Query Method의 모든 메서드가 Pageable 객체를 인자로 전달받을 수 있음을 활용해서 정렬과 관련된 부분은 페이지 객체로 처리 → QueryMethod 이름이 짧아짐

### Query Methods 사용해보기 - 삭제 작업 (D)
- 기본적으로 Spring 테스트 코드는 실행된 이후 롤백됨!
- 삭제 작업의 경우 두 개의 어노테이션을 추가하여 DB 상에서 실제로 데이터가 삭제되는지 체크 가능
- `@Transactional`, `@Commit`

| 어노테이션 이름         | 역할                                |
|:-----------------|:----------------------------------|
| `@Transactional` | 테스트 코드에 포함된 Jpa 관련 코드를 트랜잭션에 포함시킴 |
| `@Commit`        | 트랜잭션 내용을 반영시키는 역할                 |

- 실제로, 동일한 테스트 코드에 대해서 @Commit 포함 여부에 따라 DB 반영 여부가 달라짐을 확인할 수 있었다
  - `@Commit` 작성: 실제 DB에 반영
    - <img width="250" alt="@Commit 포함" src="https://github.com/Moon-GD/hello-java-spring/assets/74173976/355c1de8-8777-4a8f-8118-1e4eec5cbe05" />
  - `@Commit` 제거: 테스트 코드 롤백으로 실제 DB에 반영 ❌
    - <img width="250" alt="@Commit 제거" src="https://github.com/Moon-GD/hello-java-spring/assets/74173976/5247a4ec-1e71-41ff-bdd2-71936407d714" />


<hr />

#### updated: 2024.03.16 (Sat)

# Chapter 01. 프로젝트를 위한 준비

## Hello World 프로젝트 jar로 압축하고, 자바 환경에서 실행하기
- IntelliJ gradle 텝에서 `bootJar` 파일 확인
- 이미지 삽입
- 파일 실행하면 프로젝트 `build/libs` 하위에 jar 확장자를 가진 파일이 생성된 것을 확인 가능

```shell
# 자바가 실행 가능한 경로의 터미널에서
$ java -jar test.jar

# 터미널에서 스프링 프로젝트가 구동되는 것을 확인!
```

## 💡 배운 내용
### 1. 빌드 도구 양대 산맥
- 1️⃣ Gradle
  - <img src="https://img.shields.io/badge/Apache Groovy-4298B8?style=flat&logo=Apache Groovy&logoColor=white"/> 기반
  - 일반적으로 사용되는 빌드 도구
  - 장점
   - Maven에 비해 상대적으로 유연하고 간결한 빌드 스크립트 제공
   - 대규모 프로젝트에서 빌드 시간을 단축하는데에 도움이 된다 
  - 단점
   - 상대적으로 높은 학습 곡선

- 2️⃣ Maven
  - `xml` 기반의 프로젝트 관리 도구
  - 장점
    - 상대적으로 간단한 설정 파일
    - 표준화된 구조를 통해 프로젝트의 일관성과 유지 보수성을 향상
    - 더 오래 사용되며 지원되는 플러그인, 참고할 자료 등이 많다
  - 단점
    - `xml` 코드는 번잡해보일 수 있다
    - 상대적으로 부족한 유연성

### 2. Lombok
- `java` 소스 코드의 중복을 줄이고 개발자 생산성을 높이기 위해 만들어진 라이브러리
- 어노테이션 기반의 편의성 제공 (@Getter, @Setter, @ToString, ...)

### 3. Banner
- `Spring`은 `src/main/resources/banner.txt`가 존재하면 해당 배너 파일을 서버를 구동할 때 출력한다 

### 4. 스프링 부트가 자동으로 설정해주는 부분
- Spring에서 JSON을 사용하기 위해서는 `Jackson-databind`와 같은 라이브러리가 필요
- Spring Boot에서는 이니셜라이저로 프로젝트를 생성할 때 `spring-web`을 의존성에 추가하면 관련 라이브러리가 자동으로 추가된다

### 5. @RestController
- HTTP 요청에 대한 응답으로 JSON, XML 등의 데이터를 반환하는 기능 추가
- @RestController = @Controller + @ResponseBody

<hr />

#### updated: 2024.02.21 (Wed)
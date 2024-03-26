# Chapter 03. 스프링 MVC와 Thymeleaf

<br />

## 3.1 Thymeleaf를 사용하는 프로젝트 생성

- 기본 html 구조를 훼손할 우려가 있는 jsp에 비해 학습 허들도 낮고 활용도 좋은 Thymeleaf를 템플릿 엔진으로 선정!
- Thymeleaf는 요청 시간에 서버 측에서 HTML을 생성하여 클라이언트로 전달
- 실시간 업데이트를 확인하기 위해 Thymeleaf 캐시 설정 꺼두고 시작!

```groovy
// application.properties
spring.thymeleaf.cache=false
```

- Thymeleaf 템플릿을 활용하기 위한 Sample Controller 구성
- `@Log4j2`는 처음 봐서 낯설었는데, 어노테이션이 붙어 있는 클래스 내부에 Logger 객체를 주입해주는 것 같았다.

```java
@Controller  // mvc 중 c 역할임을 명시하고 정의
@RequestMapping("/sample")  // 요청에 대한 기본 URL 설정
@Log4j2  // Log4j2 라이브러리의 로그 객체를 주입
public class SampleController {

    @GetMapping("/ex1")
    public void ex1() {
        log.info("ex1.....");
        
        return "sample/ex1";
    }
}
```

- 이후, 컨트롤러에서 반환해줄 Thymeleaf 템플릿 작성
- 기본적으로 Thymeleaf는 `project 기본 경로/main/resources/templates` 폴더를 사용!

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head> 상세 내용 생략... </head>
<body>
    <h1 th:text='${"Hello World"}'></h1>
</body>
</html>
```

## 3.2 Thymeleaf의 기본 사용법

### 1️⃣ DTO 구성
- Thymeleaf 템플릿 예제에 필요한 샘플 DTO 구성

```java
// dto/SampleDto

@Data  // lombok 어노테이션으로, getter/setter, toString(), equals() 등을 자동으로 생성
@Builder(toBuilder = true)  // toBuilder: 빌더 패턴으로 생성된 객체를 활용하여 새로운 객체를 구성할 수 있도록 설정
public class SampleDto {
    private Long sno;

    private String first;

    private String last;

    private LocalDateTime regTime;
}
```

- TODO: 템플릿 파일 구성한 부분부터 정리 다시하기

<hr />

#### updated: 2024.03.26 (Tue)

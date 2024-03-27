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

### 3.2.1 반복문 처리
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

- Dto 객체 List에 담아 전달할 수 있도록 컨트롤러 코드 작성

```java
// SampleController.java
@GetMapping("ex2")
public String ex2(Model model) {
    log.info("ex2.....");

    List<SampleDto> list = LongStream.rangeClosed(1, 20).mapToObj(
            i ->  SampleDto.builder()
                    .sno(i)
                    .first("First..." + i)
                    .last("Last..."+i)
                    .regTime(LocalDateTime.now())
                    .build()
    ).toList();

    model.addAttribute("list", list);

    return "sample/ex2";
}
```

- Model에서 넘긴 정보 받아서 구성할 수 있도록 Thymeleaf 템플릿 코드 작성
- `th:each`를 활용하면 state라는 객체를 활용할 수 있음!
  - state.index: 0부터 시작
  - state.count: 1부터 시작

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    head 내용 생략
</head>
<body>
    <ul>
        <li th:each="dto, state: ${list}">
            [[${dto}]]
        </li>
    </ul>
</body>
</html>
```

### 3.2.2 제어문 처리

1️⃣if, else 활용

- th:if, th:else 등을 활용해서 작성 가능
- JS처럼 삼항 연산자도 활용 가능

### 3.2.3 inline 속성

- 리다이렉션 + inline 속성을 활용하여 컨트롤러 코드 작성
  - 리다이렉션
    - `RedirectAttributes` 활용: 리다이렉션이 발생했을 때 속성을 전달하기 위해 Model 처럼 사용
    - `RedirectAttributes`의 `addFlashAttribute`는 리다이렉션 이후에만 사용할 속성을 정의하는 것
  - inline 속성
    - script 태그를 활용한 부분을 개발자 도구에서 살펴보면 JS에 맞게 변환되어서 전달됨!

```java
@GetMapping("/exInline")
public String exInline(RedirectAttributes redirectAttributes) { 
    log.info("exInline.....");

    SampleDto sampleDto = SampleDto.builder().
            sno(100L).
            first("First...").
            last("Last...").
            regTime(LocalDateTime.now()).
            build();
    
    redirectAttributes.addFlashAttribute("result", "success");
    redirectAttributes.addFlashAttribute("dto", sampleDto);

    return "redirect:/sample/ex3";
}

@GetMapping("/ex3")
public void ex3() {
    log.info("ex3.....");
    
    // 반환하는 문자열이 없으면 스프링은 URL 경로 기반으로 템플릿을 탐색해서 반환
}
```

- 템플릿 코드 작성
  - th:block를 활용하면 React의 Fragment 처럼 빈 태그의 역할을 수행하지만, 실제 html로 변환되었을 때는 아무런 태그도 되지 않는다!

```html
<!doctype html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        head 내용 생략
    </head>
    <body>
        <th:block th:text="${result}"/>
        <th:block th:text="${dto}"/>

        <script>
            const msg = [[${result}]];
            const dto = [[${dto}]];
    
            console.log("msg: ", msg);
            console.log("dto: ", dto);
        </script>
    </body>
</html>
```

- 즉, 위의 코드는 아래와 같다

```html
<!doctype html>
    <html lang="en">
    <head>
        head 내용 생략
    </head>
    <body>
        success
        SampleDto(sno=100, first=First..., last=Last..., regTime=2024-03-27T20:47:39.754038)
    
        <script>내용 생략...</script>
    </body>
</html>
```

### 3.2.4 링크 처리

- Thymeleaf는 `@{}`를 활용하여 링크 처리
- 여러 개의 링크를 확인하기 위해 기존 컨트롤러 ex2()에서 exLink도 처리하도록 구성

```java
// SampleController.java

// As-Is
@GetMapping("/ex2")
public String ex2(Model model) {}

// To-Be
@GetMapping({"/ex2", "exLink"})
public String ex2(Model model) {}
```

- 이후, 아래와 같이 템플릿 작성하며 query string 방식과 segment 방식 모두 활용해보기!

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    생략...
</head>
  <body>
      <ul>
          <!-- query string 방식-->
          <h1 th:text="${'query string 방식'}"/>
          <li th:each="dto: ${list}">
              <a th:href="@{/sample/someHtmlURL(sno=${dto.getSno()})}">[[${dto}]]</a>
          </li>
  
          <!-- segment 방식-->
          <h1 th:text="${'segment 방식'}"/>
          <li th:each="dto: ${list}">
              <a th:href="@{/sample/someHtmlURL/{sno}(sno = ${dto.getSno()})}">[[${dto}]]</a>
          </li>
      </ul>
  </body>
</html>
```

## 3.3 Thymeleaf의 기본 객체와 LocalDateTime

- 정리 예정

<hr />

#### updated: 2024.03.27 (Wed)

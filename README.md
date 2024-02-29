# Chapter 02. Maria DB와 Spring Data JPA

## 2.1 MariaDB 설치

- 크게 GUI, CLI의 2가지 설치 방법이 존재하지만..
- ❗한 가지 중요한 것은 root 계정을 설정하게 되면 비밀번호를 꼭 기억하는 것! ❗️

### 1️⃣ GUI 기반
- <a href="https://mariadb.org/">공식 홈페이지</a>로!

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

### 3️⃣ Sequel Ace 연결

- 프로그램 실행 후 아래와 같이 기입...

- 사진 첨부하기

- 그리고 Connect 시도하면 connection error가 발생!
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

- 사진 첨부!

<hr />

#### updated: 2024.02.24 (Sat)

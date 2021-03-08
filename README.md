# Java-ORM-Standard-JPA
자바 ORM 표준 JPA 학습공간입니다.



## SQL 중심적인 개발의 문제점

### 객체와 관계형 데이터베이스의 차이

1. 상속 (객체: 상속 관계 - Table: 슈퍼타입 서브타입 관계)
2. 연관관계 (객체: 참조 - 테이블: 외래키)
3. 데이터 타입
4. 데이터 식별 방법

**자바 컬렉션의 조회 객체와 SQL을 통한 조회는 다른 결과를 일으킨다.**




## JPA가 무엇인가?

### JPA: Java Persistence API

자바 진영의 **ORM** 기술 표준

애플리케이션과 JDBC 사이에서 동작(개발자가 Query를 작성할 필요가 없음)

EJB(Entity Bean 자바 표준) ➡ Hibernate(오픈 소스) ➡ JPA(자바 표준)



### ORM: Object-relational mapping(객체 관계 매핑)

ORM 프레임워크가 객체와 관계형 데이터베이스사이에서 연결고리가 된다.



### JPA를 왜 사용해야 하는가?

#### 생상성

- 저장: jpa.persist(member)
- 조회: Member member = jpa.find(memberId)

- 수정: member.setName("변경될 이름")
- 삭제: jpa.remove(member)

#### 자바 객체와 관계형 데이터베이스의 패러다임 불일치를 해결

- 관계형 데이터베이스의 Query를 자바 컬랙션 처럼 사용할 수 있다.

#### JPA의 성능 최적화 기능

1. 1차 캐시와 동일성(identity) 보장
2. 트랙잭션을 지원하는 쓰기 지연(transactional write-behind)
3. 지연 로딩(Lazy Loading)
   - 지연 로딩: 객체가 실제 사용될 때 로딩
   - 즉시 로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회



## JPA Project

### 프로젝트 생성

1. 데이터베이스 생성(H2 Database)
2. Maven 또는 Gradle Project 생성
3. 의존성 주입(JPA Hibernate: javax.persistence-api, H2 Database ) 
   JPA라는 인터페이스의 구현체로 Hibernate를 사용



### JPA 설정하기

- JPA 설정파일: `/META-INF/persistence.xml`
  - Dialect 속성: 데이터베이스 고유의 언어



### JPA 동작 확인

- JpaMain 클래스
  
  - `Persistence.createEntityManagerFactory{persistenceUnitName}`
  
- 객체와 테이블 매핑

  ```sql
  CREATE TABLE MEMBER (
      id bigint not null,
      name varchar(255),
      primary key (id)
  );
  ```
  
  ```java
  @Entity
  //@Table(name = "Member") 테이블명이 클래스명과 같으면 생략가능
  public class Member {
      @Id
      private Long id;
      //@Column(name = "username") 칼럼이 인스턴스변수와 같으면 생략가능
      private String name;
  
      public Long getId() {
          return id;
      }
  
      public void setId(Long id) {
          this.id = id;
      }
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  }
  ```

- 트랜잭션(작업의 단위)

  - 트랜잭션 얻기&시작: ``EntityTransaction tx = entityManager.getTransaction(); tx.begin();``
  - 트랜잭션 커밋: ``tx.commit();``
  



### 주의

- **EntityManagerFactory**는 하나만 생성해서 애플리케이션 전체에서 공유
- **EntityManager**는 쓰레드 간에 공유해서는 안된다.(사용하고 버려야함!)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**



### JPQL (테이블이 아닌 엔티티 객체 중심으로 쿼리를 사용할 수 있다.)

- SQL과 유사한 문법을 가짐, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원

```java
List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5) // pagination(페이징) 가능
                    .setMaxResults(8) // 5번 부터 8개 조회
                    .getResultList();
```



## 영속성 관리

### 영속성 컨텍스트

#### 엔티티의 생명주기

- 비영속: 객체를 생성한 상태, JPA와 연관없음
- 영속: 객체를 저장한 상태 ``entityManager.persist(Object)`` (단, DB에 저장한 시점은 아님)
- 준영속: 영속성 컨텍스트가 관리하지 않는 상태
- 삭제: 영속성 컨텍스트와 DB 두 곳에서 객체 삭제

#### 영속성 컨텍스트의 이점

- 1차 캐시
- 영속 엔티티의 동일성 보장
- 엔티티 등록(트랜잭션을 지원하는 쓰기 지연)
- 엔티티 수정(변경 감지:Dirty Checking) 



### 플러시

**영속성 컨텍스트의 변경내용을 데이터베이스에 반영/동기화**(1차 캐시가 지워지지 않음)
**트랙잭션이라는 작업 단위가  중요 ➡ 커밋 직전에만 동기화하면 됨**

#### 영속성 컨텍스트를 플러시하는 방법

```java
// 1. 강제 플러시
entityManager.flush();
// 2. commit 시 자동 플러시
entityTransaction.commit();
// 3. JPQL 쿼리 실행 시 자동으로 플러시 호출
query = entity.createQuery(sqlQuery);
List<Object> list = query.getResultList();
```

#### 플러시 모드 옵션

``entityManager.setFlushMode(FlushModeType.COMMIT)``

- FlushModeType.AUTO: 커밋이나 쿼리를 실행할 때 플러시(기본값)
- FlushModeType.COMMIT: 커밋할 때만 플러시



### 준영속 상태

**엔티티가 영속성 컨텍스트에서 분리(detached)**

**영속성 컨텍스트가 제공하는 기능을 사용할 수 없음**

```java
// 특정 엔티티만 분리
entityManager.detach(entity);
// 모든 엔티티에 대해 영속성 컨텍스트 초기화
entityManager.clear();
// 영속성 컨텍스트 종료
entityManager.close();
```



## 엔티티 매핑

### 객체와 테이블 매핑

#### @Entity

-	JPA가 관리하는 엔티티이다. 

- 주의: 기본 생성자 필수, final, enum, interface, inner 클래스에는 사용 불가

#### @Table

- @Table은 엔티티와 매핑할 테이블 지정



### 데이터베이스 스키마 자동 생성

- DDL을 애플리케이션 실행 시점에서 자동 생성

#### 주의: 운영 장비에는 절대 create, create-drop, update를 사용하면 안된다.

#### DDL 생성 기능

- @Column으로 각 컬럼에 제약조건을 설정할 수 있다.
- JPA의 실행 로직에는 영향을 주지 않는다.



### 필드와 컬럼 매핑

#### @Id

- 테이블 ID

#### @Column

- name: 필드와 매핑할 테이블의 칼럼 이름
- insertable, updateable: insert, update 반영에 대한 true/false
- nullable(DDL): not null에 대한 true/false
- unique(DDL): unique 제약 조건 true/false
- columnDefinition(DDL): 데이터베이스 칼럼 정보를 직접 줄 수 있다.(varchar(100), default 'EMPTY')
- length(DDL): 문자 길이의 제약조건, 엔티티의 String 타입에만 사용한다.
- precision, scale(DDL): 범위가 큰 숫자나 정밀한 소수점을 나타낼 때 사용

#### @Enumerated
- EnumType.ORDINAL: enum 순서를 DB에 저장 (Default)
- EnumType.STRING: enum 이름을 DB에 저장

#### @Temporal

- TemporalType.DATE, TemporalType.TIME, TemporalType.TIMESTAMP를 적용하여 사용한다.
- java 8 기준으로 LocolDate, LocalDateTime 타입이 추가되어 @Temporal를 사용하지 않아도 된다.

#### @Lob
- 길이가 긴 문자열(큰 컨텐츠)
- 문자열은 CLOB, 나머지는 BLOB으로 매핑된다.

#### @Transient

- 엔티티 매핑에서 제외할 인스턴스 변수



### 기본 키 매핑


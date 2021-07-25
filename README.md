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

#### 직접 할당: @Id

#### 자동 생성(@GeneratedValue strategy)

- IDENTITY: 데이터베이스에 위임
  - DB에 들어가기 전 까지 인덱스를 알 수 없다.
  - 영속성 컨텍스트는 PK를 가지고 있어야한다.
  - 결국 영속성 상태 전에 INSERT Query를 먼저 날린다.
- SEQUENCE - @SequenceGenerator (데이터베이스 오브젝트로 관리)
  - 마찬가지로 영속성 컨텍스트에 PK 필요하기 때문에 DB에서 시퀀스를 찾아온다.
  - 시퀀스를 찾는 과정에서 INSERT Query를 하지 않아도 된다.
  - ``allocationSize = 50 `` 설정값 만큼 시퀀스를 미리 확보(매 번 DB에서 조회하는 번거로움을 줄여줌)
- TABLE - @TableGenerator (데이터베이스 테이블로 관리 - 키 전용 테이블)

#### 권장하는 식별자 전략

- **기본 키 제약 조건**: null이 아님, 유일하고 변하면 안된다.
  - 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. (대리키 또는 대체키를 사용할 것을 권장한다.)
  - 예) 주민등록번호도 기본 키로 적절하지 않다. (비즈니스에 사용되는 값을 키로 사용하지 말자!)
  - **권장: Long형 + 대체키 + 키 생성전략 사용**



## 연관관계 매핑 기초

### 단방향 연관관계

```java
// Member Entity에서 작성 시
@ManyToOne // Member와 Team의 관계를 N(Member) : 1(Team)로 연관 짓는다.
@JoinColumn(name = "TEAM_ID") // TEAM_ID가 외래키 역할임을 JoinColumn으로 명시한다.
private Team team; // 즉, Member 테이블의 외래키 TEAM.TEAM_ID 테이블 연관관계를 객체 연관관계로 보여준다.
```

- 테이블 연관관계를 객체 연관관계로 모델링했다.



### 양방향 연관관계와 연관관계의 주인

```java
// Team Entity에서 작성 시 
// mappedBy에 양방향 연관관계을 맺을 Column 혹은 엔티티 멤버변수를 등록
@OneToMany(mappedBy = "team") // Member.team
private List<Member> members = new ArrayList<>();
```

- mappedBy: 

#### 객체와 테이블이 관계를 맺는 차이

- 객체 연관관계 = 2개
  - 회원 ➡ 팀 연관관계 1개(단방향)
  - 팀 ➡ 회원 연관관계 1개(단방향)
- 테이블 연관관계 = 1개
  - 회원 ↔ 팀의  연관관계 1개(양방향)

- **양방향 연관관계는 단방향 연관관계 2개이다.**
- 그렇다면 두 엔티티 중 **외래키를 갖는 엔티티**는 누구인가?
  - 연관관계의 주인(Owner)를 정한다.

#### 연관관계의 주인(Owner)

- **연관관계의 주인만이 외래 키를 관리(등록, 수정)**
- **주인이 아닌쪽은 읽기만 가능**
- 주인은 mappedBy 속성을 사용해서는 안됨(mappedBy는 read만 하기 때문)
- 주인이 아닌 엔티티는 mappedBy를 사용하여 연관관계의 주인을 지정(외래키)
- 누구를 연관관계의 주인(Onwer)로 할 것인가?
  - 외래 키가 있는 곳을 주인으로 할 것(**테이블 연관관계에서 외래 키를 가지고 있는 테이블에 대한 엔티티**) 

#### 주의점

- 역방향으로 연관관계를 설정할 경우 참조되지 않음

  ```java
  // 연관관계의 주인이 Member인 경우
  Team team = new Team();
  team.setName("teamA");
  em.persist(team);
  
  Member member = new Memeber();
  member.setName("memberA");
  
  // 역방향(주인이 아닌 방향)만 연관관계가 설정된 경우
  team.getMembers().add(member);
  
  // member.setTeam(team); 정방향으로 해주어야 함, Member가 주인이기 때문
  
  em.persist(member);
  ```

- 정방향 연관관계 뿐만 아니라 **양쪽 모두 값을 설정**해주어야 함

  ```java
  // 연관관계의 주인이 Member인 경우
  Team team = new Team();
  team.setName("teamA");
  em.persist(team);
  
  Member meber = new Memeber();
  member.setName("memberA");
  
  // 정방향에서 연관관계 설정
  member.setTeam(team);
  em.persist(member);
  
  // 정방향에서 설정된 연관관계에 따라서 값을 저장
  team.getMembers().add(member);
  ```

- 권장 방법: 연관관계에서 양쪽 값 모두 설정하는 연관관계 편의 메서드 사용

  ```java
  // 두 연관관계 중 하나의 메서드만 사용하는 것을 권장
  @Entity(name = "MEMBER")
  public class Member {
  	/* Columns */
      // 연관관계 편의 
  	public void changeTeam(Team team) { 
  		this.team = team;
  		team.getMembers().add(this);
  	}
  }
  
  @Entity(name = "TEAM")
  public class Team {
  	/* Columns */
      // 연관관계 편의
      public void addMember(MemberT member) {
          member.setTeam(this);
          this.members.add(member);
      }
  }
  ```

- 무한루프를 조심해야 한다.

  - 조회할 때 양쪽에서 서로 참조하여 무한루프에 빠질 수 있다!
  - 예) toString(), lombok, JSON 생성 라이브러리(컨트롤러에서 엔티티 자체를 반환하지 말자! 대안으로 DTO를 반환)

#### 양방향 매핑 정리

- **단방향 매핑만으로도 이미 연관관계 매핑은 완료**(단방향 매핑 이후 더 이상 테이블에 영향을 주지 않음)
- 양방향 매핑을 필요할 때 추가해주면 된다. (JPQL 역방향 탐색)
- 연관관계의 주인을 정하는 기준
  - **연관관계의 주인은 외래 키의 위치를 기준으로 정해야 한다.** (비즈니스 로직 X)



## 다양한 연관관계 매핑

| 테이블                        | 객체                                             |
| ----------------------------- | ------------------------------------------------ |
| 외래 키 하나로 양쪽 조인 가능 | 참조용 필드가 있는 쪽으로만 참조 가능            |
| 사실 방향이라는 개념이 없음   | 한쪽만 참조하면 단방향                           |
|                               | 양쪽이 서로 참조(단방향 두 개, 양방향 같아 보임) |

#### 연관관계의 주인

- 테이블은 **외래 키 하나로 두 테이블이 연관관계**를 맺음

- 연관관계의 주인: 외래 키를 관리하는 참조
- 주인의 반대편: 외래 키에 영향을 주지 않음, 단순 조회만 가능(``mappedBy="field"``)

### 다대일 [N:1] : @ManyToOne

- 외래 키가 있는 쪽이 연관관계의 주인

- 양쪽을 서로 참조하도록 개발

### 일대다 [1:N] : @OneToMany

- 일(1)이 연관관계 주인이 되지만, 테이블 상에선 여전히 외래 키가 다(N) 쪽에 존재

- 외래 키가 존재하는 테이블에 대한 update query문이 한 번 더 발생(권장하지 않음,  다대일을 권장)

  #### 일대다 양방향

  연관관계 주인이 아닌 엔티티에서 ``@ManyToOne``

  ``@JoinColumn(name = "", insertable = false, updatable = false)``: 읽기 전용으로 조인

### 일대일 [1:1] : @OneToOne

- 외래 키를 주 테이블과 대상 테이블 중에 선택해도 무방하다.
  (상대적으로 주 테이블은 대상 테이블보다 접근이 많은 테이블)
- 주 테이블에 외래 키가 있는 경우(권장하는 방법)
  - 다대일 연관관계를 단방향으로 설정한 것과 같음
- 대상 테이블에 외래 키가 있는 경우
  - 단방향은 지원하지 않음
  - 양방향은 가능, 결국 주 테이블의 외래 키를 연관관계 주인이 직접 관리

### 다대다 [N:N] : @ManyToMany

- 실무에서 쓰면 안됨!
- 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없음
  연결 테이블을 추가하여 일대다, 다대일 관계로 풀어내야 함(연결 테이블을 엔티티로 사용하여 대체)
  - 연결 테이블로 인해 쿼리가 복잡해지고, 성능에도 악영향을 미친다.
- 객체는 다대다 관계가 가능



## 고급 매핑

### 상속관계 매핑

- 관계형 데이터베이스는 상속 관계가 없음
- 슈퍼타입 서브타입 관계와 유사함

#### 전략

- 조인 전략: 부모 클래스에 해당하는 테이블과 자식 클래스에 해당하는 테이블을 조인

  ```java
  @Inheritance(strategy = InheritanceType.JOINED)
  ```

  - 부모 클래스에 해당하는 테이블에 DTYPE Column 권장 (``@DiscriminatorColumn``)
  - ``@DiscriminatorValue("name")``: name을 수정하여 DTYPE의 값을 정할 수 있다.
  - 가장 많이 사용되는 전략이다. (단, 조인을 많이 사용할 수록 성능이 저하된다.)

- 단일 테이블 전략: 자식 클래스들의 모든 필드를 가지는 하나의 테이블 구현

  ```java
  @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
  ```

  - 단일 테이블의 경우 DTYPE Column은 필수(``@DiscriminatorColumn``을 생략해도 생성됨)
  - 조인을 하지 않기 때문에 성능이 좋다. (단, 테이블이 많이지면 성능을 기대하기 어려움)
  - 테이블이 단순하다면 추천한다.

- 클래스 단위 테이블 전략: 자식 클래스마다 테이블 구현

  ```java
  @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
  ```

  - 복잡한 쿼리를 유발할 수 있는 단점이 존재
    (다형성을 이용하여 조회할 경우 Union을 사용하여 모든 테이블을 조회하기 때문에 비효율적임)
  - DBA와 ORM 설계자 모두 **권장하지 않음**



### Mapped SuperClass - 매핑 정보 상속

#### @MappedSuperclass

- 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할을 한다.

- 상속관계 매핑이 아니다. (당연히 엔티티도 없으며, 테이블과 매핑되지 않는다.)
  - 조회 할 수 없다.
- 객체를 인스턴스화 하지  않음으로 추상 클래스를 권장한다.

| 상속관계 매핑 | 매핑 정보 상속    |
| ------------- | ----------------- |
| @Entity       | @MappedSuperclass |



## 프록시와 연관관계 관리

### 프록시

- ``em.getReference()``는 데이터베이스 조회를  미루는 가짜(프록시) 엔티티 객체를 조회
  - 실제 클래스를 상속 받아서 만들어짐.
  - 데이터베이스에서 조회한 실제 객체를 참조(target)를 보관

- 프록시 객체의 초기화
  - 초기화 요청 시 영속성 컨테스트에서 만들어진 실제 객체를 참조

#### 프록시의 특징

- 프록시 객체는 처음 사용할 때 한 번만 초기화 
- 프록시 객체는 초기화 시 실제 객체가 되지 않는다. (다만, 타겟에 실제 객체를 참조 할 뿐임.)
  - 타입 체크 시 ``instance of`` 사용 필수! (Type 비교에 ``==``는 사용하지 않는다.)


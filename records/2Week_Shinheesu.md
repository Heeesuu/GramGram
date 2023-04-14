# 1Week_Shinheesu.md

## Title: [2Week] 신희수

### 미션 요구사항 분석 & 체크리스트

* **미션요구사항 분석**
</br>
2주차 필수 미션 요구 기능
</br>
1 . 한명의 인스타 회원이 11명 이상의 호감상대를 등록할 수 없다.</br>
2 . 한명의 인스타회원이 한 번 등록한 인스타회원을 중복으로 호감표시를 할 수 없다.</br>
3 . Id가 중복되더라도 매력포인트가 다르면 수정 가능하다.</br>
</br>
위 기능들을 구현하기 위해서 저장할 때 호감목록 ID의 수가 11이상인지 확인한다. 11이상일때 rq.historyback을 이용하여 오류메시지를 반환한다.
11이하 일때 호감목록에 동일한 ID가 있는지 확인하고 없다면 저장한다. 있다면 호감사유도 같은지 확인한다.
같다면 rq.historyback으로 오류메시지를 반환한다. 같지 않다면 호감사유만 수정하여 저장한다.
</br>
</br>

* **체크리스트**
</br>
- -[x] 호감ID 저장할때 호감목록의 ID의 수가 10개 이상인지 확인하고 10개 이상일때 rq.historyback
- -[x] 호감ID가 10개 이하일때 호감목록에 등록하는 ID와 동일한 ID가 있는지 확인하고 없다면 저장
- -[x] 호감목록에 등록하는 ID와 동일한 ID가 있다면 매력포인트도 같은지 확인하고 같다면 rq.historyback
- -[x] 호감목록에 등록하는 ID와 동일한 ID가 있고 매력포인트가 다르다면 매력포인트만 수정
  


---

### 1주차 미션 요약

<br>

### **[접근 방법]**

<br>

#### 호감목록 사람의 수가 11이상인지 확인
>호감을 표시할수록 from_insta_member_id 칼럼에 똑같은 아이디가 하나씩 추가되기 때문에 
현재로그인한 유저의 아이디와 똑같은 아이디의 갯수 = 유저가 호감을 표시한 아이디의 갯수

> 레포지터리에 fromInstaMemberId의 갯수를 세는 메서드를 만든다.
`long countByFromInstaMemberId(Long fromInstaMemberId);`

> 현재로그인한 유저의 Id를 받아와서 그 Id에 해당하는 fromInstaMemberId의 갯수를 세고
그 갯수가 11개 이상일 때 오류메세지를 반환한다.
```java
 Long fromInstaMemberId = member.getInstaMember().getId();

        if (likeablePersonRepository.countByFromInstaMemberId(fromInstaMemberId) >= 10){
        return RsData.of("F-3", "호감상대는 최대 10명까지만 등록 할 수 있습니다.");
        }
```

#### 호감목록에 동일한 ID가 있는지 확인하고 있다면 등록 제한

> to_insta_member_username이 같은지 확인해야한다.

>username을 찾는 findBy메소드를 리포지터리에 작성
`Optional<LikeablePerson> findByToInstaMemberUsername(String username);`

>existingLikeablePerson은 이미 존재하는 Person을 뜻한다.
따라서 findBy로 username을 찾았을때 existingLikeablePerson이 존재하면(==중복된 아이디가 리포지터리에 존재) 오류메시지를 반환한다.

```java
Optional<LikeablePerson> existingLikeablePerson = likeablePersonRepository.findByToInstaMemberUsername(username);
if (existingLikeablePerson.isPresent()) {
return RsData.of("F-4", "이미 등록된 ID입니다.");
}
 ```

#### 호감목록에 등록하는 ID와 호감사유가 동일한 ID가 있다면 등록 제한

>레포지터리에 username뿐만아니라 attractiveTypeCode까지 찾는 findBy메소드로 수정한다.
`Optional<LikeablePerson> findByToInstaMemberUsernameAndAttractiveTypeCode(String username, int attractiveTypeCode);`

>username과 attractiveTypeCode까지 동시에 같은 객체가 있다면 등록제한

```java
Optional<LikeablePerson> existingLikeablePerson = likeablePersonRepository.findByToInstaMemberUsernameAndAttractiveTypeCode(username, attractiveTypeCode);
if (existingLikeablePerson.isPresent()) {
            return RsData.of("F-4", "이미 등록된 ID입니다.");
        }
```

#### ID가 같지만 호감사유가 다를때는 수정할수 있도록

> 수정할 수 있도록 LikeablePerson에 `@Setter` 추가

>레포지터리에 `Optional<LikeablePerson> findByToInstaMemberUsername(String username);`
다시 username으로 수정한다.
왜냐하면 attractiveTypeCode는 달라도 되고 같아도 되기때문에 attractiveTypeCode까지 같을 필요가없다고 생각

>attractiveTypeCode는 같거나 다를수 있기때문에 Optional 메소드 filter를 이용하여 attractiveTypeCode를 구분한다.

>username과 attractiveTypeCode가 같은 existingLikeablePersonAndType객체 생성
```java
Optional<LikeablePerson> existingLikeablePersonAndType = likeablePersonRepository
                .findByToInstaMemberUsername(username)
                .filter(p -> p.getAttractiveTypeCode() == attractiveTypeCode);
```
> username은 같지만 attractiveTypeCode가 다른 existingLikeablePerson객체 생성
```java
Optional<LikeablePerson> existingLikeablePerson = likeablePersonRepository
                .findByToInstaMemberUsername(username)
                .filter(p -> p.getAttractiveTypeCode() != attractiveTypeCode);
```
> 이제 두개의 객체를 이용하여 둘다 같을땐 오류메세지 반환, 호감사유가 다를땐 수정작업을 한다.
> 오류메세지 반환
```java
if (existingLikeablePersonAndType.isPresent()) {
            return RsData.of("F-4", "이미 등록된 ID입니다.");
        }
```
> 수정 작업
```java
if (existingLikeablePerson.isPresent()) {
            existingLikeablePerson.get().setAttractiveTypeCode(attractiveTypeCode);
            likeablePersonRepository.save(existingLikeablePerson.get());
            return RsData.of("S-2", "인스타 유저(%s)의 매력포인트가 수정되었습니다.".formatted(username));
        }
```



<br>

<br>


### **[특이사항]**

filter를 사용하지 않고 attractiveTypeCode의 변화 유무에 대해서 어떻게 구현할 수 있을지
해결하지 못했다.


### **[Refactoring]**








 
# 4Week_Shinheesu.md

## Title: [4Week] 신희수

### 미션 요구사항 분석 & 체크리스트

* **미션요구사항 분석**
  </br>
  4주차 필수 미션 요구 기능
  </br>
  1 . 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용 </br>
  2 . 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현 </br>
  </br>
  호감 리스트에서 성별 종류를 선택할 수 있는데, 
"전체"를 선택하면 남녀 구분 없이 모든 호감 표시자가 나오고, 
"남성"를 선택하면 호감 표시자의 성별이 남자인 사람들만 나오며, 
"여성"를 선택하면 호감 표시자의 성별이 여자인 사람들만 나오도록 구현한다.
  </br>
  </br>

* **체크리스트**
  </br>
- -[x] "남성"을 선택했을때 성별이 남성인 사람들만 나타나도록 구현
- -[x] "여성"을 선택했을때 성별이 여성인 사람들만 나타나도록 구현
- -[x] 네이버클라우드플랫폼을 이용한 배포 작업



---

### 4주차 미션 요약

<br>

### **[접근 방법]**

<br>

#### 파라미터 구현

```java
@RequestParam(value = "gender", required = false) String gender
```
>toList페이지에서 성별을 선택했을때 의 URL이 https://localhost/usr/likeablePerson/toList?gender=M&attractiveTypeCode=&sortCode=1&continue 이기 때문에
gender 파라미터를 받아들인다. 맨처음 toList URL로 접속했을땐 파라미터 값이 없으므로 필수로 입력받아야 할 파라미터가 아니라서 required=false로 설정한다.

<br>

#### entity 수정

```java
public String getGenderDisplayName() {
        return switch (gender) {
            case "W" -> "여성";
            case "M" -> "남성";
            default -> "전체";
        };
    }
```
>먼저 toList.html을 확인했을때 `genderDisplayName=${likeablePerson.fromInstaMember.genderDisplayName}|`
의 타임리프 형식을 사용하고 있다. likeablePerson의 fromInstaMember는 Instmember의 entity이다.
따라서 Instamember entity에서 genderDisplayName을 확실히 구분할 수 있도록 수정한다.

<br>

#### 성별이 "남성" 인 유저목록 구현


```java
if (gender != null) {

    if (gender.equals("M")) {
        likeablePeople = likeablePeople.stream()
        .filter(likeablePerson -> likeablePerson.getFromInstaMember().getGenderDisplayName().equals("남성"))
        .collect(Collectors.toList()); 
    }
```

```java
public InstaMember getFromInstaMember() {
        return fromInstaMember;
    }
```

> 일단 `if (gender != null)`을 넣어줌으로써 gender가 null일때 발생하는 오류를 막을 수 있다.
그리고 GenderDisplayName을 사용하기 위해서 엔티티에 fromInstaMember를 리턴하는 getFromInstaMember()메서드를 만들었다.

> 스트림을 이용하여 gender가 M일때 case "M" -> "남성" 이므로 genderDisplay가 "남성"인 사람들만 리스트에 출력할 수 있도록 하였다.


<br>

#### 성별이 "여성" 인 유저목록 구현

```java
else if (gender.equals("W")) {
        likeablePeople = likeablePeople.stream()
        .filter(likeablePerson -> likeablePerson.getFromInstaMember().getGenderDisplayName().equals("여성"))
        .collect(Collectors.toList());
    }
```

> "남성" 인 유저목록과 똑같이 적용하면 된다.

 


<br>

<br>


### **[특이사항]**



### **[Refactoring]**

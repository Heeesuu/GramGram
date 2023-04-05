# 1Week_Shinheesu.md

## Title: [1Week] 신희수

### 미션 요구사항 분석 & 체크리스트

* **미션요구사항 분석**
</br>
1주차 필수 미션에서 요구하는 핵심 기능은 호감목록 페이지에서 특정 항목을 삭제하고, 삭제처리를 하기전에 해당 항목에 대한 소유권이
본인에게 있는지 확인을 해야한다.
그리고 삭제 후에는 rq.redirectWithMsg 함수를 
사용하여 알림 메시지와 함께 호감목록 페이지로 돌아와야한다.
</br>
</br>

* **체크리스트**
</br>
-[x] 삭제 버튼을 클릭하였을 때, 해당 항목의 Id와 로그인한 사용자의 Id를 서버에 전송
-[x] 서버에서 전달받은 Id가 같은지 소유권을 확인
-[x] 소유권이 같다면 해당 항목 삭제
-[x] 삭제 후 rq.redirectWithMsg 함수를 이용하여 호감목록 페이지로 리디렉션
-[x] 삭제되었습니다라는 메시지와 해당 항목이 화면에서도 삭제 


---

### 1주차 미션 요약

<br>

### **[접근 방법]**

<br>

* **findByID()** <br>
`likeablePersonRepository` 에서 Id를 기준으로 데이터를 받아와 삭제기능을 구현해야한다고 생각을 하였습니다.
따라서 **findById()** 메서드를 구현하여 특정 Id에 해당하는 호감상대를 찾아서
`Optional` 형태로 반환하였습니다.

<br>

* **LikeablePersonService**
<br>
받은 Id를 통해 `LikeablePersonService` 에 **delete()** 메서드를 구현하였습니다.
먼저 `likeablePersonRepository`에서 데이터를 받아
`FromInstaMember`(호감을 표시한 사람)의 속성과 `member` 객체의 `instaMember` 속성이 다를 때 오류메세지를 반환합니다.
소유권이 확인되었다면 Id를 통해서 받았던 항목을 `likeablePersonRepository` 에서 삭제하고 `RsData`객체를 통해 삭제 성공 메세지를 반환합니다.
 
<br>

* **LikeablePersonController**
<br>
삭제버튼을 눌렀을 때의 `"/delete/{id}"`URL을 매핑합니다. URL에 있는 `id` 값은 **findById()** 메서드를 이용하여 `LikeablePerson` 객체를 조회합니다.
그리고 서비스에서 구현했던 **delete()** 메서드를 이용하여 해당 id의 객체를 삭제합니다.
동시에 소유권을 확인하기 위해 현재 로그인한 유저를 나타내는 **rq.getMember()** 를 전달하였습니다.
삭제를 성공하면 삭제결과를 담고있는 `RsData` 객체가 반환되어지고 **redirectWithMsg()** 메서드로 인해 list페이지로 리다이렉트합니다.
만약 삭제를 실패하면 **historyBack()** 메서드가 실행되어 이전 페이지로 되돌아가도록 하였습니다.

<br>

### **[특이사항]**

호감 목록에서 좋아하는 사람의 ID 순서대로 1, 2, 3 ... 을 구현하고싶어서 likeable_person.id를 표시하려고 했지만
6번 id가 삭제되어도 7번 id가 6번으로 올라오는것이 아닌 7번 그대로 남아있어서 id를 이용하여 구현 할 수 없었습니다.

인스타 아이디에 한글이 작성되었을때 에러페이지로 이동하는데, q.redirectWithMsg 함수를 이용하여 기존 페이지로 리디렉션을 
구현하려고했으나 하지 못했습니다.

**[Refactoring]**

    - UI 디자인 개선 작업


 
# 3Week_Shinheesu.md

## Title: [3Week] 신희수

### 미션 요구사항 분석 & 체크리스트

* **미션요구사항 분석**
  </br> 
호감표시, 호감사유변경 후에 개별 호감표시건에 대해 쿨타임 시간 동안은 호감취소와
호감사유 변경을 할 수 없도록 작업한다. (버튼 비활성화, url접속 제한)
  </br>
네이버 클라우드 플랫폼을 이용하여 배포한다 (도메인 없이, IP로 접속할 수 있도록)
  </br>

* **체크리스트**
  </br>
- [x]  쿨타임 시간에 맞게 남은 시간이 나타날 수 있도록 한다.
- [x]  쿨타임이 지나지 않으면 수정, 삭제 버튼 비활성화
- [x]  쿨타임이 지나지 않으면 URL을 통한 수정페이지 접속 제한
- [x]  네이버 클라우드를 이용하여 배포


---

### 3주차 미션 요약

<br>

### **[접근 방법]**
<br>

#### 쿨타임 시간에 맞게 남은시간 나타내기
```java
public String getModifyUnlockDateRemainStrHuman() {
        Duration duration = Duration.between(LocalDateTime.now(), AppConfig.genLikeablePersonModifyUnlockDate());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + "시간 " + minutes + "분";
    }
```


>"호감사유변경과 호감삭제는 2시간 16분 후에 가능합니다." 의 형식으로 2시간 16분이 하드코딩
되어있었기 때문에 남은 쿨타임에 맞춰 시간과 분을 나타내었다.


#### 쿨타임이 지나지 않으면 수정, 삭제 버튼 비활성화

```java
<a th:href="@{|modify/${likeablePerson.id}|}" class="btn btn-sm btn-outline"
        th:classappend="${!likeablePerson.modifyUnlocked ? 'btn-disabled' : ''}">
<i class="fa-solid fa-pen-to-square"></i>
        &nbsp;
호감사유 변경
</a>
```

>`th:classappend="${!likeablePerson.modifyUnlocked ? 'btn-disabled' : ''`를 이용하여
(likeablePerson.modifyUnlocked = 수정이 가능) 앞에 !의 부정형이 있으므로 수정이 가능하지 않은 상태일때, 버튼을 비활성화 시킨다.


#### 쿨타임이 지나지 않으면 URL을 통한 수정페이지 접속 제한

```java
public RsData canModifyUnlocked(Member member, LikeablePerson likeablePerson) {
        if (!likeablePerson.isModifyUnlocked()) {
            return RsData.of("F-10", "3시간 동안은 호감취소와 호감사유변경을 할 수 없습니다.");
		}
        return RsData.of("S-3","호감취소와 호감사유변경이 가능합니다.");
    }
}
```

```java
RsData canIsModifyUnlockedRsData = likeablePersonService.canModifyUnlocked(rq.getMember(), likeablePerson);
        if (canIsModifyUnlockedRsData.isFail()) return "error/404";
```


>삭제는 POST방식을 통해 접속하기 때문에 URL을 제한할 필요가 없지만 GET 방식으로도 접속 가능한 수정은
쿨타임이 지나지 않았더라도 usr/likeablePerson/modify/3의 주소로 들어갔을때 id = 3의 회원의 수정 페이지로 접속된다.

>따라서 쿨타임이 지났을땐 접속 가능하지만, 지나지 않고 URL접속을 했을땐 404페이지로 리디렉트 할수 있도록 하였다.


### **[특이사항]**

list페이지를 새로고침할때마다 쿨타임의 남은 시간이 업데이트 되도록 구현 하고싶었는데, 검색해보니
자바스크립트를 이용하여 구현해야하는것 같아 적용을 못함

### **[Refactoring]**





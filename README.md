# 1. 주요 구현

## (1) 백엔드

### 1) Docker: 컨테이너 기반 환경 구성

본 프로젝트에서는 Docker와 Docker-compose를 활용하여 다수의 컨테이너를 효율적으로 실행하고 관리하였습니다. 특히, 메시지 기능(분실물 게시물의 메시지 버튼을 눌렀을 때 통신하는 기능) 구현에 있어 핵심적인 역할을 수행하였습니다.

### 주요 활용 사례:

- **환경 독립성 보장**:
Docker를 통해 모든 서비스가 동일한 환경에서 실행되도록 보장함으로써 개발 환경과 운영 환경 간의 차이를 최소화했습니다. 이를 통해 디버깅 및 배포 작업의 일관성이 유지되었습니다.
- **서비스 간 통신 구현**:
Docker-compose를 사용하여 Kafka, Zookeeper, 애플리케이션 컨테이너 등 세 개의 컨테이너를 하나의 네트워크로 연결하였습니다. 이를 통해 서비스 간의 데이터 교환이 원활히 이루어졌으며, 특히 메시지 기능과 같은 유저 간 상호작용 지원에 기여하였습니다.
- **배포 간소화**:
Docker-compose를 활용하여 종속성과 설정 작업을 단순화하여 애플리케이션을 손쉽게 배포하였습니다. 이는 운영 환경에서의 설치 및 실행 속도를 크게 개선시켰습니다.

---

### 2) Kafka: 메시지 이벤트 처리

Apache Kafka는 대규모 메시징 처리를 지원하는 분산형 스트리밍 플랫폼으로, 본 프로젝트에서는 메시지 이벤트의 안정적인 처리와 성능 향상을 위해 사용되었습니다. Kafka를 선택한 이유와 사용 방식은 다음과 같습니다.

### Kafka의 주요 장점:

- **대량의 메시지 처리 성능**:
Kafka는 높은 처리량과 낮은 대기 시간을 제공하여 RabbitMQ와 같은 기존 메시징 시스템의 단점을 극복하였습니다.
- **내결함성**:
Kafka의 복제(replicas)와 로그(log-based) 방식은 메시지 유실 방지와 시스템 안정성 향상에 기여했습니다.
- **확장성**:
Kafka의 분산 아키텍처는 시스템이 증가하는 메시지 양을 유연하게 처리할 수 있도록 지원합니다.

### 구현 세부 사항:

- 메시지 이벤트 처리를 위해 `@KafkaListener` 어노테이션을 활용하였습니다.
- 예를 들어, 다음 코드를 통해 메시지 토픽(`chat-messages`)과 그룹(`chat-group`)을 모니터링하며 사용자 간 메시지 상호작용을 실시간으로 처리하였습니다.

```java
@KafkaListener(topics = "chat-messages", groupId = "chat-group")
public void processMessage(String message) {
    // 메시지 처리 로직
    System.out.println("Received: " + message);
}
```

- 메시지 토픽과 데이터는 모두 Kafka 브로커에 저장되며, 필요에 따라 소비자(consumers)에게 안전하게 배포됩니다.

---

### 3) 검색 기능(Search)

본 프로젝트에서는 AI를 활용하여 정제된 데이터를 서버 측에서 별도의 검색 요청으로 처리하였습니다. 사용자의 입력 데이터와 AI가 정제한 데이터를 함께 활용하여 검색 정확도를 향상시키는 방식을 채택하였습니다.

### 주요 구현 사항:

- **데이터 정제 및 통합**:
AI가 제공하는 정제된 데이터(dto)를 서버로 전송하고, 사용자 입력 데이터를 병합하여 검색 요청을 수행하였습니다.
- **정확도 향상**:
특히, 사용자가 세 개 이상의 검색 키워드(예: 이름, 브랜드, 시간대, 색상 등)를 입력할 경우 이를 조합하여 검색의 정확도를 높였습니다.
- **검색 프로세스 개선**:
데이터 정제 후 추가적인 필터링을 통해 결과가 보다 직관적이고 신뢰성 있게 제공되도록 설계하였습니다.

---

### **4) ItemService 주요 기능**

### **i) 분실물 등록 (`registerLostItem`)**

- 사용자가 분실물을 등록할 때 보상 포인트와 이미지를 처리하고 데이터를 데이터베이스에 저장합니다.
- **보상 처리**:
    - 사용자가 설정한 포인트가 부족하면 `INSUFFICIENT_POINTS` 예외가 발생합니다.
    - 사용자가 입력한 보상 포인트를 차감하고 보상 객체를 생성합니다.
- **이미지 처리**:
    - 이미지 파일을 AWS S3 버킷에 업로드하고 URL을 반환합니다.
- **결과**:
    - 데이터베이스에 보상 및 이미지 URL 정보를 포함한 분실물 정보를 저장합니다.

---

### **ii) 습득물 등록 (`reportFoundItem`)**

- 사용자가 습득물을 등록할 때 이미지를 S3 버킷에 업로드하고 데이터를 데이터베이스에 저장합니다.
- **데이터 처리**:
    - 습득물의 이름, 카테고리, 주소 등 주요 정보를 저장합니다.
    - 이미지를 업로드하고 URL을 습득물 객체에 추가합니다.
- **결과**:
    - 데이터베이스에 습득물 정보를 저장합니다.

---

### **iii) 댓글 관리**

- **분실물 댓글**:
    - 댓글 생성: `registerLostItemComment`
    - 댓글 수정: `updateLostItemComment`
    - 댓글 삭제: `deleteLostItemComment`
- **습득물 댓글**:
    - 댓글 생성: `registerFoundItemComment`
    - 댓글 수정: `updateFoundItemComment`
    - 댓글 삭제: `deleteFoundItemComment`
- **기능**:
    - 각 댓글은 부모-자식 관계를 지원하며 계층 구조로 반환됩니다.
    - 댓글 작성자와 로그인한 사용자가 일치하는지 확인하여 권한을 검증합니다.

---

### **iv) 상태 변경**

- 분실물 및 습득물 상태를 변경합니다 (`REGISTERED` → `RETURNED`).
    - 사용자가 자신의 항목만 상태를 변경할 수 있도록 권한을 확인합니다.
    - 변경된 상태를 데이터베이스에 저장합니다.

---

### **v) 고급 검색**

- `advancedSearchLostItems`와 `advancedSearchFoundItems`를 통해 이름, 브랜드, 색상 등의 여러 조건으로 검색합니다.
- 검색 결과에 포함되기 위해 최소 3개의 조건이 일치해야 합니다.

---

### **vi) 메시지 전송**

- 분실물 및 습득물 관련 메시지를 전송합니다.
    - 요청 데이터를 바탕으로 Kafka를 통해 메시지를 전송하며, 메시지 내용은 데이터베이스에 저장됩니다.
    - 메시지 전송 실패 시 예외를 처리하여 안정적인 메시징을 지원합니다.

---

### **5) ItemController 주요 엔드포인트**

### **i) 등록**

- **분실물 등록**: `/api/items/lost/register`
- **습득물 등록**: `/api/items/found/report`
- JSON 요청 데이터를 받아 서비스 계층에서 처리합니다.

---

### **ii) 검색**

- **간단 검색**:
    - 분실물: `/api/items/lost/search`
    - 습득물: `/api/items/found/search`
- **고급 검색**:
    - 분실물: `/api/items/lost/advanced-search`
    - 습득물: `/api/items/found/advanced-search`
- 각 엔드포인트에서 전달된 검색 조건을 바탕으로 데이터를 반환합니다.

---

### **iii) 상태 변경**

- **분실물 상태 업데이트**: `/api/items/lost/{lostItemId}/status`
- **습득물 상태 업데이트**: `/api/items/found/{foundItemId}/status`
- 사용자의 권한을 확인한 후 상태를 변경합니다.

---

### **iv) 댓글 관리**

- **댓글 작성**:
    - 분실물: `/api/items/lost/comment`
    - 습득물: `/api/items/found/comment`
- **댓글 수정**:
    - 분실물: `/api/items/lost/comment/{commentId}`
    - 습득물: `/api/items/found/comment/{commentId}`
- **댓글 삭제**:
    - 분실물: `/api/items/lost/comment/{commentId}`
    - 습득물: `/api/items/found/comment/{commentId}`

---

### **v) 메시지 전송**

- **분실물 메시지**: `/api/items/{lostItemId}/message/send`
- **습득물 메시지**: `/api/items/{foundItemId}/message/send`
- 요청 데이터를 바탕으로 Kafka를 이용해 메시지를 전송하며, 안정적인 전송을 위해 데이터베이스에 메시지 내용을 기록합니다.

---

### **6) MemberService 주요 기능**

### **i) 카카오 로그인 (`kakaoLogin`)**

- 사용자가 카카오 로그인을 통해 서비스에 인증 및 회원가입을 진행합니다.
- **프로세스**:
    1. **카카오 AccessToken 획득**:
        - 사용자가 전달한 인증 코드를 통해 AccessToken을 가져옵니다.
        - 실패 시 `KAKAO_FETCH_ACCESS_TOKEN_FAIL` 예외가 발생합니다.
    2. **카카오 사용자 정보 가져오기**:
        - AccessToken을 이용하여 사용자 프로필 정보를 조회합니다.
        - 실패 시 `KAKAO_FETCH_USER_DATA_FAIL` 예외가 발생합니다.
    3. **회원 데이터 처리**:
        - 이미 존재하는 `kakaoId`를 가진 회원이 있으면 해당 회원 정보를 반환합니다.
        - 없는 경우, 카카오에서 가져온 정보를 바탕으로 새 회원을 생성하고 데이터베이스에 저장합니다.
- **결과**:
    - 회원 데이터가 반환됩니다.

---

### **ii) 토큰 생성 (`generateToken`)**

- 회원 인증 후 JWT 토큰을 발급합니다.
- **프로세스**:
    - 회원의 `kakaoId`를 통해 `UserDetails`를 조회하고 인증 객체를 생성합니다.
    - `JwtProviderUtil`을 사용해 JWT 토큰을 발급합니다.
- **결과**:
    - JWT 토큰(`Bearer` 타입)을 반환합니다.

---

### **iii) 회원 정보 업데이트 (`updateMyMember`)**

- 사용자가 자신의 닉네임과 같은 정보를 수정할 수 있습니다.
- **프로세스**:
    - 전달받은 새로운 닉네임으로 회원 객체의 닉네임을 업데이트합니다.
    - 변경된 정보를 데이터베이스에 저장합니다.
- **결과**:
    - 업데이트된 회원 정보가 반환됩니다.

---

### **iv) 포인트 충전 (`addPoints`)**

- 사용자가 자신의 포인트를 충전할 수 있습니다.
- **프로세스**:
    1. 충전 요청 포인트가 유효한지 확인합니다(0 이하일 경우 `INVALID_POINT_AMOUNT` 예외 발생).
    2. 로그인된 사용자의 포인트를 요청된 값만큼 증가시킵니다.
    3. 변경된 회원 정보를 데이터베이스에 저장합니다.
- **결과**:
    - 포인트가 증가된 회원 정보가 반환됩니다.

---

### **v) 회원 정보 조회 (`getMemberByPrincipal` 및 `getMemberById`)**

- **`getMemberByPrincipal`**:
    - 인증 객체를 바탕으로 현재 로그인된 사용자 정보를 확인합니다.
    - 사용자를 찾을 수 없는 경우 `USER_NOT_FOUND` 예외가 발생합니다.
- **`getMemberById`**:
    - 사용자 ID를 기반으로 회원 정보를 조회합니다.
    - 사용자를 찾을 수 없는 경우 `USER_NOT_FOUND` 예외가 발생합니다.

---

### **7) UserController 주요 엔드포인트**

### **i) 회원 정보 관리**

- **사용자 정보 조회**:
    - **엔드포인트**: `/api/users/me`
    - 로그인된 사용자의 정보를 조회하여 반환합니다.
    - **서비스 호출**: `getMemberByPrincipal`.
- **사용자 정보 수정**:
    - **엔드포인트**: `/api/users/me`
    - 닉네임과 같은 사용자 정보를 수정합니다.
    - **서비스 호출**: `updateMyMember`.

---

### **ii) 카카오 로그인**

- **엔드포인트**: `/api/users/login`
- 카카오 인증 코드를 받아 사용자를 로그인 처리한 뒤 JWT 토큰을 반환합니다.
- **서비스 호출**: `kakaoLogin` → `generateToken`.

---

### **iii) 포인트 충전**

- **엔드포인트**: `/api/users/points`
- 사용자의 포인트를 충전합니다.
- **서비스 호출**: `addPoints`.

---

### **iv) 기술 스택**

- **Spring Boot**: RESTful API 구현 및 사용자 인증 관리.
- **JWT**: 사용자 인증 및 권한 검증.
- **Kakao API**: 카카오 인증 및 사용자 데이터 연동.
- **Lombok**: 코드 간소화(`@RequiredArgsConstructor` 등을 사용하여 의존성 주입 처리).



# 5. 기술적 어려움

## (1) 백엔드

### **1) AWS 서버의 제한사항**

- **문제:**
    
    EC2 Free Tier를 사용하면서 RAM 사용량에 민감해졌습니다. 특히 Docker-compose로 Kafka, Zookeeper, Application 컨테이너 3개를 실행했을 때 과부하로 인해 서버가 자주 다운되는 문제가 발생했습니다.
    
- **해결 방법:**
    1. Docker-compose에서 각 컨테이너의 최대 메모리 용량과 CPU 사용량을 조정하여 자원 관리를 최적화했습니다.
    2. Kafka 설정에서 메시지를 하나의 토픽으로 분류하고, 세부 그룹으로 나누어 효율적으로 처리했습니다.
    3. 이를 통해 부하 테스트(초당 20개의 요청)에서도 서버가 안정적으로 운영될 수 있었습니다.

---

### **2) Docker와 Kafka 도입 초기의 어려움**

- **문제:**
    
    Docker와 Kafka를 처음 사용하며 분산 메시지 처리와 Docker-compose를 기반으로 한 컨테이너 관리에 어려움을 겪었습니다.
    
    - 로컬 환경과 배포 환경의 차이로 인해 초기에는 배포 안정성을 확보하기 쉽지 않았습니다.
    - Kafka의 설정과 메시지 분산 구조를 이해하고 활용하는 데 시간과 학습이 필요했습니다.
- **해결 방법:**
    1. Kafka를 분산 처리와 서비스 확장을 위해 선택하였으며, 초기에는 설정과 사용법을 학습하는 데 집중했습니다.
    2. Docker-compose를 활용하여 Kafka와 Zookeeper를 컨테이너 네트워크로 연결하고, 환경 간 설정 차이를 최소화하여 배포 안정성을 확보했습니다.
    - 결과적으로 Docker와 Kafka를 활용해 배포와 메시징 안정성을 확보했으며, 최종적으로 만족스러운 성과를 얻을 수 있었습니다.

---

### **3) API 명세 작성 및 Swagger 활용의 어려움**

- **문제:**
    
    초기에는 Swagger를 활용하여 API 명세를 작성하는 데 익숙하지 않아 불완전한 명세를 작성하게 되었습니다.
    
    API 명세가 불완전한 상태에서는 프론트엔드와 백엔드 간의 통합이 어려웠고, 팀 간 의사소통에도 비효율이 발생했습니다.
    
- **해결 방법:**
    1. Swagger 사용법과 명세 작성 방법을 학습한 후, 작성한 API 명세를 전면 재정비했습니다.
    2. 이를 통해 프론트엔드와 백엔드의 통합을 명확히 하고, 팀 간 커뮤니케이션 효율성을 크게 향상시켰습니다.
    3. 최종적으로는 명확하고 정리된 API 명세를 기반으로 서비스 개발을 진행할 수 있었습니다.

---

### **4) 검색 기능 설계와 데이터 정제**

- **문제:**
    
    사용자 입력 데이터와 AI 정제 데이터를 어떻게 활용하고, 이를 사용자 경험에 맞게 설계할지에 대한 고민이 있었습니다.
    
    - 모든 데이터를 AI 정제에 의존하기에는 GPT AI에게 과도한 작업을 맡기기 어려운 상황이었습니다.
    - 또한 사용자가 입력한 데이터와 AI 정제 데이터 간의 매칭 과정을 효율적으로 설계하는 데 어려움이 있었습니다.
- **해결 방법:**
    1. 사용자 입력 데이터와 AI 정제 데이터를 비교 분석하여 매칭하는 방식을 채택했습니다.
    2. 사용자 경험을 고려하여, 정제된 데이터를 직접 노출하기보다는 사용자가 입력한 데이터를 기반으로 검색 결과를 반환하도록 설계했습니다.
    3. AI 정제 데이터와 사용자 입력 데이터를 모두 저장하여 향후 비교 및 확장 가능성을 열어두었습니다.
    4. 중앙대학교 캠퍼스를 주 타겟으로 설정했지만, 향후 확장 가능성을 고려하여 빌딩별로 데이터를 구분하지 않고 데이터를 정제했습니다.

---

### **5) 결론**

이 프로젝트는 기술적 한계를 극복하고 Docker, Kafka, Swagger 등 다양한 기술을 활용하여 안정성과 효율성을 확보한 경험이었습니다. 특히, 제한된 환경에서 최적의 성능을 달성하기 위해 컨테이너 자원 관리, 분산 메시지 처리, 검색 데이터 정제 등의 문제를 해결하며 기술적 성장과 더불어 사용자 경험 중심의 서비스를 완성할 수 있었습니다.

이를 통해 단순히 기술을 활용하는 것을 넘어, 실제 사용자와 환경의 요구에 맞춘 최적화된 솔루션을 설계하는 데 큰 성과를 얻었습니다.

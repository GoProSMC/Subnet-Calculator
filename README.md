# 서브넷 계산기 (Subnet Calculator)

서브넷 마스크 및 IP 대역 계산기 안드로이드 앱입니다.

## 주요 기능

- **IP 주소 입력**: 4개의 옥텟으로 IP 주소 입력
- **CIDR 표기법**: /0 ~ /32 선택 가능
- **자동 계산**: 입력 시 실시간 계산
- **상세 정보 제공**:
  - 서브넷 마스크
  - 네트워크 주소
  - 브로드캐스트 주소
  - 첫 번째/마지막 호스트
  - 총 호스트 수 및 사용 가능 호스트 수
  - 와일드카드 마스크
  - 이진수 표현
  - IP 클래스 (A, B, C, D, E)
  - IP 타입 (공인/사설/루프백 등)

## 스크린샷

<img width="399" height="731" alt="image" src="https://github.com/user-attachments/assets/58895a6b-8462-4895-9e23-64b493196044" />
<img width="397" height="282" alt="image" src="https://github.com/user-attachments/assets/1112511e-6be7-414e-b483-0d289a77d4cf" />

## 기술 스택

- Kotlin
- ViewBinding
- Material Design 3
- 비트 연산을 활용한 서브넷 계산

## 요구사항

- Android 7.0 (API 24) 이상
- Android Studio

## 설치 방법

1. 이 저장소를 클론합니다
```bash
git clone https://github.com/GoProSMC/Subnet-Calculator.git
```

2. Android Studio에서 프로젝트를 엽니다

3. Gradle 동기화를 기다립니다

4. 실제 기기 또는 에뮬레이터에서 실행합니다

## 사용 방법

1. IP 주소의 각 옥텟을 입력 (예: 192.168.1.0)
2. CIDR 값 선택 (예: /24)
3. 자동으로 계산 결과 표시
4. "복사" 버튼으로 결과를 클립보드에 복사

## 예제

**입력**: 192.168.1.0 /24
- 서브넷 마스크: 255.255.255.0
- 네트워크 주소: 192.168.1.0
- 브로드캐스트: 192.168.1.255
- 사용 가능 호스트: 254개

**입력**: 10.0.0.0 /8
- 서브넷 마스크: 255.0.0.0
- 네트워크 주소: 10.0.0.0
- 브로드캐스트: 10.255.255.255
- 사용 가능 호스트: 16,777,214개

## 학습 포인트

- 비트 연산 (AND, OR, NOT, Shift)
- IP 주소 체계
- 서브넷팅 개념
- CIDR 표기법
- 네트워크 클래스
  

## 라이선스

MIT License

## 기여

이슈와 풀 리퀘스트는 언제나 환영합니다!
---
Made by [spear34000](https://github.com/spear34000)

# 체크메이트

![image](https://user-images.githubusercontent.com/62784314/197489555-3a0b20a6-feba-410d-a05d-02a0f92db421.png)

코로나19 이후 많은 기업이 재택근무제를 도입하면서 화상회의 빈도가 높아졌습니다. 그러나 줌 피로라는 신조어가 등장할 만큼 상대방을 가까이 마주하며 회의를 진행하는 것이 사람들의 피로도를 상승시킵니다. 본 프로젝트는
얼굴 합성과 이미지 애니메이션을 이용한 아바타를 통해 사용자가 화상회의에 참가할 수 있는 시스템입니다. 사용자와 닮은 개성 있는 캐릭터는 실시간으로 사용자의 표정 및 움직임을 반영하여 화상회의에 적용될 수 있고
채팅과 커뮤니티에서 캐릭터의 이모티콘으로 감정 표현을 할 수 있습니다.

## 기능

1. 나만의 아바타 생성

- Gan 알고리즘을 통해 사용자의 얼굴을 아바타로 자동 생성한다. 사용자는 등록된 아바타를 통해 화상회의 참여가 가능하다.

1. 실시간으로 아바타에 사용자의 표정 반영

- 화상회의 안에서 사용자의 움직임에 따라 캐릭터 사진이 실시간으로 움직인다.

1. 아바타의 표정에 따른 이모티콘 생성

- 생성된 캐릭터 이미지를 Talking head anime model을 통해 슬픔, 기쁨, 윙크, 화남 등 감정 표정을 생성하여 이모티콘으로 제공한다.

1. 일정 관리 비서 AI 챗봇

- 챗봇을 통해 일정을 관리할 수 있다.(일정 조회, 일정 예약, 일정 알림)
-

## **🛠️ 기술스택 🛠️**

- Flask
- AI : pytorch, Google Colab, anaconda, Google DialogFlow
- Spring boot
- Build tool : gradle
- CI/CD : Jenkins, Docker
- Test : Junit
- API 문서 : Swagger UI
- etc : Lombok, Spring Data JPA
- cloud server : AWS
- DB : mariaDB, Redis
- React
- 화상회의 : Openvidu
- Android Studio
- 알림, 채팅 : Firebase 클라우드 메시징(FCM)
- 형상 관리 : github
- 의사소통 : Notion, Slack, Google Drive, gather town

## 👇프로젝트의 자세한 설계과정을 보고싶다면 wiki 👇

<a href="https://github.com/checkmate2022/Backend/wiki"><img width="240" alt="image" src="https://user-images.githubusercontent.com/62784314/197489630-3230add5-241b-4fa6-9282-ecd4811c1420.png"></a>

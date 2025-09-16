# 🎬 MovieLog

🎬 **MovieLog**는 TMDB API와 Firebase Firestore를 활용하여 영화를 검색하고, ⭐별점과 📝후기를 기록하며  
자신만의 영화 기록을 남길 수 있는 안드로이드 애플리케이션입니다.  

사용자가 **개인적인 감상과 평가를 저장**하여 자신만의 영화 일지를 만들어갈 수 있도록 설계하였습니다.

--- 
## 기능 설명

### 1. MainActivity

- 앱 실행 시 가장 먼저 실행되는 메인 액티비티

- `MovieListFragment`를 메인 UI로 로드하여 영화 목록 관리를 중심으로 화면 구성

- 📋 `MovieListFragment`

    - Firestore에 저장된 영화 목록을 불러와 RecyclerView로 표시

    - `MovieAdapter`를 통해 영화 카드(포스터, 제목, 연도, 리뷰, 평점)를 렌더링
    
    - 정렬 기능 제공

        - 평점순 (기본)

        - 최신순 (연도 기준)

    - 상단의 ➕ 버튼을 통해 `AddMovieActivity`로 이동하여 새 영화 추가

    - 영화 아이템 클릭 시 `DetailActivity`로 이동

### 2. AddMovieActivity

- 새로운 영화 추가 화면

- 기능 요약

    - 제목/연도를 입력해 TMDB API로 영화 검색

    - 포스터, 제목, 개봉연도, 줄거리 자동 불러오기

    - 사용자가 직접 별점과 리뷰 작성 가능

    - 입력된 데이터는 Firebase Firestore에 저장

    - TMDB에서 예고편 키(trailer key) 가져옴

### 3. DetailActivity

- 선택한 영화의 상세 정보 화면

- Firestore에 저장된 데이터를 불러와 표시

    - 포스터, 제목, 개봉연도, 줄거리, 리뷰, 평점

- 추가 기능

    - ⭐ 별점과 리뷰 수정

    - ❌ 영화 삭제
 
    - 🎥 해당 영화의 TMDB 상세 페이지로 이동 (포스터 클릭 시)

    - 🎥 해당 영화의 Youtube 예고편으로 이동 (Youtube 로고 클릭 시)

## 실행

### 📱 모바일에서 APK 실행

- `app-debug.apk`를 모바일에 내려받아 설치 후 실행 가능합니다.

### 🖥️ PC에서 APK 실행 (Android Emulator)

1. Android Studio → Device Manager → Create Virtual Device → 에뮬레이터 실행
2. APK 설치:
   - 드래그&드롭: `app-debug.apk`를 에뮬레이터 창에 끌어넣기
   - 또는 ADB:
     ```bash
     adb install -r app-debug.apk
     ```
3. 앱 서랍에서 아이콘 실행

## 🛠️ 기술 스택

- Android Studio Meerkat | 2024.3.1
- Firebase Firestore
- Language : Kotlin


## 스크린 샷

# Android-webview-fileupload

## 애플리케이션 설명

- 안드로이드 웹뷰에서 파일업로드 기능을 수행한다.
- 웹 `<input>` 태그의 `accept`를 구분하여 카메라/비디오 앱과 파일탐색기를 동시에 작업을 선택할 수 있도록 하였다.
  - `accpet`이 `.jpg or image/*`일 경우 카메라앱
  - `.mp4 or video/*`일 경우 비디오앱
- 파일탐색기에서는 웹에서 지정한 MIME, accept타입을 모두 필터링 할 수 있게 하였다.
  - 단, 웹에서 정확한 표현식을 작성해야 한다.
- 원하는 URL을 입력하여 업로드 테스트할 수 있고 URL을 미기입시 업로드를 테스트할 수 있는 웹으로 이동함.
  - 노션에서 테스트하기 편리함
- 하나의 activity에 다 정의하기에 너무 많아 특정 로직을 수행하는 class를 새로 만듬

## 스크린샷

<img src="https://user-images.githubusercontent.com/58923717/135853289-a98411e6-0c77-4714-8901-1a79a232e007.jpg" width=300/>

## 부가설명

- 4.4, 4.x 등 몇몇 webView에서 설정값이 다르지만 너무 옛날 API여서 최소 API를 21로 잡았음.


# AndroidToPCAgent
PC와 Android Device 연동 구상
 
 
> 필요성: 과거 feature phone과는 달리 Smart Phone의 경우는 PC를 통해 핸드폰의 정보를 저장/삭제할 필요가 전혀 없다. 그럼에도 불구하고 PC와 Android 단말기의 연동이 필요한 이유는 다음과 같다.
 
- 오랜 시간을 요구하는 QA 작업의 자동화(배터리, 네트웍상태 체크, CPU 점유, 등등)
- 단말기에 대용량데이터(파일, phonebook, 기타 등등)를 전송해야 할 경우
  
연동환경:  Android에서 PC와 연동하기 위해서는 다음과 같은 환경이 필요하다.

단말기에서는 반드시 **[설정-응용프로그램-개발-USB 디버깅]**이 체크되어 있어야 한다.
PC에서는 USB Driver가 설치되어 있어야 하며, ADB도 설치가 되어 있어야 한다.
 
연동방법:  pc에서 adb를 이용하여 linux App를 실행한다. Linux App는 단말기 내의 APK와 소켓 통신을 한 후, 그 결과값을 printf로 출력한다. PC에서는 그 결과값을 분석하여 처리한다.
![image](http://postfiles14.naver.net/20120215_189/adsloader_13293041171767kEkS_JPEG/2.JPG?type=w2) 

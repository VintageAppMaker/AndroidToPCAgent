# AndroidToPCAgent
[원본블로그](http://blog.naver.com/adsloader/50134141484)

### PC와 Android Device 연동 구상 
 
> 필요성: 과거 feature phone과는 달리 Smart Phone의 경우는 PC를 통해 핸드폰의 정보를 저장/삭제할 필요가 전혀 없다. 그럼에도 불구하고 PC와 Android 단말기의 연동이 필요한 이유는 다음과 같다.
 
- 오랜 시간을 요구하는 QA 작업의 자동화(배터리, 네트웍상태 체크, CPU 점유, 등등)
- 단말기에 대용량데이터(파일, phonebook, 기타 등등)를 전송해야 할 경우
  
연동환경:  Android에서 PC와 연동하기 위해서는 다음과 같은 환경이 필요하다.
![image](http://postfiles3.naver.net/20120215_18/adsloader_1329304117916Fdxhf_JPEG/1.JPG?type=w2) 

단말기에서는 반드시 **[설정-응용프로그램-개발-USB 디버깅]**이 체크되어 있어야 한다.
PC에서는 USB Driver가 설치되어 있어야 하며, ADB도 설치가 되어 있어야 한다.
 
연동방법:  pc에서 adb를 이용하여 linux App를 실행한다. Linux App는 단말기 내의 APK와 소켓 통신을 한 후, 그 결과값을 printf로 출력한다. PC에서는 그 결과값을 분석하여 처리한다.
![image](http://postfiles14.naver.net/20120215_189/adsloader_13293041171767kEkS_JPEG/2.JPG?type=w2) 

### PC에서 ADB로 제어
 
adb에서 Activity와 Service에 정보를 전달하는 방법이 있다. adb shell의 "AM"이라는 명령어이다. 
이 명령어는 사실 Android의 Froyo 풀소스를 보게되면 AM.java라는 파일로 존재하는 프로그램이다. 

만약 com.psw 패키지의 com.psw.main Activity를 호출하고자 한다면 다음과 같이 PC의 커맨드 창에서 입력하면 된다.

~~~
 adb shell am start -a android.intent.action.MAIN -n com.psw/com.psw.main
~~~

그러나 am의 명령어는 Activity나 Service에 파마메터를 통해 Intent를 던지는 것 밖에는 할 수 없다. 그러므로 PC와의 
통신으로 사용하기에는 부족한 면이 있다. 그래서 linux용 Application을 만들어 그 프로그램을 호출하면 Activity나 Service와
TCP 통신을 하고 결과값을 console로 받으면 원하는 기능을 구현할 수 있다.

ADB shell에서 linux application을 실행할 수 있는 경로는 다음과 같다. 

~~~
 /data/local/
~~~

이곳에 프로그램을 복사한 후, 퍼미션을 지정한 후에 실행하면 된다.
아래는 DOS 커맨드 상에서 명령어를 편하게 사용할 수 있도록 만든 배치파일이다. 

~~~
@echo off
 
title APK Analyzer(Device, PC)
@ 작성자: 박성완(adsloader@naver.com)
@ 작성일: 2011.05.28
@ 목적  : 단말기 채크용 메뉴
 
:_main
cls
color 2f
echo.         -(Device)-------------------------------------
echo          1. Device의 실행 프로세스 가져오기 
echo          2. APK의 정보보기 
echo          3. Device의 정보보기 
echo          4. Debuggable 채크(단말기 내의 APK) 
echo          5. All installed APK List 
echo          6. SMS 정보
echo          7. 그림정보
echo          8. mp3 정보
echo          9. 동영상 정보
echo          10. lockscreen on
echo          11. lockscreen off
echo.
echo.         --(필수매뉴)----------------------------------
echo          0. 필수프로그램 설치(Device 연결시 반드시 해야 함)
echo.         ----------------------------------------------
echo          q. 종료 
echo.
echo.
 
set /p menu=번호를 입력해주세요 :
if "%menu%"=="1" goto _devps
if "%menu%"=="2" goto _apkinfo
if "%menu%"=="3" goto _devinfo
if "%menu%"=="4" goto _debug
if "%menu%"=="5" goto _ALLAPK
if "%menu%"=="6" goto _SMS
if "%menu%"=="7" goto _PIC
if "%menu%"=="8" goto _MP3
if "%menu%"=="9" goto _VIDEO
if "%menu%"=="10" goto _LOCKON
if "%menu%"=="11" goto _LOCKOFF
if "%menu%"=="0" goto _install
if "%menu%"=="q" goto _end
goto _main
 
:_devps
color 1B
adb shell ps
pause
goto _main
 
:_apkinfo
cls
color 07
set /p packagename=패키지명 입력해주세요 :
adb shell /data/local/tmp/cmd_apk app %packagename%
pause
goto _main
 
:_devinfo
color 07
cls
adb shell /data/local/tmp/cmd_apk dev  psw
pause
goto _main
 
:_debug
color 06
cls
adb shell /data/local/tmp/cmd_apk debug psw
pause
goto _main
 
:_ALLAPK
color 0F
cls
adb shell /data/local/tmp/cmd_apk all psw
pause
goto _main
 
:_SMS
color 0F
cls
adb shell /data/local/tmp/cmd_apk sms psw
pause
goto _main
 
:_PIC
color 0F
cls
adb shell /data/local/tmp/cmd_apk pic psw
pause
goto _main
 
:_MP3
color 0F
cls
adb shell /data/local/tmp/cmd_apk mp3 psw
pause
goto _main
 
:_VIDEO
color 0F
cls
adb shell /data/local/tmp/cmd_apk video psw
pause
goto _main
 
:_LOCKON
color 0F
cls
adb shell /data/local/tmp/cmd_apk keyguard on
pause
goto _main
 
:_LOCKOFF
color 0F
cls
adb shell /data/local/tmp/cmd_apk keyguard off
pause
goto _main
:_install
cls
color 07
 
echo. linux agent 설치 중 입니다...
adb push cmd_apk /data/local/tmp
adb shell chmod 755 /data/local/tmp/cmd_apk
 
echo. apk 설치 중 입니다...
adb uninstall com.psw
adb install apklistener.apk
 
echo. apk 실행합니다...
adb shell am start -a android.intent.action.MAIN -n com.psw/com.psw.main
pause
 
goto _main
 
:_end
color 07
~~~
 
### Linux Agent 만들기
Linux App을 만들기 위해서는 NDK를 받아서 Execute-able 바이너리를 생성하면 된다. 이는 NDK의 Sample을 보면 나와있다.
그러나 윈도우에서 쉽게 컴파일 하고 싶다면, cross-compiler를 다운받아서 컴파일 해도 된다.

컴파일러를 다운로드 받는 방법은 다음과 같다.
   1. http://www.codesourcery.com/sgpp/lite/arm/portal/subscription?@template=lite 에 접속
   2. Sourcery CodeBench Lite 2011.09-70 All versions... 을 선택
   3. IA32 Windows Installer 를 선택

설치 후, 컴파일 및 퍼미션을 수행하는 방법은 다음과 같다.

~~~
arm-none-linux-gnueabi-gcc -o cmd_apk -static cmd_apk.c
adb push cmd_apk /data/local/tmp
adb shell chmod 755 /data/local/tmp/cmd_apk
~~~ 


cmd_apk.c

~~~c
/*
    제목: sendtoapk
    작성자: 박성완(adsloader@naver.com)
    작성일: 2011.05.26
    목적 : 1. APK에다소켓으로메시지전달
           2. adb shell을이용하여PC에서전송
        
 */
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h> /* basic socket definitions */
#include <sys/types.h> /* basic system data types */
#include <sys/un.h> /* for Unix domain sockets */
#include <netinet/in.h> /* for Unix internet sockets */
 
#define PACKET_SIZE 1024
#define KEYWORD_SIZE 100
 
// Packet 정의
typedef struct tagPacket{
    char keyword[KEYWORD_SIZE];
    char pData [PACKET_SIZE - KEYWORD_SIZE];
} Packet;
 
enum ERRORID{
    PARAMERROR =0,
    SOCEKTERROR,
    PARSINGERROR
};
 
// 사용법설명
static void usage()
{
    char* usemessage = "=======================================\r\n"
                       "usage...\r\n"
                       "./cmd_apk [command] [data]\r\n"
                       "ex) cmd_apk app com.android.phone\r\n"
                       "    cmd_apk dev PSW\r\n"
                       "    cmd_apk debug PSW\r\n"
                       "\r\n"
                       "\r\n"
                       "[command]\r\n"
                       "   -app:  app infomation. [data] is package name\r\n"
                       "   -dev:  device information. [data] is always \"PSW\"\r\n"
                       "   -debug:check debuggable. [data] is always \"PSW\"\r\n"
                       "   -all  :display all insatlled apk. [data] is always \"PSW\"\r\n"
                       "   -sms  :display all sms data. [data] is always \"PSW\"\r\n"
                       "   -pic  :display all Picture. [data] is always \"PSW\"\r\n"
                       "   -mp3  :display all music. [data] is always \"PSW\"\r\n"
                       "   -video:display all Movie. [data] is always \"PSW\"\r\n"
                       "   -keyguard:keyguard enable/disable. [data] 'on', 'off' \r\n"
                       "\r\n"
                       "=======================================\r\n";
 
    printf(usemessage);
}
 
// Error 메시지
static char* GetMessage(int nId)
{
    char* szMessage [] = {
        "ERROR# Parameter wrong",
        "ERROR# Socket",
        "ERROR# Parsing"
    };
 
    return szMessage[nId];
}
 
// Error 문자열출력-> 결국PC 분석
void ErrMSG(char* msg)
{
    printf("%s", msg);
}
 
// 크기만큼읽기
static int RecvBySize(int fd, char* pData, int nSize)
{
    int nReadSize = 0;
    int nReserved = nSize;
 
    while(1){
        int nRead = recv(fd, pData + nReadSize, nReserved, 0);
 
        nReadSize += nRead;
        nReserved -= nRead;
       
        if(nReadSize == nSize) break;
        if(nRead     == -1   ) return nRead;
        if(nRead     == 0    ) break;
    }
 
    return nReadSize;
}
 
// 보내기
static void ReadWait(int fd)
{   
    char pSize[KEYWORD_SIZE];
    Packet p;
   
    int length = -1;
       
    if ((length = RecvBySize(fd, pSize, KEYWORD_SIZE)) <= 0){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        return;
    }
   
    // 받을크기저장
    length = atoi(pSize);
    if (length <= 0) return;
   
    char* pData = malloc(length);
 
    if ((length = RecvBySize(fd, pData, length)) <= 0){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        return;
    }
   
    //printf("\r\n___________result(size = %s)_____________\r\n", pSize);
    printf("%s", pData);
 
    free(pData);
}
 
// 받기
static void SendCommand(int fd, char* szKeyword, char* szData)
{
    Packet p;
   
    bzero(&p, sizeof(Packet));      
    strcpy(p.keyword, szKeyword);
    strcpy(p.pData, szData);
    
    send(fd, (char*)&p, PACKET_SIZE, 0);
}
 
// 명령어처리
static void CommandProgress(char* szKeyword, char* szData)
{
    int DEST_PORT = 12222;
    char* DEST_IP = "127.0.0.1";
 
    int sockfd;
    struct sockaddr_in dest_addr; 
 
 
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
 
    dest_addr.sin_family = AF_INET;       
    dest_addr.sin_port = htons(DEST_PORT);
    dest_addr.sin_addr.s_addr = inet_addr(DEST_IP);
    bzero(&(dest_addr.sin_zero), 8);      
   
    if (connect(sockfd, (struct sockaddr *)&dest_addr, sizeof(struct sockaddr)) == -1){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        exit(0);
    }
   
    SendCommand(sockfd, szKeyword, szData);
    ReadWait(sockfd);
   
    close( sockfd );
}
 
 
// 메인함수
int main( int argc, char **argv )
{
    if (argc < 3){
        usage();
        //ErrMSG (GetMessage(PARAMERROR));
        exit(0);
    }
 
    CommandProgress(argv[1], argv[2]);
}
~~~

### 실행하기
- 단말기를 USB 케이블과 연결한다(ADB와 USB 드라이버는 이미 설치되어 있어야 한다).
- run.bat을 실행한다.

![image](http://postfiles1.naver.net/20120215_128/adsloader_1329315188800Fj1cc_PNG/4.PNG?type=w2) 

- 0을 눌러 APK를 설치한다.
- 5를 눌러 APK List를 본다.

![image](http://postfiles16.naver.net/20120215_223/adsloader_1329315240897mOqsf_PNG/5.PNG?type=w2) 

- 9를 눌러 동영상 정보를 본다.

![image](http://postfiles7.naver.net/20120215_278/adsloader_1329315487503vyE5V_PNG/6.PNG?type=w2) 



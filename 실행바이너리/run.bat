@echo off

title APK Analyzer(Device, PC) 
@ 작성자: 박성완(adsloader@naver.com)
@ 작성일: 2011.05.28 
@ 목적  : 단말기 채크용 메뉴 

:_main
cls
color 2f
echo.
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

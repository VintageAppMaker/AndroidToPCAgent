@echo off

title APK Analyzer(Device, PC) 
@ �ۼ���: �ڼ���(adsloader@naver.com)
@ �ۼ���: 2011.05.28 
@ ����  : �ܸ��� äũ�� �޴� 

:_main
cls
color 2f
echo.
echo.         -(Device)-------------------------------------
echo          1. Device�� ���� ���μ��� ��������  
echo          2. APK�� ��������  
echo          3. Device�� ��������  
echo          4. Debuggable äũ(�ܸ��� ���� APK)  
echo          5. All installed APK List  
echo          6. SMS ����
echo          7. �׸����� 
echo          8. mp3 ����
echo          9. ������ ����
echo          10. lockscreen on
echo          11. lockscreen off
echo.
echo.         --(�ʼ��Ŵ�)----------------------------------
echo          0. �ʼ����α׷� ��ġ(Device ����� �ݵ�� �ؾ� ��) 
echo.         ----------------------------------------------
echo          q. ����  
echo.
echo.

set /p menu=��ȣ�� �Է����ּ��� : 
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
set /p packagename=��Ű���� �Է����ּ��� : 
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

echo. linux agent ��ġ �� �Դϴ�...
adb push cmd_apk /data/local/tmp
adb shell chmod 755 /data/local/tmp/cmd_apk

echo. apk ��ġ �� �Դϴ�...
adb uninstall com.psw 
adb install apklistener.apk

echo. apk �����մϴ�...
adb shell am start -a android.intent.action.MAIN -n com.psw/com.psw.main
pause

goto _main

:_end
color 07 

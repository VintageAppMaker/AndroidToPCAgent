#!/bin/bash
RED='\033[0;41;30m'
STD='\033[0;0;39m'

pause(){
    read -p "Press [Enter] key to continue..."
}

one(){
  	adb shell ps
    pause
}

two(){
    echo "apk 실행합니다..."
    adb shell am start -a android.intent.action.MAIN -n com.psw/com.psw.main
    pause
}

three(){
    echo "install된 apk 리스트..."
    adb shell /data/local/tmp/cmd_apk all psw
    pause
}

four(){
    echo "동영상 정보..."
    adb shell /data/local/tmp/cmd_apk video psw
    pause
}

five(){
    echo "이미지 정보..."
    adb shell /data/local/tmp/cmd_apk pic psw
    pause
}

six(){
    echo "SMS 정보..."
    adb shell /data/local/tmp/cmd_apk sms psw
    pause
}
zero(){
    echo "linux agent 설치 중 입니다..."
    adb push cmd_apk /data/local/tmp
    adb shell chmod 755 /data/local/tmp/cmd_apk

    echo "apk 설치 중 입니다..."
    adb uninstall com.psw
    adb install apklistener.apk
}

# function to display menus
show_menus() {
  	clear
    echo          "1. Device의 실행 프로세스 가져오기"
    echo          "2. APK(app) 실행하기"
    echo          "3. All installed APK List"
    echo          "4. 동영상 정보"
    echo          "5. 이미지 정보"
    echo          "6. SMS 정보"
    echo          "--(필수매뉴)----------------------------------"
    echo          "0. 필수프로그램 설치(Device 연결시 반드시 해야 함)"
    echo          "q. 종료  "
    echo          "----------------------------------------------"
}

read_options(){
  	local choice
  	read -p "Enter choice [ 1 - 0 ] q is quit " choice
  	case $choice in
    		1) one ;;
    		2) two ;;
        3) three ;;
        4) four ;;
        5) five ;;
        6) six ;;
    		q) exit 0;;
        0) zero ;;
    		*) echo -e "${RED}Error...${STD}" && sleep 2
  	esac
}

# ----------------------------------------------
# Step #3: Trap CTRL+C, CTRL+Z and quit singles
# ----------------------------------------------
trap '' SIGINT SIGQUIT SIGTSTP

# -----------------------------------
# Step #4: Main logic - infinite loop
# ------------------------------------
while true
do
  	show_menus
  	read_options
done

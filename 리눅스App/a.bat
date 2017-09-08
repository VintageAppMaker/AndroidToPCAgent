arm-none-linux-gnueabi-gcc -o cmd_apk -static cmd_apk.c
adb push cmd_apk /data/local/tmp
adb shell chmod 755 /data/local/tmp/cmd_apk


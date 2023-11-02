:@echo off

adb shell rm /mnt/sdcard/Napoleon/napoleon.db
adb shell am broadcast -a com.grsoft.napoleon.test.intent.SyncTest

:start

set ADB_RES=
for /f %%i in ('adb shell ls /mnt/sdcard/Napoleon ^| grep napoleon.db') do set ADB_RES=%%i
if '%ADB_RES%' == '' goto wait
goto end

:wait
echo Wait db
sleep 1
goto start

:end
sleep 2
adb pull /mnt/sdcard/Napoleon/napoleon.db
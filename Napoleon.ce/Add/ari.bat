@echo off

cmd /c ar

if %ERRORLEVEL% == 0 (
	%PYTHONEXE% %~dp0install.py
	cmd /c "adb shell monkey -p com.grsoft.napoleon -c android.intent.category.LAUNCHER 1"
)

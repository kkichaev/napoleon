@echo off

if not "%1" == "" (
	echo "Usage: ant release"
	exit /b
)

cmd /C "ant release"

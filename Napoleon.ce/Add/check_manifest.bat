@echo off
%PYTHONEXE% %~dp0check_manifest.py
echo %ERRORLEVEL%
pause
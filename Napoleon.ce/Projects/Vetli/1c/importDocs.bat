@echo off

echo %PROCESSOR_ARCHITECTURE% | find /i "x86" > nul
if %errorlevel%==0 (
    cmd.exe /C "cscript %~dp0importDocs.js"
) else (
    %windir%\SysWoW64\cmd.exe /C "cscript %~dp0importDocs.js"
)

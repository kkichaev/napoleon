@ECHO OFF
call %ANT_HOME%\bin\ant clean
call %ANT_HOME%\bin\ant release
pause
@ECHO ON

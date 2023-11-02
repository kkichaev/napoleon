@ECHO OFF
call %ANT_HOME%\bin\ant clean all
call %ANT_HOME%\bin\ant release
pause
@ECHO ON

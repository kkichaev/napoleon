make server -B --jobs=8
if %errorlevel% neq 0 goto error

make reporter --jobs=8
if %errorlevel% neq 0 goto error

make manager --jobs=8
if %errorlevel% neq 0 goto error

make install
if %errorlevel% neq 0 goto error

goto ok

:error
pause

:ok
if "%1" == "" goto end
if exist %1pod%2.dbf goto end
copy %3pd.dbf %1pod%2.dbf
:end
exit

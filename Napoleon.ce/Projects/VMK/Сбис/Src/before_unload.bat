copy /y "%2_F.DBF" "%1_F.dbf"
copy /y "%2_W.DBF" "%1_W.dbf"
del "%1_O*.dbf"
:pause
exit
copy /y "%1_F.DBF" "%1FOLDERS.dbf"
copy /y "%1_W.DBF" "%1WAREHOUS.dbf"
copy /y "%1G*.DBF" "%1O*.dbf"
copy /y "%1__I*.DBF" "%1PRM*.dbf"
"%1/Src/encoder" "%1order.txt" "%1order.cfg"
:pause
exit
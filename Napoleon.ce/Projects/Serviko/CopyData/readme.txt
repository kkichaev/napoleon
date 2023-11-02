Зарегистрировать COM объект
regsvr32 ComGRServer.dll

Изменить значения в copy_data.js на правильные
var SRC_IP = '10.20.0.79';
var SRC_PORT = 8850;

var DEST_IP = '127.0.0.1';
var DEST_PORT = 8850;

Запустить
cscript copy_data.js



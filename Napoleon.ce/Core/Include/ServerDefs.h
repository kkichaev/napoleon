/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Константы определенные для обмена с сервером
 *
 * ert   24/09/2009   creating
 */ 
#ifndef __SERVER_DEFS_H
#define __SERVER_DEFS_H

#define PRIMARY_KEY_STR				L"primaryKey"
#define INDEX_KEY_STR				L"index"
#define UNIQUE_INDEX_KEY_STR		L"uniqueIndex"
#define ADMIN_ID						L"\x1a\x2d\x3m\x4i\x5n"

#define SERVER_COMMAND     L"ServerCommand"
#define COMMAND_MEMBER     L"command"
#define PARAM_MEMBER       L"param"
#define USERID_MEMBER      L"userid"
#define LOGIN_MEMBER       L"login"
#define PASSWORD_MEMBER    L"password"
#define VERSION_MEMBER     L"version"
#define DURATION_MEMBER    L"duration"
#define PROGID_MEMBER      L"progid"

#define SERVER_ANSWER      L"ServerAnswer"
#define RESPONSE_MEMBER    L"response"
#define MESSAGE_MEMBER     L"message"

#define USER_OBJECT        L"User"
#define USER_ID_MEMBER     L"id"
#define USER_NAME_MEMBER   L"name"
#define DURATION_MEMBER    L"duration"
#define REGISTRED_MEMBER   L"registred"
#define CATEGORY_MEMBER		L"category"
#define LICENSE_TYPE_MEMBER   L"licenseType"

#define IMPERSONATE        L"AS"

#define QUIT_COMMAND        L"QUIT" // для освобождения сессии

#define GET_COMMAND         L"GET"
#define GET_PARAM_SEPARATOR L','

// параметр ObjName:Condition
#define SELECT_COMMAND      L"SELECT"

 // параметр ObjName:Condition
#define REMOVE_COMMAND      L"REMOVE"

#define PUT_COMMAND         L"PUT"

// параметр RET_IDS для возвращения PK новой записи (только если PK integer)
#define FORCE_PUT           L"FORCE PUT"
#define PUT_NO_EXEC         L"PUT NO EXEC"
#define RET_IDS             L"RETURN IDS"

#define BYE_COMMAND         L"BYE"

#define DONE_COMMAND        L"DONE"

// параметр PluginName
#define PLUGIN_CONNECT      L"Plugin Connect"

#define KEEP_ALIVE          L"Keep Alive"

// параметр ObjName
#define GET_OBJ_FORMAT      L"ObjFormatGet"

// plugin не ждет ответа
#define PLUGIN_CLOSED       L"Plugin Closed"

// параметр название модуля
#define GET_REPORT          L"Get Report"

// команда работы с объектами, за ней следуют наборы объектов
#define OBJECTS_COMMAND     L"Object Command"
#define READ_OBJECTS        L"Read"
#define WRITE_OBJECTS       L"Write"
#define REMOVE_OBJECTS      L"Remove"
#define SERV_RESPONSE       L"^response"
#define SERV_RESULT         L"^result"

#define UPDATE_COMMAND      L"UpdateCommand"
#define PACKET_MEMBER       L"packet"

// param  category/cur_version
// return upd_version/upd_size
#define CHECK_UPDATE        L"CHECK UPDATE"

// param  category/cur_version/packet_size
// return upd_version/upd_size
#define GET_UPDATE          L"GET UPDATE"

// param category/upd_version/offset/packet_size
#define GET_UPD_PACKET      L"GET UPD PACKET"

#define COM_LOGIN           L"\x2C\x3O\x4M\x5L\x6O\x7G\x7I\x6N"
#define COM_ID              L"\x5A\x1O\x1fM\xeL\xdI\xcG\x23I\x1D"
#define COM_DIVISION        -1

#define SERVER_INFO         L"%ServerInfo"

#endif
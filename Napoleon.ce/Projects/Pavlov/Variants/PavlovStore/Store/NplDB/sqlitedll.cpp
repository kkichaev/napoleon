/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * DLL wrapper для SQLite
 *
 *  ert   16/10/2009   creating
 */ 
#include "stdafx.h"

#define DECL_EXPORT
#include "sqlitedll.h"

DECL_SPEC int sql_bind_int64(sqlite3_stmt* stmt, int col, sqlite3_int64 value)
{
   return sqlite3_bind_int64(stmt, col, value); 
}

DECL_SPEC int sql_bind_int(sqlite3_stmt* stmt, int col, int value)
{
   return sqlite3_bind_int(stmt, col, value);
}

DECL_SPEC int sql_bind_double(sqlite3_stmt* stmt, int col, double value)
{
   return sqlite3_bind_double(stmt, col, value);
}

DECL_SPEC int sql_bind_text16(sqlite3_stmt* stmt, int col, const void* value, int size, void(*xDel)(void*))
{
   return sqlite3_bind_text16(stmt, col, value, size, xDel);
}

DECL_SPEC int sql_bind_blob(sqlite3_stmt* stmt, int col, const void* value, int n, void(*xDel)(void*))
{
   return sqlite3_bind_blob(stmt, col, value, n, xDel);
}

DECL_SPEC sqlite3_int64 sql_column_int64(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_int64(stmt, iCol); 
}

DECL_SPEC const unsigned char *sql_column_text(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_text(stmt, iCol);
}

DECL_SPEC int sql_column_int(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_int(stmt, iCol);
}

DECL_SPEC double sql_column_double(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_double(stmt, iCol);
}

DECL_SPEC int sql_column_bytes(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_bytes(stmt, iCol);
}

DECL_SPEC const void *sql_column_blob(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_blob(stmt, iCol);
}

DECL_SPEC const void *sql_column_text16(sqlite3_stmt* stmt, int iCol)
{
   return sqlite3_column_text16(stmt, iCol);
}

DECL_SPEC int sql_create_collation16(sqlite3* db, const void *zName, int eTextRep, void* param, int(*xCompare)(void*,int,const void*,int,const void*))
{
   return sqlite3_create_collation16(db, zName, eTextRep, param, xCompare);
}

DECL_SPEC int sql_close(sqlite3* db)
{
   int ret = SQLITE_OK;
   if( db != NULL )
   {
      sqlite3_stmt *pStmt;
      while( (pStmt = sqlite3_next_stmt(db, 0))!=0 )
         sqlite3_finalize(pStmt);

      ret = sqlite3_close(db);
      sqlite3_shutdown();
   }
   return ret;
}

DECL_SPEC int sql_open(const char *filename, sqlite3 **ppDb)
{
   sqlite3_initialize();
   return sqlite3_open(filename, ppDb);
}

DECL_SPEC int sql_open16(const void *filename, sqlite3 **ppDb)
{
   sqlite3_initialize();
   return sqlite3_open16(filename, ppDb);
}

DECL_SPEC int sql_exec(sqlite3* db, const char *sql, int (*callback)(void*,int,char**,char**), void* param, char **errmsg )
{
   return sqlite3_exec(db, sql, callback, param, errmsg);
}

DECL_SPEC void sql_free(void* p)
{
   sqlite3_free(p);
}

DECL_SPEC int sql_finalize(sqlite3_stmt *pStmt)
{
   return sqlite3_finalize(pStmt);
}

DECL_SPEC int sql_prepare16(
  sqlite3 *db,            /* Database handle */
  const void *zSql,       /* SQL statement, UTF-16 encoded */
  int nByte,              /* Maximum length of zSql in bytes. */
  sqlite3_stmt **ppStmt,  /* OUT: Statement handle */
  const void **pzTail     /* OUT: Pointer to unused portion of zSql */
)
{
   return sqlite3_prepare16(db, zSql, nByte, ppStmt, pzTail);
}

DECL_SPEC int sql_prepare16_v2(
  sqlite3 *db,            /* Database handle */
  const void *zSql,       /* SQL statement, UTF-16 encoded */
  int nByte,              /* Maximum length of zSql in bytes. */
  sqlite3_stmt **ppStmt,  /* OUT: Statement handle */
  const void **pzTail     /* OUT: Pointer to unused portion of zSql */
)
{
   return sqlite3_prepare16_v2(db, zSql, nByte, ppStmt, pzTail);
}

DECL_SPEC int sql_step(sqlite3_stmt* stmt)
{
   return sqlite3_step(stmt);
}

DECL_SPEC int sql_reset(sqlite3_stmt *pStmt)
{
   return sqlite3_reset(pStmt);
}

DECL_SPEC sqlite3_int64 sql_last_insert_rowid(sqlite3* db)
{
   return sqlite3_last_insert_rowid(db);
}

BOOL WINAPI DllMain( HANDLE hInstDll, ULONG ulReason, LPVOID lpReserved )
{
   return TRUE;
}

DECL_SPEC void sql_result_text16(sqlite3_context* context, const void* value, int len, void(* freeFunc)(void*))
{
   sqlite3_result_text16(context, value, len, freeFunc);
}

DECL_SPEC void sql_result_int(sqlite3_context* context, int value)
{
   sqlite3_result_int(context, value);
}

DECL_SPEC void sql_result_int64(sqlite3_context* context, sqlite3_int64 value)
{
   sqlite3_result_int64(context, value);
}

DECL_SPEC void sql_result_double(sqlite3_context* context, double value)
{
   sqlite3_result_double(context, value);
}

DECL_SPEC void sql_result_null(sqlite3_context* context)
{
   sqlite3_result_null(context);
}

DECL_SPEC void sql_result_error16(sqlite3_context* context, const void* value, int len)
{
   sqlite3_result_error16(context, value, len);
}

DECL_SPEC const void *sql_value_blob(sqlite3_value* value)
{
   return sqlite3_value_blob(value);
}

DECL_SPEC const void *sql_value_text16(sqlite3_value* value)
{
   return sqlite3_value_text16(value);
}

DECL_SPEC int sql_value_bytes(sqlite3_value* value)
{
   return sqlite3_value_bytes(value);
}

DECL_SPEC int sql_value_int(sqlite3_value* value)
{
   return sqlite3_value_int(value);
}

DECL_SPEC int sql_create_function16(sqlite3 *db, const void *zFunctionName, int nArg, int eTextRep, void *pApp,
  void (*xFunc)(sqlite3_context*,int,sqlite3_value**),
  void (*xStep)(sqlite3_context*,int,sqlite3_value**),
  void (*xFinal)(sqlite3_context*))
{
   return sqlite3_create_function16(db, zFunctionName, nArg, eTextRep, pApp, xFunc, xStep, xFinal); 
}
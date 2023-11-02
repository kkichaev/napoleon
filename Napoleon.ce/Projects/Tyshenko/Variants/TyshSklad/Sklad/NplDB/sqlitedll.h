/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * DLL wrapper для SQLite
 *
 *  ert   16/10/2009   creating
 */ 
#ifndef __SQLITE_DLL_H
#define __SQLITE_DLL_H

#ifdef DECL_EXPORT
#define DECL_SPEC __declspec(dllexport)
#else
#define DECL_SPEC __declspec(dllimport)
#endif

#include "sqlite3.h"

#ifdef __cplusplus
extern "C" {
#endif

DECL_SPEC int sql_bind_int64(sqlite3_stmt* stmt, int col, sqlite3_int64 value);
DECL_SPEC int sql_bind_int(sqlite3_stmt* stmt, int col, int value);
DECL_SPEC int sql_bind_double(sqlite3_stmt* stmt, int col, double value);
DECL_SPEC int sql_bind_text16(sqlite3_stmt* stmt, int col, const void* value, int size, void(*xDel)(void*));
DECL_SPEC int sql_bind_blob(sqlite3_stmt* stmt, int col, const void* value, int n, void(*xDel)(void*));

DECL_SPEC sqlite3_int64 sql_column_int64(sqlite3_stmt* stmt, int iCol);
DECL_SPEC const unsigned char *sql_column_text(sqlite3_stmt* stmt, int iCol);
DECL_SPEC int sql_column_int(sqlite3_stmt* stmt, int iCol);
DECL_SPEC double sql_column_double(sqlite3_stmt* stmt, int iCol);
DECL_SPEC int sql_column_bytes(sqlite3_stmt* stmt, int iCol);
DECL_SPEC const void *sql_column_blob(sqlite3_stmt* stmt, int iCol);
DECL_SPEC const void *sql_column_text16(sqlite3_stmt*, int iCol);
DECL_SPEC int sql_column_count(sqlite3_stmt *pStmt);
DECL_SPEC const void *sql_column_name16(sqlite3_stmt*, int N);

DECL_SPEC const void *sql_value_blob(sqlite3_value* value);
DECL_SPEC const void *sql_value_text16(sqlite3_value* value);
DECL_SPEC int sql_value_bytes(sqlite3_value* value);
DECL_SPEC int sql_value_int(sqlite3_value* value);

DECL_SPEC void sql_result_text16(sqlite3_context* context, const void* value, int len, void(*freeFunc)(void*));
DECL_SPEC void sql_result_int(sqlite3_context* context, int value);
DECL_SPEC void sql_result_int64(sqlite3_context* context, sqlite3_int64 value);
DECL_SPEC void sql_result_double(sqlite3_context* context, double value);
DECL_SPEC void sql_result_null(sqlite3_context* context);
DECL_SPEC void sql_result_error16(sqlite3_context* context, const void* value, int len);

DECL_SPEC int sql_create_collation16(sqlite3* db, const void *zName, int eTextRep, void* param, int(*xCompare)(void*,int,const void*,int,const void*));
DECL_SPEC int sql_create_function16(sqlite3 *db, const void *zFunctionName, int nArg, int eTextRep, void *pApp, void (*xFunc)(sqlite3_context*,int,sqlite3_value**), void (*xStep)(sqlite3_context*,int,sqlite3_value**), void (*xFinal)(sqlite3_context*));

DECL_SPEC int sql_close(sqlite3* db);
DECL_SPEC int sql_open(const char *filename, sqlite3 **ppDb);
DECL_SPEC int sql_open16(const void *filename, sqlite3 **ppDb);

DECL_SPEC int sql_exec(sqlite3* db, const char *sql, int (*callback)(void*,int,char**,char**), void* param, char **errmsg );
DECL_SPEC void sql_free(void* p);

DECL_SPEC int sql_finalize(sqlite3_stmt *pStmt);

DECL_SPEC int sql_prepare16(
  sqlite3 *db,            /* Database handle */
  const void *zSql,       /* SQL statement, UTF-16 encoded */
  int nByte,              /* Maximum length of zSql in bytes. */
  sqlite3_stmt **ppStmt,  /* OUT: Statement handle */
  const void **pzTail     /* OUT: Pointer to unused portion of zSql */
);

DECL_SPEC int sql_prepare16_v2(
  sqlite3 *db,            /* Database handle */
  const void *zSql,       /* SQL statement, UTF-16 encoded */
  int nByte,              /* Maximum length of zSql in bytes. */
  sqlite3_stmt **ppStmt,  /* OUT: Statement handle */
  const void **pzTail     /* OUT: Pointer to unused portion of zSql */
);

DECL_SPEC int sql_step(sqlite3_stmt*);
DECL_SPEC int sql_reset(sqlite3_stmt *pStmt);
DECL_SPEC sqlite3_int64 sql_last_insert_rowid(sqlite3*);

#ifdef __cplusplus
} // extern "C"
#endif

#endif
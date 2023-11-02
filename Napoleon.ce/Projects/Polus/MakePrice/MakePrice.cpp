// NplCombine.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <errno.h>
#include <stdarg.h>
#include <dbf.h>
#include <map>
#include <set>

using namespace std;

char folder1[MAX_PATH];
char folder2[MAX_PATH];

set<DWORD> fid;
map<DWORD, DWORD> f1, f2;

void Error(const char *msg, ...)
{
   va_list argptr;
   va_start(argptr, msg);

   char buf[1000];
   vsprintf(buf, msg, argptr);

   MessageBox(NULL, buf, "Îøèáêà", MB_OK | MB_ICONSTOP);
}

bool CopyHeader(const char *fileName, DataForm &base)
{
   _unlink(fileName);

   FILE *f = fopen(fileName, "wb");

   if( f == NULL ) return false;

   DBHead *hd = base.GetHead();
   long rc = hd->numRec;
   hd->numRec = 0;
   
   char *p = (char*)alloca(hd->headLen+1);
   memcpy(p, hd, sizeof(*hd));

   char *dest = p + sizeof(*hd);
   for( int i=0; i<base.NumFields(); i++ )
   {
      memcpy(dest, base.GetFieldBase() + i, sizeof(DBField));
      dest += sizeof(DBField);
   }
   *dest++ = '\xd';
   *dest++ = '\x1a';

   size_t num = fwrite(p, hd->headLen+1, 1, f);

   hd->numRec = rc;

   fclose(f);

   return (num == 1);
}

DWORD FolderID(DWORD cid, bool first)
{
   DWORD newID;
   if( fid.find(cid) != fid.end() )
      newID = *fid.rbegin() + 1;
   else
      newID = cid;

   if( first )
      f1[cid] = newID;
   else
      f2[cid] = newID;

   fid.insert(newID);
   return newID;
}

bool CombineFolders(const char *folder1, const char *folder2, const char *file1, const char *file2, const char* destF, bool createIDs)
{
   string destFile(folder1);
   destFile += destF;

   string srcFile(folder1);
   srcFile += file1;
   DataForm src1, src2, dest;

   if( src1.Open(srcFile.c_str()) == false )
      return false;

   srcFile = folder2;
   srcFile += file2;
   if( !src2.Open(srcFile.c_str()) )
      return false;

   if( !CopyHeader(destFile.c_str(), src1) )
      return false;
 
   if( !dest.Open(destFile.c_str()) )
      return false;

   int level = 0;
   int i;
   fid.insert(1);
   fid.insert(2);
   for( i=0; src1.ReadRec(i); i++ )
   {
      if( i == 0 )
      {
         dest.Fill("ID", 1.0);
         dest.Fill("LEVEL", 0);
         dest.Fill("NAME", "‘ª« ¤1");
         dest.Append();
         level = 1 - atoi(src1["LEVEL"]);
      }
      memcpy(dest.GetRec(), src1.GetRec(), dest.GetHead()->recLen);

      DWORD cl = atoi(src1["LEVEL"]) + level;
      dest.Fill("LEVEL", (double)cl);
      DWORD id = atoi(src1["ID"]);
      id = (createIDs) ? FolderID(id, true) : f1[id];
      dest.Fill("ID", (double)id);
      dest.Append();
   }

   for( i=0; src2.ReadRec(i); i++ )
   {
      if( i == 0 )
      {
         dest.Fill("ID", 2.0);
         dest.Fill("LEVEL", 0);
         dest.Fill("NAME", "‘ª« ¤2");
         dest.Append();
         level -= atoi(src2["LEVEL"]);
      }
      memcpy(dest.GetRec(), src2.GetRec(), dest.GetHead()->recLen);
      DWORD cl = atoi(src2["LEVEL"]) + level;
      dest.Fill("LEVEL", (double)cl);
      DWORD id = atoi(src2["ID"]);
      id = (createIDs) ? FolderID(atoi(src2["ID"]), false) : f2[id];
      dest.Fill("ID", (double)id);
      dest.Append();
   }

   return true;
}

bool CombineFolders(const char *folder1, const char *folder2)
{
   if( !CombineFolders(folder1, folder2, "Fldrs.dbf", "Fldrs.dbf", "FOLDERS.DBF", true) )
      return false;

   string f(folder1);
   f += "_F*.DBF";
   WIN32_FIND_DATA data;
   HANDLE h = FindFirstFile(f.c_str(), &data);
   if( h != INVALID_HANDLE_VALUE )
   {
      do
      {
         CombineFolders(folder1, folder2, data.cFileName, "Fldrs.dbf", data.cFileName + 1, false);
      } while( FindNextFile(h, &data) );

      FindClose(h);
   }
   return true;
}

bool CombineWH(const char *folder1, const char *folder2)
{
   string destFile(folder1);
   destFile += "WAREHOUS.DBF";
   string tempFile(destFile);
   tempFile += ".TMP";

   string srcFile(folder1);
   srcFile += "WH.DBF";
   DataForm src1, src2, dest;

   if( src1.Open(srcFile.c_str()) == false )
      return false;

   srcFile = folder2;
   srcFile += "WH.DBF";
   if( !src2.Open(srcFile.c_str()) )
      return false;

   if( !CopyHeader(tempFile.c_str(), src1) )
      return false;
 
   if( !dest.Open(tempFile.c_str()) )
      return false;

   //set<string> ids;
   int i;
   for( i=0; src1.ReadRec(i); i++ )
   {
      memcpy(dest.GetRec(), src1.GetRec(), dest.GetHead()->recLen);

      DWORD folder = f1[atoi(src1["FOLDER"])];
      dest.Fill("FOLDER", (double)folder);

      //ids.insert(Trunc(src1["ID"]));
      dest.Append();
   }

   for( i=0; src2.ReadRec(i); i++ )
   {
      memcpy(dest.GetRec(), src2.GetRec(), dest.GetHead()->recLen);

      const char *id = Trunc(src2["ID"]);
      dest.Fill("ID2", id);

      string idN(id);
      //char def = 1;
      //while( ids.find(idN) != ids.end() )
      //{
      //   idN.at(0) = def++;
      //}
      //ids.insert(idN);
      idN += "\t\x2";
      dest.Fill("ID", idN.c_str());

      DWORD folder = f2[atoi(src2["FOLDER"])];
      dest.Fill("FOLDER", (double)folder);
      dest.Append();
   }
   dest.Close();

   unlink(destFile.c_str());
   MoveFile(tempFile.c_str(), destFile.c_str());

   return true;
}

bool DoCombine(const char *folder1, const char *folder2)
{
   if( !CombineFolders(folder1, folder2) ) return false;

   return CombineWH(folder1, folder2);
}

bool LoadFoldersData(const char *addF)
{
   HKEY hk;
   char key[100];
   strcpy(key, "SOFTWARE\\Ert\\Napoleon");
   if( RegOpenKey(HKEY_LOCAL_MACHINE, key, &hk) != ERROR_SUCCESS )
      return false;

   DWORD cb = sizeof(folder1);
   if( RegQueryValueEx(hk, "ExchangeFolder",NULL,NULL, (LPBYTE)folder1, &cb) != ERROR_SUCCESS )
   {
      RegCloseKey(hk);
      return false;
   }
   RegCloseKey(hk);

   strcat(key, "\\");
   strcat(key, addF);
   if( RegOpenKey(HKEY_LOCAL_MACHINE, key, &hk) != ERROR_SUCCESS )
      return false;

   cb = sizeof(folder2);
   if( RegQueryValueEx(hk, "ExchangeFolder",NULL,NULL, (LPBYTE)folder2, &cb) != ERROR_SUCCESS )
   {
      RegCloseKey(hk);
      return false;
   }
   RegCloseKey(hk);
   if( folder1[strlen(folder1)-1] != '\\' )
      strcat(folder1, "\\");
   if( folder2[strlen(folder2)-1] != '\\' )
      strcat(folder2, "\\");
   return true;
}

int _tmain(int argc, _TCHAR* argv[])
{
   if( argc != 2 )
   {
      printf("MakePrice.exe AddKey");
      return 1;
   }

   if( !LoadFoldersData(argv[1]) )
      return 1;

   DoCombine(folder1, folder2);
	return 0;
}


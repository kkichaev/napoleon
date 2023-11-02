// MkUpdate.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <io.h>
#include <vector>
#include <string>
#include <Binary.h>
#include <zlib.h>
#include <shlobj.h>
#include "AES.h"
#include <AtlConv.h>

using namespace std;

extern Key uploadKey;

struct FileInfo
{
   FileInfo() : data(NULL) {}
   FileInfo(const FileInfo& fi)
   {
      this->operator=(fi);
   }

   ~FileInfo() { delete data; }

   FileInfo& operator= (const FileInfo& fi)
   {
      name = fi.name;
      data = fi.data;

      const_cast<FileInfo&>(fi).data = NULL;

      return *this;
   }

   string name;
   DWORD size;
   Binary *data;
};

typedef std::vector<FileInfo> FileInfoList;

bool Compress(Binary *destBuf, const Binary &srcBuf)
{
   z_stream stream;
 
   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   deflateInit(&stream, Z_BEST_COMPRESSION);
 
   BYTE *src = (BYTE*)(const char*)srcBuf;
   stream.avail_in = srcBuf.Size();
   stream.next_in = src;
  
   int size = srcBuf.Size() + sizeof(stream.adler);
   if( size < 1000 ) size = 1000;

   BYTE *dest = (BYTE*)destBuf->Alloc(size);
   stream.avail_out = size - sizeof(stream.adler);
   stream.next_out = dest;
 
   bool res = (deflate(&stream, Z_FINISH) == Z_STREAM_END && stream.avail_out != 0);

   DWORD outb = size - stream.avail_out;
   ToStreamBytes(dest + outb, stream.adler);
   destBuf->ReduceSize(outb + sizeof(stream.adler));

   deflateEnd(&stream);

   return true; 
}

bool MakeFolder(const char* outFolder)
{
   if( outFolder[1] == ':' || *outFolder == '\\' )
   {
      bool res = true;
      if( SHCreateDirectoryExA(NULL, outFolder, NULL) != ERROR_SUCCESS )
      {
         DWORD ec = GetLastError();
         res = (ec == ERROR_ALREADY_EXISTS || ec == ERROR_FILE_EXISTS);
      }
      return res;
   }

   char buf[MAX_PATH];
   strcpy(buf, outFolder);
   char *p = buf;
   while( true )
   {
      char* ep = strchr(p, '\\');
      if( ep ) *ep = '\0';
      CreateDirectoryA(buf, NULL);
      if( !ep ) break;
      *ep = '\\';
      p = ep + 1;
   }

   return true;
}

FILE* CreateFile(const char* outFolder, const char* category, const char* version)
{
   char fileName[MAX_PATH];
   wsprintfA(fileName, "%s\\%s%s.upd", outFolder, category, version);
   if( !MakeFolder(outFolder) ) return NULL;
   return fopen(fileName, "wb");
}

void WriteHeader(FILE *wr, Key codeKey, const char* category, const char* version)
{
   Binary src;

   unsigned sz = strlen(category) + strlen(version) + 2;
   BYTE* p = src.Alloc(sz);
   strcpy((char*)p, category);
   p += (strlen(category) + 1);
   strcpy((char*)p, version);

   Binary* head = AESEncode(src, codeKey);

   DWORD size = head->Size() + sizeof(size);

   fwrite(&size, sizeof(size), 1, wr);
   fwrite((const BYTE*)(*head), head->Size(), 1, wr);
   delete head;
}

void WriteBody(FILE *wr, Key codeKey, const FileInfoList& infos)
{
   int i;
   WORD count = infos.size();
   DWORD headSize = sizeof(headSize) + sizeof(count);
   DWORD bodySize = 0;

   for( i=0; i<count; i++ )
   {
      const FileInfo& fi = infos[i];

      headSize += sizeof(DWORD);
      headSize += sizeof(DWORD);
      headSize += fi.name.size() + 1;
      bodySize += fi.data->Size();
   }

   Binary data;
   BYTE* pHead = data.Alloc(headSize + bodySize);
   BYTE* pData = pHead + headSize;

   *(DWORD*)pHead = headSize;
   pHead += sizeof(DWORD);
   *(WORD*)pHead = count;
   pHead += sizeof(WORD);

   for( i=0; i<count; i++ )
   {
      const FileInfo& fi = infos[i];
      Binary* bdata = fi.data;

      *(DWORD*)pHead = bdata->Size();
      pHead += sizeof(DWORD);

      strcpy((char*)pHead, fi.name.c_str());
      pHead += fi.name.size() + 1;

      memcpy(pData, (const BYTE*)(*bdata), bdata->Size());
      pData += bdata->Size();
   }
   Binary *body = AESEncode(data, codeKey);
   fwrite((const BYTE*)(*body), body->Size(), 1, wr);
   delete body;
}

void MakeUpdateFile(Key codeKey, const char* outFolder, const char* category, const char* version, const FileInfoList& infos)
{
   FILE* wr = CreateFile(outFolder, category, version);

   if( wr == NULL ) return;

   WriteHeader(wr, codeKey, category, version);

   WriteBody(wr, codeKey, infos);

   fclose(wr);
}

bool LoadFile(FileInfo* fi, const char *filename)
{
   FILE* rd = fopen(filename, "rb");
   if( rd == NULL ) return false;

   long size = _filelength(_fileno(rd));

   Binary src, dest;

   BYTE *p = src.Alloc(size);
   fread(p, 1, size, rd);
   fi->data = new Binary();
   bool res = Compress(fi->data, src);
   fclose(rd);

   if( res )
   {
      const char* p = strrchr(filename, '\\');
      if( p == NULL ) p = filename;
      else p++;

      fi->name = p;
      fi->size = size;
   }
   return res;
}

int _tmain(int argc, TCHAR* argv[])
{
   if( argc < 5 )
   {
      printf("MkUpdate OutFolder Category Version File [File]");
      return 1;
   }

   USES_CONVERSION;
   FileInfoList infos;
   for( int i=4; i<argc; i++ )
   {
      FileInfo fi;
      const char *fileName = W2A(argv[i]);
      printf("Prepare %s\n", fileName);
      if( !LoadFile(&fi, fileName) )
      {
         printf("Can't load file %s\n", fileName);
         return 1;
      }

      if( i < argc-2 )
      {
         if( _tcscmp(argv[i+1], _T("--name")) == 0 )
         {
            fi.name = W2A((LPCWSTR)argv[i+2]);
            i += 2;
         }
      }

      infos.push_back(fi);
   }

   MakeUpdateFile(uploadKey, W2A(argv[1]), W2A(argv[2]), W2A(argv[3]), infos);

	return 0;
}

   
/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * пакет обновления
 *
 * ert   07/12/2009   creating
 */
#include "stdafx.h"
#include "updpacket.h"
#include "sessobj.h"
#include <zlib.h>
#ifdef UNIX
#else
#include <io.h>
#endif

extern Key uploadKey;

#ifdef UNIX
#else
static bool IsFileExists(const std::string& fileName)
{
   return (GetFileAttributesA(fileName.c_str()) != 0xFFFFFFFF);
}

static void WriteString(FILE* file, const std::string& str)
{
   WORD len = (WORD)str.size();
   fwrite(&len, sizeof(len), 1, file);
   fwrite(str.c_str(), sizeof(char), len, file);
}

static bool ReadString(FILE* file, std::string* str)
{
   WORD len;
   if( fread(&len, sizeof(len), 1, file) != 1 ) return false;

   char *buf = (char*)alloca(len);
   if( fread(buf, sizeof(char), len, file) != len ) return false;

   str->assign(buf, len);
   return true;
}
#endif

UpdatePacket::UpdatePacket()
{
}

UpdatePacket::~UpdatePacket()
{
}

bool UpdatePacket::UpdateFileData::Save(FILE *file) const
{
#ifdef UNIX
   return false;
#else
   WriteString(file, name);
   return true;
#endif
}

bool UpdatePacket::UpdateFileData::Load(FILE *file)
{
#ifdef UNIX
   return false;
#else
   return ReadString(file, &name);
#endif
}

bool UpdatePacket::FileList::Load(FILE *file)
{
#ifdef UNIX
   return false;
#else
   WORD count;
   if( fread(&count, sizeof(count), 1, file) != 1 ) return false;

   if( !ReadString(file, &folder) ) return false;

   bool res = true;
   while( count-- > 0 )
   {
      UpdateFileData ufd;

      if( !ufd.Load(file) )
      {
         res = false;
         break;
      }

      push_back(ufd);
   }

   return res;
#endif
}

bool UpdatePacket::FileList::Save(FILE *file) const
{
#ifdef UNIX
   return false;
#else
   WORD count = (WORD)size();
   fwrite(&count, sizeof(count), 1, file);

   WriteString(file, folder);

   bool retVal = true;
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( !i->Save(file) )
      {
         retVal = false;
         break;
      }
   }

   return retVal;
#endif
}

bool UpdatePacket::DecodeHead(const IBinary& binaryData)
{
   const BYTE* data = binaryData.Bytes();
   DWORD headSize = *(DWORD*)data;
   DWORD len = binaryData.Size() - headSize;
   if( headSize >= len - sizeof(DWORD) ) return false;

   data += sizeof(DWORD);
   headSize -= sizeof(DWORD);
   Binary* header = AESDecode(data, headSize, uploadKey);

   if( header == NULL ) return false;

   const BYTE* hp = (*header);
   category = (const char *)hp;
   version = ((const char*)hp + category.size() + 1);

   delete header;
   return true;
}

#ifdef UNIX
#else
static bool CheckFolder(const std::string& folder)
{
   if( CreateDirectoryA(folder.c_str(), NULL) != FALSE )
      return true;

   DWORD err = GetLastError();
   if( err == ERROR_ALREADY_EXISTS )
      return true;

   if( err == ERROR_PATH_NOT_FOUND )
   {
      size_t pos = folder.find_last_of('\\');
      if( CheckFolder(folder.substr(0, pos)) )
         return (CreateDirectoryA(folder.c_str(), NULL) == TRUE);
   }

   return false;
}

static FILE* CreateDestFile(const std::string& folder, const char* fileName)
{
   if( CheckFolder(folder) == false ) return NULL;

   std::string curF(folder);
   const char* p = strrchr(fileName, '\\');
   if( p != NULL )
   {
      curF.append(fileName, p - fileName);
      if( CheckFolder(curF) == false ) return NULL;
      fileName = p + 1;
   }
   curF += '\\';
   curF += fileName;

   return fopen(curF.c_str(), "wb");
}

const BYTE* DecompressFile(const std::string& folder, const char* fileName, const BYTE* data, DWORD dataSize)
{
   const DWORD BUF_SIZE = 1024 * 100;
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   inflateInit(&stream);

   stream.avail_in = dataSize;
   stream.next_in = (BYTE*)data;

   Binary destBuf;
   BYTE *dest = (BYTE*)destBuf.Alloc(BUF_SIZE);
   stream.avail_out = BUF_SIZE;
   stream.next_out = dest;

   FILE *destFile = CreateDestFile(folder, fileName);
   if( destFile == NULL ) return false;

   bool retval = true;
   int ec = Z_OK;
   while( ec != Z_STREAM_END )
   {
      ec = inflate(&stream, Z_NO_FLUSH);
      if( ec != Z_OK && ec != Z_STREAM_END )
      {
         retval = false;
         break;
      }

      fwrite(dest, BUF_SIZE - stream.avail_out, 1, destFile);
      stream.avail_out = BUF_SIZE;
      stream.next_out = dest;
   }
   fclose(destFile);

   inflateEnd(&stream);

   return (retval) ? (const BYTE*)stream.next_in + sizeof(DWORD) : NULL;
}
#endif

Binary* Decompress(const BYTE* src, DWORD dataSize, DWORD destSize)
{
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   inflateInit(&stream);

   stream.avail_in = dataSize;
   stream.next_in = (BYTE*)src;

   Binary* destBuf = new Binary();
   BYTE *dest = (BYTE*)destBuf->Alloc(destSize);
   stream.avail_out = destSize;
   stream.next_out = dest;

   bool retval = (inflate(&stream, Z_NO_FLUSH) == Z_STREAM_END);

   destBuf->ReduceSize(destSize - stream.avail_out);

   inflateEnd(&stream);

   if( !retval )
   {
      delete destBuf;
      destBuf = NULL;
   }
   return destBuf;
}

Binary* UpdatePacket::DecodeFile(const IBinary& binaryData, int index)
{
   Binary *dest = NULL;

   const BYTE* bdata = binaryData.Bytes();
   DWORD headSize = *(DWORD*)bdata;
   DWORD len = binaryData.Size() - headSize;
   if( headSize < len - sizeof(DWORD) )
   {
      Binary* data = AESDecode(bdata + headSize, len, uploadKey);
      if( data != NULL )
      {
         const BYTE* head = (*data);
         headSize = *(DWORD*)head;
         const BYTE* body = head + headSize;
//         const BYTE* ep = body;

         head += sizeof(DWORD);
         WORD count = *(WORD*)head;
         head += sizeof(WORD);
         len -= headSize;

         for( int i=0; i<count; i++ )
         {
            if( head > body )
               break;

            DWORD fileSize = *(DWORD*)head;
            head += sizeof(DWORD);

            if( i != index )
            {
               body += fileSize;
               len -= fileSize;

               size_t nameLen = strlen((const char*)head) + 1;
               head += nameLen;

               continue;
            }

            dest = Decompress(body, fileSize, len);
            break;
         }

         delete data;
      }
   }

   return dest;
}

bool UpdatePacket::DecodeBody(const IBinary& binaryData, const std::string& fileFolder)
{
#ifdef UNIX
   return false;
#else
   const BYTE* bdata = binaryData.Bytes();
   DWORD headSize = *(DWORD*)bdata;
   DWORD len = binaryData.Size() - headSize;
   if( headSize >= len - sizeof(DWORD) ) return false;

   Binary* data = AESDecode(bdata + headSize, len, uploadKey);
   if( data == NULL ) return false;

   const BYTE* head = (*data);
   headSize = *(DWORD*)head;
   const BYTE* body = head + headSize;
//   const BYTE* ep = body;

   head += sizeof(DWORD);
   WORD count = *(WORD*)head;
   head += sizeof(WORD);
   len -= headSize;

   files.folder = fileFolder;

   bool retVal = true;
   while( count-- > 0 )
   {
      if( head > body )
      {
         retVal = false;
         break;
      }

      DWORD fileSize = *(DWORD*)head;
      head += sizeof(DWORD);

      if( DecompressFile(fileFolder, (const char*)head, body, len) == NULL )
      {
         retVal = false;
         break;
      }
      body += fileSize;

      UpdateFileData fd;
      fd.name = (const char*)head;
      files.push_back(fd);

      size_t nameLen = strlen((const char*)head) + 1;
      head += nameLen;

      len -= fileSize;
   }

   delete data;
   return retVal;
#endif
}

bool UpdatePacket::HaveFile(const std::string& name)
{
#ifdef UNIX
   return false;
#else
   for( int i = (int)(files.size()-1); i>=0; i-- )
   {
      if( files[i].name.compare(name) == 0 )
         return true;
   }
   return false;
#endif
}

bool UpdatePacket::MoveFile(const std::string& name, const std::string& destFolder)
{
#ifdef UNIX
   return false;
#else
   FileList::iterator i = files.begin();
   for( ; i != files.end(); i++ )
   {
      if( (*i).name.compare(name) == 0 )
         break;
   }

   if( i == files.end() ) return false;

   std::string destName(destFolder);
   if( *destName.rbegin() != '\\' ) destName += "\\";
   destName += name;

   std::string srcName(files.folder.c_str());
   if( *srcName.rbegin() != '\\' ) srcName += "\\";
   srcName += name;

   if( CopyFileA(srcName.c_str(), destName.c_str(), FALSE) != FALSE )
   {
      files.erase(i);
      DeleteFileA(srcName.c_str());

      return true;
   }

   return false;
#endif
}

bool UpdatePacket::Save(FILE* file) const
{
#ifdef UNIX
   return false;
#else
   WriteString(file, category);
   WriteString(file, version);
   bool res = files.Save(file);
   fclose(file);

   return res;
#endif
}

bool UpdatePacket::Load(FILE* file)
{
#ifdef UNIX
   return false;
#else
   bool err = !ReadString(file, &category);
   err = (err || !ReadString(file, &version));
   err = (err || !files.Load(file));
   fclose(file);

   return !err;
#endif
}

#ifdef UNIX
#else
// if false all changes rollbacked
struct FileMoveData
{
   std::string oldName;
   std::string newName;

   FileMoveData(const std::string& o, const std::string& n) : oldName(o), newName(n) {}
};

class RenamedList : public std::vector<FileMoveData>
{
public:
   RenamedList() {}

   void DeleteFiles();
   void Rollback();
};

void RenamedList::DeleteFiles()
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      DeleteFileA(i->newName.c_str());
   }
}

void RenamedList::Rollback()
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      MoveFileExA(i->newName.c_str(), i->oldName.c_str(), MOVEFILE_COPY_ALLOWED);
   }
}
#endif

bool UpdatePacket::MoveFiles(const std::string& destFolder, std::string *failedFile)
{
#ifdef UNIX
   return false;
#else
   FILETIME ft;
   SYSTEMTIME st;
   RenamedList prevFiles, updatedFiles;
   char tempExt[50];

   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &ft);
   wsprintfA(tempExt, ".tmp.%X", ft.dwLowDateTime % 0xFFFF);

   bool retVal = true;
   FileList::iterator i = files.begin();
   for( ; i != files.end(); i++ )
   {
      std::string fileName(destFolder);
      if( *fileName.rbegin() != '\\' ) fileName += "\\";
      fileName += i->name;

      if( IsFileExists(fileName) )
      {
         std::string tempFile(fileName + tempExt);
         if( MoveFileExA(fileName.c_str(), tempFile.c_str(), MOVEFILE_REPLACE_EXISTING) == FALSE )
         {
            if( failedFile != NULL ) failedFile->assign(fileName);
            retVal = false;
            break;
         }
         prevFiles.push_back(FileMoveData(fileName, tempFile));
      }

      std::string srcFile(files.folder);
      if( *srcFile.rbegin() != '\\' ) srcFile += "\\";
      srcFile += i->name;

      if( MoveFileExA(srcFile.c_str(), fileName.c_str(), MOVEFILE_COPY_ALLOWED) == FALSE )
      {
         if( failedFile != NULL ) failedFile->assign(srcFile);
         retVal = false;
         break;
      }
      updatedFiles.push_back(FileMoveData(srcFile, fileName));
   }

   if( retVal ) // commit
   {
      prevFiles.DeleteFiles();
   } else // rollback
   {
      updatedFiles.Rollback();
      prevFiles.Rollback();
   }
   return retVal;
#endif
}

/*
bool UpdatePacket::Read(const IBinary& binaryData, const Key key)
{
   const BYTE* p = binaryData.Bytes();

   DWORD headSize = *(DWORD*)p;
   DWORD len = binaryData.Size() - headSize;

   p += sizeof(DWORD);
   headSize -= sizeof(DWORD);
   Binary* header = Decode(p, headSize, key);

   if( header == NULL ) return false;

   const BYTE* hp = (*header);
   category = (const char *)hp;
   version = ((const char*)hp + category.size() + 1);

   delete header;

   data = Decode(p + headSize, len, key);
   return (data != NULL);
}

Binary* UpdatePacket::Encode(const Key key)
{
   if( data == NULL ) return NULL;

   Binary src;
   DWORD headSize = category.size() + version.size() + 2;

   BYTE *p = src.Alloc(headSize);
   strcpy((char*)p, category.c_str());
   p += (category.size() + 1);
   strcpy((char*)p, version.c_str());

   Binary* head = ::Encode(src, key);
   Binary* edata = ::Encode(*data, key);
   src.Clear();

   headSize = head->Size() + sizeof(headSize);
   DWORD size = headSize + edata->Size();

   Binary *out = new Binary();
   p = out->Alloc(size);
   *(DWORD*)p = headSize;
   p += sizeof(DWORD);
   memcpy(p, (const BYTE*)(*head), head->Size());

   p += head->Size();
   memcpy(p, (const BYTE*)(*edata), edata->Size());

   delete head;
   delete edata;
   return out;
}
*/

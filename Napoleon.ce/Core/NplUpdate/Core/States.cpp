/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* NplUpdate
*
*  ert   09/11/2009   creating
*/
#include "stdafx.h"
#include "States.h"
#include "Util.h"
#include <ServerDefs.h>
#include <DataReader.h>

const DWORD PacketSize = 20 * 1024;

const wchar_t UpdateExt[] = L".upd";

const wchar_t* Update::StateName = L"update";
//
//-------------------------------- states ------------------------------------
//
CheckUpdate checkUpdate;
Update updateState;
ErrorState errorState;

//
//-------------------------------- CheckUpdate ------------------------------------
//
State* CheckUpdate::Execute()
{
   const ProgConfig& config = app.Config();
   bool ok = false;
   
   if( !config.category.empty() )
   {
      std::wstring tvalue = config.category + L"/" + config.version;
      ReceivedStream* stream = Receive(CHECK_UPDATE, tvalue.c_str(), config);
      if( stream != NULL )
      {
         stream->PrepareRead();
         ok = CheckAnswer(stream, &tvalue);
         delete stream;
      }
   }

   if( ok )
   {
      std::wstring text(L"<html><body>Для обновления доступна новая версия<p>Загрузить обновление?</body></html>");

      if( app.Alert(text.c_str(), IDC_UPDATE) )
         return &updateState;
   }
   return NULL;
}

//
//-------------------------------- Update ------------------------------------
//
void Update::Clear()
{
   size = 0;
   app.ResetVersion();
}

State* Update::Execute()
{
   const ProgConfig& config = app.Config();
   app.RemoveListner();

   if( config.category.empty() )
      return NULL;

   const wchar_t *cmd = NULL;
   wchar_t buf[50];

   std::wstring tvalue(config.category + L"/" + config.version);
   if( size == 0 )
   {
      cmd = GET_UPDATE;

      wsprintf(buf, L"/%d", PacketSize);
      tvalue += buf;
      std::wstring fileName;

      GetIntStateFile(&fileName, StateName);
      config.Save(fileName.c_str());
   } else
   {
      DWORD curSize = GetUpdateSize();
      if( curSize < size )
      {
         cmd = GET_UPD_PACKET;
         wsprintf(buf, L"/%d/%d", curSize, PacketSize);
         tvalue += buf;
      } else
      {
         std::wstring fileName;
         GetUpdateFileName(&fileName);
         decodeState.SetFileName(fileName);

         return &decodeState;
      }
   }

   app.AddNetListner();
   return Receive(cmd, tvalue.c_str());
}

State* Update::Receive(const wchar_t* cmd, const wchar_t* cParam)
{
   ReceivedStream* stream = ::Receive(cmd, cParam, app.Config());
   State* retState = NULL;
   if( stream )
   {
      std::wstring tvalue;

      stream->PrepareRead();
      if( CheckAnswer(stream, &tvalue) )
      {
         if( size == 0 )
         {
            int pos = tvalue.find(L'/');
            if( pos > 0 )
            {
               const wchar_t* p = tvalue.c_str();
               app.SetVersion(p, pos);
               size = _wtoi(p + pos + 1);
            }

            // если такой файл был - удаляем его
            std::wstring fileName;
            GetUpdateFileName(&fileName);
            DeleteFile(fileName.c_str());
         }

         WriteUpdate(stream);
         retState = &updateState;
      }

      delete stream;
   }
   return retState;
}

void Update::GetUpdateFileName(std::wstring *fileName)
{
   const ProgConfig& config = app.Config();
   app.GetUpdateFolder(fileName);

   fileName->append(config.category);
   fileName->append(config.version);
   fileName->append(UpdateExt);
}

DWORD Update::GetUpdateSize()
{
   std::wstring fileName;
   GetUpdateFileName(&fileName);

   FILE* file = _wfopen(fileName.c_str(), L"rb");
   if( file == NULL )
      return 0;

   fseek(file, 0, SEEK_END);
   DWORD curSize = ftell(file);
   fclose(file);

   return curSize;
}

class UpdWriter : public IBinaryWriter
{
public:
   UpdWriter(const wchar_t *_fileName) : fileName(_fileName) {}
   ~UpdWriter() {}

   virtual bool Write(IReflectableData* data, ReceivedStream* stream, DWORD size)
   {
      if( size == 0 ) return true;

      Binary b;
      BYTE *p = b.Alloc(size);
      while( (int)size > 0 )
      {
         wchar_t sym = stream->Get();
         if( size == 1 ) *p = (char)sym;
         else
         {
            *(wchar_t*)p = sym;
            p += sizeof(wchar_t);
         }

         size -= sizeof(wchar_t);
      }

      bool retVal = false;
      FILE *f = _wfopen(fileName.c_str(), L"a+b");
      if( f )
      {
         retVal = true;
         fwrite((const BYTE*)b, sizeof(BYTE), b.Size(), f);
         fclose(f);
      }
      return retVal;
   }

protected:
   std::wstring fileName;
};

static const wchar_t* UpdFileName;
static IBinaryWriter* GetUpdWriter(const wchar_t* fieldName)
{
   return new UpdWriter(UpdFileName);
}

bool Update::WriteUpdate(ReceivedStream* stream)
{
   bool res = false;
   ServerCommand cmd;
   std::wstring tval, fileName;

   GetUpdateFileName(&fileName);

   UpdFileName = fileName.c_str();
   if( stream->CopyUntill(&tval, L'[') )
   {
      DataReader* reader = DataReader::CreateReader(cmd.GetType(), stream, GetUpdWriter);
      res = (reader != NULL && reader->Read(&cmd, stream));
      delete reader;
   }

   return res;
}

State* ErrorState::Execute()
{
   if( app.Alert(message.c_str(), IDC_ERROR) )
   {
      std::wstring fileName;
      GetIntStateFile(&fileName, Update::StateName);
      app.Do(fileName.c_str());
   }
   return NULL;
}

void ErrorState::SetText(const wchar_t* str)
{
   message = str;
}

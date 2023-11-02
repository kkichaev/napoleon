/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Decompress impl
 * 
 *  ert   25/09/2007   creating
 */ 
#include "stdafx.h"
#include <Module.h>

#include <StringHolder.h>
#include <Table.h>
#include "Network.h"

#include <Compress.h>

#include <zlib.h>
#include <set>

class WRTable : public CETable
{
public:
   WRTable(const CEDBFormat &format) : CETable(format) {}

   // запись без первичного ключа
   CEOID WriteFields(const IReflectableData& data, int keyIndex, CEOID oid, const std::vector<int> &fields)
   {
      int count = fields.size();
      CEPROPVAL *value = (CEPROPVAL*)calloc(sizeof(CEPROPVAL), (count + 1));
      AllocList allocated;

      int cr = 0;
      for( int i=0 ;i<count; i++ )
      {
         format.SetProp(value + cr, data, fields[i], &allocated);
         cr++;
      }
      //format.SetProp(value, data, &allocated);

      // добавим флаг, если имеется первичный ключ
      if( format.PrimaryKey() >= 0 )
      {
         value[count].propid = FLAG_PROPID;
         value[count].val.uiVal = DIRTY_FLAG;

         count++;
      }
      oid = CeWriteRecordProps(handle, oid, count, value);
      LocalFree(value);

      return oid;
   }
};

static bool Finded(const wchar_t *src, const wchar_t **buf, int count)
{
   for( int i=0; i<count; i++ )
      if( !wcscmp(src, buf[i]) ) return true;
   return false;
}

bool ProcessingReceived(const SyncFormat &sf, const char *fn, const wchar_t *message, 
                        ReceiveParam *param, const wchar_t **excludedFields, int excludedCount, PrcAfterWrite aw)
{
   if( param->pi ) param->pi->SetText(message);
   CEDBFormat format(sf.TypeName(), sf.KeyField());
   WRTable table(format);
   const DataReflector &reflector = GetTypeReflector(sf.TypeName());
   int index = reflector.Find(sf.KeyField());
   bool tableCreated = false;

   param->ec = 0;
   if( table.Open(sf.FileName(), format.CEType(sf.KeyField())) == false )
   {
      if( table.Create(sf.FileName()) )
         table.SetTag(sf.KeyField(), true);
      else
      {
         param->ec = 1;
         return false;
      }
      tableCreated = true;
   }

   std::string fileName, destName;
   _Module.MakeFileName(&fileName, fn);   
   _Module.MakeFileName(&destName, TMP_DECOMPRESS);


   FILE *dest = Decompress(fileName.c_str(), destName.c_str());
   if( dest == NULL )
   {
      param->ec = 1;
      return false;
   }

   fseek(dest, 0, SEEK_SET);
   FileReader reader(dest);
   IReflectableData *data = reflector.Create();

   int size = 0;
   if( param->pi )
   {
      fseek(dest, 0, SEEK_END);
      size = ftell(dest);
      fseek(dest, 0, SEEK_SET);

      param->pi->SetMax(size);
   }

   std::vector<int> fields;
   CEPROPVAL key;
   AllocList allocated;

   for( int i=reflector.Count()-1; i>=0; i-- )
   {
      if( Finded(reflector.Type(i).name, excludedFields, excludedCount) )
         continue;
      fields.push_back(i);
   }

   while( !feof(dest) )
   {
      reader.ClearBuffers();
      if( sf.Deserialize(data, reader) == false )
         break;
      if( param->pi )
         param->pi->SetPos(ftell(dest));

      CEOID oid = NULL;
      if( tableCreated == false && index >= 0 )
      {
         format.SetProp(&key, *data, index, &allocated);     
         oid = table.Seek(key);

         if( oid )
            table.WriteFields(*data, index, oid, fields);
         else
         {
            table.Add(*data, -1);
            oid = table.LastOID();
         }
      } else
      {
         table.Add(*data, -1);
         oid = table.LastOID();
      }
      if( aw != NULL )
         aw(*data, oid);
   }
/*
   DWORD ct = GetTickCount(), ct1;
   DWORD tms[5] = { 0 };
   while( !feof(dest) )
   {
      reader.ClearBuffers();
      if( sf.Deserialize(data, reader) == false )
         break;

      ct1 = GetTickCount();
      tms[0] += ct1 - ct;
      ct = ct1;

      if( param->pi )
         param->pi->SetPos(ftell(dest));

      ct1 = GetTickCount();
      tms[1] += ct1 - ct;
      ct = ct1;

     if( tableCreated == false )
      {
         format.SetProp(&key, *data, index, &allocated);     
         CEOID oid = table.Seek(key);

         ct1 = GetTickCount();
         tms[2] += ct1 - ct;
         ct = ct1;

         if( oid )
            table.WriteFields(*data, index, oid, fields);
         else
            table.Add(*data, -1);

         ct1 = GetTickCount();
         tms[3] += ct1 - ct;
         ct = ct1;

      } else
         table.Add(*data, -1);
   }
*/

   if( param->pi ) param->pi->SetPos(ftell(dest));

   delete data;
   fclose(dest);
   if( param->ec == 0 )
   {
      _Module.DeleteFile(destName.c_str());
#if defined(PCMagazine) || defined(DEBUG)
#else
      _Module.DeleteFile(fileName.c_str());
#endif
   }
   return (param->ec) ? false : true;
}

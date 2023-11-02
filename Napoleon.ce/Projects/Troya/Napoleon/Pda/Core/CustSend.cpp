/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Custom Send - передача остатков
 *
 *  ert   06/04/2008   creating
 */
#include "stdafx.h"
#include "CustSend.h"
#include <CEInt.h>
#include <Sync.h>
#include <Table.h>
#include <OrgRmnts.h>

#define TMP_RMNTS_FILE       ".\\NplTmpRmnts"

std::vector<CEOID> remoids;

static bool StreamingRemnant(SendStream *stream, const OrgRemnants &remnants)
{
   if( (remnants.flags & orfDirty) == 0 ) return false;

   if( stream->stream == NULL )
   {
      std::string fn;
      _Module.MakeFileName(&fn, TMP_RMNTS_FILE);
      stream->stream = fopen(fn.c_str(), "w+b");
      if( stream->stream == NULL ) return false;
   }

   SyncOrgRemnants so;
   FileWriter wr;
   wr.Attach(stream->stream);
   so.Serialize(&wr, remnants);
   stream->count++;
   return true;
}

static bool MakeRemnantStream(CEOID oid, std::vector<SendStream> *stream)
{
   SendStream ss;
   if( oid == 0 )
   {
      SyncOrgRemnants so;
      CEDBFormat format(so);
      CETable table(format);

      if( table.Open(so.FileName()) == false ) return true;
      CEOID rid = table.SetPos(0);

      while( rid != NULL )
      {
         OrgRemnants ormnt;
         table.GetCurrent(&ormnt);
         if( StreamingRemnant(&ss, ormnt) )
            remoids.push_back(rid);

         rid = table.MoveNext(true);
      }
   } else
   {
      OrgRemnantsImpl rmnts(oid);
      if( rmnts.Read() && (rmnts.flags & orfDirty) )
      {
         if( StreamingRemnant(&ss, rmnts) )
            remoids.push_back(rmnts.rmid);
      }
   }

   if( ss.stream != NULL )
   {
      //fseek(ss.stream, SEEK_END, 0);
      ss.size = ftell(ss.stream);
      ss.text = L"Передача остатков";
      ss.cmd = SND_ORG_RMNTS;

      stream->push_back(ss);
   }
   return true;
}

bool CustomSendPrepare(const std::vector<CEOID> &recs, std::vector<SendStream> *stream, bool multiOrgs)
{
   CEOID orgID = 0;
   if( !multiOrgs )
   {
      SyncOrder sord;
      CEDBFormat of(sord);
      CETable otable(of);
      Order order;

      otable.Open(sord.FileName());
      otable.Seek(recs.front());
      otable.GetCurrent(&order);

      orgID = order.id;
   }
   return MakeRemnantStream(orgID, stream);
}

void CustomSendCleanUp(std::vector<SendStream> *stream)
{
   _Module.DeleteFile(TMP_RMNTS_FILE);

   bool fail = true;
   std::vector<SendStream>::const_iterator i = stream->begin();
   while( i != stream->end() )
   {
      if( !strcmp(i->cmd, SND_ORG_RMNTS) )
      {
         fail = !i->sended;
         break;
      }
      i++;
   }
   if( !fail )
   {
      SyncOrgRemnants so;
      CEDBFormat format(so);
      CETable table(format);

      if( !table.Open(so.FileName()) ) return;

      std::vector<CEOID>::iterator i = remoids.begin();
      for( ; i != remoids.end(); i++ )
      {
         OrgRemnants rmnts;
         table.Seek((*i));
         table.GetCurrent(&rmnts);
         rmnts.flags &= (~orfDirty);

         table.WriteRecord(rmnts, (*i));
      }
   }

   remoids.clear();
}

/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Session object.
 *
 * ert   21/08/2010   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "objdef.h"
#include "objects.h"
#include "session.h"

#include <stdobjs.h>

#include <srvutility.h>

#include <atlconv.h>

using namespace GRServer;

DWORD SessionObject::SendLimitDefault = 0;

//#include <server.h>
//static void Log(const ObjectDef* od, const char* action)
//{
//   if( od == NULL )
//      return;
//
//   if( od->name.compare(L"Agents") == 0 )
//      gServer->AddLog("Agents table %s", action);
//}

//
//------------------------------------------ SessionObject ----------------------------------------------------
//
SessionObject::SessionObject(const ObjectDef* od, Session *_session) :
session(_session), parent(NULL), currentIndex(-1), flags(0), fireEvent(false), needDebug(false)
{
   InitObject(od);
   curObjIndex = 0;
}

SessionObject::SessionObject(Session* _session) :
objDef(NULL), session(_session), parent(NULL), currentIndex(-1), flags(0), fireEvent(false), needDebug(false)
{
   curObjIndex = 0;
}

void GRServer::SetFileFieldBaseFolder(std::string* dest, const IObjectData::Field& src, const IServerConfig& config)
{
	const char* imgFolder = config.ImageFolder();
	if (IsLocalName(imgFolder))
		dest->assign(config.ExchangeFolder()).append(imgFolder);
	else
		dest->assign(imgFolder);

   //dest->assign(exchangeFolder);
   //if( !src.baseFolder.empty() )
   //{
   //   USES_CONVERSION;
   //   const char* bf = W2A(src.baseFolder.c_str());
   //   if( IsLocalName(bf) )
   //      dest->append(bf);
   //   else
   //      dest->assign(bf);
   //}
}

void SessionObject::InitObject(const ObjectDef* od)
{
   objDef = od;
	needDebug = od->needDebug;

   bool addChild = false;
   FormatList *fmtList = session->GetFormatList();
   format = fmtList->GetFormat(od->name);
   if( format == NULL )
   {
      format = fmtList->NewFormat(od->name);
      addChild = true;
      fmtList->AddFormat(format, false);
   }

   ObjectDef::Fields::const_iterator i = od->fields.begin();
   for( ; i != od->fields.end(); i++ )
   {
      const MemberFormat& mf = i->format;
      if( mf.type == MemberFormat::mtObject )
      {
         const ObjectDef* ch = session->GetObjDef(i->data);
         if( ch != NULL )
         {
            Child child;
            child.fieldName = mf.name;
            child.object = new SessionObject(ch, session);
            child.object->parent = this;

            childs.push_back(child);
         }
		}

      if( addChild )
         format->push_back(mf);
   }
	
	i = od->fields.begin();
   for( ; i != od->fields.end(); i++ )
   {
      const MemberFormat& mf = i->format;
		if( mf.type == MemberFormat::mtBinary && (i->flags & ObjectDef::Field::File) != 0 && !i->src.empty() )
      {
			int srcidx = format->FindMember(i->src.c_str());
         if( srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString )
         {
				int fldIndex = format->FindMember(i->format.name.c_str());
				//if (fldIndex < 0 && (i->flags & ObjectDef::Field::CanAddFieldToFormat) != 0)
				//{
				//	format->push_back(mf);
				//	fldIndex = format->FindMember(i->format.name.c_str());
				//}
				if( fldIndex >= 0 )
				{
					std::string folder;
					SetFileFieldBaseFolder(&folder, *i, gServer->GetConfig());
					lazyWriters.push_back(new FileField(srcidx, fldIndex, folder.c_str(), gServer));
				}
			}
      }
	}

}

SessionObject::SessionObject(const std::wstring &name, Session *_session) :
   objDef(NULL), session(_session), parent(NULL), currentIndex(-1), flags(0)
{
   FormatList *fmtList = session->GetFormatList();
   Format *f = fmtList->GetFormat(name);
   if( f == NULL )
   {
      f = fmtList->NewFormat(name);
      fmtList->AddFormat(f, false);
   } else
   {
      Format::const_iterator fi = f->begin();
      for( ; fi != f->end(); fi++ )
      {
         if( fi->type == MemberFormat::mtObject )
         {
            std::wstring fmtName(name);
            fmtName += L"$";
            fmtName += fi->name;

            Child child;
            child.fieldName = fi->name;
            child.object = new SessionObject(fmtName, session);
            child.object->parent = this;

            childs.push_back(child);
         }
      }
   }

   format = f;
}

SessionObject::~SessionObject()
{
	std::vector<FileField*>::iterator i = lazyWriters.begin();
	for( ; i != lazyWriters.end(); i++ )
		delete (*i);
}

bool SessionObject::Reading(const wchar_t* filter, bool createReader, bool resolveFiles)
{
   bool res = false;
	DWORD start = GetTickCount();
	size_t cb = session->CurrentMemory();
	if( (!createReader && source.reader != NULL) || CreateReader(filter) )
   {
      res = Load(NULL, resolveFiles);
      CloseReader();
	
		DWORD finish = GetTickCount();

		USES_CONVERSION;
		gServer->AddLog(IErrorLogger::Full, "reading (%d) time = %u, cb =%u, recs = %u (%s:%s)", 
			session->GetSocket(), finish - start, (session->CurrentMemory() - cb), size(), 
			W2A(format->name.c_str()),
			filter == NULL? "" : W2A(filter));
	}

   return res;
}

bool SessionObject::Writing(RID_LIST *ids)
{
   bool res = false;
   if( CreateWriter(NULL) )
   {
      res = Write(true, ids);
      CloseWriter();
   }

   return res;
}

ISessionObject* SessionObject::GetChild(const std::wstring& fieldName) const
{
   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
   {
      if( i->fieldName.compare(fieldName) == 0 )
         return const_cast<SessionObject*>(i->object);
   }

   return NULL;
}

void ReplaceCurUser(std::wstring* fstr, const wchar_t* filter, const wchar_t* userID)
{
   const wchar_t curUserIDTag[] = L"$CURRENT_USERID";
   const wchar_t* s = filter;
   while( true )
   {
      const wchar_t* p = wcsstr(s, curUserIDTag);
      if( p == NULL )
      {
         fstr->append(s);
         break;
      }
      fstr->append(s, p-s);
      fstr->append(userID);
      s = p + (sizeof(curUserIDTag)/ sizeof(curUserIDTag[0]) - 1);
   }
}


// функция даты вида ToDate('20.04.2010') ToDate('20-04-2010 10:20:15') ToDate('20/04/2010 10:20:15')
static const wchar_t* AddDate(CString* dest, const wchar_t *src)
{
   while( *src != L'(' ) if( *src++ == L'\0' ) return NULL;
   while( *src != L'\'' && *src != L'"' ) if( *src++ == L'\0' ) return NULL;

   SYSTEMTIME st = {0};
   const wchar_t* ep;
   if( !StrToSystemTime(&st, src+1, &ep) )
      return NULL;

   if( *ep != L'\'' && *ep != L'"' ) return NULL;
   while( *ep != ')' && *ep!= L'\0' ) ep++;
   if( *ep == L'\0' ) return NULL;
   
   wchar_t buf[50];
   FILETIME ft;
   SystemTimeToFileTime(&st, &ft);
   wsprintf(buf, L"%d%09d", (int)((*(__int64*)&ft) / 1000000000), (int)((*(__int64*)&ft) % 1000000000));
   dest->append(buf);

   return ep+1;
}

static bool ConvertToDateRemoveQTY(CString* dst, const wchar_t *src)
{
   wchar_t* buf = (wchar_t*)alloca((wcslen(src) + 1) * sizeof(wchar_t));
   wcscpy(buf, src);
   CharUpper(buf);

	wchar_t* tp = wcsstr(buf, L"SETQTYFILTER");
	if (tp != NULL)
	{
		return true;
	}

   bool ret = true;
   const wchar_t *sp = buf, *sp1 = src;
   while( true )
   {
      const wchar_t* p = wcsstr(sp, L"TODATE");
      if( p == NULL )
      {
         dst->append(sp1);
         break;
      }
      dst->append(sp1, (unsigned)(p - sp));
      sp = AddDate(dst, p + sizeof(L"TODATE")/sizeof(wchar_t) - 1);
      if( sp == NULL )
      {
         ret = false;
         break;
      }

      sp1 = src + (sp - buf);
   }

   return ret;
}

bool SessionObject::PrepareFilterStr(CString* dest, const CString& src) const
{
   return ConvertToDateRemoveQTY(dest, src.c_str());
}

bool SessionObject::CreateReader(const wchar_t* filter, bool forceCreate, bool clearObject)
{
	// check only for top objects
	if (parent == NULL)
	{
		const User* user = &session->GetUser();
		if (user != NULL && !user->ObjectAllowed(format->name, User::oaRead))
		{
			USES_CONVERSION;
			const char *userA = W2A(user->UserName());
			const char *objA = W2A(format->name.c_str());

			gServer->AddError(false, "Пользователю '%s' запрещено смотреть объект '%s'", userA, objA);
			return false;
		}
	}

	
	if (!forceCreate && size() > 0 && (filter == NULL || *filter == L'\0')) return true;
   if( objDef == NULL ) return false;

   if( source.reader != NULL )
      delete source.reader;

   if( clearObject && size() != 0 )
      clear();

   //Log(objDef, "open");

	if (!fireEvent)
	{
		fireEvent = true;
		const_cast<ObjectDef*>(objDef)->events.Fire(Event::BeforeRead, session, this);
		fireEvent = false;
	}
	
	SourceType st = stCommon;
	source.reader = DataSource::CreateReader(objDef->GetSource(), *this, (std::wstring*)&source.readerName, &st);

   ChildList::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i).object->CreateReader(L"", forceCreate, clearObject);

   if(source.reader != NULL)
   {
		source.type = st;
      bool res = true;
      if( filter != NULL && *filter != L'\0' )
      {
         std::wstring fstr;

         if( session->IsUserAssigned() )
            ReplaceCurUser(&fstr, filter, session->GetUser().ID());
         else
            fstr = filter;

         res = source.reader->SetFilter(fstr.c_str(), *this);
      }
      return res;
   }
   return false;
}

bool SessionObject::MoveNext()
{
   int sz = (int)size();
   if( sz > 0 )
   {
      if( currentIndex < 0 ) currentIndex = 0;
      else if( currentIndex < sz ) currentIndex++;
      return (currentIndex < sz);
   }

   return (source.reader != NULL && source.reader->MoveNext(NULL));
}

void SessionObject::LoaderClose()
{
   CloseReader();
}

void SessionObject::LoadObject(Object* o, bool updateExecutable, bool resolveFiles)
{
	int idx = curObjIndex;
	curObjIndex = (int)size();
	push_back(o);

	ChildList::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
   {
      SessionObject* object = (*i).object;
      if( object->Load(o, resolveFiles) )
      {
         Object::Field* f = (Object::Field*)o->GetField(i->fieldName.c_str());
         if( f != NULL )
         {
            ServObject* so = new ServObject(object->format);
            object->MoveValuesTo(so);
            const_cast<Member&>(f->member).object = so;

            delete f;
         }
      }
   }

   if( updateExecutable )
   {
      //int idx = curObjIndex;
      //curObjIndex = (int)size();
      //push_back(o);

      UpdateExecutableFields(o, false);

      //*rbegin() = NULL;
      //pop_back();
      //curObjIndex = idx;
   }

	*rbegin() = NULL;
	pop_back();
	curObjIndex = idx;

	if (resolveFiles)
	{
		PrepareToString(*o);
	}
}


Object* SessionObject::ReadObject(bool updateExecutable, bool resolveFiles)
{
   Object* o = source.reader->Create(*format);
   if( source.reader->Get(o) )
   {
      LoadObject(o, updateExecutable, resolveFiles);
   } else
   {
      delete o;
      o = NULL;
   }

   return o;
}

bool SessionObject::UpdateExecutableFields(Object* curObject, bool put)
{
   bool ret = true;
   if( HaveExecutableFields(put) )
   {
      ret = LoadExecutableFields(*curObject, put, NULL);
      //if( ret )
      bool bret = childs.UpdateExecutableFields(*curObject, put);
      if( ret ) ret = bret;
   }

   return ret;
}

void SessionObject::PrepareToString(const Object& obj) const
{
	std::vector<FileField*>::const_iterator fi = lazyWriters.begin();
	for( ; fi != lazyWriters.end(); fi++ )
	{
		int idx = (*fi)->GetMeIndex();
		Member& m = const_cast<Object&>(obj).at(idx);
		if( m.binary == NULL )
			(*fi)->ReadFile(const_cast<Object*>(&obj));
	}
	const_cast<SessionObject*>(this)->FireEvent(Event::OnLoad, (Object*)&obj);

	childs.UpdateFileFields(obj, true);
}

bool SessionObject::FireEvent(IEvent::Type eventType, Object* o)
{
	bool ret = true;
	if (objDef != NULL && objDef->events.HaveEvent(eventType))
	{
		if (o != NULL)
		{
			curObjIndex = 0;
			insert(begin(), o);
		}

		fireEvent = true;

		std::vector<IDataSource::IWriter*> writers;
		SourceType sv = source.type;
		SaveWriter(&writers);

		ret = const_cast<ObjectDef*>(objDef)->events.Fire(eventType, session, this);

		RestoreWriter(writers.begin());
		source.type = sv;

		fireEvent = false;

		if (o != NULL)
		{
			iterator i = begin();
			(*i) = NULL;
			erase(i);
		}
	}
	return ret;
}

void SessionObject::AfterToString(const Object& obj) const
{
	std::vector<FileField*>::const_iterator fi = lazyWriters.begin();
	for( ; fi != lazyWriters.end(); fi++ )
	{
		int idx = (*fi)->GetMeIndex();
		Member& m = const_cast<Object&>(obj).at(idx);
		if( m.binary != NULL )
		{
			delete m.binary;
			m.binary = NULL;
		}
	}
	childs.UpdateFileFields(obj, false);
}


DWORD SessionObject::SendLimit() const
{
   return (objDef != NULL) ? objDef->sendLimit : SendLimitDefault;
}

bool SessionObject::WriteTo(OutStream *os)
{
   int sz = (int)size();
   if( sz > 0 )
   {
      if( currentIndex < 0 ) currentIndex = 0;

      if( currentIndex < sz )
      {
			Object* o = at(currentIndex);

			PrepareToString(*o);
         o->ToString(os, session->GetFormatList());
			AfterToString(*o);

         if( objDef )
         {
				curObjIndex = currentIndex;
				FireEvent(Event::Get, NULL);
         }
      }
      return (currentIndex < sz);
   }

	Object* o = ReadObject(true);
   if( o == NULL ) return false;

	if (os != NULL)
	{
		PrepareToString(*o);
		o->ToString(os, session->GetFormatList());
		AfterToString(*o);
	}

	FireEvent(Event::Get, o);

   delete o;

   return true;
}

bool SessionObject::Load(Object* parentObject, bool resolveFiles)
{
   currentIndex = -1;
   if( source.reader == NULL )
      return (size() > 0);

	bool ret = true;
   while( source.reader->MoveNext(parentObject) )
   {
      Object* o = ReadObject((parentObject == NULL), resolveFiles);
      if( o == NULL ) break;
		if (session->CanAddObject(*o) == false)
		{
			delete o;
			ret = false;
			break;
		}
      push_back(o);
	}
   return ret;
}

void SessionObject::RefreshExecutableData(bool put)
{
	if (HaveExecutableFields(put))
   {
      curObjIndex = 0;
      iterator i = begin();
      for( ; i != end(); i++, curObjIndex++ )
      {
         Object& o = *(*i);
         LoadExecutableFields(o, put, NULL);
         childs.UpdateExecutableFields(o, put);
      }
   }
}

void SessionObject::CloseReader()
{
   //Log(objDef, "close");

	if (objDef && !fireEvent)
	{
		fireEvent = true;
		const_cast<ObjectDef*>(objDef)->events.Fire(Event::ReadCommit, session, this);
		fireEvent = false;
	}

	if ((objDef->flags & IObjectDef::RemoveOnCommit) == 0 && source.reader != NULL)
   {
      source.reader->Close();
      delete source.reader;
      source.reader = NULL;
   }

   ChildList::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i).object->CloseReader();
}

void SessionObject::RemoveSource()
{
   if( source.reader != NULL )
   {
      source.reader->Remove();
      delete source.reader;
      source.reader = NULL;
   }
}

IDataSource::IWriter* SessionObject::CreateWriter(IDataSource::IWriter* parentWriter, SourceType srcType, std::wstring *sourceName)
{
   if( objDef == NULL ) return NULL;

   if( source.writer != NULL ) delete source.writer;

   source.writer = DataSource::CreateWriter(parentWriter, objDef->GetSource(), *this, (std::wstring*)&source.writerName, &srcType);
   if( source.writer != NULL )
   {
      if( sourceName != NULL )
         *sourceName = (const std::wstring&)source.writerName;
      source.type = srcType;

      ChildList::iterator i = childs.begin();
      for( ; i != childs.end(); i++ )
      {
         if( format->FindMember(i->fieldName.c_str()) >= 0 )
         {
            std::wstring name;
            IDataSource::IWriter* childWriter = (*i).object->CreateWriter(source.writer, srcType, &name);
            if( childWriter != NULL )
            {
               source.writer->AddChild(childWriter, name);
            }
         }
      }

      if( source.writer->Prepare(*this) == false )
      {
         delete source.writer;
         source.writer = NULL;
      }
   }
   return source.writer;
}

void SessionObject::ChildList::UpdateFileFields(const Object &parentObject, bool prepare) const
{
	const_iterator i = begin();
	for (; i != end(); i++)
	{
		Object::Field* f = (Object::Field*)parentObject.GetField(i->fieldName.c_str());
		if (f != NULL)
		{
			ServObject* so = const_cast<Member&>(f->member).object;
			if (so != NULL)
			{
				ServObject::iterator oi = so->begin();
				for (; oi != so->end(); oi++)
				{
					if (prepare)
						i->object->PrepareToString(*(*oi));
					else
						i->object->AfterToString(*(*oi));
				}
			}
			delete f;
		}
	}
}

bool SessionObject::ChildList::UpdateExecutableFields(Object &parentObject, bool put)
{
   bool ret = true;

   iterator i = begin();
   for( ; ret && i != end(); i++ )
   {
      Object::Field* f = (Object::Field*)parentObject.GetField(i->fieldName.c_str());
      if( f != NULL )
      {
         ret = i->object->UpdateExecutableFields(parentObject, *f, put);
         delete f;
      }
      else
      {
         // может быть вариант что поле в паренте нету (see FocusedFolders)
         continue;
         //ret = false;
      }
   }

   return ret;
}

bool SessionObject::UpdateExecutableFields(Object& parentObject, Object::Field& field, bool put)
{
	if (objDef == NULL || objDef->HaveExecutableFields(put ? MemberFormat::ExecOnPut : MemberFormat::ExecOnGet) == false)
      return true;

   bool ret = true;
   ServObject* so = const_cast<Member&>(field.member).object;
   if( so != NULL )
   {
      ServObject::iterator i;
      bool needRemove = false;
      if( size() == 0 )
      {
         needRemove = true;
         i = so->begin();
         for( ; i != so->end(); i++ )
            push_back(*i);
      }

      //std::vector<int> fields;
      //Object *firstObject;

      curObjIndex = 0;
      i = begin();
      for( ; ret && i != end(); i++, curObjIndex++ )
      {
         ret = LoadExecutableFields(*(*i), put, NULL);
			childs.UpdateExecutableFields(*(*i), put);

         //if( i == so->begin() )
         //{
         //   firstObject = (*i);
         //   ret = LoadExecutableFields(*firstObject, put, &fields);
         //} else
         //{
         //   ret = (*i)->CopyFrom(*firstObject, fields);
         //}
      }

      if( needRemove )
      {
         i = begin();
         int idx = 0;
         for( ; i != end(); i++, idx++ )
            so->at(idx) = (*i);

         clear();
      }
   }

   return ret;
}

static void GetPassList(std::vector<int>* pass, const ObjectDef* objDef, DWORD eflag)
{
   std::set<int> used;

   used.insert(1);

   ObjectDef::Fields::const_iterator i = objDef->fields.begin();
   for( ; i != objDef->fields.end(); i++ )
   {
      if( (i->format.flags & eflag) == 0 ||  used.find(i->pass) != used.end() )
         continue;
      used.insert(i->pass);
   }

   // ordering
   std::set<int>::const_iterator ui = used.begin();
   for( ; ui != used.end(); ui++ )
      pass->push_back(*ui);
}

bool SessionObject::LoadExecutableFields(Object& obj, bool put, std::vector<int> *fieldIndexes)
{
	if (objDef == NULL || objDef->HaveExecutableFields(put ? MemberFormat::ExecOnPut : MemberFormat::ExecOnGet) == false)
      return true;

   bool res = true;
   std::vector<int> pass;
   DWORD eflag = (put) ? MemberFormat::ExecOnPut : MemberFormat::ExecOnGet;
   GetPassList(&pass, objDef, eflag);
   std::vector<int>::const_iterator pi = pass.begin();
   for( ; pi != pass.end(); pi++ )
   {
      ObjectDef::Fields::const_iterator i = objDef->fields.begin();
      for( ; i != objDef->fields.end(); i++ )
      {
         if( i->pass != (*pi) )
            continue;
         if( (i->format.flags & eflag) != 0 )
         {
            int index = format->FindMember(i->format.name.c_str());
            if( index >= 0 )
            {
               Token val;
               MemberData md;
               md.format = &format->at(index);
               md.member = &obj.at(index);

               bool needRemove = false;
               if( size() == 0 )
               {
                  curObjIndex = 0;
                  needRemove = true;
                  push_back(&obj);
               }

               if( session->Parse(&val, i->execStmt, this) )
               {
                  bool tres = val.CopyTo(&md);

                  if( tres && fieldIndexes != NULL )
                     fieldIndexes->push_back(index);
                  if( res ) res = tres;
               } else
               {
                  res = false;
               }

               if( needRemove )
               {
                  *rbegin() = NULL;
                  pop_back();
               }
            }
         }
      }
   }

   return res;
}

bool SessionObject::HaveExecutableFields(bool put) const
{
	if (objDef != NULL && objDef->HaveExecutableFields(put ? MemberFormat::ExecOnPut : MemberFormat::ExecOnGet))
      return true;

   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
   {
      if( i->object->HaveExecutableFields(put) )
         return true;
   }

   return false;
}

void SessionObject::SaveWriter(std::vector<IDataSource::IWriter*> *writers)
{
   writers->push_back(source.writer);
   source.writer = NULL;

   ChildList::iterator i = childs.begin();
   for( ; i!= childs.end(); i++ )
      (*i).object->SaveWriter(writers);
}

void SessionObject::RestoreWriter(std::vector<IDataSource::IWriter*>::iterator current)
{
	delete source.writer;
   source.writer = (*current);

   ChildList::iterator i = childs.begin();
   for( ; i!= childs.end(); i++ )
   {
      current++;
      (*i).object->RestoreWriter(current);
   }
}

bool SessionObject::Write(bool updateExecutable, RID_LIST *ids)
{
   bool res = true;
   if( source.writer != NULL )
   {
		if (needDebug)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "Start write object (%X): %s, %s", (size_t)this, W2A(this->format->name.c_str()), ((fireEvent) ? "fireEvent" : "no fireEvent"));
		}

      bool clearIDS = true;
      curObjIndex = 0;
      iterator i = begin();
      for( ; i != end(); i++, curObjIndex++ )
      {
         Object* o = (*i);
         if( updateExecutable )
            UpdateExecutableFields(o, true);

         RowID rid = NO_ROWID;
         if( !Write(*o, &rid) )
            res = false;
         if( ids != NULL ) ids->push_back(rid);
         if( rid != NO_ROWID ) clearIDS = false;
      
		}
      if( ids != NULL && clearIDS )
         ids->clear();

		if (needDebug)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "Done write object (%X): %s", (size_t)this, ((fireEvent) ? "fireEvent" : "no fireEvent"));
		}

		if (updateExecutable && !FireEvent(Event::Put, NULL))
			res = false;

		//if (objDef && !fireEvent && updateExecutable)
  //    {
		//	fireEvent = true;
		//	
		//	std::vector<IDataSource::IWriter*> writers;
		//	SourceType sv = source.type;
		//	SaveWriter(&writers);

		//	if (!const_cast<ObjectDef*>(objDef)->events.Fire(Event::Put, session, this))
		//		res = false;

  //       RestoreWriter(writers.begin());
		//	source.type = sv;

		//	fireEvent = false;

		//}
   }

   //delete so;
   return res;
}

bool SessionObject::Write(const Object& o, RowID *ids)
{
   return source.writer->Write(o, ids);
}

void SessionObject::CloseWriter()
{
   if( source.writer != NULL )
   {
      source.writer->Close();
      delete source.writer;
      source.writer = NULL;
   }

   ChildList::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i).object->CloseWriter();

   if( objDef && !fireEvent )
   {
		fireEvent = true;
		const_cast<ObjectDef*>(objDef)->events.Fire(Event::WriteCommit, session, this);
		fireEvent = false;
	}
}

IDataSource::IRemover* SessionObject::CreateRemover(IDataSource::IRemover* parent, SourceType srcType, std::wstring *sourceName)
{
   if( source.remover != NULL ) delete source.remover;

   source.remover = DataSource::CreateRemover(parent, objDef->GetSource(), *this, (std::wstring *)&source.removerName, &srcType);

   if( source.remover != NULL)
   {
      if( sourceName != NULL )
         *sourceName = (const std::wstring&)source.removerName;

      ChildList::iterator i = childs.begin();
      for( ; i != childs.end(); i++ )
      {
         std::wstring name;
         IDataSource::IRemover* childRemover = (*i).object->CreateRemover(source.remover, srcType, &name);
         if( childRemover != NULL )
         {
            source.remover->AddChild(childRemover, name);
         }
      }
   }

   return source.remover;
}

void SessionObject::CloseRemover()
{
   if( source.remover )
   {
      source.remover->Close();
      delete source.remover;
      source.remover = NULL;
   }

   ChildList::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i).object->CloseRemover();
}

bool SessionObject::Removing(const wchar_t* filter)
{
   bool ret = false;

   if( objDef != NULL )
   {
      CreateRemover(NULL, stCommon, NULL);
      if( source.remover != NULL )
      {
         ret = source.remover->Remove(filter);
         CloseRemover();

			fireEvent = true;
			const_cast<ObjectDef*>(objDef)->events.Fire(Event::Remove, session, this);
			fireEvent = false;
		}
   }
   return ret;
}

bool ServObjFormat::ReadMembers(ParseStreamU& stream, FormatList *fmtList)
{
   if( !Format::ReadMembers(stream, fmtList) )
      return false;

   const ObjectDef* od = ObjectDef::Get(name);
	if (od != NULL && od->HaveExecutableFields(MemberFormat::ExecOnPut | MemberFormat::ExecOnGet))
   {
      ObjectDef::Fields::const_iterator fi = od->fields.begin();
      for( ; fi != od->fields.end(); fi++ )
      {
         const MemberFormat& mf = fi->format;
         if( (mf.flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet | MemberFormat::CanCreate)) != 0 && FindMember(mf.name.c_str()) < 0 )
            push_back(mf);

      }
   }
   return true;
}

void SessionObject::WriteFormat(OutStream* os) const
{
#ifdef ADS_COMPATIBILITY
   const wchar_t* v = session->GetUser().Version();
   std::wstring name(format->name);
   if( (v == NULL || *v == L'\0') && (name.compare(L"OrderPrev") == 0 || name.compare(L"PDAOrderPrev") == 0 || name.compare(L"ClientPrev") == 0) )
   {
      name = name.substr(0, name.size() - 4);
      os->Append(name.c_str());
      format->MembersToString(os, session->GetFormatList());
   } else
      format->ToString(os, session->GetFormatList());
#else
   format->ToString(os, session->GetFormatList());
#endif
}

bool SessionObject::DoObjCommand(const std::wstring& action, OutStream* stream)
{
   bool done = false;
   std::wstring msg;

   IEvent::Type et;
   std::vector<IDataSource::IWriter*> writers;
   if( objDef )
   {
		fireEvent = true;
		
		SaveWriter(&writers);

      bool updPut = true;
      if(action.compare(REMOVE_OBJECTS) == 0)
      {
         et = Event::Remove;
      } else if(action.compare(READ_OBJECTS) == 0)
      {
         et = Event::Get;
         updPut = false;
      } else
      {
         et = Event::Put;
      }

      RefreshExecutableData(updPut);

		const_cast<ObjectDef*>(objDef)->events.Fire(et, session, this);
		RestoreWriter(writers.begin());
	
		fireEvent = false;
	}

   IDataSource::IObjSource *objSource = DataSource::CreateObjSource(objDef->GetSource(), *this);
   if( objSource != NULL )
   {
      BSTR tmsg = NULL;
      ExchangeList* so = objSource->Do(this, action, session->GetFormatList(), &tmsg);
      if( tmsg )
      {
         msg = tmsg;
#if UNIX
         free(tmsg);
#else
         SysFreeString(tmsg);
#endif
      }
      if( so )
      {
         done = true;
         WriteAnswer(stream, true, msg.c_str());
         so->ToString(stream);
			
			if( objDef && so->size() > 0 && et == Event::Put)
			{
				for( unsigned i=0; i<so->size(); i++ )
				{
					const ServObject* src = so->at(i);
					if( this->format->name.compare(src->format->name) == 0 )
					{
						for( unsigned si=0; si < this->size() && si<src->size(); si++ )
							src->at(si)->Copy(this->at(si));
						
						fireEvent = true;

						const_cast<ObjectDef*>(objDef)->events.Fire(et, session, this);
						
						RestoreWriter(writers.begin());

						fireEvent = false;
						break;
					}
				}
			}
         
			delete so;
      } else if( msg.empty() )
         msg =  L"Нет данных";

      objSource->Close();
      delete objSource;
   } else
      msg = L"Не могу создать OnLine объект";

   if( !done )
      WriteAnswer(stream, false, msg.c_str());

   return true;
}

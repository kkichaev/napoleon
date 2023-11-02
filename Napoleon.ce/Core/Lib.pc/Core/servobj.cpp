/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Server object.
 *
 * ert   16/09/2009   creating
 */
#include "stdafx.h"
#include "servobj.h"

#include <algorithm>

using namespace GRServer;
static const char* servObjReadError;

static _locale_t enLocale;

bool ServObject::InitLocale()
{
	enLocale = _create_locale(LC_NUMERIC, "ENU");
	return (enLocale != NULL);
}

_locale_t ServObject::GetLocale()
{
	return enLocale;
}

 static void InitMember(Member* m, const MemberFormat& format)
{
   switch(format.type)
   {
   case MemberFormat::mtBinary:
      m->binary = NULL;
      break;
   case MemberFormat::mtDateTime:
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &m->datetime);
      break;
   }
   case MemberFormat::mtNumber:
      m->number = 0;
      break;
   case MemberFormat::mtObject:
      m->object = NULL;
      break;
   case MemberFormat::mtString:
      m->str = new CString();
      break;
    default:
        break;
   }
}

Format::~Format()
{
}

bool Format::Read(ParseStreamU& stream, FormatList *fmtList)
{
   if( name.empty() )
   {
      if( !stream.CopyUntill(&name, L'[') ) return false;
   } else
   {
      if( stream.Current() != L'[' )
      {
         std::wstring tn;
         if( !stream.CopyUntill(&tn, L'[') || tn.compare(name) != 0 )
            return false;
      }
   }

   if( !stream.MoveNext() ) return false;
   //
   // read from sym after '[' till ']'
   //
   if( !ReadMembers(stream, fmtList) ) return false;

   stream.MoveNext();
   return true;
}

bool Format::ReadType(MemberFormat *format, ParseStreamU& stream)
{
   if( !stream.MoveNext() ) return false;

   wchar_t sym = stream.Current();
   switch( sym )
   {
   case L's':
      format->type = MemberFormat::mtString;
      break;
   case L'n':
   {
      format->type = MemberFormat::mtNumber;
      if( stream.Next() == L'(' )
      {
         std::wstring val;
         stream.MoveNext(); // eat 'n'
         stream.MoveNext(); // eat '('
         if( !stream.CopyUntill(&val , L')') )
            return false;
         format->format.fraction = _wtoi(val.c_str());
      } else
         format->format.fraction = 0;
      break;
   }
   case L'b':
      format->type = MemberFormat::mtBinary;
      break;
   case L'd':
   {
      format->type = MemberFormat::mtDateTime;
      if( stream.Next() == L't' )
      {
         stream.MoveNext();
         format->format.dateFormat = MemberFormat::Stamp;
      } else
      {
         format->format.dateFormat = MemberFormat::Date;
      }
      break;
   }
   case L't':
      format->type = MemberFormat::mtDateTime;
      format->format.dateFormat = MemberFormat::Time;
      break;
   default:
      return false;
   }

   return true;
}

bool Format::ReadMembers(ParseStreamU& stream, FormatList *fmtList)
{
   MemberFormat mf;

   bool done = false, error = false;
   while( !done && !error && !stream.EOS() )
   {
      wchar_t sym = stream.Current();
      switch( sym )
      {
      case L',':
      case L']':
         push_back(mf);
         mf.name.clear();
         done = (sym == L']');
         break;

      case L':':
         if( !ReadType(&mf, stream) )
            error = true;
         break;

      case L'[':
      {
         stream.MoveNext();

         mf.type = MemberFormat::mtObject;
         std::wstring fname = name + L"$" + mf.name;

         bool addFmt = false;
         Format *f = fmtList->GetFormat(fname);
         if( f == NULL )
         {
            f = fmtList->NewFormat(fname);
            addFmt = true;
         } else
            f->clear();

         if( !f->ReadMembers(stream, fmtList) )
         {
            error = true;
            if( addFmt ) delete f;
         }
         if( addFmt ) fmtList->AddFormat(f, true);
         break;
      }

      default:
         mf.name.append(1, sym);
      }

      if( !done ) stream.MoveNext();
   }

   return done;
}

void Format::ToString(OutStream *str, const FormatList *fmtList) const
{
   str->Append(name.c_str());
   MembersToString(str, fmtList);
}

void Format::MembersToString(OutStream *str, const FormatList *fmtList) const
{
   str->Append(L"[");

   const_iterator i = begin();
   bool start = true;
   for( ; i != end(); i++ )
   {
      if( ((*i).flags & MemberFormat::Hidden) != 0 )
         continue;

      if( start ) start = !start;
      else str->Append(L",");

      str->Append(i->name.c_str());
      switch(i->type)
      {
      case MemberFormat::mtString:
         str->Append(L":s");
         break;
      case MemberFormat::mtBinary:
         str->Append(L":b");
         break;
      case MemberFormat::mtNumber:
         str->Append(L":n");
         if( i->format.fraction != 0 )
         {
            wchar_t buf[20];
            wsprintfW(buf, L"(%d)", i->format.fraction);
            str->Append(buf);
         }
         break;
      case MemberFormat::mtDateTime:
         switch( i->format.dateFormat )
         {
         case MemberFormat::Stamp:
            str->Append(L":dt");
            break;
         case MemberFormat::Date:
            str->Append(L":d");
            break;
         case MemberFormat::Time:
            str->Append(L":t");
            break;
         }
         break;
      case MemberFormat::mtObject:
         {
            Format* f = fmtList->GetFormat(name + L"$" + i->name);
            if( f != NULL )
               f->MembersToString(str, fmtList);
         }
         break;

      default:
         break;
      }
   }

   str->Append(L"]");
}

int Format::FindMember(const wchar_t *name, bool ignoreCase) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      int cmp = (ignoreCase) ? _wcsicmp(i->name.c_str(), name) : i->name.compare(name);
      if( cmp == 0 )
         return (int)distance(begin(), i);
   }

   return -1;
}

FormatList::~FormatList()
{
   free(cryptData);
}

Format* FormatList::GetFormat(const std::wstring& name) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( (*i)->name.compare(name) == 0 )
         return (Format*)((const Format*)(*i));
   }

   return (formatHolder) ? formatHolder->GetFormat(name) : NULL;
}

void FormatList::AddFormat(Format* format, bool pushFront)
{
   if( pushFront )
      insert(begin(), format);
   else
      push_back(format);
}

static bool ReadString(std::wstring* val, ParseStreamU& stream)
{
   if( stream.Current() != L'"' ) return false;
   stream.MoveNext();

   while( true )
   {
      if( stream.EOS() ) return false;

      wchar_t sym = stream.Current();
      if( sym == L'"' ) break;
      if( sym == L'\\' )
      {
         if( !stream.MoveNext() ) return false;

         wchar_t sym1 = stream.Current();
         switch( sym1 )
         {
         case L'\\': break;
         case L'/': sym = sym1; break;
         case L'"': sym = sym1; break;
         case L'b': sym = L'\b'; break;
         case L'f': sym = L'\f'; break;
         case L'n': sym = L'\n'; break;
         case L'r': sym = L'\r'; break;
         case L't': sym = L'\t'; break;
         default:
            val->append(1, sym);
            sym = sym1;
            break;
         }
      }
      val->append(1, sym);
      stream.MoveNext();
   }
   return stream.MoveNext();
}

static bool ReadNumber(double *val, ParseStreamU& stream)
{
   const wchar_t sym[] = L"0123456789.eE-+";
   std::wstring value;

   while( !stream.EOS() && wcschr(sym, stream.Current()) )
   {
      value.append(1, stream.Current());
      stream.MoveNext();
   }

   const wchar_t *ep;
	setlocale(LC_NUMERIC, "English");
   (*val) = wcstod(value.c_str(), (wchar_t**)&ep);
   return (!value.empty() && *ep == '\0');
}

static bool ReadBinary(Member* m, ParseStreamU& stream)
{
   std::wstring val;
   if( !stream.CopyUntill(&val, L':') || !stream.MoveNext() ) return false;

   Binary *b = NULL;
   int len = _wtoi(val.c_str());
   if( len )
   {
      b = new Binary();
      wchar_t* p = (wchar_t*)b->Alloc(len);

      while( (int)len > 0 )
      {
         if( len == 1 )
            *(char*)p = (char)stream.Current();
         else
            *p++ = stream.Current();

         len -= sizeof(wchar_t);
         if( !stream.MoveNext() ) return false;
      }
   }

   m->binary = new MemoryBinary(b);

   return true;
}

static bool ReadWord(WORD *value, int digits, ParseStreamU& stream)
{
   *value = 0;

   while(digits-- > 0)
   {
      wchar_t dig = stream.Current();
      if( iswdigit(dig) == 0 ) return false;

      *value *= 10;
      *value += dig - L'0';

      if( !stream.MoveNext() ) return false;
   }

   return true;
}

static bool ReadDateTime(Member* m, const MemberFormat& mf, ParseStreamU& stream)
{
   SYSTEMTIME st = {0};

   // set date 2009-12-25
   if( mf.format.dateFormat == MemberFormat::Stamp || mf.format.dateFormat == MemberFormat::Date )
   {
      if( !ReadWord(&st.wYear, 4, stream) ) return false;

      // íå áóäåì ïðîâåðÿòü ðàçäåëèòåëè
      stream.MoveNext();
      if( !ReadWord(&st.wMonth, 2, stream) ) return false;

      stream.MoveNext();
      if( !ReadWord(&st.wDay, 2, stream) ) return false;
   }

   // set time 14:05:06
   if( mf.format.dateFormat == MemberFormat::Stamp || mf.format.dateFormat == MemberFormat::Time )
   {
      if( mf.format.dateFormat == MemberFormat::Stamp ) stream.MoveNext();

      if( !ReadWord(&st.wHour, 2, stream) ) return false;

      stream.MoveNext();
      if( !ReadWord(&st.wMinute, 2, stream) ) return false;

      stream.MoveNext();
      if( !ReadWord(&st.wSecond, 2, stream) ) return false;
   }

   if( st.wYear == 0 )
   {
      st.wDay = 1;
      st.wMonth = 1;
      st.wYear = 2000;
   }

   SystemTimeToFileTime(&st, &m->datetime);
   return true;
}

Object::~Object()
{
   FreeMembers(begin(), end());
}

const Object::Field* Object::GetField(const wchar_t* name) const
{
   Object::const_iterator i = begin();
   Format::const_iterator fi = format.begin();

   for( ; fi != format.end(); fi++, i++ )
   {
      if( _wcsicmp(fi->name.c_str(), name) == 0 )
         return new Field((*fi), (*i));
   }
   return NULL;
}

void Object::FreeMembers(iterator _s, iterator _e)
{
   Format::const_iterator fi = format.begin();
 //  iterator i = _s;
	//iterator e = _Make_iter(_e);

   advance(fi, distance(begin(), _s));
   for( ; _s != _e; fi++, _s++ )
      FreeMember(_s, fi);
}

void Object::FreeMember(iterator _s)
{
   Format::const_iterator fi = format.begin();
   advance(fi, distance(begin(), _s));

   FreeMember(_s, fi);
}

void Object::FreeMember(iterator i, Format::const_iterator fi)
{
   switch(fi->type)
   {
   case MemberFormat::mtString:
      delete (*i).str;
      break;

   case MemberFormat::mtObject:
      delete (*i).object;
      break;

   case MemberFormat::mtBinary:
      delete (*i).binary;
      break;

    default:
      break;
   }
}

bool Object::ReadMember(Member* m, const MemberFormat& mf, ParseStreamU& stream, FormatList *fmtList)
{
   switch( mf.type )
   {
   case MemberFormat::mtString:
      m->str = new CString();
      if( ReadString((std::wstring*)m->str, stream) ) return true;
      delete m->str;
      break;

   case MemberFormat::mtNumber:
      if( ReadNumber(&m->number, stream) ) return true;
      break;

   case MemberFormat::mtObject:
    {
       Format *f = fmtList->GetFormat(format.name + L"$" + mf.name);
       if( f == NULL ) return false;
       m->object = new ServObject(f);
       if( m->object->ReadObjects(stream, fmtList) ) return true;
       delete m->object;
       break;
    }

   case MemberFormat::mtBinary:
      if( ReadBinary(m, stream) ) return true;
      break;

   case MemberFormat::mtDateTime:
      if( ReadDateTime(m, mf, stream) ) return true;
      break;

   default:
      break;
   }

   return false;
}

bool Object::ReadMembers(ParseStreamU& stream, FormatList *fmtList)
{
	if (stream.Current() != L'[')
	{
		servObjReadError = " start obj error";
		return false;
	}

   Format::const_iterator fi = format.begin();
   for( ; fi != format.end(); fi++ )
   {
      Member m;
      if( (fi->flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet)) != 0 )
		{
			servObjReadError = " exec error";
			return false;
		}

		if (!stream.MoveNext())
		{
			servObjReadError = " move next error";
			return false;
		}

      if( stream.Current() == L']' )
      {
         stream.MoveNext();
         break; //ïóñòîé îáúåêò
      }

		if (!ReadMember(&m, (*fi), stream, fmtList))
		{
			servObjReadError = " read memeber error";
			return false;
		}

      push_back(m);

      wchar_t sym = stream.Current();
      if( sym == L']' )
      {
         // äîáàâèì ðàñ÷åòíûå ïîëÿ
         while( ++fi != format.end() )
         {
            if( (fi->flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet | MemberFormat::CanCreate)) == 0 )
               return false;
            else
            {
               InitMember(&m, (*fi));
               push_back(m);
            }
         }

         stream.MoveNext();
         break;
      } else if( sym != L',' )
		{
			servObjReadError = " move next ',' error";
			return false;
		}
	}

   return true;
}

Object* Object::Read(ParseStreamU& stream, const Format& format, FormatList *fmtList)
{
   Object *o = new Object(format);
   if( o->ReadMembers(stream, fmtList) == false )
   {
      delete o;
      o = NULL;
   }

   return o;
}

Object* Object::Create(const Format& format)
{
   Object *o = new Object(format);

   Format::const_iterator i = format.begin();
   for( ; i != format.end(); i++ )
   {
      Member m;
      InitMember(&m, (*i));
      o->push_back(m);
   }
   return o;
}

bool Object::MoveTo(Object* dest)
{
   if( &(dest->format) != &(format) )
      return false;

   Format::const_iterator fi = format.begin();
   iterator si = begin(), di = dest->begin();
   for( ; si != end(); si++, di++, fi++ )
   {
      dest->FreeMember(di, fi);

      *di = *si;
      (*si).object = NULL;
   }

   return true;
}

void Object::ToString(OutStream *str, const FormatList *fmtList) const
{
	//OutStream *str = new OutStream();
   const_iterator i = begin();
   Format::const_iterator fi = format.begin();

   bool started = true;
   str->Append(L"[");
   for( ; i != end(); i++, fi++ )
   {
      if( ((*fi).flags & MemberFormat::Hidden) != 0 )
         continue;

      if( started ) started = !started;
      else str->Append(L",");

      switch( fi->type )
      {
      case MemberFormat::mtString:
         str->AppendQuoted((std::wstring&)*i->str);
         break;
      case MemberFormat::mtObject:
         if( i->object == NULL || i->object->size() == 0 ) str->Append(L"[]");
         else i->object->MembersToString(str, fmtList);
         break;
      case MemberFormat::mtNumber:
         str->Append(i->number, fi->format.fraction);
         break;
      case MemberFormat::mtBinary:
         if( i->binary == NULL )
            str->Append((const BYTE*)0, 0);
         else
         {
            str->Append(i->binary->Bytes(), i->binary->Size());
            i->binary->Close();
         }
         break;
      case MemberFormat::mtDateTime:
         str->Append(i->datetime,
            (fi->format.dateFormat == MemberFormat::Stamp || fi->format.dateFormat == MemberFormat::Date),
            (fi->format.dateFormat == MemberFormat::Stamp || fi->format.dateFormat == MemberFormat::Time));
         break;
    default:
        break;
      }
   }
   str->Append(L"]");
	
	//_str->Append(*str);
	//delete str;
}

const Member* Object::operator [](const wchar_t *name) const
{
   int index = format.FindMember(name);
   return (index < 0) ? NULL : &at(index);
}

Member* Object::operator [](const wchar_t *name)
{
   int index = format.FindMember(name);
   return (index < 0) ? NULL : &at(index);
}

void Object::Assign(const Member &member, const wchar_t *name)
{
   int index = format.FindMember(name);

   if( index >= 0 )
      at(index) = member;
}

bool Object::CopyFrom(const Object& ref, const std::vector<int>& indexes)
{
   if( &format != &ref.format ) return false;

   bool ret = true;
   std::vector<int>::const_iterator i = indexes.begin();
   for( ; ret && i != indexes.end(); i++ )
   {
      int idx = (*i);
      switch( format.at(idx).type )
      {
      case MemberFormat::mtDateTime:
         at(idx).datetime = ref.at(idx).datetime;
         break;
      case MemberFormat::mtNumber:
         at(idx).number = ref.at(idx).number;
         break;
      case MemberFormat::mtString:
         at(idx).str->assign((std::wstring&)*ref.at(idx).str);
         break;
      default:
         ret = false;
         break;
      }
   }

   return ret;
}

void Object::Copy(Object* dest) const
{
   const_iterator si = begin();
   int idx = 0;
   for( ; si != end(); si++, idx++)
   {
		const MemberFormat& fmt = format.at(idx);
		int didx = dest->format.FindMember(fmt.name.c_str());
		if( didx < 0 || dest->format.at(didx).Equals(fmt) == false)
			continue;
	
		Member& di = dest->at(didx);
      switch( fmt.type )
      {
      case MemberFormat::mtDateTime:
         di.datetime = (*si).datetime;
         break;
      case MemberFormat::mtNumber:
         di.number = (*si).number;
         break;
      case MemberFormat::mtString:
         di.str->assign((std::wstring&)*(*si).str);
         break;
      case MemberFormat::mtObject:
         if( (*si).object )
         {
            if( di.object == NULL )
               di.object = new ServObject((*si).object->format);
            (*si).object->Copy(di.object);
         } else
         {
            delete di.object;
            di.object = NULL;
         }
         break;
      case MemberFormat::mtBinary:
         delete di.binary;
         di.binary = NULL;
         if( (*si).binary )
         {
            DWORD size = (*si).binary->Size();
            BYTE *dest = (BYTE*)malloc(size);
            memcpy(dest, (*si).binary->Bytes(), size);

            di.binary = new MemoryBinary(new Binary(dest, size));
         }
         break;
       default:
          break;
      }
   }
}

void ServObject::MoveValuesTo(ServObject* ol)
{
   iterator i = begin();
   for( ; i != end(); i++ )
   {
      ol->push_back((*i));
      *i = NULL;
   }
   clear();
}

bool ServObject::ReadObjects(ParseStreamU& stream, FormatList* fmtList)
{
   while( true )
   {
      Object *o = Object::Read(stream, *format, fmtList);
      if( o != NULL )
      {
         if( !o->IsEmpty() )
            push_back(o);
         else
            delete o;
      }
      else
         break;

      if( stream.Current() != L'[' )
         return true;
   }

   return false;
}

void ServObject::ToString(OutStream *str, const FormatList *fmtList) const
{
   format->ToString(str, fmtList);
   MembersToString(str, fmtList);
}

void ServObject::MembersToString(OutStream *str, const FormatList* fmtList) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
	{
		PrepareToString(*(*i));
      (*i)->ToString(str, fmtList);
		AfterToString(*(*i));
	}
}

Object* ServObject::AddObject()
{
   Object *o = Object::Create(*format);
   push_back(o);
   return o;
}

bool ServObject::Read(ParseStreamU& stream, FormatList* fmtList)
{
   bool fmtCreated = false;
   if( format == NULL )
   {
      format = fmtList->NewFormat(L"");
      fmtCreated = true;
   } else
      format->clear();

   clear();

   if( !format->Read(stream, fmtList) )
   {
      if( fmtCreated )
      {
         format = NULL;
         delete format;
      }
      return false;
   }

   if( fmtCreated )
      fmtList->AddFormat(format, true);

   while( stream.Current() == L'[' )
   {
      Object *o = Object::Read(stream, *format, fmtList);
		if (o == NULL)
			return false;

      push_back(o);
   }
   return true;
}

void ServObject::Copy(ServObject *dest) const
{
   if( format != dest->format )
      return;

   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      Object* o = dest->AddObject();
      (*i)->Copy(o);
   }
}

static size_t CountObjectsSize(ServObject *so)
{
	size_t cb = 0;

	if (so != NULL)
	{
		ServObject::const_iterator i = so->begin();
		for (; i != so->end(); i++)
		{
			cb += (*i)->Size();
		}
	}

	return cb;
}

size_t Object::Size() const
{
	size_t cb = 0;
	const_iterator i = begin();
	Format::const_iterator fi = format.begin();
	
	for (; i != end(); i++, fi++)
	{
		switch (fi->type)
		{
		case MemberFormat::mtString:
			cb += (i->str != NULL) ? i->str->size() * sizeof(wchar_t): 0;
			break;
		case MemberFormat::mtNumber:
		case MemberFormat::mtDateTime:
			cb += 8;
			break;
		case MemberFormat::mtBinary:
			cb += (i->binary != NULL) ? i->binary->Size() : 0;
			break;
		case MemberFormat::mtObject:
			cb += CountObjectsSize(i->object);
			break;
		}
	}

	return cb;
}

void ExchangeList::ToString(OutStream *str) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      const ServObject *so = (*i);
      if( so->size() > 0 )
         so->ToString(str, fmtList);
   }
}

bool ExchangeList::Read(ParseStreamU& stream, IServObjectCreator* creator, bool pushEmptyObjects)
{
#ifdef UNIX
#else
	setlocale(LC_NUMERIC, "English");
#endif
	
	while (!stream.EOS())
   {
      std::wstring name;
		if (!stream.CopyUntill(&name, L'['))
		{
			readError = "obj name error";
			return false;
		}

      ServObject* so = creator->Create(name);
      if( so == NULL || !so->Read(stream, fmtList) )
      {
			readError = servObjReadError;
			delete so;
         return false;
      }

      if( so->size() > 0 || pushEmptyObjects)
         push_back(so);
      else
         delete so;
   }

   return true;
}

void ExchangeList::RemoveTo(ExchangeList::const_iterator i)
{
   int dist = (int)distance((const_iterator)begin(), i);
   while( dist-- > 1 )
      EraseFront();
}

void ExchangeList::EraseFront()
{
   if( size() > 0 )
   {
      ServObject* so = front();
      delete so;
      front() = NULL;
      erase(begin());
   }
}

/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Filter
 *
 * ert   20/10/2010   creating
 */
#include "stdafx.h"

#if 1

#include "filter.h"
#include "objdef.h"
#include <sources.h>
#include <session.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

struct FilterValue
{
   FilterValue();
   FilterValue(const FilterValue& src);

   ~FilterValue();

   bool operator< (const FilterValue& value) const { return (Compare(value) < 0); }
   int Compare(const FilterValue& value) const;

   FilterValue& operator= (const FilterValue& src);

   void Assign(const CString& str) { Assign((std::wstring&)str); }

   void Assign(const std::wstring& src)
   {
		isEmpty = false;

      if( isString ) value.str->assign(src);
      else
      {
         isString = true;
         value.str = new std::wstring(src);
      }
   }

   void Assign(double val)
   {
		isEmpty = false;
		if (isString) delete value.str;

      isString = false;
      value.dblValue = val;
   }

   void Assign(const FILETIME& val)
   {
		isEmpty = false;
		if (isString) delete value.str;

      isString = false;
      __int64 tval = *(__int64*)&val;
      value.dblValue = (double)tval;
   }

   void Assign(const SYSTEMTIME& st)
   {
      FILETIME ft;
      SystemTimeToFileTime(&st, &ft);
      Assign(ft);
   }


   union
   {
      std::wstring* str;
      double dblValue;
   } value;

   bool isString;
	bool isEmpty;
};

class FilterValueHead
{
public:
   //              =,  >,  <,   >=,   <=,   <>,    in
   enum Operator { Eq, Gt, Lt, GtEq, LtEq, NotEq, InSet };

   FilterValueHead();
   FilterValueHead(const MemberFormat& format, const std::wstring& data, Operator op);
   FilterValueHead(const FilterValue& constValue, const std::wstring& data, Operator op);
   FilterValueHead(const FilterValueHead& src);
   ~FilterValueHead();

   FilterValueHead& operator= (const FilterValueHead& src);

   bool Op(const FilterValue& _left, const FilterValue& _right);
   Operator GetOperator() const { return op; }

   void Load(FilterValue* value, const DataForm& base); // load my field
   void Load(FilterValue* value, const Object& obj); // load out field

   void Load(FilterValue* value)
   {
      if( isConstValue )
         *value = constValue;
   }

   bool IsConstValue() const { return isConstValue; }

public:
   struct IDBReader
   {
      virtual ~IDBReader() {}
      virtual void Load(FilterValue* value, const DataForm& base) = 0;

      static IDBReader* Create(const std::wstring& data, MemberFormat::MemberType type,
         MemberFormat::DateFormat dateFormat, const DataForm& base);
   };

   struct IObjReader
   {
      virtual ~IObjReader() {}
      virtual void Load(FilterValue* value, const Object& obj) = 0;

      static IObjReader* Create(const MemberFormat& member, const GRServer::Format& format);
   };

protected:
   IDBReader* dbReader;
   IObjReader *objReader;

   std::wstring data; // DB field (for my field)
   MemberFormat format; // Object field (for out field)
   FilterValue constValue;

   bool isConstValue;

   Operator op;
};

class FilterFunc
{
public:
   FilterFunc() { this->owner = NULL; }

   virtual ~FilterFunc() {}
   virtual bool Op(const DataForm& srcBase, const SessionObject& thisObj) = 0; //thisObj - доступ к сессии
   virtual FilterFunc* Clone() const = 0;

   virtual void SetOwner(IFilterInSet* owner) { this->owner = owner; }

protected:
   IFilterInSet* owner;
};

class FilterFunctionBase : public FilterFunc
{
public:
   FilterFunctionBase(FilterValueHead* head, const std::wstring& object);
   ~FilterFunctionBase();

protected:
   bool LoadValue(FilterValue* value, const SessionObject& thisObj);

protected:
   std::wstring object;
   FilterValueHead* head;
};

class OpFunction : public FilterFunctionBase
{
public:
   OpFunction(FilterValueHead* head, const std::wstring& object);
   ~OpFunction();

   virtual bool Op(const DataForm& srcBase, const SessionObject& thisObj); //thisObj - доступ к сессии

   FilterFunc* Clone() const
   {
      OpFunction *f = new OpFunction(new FilterValueHead(*head), object);
      return f;
   }

protected:
};

class InSetFunction : public FilterFunctionBase
{
public:
   InSetFunction(FilterValueHead* head, const std::wstring& object);
   ~InSetFunction();

   virtual bool Op(const DataForm& srcBase, const SessionObject& thisObj); //thisObj - доступ к сессии

   FilterFunc* Clone() const
   {
      InSetFunction *f = new InSetFunction(new FilterValueHead(*head), object);
      return f;
   }

protected:
   void Load(const SessionObject& thisObj);

protected:
   struct FilterValuePtrCmp
   {
      bool operator() (FilterValue* const &_left, FilterValue* const &_right) const
      {
         return _left->operator < (*_right);
      }
   };

   typedef std::set<FilterValue*, FilterValuePtrCmp> Fields;

   Fields fields;
   bool loaded;
};

class NotFunction : public FilterFunc
{
public:
   NotFunction(FilterFunc* foo) { func = foo; }
   ~NotFunction() { delete func; }

   virtual bool Op(const DataForm& srcBase, const SessionObject& thisObject)
   {
      return !func->Op(srcBase, thisObject);
   }

   FilterFunc* Clone() const
   {
      NotFunction *f = new NotFunction(func->Clone());
      return f;
   }

   virtual void SetOwner(IFilterInSet* owner)
   {
      FilterFunc::SetOwner(owner);
      func->SetOwner(owner);
   }

protected:
   FilterFunc* func;
};

class BinaryFunction : public FilterFunc
{
public:
   BinaryFunction(FilterFunc* _left, FilterFunc* _right, bool andOp /* && or || */)
   {
      this->_left = _left;
      this->_right = _right;
      this->andOp = andOp;
   }

   ~BinaryFunction()
   {
      delete _left;
      delete _right;
   }

   virtual bool Op(const DataForm& srcBase, const SessionObject& thisObject)
   {
      return (andOp) ?
         (_left->Op(srcBase, thisObject) && _right->Op(srcBase, thisObject)) :
         (_left->Op(srcBase, thisObject) || _right->Op(srcBase, thisObject));
   }

   FilterFunc* Clone() const
   {
      BinaryFunction *f = new BinaryFunction(_left->Clone(), _right->Clone(), andOp);
      return f;
   }

   virtual void SetOwner(IFilterInSet* owner)
   {
      FilterFunc::SetOwner(owner);

      _left->SetOwner(owner);
      _right->SetOwner(owner);
   }

protected:
   FilterFunc* _left;
   FilterFunc* _right;
   bool andOp;
};

class FilterInSet : public IFilterInSet
{
public:
   FilterInSet(FilterFunc* func);
   virtual ~FilterInSet();

   virtual bool InSet(const DataForm& srcBase, const SessionObject& thisObject);

   virtual IFilterInSet* Clone() const
   {
      FilterInSet *f = new FilterInSet(func->Clone());
      return f;
   }

   virtual void SetUserFilter(const std::wstring& userFilter) { this->userFilter = userFilter; }

   std::wstring userFilter;

protected:
   FilterFunc* func;
};

class FilterObjHolder : public IFilterObjHolder
{
public:
   FilterObjHolder(const std::vector<FilterValueHead*>& fields, FilterInSet* filter);
   virtual ~FilterObjHolder();

   virtual void Load(const DataForm& base, const SessionObject& thisObject, IObjectReader* reader); // load My objects from DB

   virtual bool Next(const Object& parent);
   virtual bool Get(Object* dest) const;

   virtual IFilterObjHolder* Clone() const
   {
      Head newHead;
      Head::const_iterator i = head.begin();
      for( ; i != head.end(); i++ )
         newHead.push_back(new FilterValueHead(*(*i)));

      return new FilterObjHolder(newHead, (filter) ? (FilterInSet*)filter->Clone() : NULL);
   }

protected:
   typedef std::vector<FilterValueHead*> Head;
   Head head;

   class Key : public std::vector<FilterValue>
   {
   public:
      void Load(const DataForm& base, const Head& head);
      void Load(const Object& obj, const Head& head);

      bool operator< (const Key& src) const;
   };

   typedef std::map<Key, ServObject*> Fields;

   Fields fields;
   Fields::const_iterator current;
   int objIndex;

   FilterInSet* filter;
};

struct FilterFieldData
{
   FilterFieldData() : isDBField(false), isConstValue(false) {}

   std::wstring name;
   std::wstring object;

   FilterValue constValue; // for const values

   bool isDBField;
   bool isConstValue;

   bool IsParent() const { return (object.compare(L"parent") == 0); }
   bool IsMy() const { return isDBField; }
   bool IsConstValue() const { return isConstValue; }

	bool Parse(StringStream& stream, const SessionObject& thisObject);
};

// Field op Field
class FilterItemReader
{
public:
   FilterItemReader() {}

   // читает и приводит к виду left - My, right - other
   FilterValueHead* Parse(StringStream& stream, const SessionObject& thisObject);

   bool IsCPRelation() const { return isCPRelation; }

   const std::wstring& Object() const { return object; }

protected:
   bool ReadOp(StringStream& stream);
   const IObjectData* GetOD(const FilterFieldData& src, const SessionObject& thisObject);
   FilterValueHead::Operator ReverseOp(FilterValueHead::Operator op);

   FilterValueHead* MakeValueHead(const FilterFieldData& _left, const FilterFieldData& _right, bool reverseOp, const SessionObject& thisObject);

   std::wstring object;
   FilterFieldData left, right;
   FilterValueHead::Operator op;
   bool isCPRelation;
};

// [DBField] || $objName.FieldName || 'constValue'
bool FilterFieldData::Parse(StringStream& stream, const SessionObject& thisObject)
{
	const IObjectData* myOD = thisObject.GetObjectDef();
	const Session &session = (const Session &)thisObject.GetSession();

   bool ret = false;
   if( !stream.EatWhite() )
      return ret;
   wchar_t sym = stream.Current();
   stream.MoveNext();

   name.clear();
   object.clear();
   isDBField = false;

   if( sym == L'[' )
   {
      isDBField = true;
      ret = stream.CopyUntill(&name, L']');
   } else if( sym == L'$' )
   {
      if( stream.CopyUntill(&object, L'.') )
      {
         stream.MoveNext();

         wchar_t cs = stream.Current();
         if( iswalnum(cs) )
         {
            while( true ) // copy only char & numbers
            {
               name.append(1, cs);
               cs = stream.Next();
               if( iswalnum(cs) == 0 || !stream.MoveNext() )
                  break;
            }
         }
         ret = !name.empty();

			if (ret)
			{
				if (object.compare(L"object") == 0)
				{
					const ObjectDef::Field* f = myOD->FindField(name);
					if (f == NULL) ret = false;
					else
					{
						isDBField = true;
						name = f->data;
					}
				} else 
				{
					const SessionObject* so = session.FindObject(object, &thisObject);
					Token token;
					if (so == NULL)
						ret = false;
					else
					{
						if (so->GetValue(&token, name, true))
						{
							std::wstring buf;
							if (token.ToString(&buf))
							{
								isConstValue = true;
								this->constValue.Assign(buf);
							}
						}
						else if (so->GetObjectDef()->FindField(name) == NULL)
						{
							ret = false;
						}
					}
				}
			}
      }
   } else if( sym == L'\'' )
   {
      std::wstring tv;
      ret = stream.CopyUntill(&tv, L'\'');

      isConstValue = true;
      this->constValue.Assign(tv);
   }

   stream.MoveNext();
   return ret;
}

FilterValueHead* FilterItemReader::MakeValueHead(const FilterFieldData& _left, const FilterFieldData& _right, bool reverseOp, const SessionObject& thisObject)
{
   FilterValueHead* value = NULL;
   isCPRelation = false;

   if( !_right.IsMy() )
   {
      object = _right.object;

      if( _right.IsConstValue() )
         value = new FilterValueHead(_right.constValue, _left.name, (reverseOp) ? ReverseOp(op) : op);
      else
      {
         const IObjectData* otherOD = GetOD(_right, thisObject);
         if( otherOD )
         {
            const ObjectDef::Field* rf = otherOD->FindField(_right.name);
            if( rf )
            {
               value = new FilterValueHead(rf->format, _left.name, (reverseOp) ? ReverseOp(op) : op);
               isCPRelation = (_right.IsParent() && op == FilterValueHead::Eq);
            }
         }
      }
   }

   return value;
}

FilterValueHead* FilterItemReader::Parse(StringStream& stream, const SessionObject& thisObject)
{
   FilterValueHead* value = NULL;
 //  const IObjectData* myOD = thisObject.GetObjectDef();
	//const Session& session = (const Session&)thisObject.GetSession();
	//if (left.Parse(stream, myOD, session) && ReadOp(stream) && right.Parse(stream, myOD, session))
	if (left.Parse(stream, thisObject) && ReadOp(stream) && right.Parse(stream, thisObject))
   {
//      bool rightIsMy = right.IsMy();
      if( left.IsMy() )
         value = MakeValueHead(left, right, false, thisObject);
      else
         value = MakeValueHead(right, left, true, thisObject);
   }

   return value;
}

const IObjectData* FilterItemReader::GetOD(const FilterFieldData& src, const SessionObject& thisObject)
{
   const IObjectData* ret = NULL;
   if( src.IsParent() )
   {
      const ISessionObject* parentI = thisObject.Parent();
      const SessionObject* parent = (parentI == NULL) ? NULL : (const SessionObject*)parentI->Self() ;
      if( parent != NULL )
         ret = parent->GetObjectDef();
   } else
      ret = ObjectDef::Get(src.object);

   return ret;
}

bool FilterItemReader::ReadOp(StringStream& stream)
{
   bool ret = false;
   if( stream.EatWhite() )
   {
      wchar_t sym = stream.Current();
      switch( towupper(sym) )
      {
      case L'=':
         op = FilterValueHead::Eq;
         ret = true;
         break;

      case L'>':
      {
         wchar_t next = stream.Next();
         if( next == L'=' )
         {
            stream.MoveNext();
            ret = true;
            op = FilterValueHead::GtEq;
         } else if( IsSpace(next) )
         {
            ret = true;
            op = FilterValueHead::Gt;
         }
         break;
      }

      case L'<':
      {
         wchar_t next = stream.Next();
         if( next == L'=' )
         {
            stream.MoveNext();
            ret = true;
            op = FilterValueHead::LtEq;
         } else if( next == L'>' )
         {
            stream.MoveNext();
            ret = true;
            op = FilterValueHead::NotEq;
         } else if( IsSpace(next) )
         {
            ret = true;
            op = FilterValueHead::Lt;
         }
         break;
      }
      case L'I':
      {
         if( towupper(stream.Next()) == L'N' )
         {
            stream.MoveNext();
            ret = true;
            op = FilterValueHead::InSet;
         }
         break;
      }
      }
   }

   stream.MoveNext();
   return ret;
}

FilterValueHead::Operator FilterItemReader::ReverseOp(FilterValueHead::Operator op)
{
   FilterValueHead::Operator ret = op;

   switch(op)
   {
   case FilterValueHead::Gt:
      ret = FilterValueHead::LtEq;
      break;
   case FilterValueHead::Lt:
      ret = FilterValueHead::GtEq;
      break;
   case FilterValueHead::GtEq:
      ret = FilterValueHead::Lt;
      break;
   case FilterValueHead::LtEq:
      ret = FilterValueHead::Gt;
      break;
   default: break;
   }

   return ret;
}

//static bool CheckOp(const std::wstring& op)
//{
//   const wchar_t* str = op.c_str();
//
//   if( _wcsicmp(str, L"AND") == 0 ) return true;
//   if( _wcsicmp(str, L"OR") == 0 ) return true;
//
//   return false;
//}

struct ExprValue
{
   ExprValue() { function = NULL; }

   FilterFunc* function;
   std::vector<FilterValueHead*>* cprFields;
};

static bool ReadComplexExpr(ExprValue *value, StringStream& stream, const SessionObject& thisObject);
static bool ReadSimpleExpr(ExprValue *value, StringStream& stream, const SessionObject& thisObject);
static bool ReadStatement(ExprValue *value, StringStream& stream, const SessionObject& thisObject, const wchar_t* stopSym = L"");

static bool ReadStatement(ExprValue *value, StringStream& stream, const SessionObject& thisObject, const wchar_t* stopSym)
{
   bool ret = true;

   do
   {
      if( !ReadComplexExpr(value, stream, thisObject) )
      {
         ret = false;
         break;
      }
      if( stream.EOS() || stream.EatWhite() == false )
         break;

      wchar_t sym = stream.Current();
      if( wcschr(stopSym, sym) != NULL )
      {
         stream.MoveNext();
         break;
      }
   } while( true );

   return ret;
}

static bool ReadSimpleExpr(ExprValue *value, StringStream& stream, const SessionObject& thisObject)
{
   if( !stream.EatWhite() )
      return false;

   bool ret = false;
   wchar_t sym = stream.Current();
   if( sym == L'(' )
   {
      stream.MoveNext();
      ret = ReadStatement(value, stream, thisObject, L")");
   } else if( sym == L'$' || sym == L'[' )
   {
      FilterItemReader reader;
      FilterValueHead* vh = reader.Parse(stream, thisObject);
      if( vh )
      {
         if( reader.IsCPRelation() )
            value->cprFields->push_back(vh);
         else
         {
            value->function = (vh->GetOperator() == FilterValueHead::InSet) ?
               (FilterFunc*)new InSetFunction(vh, reader.Object()) :
               (FilterFunc*)new OpFunction(vh, reader.Object());
         }
         ret = true;
      }
   } else // may be function
   {
      if( stream.CheckString(L"NOT", true) )
      {
         ExprValue ev;
         ev.cprFields = value->cprFields;

         stream.MoveNext();
         ret = ReadSimpleExpr(&ev, stream, thisObject);
         if( ev.function == NULL ) ret = false;

         if( ret )
         {
            _ASSERT(value->function == NULL);
            value->function = new NotFunction(ev.function);
         }
      }
   }

   return ret;
}

static bool ReadComplexExpr(ExprValue *value, StringStream& stream, const SessionObject& thisObject)
{
   if( !stream.EatWhite() )
      return false;

   bool ret = false, andFunc;
   wchar_t sym = towupper(stream.Current());
   if( sym == L'A' ) // and
   {
      if( stream.CheckString(L"AND", true) )
      {
         andFunc = true;
         ret = true;
      }
   } else if( sym == L'O' ) // or
   {
      if( stream.CheckString(L"OR", true) )
      {
         andFunc = false;
         ret = true;
      }
   }
   if( ret )
      stream.MoveNext();

   if( value->function == NULL )
      return ReadSimpleExpr(value, stream, thisObject);


   if( ret )
   {
      ExprValue ev;
      ev.cprFields = value->cprFields;
      ret = ReadSimpleExpr(&ev, stream, thisObject);
      if( ret && ev.function )
         value->function = new BinaryFunction(value->function, ev.function, andFunc);
   }

   return ret;
}

bool FilterReader::Parse(FilterReader::Data* data, StringStream& stream, const SessionObject& thisObject)
{
   std::vector<FilterValueHead*> cprFields;
   ExprValue ev;
   ev.cprFields = &cprFields;

   bool ret = ReadStatement(&ev, stream, thisObject);
   if( ret )
   {
      FilterInSet* filter = (ev.function) ? new FilterInSet(ev.function) : NULL;
      if( cprFields.size() > 0 )
         data->holder = new FilterObjHolder(cprFields, filter);
      else
         data->filter = filter;
   }

   return ret && (data->holder != NULL && data->filter != NULL);
}

//
//-------------------------- FilterValue -------------------------------
//
FilterValue::FilterValue() : isString(false), isEmpty(true)
{
   value.str = NULL;
}

FilterValue::FilterValue(const FilterValue& src) : isString(false)
{
   value.str = NULL;
   operator=(src);
}

FilterValue::~FilterValue()
{
   if( isString )
      delete value.str;
}

int FilterValue::Compare(const FilterValue& src) const
{
   //_ASSERT( isString == src.isString);
	if (isEmpty)
		return src.isEmpty ? 0 : -1;

   if( isString )
      return wcscmp(value.str->c_str(), src.value.str->c_str());

   double v = value.dblValue - src.value.dblValue;
   return (v < 0) ? -1 : (v > 0) ? 1 : 0;
}

FilterValue& FilterValue::operator= (const FilterValue& src)
{
   if( &src != this )
   {
		if (isString) 
		{
			delete value.str;
			value.str = NULL;
		}

      isString = src.isString;
		isEmpty = src.isEmpty;

		if (!isEmpty)
		{
			if (isString) value.str = new std::wstring(*src.value.str);
			else value.dblValue = src.value.dblValue;
		}
   }
   return *this;
}

//
//-------------------------- FilterObjHolder -------------------------------
//
FilterObjHolder::FilterObjHolder(const std::vector<FilterValueHead*>& fields, FilterInSet* filter)
{
   head = fields;
   this->filter = filter;
   current = this->fields.begin();
}

FilterObjHolder::~FilterObjHolder()
{
   delete filter;
   std::vector<FilterValueHead*>::iterator i = head.begin();
   for( ; i != head.end(); i++ )
      delete (*i);

   Fields::iterator fi = fields.begin();
   for( ; fi != fields.end(); fi++ )
      delete fi->second;
}

void FilterObjHolder::Load(const DataForm& base, const SessionObject& thisObject, IObjectReader* reader)
{
   ObjectReader oreader;
   if( reader == NULL )
      reader = &oreader;
   reader->Create(thisObject, base);

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      if( filter && filter->InSet(base, thisObject) == false )
         continue;

      Key k;
      k.Load(base, head);

      ServObject *so = NULL;
      Fields::const_iterator fnd = fields.find(k);
      if( fnd == fields.end() )
      {
         so = new ServObject(thisObject.format);
         fields.insert(Fields::value_type(k, so));
      } else
         so = fnd->second;

      // load
      reader->Read(so, base);
   }

   current = fields.begin();
   objIndex = -1;
}

void FilterObjHolder::Key::Load(const DataForm& base, const Head& head)
{
   Head::const_iterator i = head.begin();
   for( ; i != head.end(); i++ )
   {
      FilterValue v;
      (*i)->Load(&v, base);

      push_back(v);
   }
}

void FilterObjHolder::Key::Load(const Object& obj, const Head& head)
{
   Head::const_iterator i = head.begin();
   for( ; i != head.end(); i++ )
   {
      FilterValue v;
      (*i)->Load(&v, obj);

      push_back(v);
   }
}

bool FilterObjHolder::Key::operator< (const Key& src) const
{
   const_iterator si = begin(), di = src.begin();
   for( ; si != end(); si++, di++ )
   {
      if( (*si).operator<((*di)) == false )
         return false;
   }

   return true;
}

bool FilterObjHolder::Next(const Object& parent)
{
   if( fields.size() == 0 )
      return false;

   Key key;
   key.Load(parent, head);
   Fields::const_iterator fnd = fields.find(key);

   if( current != fnd )
   {
      current = fnd;
      objIndex = -1;
   }

   if( current == fields.end() || objIndex >= (int)current->second->size() - 1 )
      return false;

   objIndex++;
   return true;
}

bool FilterObjHolder::Get(Object* dest) const
{
   current->second->at(objIndex)->Copy(dest);
   return true;
}

//
//-------------------------- FilterInSet -------------------------------
//
FilterInSet::FilterInSet(FilterFunc* func)
{
   this->func = func;
   func->SetOwner(this);
}

FilterInSet::~FilterInSet()
{
   delete func;
}

bool FilterInSet::InSet(const DataForm& srcBase, const SessionObject& thisObject)
{
   return func->Op(srcBase, thisObject);
}


//
//-------------------------- InSetFunction -------------------------------
//
InSetFunction::InSetFunction(FilterValueHead* head, const std::wstring& object) : FilterFunctionBase(head, object)
{
   loaded = false;
}

InSetFunction::~InSetFunction()
{
   Fields::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      delete (FilterValue*)(*i);
}

bool InSetFunction::Op(const DataForm& srcBase, const SessionObject& thisObj)
{
   if( !loaded )
      Load(thisObj);

   FilterValue value;
   head->Load(&value, srcBase);

   Fields::const_iterator fnd = fields.find(&value);
   return (fnd != fields.end());
}

void InSetFunction::Load(const SessionObject& thisObj)
{
   loaded = true;

   Session& session = (Session&)thisObj.GetSession();
   const ISessionObject* objI = session.LoadObject(object, &thisObj, (owner) ? ((FilterInSet*)owner)->userFilter.c_str() : L"");
   if( objI )
   {
      const ServObject* obj = objI->Self();
      ServObject::const_iterator i = obj->begin();
      for( ; i != obj->end(); i++ )
      {
         FilterValue *value = new FilterValue();
         head->Load(value, *(*i));

         if( fields.insert(value).second == false )
            delete value;
      }
   }
}

//
//-------------------------- OpFunction -------------------------------
//
FilterFunctionBase::FilterFunctionBase(FilterValueHead* head, const std::wstring& object)
{
   this->head = head;
   this->object = object;
}

FilterFunctionBase::~FilterFunctionBase()
{
   delete head;
}

bool FilterFunctionBase::LoadValue(FilterValue* value, const SessionObject& thisObj)
{
   bool ret = false;
   if( head->IsConstValue() )
   {
      head->Load(value);
      ret = true;
   } else
   {
      const Session& session = (const Session&)thisObj.GetSession();
      const SessionObject* obj = session.FindObject(object, &thisObj);

      if( obj )
      {
         int index = obj->CurObjectIndex();
         if( index >= 0 && index < (int)obj->size() )
         {
            head->Load(value, *obj->at(index));
            ret = true;
         }
      }
   }
   return ret;
}

//
//-------------------------- OpFunction -------------------------------
//
OpFunction::OpFunction(FilterValueHead* head, const std::wstring& object) : FilterFunctionBase(head, object)
{
}

OpFunction::~OpFunction()
{
}

bool OpFunction::Op(const DataForm& srcBase, const SessionObject& thisObj)
{
   FilterValue me, other;

   head->Load(&me, srcBase);
   if( LoadValue(&other, thisObj) )
      return head->Op(me, other);

   return false;
}

//
//-------------------------- FilterValueHead -------------------------------
//
FilterValueHead::FilterValueHead() : isConstValue(false)
{
   dbReader = NULL;
   objReader = NULL;
}

FilterValueHead::FilterValueHead(const FilterValue& constValue, const std::wstring& data, Operator op) : isConstValue(true)
{
   dbReader = NULL;
   objReader = NULL;

   this->constValue = constValue;
   this->data = data;
   this->op = op;

   this->format.type = (constValue.isString) ? MemberFormat::mtString : MemberFormat::mtNumber;
}

FilterValueHead::FilterValueHead(const MemberFormat& _format, const std::wstring& data, Operator op) : isConstValue(false)
{
   dbReader = NULL;
   objReader = NULL;

   this->format = _format;
   this->data = data;
   this->op = op;
}

FilterValueHead::FilterValueHead(const FilterValueHead& src)
{
   dbReader = NULL;
   objReader = NULL;

   this->operator=(src);
}

FilterValueHead::~FilterValueHead()
{
   delete dbReader;
   delete objReader;
}

FilterValueHead& FilterValueHead::operator= (const FilterValueHead& src)
{
   if( this != &src )
   {
      this->data = src.data;
      this->op = src.op;

      this->isConstValue = src.isConstValue;
      if( isConstValue )
         this->constValue = src.constValue;
      else
         this->format = src.format;

      delete dbReader;
      delete objReader;

      dbReader = NULL;
      objReader = NULL;
   }

   return *this;
}

bool FilterValueHead::Op(const FilterValue& _left, const FilterValue& _right)
{
   int cmp = _left.Compare(_right);

   bool ret = false;
   switch(op)
   {
   case Eq:
      ret = (cmp == 0);
      break;
   case Gt:
      ret = (cmp > 0);
      break;
   case Lt:
      ret = (cmp < 0);
      break;
   case GtEq:
      ret = (cmp >= 0);
      break;
   case LtEq:
      ret = (cmp <= 0);
      break;
   case NotEq:
      ret = (cmp != 0);
      break;
   default: break;
   }
   return ret;
}

void FilterValueHead::Load(FilterValue* value, const DataForm& base)
{
   if( dbReader == NULL )
      dbReader = IDBReader::Create(data, format.type, format.format.dateFormat, base);

   dbReader->Load(value, base);
}

void FilterValueHead::Load(FilterValue* value, const Object& obj)
{
   if( isConstValue )
      *value = constValue;
   else
   {
      if( objReader == NULL )
         objReader = IObjReader::Create(format, obj.GetFormat());

      objReader->Load(value, obj);
   }
}

//
//---------------------- FilterValueHead::IDBReader -----------------------------------
//
struct EmptyDBReader : public FilterValueHead::IDBReader
{
   virtual void Load(FilterValue* value, const DataForm& base) {}
};

struct StringDBReader : public FilterValueHead::IDBReader
{
   int width, offset;
   StringDBReader(int width, int offset) { this->width = width; this->offset = offset; }

   virtual void Load(FilterValue* value, const DataForm& base)
   {
      char *buf = (char*)alloca(width + 1);
      memcpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      USES_CONVERSION;
      std::string sbuf;
      std::wstring val(A2W_CP(Trunc(buf, &sbuf), DBF_CODE_PAGE));
      value->Assign(val);
   }
};

struct NumberDBReader : public FilterValueHead::IDBReader
{
   int width, offset;
   NumberDBReader(int width, int offset) { this->width = width; this->offset = offset; }

   virtual void Load(FilterValue* value, const DataForm& base)
   {
      char *buf = (char*)alloca(width + 1);
      memcpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      double val = atof(buf);
      value->Assign(val);
   }
};

struct DateTimeDBReader : public FilterValueHead::IDBReader
{
   int width, offset;
   MemberFormat::DateFormat format;
   DateTimeDBReader(int width, int offset, MemberFormat::DateFormat format) { this->width = width; this->offset = offset; this->format = format; }

   virtual void Load(FilterValue* value, const DataForm& base)
   {
      char *buf = (char*)alloca(width + 1);
      memcpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      FILETIME ft = ReadFileTime(format, buf);
      value->Assign(ft);
   }
};

FilterValueHead::IDBReader* FilterValueHead::IDBReader::Create(const std::wstring& data, MemberFormat::MemberType type,
                                                               MemberFormat::DateFormat dateFormat, const DataForm& base)
{
   FilterValueHead::IDBReader* ret = NULL;

   USES_CONVERSION;
   int index = base.Field(W2A_CP(data.c_str(), DBF_CODE_PAGE));
   if( index >= 0 )
   {
      DBField* f = base.GetFieldBase() + index;

      switch( type )
      {
      case MemberFormat::mtString:
         ret = new StringDBReader(f->width, f->offset);
         break;
      case MemberFormat::mtNumber:
         ret = new NumberDBReader(f->width, f->offset);
         break;
      case MemberFormat::mtDateTime:
         ret = new DateTimeDBReader(f->width, f->offset, dateFormat);
         break;
      default: break;
      }
   }

   return (ret) ? ret : new EmptyDBReader();
}

//
//------------------------------ FilterValueHead::::IObjReader -------------------------------
//
struct EmptyObjReader : public FilterValueHead::IObjReader
{
   virtual void Load(FilterValue* value, const Object& obj) {}
};

struct StringObjReader : public FilterValueHead::IObjReader
{
   int index;
   StringObjReader(int index) { this->index = index; }

   virtual void Load(FilterValue* value, const Object& obj)
   {
      value->Assign(*obj.at(index).str);
   }
};

struct NumberObjReader : public FilterValueHead::IObjReader
{
   int index;
   NumberObjReader(int index) { this->index = index; }

   virtual void Load(FilterValue* value, const Object& obj)
   {
      value->Assign(obj.at(index).number);
   }
};

struct DateTimeObjReader : public FilterValueHead::IObjReader
{
   int index;
   DateTimeObjReader(int index) { this->index = index; }

   virtual void Load(FilterValue* value, const Object& obj)
   {
      value->Assign(obj.at(index).datetime);
   }
};

FilterValueHead::IObjReader* FilterValueHead::IObjReader::Create(const MemberFormat& member, const GRServer::Format& format)
{
   FilterValueHead::IObjReader* ret = NULL;
   int index = format.FindMember(member.name.c_str());
   if( index >= 0 )
   {
      switch( member.type )
      {
      case MemberFormat::mtString:
         ret = new StringObjReader(index);
         break;
      case MemberFormat::mtNumber:
         ret = new NumberObjReader(index);
         break;
      case MemberFormat::mtDateTime:
         ret = new DateTimeObjReader(index);
         break;
      default: break;
      }
   }

   return (ret) ? ret : new EmptyObjReader();
}
#endif

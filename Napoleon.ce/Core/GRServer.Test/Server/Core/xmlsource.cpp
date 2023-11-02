/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * XML source
 *
 * предполагается, что при загрузке сервера база данные приводится в соответствие с objects.xml
 * и в дальнейшем для описания таблиц используется ObjectDef (он должен быть синхонизирован с TableDef)
 * имя таблицы - это имя объекта
 *
 * ert   25/03/2013   creating
 */
#include "stdafx.h"
#include "creators.h"
#include "server.h"
#include "sessobj.h"
#include "objdef.h"
#include "srvdata.h"
#include "session.h"
#include "srvutility.h"
#include <ServerDefs.h>

#include <xml.h>
#include <loaders.h>
#include <srvutility.h>

#include <folderholder.h>
#include <dateparse.h>

#include <shlobj.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

static const wchar_t* TABLE_NAME_TAG = L"tableName";
static const wchar_t* DOC_ELEMENT_TAG = L"docElement";
static const wchar_t* ROOT_TAG = L"rootElement";
static const wchar_t* ENCLOSURE_TAG = L"enclosureElement";
static const wchar_t* ENCODING_TAG = L"encoding";
static const wchar_t* WRITE_MODE_TAG = L"mode";
static const wchar_t* ARCHIVE_FOLDER_TAG = L"archiveFolder";

static const wchar_t* DEFAULT_ROOT_TAG = L"data";

struct Params
{
public:
   std::wstring tableName;
   std::wstring docRoot;
   std::wstring rootTag;
   std::wstring enclosure;
   std::wstring encoding;
   std::wstring writeMode;
   std::wstring archiveFolder;

   Params() // : docRoot(L"doc")
   {
   }

   bool Load(const ParamList& parameters, const ISessionObject& object);
};

class XMLField
{
public:
   XMLField(int _fieldIndex) : fieldIndex(_fieldIndex) {}
   virtual ~XMLField() {}

   static XMLField* Create(const IObjectData::Field& field, int idx);

   virtual bool Set(Object* dest, const std::wstring& value) const = 0;
   virtual bool Get(std::wstring* value, const Object& src) const = 0;
   virtual bool MoveData(Object* dest, Object* src) const;

protected:
   int fieldIndex;
};

class XMLBinderBase
{
public:
   virtual ~XMLBinderBase();

	void SetParser(XmlParser *parser)  { this->parser = parser; }

   const std::wstring& Name() const { return name; }
   bool Prepare(const std::wstring& name, const std::wstring& enclosure, const ISessionObject& object);

protected:
   GRServer::Format* format;
   std::wstring name;
   std::wstring enclosure;

   std::map<std::wstring, XMLField*> fields;
	//std::vector<FileField*> files;
	XmlParser *parser;
};

typedef std::map<Object*, ServObject*> DataHolder;
class XMLBinder : public XMLBinderBase, public IXmlHandler
{
public:
	XMLBinder();
   ~XMLBinder();

   bool Prepare(const std::wstring& name, const std::wstring& enclosure, const ISessionObject& object, DataHolder* data);

   bool SetParent(XMLBinder* parent)
   {
      this->parent = parent;
      if( parent != NULL )
         parent->childs[(enclosure.empty()) ? name : enclosure] = this;
      return true;
   }

   bool Load();

   Object* Current() const { return (Object*)current; }

   bool MoveData(Object* dest, Object* src) const;

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

   virtual bool IsError() const { return false; }
   virtual const wchar_t* GetError() const { return L""; }

	void PutChildObject();

protected:
   bool loading;
   XMLBinder* parent;
   Object* current;
   DataHolder* data;

   std::map<std::wstring, XMLField*>::const_iterator curField;
   std::map<std::wstring, XMLBinder*> childs;
};

class XMLReaderBase : public IDataSource::IReader
{
public:
   XMLReaderBase(const ISessionObject& object);

   bool Open(const Params& params);

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual void AddChild(const std::wstring& childName, IReader* reader)
   {
      XMLCreator xc;
      if( childName.compare(xc.Name()) == 0 )
         childs.push_back((XMLReaderBase*)reader);
   }

   Object* GetCurObj() const
   { 
      return (current == data.end() || current->second->size() == 0 ) ? NULL : (Object*)current->second->front();
   }

	XmlParser *GetParser() { return this->parser; }

protected: 
   const ISessionObject& object;
   std::string fileName, archiveFolder;

   XMLReaderBase* parent;
   XMLBinder binder;

	XmlParser *parser;

   DataHolder data;
   DataHolder::iterator current;

   std::vector<XMLReaderBase*> childs;

   bool PrepareBinder(XMLBinder* parent);
};

class XMLReader : public XMLReaderBase
{
public:
   XMLReader(const ISessionObject& object);
	~XMLReader();

   virtual bool MoveNext(Object *parentObject);
   virtual void Remove();

protected: 
   bool prepared;
};

class XMLReaderChild : public XMLReaderBase
{
public:
   XMLReaderChild(const ISessionObject& object);
};

class XMLWriteBinder : public XMLBinderBase
{
public:
   const std::wstring& Enclosure() const { return enclosure; }

   bool Write(std::wstring* str, const Object& src);
};

class XMLWriterChild : public IDataSource::IWriter
{
public:
   XMLWriterChild();
   virtual ~XMLWriterChild();

   virtual bool Open(const Params& params, const ISessionObject& object);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid) {return true;}
   virtual void Close() {}

   bool Put(std::wstring* str, const Object& parent);

   virtual void AddChild(IWriter* writer, const std::wstring& typeName) 
   {
      XMLCreator xc;
      if( typeName.compare(xc.Name()) == NULL )
         xmlChilds.push_back((XMLWriterChild*)writer);
      else
         childs.push_back(writer); 
   }

protected:
   std::wstring tag;
   std::wstring enclosure;
   std::vector<XMLWriterChild*> xmlChilds;

   XMLWriteBinder binder;

   int childIndex;
};

class XMLWriter : public XMLWriterChild
{
public:
   XMLWriter();
   virtual ~XMLWriter();

   bool Open(const Params& params, const ISessionObject& object);

   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

public:
   bool started;
   std::wstring rootTag;

   std::string fileName;
   FILE *file;

   enum Encoding { UTF8, UTF16 };
   Encoding encoding;

   enum WriteMode { Overwrite, Append };
   WriteMode mode;

   void PutString(const std::wstring& str);
   FILE* OpenAppend();
};

class XMLFolderReader : public IDataSource::IReader
{
public:
   XMLFolderReader(const ISessionObject& object, IDataSource::IReader* child, bool autoID);
   ~XMLFolderReader() { Close(); }

   virtual void Remove() {}
   virtual void Close();
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return child->SetFilter(filter, object); }

   virtual bool MoveNext(Object *parentObject) { return child->MoveNext(parentObject); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return child->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return child->Value(name); }
   virtual void AddChild(const std::wstring& childName, IReader* reader) { child->AddChild(childName, reader); }

protected:
   IDataSource::IReader* child;

   bool autoID;
   mutable int curIndex;

   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder *folderHolder;
   ISession* session;
};

class XMLFolderWriter : public IDataSource::IWriter
{
public:
   XMLFolderWriter(IDataSource::IWriter *_child) : child(_child) {}
   ~XMLFolderWriter() { Close(); }

   virtual bool Prepare(const ISessionObject& object) { return child->Prepare(object); }
   virtual bool Write(const Object& o, RowID *rid) { return child->Write(o, rid); }
   virtual void Close() { child->Close(); }

   virtual void AddChild(IWriter* writer, const std::wstring& typeName) { child->AddChild(writer, typeName); }

   virtual void SetBaseFolder(const std::string& baseFolder) { child->SetBaseFolder(baseFolder); }

protected:
   IDataSource::IWriter *child;
};

//
//--------------------------------- Params ----------------------------------
//
bool Params::Load(const ParamList& parameters, const ISessionObject& object)
{
   bool res = false;

   CString *tname = NULL;
   const Parameter* tn = parameters.Find(TABLE_NAME_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      res = true;
      tableName.assign((const std::wstring&)*tname);
   }

   tn = parameters.Find(DOC_ELEMENT_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      docRoot.assign((const std::wstring&)*tname);
   }

   tn = parameters.Find(ROOT_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      rootTag.assign((const std::wstring&)*tname);
   } else
      rootTag = DEFAULT_ROOT_TAG;

   tn = parameters.Find(ENCLOSURE_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      enclosure.assign((const std::wstring&)*tname);
   }

   tn = parameters.Find(ENCODING_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      encoding.assign((const std::wstring&)*tname);
   }

   tn = parameters.Find(WRITE_MODE_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      writeMode.assign((const std::wstring&)*tname);
   }

   tn = parameters.Find(ARCHIVE_FOLDER_TAG, -1);
   if( tn != NULL && object.GetSession().Parse(&tname, tn->value, &object) )
   {
      archiveFolder.assign((const std::wstring&)*tname);
   }

   delete tname;
   return res;
}

//
//--------------------------------- XMLBinder ----------------------------------
//
class XMLStringField : public XMLField
{
public:
   XMLStringField(int _fieldIndex) : XMLField(_fieldIndex) {}

   virtual bool Set(Object* dest, const std::wstring& value) const
   {
      dest->at(fieldIndex).str->append(value);
      return true;
   }
   virtual bool MoveData(Object* dest, Object* src) const
   {
      Member& msrc = src->at(fieldIndex);
      dest->at(fieldIndex).str->assign(*msrc.str);
      msrc.str->clear();
      return true;
   }
   
   virtual bool Get(std::wstring* value, const Object& src) const
   {
      const std::wstring& s = (const std::wstring&)*src.at(fieldIndex).str;
      std::wstring::const_iterator i = s.begin();
      for( ; i != s.end(); i++ )
      {
         wchar_t sym = *i;
         switch(sym)
         {
		   case L'<': case L'>':
			   value->append(1, L'&');
			   value->append(1, (sym==L'<' ? L'l' : L'g'));
			   value->append(L"t;");	
			   break;

		   case L'&':
            value->append(L"&amp;");
			   break;

		   case L'\'': case L'\"': 
            value->append((sym == L'\'' ? L"&apos;" : L"&quot;"));
			   break;

         default:
            value->append(1, sym);
            break;
         }
      }
      //value->assign((const std::wstring&)*src.at(fieldIndex).str);
      return true;
   }
};

class XMLNumberField : public XMLField
{
public:
   XMLNumberField(int _fieldIndex, int _scale) : XMLField(_fieldIndex), scale(_scale) {}

   virtual bool Set(Object* dest, const std::wstring& value) const
   {
      std::wstring str;
      std::wstring::const_iterator i = value.begin();
      for( ; i != value.end(); i++ )
      {
        wchar_t sym = (*i);
        if( sym == L' ' )
           continue;
        if( sym == L',')
           sym = L'.';
        str.append(1, sym);
      }
      dest->at(fieldIndex).number = _wtof(str.c_str());
      return true;
   }

   virtual bool Get(std::wstring* value, const Object& src) const
   {
      char buf[200];
      double v = src.at(fieldIndex).number;
      if( scale == 0 )
         sprintf(buf, "%d", (int)v);
      else
         sprintf(buf, "%.*f", scale, v);

      USES_CONVERSION;
      value->assign(A2W(buf));
      return true;
   }

private:
   int scale;
};

class XMLDateField : public XMLField
{
public:
   XMLDateField(int _fieldIndex, MemberFormat::DateFormat dateFormat, const std::wstring& dataFormat) : 
      XMLField(_fieldIndex)
   {
      const wchar_t* format = 
         (!dataFormat.empty()) ? 
            dataFormat.c_str() : (dateFormat == MemberFormat::Date) ? 
            L"DD/MM/YYYY" : (dateFormat == MemberFormat::Time) ? 
            L"HH:mm:SS" :  
            L"DD/MM/YYYY HH:mm:SS";
      inited = parser.SetFormat(format);
   }

   virtual bool Set(Object* dest, const std::wstring& value) const
   {
      return (inited && parser.FromString(&dest->at(fieldIndex).datetime, value.c_str()));
   }

   virtual bool Get(std::wstring* value, const Object& src) const
   {
      return (inited && parser.ToString(value, src.at(fieldIndex).datetime));
   }

private:
   DateParser parser;
   bool inited;
};

XMLField* XMLField::Create(const IObjectData::Field& field, int idx)
{
   XMLField *ret = NULL;

   switch(field.format.type)
   {
   case MemberFormat::mtString:
      ret = new XMLStringField(idx);
      break;

   case MemberFormat::mtNumber:
      ret = new XMLNumberField(idx, field.format.format.fraction);
      break;

   case MemberFormat::mtDateTime:
      ret = new XMLDateField(idx, field.format.format.dateFormat, field.dataFormat);
      break;
   }

   return ret;
}

bool XMLField::MoveData(GRServer::Object *dest, GRServer::Object *src) const
{
   dest->at(fieldIndex) = src->at(fieldIndex);
   return true;
}

//
//--------------------------------- XMLBinder ----------------------------------
//
XMLBinder::XMLBinder() : current(NULL), parent(NULL), loading(false)
{
}

XMLBinder::~XMLBinder()
{
   delete current;
   current = NULL;

	//std::vector<FileField*>::iterator fi = files.begin();
	//for (; fi != files.end(); fi++)
	//	delete (*fi);
	//files.clear();
}

bool XMLBinder::MoveData(Object* dest, Object* src) const
{
   std::map<std::wstring, XMLField*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      i->second->MoveData(dest, src);

	//std::vector<FileField*>::const_iterator fi = files.begin();
	//for (; fi != files.end(); fi++)
	//	if (!(*fi)->ReadFile(dest))
	//		gServer->AddError(false, "Error while reading file");
	
	return true;
}

XMLBinderBase::~XMLBinderBase()
{
   std::map<std::wstring, XMLField*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      delete i->second;
   fields.clear();
}

bool XMLBinderBase::Prepare(const std::wstring& name, const std::wstring& enclosure, const ISessionObject& object)
{
   format = object.Self()->format;
   this->name = name;
   this->enclosure = enclosure;

   const IObjectData *od = object.GetObjectDef();
   if( od == NULL )
      return false;

   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
		int idx = format->FindMember(fi->format.name.c_str());
		if (idx < 0)
			continue;

		//if ((fi->flags & IObjectData::Field::File) != 0 && !fi->src.empty())
		//{
		//	int srcidx = format->FindMember(fi->src.c_str());
		//	if (srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString)
		//	{
		//		std::string folder;
		//		SetFileFieldBaseFolder(&folder, *fi, gServer->GetConfig());
		//		files.push_back(new FileField(srcidx, idx, folder.c_str(), gServer));
		//	}
		//	continue;
		//}

		if (fi->data.empty())
			continue;
      XMLField* f = XMLField::Create(*fi, idx);
      if( f == NULL )
         continue;
      fields[fi->data] = f;
   }

   return true;
}

bool XMLBinder::Prepare(const std::wstring& name, const std::wstring& enclosure, const ISessionObject& object, DataHolder* data)
{
   if( XMLBinderBase::Prepare(name, enclosure, object) == false )
      return false;

   this->data = data;
   return true;
}

bool XMLBinder::Load()
{
   delete current;
   current = Object::Create(*format);
   curField = fields.end();
   
   loading = (parent != NULL && (enclosure.empty() || name.empty()));
	parser->SetHandler(this);

   return true;
}

void XMLBinder::StartElement(const std::wstring& name, const Attributes& atts)
{
   if( (parent == NULL || !enclosure.empty()) && !loading )
   {
      loading = (this->name.compare(name) == 0);
      return;
   }

   curField = fields.find(name);
   if( curField == fields.end() )
   {
      std::map<std::wstring, XMLBinder*>::iterator fnd = childs.find(name);
      if( fnd != childs.end() )
      {
         fnd->second->Load();
      } else
      {
			parser->SetHandler(new SkipTagHandler(this, name.c_str()));
      }
   }
}

void XMLBinder::PutChildObject()
{
	Object* po = (parent == NULL) ? NULL : parent->Current();
	DataHolder::iterator fnd = data->find(po);
	if (fnd == data->end())
	{
		ServObject* so = new ServObject(format);
		so->push_back(current);
		(*data)[po] = so;
	}
	else
		fnd->second->push_back(current);

	current = Object::Create(*format);
	if (parent != NULL && enclosure.empty())
		parser->SetHandler(parent);
	else
		loading = name.empty();
}

void XMLBinder::EndElement(const std::wstring& name)
{
   if( enclosure.compare(name) == 0 )
   {
		if (parent->fields.size() == 0)
		{
			/* specal case insert whData to parts
			<parts>				<whData>					<weight>1.36</weight>					<weight>2.36</weight>					<weight>3.36</weight>				</whData>				<whData>					<weight>1.2</weight>					<weight>2.2</weight>					<weight>3.2</weight>				</whData>			</parts>
			*/
			parent->PutChildObject();
		}
		parser->SetHandler(parent);
   } else if( this->name.compare(name) == 0 )
   {
		PutChildObject();
   }
   curField = fields.end();
}

void XMLBinder::CharacterData(const std::wstring& value)
{
	if (curField != fields.end() && current != NULL)
	{
		curField->second->Set(current, value);
		
		// case one-field object
		if (parent != NULL && name.empty())
			PutChildObject();
	}
}

//
//--------------------------------- XMLReader ----------------------------------
//
XMLReader::XMLReader(const ISessionObject& _object) :
   XMLReaderBase(_object), prepared(false)
{
	parser = new XmlParser();
	binder.SetParser(parser);
}

XMLReader::~XMLReader()
{
	delete parser;
}

bool XMLReaderBase::Open(const Params& params)
{
   USES_CONVERSION;
   const ServerConfig &config = (const ServerConfig&)object.GetSession().Config();
   std::string tfile;

   tfile = W2A(params.tableName.c_str());
   tfile.insert(0, config.ExchangeFolder());
   FullFileName(&fileName, tfile.c_str());

	if (params.tableName.empty() == false)
	{
#ifdef UNIX
		glob_t gfnd;
		glob(fileName.c_str(), 0, NULL, &gfnd);
		if (gfnd.gl_pathc == 0)
			return false;
		globfree(&gfnd);
#else
		WIN32_FIND_DATAA fdata;
		string folder = fileName.substr(0, fileName.find_last_of('\\') + 1);
		HANDLE handle = FindFirstFileA(fileName.c_str(), &fdata);
		if (handle == INVALID_HANDLE_VALUE)
			return false;
		FindClose(handle);
#endif
	}

   tfile = W2A(params.archiveFolder.c_str());
   if( !tfile.empty() )
   {
      if( IsLocalName(tfile.c_str()) )
         tfile.insert(0, config.ExchangeFolder());
      FullFileName(&archiveFolder, tfile.c_str());
      if( *archiveFolder.rbegin() != '\\' )
         archiveFolder.append(1, L'\\');
   }

#ifdef UNIX
   ConvertPath(fileName, &fileName);
   ConvertPath(archiveFolder, &archiveFolder);
#endif
   return binder.Prepare(params.docRoot, params.enclosure, object, &data);
}

bool XMLReader::MoveNext(Object *parentObject)
{
   if( !prepared )
   {
      PrepareBinder(NULL);

#ifdef UNIX
      glob_t gfnd;
      glob(fileName.c_str(), 0, NULL, &gfnd);
      if( gfnd.gl_pathc == 0 )
         return false;
      globfree(&gfnd);
#else
      WIN32_FIND_DATAA data;
      string folder = fileName.substr(0, fileName.find_last_of('\\') + 1);
      HANDLE handle = FindFirstFileA(fileName.c_str(), &data);
      if( handle == INVALID_HANDLE_VALUE )
         return false;

      do
      {
         std::string tfile = folder + data.cFileName;
         binder.Load();
         parser->Parsing(tfile, &binder);
      } while( FindNextFileA(handle, &data) );
      FindClose(handle);
#endif

      prepared = true;
   }

   return XMLReaderBase::MoveNext(parentObject);
}

bool XMLReaderBase::PrepareBinder(XMLBinder* parent)
{
   std::vector<XMLReaderBase*>::iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
   {
      (*i)->PrepareBinder(&binder);
   }

   if( parent != NULL )
      binder.SetParent(parent);

   return true;
}

void XMLReader::Remove()
{
#ifdef UNIX
#error "Not implemented"
#else
   if( archiveFolder.empty() == false )
   {
      USES_CONVERSION;
      SHCreateDirectory(NULL, A2W(archiveFolder.c_str()));
   }

   WIN32_FIND_DATAA data;
   string folder = fileName.substr(0, fileName.find_last_of('\\') + 1);
   HANDLE handle = FindFirstFileA(fileName.c_str(), &data);
   if( handle == INVALID_HANDLE_VALUE )
      return;

   do
   {
      std::string tfile = folder + data.cFileName;
      if( archiveFolder.empty() )
         _unlink(tfile.c_str());
      else
      {
         std::string newName(archiveFolder);
         newName += data.cFileName;

         MoveFileExA(tfile.c_str(), newName.c_str(), MOVEFILE_COPY_ALLOWED | MOVEFILE_REPLACE_EXISTING);
      }
   } while( FindNextFileA(handle, &data) );

   FindClose(handle);
#endif
      
}

//
//---------------------------- XMLReaderBase --------------------------------
//
XMLReaderBase::XMLReaderBase(const GRServer::ISessionObject &_object) :
   object(_object), parent(NULL)
{
   current = data.end();
}

bool XMLReaderBase::MoveNext(Object *parentObject)
{
   Object* par = (parent == NULL) ? NULL : parent->GetCurObj();
   if( current == data.end() || current->first != par )
   {
      current = data.find(par);
      return ( current != data.end() && current->second->size() > 0 );
   }
   ServObject *curO = current->second;
   if( curO->size() > 0 )
   {
      Object* o = curO->front();
      delete o;

      curO->front() = NULL;
      curO->erase(curO->begin());
   }
   return (curO->size() > 0);
}

bool XMLReaderBase::Get(Object* o) const
{
   if( current == data.end() || current->second->size() == 0 )
      return false;

   return binder.MoveData(o, current->second->front());
}

void XMLReaderBase::Close()
{
   DataHolder::iterator i = data.begin();
   for( ; i != data.end(); i++ )
      delete i->second;
   data.clear();
}

const MemberFormat* XMLReaderBase::Type(const wchar_t* name) const
{
   return NULL;
}

const Member* XMLReaderBase::Value(const wchar_t* name) const
{
   return NULL;
}

//
//---------------------------- XMLReaderChild --------------------------------
//
XMLReaderChild::XMLReaderChild(const ISessionObject& object) :
   XMLReaderBase(object)
{
   ISessionObject* parent = object.Parent();
   XMLCreator xc;
   ObjectSource* source;
   if( (parent != NULL) && ((source = parent->GetSource()) != NULL) && (source->readerName.compare(xc.Name()) == 0) )
   {
      source->reader->AddChild(xc.Name(), this);
      this->parent = (XMLReaderBase*)source->reader;
		this->parser = ((XMLReaderBase*)this->parent)->GetParser();
		binder.SetParser(this->parser);
   }
}

//
//--------------------------------- XMLWriteBinder ----------------------------------
//
bool XMLWriteBinder::Write(std::wstring* str, const Object& src)
{
   std::map<std::wstring, XMLField*>::const_iterator fi = fields.begin();
   for( ; fi != fields.end(); fi++ )
   {
      std::wstring value;
      fi->second->Get(&value, src);

      (*str) += L"  ";
      (*str) += L"<"; (*str) += fi->first; (*str) += L">";
      (*str) += value;
      (*str) += L"</"; (*str) += fi->first; (*str) += L">"; (*str) += L"\n";
   }
   return true;
}

//
//--------------------------------- XMLWriterChild ----------------------------------
//
XMLWriterChild::XMLWriterChild() : childIndex(-1)
{
}

XMLWriterChild::~XMLWriterChild()
{
}

bool XMLWriterChild::Open(const Params& params, const ISessionObject& object)
{
   tag = params.docRoot;
   enclosure = params.enclosure;

   const ISessionObject* parentObj = object.Parent();
   if( parentObj != NULL )
   {
      const std::wstring& oname = object.Self()->format->name;
      size_t off = oname.find_last_of(L'$');
      childIndex  = ((const SessionObject*)parentObj->Self())->format->FindMember(oname.substr(off+1).c_str());
   }
   return true;
}

bool XMLWriterChild::Put(std::wstring* _str, const Object& parent)
{
   std::wstring& str = *_str;

   if( childIndex >= 0 )
   {
      ServObject *so = parent.at(childIndex).object;
      if( so == NULL || so->size() == 0 )
      {
         const std::wstring& tag = (binder.Enclosure().empty()) ? binder.Name() : binder.Enclosure();
         str += L"<"; str += tag; str += L"/>"; str += L"\n";
      } else
      {
         if(!binder.Enclosure().empty())
         {
            str += L"<"; str += binder.Enclosure(); str += L">"; str += L"\n";
         }

         ServObject::const_iterator i = so->begin();
         for( ; i != so->end(); i++ )
         {
				if (!binder.Name().empty())
				{
					str += L"<"; str += binder.Name(); str += L">"; str += L"\n";
				}

            binder.Write(&str, *(*i));

            std::vector<XMLWriterChild*>::iterator ci = xmlChilds.begin();
            for( ; ci != xmlChilds.end(); ci++ )
               (*ci)->Put(&str, *(*i));

				if (!binder.Name().empty())
				{
					str += L"</"; str += binder.Name(); str += L">"; str += L"\n";
				}
         }
         if(!binder.Enclosure().empty())
         {
            str += L"</"; str += binder.Enclosure(); str += L">"; str += L"\n";
         }
      }
   }
   return true;
}

bool XMLWriterChild::Prepare(const ISessionObject& object)
{
   return binder.Prepare(tag, enclosure, object);
}

//
//--------------------------------- XMLWriter ----------------------------------
//
XMLWriter::XMLWriter() : started(false), file(NULL), encoding(UTF8), mode(Overwrite)
{
}

bool XMLWriter::Open(const Params& params, const ISessionObject& object)
{
   if( !XMLWriterChild::Open(params, object) )
      return false;

   USES_CONVERSION;
   fileName = W2A(params.tableName.c_str());

   if( params.encoding.empty() == false )
   {
      const wchar_t* pstr = params.encoding.c_str(); 
      encoding = (_wcsicmp(pstr, L"UTF8") == 0 || _wcsicmp(pstr, L"UTF-8") == 0) ? UTF8 : UTF16;
   }

   if( params.writeMode.empty() == false )
   {
      const wchar_t* pstr = params.writeMode.c_str(); 
      mode = (_wcsicmp(pstr, L"APPEND") == 0) ? Append : Overwrite;
   }

   rootTag = params.rootTag;

   const ServerConfig &config = (const ServerConfig&)object.GetSession().Config();
   fileName.insert(0, config.ExchangeFolder());
#ifdef UNIX
   ConvertPath(fileName, &fileName);
#endif

   return true;
}

XMLWriter::~XMLWriter()
{
   Close();
}

static bool SkipTo(const wchar_t* sym, FILE *f)
{
   const wchar_t *sp = sym;
   while( *sp != L'\0' )
   {
      wchar_t cs = fgetwc(f);
      if( cs == WEOF )
         return false;
      if( cs == *sp )
         sp++;
      else
         sp = sym;
   }

   return true;
}

static bool ReadTag(std::wstring* value, FILE *f)
{
   while( true )
   {
      wchar_t cs = fgetwc(f);
      if( cs == WEOF )
         return false;
      if( cs == L'>' )
         break;
      if( cs == L' ' )
      {
         if( value->empty() )
            continue;
         return SkipTo(L">", f);
      }
      value->append(1, cs);
   }
   return true;
}

static bool ReadRootTag(std::wstring* tag, FILE *f)
{
   while( true )
   {
      wchar_t sym = fgetwc(f);
      if( sym == WEOF )
         return false;
      if( sym == L'?' )
      {
         sym = fgetwc(f);
         if( sym == L'>' )
         {
            // read tag
            if( !SkipTo(L"<", f) )
               return false;
            
            tag->clear();
            ReadTag(tag, f);
            break;
         }
      }
   }

   return true;
}

static bool MoveToEnd(const std::wstring& tag, bool utf8, FILE *f)
{
   std::wstring check(L"</");
   check += tag; check += L">";

   if( !SkipTo(check.c_str(), f) )
      return false;

   long pos = ftell(f);
   if( utf8 )
   {
      USES_CONVERSION;
      const char *p = W2A_CP(check.c_str(), CP_UTF8);
      pos -= (long)strlen(p);
   } else
   {
      // в UTF16 все должно быть четно
      if( (pos % 2) != 0 )
         pos--;
      pos -= (long)(check.size() * sizeof(short));
   }
   fseek(f, (long)pos, SEEK_SET);
   return true;
}

FILE* XMLWriter::OpenAppend()
{
   FILE *f = fopen(fileName.c_str(), "rt");
   if( f != NULL )
   {
      int sym1 = fgetc(f);
      int sym2 = fgetc(f);
      encoding = ( sym1 == 0xFF && sym2 == 0xFE ) ? UTF16 : UTF8;
      fclose(f);

      f = fopen(fileName.c_str(), (encoding==UTF8) ? "r+t,ccs=UTF-8" : "r+t,ccs=UTF-16LE");
      if( !ReadRootTag(&rootTag, f) )
      {
         fclose(f);
         f = NULL;
      } else 
      {
         if( !MoveToEnd(rootTag, (encoding==UTF8), f) )
         {
            fclose(f);
            f = NULL;
         }
      }
   }

   return f;
}

bool XMLWriter::Write(const Object& o, RowID *rid)
{
   std::wstring str;
   if( !started )
   {
      if( mode == Append && IsFileExists(fileName) )
      {
         file = OpenAppend();
         if( file == NULL )
         {
            gServer->AddLog(IErrorLogger::Short, "Can't open file %s, for appending", fileName.c_str());
            return false;
         }
      }
      else
      {
         file = fopen(fileName.c_str(), (encoding==UTF8) ? "wt,ccs=UTF-8" : "wt,ccs=UTF-16LE");
         if( file == NULL )
         {
            gServer->AddLog(IErrorLogger::Short, "Can't open file %s, for writing", fileName.c_str());
            return false;
         }
         str = L"<?xml version=\"1.0\" encoding=\""; str += (encoding==UTF8) ? L"UTF-8" : L"UTF-16"; str += L"\"?>\n<"; str += rootTag; str += L">\n";
      }
      started = true;
   }

   str += L"<"; str += binder.Name(); str += L">"; str += L"\n";

   binder.Write(&str, o);

   std::vector<XMLWriterChild*>::iterator xi = xmlChilds.begin();
   for( ; xi != xmlChilds.end(); xi++ )
      (*xi)->Put(&str, o);

   str += L"</"; str += binder.Name(); str += L">"; str += L"\n";

   PutString(str);

   WriterList::iterator ci = childs.begin();
   for( ; ci != childs.end(); ci++ )
      (*ci)->Write(o, NULL);

   return true;
}

void XMLWriter::PutString(const std::wstring& str)
{
   fputws(str.c_str(), file);
   //USES_CONVERSION;
   //if( encoding == UTF8 )
   //{
   //   fputws(W2A_CP(str.c_str(), CP_UTF8), file);
   //} else
   //{
   //   USES_WCONVERSION;
   //   fputws(W32_16(str.c_str()), file);
   //}
}

void XMLWriter::Close()
{
   if( file != NULL )
   {
      std::wstring str;
      str = L"</"; str += rootTag; str += L">";

      PutString(str);

      fclose(file);
      file = NULL;
   }
}

//
//----------------------- XMLCreator --------------------------------
//
IDataSource::IReader* XMLCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   Params p;
   p.Load(parameters, object);

   XMLReaderBase *ret = NULL;
   if( object.Parent() != NULL )
   {
      ret = new XMLReaderChild(object);
   } else
   {
      ret = new XMLReader(object);
   }
   if( !ret->Open(p) )
   {
      //USES_CONVERSION;
      //gServer->AddLog("Can't open XML %s", W2A(p.tableName.c_str()));

      delete ret;
      ret = NULL;
   }
   return ret;
}

IDataSource::IWriter*  XMLCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   Params p;
   p.Load(parameters, object);
   XMLWriterChild *ret;
   if( object.Parent() != NULL )
   {
      ret = new XMLWriterChild();
   } else
   {
      ret = new XMLWriter();
   }

   if( !ret->Open(p, object) )
   {
      delete ret;
      ret = NULL;
   }
   return ret;
}


//
//-------------------------------------- XMLFolderReader ----------------------------------------------
//
XMLFolderReader::XMLFolderReader(const ISessionObject& object, IDataSource::IReader* _child, bool autoID) :
   child(_child), curIndex(1)
{
   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);

   GRServer::Format* f = object.Self()->format;
   keyIndex = f->FindMember(L"fid");
   valueIndex = f->FindMember(L"id");

   session = &object.GetSession();

   this->autoID = autoID;
}

void XMLFolderReader::Close()
{
   if( child != NULL )
   {
      delete child;
      child = NULL;
   }
}

bool XMLFolderReader::Get(Object* o) const
{
   if( !child->Get(o) )
      return false;

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
   {
      if( autoID )
      {
         o->at(valueIndex).number = curIndex++;
      }
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   }
   return true;
}

//
//----------------------- XMLFolderCreator --------------------------------
//
IDataSource::IReader* XMLFolderCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   Params p;
   p.Load(parameters, object);

   IDataSource::IReader *ret = NULL;
   if( object.Parent() == NULL )
   {
      XMLReaderBase *child = new XMLReader(object);
      if( child->Open(p) )
         ret = new XMLFolderReader(object, child, true);
      else
         delete child;
   }
   return ret;
}

IDataSource::IWriter*  XMLFolderCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   Params p;
   p.Load(parameters, object);
   IDataSource::IWriter *ret;
   if( object.Parent() == NULL )
   {
      XMLWriter *child = new XMLWriter();
      if( child->Open(p, object) )
         ret = new XMLFolderWriter(child);
      else
         delete child;
   }

   return ret;
}

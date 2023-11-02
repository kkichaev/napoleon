#include "gtkservice.h"
#include "controls.h"

//#define XML_STATIC
//#include "expat\expat.h"
//
//class BaseLoader
//{
//public:
//	BaseLoader(BaseLoader* parent, XML_Parser parser, const char* endElement)
//	{
//		this->parent = parent;
//		this->parser = parser;
//		this->endElement = endElement;
//	}
//	~BaseLoader() {}
//
//	virtual void OnEndElement(const char* element)
//	{
//		if (strcmp(element, endElement) == 0)
//		{
//			if (parent != NULL)
//			{
//				XML_SetUserData(parser, parent);
//				delete this;
//			}
//		}
//	}
//
//	virtual void OnStartElement(const char* element, const char** atts)
//	{
//	}
//
//	virtual void OnCharData(const std::string& value)
//	{
//	}
//
//protected:
//	BaseLoader* parent;
//	XML_Parser parser;
//	const char* endElement;
//};
//
//class ControlLoader : public BaseLoader
//{
//public:
//	ControlLoader(BaseLoader* parent, XML_Parser parser, const char* endElement, const std::string& type) : BaseLoader(parent, parser, endElement), control(NULL)
//	{
//		this->controlType = type;
//	}
//	virtual ~ControlLoader() {}
//
//	static ControlLoader* Create(BaseLoader* parent, XML_Parser parser, const char* endElement, const std::string& type) { return new ControlLoader(parent, parser, endElement, type); }
//
//	Control* BindControl(Control* ctrl) 
//	{ 
//		if (ctrl == NULL)
//			this->control = CreateControl();
//		else
//			this->control = ctrl;
//		return this->control;
//	}
//
//	virtual void OnStartElement(const char* element, const char** atts)
//	{
//		if (strcmp(element, "property") == 0 && GetAttribute(&curElement, "name", atts))
//			return;
//		curElement = element;
//	}
//
//	virtual void OnCharData(const std::string& value)
//	{
//		if (!curElement.empty())
//		{
//			control->SetProperty(curElement.c_str(), value.c_str());
//			curElement.clear();
//		}
//	}
//
//	bool GetAttribute(std::string* value, const char* name, const char** atts)
//	{
//		if (atts == NULL)
//			return false;
//
//		for (int i = 0; atts[i]; i++)
//		{
//			if (strcmp(atts[i++], name) == 0)
//			{
//				value->assign(atts[i]);
//				return true;
//			}
//		}
//		return false;
//	}
//
//protected:
//	virtual Control* CreateControl() { return Control::Create(controlType); }
//
//protected:
//	Control* control;
//	std::string curElement;
//	std::string controlType;
//};
//
//class ScreenLoader : public ControlLoader
//{
//public:
//	ScreenLoader(XML_Parser parser, const std::string& deviceID) : ControlLoader(NULL, parser, "screen", "screen")
//	{
//		this->deviceID = deviceID;
//	}
//
//	virtual Control* CreateControl() { return new Screen(deviceID); }
//
//	virtual void OnStartElement(const char* element, const char** atts)
//	{
//		if (strcmp(element, "screen") == 0)
//		{
//			GetAttribute(&control->name, "name", atts);
//		} else if (strcmp(element, "control") == 0)
//		{
//			std::string type;
//			if (GetAttribute(&type, "type", atts))
//			{
//				std::string name;
//				GetAttribute(&name, "name", atts);
//				ControlLoader* loader = ControlLoader::Create(this, parser, element, type);
//				if (loader != NULL)
//				{
//					Control* ctrl = loader->BindControl(((Screen*)control)->FindControl(name));
//					ctrl->name = name;
//					((Screen*)control)->AddControl(ctrl);
//
//					XML_SetUserData(parser, loader);
//				}
//			}
//			else
//			{
//				BaseLoader* loader = new BaseLoader(this, parser, element);
//				XML_SetUserData(parser, loader);
//			}
//		}
//		else
//			ControlLoader::OnStartElement(element, atts);
//	}
//
//	std::string deviceID;
//};
//
//static void __cdecl StartElementHandler(BaseLoader* loader, const char *name, const char **atts)
//{
//	loader->OnStartElement(name, atts);
//}
//
//static void __cdecl EndElementHandler(BaseLoader* loader, const char *name)
//{
//	loader->OnEndElement(name);
//}
//
//static void __cdecl CharacterDataHandler(BaseLoader* loader, const char *name, int len)
//{
//	std::string value(name, len);
//	loader->OnCharData(value);
//}

DeviceData::DeviceData() : screen(NULL) {
    requestConnect = false;
    
//    deviceSocket = -1;
    lastConnect.dwHighDateTime = 0;
    lastConnect.dwLowDateTime = 0;
    
    screen = new Screen(this);
}

DeviceData::~DeviceData() {
    if (screen != NULL)
        delete screen;
}

bool DeviceData::RenderScreen(DataBuffer* out) const {
    bool ret = false;
    ret = screen->Render(out);
            
    //	XML_Parser parser = XML_ParserCreate(NULL);
    //	if (parser == NULL)
    //	{
    //		return ret;
    //	}
    //
    //	FILE* f = fopen("test.xml", "rt");
    //	if (f != NULL)
    //	{
    //		std::string src;
    //		char buf[1000];
    //		while (true)
    //		{
    //			int rd = fread(buf, sizeof(char), sizeof(buf), f);
    //			if (rd <= 0)
    //				break;
    //
    //			src.append(buf, rd);
    //		}
    //		fclose(f);
    //
    //
    //		ScreenLoader *handler = new ScreenLoader(parser, id);
    //		screen = (Screen*)handler->BindControl(screen);
    //
    //		XML_SetElementHandler(parser, (XML_StartElementHandler)StartElementHandler, (XML_EndElementHandler)EndElementHandler);
    //		XML_SetCharacterDataHandler(parser, (XML_CharacterDataHandler)CharacterDataHandler);
    //		XML_SetUserData(parser, handler);
    //
    //		bool retVal = (XML_Parse(parser, src.c_str(), src.size(), true) != XML_STATUS_ERROR);
    //		XML_ParserFree(parser);
    //
    //		delete handler;
    //
    //		ret = screen->Render(out);
    //	}

    return ret;
}

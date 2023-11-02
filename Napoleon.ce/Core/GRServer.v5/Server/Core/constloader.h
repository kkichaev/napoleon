#ifndef _CONST_LOADER_H
#define _CONST_LOADER_H

#include <string>
#include <token.h>
#include "xml.h"

namespace GRServer {
struct IXmlHandler;

class ConstLoader 
{
public:
	static void Load(IXmlHandler* prevHandler, const GRServer::IXmlHandler::Attributes& atts);
	//static void ResolveConsts();

	static bool CheckConst(const std::wstring& text, short* val);
	static bool CheckConst(const std::wstring& text, unsigned short* val) { return CheckConst(text, (short*)val); }

	static bool CheckConst(const std::wstring& text, int* val);
	static bool CheckConst(const std::wstring& text, unsigned* val) { return CheckConst(text, (int*)val); }

#ifdef UNIX
#else
	static bool CheckConst(const std::wstring& text, long* val);
#endif
	static bool CheckConst(const std::wstring& text, unsigned long* val) { return CheckConst(text, (long*)val); }

	static bool CheckConst(const std::wstring& text, __int64* val);
	static bool CheckConst(std::wstring* val);
};

};

#endif
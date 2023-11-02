/*
 * Copyright (C), 2009-2011, Денис Мосягин
 *
 * modify tag of objdef 
 *
 * ert   13/07/2011   creating
 */
#ifndef _CHANGE_OBJ_DEF_H
#define _CHANGE_OBJ_DEF_H

#include "xml.h"
#include "objdef.h"

namespace GRServer {

void LoadFeature(IXmlHandler *prevHandler, const IXmlHandler::Attributes& atts);

void LoadModifyData(ObjectDef::ObjectSet* objects, IXmlHandler *prevHandler, const IXmlHandler::Attributes& atts, const std::wstring& tag);

void UpdateObjectDef(ObjectDef::ObjectSet* objects);

} //namespace GRServer


#endif
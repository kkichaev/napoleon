/*
 * Copyright (C), 2009 - 2011, Денис Мосягин
 *
 * Фичи 
 *
 * ert   20/07/2011   creating
 */
#ifndef __GRSERVER_FTR_H
#define __GRSERVER_FTR_H

#include <string>

#include "token.h"

namespace GRServer {

bool LoadFeatures(const std::string& fileName);

bool HaveFeature(const std::wstring& ftrExpr);

bool GetFeatureValue(Token* res, const std::wstring& feature);

} // namespace GRServer

#endif
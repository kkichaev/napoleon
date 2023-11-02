/*
* Copyright (C), 2007-2010, Денис Мосягин
*
* Comobox для папки фото
* 
*  ert   19/05/2010   creating
*/ 
#ifndef __PHOTO_FOLDER_H
#define __PHOTO_FOLDER_H

struct Preference;

void LoadFolderData(CWindow& parent, UINT id);
void StoreFolderData(CWindow& parent, UINT id, Preference* p);

#endif
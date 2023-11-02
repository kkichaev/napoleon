#ifndef __ADD_H
#define __ADD_H

#define SECOND_FOLDER "Papki.dbf"
#define SECOND_PRICE  "Tovar.dbf"

void RecodeFoldersID(const char *folder, std::set<DWORD> *src, std::map<DWORD, DWORD> *recode, bool loadFolders);

#endif 

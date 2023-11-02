/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * пакет обновления
 *
 * ert   07/12/2009   creating
 */
#ifndef __UPD_PACKET_H
#define __UPD_PACKET_H

#include <Binary.h>
#include <AES.h>

#include <string>
#include <vector>

const char UPDATE_PROG[] = "GRUpdate.exe";

class UpdatePacket
{
public:
   struct UpdateFileData
   {
      std::string name;

      bool Save(FILE *file) const;
      bool Load(FILE *file);
   };

   class FileList : public std::vector<UpdateFileData>
   {
   public:
      FileList() {}

      std::string folder;

      bool Save(FILE* file) const;
      bool Load(FILE* file);
   };

   UpdatePacket();
   ~UpdatePacket();

   bool DecodeHead(const IBinary& binaryData);
   bool DecodeBody(const IBinary& binaryData, const std::string& fileFolder);
   Binary* DecodeFile(const IBinary& binaryData, int index);

   bool HaveFile(const std::string& name);
   bool MoveFile(const std::string& name, const std::string& destFolder);

   // if false all changes rollbacked
   bool MoveFiles(const std::string& destFolder, std::string *failedFile = NULL);

   bool Save(FILE* file) const;
   bool Load(FILE* file);

   const std::string& Category() const { return category; }
   const std::string& Version() const { return version; }
   const std::string& FilesFolder() const { return files.folder; }

protected:
   std::string category;
   std::string version;

   FileList files;
};



#endif

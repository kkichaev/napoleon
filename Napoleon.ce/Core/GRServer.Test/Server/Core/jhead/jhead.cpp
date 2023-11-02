#include "stdafx.h"

#include "jhead.h"

#include "srvutility.h"

void ErrNonfatal(char * msg, int a1, int a2)
{
}

void ErrFatal(char * msg)
{
}

void FileTimeAsString(char * TimeStr)
{
    struct tm ts;
    ts = *localtime(&ImageInfo.FileDateTime);
    strftime(TimeStr, 20, "%Y:%m:%d %H:%M:%S", &ts);
}

int DumpExifMap  = FALSE;
int ShowTags = FALSE;

void GRServer::JPEGAddComment(const char* fileName, const char* comment)
{
   ResetJpgfile();

   // Start with an empty image information structure.
   memset(&ImageInfo, 0, sizeof(ImageInfo));
   ImageInfo.FlashUsed = -1;
   ImageInfo.MeteringMode = -1;
   ImageInfo.Whitebalance = -1;
   strncpy(ImageInfo.FileName, fileName, PATH_MAX);

   ReadJpegFile(fileName, READ_ALL);

   create_EXIF(comment);

   //int len = strlen(comment);
   //if( len > sizeof(ImageInfo.Comments) )
   //   len = sizeof(ImageInfo.Comments);
   //strncpy(ImageInfo.Comments, comment, len);
   //ImageInfo.CommentWidthchars = len;

   WriteJpegFile(fileName);
   DiscardData();
}

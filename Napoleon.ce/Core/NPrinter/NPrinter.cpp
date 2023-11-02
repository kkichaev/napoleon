/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Интерфейс печати
 *
 *  ert   09/05/2008   creating
 */ 
#include "stdafx.h"
#include "NPrinter.h"
#include <projects.h>

//#define DEBUG_FANTOM 1

#ifdef DEBUG
#define TEST_BMP 1
#endif

HINSTANCE hInst;

//
//--------------------------------------- NPlatform ------------------------------
//
NPlatform::NPlatform(IConnect *connect, PrinterType prnType) : cancel(false), printType(prnType)
{
   this->connect = connect;

   //InitDeviceComm();
   IOMode.bDevID = TRUE;
   strcpy(strModel, prnType);
   strPens[0] = '\0';
#ifdef APDK_DJGENERICVIP
   VIPVersion = 3;
#else
   VIPVersion = 0;
#endif
//   VIPVersion = 0;
}

NPlatform::~NPlatform()
{
   if( connect != NULL )
      delete connect;
}

void NPlatform::DisplayPrinterStatus (DISPLAY_STATUS ePrinterStatus)
{
}

DRIVER_ERROR NPlatform::BusyWait(DWORD msec)
{
   //Sleep(msec);
   return (cancel) ? JOB_CANCELED : NO_ERROR;
}

DRIVER_ERROR NPlatform::ReadDeviceID(BYTE* strID, int iSize)
{
#ifdef DEBUG_FANTOM
   strncpy((char*)strID, "deskjet 3320", iSize);
#else
   strncpy((char*)strID, printType, iSize);
#endif
   return NO_ERROR;
}

BOOL NPlatform::GetStatusInfo(BYTE* bStatReg)
{
   return FALSE;
}

DRIVER_ERROR NPlatform::ToDevice(const BYTE* pBuffer, DWORD* dwCount)
{
#ifndef TEST_BMP
   if( connect->Write(pBuffer, dwCount) == false )
         return SYSTEM_ERROR;
#else
   *dwCount = 0;
#endif

   return NO_ERROR;
}

DRIVER_ERROR NPlatform::FromDevice(BYTE* pReadBuff, DWORD* wReadCount)
{
   if( connect->Read(pReadBuff, wReadCount) == false )
      return SYSTEM_ERROR;

   return NO_ERROR;
}

void NPlatform::CancelJob()
{
   cancel = true;

   connect->Close();
}

//
//--------------------------------------- NPrinter ------------------------------
//
NPrinter::NPrinter() : printer(NULL), printContext(NULL), job(NULL), startPrinting(false), line(NULL), hbmp(NULL)
{
}

NPrinter::~NPrinter()
{
   Close();
}

void NPrinter::Close()
{
   if( job != NULL )
   {
      if( startPrinting )
         job->NewPage();

      delete job;
      job = NULL;
   }

   if( printContext != NULL )
   {
      delete printContext;
      printContext = NULL;
   }

   if( printer != NULL )
   {
      printer->EndPrint();

      delete printer;
      printer = NULL;
   }

   if( line != NULL )
   {
      delete line;
      line = NULL;
   }

   if( hbmp != NULL )
   {
      DeleteObject(hbmp);
      hbmp = NULL;
   }
}

PrinterType NPrinter::printerTypes[] = 
{
   "HP DeskJet 4",
   "DESKJET 6",
   "DESKJET 91",
   "HP LaserJet",
   "HP LaserJet M1005",
   "hp LaserJet 1010",
   "deskjet 3320",
   "deskjet 3600",
   "Deskjet D4100",
   "Deskjet D26",
   "deskjet 5100",
   "DESKJET 96",
   "DJ55xx",
};

char* NPrinter::printerDesc[] =
{
   "HP DeskJet 400 - 499",
   "HP DeskJet 600 - 699",
   "HP DJ 9xx, 1120-1125,1220-1280,3810-3822",
   "HP LaserJet 4 - 6",
   "HP M1005,M1120,M1319,P1005-P1008,P1505",
   "HP LaserJet 1000-1022",
   "DJ 332x,342x,3528-35,3740,3900-3940",
   "DJ 3600,3740,3745,3840,3845",
   "DJ D4100,D4106,D42,D43",
   "DJ D16,D26",
   "DJ 698x",
   "DJ 6122",
   "Officejet H470",
};

PrinterType* NPrinter::GetPrinterTypes(int *count)
{
   if( count != NULL )
      *count = sizeof(printerTypes)/sizeof(printerTypes[0]);
   return printerTypes;
}

char**  NPrinter::GetPrinterDesc(int *count)
{
   if( count != NULL )
      *count = sizeof(printerDesc)/sizeof(printerDesc[0]);
   return printerDesc;
}

bool NPrinter::Connect(const ConnectData& data, PrinterType prnType)
{
#ifdef DEBUG_FANTOM
   IConnect *c = new BTConnection();
#else
   IConnect *c = GetConnection(data.connectID);
#endif
   if( c == NULL )
      return false;

   bool ret = c->Connect(data);
   if( !ret )
      return false;
/*
   wchar_t buf[MAX_PATH], *p;
   GetModuleFileName(NULL, buf, MAX_PATH);
   p = wcsrchr(buf, L'\\');
   wcscpy(p+1, L"logwr");
   FILE *f = _wfopen(buf, L"rb");
   if( f != NULL )
   {
      fseek(f, 0, SEEK_END);
      DWORD sz = ftell(f);

      BYTE *buf = (BYTE*)malloc(sz);
      fread(buf, sz, 1, f);

      c->Write(buf, &sz);

      free(buf);
      fclose(f);

      return false;
   }
*/
   printer = new NPlatform(c, prnType);
   if( printer->constructor_error > NO_ERROR )
   {
      delete printer;
      return false;
   }
   return true;
}

BYTE* SetByte(BYTE *dest, BYTE val)
{
   if( val )
   {
      *dest++ = 0xFF;
      *dest++ = 0xFF;
      *dest++ = 0xFF;
   } else
   {
      *dest++ = 0;
      *dest++ = 0;
      *dest++ = 0;
   }
   return dest;
}


void CopyLine(BYTE *dest, BYTE *src, DWORD width)
{
   while( width > 0 )
   {
      dest = SetByte(dest, *src & 0x80);
      if( width > 1 ) dest = SetByte(dest, *src & 0x40);
      if( width > 2 ) dest = SetByte(dest, *src & 0x20);
      if( width > 3 ) dest = SetByte(dest, *src & 0x10);
      if( width > 4 ) dest = SetByte(dest, *src & 0x8);
      if( width > 5 ) dest = SetByte(dest, *src & 0x4);
      if( width > 6 ) dest = SetByte(dest, *src & 0x2);
      if( width > 7 ) dest = SetByte(dest, *src & 0x1);
      width -= 8;
      src++;
   }
}

void CopyAlbumLine(BYTE *dest, BYTE *src, DWORD emptyLines, DWORD lines, DWORD lineWidth, WORD pixel)
{
   DWORD i;
   for( i=0; i<emptyLines; i++ )
   {
      dest = SetByte(dest, 1);
      src += lineWidth;
   }

   BYTE mask = (0x80 >> pixel);

   for( ; i<lines; i++ )
   {
      dest = SetByte(dest, *src & mask);
      src += lineWidth;
   }
}

#ifdef TEST_BMP

void SaveToFile(const wchar_t *fileNameW, HBITMAP hb, BYTE *bits)
{
   int len = wcslen(fileNameW) + 1;
   char *fileName = (char*)alloca(len);
   wcstombs(fileName, fileNameW, len);

   FILE *wr = fopen(fileName, "wb");

   if( wr == NULL ) return;

   char buf[sizeof(BITMAPINFOHEADER) + sizeof(RGBQUAD) * 2];
   BITMAPINFOHEADER *bmi = (BITMAPINFOHEADER*)buf;
   BITMAPFILEHEADER bmfh;
   BITMAP bmp;

   GetObject(hb, sizeof(bmp), &bmp);

   LONG imageSize = bmp.bmWidthBytes * bmp.bmHeight;

   bmi->biSize = sizeof(*bmi);
   bmi->biWidth = bmp.bmWidth;
   bmi->biHeight = bmp.bmHeight;
   bmi->biPlanes = 1;
   bmi->biBitCount = 1;
   bmi->biCompression = BI_RGB;
   bmi->biSizeImage = imageSize;
   bmi->biXPelsPerMeter = 300;
   bmi->biYPelsPerMeter = 300;
   bmi->biClrUsed = 2;
   bmi->biClrImportant = 0;

   ((BITMAPINFO*)bmi)->bmiColors[0].rgbBlue = 0;
   ((BITMAPINFO*)bmi)->bmiColors[0].rgbRed = 0;
   ((BITMAPINFO*)bmi)->bmiColors[0].rgbGreen = 0;
   ((BITMAPINFO*)bmi)->bmiColors[1].rgbBlue = 0xFF;
   ((BITMAPINFO*)bmi)->bmiColors[1].rgbRed = 0xFF;
   ((BITMAPINFO*)bmi)->bmiColors[1].rgbGreen = 0xFF;
 
   DWORD headerSize = bmi->biSize + sizeof(bmfh)  + sizeof(RGBQUAD) * 2;

   LONG lFileSize = headerSize + imageSize ;
   bmfh.bfType = 'B'+('M'<<8);
   bmfh.bfOffBits = headerSize;
   bmfh.bfSize = lFileSize;
   bmfh.bfReserved1 = bmfh.bfReserved2 = 0;

   fwrite(&bmfh, 1, sizeof(BITMAPFILEHEADER), wr);
   fwrite(bmi, 1, bmi->biSize + sizeof(RGBQUAD) * 2, wr);
   fwrite(bits, 1, imageSize, wr);

   fclose(wr);
}

HBITMAP CreateMonoBitmap(HDC dc, DWORD width, DWORD height, BYTE **bits);
bool NPrinter::Print(const wchar_t *name, IDataSource *source, IProgressIndicator *progress, int copies)
{
   FormMaker fm(2400, 3320, 300);

   if( fm.Load(name, source) == false )
      return false;

   HDC dc = CreateCompatibleDC(NULL);
   int ctr = 0;
   while( true )
   {
      BYTE *bits;
      HBITMAP hbmp = CreateMonoBitmap(dc, (DWORD)fm.Width(), (DWORD)fm.Height(), &bits);
      if( hbmp == NULL )
         break;

      SetBkColor(dc, RGB(0xFF,0xFF,0xFF));
      SetTextColor(dc, RGB(0,0,0));
      SetBkMode(dc, OPAQUE);
      SelectObject(dc, hbmp);

      DWORD maxHeight = fm.Height();

      // make page
      progress->SetText(L"Печать страницы...");

      if( !fm.MakePage(dc, &maxHeight) )
	  {
	    DeleteObject(hbmp);
		break;
	  }

      wchar_t buf[MAX_PATH];
      GetModuleFileName(hInst, buf, MAX_PATH);
      wchar_t *p = wcsrchr(buf, L'\\');
      wsprintf(p+1, L"Test%d.bmp", ctr++);
      SaveToFile(buf, hbmp, bits);

      DeleteObject(hbmp);
   }
   DeleteDC(dc);
   return true;
}

#else


bool LoadBits(const wchar_t *fileName, BITMAP *b, BYTE* &bits)
{
   FILE *f = _wfopen(fileName, L"rb");
   if( f == NULL ) return false;

   char buf[sizeof(BITMAPINFOHEADER) + sizeof(RGBQUAD) * 2];
   BITMAPINFOHEADER *bmi = (BITMAPINFOHEADER*)buf;
   BITMAPFILEHEADER bmfh;

   fread( &bmfh, 1, sizeof(BITMAPFILEHEADER), f);
   fread( bmi, 1, bmfh.bfOffBits - sizeof(bmfh), f);

   int imageSize = bmfh.bfSize - bmfh.bfOffBits;
   bits = new BYTE[imageSize];
   fread(bits, 1, imageSize, f);

   b->bmBits = bits;
   b->bmBitsPixel = 1;
   b->bmHeight = bmi->biHeight;
   b->bmPlanes = 1;
   b->bmType = bmfh.bfType;
   b->bmWidth = bmi->biWidth;
   b->bmWidthBytes = imageSize / bmi->biHeight;

   fclose(f);
   return true;
}

HBITMAP CreateMonoBitmap(HDC dc, DWORD width, DWORD height, BYTE **bits);

#define DEBUG_PRINTER_BAG 1

bool NPrinter::Print(const wchar_t *name, IDataSource *source, IProgressIndicator *progress, int copies)
{
   printer->PreparePrint();

   // work
   //DWORD width = printContext->InputPixelsPerRow();
   //DWORD height = (DWORD)(printContext->EffectiveResolutionY() * printContext->PrintableHeight());
   //FormMaker fm(width, height, (DWORD)printContext->EffectiveResolutionY());
   DWORD width = 2400;
   //DWORD height = 3800;
   //DWORD width = 2400;
   DWORD height = 3320;
   FormMaker fm(width, height, 300);

   if( fm.Load(name, source) == false )
   {
      Close();
      return false;
   }

   startPrinting = true;
   DWORD lineSize = width * 3;
   line = new BYTE [lineSize];
   bool printFoolPage = true; //((strcmp("deskjet 5100", printer->Type()) == 0));
   
   //int ctr = 0;

   HDC dc = CreateCompatibleDC(NULL);
   std::vector<BYTE*> vBytes;
   std::vector<HBITMAP> vBmps;

   BITMAP bmp;
   while( true )
   {
      BYTE *bits;
      HBITMAP hbmp = CreateMonoBitmap(dc, (DWORD)fm.Width(), (DWORD)fm.Height(), &bits);
      if( hbmp == NULL )
         break;

      SetBkColor(dc, RGB(0xFF,0xFF,0xFF));
      SetTextColor(dc, RGB(0,0,0));
      SetBkMode(dc, OPAQUE);
      SelectObject(dc, hbmp);
      GetObject(hbmp, sizeof(bmp), &bmp);

      DWORD maxHeight = height;
      if( !fm.MakePage(dc, &maxHeight) )
      {
         DeleteObject(hbmp);
         break;
      }
      
      vBytes.push_back(bits);
      vBmps.push_back(hbmp);
   }

   printContext = new PrintContext(printer, 2400);//, 2400, A4);//, 0, LETTER, QUALITY_DRAFT);//2400, 2400, LETTER, QUALITY_NORMAL, MEDIA_PLAIN, GREY_K);
   if( printContext->constructor_error > NO_ERROR )
   {
      Close();
      return false;
   }

   //printContext->SetPaperSize(A4, FALSE);

   //if( copies > 1 )
   //   printContext->SetCopyCount(copies);

   job = new Job(printContext);
   if( job->constructor_error > NO_ERROR )
   {
      Close();
      return false;
   }

   //BITMAP bmp;
   //BYTE *bits;
   //HBITMAP hbmp = CreateMonoBitmap(dc, (DWORD)fm.Width(), (DWORD)fm.Height(), &bits);
   //if( hbmp == NULL ) return false;
   //SetBkColor(dc, RGB(0xFF,0xFF,0xFF));
   //SetTextColor(dc, RGB(0,0,0));
   //SetBkMode(dc, OPAQUE);
   //SelectObject(dc, hbmp);
   //GetObject(hbmp, sizeof(bmp), &bmp);

   //RECT pageBounds = {0, 0, bmp.bmWidth, bmp.bmHeight};


   //wchar_t path[MAX_PATH], *ep;
   //GetModuleFileName(NULL, path, MAX_PATH);
   //ep = wcsrchr(path, L'\\');
   //wcscpy(ep+1, L"Test0.bmp");
   //if( !LoadBits(path, &bmp, bits) )
   //   return false;

   //int pg = 1;
   //while( pg++ < 3 )
   //{
   while( true )
   {
      if( vBytes.size() == 0 )
         break;
      BYTE* bits = vBytes.front();
      vBytes.erase(vBytes.begin());

      // make page
      progress->SetText(L"Печать страницы...");

      //DWORD maxHeight = height;
      //hbmp = fm.MakePage(dc, &maxHeight, &bits);
      //if( hbmp == NULL ) break;
      //if( !fm.MakePage(dc, &maxHeight, hbmp) ) break;

      //FillRect(dc, &pageBounds, (HBRUSH)GetStockObject(WHITE_BRUSH));

      DWORD maxHeight = height;
      //if( !fm.MakePage(dc, &maxHeight) ) return false;

      int nCopies = copies;
      if( printFoolPage )
         maxHeight = fm.Height();

      while( nCopies-- > 0 )
      {
         if( fm.Album() )
         {
            BYTE *src = bits, pixel = 0;
            int emptyLines = fm.Height() - maxHeight;
            if( emptyLines < 0 ) emptyLines = 0;

            progress->SetMax(fm.Width());
            progress->SetPos(0);

            int h = 0;
            for( ; h<bmp.bmWidth ; h++ )
            //for( ; h<bmp.bmWidth && h < 500; h++ )
            {
               memset(line, 0xFF, lineSize);

               CopyAlbumLine(line, src, emptyLines, bmp.bmHeight, bmp.bmWidthBytes, pixel++);
               job->SendRasters(line);
               progress->SetPos(h);

               if( pixel > 7 )
               {
                  pixel = 0;
                  src++;
               }
            }
            h++;
         } else
         {
            BYTE *src = bits + bmp.bmWidthBytes * (bmp.bmHeight - 1);
            progress->SetMax(maxHeight);
            for( DWORD h = 0; h<height && h < maxHeight; h++ )
            {
               CopyLine(line, src, width);
               job->SendRasters(line);
               src -= bmp.bmWidthBytes;

               progress->SetPos(h);
            }
         }
         job->NewPage();
      }
   }

   std::vector<HBITMAP>::iterator i = vBmps.begin();
   for( ; i != vBmps.end(); i++ )
      DeleteObject(*i);

   //DeleteObject(hbmp);
   //hbmp = NULL;

   delete line;
   line = NULL;
   
   DeleteDC(dc);

   startPrinting = false;
   if( job != NULL )
   {
      delete job;
      job = NULL;
   }

   return true;
}
#endif

void NPrinter::Cancel()
{
   if( printer )
      printer->CancelJob();

   Close();
}

//
//--------------------------------------- GetPrinter ------------------------------
//

void GetPrinter(IPrinter **printer)
{
   *printer = new NPrinter();
}


//
//--------------------------------------- GetConnection ------------------------------
//
HINSTANCE btwc;
IConnect* GetConnection(int index)
{
   if( index > 0 ) return NULL;

   // check Widcomm
   HINSTANCE hi = LoadLibrary(L"BtSdkCE50.dll");
   if( hi == NULL )
      return new BTConnection();

   btwc = LoadLibrary(L"BTWC.dll");
   if( btwc == NULL )
      return new BTConnection();

   typedef IConnect* (*TGetConnect)(int);
   TGetConnect tg = (TGetConnect)GetProcAddress(btwc, L"GetConnection"); 
   return tg(0);
}


//
//--------------------------------------- DllMain ------------------------------
//

BOOL WINAPI DllMain( HANDLE hInstDll, ULONG ulReason, LPVOID lpReserved )
{
   switch( ulReason )
   {
      case DLL_PROCESS_ATTACH :
         hInst = (HINSTANCE)hInstDll;
         break;
      
      case DLL_PROCESS_DETACH:
         if( btwc != NULL )
            FreeLibrary(btwc);
         break;
         
      case DLL_THREAD_ATTACH:
         break;
         
      case DLL_THREAD_DETACH:
         break;
         
   }
   return TRUE;
}

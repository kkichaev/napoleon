/*--------------------------------------------------------------
   (C) Copyright 2005, Denis Mosyagin

   DBF implementation
   ert   15/10/1994   creating
----------------------------------------------------------------*/

#ifndef __DBF_H
#define __DBF_H

#include <string.h>
#ifdef UNIX
#else
#include <io.h>
#endif
#include <string>

enum Boolean { False, True };

typedef unsigned short ushort;
typedef unsigned char uchar;

class DataForm;

#define MaxFileName  256
#define MaxStrLen    256
#define NumItems(a) sizeof(a)/sizeof(a[0])

enum JustifyType { NoJustify, LeftJustify, RightJustify, CenterJustify };
void Format( char* &dst,const char *src, int &len,int Width, JustifyType Jst );
//const char* FloatToStr( double vol, int Width, unsigned char Prec, int Type );
const char *Trunc(const char *src, std::string* buf);

struct DateType
{
    unsigned char day[3];
    unsigned char month[3];
    unsigned char year[5];
};

enum DBFErrorType
{
    detNoMemory,
    detCantOpen
};

const char DelSym  = '*';
struct DBRec
{
    unsigned char name[12];
    unsigned char type;
    unsigned char width;
    unsigned char prec;
};

struct DBHead
{
    uchar isMemo;          // признак наличия MEMO полей:
                             // 03h - нет полей,
                             // F5h - есть поле as fox
    unsigned char date[3]; // дата последней корректировки - ггммдд
    long  numRec;          // число записей
    short headLen;        // размер заголовка
    short recLen;         // размер записи
    unsigned char noth[20];       // резерв = 00h
};

struct DBField
{
    unsigned char name[11];  // имя поля записи
    unsigned char type;       // 43h (C) - символьный (character)
                               // 4eh (N) - числовой   (number)
                               // 44h (D) - дата       (date)
                               // 4ch (L) - логический (logical)
                               // 4dh (M) - примечания (memo)
    short offset;              // смещение поля относительно начала записи
    unsigned char empty[2];   // резерв = 00h
    unsigned char width;      // размер поля (в байтах).
                                // D (дата) = 8
                                // L (логический) = 1
                                // M (примечания) = 10
    unsigned char prec;        // число цифр после десятичной точки
    unsigned char empty1[14];  // резерв = 00h
};

// Mode for DataForm
// XXXXXXXX - flNoNeedClose
//    │││└──- flDBError
//    ││└───- flNeedReWrite
//    │└────- flNoReadDelete
//    └────── flUseChange

const short flNoNeedClose  = 1;
const short flDBError      = 2;
const short flNeedReWrite  = 4;
const short flNoReadDelete = 8;
const short flUseChange    = 0x10;

void GetKeyField( void *buf, DataForm *base );  // Copy Key Field To Buffer

long ReverceLong( long val );
int ReverceInt( int val );
void WriteDigit( char *p, long val, int wdh );
char *ParseFileName( const char *fname );

class DataForm
{
public:
    Boolean Error() const { return (Boolean)( mode & flDBError ); }

    DataForm();
    ~DataForm();

    bool Opened() const { return (sPtr != -1); }

    bool Open(const char *fname, bool checkExt = true);              // Открытие существующего файла
    bool Create( const char *_fname, int numFields, DBRec *Recs );
    void Close();
    const char* GetName() const { return fname.c_str(); }

    int Field(const char *name) const;
    const char* operator[] (const char *fldName) const;
    const char* operator[] ( int fldNum ) const;

    char* GetField( const char *fname ) const;
    char* GetField( int fnum ) const { return recBuf+fields[fnum].offset; }
    DBField* GetFieldRef( const char *fname ) const;
    DBHead*  GetHead() const { return head; }
    DBField* GetFieldBase() const { return fields; }
    int      NumFields() const { return numField; }
    void     WriteFieldsRef();

    void  Fill( char *rcBuf, const char *fldName, void *Val );
    void  Fill( char *rcBuf, int    numFld, void *Val );
    void  Fill( const char *fldName, double Val );

    void  Fill( const char *fldName, void *Val ) { Fill( recBuf, fldName, Val ); }
    void  Fill( int numFld, void* Val ) {  Fill( recBuf, numFld, Val ); }
    void  Fill( const char *fld, const char *Val );
    void  Fill( const char *fld, int Val ) { double v = Val; Fill(fld,&v); }

    long  GetNum() { return cIndex; }
    long  GetRecNo() const { return cIndex; }
    long  NumRec() { return head->numRec; }
    int   RecLen() { return head->recLen; }
    const char *GetRec() const { return recBuf; }

    bool IsDelete() { return (*recBuf == '*'); }

    int   GetWidth( const char *fname );
    int   GetWidth( int nfld ) { return fields[nfld].width; }

    void  WriteRec();
    bool  WriteRec( long rc );

    bool Append( const char *recBuf );
    bool Append() { return Append( recBuf ); }
    void ResetRec() { memset( recBuf, ' ', head->recLen ); }

    bool ReadRec( long indx ) const;            // Установка новой записи

    void  NotReadDelete() { mode |= flNoReadDelete; }
    void  ReadDelete()    { mode &= (~flNoReadDelete); }

    void  MarkDelete(bool del) { *recBuf = (del) ? '*' : ' '; }
    void  DeleteRec();
    void  RecallRec();

    void  GoTop();
    void  GoBottom();

    void ZapFile() {head->numRec = 0; mode |= flNeedReWrite;}

    bool Eof() const { return eofFlag; }

	 void Delete();

protected:
    void SetError( DBFErrorType err ) { mode |= flDBError; ErrKind = err; }
    DBFErrorType ErrKind;
    int mode;

    std::string fname;
    int          sPtr;    // Файловый поток

    mutable char   *recBuf;       // Буфер текущей записи
    char   *str;          // Указатель на строку значения поля (оператор [])

    DBHead  *head;     // Заголовок dbf файла
    DBField *fields;   // Описание полей

    mutable long  cIndex;      // Номер текущей записи
    int   numField;    // Число полей

    bool eofFlag;

    int   GetHead( void* &hdbf );
    void  ReadRec() const;                       // Читает текущую запись
};

#endif

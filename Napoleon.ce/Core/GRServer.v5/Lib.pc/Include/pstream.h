/*
 * Copyright (C), 2009, ����� �������
 *
 * Parse Stream
 *
 * ert   17/09/2009   creating
 */
#ifndef __GR_PARSE_STREAM_H
#define __GR_PARSE_STREAM_H

#include <string>

namespace GRServer {

inline bool IsSpace(wchar_t sym) { return iswspace(sym) != 0; }
inline bool IsSpace(char sym) { return ((signed)sym > 0 && isspace(sym)); }

template <class TString, typename _CPChar = typename TString::const_pointer> class ParseStream
{
public:
   typedef typename TString::value_type Char;
   typedef _CPChar CPChar;

//#ifdef UNIX
//   typedef const unsigned short* CPChar;
//#else
//   typedef typename TString::const_pointer CPChar;
//#endif

   ParseStream(CPChar start, CPChar end) : startI(start), endI(end) {}

   bool EOS() const { return (startI == endI); }

   Char Current() const { return ( EOS() ) ? (Char)0 : (*startI); }
   Char Next() const
   {
      if( EOS() ) return (Char)0;

      CPChar i = startI;
      return (++i == endI) ? (Char)0 : (*i);
   }

   bool MoveNext()
   {
      if( !EOS() )
         startI++;
      return !EOS();
   }

   bool EatWhite()
   {
      while( !EOS() )
      {
         if( !IsSpace((Char)(*startI)) ) return true;
         startI++;
      }
      return false;
   }

   bool CopyUntill(TString *dest, Char stopSym)
   {
      while( !EOS() )
      {
         Char sym = (*startI);
         if( sym == stopSym ) break;

#ifdef VERSION_5
         if (sym == '\\')
         {
            startI++;
            if (EOS())
               break;
            sym = (*startI);
         }
#endif

         dest->append(1, sym);
         startI++;
      }
      return !EOS();
   }

protected:
   CPChar startI, endI;
};

typedef ParseStream<std::wstring> ParseStreamW;

#ifdef UNIX
typedef ParseStream<std::wstring, const unsigned short*> ParseStreamU;
#else
typedef ParseStream<std::wstring> ParseStreamU;
#endif
typedef ParseStream<std::string> ParseStreamA;

} // namespace GRServer

#endif

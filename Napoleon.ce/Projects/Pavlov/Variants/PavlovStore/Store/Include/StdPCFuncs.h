/*
* Copyright (C), 2007, Денис Мосягин
*
* Стандартные функции системы для PC
*
*  ert   04/12/2007   creating
*/

#ifndef __STD_PC_FUNCS_H
#define __STD_PC_FUNCS_H

inline DWORD ScaleDouble(double val, DWORD scale)
{
   if( val >= 0 )
      return (DWORD)((val + 1.0 / (scale * 10)) * scale);
   else
      return (DWORD)((val - 1.0 / (scale * 10)) * scale);
}


#endif

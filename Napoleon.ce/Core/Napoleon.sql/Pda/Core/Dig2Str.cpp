/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Реализация функций накладной
 *
 *  ert   17/05/2008   creating
 */
#include "stdafx.h"
#include "DoPrint.h"

wchar_t *digSet[] =
{
  L" один", L" два", L" три", L" четыре", L" пять", L" шесть", L" семь", L" восемь",
  L" девять", L" десять", L" одиннадцать", L" двенадцать", L" тринадцать",
  L" четырнадцать", L" пятнадцать", L" шестнадцать", L" семнадцать",
  L" восемнадцать", L" девятнадцать"
};

wchar_t *DecDigSet[] =
{
  L" двадцать", L" тридцать", L" сорок", L" пятьдесят", L" шестьдесят",
  L" семьдесят", L" восемьдесят", L" девяносто"
};

wchar_t *HunDigSet[] =
{
  L" сто", L" двести", L" триста", L" четыреста", L" пятьсот", L" шестьсот",
  L" семьсот", L" восемьсот", L" девятьсот"
};

wchar_t *OthDig[] =
{
  L"",
  L" тысяч",
  L" миллион",
  L" миллиард"
};

wchar_t *ShortOth[]=
{
  L"",
  L" тыс",
  L" млн",
  L" млрд"
};

const wchar_t *FirstRest[] =
{
  L"",
  L"а",
  L"и"
};

const wchar_t *LastRest[] =
{
  L"ов",
  L"",
  L"а"
};

const wchar_t *ShortRest[] =
{
  L".",
  L".",
  L"."
};

static void Conv1000( int val, int step, wchar_t *str);

void AddRest( long val, std::wstring *str, const wchar_t *base, const wchar_t *restSet[] )
{
  int rVal = (int)(val%100);
  (*str) += base;

  if( rVal > 10 && rVal < 20 )
  {
     (*str) += restSet[0];
     return;
  }

  rVal %= 10;
  switch ( rVal )
  {
     case 1:
        (*str) += restSet[1];
        return;
     case 2:
     case 3:
     case 4:
        (*str) += restSet[2];
        return;
  }
  (*str) += restSet[0];
}


static void Conv1000( int val, int step, std::wstring *str)
{
  int rest = val % 100;
  val /= 100;

  if ( val ) (*str) = HunDigSet[val-1];
  else str->clear();

  if ( !rest ) return;
  if ( rest < 20 )
  {
     if ( step == 1 )
        switch ( rest )
        {
           case 1:
              (*str) += L" одна";
              return;
           case 2:
              (*str) += L" две";
              return ;
        }
     (*str) += digSet[rest-1];
  } else
  {
     (*str) += DecDigSet[(rest/10)-2];
     if ( rest % 10 )
     {
        rest %= 10;
        if ( step == 1 )
           switch ( rest )
           {
              case 1:
                 (*str) += L" одна";
                 return;
              case 2:
                 (*str) += L" две";
              return ;
           }
        (*str) += digSet[rest-1];
    }
  }
}

void DigToText(std::wstring *str, DWORD dig)
{
  int step = 0;
  long lastDig;
  if ( dig == 0 )
  {
     *str = L" ноль";
     return;
  }

  str->clear();
  do
  {
     int rest = (int)(dig%1000);
     lastDig = dig;
     dig /= 1000;
    
     if ( rest != 0 )
     {
        std::wstring curDig;
        Conv1000(rest, step, &curDig);

        if ( step != 0 )
           AddRest( lastDig, &curDig, OthDig[step], (step == 1) ? FirstRest : LastRest );

        curDig += (*str);
        *str = curDig;
     }
     step++;
  } while ( dig > 0 );
}


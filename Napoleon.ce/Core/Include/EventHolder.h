/*
* Copyright (C), 2007, ƒенис ћос€гин
*
* —обыти€, обработка событий
*
* ert   19/03/2007   creating
*/ 

#ifndef _EVENT_H
#define _EVENT_H

#include <vector>

//
// ’ранитель событи€ _E
//
template<class _E> class EventHolder : public std::vector<_E>
{
public:
   typedef std::vector<_E> HandlerList;

   EventHolder() {}
   virtual ~EventHolder() {}

   //
   // добавить обработчик
   //
   void AddHandler(_E eh) { push_back(eh); }
   // то же использу€ оператор +=
   EventHolder& operator+= (_E eh) { AddHandler(eh); return *this; }

   //
   // удалить обработчик
   //
   void RemoveHandler(_E eh)
   {
      HandlerList::iterator i = begin();
      while( i != end() )
      {
         if( (*i) == eh )
         {
            erase(i); // я не пон€л как можно удал€ть и ходить по вектору
            break;    // пока будет смотреть только один handler             
         }
         i++;
      }
   }
   // то же использу€ оператор -=
   EventHolder& operator-= (_E eh) { RemoveHandler(eh); return *this; }
};

#endif

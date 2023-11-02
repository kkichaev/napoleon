/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Объект для обмена с сервером
*
*  ert   05/09/2010   creating
*/
#ifndef __SERVER_OBJECT_H
#define __SERVER_OBJECT_H

#include <Reflection.h>
#include <ServerDefs.h>
#include <Module.h>

struct IServObject
{
   virtual const DataReflector& DataType() const = 0;
   virtual IReflectableData* GetSelf() const = 0;
   virtual void UnbindData() = 0;
};

int ObjectExchange(IServObject *object, const wchar_t* command, std::wstring *answer);

//
// предполагаем T порожден от DbImpl<> - нам нужен UnbindStrings
//
template <class T> class ServObject : public T, public IServObject
{
public:
   ServObject() { servResponse = L""; servResult = 0; }

   wchar_t *servResponse;
   WORD     servResult;

   static IReflectableData* Creator() { return new ServObject<T>(); } 
   static const wchar_t* TypeName() { return name.GetName(); }
   virtual const DataReflector& GetType() const { return GetTypeReflector(name.GetName()); }

   virtual const DataReflector& DataType() const { return GetTypeReflector(T::TypeName()); }
   virtual IReflectableData* GetSelf() const { return (IReflectableData*) this; }
   virtual void UnbindData() { return UnbindStrings(); }

protected:
   class Name : public std::wstring
   {
   public:
      Name()
      {
      }

      const wchar_t* GetName()
      {
         if( empty() )
         {
            T t;
            
            assign(L"ServObject");
            append(t.GetType().Name());

            Register();
         }
         return c_str();
      }

      void Register()
      {
         const DataReflector* fr = FindTypeReflector(this->c_str());
         if( fr == NULL )
         {
            DataReflector *dr = new DataReflector(ServObject<T>::Creator, this->c_str());
            dr->AddMember(new ParentType(T::TypeName()));
            dr->AddMember(new StringType(SERV_RESPONSE, offsetof(ServObject<T>, servResponse)));
            dr->AddMember(new UShortType(SERV_RESULT, offsetof(ServObject<T>, servResult)));

            RegisterTypeReflector(dr);
         }
      }
   };

   static Name name;
};

template <class T> typename ServObject<T>::Name ServObject<T>::name;

#endif

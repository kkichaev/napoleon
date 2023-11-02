/*
 * Copyright (C), 2009, ƒенис ћос€гин
 *
 * «агрузчик сервера (служба или трей)
 *
 * ert   24/03/2009   creating
 */
#ifndef __GR_LOADER_H
#define __GR_LOADER_H

namespace GRServer {

struct IProgramLoader;

struct IRunnableModule
{
   // запуск сервера - должен проходить максимально быстро
   virtual bool Init(DWORD argc, const char **argv, IProgramLoader *loader) = 0;

   // основной цикл
   virtual void WINAPI Run() = 0;

   // корректно остановить модуль
   // если модуль не будет остановлен в отведенное врем€, функци€ убиваетс€
   virtual void Stop() = 0;

   // если не получилось корректно остановить за отведенный интервал времени,
   // выключим модуль
   virtual void Kill() = 0;

   virtual const wchar_t* Name() const = 0;

   // если возвращает NULL или пустую строку, используетс€ Name
   virtual const wchar_t* DisplayName() const = 0;
};

struct IProgramLoader
{
   IProgramLoader(IRunnableModule *module) { this->module = module; }

   // запуск модул€
   virtual bool Run() = 0;

   // остановка модул€
   virtual void Stop() = 0;

   // лог и статус

   // добавить сообщение в лог
   virtual void ShowCriticalError(const wchar_t* msg) = 0;

   // строка дл€ запуски сервера
   virtual const char* ExecString(const IRunnableModule& module) const = 0;

   // останавливаем сервер
   virtual void Stopping(const IRunnableModule& module) = 0;

protected:
   IRunnableModule *module;
};

struct IProgramNotify
{
   virtual void ProgramNotify(IProgramLoader *loader, HWND hWnd) = 0;
};

} // namespace GRServer

#endif

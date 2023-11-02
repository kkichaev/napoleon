/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Интерфес нити
 *
 * ert   21/08/2010   creating
 */
#ifndef __ITHREAD_H
#define __ITHREAD_H

namespace GRServer {

struct IThreadWorker
{
   virtual ~IThreadWorker() {}
   virtual DWORD Execute() = 0;
};

} // namespace GRServer

#endif

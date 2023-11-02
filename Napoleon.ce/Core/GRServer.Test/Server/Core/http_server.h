#ifndef __HTTP_SERVER_H
#define __HTTP_SERVER_H

#include <socket.h>
#include "dispatcher.h"


extern "C" DWORD HandleHTTP(GRServer::Socket& socket, GRServer::Dispatcher* dispatcher);

#endif
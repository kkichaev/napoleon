#include "grjs.h"
#include "common.h"
#include "thread.h"
#include "mutex.h"

#include <map>
#include <vector>

using namespace std;

struct SendData
{
	const unsigned char* data;
	DWORD size;
	DWORD cp;
};

static Mutex dataMutex;
static map<SOCKET, vector<SendData*>> sendData;
static vector<SOCKET> needClose;

#ifndef WIN32
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
#endif

class ClientSendThread : public Thread
{
	virtual void Execute();
};

static ClientSendThread sendThread;

static void SendAll(SOCKET s)
{
	map<SOCKET, vector<SendData*>>::iterator fnd = sendData.find(s);
	if (fnd == sendData.end())
		return;

	while (fnd->second.size())
	{
		SendData *sd = *fnd->second.begin();

		DWORD needSend = sd->size - sd->cp;
		while (needSend > 0)
		{
			int rc = send(s, (const char*)sd->data + sd->cp, needSend, 0);
			if (rc <= 0)
			{
				PutLog("SendAll to %d %d rc = %d", s, needSend, rc);
				break;
			}

			needSend -= rc;
			sd->cp += rc;
		}

		fnd->second.erase(fnd->second.begin());
		free((void*)sd->data);
		delete sd;
	}
}

void ClientSendThread::Execute()
{
	while (true)
	{
#ifndef WIN32
		pthread_mutex_lock(&mutex);

		while (needClose.size() == 0 && sendData.size() == 0)
		{
			pthread_cond_wait(&cond, &mutex);
		}

		pthread_mutex_unlock(&mutex);
#endif
		while (needClose.size() > 0)
		{
			dataMutex.Lock();

			SOCKET ci = *needClose.begin();
			SendAll(ci);
			closesocket(ci);
			PutLog("Close socket %d", ci);

			needClose.erase(needClose.begin());

			dataMutex.Unlock();
		}

		if (sendData.size() == 0)
			continue;

		dataMutex.Lock();
		map<SOCKET, vector<SendData*>>::iterator i = sendData.begin();
		
		SOCKET socket = i->first;
		SendData *sd = i->second.front();

		dataMutex.Unlock();

		DWORD needSend = sd->size - sd->cp;
		int rc = send(socket, (const char*)sd->data + sd->cp, needSend, 0);

		if (needSend != rc )
			PutLog("Send to %d %d rc = %d", socket, needSend, rc);

		if (rc > 0)
		{
			if (rc < needSend)
			{
				sd->cp += rc;
			}
			else
			{
				dataMutex.Lock();
				i = sendData.find(socket);
				vector<SendData*>::iterator vi = i->second.begin();
				for (; vi != i->second.end(); i++)
				{
					if ((*vi) == sd)
					{
						i->second.erase(vi);
						break;
					}
				}
				if (i->second.size() == 0)
					sendData.erase(i);

				//if (sendData.size() == 0)
				//{
				//	PutLog("No send data");
				//}

				dataMutex.Unlock();

				free((void*)sd->data);
				delete sd;
			}
		}
		else
		{
			dataMutex.Lock();
			i = sendData.find(socket);
			while (i->second.size())
			{
				sd = *i->second.begin();
				free((void*)sd->data);
				delete sd;

				i->second.erase(i->second.begin());
			}
			sendData.erase(i);
			dataMutex.Unlock();
		}
	}
}

void Sender::Start()
{
	sendThread.Start();
}

void Sender::Stop()
{
	sendThread.Kill();
#ifndef WIN32
	pthread_cond_destroy(&cond);
#endif
}

void Sender::ForceClose(SOCKET socket)
{
	dataMutex.Lock();
	needClose.push_back(socket);
	dataMutex.Unlock();

#ifndef WIN32
	pthread_mutex_lock(&mutex);
	pthread_cond_signal(&cond);
	pthread_mutex_unlock(&mutex);
#endif
}

void Sender::Send(SOCKET socket, const unsigned char* data, DWORD cb)
{
	SendData *sd = new SendData();
	sd->data = data;
	sd->size = cb;
	sd->cp = 0;

	dataMutex.Lock();
	sendData[socket].push_back(sd);
	dataMutex.Unlock();

#ifndef WIN32
	pthread_mutex_lock(&mutex);
	pthread_cond_signal(&cond);
	pthread_mutex_unlock(&mutex);
#endif
}

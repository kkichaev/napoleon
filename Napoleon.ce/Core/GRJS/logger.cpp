#include "grjs.h"
#include "common.h"
#include "thread.h"

#include <sstream>

#include <curl/curl.h>

using namespace std;

const int ACTION_OPEN = 0;
const int ACTION_CLOSE = 1;

const DWORD SEND_THRESHOLD = 2 * 1024;

// client json {"clients":[{"id":%d,"cid":%d,"action":%d,"date:"%d,"duration":%d,"trafic":%d}]}
// server json {"servers":[{"id":%d,""action":%d,"date":%d,"address":"%s"}]}

static std::string clientJSON;
static std::string serverJSON;

static CURLM *multi_handle;

class SendThread : public Thread
{
public:
	SendThread() { finish = false; }

	virtual void Execute();

	void CloseHandles();
	void SetFinish() { finish = true; }

private:
	bool finish;
};

static SendThread* sendThread;

static void CheckAndSend(bool forceSend)
{
	int size = clientJSON.size() + serverJSON.size();

	if (size > SEND_THRESHOLD || (size > 0 && forceSend))
	{
		std::string sendBuf;
		sendBuf += "{";
		if (clientJSON.size() > 0)
		{
			sendBuf.append("\"clients\":[").append(clientJSON).append("]");
			if (serverJSON.size() > 0)
				sendBuf.append(",");
			clientJSON.clear();
		}

		if (serverJSON.size() > 0)
		{
			sendBuf.append("\"servers\":[").append(serverJSON).append("]");
			serverJSON.clear();
		}
		sendBuf += "}";
		
		//start sends
		CURL *handle = curl_easy_init();
		curl_easy_setopt(handle, CURLOPT_URL, "https://grsoft.ru//grjs/grjs.php");
		curl_easy_setopt(handle, CURLOPT_COPYPOSTFIELDS, sendBuf.c_str());
		curl_multi_add_handle(multi_handle, handle);
	}
}

void Logger::Add(const ClientHandler& h)
{
	std::stringstream ss;
	ss << "{\"id\":" << h.id << ",\"cid\":" << h.ServerID() << ",\"action\":" << ACTION_CLOSE << ",\"date\":" << h.start
		<< ",\"duration\":" << h.end - h.start << ",\"traficClient\":" << h.TraficClient() << ",\"traficServer\":" << h.TraficServer() << "}";

	if (clientJSON.size() > 0)
	{
		clientJSON += ",";
	}

	clientJSON += ss.str();

	CheckAndSend(false);
}

void Logger::Add(const ServerHandler& s)
{
	int action = (s.end == 0) ? ACTION_OPEN : ACTION_CLOSE;
	time_t time = (s.end == 0) ? s.start : s.end;
	std::stringstream ss;
	ss << "{\"id\":" << s.id << ",\"action\":" << action << ",\"date\":" << time << ",\"address\":\"" << s.address << "\" }";

	if (serverJSON.size() > 0)
	{
		serverJSON += ",";
	}

	serverJSON += ss.str();
	
	CheckAndSend(false);
}

void Logger::Start()
{
	curl_global_init(CURL_GLOBAL_ALL);
	multi_handle = curl_multi_init();

	sendThread = new SendThread();
	sendThread->Start();
}

void Logger::Stop()
{
	int still_running = 0;
	CheckAndSend(true);

	sendThread->SetFinish();
	sendThread->Join();

	delete sendThread;

	curl_multi_cleanup(multi_handle);
}


void SendThread::Execute()
{
	do
	{
		struct timeval timeout;
		int rc;

		fd_set fdread;
		fd_set fdwrite;
		fd_set fdexcep;
		int maxfd = -1;

		long curl_timeo = -1;

		FD_ZERO(&fdread);
		FD_ZERO(&fdwrite);
		FD_ZERO(&fdexcep);

		timeout.tv_sec = 1;
		timeout.tv_usec = 0;

		curl_multi_timeout(multi_handle, &curl_timeo);
		if (curl_timeo >= 0) {
			timeout.tv_sec = curl_timeo / 1000;
			if (timeout.tv_sec > 1)
				timeout.tv_sec = 1;
			else
				timeout.tv_usec = (curl_timeo % 1000) * 1000;
		}

		if (curl_multi_fdset(multi_handle, &fdread, &fdwrite, &fdexcep, &maxfd) != CURLM_OK) {
			break;
		}

		if (maxfd == -1) {
			CloseHandles();
#ifdef _WIN32
			Sleep(100);
			rc = 0;
#else
			struct timeval wait = { 0, 100 * 1000 }; /* 100ms */
			rc = select(0, NULL, NULL, NULL, &wait);
#endif
		}
		else {
			rc = select(maxfd + 1, &fdread, &fdwrite, &fdexcep, &timeout);
		}

		if (rc < 0)
			break;

		int still_running = 0;
		curl_multi_perform(multi_handle, &still_running);
		if (still_running == 0 && finish)
		{
			break;
		}
	} while (true);

	CloseHandles();
}

void SendThread::CloseHandles()
{
	struct CURLMsg *m;
	do {
		int msgq = 0;
		m = curl_multi_info_read(multi_handle, &msgq);
		if (m && (m->msg == CURLMSG_DONE)) {
			CURL *e = m->easy_handle;
			curl_multi_remove_handle(multi_handle, e);
			curl_easy_cleanup(e);
		}
	} while (m);
}

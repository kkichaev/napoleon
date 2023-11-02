
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>

#include "grsmanager.h"

using namespace std;
using namespace GRSManager;

static void ThreadSignalHandler(int sig, siginfo_t *s1, void *unused)
{
    throw sig;
}

static void *RunThread(ThreadParam *param)
{
    struct sigaction sa;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset(&sa.sa_mask);
    sa.sa_sigaction = ThreadSignalHandler;
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGILL, &sa, NULL);
    sigaction(SIGFPE, &sa, NULL);
    sigaction(SIGBUS, &sa, NULL);
    sigaction(SIGTRAP, &sa, NULL);

    sigset_t set;
    sigemptyset(&set);
    sigaddset(&set, SIGINT);
    sigaddset(&set, SIGTSTP);
    pthread_sigmask(SIG_BLOCK, &set, NULL);

    void *result = 0;
    try
    {
        param->runner->Run(param);
        ThreadRunner *wrk = param->runner;
        param->runner = NULL;
        delete wrk;
    }
    catch (...)
    {
        Log("Exceptioin in thread");

        if (param->runner != NULL)
        {
            delete param->runner;
            param->runner = NULL;
        }
    }

    return result;
}

ThreadParam *ThreadParam::StartThread(ThreadRunner *runner)
{
    ThreadParam *p = new ThreadParam();
    p->runner = runner;

    int res = pthread_create(&p->handle, NULL, (void *(*) (void *))RunThread, p);

    if(res != 0)
    {
        delete p->runner;
        delete p;
        p = NULL;
    }

    return p;
}

int main(void)
{
    // read config
    Config cfg;
    if (!cfg.Load("./grsmanager.ini"))
        return 1;

    DBManager dbm;
    FCGIManager fcgim(dbm);
    CMDManager cmdm(fcgim, dbm);

    if (dbm.Open(cfg) && cmdm.Start(cfg))
    {
        fcgim.Run(cfg);
    }

    // open command connection

    // open FCGI connection

    // set signal handler
    // handle commands
    // handle FCGI

    return 0;
}

#include "grsmanager.h"
#include <sys/socket.h>
#include <unistd.h>

using namespace GRSManager;
using namespace std;

extern char ** environ;
void GRSManager::Log(const char *msg, ...)
{
    va_list args;
    va_start(args, msg);
    
    vprintf(msg, args);
    printf("\n");
    
    va_end(args);
}

// Maximum number of bytes allowed to be read from stdin
static const unsigned long STDIN_MAX = 1000000;

static void penv(ostream& out, const char * const * envp)
{
    out << "<PRE>\n";
    for ( ; *envp; ++envp)
    {
        out << *envp << "\n";
    }
    out << "</PRE>\n";
}

static long gstdin(istream& in, ostream& err, FCGX_Request * request, char ** content)
{
    char * clenstr = FCGX_GetParam("CONTENT_LENGTH", request->envp);
    unsigned long clen = STDIN_MAX;

    if (clenstr)
    {
        clen = strtol(clenstr, &clenstr, 10);
        if (*clenstr)
        {
            err << "can't parse \"CONTENT_LENGTH="
                 << FCGX_GetParam("CONTENT_LENGTH", request->envp)
                 << "\"\n";
            clen = STDIN_MAX;
        }

        // *always* put a cap on the amount of data that will be read
        if (clen > STDIN_MAX) clen = STDIN_MAX;

        *content = new char[clen];

        in.read(*content, clen);
        clen = in.gcount();
    }
    else
    {
        // *never* read stdin when CONTENT_LENGTH is missing or unparsable
        *content = 0;
        clen = 0;
    }

    // Chew up any remaining stdin - this shouldn't be necessary
    // but is because mod_fastcgi doesn't handle it correctly.

    // ignore() doesn't set the eof bit in some versions of glibc++
    // so use gcount() instead of eof()...
    do in.ignore(1024); while (in.gcount() == 1024);

    return clen;
}

void HandleRequest(FCGX_Request *request)
{
    fcgi_streambuf inb(request->in);
    fcgi_streambuf outb(request->out);
    fcgi_streambuf errb(request->err);

    std::istream in(&inb);
    std::ostream out(&outb);
    std::ostream err(&errb);

    char * content;
    unsigned long clen = gstdin(in, err, request, &content);

    out << "Content-type: text/html\r\n"
            "\r\n"
            "<TITLE>echo-cpp</TITLE>\n"
            "<H1>echo-cpp</H1>\n";
            // "<H4>PID: " << pid << "</H4>\n";
            // "<H4>Request Number: " << ++count << "</H4>\n";

    out << "<H4>Request Environment</H4>\n";
    penv(out, request->envp);

    // // out << "<H4>Process/Initial Environment</H4>\n";
    // // penv(environ);

    out << "<H4>Standard Input - " << clen;
    if (clen == STDIN_MAX) out << " (STDIN_MAX)";
    out << " bytes</H4>\n";
    if (clen) out.write(content, clen);

    if (content) delete []content;

    FCGX_FFlush(request->out);
    FCGX_Free(request, 1);
    delete request;
}


FCGIManager::FCGIManager(DBManager& _dbm) :
    socket(-1)
    ,closing(false)
    ,dbm(_dbm)
{
}

FCGIManager::~FCGIManager()
{
}

void FCGIManager::Run(const Config& cfg)
{
    FCGX_Init();
    socket = FCGX_OpenSocket(cfg.fcgiSocket.c_str(), 10);
    if(socket < 0)
    {
        Log("Can't open socket <%s> errno %d", cfg.fcgiSocket.c_str(), errno);
        return;
    }

    while (!closing)
    {
        FCGX_Request *request = new FCGX_Request();
        FCGX_InitRequest(request, socket, 0);
        request->keepConnection = 0;

        if(FCGX_Accept_r(request) != 0)
            break;

        HandleRequest(request);
    }

    if(socket != -1)
    {
        shutdown(socket, SHUT_RDWR);
        close(socket);
    }
}
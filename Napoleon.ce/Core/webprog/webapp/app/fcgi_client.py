import json
import socket
import random
from typing import Self
from urllib.parse import quote

from flask import current_app
from collections.abc import Collection

from app.api.error import bad_request


class FastCGIClient:
    """A Fast-CGI Client for Python"""

    # private
    __FCGI_VERSION = 1

    __FCGI_ROLE_RESPONDER = 1
    __FCGI_ROLE_AUTHORIZER = 2
    __FCGI_ROLE_FILTER = 3

    __FCGI_TYPE_BEGIN = 1
    __FCGI_TYPE_ABORT = 2
    __FCGI_TYPE_END = 3
    __FCGI_TYPE_PARAMS = 4
    __FCGI_TYPE_STDIN = 5
    __FCGI_TYPE_STDOUT = 6
    __FCGI_TYPE_STDERR = 7
    __FCGI_TYPE_DATA = 8
    __FCGI_TYPE_GETVALUES = 9
    __FCGI_TYPE_GETVALUES_RESULT = 10
    __FCGI_TYPE_UNKOWNTYPE = 11

    __FCGI_HEADER_SIZE = 8

    # request state
    FCGI_STATE_SEND = 1
    FCGI_STATE_ERROR = 2
    FCGI_STATE_SUCCESS = 3

    def __init__(self, host, port, timeout, keepalive):
        self.host = host
        self.port = port
        self.timeout = timeout
        if keepalive:
            self.keepalive = 1
        else:
            self.keepalive = 0
        self.sock = None
        self.requests = dict()
        self.error = ""

    def close(self):
        if self.sock: self.sock.close()
        self.sock = None

    def __connect(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.settimeout(self.timeout)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        # if self.keepalive:
        #     self.sock.setsockopt(socket.SOL_SOCKET, socket.SOL_KEEPALIVE, 1)
        # else:
        #     self.sock.setsockopt(socket.SOL_SOCKET, socket.SOL_KEEPALIVE, 0)
        try:
            self.sock.connect((self.host, int(self.port)))
        except socket.error as msg:
            self.sock.close()
            self.sock = None
            self.error = repr(msg)
            # print(repr(msg))
            return False
        return True

    def __encode(self, fcgi_type, content:bytearray, requestid) -> bytearray:
        length = len(content)
        res = bytearray()
        res.append(FastCGIClient.__FCGI_VERSION)
        res.append(fcgi_type)
        res.append((requestid >> 8) & 0xFF)
        res.append(requestid & 0xFF)
        res.append((length >> 8) & 0xFF)
        res.append(length & 0xFF)
        res.extend(b'\0\0')
        res.extend(content)
        return res

    def __encodeParams(self, name, value) -> bytearray:
        def appendLen(rec:bytearray, len:int) -> bytearray:
            if len < 128:
                rec.append(len)
            else:
                rec.append((len >> 24) | 0x80)
                rec.append((len >> 16) & 0xFF)
                rec.append((len >> 8) & 0xFF)
                rec.append(len & 0xFF)
            return rec

        value = str(value)
        name = str(name)
        
        record = bytearray()
        appendLen(record, len(name))
        appendLen(record, len(value))

        record.extend(name.encode('utf-8'))
        record.extend(value.encode('utf-8'))
        return record

    def __decodeFastCGIRecord(self):
        def makeHeader(stream) -> dict[str, any]:
            header = dict()
            header['version'] = stream[0]
            header['type'] = stream[1]
            header['requestId'] = (stream[2] << 8) + stream[3]
            
            header['contentLength'] = (stream[4] << 8) + stream[5]
            header['paddingLength'] = stream[6]
            header['reserved'] = stream[7]
            
            return header

        header = bytearray()
        while len(header) < FastCGIClient.__FCGI_HEADER_SIZE:
            need_rcv = FastCGIClient.__FCGI_HEADER_SIZE - len(header)
            rcv = self.sock.recv(need_rcv)
            if not rcv:
                return False
            header.extend(rcv)

        record = makeHeader(header)
        record['content'] = bytearray()

        contentLength = int(record['contentLength'])

        while contentLength > 0:
            buffer = self.sock.recv(contentLength)
            if not buffer: break
            contentLength -= len(buffer)
            record['content'].extend(buffer)

        padLen = int(record['paddingLength'])
        if padLen > 0:
            self.sock.recv(padLen)
        return record

    def request(self, nameValuePairs={}, post:bytes=None) -> None | bytearray:
        if not self.__connect():
            # print('connect failure! please check your fasctcgi-server !!')
            return

        requestId = random.randint(1, (1 << 16) - 1)
        self.requests[requestId] = dict()
        request = bytearray()

        beginFCGIRecordContent = bytearray(b'\0')
        beginFCGIRecordContent.append(FastCGIClient.__FCGI_ROLE_RESPONDER)
        beginFCGIRecordContent.append(self.keepalive)
        beginFCGIRecordContent.extend(b'\0\0\0\0\0')

        request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_BEGIN,
                                              beginFCGIRecordContent, requestId))
        paramsRecord = bytearray()
        if nameValuePairs:
            for (name, value) in nameValuePairs.items():
                # paramsRecord = self.__encodeNameValueParams(name, value)
                # request += self.__encodeFastCGIRecord(FastCGIClient.__FCGI_TYPE_PARAMS, paramsRecord, requestId)
                paramsRecord.extend(self.__encodeParams(name, value))

        if len(paramsRecord):
            request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_PARAMS, paramsRecord, requestId))

        request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_PARAMS, ''.encode('utf-8'), requestId))

        if post:
            while True:
                postLen = len(post)
                if postLen == 0 : break

                if postLen < 65535:
                    chunkLen = postLen
                    chunk = post
                else:
                    chunkLen = postLen % 65535 + 1
                    chunk = post[0:chunkLen]

                request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_STDIN, chunk, requestId))
                post = post[chunkLen:]
            # request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_STDIN, post, requestId))

        request.extend(self.__encode(FastCGIClient.__FCGI_TYPE_STDIN, ''.encode('utf-8'), requestId))

        self.sock.send(request)

        self.requests[requestId]['state'] = FastCGIClient.FCGI_STATE_SEND
        self.requests[requestId]['response'] = bytearray()
        return self.__waitForResponse(requestId)

    def __waitForResponse(self, requestId) -> bytearray :
        while True:
            response = self.__decodeFastCGIRecord()
            if not response:
                break
            if response['type'] == FastCGIClient.__FCGI_TYPE_STDOUT \
                    or response['type'] == FastCGIClient.__FCGI_TYPE_STDERR:
                if response['type'] == FastCGIClient.__FCGI_TYPE_STDERR:
                    self.requests['state'] = FastCGIClient.FCGI_STATE_ERROR
                if requestId == int(response['requestId']):
                    self.requests[requestId]['response'] += response['content']
            if response['type'] == FastCGIClient.FCGI_STATE_SUCCESS:
                self.requests[requestId]
        return self.requests[requestId]['response']

    def __repr__(self):
        return "fastcgi connect host:{} port:{}".format(self.host, self.port)

class FCGIManager:

    def __init__(self) -> None:
        self.host = current_app.config['GRS_FCGI_HOST']
        self.port = current_app.config['GRS_FCGI_PORT']
        self.cli = FastCGIClient(self.host, self.port, 3000, 0)
        self.objects : dict[str,list[any]] = {}

    def headers(self, headers:dict[str:any]) -> dict[str:any]:
        h = {
            'GATEWAY_INTERFACE': 'FastCGI/1.0',
            'SCRIPT_NAME': '/',
            'QUERY_STRING': '',
            'DOCUMENT_ROOT': '/',
            'SERVER_NAME': "localhost",
            'SERVER_PROTOCOL': 'HTTP/1.1',
            'CONTENT_TYPE': 'application/json',
            'HTTP_HOST': 'localhost'
        }
        h.update(headers)
        return h

    def get_object(self, server_id:str, name:str) -> list[any]:
        if not name in self.objects:
            res = self.send_to_server(server_id, '/object/' + name)
            answ, data = get_result_data(res)
            docs = data.get_list(name)
            self.objects[name] = docs
            
        return self.objects[name]


    def send_to_server(self, server_id:str, uri:str, method:str = 'GET', post_data:any=None, h:dict[str:any]={}) -> bytearray:
        cntlen = 0
        if post_data:
            if not isinstance(post_data, str):
                post_data = json.dumps(post_data)
            post_data = post_data.encode('utf-8')
            cntlen = len(post_data)

        h.update({
            'SERVER_CODE':server_id,
            'REQUEST_METHOD': method,
            'REQUEST_URI': uri,
            'CONTENT_LENGTH': cntlen,
        })

        headers = self.headers(h)
        res = self.cli.request(headers, post_data)
        self.cli.close()
        if not res: return bad_request(self.cli.error)
        return res
    
    def send_to_manager(self, command:str, params:dict[str:any]={}) -> bytearray:
        if params :
            params = json.dumps(params)
            command += " " + quote(params)

        headers = self.headers({
            'REQUEST_METHOD': 'GET',
            'CONTENT_LENGTH': 0,
            'REQUEST_URI': '/',
            'HTTP_GRCOMMAND': command,
        })

        res = self.cli.request(headers)
        self.cli.close()
        if not res: return bad_request(self.cli.error)
        
        pos = res.find(b'\r\n\r\n')
        if pos > 0: res = res[pos + 4:]
        return res

    @staticmethod
    def get(manager_id:str='') ->  Self :
        return FCGIManager()
        

# def send_to_server(headers:dict[str:any], uri:str, method:str, post_data:str) -> bytearray:
#     host = current_app.config['GRS_FCGI_HOST']
#     port = current_app.config['GRS_FCGI_PORT']
#     cli = FastCGIClient(host, port, 3000, 0)

#     cntlen = 0
#     if post_data:
#         post_data = post_data.encode('utf-8')
#         cntlen = len(post_data)

#     params = {
#         'GATEWAY_INTERFACE': 'FastCGI/1.0',
#               'REQUEST_METHOD': method,
#               'REQUEST_URI': uri,
#               'DOCUMENT_ROOT': '/',
#               'SERVER_NAME': "localhost",
#               'SERVER_PROTOCOL': 'HTTP/1.1',
#               'CONTENT_TYPE': 'application/json',
#               'CONTENT_LENGTH': cntlen,
#               'HTTP_HOST': 'localhost',
#     }
#     if headers :
#         params.update(headers)
    

#     res = cli.request(params, post_data)
#     cli.close()
#     if not res: return bad_request(cli.error)
#     return res

# def send(command:str, params:dict[str:any]) -> bytearray:
#     host = current_app.config['GRS_FCGI_HOST']
#     port = current_app.config['GRS_FCGI_PORT']
#     cli = FastCGIClient(host, port, 3000, 0)

#     if params :
#         params = json.dumps(params)
#         command += " " + quote(params)
    
#     params = {'GATEWAY_INTERFACE': 'FastCGI/1.0',
#               'REQUEST_METHOD': 'GET',
#               'SCRIPT_NAME': '/',
#               'QUERY_STRING': '',
#               'REQUEST_URI': '/',
#               'DOCUMENT_ROOT': '/',
#               'SERVER_NAME': "localhost",
#               'SERVER_PROTOCOL': 'HTTP/1.1',
#               'CONTENT_TYPE': 'application/json',
#               'CONTENT_LENGTH': 0,
#               'HTTP_GRCOMMAND': command,
#               'HTTP_HOST': 'localhost',
#     }

#     res = cli.request(params)
#     cli.close()
#     if not res: return bad_request(cli.error)
    
#     pos = res.find(b'\r\n\r\n')
#     if pos > 0: res = res[pos + 4:]
#     return res

class ServerAnswer:
    def __init__(self, data = None) -> None:

        if isinstance(data, Collection):
            self.data = data

            data = data[0]
            self.result = data["response"] == 1
            self._message = data['message']
        else:
            self.result = False
            self._message = ""

    @property
    def ok(self) : return self.result

    @property
    def message(self): return self._message

class JsonResult:
    def __init__(self) -> None:
        self.data : dict[str,list[dict[str,any]]] = {}

    def add(self, name:str, values) -> None:
        if not name in self.data:
            self.data[name] = []
        self.data[name].extend(values)

    def get(self, obj_name:str, el_name:str) -> any:
        val = self.get_list(obj_name)

        if val:        
            val = self.data[obj_name][0]
            return val[el_name] if el_name in val else None
        return None

    def get_list(self, obj_name:str) -> list[dict[str, any]] | None:        
        return self.data[obj_name] if obj_name in self.data else None

def get_result_data(res: bytearray) -> tuple[ServerAnswer, None|JsonResult]:
    try:
        # need check response code
        pos = res.find(b'\r\n\r\n')
        if pos != -1:
            res = res[pos + 4:].decode('utf-8')
        tres = json.loads(res)
        if isinstance(tres, Collection) :
            data = JsonResult()
            answ = ServerAnswer()
            for el in tres:
                name = el['name']
                if name == "ServerAnswer": answ = ServerAnswer(el["data"])
                else: 
                    values = el['data']
                    data.add(name, values)
            return (answ, data)
    except:
        pass
    return (ServerAnswer(), None)
    

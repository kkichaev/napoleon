from flask import jsonify
from werkzeug.http import HTTP_STATUS_CODES

def error_response(status_code, message=''):
    # payload = {'error': HTTP_STATUS_CODES.get(status_code, 'Unknown error')}
    # if message:
    #     payload['message'] = message
    response = jsonify([{"name":"ServerAnswer", "data":[{"response":0, "message":message}]}])
    response.status_code = status_code
    return response

def bad_request(message):
    return error_response(400, message)

def unauthorized(message):
    return error_response(401, message)

def good_response(obj_name:str|None = None, data:dict[str,any]|list[dict[str,any]]|None = None):
    res = [{"name":"ServerAnswer", "data":[{"response":1, "message":''}]}]

    if data != None:
        if type(data) is dict:
            data = [data]
        res.append({'name':obj_name,'data':data})

    response = jsonify(res)
    response.status_code = 200
    return response

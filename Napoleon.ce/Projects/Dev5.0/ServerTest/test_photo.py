import urllib.request

URL = 'http://localhost:8888/file/PricePhoto'
headers = {
    "Content-Type": "application/json; charset=utf-8",
    "Authorization": "Basic Mjoy"
}
data = '{"items":[{"id":"00-00000169"}]}' # Кефир 2,5%(плёнка) 900гр. 20шт.(АМ)

photoFile = ".\\test_photo.jpeg"

f = open(photoFile, 'rb')
photo = f.read()
f.close()


req = urllib.request.Request(URL, data=bytearray(data,'utf-8'), headers=headers, method="POST")
with urllib.request.urlopen(req) as response:
    photoUrl = response.read().decode('utf-8')
    
    print("Get photo url", photoUrl)
    
    print("Post photo")
    h = {"Authorization": "Basic Mjoy"}
    req = urllib.request.Request('http://localhost:8888' + photoUrl, data=photo, headers=h, method="POST")

    r = urllib.request.urlopen(req)
    print("Answer", r.read().decode("utf-8"))
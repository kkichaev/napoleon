from django.shortcuts import render

from django.http import HttpResponse
import json

from search.data_collector import UrlData, getUrls, openDB

# Create your views here.

def makeUrls(query:str) -> list[UrlData]:
   db = openDB()
   urls = getUrls(db, query)
   return urls

def search(request):
   query = request.GET.get('q', '')
   # print(query)

   urls = makeUrls(query)
   # db = openDB()
   # tokens = Tokens(db)
   # t = tokens.getTokens(query)
   # urls = tokens.getUrls(t)

   data = [x.__dict__ for x in urls]

   result = {'result':1, 'data':data, 'query':query}
   return HttpResponse(json.dumps(result), content_type='application/json')

def index(request):
   return render(request, 'index.html', {})

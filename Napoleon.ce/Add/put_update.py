import xml.etree.ElementTree as ET
import os.path, time
import re
from subprocess import call

import random
import string
import sys
import mimetypes
import urllib2
import httplib
import urllib
import datetime
from time import strptime

def random_string (length):
    return ''.join (random.choice (string.letters) for ii in range (length + 1))

def encode_multipart_data (data, files):
    boundary = random_string (30)

    def get_content_type (filename):
    	return mimetypes.guess_type (filename)[0] or 'application/octet-stream'

    def encode_field (field_name):
    	return ('--' + boundary,
    	        'Content-Disposition: form-data; name="%s"' % field_name,
    	        '', str (data [field_name]))

    def encode_file (field_name):
    	filename = files [field_name]
    	return ('--' + boundary,
    	        'Content-Disposition: form-data; name="%s"; filename="%s"' % (field_name, filename),
    	        'Content-Type: %s' % get_content_type(filename),
    	        '', open (filename, 'rb').read ())

    lines = []
    for name in data:
    	lines.extend (encode_field (name))
    for name in files:
    	lines.extend (encode_file (name))
    lines.extend (('--%s--' % boundary, ''))
    body = '\r\n'.join (lines)

    headers = {'content-type': 'multipart/form-data; boundary=' + boundary,
               'content-length': str (len (body))}

    return body, headers

def send_post (url, data, files):
    req = urllib2.Request (url)
    connection = httplib.HTTPConnection (req.get_host ())
    connection.request ('POST', req.get_selector (),
                        *encode_multipart_data (data, files))
    response = connection.getresponse ()
    print 'Code: ' + str(response.status) + ' ' + response.reason
    print 'response = '  + response.read ()

def check_older(v, p, d):
    link = 'http://212.232.41.126/upgrade/projects.php'
    f = urllib.urlopen(link)
    h = f.read()
    
    table = h[h.find("<tr>"):h.rfind("</tr>")+len("</tr>")]

    for a in table.split("<tr>"):
        c = a.split("<td>")
        
        if len(c) < 4:
            continue
            
        sp = c[1][0:c[1].rfind("</td>")]
        
        if len(c) > 4 and p == sp:
            cv = c[2][0:c[2].rfind("</td>")]
                        
            a1 = v.split(".")
            a2 = cv.split(".")
            
            incremented = False
            
            for i, aa in enumerate(a1):
                if int(a2[i]) < int(aa):
                    incremented = True
                    break
            
            if not incremented:
                raise Exception('version is not incremented')
            
            cd = c[4][0:c[4].rfind("</td>")]
            t = strptime(cd,"%d-%m-%Y %H:%M:%S")
            
            if d < time.mktime(t):
                raise Exception('server version is older than downloads')
                
            return;
            
version = None
project = None
bin_file = None
bin_name = None

if os.path.exists('app/build.gradle'):
   with open('app/build.gradle', 'r') as myfile: 
      data = myfile.read()
      srch = re.compile('outputFileName = "(.*)"')
      fnd = srch.search(data)
      if fnd != None:
          bin_name = fnd.group(1)
          bin_file = "app/build/outputs/apk/release/" + bin_name
          cmpl_file = 'app/src/main/res/values/compilation_info.xml'

else:
   e = ET.parse('build.xml').getroot()
   for proj in e.iter('project'):
      bin_name = proj.attrib.get('name')

      cmpl_file = 'res\\values\\compilation_info.xml'
      bin_file = 'bin\\' + bin_name + '.apk'


if bin_name == None:
   raise Exception('No project')

e = ET.parse(cmpl_file).getroot()
for el in e.findall(".*[@name='version']") :
    version = el.text.split(' ')[0]
    break

for el in e.findall(".*[@name='project']") :
    project = el.text
    break


if version == None or project == None:
   raise Exception('No data')

print "ver " + version + " proj " + project

if check_older(version, project, os.path.getmtime(bin_file)):
   raise Exception('Older bin')
data = {'project' : project, 'version' : version, 'category' : 'Android' }
files = {'filename' : bin_file }

send_post('http://212.232.41.126/upgrade/load.php', data, files)


import xml.etree.ElementTree as ET
import os.path, time
from subprocess import call

import random
import string
import sys
import mimetypes
import urllib2
import httplib

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
    print 'response = '  + response.read ()
    print 'Code: ' + str(response.status) + ' ' + response.reason


e = ET.parse('build.xml').getroot()
bin_name = None
for proj in e.iter('project'):
   bin_name = proj.attrib.get('name')
if bin_name == None:
   raise Exception('No project')


cmpl_file = 'res\\values\\compilation_info.xml'
bin_file = 'bin\\' + bin_name + '-release.apk'

if time.ctime(os.path.getmtime(bin_file)) < time.ctime(os.path.getmtime(cmpl_file)) :
   raise Exception('Older bin')

e = ET.parse(cmpl_file).getroot()

version = None
project = None

for el in e.findall(".*[@name='version']") :
   version = el.text.split(' ')[0]
   break

for el in e.findall(".*[@name='project']") :
   project = el.text
   break

if version == None or project == None:
   raise Exception('No data')

print "ver " + version + " proj " + project

data = {'project' : project, 'version' : version, 'category' : 'Android' }
files = {'filename' : bin_file }

send_post('http://212.232.41.126/upgrade/load.php', data, files)


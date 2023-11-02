import os
import xml.etree.ElementTree as ET


e = ET.parse('build.xml').getroot()
bin_name = None
for proj in e.iter('project'):
   bin_name = proj.attrib.get('name')
if bin_name == None:
   raise Exception('No project')
n = bin_name
n += "-release.apk"
dir = "./bin/"

os.system("adb install -r " + dir + n);

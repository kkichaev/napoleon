import grserver;

grserver.connect('127.0.0.1', 8888)
print("Connected!")

obj = grserver.get("Org");
if obj == None:
	print "Got error: " + grserver.error();
	exit(1);

for x in obj:
	print "id: " + x.id + " name: " + x.name;

grserver.close()
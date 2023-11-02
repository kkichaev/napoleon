

from datetime import datetime


def run(server):    
    entries = [
        {
            # 'starting':datetime.now(),
            'cycle':False,
            # 'second':10
            'minute':30,
        },
        {
            # 'starting':datetime.now(),
            'cycle':False,
            # 'second':10
            'minute':8,
        },
        {
            # 'starting':datetime.now(),
            'cycle':False,
            # 'second':10
            'minute':46,
        }
    ]
    schedule = {
        'id' : 'TestTask',
        'name' : 'Schedule test',
        'description' : 'Test schedule',
        'module' : 'sch_run',
        'params' : '',
        'entries' : entries
    }

    schId = 'TestTask'
    task = server.GetTask(schId)
    print(schId, task)
    
    status = server.TaskStatus(schId)
    # 0 - not found
    # 1 - scheduled
    # 2 - runnging
    print(schId, status)

    # if not server.Schedule(schedule, True):
    #     print("Error while schedule")
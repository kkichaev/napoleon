from datetime import datetime, timedelta
import logging
import sys

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('starting')

    schId = 'CheckDeviations'    

    status = server.TaskStatus(schId)
    # 0 - not found
    # 1 - scheduled
    # 2 - runnging
    if status != 1:
        logging.info('Register task' + schId)

        nextStart = datetime.now() + timedelta(seconds=10)
        entries = [
            {
                #'starting':datetime.now() + timedelta(seconds=10),
                'cycle':False,
                'second':nextStart.second,
                'minute':nextStart.minute,
                'hour':nextStart.hour,
                'day':nextStart.day,
                'month':nextStart.month,
            },
        ]
        schedule = {
            'id' : schId,
            'name' : 'Sends route deviations',
            'description' : 'Sends route deviations to managers',
            'module' : 'sends_deviations',
            'params' : '',
            'entries' : entries
        }
        server.Schedule(schedule, False)
    else:
        logging.info('Task ' + schId + 'exists no action')

    logging.info('end')

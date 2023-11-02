#pragma once

#include <vector>
#include <map>
#include <string>
#include <thread>

#include "sch_service.h"
#include "event.h"

namespace GRServer 
{

class Object;
class Dispatcher;
class Session;

class SchedulerManager : public IScheduler
{
public:
	SchedulerManager(Dispatcher* owner);
	~SchedulerManager() { Clear(); }

	bool Starting();
	void Clear();

	void Reload();

	virtual Scheduler* Get(const std::wstring& id) const;
	virtual bool Put(const Scheduler& sch, bool putToDB);

	virtual bool IsRunning(const std::wstring &taskid) const;
	virtual bool Remove(const std::wstring& id, bool removeFromDB);

	void RunTask(const Scheduler* task);

	typedef std::pair<Scheduler*, std::thread::native_handle_type> ScheduleData;
	typedef std::map<std::wstring, ScheduleData> ThreadMap;

private:
	Dispatcher* owner;

	std::vector<const Scheduler*> running;
	ThreadMap tasks;

	void ScheduleTask(const Scheduler* task);
	ThreadMap::const_iterator Find(const std::wstring& id) const;
	
	void WriteToDB(const Scheduler* task) const;
	void RemoveFromDB(const std::wstring& id) const;

	void RemoveEntry(ScheduleData& sch);
	//void StopingThread(ScheduleData sch);

	Session* CreateSession() const;
};

} // namespace GRServer
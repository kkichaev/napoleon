#pragma once

#include <string>
#include <vector>
#include <time.h>

#define SCHEDULE_SERVICE L"ScheduleService"
namespace GRServer {
class Object;
class Session;
class Scheduler
{
public:
	Scheduler(const Scheduler& src);
	Scheduler() : removeAfterRun(false) {}

	class Entry
	{
	public:
		int64_t starting;  // utc unix time 
		bool cycle;

		// -1 not user field
		// minute = 1
		// cycle == true - every one minute
		// cycle == false every first minute of hour

		int second;
		int minute;
		int hour;
		int day;
		int month;

		Entry() {
			starting = 0;
			cycle = false;
			second = minute = hour = day = month = -1;
		}

		struct Field
		{
			const wchar_t* name;
			int Scheduler::Entry::* ptr;

			Field(const wchar_t* n, int Scheduler::Entry::* p) { name = n; ptr = p; }
		};

		static Field* fields() {
			static Field f[] = {
				Field(L"second", &Scheduler::Entry::second),
				Field(L"minute", &Scheduler::Entry::minute),
				Field(L"hour", &Scheduler::Entry::hour),
				Field(L"day", &Scheduler::Entry::day),
				Field(L"month", &Scheduler::Entry::month),
				Field(L"", NULL),
			};
			return f;
		}

		int64_t NextStart(time_t now) const;

		void Load(const Object& src);	
		void Set(Object* dest) const;

		bool SetIncTime(::tm* ctimes, bool nextDate) const;
	};

	std::vector<Entry> entries;

	std::wstring id;
	std::wstring name;
	std::wstring description;
	std::wstring module;
	std::wstring params;

	bool removeAfterRun;

	struct Field
	{
		const wchar_t* name;
		std::wstring Scheduler::* ptr;

		Field(const wchar_t* n, std::wstring Scheduler::* p) { name = n; ptr = p; }
	};

	static Field* fields() {
		static Field f[] = {
			Field(L"id", &Scheduler::id),
			Field(L"name", &Scheduler::name),
			Field(L"description", &Scheduler::description),
			Field(L"module", &Scheduler::module),
			Field(L"params", &Scheduler::params),
			Field(L"", NULL),
		};
		return f;
	}


	static Scheduler* Load(const Object& src);

	void Set(Session* s,	Object* dest) const;

	// in milliseconds
	int64_t NextStartInterval(time_t now) const;

	bool operator==(const Scheduler& src) const { return id.compare(src.id) == 0; }

private:
};

class IScheduler
{
public:
	virtual ~IScheduler() {}

	virtual Scheduler* Get(const std::wstring& id) const = 0;
	virtual bool Put(const Scheduler& sch, bool putToDB) = 0;
	virtual bool Remove(const std::wstring& id, bool removeFromDB) = 0;
	virtual bool IsRunning(const std::wstring& taskid) const = 0;
};
}
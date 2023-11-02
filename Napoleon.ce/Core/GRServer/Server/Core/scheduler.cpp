#include "stdafx.h"

#include "scheduler.h"

#include <chrono>
#include <functional>
#include <mutex>
#include <algorithm>

#include "server.h"
#include "session.h"
#include "srvdata.h"
#include "dispatcher.h"
#include "event.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

static std::mutex mutex;

static std::thread::native_handle_type RunTask(const Scheduler* task, int64_t interval, SchedulerManager* manager)
{
   std::thread th = std::thread([interval, manager, task]()
   {
      std::this_thread::sleep_for(std::chrono::milliseconds(interval));
      manager->RunTask(task);
   });
   auto ret = th.native_handle();
   th.detach();

   return ret;
}

Scheduler::Scheduler(const Scheduler& src)
{
   if (&src != this)
   {
      (*this) = src;
   }
}

int64_t Scheduler::NextStartInterval(time_t now) const
{
   int64_t res = -1;
   auto i = entries.begin();
   for (; i != entries.end(); i++)
   {
      int64_t ni = (*i).NextStart(now);
      if (res < 0 || res > ni)
         res = ni;
   }

   return res * 1000;
}

inline bool CanIncDay(::tm* ctime)
{
   int mon = ctime->tm_mon;
   if (mon == 0 || mon == 2 || mon == 4 || mon == 6 || mon == 7 || mon == 9 || mon == 11)
      return ctime->tm_mday < 31;
   if (mon != 1)
      return ctime->tm_mday < 30;

   int year = ctime->tm_year + 1900;
   bool isLeap = ((year % 4) == 0 && (year % 100) == 0 && (year % 400) == 0);
   return isLeap ? ctime->tm_mday < 28 : ctime->tm_mday < 27;
}

bool Scheduler::Entry::SetIncTime(::tm* ctime, bool nextDate) const
{
   if (nextDate)
   {
      //if (second < 0)
      //{
      //   if (ctime->tm_sec < 59)
      //   {
      //      ctime->tm_sec++;
      //      return true;
      //   }
      //   ctime->tm_sec = 0;
      //}
      if (minute < 0)
      {
         if (ctime->tm_min < 59)
         {
            ctime->tm_min++;
            return true;
         }
         ctime->tm_min = 0;
      }
      if (hour < 0)
      {
         if (ctime->tm_hour < 23)
         {
            ctime->tm_hour++;
            return true;
         }
         ctime->tm_hour = 0;
      }
      if (day < 0)
      {
         if (CanIncDay(ctime))
         {
            ctime->tm_mday++;
            return true;
         }
         ctime->tm_mday = 1;
      }
      if (month < 0)
      {
         if (ctime->tm_mon < 11)
         {
            ctime->tm_mon++;
         }
         else
         {
            ctime->tm_mon = 1;
            ctime->tm_year++;
         }
         return true;
      }
      return false;
   }
   else
   {
      if (second >= 0)
         ctime->tm_sec = second;
      if (minute >= 0)
         ctime->tm_min = minute;
      if (hour >= 0)
         ctime->tm_hour = hour;
      if (day > 0 && day < 29)
         ctime->tm_mday = day;
      if (month > 0)
         ctime->tm_mon = month - 1;
      return true;
   }
}

int64_t Scheduler::Entry::NextStart(time_t now) const
{
   if (starting > now)
   {
      return starting - now;
   }

   if (cycle)
   {
      int64_t interval = 0;
      if (second > 0)
         interval += second;
      if (minute > 0)
         interval += minute * 60;
      if (hour > 0)
         interval += hour * 3600;
      if (day > 0)
         interval += day * 24 * 3600;

      if (interval == 0)
         return 0;

      int64_t gap = interval - (now - starting) % interval;
      return gap;
   }

#ifdef UNIX
   tm ctime;
   localtime_r(&now, &ctime);
#else
   ::tm ctime;
   localtime_s(&ctime, &now);
#endif
   ctime.tm_sec = 0;
   SetIncTime(&ctime, false);

   while (true)
   {
      time_t checkTime = mktime(&ctime);
      if (checkTime > now)
         return checkTime - now;
      if (!SetIncTime(&ctime, true))
      {
         return 0;
      }
   }
}

//Scheduler::Entry::Field Scheduler::Entry::fields[] = {
//   Field(L"second", &Scheduler::Entry::second),
//   Field(L"minute", &Scheduler::Entry::minute),
//   Field(L"hour", &Scheduler::Entry::hour),
//   Field(L"day", &Scheduler::Entry::day),
//   Field(L"month", &Scheduler::Entry::month),
//   Field(L"", NULL),
//};
//

void Scheduler::Entry::Load(const Object& src)
{
   Field* flds = fields();
   for (int i = 0; flds[i].name && *flds[i].name; i++)
   {
      Field& el = flds[i];
      const Member* m = src[el.name];
      if (m != NULL)
      {
         this->*el.ptr = m->number < 0 ? -1 : (int)(m->number + 0.005);
      }
   }

   const Member* m = src[L"starting"];
   if (m != NULL)
      starting = (int64_t)(m->number + 0.005);

   m = src[L"cycle"];
   if (m != NULL)
      cycle = m->number != 0;
}

void Scheduler::Entry::Set(Object* dest) const
{
   Field* flds = fields();
   for (int i = 0; flds[i].name && *flds[i].name; i++)
   {
      Field& el = flds[i];
      Member* m = (*dest)[el.name];
      if (m != NULL)
      {
         m->number = this->*el.ptr;
      }
   }

   Member* m = (*dest)[L"starting"];
   if (m != NULL)
      m->number = (double)starting;

   m = (*dest)[L"cycle"];
   if (m != NULL)
      m->number = cycle ? 1 : 0;
}


Scheduler* Scheduler::Load(const Object& src)
{
   Scheduler* ret = new Scheduler();
   Scheduler::Field* flds = fields();
   for (int i = 0; flds[i].name && *flds[i].name; i++)
   {
      Field& el = flds[i];
      const Member* m = src[el.name];
      if (m != NULL) {
         (ret->*el.ptr).assign((const std::wstring&)*m->str);
      }
   }

   const Member* m = src[L"items"];
   if (m != NULL && m->object != NULL)
   {
      auto oi = m->object->begin();
      for (; oi != m->object->end(); oi++)
      {
         Entry e;
         e.Load(*(*oi));
         ret->entries.push_back(e);
      }
   }

   return ret;
}

void Scheduler::Set(Session* s, Object* dest) const
{
   Scheduler::Field* flds = fields();
   for (int i = 0; flds[i].name && *flds[i].name; i++)
   {
      Field& el = flds[i];
      const Member* m = (*dest)[el.name];
      if (m != NULL) {
         m->str->assign(this->*el.ptr);
      }
   }

   Member* m = (*dest)[L"items"];
   m->object = new SessionObject(L"ServerTaskScheduler$items", s);
   auto oi = entries.begin();
   for (; oi != entries.end(); oi++)
   {
      Object* d = m->object->AddObject();
      (*oi).Set(d);
   }
}

SchedulerManager::SchedulerManager(Dispatcher* owner)
{
   this->owner = owner;
}

void SchedulerManager::ScheduleTask(const Scheduler* task)
{
   time_t now = time(NULL);
   int64_t next = task->NextStartInterval(now);

   if (next > 0)
   {
      std::stringstream ss;
      unsigned ms = (unsigned)(next / 1000);
      unsigned h = (ms / 3600);

      ms %= 3600;
      unsigned m = ms / 60;
      unsigned sec = ms % 60;
      if (h > 0) ss << h << " h ";
      if (m > 0) ss << m << " min ";
      if (sec > 0) ss << sec << " sec ";


      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Task %s scheduled after %s"
         , W2A_CP(task->id.c_str(), CP_UTF8)
         , ss.str().c_str());

      auto handle = ::RunTask(task, next, this);

      std::lock_guard<std::mutex> guard(mutex);
      tasks[task->id] = ScheduleData(const_cast<Scheduler*>(task), handle);
   }
   else
   {
      std::lock_guard<std::mutex> guard(mutex);

      tasks.erase(task->id);
      delete task;
   }
}

bool SchedulerManager::IsRunning(const std::wstring& taskid) const
{
   std::lock_guard<std::mutex> guard(mutex);

   auto cmpName = [taskid](const Scheduler* s) { return s->id.compare(taskid) == 0; };
   auto fnd = std::find_if(running.begin(), running.end(), cmpName);

   return fnd != running.end();
}

Scheduler* SchedulerManager::Get(const std::wstring& id) const
{
   ThreadMap::const_iterator fnd = Find(id);
   return (fnd == tasks.end()) ? NULL : fnd->second.first;
}

SchedulerManager::ThreadMap::const_iterator SchedulerManager::Find(const std::wstring& id) const
{
   std::lock_guard<std::mutex> guard(mutex);
   return tasks.find(id);
}

//void SchedulerManager::StopingThread(ScheduleData sch)
//{
//#ifdef UNIX
//   pthread_cancel(sch.second);
//#else
//   TerminateThread((HANDLE)sch.second, 0);
//#endif
//}

bool SchedulerManager::Remove(const std::wstring& id, bool removeFromDB)
{
   auto fnd = Find(id);
   if (fnd == tasks.end())
      return true;

   RemoveEntry(const_cast<SchedulerManager::ScheduleData&>(fnd->second));
   
   if (removeFromDB)
      RemoveFromDB(id);

   std::lock_guard<std::mutex> guard(mutex);
   tasks.erase(fnd);

   USES_CONVERSION;
   gServer->AddLog(IErrorLogger::Full, "Task %s removed", W2A_CP(id.c_str(), CP_UTF8));

   return true;
}

bool SchedulerManager::Put(const Scheduler& sch, bool putToDB)
{
   Remove(sch.id, true);

   Scheduler* s = new Scheduler(sch);

   if(putToDB)
      WriteToDB(s);

   USES_CONVERSION;
   gServer->AddLog(IErrorLogger::Full, "Task %s added", W2A_CP(sch.id.c_str(), CP_UTF8));

   ScheduleTask(s);
   return true;
}

bool SchedulerManager::Starting()
{
   Reload();

   return true;
}

void SchedulerManager::Reload()
{
   Clear();

   Session* s = CreateSession();

   ISessionObject* so = s->LoadObject(L"ServerTaskScheduler", NULL);
   if (so != NULL)
   {
      const ServObject* svo = so->Self();
      auto si = svo->begin();
      for (; si != svo->end(); si++)
      {
         Scheduler* sch = Scheduler::Load(*(*si));
         if (sch != NULL)
            ScheduleTask(sch);
      }
   }
   delete s;
}

Session* SchedulerManager::CreateSession() const{
   std::wstring pwd;
   ServerData::GetCOMPassword(&pwd);
   Session* s = new Session(owner);
   s->Auth(COM_LOGIN, pwd.c_str());
   return s;
}

void SchedulerManager::WriteToDB(const Scheduler* task) const
{
   Session* s = CreateSession();

   SessionObject* so = s->Build(L"ServerTaskScheduler", false);
   if (so != NULL)
   {
      Object* o = so->AddObject();
      task->Set(s, o);

      so->Writing();
      delete so;
   }
   delete s;
}

void SchedulerManager::RemoveFromDB(const std::wstring& id) const
{
   Session* s = CreateSession();

   SessionObject* so = s->Build(L"ServerTaskScheduler", false);
   if (so != NULL)
   {
      std::wstring filter;
      filter.append(L"\"id\"='").append(id).append(L"'");
      so->Removing(filter.c_str());
      delete so;
   }
   delete s;
}

void SchedulerManager::RemoveEntry(ScheduleData& sch)
{
   sch.first->removeAfterRun = true;
   //if (!IsRunning(sch.first->id))
   //{
   //   StopingThread(sch);
   //   delete sch.first;
   //}
}

void SchedulerManager::Clear()
{
   auto ti = tasks.begin();
   for (; ti != tasks.end(); ti++)
   {
      RemoveEntry(ti->second);
   }
   tasks.clear();
}

void SchedulerManager::RunTask(const Scheduler* task)
{
   if (task->removeAfterRun)
   {
      delete task;
      return;
   }

   {
      std::lock_guard<std::mutex> guard(mutex);
      running.push_back(task);
   }

   USES_CONVERSION;

   gServer->AddLog(IErrorLogger::Full, "Task %s is starting", W2A_CP(task->id.c_str(), CP_UTF8));

   // run ....

   try
   {
      CString name(task->module);
      Member rn;
      rn.str = &name;

      Session* s = CreateSession();
      owner->HandleCommand(GET_REPORT, &rn, s);

      delete s;
   }
   catch (...)
   {
      gServer->AddLog(IErrorLogger::Full, "Exception while run scheduled task");
   }

   gServer->AddLog(IErrorLogger::Full, "Task %s finished", W2A_CP(task->id.c_str(), CP_UTF8));

   {
      std::lock_guard<std::mutex> guard(mutex);
      auto i = std::find(running.begin(), running.end(), task);
      if (i != running.end())
         running.erase(i);
   }

   if (task->removeAfterRun)
   {
      delete task;
   }
   else
   {
      ScheduleTask(task);
   }
}
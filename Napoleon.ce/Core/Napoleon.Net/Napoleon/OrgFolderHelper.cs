using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Napoleon
{
    class OrgFolderHelper
    {
        private int GetCurWeekIdx(DateTime date)
        {
            int result = -1;

            DateTime dt = new DateTime(DateTime.Now.Year, 1, 1);
            TimeSpan ts = new TimeSpan(date.Ticks);
            ts = ts.Subtract(new TimeSpan(dt.Ticks));
            if (ts.TotalDays >= 0)
                result = (int)(ts.TotalDays / 7) % 4 + 1;

            //string ss = ConfigHelper.GetValue(ConfigHelper.SHEDULE_START).Trim();
            //if (ss.Length > 0)
            //{
            //    try
            //    {
            //        //DateTime dt = DateTime.ParseExact(ss, "yyyy-MM-dd", null);
            //    }
            //    catch { }
            //}

            return result;
        }

        private List<OrgFolderItem> GetFolderItems(int currentWeek, string day)
        {
            List<OrgFolderItem> result = new List<OrgFolderItem>();

            List<OrgFolder> of = Update.GetStoredList<OrgFolder>(OrgFolder.OBJECT_NAME);

            foreach (OrgFolder f in of)
            {
                if (f.name.Equals(day))
                {
                    result.AddRange(f.items);
                    break;
                }
                else if (f.name.Length > 1 ? f.name.Substring(1).Equals(day) : false)
                {
                    int cw = -1;
                    Int32.TryParse(f.name.Substring(0, 1), out cw);
                    if (currentWeek < 0 || cw == currentWeek)
                    {
                        result.AddRange(f.items);
                        break;
                    }
                }
            }

            return result;
        }

        public List<OrgFolderItem> GetAgentRoute(DateTime date)
        {
            return GetFolderItems(GetCurWeekIdx(date), GetDayNameByIndex((int)date.DayOfWeek));
        }

        protected string GetDayNameByIndex(int day)
        {
            string[] days = new string[] { "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота" };
            return days[day];
        }
    }


}

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ClientCardEx 
   {
      public static void DoReport(Form owner, DateTime start, DateTime finish, string userid)
      {
         Data data = new Data();
         data.start = start;
         data.finish = finish;
         data.userid = userid;

         ReportResult.DoReport("clientcard", data, owner);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userid = string.Empty;
      }
   }
}

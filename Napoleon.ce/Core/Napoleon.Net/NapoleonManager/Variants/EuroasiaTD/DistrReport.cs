using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DistrReport
   {
      class RepParam : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public string users = string.Empty;
         public string folderid = string.Empty;
         public string division = string.Empty;
      }

      public void Do(Form owner, FmDistrRepParam param)
      {
         RepParam rp = new RepParam();
         rp.start = param.Start;
         rp.finish = param.Finish;
         rp.users = param.UserIDS;
         rp.folderid = param.FolderID;
         rp.division = param.Division;

         ReportResult.DoReport("distrib_report", rp, owner);
      }

   }
}

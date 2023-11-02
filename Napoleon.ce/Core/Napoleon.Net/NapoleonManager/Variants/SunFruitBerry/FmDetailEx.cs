using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{

   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      private DataSet<int, AgentMemo> dsAgemtMemo;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {

         dsAgemtMemo = (DataSet<int, AgentMemo>)DataModule.Get(AgentMemo.OBJECT_NAME) ?? new DataSet<int, AgentMemo>(AgentMemo.OBJECT_NAME);
         documents.Add(new DocumentInfo(dsAgemtMemo, ObjType.TObjType.AgentMemo));

         //List<DocView> views = new List<DocView>(docViews);
         //views.Add(new DocView(AgentMemo.OBJECT_NAME, "Оборудование", typeof(AgentMemo)));
         //docViews = views.ToArray();
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsAgemtMemo.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsAgemtMemo);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }
      
   }
}

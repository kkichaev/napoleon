using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{

   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      private DataSet<int, Equipment> dsEquip;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {

         dsEquip = (DataSet<int, Equipment>)DataModule.Get(Equipment.OBJECT_NAME) ?? new DataSet<int, Equipment>(Equipment.OBJECT_NAME);
         documents.Add(new DocumentInfo(dsEquip, ObjType.TObjType.Equipment));

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Equipment.OBJECT_NAME, "Оборудование", typeof(EquipOverview)));
         docViews = views.ToArray();
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsEquip.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsEquip);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.Collections;
using System.Drawing;
using System.IO;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      protected SimpleDataSet<MonitoringW> dsMonitoring = null;
      protected SimpleDataSet<RivalMonitoring> dsRivalMonitoring = null;
      protected SimpleDataSet<Merchendizing> dsMerch = null;
      protected DataSet<string, ManagerFolder> dsFolder;
      protected DataSet<string, Price> dsRivalPrice;

#if MorozkoSPBMonitor
      string filterBase;
#endif

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
#if MorozkoSPBMonitor
         filterBase = COMMON_FILTER_STR;
         tbnMessage.Visible = false;
         tsReportMenu.Visible = false;
         dgvDetail.ContextMenuStrip = null;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
#endif
         dsMonitoring = (SimpleDataSet<MonitoringW>)DataModule.Get(MonitoringW.OBJECT_NAME) ?? new SimpleDataSet<MonitoringW>(MonitoringW.OBJECT_NAME);
         dsMerch = (SimpleDataSet<Merchendizing>)DataModule.Get(Merchendizing.OBJECT_NAME) ?? new SimpleDataSet<Merchendizing>(Merchendizing.OBJECT_NAME);
         dsFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, true);

         dsRivalMonitoring = (SimpleDataSet<RivalMonitoring>)DataModule.Get(RivalMonitoring.OBJECT_NAME) ?? new SimpleDataSet<RivalMonitoring>(RivalMonitoring.OBJECT_NAME);

         dsRivalPrice = new DataSet<string, Price>("RivalPrice", false);

         List<DocView> docs = new List<DocView>(docViews);
         ObjType ot = new ObjType(ObjType.TObjType.Monitoring);
         docs.Add(new DocView(MonitoringW.OBJECT_NAME, ot.ToString(), typeof(FmMonitoringView)));
         ot = new ObjType(ObjType.TObjType.Merch);
         docs.Add(new DocView(Merchendizing.OBJECT_NAME, ot.ToString(), typeof(FmMerchView)));
         
         ot = new ObjType(ObjType.TObjType.RivalMonitoring);
         docs.Add(new DocView(RivalMonitoring.OBJECT_NAME, ot.ToString(), typeof(FmMonitoringView)));

         docViews = docs.ToArray();

         documents.Add(new DocumentInfo(dsMonitoring, ObjType.TObjType.Monitoring));
         documents.Add(new DocumentInfo(dsMerch, ObjType.TObjType.Merch));
         documents.Add(new DocumentInfo(dsRivalMonitoring, ObjType.TObjType.RivalMonitoring));
      }

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
#if MorozkoSPBMonitor
         COMMON_FILTER_STR = "\"userid\"='{3}' and " + ((MainFormEx)MainForm.Instance).GetMonitorFilter(agentID);
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd.AddDays(1));
#else
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd);
#endif
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsMerch.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsFolder.Filter = "\"userid\" is null or \"userid\"=''";
         dsRivalMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsFolder);
         updSets.Add(dsRivalPrice);
         updSets.Add(dsMonitoring);
         updSets.Add(dsRivalMonitoring);
         updSets.Add(dsMerch);

#if MorozkoSPBMonitor
         dsScriptDoc.Filter = String.Format(filterBase + " and " + ((MainFormEx)MainForm.Instance).ScriptFilter(), "created", dateBegin, dateEnd, agentID);
#endif
      }


#if MorozkoSPBMonitor

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrderDetailEx(documents);
      }
#else
      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }
#endif

#if MorozkoSPBMonitor
   class OrderDetailEx : ScriptDetail
   {
      public OrderDetailEx(List<DocumentInfo> documents)
         : base(documents)
      {
      }

      protected override bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes)
      {
         return false;
      }
   }
#endif
   }
}

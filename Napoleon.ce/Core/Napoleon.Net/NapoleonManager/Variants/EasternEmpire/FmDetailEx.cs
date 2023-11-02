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
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<Distrib> dsDistrib = new SimpleDataSet<Distrib>(Distrib.OBJECT_NAME);

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         documents.Add(new DocumentInfo(dsDistrib, ObjType.TObjType.Distr));

         ToolStripMenuItem  wtReport = new ToolStripMenuItem("Отчет о работе");
         wtReport.Click += new EventHandler(wtReport_Click);
         tsReportMenu.DropDownItems.Add(wtReport);

         List<DocView> docs = new List<DocView>(docViews);
         ObjType ot = new ObjType(ObjType.TObjType.Distr);
         docs.Add(new DocView(Distrib.OBJECT_NAME, ot.ToString(), typeof(FmDistribView)));
         docViews = docs.ToArray();
      }

      private void wtReport_Click(object sender, EventArgs e)
      {
         WorkTimeReport.Do(GetDateForStartPeriod(), GetDateForEndPeriod(), this);
      }

      void Init()
      {
///         detailPanel.Controls.Add(dgvDetailItems);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsDistrib.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsDistrib);
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Distrib dd = odr.StoreObject as Distrib;
         if( dd != null )
         {
            String text = String.Format("Наши фейсы: {0}\nЧужие фейсы: {1}", dd.outFaces, dd.theirFaces);
            tbVisitText.Text = text;
            return tbVisitText;
         }
         return null; 
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }


   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      private DataSet<int, Facing> dsFacing;

      public FmDetailEx(FmDetailData detailData) : base(detailData)
      {
#if FOCUSED_GROUP
         FocusReport.FocusItemIsFolder = false;
#endif
         ContextMenuStrip menu = new ContextMenuStrip();

         menu.Items.Add("Создать задачу", null, new EventHandler(delegate(object sender, EventArgs arg)
            {
               OrgTaskInfo info = new OrgTaskInfo();
               OrderDetailRepresentation o = (OrderDetailRepresentation)dgvDetail.CurrentRow.DataBoundItem;
               info.id = o.NOrg.id;
               info.name = o.NOrg.name;
               Agent a = GetSelectedAgent();

               if(a != null)
                  FmAgentTaskList.ShowForm(info, dtpBegin.Value.Date, dtpEnd.Value.Date, a.id);
            }));

         dgvDetail.ContextMenuStrip = menu;

         dsFacing = (DataSet<int, Facing>)DataModule.Get(Facing.OBJECT_NAME) ?? new DataSet<int, Facing>(Facing.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsFacing, ObjType.TObjType.Facing));

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Facing.OBJECT_NAME, "Фейсинг", typeof(FacingControl)));
         docViews = views.ToArray();

      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsFacing.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsFacing);
      }

      private Control FindDetailControl(DocView dv)
      {
         Control result = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(dv.viewer.Name))
            {
               result = cc;
               break;
            }

         return result;
      }

      protected override void UpdateDetail(OrderDetailRepresentation odr)
      {
         Control c = RefreshDetail(odr);

         if (c != null)
            c.BringToFront();
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;

         DocView dv = GetDocView(Enum.GetName(typeof(ObjType.TObjType), odr.Doctype.Val));

         if (dv != null)
         {
            result = FindDetailControl(dv); ;

            if (result == null)
            {
               result = dv.MakeControl();
               detailPanel.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }

         return result;
      }


   }
}
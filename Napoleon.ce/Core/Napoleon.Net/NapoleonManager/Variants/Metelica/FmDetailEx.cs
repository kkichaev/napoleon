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
      public FmDetailEx(FmDetailData detailData) : base(detailData)
      {
#if FOCUSED_GROUP
         FocusReport.FocusItemIsFolder = false;
#endif
         ContextMenuStrip menu =  dgvDetail.ContextMenuStrip;

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
      }

      protected override void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         if (GetOrder(dgvDetail.CurrentRow) == null && GetIncass(dgvDetail.CurrentRow) == null)
            miMakeDup.Visible = false;
         else
            miMakeDup.Visible = true;
      }
   }
}
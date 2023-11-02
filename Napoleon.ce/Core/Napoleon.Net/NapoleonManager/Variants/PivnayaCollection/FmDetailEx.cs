using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      private DataSet<int, InvAudit> dsInvAudit;
      DataSet<string, Inventory> dsInventory;
      SimpleDataSet<Tare> dsTare;

      private InvAuditCtrl ctrl;
      private DocView iadv, tareView;
      TareView tareCtrl;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsInvAudit = (DataSet<int, InvAudit>)DataModule.Get(InvAudit.OBJECT_NAME) ?? new DataSet<int, InvAudit>(InvAudit.OBJECT_NAME);
         dsTare = (SimpleDataSet<Tare>)DataModule.Get(Tare.OBJECT_NAME) ?? new SimpleDataSet<Tare>(Tare.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsInvAudit, ObjType.TObjType.InvAudit));
         documents.Add(new DocumentInfo(dsTare, ObjType.TObjType.Tare));
         dsInventory = new DataSet<string, Inventory>(Inventory.OBJECT_NAME, false);

         ctrl = new InvAuditCtrl();
         ctrl.Dock = System.Windows.Forms.DockStyle.Fill;
         detailPanel.Controls.Add(ctrl);
         iadv = new DocView(InvAudit.OBJECT_NAME, "Аудит оборудования", typeof(InvAuditCtrl));


         ToolStripMenuItem invaudit = new ToolStripMenuItem();
         invaudit.Name = "invaudit";
         invaudit.Size = new System.Drawing.Size(161, 22);
         invaudit.Text = "Аудит оборудования";
         invaudit.Click += new System.EventHandler((o, e) =>
         {
            AuditReport.Do(dtpBegin.Value.Date, dtpEnd.Value.Date, this, GetSelectedAgent().id);
         });

         tsReportMenu.DropDownItems.Add(invaudit);

         tareCtrl = new TareView();
         tareCtrl.Dock = System.Windows.Forms.DockStyle.Fill;
         detailPanel.Controls.Add(tareCtrl);
         tareView = new DocView(Tare.OBJECT_NAME, "Тара", typeof(TareView));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
         string filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsInvAudit.Filter = filter;
         dsTare.Filter = filter;

         updSets.Add(dsInventory);
         updSets.Add(dsInvAudit);
         updSets.Add(dsTare);
      }

      internal override System.Windows.Forms.Control RefreshDetail(GRSoft.NapoleonManager.OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.InvAudit)
         {
            InvAudit p = odr.StoreObject as InvAudit;

            if (p != null)
               ctrl.InitDataSet(p.items);

            lbNotes.Visible = true;
            lbNotes.Text = string.Format("Даты промывок {0:dd/MM/yyyy} - {1:dd/MM/yyyy}", p.penult, p.last);

            return ctrl;
         }
         else if (odr.Doctype.Val == ObjType.TObjType.Tare)
         {
            Tare p = odr.StoreObject as Tare;

            if (p != null)
               tareCtrl.InitDataSet(p.items);

            return tareCtrl;
         }

         else return null;
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals(InvAudit.OBJECT_NAME)) 
            return iadv;

         if (docType.Equals(Tare.OBJECT_NAME))
            return tareView;

         return base.GetDocView(docType);
      }

   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{ 
[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<DistribGroupDoc> dsDistribDoc = new SimpleDataSet<DistribGroupDoc>(DistribGroupDoc.OBJECT_NAME);
      DataSet<string, DistribGroup> dsGroups = new DataSet<string, DistribGroup>(DistribGroup.OBJECT_NAME);
      FmDistribDetail distrDetail = new FmDistribDetail();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         DataGridViewTextBoxColumn clmnWeight = new DataGridViewTextBoxColumn();

         clmnWeight.DataPropertyName = "Weight";
         clmnWeight.HeaderText = "Вес";
         clmnWeight.Name = "dgvDetailColumnSum";
         clmnWeight.DefaultCellStyle.Format = "N1";
         clmnWeight.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;

         dgvDetail.Columns.Insert(dgvDetail.Columns.IndexOf(dgvDetailColumnSum) + 1, clmnWeight);

         DocumentInfo di = new DocumentInfo(dsDistribDoc, ObjType.TObjType.OrgDistrib);
         documents.Add(di);


         distrDetail.Dock = System.Windows.Forms.DockStyle.Fill;
         distrDetail.Location = new System.Drawing.Point(0, 0);
         distrDetail.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         distrDetail.Name = "distrDetail";
         distrDetail.Size = new System.Drawing.Size(611, 187);

         detailPanel.Controls.Add(distrDetail);

         ToolStripItemCollection ic = tsReportMenu.DropDownItems;

         ToolStripMenuItem wtReport = new ToolStripMenuItem("Дистрибуция");
         wtReport.Click += new EventHandler((o, e) => FmDistrReport.Do(GetDateForStartPeriod(), GetDateForEndPeriod(), GetSelectedIdAgent()));
         ic.Add(wtReport);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsDistribDoc.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         if (dsGroups.Count == 0)
         {
            dsGroups.Filter = "\"id\" <> ''";
            updSets.Add(dsGroups);
         }
         updSets.Add(dsDistribDoc);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }

      protected override string GetVisitText(Visit v)
      {
         string text = base.GetVisitText(v);

         string rfrg = "";
         foreach(Org.Rfrg r in v.org.refregerators)
         {
            rfrg += r.id + " " + r.name + "\n";
         }

         return rfrg + text;
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         distrDetail.Visible = false;
         if (odr.Doctype.Val == ObjType.TObjType.OrgDistrib)
         {
            DistribGroupDoc dgd = odr.StoreObject as DistribGroupDoc;
            distrDetail.dgvItems.DataSource = dgd.items;
            distrDetail.Visible = true;
            return distrDetail;
         }
         return null;
      }

      protected override void lblAdress_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null && odr.NOrg != null)
         {
            FmOrgInfo form = new FmOrgInfo();
            form.Address = odr.NOrg.Address;
            form.OrgName = odr.NOrg.Name;

            form.Show();
         }
      }
   }
}

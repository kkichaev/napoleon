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
      SimpleDataSet<PriceMonOrgDoc> dsMon = new SimpleDataSet<PriceMonOrgDoc>(PriceMonOrgDoc.OBJECT_NAME, false);
      DataGridView dgvDetailItems = new DataGridView();
      DataGridViewTextBoxColumn clmnName = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnCost = new DataGridViewTextBoxColumn();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         documents.Add(new DocumentInfo(dsMon, ObjType.TObjType.Monitoring));

         ToolStripItem tsi = new ToolStripMenuItem();
         tsi.Name = "tsbActions";
         tsi.Size = new System.Drawing.Size(152, 22);
         tsi.Text = "Мониторинг";
         tsi.Click += new EventHandler((o, e) => FmMonitoringRep.Open(dtpBegin.Value.Date, dtpEnd.Value.Date));

         tsReportMenu.DropDownItems.Add(tsi);
      }

      void Init()
      {
         dgvDetailItems.Visible = false;
         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).BeginInit();

         dgvDetailItems.AllowUserToAddRows = false;
         dgvDetailItems.AllowUserToDeleteRows = false;
         dgvDetailItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         dgvDetailItems.Dock = System.Windows.Forms.DockStyle.Fill;
         dgvDetailItems.Location = new System.Drawing.Point(0, 0);
         dgvDetailItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         dgvDetailItems.Name = "dgvDetailItems";
         dgvDetailItems.RowHeadersVisible = false;
         dgvDetailItems.Size = new System.Drawing.Size(611, 187);

         dgvDetailItems.Columns.AddRange(new DataGridViewColumn[] {
            clmnName,
            clmnCost,
         });

         clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnName.DataPropertyName = "Name";
         clmnName.FillWeight = 400F;
         clmnName.HeaderText = "Наименование";
         clmnName.Name = "Name";

         clmnCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnCost.DataPropertyName = "Cost";
         clmnCost.HeaderText = "Цена";
         clmnCost.Name = "Cost";

         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).EndInit();
         
         detailPanel.Controls.Add(dgvDetailItems);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsMon.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsMon);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDetailItems.Visible = false;
         foreach (DocumentInfo di in documents)
         {
            if (di.Type == odr.Doctype.Val)
            {
              PriceMonOrgDoc dobj = odr.StoreObject as PriceMonOrgDoc;
               if (dobj != null)
               {
                  dgvDetailItems.Visible = true;
                  dgvDetailItems.DataSource = dobj.items;
                  return dgvDetailItems;
               }
            }
         }

         return null; 
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new ScriptDetail(documents);
      }
   }
}

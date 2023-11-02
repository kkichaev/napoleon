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
      SimpleDataSet<ItemsAudit> dsItemsAudit = new SimpleDataSet<ItemsAudit>(ItemsAudit.OBJECT_NAME);
      SimpleDataSet<RfrgAudit> dsRfrgAudit = new SimpleDataSet<RfrgAudit>(RfrgAudit.OBJECT_NAME);

      RfrgAuditDetail rfrgDetail;

      DataGridView dgvDetailItems = new DataGridView();
      DataGridViewTextBoxColumn clmnName = new DataGridViewTextBoxColumn();

      DataGridViewCheckBoxColumn clmnRepr = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnPack = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnBlock = new DataGridViewCheckBoxColumn();
      DataGridViewCheckBoxColumn clmnPrice = new DataGridViewCheckBoxColumn();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         rfrgDetail = new RfrgAuditDetail();
         rfrgDetail.Name = "rfrgDetail";
         rfrgDetail.Size = new System.Drawing.Size(611, 187);
         rfrgDetail.Dock = DockStyle.Fill;
         detailPanel.Controls.Add(rfrgDetail);

         documents.Add(new DocumentInfo(dsItemsAudit, ObjType.TObjType.ItemsAudit));
         documents.Add(new DocumentInfo(dsRfrgAudit, ObjType.TObjType.RfrgAudit));
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

            clmnRepr,
            clmnPack,
            clmnBlock,
            clmnPrice
         });

         clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnName.DataPropertyName = "Name";
         clmnName.FillWeight = 400F;
         clmnName.HeaderText = "Наименование";
         clmnName.Name = "Name";

         clmnRepr.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnRepr.DataPropertyName = "Repr";
         clmnRepr.HeaderText = "Представление";
         clmnRepr.Name = "clmnRepr";

         clmnPack.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPack.DataPropertyName = "Pack";
         clmnPack.HeaderText = "Выкладка";
         clmnPack.Name = "clmnPack";

         clmnBlock.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnBlock.DataPropertyName = "Block";
         clmnBlock.HeaderText = "Блок";
         clmnBlock.Name = "clmnBlock";

         clmnPrice.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPrice.DataPropertyName = "Price";
         clmnPrice.HeaderText = "Ценник";
         clmnPrice.Name = "clmnPrice";

         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).EndInit();
         
         detailPanel.Controls.Add(dgvDetailItems);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         String docFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);;
         
         dsItemsAudit.Filter = docFilter;
         updSets.Add(dsItemsAudit);
         
         dsRfrgAudit.Filter = docFilter;
         updSets.Add(dsRfrgAudit);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDetailItems.Visible = false;
         ItemsAudit doc = odr.StoreObject as ItemsAudit;
         if(doc != null)
         {
            List<ItemsAudit.Item> items = new List<ItemsAudit.Item>();
            foreach (ItemsAudit.Item data in doc.items)
               items.Add(data);
            dgvDetailItems.DataSource = items;

            dgvDetailItems.Visible = true;
            return dgvDetailItems;
         }
         else
         {
            RfrgAudit rdoc = odr.StoreObject as RfrgAudit;
            if( rdoc != null )
            {
               rfrgDetail.SetSource(rdoc);
               rfrgDetail.Visible = true;
               return rfrgDetail;
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

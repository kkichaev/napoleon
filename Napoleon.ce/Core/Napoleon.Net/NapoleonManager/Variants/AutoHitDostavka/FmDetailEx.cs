using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<Dispatch> dsDispatch = null;
      SimpleDataSet<DShipment> dsDShipment = null;
      SimpleDataSet<DReturn> dsDReturn = null;
      SimpleDataSet<DTask> dsDTask = null;
      SimpleDataSet<DVisit> dsDVisit = null;
      SimpleDataSet<DIncass> dsDIncass = null;

      DataGridView dgvDetailItems = new DataGridView();

      DataGridViewTextBoxColumn clmnPos = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnName = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnQty = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnFact = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnCost = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnCause = new DataGridViewTextBoxColumn();
      DataGridViewTextBoxColumn clmnRemark = new DataGridViewTextBoxColumn();


      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         Init();

         clmnRouteOrder.Visible = false;
         tsReportMenu.Visible = false;
         tslFilter.Visible = false;
         btnCoverArea.Visible = false;
         toolStripSeparator2.Visible = false;
         tbnMessage.Visible = false;
         toolStripSeparator2.Visible = false;
         toolStripSeparator3.Visible = false;
         tsslCount.Visible = false;
         tsslSum.Visible = false;
         sbMode.Visible = true;

         dsDispatch = (SimpleDataSet<Dispatch>) DataModule.Get(Dispatch.OBJECT_NAME) ?? new SimpleDataSet<Dispatch>(Dispatch.OBJECT_NAME, true);
         dsDShipment = (SimpleDataSet<DShipment>)DataModule.Get(DShipment.OBJECT_NAME) ?? new SimpleDataSet<DShipment>(DShipment.OBJECT_NAME, true);
         dsDReturn = (SimpleDataSet<DReturn>)DataModule.Get(DReturn.OBJECT_NAME) ?? new SimpleDataSet<DReturn>(DReturn.OBJECT_NAME, true);
         dsDTask = (SimpleDataSet<DTask>)DataModule.Get(DTask.OBJECT_NAME) ?? new SimpleDataSet<DTask>(DTask.OBJECT_NAME, true);
         dsDVisit = (SimpleDataSet<DVisit>)DataModule.Get(DVisit.OBJECT_NAME) ?? new SimpleDataSet<DVisit>(DVisit.OBJECT_NAME, true);
         dsDIncass = (SimpleDataSet<DIncass>)DataModule.Get(DIncass.OBJECT_NAME) ?? new SimpleDataSet<DIncass>(DIncass.OBJECT_NAME, true);

         documents.Add(new DocumentInfo(dsDispatch, ObjType.TObjType.Script));
         documents.Add(new DocumentInfo(dsDShipment, ObjType.TObjType.DShipment));
         documents.Add(new DocumentInfo(dsDReturn, ObjType.TObjType.DReturn));
         documents.Add(new DocumentInfo(dsDTask, ObjType.TObjType.Task));
         documents.Add(new DocumentInfo(dsDVisit, ObjType.TObjType.OtVisit));
         documents.Add(new DocumentInfo(dsDIncass, ObjType.TObjType.DIncass));
      }

      private void Init()
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
         dgvDetailItems.CellFormatting += dgvDetailItems_CellFormatting;

         dgvDetailItems.Columns.AddRange(new DataGridViewColumn[] {
            clmnPos,
            clmnName,
            clmnQty,
            clmnFact,
            clmnCost,
            clmnCause,
            clmnRemark
         });

         clmnPos.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPos.DataPropertyName = "Pos";
         clmnPos.HeaderText = "№";
         clmnPos.Name = "Cell";

         clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnName.DataPropertyName = "Item";
         clmnName.FillWeight = 400F;
         clmnName.HeaderText = "Наименование";
         clmnName.Name = "clmnName";

         clmnCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnCost.DataPropertyName = "Cost";
         clmnCost.HeaderText = "Цена";
         clmnCost.Name = "Cost";

         clmnQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnQty.DataPropertyName = "Inqty";
         clmnQty.HeaderText = "Кол-во в накладной";
         clmnQty.Name = "Qty";

         clmnFact.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnFact.DataPropertyName = "Outqty";
         clmnFact.HeaderText = "Кол-во отгружено";
         clmnFact.Name = "Fact";

         clmnCause.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnCause.DataPropertyName = "Cause";
         clmnCause.HeaderText = "Причина";
         clmnCause.Name = "Cause";

         clmnRemark.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnRemark.DataPropertyName = "Remark";
         clmnRemark.HeaderText = "Комментарий";
         clmnRemark.Name = "Remark";

         ((System.ComponentModel.ISupportInitialize)dgvDetailItems).EndInit();
         
         detailPanel.Controls.Add(dgvDetailItems);
      }

      void dgvDetailItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DShipmentItem item = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DShipmentItem;

         if (item.Inqty != item.Outqty)
            e.CellStyle.BackColor = Color.Gray;
         else
            e.CellStyle.BackColor = Color.White;
      }

string ORG_FILTER = "not \"userid\" is null or \"userid\" is null";
      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         updSets.Clear();

         if (dsPrice.Count == 0)
         {
            updSets.Add(dsPrice);

            if(dsAgentPrice.Count == 0)
               updSets.Add(dsAgentPrice);
         }

if(dsOrg.Count == 0 || dsOrg.Filter != ORG_FILTER )
{
dsOrg = new DataSet<string, Org>(Org.OBJECT_NAME);
dsOrg.Filter = ORG_FILTER;
         updSets.Add(dsOrg);
}


         dsDispatch.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDShipment.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDReturn.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDTask.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsDVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentID);
         dsDIncass.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);

         updSets.Add(dsDVisit);
         updSets.Add(dsDTask);
         updSets.Add(dsDReturn);
         updSets.Add(dsDShipment);
         updSets.Add(dsDispatch);
         updSets.Add(dsDIncass);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
      }

      internal override OrdersDetail CreateOrderDetail() { return new DostavkaDetail(documents); }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         if (docType.Equals(DShipment.OBJECT_NAME))
            return new DocView(DShipment.OBJECT_NAME, "Отгрузка", typeof(DShipmentView));

         if (docType.Equals(DReturn.OBJECT_NAME))
            return new DocView(DReturn.OBJECT_NAME, "Возврат", typeof(DReturnView));

         return base.GetDocView(docType);
      }

      protected override void SetScriptInfo(ScriptDoc sd)
      {
         base.SetScriptInfo(sd);

         Dispatch d = sd as Dispatch;

         List<Image> nativePicture = new List<Image>();
         if (d != null && d.VisitObj != null)
         {
            DVisit v = d.VisitObj;

            int count = 0;
            foreach (Visit.VisitItem vi in v.items)
            {
               if (vi.id != null)
               {
                  try
                  {
                     MemoryStream stream = new MemoryStream(vi.id);
                     Image image = new Bitmap(stream);
                     image.Tag = new VisitTag(v, vi);

                     nativePicture.Add(image);
                     imPhoto.Images.Add(image);

                     count++;
                     String tag = count.ToString();
                     ListViewItem lvi = lvPhoto.Items.Add(tag);
                     lvi.ImageIndex = nativePicture.Count - 1;
                     lvi.Tag = new VisitTag(v, vi);

                  }
                  catch (Exception)
                  {
                  }
               }
            }
         }
         imPhoto.Tag = nativePicture;
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         dgvDetailItems.Visible = false;

         if (odr.StoreObject is DShipment || odr.StoreObject is DReturn)
         {
            GRSoft.Network.DataObject obj = odr.StoreObject;

            FieldInfo fi = obj.GetType().GetField("items");
            if (fi != null)
            {

               List<DShipmentItem> list = new List<DShipmentItem>((List<DShipmentItem>)fi.GetValue(obj));
               list.Sort((x, y) => { return x.Pos - y.Pos; });
               dgvDetailItems.DataSource = list;
               return dgvDetailItems;
            }
         }
         else if (odr.StoreObject is DIncass || odr.StoreObject is DTask)
            return tbVisitText;

         return null;
      }
   }

   class DostavkaDetail : OrdersDetail
   {
      public DostavkaDetail(List<DocumentInfo> documents) :
         base(documents)
      { 
         
      }

      protected override bool NeedAddNotVisited(FmDetailData cond, bool checkRoute, List<Org> routes) { return false; }

      protected override bool LoadIntDocument(FmDetailData fdd, DocumentInfo di)
      {
         bool scr = di.dataSet.Name.Equals(Dispatch.OBJECT_NAME);
         if (!((FmDetail)fdd.fmDetail).IsScriptMode)
            return !scr;
         else
            return scr;
      }
   }
}

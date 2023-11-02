using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      protected DataSet<int, Layout> dsLayout;
      private LayoutDetail layoutDetail = new LayoutDetail(), scriptLayout = new LayoutDetail();
      Dictionary<DateTime, LayoutApprove> approved = null;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsLayout = (DataSet<int, Layout>)DataModule.Get(GRSoft.NapoleonManager.Layout.OBJECT_NAME) ?? new DataSet<int, Layout>(GRSoft.NapoleonManager.Layout.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsLayout, ObjType.TObjType.Layout));

         layoutDetail.SetOwner(this);
         layoutDetail.Visible = false;
         layoutDetail.Dock = DockStyle.Fill;
         detailPanel.Controls.Add(layoutDetail);

      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         FmDetail.DocView result = null;

         if (docType.Equals(GRSoft.NapoleonManager.Layout.OBJECT_NAME))
            result = new DocViewEx(GRSoft.NapoleonManager.Layout.OBJECT_NAME, "Выкладка", typeof(LayoutDetail), this, false, layoutDetail);
         else
            result = base.GetDocView(docType);

         return result;
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = base.RefreshDetail(odr);

         if (odr.StoreObject is Layout)
         {
            layoutDetail.SetData(odr.StoreObject as Layout);

            //layoutDetail.EnableWriteData(isSU || isDisplayOperator);
            result = layoutDetail;
         }

         return result;
      }

      protected override string TotalCount()
      {
         return base.TotalCount() + " Выкладок: " + countLayout.ToString();
      }

      void MoveToBack()
      {
         if (layoutDetail.RowIndex >= 0)
            dgvDetail.CurrentCell = dgvDetail.Rows[layoutDetail.RowIndex].Cells[dgvDetailColumnOrg.DisplayIndex];
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         if (curRow != null && curRow.Index == layoutDetail.RowIndex)
            return;

         if (curRow != null
            && curRow.DataBoundItem is OrderDetailRepresentation
            && !(((OrderDetailRepresentation)curRow.DataBoundItem).StoreObject is BaseDocument))
         {
            wbPhoto.DocumentText = "<html></html>";
            assignedHtml = string.Empty;
         }

         layoutDetail.RowIndex = -1;
         layoutDetail.GridRowIndex = (curRow == null) ? -1 : curRow.Index;

         base.UpdateDetailTable(curRow);
      }

      internal override OrdersDetail CreateOrderDetail() { return new ScriptDetailEx(documents); }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string flt = string.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsLayout.Filter = flt;

         updSets.Add(dsLayout);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      class DocViewEx : DocView
      {
         FmDetailEx owner;
         bool enableWrite;
         LayoutDetail ld;

         public DocViewEx(String type, String title, Type viewer, FmDetailEx owner, bool enableWrite, LayoutDetail ld)
            : base(type, title, viewer)
         {
            this.owner = owner;
            this.enableWrite = enableWrite;
            this.ld = ld;
         }

         public override Control MakeControl()
         {
            //LayoutDetail ld = (LayoutDetail)base.MakeControl();
            //ld.SetOwner(owner);
            //ld.EnableWriteData(enableWrite);
            ld.SetData(null);
            ld.Visible = true;
            return ld;
         }
      }

      int countLayout = 0;

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();

         countLayout = 0;
         //List<int> needRemove = new List<int>();
         foreach (KeyValuePair<int, Layout> kv in dsLayout)
         {
            //if (kv.Value.IsEmpty)
            //   needRemove.Add(kv.Key);
            //else
            countLayout++;
         }

         //needRemove.ForEach(x => dsLayout.Remove(x));

         approved = null;
      }


   }

   public class ScriptDetailEx : ScriptDetail
   {
      public ScriptDetailEx() {}
      public ScriptDetailEx(List<DocumentInfo> documents) : base(documents) {}
      protected override void LoadPotenzialOrgVisit(FmDetailData cond, bool oneDay) {}
   }
}

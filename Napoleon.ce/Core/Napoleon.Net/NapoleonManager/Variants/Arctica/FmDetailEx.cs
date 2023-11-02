using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;

namespace GRSoft.NapoleonManager
{ 
[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      SimpleDataSet<PhoneCall> dsPhones = new SimpleDataSet<PhoneCall>(PhoneCall.OBJECT_NAME, false);
      ObjType noPhoto = new ObjType(ObjType.TObjType.NoPhoto);

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         DocumentInfo di = new DocumentInfo(dsPhones, ObjType.TObjType.PhoneCall);
         documents.Add(di);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsPhones.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsPhones);
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         PhoneCall pc = odr.StoreObject as PhoneCall;
         if(pc != null)
         {
            tbVisitText.Text = pc.remark;
            return tbVisitText;

         }

         return base.RefreshDetail(odr);
      }

      protected override void UpdateRemark(OrderDetailRepresentation odr)
      {
         Order o = odr.StoreObject as Order;
         if (o != null && o.byPhone > 0)
         {
            lbNotes.Visible = true;
            lbNotes.Text = "По телефону: " + odr.Notes;

         }
         else
         {
            base.UpdateRemark(odr);
         }
      }

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         if (e.RowIndex < 0 || e.RowIndex >= dgvDetail.Rows.Count || refreshing)
            return;

         Order ord = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject as Order;
         
         if (ord != null && ord.byPhone > 0)
         {
            e.CellStyle.BackColor = Color.LightSkyBlue;
         } else
         {
            base.CellFormatting(e);
         }
      }

      protected override void UpdateFiltersListInComboBox()
      {
         cbFilter.SuspendLayout();
         cbFilter.Items.Clear();
         cbFilter.Items.Add("Все");

         foreach (ObjType tObjType in oDetail.FiltersAvailable)
            cbFilter.Items.Add(tObjType);

         cbFilter.Items.Add(noPhoto);

         //cbFilter.Sorted = true;
         cbFilter.SelectedIndex = 0;
         cbFilter.ResumeLayout();
      }

      protected override void UpdateGrid(bool refreshFilterCB)
      {
         if(!refreshFilterCB)
         {
            if(cbFilter.SelectedItem == noPhoto)
            {
               ((OrderDetailEx)oDetail).LoadNoPhoto(dtpBegin.Value, GetDateForEndPeriod(), GetSelectedAgent());
               BindingSource bs = new BindingSource();
               bs.DataSource = oDetail;
               dgvDetail.DataSource = bs;

               tsslCount.Text = "";

               tsslSum.Text = "";

               return;
            }
         }
         base.UpdateGrid(refreshFilterCB);
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrderDetailEx(documents);
      }

      class OrderDetailEx : ScriptDetail
      {
         public OrderDetailEx(List<DocumentInfo> docs) : base(docs)
         {
         }

         internal void LoadNoPhoto(DateTime start, DateTime finish, Agent agent)
         {
            Dictionary<DateTime, List<OrgFolderItem>> droute = GetAgentRoute(start, finish, agent);
            List<Org> route = MakeUniqOrgList(droute);

            List<string> docs = new List<string>();
            docs.Add(visitName);
            Dictionary<string, bool> haveDocs = LoadHaveDocs(docs);

            Clear();

            bool oneDay = (start.Date == finish.AddDays(-1).Date);
            foreach(Org o in route)
            {
               if (o == null || haveDocs.ContainsKey(o.id))
                  continue;

               Add(new OrderDetailRepresentation(DateTime.MinValue,
                  new ObjType(ObjType.TObjType.NoPhoto),
                  DateTime.MinValue, DateTime.MinValue, o, 0, 0, 0, o, oneDay));
            }
         }
      }
   }
}

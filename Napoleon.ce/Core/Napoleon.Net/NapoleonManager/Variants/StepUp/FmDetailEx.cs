using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager 
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      public DataSet<int, Contract> dsContract;
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsContract = (DataSet<int, Contract>) DataModule.Get(Contract.OBJECT_NAME) ?? new DataSet<int, Contract>(Contract.OBJECT_NAME);

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Contract.OBJECT_NAME, "Контракт", typeof(ContractOverview)));
         docViews = views.ToArray();
         dgvDetailColumnSum.Visible = false;
         tsClienCard.Visible = false;
         btnCoverArea.Visible = false;

         documents.Add(new DocumentInfo(dsContract, ObjType.TObjType.Contract));
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsContract.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsContract);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
      }

      protected override void UpdateDetail(OrderDetailRepresentation odr)
      {
         Control c = RefreshDetail(odr);
         c.BringToFront();
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
               scBottom.Panel1.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }
         
         return result;
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         HideContractView();
         base.UpdateDetailTable(curRow);
      }

      private void HideContractView()
      {
         string name = typeof(ContractOverview).Name;

         Control c = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(name))
            {
               c = cc;
               break;
            }

         if (c != null)
            c.Visible = false;
      }

   }
  
}

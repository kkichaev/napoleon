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
      DataSet<string, OrgEx> dsOrgEx;
      DataSet<string, OrgNotes> dsOrgNotes;
      SimpleDataSet<Planogram> dsPlanogram;

      public FmDetailEx(FmDetailData data) : base(data)
      {
         dsOrgEx = new DataSet<string, OrgEx>(Org.COMMON_OBJECT_NAME, false);

         dsOrgNotes = (DataSet<string, OrgNotes>)DataModule.Get(OrgNotes.OBJECT_NAME) ??
            new DataSet<string, OrgNotes>(OrgNotes.OBJECT_NAME);
         dgvRemnantsItemsQty.Visible = false;
         dgvDetailColumnSum.Visible = false;

         dsPlanogram = (SimpleDataSet<Planogram>)DataModule.Get(Planogram.OBJECT_NAME) ??
            new SimpleDataSet<Planogram>(Planogram.OBJECT_NAME);
         documents.Add(new DocumentInfo(dsPlanogram, ObjType.TObjType.Planogram));
      }

      protected override void AdjustFilterForDS(string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(agentID, dateBegin, dateEnd);

         dsPlanogram.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         Agent a = GetSelectedAgent();

         if(a != null)
         {
            dsOrgNotes.Filter = "userid='" + a.id + "'";
            updSets.Insert(0, dsOrgEx);
            updSets.Add(dsOrgNotes);
            updSets.Add(dsPlanogram);

            if (!updSets.Contains(dsOrg))
            {
               updSets.Insert(0, dsOrg);
            }
         }
      }

      protected override void UpdateDetailTable(System.Windows.Forms.DataGridViewRow curRow)
      {
         base.UpdateDetailTable(curRow);

         if (curRow != null)
         {
            Agent a = GetSelectedAgent();
            string id = string.Empty;
            OrderDetailRepresentation ord = curRow.DataBoundItem as OrderDetailRepresentation;

            if (ord != null)
               id = ord.NOrg.id;

            if (id.Length > 0 && a != null && dsOrgNotes.ContainsKey(id))
            {
               lbNotes.Text = dsOrgNotes[id].text;
               lbNotes.Visible = true;
            }
         }
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         if (odr.Doctype.Val == ObjType.TObjType.Planogram)
         {
            Planogram doc = odr.StoreObject as Planogram;
            String text = doc.approved == 0 ? "Несоответствие" : "Соответствие";
            tbVisitText.Text = text;
            return tbVisitText;
         }

         return null;
      }

   }

   class OrgNotes : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "OrgNotes";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public string userid = string.Empty;
   }
}

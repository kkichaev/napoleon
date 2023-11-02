using GRSoft.Network;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      ToolStripMenuItem miDelete;
      ComboBox cbOrgType = new ComboBox();

      public SimpleDataSet<OrgType> dsOrgType = new SimpleDataSet<OrgType>(OrgType.OBJECT_NAME);

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         miDelete = new ToolStripMenuItem("Удалить", null);
         miDelete.Name = "miDelete";
         miDelete.Click += miDelete_Click;
         cmDgvDetail.Items.Add(miDelete);

         Size = new System.Drawing.Size(1470, 700);
         
         cbOrgType.Location = new System.Drawing.Point(985, 8);
         cbOrgType.Font = cbFilter.Font;
         cbOrgType.Size = cbFilter.Size;
         cbOrgType.BringToFront();
         cbOrgType.Visible = true;

         Controls.Add(cbOrgType);

         tslFilter.Margin = new System.Windows.Forms.Padding(0, 1, 390, 2);
         cbOrgType.BringToFront();
         cbOrgType.Anchor = AnchorStyles.Right | AnchorStyles.Top;

         Load += FmDetailEx_Load;
      }

      void FmDetailEx_Load(object sender, System.EventArgs e)
      {
         DataSet<string, OrgType> ds = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME);

          cbOrgType.Items.Add("Все");
          cbOrgType.SelectedIndex = 0;

          if (ds != null)
          {
             foreach (OrgType t in ds.Values)
             {
                cbOrgType.Items.Add(t);
             }
          }

          cbOrgType.SelectedValueChanged += cbFilter_SelectedIndexChanged;
      }

      void miDelete_Click(object sender, System.EventArgs e)
      {
         if (DialogUtil.AskToDel(this))
         {
            OrderDetailRepresentation o = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

            if (o != null)
            {
               Incass incass = o.StoreObject as Incass;

               if (incass != null)
               {
                  SimpleDataSet<Incass> ds = new SimpleDataSet<Incass>(Incass.OBJECT_NAME);
                  ds.Filter = string.Format("\"userid\"='{0}' and \"created\"=ToDate('{1:dd/MM/yyyy HH:mm:ss}')", incass.userid, incass.created);
                  ds.Add(incass);
                  
                  if (!DataModule.RemoveDataSet(ds, Config.GetConfig().GetConnection()))
                  {
                     DialogUtil.UpdateErrMsg(this);
                  }
                  else
                  {
                     dgvDetail.Rows.Remove(dgvDetail.CurrentRow);
                  }
               }
            }
         }
      }

      protected override void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         base.cmDgvDetail_Opening(sender, e);

         if (dgvDetail.CurrentRow != null)
         {
            OrderDetailRepresentation o = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

            if (o != null)
            {
               miDelete.Visible = o.StoreObject is Incass;
            }
         }
      }

      public override bool LoadIntDocument(BaseDocument doc)
      {
         bool result = cbOrgType.SelectedIndex == 0;

         if (!result)
         {
            OrgType ot = cbOrgType.SelectedItem as OrgType;

            if (ot != null)
            {
               Org o = doc.Org;

               if (o != null)
               {
                  result = ot.id.Equals(o.orgtype);
               }
            }
         }

         return result;
      }
   }
}

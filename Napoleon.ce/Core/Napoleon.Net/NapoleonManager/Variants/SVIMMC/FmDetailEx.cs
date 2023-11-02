using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      ToolStripItem itChangeOrg, delOrg;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         itChangeOrg = cmDgvDetail.Items.Add("Сменить организацю");
         itChangeOrg.Click += new EventHandler(itChangeOrg_Click);

         delOrg = cmDgvDetail.Items.Add("Удалить документ");
         delOrg.Click += new EventHandler(itDelDoc_Click);
      }

      void itChangeOrg_Click(object sender, EventArgs e)
      {
         FmSelectContrAgent.ShowForm(dsOrg, dsPtnzOrg, dsOrgFolder, OrgSelected, this);
      }

      void itDelDoc_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Удалить документ?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            IDataSet rmv = GetRmvDataSet();

            if (rmv != null)
               DoUpdate(null, rmv);
         }
      }

      private IDataSet GetRmvDataSet()
      {
         IDataSet result = null;
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         const string Filter = "\"userid\"='{0}' and \"created\"=ToDate('{1:dd/MM/yyyy HH:mm:ss}')";

         if (odr != null)
         {
            GRSoft.Network.DataObject dataObject = odr.StoreObject;

            if (dataObject is Returns)
            {
               SimpleDataSet<Returns> ord = new SimpleDataSet<Returns>("FixReturns", false, true);
               ord.Filter = String.Format(Filter, ((Returns)dataObject).AgentID, ((Returns)dataObject).created);
               result = ord;
            }
            else if (dataObject is Sales)
            {
               SimpleDataSet<Sales> ord = new SimpleDataSet<Sales>("FixSales", false, true);
               ord.Filter = String.Format(Filter, ((Sales)dataObject).AgentID, ((Sales)dataObject).created);
               result = ord;
            }
            else if (dataObject is Order)
            {
               SimpleDataSet<Order> ord = new SimpleDataSet<Order>("FixOrder", false, true);
               ord.Filter = String.Format(Filter, ((Order)dataObject).AgentID, ((Order)dataObject).created);
               result = ord;
            }
            else if (dataObject is Incass)
            {
               SimpleDataSet<Incass> ord = new SimpleDataSet<Incass>("FixIncass", false, true);
               ord.Filter = String.Format(Filter, ((Incass)dataObject).AgentID, ((Incass)dataObject).created);
               result = ord;
            }
         }

         return result;
      }

      void DoUpdate(IDataSet add, IDataSet rmv)
      {
         Config cfg = Config.GetConfig();
         bool res = false;

         if (rmv != null)
         {
            res = DataModule.RemoveDataSet(rmv, cfg.GetConnection());
         } 
         else if (add != null)
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(add);
            res = DataModule.UpdateDataSet(wrSet, null, null, cfg.GetConnection(), GetSelectedIdAgent());
         }

         if (res)
         {
            MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
               MessageBoxIcon.Information);
            btnRefresh.PerformClick();
         }
         else
         {
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }
      }

      public void OrgSelected(object sender, Org org)
      {
         ((Form)sender).Close();
         IDataSet add = GetFixDataSet(org);

         if (add != null)
            DoUpdate(add, null);
      }

      protected IDataSet GetFixDataSet(Org org)
      {
         IDataSet result = null;
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null)
         {
            GRSoft.Network.DataObject dataObject = odr.StoreObject;

            if (dataObject is Returns)
            {
               SimpleDataSet<Returns> ord = new SimpleDataSet<Returns>("FixReturns", false, true);
               ord.Add((Returns)dataObject);
               ((Returns)dataObject).org = org;
               ((Returns)dataObject).id = org.id;
               result = ord;
            }
            else if (dataObject is Sales)
            {
               SimpleDataSet<Sales> ord = new SimpleDataSet<Sales>("FixSales", false, true);
               ord.Add((Sales)dataObject);
               ((Sales)dataObject).org = org;
               ((Sales)dataObject).id = org.id;
               result = ord;
            } 
            else if (dataObject is Order)
            {
               SimpleDataSet<Order> ord = new SimpleDataSet<Order>("FixOrder", false, true);
               ord.Add((Order)dataObject);
               ((Order)dataObject).org = org;
               ((Order)dataObject).id = org.id;
               result = ord;
            }
            else if (dataObject is Incass)
            {
               SimpleDataSet<Incass> ord = new SimpleDataSet<Incass>("FixIncass", false, true);
               ord.Add((Incass)dataObject);
               ((Incass)dataObject).org = org;
               ((Incass)dataObject).id = org.id;
               result = ord;
            }
         }

         return result;
      }

      protected override void cmDgvDetail_Opening(object sender, System.ComponentModel.CancelEventArgs e)
      {
         if (GetRmvDataSet() == null)
            e.Cancel = true;

         //base.cmDgvDetail_Opening(sender, e);

         //if (e.Cancel == false)
         //{
         //   OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         //   if (odr != null && odr.StoreObject is Order)
         //      itChangeOrg.Visible = true;
         //   else
         //      itChangeOrg.Visible = false;
         //}
      }
   }
}

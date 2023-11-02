using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgs : Form
   {
      public DataSet<string, Org> dsOrg;
      bool clearing = false;

      public FmOrgs()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
            new DataSet<string, Org>(Org.OBJECT_NAME);

         grid.AutoGenerateColumns = false;
      }

      private void FmOrgs_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            foreach (Agent da in mc.GetAgents().Data)
               cbAgents.Items.Add(da);
            btnRefresh.PerformClick();
         }

         cbAgents.SelectedIndex = 0;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if (a != null)
         {
            FmOrgLoad fm = new FmOrgLoad();
            fm.SetAgent(a, this);
            fm.Show();
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrg);
            dsOrg.Filter = "(\"id\" is null or \"id\" is not null) and (\"rem\" = 0 or \"rem\" is null)";
            dsOrg.Filter += string.Format(" and \"userid\"=\"{0}\"", a.ID);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
         }
      }

      private void DoLoadData()
      {
         Filter();
      }

      private void Filter()
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            List<Org> orgs = new List<Org>();
            string text = tbFind.Text.Trim().ToUpper();

            foreach (Org o in dsOrg.Values)
               if (text.Length == 0 ||o.Name.ToUpper().Contains(text))
                  orgs.Add(o);

            BindingListView<Org> data = new BindingListView<Org>(orgs);
            data.Sort = "Name";
            grid.DataSource = data;
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         ObjectView<Org> vorg = grid.CurrentRow.DataBoundItem as ObjectView<Org>;

         if (vorg != null)
         {
            Org org = vorg.Object;

            if (org != null)
            {
               if (new FmOrgEdit(org).ShowDialog() == DialogResult.OK)
               {
                  DataSet<string, Org> ds = new DataSet<string, Org>(Org.OBJECT_NAME);
                  ds.Add(org.id, org);

                  List<IDataSet> upd = new List<IDataSet>();
                  upd.Add(ds);

                  Config cfg = Config.GetConfig();

                  if (DataModule.UpdateDataSet(upd, null, null, cfg.GetConnection()))
                     MessageBox.Show("Данные обновлены успешно!", "Информация", MessageBoxButtons.OK,
                        MessageBoxIcon.Information);

                  else
                     MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                        MessageBoxIcon.Error);

                  grid.Refresh();
               }
            }
         }
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            ClearFind(this, EventArgs.Empty);
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         Filter();
      }

      private void ClearFind(object sender, EventArgs e)
      {
         clearing = true;
         tbFind.Clear();
         Filter();
         clearing = false;
      }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbFind.Clear();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show(this, "Вы действительно хотите удалить клиента?", "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.OK)
         {
            DataSet<string, Org> ds = new DataSet<string, Org>(Org.OBJECT_NAME, false);

            ObjectView<Org> vorg = grid.CurrentRow.DataBoundItem as ObjectView<Org>;

            if (vorg != null)
            {
               Org o = vorg.Object;
               if (o != null)
               {
                  ds.Add(o.id, o);
                  o.rem = 1;
               }

               grid.Rows.Remove(grid.CurrentRow);
            }

            if (ds.Count > 0)
            {
               List<IDataSet> upd = new List<IDataSet>();
               upd.Add(ds);

               Config cfg = Config.GetConfig();

               if (DataModule.UpdateDataSet(upd, null, null, cfg.GetConnection()))
                  MessageBox.Show("Данные обновлены успешно!", "Информация", MessageBoxButtons.OK,
                     MessageBoxIcon.Information);

               else
                  MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                     MessageBoxIcon.Error);
            }
         }
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }
   }
}

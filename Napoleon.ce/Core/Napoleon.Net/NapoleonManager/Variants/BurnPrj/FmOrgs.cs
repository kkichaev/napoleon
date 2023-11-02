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
      protected DataSet<string, Org> dsOrg;
      protected DataSet<int, DivisionOrg> dsDivisionOrg;
      bool clearing = false;

      public FmOrgs()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
            new DataSet<string, Org>(Org.OBJECT_NAME);

         dsDivisionOrg = (DataSet<int, DivisionOrg>)DataModule.Get(DivisionOrg.OBJECT_NAME) ??
           new DataSet<int, DivisionOrg>(DivisionOrg.OBJECT_NAME);

         grid.AutoGenerateColumns = false;

         cbDivision.SelectedIndexChanged -= cbDivision_SelectedIndexChanged;
      }

      private void FmOrgs_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            cbDivision.Items.AddRange(list.ToArray());
            SelectDivision(mc);
            cbDivision.SelectedIndexChanged += cbDivision_SelectedIndexChanged;
            btnRefresh.PerformClick();
         }
      }

      private void SelectDivision(Manager mc)
      {
         int sel = -1;
         for (int i = 0; i < cbDivision.Items.Count; i++)
         {
            Division d = (Division)cbDivision.Items[i];

            if (d.id.Equals(mc.Division.id))
            {
               sel = i;
               break;
            }
         }

         cbDivision.Items.Insert(0, new Division() 
         {
            name = "<Все>",
            id = -1
         });

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         new FmOrgLoad().Show();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         dsOrg.Filter = "(\"id\" is null or \"id\" is not null) and (\"rem\" = 0 or \"rem\" is null)";
         upd.Add(dsDivisionOrg);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         Filter();
      }

      private void cbDivision_SelectedIndexChanged(object sender, EventArgs e)
      {
         Filter();
      }

      private void Filter()
      {
         Division d = cbDivision.SelectedItem as Division;

         if (d != null)
         {
            List<String> ids = new List<string>();

            if (d.id == -1)
            {
                Manager mc = CurrentUser.user as Manager;

                if (mc != null)
                {
                   List<Division> list = mc.AllDivisions;

                   foreach (Division dd in list)
                   {
                      if (dsDivisionOrg.ContainsKey(dd.id))
                      {
                         DivisionOrg divOrg = dsDivisionOrg[dd.id];

                         foreach (DivisionOrg.DivisionOrgItem item in divOrg.items)
                            ids.Add(item.id);
                      }
                   }
                }
            }else if (dsDivisionOrg.ContainsKey(d.id))
            {
               DivisionOrg divOrg = dsDivisionOrg[d.id];

               foreach (DivisionOrg.DivisionOrgItem item in divOrg.items)
                  ids.Add(item.id);
            }


            List<Org> orgs = new List<Org>();
            string text = tbFind.Text.Trim().ToUpper();

            foreach (Org o in dsOrg.Values)
               if ((ids.Contains(o.id)) && (text.Length == 0 ||
                     o.Name.ToUpper().Contains(text) ||
                     o.formatTT.ToUpper().Contains(text)
                  ))
                  orgs.Add(o);

            BindingListView<Org> data = new BindingListView<Org>(orgs);
            data.Sort = "DisplayName";
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
   }
}

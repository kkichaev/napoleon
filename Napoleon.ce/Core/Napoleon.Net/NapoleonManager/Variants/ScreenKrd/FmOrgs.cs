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
         Division dv = cbDivision.SelectedItem as Division;
         if(dv == null || dv.id <= 0)
         {
            MessageBox.Show("Выберите подразделение для добавления контрагента", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         bool saved = false;
         ObjectView<Org> o = ((BindingListView<Org>)grid.DataSource).AddNew();
         Org org = o.Object;
         org.id = Guid.NewGuid().ToString().Replace("-", "");
         if(new FmOrgEdit(org).ShowDialog() == DialogResult.OK)
         {
            if(org.name.Length != 0)
            {
               saved = SaveOrg(org, dv);
            }
         }

         if(!saved)
         {
            ((BindingListView<Org>)grid.DataSource).DataSource.Remove(o);
         }
         grid.Refresh();
      }

      bool SaveOrg(Org org, Division d)
      {
         DataSet<string, Org> ds = new DataSet<string, Org>(Org.OBJECT_NAME);
         ds.Add(org.id, org);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(ds);

         if(d != null)
         {
            DivisionOrg dorgs = null;
            foreach(DivisionOrg dorg in dsDivisionOrg.Data)
            {
               if(dorg.id == d.id)
               {
                  dorgs = dorg;
                  break;
               }
            }

            if(dorgs == null)
            {
               dorgs = new DivisionOrg();
               dorgs.id = d.id;
            }
            bool finded = false;
            foreach(DivisionOrg.DivisionOrgItem oi in dorgs.items)
            {
               if(oi.id == org.id)
               {
                  finded = true;
               }
            }

            if(!finded)
            {
               DivisionOrg.DivisionOrgItem oi = new DivisionOrg.DivisionOrgItem();
               oi.id = org.id;
               dorgs.items.Add(oi);

               SimpleDataSet<DivisionOrg> wrdo = new SimpleDataSet<DivisionOrg>(DivisionOrg.OBJECT_NAME, false);
               wrdo.Add(dorgs);
               upd.Add(wrdo);
            }
         }

         Config cfg = Config.GetConfig();

         bool writed = DataModule.UpdateDataSet(upd, null, null, cfg.GetConnection());
         if (!writed)
            MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);

         return writed;
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
                  SaveOrg(org, null);
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

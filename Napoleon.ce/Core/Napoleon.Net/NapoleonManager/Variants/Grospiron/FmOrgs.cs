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
        bool clearing = false;

        ListItemSource cities = new ListItemSource(ListItemSource.CITIES_OBJECT);
        ListItemSource brands = new ListItemSource(ListItemSource.BRAND_OBJECT);
        ListItemSource orgFormats = new ListItemSource(ListItemSource.FORMAT_OBJECT);

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

            cbAgents.Items.Add("<Все>");

            if (mc != null)
            {
                foreach (Agent da in mc.GetAgents().Data)
                    cbAgents.Items.Add(da);
                RefreshData();
            }

            cbAgents.SelectedIndex = 0;
        }

        public void RefreshData()
        {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrg);
            dsOrg.Filter = "not id is null";

            upd.Add(cities);
            upd.Add(brands);
            upd.Add(orgFormats);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        private void btnRefresh_Click(object sender, EventArgs e)
        {
            RefreshData();
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
            List<Org> orgs = new List<Org>();
            Agent agent = cbAgents.SelectedItem as Agent;
            
            string text = tbFind.Text.Trim().ToUpper();
            foreach(Org o in dsOrg.Data)
            {
               if ((agent == null || o.userid == agent.id) && (text.Length == 0 || o.Contains(text)))
                  orgs.Add(o);
            }

            SortableBindingList<Org> data = new SortableBindingList<Org>(orgs);
            grid.DataSource = data;
        }

        bool EditOrg(Org org, Agent a)
        {
            if (a == null)
            {
               MessageBox.Show("Выберите агента", "Ошибка", MessageBoxButtons.OK,
                     MessageBoxIcon.Error);
               return false;
            }

            bool ret = false;
            FmOrgEdit fm = new FmOrgEdit();
            fm.SetOrg(org, brands, orgFormats, cities);

            if (fm.ShowDialog() == DialogResult.OK)
            {
                org.userid = a.id;

                List<IDataSet> upd = new List<IDataSet>();
                DataSet<string, Org> ds = new DataSet<string, Org>(Org.OBJECT_NAME, false);
                ds.Add(org.id, org);
                upd.Add(ds);

                Config cfg = Config.GetConfig();

                if (!DataModule.UpdateDataSet(upd, null, null, cfg.GetConnection()))
                    MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                        MessageBoxIcon.Error);
                else
                {
                    ret = true;
                }
            }

            return ret;
        }

        private void btnEdit_Click(object sender, EventArgs e)
        {
            Agent a = cbAgents.SelectedItem as Agent;
            Org org = grid.CurrentRow.DataBoundItem as Org;

            if (org != null && a != null)
            {
                if(EditOrg(org, a))
                {
                    grid.Refresh();
                }
            }
        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            Agent a = cbAgents.SelectedItem as Agent;
            SortableBindingList<Org> src = (SortableBindingList<Org>)grid.DataSource;
            Org o = src.AddNew();
            o.id = Guid.NewGuid().ToString().Replace("-", "");
            if(EditOrg(o, a))
            {
                grid.Refresh();
            } else
            {
                src.Remove(o);
            }
            //new FmOrgLoad().Show();
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
            if (grid.SelectedRows.Count == 0)
                return;

            string text = "Удалить выбранные точки?" ;
            if (MessageBox.Show(this, text, "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.OK)
            {
                DataSet<string, Org> ds = new DataSet<string, Org>(Org.OBJECT_NAME, false);

                foreach(DataGridViewRow r in grid.SelectedRows)
                {
                    Org org = r.DataBoundItem as Org;
                    org.hidden = org.hidden > 0 ? 0 : 1;
                    ds.Add(org.id, org);
                }

                List<IDataSet> upd = new List<IDataSet>();
                upd.Add(ds);

                Config cfg = Config.GetConfig();

                if (!DataModule.UpdateDataSet(upd, null, null, cfg.GetConnection()))
                {
                    MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                        MessageBoxIcon.Error);
                }
                else
                {
                    grid.Refresh();
                }
            }
        }

        private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
        {
            
            Org o = grid.Rows[e.RowIndex].DataBoundItem as Org;
            e.CellStyle.BackColor = (o.hidden == 0) ? grid.DefaultCellStyle.BackColor : Color.LightGray;
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            Agent a = cbAgents.SelectedItem as Agent;
            if(a != null)
            {
                FmOrgLoad fm = new FmOrgLoad();
                fm.SetAgent(a, this);
                fm.Show();

            }
        }

      private void grid_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;
         if (a == null || e.RowIndex < 0)
            return;

         Org o = grid.Rows[e.RowIndex].DataBoundItem as Org;
         if(EditOrg(o, a))
         {
            grid.InvalidateRow(e.RowIndex);
            grid.Update();
         }
      }
   }
}

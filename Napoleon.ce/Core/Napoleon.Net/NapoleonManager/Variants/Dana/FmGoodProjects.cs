using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
    public partial class FmGoodProjects : Form
    {
        DataSet<string, Firm> firms = new DataSet<string, Firm>(Firm.OBJECT_NAME, false);
        DataSet<string, Bases1c> bases = new DataSet<string, Bases1c>(Bases1c.OBJECT_NAME, false);
        SimpleDataSet<GoodsProjects> projects = new SimpleDataSet<GoodsProjects>(GoodsProjects.OBJECT_NAME, false);

        SimpleDataSet<GoodsProjects> changedProjects = new SimpleDataSet<GoodsProjects>(GoodsProjects.OBJECT_NAME, false);


        public FmGoodProjects()
        {
            InitializeComponent();
            projects.Filter = "not \"id\" is null";

        }

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            RefreshData();
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            base.OnClosing(e);
            if (!CheckChanges())
                e.Cancel = true;
        }

        bool CheckChanges()
        {
            if (!tsbSave.Enabled)
                return true;

            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.No)
                return true;
            if (dr == DialogResult.Cancel)
                return false;

            return SaveChanges(false);
        }

        private bool SaveChanges(bool showDialog)
        {
            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(changedProjects);

            bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
            if (showDialog)
                MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

            if (ret)
            {
                changedProjects.Clear();
            }
            return ret;
        }

        private void RefreshData()
        {
            List<IDataSet> upd = new List<IDataSet>();

            upd.Add(bases);
            upd.Add(projects);
            upd.Add(firms);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        void DoLoadData()
        {
            List<Firm> f = new List<Firm>();
            foreach (Firm frm in firms.Data)
                f.Add(frm);

            f.Sort();
            f.Insert(0, new Firm());

            clmnFirm.DataSource = f;
            clmnFirm.DisplayMember = "Name";
            clmnFirm.ValueMember = "Id";

            List<GoodsProjects> src = new List<GoodsProjects>();
            foreach (GoodsProjects gp in projects.Data)
            {
                src.Add(gp);
            }
            src.Sort();
            dgvItems.DataSource = new SortableBindingList<GoodsProjects>(src);
        }

        private void tsbSave_Click(object sender, EventArgs e)
        {
            tsbSave.Enabled = !SaveChanges(true);
        }

        private void tsbRefresh_Click(object sender, EventArgs e)
        {
            RefreshData();
        }

        private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
        {
            GoodsProjects gp = dgvItems.CurrentRow.DataBoundItem as GoodsProjects;
            changedProjects.Add(gp);

            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
            tsbSave.Enabled = true;
        }
    }
}

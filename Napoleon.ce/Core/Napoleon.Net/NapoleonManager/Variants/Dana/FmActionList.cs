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
    public partial class FmActionList : Form
    {
        DataSet<string, DanaAction> actions;

        DataSet<string, OrgCluster> clusters;
        DataSet<string, Org> orgs;
        DataSet<string, Price> price;

        DataSet<string, ActionCount> actionCount = new DataSet<string, ActionCount>(ActionCount.OBJECT_NAME, false);

        public FmActionList()
        {
            InitializeComponent();

            clusters = DataModule.Get(OrgCluster.OBJECT_NAME) as DataSet<string, OrgCluster> ??
               new DataSet<string, OrgCluster>(OrgCluster.OBJECT_NAME, true);

            orgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org> ??
               new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, true);

            price = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ??
               new DataSet<string, Price>(Price.OBJECT_NAME, true);
        }

        public DataSet<string, DanaAction> Actions { get { return actions; } set { actions = value; } }

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            RefreshData(true);
        }

        void RefreshData(bool updateOnlyCount)
        {
            List<IDataSet> upd = new List<IDataSet>();

            string filter = "";
            foreach(DanaAction da in actions.Data)
            {
                filter += "'" + da.id + "',";
            }

            if(filter.Length > 0)
            {
                actionCount.Filter = '"' +  filter.Remove(filter.Length - 1) + '"';
                upd.Add(actionCount);
            }

            if (!updateOnlyCount)
            {
                if (clusters.Count == 0)
                    upd.Add(clusters);

                if (orgs.Count == 0)
                    upd.Add(orgs);

                if (price.Count == 0)
                    upd.Add(price);

                upd.Add(actions);
            }
            if(upd.Count > 0)
                FmWait.StdDataRefresh(this, upd, DoLoadData);
            else
            {
                DoLoadData();
            }
        }

        void DoLoadData()
        {
            List<int> divisions = new List<int>();
            Manager m = CurrentUser.user as Manager;
            if(m != null)
            {
                foreach (Division d in m.AllDivisions)
                    divisions.Add(d.id);
            }

            List<DanaAction> src = new List<DanaAction>();
            foreach (DanaAction d in actions.Data)
            {
                if (divisions.Contains(d.creatorDivision))
                    src.Add(d);
            }

            dgvItems.DataSource = new SortableBindingList<DanaAction>(src);
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            RefreshData(false);
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            FmActionEdit fm = new FmActionEdit();
            DanaAction doc = new DanaAction();

            doc.id = Guid.NewGuid().ToString().Replace("-", "");
            doc.creatorDivision = (CurrentUser.user as Manager).Division.id;

            fm.Doc = doc;
            if (fm.ShowDialog() == DialogResult.OK)
            {
                SortableBindingList<DanaAction> src = (SortableBindingList<DanaAction>)dgvItems.DataSource;
                src.Add(doc);
                actions.Add(doc.id, doc);
            }
        }

        void EditAction(int rowIndex)
        {
            if (rowIndex >= 0 && rowIndex < dgvItems.Rows.Count)
            {
                DataGridViewRow sel = dgvItems.Rows[rowIndex];
                DanaAction doc = sel.DataBoundItem as DanaAction;

                ActionCount ac = null;
                actionCount.TryGetValue(doc.id, out ac);


                FmActionEdit fm = new FmActionEdit();
                fm.Doc = doc;
                fm.setActionCount(ac);

                if (fm.ShowDialog() == DialogResult.OK)
                {
                    dgvItems.InvalidateRow(rowIndex);
                }
            }
        }

        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            if (dgvItems.CurrentRow != null)
                EditAction(dgvItems.CurrentRow.Index);
        }

        private void toolStripButton4_Click(object sender, EventArgs e)
        {
            if (dgvItems.SelectedRows.Count > 0)
            {
                if (MessageBox.Show("Удалить акции?", "Подтверждение", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                {
                    SimpleDataSet<DanaAction> dsOut = new SimpleDataSet<DanaAction>(DanaAction.OBJECT_NAME, false);
                    foreach (DataGridViewRow r in dgvItems.SelectedRows)
                    {
                        DanaAction da = r.DataBoundItem as DanaAction;
                        da.hidden = 1;
                        dsOut.Add(da);
                    }

                    List<IDataSet> upd = new List<IDataSet>(new IDataSet[] { dsOut });
                    if (DataModule.UpdateDataSet(upd, null, null, Config.GetConfig().GetConnection()))
                    {
                        SortableBindingList<DanaAction> src = (SortableBindingList<DanaAction>)dgvItems.DataSource;
                        foreach (DanaAction da in dsOut.Data)
                        {
                            src.Remove(da);
                            actions.Remove(da.id);
                        }
                    }
                    else
                    {
                        MessageBox.Show("Ошибка при записи.");
                    }
                }
            }
        }

        private void dgvItems_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            EditAction(e.RowIndex);
        }

        private void dgvItems_KeyDown(object sender, KeyEventArgs e)
        {
            if(e.KeyCode == Keys.Enter && dgvItems.CurrentRow != null)
            {
                EditAction(dgvItems.CurrentRow.Index);
            }
        }
    }
}

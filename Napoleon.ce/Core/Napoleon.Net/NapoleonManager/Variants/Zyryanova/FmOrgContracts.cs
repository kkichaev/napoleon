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
    public partial class FmOrgContracts : Form
    {
        SimpleDataSet<OrgContracts> contracts;
        SimpleDataSet<ContractMatrix> matrix = new SimpleDataSet<ContractMatrix>(ContractMatrix.OBJECT_NAME, false);
        DataSet<string, Org> orgs;

        Agent agent;
        public FmOrgContracts()
        {
            InitializeComponent();
            dgvContracts.AutoGenerateColumns = false;
            dgvOrgs.AutoGenerateColumns = false;
        }

        public Agent Agent { set { agent = value; } }

        public static void Open(Agent agent)
        {
            FmOrgContracts form = new FmOrgContracts();
            form.Agent = agent;
            form.Show();
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
            ReplacedSet rs = new ReplacedSet(agent.id, contracts);
            List<ReplacedSet> rpl = new List<ReplacedSet>();
            rpl.Add(rs);

            bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
            if (showDialog)
                MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

            return ret;
        }

        private void RefreshData()
        {
            List<IDataSet> upd = new List<IDataSet>();

            if(matrix.Count == 0)
                upd.Add(matrix);

            orgs = (DataSet<string, Org>)DataModule.GetUserDataSet(agent.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);
            if(orgs.Count == 0)
            upd.Add(orgs);
            contracts = (SimpleDataSet<OrgContracts>)DataModule.GetUserDataSet(agent.id, OrgContracts.OBJECT_NAME, typeof(SimpleDataSet<OrgContracts>), true);
            upd.Add(contracts);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        void DoLoadData()
        {
            List<Org> o = new List<Org>();
            foreach (Org org in orgs.Data)
                o.Add(org);
            o.Sort();
            dgvOrgs.DataSource = new  SortableBindingList<Org>(o);
            tsbSave.Enabled = false;
        }

        private void tsbSave_Click(object sender, EventArgs e)
        {
            tsbSave.Enabled = !SaveChanges(true);
        }

        private void tsbRefresh_Click(object sender, EventArgs e)
        {
            if(CheckChanges())
                RefreshData();
        }

        private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
        {
            Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;
            loadContracts(o);
        }


        void loadContracts(Org o)
        {
            List<OrgContracts> cm = new List<OrgContracts>();
            foreach (OrgContracts m in contracts.Data)
            {
                if (m.id == o.id)
                {
                    cm.Add(m);
                }
            }

            dgvContracts.DataSource = new BindingList<OrgContracts>(cm);
        }

        private void tsbAdd_Click(object sender, EventArgs e)
        {
            List<OrgContracts> cur = new List<OrgContracts>();
            foreach(OrgContracts oc in (BindingList<OrgContracts>)dgvContracts.DataSource)
            {
                cur.Add(oc);
            }

            FmContractSelect cs = new FmContractSelect();
            cs.SetData(matrix, cur);
            if(cs.ShowDialog() == DialogResult.OK)
            {
                Org o = dgvOrgs.CurrentRow.DataBoundItem as Org;
                List<string> ret = cs.Selected;

                foreach(string s in ret)
                {
                    bool find = false;
                    foreach(OrgContracts oc in cur)
                    {
                        if(oc.name == s)
                        {
                            find = true;
                            break;
                        }
                    }
                    if(!find)
                    {
                        OrgContracts nc = new OrgContracts();
                        nc.id = o.id;
                        nc.name = s;
                        contracts.Add(nc);
                    }
                    foreach(OrgContracts oc in cur)
                    {
                        if(!ret.Contains(oc.name))
                        {
                            removeContract(oc);
                        }
                    }
                }

                loadContracts(o);
                tsbSave.Enabled = true;
            }
        }

        void removeContract(OrgContracts oc)
        {
            foreach (KeyValuePair<int, OrgContracts> kv in contracts)
            {
                if (kv.Value == oc)
                {
                    contracts.Remove(kv.Key);
                    break;
                }
            }
        }

        private void tsbDel_Click(object sender, EventArgs e)
        {
            foreach (DataGridViewRow r in dgvContracts.SelectedRows)
            {
                OrgContracts oc = r.DataBoundItem as OrgContracts;
                removeContract(oc);
            }

            tsbSave.Enabled = true;
        }
    }
}

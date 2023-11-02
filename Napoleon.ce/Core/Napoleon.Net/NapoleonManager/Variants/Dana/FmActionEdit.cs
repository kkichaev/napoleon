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
    public partial class FmActionEdit : Form
    {
        DanaAction doc;
        DataSet<string, OrgCluster> dsCluster;

        public FmActionEdit()
        {
            InitializeComponent();

            dsCluster = DataModule.Get(OrgCluster.OBJECT_NAME) as DataSet<string, OrgCluster>;
            if (dsCluster != null)
            {
                foreach (OrgCluster oc in dsCluster.Data)
                {
                    cbCluster.Items.Add(oc);
                }
            }

        }

        public void setActionCount(ActionCount ac)
        {
            if(ac != null)
            {
                lbUsed.Text = ((int)(ac.used + 0.01)).ToString() + " использовано";
            }
        }

        public DanaAction Doc { get { return doc; } set { doc = value; UpdateFromDoc(); } }


        void UpdateFromDoc()
        {
            tbName.Text = doc.name;
            tbDescr.Text = doc.descr;
            dtpStart.Value = doc.start;
            dtpFinish.Value = doc.finish;

            OrgCluster oc;
            if (dsCluster.TryGetValue(doc.clusterId, out oc))
                cbCluster.SelectedItem = oc;
            else
            {
                cbCluster.SelectedItem = null;
            }

            setItemButtonText();
            setOrgButtonText();

            List<DanaAction.Item> src = new List<DanaAction.Item>(doc.items);
            dgvItems.DataSource = new BindingList<DanaAction.Item>(src);

            if(doc.type == DanaAction.GIFT_TYPE)
            {
                rbItem.Checked = true;
            } else
            {
                rbDiscount.Checked = true;
            }

            UpdateFromType(doc.type);

            tbDiscount.Text = doc.discount.ToString();
            tbQty.Text = doc.qty.ToString();
        }

        bool SaveDoc()
        {
            doc.start = dtpStart.Value;
            doc.finish = dtpFinish.Value;
            doc.name = tbName.Text;
            doc.descr = tbDescr.Text;
            Double.TryParse(tbDiscount.Text, out doc.discount);
            Double.TryParse(tbQty.Text, out doc.qty);

            doc.items.Clear();
            foreach(DanaAction.Item i in (BindingList<DanaAction.Item>)dgvItems.DataSource)
            {
                doc.items.Add(i);
            }

            doc.type = (rbItem.Checked) ? DanaAction.GIFT_TYPE :
                DanaAction.DISCOUNT_TYPE;

            OrgCluster sel = cbCluster.SelectedItem as OrgCluster;
            if (sel != null)
            {
                doc.cluster = sel;
                doc.clusterId = sel.id;
            } else
            {
                doc.cluster = null;
                doc.clusterId = "";
            }

            SimpleDataSet<DanaAction> wr = new SimpleDataSet<DanaAction>(DanaAction.OBJECT_NAME, false);
            wr.Add(doc);
            List<IDataSet> upd = new List<IDataSet>(new IDataSet[] { wr });

            if(!DataModule.UpdateDataSet(upd, null, null, Config.GetConfig().GetConnection()))
            {
                return false;
            }

            return true;
        }

        private void button3_Click(object sender, EventArgs e)
        {
            if(SaveDoc())
            {
                DialogResult = DialogResult.OK;
                Close();
            }
            else
            {

            }
        }

        private void btnOrg_Click(object sender, EventArgs e)
        {
            List<Org> sel = FmSelectOrgs.DoSelect(null, true);
            if (sel != null)
            {
                if (sel.Count >= 1)
                {
                    doc.org = sel[0];
                    doc.orgId = doc.org.id;
                }
                else
                {
                    doc.org = null;
                    doc.orgId = "";
                }
                setOrgButtonText();
            }
        }

        void setOrgButtonText()
        {
            if (doc.org != null)
            {
                btnOrg.Text = doc.org.Name;
            }
            else
            {
                btnOrg.Text = "Применить для клиента...";
            }
        }

        void setItemButtonText()
        {
            if (doc.item != null && doc.item.Name.Length > 0)
                btnItem.Text = doc.item.Name;
            else
                btnItem.Text = "Выбрать товар...";
        }

        private void btnItem_Click(object sender, EventArgs e)
        {
            Price p;
            if (FmSelectSKU.SkuDialogQuery(this, out p) == DialogResult.OK)
            {
                doc.itemId = p.id;
                doc.item = p;
                setItemButtonText();
            }
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            Price p;
            if (FmSelectSKU.SkuDialogQuery(this, out p) == DialogResult.OK)
            {
                DanaAction.Item i = ((BindingList<DanaAction.Item>)dgvItems.DataSource).AddNew();
                i.item = p;
                i.id = p.id;
                i.qty = 1;
            }
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            if(dgvItems.SelectedRows.Count > 0)
            {
                if(MessageBox.Show("Удалить элементы?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                {
                    List<DanaAction.Item> src = new List<DanaAction.Item>();
                    foreach(DataGridViewRow r in dgvItems.SelectedRows)
                    {
                        src.Add(r.DataBoundItem as DanaAction.Item);
                    }

                    src.ForEach(x =>
                        ((BindingList<DanaAction.Item>)dgvItems.DataSource).Remove(x));
                }
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        void ChoosePriceItem(int rowIndex)
        {
            if (rowIndex < 0 || rowIndex >= dgvItems.Rows.Count)
                return;

            DanaAction.Item i = dgvItems.Rows[rowIndex].DataBoundItem as DanaAction.Item;
            Price p;
            if (FmSelectSKU.SkuDialogQuery(this, out p) == DialogResult.OK)
            {
                i.item = p;
                i.id = p.id;
                ((BindingList<DanaAction.Item>)dgvItems.DataSource).ResetItem(rowIndex);
            }
        }

        private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
        {
            ChoosePriceItem(e.RowIndex);
        }

        private void dgvItems_KeyDown(object sender, KeyEventArgs e)
        {
            if((e.KeyCode == Keys.Enter || e.KeyCode == Keys.F2) && dgvItems.CurrentCell != null && dgvItems.CurrentCell.ColumnIndex == 0)
            {
                ChoosePriceItem(dgvItems.CurrentRow.Index);
            }
        }

        void UpdateFromType(int actionType)
        {
            bool itemsEnable = true;
            bool discountEnable = true;
            if(actionType == DanaAction.GIFT_TYPE)
            {
                discountEnable = false;
            }
            btnItem.Enabled = itemsEnable;
            tbDiscount.Enabled = discountEnable;
        }

        private void rbItem_CheckedChanged(object sender, EventArgs e)
        {
            UpdateFromType(DanaAction.GIFT_TYPE);
        }

        private void rbDiscount_CheckedChanged(object sender, EventArgs e)
        {
            UpdateFromType(DanaAction.DISCOUNT_TYPE);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            dtpFinish.Value = DateTime.Now.AddDays(-1);
            if(SaveDoc())
            {
                DialogResult = DialogResult.OK;
                Close();

            }
        }
    }
}

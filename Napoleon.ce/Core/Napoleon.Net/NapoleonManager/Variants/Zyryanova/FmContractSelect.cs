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
    public partial class FmContractSelect : Form
    {
        public FmContractSelect()
        {
            InitializeComponent();
        }

        internal void SetData(SimpleDataSet<ContractMatrix> contracts, List<OrgContracts> selected)
        {
            List<String> sel = new List<string>();
            foreach(OrgContracts oc in selected)
            {
                sel.Add(oc.name);
            }

            List<ContractMatrix> src = new List<ContractMatrix>();
            foreach (ContractMatrix oc in contracts.Data)
                src.Add(oc);
            src.Sort();

            foreach(ContractMatrix c in src)
            {
                lbItems.Items.Add(c, sel.Contains(c.name));
            }
        }

        public List<String> Selected
        {
            get
            {
                List<String> ret = new List<string>();
                foreach(object sel in lbItems.CheckedItems)
                {
                    ret.Add(((ContractMatrix)sel).name);
                }

                return ret;
            }
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.OK;
            Close();
        }
    }
}

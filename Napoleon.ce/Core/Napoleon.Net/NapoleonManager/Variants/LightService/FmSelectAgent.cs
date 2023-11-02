using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class FmSelectAgent : Form
    {
        public FmSelectAgent()
        {
            InitializeComponent();
        }

        public void SetAgents(List<Agent> agents)
        {
            agents.Sort();
            foreach (Agent a in agents)
                lstAgents.Items.Add(a);
        }

        public List<Agent> Checked
        {
            get
            {
                List<Agent> ret = new List<Agent>();
                foreach (object o in lstAgents.CheckedItems)
                    ret.Add(o as Agent);

                return ret;
            }
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
            Close();
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.OK;
            Close();
        }
    }
}

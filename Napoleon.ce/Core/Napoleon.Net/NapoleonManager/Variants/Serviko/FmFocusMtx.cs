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
   public partial class FmFocusMtx : Form
   {
      private DataSet<String, AgentAssortMtx> dsAgentMtx = new DataSet<string, AgentAssortMtx>(AgentAssortMtx.OBJECT_NAME);
      private DataSet<int, Matrix> dsMatrix;

      private List<Agent> agents = new List<Agent>();

      public FmFocusMtx()
      {
         InitializeComponent();

         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? new DataSet<int, Matrix>(Matrix.OBJECT_NAME);
         btnSave.Enabled = false;
      }

      private void FmFocusMtx_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               agents.Add(da.agent);
            }

            agents.Sort();
         }

         btnRefresh.PerformClick();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsAgentMtx);
         upd.Add(dsMatrix);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         List<Matrix> mtx = new List<Matrix>();
         mtx.AddRange(dsMatrix.Values);
         mtx.Sort(new Comparison<Matrix>(delegate(Matrix lhs, Matrix rhs) { return lhs.name.CompareTo(rhs.name); }));
         Matrix empty = new Matrix();
         mtx.Insert(0, empty);

         MatrixClmn.Items.Clear();
         foreach (Matrix m in mtx)
            if (m != null && m.name != null && m.name.Length > 0 && m.items != null && m.items.Count > 0)
               MatrixClmn.Items.Add(m.name);

         List<AgentAssortMtx> data = new List<AgentAssortMtx>();
         foreach (Agent a in agents)
         {
            if (!dsAgentMtx.ContainsKey(a.id))
            {
               AgentAssortMtx aam = new AgentAssortMtx();
               aam.userid = a.id;
               aam.agent = a;
               dsAgentMtx[a.id] = aam;
            }

            data.Add(dsAgentMtx[a.id]);
         }

         grid.DataSource = data;
         btnSave.Enabled = false;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(dsAgentMtx);

         if (DataModule.WriteDataSet(wrSet, Config.GetConfig().GetConnection()))
            DialogUtil.SavedGood(this);
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void FmFocusMtx_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }
   }

   partial class AgentAssortMtx : GRSoft.Network.DataObject
   {
      public String Agent { get { return agent != null ? agent.Name : string.Empty; } }
      public String Matrix { get { return matrix; } set { matrix = value; } }
   }
}

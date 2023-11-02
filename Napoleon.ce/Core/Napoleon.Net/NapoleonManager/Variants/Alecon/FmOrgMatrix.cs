using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgMatrix : Form
   {
      DataSet<string, Org> dsOrg;
      DataSet<int, Matrix> dsMatrix;
      DataSet<string, OrgMtx> dsOrgMatrix;

      public FmOrgMatrix()
      {
         InitializeComponent();
         dgvOrg.AutoGenerateColumns = false;
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? 
            new DataSet<int, Matrix>(Matrix.OBJECT_NAME);
         dgvOrg.DataError += new DataGridViewDataErrorEventHandler(dgvOrg_DataError);
      }

      void dgvOrg_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         //throw new NotImplementedException();
      }

      private void FillAgents()
      {
         if (CurrentUser.user == null)
            return;

         cbAgents.Items.Clear();
         List<Agent> list = new List<Agent>();

         foreach (Agent a in CurrentUser.user.GetAgents().Data)
            list.Add(a);

         if (list.Count > 0)
         {
            list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         }

         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;
      }

      private void FmOrgMatrix_Load(object sender, EventArgs e)
      {
         FillAgents();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DataModule.DataProcessed += new EventHandler(DataLoaded);

         if (cbAgents.SelectedItem as Agent != null)
         {
            string id = ((Agent)cbAgents.SelectedItem).id;
            dsOrg = DataModule.GetUserDataSet(id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, id), dsOrg.Name);

            dsOrgMatrix = DataModule.GetUserDataSet(id, "OrgMtx", typeof(DataSet<string, OrgMtx>)) as DataSet<string, OrgMtx>;
            dsOrgMatrix.Command = new ServerCommand(Commands.Impersonate(Commands.GET, id), dsOrgMatrix.Name);
         }

         List<IDataSet> upd = new List<IDataSet>();

         if (dsOrg != null)
            upd.Add(dsOrg);

         upd.Add(dsMatrix);
         upd.Add(dsOrgMatrix);

         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            upd, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      private void ControlsFillAfterLoaded()
      {
         FmWait.CloseForm();
         List<OrgMtx> list = new List<OrgMtx>();

         foreach (Org o in dsOrg.Values)
         {
            OrgMtx om = new OrgMtx();
            om.id = o.id;
            om.org = o;

            Agent a = (cbAgents.SelectedItem) as Agent;

            if (a != null)
               om.userid = a.id;

            if (dsOrgMatrix.ContainsKey(o.id))
               om.matrix = dsOrgMatrix[o.id].matrix;

            list.Add(om);
         }



         list.Sort(new Comparison<OrgMtx>(delegate(OrgMtx lhs, OrgMtx rhs) { return lhs.Name.CompareTo(rhs.Name); }));
         dgvOrg.DataSource = list;

         List<Matrix> mtx = new List<Matrix>();
         mtx.AddRange(dsMatrix.Values);
         mtx.Sort(new Comparison<Matrix>(delegate(Matrix lhs, Matrix rhs) { return lhs.name.CompareTo(rhs.name); }));
         Matrix empty = new Matrix();
         mtx.Insert(0, empty);

         dgvOrgMatrix.Items.Clear();
         foreach(Matrix m in mtx)
            if(m != null && m.name != null && m.name.Length > 0 && 
               m.items != null && m.items.Count > 0)
               dgvOrgMatrix.Items.Add(m.name);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (cbAgents.SelectedItem as Agent != null)
         {
            string id = ((Agent)cbAgents.SelectedItem).id;
            DataSet<string, OrgMtx> ds = new DataSet<string, OrgMtx>(OrgMtx.OBJECT_NAME, false);

            foreach(DataGridViewRow row in dgvOrg.Rows)
            {
               OrgMtx o = row.DataBoundItem as OrgMtx;

               if (o != null && o.matrix != null && o.matrix.Length > 0)
                  ds.Add(o.id, o);
            }

            List<ReplacedSet> list = new List<ReplacedSet>();
            ReplacedSet rs = new ReplacedSet(id, ds);
            list.Add(rs);
            DataModule.UpdateDataSet(null, null, list, Config.GetConfig().GetConnection());
         }
      }
   }

   class OrgMtx : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMtx";
      [KeyField]
      public string id;
      public string userid;
      public string matrix;

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string Matrix { get { return matrix; } set { matrix = value; } }
      public string Name { get { return org != null ? org.name : "Объект с кодом <" + id + ">"; } }
   }
}

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
   public partial class FmRetMatrix : Form
   {
      DataSet<string, Org> dsOrg;
      DataSet<int, Matrix> dsMatrix;
      DataSet<string, RetMtx> dsRetMatrix;

      public FmRetMatrix()
      {
         InitializeComponent();
         dgvOrg.AutoGenerateColumns = false;
         dsMatrix = (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME) ?? 
            new DataSet<int, Matrix>(Matrix.OBJECT_NAME);
         dgvOrg.DataError += new DataGridViewDataErrorEventHandler(dgvOrg_DataError);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         if (MainForm.Instance.CheckIsMainDataPresents(true))
         {
            FillAgents();
            RefreshData();
         }
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


      void RefreshData()
      {

         DataModule.DataProcessed += new EventHandler(DataLoaded);

         if (cbAgents.SelectedItem as Agent != null)
         {
            string id = ((Agent)cbAgents.SelectedItem).id;
            dsOrg = DataModule.GetUserDataSet(id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, id), dsOrg.Name);

            dsRetMatrix = DataModule.GetUserDataSet(id, "RetMtx", typeof(DataSet<string, RetMtx>)) as DataSet<string, RetMtx>;
            dsRetMatrix.Command = new ServerCommand(Commands.Impersonate(Commands.GET, id), dsRetMatrix.Name);
         }

         List<IDataSet> upd = new List<IDataSet>();

         if (dsOrg != null)
            upd.Add(dsOrg);

         upd.Add(dsMatrix);
         if( dsRetMatrix != null )
            upd.Add(dsRetMatrix);

         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            upd, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      private void ControlsFillAfterLoaded()
      {
         FmWait.CloseForm();
         List<RetMtx> list = new List<RetMtx>();

         foreach (Org o in dsOrg.Values)
         {
            RetMtx om = new RetMtx();
            om.id = o.id;
            om.org = o;

            Agent a = (cbAgents.SelectedItem) as Agent;

            if (a != null)
               om.userid = a.id;

            if (dsRetMatrix.ContainsKey(o.id))
               om.matrix = dsRetMatrix[o.id].matrix;

            list.Add(om);
         }



         list.Sort(new Comparison<RetMtx>(delegate(RetMtx lhs, RetMtx rhs) { return lhs.Name.CompareTo(rhs.Name); }));
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
            DataSet<string, RetMtx> ds = new DataSet<string, RetMtx>(RetMtx.OBJECT_NAME, false);

            foreach(DataGridViewRow row in dgvOrg.Rows)
            {
               RetMtx o = row.DataBoundItem as RetMtx;

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

   class RetMtx : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "RetMtx";
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

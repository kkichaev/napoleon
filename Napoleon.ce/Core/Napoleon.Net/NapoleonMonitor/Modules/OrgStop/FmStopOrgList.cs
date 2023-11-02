using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmStopOrgList : Form
   {
      public int sortColumn;
      public bool sortAsc = true;
      List<Agent> agents = new List<Agent>();
      Dictionary<string, DataSet<string, Org>> orgs = new Dictionary<string, DataSet<string, Org>>();

      DataSet<String, OrgStop> dsStop;
      DataSet<String, OrgStop> dsStopDel;
      List<OrgListData> items = new List<OrgListData>();
      
      public FmStopOrgList()
      {
         InitializeComponent();
         dgvOrgs.AutoGenerateColumns = false;
      }

      protected override void  OnLoad(EventArgs e)
      {
 	      base.OnLoad(e);

         Manager dm = CurrentUser.user as Manager;
         if (agents.Count == 0)
         {
            if (dm == null)
               return;

            foreach (Agent a in dm.GetAgents().Data)
               agents.Add(a);
         }

         List<IDataSet> updSets = new List<IDataSet>();

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               updSets.Add(orgs);
            }
            this.orgs[a.id] = orgs;
         }

         dsStop = new DataSet<string,OrgStop>(OrgStop.OBJECT_NAME, false);
         dsStopDel = new DataSet<string, OrgStop>(OrgStop.OBJECT_NAME, false);

         updSets.Add(dsStop);

         DataModule.DataProcessed += new EventHandler(DataLoaded);
         DataModule.OnDataResponceError += new EventDataResponseError(DataError);
         FmWait.ShowForm(this,  DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new EmptyParamHandler(delegate() { RefreshData(); }));
      }

      private void RefreshData()
      {
         items.Clear();

         List<string> ids = new List<string>();

         foreach(DataSet<string, Org> dsOrg in orgs.Values)
            foreach (Org o in dsOrg.Data)
            {
               if (!ids.Contains(o.id))
               {
                  ids.Add(o.id);
                  items.Add(new OrgListData(o, this));
               }
            }

         sortAsc = true;
         sortColumn = clmnName.DisplayIndex;

         items.Sort();

         dgvOrgs.DataSource = items;
         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);
      }

      void  dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if( dgvOrgs.Columns[dgvOrgs.CurrentCell.ColumnIndex].DisplayIndex == clmnStop.DisplayIndex )
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      void DataError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
      }

      internal bool IsStopped(Org org)
      {
         return dsStop.ContainsKey(org.id);
      }

      internal void SetStopped(Org org, bool value)
      {
         tbSave.Enabled = true;
         OrgStop os = new OrgStop();
         os.id = org.id;
         if (value)
         {
            dsStop[org.id] = os;
            dsStopDel.Remove(org.id);
         } else 
         {
            dsStop.Remove(org.id);
            dsStopDel[org.id] = os;
         }
      }

      protected override void  OnClosing(CancelEventArgs e)
      {
         if( tbSave.Enabled )
         {
            DialogResult res = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if( res == DialogResult.Cancel )
               e.Cancel = true;
            else if( res == DialogResult.Yes )
               SaveData();
         }
 	       base.OnClosing(e);
      }

      private void SaveData()
      {
         bool res = false;
         if (dsStop.Count == 0)
         {  
            res = DataModule.RemoveDataSet(dsStop, Config.GetConfig().GetConnection());
         }
         else
         {
            List<IDataSet> wr = new List<IDataSet>();
            List<IDataSet> rm = new List<IDataSet>();
            wr.Add(dsStop);
            if (dsStopDel.Count > 0)
               rm.Add(dsStopDel);
            res = DataModule.UpdateDataSet(wr, rm, null, Config.GetConfig().GetConnection());
         }
         if( res )
            tbSave.Enabled = false;
      }

      private void tbSave_Click(object sender, EventArgs e)
      {
         SaveData();
      }

      private void tsClearSearch_Click(object sender, EventArgs e)
      {
         tsSearch.Clear();

         items.Sort();
         dgvOrgs.DataSource = items;
      }

      private void tsSearch_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();
         timer1.Start();
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         String text = tsSearch.Text.ToUpper();
         if( text.Length == 0 )
            dgvOrgs.DataSource = items;
         else
         {
            List<OrgListData> filtred = new List<OrgListData>();
            foreach(OrgListData od in items)
            {
               if( od.Name.ToUpper().Contains(text) )
                  filtred.Add(od);
            }

            dgvOrgs.DataSource = filtred;
         }
      }

      private void dgvOrgs_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (dgvOrgs.Columns[e.ColumnIndex].DisplayIndex == clmnStop.DisplayIndex)
         {
            if (sortColumn == clmnStop.DisplayIndex)
               sortAsc = !sortAsc;
            sortColumn = clmnStop.DisplayIndex;
         }
         else
         {
            if (sortColumn == clmnName.DisplayIndex)
               sortAsc = !sortAsc;
            sortColumn = clmnName.DisplayIndex;
         }

         List<OrgListData> sitems = ((List<OrgListData>)dgvOrgs.DataSource);
         sitems.Sort();
         dgvOrgs.DataSource = null;
         dgvOrgs.DataSource = sitems;
      }

      private void dgvOrgs_CurrentCellDirtyStateChanged_1(object sender, EventArgs e)
      {
         if (dgvOrgs.CurrentCell.ColumnIndex == clmnStop.DisplayIndex)
         {
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
            dgvOrgs.InvalidateRow(dgvOrgs.CurrentCell.RowIndex);
         }
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.RowIndex >= 0)
         {
            OrgListData data = dgvOrgs.Rows[e.RowIndex].DataBoundItem as OrgListData;
            if (data != null)
               e.CellStyle.BackColor = data.Stopped ? Color.LightSteelBlue : dgvOrgs.DefaultCellStyle.BackColor;
         }
      }
   }

   class OrgListData : Org, IComparable<OrgListData>
   {
      //Org org;
      FmStopOrgList owner;

      public OrgListData(Org org, FmStopOrgList owner)
      {
         //this.org = org;
         try
         {
            FieldInfo[] myObjectFields = typeof(Org).GetFields(BindingFlags.Public | BindingFlags.Instance);
            foreach (FieldInfo fi in myObjectFields)
               fi.SetValue(this, fi.GetValue(org));
         }
         catch (Exception )
         {
         }
         this.owner = owner;
      }

      public bool Stopped
      {
         get { return owner.IsStopped(this); }
         set { owner.SetStopped(this, value); }
      }
   
#region Члены IComparable<OrgListData>
      public int  CompareTo(OrgListData other)
      {
         if (owner.sortColumn == owner.clmnStop.DisplayIndex)
         {
            int res = 0;
            if (Stopped)
               res--;
            if (other.Stopped)
               res++;
            if (res != 0)
               return owner.sortAsc ? res : -res;
         }
 	      return owner.sortAsc ? Name.CompareTo(other.Name) : other.Name.CompareTo(Name);
      }

#endregion
   }

   class OrgStop : GRSoft.Network.DataObject
   {
      public static readonly String OBJECT_NAME = "OrgStop";
      
      [KeyField]
      public String id;
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.UILib;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrg : Form
   {
      private DataSet<string, Org> dsOrg = new DataSet<string,Org>(Org.COMMON_OBJECT_NAME, false);

      private SortableBindingList<Org> data = new SortableBindingList<Org>();

      private DataSet<string, Org> chBase = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      private DataSet<string, Org> rmBase = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);


      private System.Threading.Timer findWait = null;

      SortableBindingList<Org> filter = new SortableBindingList<Org>();

      public FmOrg()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;

         foreach (DataGridViewColumn c in grid.Columns)
            cbFind.Items.Add(c.HeaderText);

         if (cbFind.Items.Count > 0)
            cbFind.SelectedIndex = 0;

         btnSave.Enabled = false;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Save(false);
      }

      private void Save(bool silent)
      {
         List<IDataSet> wrSet = new List<IDataSet>();

         if(chBase.Count > 0)
            wrSet.Add(chBase);

         List<IDataSet> remSet = new List<IDataSet>();

         if(rmBase.Count > 0)
            remSet.Add(rmBase);

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         Dictionary<string, ReplacedSet> wr = new Dictionary<string, ReplacedSet>();

         foreach (ReplacedSet rs in wr.Values)
            rpl.Add(rs);

         if (DataModule.UpdateDataSet(wrSet, remSet, rpl, Config.GetConfig().GetConnection()))
         {
            btnSave.Enabled = false;
            ClearChanges(); 

            if(!silent)
               DialogUtil.SavedGood(this);
         }
         else
            if(!silent)
               DialogUtil.UpdateErrMsg(this);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);

         FmWait.StdDataRefresh(this, upd, DoLoadData);

         btnSave.Enabled = false;
         ClearChanges();
      }

      private void ClearChanges()
      {
         chBase.Clear();
         rmBase.Clear();
      }

      private void DoLoadData()
      {
         data.Clear();

         foreach(Org o in dsOrg.Data)
            data.Add(o);

         DoSearch(string.Empty);
         //if(grid.DataSource != null)
         //   grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
      }

      private void FmOrg_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Org o = new Org();
         o.id = GRSoft.Network.DataObject.GenId();

         filter.Add(o);
         data.Add(o);
         grid.CurrentCell = grid[0, grid.RowCount - 1];
         grid.BeginEdit(true);         
      }
      
      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;

         if (e.RowIndex != -1)
         {
            Org o = grid.Rows[e.RowIndex].DataBoundItem as Org;

            if (o != null)
            {
               if (!chBase.ContainsKey(o.id))
                  chBase.Add(o.id, o);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Org o = grid.CurrentRow.DataBoundItem as Org;

         if(o != null && DialogUtil.AskToDel(this))
         {
            if (!rmBase.ContainsKey(o.id))
                  rmBase.Add(o.id, o);

            data.Remove(o);
            filter.Remove(o);

            btnSave.Enabled = true;
         }
      }

      private void FmOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save(true);
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         tbFind.Text = string.Empty;
      }

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         if (findWait != null)
            findWait.Dispose();
         findWait = new System.Threading.Timer(new TimerCallback(TimePassed), tbFind.Text.Trim(), 500, 0);
      }

      void DoSearch(string val)
      {
         if (cbFind.SelectedIndex >= 0 && cbFind.SelectedIndex < grid.Columns.Count)
         {
            val = val.ToUpper().Trim();
            
            filter.Clear();
            String prop = grid.Columns[cbFind.SelectedIndex].DataPropertyName;

            if (prop == "Userid")
               prop = "AgentName";

            if (prop.Trim().Length > 0)
            {
               PropertyInfo f = typeof(Org).GetProperty(prop);

               if (f != null)
                  foreach (Org o in data)
                     if (val.Length == 0 || f.GetValue(o, null).ToString().ToUpper().Contains(val))
                        filter.Add(o);

               grid.DataSource = filter;

               if (grid.RowCount > 0 && grid.ColumnCount > cbFind.SelectedIndex)
                  grid.CurrentCell = grid[cbFind.SelectedIndex, 0];
            }
         }
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FMOrgMutex");
            if (m.WaitOne(0))
               grid.Invoke(new InvokeParamHandler(
                  delegate(object param)
                  {
                     DoSearch((string)param);
                  }), new object[] { o });
            m.ReleaseMutex();
         }
         catch (Exception)
         {
         }
      }

      private void grid_KeyPress(object sender, KeyPressEventArgs e)
      {
         if (!grid.IsCurrentCellInEditMode)
         {
            tbFind.Focus();
            SendKeys.Send(e.KeyChar.ToString());
         }
      }

      private void grid_CellMouseDown(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (cbFind.Items.Count > e.ColumnIndex)
            cbFind.SelectedIndex = e.ColumnIndex;
      }

      private delegate void DoTraverse(Org org);


      private void TraverseSelection(DoTraverse work)
      {
         List<int> rows = new List<int>();

         foreach (DataGridViewCell c in grid.SelectedCells)
         {
            int r = c.RowIndex;

            if (rows.Contains(r))
               continue;

            rows.Add(r);

            Org o = grid.Rows[r].DataBoundItem as Org;

            if (o != null)
               work(o);

            chBase.Add(o.id, o);
         }

         grid.Invalidate();
         btnSave.Enabled = true;
      }


      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         //Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;
      }

      private void grid_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         //Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         //if (((DataGridView)sender).CurrentRow != null)
         //   SettingCtxMenu(OrgIsPotenizal(((DataGridView)sender).CurrentRow.DataBoundItem as Org));
      }

      private void grid_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         //suppressing all errors
      }

      private void btnCensus_Click(object sender, EventArgs e)
      {
         DoSearch(tbFind.Text.Trim());
      }
   }

   public class OrgContact : GRSoft.Network.DataObject
   {
      public string name = string.Empty;
      public string phone = string.Empty;
   }

   public partial class Org
   {
      [ItemType(typeof(OrgContact))]
      public List<OrgContact> contacts = new List<OrgContact>();

      public string Contact
      {
         get { return contacts.Count == 0 ? string.Empty : contacts[0].name; }
         set
         {
            if (contacts.Count == 0)
               contacts.Add(new OrgContact());

            contacts[0].name = value;
         }
      }

      public string Phone
      {
         get { return contacts.Count == 0 ? string.Empty : contacts[0].phone; }
         set
         {
            if (contacts.Count == 0)
               contacts.Add(new OrgContact());

            contacts[0].phone = value;
         }
      }

      public string NName { get { return name; } set { name = value; } }
   }
}

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
      private DataSet<string, PotenzialOrg> dsPtnzOrg = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);

      private SortableBindingList<Org> data = new SortableBindingList<Org>();

      private DataSet<string, Org> chBase = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      private DataSet<string, Org> rmBase = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      private DataSet<string, PotenzialOrg> chPtz = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
      private DataSet<string, PotenzialOrg> rmPtz = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
      private SimpleDataSet<PODel> podel = new SimpleDataSet<PODel>(PODel.OBJECT_NAME, false);


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

         dsPtnzOrg.Filter = DataUtils.MakeFilterFromAgents(null, Agents.GetDataSet());
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

         if(podel.Count > 0)
            wrSet.Add(podel);

         List<IDataSet> remSet = new List<IDataSet>();

         if(rmBase.Count > 0)
            remSet.Add(rmBase);

         if(rmPtz.Count > 0)
            remSet.Add(rmPtz);

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         Dictionary<string, ReplacedSet> wr = new Dictionary<string, ReplacedSet>();

         foreach (Org o in chPtz.Data)
         {
            ReplacedSet rs = null;
            if (wr.ContainsKey(o.userid))
               rs = wr[o.userid];
            else
            {
               rs = new ReplacedSet(o.userid, new SimpleDataSet<Org>(PotenzialOrg.OBJECT_NAME, false, true));
               rs.dontRemove = true;
               wr[o.userid] = rs;
            }

            ((SimpleDataSet<Org>)rs.data).Add(o);
         }

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
         upd.Add(dsPtnzOrg);

         FmWait.StdDataRefresh(this, upd, DoLoadData);

         btnSave.Enabled = false;
         ClearChanges();
      }

      private void ClearChanges()
      {
         chBase.Clear();
         rmBase.Clear();
         chPtz.Clear();
         rmPtz.Clear();
         podel.Clear();
      }

      private void DoLoadData()
      {
         data.Clear();

         foreach(Org o in dsOrg.Data)
            data.Add(o);

         foreach (Org o in dsPtnzOrg.Data)
            data.Add(o);

         DoSearch(string.Empty);
         grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
      }

      private void FmOrg_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         Manager dm = CurrentUser.user as Manager;
         list.Add(new Agent());

         if (dm != null)
         {
            Agents agents = dm.GetAgents();

            if (agents != null)
               foreach (Agent a in agents.Data)
                  list.Add(a);
         }

         agent.DataSource = list;
         agent.DisplayMember = "Name";
         agent.ValueMember = "ID";
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
               if (o is PotenzialOrg && !chPtz.ContainsKey(o.id))
                  chPtz.Add(o.id, o);
               else if (!chBase.ContainsKey(o.id))
                  chBase.Add(o.id, o);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Org o = grid.CurrentRow.DataBoundItem as Org;

         if(o != null && DialogUtil.AskToDel(this))
         {
            if (o is PotenzialOrg)
            {
               PODel p = new PODel();
               p.id = o.id;
               p.userid = o.userid;

               podel.Add(p);

               if (!rmPtz.ContainsKey(o.id))
                  rmPtz.Add(o.id, o);
            }else if (!rmBase.ContainsKey(o.id))
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
                     {
                        if(btnCensus.CheckState == CheckState.Unchecked || (btnCensus.CheckState == CheckState.Checked && OrgIsPotenizal(o)))
                           filter.Add(o);
                     }

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

         if (e.Button == System.Windows.Forms.MouseButtons.Right && e.ColumnIndex != -1 && e.RowIndex != -1)
            SettingCtxMenu(OrgIsPotenizal(((DataGridView)sender).CurrentRow.DataBoundItem as Org));
      }

      private void SettingCtxMenu(bool po)
      {
         miAssignToAgent.Visible = !po;
         miUnissign.Visible = !po;
         miMainOrg.Visible = po;
      }

      private delegate void DoTraverse(Org org);

      private void miAssignToAgent_Click(object sender, EventArgs e)
      {
         Agent a = FmSelectAgent.DoSelect();

         if (a != null)
         {
            TraverseSelection((o) =>
            {
               o.agent = a;
               o.userid = a.id;
            });
         }
      }

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

      private void miUnissign_Click(object sender, EventArgs e)
      {
         TraverseSelection((o) =>
         {
            o.agent = null;
            o.userid = string.Empty;
         });
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;
         if (o != null && OrgIsPotenizal(o))
            e.CellStyle.ForeColor = Color.Red;
      }

      private void grid_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;

         if (o != null && OrgIsPotenizal(o) && e.ColumnIndex == agent.Index)
            ((DataGridView)sender)[e.ColumnIndex, e.RowIndex].ReadOnly = true;
         
         if(o != null)
            SettingCtxMenu(o is PotenzialOrg);
      }

      private bool OrgIsPotenizal(Org o)
      {
         return o != null && o is PotenzialOrg && !((PotenzialOrg)o).converted;
      }

      private void miMainOrg_Click(object sender, EventArgs e)
      {
         TraverseSelection((o) =>
         {
            if (chPtz.ContainsKey(o.id))
               chPtz.Remove(o.id);

            if(!rmPtz.ContainsKey(o.id))
               rmPtz.Add(o.id, o);

            PODel p = new PODel();
            p.id = o.id;
            p.userid = o.userid;

            podel.Add(p);

            ((PotenzialOrg)o).converted = true;
            btnSave.Enabled = true;
         });
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (((DataGridView)sender).CurrentRow != null)
            SettingCtxMenu(OrgIsPotenizal(((DataGridView)sender).CurrentRow.DataBoundItem as Org));
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            ConvertOrg(dlg.FileName);
      }

      private void ConvertOrg(string file)
      {
         dsOrg.Clear();
         dsPtnzOrg.Clear();
         ClearChanges();

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);

         var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [Лист1$]", connectionString);
         var ds = new DataSet();

         adapter.Fill(ds, "name");

         DataTable data = ds.Tables["name"];

         foreach (DataRow row in data.Rows)
         {
            object[] r = row.ItemArray;

            if (r[0].ToString().Length == 0)
               break;
            
            Org org = new Org();
            org.id = GRSoft.Network.DataObject.GenId();
            org.name = r[0].ToString().Trim();
            org.address = r[1].ToString().Trim();
            //string adr = r[1].ToString().Trim();

            //if (adr.Length > 0)
            //   org.address = "Пенза, " + adr;

            string fio = r[2].ToString().Trim();
            string phone = r[3].ToString().Trim();

            if (fio.Length > 0 || phone.Length > 0)
            {
               OrgContact c = new OrgContact();
               c.name = fio;
               c.phone = phone;

               org.contacts.Add(c);
            }

            chBase.Add(org.id, org);
         }

         btnSave.Enabled = true;
         btnSave.PerformClick();
         btnRefresh.PerformClick();
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
}

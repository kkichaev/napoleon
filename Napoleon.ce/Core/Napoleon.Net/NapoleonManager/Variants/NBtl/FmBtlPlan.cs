using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmBtlPlan : Form
   {
      private DataSet<string, ContractDef> dsContract;
      private DataSet<string, Org> dsOrg;
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<int, BtlPlan> dsPlan;
      private DataSet<string, City> dsCity;
      private DataSet<string, BtlPlan> changed = new DataSet<string, BtlPlan>(BtlPlan.OBJECT_NAME, false);
      private Dictionary<String, BtlPlan> plan = new Dictionary<string, BtlPlan>();
      private DataSet<string, Price> dsPrice;

      SortableBindingList<BtlPlan> filter = new SortableBindingList<BtlPlan>();
      SortableBindingList<BtlPlan> data = new SortableBindingList<BtlPlan>();
      private System.Threading.Timer findWait = null;

      public FmBtlPlan()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsPlan = (DataSet<int, BtlPlan>)DataModule.Get(BtlPlan.OBJECT_NAME) ?? new DataSet<int, BtlPlan>(BtlPlan.OBJECT_NAME);
         dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);

         grid.AutoGenerateColumns = false;

         foreach (DataGridViewColumn c in grid.Columns)
            cbFind.Items.Add(c.HeaderText);

         if (cbFind.Items.Count > 0)
            cbFind.SelectedIndex = 0;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         dsContract.Filter = string.Format("\"finish\" >= ToDate('{0:dd/MM/yyyy}')", DateTime.Now);
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsPrice);
         upd.Add(dsCity);
         upd.Add(dsSlsnet);
         upd.Add(dsPlan);
         upd.Add(dsContract);
         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         cbContract.Items.Clear();

         foreach (ContractDef c in dsContract.Data)
            cbContract.Items.Add(c);

         foreach (BtlPlan p in dsPlan.Data)
            plan[p.id + '\t' + p.cid] = p;

         grid.Sort(grid.Columns[0], ListSortDirection.Ascending);

         if (cbContract.Items.Count > 0)
            cbContract.SelectedIndex = 0;
      }

      private void DoPlan()
      {
         if (cbContract.SelectedItem != null)
         {
            string cid = ((ContractDef)cbContract.SelectedItem).id;

            ClearChanges();
            data.Clear();

            foreach (Org o in dsOrg.Data)
            {
               BtlPlan d = new BtlPlan();
               string key = o.id + '\t' + cid;

               if (plan.ContainsKey(key))
                  d = plan[key];
               else
               {
                  d = new BtlPlan();
                  d.id = o.id;
                  d.cid = cid;
               }

               data.Add(d);
            }

            grid.DataSource = data;
         }
      }

      private void FmBtlPlan_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      void TimePassed(object o)
      {
         try
         {
            Mutex m = new Mutex(false, "FmBtlPlan");
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

      void DoSearch(string val)
      {
         if (cbFind.SelectedIndex >= 0 && cbFind.SelectedIndex < grid.Columns.Count)
         {
            val = val.ToUpper();

            if (val.Length == 0)
               grid.DataSource = data;
            else
            {
               filter.Clear();
               String prop = grid.Columns[cbFind.SelectedIndex].DataPropertyName;

               if (prop.Trim().Length > 0)
               {
                  PropertyInfo f = typeof(BtlPlan).GetProperty(prop);

                  if (f != null)
                     foreach (BtlPlan o in data)
                        if (f.GetValue(o, null).ToString().ToUpper().Contains(val))
                        {
                           filter.Add(o);
                        }

                  grid.DataSource = filter;

                  if (grid.RowCount > 0 && grid.ColumnCount > cbFind.SelectedIndex)
                     grid.CurrentCell = grid[cbFind.SelectedIndex, 0];
               }
            }
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

      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         if (findWait != null)
            findWait.Dispose();
         findWait = new System.Threading.Timer(new TimerCallback(TimePassed), tbFind.Text.Trim(), 500, 0);
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;

         if (e.RowIndex != -1)
            MarkPlanChanged(grid.Rows[e.RowIndex].DataBoundItem as BtlPlan);
      }

      private void MarkPlanChanged(BtlPlan p)
      {
         if (p != null)
         {
            if (!changed.ContainsKey(p.id))
               changed.Add(p.id, p);
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Save(false);
      }

      private void Save(bool silent)
      {
         List<IDataSet> wrSet = new List<IDataSet>();

         if (changed.Count > 0)
            wrSet.Add(changed);

         List<ReplacedSet> rpl = new List<ReplacedSet>();

         if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
         {
            btnSave.Enabled = false;
            ClearChanges();

            if (!silent)
               DialogUtil.SavedGood(this);
         }
         else
            if (!silent)
               DialogUtil.UpdateErrMsg(this);
      }

      private void ClearChanges()
      {
         btnSave.Enabled = false;
         changed.Clear();
      }

      private void cbContract_SelectedIndexChanged(object sender, EventArgs e)
      {
         DoPlan();
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         BtlPlan plan = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as BtlPlan;
         ContractDef cdef = cbContract.SelectedItem as ContractDef;

         if (plan != null && cdef != null)
         {
            Dictionary<String, BtlPlan.BtlPlanItem> items = new Dictionary<string, BtlPlan.BtlPlanItem>();

            foreach (BtlPlan.BtlPlanItem i in plan.items)
               items.Add(i.id, i);

            foreach(ContractIDeftem i in cdef.items)
               if (i.item.my == 1 && !items.ContainsKey(i.id))
               {
                  BtlPlan.BtlPlanItem b = new BtlPlan.BtlPlanItem();
                  b.id = i.id;
                  b.item = i.item;
                  plan.items.Add(b);
               }

            List<BtlPlan.BtlPlanItem> data = new List<BtlPlan.BtlPlanItem>();
            data.AddRange(plan.items);

            dgvItems.DataSource = data;
         }
      }

      private void dgvItems_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         DataGridViewRow r =  grid.CurrentRow;
         
         if(r != null)
         {
            MarkPlanChanged(r.DataBoundItem as BtlPlan);
            btnSave.Enabled = true;
         }
      }

      private void FmBtlPlan_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            Save(true);
      }
   }

   public class OrgPropAttribute : Attribute
   {
      private string prop = string.Empty;

      public OrgPropAttribute(string prop) 
      {
         this.prop = prop;
      }

      public string Prop { get { return prop; } }
   }

   public partial class BtlPlan
   {
      [OrgProp("City")]
      public string city = string.Empty;
      [OrgProp("Slsnet")]
      public string slsnet = string.Empty;
      [OrgProp("Address")]
      public string addr = string.Empty;

      private DataSet<string, Org> org = null;

      private string getData(string name)
      {
         string val = string.Empty;

         FieldInfo fi = GetType().GetField(name);

         if (fi != null)
         {
            val = (string)fi.GetValue(this);

            if(val.Length == 0)
            {
               if (org == null)
                  org = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME);

               if (org.ContainsKey(id))
               {
                  OrgPropAttribute a = (OrgPropAttribute)Attribute.GetCustomAttribute(fi, typeof(OrgPropAttribute));

                  if (a != null)
                  {
                     Org o = org[id];
                     val = o.GetType().GetProperty(a.Prop).GetValue(o, null).ToString();

                     fi.SetValue(this, val);
                  }
               }
            }
         }

         return val;
      }

      public string City  { get { return getData("city"); } }

      public string Slsnet { get { return getData("slsnet"); } }

      public string Addr { get { return getData("addr"); } }

      public double Face { get { return face; } set { face = value; } }

      public partial class BtlPlanItem
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;

         public string Name { get { return item != null ? item.Name : id; } }
         public double Face { get { return face; } set { face = value; } }
      }
   }
}

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
      DataSet<string, Org> dsOrg;
      DataSet<string, Slsnet> dsSlsnet;
      DataSet<string, BtlPlan> dsPlan;
      DataSet<string, City> dsCity;
      DataSet<string, BtlPlan> changed = new DataSet<string,BtlPlan>(BtlPlan.OBJECT_NAME, false);

      SortableBindingList<BtlPlan> filter = new SortableBindingList<BtlPlan>();
      SortableBindingList<BtlPlan> data = new SortableBindingList<BtlPlan>();
      private System.Threading.Timer findWait = null;

      public FmBtlPlan()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         
         dsSlsnet = (DataSet<string, Slsnet>)DataModule.Get(Slsnet.OBJECT_NAME) ?? new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsPlan = (DataSet<string, BtlPlan>)DataModule.Get(BtlPlan.OBJECT_NAME) ?? new DataSet<string, BtlPlan>(BtlPlan.OBJECT_NAME);
         dsCity = (DataSet<string, City>)DataModule.Get(City.OBJECT_NAME) ?? new DataSet<string, City>(City.OBJECT_NAME);

         grid.AutoGenerateColumns = false;

         foreach (DataGridViewColumn c in grid.Columns)
            cbFind.Items.Add(c.HeaderText);

         if (cbFind.Items.Count > 0)
            cbFind.SelectedIndex = 0;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsCity);
         upd.Add(dsSlsnet);
         upd.Add(dsPlan);
         upd.Add(dsOrg);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         ClearChanges();

         foreach (Org o in dsOrg.Data)
         {
            BtlPlan d = new BtlPlan();

            if (dsPlan.ContainsKey(o.id))
               d = dsPlan[o.id];
            else 
            {
               d = new BtlPlan();
               d.id = o.id;
            }

            data.Add(d);
         }

         grid.DataSource = data;
         grid.Sort(grid.Columns[0], ListSortDirection.Ascending);
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
         {
            BtlPlan o = grid.Rows[e.RowIndex].DataBoundItem as BtlPlan;

            if (o != null)
            {
               if (!changed.ContainsKey(o.id))
                  changed.Add(o.id, o);
            }
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
   }
}

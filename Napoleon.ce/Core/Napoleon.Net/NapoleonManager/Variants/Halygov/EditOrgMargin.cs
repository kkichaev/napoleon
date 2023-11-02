using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EditOrgMargin : Form
   {
      List<Agent> agents = new List<Agent>();

      Dictionary<string, DataSet<string, Org>> orgs = new Dictionary<string, DataSet<string, Org>>();
      SimpleDataSet<OrgMargin> margins = new SimpleDataSet<OrgMargin>(OrgMargin.OBJECT_NAME, false);
      DataSet<string, OrgMargin> changed = new DataSet<string, OrgMargin>(OrgMargin.OBJECT_NAME, false);

      SortableBindingList<MarginRowData> allData = new SortableBindingList<MarginRowData>();

      public EditOrgMargin()
      {
         InitializeComponent();
         
         dgvMargins.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         Manager dm = CurrentUser.user as Manager;
         if (agents.Count == 0)
         {
            if (dm == null)
               return;

            foreach (Agent a in dm.GetAgents().Data)
               agents.Add(a);
         }

         string uid = DataUtils.MakeFilterFromAgents(null, dm.GetAgents());

         List<IDataSet> upd = new List<IDataSet>();

         foreach (Agent a in dm.GetAgents().Data)
         {
            DataSet<string, Org> orgs =
               DataModule.GetUserDataSet(a.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (orgs.Count == 0)
            {
               orgs.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orgs.Name);
               upd.Add(orgs);
            }
            this.orgs[a.id] = orgs;
         }

         margins.Filter = uid;
         upd.Add(margins);

         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         changed.Clear();
         btnSave.Enabled = false;

         Dictionary<string, OrgMargin> data = new Dictionary<string, OrgMargin>();
         foreach (OrgMargin om in margins.Data)
            data[om.Key] = om;

         List<MarginRowData> src = new List<MarginRowData>();
         
         foreach(KeyValuePair<string, DataSet<string, Org>> kv in orgs)
         {
            foreach(Org o in kv.Value.Data)
            {
               OrgMargin om = new OrgMargin(kv.Key, o);
               if (data.ContainsKey(om.Key))
                  om = data[om.Key];
               MarginRowData mrd = new MarginRowData(om, o, DataChanged);
               src.Add(mrd);
            }
         }

         allData = new SortableBindingList<MarginRowData>(src, TypeDescriptor.GetProperties(typeof(MarginRowData))["Name"], ListSortDirection.Ascending);
         dgvMargins.DataSource = allData;
      }

      private void DataChanged(MarginRowData data)
      {
         btnSave.Enabled = true;

         OrgMargin om = new OrgMargin();
         om.userid = data.UserID;
         om.id = data.ID;
         om.value = data.Margin;

         string key = om.Key;
         if (changed.ContainsKey(key))
            changed[key].value = om.value;
         else
            changed[key] = om;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         e.Cancel = !CheckChanges();
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(changed);

         bool ret = DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         if (ret)
            changed.Clear();

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData();
      }

      private void tbSetCheck_Click(object sender, EventArgs e)
      {
         Checking(true);
      }

      private void tbResetCheck_Click(object sender, EventArgs e)
      {
         Checking(false);
      }

      private void Checking(bool val)
      {
         int i = 0;

         SortableBindingList<MarginRowData> src = (SortableBindingList<MarginRowData>)dgvMargins.DataSource;
         foreach (MarginRowData mrd in src)
         {
            mrd.Checked = val;
            src.ResetItem(i++);
         }
      }

      private void btnApply_Click(object sender, EventArgs e)
      {
         double val = 0;
         if( !Double.TryParse(tbMargin.Text, out val) )
         {
            MessageBox.Show("Ошибка при вводе числа");
            return;
         }

         SortableBindingList<MarginRowData> src = (SortableBindingList<MarginRowData>)dgvMargins.DataSource;
         int i = 0;
         foreach (MarginRowData mrd in src)
         {
            if (mrd.Checked)
            {
               mrd.Margin = val;
               src.ResetItem(i);
            }
            i++;
         }
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         timer1.Stop();
         DoSearch(tbFind.Text);
      }

      private void DoSearch(string p)
      {
         p = p.ToUpper();

         List<MarginRowData> src = new List<MarginRowData>();
         foreach(MarginRowData mrd in allData)
         {
            if (mrd.Owner.ToUpper().Contains(p) || mrd.Name.ToUpper().Contains(p))
               src.Add(mrd);
         }

         dgvMargins.DataSource = new SortableBindingList<MarginRowData>(src);
      }

      private void btnClearFind_Click(object sender, EventArgs e)
      {
         timer1.Stop();
         clearing = true;
         tbFind.Clear();

         dgvMargins.DataSource = allData;

         clearing = false;
      }

      bool clearing = false;
      private void tbFind_TextChanged(object sender, EventArgs e)
      {
         timer1.Stop();

         if (tbFind.Text.Length > 0)
            timer1.Start();
         else if (!clearing)
            btnClearFind_Click(sender, e);
      }
   }

   
   class OrgMargin : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMargins";

      public string userid = "";
      public string id = "";

      public double value = 0;

      public string Key { get { return userid + "|" + id; } }

      public OrgMargin() { }
      
      public OrgMargin(string key, Org o)
      {
         this.userid = key;
         this.id = o.id;
      }
   }

   class MarginRowData : IComparable<MarginRowData>
   {
      Org org = null;
      string userid = null;
      double value;

      ChangedData Handler;
      bool check = false;

      public MarginRowData(OrgMargin src, Org org, ChangedData Handler)
      {
         this.org = org;
         userid = src.userid;
         value = src.value;

         this.Handler = Handler;
      }

      public double Margin
      { 
         get { return value; }
         set
         {
            if (this.value != value)
            {
               this.value = value;
               Handler(this);
            }
         }
      }

      public string Name { get { return org.Name; } }
      public string Address { get { return org.Address; } }
      public string Owner { get { return org.ownerData; } }

      public string UserID { get { return userid; } }
      public string ID { get { return org.id; } }

      public Org Org { get { return org; } }

      public int CompareTo(MarginRowData other)
      {
         return org.Name.CompareTo(other.org.Name);
      }

      public bool Checked { get { return check; } set { check = value; } }
   }

   delegate void ChangedData(MarginRowData data);
}

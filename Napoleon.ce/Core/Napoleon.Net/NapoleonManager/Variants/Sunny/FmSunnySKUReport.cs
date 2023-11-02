using GRSoft.NapoleonManager.Utils;
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
   public partial class FmSunnySKUReport : Form
   {
      static FmSunnySKUReport instance = null;
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      ManagerFolder selectedFolder = null;
      List<DateTime> weeks;
      Agent currentAgent;
      List<string> checkedFolders;

      public FmSunnySKUReport()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
         RefreshWeeks();
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmSunnySKUReport();
            instance.Show();
         }
         else
            instance.BringToFront();
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         Manager m = CurrentUser.user as Manager;
         if(m != null)
         {
            List<Agent> src = new List<Agent>();
            foreach(Agent a in m.GetAgents().Data)
            {
               src.Add(a);
            }
            src.Sort();
            src.ForEach(x => cbAgents.Items.Add(x));
         }
      }

      private void tsFolder_Click(object sender, EventArgs e)
      {
         if( FmSelectSKU.SelectFolder(this, out selectedFolder) == System.Windows.Forms.DialogResult.OK)
         {
            tsFolder.Text = selectedFolder.name;
            RefreshData();
         }

      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void RefreshData()
      {
         if (selectedFolder == null || cbAgents.SelectedItem == null)
            return;

         DateTime dt = RefreshWeeks();

         Agent selAgent = (Agent)cbAgents.SelectedItem;

         orders.Filter = String.Format("\"userid\" = '{0}' and \"created\" >= ToDate('{1:dd/MM/yyyy}')", selAgent.id, dt);
         List<IDataSet> upd = new List<IDataSet>();

         DataSet<string, Org> orgs = (DataSet<string, Org>)DataModule.GetUserDataSet(selAgent.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);
         if (orgs.Count == 0)
            upd.Add(orgs);

         DataSet<string, Price> price = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         if (price.Count == 0)
         {
            price.Filter = "\"userid\" is null or \"userid\"=''";
            upd.Add(price);
         }
         if (currentAgent == null || currentAgent != selAgent)
         {
            currentAgent = selAgent;
            upd.Add(orders);
         }

         if (upd.Count > 0)
            FmWait.StdDataRefresh(this, upd, DoReport);
         else
            DoReport();
      }

      private DateTime RefreshWeeks()
      {
         weeks = new List<DateTime>();
         DateTime dt = DateTime.Now;
         DateTime ed = dt;
         while (dt.DayOfWeek != DayOfWeek.Monday)
            dt = dt.AddDays(-1);

         DataGridViewColumn[] clmns = new DataGridViewColumn[] { clmnSKU5, clmnSKU4, clmnSKU3, clmnSKU2, clmnSKU1 };
         for (int i = 0; i < 4; i++)
         {
            string text = string.Format("Продано {0:dd/MM/yyyy} - {1:dd/MM/yyyy}", dt, ed);
            clmns[i].HeaderText = text;

            weeks.Add(dt.Date);
            ed = dt.AddDays(-1);
            dt = dt.AddDays(-7);
         }

         clmns[4].HeaderText = string.Format("Продано {0:dd/MM/yyyy} - {1:dd/MM/yyyy}", dt, ed);
         return dt;
      }

      void DoReport() 
      {
         DataSet<string, ManagerFolder> folders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME);
         if (folders == null)
            return;

         checkedFolders = new List<string>();
         int level = -1;
         foreach(ManagerFolder mf in folders.Data)
         {
            if(mf.id == selectedFolder.id)
            {
               level = mf.level;
               checkedFolders.Add(mf.id);
               continue;
            }
            if (level < 0)
               continue;
            if (mf.level <= level)
               break;

            checkedFolders.Add(mf.id);
         }

         TotalRow tr = new TotalRow();
         Dictionary<string, ReportData> data = new Dictionary<string, ReportData>();
         foreach(Order doc in orders.Data)
         {
            if (!data.ContainsKey(doc.id))
               data.Add(doc.id, new ReportData(doc.org));

            ReportData rd = data[doc.id];
            rd.AddDoc(doc, checkedFolders, WeekIndex(doc, weeks));
         }

         List<ReportData> src = new List<ReportData>(data.Values);
         src.ForEach(x => tr.Add(x));
         src.Sort();
         src.Add(tr);
         dgvItems.DataSource = new SortableBindingList<ReportData>(src);
      }

      int WeekIndex(Order doc, List<DateTime> weeks)
      {
         int index = 0;
         foreach (DateTime dt in weeks)
         {
            if (dt.CompareTo(doc.Created) < 0)
               break;
            index++;
         }

         return index;
      }

      public class ReportData : IComparable<ReportData>
      {
         Org org;
         public Dictionary<string, bool>[] items = new Dictionary<string, bool>[5];
         public List<Dictionary<Price, bool>> sold = new List<Dictionary<Price, bool>>();

         public ReportData(Org o)
         {
            this.org = o;
            for(int i=0; i<5; i++)
               sold.Add(new Dictionary<Price,bool>());
         }

         public void AddDoc(Order doc, List<string> folders, int index)
         {
            foreach (OrderItem oi in doc.items)
               if (folders.Contains(oi.item.fid))
               {
                  items[index][oi.id] = true;
                  //values[index] += (int)(oi.qty + 0.5);
                  sold[index][oi.item] = true;
               }
         }

         public string Name { get { return org.Name; } }

         public int W1 { get { return items[4].Count; } }
         public int W2 { get { return items[3].Count; } }
         public int W3 { get { return items[2].Count; } }
         public int W4 { get { return items[1].Count; } }
         public int W5 { get { return items[0].Count; } }

         //public int W1 { get { return values[4]; } }
         //public int W2 { get { return values[3]; } }
         //public int W3 { get { return values[2]; } }
         //public int W4 { get { return values[1]; } }
         //public int W5 { get { return values[0]; } }

         public virtual int CompareTo(ReportData other)
         {
            return Name.CompareTo(other.Name);
         }

         public Org Org { get { return org; } }
      }

      class TotalOrg : Org
      {
         public TotalOrg()
         {
            name = "Итого";
         }

         public override string Name
         {
            get
            {
               return name;
            }
            set
            {
               base.Name = value;
            }
         }
      }

      class TotalRow : ReportData
      {
         public TotalRow() : base(new TotalOrg())
         {
         }

         public void Add(ReportData src)
         {
            for (int i = 0; i < items.Length; i++)
            {
               foreach(String key in src.items[i].Keys)
               {
                  items[i][key] = true;
               }
            }
            //for(int i=0; i<values.Length; i++)
            //{
            //   values[i] += src.values[i];
            //}
         }

         public override int CompareTo(ReportData other)
         {
            return other is TotalRow ? 0 : -1;
         }
      }

      private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         ReportData rd = dgvItems.Rows[e.RowIndex].DataBoundItem as ReportData;
         if (weeks == null || rd is TotalRow)
            return;

         FmSunnySKUDetail.Open(rd, weeks);
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         RefreshData();
      }
   }
}

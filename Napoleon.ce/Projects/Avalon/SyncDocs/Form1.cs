using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace SyncDocs
{
   public partial class Form1 : Form
   {
      enum HasMode { Agents, Orgs, Price };

      public Config config;
      public SimpleDataSet<SyncObjects> syncObjects = new SimpleDataSet<SyncObjects>(SyncObjects.OBJECT_NAME, false);

      public DataSet<string, Agent> avalonAgents = new DataSet<string, Agent>(Agent.OBJECT_NAME, false);
      public DataSet<string, Org> avalonOrgs = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      public DataSet<string, Price> avalonPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      public DataSet<string, Agent> dydoAgents = new DataSet<string, Agent>(Agent.OBJECT_NAME, false);
      public DataSet<string, Org> dydoOrgs = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, false);
      public DataSet<string, Price> dydoPrice = new DataSet<string, Price>(Price.OBJECT_NAME, false);

      public SimpleDataSet<VandAudit> dsAudit = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME, false);
      public SimpleDataSet<VandSales> dsSales = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME, false);
      public SimpleDataSet<VandReload> dsReload = new SimpleDataSet<VandReload>(VandReload.OBJECT_NAME, false);
      SimpleDataSet<VandRestock> dsRestock = new SimpleDataSet<VandRestock>(VandRestock.OBJECT_NAME, false);

      public const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy} 23:59:59')";

      public Dictionary<string, string> agentsChange = new Dictionary<string, string>();
      public Dictionary<string, string> orgsChange = new Dictionary<string, string>();
      public Dictionary<string, string> priceChange = new Dictionary<string, string>();
      Dictionary<string, string> saveChangesDic;

      ComboBox destListComboBox;
      bool saveOnlyDiff;

      public event EventHandler Refreshed;

      public Form1()
      {
         InitializeComponent();

         config = Config.Load();
         IPAvalon.Text = config.IPAvalon;
         IPDydo.Text = config.IPDydo;

         portAvalon.Text = config.portAvalon.ToString();
         portDydo.Text = config.portDydo.ToString();

         dtpStart.Value = DateTime.Now.Date;
         dtpFinish.Value = DateTime.Now.Date;

         dgvData.AutoGenerateColumns = false;

         saveOnlyDiff = true;
         tsbType.SelectedIndex = 0;
         tsbViewType.SelectedIndex = 0;

         clmnDestObject.DisplayMember = "Name";
         clmnDestObject.ValueMember = "ID";
      }

      private void LoadAvalonData()
      {
         //string dtxt = "ip1 " + config.IPDydo + ":" + config.portDydo.ToString() + "\nip2 " + config.IPAvalon + ":" + config.portAvalon.ToString();
         //MessageBox.Show(dtxt);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(syncObjects);
         
         DBConnection conn = config.AvalonConnection;
         DataModule.RefreshDataSet(avalonAgents, conn, false, null).Join();

         string priceFilter = "\"userid\" in(";
         foreach(Agent a in avalonAgents.Data)
         {
            priceFilter += "'" + a.id + "',";
         }
         avalonPrice.Filter = priceFilter.Remove(priceFilter.Length - 1) + ")";
         
         upd.Add(avalonOrgs);
         upd.Add(avalonPrice);

         //MessageBox.Show("Load Avalon");
         FmWait.StdDataRefresh(this, upd, conn, LoadDydoData);
      }

      private void LoadDydoData()
      {
         //MessageBox.Show("Load Dydo");

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dydoAgents);
         upd.Add(dydoOrgs);
         upd.Add(dydoPrice);

         dsAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dtpStart.Value, dtpFinish.Value);
         upd.Add(dsAudit);
         dsSales.Filter = dsAudit.Filter;
         upd.Add(dsSales);
         dsReload.Filter = dsAudit.Filter;
         upd.Add(dsReload);

         //dsRestock.Filter = dsAudit.Filter;
         //upd.Add(dsRestock);

         FmWait.StdDataRefresh(this, upd, config.DydoConnection, RefreshData);
      }

      private void RefreshData()
      {
         agentsChange.Clear();
         priceChange.Clear();
         orgsChange.Clear();

         foreach (SyncObjects so in syncObjects.Data)
         {
            if (so.type == SyncObjects.AGENTS)
               agentsChange[so.srcId] = so.destId;
            else if (so.type == SyncObjects.ORGS)
               orgsChange[so.srcId] = so.destId;
            else if (so.type == SyncObjects.PRICE)
               priceChange[so.srcId] = so.destId;
         }

         UpdateGrid();

         if (Refreshed != null)
            Refreshed.Invoke(this, EventArgs.Empty);
      }

      void RefreshColumnItems(IDataSet set)
      {
         List<IDNameObject> items = new List<IDNameObject>();
         foreach (IDNameObject a in set.Data)
            items.Add(a);
         clmnDestObject.Items.Clear();
         items.ForEach(x => clmnDestObject.Items.Add(x));
      }

      void ShowAgents(bool onlyDiff)
      {
         dgvData.SuspendLayout();
         dgvData.DataSource = null;
         
         List<RowData> src = new List<RowData>();
         foreach(Agent a in dydoAgents.Data)
            if(!onlyDiff || HasData(a.id, HasMode.Agents))
               src.Add(new RowData(a, FindAvalonData(a)));

         RefreshColumnItems(avalonAgents);

         dgvData.DataSource = new SortableBindingList<RowData>(src);
         dgvData.ResumeLayout();
      }

      void ShowOrgs(bool onlyDiff)
      {
         dgvData.SuspendLayout();
         dgvData.DataSource = null;

         List<RowData> src = new List<RowData>();
         foreach (Org a in dydoOrgs.Data)
            if (!onlyDiff || HasData(a.id, HasMode.Orgs))
               src.Add(new RowData(a, FindAvalonData(a)));

         RefreshColumnItems(avalonOrgs);

         dgvData.DataSource = new SortableBindingList<RowData>(src);
         dgvData.ResumeLayout();
      }

      void ShowPrice(bool onlyDiff)
      {
         dgvData.SuspendLayout();
         dgvData.DataSource = null;

         List<RowData> src = new List<RowData>();
         foreach (Price a in dydoPrice.Data)
            if (!onlyDiff || HasData(a.id, HasMode.Price))
               src.Add(new RowData(a, FindAvalonData(a)));

         RefreshColumnItems(avalonPrice);

         dgvData.DataSource = new SortableBindingList<RowData>(src);
         dgvData.ResumeLayout();
      }

      private bool HasData(string id, HasMode mode)
      {
         foreach (VandAudit doc in dsAudit.Data)
         {
            if (mode == HasMode.Agents && doc.userid == id)
               return true;
            if (mode == HasMode.Orgs && doc.id == id)
               return true;
            if(mode == HasMode.Price)
            {
               foreach (ItemBase item in doc.items)
                  if (item.id == id)
                     return true;
            }
         }

         foreach (VandSales doc in dsSales.Data)
         {
            if (mode == HasMode.Agents && doc.userid == id)
               return true;
            if (mode == HasMode.Orgs && doc.id == id)
               return true;
            if (mode == HasMode.Price)
            {
               foreach (ItemBase item in doc.items)
                  if (item.id == id)
                     return true;
            }
         }

         foreach (VandReload doc in dsReload.Data)
         {
            if (mode == HasMode.Agents && doc.userid == id)
               return true;
            if (mode == HasMode.Orgs && doc.id == id)
               return true;
            if (mode == HasMode.Price)
            {
               foreach (ItemBase item in doc.items)
                  if (item.id == id)
                     return true;
            }
         }

         foreach (VandRestock doc in dsRestock.Data)
         {
            if (mode == HasMode.Agents && doc.userid == id)
               return true;
            if (mode == HasMode.Price)
            {
               foreach (ItemBase item in doc.items)
                  if (item.id == id)
                     return true;
            }
         }

         return false;
      }

      private Org FindAvalonData(Org a)
      {
         if (!orgsChange.ContainsKey(a.id))
            return null;

         string id = orgsChange[a.id];
         return avalonOrgs.ContainsKey(id) ? avalonOrgs[id] : null;
      }

      private Price FindAvalonData(Price a)
      {
         if (!priceChange.ContainsKey(a.id))
            return null;

         string id = priceChange[a.id];
         return avalonPrice.ContainsKey(id) ? avalonPrice[id] : null;
      }

      private Agent FindAvalonData(Agent a)
      {
         if(!agentsChange.ContainsKey(a.id))
            return null;

         string id = agentsChange[a.id];
         return avalonAgents.ContainsKey(id) ? avalonAgents[id] : null;
      }

      void SaveChanges(Dictionary<string, string> chDic, bool appendData)
      {
         if (dgvData.DataSource == null || ((ICollection)dgvData.DataSource).Count == 0)
            return;

         SortableBindingList<RowData> osrc = dgvData.DataSource as SortableBindingList<RowData>;
         if (!appendData)
            chDic.Clear();

         foreach (RowData i in osrc)
            if (i.DestObject != "")
               chDic[i.SrcObject.ID] = i.DestObject;
      }

      void UpdateGrid()
      {
         SaveChanges(saveChangesDic, saveOnlyDiff);

         bool onlyDiff = tsbViewType.SelectedIndex == 1;
         saveOnlyDiff = onlyDiff;
         saveChangesDic = GetChangesDictionary();

         switch(tsbType.SelectedIndex)
         {
            case 0:
               ShowAgents(onlyDiff);
               break;
            case 1:
               ShowOrgs(onlyDiff);
               break;
            case 2:
               ShowPrice(onlyDiff);
               break;
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (portDydo.Text.Length > 0 && portAvalon.Text.Length > 0 && IPDydo.Text.Length > 0 && IPAvalon.Text.Length > 0)
         {
            int.TryParse(portDydo.Text, out config.portDydo);
            int.TryParse(portAvalon.Text, out config.portAvalon);
            config.IPAvalon = IPAvalon.Text;
            config.IPDydo = IPDydo.Text;

            config.Save();
         }

         base.OnClosing(e);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         LoadAvalonData();
      }

      private void tsbViewType_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateGrid();
      }

      private void tsbType_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateGrid();
      }

      private void dgvData_EditingControlShowing(object sender, DataGridViewEditingControlShowingEventArgs e)
      {
         if (dgvData.CurrentCell.ColumnIndex == clmnDestObject.DisplayIndex && e.Control is ComboBox)
         {
            destListComboBox = e.Control as ComboBox;
            destListComboBox.DropDownStyle = ComboBoxStyle.DropDown;
            destListComboBox.AutoCompleteMode = AutoCompleteMode.Suggest;
            destListComboBox.AutoCompleteSource = AutoCompleteSource.ListItems;

            //destListComboBox.DropDown += (o, u) => ((ComboBox)o).AutoCompleteMode = AutoCompleteMode.None;
            //destListComboBox.DropDownClosed += (o, u) => ((ComboBox)o).AutoCompleteMode = AutoCompleteMode.Suggest;
         }
      }

      public SimpleDataSet<SyncObjects> PrepareSyncObjects()
      {
         syncObjects.Clear();
         PutChangesData(syncObjects, agentsChange, SyncObjects.AGENTS);
         PutChangesData(syncObjects, orgsChange, SyncObjects.ORGS);
         PutChangesData(syncObjects, priceChange, SyncObjects.PRICE);
         return syncObjects;
      }

      private void button2_Click(object sender, EventArgs e)
      {
         if (!CheckAllAssigned())
         {
            DialogResult dr = MessageBox.Show("Внимание! В документах есть данные, которым не назначено соответсвие. Такие документы не будут принимться. Продолжить?",
               "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (dr == System.Windows.Forms.DialogResult.No)
               return;
         }

         PrepareSyncObjects();

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         if (dsAudit.Count > 0)
         {
            PrepareSet(rpl, new List<VandAudit>((IEnumerable<VandAudit>)dsAudit.Data), true);
            PrepareSet(rpl, new List<VandSales>((IEnumerable<VandSales>)dsSales.Data), true);
            PrepareSet(rpl, new List<VandReload>((IEnumerable<VandReload>)dsReload.Data), true);
         }
         //ReplacedSet rs = new ReplacedSet(syncObjects);
         //rpl.Add(rs);

         bool res = DataModule.UpdateDataSet(null, null, rpl, config.AvalonConnection);
         if( res )
         {
            MessageBox.Show("Данные сохранены");
         } else
         {
            MessageBox.Show("Ошибка при записи");
         }
      }

      Dictionary<string, string> GetChangesDictionary()
      {
         Dictionary<string, string> chDic = tsbType.SelectedIndex == 0 ? agentsChange :
            tsbType.SelectedIndex == 1 ? orgsChange :
            priceChange;

         return chDic;
      }

      private void dgvData_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == clmnDestObject.DisplayIndex)
         {
            DataGridViewRow r = dgvData.Rows[e.RowIndex];
            DataGridViewComboBoxCell c = r.Cells[e.ColumnIndex] as DataGridViewComboBoxCell;
            List<IDNameObject> src = new List<IDNameObject>();
            LoadUnusedObjects(src, (r.DataBoundItem as RowData).DestObject);

            c.Items.Clear();
            src.ForEach(x => c.Items.Add(x));
         }
      }

      private void LoadUnusedObjects(List<IDNameObject> src, string id)
      {
         Dictionary<string, string> chDic = GetChangesDictionary();
         ICollection data = tsbType.SelectedIndex == 0 ? avalonAgents.Data :
            tsbType.SelectedIndex == 1 ? avalonOrgs.Data :
            avalonPrice.Data;

         List<string> used = new List<string>();
         foreach (string v in chDic.Values)
            used.Add(v);

         foreach(DataGridViewRow r in dgvData.Rows)
         {
            RowData rd = r.DataBoundItem as RowData;
            if (used.Contains(rd.DestObject) == false)
               used.Add(rd.DestObject);
         }

         foreach(IDNameObject o in data)
         {
            if( o.ID == id )
               src.Insert(0, o);
            else if (used.Contains(o.ID) == false)
               src.Add(o);
         }

         src.Insert(0, new IDNameObject());
         src.Sort();
      }

      private void dgvData_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         e.ThrowException = false;
         (dgvData.Rows[e.RowIndex].DataBoundItem as RowData).DestObject = "";
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         dgvData.CommitEdit(DataGridViewDataErrorContexts.Commit);

         SaveChanges(saveChangesDic, saveOnlyDiff);

         syncObjects.Clear();
         PutChangesData(syncObjects, agentsChange, SyncObjects.AGENTS);
         PutChangesData(syncObjects, orgsChange, SyncObjects.ORGS);
         PutChangesData(syncObjects, priceChange, SyncObjects.PRICE);

         if (syncObjects.Count == 0)
            return;

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         ReplacedSet rs = new ReplacedSet(syncObjects);
         rpl.Add(rs);
         bool res = DataModule.UpdateDataSet(null, null, rpl, config.AvalonConnection);
         if (res)
         {
            MessageBox.Show("Данные сохранены");
         }
         else
         {
            MessageBox.Show("Ошибка при записи");
         }

      }

      private void button3_Click(object sender, EventArgs e)
      {
         FmDocuments fm = new FmDocuments();
         fm.MainForm = this;
         fm.Show();
      }
   }

   class RowData
   {
      IDNameObject src;
      String destId = "";

      public RowData(IDNameObject src, IDNameObject dest)
      {
         this.src = src;
         if (dest != null)
            this.destId = dest.id;
      }

      public IDNameObject SrcObject { get { return src; } }
      public string DestObject 
      { 
         get { return destId; }
         set { destId = value; }
      }
   }


   public class SortableBindingList<T> : BindingList<T>
   {
      private bool _isSorted;
      private ListSortDirection _sortDirection = ListSortDirection.Ascending;
      private PropertyDescriptor _sortProperty;

      public SortableBindingList(IList<T> list) : base(list) { }
      public SortableBindingList() { }
      public SortableBindingList(IList<T> list, PropertyDescriptor property, ListSortDirection direction) :
         base(list)
      {
         ApplySortCore(property, direction);
      }

      protected override void ApplySortCore(PropertyDescriptor property, ListSortDirection direction)
      {
         _sortProperty = property;
         _sortDirection = direction;

         // Get list to sort
         List<T> items = this.Items as List<T>;

         // Apply and set the sort, if items to sort
         if (items != null)
         {
            PropertyComparer<T> pc = new PropertyComparer<T>(property, direction);
            items.Sort(pc);
            _isSorted = true;
         }
         else
         {
            _isSorted = false;
         }

         // Let bound controls know they should refresh their views
         this.OnListChanged(new ListChangedEventArgs(ListChangedType.Reset, -1));
      }

      protected override bool SupportsSortingCore { get { return true; } }
      protected override bool IsSortedCore { get { return _isSorted; } }
      protected override PropertyDescriptor SortPropertyCore { get { return _sortProperty; } }
      protected override ListSortDirection SortDirectionCore { get { return _sortDirection; } }

      protected override void RemoveSortCore()
      {
         _sortDirection = ListSortDirection.Ascending;
         _sortProperty = null;
         _isSorted = false;
      }
   }

   public class PropertyComparer<T> : IComparer<T>
   {
      private PropertyDescriptor property;
      private ListSortDirection sortDirection;

      public PropertyComparer(PropertyDescriptor property, ListSortDirection sortDirection)
      {
         this.property = property;
         this.sortDirection = sortDirection;
      }

      public int Compare(T x, T y)
      {
         object valueX = property.GetValue(x);
         object valueY = property.GetValue(y);

         return (sortDirection == ListSortDirection.Ascending) ?
            Comparer.Default.Compare(valueX, valueY) :
            Comparer.Default.Compare(valueY, valueX);
      }
   }
}

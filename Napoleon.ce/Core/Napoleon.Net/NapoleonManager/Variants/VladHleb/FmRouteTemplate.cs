using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;
using System.Net;
using System.IO;
using System.Xml;
using GRSoft.Network;
using GRSoft.NapoleonManager.Maps;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;

namespace GRSoft.NapoleonManager
{

   public partial class FmRouteTemplate : Form, FmSelectContrAgent.Selected
   {
      public const int SHEDULE_WEEK_COUNT = 4;
      public const string DATE_FORMAT = "yyyy-MM-dd";
      private SimpleDataSet<RouteTemplate> dsOrgFolder;
      SimpleDataSet<RouteTemplate> dsRemoveOrgFolder;

      private DataSet<string, Org> dsOrg;
      private OrgRouteQueue orgQueue = new OrgRouteQueue();
      private DataSet<int, CommonConfig> dsConfig;

      static FmRouteTemplate instance = null;

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmRouteTemplate();
            instance.Show();
         }
         else
         {
            instance.BringToFront();
            instance.RefreshData();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }


      public FmRouteTemplate()
      {
         InitializeComponent();

         dsOrgFolder = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);
         dsRemoveOrgFolder = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);

         dsOrg = DataModule.Get(Org.COMMON_OBJECT_NAME) == null ? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME) : (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME);
         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
         
         dgvOrgs.AutoGenerateColumns = false;
         dgvRoute.AutoGenerateColumns = false;
         wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_1);

         cbSelectDay.Items.Add(new AllDays());
         for (int i = 1; i <= 7; i++)
            cbSelectDay.Items.Add(new SelDayItem(i));

         cbSelectDay.SelectedIndex = ((int)DateTime.Now.DayOfWeek == 0) ? 7 : (int)DateTime.Now.DayOfWeek;
         cbSelectDay.SelectedIndexChanged += (o, e) => { OnSelectedDayChanged(dgvRoute.CurrentRow == null ? null : dgvRoute.CurrentRow.DataBoundItem as RouteData); };

         cbWeek.SelectedIndex = 0;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
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
         if( dsOrgFolder.Count > 0)
            wr.Add(dsOrgFolder);

         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, "");
         string v = dtpRouteStart.Value.ToString(DATE_FORMAT);
         if (v != cc.value)
         {
            DataSet<int, CommonConfig> dcc = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            cc.value = v;
            dcc[0] = cc;
            wr.Add(dcc);
         }

         List<IDataSet> rmv = new List<IDataSet>();
         if (dsRemoveOrgFolder.Count > 0)
         {
            // уберем те элементы, которые есть в списек сохраняемых
            List<string> usedNames = new List<string>();
            foreach (RouteTemplate i in dsOrgFolder.Data)
               usedNames.Add(i.name);

            List<int> needRemove = new List<int>();
            foreach (KeyValuePair<int, RouteTemplate> kv in dsRemoveOrgFolder)
               if (usedNames.Contains(kv.Value.name))
                  needRemove.Add(kv.Key);

            needRemove.ForEach((x) => { dsRemoveOrgFolder.Remove(x); });
            if (dsRemoveOrgFolder.Count > 0)
               rmv.Add(dsRemoveOrgFolder);
         }

         bool ret = DataModule.UpdateDataSet(wr, rmv, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         if( ret )
            dsRemoveOrgFolder.Clear();

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsOrgFolder);
         upd.Add(dsConfig);
         FmWait.StdDataRefresh(this, upd, LoadData);
      }

      DateTime StartOfWeek
      {
         get
         {
            int dw = (int)DateTime.Today.DayOfWeek;
            int daySpan = (dw == 0) ? 6 : dw - 1;
            return DateTime.Today.Subtract(new TimeSpan(daySpan, 0, 0, 0));
         }
      }

      void LoadData()
      {
         // будем хранить начало расписания для каждого агента отдельно - мало ли что :)
         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, "");
         try
         {
            dtpRouteStart.Value = cc.value.Length == 0 ?
               StartOfWeek :
               DateTime.ParseExact(cc.value, DATE_FORMAT, null);
         }
         catch (Exception)
         {
            dtpRouteStart.Value = DateTime.Now;
         }

         SortableBindingList<RouteData> src = new SortableBindingList<RouteData>();
         foreach (RouteTemplate i in dsOrgFolder.Data)
            src.Add(new RouteData(i, this));

         dgvRoute.DataSource = src;
         OnSelectedDayChanged(src.Count == 0 ? null : dgvRoute.Rows[0].DataBoundItem as RouteData);

         tsbSave.Enabled = false;
      }


      private void MakeWeekColumnHeader()
      {
         WeekDay wd = cbSelectDay.SelectedItem as WeekDay;
         if (wd == null)
            wd = cbSelectDay.Items[1] as WeekDay;

         // найдем индекс текущей недели
         TimeSpan ts = new TimeSpan(DateTime.Today.Ticks);
         ts = ts.Subtract(new TimeSpan(dtpRouteStart.Value.Ticks));
         if (wd != null && ts.TotalDays >= 0)
         {
            int curWeekIndex = (int)(ts.TotalDays / 7) % 4;
            List<DataGridViewColumn> columns = new List<DataGridViewColumn>();

            curWeekIndex = -curWeekIndex;
            DateTime dt = GetDate(wd, curWeekIndex++);
            dgvOrgW1.Tag = dt;
            dgvOrgW1.HeaderText = dt.ToString("dd/MM/yy");

            dt = GetDate(wd, curWeekIndex++);
            dgvOrgW2.Tag = dt;
            dgvOrgW2.HeaderText = dt.ToString("dd/MM/yy");

            dt = GetDate(wd, curWeekIndex++);
            dgvOrgW3.Tag = dt;
            dgvOrgW3.HeaderText = dt.ToString("dd/MM/yy");

            dt = GetDate(wd, curWeekIndex++);
            dgvOrgW4.Tag = dt;
            dgvOrgW4.HeaderText = dt.ToString("dd/MM/yy");
         }
      }

      DateTime GetDate(WeekDay wd, int weekIndex)
      {
         int needDate = wd.Number;
         DateTime dt = DateTime.Today;
         int curDate = (int)dt.DayOfWeek;
         if (curDate == 0) curDate = 7;

         dt = dt.AddDays(needDate - curDate);
         dt = dt.AddDays(7 * weekIndex);

         return dt;
      }

      public void ItemChanged(RouteTemplate item)
      {
         tsbSave.Enabled = true;
      }

      public void ItemChanged(RouteTemplateItem item)
      {
         tsbSave.Enabled = true;
      }

      void OnSelectedDayChanged(RouteData item)
      {
         
         WeekDay cd = cbSelectDay.SelectedItem as WeekDay;
         MakeWeekColumnHeader();

         //RouteData item = dgvRoute.CurrentRow == null ? null : dgvRoute.CurrentRow.DataBoundItem as RouteData;
         if( cd == null )
         {
            Dictionary<string, AllDaysItemData> used = new Dictionary<string, AllDaysItemData>();
            if(item != null)
               foreach (RouteTemplateItem i in item.Data.items)
               {
                  if (used.ContainsKey(i.id))
                     used[i.id].Add(i);
                  else
                     used[i.id] = new AllDaysItemData(i);
               }

            List<AllDaysItemData> src = new List<AllDaysItemData>(used.Values);
            src.Sort();
            dgvOrgs.DataSource = src;
         }
         else
         {
            List<RouteItemData> src = new List<RouteItemData>();
            if(item != null)
               foreach (RouteTemplateItem i in item.Data.items)
               {
                  if (i.weekDay == cd.FullName)
                  {
                     src.Add(new RouteItemData(i, this));
                  }
               }
            src.Sort();

            dgvOrgs.DataSource = new BindingList<RouteItemData>(src);
         }
      }

      void ReorderItems()
      {
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
         if (src == null)
            return;
         int pos = 0;
         foreach (RouteItemData i in src)
            i.Data.pos = pos++;
      }

      private void btnRemove_Click(object sender, EventArgs e)
      {
         if (dgvRoute.CurrentRow == null)
            return;

         RouteData item = dgvRoute.CurrentRow.DataBoundItem as RouteData;
         SortableBindingList<RouteData> src = (SortableBindingList<RouteData>)dgvRoute.DataSource;
         src.Remove(item);
         foreach(KeyValuePair<int,RouteTemplate> kv in dsOrgFolder)
            if(kv.Value == item.Data)
            {
               dsOrgFolder.Remove(kv.Key);
               break;
            }

         dsRemoveOrgFolder.Add(item.Data);
         tsbSave.Enabled = true;
      }

      public bool IsNameExists(string name)
      {
         foreach (RouteTemplate i in dsOrgFolder.Data)
            if (i.name == name)
               return true;

         return false;
      }

      public string GetNewName()
      {
         string name = "Маршрут";
         int index = 1;

         while (IsNameExists(name + index.ToString()))
            index++;
         return name + index.ToString();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<RouteData> src = (SortableBindingList<RouteData>)dgvRoute.DataSource;
         RouteTemplate rt = new RouteTemplate();
         rt.name = GetNewName();
         src.Add(new RouteData(rt, this));
         dsOrgFolder.Add(rt);

         tsbSave.Enabled = true;
      }

      private void dgvRoute_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         OnSelectedDayChanged(dgvRoute.Rows[e.RowIndex].DataBoundItem as RouteData);
      }

      private void dtpRouteStart_ValueChanged(object sender, EventArgs e)
      {
         MakeWeekColumnHeader();
         tsbSave.Enabled = true;
      }

      void MoveOrg(bool moveUp)
      {
         if (dgvOrgs.CurrentRow == null || cbSelectDay.SelectedItem as WeekDay == null)
            return;

         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
         int index = dgvOrgs.CurrentRow.Index;
         if (moveUp && index == 0 || !moveUp && index == src.Count - 1)
            return;

         SuspendLayout();

         RouteItemData data = dgvOrgs.CurrentRow.DataBoundItem as RouteItemData;
         src.RemoveAt(index);
         if (moveUp)
            index--;
         else
            index++;
         src.Insert(index, data);
         dgvOrgs.CurrentCell = dgvOrgs.Rows[index].Cells[0];

         ReorderItems();

         ResumeLayout();
         tsbSave.Enabled = true;
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         MoveOrg(sender == tsbUp);
      }

      private void tsbDelete_Click(object sender, EventArgs e)
      {
         if (dgvOrgs.CurrentRow == null || cbSelectDay.SelectedItem as WeekDay == null)
            return;

         RouteData rd = dgvRoute.CurrentRow.DataBoundItem as RouteData;
         RouteItemData item = dgvOrgs.CurrentRow.DataBoundItem as RouteItemData;

         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;

         src.Remove(item);
         rd.Data.items.Remove(item.Data);

         tsbSave.Enabled = true;
      }

      void AddOrg(object sender, Org org)
      {
         WeekDay wd = cbSelectDay.SelectedItem as WeekDay;
         RouteData rd = dgvRoute.CurrentRow.DataBoundItem as RouteData;
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;

         RouteTemplateItem item = new RouteTemplateItem();
         item.weekDay = wd.FullName;
         item.id = org.id;
         item.org = org;
         rd.Data.items.Add(item);

         src.Add(new RouteItemData(item, this));
         ReorderItems();

         tsbSave.Enabled = true;
      }

      private void tsbAddOrg_Click(object sender, EventArgs e)
      {
         if (cbSelectDay.SelectedItem as WeekDay == null || dgvRoute.CurrentRow == null)
            return;

         FmSelectContrAgent.ShowForm(dsOrg, null, this, AddOrg, this);
      }

      public bool IsSelected(Org o)
      {
         if (dgvRoute.CurrentRow != null)
         {
            RouteData rd = dgvRoute.CurrentRow.DataBoundItem as RouteData;
            foreach (RouteTemplateItem i in rd.Data.items)
               if (i.id == o.id)
                  return true;
         }
         return false;
      }

      private void dgvOrgs_DragEnter(object sender, DragEventArgs e)
      {
         if (cbSelectDay.SelectedItem as WeekDay != null && e.Data.GetDataPresent(typeof(DragDropObject)))
            e.Effect = DragDropEffects.Copy;
         else
            e.Effect = DragDropEffects.None;
      }

      private void dgvOrgs_DragDrop(object sender, DragEventArgs e)
      {
         DragDropObject ddo = e.Data.GetData(typeof(DragDropObject)) as DragDropObject;
         if(ddo.Source is FmSelectContrAgent)
         {
            List<Org> rc = ddo.Data as List<Org>;
            int i = rc.Count-1;
            for (; i >= 0; i--)
            {
               AddOrg(this, rc[i]);
            }
         }
      }

      private void toolStripMenuItem1_Click(object sender, EventArgs e)
      {
         if (CheckChanges() == false)
            return;

         RouteData rd = dgvRoute.CurrentRow.DataBoundItem as RouteData;
         Agent a = FmSelectAgent.DoSelect();
         if( a != null )
         {
            List<IDataSet> wr = new List<IDataSet>();
            List<IDataSet> rmv = new List<IDataSet>();
            List<ReplacedSet> rpl = new List<ReplacedSet>();

            SimpleDataSet<Message> wrMsg = new SimpleDataSet<Message>(Message.OBJECT_NAME, false);
            Message m = new Message();
            m.date = DateTime.Now;
            m.message = "Внимание! Изменился маршрут";
            wrMsg.Command = new ServerCommand(Commands.Impersonate(Commands.FORCE_PUT, a.id), "");
            wrMsg.Add(m);
            ReplacedSet rs = new ReplacedSet(a.id, wrMsg);
            rs.dontRemove = true;
            rpl.Add(rs);

            SimpleDataSet<OrgFolder> wrRoute = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
            SimpleDataSet<OrgFolder> rmvRoute = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
            wr.Add(wrRoute);
            rmv.Add(rmvRoute);

            bool isComplex = rd.IsComplexRoute;

            foreach (object o in cbSelectDay.Items)
            {
               WeekDay wd = o as WeekDay;
               if (wd == null)
                  continue;

               if(isComplex)
               {
                  OrgFolder ormv = new OrgFolder();
                  ormv.agent = a;
                  ormv.name = wd.FullName;
                  rmvRoute.Add(ormv);

                  int mask = 1;
                  for (int cd = 1; cd < 4; cd++, mask >>= 1)
                  {
                     OrgFolder of = new OrgFolder();
                     of.agent = a;
                     of.name = wd.FullName;
                     rd.Data.FillItems(of, wd, mask);
                     if (of.items == null || of.items.Count == 0)
                        rmvRoute.Add(of);
                     else
                        wrRoute.Add(of);
                  }
               }
               else
               {
                  for (int cd = 1; cd < 4; cd++)
                  {
                     OrgFolder ormv = new OrgFolder();
                     ormv.agent = a;
                     ormv.name = cd.ToString() + wd.FullName;
                     rmvRoute.Add(ormv);
                  }

                  OrgFolder of = new OrgFolder();
                  of.agent = a;
                  of.name = wd.FullName;
                  rd.Data.FillItems(of, wd, RouteTemplateItem.ALL_WEEKS);
                  if (of.items == null || of.items.Count == 0)
                     rmvRoute.Add(of);
                  else
                     wrRoute.Add(of);
               }
            }

            if (DataModule.UpdateDataSet(wr, rmv, rpl, Config.GetConfig().GetConnection()))
               MessageBox.Show("Маршрут обновлен");
         }
      }

      private void tsbShowMap_Click(object sender, EventArgs e)
      {
         WeekDay wd = cbSelectDay.SelectedItem as WeekDay;

         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;

         OrgRouteQueue routeQueue = new OrgRouteQueue();

         int mask = (1 >> cbWeek.SelectedIndex);
         foreach(RouteItemData item in src)
         {
            if ((item.Data.weekMask & mask) == 0)
               continue;

            OrgFolderItem ofi = new OrgFolderItem();
            ofi.pos = item.Data.pos;
            ofi.org = item.Data.org;
            ofi.name = item.Data.id;

            OrgRouteQueueItem ri = new OrgRouteQueueItem(routeQueue, ofi.pos, ofi, wd, cbWeek.SelectedIndex + 1);
            routeQueue.Add(ri);
         }

         string txt = MapEngine.Route(Config.GetConfig().mapSource, routeQueue);
         wb.DocumentText = txt;

      }
   }

   class AllDaysItemData : IComparable<AllDaysItemData>
   {
      String daysText;
      RouteTemplateItem data;
      int mask;
      public AllDaysItemData(RouteTemplateItem data)
      {
         this.data = data;
         mask = data.weekMask;

         WeekDay wd = new WeekDay(data.weekDay);
         daysText = wd.ShortName;
      }
   
      public void Add(RouteTemplateItem addData)
      {
         mask |= addData.weekMask;
         WeekDay wd = new WeekDay(addData.weekDay);
         daysText += "," + wd.ShortName;
      }

      public string OrgName { get { return data.org.Name; } }

      public string Day { get { return daysText; } }

      public bool W1 { get { return (mask & RouteTemplateItem.WEEK1) != 0; } }
      public bool W2 { get { return (mask & RouteTemplateItem.WEEK2) != 0; } }
      public bool W3 { get { return (mask & RouteTemplateItem.WEEK3) != 0; } }
      public bool W4 { get { return (mask & RouteTemplateItem.WEEK4) != 0; } }

      public int CompareTo(AllDaysItemData other)
      {
         return data.org.Name.CompareTo(other.data.org.Name);
      }
   }

   class RouteItemData : IComparable<RouteItemData>
   {
      RouteTemplateItem data;
      FmRouteTemplate owner;

      public RouteItemData(RouteTemplateItem data, FmRouteTemplate owner)
      {
         this.data = data;
         this.owner = owner;
      }

      public string OrgName { get { return data.org.Name; } }

      public RouteTemplateItem Data { get { return data; } }

      public string Day { get { return data.weekDay; } }

      public bool W1
      {
         get { return (data.weekMask & RouteTemplateItem.WEEK1) != 0; }
         set { data.SetMask(value, RouteTemplateItem.WEEK1); owner.ItemChanged(data); }
      }

      public bool W2
      {
         get { return (data.weekMask & RouteTemplateItem.WEEK2) != 0; }
         set { data.SetMask(value, RouteTemplateItem.WEEK2); owner.ItemChanged(data); }
      }

      public bool W3
      {
         get { return (data.weekMask & RouteTemplateItem.WEEK3) != 0; }
         set { data.SetMask(value, RouteTemplateItem.WEEK3); owner.ItemChanged(data); }
      }

      public bool W4
      {
         get { return (data.weekMask & RouteTemplateItem.WEEK4) != 0; }
         set { data.SetMask(value, RouteTemplateItem.WEEK4); owner.ItemChanged(data); }
      }

      public int CompareTo(RouteItemData other)
      {
         return data.pos - other.data.pos;
      }
   }

   class RouteData
   {
      RouteTemplate data;
      FmRouteTemplate owner;

      public RouteData(RouteTemplate data, FmRouteTemplate owner)
      {
         this.data = data;
         this.owner = owner;
      }

      public bool IsComplexRoute
      {
         get
         {
            foreach (RouteTemplateItem i in data.items)
               if (i.weekMask != RouteTemplateItem.ALL_WEEKS)
                  return true;
            return false;
         }
      }

      public string Name
      {
         get { return data.name; }
         set
         {
            if (owner.IsNameExists(value) == false)
            {
               data.name = value;
               owner.ItemChanged(data);
            }
         }
      }

      public RouteTemplate Data { get { return data; } }
   }

   class SelDayItem : WeekDay
   {
      public SelDayItem(int index)
         : base(index)
      {

      }
      public override string ToString() { return FullName; }
   }

   class AllDays
   {
      public AllDays() { }
      public override string ToString() { return "<все>"; }
   }
}

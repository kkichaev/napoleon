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

   public partial class FmRouteAssign : Form, FmSelectContrAgent.Selected, IRoute
   {
      public const int SHEDULE_WEEK_COUNT = 4;
      public const string DATE_FORMAT = "yyyy-MM-dd";
      private SimpleDataSet<OrgFolder> dsOrgFolder;

      private DataSet<string, Org> dsOrg;
      private DataSet<int, CommonConfig> dsConfig;
      private SimpleDataSet<ScriptDef> scripts = new SimpleDataSet<ScriptDef>(ScriptDef.OBJECT_NAME);

      Agent currentAgent;
      SelDayItem currentDay;

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData(true);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }


      public FmRouteAssign()
      {
         InitializeComponent();

         dsOrgFolder = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);

         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
         
         dgvOrgs.AutoGenerateColumns = false;
         wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_1);

         cbSelectDay.Items.Add(new AllDays());
         for (int i = 1; i <= 7; i++)
            cbSelectDay.Items.Add(new SelDayItem(i));

         cbSelectDay.SelectedIndex = ((int)DateTime.Now.DayOfWeek == 0) ? 7 : (int)DateTime.Now.DayOfWeek;
         currentDay = (SelDayItem)cbSelectDay.SelectedItem;
         cbSelectDay.SelectedIndexChanged += (o, e) => { TryRefreshData(); };

         cbWeek.SelectedIndex = 0;
      }

      void TryRefreshData()
      {
         if( !CheckChanges() )
         {
            if (cbAgents.SelectedItem != currentAgent)
               cbAgents.SelectedItem = currentAgent;
            if (cbSelectDay.SelectedItem != currentDay)
               cbSelectDay.SelectedItem = currentDay;
            return;
         }

         bool loadOrgs = currentAgent != (Agent)cbAgents.SelectedItem;
         currentAgent = (Agent)cbAgents.SelectedItem;
         currentDay = cbSelectDay.SelectedItem as SelDayItem;
         RefreshData(loadOrgs);
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

      public void SetCurrentAgent(Agent a)
      {
         Agents list = (CurrentUser.user as Manager).GetAgents();
         foreach (Agent ia in list.Data)
            cbAgents.Items.Add(ia);
         cbAgents.SelectedItem = a;
         currentAgent = a;
         cbAgents.SelectedIndexChanged += (o, e) => { TryRefreshData(); };
      }

      private bool SaveChanges(bool showDialog)
      {
         if (currentDay == null || currentAgent == null)
            return true;

         List<IDataSet> wr = new List<IDataSet>();
         List<IDataSet> rmv = new List<IDataSet>();
         SimpleDataSet<OrgFolder> updSet = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
         SimpleDataSet<OrgFolder> rmvSet = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
         rmv.Add(rmvSet);

         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;

         //if( dsOrgFolder.Count > 0)
         //   wr.Add(dsOrgFolder);

         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, "");
         string v = dtpRouteStart.Value.ToString(DATE_FORMAT);
         if (v != cc.value)
         {
            DataSet<int, CommonConfig> dcc = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            cc.value = v;
            dcc[0] = cc;
            wr.Add(dcc);
         }

         if( src.Count == 0)
         {
            OrgFolder ormv = new OrgFolder();
            ormv.agent = currentAgent;
            ormv.userid = currentAgent.id;
            ormv.name = currentDay.FullName;
            rmvSet.Add(ormv);

            for( int i=1; i<=4; i++)
            {
               ormv = new OrgFolder();
               ormv.agent = currentAgent;
               ormv.userid = currentAgent.id;
               ormv.name = i.ToString() + currentDay.FullName;
               rmvSet.Add(ormv);
            }

         }
         else
         {
            wr.Add(updSet);

            bool isComplexRoute = IsComplexRoute;
            if (isComplexRoute)
            {
               OrgFolder ormv = new OrgFolder();
               ormv.agent = currentAgent;
               ormv.userid = currentAgent.id;
               ormv.name = currentDay.FullName;
               rmvSet.Add(ormv);

               for (int cd = 1; cd < 4; cd++)
               {
                  OrgFolder of = new OrgFolder();
                  of.agent = currentAgent;
                  of.userid = currentAgent.id;
                  of.name = cd.ToString() + currentDay.FullName;
                  updSet.Add(of);
               }
            }
            else
            {
               OrgFolder of = new OrgFolder();
               of.agent = currentAgent;
               of.userid = currentAgent.id;
               of.name = currentDay.FullName;
               updSet.Add(of);

               for (int cd = 1; cd < 4; cd++)
               {
                  OrgFolder ormv = new OrgFolder();
                  ormv.agent = currentAgent;
                  ormv.userid = currentAgent.id;
                  ormv.name = cd.ToString() + currentDay.FullName;
                  rmvSet.Add(ormv);
               }

            }
            foreach(RouteItemData rid in src)
            {
               if (!isComplexRoute)
               {
                  foreach (OrgFolder of in updSet.Data)
                  {
                     OrgFolderItem ofi = new OrgFolderItem();
                     ofi.pos = rid.pos;
                     ofi.name = rid.org.id;
                     ofi.org = rid.org;

                     of.items.Add(ofi);
                     break;
                  }
               }
               else
               {
                  int mask = 1;
                  for (int cd = 1; cd < 4; cd++, mask >>= 1)
                  {
                     if ((rid.weekMask & mask) != 0)
                     {
                        string day = cd.ToString() + currentDay.FullName;
                        foreach (OrgFolder of in updSet.Data)
                           if (of.name == day)
                           {
                              OrgFolderItem ofi = new OrgFolderItem();
                              ofi.pos = rid.pos;
                              ofi.name = rid.org.id;
                              ofi.org = rid.org;

                              of.items.Add(ofi);
                              break;
                           }
                     }
                  }
               }
            }
         }

         bool ret = DataModule.UpdateDataSet(wr, rmv, null, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void RefreshData(bool loadOrgs)
      {
         if (currentAgent == null)
            return;

         List<IDataSet> upd = new List<IDataSet>();

         if (loadOrgs)
         {
            dsOrg = (DataSet<string, Org>)DataModule.GetUserDataSet(currentAgent.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>), true);
            upd.Add(dsOrg);
         }

         upd.Add(dsOrgFolder);
         dsOrgFolder.Filter = "\"userid\"='" + currentAgent.id + "'";
         
         if( currentDay != null )
            dsOrgFolder.Filter += " and (\"name\"='" + currentDay.FullName + "' or \"name\"='?" + currentDay.FullName + "')";

         upd.Add(dsConfig);
         dsConfig.Filter = "\"userid\" is null or \"userid\" = ''";

         if (scripts.Count == 0)
         {
            upd.Add(scripts);
            scripts.Filter = "\"userid\" is null or \"userid\" = ''";
         }

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

         MakeWeekColumnHeader();

         bool selectAddDays = currentDay == null;
         Dictionary<Org, RouteItemData> items = new Dictionary<Org, RouteItemData>();
         foreach(OrgFolder of in dsOrgFolder.Data)
         {
            try
            {
               WeekDay wd = new WeekDay(of.name);

               int mask = RouteItemData.ALL_WEEK;
               int wi;
               if (int.TryParse(new String(new char[] { of.name[0] }), out wi) && wi > 0)
                  mask = 1 >> (wi - 1);

               foreach (OrgFolderItem ofi in of.items)
               {
                  if (items.ContainsKey(ofi.org))
                  {
                     RouteItemData rid = items[ofi.org];
                     if (selectAddDays)
                        rid.AddDay(wd.ShortName);
                     rid.AddMask(mask);
                  }
                  else
                     items[ofi.org] = new RouteItemData(ofi, mask, selectAddDays ? wd.ShortName : wd.FullName, this);
               }

            }
            catch (Exception)
            { }
         }


         List<RouteItemData> src = new List<RouteItemData>(items.Values);
         src.Sort();
         dgvOrgs.DataSource = new BindingList<RouteItemData>(src);
         dgvOrgs.ReadOnly = (currentDay == null);

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

      public void ItemChanged(RouteItemData item)
      { 
         tsbSave.Enabled = true;
      }
      
      void ReorderItems()
      {
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
         if (src == null)
            return;
         int pos = 0;
         foreach (RouteItemData i in src)
            i.pos = pos++;
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

         RouteItemData item = dgvOrgs.CurrentRow.DataBoundItem as RouteItemData;

         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;

         src.Remove(item);

         tsbSave.Enabled = true;
      }

      void AddOrg(object sender, Org org)
      {
         if (currentDay == null)
            return;

         WeekDay wd = cbSelectDay.SelectedItem as WeekDay;
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
         OrgFolderItem ofi = new OrgFolderItem();
         ofi.org = org;
         ofi.pos = 0;
         ofi.name = org.id;

         RouteItemData item = new RouteItemData(ofi, RouteItemData.ALL_WEEK, wd.FullName, this);
         src.Add(item);
         ReorderItems();

         tsbSave.Enabled = true;
      }

      private void tsbAddOrg_Click(object sender, EventArgs e)
      {
         if (cbSelectDay.SelectedItem as WeekDay == null)
            return;

         FmSelectContrAgent.ShowForm(dsOrg, null, this, AddOrg, this);
      }

      public bool IsSelected(Org o)
      {
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
            foreach (RouteItemData i in src)
               if (i.org == o)
                  return true;

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

      private void tsbShowMap_Click(object sender, EventArgs e)
      {
         WeekDay wd = cbSelectDay.SelectedItem as WeekDay;
         OrgRouteQueue routeQueue = MakeOrgRouteQueue(wd, cbWeek.SelectedIndex);

         string txt = MapEngine.Route(Config.GetConfig().mapSource, routeQueue);
         wb.DocumentText = txt;

      }

      private OrgRouteQueue MakeOrgRouteQueue(WeekDay wd, int selWeek)
      {
         BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
         OrgRouteQueue routeQueue = new OrgRouteQueue();

         int mask = (1 >> selWeek);
         foreach (RouteItemData item in src)
         {
            if ((item.weekMask & mask) == 0)
               continue;

            OrgFolderItem ofi = new OrgFolderItem();
            ofi.pos = item.pos;
            ofi.org = item.org;
            ofi.name = item.org.id;

            OrgRouteQueueItem ri = new OrgRouteQueueItem(routeQueue, ofi.pos, ofi, wd, selWeek + 1);
            routeQueue.Add(ri);
         }
         return routeQueue;
      }

      public bool IsComplexRoute { 
         get 
         {
            BindingList<RouteItemData> src = (BindingList<RouteItemData>)dgvOrgs.DataSource;
            foreach (RouteItemData rid in src)
               if ((rid.weekMask & RouteItemData.ALL_WEEK) != RouteItemData.ALL_WEEK)
                  return true;

            return false;
         } 
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;;
         SimpleDataSet<OrgFolder> of = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
         of.Filter = "\"userid\"='" + a.id + "'";

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), of, null).Join();

         Invoke(new EmptyParamHandler(
            delegate
            {
               OrgRouteQueue rq = new OrgRouteQueue();
               foreach(OrgFolder oflder in of.Data) 
               {
                  WeekDay wd = new WeekDay(oflder.name);
                  foreach(OrgFolderItem ofi in oflder.items)
                  {
                     int wi;
                     OrgRouteQueueItem item;
                     if(int.TryParse(oflder.name.Substring(0, 1), out wi))
                        item = new OrgRouteQueueItem(rq, ofi.pos, ofi, wd, wi);
                     else
                        item = new OrgRouteQueueItem(rq, ofi.pos, ofi, wd);
                     rq.Add(item);
                  }
               }

               RouteReport report = new RouteReport("route_report_{0}.html");
               RouteReportData data = new RouteReportData();
               data.agent = a;
               data.queue.AddRange(rq);
               report.Build(data, of);
               report.Show();
            }));
      }
   }

   public class RouteItemData : IComparable<RouteItemData>
   {
      public static readonly int ALL_WEEK = 0xF;
      public static readonly int WEEK1 = 0x1;
      public static readonly int WEEK2 = 0x2;
      public static readonly int WEEK3 = 0x4;
      public static readonly int WEEK4 = 0x8;

      public Org org;
      public int pos;
      public int weekMask;
      string day;

      FmRouteAssign owner;

      public RouteItemData(OrgFolderItem item, int mask, string day, FmRouteAssign owner)
      {
         this.org = item.org;
         this.owner = owner;
         this.weekMask = mask;
         this.pos = item.pos;
         this.day = day;
      }

      public void AddDay(string day) 
      { 
         this.day += "," + day;
      }

      public void AddMask(int mask) { weekMask |= mask; }

      public string OrgName { get { return org.Name; } }

      public string Day { get { return day; } }

      public bool W1
      {
         get { return (weekMask & WEEK1) != 0; }
         set { SetMask(value, WEEK1); owner.ItemChanged(this); }
      }

      public bool W2
      {
         get { return (weekMask & WEEK2) != 0; }
         set { SetMask(value, WEEK2); owner.ItemChanged(this); }
      }

      public bool W3
      {
         get { return (weekMask & WEEK3) != 0; }
         set { SetMask(value, WEEK3); owner.ItemChanged(this); }
      }

      public bool W4
      {
         get { return (weekMask & WEEK4) != 0; }
         set { SetMask(value, WEEK4); owner.ItemChanged(this); }
      }

      public int CompareTo(RouteItemData other)
      {
         return pos - other.pos;
      }

      public void SetMask(bool value, int week)
      {
         if (value)
            weekMask |= week;
         else
            weekMask &= (~week);
      }

      public int Pos { get { return pos + 1; } }
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

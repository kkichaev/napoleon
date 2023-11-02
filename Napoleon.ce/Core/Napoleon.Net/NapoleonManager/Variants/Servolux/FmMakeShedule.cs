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
   public partial class FmMakeShedule : Form
   {
      SimpleDataSet<ServoluxShedule> shedule;
      DataSet<string, ServoluxSheduleItem> sheduleItems;

      ManagerLogList mgrLog = new ManagerLogList();

      protected List<ServoluxSheduleItem> sheduleData;

      Color errorCellColor;
      Font boldFont, defFont;
      bool needSaveShedule = false;
      private System.Object lockThis = new System.Object();
      protected Dictionary<string, bool> usedAgents = new Dictionary<string, bool>();
      DataSet<string, AgentOrgs> agentOrgs = new DataSet<string, AgentOrgs>(AgentOrgs.OBJECT_NAME, false);

      DataSet<string, Org> orgs;
      protected bool canWrite = false;
      Boolean withoutRoute = false;

      public FmMakeShedule()
      {
         InitializeComponent();

         shedule = new SimpleDataSet<ServoluxShedule>(ServoluxShedule.OBJECT_NAME, false);
         sheduleItems = new DataSet<string, ServoluxSheduleItem>(ServoluxSheduleItem.OBJECT_NAME, false);

         shedule.Filter = SheduleFilter();
         sheduleItems.Filter = SheduleFilter();

         errorCellColor = Color.LightPink;
         orgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org> ??
            new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, true);

         dgvItems.AutoGenerateColumns = false;
         ServoluxSheduleItem.Modifed += ServoluxSheduleItem_Modifed;

         List<IntStringData> dditems = new List<IntStringData>(RouteValues());

         DataGridViewComboBoxColumn[] clmns = new DataGridViewComboBoxColumn[] { clmnMonday, clmnTue, clmnWed, clmnThursday, clmnFriday, clmnSaturday, clmnSunday };
         foreach(DataGridViewComboBoxColumn cc in clmns)
         {
            cc.DataSource = dditems;
            cc.ValueMember = "ID";
            cc.DisplayMember = "Name";
         }

         List<IntStringData> cicleItems = new List<IntStringData>(CicleValues());
         clmnCicle.DataSource = cicleItems;
         clmnCicle.ValueMember = "ID";
         clmnCicle.DisplayMember = "Name";


         defFont = clmnOrg.DefaultCellStyle.Font;
         if (defFont == null)
            defFont = dgvItems.Font;
         boldFont = new Font(defFont, FontStyle.Bold);

         AdjustForm();

         Manager m = CurrentUser.user as Manager;
         canWrite = m.HaveRight(RightTokens.Get("CanChangeRoute"), RightActions.Write);
      }

      protected virtual void AdjustForm()
      {
         clmnMRCode.Visible = false;
         clmnMrAdd.Visible = false;
         foreach (ToolStripItem sti in new ToolStripItem[] { tsMerch, tsbMerch, btnMerchClear, tsMerchAdd, tsbMerchAdd, btnMerchAddClear })
         {
            sti.Visible = false;
         }

      }

      protected virtual IntStringData[] CicleValues()
      {
         return new IntStringData[] {
            new IntStringData(0, ""),
            new IntStringData(1, "Нечет."),
            new IntStringData(2, "Чет."),
         };
      }

      protected virtual IntStringData[] RouteValues()
      {
         return new IntStringData[] {
            new IntStringData(0, ""),
            new IntStringData(1, "В"),
            new IntStringData(2, "ВМ"),
            //new IntStringData(3, "М"),
            new IntStringData(4, "Д"),
            //new IntStringData(5, "ДМ"),
            //new IntStringData(6, "m"),
            new IntStringData(7, "ВД"),
            new IntStringData(8, "З"),
            new IntStringData(9, "ВЗ"),
         };
      }

      protected virtual string SheduleFilter()
      {
         return "\"created\" = (select max(\"created\") from \"ServoluxShedule\" where \"routeType\" is null or \"routeType\" = '" + ServoluxShedule.AGENT_ROUTE_TYPE + "')";
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (!CheckChanges())
            e.Cancel = true;
         base.OnClosing(e);
      }

      protected override void OnClosed(EventArgs e)
      {
         ServoluxSheduleItem.Modifed -= ServoluxSheduleItem_Modifed;
         base.OnClosed(e);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         if(orgs.Count == 0)
            upd.Add(orgs);

         string where = "\"userid\" in (";
         Manager dm = CurrentUser.user as Manager;
         foreach (Agent a in dm.GetAgents().Data)
            where += "'" + a.id + "',";
         where = where.Remove(where.Length - 1) + ")";
         //agentOrgs.Filter = where;
         upd.Add(agentOrgs);

         Agents ag = Agents.GetDataSet();
         if (ag.Count == 0)
            upd.Add(ag);

         upd.Add(shedule);
         upd.Add(sheduleItems);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      protected virtual bool TestAgent(Agent a)
      {
         return a.isMerch == 0;
      }

      protected virtual string RouteType()
      {
         return ServoluxShedule.AGENT_ROUTE_TYPE;
      }

      void DoLoadData()
      {
         List<string> agents = new List<string>();
         List<string> mgr = new List<string>();
         List<string> dsp = new List<string>();
         Manager dm = CurrentUser.user as Manager;


         agents.Add("");
         mgr.Add("");
         dsp.Add("");

         usedAgents.Clear();
         foreach (Agent a in dm.GetAgents().Data)
         {
            if (!TestAgent(a))
               continue;

            usedAgents[a.id] = true;

            if (a.isDsp != 0)
               dsp.Add(a.id);
            else if (a.isMerch != 0)
               mgr.Add(a.id);
            else
               agents.Add(a.id);
         }

         //clmnAgentAdd.DataSource = agents;
         //clmnMrAdd.DataSource = mgr;
         clmnMRCode.DataSource = mgr;
         clmnDCode.DataSource = dsp;

         DateTime created = DateTime.Now;
         if(shedule.Count == 0)
         {
            needSaveShedule = true;
            ServoluxShedule doc = new ServoluxShedule();
            doc.created = created;
            doc.routeType = RouteType();
            shedule.Add(doc);
         }
         else
         {
            foreach(ServoluxShedule doc in shedule.Data)
            {
               created = doc.created;
               if (doc.routeType == "")
               {
                  doc.routeType = RouteType();
                  needSaveShedule = true;
               }
               break;
            }
         }

         Dictionary<int, bool> used = new Dictionary<int, bool>();
         foreach (IntStringData id in RouteValues())
            used[id.ID] = true;

         sheduleData = new List<ServoluxSheduleItem>();
         foreach(Org o in orgs.Data)
         {
            if (agentOrgs.ContainsKey(o.id) == false)
               continue;
            o.userid = agentOrgs[o.id].userid;
            if (sheduleItems.ContainsKey(o.id))
            {
               ServoluxSheduleItem shi = sheduleItems[o.id];
               shi.CheckValues(used);
               shi.org = o;
               sheduleData.Add(shi);
               continue;
            }

            ServoluxSheduleItem i = new ServoluxSheduleItem();
            i.org = o;
            i.id = o.id;
            i.created = created;
            sheduleData.Add(i);
         }

         dgvItems.DataSource = new SortableBindingList<ServoluxSheduleItem>(sheduleData);
      }

      void ServoluxSheduleItem_Modifed(object sender, ModifySheduleEventArgs e)
      {
         if (canWrite)
         {
            ServoluxSheduleItem i = sender as ServoluxSheduleItem;
            mgrLog.PutLog(i.Agent, i.org.id, "", "Shedule", e.newValue, e.oldValue);
            btnSave.Enabled = true;
         }
      }

      private void dgvItems_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {

      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         dgvItems.InvalidateRow(dgvItems.CurrentRow.Index);
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         ServoluxSheduleItem row = dgvItems.Rows[e.RowIndex].DataBoundItem as ServoluxSheduleItem;
         if (row == null)
            return;

         Color backColor = dgvItems.DefaultCellStyle.BackColor;
         DataGridViewColumn clmn = dgvItems.Columns[e.ColumnIndex];
         if(clmn == clmnOrg || clmn == clmnAddress)
         {
            e.CellStyle.Font = (row.dirty) ? boldFont : defFont;
         } else if(clmn.DataPropertyName == "DCode")
         {
            if (row.DispError)
               backColor = errorCellColor;
            e.CellStyle.BackColor = backColor;
         } else if(clmn.DataPropertyName == "MRCode")
         {
            if(row.MerchError)
               backColor = errorCellColor;
            e.CellStyle.BackColor = backColor;
         }
         else if (clmn.DataPropertyName == "Cicle")
         {
            if (row.CicleError)
               backColor = errorCellColor;
            e.CellStyle.BackColor = backColor;
         }
         //else if (clmn.DataPropertyName == "MRAdd")
         //{
         //   if (row.MerchAddError)
         //      backColor = errorCellColor;
         //   e.CellStyle.BackColor = backColor;
         //}
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
         SortableBindingList<ServoluxSheduleItem> src = (SortableBindingList<ServoluxSheduleItem>)dgvItems.DataSource;
         foreach (ServoluxSheduleItem i in src)
         {
            if (i.HaveError)
            {
               dgvItems.CurrentCell = dgvItems.Rows[src.IndexOf(i)].Cells[0];
               MessageBox.Show("Не могу сохранить данные - ошибка в строке", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
               return false;
            }
         }

         SimpleDataSet<ServoluxSheduleItem> wr = new SimpleDataSet<ServoluxSheduleItem>(ServoluxSheduleItem.OBJECT_NAME);
         foreach (ServoluxSheduleItem i in src)
            if (i.dirty)
               wr.Add(i);

         if (wr.Count == 0)
            return true;

         List<IDataSet> wrs = new List<IDataSet>();
         wrs.Add(wr);

         if (needSaveShedule)
            wrs.Add(shedule);
         if (mgrLog.Count > 0)
            wrs.Add(mgrLog);

         bool ret = DataModule.UpdateDataSet(wrs, null, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            foreach (ServoluxSheduleItem i in wr.Data)
               i.dirty = false;
            needSaveShedule = false;
         }

         if (showDialog)
         {
            dgvItems.Refresh();
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }

         return ret;
      }

      //protected List<OrgFolder> GetAgentRoutes(SimpleDataSet<OrgFolder> curRoutes, string agent, bool merchRoute)
      //{
      //   List<OrgFolder> agentRoute = new List<OrgFolder>();
      //   foreach (OrgFolder of in curRoutes.Data)
      //   {
      //      if (of.userid == agent)
      //      {
      //         List<OrgFolderItem> needRemove = new List<OrgFolderItem>();
      //         foreach (OrgFolderItem ofi in of.items)
      //         {
      //            if ((merchRoute && ofi.kind == "М") || (!merchRoute && ofi.kind != "М"))
      //            {
      //               needRemove.Add(ofi);
      //            }
      //         }
      //         needRemove.ForEach(x => of.items.Remove(x));
      //         agentRoute.Add(of);
      //      }
      //   }

      //   return agentRoute;
      //}

      protected virtual Dictionary<string, List<OrgFolder>> PrepareRoutes(List<IDataSet> wrs, List<ReplacedSet> rpl)
      {
         //SimpleDataSet<OrgFolder> curRoutes = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
         //curRoutes.Filter = "not \"userid\" is null";
         //DataModule.RefreshDataSet(curRoutes, Config.GetConfig().GetConnection(), false, null).Join();

         Agents agents = Agents.GetDataSet();

         Dictionary<string, List<OrgFolder>> data = new Dictionary<string, List<OrgFolder>>();
         foreach (ServoluxSheduleItem si in sheduleData)
         {
            Dictionary<string, AgentRouteData> idata = si.GetUserDayList();
            foreach (KeyValuePair<string, AgentRouteData> kv in idata)
            {
               if (usedAgents.ContainsKey(kv.Key) == false)
                  continue;

               if (data.ContainsKey(kv.Key) == false)
                  data.Add(kv.Key, new List<OrgFolder>());

               foreach (RouteDayData routeData in kv.Value)
               {
                  bool added = false;
                  foreach (OrgFolder of in data[kv.Key])
                  {
                     if (of.name == routeData.day)
                     {
                        added = true;
                        AddFolderItem(si, of, routeData.routeLetter);
                        break;
                     }
                  }
                  if (!added)
                  {
                     OrgFolder newf = new OrgFolder();
                     newf.name = routeData.day;
                     newf.userid = kv.Key;
                     if (agents.ContainsKey(kv.Key))
                        newf.agent = agents[kv.Key];
                     AddFolderItem(si, newf, routeData.routeLetter);

                     data[kv.Key].Add(newf);
                  }
               }
            }
         }
         return data;
      }

      private void tsbExport_Click(object sender, EventArgs e)
      {
         if (!canWrite || !CheckChanges())
            return;


         Cursor.Current = Cursors.WaitCursor;

         List<IDataSet> wrs = new List<IDataSet>();
         List<ReplacedSet> rpl = new List<ReplacedSet>();

         Dictionary<string, List<OrgFolder>> data = PrepareRoutes(wrs, rpl);

         string startSheduler = (new DateTime(DateTime.Now.Year, 1, 1)).ToString(Route.DATE_FORMAT);

         SimpleDataSet<CommonConfig> cfg = new SimpleDataSet<CommonConfig>(CommonConfig.OBJECT_NAME, false);

         foreach(KeyValuePair<string, List<OrgFolder>> kv in data)
         {
            SimpleDataSet<OrgFolder> wr = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME);
            foreach(OrgFolder of in kv.Value)
               wr.Add(of);
            ReplacedSet r = new ReplacedSet(kv.Key, wr);
            rpl.Add(r);
            CommonConfig cc = new CommonConfig();
            cc.userid = kv.Key;
            cc.key = ConfigKeyItems.SHEDULE_START.Key;
            cc.value = startSheduler;

            cfg.Add(cc);
         }

         if(cfg.Count > 0)
         {
            wrs.Add(cfg);
         }
         bool res = DataModule.UpdateDataSet(wrs, null, rpl, Config.GetConfig().GetConnection());

         Cursor.Current = Cursors.Default;

         if (res)
            MessageBox.Show("Маршруты созданы");
         else
            MessageBox.Show("Ошибка при записи маршрутов");
      }

      protected static void AddFolderItem(ServoluxSheduleItem si, OrgFolder of, string letter)
      {
         //foreach(OrgFolderItem cfi in of.items)
         //{
         //   if(cfi.name == si.id)
         //   {
         //      if (cfi.kind == "М")
         //         cfi.kind = letter;
         //      return;
         //   }
         //}

         OrgFolderItem ofi = new OrgFolderItem();
         ofi.pos = of.items.Count;
         ofi.name = si.id;
         ofi.org = si.org;
         ofi.kind = letter;
         of.items.Add(ofi);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if(CheckChanges())
            LoadData();
      }

      void TextChange(object sender, EventArgs e)
      {
         timer1.Stop();
         timer1.Start();
      }

      void DoClear(object sender, EventArgs e)
      {
         if (sender == btnAgentClear)
            tsbAgent.Text = "";
         else if (sender == btnAgentAddClear)
            tsbAgentAdd.Text = "";
         else if (sender == btnAddressClear)
            tsbAddress.Text = "";
         else if (sender == btnDispClear)
            tsbDisp.Text = "";
         else if (sender == btnMerchAddClear)
            tsbMerchAdd.Text = "";
         else if (sender == btnMerchClear)
            tsbMerch.Text = "";
      }

      private void timer1_Tick(object sender, EventArgs e)
      {
         DoSearch();
      }

      bool SearchAddress(ServoluxSheduleItem i, string str)
      {
         if (str.Length == 0)
            return true;

         string name = i.Name.ToUpper();
         string adr = i.Address.ToUpper();

         string[] val = str.Split(new char[] { ' ' });
         foreach(string v in val)
         {
            if (name.Contains(v) == false && adr.Contains(v) == false)
               return false;
         }

         return true;
      }

      bool InArray(string checkStr, string[] arr)
      {
         if (arr.Length == 0 || arr.Length == 1 && arr[0].Length == 0)
            return true;

         foreach (string val in arr)
            if (checkStr.Contains(val))
               return true;

         return false;
      }

      string[] Split(string text)
      {
         List<string> values = new List<string>();
         foreach(string v in text.ToUpper().Split(new char[] {','}))
         {
            values.Add(v.Trim());
         }

         return values.ToArray();
      }

      void DoSearch()
      {
         lock (lockThis)
         {
            string addr = tsbAddress.Text.ToUpper();
            string[] agent = Split(tsbAgent.Text);
            string[] agentadd = Split(tsbAgentAdd.Text);
            string[] disp = Split(tsbDisp.Text);
            string[] merch = Split(tsbMerch.Text);
            string[] merchAdd = Split(tsbMerchAdd.Text);

            timer1.Stop();
            dgvItems.SuspendLayout();

            List<ServoluxSheduleItem> src = new List<ServoluxSheduleItem>();
            foreach (ServoluxSheduleItem i in sheduleData)
            {
               if (withoutRoute && !i.NotInRoute)
                  continue;

               if (!SearchAddress(i, addr))
                  continue;
               if (!InArray(i.Agent.ToUpper(),agent))
                  continue;
               //if (!InArray(i.AgentAdd.ToUpper(),agentadd))
               //   continue;
               if (!InArray(i.DCode.ToUpper(),disp))
                  continue;
               if (!InArray(i.MRCode.ToUpper(),merch))
                  continue;
               //if (!InArray(i.MRAdd.ToUpper(),merchAdd))
               //   continue;

               src.Add(i);
            }

            dgvItems.DataSource = new SortableBindingList<ServoluxSheduleItem>(src);
            dgvItems.ResumeLayout();
         }
      }

      private void tsbNoRoute_Click(object sender, EventArgs e)
      {
         withoutRoute = !withoutRoute;
         tsbNoRoute.Text = withoutRoute ? "Все" : "Без маршрута";
         DoSearch();
      }
   }

   public class IntStringData
   {
      int id;
      string name;

      public IntStringData(int id, string name)
      {
         this.id = id;
         this.name = name;
      }

      public int ID { get { return id; } }
      public string Name { get { return name; } }
   }
}

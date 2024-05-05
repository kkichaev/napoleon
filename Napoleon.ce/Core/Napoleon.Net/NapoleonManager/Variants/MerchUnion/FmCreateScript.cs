using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Net;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmCreateScript : Form
   {
      private ScriptDoc document = new ScriptDoc();
      protected DataSet<int, ScriptDef> dsScriptDef = (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME);
      private Dictionary<String, ScriptDocItem> items = new Dictionary<string, ScriptDocItem>();
      protected DataSet<string, Org> dsOrg;
      DataSet<int, OrgFolder> dsOrgFolder;
      private Dictionary<string, DocControl> docHash = new Dictionary<string, DocControl>();
      protected OrgLocations dsOrgLocation = OrgLocations.GetDataSet();

      public FmCreateScript(ScriptDoc script)
      {
         InitializeComponent();
         this.document = script;

         foreach (ScriptDocItem i in document.items)
            if (i.itemID.Length > 0 && i.Document != null)
               items[i.itemID] = i;
      }

      private void FmCreateScript_Load(object sender, EventArgs e)
      {
         if (dsScriptDef.ContainsKey(document.scriptId))
         {
            ScriptDef def = dsScriptDef[document.scriptId];

            foreach (ScriptDefItem i in def.items)
            {
               if (!items.ContainsKey(i.id))
                  continue;

               if (i.curType.Equals("Visit"))
               {
                  VisitControl control = new VisitControl((items[i.id].Document as Visit).Clone());
                  listView.Items.Add(i.Name).Tag = control;

                  docHash[i.id] = control;
               }
               else if (i.curType.Equals("Answer"))
               {
                  AnswerControl control = new AnswerControl((items[i.id].Document as Answer).Clone());
                  listView.Items.Add(i.Name).Tag = control;

                  docHash[i.id] = control;
               }
               else if (i.curType.Equals("OrgRemnants"))
               {
                  RemnantsControl control = new RemnantsControl((items[i.id].Document as OrgRemnants).Clone());
                  listView.Items.Add(i.Name).Tag = control;

                  docHash[i.id] = control;
               }
            }
         }

         Agent selAgent = null;
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
               if (document.userid == da.agent.id)
                  selAgent = da.agent;
            }

            al.Sort();
            al.ForEach(x => cbAgents.Items.Add(x));
         }

         cbAgents.SelectedItem = selAgent;
      }

      private void listView_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (((ListView)sender).SelectedItems.Count > 0)
         {
            UserControl c = ((ListView)sender).SelectedItems[0].Tag as UserControl;
            splitContainer1.Panel2.Controls.Clear();
            c.Dock = DockStyle.Fill;
            splitContainer1.Panel2.Controls.Add(c);
         }
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>), true) as DataSet<string, Org>;
            dsOrgFolder = DataModule.GetUserDataSet(a.id, OrgFolder.OBJECT_NAME, typeof(DataSet<int, OrgFolder>), true) as DataSet<int, OrgFolder>;

            if (dsOrgFolder.Count == 0)
            {
               List<IDataSet> upd = new List<IDataSet>();
               upd.Add(dsOrg);
               upd.Add(dsOrgFolder);

               FmWait.StdDataRefresh(this, upd, LoadOrgs);
            }
            else
            {
               LoadOrgs();
            }
         }
      }

      private void LoadOrgs()
      {
         cbOrg.BeginUpdate();

         cbOrg.Items.Clear();
         Dictionary<string, Org> dict = new Dictionary<string, Org>();

         foreach (OrgFolder of in dsOrgFolder.Values)
         {
            foreach(OrgFolderItem ofi in of.items)
            {
               if (ofi.org != null)
                  dict[ofi.name] = ofi.org;
            }
            
         }

         List<Org> list = new List<Org>();

         foreach (Org o in dict.Values)
            list.Add(o);

         list.Sort((x, y) => x.Name.CompareTo(y.Name));
         list.ForEach((o)=>cbOrg.Items.Add(o));

         Org sel = null;

         for (int i = 0; i < cbOrg.Items.Count - 1; i++)
         {
            if (((Org)cbOrg.Items[i]).id.Equals(document.id))
            {
               sel = (Org)cbOrg.Items[i];
               break;
            }
         }

         cbOrg.Text = "";
         cbOrg.SelectedItem = sel;

         cbOrg.EndUpdate();
      }

      TimeSpan ScriptSpan(ScriptDoc d)
      {
         DateTime dt = d.created.AddMinutes(1);
         foreach(ScriptDocItem sdi in d.items)
         {
            if (!sdi.Inited) continue;
            if (dt < sdi.date)
               dt = sdi.date;
         }
         return dt - d.created;
      }

      const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy HH:mm:00}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy HH:mm:00}') and \"userid\"='{3}'";

      bool IntersectsTime(ScriptDoc d, DateTime st, DateTime end)
      {
         DateTime d1 = d.created;
         DateTime d2 = d1 + ScriptSpan(d);

         if (end < d1 || st > d2) return false;
         return true;
      }

      ScriptDoc HasIntersects(DateTime dt, string userid)
      {
         SimpleDataSet<ScriptDoc> docs = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
         docs.Filter = string.Format(COMMON_FILTER_STR, "created", dt.Date, dt.Date.AddDays(1), userid);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(docs);

         Config cfg = Config.GetConfig();
         DataModule.RefreshGiveSets(cfg.GetConnection(), upd, null).Join();

         TimeSpan cdSpan = ScriptSpan(document);
         DateTime stDate = dt;
         DateTime enDate = dt + cdSpan;

         foreach(ScriptDoc sd in docs.Data)
         {
            if (IntersectsTime(sd, stDate, enDate))
               return sd;
         }

         return null;
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         if (((Org)cbOrg.SelectedItem) == null)
         {
            MessageBox.Show("Выберите организацию!");
            return;
         }

         string userid = ((Agent)cbAgents.SelectedItem).id;
         ScriptDoc sd = HasIntersects(dateTimePicker1.Value, userid);
         if(sd != null)
         {
            string msg = string.Format("Время пересекается со сценарием {0:dd/MM/yyyy HH:mm}", sd.created);
            MessageBox.Show(msg);
            return;
         }

         string old_userid = string.Empty;

         DataSet<int, ScriptDoc> script = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
         DataSet<int, Visit> visits = new DataSet<int, Visit>("CopyVisit", false);
         DataSet<int, Answer> answers = new DataSet<int, Answer>(Answer.OBJECT_NAME, false);
         DataSet<int, OrgRemnants> remnants = new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME, false);

         DataSet<int, ScriptDoc> upd_script = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
         DataSet<int, Visit> upd_visits = new DataSet<int, Visit>("CopyVisit", false);
         DataSet<int, Visit> upd_old_visits = new DataSet<int, Visit>("CopyVisit", false);
         DataSet<int, Answer> upd_answers = new DataSet<int, Answer>(Answer.OBJECT_NAME, false);
         DataSet<int, OrgRemnants> upd_remnants = new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME, false);
         DataSet<int, VisitItemDoc> addItems = new DataSet<int, VisitItemDoc>(VisitItemDoc.OBJECT_NAME, false);

         string fltr = String.Format(COMMON_FILTER_STR, "created", dateTimePicker1.Value, dateTimePicker1.Value.AddMinutes(2), userid);
         string vfltr = String.Format(COMMON_FILTER_STR, "date", dateTimePicker1.Value, dateTimePicker1.Value.AddMinutes(2), userid);

         script.Filter = fltr;
         visits.Filter = vfltr;
         answers.Filter = fltr;
         remnants.Filter = fltr;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(script);
         upd.Add(visits);
         upd.Add(answers);
         upd.Add(remnants);

         Config cfg = Config.GetConfig();
         DataModule.RefreshGiveSets(cfg.GetConnection(), upd, null).Join();

         DateTime created = GetFreeTime(dateTimePicker1.Value, script.ValueList);

         Random rnd = new Random();
         TimeSpan ts = document.sended.CompareTo(document.created) > 0 ? document.sended.Subtract(document.created) : new TimeSpan((long)rnd.Next(15 * 60) * 10000000);

         TimeSpan crSpan = created.Subtract(document.created);

         document.created = created;
         document.date = created;
         document.userid = userid;
         document.id = ((Org)cbOrg.SelectedItem).id;
         document.sended = created.Add(ts);
         document.fake = 1;

         OrgLocation loc = dsOrgLocation.GetLocation(document.id);
         if (loc == null)
            loc = new OrgLocation();

         document.latitude = loc.latitude;
         document.longitude = loc.longitude;

         for (int i = 0; i < document.items.Count; i++)
         {
            //created = created.AddSeconds(1);
            created = document.items[i].date + crSpan;

            ScriptDocItem item = document.items[i];

            if (item.state != ScriptDocItem.DOC_INITED || item.Document == null)
               continue;

            if (item.type.Equals("Visit"))
            {
               if (docHash.ContainsKey(item.itemID))
               {
                  created = GetFreeTime(created, visits.ValueList);

                  Visit old = (Visit)item.Document;
                  if (old.sended.CompareTo(old.created) > 0)
                     ts = old.sended.Subtract(old.created);
                  else
                     ts = new TimeSpan((long)rnd.Next(15 * 60) * 10000000);

                  VisitControl vc = (VisitControl)docHash[item.itemID];
                  item.Document = vc.UpdateDoc();
                  item.date = created;

                  vc.addItems.ForEach((c) => {
                     c.__date = created;
                     addItems.Add(addItems.Count, c);
                     });
                  ((Visit)item.Document).id = document.id;
                  ((Visit)item.Document).userid = document.userid;

                  ((Visit)item.Document).date = created;
                  ((Visit)item.Document).created = ((Visit)item.Document).date;
                  ((Visit)item.Document).sended = created.Add(ts);
                  ((Visit)item.Document).latitude = loc.latitude;
                  ((Visit)item.Document).longitude = loc.longitude;

                  List<string> hash = new List<string>();

                  foreach (GRSoft.NapoleonManager.Visit.VisitItem ii in (item.Document as Visit).items)
                     hash.Add(ii.name);

                  List<GRSoft.NapoleonManager.Visit.VisitItem> items = new List<Visit.VisitItem>();

                  foreach (GRSoft.NapoleonManager.Visit.VisitItem ii in old.items)
                  {
                     if (!hash.Contains(ii.name))
                        items.Add(ii);
                  }

                  old.items = items;

                  upd_visits.Add(upd_visits.Count, document.items[i].Document);

                  if (old_userid.Length == 0)
                     old_userid = old.userid;

                  upd_old_visits.Add(upd_old_visits.Count, old);
               }
            }
            else if (item.type.Equals("Answer"))
            {
               created = GetFreeTime(created, answers.ValueList);
               item.Document = docHash[item.itemID].UpdateDoc();

               Answer srcDoc = (Answer)item.Document;
               if (srcDoc.sended.CompareTo(srcDoc.created) > 0)
                  ts = srcDoc.sended.Subtract(srcDoc.created);
               else
                  ts = new TimeSpan((long)rnd.Next(15 * 60) * 10000000);

               item.date = created;
               srcDoc.id = document.id;
               srcDoc.userid = document.userid;
               srcDoc.created = created;
               srcDoc.sended = created.Add(ts);
               srcDoc.latitude = loc.latitude;
               srcDoc.longitude = loc.longitude;

               upd_answers.Add(upd_answers.Count, item.Document);
            }
            else if (item.type.Equals("OrgRemnants"))
            {
               created = GetFreeTime(created, remnants.ValueList);
               item.Document = docHash[item.itemID].UpdateDoc();

               OrgRemnants srcDoc = (OrgRemnants)item.Document;
               if (srcDoc.sended.CompareTo(srcDoc.created) > 0)
                  ts = srcDoc.sended.Subtract(srcDoc.created);
               else
                  ts = new TimeSpan((long)rnd.Next(15 * 60) * 10000000);

               item.date = created;
               srcDoc.id = document.id;
               srcDoc.userid = document.userid;
               srcDoc.created = created;
               srcDoc.sended = created.Add(ts);
               srcDoc.date = created;
               srcDoc.latitude = loc.latitude;
               srcDoc.longitude = loc.longitude;

               upd_remnants.Add(upd_remnants.Count, item.Document);
            }
         }

         upd_script.Add(upd_script.Count, document);

         List<IDataSet> update = new List<IDataSet>();

         update.Add(upd_script);
         update.Add(upd_visits);
         update.Add(upd_answers);
         update.Add(upd_remnants);

         List<IDataSet> update_old = new List<IDataSet>();

         if (upd_old_visits.Count > 0)
            update_old.Add(upd_old_visits);

         bool res = DataModule.PutNoExec(update, cfg.GetConnection()); //DataModule.UpdateDataSet(update, null, null, cfg.GetConnection(), document.userid);

         if (!res)
         {
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
            return;
         }

         if (addItems.Count > 0)
         {
            List<IDataSet> wr = new List<IDataSet>();
            wr.Add(addItems);
            res = DataModule.UpdateDataSet(wr, null, null, cfg.GetConnection(), userid);
         }

         if (!res)
         {
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
            return;
         }

         if (res && (update_old.Count > 0))
            res = DataModule.PutNoExec(update_old, cfg.GetConnection()); //DataModule.UpdateDataSet(update_old, null, null, cfg.GetConnection(), old_userid);

         if (res) 
            if (MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
               MessageBoxIcon.Information) == System.Windows.Forms.DialogResult.OK)
               Close();
            else
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
      }

      private DateTime GetFreeTime(DateTime dateTime, IList ds)
      {
         
         DateTime res = dateTime;

         if (ds.Count > 0)
         {
            BaseDocument d = ds[0] as BaseDocument;

            if (d != null)
               res = d.created.AddSeconds(1);
         }

         return res;
      }
   }
}

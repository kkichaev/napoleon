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

      string visitFilter = "";
      SimpleDataSet<Visit> visData = new SimpleDataSet<Visit>("Visit", false);
      Dictionary<DateTime, Visit> visits = new Dictionary<DateTime, Visit>();
      public FmCreateScript(ScriptDoc script)
      {
         InitializeComponent();
         this.document = script;

         foreach (ScriptDocItem i in document.items)
         {
            if (i.type == "Visit" && i.Inited)
            {
               if(visitFilter.Length == 0)
               {
                  visitFilter = "userid = '" + document.userid + "' and created in (";
               }
               else
               {
                  visitFilter += ",";
               }
               visitFilter += String.Format("ToDate('{0:dd/MM/yyyy HH:mm:ss}')", i.date);
               items[i.itemID] = i;
               continue;
            }
            if (i.itemID.Length > 0 && i.Document != null)
               items[i.itemID] = i;
         }
      }

      void LoadVisits()
      {
         if (visitFilter.Length == 0)
            return;

         visData.Filter = visitFilter + ")";

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(visData);
         FmWait.StdDataRefresh(this, upd, RefreshVisits);
      }

      void RefreshVisits()
      {
         foreach(Visit v in visData.Data)
         {
            visits.Add(v.created, v);
         }

         ScriptDef def = dsScriptDef[document.scriptId];

         foreach (ScriptDefItem i in def.items)
         {
            if (!items.ContainsKey(i.id))
               continue;

            if (i.curType.Equals("Visit"))
            {
               Visit v;
               if (visits.TryGetValue(items[i.id].date, out v))
               {
                  VisitControl control = new VisitControl(v.Clone());
                  listView.Items.Add(i.Name).Tag = control;

                  docHash[i.id] = control;
               }
            }
            else if (i.curType.Equals("Answer"))
            {
               AnswerControl control = new AnswerControl((items[i.id].Document as Answer).Clone());
               listView.Items.Add(i.Name).Tag = control;

               docHash[i.id] = control;
            }
            else if (i.curType.Equals("Contract"))
            {
               ContractControl control = new ContractControl((items[i.id].Document as Contract).Clone());
               listView.Items.Add(i.Name).Tag = control;

               docHash[i.id] = control;
            }
            else if (i.curType.Equals("Distrib"))
            {
               RemnantsControl control = new RemnantsControl((items[i.id].Document as Distrib).Clone());
               listView.Items.Add(i.Name).Tag = control;

               docHash[i.id] = control;
            }
         }
      }

      private void FmCreateScript_Load(object sender, EventArgs e)
      {
         if (dsScriptDef.ContainsKey(document.scriptId))
         {
            LoadVisits();
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
            if(c != null)
            {
               c.Dock = DockStyle.Fill;
               splitContainer1.Panel2.Controls.Add(c);
            }
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

      List<DateTime> GetOccupied(string userid, DateTime from, DateTime till)
      {
         List<DateTime> ret = new List<DateTime>();

         DataSet<int, ScriptDoc> script = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
         DataSet<int, Visit> visits = new DataSet<int, Visit>("VisitInfo", false);
         DataSet<int, Contract> contracts = new DataSet<int, Contract>(Contract.OBJECT_NAME, false);
         DataSet<int, Answer> answers = new DataSet<int, Answer>(Answer.OBJECT_NAME, false);
         DataSet<int, Distrib> remnants = new DataSet<int, Distrib>(Distrib.OBJECT_NAME, false);

         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy HH:mm:00}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy HH:mm:00}') and \"userid\"='{3}'";
         string fltr = String.Format(COMMON_FILTER_STR, "created", from, till, userid);
         string vfltr = String.Format(COMMON_FILTER_STR, "date", from, till, userid);

         script.Filter = fltr;
         visits.Filter = vfltr;
         answers.Filter = fltr;
         remnants.Filter = fltr;

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(script);
         upd.Add(visits);
         upd.Add(answers);
         upd.Add(remnants);
         upd.Add(contracts);

         Config cfg = Config.GetConfig();
         DataModule.RefreshGiveSets(cfg.GetConnection(), upd, null).Join();

         foreach(IDataSet st in new IDataSet[]{script, visits, answers, remnants, contracts})
         {
            foreach(object o in st.Data)
            {
               ret.Add(((BaseDocument)o).created);
            }
         }

         return ret;
      }

      Random rnd = new Random();
      void UpdateDoc(BaseDocument doc, BaseDocument src, ScriptDoc document, DateTime created)
      {
         TimeSpan ts;

         if (src.sended.CompareTo(src.created) > 0)
            ts = src.sended.Subtract(src.created);
         else
            ts = new TimeSpan((long)rnd.Next(15 * 60) * 10000000);


         doc.id = document.id;
         doc.userid = document.userid;

         doc.date = created;
         doc.created = created;
         doc.sended = created.Add(ts);
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         if (((Org)cbOrg.SelectedItem) == null)
         {
            MessageBox.Show("Выберите организацию!");
            return;
         }

         string userid = ((Agent)cbAgents.SelectedItem).id;
         string old_userid = string.Empty;

         List<DateTime> occupied = GetOccupied(userid, dateTimePicker1.Value, dateTimePicker1.Value.AddMinutes(2));

         DataSet<int, ScriptDoc> upd_script = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
         DataSet<int, Visit> upd_visits = new DataSet<int, Visit>("Visit", false);
         DataSet<int, Contract> upd_contracts = new DataSet<int, Contract>(Contract.OBJECT_NAME, false);
         DataSet<int, Answer> upd_answers = new DataSet<int, Answer>(Answer.OBJECT_NAME, false);
         DataSet<int, Distrib> upd_remnants = new DataSet<int, Distrib>(Distrib.OBJECT_NAME, false);
         DataSet<int, VisitItemDoc> addItems = new DataSet<int, VisitItemDoc>(VisitItemDoc.OBJECT_NAME, false);



         DateTime created = GetFreeTime(dateTimePicker1.Value, occupied);
         DateTime oldCreated = document.created;

         TimeSpan ts = document.sended.CompareTo(document.created) > 0 ? document.sended.Subtract(document.created) : new TimeSpan((long)rnd.Next(15 * 60) * 10000000);

         document.created = created;
         document.date = created;
         document.userid = userid;
         document.id = ((Org)cbOrg.SelectedItem).id;
         document.sended = created.Add(ts);

         for (int i = 0; i < document.items.Count; i++)
         {
            ScriptDocItem item = document.items[i];

            if (item.state != ScriptDocItem.DOC_INITED || item.Document == null)
               continue;

            BaseDocument src = item.Document as BaseDocument;

            ts = src.created.Subtract(oldCreated);
            ts.Add(new TimeSpan(rnd.Next(2 * 60) - 60));

            DateTime docCreated = GetFreeTime(created.Add(ts), occupied);

            if (item.type.Equals("Visit"))
            {
               if (docHash.ContainsKey(item.itemID))
               {
                  VisitControl vc = (VisitControl)docHash[item.itemID];
                  Visit newDoc = (Visit)vc.UpdateDoc();
                  if (newDoc.items.Count > 0)
                  {
                     List<TimeSpan> photos = vc.PhotoTS;

                     UpdateDoc(newDoc, vc.Src, document, docCreated);
                     item.Document = newDoc;
                     item.date = docCreated;

                     DateTime dt = docCreated;
                     foreach(Visit.VisitItem vi in newDoc.items)
                     {
                        vi.date = dt;
                        if (photos.Count > 0)
                        {
                           TimeSpan ct = photos[rnd.Next(photos.Count)];
                           dt = dt.Add(ct);
                        }
                     }

                     upd_visits.Add(upd_visits.Count, item.Document);
                     upd_visits.Add(upd_visits.Count, vc.Src);
                  }
                  else
                  {
                     MessageBox.Show("В одно из посещений не добавлены фото");
                     return;
                  }
               }
            }
            else if (item.type.Equals("Answer"))
            {
               BaseDocument srcDoc = (BaseDocument)item.Document;
               BaseDocument newDoc = docHash[item.itemID].UpdateDoc() as BaseDocument;

               UpdateDoc(newDoc, srcDoc, document, docCreated);
               item.Document = newDoc;
               item.date = docCreated;

               upd_answers.Add(upd_answers.Count, newDoc);
            }
            else if (item.type.Equals("Distrib"))
            {
               BaseDocument srcDoc = (BaseDocument)item.Document;
               BaseDocument newDoc = docHash[item.itemID].UpdateDoc() as BaseDocument;

               UpdateDoc(newDoc, srcDoc, document, docCreated);
               item.Document = newDoc;
               item.date = docCreated;

               upd_remnants.Add(upd_remnants.Count, newDoc);
            }
            else if (item.type.Equals(Contract.OBJECT_NAME))
            {
               BaseDocument srcDoc = (BaseDocument)item.Document;
               BaseDocument newDoc = docHash[item.itemID].UpdateDoc() as BaseDocument;

               UpdateDoc(newDoc, srcDoc, document, docCreated);
               item.Document = newDoc;
               item.date = docCreated;

               upd_contracts.Add(upd_remnants.Count, newDoc);
            }
         }

         upd_script.Add(upd_script.Count, document);

         List<IDataSet> update = new List<IDataSet>();

         update.Add(upd_script);
         update.Add(upd_visits);
         update.Add(upd_answers);
         update.Add(upd_remnants);
         update.Add(upd_contracts);

         Config cfg = Config.GetConfig();
         bool res = DataModule.PutNoExec(update, cfg.GetConnection()); 
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

         //if (res && (update_old.Count > 0))
         //   res = DataModule.PutNoExec(update_old, cfg.GetConnection()); //DataModule.UpdateDataSet(update_old, null, null, cfg.GetConnection(), old_userid);

         if (res) 
            if (MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
               MessageBoxIcon.Information) == System.Windows.Forms.DialogResult.OK)
               Close();
            else
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
      }

      private DateTime GetFreeTime(DateTime dateTime, List<DateTime> occupied)
      {
         while (true)
         {
            if (!occupied.Contains(dateTime))
            {
               occupied.Add(dateTime);
               return dateTime;
            }
            dateTime = dateTime.AddSeconds(1);
         }
      }
   }
   interface IAnswerControl
   {
      void SetValue(List<AnswerItem> value);
      List<AnswerItem> GetValue();
   }

   class ContractControl : DocControl
   {
      Contract src;
      public ContractControl(Contract src)
      {
         this.src = src;
      }

      public Network.DataObject UpdateDoc()
      {
         return src;
      }
   }
}

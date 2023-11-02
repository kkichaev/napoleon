using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private DataSet<string, Slsnet> dsSlsnet;
      private DataSet<string, City> dsCity;
      private DataSet<int, Contract> dsContract;
      DataSet<string, NBTLViewer> viewers = new DataSet<string, NBTLViewer>(NBTLViewer.OBJECT_NAME);

      public MainFormEx()
      {
         dsSlsnet = new DataSet<string, Slsnet>(Slsnet.OBJECT_NAME);
         dsCity = new DataSet<string, City>(City.OBJECT_NAME);
         dsContract = new DataSet<int, Contract>(Contract.OBJECT_NAME);

#if NbtlMonitor
         ServerCommand.Category = "btlViewer";

         foreach(ToolStripButton tsb in new ToolStripButton[] { tsbMakeHtml, btnOrderReport, btnCensus, 
            btnDivision, btnTask, btnPriceRemnants, btnUserLocation, tsbCoverArea, btnSavePhoto })
         {
            tsb.Visible = false;
         }
         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         tgvAgentsSummaryProgres.Visible = false;
#else
         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
#endif
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         updSets.Insert(0, dsCity);
         updSets.Insert(0, dsSlsnet);
         updSets.Add(dsContract);

#if NbtlMonitor
         updSets.Add(scripts);
#endif
      }

#if NbtlMonitor

      SimpleDataSet<ScriptDoc> scripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);
      protected override void AddMainSets(List<IDataSet> upd)
      {
         base.AddMainSets(upd);
         upd.Add(viewers);
         Config c = Config.GetConfig();
         viewers.Filter = string.Format("id='{0}' and password='{1}'", c.login, c.password);
      }

      protected override void OnMainDataFetched(List<IDataSet> upd)
      {
         if(viewers.Count > 0)
         {
            IEnumerator ie = viewers.Data.GetEnumerator();
            ie.MoveNext();
            NBTLViewer v = (NBTLViewer)ie.Current;
            DivisionManager m = new DivisionManager();
            m.login = v.id;
            m.password = v.password;
            m.division = v.division;

            foreach(IDataSet ds in upd)
            {
               if(ds.Name == DivisionManager.OBJECT_NAME)
               {
                  ds.Add(m.login, m);
                  break;
               }
            }
         }
      }

      public string ViewerContracts()
      {
         string wherein = "";
         NBTLViewer v;
         Manager mgr = CurrentUser.user as Manager;
         if (mgr != null && mgr.src != null && viewers.TryGetValue(mgr.src.login, out v))
         {
            foreach (NBTLViewer.Item i in v.contracts)
            {
               if (wherein.Length > 0) wherein += ",";
               wherein += "'" + i.id + "'";
            }
         }

         return wherein;
      }

      public string ScriptFilter()
      {
         string wherein = ViewerContracts();
         return wherein.Length > 0 ?
            "\"scriptId\" in (select \"id\" from \"ScriptDef\" where \"cdefid\" in (" + wherein + "))" :
            "1 = 0";
      }

      public string GetMonitorFilter(string userid)
      {
         Manager mgr = CurrentUser.user as Manager;
         if (mgr != null && mgr.src != null)
         {
            string uidFilter = " and 1 = 1 ";
            if (userid != null)
               uidFilter = " and sd.\"userid\"='" + userid + "' ";

            string filter = "\"{0}\" in (select sdi.\"date\" from \"ScriptDoc$items\" sdi INNER JOIN \"ScriptDoc\" sd on " +
               "sdi.\"ScriptDoc$userid\" = sd.\"userid\" and sdi.\"ScriptDoc$created\" = sd.\"created\" where sd.\"created\" >= ToDate('{1:dd/MM/yyyy}') and " +
               "sd.\"created\" < ToDate('{2:dd/MM/yyyy} 23:59:59') and (sd." + ScriptFilter() + ")"+ uidFilter + ")";
            return filter;
         }

         return "1 = 0";
      }

      Dictionary<string, List<DateTime>> availDocs = new Dictionary<string, List<DateTime>>();
      protected override void AfterRefreshData()
      {
         Dictionary<string, UserActivity> ua = new Dictionary<string, UserActivity>();
         foreach(UserActivity u in dsUserActivity.Data)
         {
            ua[u.id] = u;
         }

         base.AfterRefreshData();

         availDocs.Clear();

         foreach(ScriptDoc sd in scripts.Data)
         {
            UserActivity act;
            if(ua.TryGetValue(sd.userid, out act))
            {
               if(act.date.CompareTo(sd.sended) < 0)
               {
                  act.date = sd.sended;
               }
            }

            List<DateTime> ddocs;
            if(!availDocs.TryGetValue(sd.userid, out ddocs))
            {
               ddocs = new List<DateTime>();
               availDocs[sd.userid]= ddocs;
            }
            foreach(ScriptDocItem sdi in sd.items)
            {
               ddocs.Add(sdi.date);
            }
         }
      }
      public bool HaveDoc(BaseDocument sd)
      {
         List<DateTime> ddocs;
         if (!availDocs.TryGetValue(sd.userid, out ddocs))
         {
            return false;
         }

         return ddocs.Contains(sd.created);
      }

#endif

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
#if NbtlMonitor
         COMMON_FILTER_STR = GetMonitorFilter(null);
         string dateF = String.Format("and \"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')"
            , "created", dateBegin, dateEnd);
         scripts.Filter = ScriptFilter() + dateF;
#endif
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsContract.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

      class DivisionSummaryEx : DivisionSummary
      {
         public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig):base(dsConfig) { }
         
         protected override void PostAddData()
         {
            IDataSet cdata = DataModule.Get(Contract.OBJECT_NAME);
            if (cdata != null)
               foreach (Contract order in cdata.Data)
                  this.Add(order);
         }

         private void Add(Contract contract)
         {
            if (contract.agent != null && ContainsKey(contract.userid))
            {
               SummaryData sd = this[contract.userid];
               sd.AddOrg(contract);
            }
         }
      }
   }

#if NbtlMonitor
   class SummaryDataMon : SummaryData
   {
      public SummaryDataMon(Agent agent, DataSet<int, CommonConfig> dsConfig) : base(agent, dsConfig)
      {
      }

      public override void AddOrg(BaseDocument doc)
      {
         if (((MainFormEx)MainForm.Instance).HaveDoc(doc))
            base.AddOrg(doc);
      }
      public override void AddOrder(BaseDocument doc)
      {
         if (((MainFormEx)MainForm.Instance).HaveDoc(doc))
            base.AddOrder(doc);
      }
   }
#endif
}

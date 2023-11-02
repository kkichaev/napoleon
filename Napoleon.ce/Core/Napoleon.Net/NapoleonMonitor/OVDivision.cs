using GRSoft.Network;
using System.Collections.Generic;
using GRSoft.NapoleonManager.Utils;
using System;
using System.Windows.Forms;


namespace GRSoft.NapoleonManager
{
   class OrderVisitDivision :
      Dictionary<String, List<OrderDetailRepresentation>>
   {
      private Division division;
      private TimeInterval timeInterval;
      private Dictionary<String, List<Org>> routes = new Dictionary<string, List<Org>>();
      private DataSet<int, OrgFolder> dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
      private DataSet<int, Order> dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME);
      private DataSet<int, VisitInfo> dsVisit = (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME);
      private Form parent;

      public event EmptyParamHandler OnLoadComplete;

      public OrderVisitDivision(Form parent, Division division, TimeInterval timeInterval)
      {
         this.division = division;
         this.timeInterval = timeInterval;
         this.parent = parent;
      }

      //Настроить фильтры для наборов данных
      private void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         const string COMMON_FILTER_STR = "{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy}') and {3}";

         string agentFilter = DataUtils.MakeFilterFromAgents(null, division.GetAllAgents());

         dsOrgFolder.Filter = agentFilter;
         dsOrder.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentFilter);
         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd, agentFilter);
      }

      public void Load()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         AdjustFilterForDS(timeInterval.begin, timeInterval.end);

         updSets.Add(DsCommonOrgs.GetCommonOrgs());
         updSets.Add(dsOrgFolder);
         updSets.Add(dsOrder);
         updSets.Add(dsVisit);

         DataModule.SetDataRepsonceHandlers(OnDataRetrieved, OnRetrievedError);

         FmWait.ShowForm(parent,
            DataModule.RefreshGiveSets(MainForm.Instance.conn, updSets, FmWait.ProgressIndicator));
      }

      private void EndOfRetrieveProcess()
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }
      private void OnDataRetrieved(object sender, EventArgs e)
      {
         EndOfRetrieveProcess();
         LoadThis();
      }

      private void OnRetrievedError(EDataResponse e)
      {
         EndOfRetrieveProcess();
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private void LoadThis()
      {
         Clear();
         routes.Clear();
         routes = GetRoutePeriod(timeInterval.begin, timeInterval.end);

         bool oneDay = false;

         //foreach (ScriptDoc doc in DataModule.Get(ScriptDoc.OBJECT_NAME).Data)
         //{
         //   if (!ContainsKey(doc.userid))
         //      Add(doc.userid, new List<OrderDetailRepresentation>());

         //   List<OrderDetailRepresentation> reprList = this[doc.userid];

         //   Order o = doc.Order;
         //   reprList.Add(new OrderDetailRepresentation(doc.date,
         //      new ObjType(ObjType.TObjType.Script),
         //      doc.date, doc.sended, doc.org,
         //         doc.OrderSum, doc.IncassSum, (o == null) ? 0 : o.Qty, doc, oneDay));
         //}

         foreach (KeyValuePair<string, List<Org>> agentRoute in routes)
         {
            if (!ContainsKey(agentRoute.Key))
               Add(agentRoute.Key, new List<OrderDetailRepresentation>());

            List<OrderDetailRepresentation> reprList = this[agentRoute.Key];
            List<Org> route = agentRoute.Value;

            if (route == null || route.Count == 0)
               continue;

            foreach (Org org in route)
            {
               if (org != null && OrdersDetail.OrgNotInVisit(org))
               {
                  reprList.Add(new OrderDetailRepresentation(DateTime.MinValue,
                     new ObjType(ObjType.TObjType.NotVisit),
                     DateTime.MinValue, DateTime.MinValue, org, 0, 0, 0, org, oneDay));
               }
            }
         }

         if (OnLoadComplete != null)
            OnLoadComplete();
      }

      private Dictionary<String, List<Org>> GetRoutePeriod(DateTime begin, DateTime end)
      {
         Dictionary<String, List<Org>> result = new Dictionary<String, List<Org>>();
         List<string> dayProcessed = new List<string>();
         DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

         while (begin.Date < end.Date)
         {
            WeekDay weekDay = new WeekDay(begin.DayOfWeek);

            if (!dayProcessed.Contains(weekDay.FullName))
            {
               foreach (OrgFolder orgFolder in routes.Data)
               {
                  if (orgFolder.name.Equals(weekDay.FullName))
                  {
                     if (!result.ContainsKey(orgFolder.agent.id))
                        result.Add(orgFolder.agent.id, new List<Org>());

                     List<Org> listOrg = result[orgFolder.agent.id];


                     foreach (OrgFolderItem item in orgFolder.items)
                     {
                        if (!listOrg.Contains(item.org))
                           listOrg.Add(item.org);
                     }
                  }
               }

               dayProcessed.Add(weekDay.FullName);
            }

            begin = begin.AddDays(1);
         }

         return result;
      }

      public String DivisionName { get { return division.name; } }
      public DateTime DateBegin { get { return timeInterval.begin; } }
      public DateTime DateEnd { get { return timeInterval.end; } }

      public List<Org> GetRoute(String agentID)
      {
         if (routes.ContainsKey(agentID))
            return routes[agentID];
         else
            return null;
      }
   }
}
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmMakeSheduleMerch : FmMakeShedule
   {
      public FmMakeSheduleMerch()
      {
         Text = "Распределение маршрутов мерчендайзеров";
      }

      protected override void AdjustForm()
      {
         //clmnAgentAdd.Visible = false;
         clmnDCode.Visible = false;

         foreach (ToolStripItem sti in new ToolStripItem[] { tsAgentAdd, tsbAgentAdd, btnAgentAddClear, tsDisp, tsbDisp, btnDispClear })
         {
            sti.Visible = false;
         }
      }

      //void AddMerchAgentRoute(List<ReplacedSet> rpl, Dictionary<string, SimpleDataSet<MerchRouteForAgent>> agentMerchData, OrgFolder of, ServoluxSheduleItem si)
      //{
      //   if (si.Agent.Length == 0)
      //      return;

      //   SimpleDataSet<MerchRouteForAgent> wrs;
      //   if(!agentMerchData.TryGetValue(si.Agent, out wrs))
      //   {
      //      wrs = new SimpleDataSet<MerchRouteForAgent>(MerchRouteForAgent.OBJECT_NAME, false);
      //      rpl.Add(new ReplacedSet(si.Agent, wrs));
      //      agentMerchData.Add(si.Agent, wrs);
      //   }

      //   MerchRouteForAgent data = new MerchRouteForAgent();
      //   data.userid = si.Agent;
      //   data.id = si.org.id;
      //   data.day = of.name;
      //   wrs.Add(data);
      //}
      
      protected override Dictionary<string, List<OrgFolder>> PrepareRoutes(List<IDataSet> wrs, List<ReplacedSet> rpl)
      {
         //SimpleDataSet<OrgFolder> curRoutes = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
         //curRoutes.Filter = "not \"userid\" is null";
         //DataModule.RefreshDataSet(curRoutes, Config.GetConfig().GetConnection(), false, null).Join();

         Agents agents = Agents.GetDataSet();

         Dictionary<string, SimpleDataSet<MerchRouteForAgent>> agentMerchData = new Dictionary<string, SimpleDataSet<MerchRouteForAgent>>();

         Dictionary<string, List<OrgFolder>> data = new Dictionary<string, List<OrgFolder>>();
         foreach (ServoluxSheduleItem si in sheduleData)
         {
            Dictionary<string, AgentRouteData> idata = si.GetUserDayList();
            foreach (KeyValuePair<string, AgentRouteData> kv in idata)
            {
               if (usedAgents.ContainsKey(kv.Key) == false)
               {
                  //if(si.Agent == kv.Key)
                  //{
                  //   if(data.ContainsKey(si.Agent) == false)
                  //      data.Add(si.Agent, GetAgentRoutes(curRoutes, si.Agent, true));
                  //} else
                     continue;
               }

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
                        //AddMerchAgentRoute(rpl, agentMerchData, of, si);
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
                     //AddMerchAgentRoute(rpl, agentMerchData, newf, si);

                     data[kv.Key].Add(newf);
                  }
               }
            }
         }
         return data;
      }

      protected override string SheduleFilter()
      {
         return "\"created\" = (select max(\"created\") from \"ServoluxShedule\" where \"routeType\" = '" + ServoluxShedule.MERCH_ROUTE_TYPE + "')";
      }

      protected override bool TestAgent(Agent a)
      {
         return a.isMerch != 0;// || a.isDsp != 0;
      }

      protected override string RouteType()
      {
         return ServoluxShedule.MERCH_ROUTE_TYPE;
      }

      protected override IntStringData[] CicleValues()
      {
         return new IntStringData[] {
            new IntStringData(0, ""),
            new IntStringData(3, "2"),
            //new IntStringData(4, "3"),
            new IntStringData(5, "4"),
         };
      }

      protected override IntStringData[] RouteValues()
      {
         return new IntStringData[] {
            new IntStringData(0, ""),
            new IntStringData(3, "М"),
            //new IntStringData(5, "ДМ"),
            //new IntStringData(6, "m"),
         };
      }

   }
}

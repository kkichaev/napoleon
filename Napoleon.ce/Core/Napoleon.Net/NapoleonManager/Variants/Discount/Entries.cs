using GRSoft.Network;
/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using System;
using System.Collections.Generic;
namespace GRSoft.NapoleonManager
{
   class FormEntries
   {
      internal static DivisionForm OpenDivisionForm()
      {
         return new DivisionForm();
      }

      internal static FmDetail OpenDetailForm(FmDetailData data)
      {
         return new FmDetail(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserForm(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensus();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         if (baseType == typeof(ProgramInitializer))
            return typeof(ProgramInitializerEx);
         return baseType;
      }
   }

   public class ProgramInitializerEx : ProgramInitializer
   {
      public override bool Initialize()
      {
         bool doConvert = false;
         string adminPassword = "";

         string[] args = Environment.GetCommandLineArgs();
         for (int i = 0; i < args.Length; i++)
         {
            string ca = args[i].ToLower();
            if(ca == "/load_dbf")
            {
               doConvert = true;
            } else if(ca.StartsWith("/p:"))
            {
               string[] pa = ca.Split(new char[] { ':' });
               if (pa.Length > 1)
                  adminPassword = pa[1];
            }
         }

         if( doConvert && adminPassword.Length > 0)
         {
            Agents agents = Agents.GetDataSet();
            SimpleDataSet<OrgTask> task = new SimpleDataSet<OrgTask>("OrgTaskDBF", false);
            SimpleDataSet<Question> quest = new SimpleDataSet<Question>("QuestionDBF", false);
            SimpleDataSet<QuestValue> qvals = new SimpleDataSet<QuestValue>("QuestionItemValueDBF", false);


            Config cfg = Config.GetConfig();
            DBConnection dbc = cfg.GetConnection();
            dbc.login = "admin";
            dbc.password = adminPassword;

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(task);
            upd.Add(quest);
            upd.Add(qvals);
            upd.Add(agents);

            System.Threading.Thread t = DataModule.RefreshGiveSets(dbc, upd, null);
            t.Join();

            upd.Clear();
            if(task.Count > 0)
            {
               task.Name = OrgTask.OBJECT_NAME;
               upd.Add(task);
            }

            SimpleDataSet<AgentQuest> aqst = new SimpleDataSet<AgentQuest>(AgentQuest.OBJECT_NAME, false);
            if(quest.Count > 0)
            {
               Dictionary<string, List<string>> values = new Dictionary<string, List<string>>();
               foreach(QuestValue qv in qvals.Data)
               {
                  List<string> ival = null;
                  if (values.ContainsKey(qv.iditem))
                     ival = values[qv.iditem];
                  else
                  {
                     ival = new List<string>();
                     values.Add(qv.iditem, ival);
                  }
                  ival.Add(qv.value);
               }

               foreach(Question q in quest.Data)
               {
                  foreach(QuestionItem qitem in q.items)
                  {
                     if(values.ContainsKey(qitem.iditem))
                     {
                        qitem.values = new List<QuestionItemValue>();
                        foreach(string sv in values[qitem.iditem])
                        {
                           QuestionItemValue qvi = new QuestionItemValue();
                           qvi.value = sv;
                           qitem.values.Add(qvi);
                        }
                     }
                  }
                  q.InvalidateHtml();
                  foreach(Agent a in agents.Data)
                  {
                     AgentQuest aq = new AgentQuest();
                     aq.idquest = q.idquest;
                     aq.userid = a.id;
                     aqst.Add(aq);
                  }
               }

               quest.Name = Question.OBJECT_NAME;
               upd.Add(quest);
               upd.Add(aqst);
            }

            DataModule.UpdateDataSet(upd, null, null, dbc);
         }
         return !doConvert;
      }
   }

   class QuestValue : DataObject
   {
      public string iditem = "";
      public string value = "";
   }
}
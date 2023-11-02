using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Net.Mail;
using System.Net;
using System.IO;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   class EmailJob
   {
      public static int NO_ERR = 0;
      public static int ERR_CODE = 1;

      public static readonly string USE_LOG_FILE = "-log";
      public static readonly string LOGFILENAME = "emaillog.txt";

      public const string COM_ID = "\x5A\x1O\x1fM\xeL\xdI\xcG\x23I\x1D";
      public const string COM_LOGIN = "\x2C\x3O\x4M\x5L\x6O\x7G\x7I\x6N";

      private DataSet<string, DivisionManager> dsManagers = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME, false);
      private DataSet<int, Division> dsDivision = new DataSet<int, Division>(Division.OBJECT_NAME, false);
      public GRSoft.Network.SimpleDataSet<ManagerConfigObj> dsManagerConfig = new Network.SimpleDataSet<ManagerConfigObj>(ManagerConfigObj.OBJECT_NAME, false);
      private DataSet<string, Agent> dsAgents = new DataSet<string, Agent>(Agent.OBJECT_NAME, false);
      bool uselog = false;
      string logFileName = LOGFILENAME;
      int divisionIdx = -1;

      public int DoJob()
      {
         int res = NO_ERR;

         List<string> arguments = new List<string>(Environment.GetCommandLineArgs());
         int pos = arguments.IndexOf(USE_LOG_FILE);

         uselog = pos != -1;

         if (pos != -1 && pos < arguments.Count - 1)
            logFileName = arguments[pos + 1];

         WriteToLog("start napoleon manager for send emails");
         dsManagerConfig.Filter = string.Format("\"manager\"='{0}'", COM_ID);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsAgents);
         upd.Add(dsManagers);
         upd.Add(dsDivision);
         upd.Add(dsManagerConfig);

         Config cfg = Config.GetConfig();
         cfg.login = COM_LOGIN;
         cfg.password = string.Empty;

         DataModule.ClearEvents();
         DataModule.DataProcessed += new EventHandler((o, e) =>
         {
            DataModule.ClearEvents();
            WriteToLog("server data retrieved");
            WriteToLog(string.Format("data size: {0}", dsManagers.Count));
         });

         DataModule.OnDataResponceError += new EventDataResponseError((e) =>
         {
            DataModule.ClearEvents();
            WriteToLog(string.Format("grsoft server connection error: {0}", e.Msg));
            res = ERR_CODE;
         });


         DataModule.RefreshGiveSets(cfg.GetConnection(), upd, null).Join();

         if (res == NO_ERR)
         {
            foreach (Division d in dsDivision.Data)
            {
               if (d.parent <= 0)
               {
                  divisionIdx = d.id;
               }
               else
               {
                  dsDivision[d.parent].Childs.Add(d);
               }
            }

            if (divisionIdx < 0)
            {
               WriteToLog("Can't find division");
            }
            else
            {
               bool noerr = true;
               foreach (DivisionManager m in dsManagers.Values)
               {
                  if (m.email.Trim().Length > 0 && dsDivision.ContainsKey(m.division))
                  {
                     string attach = DoReport(m);

                     if (attach != null && attach.Length > 0)
                        WriteToLog(string.Format("manager {0} has attach {1}", m.login, attach));
                     else
                        WriteToLog(string.Format("manager {0} has not attach", m.login));

                     if (attach != null)
                        noerr = SendEmail(m, attach);

                     if (!noerr)
                        res = ERR_CODE;
                  }
               }
            }
         }

         WriteToLog("finish work");
         WriteToLog("");

         return res;
      }

      private bool SendEmail(DivisionManager m, string attach)
      {
         bool res = true;

         try
         {
            WriteToLog("email initing");
            Dictionary<string, string> setting = GetSMPTSetting();

            MailAddress from = new MailAddress(setting["from"]);
            MailAddress to = new MailAddress(m.email);
            MailMessage msg = new MailMessage(from, to);
            msg.Subject = setting["header"];
            msg.Body = setting["body"];
            msg.Attachments.Add(new System.Net.Mail.Attachment(attach));

            int port = 465;
            Int32.TryParse(setting["port"], out port);

            SmtpClient smtp = new SmtpClient();
            SetDomain(smtp);
            smtp.Host = setting["server"];
            smtp.Port = port;
            smtp.UseDefaultCredentials = false;
            smtp.Credentials = new NetworkCredential(setting["login"], setting["pwd"]);
            smtp.EnableSsl = setting["ssl"] == "1" ;
            smtp.DeliveryMethod = SmtpDeliveryMethod.Network;

            WriteToLog(string.Format("smtp server: {0}", setting["server"]));
            WriteToLog(string.Format("smtp port: {0}", setting["port"]));
            WriteToLog(string.Format("smtp from: {0}", setting["from"]));
            WriteToLog(string.Format("smtp to: {0}", m.email));
            smtp.Send(msg);
            WriteToLog("email sended successfully");
         }
         catch (Exception e)
         {
            WriteToLog(string.Format("sending ERROR: {0}: {1}", e.Message, e.InnerException != null ?  e.InnerException.Message : ""));
            res = false;
         }

         return res;
      }

      private Dictionary<string, string> GetSMPTSetting()
      {
         Dictionary<string, string> res = new Dictionary<string, string>();

         foreach (ManagerConfigObj m in dsManagerConfig.Values)
            res[m.key] = m.value;

         return res;
      }

      private string DoReport(DivisionManager m)
      {
         FmStartWorkReport.Data data = new FmStartWorkReport.Data();
         data.date = DateTime.Now.Date.AddDays(-1);
         data.userids = FmStartWorkReport.CollectUserids(dsDivision[divisionIdx].GetAllAgents());

         return ReportResult.GetReport(FmStartWorkReport.REPORT_NAME, data);
      }

      private void WriteToLog(string msg)
      {
         if (uselog)
            using (StreamWriter w = new StreamWriter(logFileName, true))
            {
               if (msg.Trim().Length == 0)
                  w.WriteLine("");
               else
                  w.WriteLine("{0}: {1}", DateTime.Now, msg);
            }
      }

      static void SetDomain(SmtpClient smtp)
      {
         var field = typeof(SmtpClient).GetField("clientDomain", BindingFlags.NonPublic | BindingFlags.Instance);
         if(field != null)
            field.SetValue(smtp, "mail._domainkey");
      }
   }
}

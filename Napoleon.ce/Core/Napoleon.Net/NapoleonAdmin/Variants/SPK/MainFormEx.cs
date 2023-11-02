using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
         clmn.DataPropertyName = "Interval";
         clmn.HeaderText = "Напомнить о заявках через, мин";
         clmn.Width = 100;

         usersView.Columns.Add(clmn);

         string[] rights = new string[] 
         {
            "DisplayChecker", "Stuff",
         };
         string[] columns = new string[] 
         {
            "Оператор по выкладке", "Сотрудник",
         };

         InitRightColumns(rights, columns);
      }
   }

   class UserDataItemEx : UserDataItem 
   {
      public int interval = 0;

      public UserDataItemEx(UserData container) : base(container)
      {
      }

      ServerConfig GetConfig(string userid, string key)
      {
         foreach (ServerConfig sc in container.dsServerConfig.Data)
         {
            if (sc.userid == userid && sc.key == key)
               return sc;
         }
         return null;
      }

      protected override void UpdateRight(string name, bool value)
      {
         base.UpdateRight(name, value);
         if (value && name == "DisplayChecker")
         {
            base.UpdateRight("Stuff", false);
         }
         if (value && name == "Stuff")
         {
            base.UpdateRight("DisplayChecker", false);
         }
      }

      int GetConfigValue(string name, int defaulValue)
      {
         int ret = defaulValue;
         if (agent != null)
         {
            ServerConfig sc = GetConfig(agent.id, name);
            if (sc != null)
            {
               int value;
               if (int.TryParse(sc.value, out value))
                  ret = value;
            }
         }

         return ret;
      }

      void ChangeConfigValue(string name, int value)
      {
         if (agent == null)
            return;

         ServerConfig sc = GetConfig(agent.id, name);
         if (sc == null)
         {
            sc = new ServerConfig();
            sc.key = name;
            sc.userid = agent.id;
            DataSet<int, ServerConfig> ds = container.dsServerConfig;
            ds.Add(ds.Count + 1, sc);
         }

         sc.value = value.ToString();

         Resolver resolver = new Resolver(name, interval, value, null);
         container.FireChanging(resolver);
      }


      public int Interval
      {
         get { return GetConfigValue("ЗаяквиДолжныОтправлены", 0); }
         set { ChangeConfigValue("ЗаяквиДолжныОтправлены", value); }
      }
   }
}

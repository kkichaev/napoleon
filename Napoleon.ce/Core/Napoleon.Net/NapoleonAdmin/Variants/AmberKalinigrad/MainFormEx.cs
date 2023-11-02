using System.Windows.Forms;
using GRSoft.Network;
using System;
using System.Collections.Generic;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      FmAgentActivity agents = new FmAgentActivity();
      SimpleDataSet<AgentActivity> agentActivity = new SimpleDataSet<AgentActivity>(AgentActivity.OBJECT_NAME, false);

      public MainFormEx()
      {
         TabPage tp = new TabPage();
         tp.Name = "agentActivity";
         tp.Padding = new System.Windows.Forms.Padding(3);
         tp.Location = new System.Drawing.Point(4, 22);
         tp.Size = new System.Drawing.Size(1238, 503);
         tp.Text = "Активность агентов";
         tp.UseVisualStyleBackColor = true;

         tp.Controls.Add(agents);

         agents.Size = new System.Drawing.Size(20, 20);
         agents.Location = new System.Drawing.Point(0, 0);
         agents.Dock = DockStyle.Fill;

         int index = tabControl1.Controls.IndexOf(userActivity) + 1;
         List<Control> upd = new List<Control>();
         while(tabControl1.Controls.Count > index)
         {
            upd.Add(tabControl1.Controls[index]);
            tabControl1.Controls.RemoveAt(index);
         }

         tabControl1.Controls.Add(tp);
         tabControl1.Controls.SetChildIndex(tp, index);

         upd.ForEach(x => { tabControl1.Controls.Add(x); });
      }

      protected override void AddUpdDataSet(System.Collections.Generic.List<IDataSet> upd)
      {
         agentActivity.Clear();
         upd.Add(agentActivity);
      }

      protected override void UpdateLoadedData()
      {
         agents.RefreshData(agentActivity);
      }
   }

   public class AgentActivity : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "AgentActivity";

      public string userid = "";
      public string login = "";
      public DateTime sended = DateTime.Now;
      public string phone = "";
      public string imei = "";
   }
}
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public partial class FmMoveAgentData : Form
   {
      Config config;
      Agent srcAgent;
      public FmMoveAgentData()
      {
         InitializeComponent();

         cbMoveType.SelectedIndex = 0;
      }

      public void SetSrcAgent(Agent srcA, Config config)
      {
         this.config = config;
         srcAgent = srcA; 
         if(srcA != null)
         {
            srcAgentLabel.Text = "Перенести с агента: " + srcA.Name;

            List<Agent> src = new List<Agent>();
            DataSet<string, Agent> dsAgents = Agents.GetDataSet();
            foreach (Agent a in dsAgents.Data)
               if (a.id != srcAgent.id)
                  src.Add(a);

            src.Sort();
            src.ForEach(x => lbAgents.Items.Add(x));
         }

      }

      private void button1_Click(object sender, EventArgs e)
      {
         Agent dest = lbAgents.SelectedItem as Agent;
         if(dest == null)
         {
            MessageBox.Show("Не выбран агент для переноса.");
            return;
         }

         string[] docs = new string[] {
            "Order",
            "Visit",
            "Returns",
         };

         bool moveDocs = (cbMoveType.SelectedIndex == 0);
         string objName = moveDocs ? MoveDocs.OBJECT_MOVE_NAME : MoveDocs.OBJECT_COPY_NAME;
         SimpleDataSet<MoveDocs> wr = new SimpleDataSet<MoveDocs>(objName, false);
         if(cbRoute.Checked)
         {
            MoveDocs obj = new MoveDocs();
            obj.src = srcAgent.id;
            obj.dst = dest.id;
            obj.doc = "OrgFolder";
            wr.Add(obj);
         }

         if(cbDocs.Checked)
         {
            foreach(string doc in docs)
            {
               MoveDocs obj = new MoveDocs();
               obj.src = srcAgent.id;
               obj.dst = dest.id;
               obj.doc = doc;
               wr.Add(obj);
            }
         }

         if(wr.Count == 0)
         {
            MessageBox.Show("Не выбрано действий");
            return;
         }

         List<IDataSet> wrs = new List<IDataSet>();
         wrs.Add(wr);
         bool ret = DataModule.UpdateDataSet(wrs, null, null, config.GetConnection());
         if (ret)
         {
            MessageBox.Show("Данные изменены");
         }

      }
   }

   class MoveDocs : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_MOVE_NAME = "MoveDocs";
      public static readonly string OBJECT_COPY_NAME = "CopyDocs";
      public string src = "";
      public string dst = "";
      public string doc = "";
   }
}

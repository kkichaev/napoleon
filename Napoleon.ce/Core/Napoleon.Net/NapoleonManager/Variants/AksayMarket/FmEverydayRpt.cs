using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEverydayRpt : Form
   {
      public FmEverydayRpt()
      {
         InitializeComponent();
         dtpDate.Value = DateTime.Now;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.date = dtpDate.Value.Date;
         CollectUserIDS(data.agents);
         ReportResult.DoReport("everyday_report", data, this);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";
         }

         public DateTime date = DateTime.MinValue;
         public List<Item> agents = new List<Item>();
      }

      private void CollectUserIDS(List<Data.Item> users)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Agent a in m.GetAgents().Values)
            {
               Data.Item item = new Data.Item();
               item.id = a.id;
               users.Add(item);
            }
         }
      }

   }
}

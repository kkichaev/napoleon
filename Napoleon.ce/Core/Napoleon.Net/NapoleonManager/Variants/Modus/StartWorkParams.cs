using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class StartWorkParams : Form
   {
      public StartWorkParams(Data data)
      {
         InitializeComponent();
         this.data = data;
         dtpBegin.Value = data.begin;
         dtpEnd.Value = data.end;
      }

      private void WorkTimeParams_FormClosing(object sender, FormClosingEventArgs e)
      {
         data.begin = dtpBegin.Value.Date;
         data.end = dtpEnd.Value.Date;

         Division d = cbDivision.SelectedItem as Division;

         if (d != null)
         {
            data.divname = d.Name;

            StringBuilder sb = new StringBuilder();
            foreach (Division.DivisionAgent a in d.GetAllAgents())
            {
               if (sb.Length > 0)
                  sb.Append(",");

               sb.Append("'").Append(a.id).Append("'");
            }

            data.userids = sb.ToString();
         }
      }

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime begin = DateTime.Now;
         public DateTime end = DateTime.Now; 
         public string userids;
         public string divname;
      }

      Data data;

      private void StartWorkParams_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            cbDivision.Items.AddRange(list.ToArray());
            SelectDivision(mc);
         }
      }

      private void SelectDivision(Manager mc)
      {
         int sel = -1;
         for (int i = 0; i < cbDivision.Items.Count; i++)
         {
            Division d = (Division)cbDivision.Items[i];

            if (d.id.Equals(mc.Division.id))
            {
               sel = i;
               break;
            }
         }

         cbDivision.SelectedIndex = sel;
      }

   }
}

using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmAuditParam : Form
   {
      public FmAuditParam()
      {
         InitializeComponent();
      }

      private void FmAuditParam_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> al = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               al.Add(da.agent);
            }

            al.Sort();
            al.ForEach(x => cbAgents.Items.Add(x));

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }

         DataSet<int, Matrix> ds = DataModule.Get(GRSoft.NapoleonManager.Matrix.OBJECT_NAME) as DataSet<int, Matrix>;

         if (ds != null)
         {
            List<Matrix> ml = new List<Matrix>();
            ml.AddRange(ds.Values);
            ml.Sort((x, y) => { return x.name.CompareTo(y.name); });
            ml.ForEach(x => cbMatrix.Items.Add(x));

            if (cbMatrix.Items.Count > 0)
               cbMatrix.SelectedIndex = 0;
         }
      }

      public DateTime Date { get { return dateTimePicker1.Value.Date; } set { dateTimePicker1.Value = value; } }
      public string Userid { get { return GetUserId(); } }
      public string Matrix { get { return GetMatrix(); } }

      private string GetMatrix()
      {
         string result = string.Empty;

         Matrix m = cbMatrix.SelectedItem as Matrix;

         if (m != null)
            result = m.name;

         return result;
      }

      private string GetUserId()
      {
         string result = string.Empty;

         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
            result = a.id;

         return result;
      }
   }
}

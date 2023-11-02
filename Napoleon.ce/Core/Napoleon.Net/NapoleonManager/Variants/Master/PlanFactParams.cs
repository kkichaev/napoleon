using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class PlanFactParams : Form
   {
      public class Data : GRSoft.Network.DataObject
      {
         public DateTime begin;
         public DateTime end;
         public int divisionID = 0;
      }

      Data data;

      public PlanFactParams(Data data)
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
      }
   }
}

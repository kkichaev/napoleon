using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class MerchParams : Form
   {
      public MerchParams(Data data)
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

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime begin;
         public DateTime end;
         public string userids;
      }

      Data data;
   }
}

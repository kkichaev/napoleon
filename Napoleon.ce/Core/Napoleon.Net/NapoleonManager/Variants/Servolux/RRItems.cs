using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class RRItems : UserControl
   {
      public RRItems()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetDocument(ReturnRequest doc) 
      { 
         dgvItems.DataSource = doc.items; 
      }
   }
}

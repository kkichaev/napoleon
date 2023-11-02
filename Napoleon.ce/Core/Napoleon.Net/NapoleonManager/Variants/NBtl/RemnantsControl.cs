using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class RemnantsControl : UserControl, DocControl
   {
      Distrib doc;

      public RemnantsControl(Distrib doc)
      {
         InitializeComponent();
         this.doc = doc;

         grid.AutoGenerateColumns = false;
         grid.DataSource = doc.items;   
      }

      public Network.DataObject UpdateDoc()
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         return doc;
      }
   }
}

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
      OrgRemnants remnants;

      public RemnantsControl(OrgRemnants remnants)
      {
         InitializeComponent();
         this.remnants = remnants;

         grid.AutoGenerateColumns = false;
         grid.DataSource = remnants.items;   
      }

      public Network.DataObject UpdateDoc()
      {
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);

         return remnants;
      }
   }
}

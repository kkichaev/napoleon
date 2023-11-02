using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPtnzlOrgEdit : Form
   {
      PotenzialOrg org = new PotenzialOrg();

      public FmPtnzlOrgEdit()
      {
         InitializeComponent();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         org.name = tbName.Text;
         org.Address = tbAddress.Text;

         base.OnClosing(e);
      }

      public virtual PotenzialOrg Org
      {
         get
         {
            return org;
         }

         set
         {
            org = value;
            tbName.Text = org.name;
            tbAddress.Text = org.Address;
         }
      }
   }
}

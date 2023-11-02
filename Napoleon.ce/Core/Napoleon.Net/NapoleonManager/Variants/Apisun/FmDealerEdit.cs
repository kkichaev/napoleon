using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmDealerEdit : Form
   {
      public FmDealerEdit()
      {
         InitializeComponent();
      }

      public static Dealer Edit(Dealer dealer)
      {
         Dealer result = null;
         FmDealerEdit form = new FmDealerEdit();

         if (dealer != null)
         {
            form.tbId.Text = dealer.Id;
            form.tbName.Text = dealer.Name;
            form.tbId.Enabled = false;
         }
         else
            form.tbId.Text = GenNewId();

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = dealer ?? new Dealer();

            if (dealer == null)
               result.id = form.tbId.Text.Trim();

            result.name = form.tbName.Text.Trim();
         }

         return result;
      }

      private static string GenNewId()
      {
         DataSet<string, Dealer> dsDealer = (DataSet<string, Dealer>)DataModule.Get(Dealer.OBJECT_NAME);
         string result = "1";

         if (dsDealer != null)
         {
            int maxNumber = 0;

            foreach (Dealer ot in dsDealer.Data)
            {
               int val = 0;
               if (Int32.TryParse(ot.id, out val))
                  if (val > maxNumber)
                     maxNumber = val;
            }

            result = (++maxNumber).ToString();
         }

         return result;
      }

      private void FmDealerEdit_Load(object sender, EventArgs e)
      {
         tbName.Focus();
      }
   }
}

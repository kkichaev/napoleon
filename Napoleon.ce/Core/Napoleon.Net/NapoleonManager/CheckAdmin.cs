using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class CheckAdmin : Form
   {
      protected CheckAdmin()
      {
         InitializeComponent();
      }

      private void ok_Click(object sender, EventArgs e)
      {
         CheckAdminPassword();
      }
      private void CheckAdminPassword()
      {
         GetCommand sc = new GetCommand("admin", password.Text, new string[] { "Division" });
         SendParam sp = new SendParam(sc, CheckData, new List<IDataSet> ());
         Thread th = Config.GetConfig().GetConnection().SendCommand(sp);
         th.Join();
      }

      public void CheckData(PacketObject result, List<IDataSet> sets)
      {
         bool res = false;
         if (result != null && result.Count != 0)
         {
            ObjectList answ = result[0];

            if (answ.Name == "ServerAnswer")
            {
               GRSoft.Network.Object o = answ[0];
               res = ((double)o["response"].Value == 1.0);
            }
         }

         DialogResult = (res) ? DialogResult.OK : DialogResult.Cancel;
      }

      public static bool CheckPassword()
      {
         CheckAdmin ca = new CheckAdmin();
         return (ca.ShowDialog() == DialogResult.OK);
      }

      private void CheckAdmin_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyData == Keys.Return)
         {
            CheckAdminPassword();
         }
      }

      private void password_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyData == Keys.Return)
         {
            CheckAdminPassword();
         }
      }
   }
}

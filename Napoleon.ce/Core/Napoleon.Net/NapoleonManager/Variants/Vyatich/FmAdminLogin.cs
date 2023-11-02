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
   public partial class FmAdminLogin : Form
   {
      public FmAdminLogin()
      {
         InitializeComponent();
      }

      public static bool CheckAdmin(Form owner)
      {
         FmAdminLogin fm = new FmAdminLogin();
         return fm.ShowDialog(owner) == DialogResult.OK;
      }

      private void ok_Click(object sender, EventArgs e)
      {
         DBConnection conn = Config.GetConfig().GetConnection();
         conn.login = "admin";
         conn.password = password.Text;

         SimpleDataSet<Agent> check = new SimpleDataSet<Agent>(Agent.OBJECT_NAME, false);
         DataModule.SetDataRepsonceHandlers(Done, ErrHandler);
         Thread th = DataModule.RefreshDataSet(check, conn, false, null);
         th.Join();

         DataModule.ClearEvents();
         if( check.Count > 0 )
            DialogResult = DialogResult.OK;
      }

      void Done(object sender, EventArgs e)
      {
      }

      void ErrHandler(EDataResponse e)
      {
         MessageBox.Show(e.Msg);
      }
   }
}

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
   public partial class FmDisabledFirms : Form
   {
      DataSet<string, DisabledFirms> dsDisabled = new DataSet<string, DisabledFirms>(DisabledFirms.OBJECT_NAME, false);

      public FmDisabledFirms()
      {
         InitializeComponent();

         if (CurrentUser.user != null)
            button1.Enabled = CurrentUser.user.HaveRight(RightTokens.Get("FmDisabledFirms"), RightActions.Write);
      }

      internal void SetFactories(List<Factory> factories)
      {
         foreach(Factory f in factories)
            lbFirms.Items.Add(f);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         DataModule.RefreshDataSet(dsDisabled, Config.GetConfig().GetConnection(), false, null).Join();
         List<int> check = new List<int>();
         foreach (Factory f in lbFirms.Items)
         {
            if (dsDisabled.ContainsKey(f.id))
               check.Add(lbFirms.Items.IndexOf(f));
         }

         foreach (int i in check)
            lbFirms.SetItemChecked(i, true);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         bool done = false;
         DBConnection conn = Config.GetConfig().GetConnection();

         CheckedListBox.CheckedItemCollection coll = lbFirms.CheckedItems;
         if (coll.Count > 0)
         {
            SimpleDataSet<DisabledFirms> wr = new SimpleDataSet<DisabledFirms>(DisabledFirms.OBJECT_NAME, false);
            foreach (Factory f in coll)
            {
               DisabledFirms df = new DisabledFirms();
               df.id = f.id;
               wr.Add(df);
            }

            done = DataModule.UpdateDataSet(null, null, new List<ReplacedSet>(new ReplacedSet[] { new ReplacedSet(wr) }), conn);
         }
         else
         {
            done = DataModule.RemoveDataSet(dsDisabled, conn);
         }

         if (done)
         {
            DialogResult = DialogResult.OK;
            return;
         }

         MessageBox.Show("Ошибка при записи");
      }
   }
}

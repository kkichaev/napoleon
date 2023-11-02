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
   public partial class SalesHistoryParams : Form
   {
      SalesHistory.Data data;

      internal SalesHistoryParams(SalesHistory.Data data, DataSet<string, Org> dsOrg, string selectedOrg)
      {
         this.data = data;
         InitializeComponent();

         Org selected = null;
         foreach (KeyValuePair<string, Org> kv in dsOrg)
         {
            kagents.Items.Add(kv.Value);
            if (selectedOrg != null && selectedOrg.Equals(kv.Key))
               selected = kv.Value;
         }

         kagents.Sorted = true;
         if (selected != null)
            kagents.SelectedItem = selected;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         data.from = dateFrom.Value;
         data.till = dateTill.Value;
         if (data.from.CompareTo(data.till) > 0)
         {
            data.from = data.till;
            data.till = dateFrom.Value;
         }

         data.org = (Org)kagents.SelectedItem;
         if (data.org == null)
         {
            DialogResult res = MessageBox.Show("Не выбран контрагент. Отчет не будет построен. Продолжать?", "Вопрос",
               MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (res == DialogResult.No)
               e.Cancel = true;
         }
         base.OnClosing(e);
      }
   }
}

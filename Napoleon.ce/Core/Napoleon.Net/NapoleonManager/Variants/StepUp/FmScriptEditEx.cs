using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptEditEx : FmScriptEdit
   {
      private DataSet<string, ContractDef> dsContract;

      protected FmScriptEditEx(PostProcess pp) : base(pp)
      {
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         const string FLT = "\"finish\" >= ToDate('{0:dd/MM/yyyy}')";
         dsContract.Filter = string.Format(FLT, DateTime.Now);

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsContract);
         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), list, null);
         t.Join();

         imageList1.Images.Add(Resources.qty2report);
         int idx = imageList1.Images.Count - 1;

         foreach (ContractDef c in dsContract.Data)
         {
            ContractDoc doc = new ContractDoc(c);
            ListViewItem item = lvDocsAvail.Items.Add(doc.type, doc.name, idx);
            item.Tag = doc;
         }
      }

      protected override void AddScriptItem()
      {
         bool shc = ScriptHasContract();
         bool allow = true;

         foreach(ListViewItem s in lvDocsAvail.SelectedItems)
            if (shc && s.Tag is ContractDoc)
            {
               allow = false;
               break;
            }

         if (allow)
            base.AddScriptItem();
         else
            MessageBox.Show("Нельзя добавить в сценарий больше одного контракта");
      }

      private bool ScriptHasContract()
      {
         bool result = false;
         foreach (ListViewItem i in lvDocs.Items)
            if (i.Tag is ContractDoc)
            {
               result = true;
               break;
            }

         return result;
      }
   }
}

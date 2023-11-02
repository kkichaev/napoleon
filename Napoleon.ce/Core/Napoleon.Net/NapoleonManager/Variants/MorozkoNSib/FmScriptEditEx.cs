using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections;
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

         lvDocsAvail.SelectedIndexChanged += lvDocsAvail_SelectedIndexChanged;

         foreach (ScriptDocument sd in ScriptDocuments.Documents)
         {
            if (!(sd is VisitDoc))
               sd.condParam = string.Empty;
         }
      }

      void lvDocsAvail_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnAdd.Enabled = true;

         ListView.SelectedListViewItemCollection list = ((ListView)sender).SelectedItems;

         foreach (ListViewItem i in list)
         {
            if (i.Tag is PlanogramDoc)
            {
               btnAdd.Enabled = FindItem(typeof(ContractDoc)) != null;
               break;
            }
         }
      }

      private ScriptDocument FindItem(Type type)
      {
         ScriptDocument result = null;

         foreach(ListViewItem i in lvDocs.Items)
            if (i.Tag != null && i.Tag.GetType() == type)
            {
               result = i.Tag as ScriptDocument;
               break;
            }

         return result;
      }

      protected override void AddScriptItem()
      {
         bool shc = FindItem(typeof(ContractDoc)) != null;
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

      protected override void Save()
      {
         PlanogramDoc pd = FindItem(typeof(PlanogramDoc)) as PlanogramDoc;
         ContractDoc cd = FindItem(typeof(ContractDoc)) as ContractDoc;
         CMonitoringDoc md = FindItem(typeof(CMonitoringDoc)) as CMonitoringDoc;

         if ((pd != null || md != null) && cd == null)
            MessageBox.Show("Если сценарий содержит планограмму или мониторинг, то обязательно надо добавить контракт");
         else
         {
            if (pd != null && cd != null)
               pd.condParam = cd.condParam;

            if (cd != null && md != null)
               md.condParam = cd.condParam;

            VisitDoc vd = FindItem(typeof(VisitDoc)) as VisitDoc;

            if (vd != null)
               vd.condParam = cd.condParam;

            if (script == null)
               script = new ScriptDef();

            if (cd != null)
               script.cdefid = cd.condParam;

            base.Save();

            if (vd != null)
               vd.condParam = "";
         }
      }
   }
}

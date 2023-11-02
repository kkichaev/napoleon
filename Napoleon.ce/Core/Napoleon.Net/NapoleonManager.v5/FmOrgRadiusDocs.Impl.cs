using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgRadiusDocs : Form
   {
      static string ORG_DISPOSITION_KEY = "ORG_DISPOSITION_DOCS";

      Dictionary<string, ScriptDocument> availDocs = new Dictionary<string, ScriptDocument>();

      DataSet<int, CommonConfig> dsConfig;

      public void __Initing()
      {

         foreach (ScriptDocument d in AvailDocs)
            availDocs.Add(d.type, d);
      }

      internal virtual ScriptDocument[] AvailDocs 
      {
         get { 
            return new ScriptDocument[]{
               new OrderDoc(),
               new IncassDoc(),
               new VisitDoc(),
            };
         }
      }

      public void SetConfig(DataSet<int, CommonConfig> dsCommonConfig)
      {
         dsConfig = dsCommonConfig;

         foreach(var item in availDocs) 
            lvItems.Items.Add(item.Value);
         
         foreach (CommonConfig cc in dsConfig.Values)
         {
            if (cc.userid.Length == 0 && cc.key == ORG_DISPOSITION_KEY)
            { 
               string[] docs = cc.value.Split(new char[] {','});
               foreach(string doc in docs)
               {
                  ScriptDocument ot;
                  if (availDocs.TryGetValue(doc, out ot))
                  {
                     int idx = lvItems.Items.IndexOf(ot);
                     if (idx >= 0)
                        lvItems.SetItemChecked(idx, true);
                  }
               }
            }
         }
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         string value = "";
         foreach(ScriptDocument ot in lvItems.CheckedItems)
         {
            if (value.Length > 0)
               value += ",";
            value += ot.type;
         }
         bool added = false;
         foreach (CommonConfig cc in dsConfig.Values)
         {
            if (cc.userid.Length == 0 && cc.key == ORG_DISPOSITION_KEY)
            {
               cc.value = value;
               added = true;
               break;
            }
         }

         if(!added)
         {
            CommonConfig cc = new CommonConfig();
            cc.userid = "";
            cc.key = ORG_DISPOSITION_KEY;
            cc.value = value;
            dsConfig.Add(dsConfig.Count + 1, cc);
         }

         List<IDataSet> sets = new List<IDataSet>();
         sets.Add(dsConfig);
         DataModule.WriteDataSet(sets, Config.GetConfig().GetConnection());

         Close();
      }
   }
}

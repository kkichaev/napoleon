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

      Dictionary<string, ObjType> availDocs = new Dictionary<string, ObjType>();

      DataSet<int, CommonConfig> dsConfig;

      public void __Initing()
      {

         foreach (string d in AvailDocs)
            availDocs.Add(d, new ObjType(d));
      }

      public virtual string[] AvailDocs 
      {
         get { return new string[]{"Order", "Incass", "Visit"};}
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
                  ObjType ot;
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
         foreach(ObjType ot in lvItems.CheckedItems)
         {
            if (value.Length > 0)
               value += ",";
            value += ot.ObjName;
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

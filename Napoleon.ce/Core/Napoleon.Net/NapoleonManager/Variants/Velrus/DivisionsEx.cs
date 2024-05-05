using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DataSet<string, OrderAction> actions;
      public static DataSet<string, Org> commonOrgs;

      public static SimpleDataSet<OrgCluster> clusters;

      public DivisionsEx()
      {
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Name = "btnActions";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Акции";
         btn.Click += new System.EventHandler((s, e) => {
            FmActions acs = new FmActions();
            acs.SetActions(actions);
            acs.Show(); 
         });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         ToolStripSeparator sp = new ToolStripSeparator();
         tb.Items.Add(sp);
         tb.Items.Add(btn);

         actions = DataModule.Get(OrderAction.OBJECT_NAME) as DataSet<string, OrderAction> ??
                new DataSet<string, OrderAction>(OrderAction.OBJECT_NAME);

         commonOrgs = dsOrg;
         clusters = new SimpleDataSet<OrgCluster>(OrgCluster.OBJECT_NAME, false);
         Width += 250;
      }

      protected override void BeforeUpdate(List<IDataSet> updSets)
      {
         base.BeforeUpdate(updSets);


         string filter = "\"rem\"=0";

         actions.Filter = filter;

         updSets.Add(actions);
         updSets.Add(clusters);

         if(dsOrg.Count == 0)
         {
            updSets.Add(dsOrg);
         }
      }

   }
}

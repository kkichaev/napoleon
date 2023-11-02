using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    class DivisionsEx : Divisions
    {
        public DataSet<string, GoodsProjects> projects;
        public DataSet<string, DanaAction> actions;
        DataSet<string, OrgCluster> clusters;
        DataSet<string, Org> orgs;
        DataSet<string, Price> price;


        public DivisionsEx()
        {
            projects = DataModule.Get(GoodsProjects.OBJECT_NAME) as DataSet<string, GoodsProjects>  ?? 
                new DataSet<string, GoodsProjects>(GoodsProjects.OBJECT_NAME);

            actions = DataModule.Get(DanaAction.OBJECT_NAME) as DataSet<string, DanaAction> ??
                new DataSet<string, DanaAction>(DanaAction.OBJECT_NAME);

            clusters = DataModule.Get(OrgCluster.OBJECT_NAME) as DataSet<string, OrgCluster> ??
                new DataSet<string, OrgCluster>(OrgCluster.OBJECT_NAME, true);

            orgs = DataModule.Get(Org.COMMON_OBJECT_NAME) as DataSet<string, Org> ??
               new DataSet<string, Org>(Org.COMMON_OBJECT_NAME, true);

            price = DataModule.Get(Price.OBJECT_NAME) as DataSet<string, Price> ??
               new DataSet<string, Price>(Price.OBJECT_NAME, true);


            ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
            btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            btn.Name = "btnGoodProject";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Проекты";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
               new FmGoodProjects().Show();
            });

            tb.Items.Add(btn);

            btn = new System.Windows.Forms.ToolStripButton();
            btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            btn.Name = "btnGoodProject";
            btn.Size = new System.Drawing.Size(101, 22);
            btn.Text = "Акции";
            btn.Click += new System.EventHandler((obj, arg) =>
            {
                FmActionList form = new FmActionList();
                form.Actions = actions;
                form.Show();
            });

            tb.Items.Add(btn);
            Width += 50;
        }

        protected override void BeforeUpdate(List<IDataSet> updSets)
        {
            base.BeforeUpdate(updSets);
            projects.Filter = "not \"id\" is null";
            updSets.Add(projects);
            if (clusters.Count == 0)
                updSets.Add(clusters);

            if (orgs.Count == 0)
                updSets.Add(orgs);

            if (price.Count == 0)
                updSets.Add(price);

            string filter = "\"hidden\"=0";

            actions.Filter = filter;

            updSets.Add(actions);
        }
    }
}

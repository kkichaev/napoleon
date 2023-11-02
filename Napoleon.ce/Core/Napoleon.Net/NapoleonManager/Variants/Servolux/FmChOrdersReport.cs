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
   public partial class FmChOrdersReport : Form
   {
      static FmChOrdersReport instance = null;

      DataSet<string, Factory> firms;
      List<Price> selectedItems = new List<Price>();
      List<Org> selectedOrgs = new List<Org>();

      public FmChOrdersReport()
      {
         InitializeComponent();

         dtpBegin.Value = DateTime.Now.Date;
         dtpEnd.Value = DateTime.Now.Date;
      }

      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      public static void Do(DataSet<string, Factory> firms)
      {
         if (instance == null)
         {
            instance = new FmChOrdersReport();
            instance.firms = firms;
            instance.Show();
         }
         else
         {
            instance.firms = firms;
            instance.BringToFront();
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         lbFactories.Items.Clear();
         foreach (Factory f in firms.Data)
            lbFactories.Items.Add(f, true);


         tvAgents.Nodes.Clear();
         Manager m = (Manager)CurrentUser.user;
         PutNodes(tvAgents.Nodes, m.Division);
      }

      private void PutNodes(TreeNodeCollection tnc, Division division)
      {
         TreeNode tn = new TreeNode(division.name);
         tn.Tag = division;
         tnc.Add(tn);

         foreach (Division ch in division.Childs)
            PutNodes(tn.Nodes, ch);

         List<Agent> agents = new List<Agent>();
         foreach (Division.DivisionAgent a in division.agents)
            if (a.agent != null)
               agents.Add(a.agent);

         agents.Sort();
         agents.ForEach(x =>
         {
            TreeNode agentNode = new TreeNode(x.name);
            agentNode.Tag = x;
            tn.Nodes.Add(agentNode);
         });
      }

      private void cancel_Click(object sender, EventArgs e)
      {
         Close();
      }


      public class Data : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public string id = "";

            public Item() { }
            public Item(string id) { this.id = id; }
         }

         public List<Item> users = new List<Item>();
         public List<Item> factories = new List<Item>();
         public List<Item> sku = new List<Item>();
         public List<Item> orgs = new List<Item>();

         public int inKG;
         public int inBox;

         public DateTime begin;
         public DateTime end;
      }

      void PutAgents(List<Data.Item> users, TreeNodeCollection tnc)
      {
         foreach(TreeNode tn in tnc)
         {
            Agent a = tn.Tag as Agent;
            if (a == null)
            {
               PutAgents(users, tn.Nodes);
            }
            else if (tn.Checked)
               users.Add(new Data.Item(a.id));
         }
      }

      private void ok_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.begin = dtpBegin.Value;
         data.end = dtpEnd.Value;

         PutAgents(data.users, tvAgents.Nodes);

         foreach (int i in lbFactories.CheckedIndices)
         {
            Factory f = (Factory)lbFactories.Items[i];
            data.factories.Add(new Data.Item(f.id));
         }

         selectedItems.ForEach(x => data.sku.Add(new Data.Item(x.id)));
         selectedOrgs.ForEach(x => data.orgs.Add(new Data.Item(x.id)));

         //Price p = linkLabel1.Tag as Price;
         //if (p != null)
         //   data.sku.Add(new Data.Item(p.id));

         data.inKG = cbInKG.Checked ? 1 : 0;
         data.inBox = cbInBox.Checked ? 1 : 0;

         if( data.inKG == 0 && data.inBox == 0)
         {
            MessageBox.Show("Не заполнен вариант вывода отчета", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         //foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         //   data.users.Add(new Data.Item(a.id));

         Config.GetConfig().GetConnection().ReceiveTimeout = 10 * 30 * 1000;
         ReportResult.DoReport("chg_ordes_report", data, this);
      }

      private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         List<Price> ret = FmSelectSKUEx.SelectItemsEx(this, selectedItems, null, true);
         if (ret != null)
         {
            linkLabel2.Visible = true;
            selectedItems = ret;
         }
         //Price p;
         //if (FmSelectSKUEx.SkuQuery(this, out p) == DialogResult.OK)
         //{
         //   linkLabel1.Text = "Фильтр по " + p.name + " " + p.thermalState + "/" + p.packName;
         //   linkLabel1.Tag = p;
         //   linkLabel2.Visible = true;
         //}
      }

      private void linkLabel2_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         //linkLabel1.Tag = null;
         //linkLabel1.Text = "Выбор SKU";
         selectedItems.Clear();
         linkLabel2.Visible = false;
      }
      
      private void checkBox3_CheckedChanged(object sender, EventArgs e)
      {
         for (int i = 0; i < lbFactories.Items.Count; i++)
            lbFactories.SetItemChecked(i, checkBox3.Checked);
      }

      void SetChecked(TreeNodeCollection tnc, bool check)
      {
         foreach(TreeNode tn in tnc)
         {
            tn.Checked = check;
            if (tn.Nodes.Count != 0)
               SetChecked(tn.Nodes, check);
         }
      }

      private void tvAgents_AfterCheck(object sender, TreeViewEventArgs e)
      {
         SetChecked(e.Node.Nodes, e.Node.Checked);
      }

      private void linkLabel3_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         List<Org> sel = FmSelectOrgs.DoSelect(selectedOrgs);
         if(sel != null)
         {
            linkLabel4.Visible = true;
            selectedOrgs = sel;
         }
      }

      private void linkLabel4_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         linkLabel4.Visible = false;
         selectedOrgs.Clear();
      }
   }
}

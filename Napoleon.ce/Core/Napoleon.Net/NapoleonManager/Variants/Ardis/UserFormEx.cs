using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      private System.Windows.Forms.ToolStrip tsOrgMenu;
      private System.Windows.Forms.ToolStripButton tbAddOrgs;
      private System.Windows.Forms.ToolStripButton tbDelOrg;

      FmSelectContrAgent selOrg;

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         InitControls();
      }

      private void InitControls()
      {
         this.tsOrgMenu = new System.Windows.Forms.ToolStrip();
         this.tbAddOrgs = new System.Windows.Forms.ToolStripButton();
         this.tbDelOrg = new System.Windows.Forms.ToolStripButton();

         this.tsOrgMenu.SuspendLayout();
         this.splitContainer1.Panel2.Controls.Add(this.tsOrgMenu);

         // 
         // tsOrgMenu
         // 
         this.tsOrgMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbAddOrgs,
            this.tbDelOrg});
         this.tsOrgMenu.Location = new System.Drawing.Point(0, 0);
         this.tsOrgMenu.Name = "tsOrgMenu";
         this.tsOrgMenu.Size = new System.Drawing.Size(232, 25);
         this.tsOrgMenu.TabIndex = 2;
         this.tsOrgMenu.Text = "toolStrip1";
         // 
         // tbbAddOrgs
         // 
         this.tbAddOrgs.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbAddOrgs.Image = Resources.ca_add;
         this.tbAddOrgs.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbAddOrgs.Name = "tbAddOrgs";
         this.tbAddOrgs.Size = new System.Drawing.Size(23, 22);
         this.tbAddOrgs.Text = "Добавить контрагента";
         this.tbAddOrgs.Click += new EventHandler(tbAddOrgs_Click);

         // 
         // tbDelOrg
         // 
         this.tbDelOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbDelOrg.Image = Resources.ca_del;
         this.tbDelOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbDelOrg.Name = "tbDelOrg";
         this.tbDelOrg.Size = new System.Drawing.Size(23, 22);
         this.tbDelOrg.Text = "Удалить контрагента";
         this.tbDelOrg.Click += new EventHandler(tbDelOrg_Click);

         this.tsOrgMenu.ResumeLayout(false);
         this.tsOrgMenu.PerformLayout();

         dgvOrgs.AllowDrop = true;
         dgvOrgs.DragEnter += new DragEventHandler(dgvOrgs_DragEnter);
         dgvOrgs.DragDrop += new DragEventHandler(dgvOrgs_DragDrop);
      }

      void dgvOrgs_DragDrop(object sender, DragEventArgs e)
      {
         List<Org> orgs = GetData(e.Data.GetData(typeof(DragDropObject)) as DragDropObject);
         if (orgs == null)
            return;

         for (int i = orgs.Count - 1; i >= 0; i--)
         {
            Org o = orgs[i];
            if (dsOrg.ContainsKey(o.id) == false)
               dsOrg.Add(o.id, o);
         }
         MakeReplacedSet();
         DataUtils.FillGridFromDS(dgvOrgs, dgvOrgsName, dsOrg);
      }

      void dgvOrgs_DragEnter(object sender, DragEventArgs e)
      {
         if (GetData(e.Data.GetData(typeof(DragDropObject)) as DragDropObject) != null)
            e.Effect = DragDropEffects.Copy;
      }

      void tbDelOrg_Click(object sender, EventArgs e)
      {
         DataGridViewSelectedCellCollection cells = dgvOrgs.SelectedCells;
         Dictionary<String, bool> removed = new Dictionary<String, bool>();
         if (cells.Count > 0 && MessageBox.Show("Удалить контрагентов?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            foreach (DataGridViewCell c in cells)
            {
               Org o = c.Value as Org;
               if( o != null )
                  removed.Add(o.id, true);
            }

            foreach(String key in removed.Keys)
            {
               dsOrg.Remove(key);
            }

            MakeReplacedSet();
            DataUtils.FillGridFromDS(dgvOrgs, dgvOrgsName, dsOrg);
         }
      }

      private void MakeReplacedSet()
      {
         //DataSet<int, AgentOrgs> s = (DataSet<int, AgentOrgs>)DataModule.GetUserDataSet(Agent.id, AgentOrgs.OBJECT_NAME, typeof(DataSet<int, AgentOrgs>));
         //int idx = 0;
         //s.Clear();
         //foreach (Org o in dsOrg.Data)
         //{
         //   AgentOrgs ao = new AgentOrgs();
         //   ao.id = o.id;
         //   ao.userid = Agent.id;

         //   s.Add(idx++, ao);
         //}
         //owner.AddReplacedSet(Agent.id, s);
         dsOrg.UseReceivedFields = true;
         owner.AddReplacedSet(Agent.id, dsOrg);
      }

      void tbAddOrgs_Click(object sender, EventArgs e)
      {
         if( selOrg == null )
         {
            DataSet<String, Org> ds = (DataSet<String, Org>)DataModule.Get("CommonOrgs");
            DataSet<String, PotenzialOrg> ds1 = (DataSet<String, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME);

            selOrg = FmSelectContrAgent.ShowForm(ds, ds1, true, AddOrg, owner);
            selOrg.FormClosing += new FormClosingEventHandler(selOrg_FormClosing);
         }
      }

      List<Org> GetData(DragDropObject ddo)
      {
         if( ddo.Source is FmSelectContrAgent)
            return ddo.Data as List<Org>;
         return null;
      }

      void selOrg_FormClosing(object sender, FormClosingEventArgs e)
      {
         selOrg = null;
      }
      
      void AddOrg(object sender, Org org)
      {
         if (dsOrg.ContainsKey(org.id))
            return;

         dsOrg.Add(org.id, org);
         MakeReplacedSet();
         DataUtils.FillGridFromDS(dgvOrgs, dgvOrgsName, dsOrg);
      }

      protected override void BeforeUpdateData(List<IDataSet> updSets)
      {
         DataSet<String, Org> ds = (DataSet<String, Org>)DataModule.Get("CommonOrgs");
         if (ds == null)
            ds = new DataSet<string, Org>("CommonOrgs");

         //DataSet<int, AgentOrgs> ao = (DataSet<int, AgentOrgs>)DataModule.GetUserDataSet(Agent.id, AgentOrgs.OBJECT_NAME, typeof(DataSet<int, AgentOrgs>));
         //ao.Filter = "userid='" + Agent.id + "'";
         //updSets.Add(ao);

         if( ds.Count == 0 )
            updSets.Add(ds);
      }
   }

   //class AgentOrgs : GRSoft.Network.DataObject
   //{
   //   public static String OBJECT_NAME = "AgentOrgs";

   //   public String userid = "";
   //   public String id = "";
   //}
}
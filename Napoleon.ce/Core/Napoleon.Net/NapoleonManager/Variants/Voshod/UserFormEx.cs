using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Collections;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      System.Windows.Forms.TreeView tvDogovors;
      System.Windows.Forms.TabPage udDogovors;

      private DataSet<string, AllowedDogovor> dsAllowedDogovors;
      private DataSet<string, AgentDogovors> dsDogovors;
      private DataSet<string, FirmConfig> dsFirms;

      public UserFormEx(Divisions owner)
         : base(owner)
      {
         this.udDogovors = new System.Windows.Forms.TabPage();
         this.tvDogovors = new System.Windows.Forms.TreeView();

         this.udDogovors.SuspendLayout();
         this.userDetails.Controls.Add(this.udDogovors);
         // 
         // udDogovors
         // 
         this.udDogovors.Controls.Add(this.tvDogovors);
         this.udDogovors.Location = new System.Drawing.Point(4, 23);
         this.udDogovors.Name = "udDogovors";
         this.udDogovors.Padding = new System.Windows.Forms.Padding(3);
         this.udDogovors.Size = new System.Drawing.Size(466, 279);
         this.udDogovors.TabIndex = 3;
         this.udDogovors.Text = "Договора";
         this.udDogovors.UseVisualStyleBackColor = true;
         // 
         // tvDogovors
         // 
         this.tvDogovors.CheckBoxes = true;
         this.tvDogovors.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvDogovors.Location = new System.Drawing.Point(3, 3);
         this.tvDogovors.Name = "tvDogovors";
         this.tvDogovors.Size = new System.Drawing.Size(460, 273);
         this.tvDogovors.TabIndex = 1;

         this.udDogovors.ResumeLayout(false);

         tvDogovors.AfterCheck += new TreeViewEventHandler(tvDogovors_AfterCheck);
      }

      protected override void BeforeUpdateData(String userid, List<IDataSet> updSets)
      {
         if (dsAllowedDogovors == null)
            dsAllowedDogovors = new DataSet<string, AllowedDogovor>("AllowedDogovors", false);

         dsAllowedDogovors.Command = new ServerCommand(Commands.Impersonate(Commands.GET, Agent.id), dsAllowedDogovors.Name);
         dsAllowedDogovors.Clear();
         updSets.Add(dsAllowedDogovors);

         if (dsDogovors == null)
         {
            dsDogovors = new DataSet<string, AgentDogovors>("FirmDogovor", false);
            updSets.Add(dsDogovors);
         }

         if (dsFirms == null)
         {
            dsFirms = new DataSet<string, FirmConfig>("FirmsConfig", false);
            updSets.Add(dsFirms);
         }
      }
      private IDataSet GetAllowedDogovorsDataSet()
      {
         dsAllowedDogovors.Clear();
         foreach (TreeNode node in tvDogovors.Nodes)
         {
            foreach (TreeNode ch in node.Nodes)
            {
               if (ch.Checked)
               {
                  AllowedDogovor ad = new AllowedDogovor();
                  ad.name = ch.Text;
                  ad.userid = Agent.id;
                  if (dsAllowedDogovors.ContainsKey(ad.name) == false)
                     dsAllowedDogovors.Add(ad.name, ad);
               }
            }
         }

         return dsAllowedDogovors;
      }

      void tvDogovors_AfterCheck(object sender, TreeViewEventArgs e)
      {
         tvDogovors.BeginUpdate();

         if (e.Node.Level == 0)
            foreach (TreeNode tn in e.Node.Nodes)
               tn.Checked = e.Node.Checked;

         tvDogovors.EndUpdate();

         owner.AddReplacedSet(Agent.id, GetAllowedDogovorsDataSet());
      }

      private Dictionary<string, string> LoadFirms(DataSet<string, FirmConfig> dsFirms)
      {
         Dictionary<string, string> firms = new Dictionary<string, string>();

         if (dsFirms.Count > 0)
         {
            foreach (FirmConfig fc in dsFirms.Data)
            {
               string[] fv = fc.value.Split(new char[] { ';' });
               foreach (string v in fv)
               {
                  string[] items = v.Split(new char[] { '\t' });
                  firms[items[1]] = items[0];
               }
               break;
            }
         }

         return firms;
      }

      protected override void AfterControlFilled()
      {
         Dictionary<string, string> firms = LoadFirms(dsFirms);

         tvDogovors.BeginUpdate();
         TreeNodeCollection tnc = tvDogovors.Nodes;
         tnc.Clear();

         foreach (AgentDogovors dog in dsDogovors.Data)
         {
            if (firms.ContainsKey(dog.firm) == false)
               continue;

            TreeNode node = new TreeNode(firms[dog.firm]);

            bool haveUncheck = false;
            foreach (AgentDogovorItem item in dog.items)
            {
               TreeNode chNode = new TreeNode(item.id);

               if (dsAllowedDogovors.ContainsKey(item.id) == false)
                  haveUncheck = true;
               else
                  chNode.Checked = true;
               node.Nodes.Add(chNode);
            }
            if (!haveUncheck)
               node.Checked = true;

            tnc.Add(node);
         }
         tvDogovors.Sort();
         tvDogovors.EndUpdate();
      }
   }

   class FirmConfig : GRSoft.Network.DataObject
   {
      [KeyField]
      public string key = "";
      public string value = "";
   }

   class AgentDogovorItem : GRSoft.Network.DataObject
   {
      public string id = "";
   }

   class AgentDogovors : GRSoft.Network.DataObject
   {
      [KeyField]
      public string firm = "";

      [ItemType(typeof(AgentDogovorItem))]
      public List<AgentDogovorItem> items = new List<AgentDogovorItem>();
   }

   class AllowedDogovor : GRSoft.Network.DataObject
   {
      [KeyField]
      public string name = "";
      public string userid = "";
   }
}
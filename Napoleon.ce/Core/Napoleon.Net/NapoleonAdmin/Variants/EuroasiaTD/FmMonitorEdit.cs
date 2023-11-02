using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public partial class FmMonitorEdit : Form
   {
      DivisionManager manager; 
      Monitor monitor;
      DataSet<string, DivisionManager> dsManagers;
      DataSet<string, Agent> dsAgents;

      public FmMonitorEdit()
      {
         InitializeComponent();
      }

      public void SetData(DataSet<int, ScriptDef> dsScripts, SimpleDataSet<ManagerFolder> dsFolders, DataSet<int, Division> dsDivision,
         DataSet<string, DivisionManager> dsManagers, DataSet<string, Agent> dsAgents,
         DivisionManager manager, Monitor monitor)
      {
         this.dsAgents = dsAgents;
         this.manager = manager;
         this.monitor = monitor;
         this.dsManagers = dsManagers;

         tbLogin.Text = manager.login;
         tbPassword.Text = manager.password;


         LoadDivisions(dsDivision);
         LoadScripts(dsScripts);
         LoadFolders(dsFolders);
      }

      TreeNode Add(TreeNode parent, ManagerFolder f)
      {
         TreeNode tn = new TreeNode(f.name);
         tn.Tag = f;
         tn.Checked = monitor.HaveFolder(f);

         if (parent == null)
            tvFolders.Nodes.Add(tn);
         else
            parent.Nodes.Add(tn);

         return tn;
      }

      private void LoadFolders(SimpleDataSet<ManagerFolder> dsFolders)
      {
         int lvl = -1;
         TreeNode parent = null;
         TreeNode prevNode = null;

         foreach(ManagerFolder mf in dsFolders.Data)
         {
            if (lvl == -1)
               prevNode = Add(null, mf);
            else if (lvl == mf.level)
               prevNode = Add(parent, mf);
            else if(lvl < mf.level)
            {
               parent = prevNode;
               prevNode = Add(parent, mf);
            } else if(lvl > mf.level)
            {
               TreeNode leftNode = prevNode.Parent;
               if (leftNode == null)
               {
                  MessageBox.Show("Некорректный объект Folder", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
                  break;
               }

               int reqLvl = mf.level;
               while (leftNode.Parent != null && reqLvl < (leftNode.Tag as ManagerFolder).level)
               {
                  leftNode = leftNode.Parent;
               }

               if (reqLvl > (leftNode.Tag as ManagerFolder).level)
                  parent = leftNode;
               else
                  parent = leftNode.Parent;

               prevNode = Add(parent, mf);
            }
            lvl = mf.level;
         }
      }

      private void LoadScripts(DataSet<int, ScriptDef> dsScripts)
      {
         foreach (ScriptDef sd in dsScripts.Data)
            lbScripts.Items.Add(sd, monitor.HaveScript(sd));
      }

      private void LoadDivisions(DataSet<int, Division> dsDivision)
      {
         foreach (Division d in dsDivision.Data)
            cbDivisions.Items.Add(d);

         Division sel;
         if (dsDivision.TryGetValue(manager.division, out sel))
            cbDivisions.SelectedItem = sel;
         else if (dsDivision.Count > 0)
            cbDivisions.SelectedIndex = 0;
      }

      private void tvFolders_AfterCheck(object sender, TreeViewEventArgs e)
      {
         CheckChildren(e.Node.Nodes, e.Node.Checked);
      }

      private void CheckChildren(TreeNodeCollection nodes, bool ch)
      {
         foreach(TreeNode tn in nodes)
         {
            tn.Checked = ch;
            if (tn.Nodes.Count > 0)
               CheckChildren(tn.Nodes, ch);
         }
      }

      private void button1_Click(object sender, EventArgs e)
      {
         string login = tbLogin.Text;
         if(manager.login != login && dsManagers.ContainsKey(login))
         {
            MessageBox.Show("Логин не уникальный");
            return;
         }
         if(dsAgents.ContainsKey(login))
         {
            MessageBox.Show("Логин совпадает с логином агента");
            return;
         }

         Division sel = cbDivisions.SelectedItem as Division;
         if(sel == null)
         {
            MessageBox.Show("Не выбрано подразделение");
            return;
         }

         manager.login = login;
         manager.password = tbPassword.Text;
         manager.division = sel.id;

         monitor.userid = manager.guid;
         monitor.scripts.Clear();
         monitor.folders.Clear();

         foreach (ScriptDef sd in lbScripts.CheckedItems)
            monitor.scripts.Add(new Monitor.ScriptItem(sd));

         AddChecked(tvFolders.Nodes, monitor);
      }

      private void AddChecked(TreeNodeCollection nodes, Monitor monitor)
      {
         foreach(TreeNode tn in nodes)
         {
            if(tn.Checked)
            {
               ManagerFolder mf = tn.Tag as ManagerFolder;
               monitor.folders.Add(new Monitor.FolderItem(mf));
            }
            if (tn.Nodes.Count > 0)
               AddChecked(tn.Nodes, monitor);
         }
      }
   }
}

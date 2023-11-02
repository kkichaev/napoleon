/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Установки маршрута агента
 * 
 * kki   23/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmRoute : Form
   {
      private static FmRoute instance;
      private DsAgent dsAgent = DsAgent.GetDataSet();
      private DsSchoolFolder dsSchoolFolder = DsSchoolFolder.GetDataSet();
      private DsSchoolEntity dsSchool = DsSchoolEntity.GetDataSet();
      string[] DAYS = new string[] { "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };

      private FmRoute()
      {
         InitializeComponent();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmRoute();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmRoute_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void RefreshDataSets()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsAgent);
         updSets.Add(dsSchoolFolder);
         updSets.Add(dsSchool);

         DataModule.SetDataRepsonceHandlers(RefreshRetrieveComlete,
            DataConnectionError);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSets, FmWait.ProgressIndicator));
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         EndOfRetrieve();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         EndOfRetrieve();
         MessageBox.Show(e.Msg);
      }

      private void EndOfRetrieve()
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
      }

      private void UpdateForm()
      {
         lbAgents.SuspendLayout();
         tvRoute.SuspendLayout();

         try
         {
            lbAgents.Items.Clear();
            object selectedItem = null;
            string selectedAgentID = PermanentData.Data.AgentID;
            
            foreach (Agent a in dsAgent.Data)
            {
               if (a.id == Agent.MANAGER_ID)
                  continue;
               lbAgents.Items.Add(a);

               if (selectedAgentID.Length > 0 &&
                     a.id.Equals(selectedAgentID))
                  selectedItem = a;
            }

            if (selectedItem != null)
               lbAgents.SelectedItem = selectedItem;
         }
         finally
         {
            lbAgents.SuspendLayout();
            tvRoute.SuspendLayout();
         }
      }

      private void ClearTreeRoute()
      {
         tvRoute.Nodes.Clear();

         foreach (string d in DAYS)
            tvRoute.Nodes.Add(d);
      }

      private void FmRoute_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnAgent_Click(object sender, EventArgs e)
      {
         FmAgent.ShowInstance();
      }

      private void btnClass_Click(object sender, EventArgs e)
      {
         FmClass.ShowInstance();
      }

      private void tvRoute_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetData(typeof(List<SchoolEntity>)) == null ||
            lbAgents.SelectedItem == null)
            e.Effect = DragDropEffects.None;
         else
            e.Effect = DragDropEffects.Copy;
      }

      private void tvRoute_DragDrop(object sender, DragEventArgs e)
      {
         List<SchoolEntity> list = (List<SchoolEntity>)e.Data.GetData(typeof(List<SchoolEntity>));
            
         if (list == null)
            return;

         TreeNode treeNode = tvRoute.SelectedNode;

         if (treeNode == null)
            return;

         if (treeNode.Tag != null &&
               treeNode.Tag is SchoolEntity)
         {
            treeNode = treeNode.Parent;
         }

         List<TreeNode> newNodes = new List<TreeNode>();
         SchoolFolder schoolFolder = (SchoolFolder)treeNode.Tag;
         List<SchoolFolderItem> listSFI = new List<SchoolFolderItem>();

         foreach (SchoolEntity se in list)
         {
            SchoolFolderItem sfi = new SchoolFolderItem();
            sfi.id = se.id;
            listSFI.Add(sfi);
         }

         if (schoolFolder == null)
         {
            schoolFolder = new SchoolFolder();

            treeNode.Tag = schoolFolder;

            if (schoolFolder.items == null)
               schoolFolder.items = new List<SchoolFolderItem>();

            schoolFolder.items.AddRange(listSFI);
            schoolFolder.name = treeNode.Text;
            schoolFolder.userid = ((Agent)lbAgents.SelectedItem).id;

            List<IDataSet> insertList = new List<IDataSet>();
            DsSchoolFolder newSchoolFolder = DsSchoolFolder.GetDataSet(false);
            newSchoolFolder.Add(1, schoolFolder);
            insertList.Add(newSchoolFolder);

            if (!DataModule.InsertDataSets(insertList, Config.GetConfig().GetConnection()))
               return;
         }
         else
         {
            List<IDataSet> updSets = new List<IDataSet>();
            DsSchoolFolder updSchoolFolder = DsSchoolFolder.GetDataSet(false);

            schoolFolder.items.AddRange(listSFI);
            updSchoolFolder.Add(schoolFolder.id, schoolFolder);
            updSets.Add(updSchoolFolder);

            if (!DataModule.UpdateDataSet(updSets, null, null,
                  Config.GetConfig().GetConnection()))
              return;
         }

         foreach (SchoolEntity se in list)
         {
            TreeNode tn = new TreeNode();
            tn.Text = se.number;

            SchoolFolderItem sfi = new SchoolFolderItem();
            sfi.id = se.id;
            tn.Tag = sfi;

            treeNode.Nodes.Add(tn);

            newNodes.Add(tn);
         }

         treeNode.Expand();
      }

      private void tvRoute_DragOver(object sender, DragEventArgs e)
      {
         Point pos = tvRoute.PointToClient(new Point(e.X, e.Y));
         TreeNode targetNode = tvRoute.GetNodeAt(pos);
         
         if (targetNode != null)
         {
            tvRoute.SelectedNode = targetNode;
         }
      }

      private void lbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         string agentID = ((Agent)((ListBox)sender).SelectedItem).id;
         string filter = String.Format("userid={0}", agentID );

         PermanentData.Data.AgentID = agentID;
         dsSchoolFolder.Filter = filter;

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsSchoolFolder);

         DataModule.SetDataRepsonceHandlers(RefreshSchoolFolderComplete, DataConnectionError);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSets, FmWait.ProgressIndicator));
      }

      private void RefreshSchoolFolderComplete(object o, EventArgs e)
      {
         EndOfRetrieve();
         Invoke(new InvokeDelegate(delegate { UpdateSchoolFolder(); }));
      }

      private void UpdateSchoolFolder()
      {
         tvRoute.SuspendLayout();
         try
         {
            ClearTreeRoute();

            foreach (TreeNode node in tvRoute.Nodes)
            {
               List<SchoolFolderItem> list = new List<SchoolFolderItem>();

               SchoolFolder currentFolder = null;
               foreach (SchoolFolder sf in dsSchoolFolder.Data)
               {
                  if (sf.name.Equals(node.Text))
                  {
                     list = sf.items;
                     currentFolder = sf;
                     break;
                  }
               }

               node.Tag = currentFolder;

               if (list.Count == 0)
                  continue;


               foreach (SchoolFolderItem sfi in list)
               { 
                  if (dsSchool.ContainsKey(sfi.id))
                  {
                     TreeNode child = new TreeNode();
                     SchoolEntity se = dsSchool[sfi.id];
                     child.Text = se.number;
                     child.Tag = sfi;
                     node.Nodes.Add(child);
                  }
               }

            }
         }
         finally
         {
            tvRoute.ResumeLayout();
         }
      }

      private void btnDelete_Click(object sender, EventArgs e)
      {
         TreeNode node = tvRoute.SelectedNode;

         if (node == null || !(node.Tag is SchoolFolderItem))
            return;

         if (MessageBox.Show(this, "Удалить пункт маршрута", "Вопрос", MessageBoxButtons.OKCancel,
               MessageBoxIcon.Question) != DialogResult.OK)
         {
            return;
         }

         dsSchoolFolder[((SchoolFolder)node.Parent.Tag).id].items.Remove((SchoolFolderItem)node.Tag);
         List<IDataSet> updateSets = new List<IDataSet>();
         updateSets.Add(dsSchoolFolder);

         if (DataModule.UpdateDataSet(updateSets, null, null, Config.GetConfig().GetConnection()))
            node.Remove();
      }
   }
}
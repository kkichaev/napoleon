/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Агенты
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
   public partial class FmAgent : Form
   {
      private static FmAgent instance;
      private DsAgent dsAgent = DsAgent.GetDataSet(); 

      private FmAgent()
      {
         InitializeComponent();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmAgent();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmAgent_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void RefreshDataSets()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsAgent);

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
         dgvAgents.SuspendLayout();

         try
         {
            dgvAgents.Rows.Clear();

            foreach (Agent agent in dsAgent.Data)
            {
               if (agent.id == Agent.MANAGER_ID)
                  continue;
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvAgents, agent.name, agent.login, agent.password,
                  agent.phone);
               row.Tag = agent;
               dgvAgents.Rows.Add(row);
            }

         }
         finally
         {
            dgvAgents.ResumeLayout();
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Agent agent = new Agent();

         if (new FmAgentEdit(agent).ShowDialog() != DialogResult.OK)
            return;

         dsAgent.Add(agent.id, agent);

         List<IDataSet> toWrite = new List<IDataSet>();
         toWrite.Add(dsAgent);

         if (DataModule.UpdateDataSet(toWrite, null, null,
            Config.GetConfig().GetConnection()))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvAgents, agent.name, agent.login, agent.password, agent.phone);
            row.Tag = agent;
            dgvAgents.Rows.Add(row);
         }
         else
            dsAgent.Remove(agent.id);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         Agent agent = (Agent)dgvAgents.CurrentRow.Tag;

         if (agent == null)
            return;

         if (new FmAgentEdit(agent).ShowDialog() != DialogResult.OK)
            return;

         List<IDataSet> toWrite = new List<IDataSet>();
         toWrite.Add(dsAgent);

         if (DataModule.UpdateDataSet(toWrite, null, null, Config.GetConfig().GetConnection()))
         {
            DataGridViewRow row = dgvAgents.CurrentRow;
            row.Cells[0].Value = agent.name;
            row.Cells[1].Value = agent.login;
            row.Cells[2].Value = agent.password;
            row.Cells[3].Value = agent.phone;
         }
      }

      private void btnDelete_Click(object sender, EventArgs e)
      {
         Agent agent = (Agent)dgvAgents.CurrentRow.Tag;

         if ( agent == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsAgent toRem = DsAgent.GetDataSet(false);
         toRem.Add(agent.id, agent);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsAgent.Remove(agent.id);
            dgvAgents.Rows.RemoveAt(dgvAgents.CurrentRow.Index);
         }
      }

      private void dgvAgents_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         Edit();
      }

      private void FmAgent_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }
   }
}
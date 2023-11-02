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
   public partial class FmTaskTemplates : Form
   {
      static FmTaskTemplates instance = null;

      DataSet<string, NapoleonTaskTemplate> dsTask = new DataSet<string, NapoleonTaskTemplate>(NapoleonTaskTemplate.OBJECT_NAME, false);
      List<Agent> agents;

      public FmTaskTemplates()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
         dtpDate.Value = DateTime.Now.Date;
      }

      public static void Open(List<Agent> agents)
      {
         if (instance == null)
         {
            instance = new FmTaskTemplates();
            instance.agents = agents;
            instance.Show();
         }
         else
         {
            instance.agents = agents;
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }
   
      void RefreshData()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsTask);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, null);
      }

      void DoLoadData()
      {
         List<NapoleonTaskTemplate> src = new List<NapoleonTaskTemplate>();
         src.AddRange((IEnumerable<NapoleonTaskTemplate>)dsTask.Data);
         src.Sort();
         dgvItems.DataSource = src;
      }

      bool WriteTasks(string task)
      {
         SimpleDataSet<NapoleonTask> wr = new SimpleDataSet<NapoleonTask>(NapoleonTask.OBJECT_NAME, false);
         foreach (Agent a in agents)
         {
            NapoleonTask nt = new NapoleonTask();
            nt.userid = a.id;
            nt.start = dtpDate.Value;
            nt.end = dtpDate.Value;
            nt.task = task;
            nt.id = GRSoft.Network.DataObject.GenId();
            wr.Add(nt);
         }

         List<IDataSet> wrSet = new List<IDataSet>(new IDataSet[] { wr });
         return DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
      }

      private void dgvItems_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         NapoleonTaskTemplate ntt = dgvItems.Rows[e.RowIndex].DataBoundItem as NapoleonTaskTemplate;
         if( !WriteTasks(ntt.Text) )
         {
            MessageBox.Show("Ошибка при записи");
         }
         else
         {
            if (MessageBox.Show("Задача была добавлена. Хотите добавить еще одну?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.No)
               Close();
         }
      }

      private void btnAddTask_Click(object sender, EventArgs e)
      {
         FmNewTask task = new FmNewTask();
         task.Date = dtpDate.Value;
         if( task.ShowDialog() == System.Windows.Forms.DialogResult.OK && task.Text.Length > 0 )
         {
            if (!WriteTasks(task.Task))
            {
               MessageBox.Show("Ошибка при записи");
            }
            else
            {
               MessageBox.Show("Задача была добавлена.", "Информация", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
         }
      }
   }
}

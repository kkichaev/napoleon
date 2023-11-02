using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmPlan : Form
   {
      private static FmPlan instance;
      private DataSet<string, Agent> dsAgents;
      private DataSet<int, Plan> dsPlan;
      private ChangesObserver<Boolean> saveObserver;

      public FmPlan()
      {
         InitializeComponent();

         dsAgents = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME) ??
            new DataSet<string, Agent>(Agent.OBJECT_NAME);
         dsPlan = (DataSet<int, Plan>)DataModule.Get(Plan.OBJECT_NAME) ??
            new DataSet<int, Plan>(Plan.OBJECT_NAME);

         saveObserver = new ChangesObserver<bool>(delegate(Boolean value)
         {
            btnSave.Enabled = value;
         });
      }

      public static void ShowInstance(Agent agent)
      {
         if (instance == null)
         {
            instance = new FmPlan();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmPlan_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void FmPlan_Load(object sender, EventArgs e)
      {
         foreach (Agent agent in dsAgents.Data)
            cbAgents.Items.Add(agent);

         cbAgents.Sorted = true;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsPlan);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator)
         );
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         List<Plan> list = new List<Plan>();

         foreach (Plan plan in dsPlan.Data)
            list.Add(plan);

         dgvPlans.DataSource = list;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Plan plan = FmPlanEdit.ShowInstance(null);

         int newKey = 0;

         IEnumerator<int> keyEnum = dsPlan.Keys.GetEnumerator();

         while (keyEnum.MoveNext())
            if (newKey < keyEnum.Current)
               newKey = keyEnum.Current;

         newKey++;

         if (plan != null)
         {
            dsPlan.Add(newKey, plan);
            RefreshData();
            saveObserver.Changed = true;
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsPlan);

         if (!DataModule.UpdateDataSet(list, null, null,
            Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка записи в базу данных.");
         else
            saveObserver.Changed = false;
      }

      private void FmPlan_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (saveObserver.Changed &&
               MessageBox.Show(this, "Сохранить изменения?", "Вопрос",
               MessageBoxButtons.OKCancel) == DialogResult.OK)
            btnSave_Click(null, null);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvPlans.CurrentRow;

         if (row != null)
         {
            Plan plan = (Plan) row.DataBoundItem;

            if (plan != null && FmPlanEdit.ShowInstance(plan) != null)
               saveObserver.Changed = true;
         }
      }
   }

   class ChangesObserver<StateType>
   {
      public delegate void OnChange<StateTye>(StateTye newValue);

      private StateType changed;
      private OnChange<StateType> onChange;

      public ChangesObserver(OnChange<StateType> onChange)
      {
         this.onChange = onChange;
      }

      public StateType Changed
      {
         get { return changed; }
         set
         {
            changed = value;

            if (onChange != null)
               onChange(value);
         }
      }
   }

}

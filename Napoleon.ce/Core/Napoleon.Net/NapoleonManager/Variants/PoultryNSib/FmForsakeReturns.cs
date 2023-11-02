using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class FmForsakeReturns : Form
   {
      DataSet<int, ReturnsEx> dsReturns = new DataSet<int, ReturnsEx>(Returns.OBJECT_NAME, false);
      DataSet<string, Price> dsPrice;
      DataSet<string, Org> dsOrg;
      Agent selectedAgent = null;

      public FmForsakeReturns()
      {
         InitializeComponent();

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);

         dgvReturn.AutoGenerateColumns = false;
         btnSave.Enabled = false;
      }

      private void FmForsakeReturns_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.Name.CompareTo(rhs.Name); }));

         cbAgent.Items.AddRange(list.ToArray());
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (selectedAgent != null)
         {
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

            dsReturns.Filter = string.Format("\"userid\"='{0}' and \"forsake\"=1", selectedAgent.id);
            dsOrg = DataModule.GetUserDataSet(selectedAgent.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            List<IDataSet> updSet = new List<IDataSet>();

            updSet.Add(dsPrice);
            updSet.Add(dsOrg);
            updSet.Add(dsReturns);

            FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
         }
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            BindingList<ReturnsEx> data = new BindingList<ReturnsEx>();

            foreach (ReturnsEx r in dsReturns.Data)
               data.Add(r);
            dgvItem.DataSource = null;
            dgvReturn.DataSource = data;
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }


      private bool SaveData()
      {
         bool result = false;

         if (selectedAgent != null)
         {
            SimpleDataSet<ReturnsEx> retSet = new SimpleDataSet<ReturnsEx>(ReturnsEx.OBJECT_NAME, false);
            SimpleDataSet<RetNtfy> ntfySet = new SimpleDataSet<RetNtfy>(RetNtfy.OBJECT_NAME, false);

            foreach (DataGridViewRow r in dgvReturn.Rows)
            {
               ReturnsEx re = (ReturnsEx)r.DataBoundItem;

               if (re.Collect)
               {
                  RetNtfy ntfy = new RetNtfy();
                  ntfy.created = re.created;
                  ntfy.userid = re.AgentID;

                  retSet.Add(re);
                  ntfySet.Add(ntfy);
               }
            }

            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(retSet);
            wrSet.Add(ntfySet);

            if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection(), selectedAgent.id))
            {
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
            }
            else
            {
               btnSave.Enabled = false;
               result = true;
            }

         }

         return result;
      }

      private void cbAgent_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (btnSave.Enabled &&
            MessageBox.Show(this, "Сохранить изменения?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
            SaveData();

         selectedAgent = cbAgent.SelectedItem as Agent;
         dgvReturn.Rows.Clear();
         dgvItem.Rows.Clear();
      }

      private void dgvReturn_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex != -1)
         {
            Returns ret = dgvReturn.Rows[e.RowIndex].DataBoundItem as Returns;

            if (ret != null)
            {
               BindingList<ReturnItem> list = new BindingList<ReturnItem>();
               foreach (ReturnItem item in ret.items)
                  list.Add(item);

               dgvItem.DataSource = list;
            }
         }
      }

      private void dgvReturn_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         ((DataGridView)sender).CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      private void dgvReturn_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveData();
      }

      private void btnPrint_Click(object sender, EventArgs e)
      {
         if (selectedAgent != null)
         {
            List<Returns> list = new List<Returns>();
            List<string> orgids = new List<string>();


            foreach (DataGridViewRow row in dgvReturn.Rows)
            {
               ReturnsEx ex = (ReturnsEx)row.DataBoundItem;
               if(!ex.Collect && !orgids.Contains(ex.id))
               {
                  list.Add(ex);
                  orgids.Add(ex.id);
               }
            }

            ForsakeReport report = new ForsakeReport();
            report.DoReport(list, selectedAgent);
            report.Visible = true;
         }
      }
   }

   class ForsakeReport : Excel
   {
      public void DoReport(List<Returns> ret, Agent agent)
      {
         SetValue(1, 1, "Отложенные возвраты");
         SetValue(2, 1, "Торговый представитель");
         SetValue(2, 2, agent.Name);
         SetValue(4, 1, "Позиция");
         SetValue(4, 2, "Организация");

         int row = 5;
         int pos = 1;
         
         foreach(Returns r in ret)
         {
            SetValue(row, 1, pos);
            SetValue(row, 2, r.OrgName);
            row++;  pos++;
         }
      }
   }
}

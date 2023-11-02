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
   
   public partial class FmWelcome : Form
   {
      private Config config = Config.GetConfig();

      //DataSets
      private Agents dsAgents = Agents.GetDataSet();
      private DivisionList dsDivisionList = DivisionList.GetDataSet();

      //FmWelcome
      public FmWelcome()
      {
         InitializeComponent();
         AdjustForm();
      }

      //Настройка формы
      private void AdjustForm()
      {
         DataModule.OnDataResponceError += new EventDataResponseError(DataConnectionError);
         DialogResult = DialogResult.Cancel;
         EnableSecondDataControl(false);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         DataModule.OnDataResponceError -= new EventDataResponseError(DataConnectionError);
         base.OnClosing(e);
      }

      //Настройка поведения визуальных компонентов
      private void EnableSecondDataControl(bool enable)
      {
         gbDivision.Enabled = enable;
      }

      //Событие окончания выборки
      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= DataLoaded;
         BeginInvoke(new EmptyParamHandler(FillControlsAfterLoading));
      }

      //Загрузка данных
      private void FillControlsAfterLoading()
      {
         //Если уже ест созданные подразделения, то нет необходимости запрашивать
         //пользователя на создание подразделения, просто закрываем окно
         if (dsDivisionList.Count > 0)
         {
            DialogResult = DialogResult.OK;
            config.Save();
            Close();
         }

         EnableSecondDataControl(true);
         Agent selectingAgent = null;

         foreach (Agent a in dsAgents.Data)
         {
            cbAgents.Items.Add(a);

            if (a.login == tbLogin.Text && a.password == tbPassw.Text)
            {
               selectingAgent = a;
            }
         }

         if (selectingAgent != null)
         {
            cbAgents.SelectedItem = selectingAgent;
         }

         gbConnection.Enabled = false;
      }

      //Ошибка при содединении
      private void DataConnectionError(EDataResponse e)
      {
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         btnConnect.Enabled = true;
         EnableSecondDataControl(false);
      }

      //Выход из приложения
      private void btnCancel_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.Cancel;
         Application.Exit();
      }

      //Проверка, все поля должны быть заполнены
      private bool ValidControlData()
      {
         foreach (Control c in gbConnection.Controls)
         {
            if (c.Text.Trim().Length == 0)
            {
               c.Focus();
               return false;
            }
         }

         return true;
      }
      //Подключиться к базе данных
      private void btnConnect_Click(object sender, EventArgs e)
      {
         if (!ValidControlData())
         {
            MessageBox.Show("Необходимо заполнить все поля!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }
         try
         {
            config.ip       = tbIP.Text;
            config.port     = Convert.ToInt32(tbPort.Text);
            config.login    = tbLogin.Text;
            config.password = tbPassw.Text;
            config.rememberPassword = cbRememberPassword.Checked;
         }
         catch (Exception exception)
         {
            MessageBox.Show(exception.Message, "Ошбика", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         DataModule.DataProcessed += DataLoaded;
         DataModule.RefreshGiveSets(config.GetConnection(), new object[] { dsAgents, dsDivisionList }, FmWait.ProgressIndicator);
      }

      //ОК, запоминаем сделанные изменения
      private void btnOK_Click(object sender, EventArgs e)
      {
         DivisionList divisionList = new DivisionList();

         Division division = new Division();
         division.parent = 0;
         division.name = tbDivision.Text;
         division.description = tbDescription.Text;
         division.id = divisionList.NextID();
         division.cheif = cbAgents.SelectedItem as Agent;

         divisionList.Add(division.id, division);

         List<IDataSet> wrList = new List<IDataSet>();
         wrList.Add(divisionList);

         if (DataModule.UpdateDataSet(wrList, null, null, config.GetConnection()) == false)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         DialogResult = DialogResult.OK;
         config.Save();
         Close();
      }
   }
}
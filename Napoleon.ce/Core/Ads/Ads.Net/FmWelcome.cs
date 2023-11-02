using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   
   public partial class FmWelcome : Form
   {
      private Config config = Config.GetConfig();

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

         tbIP.Text = "127.0.0.1";
         tbPort.Text = "8888";
         tbLogin.Text = "admin";
         tbPassw.Text = "admin";
         tbLoginDisp.Text = "disp1";
         tbPasswDisp.Text = "disp1";
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         DataModule.OnDataResponceError -= new EventDataResponseError(DataConnectionError);
         base.OnClosing(e);
      }

      //Событие окончания выборки
      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= DataLoaded;
         Invoke(new InvokeDelegate(Close));
      }

      //Ошибка при содединении
      private void DataConnectionError(EDataResponse e)
      {
         MessageBox.Show(e.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         btnConnect.Enabled = true;
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

         Division division = new Division();
         division.id = 1;
         division.name = "disp1";

         DsDivision dsDivision = new DsDivision(false);
         dsDivision.Add(division.id, division);

         DivisionManager divisionManager = new DivisionManager();
         divisionManager.login = tbLoginDisp.Text;
         divisionManager.password = tbPasswDisp.Text;
         divisionManager.division = division.id;

         DsDivisionManager dsDivisionManager = new DsDivisionManager(false);
         dsDivisionManager.Add(divisionManager.login, divisionManager);

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsDivision);
         list.Add(dsDivisionManager);

         if (DataModule.UpdateDataSet(list, null, null, config.GetConnection()))
         {
            MessageBox.Show("Данные успешно сохранены в базе данных, " +
               "вы всегда можете сменить данные для подклченя к серверу через меню Настройка");

            config.login = tbLoginDisp.Text;
            config.password = tbPasswDisp.Text;

            config.Save();
            Close();
         }
         else
         {
            MessageBox.Show("Ошибка записи в базу данных, " + 
               "проверьте запущени ли сервер, правильно ли введены логи и пароль администратора");
         }
      }

      //ОК, запоминаем сделанные изменения
      private void btnOK_Click(object sender, EventArgs e)
      {
         btnConnect_Click(null, null);
      }
   }
}
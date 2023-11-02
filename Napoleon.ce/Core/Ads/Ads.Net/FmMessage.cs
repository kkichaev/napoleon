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
   public partial class FmMessage : Form
   {
      private const int MAX_TEXT_LEN = 1000;
      private int charsRemain = MAX_TEXT_LEN;
      private DsMessage dsMessage;
      private Brigade brigade;

      // Constructor FmMessage
      public FmMessage(Brigade brigade)
      {
         InitializeComponent();
         AdjustForm();
         this.brigade = brigade;
         dsMessage = (DsMessage)DataModule.Get(Message.OBJECT_NAME) ?? new DsMessage(true);
      }

      //Настроить визуальные компоненты
      private void AdjustForm()
      {
         lbCharsRemain.Text = charsRemain.ToString();
         ToolTip tt = new ToolTip();
         tt.SetToolTip(lbCharsRemain, "Осталось символов");
         
      }

      //Открыть форму для посылки сообщения агенту
      public static void MessageShow(Brigade brigade)
      {
         FmMessage fmMessage = new FmMessage(brigade);
         fmMessage.lbAgent.Text = brigade.Name;
         fmMessage.Show();
      }

      //Если напечатано больше символов чем разрешено, то разрешаем только стирать
      private void tbMessage_KeyDown(object sender, KeyEventArgs e)
      {
         if (tbMessage.Text.Trim().Length > 0 && !e.SuppressKeyPress && e.KeyCode == Keys.Enter 
            && !(e.Modifiers == Keys.Control))
         {
            e.SuppressKeyPress = true;
            SendMessage();
         }

         if (e.KeyCode != Keys.Back)
         {
            e.SuppressKeyPress = charsRemain <= 0;
         }
      }

      //При изменении текста меняем значение/поведение значка "оставшихся символов"
      private void tbMessage_TextChanged(object sender, EventArgs e)
      {
         charsRemain = MAX_TEXT_LEN - tbMessage.Text.Trim().Length;
         lbCharsRemain.Text = charsRemain.ToString();

         if (charsRemain <= 0)
         {
            lbCharsRemain.Font = new Font(lbCharsRemain.Font, FontStyle.Bold);
            lbCharsRemain.ForeColor = Color.Red;
         }
         else
         {
            lbCharsRemain.Font = new Font(lbCharsRemain.Font, FontStyle.Regular);
            lbCharsRemain.ForeColor = Color.Black;
         }
      }

      //Отослать сообщение
      private void btnSend_Click(object sender, EventArgs e)
      {
         SendMessage();
      }

      //Сообщение записывается в базу данных
      private void SendMessage()
      {
         DataModule.SendMessage(tbMessage.Text, brigade.id, Config.GetConfig().GetConnection());

         AddMessageToHistory(tbMessage.Text);
         tbMessage.Clear();
      }

      //Добавить сообщение в историю
      private void AddMessageToHistory(string message)
      {
         tbHistory.Text = String.Format("{0}{3}{1}{3}{3}{2}", DateTime.Now.ToString(), message, tbHistory.Text,Environment.NewLine);
         tbMessage.Focus();
      }

      //Некоторая настройка визульных компонентов, когда форма получает фокус
      private void FmMessage_Activated(object sender, EventArgs e)
      {
         tbMessage.Focus();
      }

      /// <summary>
      /// История сообщений
      /// </summary>
      /// <param name="sender"> не используется</param>
      /// <param name="e">не используется</param>
      private void btnHistory_Click(object sender, EventArgs e)
      {
         FmMessageHistory.ShowModalForm(this, brigade, lbAgent.Text);
      }
   }
}
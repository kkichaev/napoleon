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
   public partial class FmMessage : Form
   {
      private const int MAX_TEXT_LEN = 1000;
      private int charsRemain = MAX_TEXT_LEN;
      private DataSet<int, Message> dsMessage;
      List<Division.DivisionAgent> agentsList = new List<Division.DivisionAgent>();

      // Constructor FmMessage
      public FmMessage(List<Division.DivisionAgent> agentsList)
      {
         this.agentsList = agentsList;
         InitializeComponent();
         AdjustForm();
         InitDataSets();
      }

      //Иницализация наборов данных
      private void InitDataSets()
      {
         dsMessage = DataModule.Get(Message.OBJECT_NAME) == null ? new DataSet<int, Message>(Message.OBJECT_NAME) :
            (DataSet<int, Message>)DataModule.Get(Message.OBJECT_NAME);
      }

      //Настроить визуальные компоненты
      private void AdjustForm()
      {
         lbCharsRemain.Text = charsRemain.ToString();
         ToolTip tt = new ToolTip();
         tt.SetToolTip(lbCharsRemain, "Осталось символов");
         
      }

      //Открыть форму для посылки сообщения агенту
      public static void MessageShow(Agent agent)
      {
         List<Division.DivisionAgent> alist = new List<Division.DivisionAgent>();
         Division.DivisionAgent da = new Division.DivisionAgent();
         da.agent = agent;
         alist.Add(da);
         FmMessage fmMessage = new FmMessage(alist);
         fmMessage.lbAgent.Text = agent.Name;
         fmMessage.ReadHistory();
         fmMessage.ShowDialog();
      }

      //Открыть форму для посылки сообщения подразделению
      public static void MessageShow(Division division)
      {
         FmMessage fmMessage = new FmMessage(division.GetAllAgents());
         fmMessage.lbAgent.Text = division.name;
         fmMessage.ReadHistory();
         fmMessage.ShowDialog();
      }
      
      //Читать историю сообщений
      public void ReadHistory()
      { 
      }

      //Если напечатано больше символов чем разрешено, то разрешаем только стирать
      private void tbMessage_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode != Keys.Back)
            e.SuppressKeyPress = charsRemain <= 0;
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
         foreach (Division.DivisionAgent divAgent in agentsList)
         {
            if (divAgent.agent == null || tbMessage.Text.Length == 0)
               continue;

            DataModule.SendMessage(tbMessage.Text, divAgent.agent.id, Config.GetConfig().GetConnection());
         }

         AddMessageToHistory(tbMessage.Text);
         tbMessage.Text = "";
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
         FmMessageHistory.ShowModalForm(this, agentsList, lbAgent.Text);
      }

      private void tbMessage_PreviewKeyDown(object sender, PreviewKeyDownEventArgs e)
      {
         if (tbMessage.Text.Trim().Length > 0 && e.KeyCode == Keys.Enter && !(e.Modifiers == Keys.Control))
            SendMessage();
      }
   }

   class SendTextBox : TextBox
   {
      protected override void OnKeyDown(KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter && !(e.Modifiers == Keys.Control))
            e.SuppressKeyPress = true;
         else
            base.OnKeyDown(e);
      }
   }
}
/*
 * Copyright (C), 2010, Гильдия разработчиков
 * 
 * История сообщений
 * 
 * kki   06/10/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmMessageHistory : Form
   {
      #region Public Methods
      /// <summary>
      /// Метод управления формой
      /// </summary>
      /// <param name="owner">владелец</param>
      /// <param name="agents">список агентов</param>
      /// <param name="caption">имя текущего агента/подразделения</param>
      public static void ShowModalForm(IWin32Window owner, List<Division.DivisionAgent> agents, string caption)
      {
         const string CAPTION_STR = "История сообщений для {0}";
         FmMessageHistory instance = new FmMessageHistory();
         instance.msgTarget = caption;
         instance.Text = String.Format(CAPTION_STR, instance.msgTarget);
         instance.agents = agents;
         instance.ShowDialog(owner);
      }
      #endregion

      #region Private Fields
      /// <summary>
      /// Набор данных сообщений
      /// </summary>
      private DataSet<int, MessageArchive> dsMessageArchive;

      /// <summary>
      /// Список агентов чью историю будет читать
      /// </summary>
      private List<Division.DivisionAgent> agents;

      /// <summary>
      /// Название объекта кому отправляются сообщения(агент, или название подразделения)
      /// </summary>
      private string msgTarget;

      /// <summary>
      /// Текущий период для которого был сделан запрос на выборку из базы данных
      /// </summary>
      private DatePeriod selPeriod;
      #endregion

      #region Private Methods
      /// <summary>
      /// Конструктор
      /// </summary>
      private FmMessageHistory()
      {
         InitializeComponent();
         InitComponents();
         InitDataSets();
      }

      /// <summary>
      /// Настройка визульных комопнентов формы
      /// </summary>
      private void InitComponents()
      {
         dtpBegin.Value = DateTime.Now.AddDays(-7);
         dtpEnd.Value = DateTime.Now.AddDays(1);

         selPeriod = new DatePeriod(dtpBegin.Value.Date, dtpEnd.Value.Date);
      }

      /// <summary>
      /// Инициализация наборов данных
      /// </summary>
      private void InitDataSets()
      {
         dsMessageArchive = (DataSet<int, MessageArchive>)DataModule.Get(MessageArchive.OBJECT_NAME)
            ?? new DataSet<int, MessageArchive>(MessageArchive.OBJECT_NAME);
      }

      /// <summary>
      /// Сформировать строчку для фильтра 
      /// </summary>
      /// <returns></returns>
      private string GetFilterString()
      {
         StoreDatePeriod();
         string filter = DataUtils.MakeDateLogDataFilter(DatePeriodBegin, DatePeriodEnd);
         const string FILTER_STR = "{0} and {1}";
         filter = String.Format(FILTER_STR, filter, UserIdIsStr(agents));

         return filter;
      }

      /// <summary>
      /// Дата начала выборки
      /// </summary>
      private DateTime DatePeriodBegin
      { 
         get { return selPeriod.From; }
      }

      /// <summary>
      /// Дата конца выборки
      /// </summary>
      private DateTime DatePeriodEnd
      {
         get { return selPeriod.Till; }
      }

      /// <summary>
      /// Сохранить период выборки в поле selPeriod
      /// </summary>
      private void StoreDatePeriod()
      {
         selPeriod = new DatePeriod(dtpBegin.Value.Date, dtpEnd.Value.Date);
      }

      //private DatePeriod selectedPeriod
      /// <summary>
      /// Формировать строку вида userid is ('id_agent1', 'id_agent2',....)
      /// </summary>
      /// <param name="agentList">Список агентов</param>
      /// <returns>сформированная строчка</returns>
      public static string UserIdIsStr(List<Division.DivisionAgent> agentList)
      {
         const string USERIDIS_STR = "\"userid\" in ({0})";
         const string USER_ID_ITEM_STR = "'{0}',";

         string result = string.Empty;

         foreach (Division.DivisionAgent agent in agentList)
         {
            if (agent.agent == null)
               continue;

            result += String.Format(USER_ID_ITEM_STR, agent.agent.id);
         }

         if (result.Length > 0)
         {
            result = result.Remove(result.Length - 1, 1);
            result = String.Format(USERIDIS_STR, result);
         }

         return result;
      }

      /// <summary>
      ///Очистить события выборки
      /// </summary>
      private void ClearRegisterDataModuleEvents()
      {
         DataModule.DataProcessed -= DataLoaded;
         DataModule.OnDataResponceError -= DataConnectionError;
      }

      /// <summary>
      /// Обновить набор данных
      /// </summary>
      private void RefreshDataSet()
      {
         dsMessageArchive.Filter = GetFilterString();

         DataModule.OnDataResponceError += DataConnectionError;
         DataModule.DataProcessed += DataLoaded;

         FmWait.ShowForm(this, DataModule.RefreshDataSet(dsMessageArchive, Config.GetConfig().GetConnection(),
            false, FmWait.ProgressIndicator));
      }

      /// <summary>
      /// Произошла ошибка в соединении
      /// </summary>
      /// <param name="e">Данные об ошибке</param>
      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         FmWait.CloseForm();

         const string MESSAGE_TEXT = "Ошибка : {0}";
         const string MESSAGE_CAPTION = "Ошибка";

         MessageBox.Show(this, String.Format(MESSAGE_TEXT, e.Msg), MESSAGE_CAPTION, MessageBoxButtons.OK,MessageBoxIcon.Error);
      }

      /// <summary>
      ///  Событие окончания выборки
      /// </summary>
      /// <param name="o">не используются</param>
      /// <param name="e">не используются</param>
      private void DataLoaded(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();

         BeginInvoke(new EmptyParamHandler(delegate {
            try
            {
               dgvArchiveMessage.SuspendLayout();
               dgvArchiveMessage.Rows.Clear();
               tbMessage.Text = string.Empty;
               Dictionary<DateTime, string> messages = new Dictionary<DateTime, string>();

               foreach (MessageArchive ma in dsMessageArchive.Data)
                  if (!messages.ContainsKey(ma.date))
                     messages.Add(ma.date, ma.message);

               foreach(KeyValuePair<DateTime, string>  kp in messages)
                  dgvArchiveMessage.Rows.Add(kp.Key.ToString(), kp.Value);
            }
            finally
            {
               tsbSave.Enabled = dgvArchiveMessage.RowCount > 0;
               dgvArchiveMessage.ResumeLayout();
            }
            FmWait.CloseForm();}));
      }

      /// <summary>
      /// Показать собщение в поле TextEdit
      /// </summary>
      private void ShowFullMessage()
      {
         tbMessage.Text = dgvArchiveMessage.CurrentRow.Cells[1].Value.ToString();
      }

      /// <summary>
      /// Щелчок на кнопке "Обновить"
      /// </summary>
      /// <param name="sender">не используется</param>
      /// <param name="e">не используется</param>
      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSet();
      }

      /// <summary>
      /// Событие изменение текущей строчки в таблице сообщений
      /// по этому событию обновляется поле TextEdit, которое содержит
      /// полную запись сообощения
      /// </summary>
      /// <param name="sender">не используется</param>
      /// <param name="e">не используется</param>
      private void dgvArchiveMessage_SelectionChanged(object sender, EventArgs e)
      {
         ShowFullMessage();
      }

      /// <summary>
      /// Событие щелчка на кнопке "Сохранить"
      /// </summary>
      /// <param name="sender">не используется</param>
      /// <param name="e">не используется</param>
      private void tsbSave_Click(object sender, EventArgs e)
      {
         SaveHistoryToFile();
      }

      /// <summary>
      /// Имя файла для сохранения истории
      /// </summary>
      private string FileName
      {
         get
         {
            const string DATE_MASK = "ddMMyyyy";
            return String.Format("{0}_{1}_{2}.txt", msgTarget, DatePeriodBegin.ToString(DATE_MASK),
               DatePeriodEnd.ToString(DATE_MASK));
         }
      }

      /// <summary>
      /// Запрос на перезепись файла с историей сообщений
      /// </summary>
      /// <returns>true - OK, иначе false</returns>
      private bool AskToReWriteHistoryFile()
      { 
         string MESSAGE_TEXT = "Файл {0} существует, перезаписать?";
         string MESSAGE_CAPTION = "Вопрос";

         return MessageBox.Show(this,
            String.Format(MESSAGE_TEXT, FileName), MESSAGE_CAPTION, MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK;
      }

      /// <summary>
      /// Сохранить содержимое таблицы сообщений в файле вида
      /// "имя цели сообщений"_"дата начала выборки"_"дата конца выборки"
      /// </summary>
      private void SaveHistoryToFile()
      {
         if (File.Exists(FileName))
            if (!AskToReWriteHistoryFile())
               return;

         try
         {
            TextWriter tw = new StreamWriter(FileName);
            foreach (DataGridViewRow row in dgvArchiveMessage.Rows)
               tw.WriteLine(String.Format("{0}\t{1}", row.Cells[0].Value, row.Cells[1].Value));
            tw.Flush();
            tw.Close();

            const string GOOD_RESULT = "Файл сохранен успешно";
            MessageBox.Show(GOOD_RESULT);
         }
         catch (IOException e)
         {
            const string MESSAGE_CAPTION = "Ошибка";
            MessageBox.Show(this, e.Message, MESSAGE_CAPTION, MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      /// <summary>
      /// Событие загрузки формы, обновить набор данных с условием по умолчанию
      /// период за неделю
      /// </summary>
      /// <param name="sender">не используется</param>
      /// <param name="e">не используется</param>
      private void FmMessageHistory_Load(object sender, EventArgs e)
      {
         RefreshDataSet();
      }
      #endregion
   }
}
using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using System.Windows.Forms;
using System.ComponentModel;
using System.Reflection;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class DecoratorFactory
   {
      public static IDecorator GetDecorator(Form form)
      {
         if (form.GetType() == typeof(MainForm))
            return new MainFormVoshodDecor((MainForm)form);

         if (form.GetType() == typeof(FmDetail))
            return new FmDetailVoshodDecor((FmDetail)form);

         return new EmptyDecorator();
      }

      public static IDecorator GetFmDetailDecorator(FmDetail form)
      {
         return new FmDetailVoshodDecor(form);
      }
   }

   class MainFormVoshodDecor : IDecorator
   {
      private MainForm form;
      private IReportFactory reportFactory;

      /* Addition UI */
      ToolStripMenuItem smiPlan = new ToolStripMenuItem("Планы...");
      ToolStripMenuItem smiRouteExcel = new ToolStripMenuItem("Экспорт маршрута в Excel");

      ToolStripButton btnPlan = new ToolStripButton("Планы...");

      public MainFormVoshodDecor(MainForm form)
      {
         this.form = form;

         reportFactory = ExcelReportFactory.CreateFactory();
         smiPlan.Click += new EventHandler(ShowPlans);
         btnPlan.Click += new EventHandler(ShowPlans);
      }

      void ShowPlans(object sender, EventArgs e)
      {
         FmAllPlans.ShowForm();
      }

      public void AdjustForm()
      {
         /* Настройка дополнительного UI */
         /* smiPlan */
         //smiPlan.Click += new EventHandler(smiPlan_Click);

         /* smiRouteExcel */
         smiRouteExcel.Click += new EventHandler(smiRouteExcel_Click);

         /* Настройка UI формы */
         /* menuAgentsSummary */
         form.menuAgentsSummary.Opening += new System.ComponentModel.CancelEventHandler(menuAgentsSummary_Opening);
         form.menuAgentsSummary.Items.Add(smiPlan);
         //form.menuAgentsSummary.Items.Add(smiRouteExcel);

         form.tsbConfig.Items.Add(btnPlan);
      }

      //private void smiPlan_Click(object sender, EventArgs e)
      //{
      //   FmPlan fmPlan = new FmPlan(
      //      form.GetSelectedAgent(), form.GetCurrentUser());
      //   fmPlan.Show();
      //}

/*
22/06/2017 Сейчас это не рабоатет = GetDsOrgFolder нету.
      private void smiRouteExcel_Click(object sender, EventArgs e)
      {
         form.BeginInvoke(new EmptyParamHandler(delegate
         {
            Division curDivision = form.GetSelectedDivision();
            DataSet<int, OrgFolder> dsOrgFolder = form.GetDsOrgFolder();

            List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = curDivision.GetAllAgents();

            UpdateDsForReport(ConvertDivisionAgentsListToDsAgents(agents), dsOrgFolder);
         }));
      }
*/

      private Agents ConvertDivisionAgentsListToDsAgents(List<GRSoft.NapoleonManager.Division.DivisionAgent> agents)
      {
         Agents result = new Agents(false);

         foreach (Division.DivisionAgent agent in agents)
         {
            try
            {
               result.Add(agent.AgentName, agent.agent);
            }
            catch {//Здесь могут быть какие то ошибки в загрузке, связанные
            //с ведением пользователем бызы данных, будем их игнорировать
            }
         }

         return result;
      }
      private void UpdateDsForReport(Agents agents, DataSet<int, OrgFolder> dsOrgFolder)
      {
         DsCommonOrgs commonOrg = DsCommonOrgs.GetCommonOrgs();
         commonOrg.Filter = DataUtils.MakeFilterFromAgents(null, agents);

         List<IDataSet> updSet = new List<IDataSet>();
         updSet.Add(commonOrg);
         updSet.Add(dsOrgFolder);

         DataModule.DataProcessed += new EventHandler(DataProcessed);
         DataModule.OnDataResponceError += new EventDataResponseError(OnDataResponceError);

         FmWait.ShowForm(form,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), updSet, FmWait.ProgressIndicator));
      }

      private void DataProcessed(object sender, EventArgs e)
      {
         ClearDataEvets();

         form.Invoke(new EmptyParamHandler(delegate
         {
            IReport rep = reportFactory.MakeRouteReport(
               form.GetDsOrgFolder(), 
               form.GetSelectedDivision().GetAllAgents(), 
               typeof(ConcreteExcelRouteReport));
            rep.Show();
            FmWait.CloseForm();
         }));
         
      }

      private void OnDataResponceError(EDataResponse e)
      {
         ClearDataEvets();
         FmWait.CloseForm();
      }

      private void ClearDataEvets()
      {
         DataModule.DataProcessed -= new EventHandler(DataProcessed);
         DataModule.OnDataResponceError -= new EventDataResponseError(OnDataResponceError);
      }

      private void menuAgentsSummary_Opening(object sender, CancelEventArgs e)
      {
         //smiPlan.Visible = form.IsCurrentRowForAgent() ? true : false;
         smiRouteExcel.Visible = true;
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         return false;
      }

   }

   class FmDetailVoshodDecor : IDecorator
   {
      private FmDetail form;

      public FmDetailVoshodDecor(FmDetail form)
      {
         this.form = form;
      }

      public void AdjustForm()
      {
        
      }

      public bool ExecFunction(FunctionArgsType args)
      {
         MethodInfo mi = this.GetType().GetMethod(args.FuncName, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance);

         if (mi == null)
            return false;

         args.RetVal = mi.Invoke(this, args.Args);

         return true;
      }

      private bool IsDocCompleted(DateTime date, GRSoft.Network.DataObject dataObject)
      {
         Order o = dataObject as Order;
         if (o != null)
         {
            return o.OutOfPlan || (o._params == 0x00004);
         }

         return true;
      }
   }

   /// <summary>
   /// Ошибка при генерации отчета
   /// </summary>
   class ECreateReportError : Exception
   {
      private Agent agent;

      public ECreateReportError(Agent agent, Exception innerException): 
         base(string.Empty, innerException)
      {
         this.agent = agent;
      }

      public Agent Agent { get { return agent; } }
   }

   /// <summary>
   /// Класс отчета по маршруту агента
   /// </summary>
   class ConcreteExcelRouteReport : ExcelRouteReport
   {
      #region Public Methods
      public ConcreteExcelRouteReport(DataSet<int, OrgFolder> dsOrgFolder,
         List<Division.DivisionAgent> agents)
         : base(dsOrgFolder, agents)
      { 
      }

      /// <summary>
      /// Вывести отчет
      /// </summary>
      public override void Show()
      {
         try
         {
            CreateReport();
            base.Show();
         }
         catch (ECreateReportError e)
         {
            const string MESSAGE_ERROR_STR = "В момент формирования отчета для агента\n\n {0}\n\nпроизошла ошибка:\n{1}";
            const string UNKNOWN_AGENT_STR = "Агент не определен";

            MessageBox.Show(String.Format(MESSAGE_ERROR_STR,
               e.Agent == null ? UNKNOWN_AGENT_STR : e.Agent.Name, e.ToString()));
         }
      }
      #endregion

      #region Private Methods
      /// <summary>
      /// Создать отчет
      /// </summary>
      private void CreateReport()
      {
         const int FISRT_SHEET_INDEX = 1;
         const int FIRST_COL_INDEX = 1;
         const int SECOND_COL_INDEX = 2;

         bool firstRow = true;
         int offset = 4;

         foreach (Division.DivisionAgent agent in AgentsList)
         {
            try
            {
               /*4.10.2010 kki
                * Если по каким либо причинам в
                * списке агент не привязан к агентам игнорируем это событие
                */
               if (agent.agent == null)
                  continue;

               int bottomRow = offset;
               int rigthCel = 0;

               object newSheet = firstRow
                  ? GetSheetByIndex(FISRT_SHEET_INDEX)
                  : AddSheet();

               MakeAgentsDaysGrid(offset, newSheet, agent, out bottomRow, out rigthCel);

               if (firstRow)
                  firstRow = false;

               SetBordersOnRange(offset, FIRST_COL_INDEX, bottomRow, rigthCel, xlContinuous);
               SetCellHorizontalAlign(offset, SECOND_COL_INDEX, bottomRow, rigthCel, xlLeft);
               SetCellVerticalAlign(offset, SECOND_COL_INDEX, bottomRow, rigthCel, xlTop);
               SetWrapeText(offset, SECOND_COL_INDEX, bottomRow, rigthCel, true);

               MakeHeader(agent.agent, rigthCel);
            }
            catch(Exception e)
            {
               throw new ECreateReportError(agent.agent, e);
            }
         }
      }

      /// <summary>
      /// Создать заголовок для листа
      /// </summary>
      /// <param name="curAgent">агент</param>
      /// <param name="right_col">номер правой колонки</param>
      private void MakeHeader(Agent curAgent, int right_col)
      {
         const string FIRST_ROW_STR = "ГРАФИК ПОСЕЩЕНИЙ ТОРГОВОГО ПРЕДСТАВИТЕЛЯ С \"____\" ПО \"_____\"________________________200     г.";
         const string SECOND_ROW_STR = "ФИО___________{0}______________________________________";
         const int FISRT_ROW_INDEX = 1;
         const int SECOND_ROW_INDEX = 2;
         const int FIRST_COLUMN_INDEX = 1;

         MergeCells(FISRT_ROW_INDEX, FIRST_COLUMN_INDEX, FISRT_ROW_INDEX, right_col);
         SetValue(FISRT_ROW_INDEX, FIRST_COLUMN_INDEX, FIRST_ROW_STR);
         SetCellHorizontalAlign(FISRT_ROW_INDEX, FIRST_COLUMN_INDEX, xlCenter);
         MergeCells(SECOND_ROW_INDEX, FIRST_COLUMN_INDEX, SECOND_ROW_INDEX, right_col);
         SetValue(SECOND_ROW_INDEX, FIRST_COLUMN_INDEX, String.Format(SECOND_ROW_STR, curAgent.Name));
         SetCellHorizontalAlign(SECOND_ROW_INDEX, FIRST_COLUMN_INDEX, xlCenter);
         SetCellBoldFont(SECOND_ROW_INDEX, FIRST_COLUMN_INDEX, true);
      }

      /// <summary>
      /// Создать таблицу маршрута для агента
      /// </summary>
      /// <param name="topRow">номер первой строки</param>
      /// <param name="newSheet">объект лист</param>
      /// <param name="agent">агент</param>
      /// <param name="bottomRow">номер нижней строки</param>
      /// <param name="rightCel">номер правой колонки</param>
      private void MakeAgentsDaysGrid(int topRow, object newSheet, Division.DivisionAgent agent,
         out int bottomRow, out int rightCel)
      {
         const string N_STR = "№";
         const double FIRST_COLUMN_WIDTH = 3.29;
         const double SECOND_COLUMN_WIDTH = 30.0;
         const int FIRST_COLUMN_INDEX = 1;

         bottomRow = topRow;
         rightCel = FIRST_COLUMN_INDEX;

         SetSheetName(newSheet, agent.AgentName);
         SetColumnWidth(rightCel, FIRST_COLUMN_WIDTH);
         SetValue(topRow, rightCel, N_STR);
         rightCel++;

         List<OrgFolder> grid = GetOrgFolderListForAgent(agent.agent, OrgFolderDataSet);

         foreach (OrgFolder of in grid)
         {
            int row = topRow;
            SetValue(row, rightCel, of.name);
            SetColumnWidth(rightCel, SECOND_COLUMN_WIDTH);
            row++;

            foreach (OrgFolderItem item in of.items)
            {
               SetValue(row, rightCel, item.org != null
                  ? item.org.Name
                  : item.name);
               row++;
            }

            if (row > bottomRow)
               bottomRow = row;

            rightCel++;
         }

         bottomRow--;
         rightCel--;

         SetCellItalicFont(topRow, FIRST_COLUMN_INDEX, topRow, rightCel, true);

         for (int i = topRow; i < bottomRow; i++)
            SetValue(i + 1, 1, (i - topRow + 1).ToString());

         SetCellVerticalAlign(topRow, FIRST_COLUMN_INDEX, bottomRow, FIRST_COLUMN_INDEX, xlCenter);
         SetCellHorizontalAlign(topRow, FIRST_COLUMN_INDEX, bottomRow, FIRST_COLUMN_INDEX, xlLeft);
      }

      /// <summary>
      /// Маршрут для агента
      /// </summary>
      /// <param name="agent">агент</param>
      /// <param name="dsOrgFolder">объект с данными</param>
      /// <returns>маршрут</returns>
      private List<OrgFolder> GetOrgFolderListForAgent(Agent agent, DataSet<int, OrgFolder> dsOrgFolder)
      {
         List<OrgFolder> result = new List<OrgFolder>();

         for (int i = 1; i <= 7; i++)
         {
            WeekDay wDay = new WeekDay(i);

            foreach (OrgFolder folder in dsOrgFolder.Data)
            {
               if (folder.agent.Equals(agent) && new WeekDay(folder.name).Equals(wDay))
               {
                  result.Add(folder);
               }
            }
         }

         return result;
      }
      #endregion
   }
}



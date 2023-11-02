using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using GRSoft.Network;
using System.Collections;
using System.Reflection;
using System.Threading;
using System.Globalization;
using GRSoft.UILib;
using System.Runtime.InteropServices;
using System.IO;
using GRSoft.NapoleonManager.Utils;
using System.Security.Cryptography.X509Certificates;

namespace GRSoft.NapoleonManager
{
   public partial class
      MainForm : Form
   {
      public static MainForm Instance;

      public static SynchronizationContext sync;

      bool convertRoteDone = false;

      //DataSets
      protected Agents dsAgents;
      protected DataSet<int, UserLog> dsUserLog = new DataSet<int, UserLog>("UserLog");

      // общие организации (файл ORGS) - может потребоваться в других местах
      //  23.09.2010 kki ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      //как стало ясно этогог файл может и не быть, тогда что бы получить список,   +
      //надо выбрать организации у всех агентов                                     +
      //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      //protected DataSet<string, Org> dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      protected DataSet<int, Order> dsOrder = new DataSet<int, Order>("Order", true, true);
      protected DataSet<int, OrderW> dsOrderW = new DataSet<int, OrderW>(OrderW.OBJECT_NAME);
      protected DataSet<int, OrgFolder> dsOrgFolder = new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
      protected DataSet<int, VisitInfo> dsVisit = new DataSet<int, VisitInfo>("VisitInfo");
      protected DataSet<int, OrgRemnants> dsOrgRemnants = new DataSet<int, OrgRemnants>("OrgRemnants");
      protected DataSet<string, UserInfo> dsUserInfo = new DataSet<string, UserInfo>(UserInfo.OBJECT_NAME);
      protected DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
      protected DataSet<string, PotenzialOrg> dsPtnzOrg = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);
      protected DataSet<string, UserActivity> dsUserActivity = new DataSet<string, UserActivity>(UserActivity.OBJECT_NAME);
      protected DataSet<int, Returns> dsReturns = new DataSet<int, Returns>(Returns.OBJECT_NAME);

      protected DataSet<int, PKO> dsPKO = new DataSet<int, PKO>(PKO.OBJECT_NAME, true, true);
      //protected DataSet<int, ScriptDoc> dsScriptDoc = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME);
      protected DataSet<int, Sales> dsSales = new DataSet<int, Sales>(Sales.OBJECT_NAME, true, true);
      protected DataSet<int, Incass> dsIncass = new DataSet<int, Incass>(Incass.OBJECT_NAME, true, true);
      protected SimpleDataSet<GPSGather> dsGather = new SimpleDataSet<GPSGather>(GPSGather.OBJECT_NAME);
      private DataSet<string, DivisionManager> dsAllManagers = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);

#if DISTR_DOC
      protected DataSet<int, Distr> dsDistr = new DataSet<int, Distr>(Distr.OBJECT_NAME);
#endif

#if PRICE_MONITORING
      protected DataSet<int, Monitoring> dsMonitoring = new DataSet<int, Monitoring>(Monitoring.OBJECT_NAME);
#endif
#if REPORT_INCLUDE_DELIVERIES
      protected DataSet<int, Delivery> dsDelivery = new DataSet<int, Delivery>(Delivery.OBJECT_NAME);
#endif

#if Sibtrade
      protected DataSet<int, Bonus> dsBonus = new DataSet<int, Bonus>(Bonus.OBJECT_NAME);
#endif

      AgentRouteSheduleHelper routeHelper = new AgentRouteSheduleHelper();

      protected EDataResponse dataResponceError;
      //Координаты текущей позиции указателя курсора, для того что бы показать окно инфо агента
      protected Point savedMousePopupPosition = new Point();

      public DBConnection conn;
      protected Employee currentUser;
      bool isReceiving = false;
      protected DataSet<int, OrderCommitted> dsOrderCommitted = new DataSet<int, OrderCommitted>(OrderCommitted.OBJECT_NAME);
#if MOVEMENT_DOC
      protected DataSet<int, MoveDoc> dsMove = new DataSet<int, MoveDoc>(MoveDoc.OBJECT_NAME);
#endif

#if QUESTION
      protected DataSet<int, Answer> dsAnswer = new DataSet<int, Answer>(Answer.OBJECT_NAME);
#endif

#if VAND_PROJECT
      protected DataSet<int, VandAudit> dsVandAudit = new DataSet<int, VandAudit>(VandAudit.OBJECT_NAME);
      protected DataSet<int, VandReload> dsVandReload = new DataSet<int, VandReload>(VandReload.OBJECT_NAME);
      protected DataSet<int, VandSales> dsVandSales = new DataSet<int, VandSales>(VandSales.OBJECT_NAME);
#endif

      Font boldCellsFont;
      protected ToolStripButton btnSavePhoto;
      protected ToolStripButton rttReport;
      protected Dictionary<int, List<DivisionManager>>  divisionManagers = new Dictionary<int, List<DivisionManager>>();

      protected void __Initing()
      {

         this.smiDetail.Click += new System.EventHandler(this.smiDetail_Click);
         this.smiRoute.Click += new System.EventHandler(this.smiRoute_Click);
         this.smiWriteMessage.Click += new System.EventHandler(this.smiWriteMessage_Click);
         this.smiInfo.Click += new System.EventHandler(this.smiInfo_Click);
         
         
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         this.btnDivision.Click += new System.EventHandler(this.btnDivision_Click);
         this.tsbMakeHtml.Click += new System.EventHandler(this.tsbMakeHtml_Click);
         this.tsbConfigBtn.Click += new System.EventHandler(this.tsbConfigBtn_Click);
         this.btnOrderReport.Click += new System.EventHandler(this.btnOrderReport_Click);
         this.btnCensus.Click += new System.EventHandler(this.btnCensus_Click);
         this.btnPriceRemnants.Click += new System.EventHandler(this.btnPriceRemnants_Click);
         this.btnGpsReport.Click += new System.EventHandler(this.btnGpsReport_Click);
         this.btnRouteAp.Click += new System.EventHandler(this.btnRouteAp_Click);
         this.btnUserLocation.Click += new System.EventHandler(this.btnUserLocation_Click);
         this.linkLabel1.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkLabel1_LinkClicked);
         this.cbConfig.SelectionChangeCommitted += new System.EventHandler(this.cbConfig_SelectionChangeCommitted);
         this.tgvAgentsSummary.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvAgentsSummary_CellFormatting);
         this.tgvAgentsSummary.DoubleClick += new System.EventHandler(this.tgvAgentsSummary_DoubleClick);
         this.tgvAgentsSummary.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tgvAgentsSummary_MouseDown);
         this.Load += new System.EventHandler(this.MainForm_Load);
         this.btnTask.Click += new System.EventHandler(this.btnTask_Click);
         this.menuAgentsSummary.Opening += new System.ComponentModel.CancelEventHandler(this.menuAgentsSummary_Opening);


#if TcarGrad || BeautyProfy
         DBConnection.TIMEOUT = 180 * 1000;
#endif

         Init();
         DecoratorFactory.GetDecorator(this).AdjustForm();

#if QUESTION_REPORT
         rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.excel;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчёт по анкетам";
         rttReport.Click += OnQuestReportPressed;
         tsbConfig.Items.Add(rttReport);
#endif

         btnSavePhoto = new System.Windows.Forms.ToolStripButton();
         btnSavePhoto.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnSavePhoto.Image = Properties.Resources.importpotorgl;
         btnSavePhoto.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnSavePhoto.Name = "rttReport";
         btnSavePhoto.Size = new System.Drawing.Size(23, 22);
         btnSavePhoto.Text = "Выгрузка фотографий";
         btnSavePhoto.Click += new System.EventHandler((o, e) =>
         {
            if (CheckIsMainDataPresents(false))
            {
               Type type = FormEntries.GetFormType(typeof(FmExportPhoto));
               ConstructorInfo ci = type.GetConstructor(Type.EmptyTypes);
               Form fm = (Form)ci.Invoke(new object[] { });
               fm.Show();
            }
            else
               MessageBox.Show("Необходимо нажать кнопку обновить в ");
         });
         tsbConfig.Items.Add(btnSavePhoto);


#if AGENT_ORG_TASK
         btnTask.Visible = true;
#elif DymovMoscow
         btnTask.Visible = true;
#else
         btnTask.Visible = false;
#endif
         TrySetProxy();
#if NO_CENSUS
         btnCensus.Visible = false;
#else
         btnCensus.Visible = true;
#endif
#if ROUTE_APPROVE
         btnRouteAp.Visible = true;
#endif
         ServicePointManager.ServerCertificateValidationCallback += AcceptAllCertifications;

#if SCRIPT_TIME_REPORT
         ToolStripButton btnScriptTimeReport = new System.Windows.Forms.ToolStripButton();
         btnScriptTimeReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnScriptTimeReport.Image = Properties.Resources.ic_av_timer;
         btnScriptTimeReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnScriptTimeReport.Name = "btnScriptTimeReport";
         btnScriptTimeReport.Size = new System.Drawing.Size(23, 22);
         btnScriptTimeReport.Text = "Oтчёт по времени пребывания в точке";
         btnScriptTimeReport.Click += new System.EventHandler((s, e) => { ScriptTimeRpt.Do(dtpBeginDate.Value.Date, dtpBeginDate.Value.AddDays(1), this); });

         tsbConfig.Items.Add(btnScriptTimeReport);
#endif

         //#if DEBUG
         //         dtpBeginDate.Value = new DateTime(2015, 10, 30);
         //#endif
      }

      static public bool AcceptAllCertifications(object sender, X509Certificate certificate, X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
      {
         return true;
      }


      protected virtual void OnQuestReportPressed(object sender, EventArgs args)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmQuestionReport));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         FmQuestionReport fm = (FmQuestionReport)ci.Invoke(new object[] { });

         fm.shortAddr = true;
         fm.Show();
      }


      protected void Init()
      {
//#if Vyatich
//         DataModule.AddAlias("ManagerPrice", "Price");
//#endif

#if Enoteka
         ToolStripMenuItem task = new ToolStripMenuItem();
         task.Name = "smiTask";
         task.Size = smiRoute.Size;
         task.Text = "Задачи...";
         task.Click += new EventHandler(task_Click);
         menuAgentsSummary.Items.Add(task);
#endif
#if Plans
         ToolStripMenuItem miPlans = new ToolStripMenuItem();
         miPlans.Text = "Планы...";
         miPlans.Click += new EventHandler(delegate(object sender, EventArgs e) {
            GRSoft.NapoleonManager.Variants.Plans.FmPlan.ShowInstance(GetSelectedAgent());
         });
         menuAgentsSummary.Items.Add(miPlans);
#endif

#if Vyatich
         this.btnPriceRemnants.Visible = true;
#elif PRESENTATION_EDITOR
         this.btnPriceRemnants.Visible = true;
#else
         this.btnPriceRemnants.Visible = false;
#endif
         sync = SynchronizationContext.Current;
         dtpBeginDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month,
            DateTime.Now.Day, 0, 0, 0);
         
         Instance = this;
      }

      void task_Click(object sender, EventArgs e)
      {
         Agent a = GetSelectedAgent();
         if (a != null)
            FmTask.Show(a);
      }

      protected void btnTask_Click(object sender, EventArgs e)
      {
#if AGENT_ORG_TASK
         Type agentTask = FormEntries.GetFormType(typeof(FmAgentTask));
         ConstructorInfo ci = agentTask.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
#endif
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         try
         {
            Config cfg = Config.GetConfig();
            DataModule.EndSession(Config.GetConfig().GetConnection(), true);

         }
         catch (Exception)
         {
         }
         base.OnClosing(e);
      }

      protected virtual void AddMainSets(List<IDataSet> upd) { }

      protected virtual void OnMainDataFetched(List<IDataSet> upd) { }

      //Получить из базы основные данные
      protected void FetchMainDataFromDB(DBConnection conn)
      {
         dataResponceError = null;

         DivisionList dsDivision = DivisionList.GetDataSet();

         List<IDataSet> refreshDataSets = new List<IDataSet>();
         dsAgents = Agents.GetDataSet();
         refreshDataSets.Add(dsAgents);
         refreshDataSets.Add(dsDivision);
         refreshDataSets.Add(dsConfig);

         AddMainSets(refreshDataSets);

         CurrentUser.InitCurrentUser(refreshDataSets, true);

         SimpleDataSet<OrgFolder> of = null;
         SimpleDataSet<OrgFolder> ofOld = null;

         Thread refreshThread = DataModule.RefreshGiveSets(conn, refreshDataSets, FmWait.ProgressIndicator);

         //FmWait.ShowForm(this, refreshThread);
         refreshThread.Join();

         OnMainDataFetched(refreshDataSets);

         if (Environment.CommandLine.Contains("/convert") && !convertRoteDone)
         {
            of = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);
            ofOld = new SimpleDataSet<OrgFolder>("OrgFolderOld", false);

            ofOld.Filter = DataUtils.MakeFilterFromAgents(null, dsAgents.Data);

            refreshDataSets.Clear();
            refreshDataSets.Add(ofOld);

            refreshThread = DataModule.RefreshGiveSets(conn, refreshDataSets, FmWait.ProgressIndicator);
            FmWait.ShowForm(this, refreshThread);
            refreshThread.Join();

            String message = "Конвертация маршрутов проведена";
            if (ofOld.Count > 0)
            {
               foreach (OrgFolder data in ofOld.Data)
               {
                  if (data.items.Count > 0)
                     of.Add(data);
               }

               List<IDataSet> wr = new List<IDataSet>(new IDataSet[] { of });
               if (DataModule.UpdateDataSet(wr, null, null, conn))
               {
                  convertRoteDone = true;
               }
               else
                  message = "Ошибка записи новых маршрутов";
            }
            else
               message = "Старые маршруты не найдены";
            MessageBox.Show(message);
         }


         dsDivision.CheckAgents();
      }

      //Произошла ошибка в соединении
      protected void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         dataResponceError = e;

         BeginInvoke(new InvokeDelegate(
            delegate
            {
               FmWait.CloseForm();
               btnRefresh.Enabled = true;
               MessageBox.Show(this, e.Msg, "Ошибка",MessageBoxButtons.OK,  MessageBoxIcon.Error);
            }));
      }

      //Очистить события выборки данных
      protected void ClearRegisterDataModuleEvents()
      {
         isReceiving = false;
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      //Проверка была ли ошибка соединения
      //После проверки текущая ошибка сбрасывается
      protected bool IsConnectionAlive()
      {
         bool result;
         result = dataResponceError == null ? true : false;


         if (!result)
         {
            FmWait.CloseForm();
            MessageBox.Show(dataResponceError.Msg);
         }

         dataResponceError = null;
         return result;
      }

      //Обновить наборы данных
      protected void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSet();
      }

      public bool CheckIsMainDataPresents(bool forceLoad)
      {
         if (!forceLoad)
         {
            if (Agents.GetDataSet().Count > 0 && CurrentUser.user is Manager)
               return true;
         }

         bool ret = false;
         dataResponceError = null;

         Config c = Config.GetConfig();
         if (!c.CheckLogin())
            return false;

         conn = c.GetConnection();
         conn.ReceiveTimeout = 20 * 60 * 1000;

         DataModule.OnDataResponceError += DataConnectionError;

         try
         {
            FetchMainDataFromDB(conn);
            DataModule.ClearEvents();
            //FmWait.CloseForm();

            if (dataResponceError != null)
               return false;

            CurrentUser.SetCurrentUser(true);
            currentUser = CurrentUser.user;

            if (!(currentUser is Manager))
            {
               FmWait.CloseForm();
               MessageBox.Show("Информация не доступна.\nТекущий пользователь не является менеджером.", "Ошибка",
                  MessageBoxButtons.OK, MessageBoxIcon.Stop);
            }
            else
            {
               Text = "Дела: подразделение " + currentUser.User.Name;
               ret = true;
            }
         }
         catch (Exception ex)
         {
            FmWait.CloseForm();
            string message;
            if (ex is ECantCreateUser || ex is EUserNotFound)
            {
               message = "Возможно, пользователь не менеджер или в программе отсутствуют подразделения";
            }
            else
            {
               if (ex.Message.Length > 0)
                  message = ex.Message;
               else
                  message = "Ошибка соединения, проверьте правильность ввода логина и пароля пользователя,\n" +
                                 "а так же наличие сетевого соединения";
            }

            MessageBox.Show(this, message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }

         return ret;
      }

      protected void RefreshDataSet()
      {
         if (!CheckIsMainDataPresents(true))
            return;

         divisionManagers = null;

         btnRefresh.Enabled = false;

         Config c = Config.GetConfig();
         conn = c.GetConnection();

         DataModule.OnDataResponceError += DataConnectionError;

         try
         {
            List<IDataSet> updSets = new List<IDataSet>();
            updSets.Add(dsOrgFolder);
            //updSets.Add(dsUserLog);
            updSets.Add(dsUserInfo);
            updSets.Add(dsOrder);
            updSets.Add(dsOrderW);
            updSets.Add(dsVisit);
            updSets.Add(dsOrgRemnants);
            updSets.Add(dsPKO);
            updSets.Add(dsConfig);
            updSets.Add(dsUserActivity);
            updSets.Add(dsPtnzOrg);
            updSets.Add(dsSales);
            updSets.Add(dsOrderCommitted);
            updSets.Add(dsReturns);
            updSets.Add(dsIncass);
            updSets.Add(dsGather);
            updSets.Add(dsAllManagers);

#if VAND_PROJECT
            updSets.Add(dsVandAudit);
            updSets.Add(dsVandReload);
            updSets.Add(dsVandSales);
#endif

#if DISTR_DOC
            updSets.Add(dsDistr);
#endif

#if PRICE_MONITORING
            updSets.Add(dsMonitoring);
#endif

#if Sibtrade
            updSets.Add(dsBonus);
#endif

#if MOVEMENT_DOC
            updSets.Add(dsMove);
#endif

#if QUESTION
            updSets.Add(dsAnswer);
#endif
#if WEIGHT_IN_TOTAL_REPORT
            DataSet<String, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
                  new DataSet<string, Price>(Price.OBJECT_NAME);
            if (dsPrice.Count == 0)
            {
               dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
               updSets.Insert(0, dsPrice);
            }
#endif

            //if (dsOrg.Count == 0)
            //   updSets.Add(dsOrg);

            tgvAgentsSummary.Focus(); // это для Commit выбора даты

#if ROUTE_HISTORY
            List<Agent> src = new List<Agent>();
            Manager m = CurrentUser.user as Manager;
            if(m != null)
            {
               foreach(Agent a in m.GetAgents().Data)
                  src.Add(a);
            }
            routeHelper.Update(updSets, src, GetStartDAte(), GetFinishDate());
#endif

            AddUpdateDataSet(updSets);

            AdjustFilterForDS(GetStartDate(), GetFinishDate());
            DataModule.DataProcessed += RefreshRetrieveComlete;
            isReceiving = true;
            FmWait.ShowForm(this, DataModule.RefreshGiveSets(conn, updSets, FmWait.ProgressIndicator));
         }
         catch (Exception ex)
         {
            isReceiving = false;
            FmWait.CloseForm();
            string message;
            if (ex is ECantCreateUser || ex is EUserNotFound)
            {
               message = "Возможно, пользователь не менеджер или в программе отсутствют подразделения";
            }
            else
            {
               if (ex.Message.Length > 0)
                  message = ex.Message;
               else
                  message = "Ошибка соединения, проверьте правильность ввода логина и пароля пользователя,\n" +
                                 "а так же наличие сетевого соединения";
            }

            MessageBox.Show(this, message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      protected virtual void AddUpdateDataSet(List<IDataSet> updSets) { }
      protected virtual void AfterRefreshData() { }

      protected string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy} 23:59:59')";
      //Настроить фильтры для наборов данных
      protected virtual void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         string agentFilter = DataUtils.MakeFilterFromAgents(null, dsAgents);

         dsOrgFolder.Filter = agentFilter;
         dsUserInfo.Filter = agentFilter;

         dsPtnzOrg.Filter = agentFilter;
         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";

         //будем выбирать общих контрагентов из объекта CommonOrgs
         //dsOrg.Filter = DataUtils.USERID_IS_NULL_STR;

         String crdFilter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd); ;
         dsOrder.Filter = crdFilter;
         dsOrderW.Filter = crdFilter;
         dsSales.Filter = crdFilter;
         dsIncass.Filter = crdFilter;
         dsGather.Filter = crdFilter;

#if DISTR_DOC
         dsDistr.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif

#if PRICE_MONITORING
         dsMonitoring.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif

#if Sibtrade
         dsBonus.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif

#if VAND_PROJECT
         dsVandAudit.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsVandReload.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsVandSales.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif

         dsVisit.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd);
         dsOrgRemnants.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "objDate", dateBegin, dateEnd);
         dsPKO.Filter = crdFilter;

#if ODBC_PLUGIN
         dsOrderCommitted.Filter = crdFilter;
#else
         dsOrderCommitted.Filter = agentFilter;
#endif

         //dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd);
         dsReturns.Filter = crdFilter;

#if MOVEMENT_DOC
         dsMove.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif

#if QUESTION
         dsAnswer.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
#endif
      }

      // Событие окончания выборки
      protected void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         AfterRefreshData();
         BeginInvoke(new InvokeDelegate(
            delegate
            {
               LoadTgvAgentSummary();
               FmWait.CloseForm();
               btnRefresh.Enabled = true;
            }));
      }

      void LoadExpanded(Dictionary<int, Boolean> expanded, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode tn in nodes)
         {
            Division d = tn.Tag as Division;
            if (d != null && tn.IsExpanded)
            {
               expanded.Add(d.id, true);
               if (tn.Nodes.Count > 0)
                  LoadExpanded(expanded, tn.Nodes);
            }
         }
      }

      void SetExpanded(Dictionary<int, Boolean> expanded, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode tn in nodes)
         {
            Division d = tn.Tag as Division;
            if (d != null && expanded.ContainsKey(d.id))
               try
               {
                  tgvAgentsSummary.ExpandNode(tn);
               }
               catch (Exception) { }

            if (tn.Nodes.Count > 0)
               SetExpanded(expanded, tn.Nodes);
         }
      }

      protected virtual DivisionSummary CreateDivisionSummary()
      {
         return DivisionSummary.Create(dsConfig);
      }

      //Загрузка таблицы 
      protected void LoadTgvAgentSummary()
      {
         Dictionary<int, Boolean> expanded = new Dictionary<int, Boolean>();
         LoadExpanded(expanded, tgvAgentsSummary.Nodes);

         tgvAgentsSummary.Nodes.Clear();
         tgvAgentsSummary.Rows.Clear();

         if (CurrentUser.user is Manager)
         {
            Manager manager = (CurrentUser.user as Manager);

            Type dsType = FormEntries.GetFormType(typeof(DivisionSummary));
            DivisionSummary ds = CreateDivisionSummary();
            ds.SetRouteHelper(routeHelper);

            SummaryDivisionData tn = ds.MakeDivisionSummary(manager.Division, GetStartDate(), GetRangeEndDate(), tgvAgentsSummaryProgres, tgvAgentsSummary);
            TreeGridNode node = tgvAgentsSummary.Nodes.AddDataItem(tn);

            foreach (DivisionManager d in GetDivisionManagers(manager.Division.id))
               node.Nodes.Add(d);

            foreach (TreeGridNode item in tn.Agents)
               node.Nodes.Add(item);
            node.Tag = manager.Division;

            List<SummaryDivisionData> childs = DoChildDivision(manager.Childs, node);
            foreach (SummaryDivisionData sdd in childs)
               tn.AddChildDivision(sdd);
            tn.ProgressImage = CreateProgressImage(tn.ProgressValue);
            node.DataItem = tn; // refresh data 

            AdjustParentNode(node);

            //Распахнуть список, если одно подразделение
            if (expanded.Count == 0)
            {
               if (tgvAgentsSummary.Rows.Count == 1)
               {
                  tgvAgentsSummary.ExpandNode(node);
               }
            }
            else
            {
               SetExpanded(expanded, tgvAgentsSummary.Nodes);
            }
         }
         else
         {
            //Мы не знаем пока что делать, если пользовател не менеджер
            throw new Exception("TO DO implement it");
         }
      }

      //Получить данные для подразделений(детей)
      protected List<SummaryDivisionData> DoChildDivision(List<Division> parent, TreeGridNode nodeParent)
      {
         List<SummaryDivisionData> res = new List<SummaryDivisionData>();

         DivisionSummary ds = CreateDivisionSummary();
         ds.SetRouteHelper(routeHelper);
         foreach (Division child in parent)
         {
            ds.Clear();
            SummaryDivisionData tn = ds.MakeDivisionSummary(child, GetStartDate(), GetRangeEndDate(), tgvAgentsSummaryProgres, tgvAgentsSummary);
            TreeGridNode childNode = nodeParent.Nodes.AddDataItem(tn);

            foreach (DivisionManager d in GetDivisionManagers(child.id))
               childNode.Nodes.Add(d);

            foreach (TreeGridNode item in tn.Agents)
               childNode.Nodes.Add(item);
            childNode.Tag = child;

            if (child.Childs.Count > 0)
            {
               List<SummaryDivisionData> chRes = DoChildDivision(child.Childs, childNode);
               foreach (SummaryDivisionData chData in chRes)
                  tn.AddChildDivision(chData);
               tn.ProgressImage = CreateProgressImage(tn.ProgressValue);
               childNode.DataItem = tn;
            }

            AdjustParentNode(childNode);
            res.Add(tn);
         }

         return res;
      }

      //Настройка для узла "Подразделения"
      protected void AdjustParentNode(TreeGridNode node)
      {
         if (boldCellsFont == null)
            boldCellsFont = new Font(tgvAgentsSummary.DefaultCellStyle.Font, FontStyle.Bold);
         node.DefaultCellStyle.Font = boldCellsFont;
      }

      public DataSet<int, Order> GetOrders() { return dsOrder; }

      

      //Возвтратить имя организации по дате лога
      protected string FindOrgFromDateAction(DateTime dateAction)
      {
         foreach (Order order in dsOrder.Data)
         {
            if (order.Created == dateAction)
            {
               return order.OrgName;
            }
         }

         foreach (VisitInfo visit in dsVisit.Data)
         {
            if (visit.date == dateAction)
            {
               return visit.OrgName;
            }
         }

         foreach (OrgRemnants remnants in dsOrgRemnants.Data)
         {
            if (remnants.date == dateAction)
            {
               return remnants.OrgName;
            }
         }

         return string.Empty;
      }

      protected override void OnKeyDown(KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
         {
            ShowDetail();
            e.Handled = true;
         }
         else if (e.KeyCode == Keys.F5 && e.Modifiers == Keys.None)
         {
            RefreshDataSet();
            e.Handled = true;
         }
         base.OnKeyDown(e);
      }

      ////Создает картинку что будет представлена в таблица, как индикатор процесса
      Image CreateProgressImage(double progress)
      {
         return ProgressImage.CreateProgressImage(progress, tgvAgentsSummaryProgres);
      }

      //Показать форму "Подробно"
      protected void smiDetail_Click(object sender, EventArgs e)
      {
         ShowDetail();
      }

#if SCRIPT_DOC
      protected bool CanScripting(string agentID)
      {
         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.ALLOW_SCRIPTING, agentID);
         return (cc == null) ? false : (int.Parse(cc.value) > 0);
      }
#endif

      //Показать форму "Подробно"
      protected virtual void ShowDetail()
      {
         TreeGridNode cr = tgvAgentsSummary.CurrentRow;

         if (cr.Cells.Count > 0 && cr.Cells[0].Value is DivisionManager)
         {
            if (!WebViewWarning.IsWebViewExists())
            {
               WebViewWarning.Open();
               return;
            }

            DivisionManager d = cr.Cells[0].Value as DivisionManager;
            Type prcType = FormEntries.GetFormType(typeof(FmRoute));
            ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
            FmRoute route = (FmRoute)ci.Invoke(new object[] { d.login, GetStartDate() });
            route.SetManagerMode(d);
            route.Show();
         }
         SummaryData sdi = (cr == null) ? null : cr.DataItem as SummaryData;
         if (cr == null || sdi == null)
         {
            return;
         }

         DataModule.DataProcessed -= RefreshRetrieveComlete;

         FmDetailData data = new FmDetailData(sdi.AgentID,
            GetStartDate(), GetFinishDate(), null);

         FmDetail detail = FormEntries.OpenDetailForm(data);
#if SCRIPT_DOC
         if (CanScripting(data.AgentId))
            detail.SetScriptMode(true);
#endif
         detail.Show();
      }

      //Форма управления подразделениями
      protected void btnDivision_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(Divisions));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      //Форма "Редактирование маршрута..."
      public void smiRoute_Click(object sender, EventArgs e)
      {
         Agent a = GetSelectedAgent();
         if (a != null)
         {
            if (!WebViewWarning.IsWebViewExists())
            {
               WebViewWarning.Open();
               return;
            }
            Route.Show(GetSelectedAgent());
         }
      }

      //Установит "видимость" всех элементов popup меню
      protected void ShowAllPopupMenuAgentsSummary(bool visible)
      {
         foreach (ToolStripItem tsi in menuAgentsSummary.Items)
         {
            tsi.Visible = IsMenuItemVisible(tsi) && visible;
         }
      }

      protected virtual Boolean IsMenuItemVisible(ToolStripItem menu) 
      {
#if NO_ROUTE_EDITOR
         if (menu == smiRoute)
            return false;
#endif
         return true; 
      }

      public bool IsCurrentRowForAgent()
      {
         return tgvAgentsSummary.CurrentRow.DataItem is SummaryData;
      }

      //Управляем видимостью меню если есть записи, то меню будет видно, иначе спрятать
      protected void menuAgentsSummary_Opening(object sender, CancelEventArgs e)
      {
         e.Cancel = true;
         ShowAllPopupMenuAgentsSummary(false);

         if (tgvAgentsSummary.CurrentRow == null)
         {
            return;
         }

         //Щелчок на подразделение, код агента не установлен
         //активно только пункт меню написать сообщение
         if (!IsCurrentRowForAgent())
         {
            smiWriteMessage.Visible = true;
         }
         // иначе для агента показываем все доступные пункты
         else
         {
            ShowAllPopupMenuAgentsSummary(true);
         }

         e.Cancel = false;
      }

      //Всплывающее окно с информацией о Агенте
      class UserInfoInPoup : Form
      {
         protected Label lbAgent = new Label();
         protected Label lbPhone = new Label();

         public UserInfoInPoup()
         {
            Font = new Font("Arial", 8.25f, FontStyle.Regular);
            TopMost = true;
            BackColor = Color.Lime;
            FormBorderStyle = FormBorderStyle.None;

            lbAgent.SetBounds(10, 10, 200, 15);
            lbPhone.SetBounds(10, 30, 200, 15);

            StartPosition = FormStartPosition.Manual;
            Size = new Size(200, 50);

            this.Controls.Add(lbAgent);
            this.Controls.Add(lbPhone);

            MouseDown += new MouseEventHandler(UserInfoInPoup_MouseDown);
            lbAgent.MouseDown += new MouseEventHandler(UserInfoInPoup_MouseDown);
            lbPhone.MouseDown += new MouseEventHandler(UserInfoInPoup_MouseDown);
         }

         void UserInfoInPoup_MouseDown(object sender, MouseEventArgs e)
         {
            Close();
         }

         public void Show(Point point, Agent agent, UserInfo userInfo)
         {
            Location = point;
#if Servolux
            lbAgent.Text = "Агент: " + "(" + agent.id + ") " + agent.name;
#else
            lbAgent.Text = "Агент: " + agent.name;
#endif

            if (userInfo != null)
            {
               lbPhone.Text = "Телефон: " + userInfo.phone;

               if (userInfo.phone.Trim().Length > 0)
                  Clipboard.SetText(userInfo.phone);
            }

            Show();
         }
      }

      //Установить заговолок формы в соответсвии с текущим пользователем
      protected void SetFormCaptionWithCurrUser(Agent a)
      {
         const string CAPTION = "Дела: пользователь {0}";

         Text = String.Format(CAPTION, a.name);
      }

      

      //Открыть страницу www.grsoft.ru в браузере
      protected void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         OpenLink.NewWindow("www.grsoft.ru");
      }

      //Двойной клик открывает форму подробно
      protected void tgvAgentsSummary_DoubleClick(object sender, EventArgs e)
      {
         ShowDetail();
      }

      //Написать сообщение
      protected void smiWriteMessage_Click(object sender, EventArgs e)
      {
         if (tgvAgentsSummary.CurrentRow.Cells[0].Value == null)
            return;

         if (GetSelectedAgent() != null)
            FmMessage.MessageShow(GetSelectedAgent());
         else
            FmMessage.MessageShow(GetSelectedDivision());
      }

      //Получить подразделение из узла таблицы
      public Division GetCurrentDivision(TreeGridNode node)
      {
         if (node == null)
            return null;

         SummaryDivisionData sdd = node.DataItem as SummaryDivisionData;
         return sdd == null ? GetCurrentDivision(node.Parent) : sdd.Division;
      }

      //Получить текушее подразделение или null
      public Division GetSelectedDivision()
      {
         return GetCurrentDivision(tgvAgentsSummary.CurrentRow);
      }

      //Получить текущего агента или null
      public Agent GetSelectedAgent()
      {
         if (tgvAgentsSummary.CurrentRow == null)
            return null;

         SummaryData sad = tgvAgentsSummary.CurrentRow.DataItem as SummaryData;
         return sad == null ? null : sad.Agent;
      }

      //Информация о текущем пользователе
      protected void smiInfo_Click(object sender, EventArgs e)
      {
         UserInfo userInfo = null;
         if (dsUserInfo.ContainsKey(GetSelectedAgent().id))
         {
            userInfo = dsUserInfo[GetSelectedAgent().id];
         }

         new UserInfoInPoup().Show(tgvAgentsSummary.PointToScreen(savedMousePopupPosition), GetSelectedAgent(), userInfo);
      }

      //Открыть отчет в HTML
      protected virtual void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         SummaryReport();
      }

      protected virtual void SummaryReport()
      {
         if (tgvAgentsSummary.Nodes.Count > 0)
         {
            Type rptType = FormEntries.GetFormType(typeof(HtmlReport));
            ConstructorInfo ci = rptType.GetConstructor(Type.EmptyTypes);
            HtmlReport htmlReport = (HtmlReport)ci.Invoke(new object[] { });

            OpenLink.NewWindow(String.Format("\"{0}\"", htmlReport.makeAgentSummaryFileInfo(tgvAgentsSummary,
               new TimeInterval(GetStartDate(), GetFinishDate()))));
         }
         else
         {
            MessageBox.Show("Нет данных для отчета", "Внимание", MessageBoxButtons.OK, MessageBoxIcon.Warning);
         }
      }

      //Выделить текущую запись в таблице по щелчку правой кнопки мышки
      protected void tgvAgentsSummary_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = tgvAgentsSummary.HitTest(e.X, e.Y);

            if (info.ColumnIndex == -1 || info.RowIndex == -1)
            {
               return;
            }

            savedMousePopupPosition.X = e.X;
            savedMousePopupPosition.Y = e.Y;

            tgvAgentsSummary.CurrentCell = tgvAgentsSummary[info.ColumnIndex, info.RowIndex];
         }
      }

      void TrySetProxy()
      {
         try
         {
            WinInetInterop.RestoreSystemProxy();
            if (Config.GetConfig().proxyIP.Length > 0)
            {
               string s = Config.GetConfig().proxyIP + ":" + Config.GetConfig().proxyPort.ToString();
               WinInetInterop.SetConnectionProxy(s);
            }
         }
         catch (Exception)
         {
         }
      }

      protected void tsbConfigBtn_Click(object sender, EventArgs e)
      {
         DBConnection prevConn = Config.GetConfig().GetConnection();

         if (FmConfig.OpenConfig(this) == DialogResult.OK)
         {
#if CONFIG_HISTORY
            SelectCurrentConfig();
#endif
            TrySetProxy();
            Text = "Дела";

            Config newCfg = Config.GetConfig();
            if (prevConn.login.Length > 0 && (prevConn.login != newCfg.login || prevConn.ip != newCfg.ip))
            {
               DataModule.EndSession(prevConn, false);
               tgvAgentsSummary.Nodes.Clear();
            }

            RefreshDataSet();
         };
      }

      protected void MainForm_Load(object sender, EventArgs e)
      {
         SetVersionText();

         //if (!Config.Exist())
         //{
         //   DialogResult dr = new FmWelcome().ShowDialog();
         //   if (dr != DialogResult.OK)
         //   {
         //      Close();
         //   }
         //}
#if CONFIG_HISTORY
         SelectCurrentConfig();
         cbConfig.Visible = true;
#else
         cbConfig.Visible = false;
#endif
      }

      protected void SelectCurrentConfig()
      {
         cbConfig.Items.Clear();

         foreach (Config cfg in ConfigHistory.Instance(false).config)
         {
            cbConfig.Items.Add(cfg);
         }

         cbConfig.Sorted = true;

         Config config = Config.GetConfig();
         for (int i = 0; i < cbConfig.Items.Count; i++)
         {
            if (((Config)cbConfig.Items[i]).name.Equals(config.name))
            {
               Config cfg = (Config)cbConfig.Items[i];
               Config.SetInstance(cfg);
               cfg.GetConnection().SetNewSession(Config.PDTFileName(cfg.name));

               cbConfig.SelectedIndex = i;
               break;
            }
         }
      }

      protected void SetVersionText()
      {
         Assembly a = Assembly.GetExecutingAssembly();
         string version = ((AssemblyFileVersionAttribute)Attribute.GetCustomAttribute(a, typeof(AssemblyFileVersionAttribute), false)).Version;
         string product = ((AssemblyProductAttribute)Attribute.GetCustomAttribute(a, typeof(AssemblyProductAttribute), false)).Product;

         StringBuilder sb = new StringBuilder();
         sb.Append("версия: ").Append(version);
         sb.Append(" / ").Append(File.GetLastWriteTime(a.GetModules()[0].FullyQualifiedName).ToShortDateString());
         sb.Append(" проект: ").Append(product);

         lbVersion.Text = sb.ToString();
      }

      public DateTime GetBeginDateForSelection()
      {
         return GetStartDate();
      }

      public Employee GetCurrentUser()
      {
         return currentUser;
      }

      virtual protected bool IsPotenzialOrgOutOfPlan(Agent a)
      {
         bool ret = false;
         if (!isReceiving)
         {
            Dictionary<string, bool> routeOrgs = null;
            foreach (Org o in dsPtnzOrg.Data)
            {
               if (o.agent == a)
               {
                  if (routeOrgs == null)
                  {
#if ROUTE_HISTORY
                     routeOrgs = routeHelper.AgentOrgs(a.id);
#else
                     routeOrgs = new Dictionary<string, bool>();

                     foreach (OrgFolder of in dsOrgFolder.Data)
                     {
                        if (of.agent != null && of.agent.id == a.id && of.items != null)
                        {
                           foreach (OrgFolderItem item in of.items)
                              routeOrgs[item.name] = true;
                        }
                     }
#endif
                  }
                  if (routeOrgs.ContainsKey(o.id) == false)
                  {
                     ret = true;
                     break;
                  }
               }
            }
         }
         return ret;
      }

      protected void tgvAgentsSummary_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         CellFormatting(e);
      }

      protected virtual void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode crow = (TreeGridNode)tgvAgentsSummary.Rows[e.RowIndex];
         SummaryData sad = crow.DataItem as SummaryData;
         if (sad != null)
         {
            if (Config.GetConfig().highliteOrderMissed && sad.HasMissedOrders == true)
               e.CellStyle.BackColor = Color.Orange;

            if (IsPotenzialOrgOutOfPlan(sad.Agent))
               e.CellStyle.ForeColor = Color.Red;
            return;
         }

         SummaryDivisionData sdd = crow.DataItem as SummaryDivisionData;
         if (sdd != null)
         {
            if (Config.GetConfig().highliteOrderMissed && sdd.HasMissedOrders == true)
               e.CellStyle.BackColor = Color.Orange;
            else
               e.CellStyle.BackColor = Color.FromArgb(221, 225, 236);
         }

         if (crow.Cells.Count > 1 && crow.Cells[0].Value is DivisionManager)
         {
            e.CellStyle.ForeColor = Color.Blue;
            e.CellStyle.Font = new System.Drawing.Font(e.CellStyle.Font, FontStyle.Bold);
         }
      }

      //Отчет по заявкам
      protected void btnOrderReport_Click(object sender, EventArgs e)
      {
         Division division = GetSelectedDivision();

         if (division != null)
            FmOrdersReport.ShowInstance(division);
      }

      protected void btnCensus_Click(object sender, EventArgs e)
      {
#if NO_CENSUS
#else
         if (CheckIsMainDataPresents(false))
         {
            FmCensus form = FormEntries.OpenCensusForm();
            form.From = GetStartDate();
            form.Till = GetFinishDate();
            form.Show();
         }
#endif
      }

      protected void btnPriceRemnants_Click(object sender, EventArgs e)
      {
#if PRICE_PHOTO_VIEW
         Type prcType = FormEntries.GetFormType(typeof(FmPricePhoto));
#else
         Type prcType = FormEntries.GetFormType(typeof(FmPrice));
#endif
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      protected void toolStripButton1_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmQuestionReport));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      protected void btnGpsReport_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmGPSReport));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      protected void cbConfig_SelectionChangeCommitted(object sender, EventArgs e)
      {
         if (((ComboBox)sender).SelectedItem != null)
         {
            Config cfg = ((ComboBox)sender).SelectedItem as Config;

            if (cfg != null)
            {
               DataModule.EndSession(Config.GetConfig().GetConnection(), false);
               Config.SetInstance(cfg);
               DataModule.ClearDataSets();
               CurrentUser.Clear();
               cfg.GetConnection().SetNewSession(Config.PDTFileName(cfg.name));
            }
         }
      }

      private void btnRouteAp_Click(object sender, EventArgs e)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmRouteApproval));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }

      private void btnUserLocation_Click(object sender, EventArgs e)
      {
         if (!WebViewWarning.IsWebViewExists())
         {
            WebViewWarning.Open();
            return;
         }
         new FmUserLocation().Show();
      }

      List<DivisionManager> GetDivisionManagers(int id)
      {
         if (divisionManagers == null)
         {
            divisionManagers = new Dictionary<int, List<DivisionManager>>();

            foreach (DivisionManager m in dsAllManagers.Data)
            {
               if (!divisionManagers.ContainsKey(m.division))
                  divisionManagers[m.division] = new List<DivisionManager>();

               if (m.Mobile)
                  divisionManagers[m.division].Add(m);
            }
         }

         return divisionManagers.ContainsKey(id) ? divisionManagers[id] : new List<DivisionManager>();
      }
   }

   class RouteSheduleKey : IComparable<RouteSheduleKey>
   {
      public WeekDay wd;
      public DateTime date;

      public RouteSheduleKey(DateTime date, WeekDay wd)
      {
         this.date = date;
         this.wd = wd;
      }

      public override int GetHashCode()
      {
         String value = date.ToShortDateString() + wd.FullName;
         return value.GetHashCode();
      }

      public override bool Equals(object obj)
      {
         RouteSheduleKey k = obj as RouteSheduleKey;
         return k == null ? false : wd.Equals(k.wd) && date.Equals(k.date);
      }

      public int CompareTo(RouteSheduleKey other)
      {
         int cmp = date.CompareTo(other.date);
         return cmp != 0 ? cmp : wd.Number - other.wd.Number;
      }
   }

   public class AgentRouteSheduleHelper
   {
      SimpleDataSet<OrgRouteShedule> src = new SimpleDataSet<OrgRouteShedule>(OrgRouteShedule.ROUTE_INTERVAL_NAME, false);

      Dictionary<string, Dictionary<WeekDay, List<SheduleData>>> data = null;
      public AgentRouteSheduleHelper()
      {
      }

      public Dictionary<string, bool> AgentOrgs(string userid)
      {
         Dictionary<string, bool> ret = new Dictionary<string, bool>();
         if (data == null)
            UpdateData();

         if (data.ContainsKey(userid))
         {
            foreach (Dictionary<WeekDay, List<SheduleData>> dataval in data.Values)
               foreach (List<SheduleData> shval in dataval.Values)
                  foreach (SheduleData sd in shval)
                     sd.LoadOrgs(ret);
         }
         return ret;
      }

      public void Update(List<IDataSet> upd, Agent agent, DateTime start, DateTime finish)
      {
         List<Agent> agents = new List<Agent>();
         agents.Add(agent);
         Update(upd, agents, start, finish);
      }

      public void Update(List<IDataSet> upd, List<Agent> agents, DateTime start, DateTime finish)
      {
         string uids = "";
         foreach (Agent a in agents)
            uids += "'" + a.id + "',";

         src.Filter = String.Format("{0};'{1:dd/MM/yyyy}';'{2:dd/MM/yyyy}'", uids.TrimEnd(new char[] { ',' }), start.Date, finish.Date);
         data = null;
         upd.Add(src);
      }

      void UpdateData()
      {
         data = new Dictionary<string, Dictionary<WeekDay, List<SheduleData>>>();
         foreach (OrgRouteShedule i in src.Data)
         {
            if (WeekDay.CheckDay(i.name) == false)
               continue;

            Dictionary<WeekDay, List<SheduleData>> adata = null;
            if (data.ContainsKey(i.userid))
               adata = data[i.userid];
            else
            {
               adata = new Dictionary<WeekDay, List<SheduleData>>();
               data[i.userid] = adata;
            }

            WeekDay wd = new WeekDay(i.name);
            List<SheduleData> shlist = null;
            if (adata.ContainsKey(wd))
               shlist = adata[wd];
            else
            {
               shlist = new List<SheduleData>();
               adata[wd] = shlist;
            }
            bool added = false;
            foreach (SheduleData sd in shlist)
            {
               if (sd.DateFrom.CompareTo(i.dateFrom) == 0)
               {
                  added = true;
                  sd.Add(i);
               }
            }
            if (!added)
               shlist.Add(new SheduleData(i));
         }
      }

      public List<OrgFolderItem> GetRoute(Agent a, DateTime date)
      {
         List<OrgFolderItem> ret = new List<OrgFolderItem>();
         if (data == null)
            UpdateData();

         date = date.Date;
         WeekDay wd = new WeekDay(date.DayOfWeek);
         if (data.ContainsKey(a.id))
         {
            Dictionary<WeekDay, List<SheduleData>> agentdata = data[a.id];
            if (agentdata.ContainsKey(wd))
            {
               SheduleData current = null;
               List<SheduleData> src = agentdata[wd];
               foreach (SheduleData sd in src)
               {
                  if (sd.DateFrom.CompareTo(date) > 0)
                     break;
                  current = sd;
               }
               if (current != null)
               {
                  current.LoadRoute(ret, date);
               }
            }
         }
         return ret;
      }

      class SheduleData : IComparable<SheduleData>
      {
         RouteSheduleKey key;
         List<OrgRouteShedule> route = new List<OrgRouteShedule>();

         public SheduleData(OrgRouteShedule src)
         {
            key = new RouteSheduleKey(src.dateFrom, new WeekDay(src.name));
            route.Add(src);
         }

         public DateTime DateFrom { get { return key.date; } }

         public int CompareTo(SheduleData other) { return key.CompareTo(other.key); }
         public override int GetHashCode() { return key.GetHashCode(); }

         public override bool Equals(object obj)
         {
            SheduleData sd = obj as SheduleData;
            return sd == null ? false : key.Equals(sd.key);
         }

         internal void LoadRoute(List<OrgFolderItem> ret, DateTime date)
         {
            OrgRouteShedule src = route[0];

            if (route.Count > 1)
            {
               int currentWeek = 1;
               TimeSpan ts = new TimeSpan(date.Ticks);
               ts = ts.Subtract(new TimeSpan(key.date.Ticks));
               if (ts.TotalDays >= 0)
                  currentWeek = (int)(ts.TotalDays / 7) % 4 + 1;

               foreach (OrgRouteShedule i in route)
                  if (i.WeekIndex == currentWeek)
                  {
                     src = i;
                     break;
                  }
            }

            src.items.ForEach(x => ret.Add(x));
         }

         internal void Add(OrgRouteShedule i) { route.Add(i); }

         internal void LoadOrgs(Dictionary<string, bool> ret)
         {
            foreach (OrgRouteShedule i in route)
            {
               foreach (OrgFolderItem ofi in i.items)
                  ret[ofi.name] = true;
            }
         }
      }
   }

   public class SummaryData
   {
      protected Agent agent;
      public int orders = 0;
      public double sum = 0;
      public double plan = 0;

      public int pko = 0;
      public int remnants = 0;
      protected Dictionary<string, int> uniqOrgs = new Dictionary<string, int>();
      protected Dictionary<string, int> uniqOrder = new Dictionary<string, int>();

      protected DataSet<int, CommonConfig> dsConfig;
      public bool hasMissedOrder = false;

      protected Image progressImage;
      protected string lastAccess;

      protected AgentRouteSheduleHelper routeHelper = null;


      public string Name { get { return agent.Name; } }
      public string AgentID { get { return agent.id; } }
      public Agent Agent { get { return agent; } }
      public Image ProgressImage { get { return progressImage; } set { progressImage = value; } }
      public int Visits { get { return GetVisitCount(); } }
      public int Orders { get { return GetOrders(); } }
      public double DocSum { get { return GetSum(); } }
      public virtual double ProgressValue { get { return plan; } }
      public virtual string LastAccess { get { return lastAccess; } set { lastAccess = value; } }
      public bool HasMissedOrders { get { return hasMissedOrder; } }
      public int UniqOrders { get { return GetUniqueOrderCount(); } }

      public SummaryData(Agent agent, DataSet<int, CommonConfig> dsConfig)
      {
         this.agent = agent;
         this.dsConfig = dsConfig;
      }

      public static SummaryData Create(Agent agent, DataSet<int, CommonConfig> dsConfig)
      {
         Type prcType = FormEntries.GetFormType(typeof(SummaryData));
         ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(Agent), typeof(DataSet<int, CommonConfig>) });
         return (SummaryData)ci.Invoke(new object[] { agent, dsConfig });
      }

      public void SetRouteHelper(AgentRouteSheduleHelper routeHelper) { this.routeHelper = routeHelper; }

      protected string MakeOrderKey(string id, DateTime date) { return String.Format("{0} {1}", date.Date.ToString("dd.MM.yyyy"), id); }

      public void AddOrder(string id, string userid, DateTime date)
      {
         string date_id_org_string = MakeOrderKey(id, date); // String.Format("{0} {1}", date.Date.ToString("dd.MM.yyyy"), id);

         if (uniqOrder.ContainsKey(date_id_org_string) == false)
            uniqOrder.Add(date_id_org_string, 1);
      }

      public void AddOrg(string id, string userid, DateTime date)
      {
         string date_id_org_string = MakeDateIdOrgString(id, date);

         if (uniqOrgs.ContainsKey(date_id_org_string) == false)
            uniqOrgs.Add(date_id_org_string, 1);
         else
         {
            CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, userid) ??
               ConfigUtils.GetCommonConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG);


            if (cc != null && cc.value.Equals(id))
            {
               uniqOrgs[date_id_org_string]++;
            }
         }
      }

      protected static string MakeDateIdOrgString(string id, DateTime date)
      {
         const String DATE_ID_ORG_FORMAT = "{0} {1}";
         return String.Format(DATE_ID_ORG_FORMAT, date.Date.ToString("dd.MM.yyyy"), id);
      }

      public virtual void AddOrg(BaseDocument doc)
      {
         string id = doc.id;
         string userid = doc.userid;
         DateTime date = doc.created;

         AddOrg(id, userid, date);
      }

      //public void AddOrg(string id, string userid, DateTime date)
      //{
      //   string date_id_org_string = String.Format("{0} {1}", date.Date.ToString("dd.MM.yyyy"), id);

      //   if (uniqOrgs.ContainsKey(date_id_org_string) == false)
      //      uniqOrgs.Add(date_id_org_string, 1);
      //   else
      //   {
      //      CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG, userid) ??
      //         ConfigUtils.GetCommonConfig(dsConfig, ConfigKeyItems.VISIT_DUBLICATES_ORG);


      //      if (cc != null && cc.value.Equals(id))
      //      {
      //         uniqOrgs[date_id_org_string]++;
      //      }
      //   }
      //}

      public virtual void Add(Order o)
      {
         AddOrder(o);
      }

      public virtual void AddOrder(BaseDocument o)
      {
         orders++;
         sum += o.Sum();

         AddOrg(o);
         AddOrder(o.id, o.userid, o.created);

         if (!hasMissedOrder)
         {
            hasMissedOrder = FmDetailBase.OrderMissed(o, (DataSet<int, OrderCommitted>)DataModule.Get(OrderCommitted.OBJECT_NAME));
         }

      }

      public virtual void Add(Incass doc)
      {
         pko++;
#if FavoriteGK
         sum += doc.Sum();
#endif
         AddOrg(doc);
      }

      public virtual void Add(PKO doc)
      {
         pko++;
         //#if !USE_ONLY_ORDER_IN_SUM
         //         sum += doc.Sum();
         //#endif
         AddOrg(doc);
      }

      public virtual void Add(VisitInfo doc)
      {
         AddOrg(doc);
      }

      public virtual int GetVisitCount()
      {
         int result = 0;

         foreach (int v in uniqOrgs.Values)
            result += v;

         return result;
      }

      public virtual int GetOrders() { return orders; }
      public virtual double GetSum() { return sum; }

      public int GetUniqueOrderCount()
      {
         int result = 0;

         foreach (int v in uniqOrder.Values)
            result += v;

         return result;
      }

      protected virtual void BeforeProgressCount(DateTime day, List<OrgFolderItem> items) { }

      public virtual void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         int dayCount = 0;
         double pc = 0;
         DateTime day = start;

         do
         {
            List<OrgFolderItem> items = null;
#if ROUTE_HISTORY
            if(routeHelper == null)
               items = routeHelper.GetRoute(agent, day);
            else
               items = GetAgentRoute(day, dsOrgFolder.Data);
#else
            items = GetAgentRoute(day, dsOrgFolder.Data);
#endif

            //Проверяем присутсвтует или маршрут на выбранный день,
            //если маршрута нет, то этот день мы просто не считаем для среднего
            if (items != null && items.Count != 0)
            {
               BeforeProgressCount(day, items);

               int count = 0;
               foreach (OrgFolderItem oi in items)
               {
                  if (IsUniqueOrgsListContaintsOrgId(day, oi.name))
                     count++;
               }

               pc += (double)count / (double)items.Count;
               dayCount++;
            }

            day = day.AddDays(1);
         } while (day < end);

         plan = dayCount == 0 ? 0 : pc / (double)dayCount * 100;
      }

      //Возвращает true, если список посещенных организаций содержит
      //проверяемое ID
      protected virtual bool IsUniqueOrgsListContaintsOrgId(DateTime d, string orgID)
      {
         String k = MakeDateIdOrgString(orgID, d);

         return uniqOrgs.ContainsKey(k);

      }

      //Возвращает имя дня недели по индексу
      protected string GetDayNameByIndex(int day)
      {
         string[] days = new string[] { "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота" };
         return days[day];
      }

      //Возвращает список организации, которые надо посетить агенту на день недели
      public virtual List<OrgFolderItem> GetAgentRoute(DateTime date, ICollection dsOrgFolder)
      {
         int currentWeek = -1;
         string day = GetDayNameByIndex((int)date.DayOfWeek);

         CommonConfig cfg = ConfigUtils.GetCommonConfig(dsConfig, ConfigKeyItems.SHEDULE_START);
         if (cfg == null)
            cfg = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.SHEDULE_START, AgentID);
         if (cfg != null)
         {
            try
            {
               if (cfg.value.Trim().Length > 0)
               {
                  DateTime dt = DateTime.ParseExact(cfg.value, "yyyy-MM-dd", null);
                  while (dt.DayOfWeek != DayOfWeek.Monday)
                     dt = dt.AddDays(-1);

                  TimeSpan ts = new TimeSpan(date.Ticks);
                  ts = ts.Subtract(new TimeSpan(dt.Ticks));
                  if (ts.TotalDays >= 0)
                     currentWeek = (int)(ts.TotalDays / 7) % 4 + 1;
               }
            }
            catch (Exception)
            {
            }
         }

         foreach (OrgFolder of in dsOrgFolder)
         {
            if (of.agent.id.Equals(AgentID))
            {
               if (of.name.Equals(day))
               {
                  of.items.Sort();
                  return of.items;
               }
               else if (of.name.Length > 1 ? of.name.Substring(1).Equals(day) : false)
               {
                  int cw = -1;
                  Int32.TryParse(of.name.Substring(0, 1), out cw);
                  if (currentWeek < 0 || cw == currentWeek)
                  {
                     of.items.Sort();
                     return of.items;
                  }
               }
            }
         }

         return null;
      }

      internal void Add(GPSGather doc)
      {
         AddOrg(doc);
      }

      public virtual Image CreateProgressImage(DataGridViewImageColumn clmn)
      {
         return GRSoft.NapoleonManager.Utils.ProgressImage.CreateProgressImage(plan, clmn);
      }
   }

   public delegate void InvokeDelegate();
}
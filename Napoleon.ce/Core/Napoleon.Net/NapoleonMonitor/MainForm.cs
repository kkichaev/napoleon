/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Главная форма
 * 
 * ert   21/04/2010   creating
 */

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
   public partial class MainForm : Form
   {
      public static MainForm Instance;

      public static SynchronizationContext sync;

      bool convertRoteDone = false;

      //DataSets
      protected Agents dsAgents;

      DataSet<string, ContractDef> dsContracts = new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME, false);
      DataSet<string, NBTLViewer> dsViewers = new DataSet<string, NBTLViewer>(NBTLViewer.OBJECT_NAME, false);

      protected DataSet<int, UserLog> dsUserLog = new DataSet<int, UserLog>("UserLog");
      
      // общие организации (файл ORGS) - может потребоваться в других местах
      //  23.09.2010 kki ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      //как стало ясно этогог файл может и не быть, тогда что бы получить список,   +
      //надо выбрать организации у всех агентов                                     +
      //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      protected DataSet<string, Org> dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      protected DataSet<int, OrgFolder> dsOrgFolder = new DataSet<int, OrgFolder>(OrgFolder.OBJECT_NAME);
      protected DataSet<string, UserInfo> dsUserInfo = new DataSet<string, UserInfo>(UserInfo.OBJECT_NAME);
      protected DataSet<int, CommonConfig> dsConfig = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
      protected DataSet<string, PotenzialOrg> dsPtnzOrg = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME);
      protected DataSet<string, UserActivity> dsUserActivity = new DataSet<string,UserActivity>(UserActivity.OBJECT_NAME);
      protected DataSet<int, ScriptDoc> dsScriptDoc = new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME);
      protected DataSet<int, ScriptDef> dsScriptDef = new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME);


      protected EDataResponse dataResponceError;
      protected DataWarningTooltip dateWarningTooltip;
      
      //Координаты текущей позиции указателя курсора, для того что бы показать окно инфо агента
      protected Point savedMousePopupPosition = new Point();

      public DBConnection conn;
      protected Employee currentUser;
      bool isReceiving = false;

      Font boldCellsFont;

      public MainForm()
      {
         InitializeComponent();

         Init();
         DecoratorFactory.GetDecorator(this).AdjustForm();

         ToolStripButton btnPhoto = new System.Windows.Forms.ToolStripButton();
         btnPhoto.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnPhoto.Image = GRSoft.NapoleonMonitor.Properties.Resources.accessorieseditor;
         btnPhoto.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnPhoto.Name = "btnPhoto";
         btnPhoto.Size = new System.Drawing.Size(23, 22);
         btnPhoto.Text = "Выгрузка фотографий";
         btnPhoto.Click += new System.EventHandler((s, e) => { new FmExportPhoto().Show(); });
         tsbConfig.Items.Add(btnPhoto);

         ToolStripButton rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = GRSoft.NapoleonMonitor.Properties.Resources.excel;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Отчёт по анкетам";
         rttReport.Click += new System.EventHandler(delegate(object sender, EventArgs e)
         {
            FmQuestionReport form = new FmQuestionReport();
            form.shortAddr = true;
            form.Show();
         });
         tsbConfig.Items.Add(rttReport);

         ToolStripButton btnReport = new System.Windows.Forms.ToolStripButton();
         btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btnReport.Image = GRSoft.NapoleonMonitor.Properties.Resources.view_calendar_timeline;
         btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         btnReport.Name = "btnReport";
         btnReport.Size = new System.Drawing.Size(23, 22);
         btnReport.Text = "Отчет по контрактам";
         btnReport.Click += new System.EventHandler((s, e) => { new FmContractReport().Show(); });
         tsbConfig.Items.Add(btnReport);

         btnTask.Visible = false;
         TrySetProxy();  

         ServicePointManager.ServerCertificateValidationCallback += AcceptAllCertifications;
      }

      static public bool AcceptAllCertifications(object sender, X509Certificate certificate, X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
      {
         return true;
      }

      protected void Init()
      {
         this.btnPriceRemnants.Visible = false;

         sync = SynchronizationContext.Current;
         dtpBeginDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month,
            DateTime.Now.Day, 0, 0, 0);
         dtpEndDate.Value = DateTime.Now;
         dtpEndDate.Enabled = false;
         Instance = this;
         dateWarningTooltip = new DataWarningTooltip();
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
         Form fm = (Form)ci.Invoke(new object [] {});
         fm.Show();
#endif
      }

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
         refreshDataSets.Add(dsContracts);
         refreshDataSets.Add(dsViewers);

         //CurrentUser.InitCurrentUser(refreshDataSets, true);

         SimpleDataSet<OrgFolder> of = null;
         SimpleDataSet<OrgFolder> ofOld = null;

         Thread refreshThread = DataModule.RefreshGiveSets(conn, refreshDataSets, FmWait.ProgressIndicator);

         FmWait.ShowForm(this, refreshThread);
         refreshThread.Join();

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
               } else
                  message = "Ошибка записи новых маршрутов";
            }
            else
               message = "Старые маршруты не найдены";
            MessageBox.Show(message);
         }

#if NO_ROUTE_EDITOR
         smiRoute.Visible = false;
#endif
         dsDivision.CheckAgents();
      }

      //Произошла ошибка в соединении
      protected void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         dataResponceError = e;
         FmWait.CloseForm(true);
         btnRefresh.Enabled = true;
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
         
         DataModule.OnDataResponceError += DataConnectionError;

         try
         {
            FetchMainDataFromDB(conn);
            DataModule.ClearEvents();
            FmWait.CloseForm();

            if (dataResponceError != null)
            {
               MessageBox.Show(this, dataResponceError.Msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               return false;
            }

            CurrentUser.SetViewers(dsViewers);

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
            } else
            {
               if (ex.Message.Length > 0)
                  message = ex.Message;
               else
                  message = "Ошибка соединения, проверьте правильность ввода логина и пароля пользователя,\n" +
                                 "а так же наличие сетевого соединения";
            }

            MessageBox.Show(this, message,"Ошибка",MessageBoxButtons.OK, MessageBoxIcon.Error);
         }

         return ret;
      }

      protected void RefreshDataSet()
      {
         if (!CheckIsMainDataPresents(true))
            return;

         btnRefresh.Enabled = false;

         Config c = Config.GetConfig();
         conn = c.GetConnection();
         
         DataModule.OnDataResponceError += DataConnectionError;

         try
         {
            List<IDataSet> updSets = new List<IDataSet>();
            updSets.Add(dsOrgFolder);
            updSets.Add(dsUserLog);
            updSets.Add(dsUserInfo);
            updSets.Add(dsScriptDoc);
            updSets.Add(dsScriptDef);
            updSets.Add(dsConfig);
            updSets.Add(dsUserActivity);
            updSets.Add(dsPtnzOrg);

#if WEIGHT_IN_TOTAL_REPORT
            DataSet<String, Price> dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
                  new DataSet<string, Price>(Price.OBJECT_NAME);
            if (dsPrice.Count == 0)
            {
               dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
               updSets.Insert(0, dsPrice);
            }
#endif

            if (dsOrg.Count == 0)
               updSets.Add(dsOrg);

            AddUpdateDataSet(updSets);

            DateTime dtEndDate = GetRangeEndDate();
            AdjustFilterForDS(dtpBeginDate.Value.Date, dtEndDate.Date);
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
            } else
            {
               if (ex.Message.Length > 0)
                  message = ex.Message;
               else
                  message = "Ошибка соединения, проверьте правильность ввода логина и пароля пользователя,\n" +
                                 "а так же наличие сетевого соединения";
            }

            MessageBox.Show(this, message,"Ошибка",MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      protected virtual void AddUpdateDataSet(List<IDataSet> updSets) {}
      protected virtual void AfterRefreshData() { }

      protected const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";
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

         dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
         dsScriptDef.Filter = "\"id\" <> 0";
         //dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "date", dateBegin, dateEnd);

         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "objDate", dateBegin, dateEnd);

      }
     
      // Событие окончания выборки
      protected void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();

         DocFilterHelper fh = new DocFilterHelper(dsScriptDef);
         fh.SetScripts(dsScriptDoc);

         AfterRefreshData();
         Invoke(new InvokeDelegate(
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
               expanded.Add(d.id, true);
            if (tn.Nodes.Count > 0)
               LoadExpanded(expanded, tn.Nodes);
         }
      }

      void SetExpanded(Dictionary<int, Boolean> expanded, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode tn in nodes)
         {
            Division d = tn.Tag as Division;
            if (d != null && expanded.ContainsKey(d.id))
               tgvAgentsSummary.ExpandNode(tn);

            if (tn.Nodes.Count > 0)
               SetExpanded(expanded, tn.Nodes);
         }
      }

      protected virtual DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummary(dsConfig);
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

            SummaryDivisionData tn = ds.MakeDivisionSummary(manager.Division, dtpBeginDate.Value.Date, GetRangeEndDate(), tgvAgentsSummaryProgres, tgvAgentsSummary);
            TreeGridNode node = tgvAgentsSummary.Nodes.AddDataItem(tn);
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
         foreach (Division child in parent)
         {
            ds.Clear();
            SummaryDivisionData tn = ds.MakeDivisionSummary(child, dtpBeginDate.Value.Date, GetRangeEndDate(), tgvAgentsSummaryProgres, tgvAgentsSummary);
            TreeGridNode childNode = nodeParent.Nodes.AddDataItem(tn);
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
         if( boldCellsFont == null )
            boldCellsFont = new Font(tgvAgentsSummary.DefaultCellStyle.Font, FontStyle.Bold);
         node.DefaultCellStyle.Font = boldCellsFont;
      }

      public DateTime GetRangeEndDate()
      {
         DateTime endDate = tsmiRange.Checked ? dtpEndDate.Value.AddDays(1) : dtpBeginDate.Value.Date.AddDays(1);
         return new DateTime(endDate.Year, endDate.Month, endDate.Day);
      }

      protected override void OnKeyDown(KeyEventArgs e)
      {
         base.OnKeyDown(e);
         if( e.KeyCode == Keys.F5 && e.Modifiers == Keys.None )
         {
            RefreshDataSet();
         }
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


      //Показать форму "Подробно"
      protected void ShowDetail()
      {
         TreeGridNode cr = tgvAgentsSummary.CurrentRow;
         SummaryData sdi = (cr == null) ? null : cr.DataItem as SummaryData;
         if (cr == null ||  sdi ==  null )
         {
            return;
         }

         DataModule.DataProcessed -= RefreshRetrieveComlete;

         FmDetailData data = new FmDetailData(sdi.AgentID,
            dtpBeginDate.Value.Date,
            tsmiToday.Checked ? dtpBeginDate.Value : dtpEndDate.Value.Date, null);

         FmDetail detail = FormEntries.OpenDetailForm(data);
         detail.SetScriptMode(true);

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
         if( a != null )
            Route.Show(GetSelectedAgent());
      }

      //Установит "видимость" всех элементов popup меню
      protected void ShowAllPopupMenuAgentsSummary(bool visible)
      {
         foreach (ToolStripItem tsi in menuAgentsSummary.Items)
         {
            tsi.Visible = visible;
         }
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

      //Проверка на правильность установки диапазона дат выборки
      protected void CheckDateValid()
      {
         if (!Visible)
         {
            return;
         }

         if (tsmiRange.Checked && dtpBeginDate.Value.Date > dtpEndDate.Value.Date)
         {
            dateWarningTooltip.Show(new Point(Location.X + dtpEndDate.Location.X,
                 Location.Y + dtpEndDate.Location.Y - 5));
         }
         else
         {
            dateWarningTooltip.Hide();
         }
      }

      //Событие изменение условия даты начала выборки
      protected void dtpBeginDate_ValueChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      //Событие изменение условия даты конца выборки
      protected void dtpEndDate_ValueChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      //форма всплывающего окна "предупреждение" о неправильном выборе даты
      public class DataWarningTooltip : Form
      {
         protected Label label = new Label();
         protected LinkLabel lbDateChange = new LinkLabel();

         public DataWarningTooltip()
         {
            StartPosition = FormStartPosition.Manual;
            TopMost = true;
            BackColor = Color.Lime;
            FormBorderStyle = FormBorderStyle.None;
            ShowInTaskbar = false;
            Size = new Size(250, 30);

            label.SetBounds(2, 2, 250, 15);
            label.Text = "Дата окончания выборки меньше даты начала";
            this.Controls.Add(label);

            lbDateChange.SetBounds(100, 17, 250, 15);
            lbDateChange.Text = "поменять";
            lbDateChange.Click += OnChangeLabel_Click;
            this.Controls.Add(lbDateChange);
         }

         public void Show(Point point)
         {
            Location = point;
            Show();
         }

         protected void OnChangeLabel_Click(object sender, EventArgs e)
         {
            DateTime dtTemp = MainForm.Instance.dtpBeginDate.Value;
            MainForm.Instance.dtpBeginDate.Value = MainForm.Instance.dtpEndDate.Value;
            MainForm.Instance.dtpEndDate.Value = dtTemp;
         }
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

               if(userInfo.phone.Trim().Length > 0)
                  Clipboard.SetText(userInfo.phone);
            }

            Show();
         }
      }

      //При перемещении формы перетаскиваем с ней и окно предупреждения о диапазоне дат
      protected void MainForm_Move(object sender, EventArgs e)
      {
         if (dateWarningTooltip.Visible)
         {
            dateWarningTooltip.Location = new Point(Location.X + dtpEndDate.Location.X,
                 Location.Y + dtpEndDate.Location.Y - 5);
         }
      }

      //Установить заговолок формы в соответсвии с текущим пользователем
      protected void SetFormCaptionWithCurrUser(Agent a)
      {
         const string CAPTION = "Дела: пользователь {0}";

         Text = String.Format(CAPTION, a.name);
      }

      //Переключение условий выборки по щелчку на кнопку
      protected void tsbSelectRange_Click(object sender, EventArgs e)
      {
         if (tsmiToday.Checked)
         {
            tsmiRange_Click(sender, e);
         }
         else
         { 
            tsmiToday_Click(sender, e);
         }
      }

      //Условие выборки "за сегодня"
      protected void tsmiToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true, "За сегодня");
      }

      //Условие выборки "за период"
      protected void tsmiRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false, "За период");
      }

      //Настройка кнопок для выбора периода 
      protected void AdjustRangeButton(bool isToday, string toolTipText)
      {
         tsbSelectRange.Image = isToday ? tsmiToday.Image : tsmiRange.Image;
         tsmiToday.Checked = isToday;
         tsmiRange.Checked = !isToday;
         tsbSelectRange.ToolTipText = toolTipText;
         dtpEndDate.Enabled = !isToday;
         CheckDateValid();
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
      protected void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         if (tgvAgentsSummary.Nodes.Count > 0)
         {
            Type rptType = FormEntries.GetFormType(typeof(HtmlReport));
            ConstructorInfo ci = rptType.GetConstructor(Type.EmptyTypes);
            HtmlReport htmlReport = (HtmlReport)ci.Invoke(new object[] { });

            OpenLink.NewWindow(String.Format("\"{0}\"", htmlReport.makeAgentSummaryFileInfo(tgvAgentsSummary,
               new TimeInterval(dtpBeginDate.Value, dtpEndDate.Value))));
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
         //if (MainForm.Instance.CheckIsMainDataPresents(true) == false)
         //   return;

         if (FmConfig.OpenConfig(this) == DialogResult.OK)
         {
//#if CONFIG_HISTORY
//            SelectCurrentConfig();
//#endif
            TrySetProxy();
            Text = "Дела";
            RefreshDataSet();
         };
      }

      protected void MainForm_Load(object sender, EventArgs e)
      {
         SetVersionText();

         if (!Config.Exist())
         {
            //DialogResult dr = new FmWelcome().ShowDialog();
            //if (dr != DialogResult.OK)
            //{
            //   Close();
            //}
         }
//#if CONFIG_HISTORY
//         SelectCurrentConfig();
//         cbConfig.Visible = true;
//#else
         cbConfig.Visible = false;
//#endif
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
               cbConfig.SelectedIndex = i;
               break;
            }
         }
      }

      protected void SetVersionText()
      {
         string result = string.Empty;
         Assembly a = Assembly.GetEntryAssembly();

         object[] attrs = a.GetCustomAttributes(typeof(AssemblyFileVersionAttribute), false);
         if (attrs.Length > 0)
         {
            AssemblyFileVersionAttribute fva = attrs[0] as AssemblyFileVersionAttribute;
            lbVersion.Text = "версия: " + fva.Version;

            string f = a.GetModules()[0].FullyQualifiedName;
            lbVersion.Text += " / " + File.GetLastWriteTime(f).ToShortDateString();
         }
         else
            lbVersion.Text = string.Empty;
      }

      public DateTime GetBeginDateForSelection()
      {
         return dtpBeginDate.Value;
      }

      public Employee GetCurrentUser()
      {
         return currentUser;
      }

      internal DataSet<int, OrgFolder> GetDsOrgFolder()
      {
         return dsOrgFolder;
      }

      protected bool IsPotenzialOrgOutOfPlan(Agent a)
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
                     routeOrgs = GetRouteOrgs(a);
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

      protected Dictionary<string, bool> GetRouteOrgs(Agent a)
      {
         Dictionary<string, bool> routeOrgs = new Dictionary<string,bool>();

         foreach (OrgFolder of in dsOrgFolder.Data)
         {
            if (of.agent.id == a.id && of.items != null)
            {
               foreach (OrgFolderItem item in of.items)
                  routeOrgs[item.name] = true;
            }
         }

         return routeOrgs;
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
            form.From = dtpBeginDate.Value;
            form.Till = dtpEndDate.Value;
            form.AdjustRangeButton(!dtpEndDate.Enabled);
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
         Form fm = (Form)ci.Invoke(new object [] {});
         fm.Show();
      }

      protected void toolStripButton1_Click(object sender, EventArgs e)
      {
         new FmQuestionReport().Show();
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
               Config.SetInstance(cfg);
               DataModule.ClearDataSets();
               CurrentUser.Clear();
               cfg.GetConnection().SetNewSession(Config.PDTFileName(cfg.name));
            }
         }
      }
   }

   public class SummaryData
   {
      Agent agent;
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


      public string Name { get { return agent.Name; } }
      public string AgentID { get { return agent.id; } }
      public Agent Agent { get { return agent; } }
      public Image ProgressImage { get { return progressImage; } set { progressImage = value; } }
      public int Visits { get { return GetVisitCount(); } }
      public int Orders { get { return orders; } }
      public double DocSum { get { return sum; } }
      public double ProgressValue { get { return plan; } }
      public string LastAccess { get { return lastAccess; } set { lastAccess = value; } }
      public bool HasMissedOrders { get { return hasMissedOrder; } }
      public int UniqOrders { get { return GetUniqueOrderCount(); } }

      public SummaryData(Agent agent, DataSet<int, CommonConfig> dsConfig)
      {
         this.agent = agent;
         this.dsConfig = dsConfig;
      }

      public void AddOrder(string id, string userid, DateTime date)
      {
         string date_id_org_string = String.Format("{0} {1}",
            date.Date.ToString("dd.MM.yyyy"), id);

         if (uniqOrder.ContainsKey(date_id_org_string) == false)
            uniqOrder.Add(date_id_org_string, 1);
      }

      public void AddOrg(string id, string userid, DateTime date)
      {
         string date_id_org_string = String.Format("{0} {1}", date.Date.ToString("dd.MM.yyyy"), id);

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

      public void AddOrg(BaseDocument doc)
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

      public virtual void Add(ScriptDoc o)
      {
         orders++;
         sum += o.Sum();

         AddOrg(o);
         AddOrder(o.id, o.userid, o.created);
      }


      public int GetVisitCount()
      {
         int result = 0;

         foreach (int v in uniqOrgs.Values)
            result += v;

         return result;
      }

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
            List<OrgFolderItem> items = GetAgentRoute(day, dsOrgFolder.Data);

            //Проверяем присутсвтует или маршрут на выбранный день,
            //если маршрута нет, то этот день мы просто не считаем для среднего
            if (items != null && items.Count != 0)
            {
               BeforeProgressCount(day, items);

               int count = 0;
               foreach (OrgFolderItem oi in items)
               {
                  if (IsUniqueOrgsListContaintsOrgId(oi.name))
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
      protected bool IsUniqueOrgsListContaintsOrgId(string orgID)
      {
         foreach (string id in uniqOrgs.Keys)
            if (id.Contains(orgID))
               return true;

         return false;

      }

      //Возвращает имя дня недели по индексу
      protected string GetDayNameByIndex(int day)
      {
         string[] days = new string[] { "Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота" };
         return days[day];
      }

      //Возвращает список организации, которые надо посетить агенту на день недели
      public List<OrgFolderItem> GetAgentRoute(DateTime date, ICollection dsOrgFolder)
      {
         int currentWeek = -1;
         string day = GetDayNameByIndex((int)date.DayOfWeek);

         CommonConfig cfg = ConfigUtils.GetCommonConfig(dsConfig, ConfigKeyItems.SHEDULE_START);
         if (cfg == null)
            cfg = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.SHEDULE_START, AgentID);
         if( cfg != null )
         {
            try
            {
               DateTime dt = DateTime.ParseExact(cfg.value, "yyyy-MM-dd", null);
               TimeSpan ts = new TimeSpan(date.Ticks);
               ts = ts.Subtract(new TimeSpan(dt.Ticks));
               if (ts.TotalDays >= 0)
                  currentWeek = (int)(ts.TotalDays / 7) % 4 + 1;
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
                  return of.items;
               }
               else if (of.name.Length > 1 ? of.name.Substring(1).Equals(day) : false)
               {
                  int cw = -1;
                  Int32.TryParse(of.name.Substring(0, 1), out cw);
                  if (currentWeek < 0 || cw == currentWeek)
                     return of.items;
               }
            }
         }

         return null;
      }
   }

   public delegate void InvokeDelegate();
}

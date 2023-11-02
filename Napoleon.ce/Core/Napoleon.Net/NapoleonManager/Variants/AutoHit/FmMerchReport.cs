/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   21/12/2010   creating
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
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Html;

namespace GRSoft.NapoleonManager
{
   public partial class FmMerchReport : Form
   {
      private static FmMerchReport instance;
      private DivisionList dsDivision = DivisionList.GetDataSet();
      private DataSet<int, GoodsAudit> dsAudit;
      private DataSet<string, ActionCategory> dsCategory;
      private DataSet<string, Goods> dsGoods;
      private Division curDivision;
      private DataSet<string, ManagerFolder> dsManagerFolder;

      private FmMerchReport()
      {
         InitializeComponent();
         Visible = false;
         InitDataSets();

         dtpBegin.Value = DateTime.Now;
         dtpEnd.Value = DateTime.Now;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         rbDivision_Click(rbDivision, EventArgs.Empty);
      }

      private void InitDataSets()
      {
         dsAudit = new DataSet<int, GoodsAudit>(GoodsAudit.OBJECT_NAME, false);
         dsCategory = new DataSet<string, ActionCategory>(ActionCategory.OBJECT_NAME, false);
         dsGoods = new DataSet<string, Goods>(Goods.OBJECT_NAME, false);
         dsManagerFolder = new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
      }

      public static void ShowInstance(Division division)
      {
         if (instance == null)
            instance = new FmMerchReport();

         instance.curDivision = division;

         if (!instance.Visible)
            instance.Show();
         else
            instance.Activate();
      }

      //Запрос к базе данных на получения списка подразделений
      private void GetDivisions()
      {
         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsDivision);

         DataModule.SetDataRepsonceHandlers(FirstTimeDataProcessed, DataConnectionError);
         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(), updSets, FmWait.ProgressIndicator));
      }

      //Конец запроса
      private void EndOfDataReceive()
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
      }

      //Обработка данных необходимых для начального вывода формы
      private void FirstTimeDataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();

         Invoke(new EmptyParamHandler(delegate
         {
            cbDivisions.Items.Clear();
            dsDivision.CheckAgents();

            DivisionItem di = null;

            foreach (Division division in dsDivision.Data)
            {
               DivisionItem cdi = new DivisionItem(division);
               cbDivisions.Items.Add(cdi);

               if (division.Equals(curDivision))
                  di = cdi;
            }

            if (di != null)
               cbDivisions.SelectedItem = di;
            else if (cbDivisions.Items.Count > 0)
               cbDivisions.SelectedIndex = 0;

            Visible = true;
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         EndOfDataReceive();

         Invoke(new EmptyParamHandler(delegate
         {
            Visible = false;
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);

            Close();
         }));
      }

      private void FmMerchReport_Load(object sender, EventArgs e)
      {
         GetDivisions();
      }

      private void FmMerchReport_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void cbPeriod_CheckedChanged(object sender, EventArgs e)
      {
         dtpEnd.Enabled = ((CheckBox)sender).Checked;
      }

      private DateTime GetEndPeriod()
      {
         return cbPeriod.Checked
            ? dtpEnd.Value.Date.AddDays(1)
            : dtpBegin.Value.Date.AddDays(1);
      }

      private void btnExcelReport_Click(object sender, EventArgs e)
      {
         CreateReport(ExcelReportDataProcessed);
      }

      private void setWarning(string warning, Control control)
      {
         MessageBox.Show(String.Format("Выберите {0}!", warning));
         control.Focus();
      }

      private void CreateReport(EventHandler reportDataProcessed)
      {
         if (rbAgents.Checked && (cbAgents.SelectedItem as Agent) == null)
         {
            setWarning("агента", cbAgents);
            return;
         }
         else if (rbDivision.Checked && (cbDivisions.SelectedItem as DivisionItem) == null)
         {
            setWarning("подразделение", cbAgents);
            return;
         }

         string AGENTS_FILTER = rbDivision.Checked ?
            DataUtils.MakeFilterFromAgents(null, CurrentUser.user.GetAgents()) :
            String.Format("\"userid\" in ('{0}')", (cbAgents.SelectedItem as Agent).id);

         string orderQryField = "created";


         string DATA_FILTER = String.Format("\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')",
            orderQryField, dtpBegin.Value.Date, GetEndPeriod());

         string FULL_FILTER = String.Format("{0} and {1}",
            AGENTS_FILTER, DATA_FILTER);

         if (AGENTS_FILTER.Length == 0)
         {
            MessageBox.Show("Выбранное подразделение не содержит агентов");
            return;
         }
         dsAudit.Filter = FULL_FILTER;

         List<IDataSet> updSets = new List<IDataSet>();

         if (dsGoods.Count == 0)
         {
            updSets.Add(dsGoods);
         }

         if (dsCategory.Count == 0)
         {
            updSets.Add(dsCategory);
         }

         if (dsManagerFolder.Count == 0)
         {
            dsManagerFolder.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            updSets.Add(dsManagerFolder);
         }

         if (rbDivision.Checked)
            foreach (Division.DivisionAgent a in ((DivisionItem)cbDivisions.SelectedItem).Agents)
            {
               if (a.agent == null)
                  continue;
               DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

               if (dsOrg.Count == 0)
               {
                  dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), dsOrg.Name);
                  updSets.Add(dsOrg);
               }
            }
         else
         {
            Agent a = cbAgents.SelectedItem as Agent;
            DataSet<string, Org> dsOrg = DataModule.GetUserDataSet(a.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;

            if (dsOrg.Count == 0)
            {
               dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), dsOrg.Name);
               updSets.Add(dsOrg);
            }
         }

         updSets.Add(dsAudit);

         DataModule.SetDataRepsonceHandlers(reportDataProcessed,
            DataConnectionError);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.GetConfig().GetConnection(),
            updSets, FmWait.ProgressIndicator));
      }

      private void BuildCategoriesTree()
      {
         ActionCategory curParent = null;
         foreach (ActionCategory curCategory in dsCategory.Data)
         {
            if (curCategory.level == 0)
               curParent = curCategory;

            if (curCategory == curParent)
               continue;

            if (curCategory.level > curParent.level)
            {
               //make this node a child of a current parent
               curCategory.parent = curParent;
            }
            else
            {
               //moving up by the tree to get the corresponding parent
               while (curCategory.level < curParent.level)
                  curParent = curParent.parent;

               //make this node a child of a node with its level - 1
               curCategory.parent = curParent.parent;
            }

            curParent = curCategory;
         }
      }

      private void ExcelReportDataProcessed(object o, EventArgs e)
      {
         EndOfDataReceive();
         BuildCategoriesTree();
         Invoke(new EmptyParamHandler(
            delegate 
            {
               List<MerchReportData.OrgShelf> items = null;
               int maxLevel;
               List<Org> orgs = null;
               List<Agent> agents = null;
               PrepareDataForReport(out items, out orgs, out agents, out maxLevel);
               IReport report = new MerchReport(items, 
                  orgs,
                  agents,
                  dsManagerFolder, 
                  (DivisionItem)cbDivisions.SelectedItem,
                  cbAgents.SelectedItem as Agent,
                  dtpBegin.Value.Date,
                  dtpEnd.Value.Date,
                  maxLevel,
                  new ExcelMerchReport());
               report.Build();
               report.Show();
            }));
      }

      //Формировать структуру данных для отчета
      private void PrepareDataForReport(out List<MerchReportData.OrgShelf> data, out List<Org> orgs, out List<Agent> agents, out int maxLevel)
      {
         data = new List<MerchReportData.OrgShelf>();
         orgs = new List<Org>();
         agents = new List<Agent>();
         maxLevel = 0;
         foreach (GoodsAudit audit in dsAudit.Data)
         {
            if (audit == null)
               continue;

            if (!orgs.Contains(audit.org))
               orgs.Add(audit.org);
            if (!agents.Contains(audit.agent))
               agents.Add(audit.agent);

            foreach(GoodsAudit.Item item in audit.items)
            {
               ActionCategory foundCategory = null;
               if (dsCategory.TryGetValue(dsGoods[item.id].fid, out foundCategory))
               {
                  if (maxLevel < foundCategory.level)
                     maxLevel = foundCategory.level;

                  bool bFound = false;
                  foreach (MerchReportData.OrgShelf oldShelf in data)
                  {
                     if (oldShelf.org == audit.org 
                        && oldShelf.category == foundCategory
                        && oldShelf.timeCreated < audit.created)
                     {
                        oldShelf.agent = audit.agent;
                        oldShelf.timeCreated = audit.created;
                        oldShelf.metersAll = item.ShelfAll;
                        oldShelf.metersOur = item.ShelfOur;
                        oldShelf.skuAll = (int)(item.ScuAll + 0.005);
                        oldShelf.skuOur = (int)(item.ScuOur + 0.005);
                        bFound = true;
                        break;
                     }
                  }
                  if (!bFound)
                  {
                     MerchReportData.OrgShelf shelf = new MerchReportData.OrgShelf();
                     shelf.agent = audit.agent;
                     shelf.category = foundCategory;
                     shelf.org = audit.org;
                     shelf.metersAll = item.shelfAll;
                     shelf.metersOur = item.shelfOur;
                     shelf.skuAll = (int)(item.scuAll + 0.005);
                     shelf.skuOur = (int)(item.scuOur + 0.005);
                     data.Add(shelf);
                  }
               }
            }
         }
         //since level starts from 0 in DBF need to increase by 1
         ++maxLevel;
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }
   }
}
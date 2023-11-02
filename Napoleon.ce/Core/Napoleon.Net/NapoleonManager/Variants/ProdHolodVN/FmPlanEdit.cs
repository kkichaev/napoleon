/*
 * Copyright (C), 2010, Гильдия разработчиков
 * 
 * Редактирование плана
 * 
 * kki   24/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   //Форма для редактирования - создания плана
   public partial class FmPlanEdit : Form
   {
      PlanItems planItems = new PlanItems();

      #region Public Methods
      /// <summary>
      /// Конструктор
      /// </summary>
      /// <param name="data">данные для заполнения формы</param>
      public FmPlanEdit(FmPlanEditData data)
         : this()
      {
         AdjustFormComponents(data);
      }
      #endregion

      #region Private Fields
      //Заголовок формы
      private readonly string FORM_AGENT_CAPTION = "План агента {0}";

      //Редактируемые данные
      private FmPlanEditData data;
      #endregion

      #region Private Methods
      /// <summary>
      /// Закрытый конструктор
      /// </summary>
      private FmPlanEdit()
      {
         InitializeComponent();

         dgvPlanItems.AutoGenerateColumns = false;
         dgvPlanItems.DataSource = planItems;
      }

      /// <summary>
      ///Настроить визуальные компоненты формы 
      /// </summary>
      /// <param name="data">необходимые данные для настройки</param>
      private void AdjustFormComponents(FmPlanEditData data)
      {
         this.data = data;

         Text = String.Format(FORM_AGENT_CAPTION, data.AgentName);
         //dtpFrom.Value = data.From;
         //dtpTill.Value = data.Till;

         //foreach (PlanItem pitem in data.Plan.items)
         //{
         //   IPlanItemType ptype = PlanItemFactory.CreatePlanItem((PlanItemFactory.PlanItemCode)
         //      Enum.ToObject(typeof(PlanItemFactory.PlanItemCode), pitem.type));
         //   ptype.Load(pitem);
         //   planItems.Add(ptype);
         //}
      }
      
      //Событие когда форма пытается перейти в "закрытое" состояние
      private void FmPlanEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         //if ((sender as FmPlanEdit).DialogResult == DialogResult.OK)
         //{
         //   Plan plan = data.Plan;
         //   plan.from = dtpFrom.Value.Date;
         //   plan.till = dtpTill.Value.Date;

         //   plan.items.Clear();

         //   foreach (IPlanItemType item in planItems)
         //   {
         //      plan.items.Add(item.ToData());
         //   }

         //   e.Cancel = !IsFormCanClose();
         //}
      }

      //Подходят ли установленные данные под условие разрешающее передать
      //эти данные вызываемоу форму объекту
      private bool IsFormCanClose()
      {
         bool result = true;

         //UpdatePlan();

         //CheckObj checkObj;
         //if (!data.CheckDatePeriod(out checkObj))
         //{
         //   result = false;
         //   const string ERROR_MSG = "Ошибка";
         //   MessageBox.Show(this, checkObj.Mgs, ERROR_MSG, MessageBoxButtons.OK, MessageBoxIcon.Error);
         //}

         return result;
      }

      #endregion

      private void btnAdd_Click(object sender, EventArgs e)
      {
         IPlanItemType planItemType = FmPlanItemEdit.ShowForm(Data, null);

         if (planItemType != null)
            planItems.Add(planItemType);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         IPlanItemType currentItem = dgvPlanItems.CurrentRow.DataBoundItem as IPlanItemType;

         if (currentItem == null)
            return;

         int index = dgvPlanItems.CurrentRow.Index;
         //int index = planItems.IndexOf(currentItem);

         //if (index == -1)
         //   return;

         IPlanItemType editedItem = FmPlanItemEdit.ShowForm(Data, currentItem);
         
         if(editedItem != null)
            planItems.UpdateItem(editedItem, index);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         const string TITLE_STR = "Вопрос";
         const string MSF_STR = "Внимание запись будет удалена! Удалить?";

         if (MessageBox.Show(MSF_STR, TITLE_STR,
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         { 
            dgvPlanItems.Rows.Remove(dgvPlanItems.CurrentRow);
         }
      }

      public FmPlanEditData Data { get { return data; } }
   }

   //Данные для формы FmPlanEdit
   public class FmPlanEditData
   {
      //Текущий агент
      private Agent agent;

      //План
      private Plan plan;

      //Набор данных плана
      private DataSet<int, Plan> dsPlan;

      //Набор данных "Price"
      private DataSet<string, Price> dsPrice;

      /// <summary>
      /// Коснтруктор
      /// </summary>
      /// <param name="agent">агент</param>
      /// <param name="plan">план</param>
      public FmPlanEditData(Agent agent, Plan plan, DataSet<int, Plan> dsPlan,
         DataSet<string, Price> dsPrice)
      {
         if (agent == null &&
            dsPlan == null)
            throw new EDataCorrupted();

         this.agent = agent;
         this.plan = plan ?? MakeNewPlan();
         this.dsPlan = dsPlan;
         this.dsPrice = dsPrice;
      }

      //Проверка периода дат, не "перекрещивается" ли установленный
      //период с уже созданными планами
      //public bool CheckDatePeriod(out CheckObj checkObj)
      //{
      //   return false;
      //   //List<DatePeriod> dateCheckingList = new List<DatePeriod>();

      //   //foreach (Plan p in dsPlan.Data)
      //   //{
      //   //   if (p != Plan)
      //   //      dateCheckingList.Add(new DatePeriod(p.from, p.till));
      //   //}

      //   //IChecker checker = new DatePeriodChecker(dateCheckingList);

      //   //return checker.Check(new DatePeriod(Plan.from, Plan.till), out checkObj);
      //}

      /// <summary>
      /// Проверка можно ли создать пункт плана в плане по его коду
      /// </summary>
      /// <param name="code"></param>
      /// <returns></returns>
      internal bool CheckForItemType(PlanItemFactory.PlanItemCode planItemCode)
      {
         //if (plan.items.Count == 0)
         //   return true;

         //if (CheckPlanItemCodeToAllowedDublicates(planItemCode))
         //   return true;

         //if (CodeAllreadyPresent(planItemCode))
         //   return false;

         return true;
      }

      private bool CheckPlanItemCodeToAllowedDublicates(PlanItemFactory.PlanItemCode planItemCode)
      {
         PlanItemFactory.PlanItemCode[] allowToDublicates = 
            new PlanItemFactory.PlanItemCode[] { PlanItemFactory.PlanItemCode.SKU_PLAN_ID };

         foreach (PlanItemFactory.PlanItemCode codeAllowed in allowToDublicates)
            if (codeAllowed == planItemCode)
               return true;

         return false;
      }

      private bool CodeAllreadyPresent(PlanItemFactory.PlanItemCode planItemCode)
      {
         //foreach (PlanItem planItem in plan.items)
         //   if (planItem.type == (int)planItemCode)
         //      return true;

         return false;
      }

      //Имя агента
      public string AgentName { get { return agent.Name; } }

      //Дата плана "с"
      //public DateTime From { get { return plan.from; } }

      //Дата плана "по"
      //public DateTime Till { get { return plan.till; } }

      //План
      public Plan Plan { get { return plan; } }

      //Набор данных план
      public DataSet<int, Plan> Plans { get { return dsPlan; } }

      //Набор данных "Price"
      public DataSet<string, Price> Price { get { return dsPrice; } }

      //Создать новый план
      private Plan MakeNewPlan()
      {
         return null;
         //Plan result = new Plan();
         //DateTime planDatePeriod = new DateTime(DateTime.Now.Year, DateTime.Now.AddMonths(1).Month, 1);
         //result.from = planDatePeriod;
         //result.till = planDatePeriod.AddMonths(1).AddDays(-1);;
         //result.userid = agent.id;
         //result.items = MakePlanItems();
         //result.date = DateTime.Now;
         
         //return result;
      }

      ////Создать пункты плана
      //private List<PlanItem> MakePlanItems()
      //{ 
      //   List<PlanItem> result = new List<PlanItem>();

      //   return result;
      //}

   }

   class PlanItems : BindingList<IPlanItemType>
   {
      public void UpdateItem(IPlanItemType item, int index)
      {
         this[index] = item;
         OnListChanged(new ListChangedEventArgs(ListChangedType.ItemChanged, index));
      }
   }
}
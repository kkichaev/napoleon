/*
 * Copyright (C), 2010, Гильдия разработчиков
 * 
 * Планы
 * 
 * kki   01/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class FmPlan : Form
   {
      #region Public Methods
      /// <summary>
      /// Конструктор
      /// </summary>
      /// <param name="agent"> Текущий агент для редактирования</param> 
      /// <param name="currentUser">Текущий пользователь программы</param>
      public FmPlan(Agent agent, Employee currentUser) : this()
      {
         foreach (Agent a in dsAgents.Data)
         {
            cbAgents.Items.Add(a);
         }

         cbAgents.Sorted = true;
         SelectAgentInCombobox(agent);

         const string FORM_CAPTION = "План: пользователь";
         Text = string.Format("{0} {1}", FORM_CAPTION, currentUser.User);
      }
      #endregion

      #region Private Methods

      #region DataSets
      private DataSet<int, Plan> dsPlan;
      private Agents dsAgents = Agents.GetDataSet();
      private DataSet<string, Price> dsPrice;
      #endregion

      /// <summary>
      /// Закрытый конструктор
      /// </summary>
      private FmPlan()
      {
         InitializeComponent();
         InitDataSets();
         UpdateEditControlButton(false);
      }

      //Инициировать наборы данных
      private void InitDataSets()
      {
         dsPlan = (DataSet<int, Plan>)DataModule.Get(Plan.OBJECT_NAME) ?? new DataSet<int, Plan>(Plan.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
      }

      //Обновить набры данных
      private void RefreshData()
      {
         Agent selectedAgent = GetSelectedAgent();

         if (selectedAgent == null)
            return;

         DataModule.DataProcessed += new EventHandler(DataLoaded);
         dsPlan.Filter = string.Format("userid='{0}'", selectedAgent.id);

         List<IDataSet> updList = new List<IDataSet>();
         updList.Add(dsPlan);

         if (dsPrice.Count == 0)
            updList.Add(dsPrice);

         FmWait.ShowForm(this, 
            DataModule.RefreshGiveSets(
               Config.GetConfig().GetConnection(), updList, FmWait.ProgressIndicator));
      }

      //Событие окончание выпорки у модуля данных
      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(ControlsFillAfterLoaded));
      }

      //Заполнить таблицу из набора данных
      private void ControlsFillAfterLoaded()
      {
         try
         {
            tgvPlan.Rows.Clear();

            if (dsPlan.Count == 0)
               return;

            tgvPlan.SuspendLayout();
            //UpdateTreeGridViewFromPlan(MakeSortedListByDateFromDsPlan());
         }
         catch 
         {
            const string ERROR_MSG = "Ошибка при загрузке таблицы";
            MessageBox.Show(ERROR_MSG); 
         }
         finally
         {
            tgvPlan.ResumeLayout();
            UpdateEditControlButton(true);
            FmWait.CloseForm();
         }
      }

      //private List<KeyValuePair<int, Plan>> MakeSortedListByDateFromDsPlan()
      //{
      //   List<KeyValuePair<int, Plan>> result = new List<KeyValuePair<int, Plan>>(dsPlan);

      //   result.Sort(new Comparison<KeyValuePair<int, Plan>>(delegate(KeyValuePair<int, Plan> p1, KeyValuePair<int, Plan> p2)
      //      { return ((Plan)(p1.Value)).from.CompareTo(((Plan)(p2.Value)).from) * -1; }));

      //   return result;
      //}

      //Заполнить TreeGridViewFrom из списка планов
      private void UpdateTreeGridViewFromPlan(List<KeyValuePair<int, Plan>> planList)
      {
         //foreach (KeyValuePair<int, Plan> plan in planList)
         //{
         //   TreeGridNode node = tgvPlan.Nodes.Add(string.Format("c {0} - по {1}", plan.Value.from.ToString("dd.MM.yyyy"),
         //      plan.Value.till.ToString("dd.MM.yyyy")),
         //      string.Empty, string.Empty, string.Empty,
         //      ProgressImage.CreateProgressImage(plan.Value.planValue, tgvPlanProgress, tgvPlan));
         //   node.Tag = plan.Value;

         //   plan.Value.items.Sort(new Comparison<PlanItem>(delegate(PlanItem item1, PlanItem item2)
         //      { return item1.type.CompareTo(item2.type); }));

         //   foreach (PlanItem planItem in plan.Value.items)
         //   {
         //      IPlanItemType pit = PlanItemFactory.CreatePlanItem(planItem);

         //      TreeGridNode childNode = node.Nodes.Add(
         //         pit, pit.Unit, pit.Value, pit.Current,
         //         ProgressImage.CreateProgressImage(pit.Progress, tgvPlanProgress, tgvPlan));

         //      childNode.Tag = plan.Value;
         //   }

         //   node.Expand();
         //}
      }
      //Обновить наборданных
      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      //Установить текущего агента в cbAgents
      private void SelectAgentInCombobox(Agent agent)
      {
         if (agent == null)
            return;

         foreach (object a in cbAgents.Items)
         {
            if ((a is Agent) && (a as Agent).Equals(agent))
            {
               cbAgents.SelectedItem = a;
            }
         }
      }

      //Возвращает текущего выбранного агента
      private Agent GetSelectedAgent()
      {
         return (Agent)cbAgents.SelectedItem;
      }

      //Добавить новый план
      private void tsAdd_Click(object sender, EventArgs e)
      {
         FmPlanEditData data = new FmPlanEditData(GetSelectedAgent(), null, dsPlan, dsPrice);
         FmPlanEdit planEdit = new FmPlanEdit(data);
         
         if (planEdit.ShowDialog(this) == DialogResult.OK)
         {
            dsPlan.Add(dsPlan.Count, data.Plan);
            SaveAndUpdateGrid(dsPlan);
         }
      }

      //Редактировать план
      private void tsEdit_Click(object sender, EventArgs e)
      {
         FmPlanEdit planEdit = new FmPlanEdit(
            new FmPlanEditData(GetSelectedAgent(), GetCurrentPlane(), dsPlan, dsPrice));

         if (planEdit.ShowDialog(this) == DialogResult.OK)
         {
            SaveAndUpdateGrid(dsPlan);
         }
      }

      /// <summary>
      /// Сохранить набор данных и обновить визуальные компоненты формы
      /// </summary>
      /// <param name="dataSet">набор данных для сохранения</param>
      private void SaveAndUpdateGrid(DataSet<int, Plan> dataSet)
      {
         SavePlansDataSet(dataSet);
         ControlsFillAfterLoaded();
      }

      //Возвращает текущий план, что выделил пользователь щелчком мышки
      private Plan GetCurrentPlane()
      {
         object result = tgvPlan.CurrentRow.Tag;

         if (result == null)
            throw new EDataCorrupted();

         return (Plan)result;
      }

      /// <summary>
      /// Сохранить набора данных в базе данных
      /// </summary>
      /// <param name="dataSet">сохраняемый набор данных</param>
      private void SavePlansDataSet(DataSet<int, Plan> dataSet)
      {
         List<IDataSet> wrDS = new List<IDataSet>();
         wrDS.Add(dataSet);

         if(DataModule.UpdateDataSet(wrDS, null, null, Config.GetConfig().GetConnection(), GetSelectedAgent().id) == false)
         {
            ShowErroToWriteDS();
            return;
         }
      }

      private static void ShowErroToWriteDS()
      {
         const string WRITING_DB_ERROR_MSG = "Ошибка при записи в базу данных.";
         const string ERROR_STR = "Ошибка";

         MessageBox.Show(WRITING_DB_ERROR_MSG, ERROR_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      //Удалить текущий, выделенный план
      private void tsDelete_Click(object sender, EventArgs e)
      {
         const string ASK_TO_DELETE = "Вы действительно хотите удалить план?";
         const string QUEST_STR = "Вопрос";

         if (MessageBox.Show(ASK_TO_DELETE, QUEST_STR, 
             MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            Plan planToRemove = GetCurrentPlane();
            DataSet<int, Plan> toRemove = new DataSet<int, Plan>(Plan.OBJECT_NAME, false);
            toRemove.Add(toRemove.Count, planToRemove);
            List<IDataSet> listToRemove = new List<IDataSet>();
            listToRemove.Add(toRemove);

            if (DataModule.UpdateDataSet(null, listToRemove, null, Config.GetConfig().GetConnection(), GetSelectedAgent().id) == false)
            {
               ShowErroToWriteDS();
               return;
            }

            RemovePlanFromDataSet(planToRemove);
            RemoveRowFromDataGrid();
         }
      }

      private void RemoveRowFromDataGrid()
      {
         if (tgvPlan.CurrentRow != null)
            tgvPlan.Nodes.Remove(tgvPlan.CurrentRow.Level == 1 ?
               tgvPlan.CurrentRow : tgvPlan.CurrentRow.Parent);
      }

      private void RemovePlanFromDataSet(Plan planToRemove)
      {
         foreach (KeyValuePair<int, Plan> plan in dsPlan)
         {
            if (plan.Value.Equals(planToRemove))
               dsPlan.Remove(plan.Key);
            break;
         }
      }

      private void UpdateEditControlButton(bool enable)
      {
         enable = true;
         tsAdd.Enabled = enable;
         tsEdit.Enabled = tgvPlan.Rows.Count > 1;
         tsDelete.Enabled = tgvPlan.Rows.Count > 1;
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (Visible)
         {
            tgvPlan.Nodes.Clear();
            UpdateEditControlButton(false);
            RefreshData();
         }
      }
      #endregion

      private void FmPlan_Load(object sender, EventArgs e)
      {
         RefreshData();
      }
   } 


   /// <summary>
   /// Исключение когда пункт не был найден в коллекции
   /// </summary>
   class EItemNotFound : Exception
   {
      string item;

      public EItemNotFound(string item)
      {
         this.item = item;
      }
   }

   

   class PlanData : List<IPlanItemType>
   { 
   }

   //Единица измерения код - именование
   class UnitAItem
   {
      int key;
      string val;
      public UnitAItem(int key, string val)
      {
         this.key = key;
         this.val = val;
      }

      public int Key { get { return key; } }
      public string Value { get { return val; } }
   }

   class EInvalidPlanIntemCode : Exception { }
   /// <summary>
   /// Класс представляет единицы измерения
   /// </summary>
   //class Units
   //{
   //   #region Public

   //   public static string GetUnitCaption(int code, int plan_item_code)
   //   {
   //      if (plan_item_code == 1)
   //         code = 0;

   //      return GetUnitsCaptionForPlanItem(plan_item_code)[code];
   //   }

   //   //Получить список возможных названий по коду плана
   //   public static string[] GetUnitsCaptionForPlanItem(int plan_item_code)
   //   {
   //      switch (plan_item_code)
   //      {
   //         case 0: return new string[] { units[0].Value, units[2].Value };
   //         case 1: return new string[] { units[1].Value };
   //         case 2: return new string[] { units[0].Value, units[1].Value };
   //      }

   //      throw new EInvalidPlanIntemCode();
   //   }

   //   //Получить код по названию
   //   public static int GetUnitCode(string unit)
   //   {
   //      foreach (UnitAItem u in units)
   //      {
   //         if (u.Value.Equals(unit))
   //            return u.Key;
   //      }

   //      throw new EItemNotFound(unit);
   //   }

   //   #endregion

   //   #region Private
   //   //Все возможные единицы измерения(необходима синхронизация индексации с КПК)
   //   private static readonly UnitAItem[] units = new UnitAItem[] 
   //      { 
   //         new UnitAItem(0, "кг"), 
   //         new UnitAItem(1, "шт"), 
   //         new UnitAItem(1, "руб") };

   //   #endregion
   //}

   //26.09.2010 kki
   //этот класс просто служит оберткой для метода форматирования
   //строки SKU
   class SkuString
   {
      public static string MakeSkuString(string title, DataSet<string, Price> dsPrice, string code)
      {
         string result = string.Empty;

         if (code == null)
            return result;

         try
         {
            result = String.Format("{0} ({1})", title, dsPrice[code].name);
         }
         catch
         {
            result = title;
         }

         return result;
      }
   }
}
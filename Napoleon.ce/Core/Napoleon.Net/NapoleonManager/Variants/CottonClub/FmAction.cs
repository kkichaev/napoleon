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
   public partial class FmAction : Form
   {
      private DataSet<string, Price> dsPrice;
      private DataSet<string, Action> dsAction;
      public DataSet<string, ActionType> dsActionType;
      private SimpleDataSet<Action> dsRemAction = new SimpleDataSet<Action>(Action.OBJECT_NAME, false);

      public FmAction()
      {
         InitializeComponent();

         btnSave.Enabled = false;
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);
         dsAction = (DataSet<string, Action>)DataModule.Get(Action.OBJECT_NAME) ?? 
            new DataSet<string, Action>(Action.OBJECT_NAME);
         dsActionType = (DataSet<string, ActionType>)DataModule.Get(ActionType.OBJECT_NAME) ??
            new DataSet<string, ActionType>(ActionType.OBJECT_NAME);

         dtpFrom.Value = DateTime.Now.Date.AddDays(-7);
         dtpTill.Value = DateTime.Now.Date.AddDays(7);
      }

      private void btnAddAction_Click(object sender, EventArgs e)
      {
         if (cbActionType.Items.Count == 0)
         {
            MessageBox.Show("Создайте, пожалуйста, типы для акций и обновите это окно", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            return;
         }

         Action act = new Action();
         act.id = Action.GenId();
         act.start = DateTime.Now;
         act.finish = DateTime.Now;
         act.items = new List<ActionItem>();
         act.actionType = (ActionType)cbActionType.Items[0];
         act.type = act.actionType.id;

         lbAction.Items.Add(act);
         lbAction.SelectedIndex = lbAction.Items.Count - 1;

         Dirty = true;
      }

      int lastSelection = -1;
      bool loadingAction = false;

      private void lbAction_SelectedIndexChanged(object sender, EventArgs e)
      {
         Action act = lbAction.SelectedItem as Action;

         if (act != null)
         {
            //bool svDirty = Dirty;
            loadingAction = true;

            cbActionType.SelectedItem = act.actionType;
            dtpStart.Value = act.start;
            dtpFinish.Value = act.finish;

            tbName.Text = act.name;
            tbDescr.Text = act.description;

            lbPrice.Items.Clear();
            foreach (ActionItem ai in act.items)
               lbPrice.Items.Add(ai.price);

            //Dirty = svDirty;
            loadingAction = false;
         }
         else
            ((ListBox)sender).SelectedIndex = lastSelection;
      }

      private void btnAddPrice_Click(object sender, EventArgs e)
      {
         Action act = lbAction.SelectedItem as Action;

         if (act != null)
         {
            List<Price> price = new List<Price>();
            price = FmSelectSKU.SelectItems(this, price, null, true);
            if (price != null)
            {
               lbPrice.Items.Clear();

               foreach (Price p in price)
               {
                  lbPrice.Items.Add(p);
                  ActionItem ai = new ActionItem();
                  ai.id = p.id;
                  ai.price = p;
                  act.items.Add(ai);
               }
            }
            Dirty = true;
         }
         else
            MessageBox.Show("Для того чтобы добавить товар выберите акцию");
      }

      private void dtpStart_ValueChanged(object sender, EventArgs e)
      {
         if (loadingAction)
            return;

         Action act = lbAction.SelectedItem as Action;
         if (act != null)
         {
            act.start = ((DateTimePicker)sender).Value.Date;
            lbAction.Items[lbAction.SelectedIndex] = act;
            Dirty = true;
         }
      }

      private void dtpFinish_ValueChanged(object sender, EventArgs e)
      {
         if (loadingAction)
            return;

         Action act = lbAction.SelectedItem as Action;
         if (act != null)
         {
            act.finish = ((DateTimePicker)sender).Value.Date;
            lbAction.Items[lbAction.SelectedIndex] = act;
            Dirty = true;
         }
      }

      private void btnDelPrice_Click(object sender, EventArgs e)
      {
         Action act = lbAction.SelectedItem as Action;

         if (act != null)
         {
            Price p = lbPrice.SelectedItem as Price;

            if (p != null)
            {
               if (MessageBox.Show(this, "Выбранный товар будет удален, удалить?", "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
               {
                  lbPrice.Items.Remove(p);

                  foreach (ActionItem ai in act.items)
                     if (ai.id.Equals(p.id))
                     {
                        act.items.Remove(ai);
                        break;
                     }
               }
               Dirty = true;
            }
            else
               MessageBox.Show("Для того чтобы удалить товар выберите товар");
         }
         else
            MessageBox.Show("Для того чтобы удалить товар выберите акцию");
      }

      private void btnDelAction_Click(object sender, EventArgs e)
      {
          Action act = lbAction.SelectedItem as Action;

          if (act != null &&
             MessageBox.Show(this, "Выбранный товар будет удален, удалить?", 
             "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
          {
             dsRemAction.Add(act);
             Dirty = true;
             lbAction.Items.Remove(act);
          }
      }

      bool SaveChanges()
      {
         SimpleDataSet<Action> dataset = new SimpleDataSet<Action>(Action.OBJECT_NAME, false);
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmvSet = new List<IDataSet>();

         foreach (object o in lbAction.Items)
         {
            Action at = (Action)o;
            dataset.Add(at);
         }

         wrSet.Add(dataset);
         rmvSet.Add(dsRemAction);

         bool ret = DataModule.UpdateDataSet(wrSet, rmvSet, null, Config.GetConfig().GetConnection());
         if (!ret)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         else
         {
            dsRemAction.Clear();
            Dirty = false;
         }

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }
      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      void RefreshData()
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         List<IDataSet> updSet = new List<IDataSet>();

         if (dsPrice.Count == 0)
            updSet.Add(dsPrice);

         updSet.Add(dsActionType);
         updSet.Add(dsAction);
         string dateFilter = String.Format("(\"start\" >= ToDate('{0:dd/MM/yyyy}') and \"start\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')) or ((\"dateEnd\" >= ToDate('{0:dd/MM/yyyy}') and \"dateEnd\" <= ToDate('{1:dd/MM/yyyy} 23:59:59')))",
            dtpFrom.Value, dtpTill.Value);

         dsAction.Filter = dateFilter;

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
            updSet, FmWait.ProgressIndicator));
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate()
         {
            lbAction.Items.Clear();

            foreach (Action at in dsAction.Data)
               lbAction.Items.Add(at);

            cbActionType.Items.Clear();
            foreach (ActionType tp in dsActionType.Data)
               cbActionType.Items.Add(tp);
         }));
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void cbActionType_SelectedIndexChanged(object sender, EventArgs e)
      {
         if (loadingAction)
            return;
         Action act = lbAction.SelectedItem as Action;
         ActionType type = cbActionType.SelectedItem as ActionType;
         if (act != null && type != null)
         {
            act.actionType = type;
            act.type = type.id;

            lbAction.Items[lbAction.SelectedIndex] = act;
            Dirty = true;
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (Dirty)
         {
            DialogResult res = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Cancel || (res == DialogResult.Yes && !SaveChanges()) )
            {
               e.Cancel = true;
               return;
            }
         }
         base.OnClosing(e);
      }

      bool Dirty
      {
         get { return btnSave.Enabled; }
         set { btnSave.Enabled = value; }
      }

      private void OnDataTextChanged(object sender, EventArgs e)
      {
         if (loadingAction)
            return;

         Action cur = lbAction.SelectedItem as Action;
         if (cur != null)
         {
            cur.description = tbDescr.Text;
            cur.name = tbName.Text;
            Dirty = true;
         }
      }
   }
}

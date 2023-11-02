/*
 * Copyright (C), 2011, Гильдия разработчиков
 * 
 * Редактирование плана пункта плана
 * 
 * kki   26/03/2011   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPlanItemEdit : Form
   {
      private static FmPlanItemEdit instance;
      private static IPlanItemType planItem;

      private PlanItemEditMediator mediator;
     

      internal static IPlanItemType ShowForm(FmPlanEditData planData,
         IPlanItemType planItem)
      {
         if (instance == null)
         {
            instance = new FmPlanItemEdit(planData, planItem);

            if (instance.ShowDialog() == DialogResult.OK)
               return FmPlanItemEdit.planItem;
         }

         return null;
      }

      private FmPlanItemEdit(FmPlanEditData planData, IPlanItemType planItem)
      {
         InitializeComponent();

         this.mediator = new PlanItemEditMediator(planData, this, planItem == null);
         FmPlanItemEdit.planItem = planItem;
      }

      private void init()
      {
         cbName.Items.AddRange(PlanItemFactory.CreateItemList().ToArray());
         cbName.Sorted = true;
         cbName.SelectedItem = planItem;
      }

      private void FmPlanItemEdit_Load(object sender, EventArgs e)
      {
         init();

         if (planItem != null)
            mediator.fillControlFromObject();
      }

      private void cbName_SelectionChangeCommitted(object sender, EventArgs e)
      {
         IPlanItemType newPlanItem = ((ComboBox)sender).SelectedItem as IPlanItemType;

         if (newPlanItem == null)
            return;

         if (planItem == null)
         {
            ApplyToNewPlanItem(newPlanItem);
            return;
         }

         if (!newPlanItem.Equals(planItem))
         {
            if (promptToChangePlanType())
               ApplyToNewPlanItem(newPlanItem);

            else
               ((ComboBox)sender).SelectedItem = planItem;
         }
      }

      private void ApplyToNewPlanItem(IPlanItemType newPlanItem)
      {
         UpdateUnitsCBItems(newPlanItem.GetUnits().ToArray());
         planItem = newPlanItem;

         mediator.OnChange(planItem);
      }

      private void UpdateUnitsCBItems(object[] values)
      {
         cbUnit.Items.Clear();
         cbUnit.Items.AddRange(values);
         cbUnit.Text = string.Empty;
      }

      private bool promptToChangePlanType()
      {
         const string TITLE_STR = "Вопрос";
         const string MSG_STR = "При изменение типа, старые данные будут утеряны, применить?";

         return MessageBox.Show(MSG_STR, TITLE_STR, 
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         BeginInvoke(new EmptyParamHandler(delegate
         {
            btnEdit.Enabled = false;

            List<Price> price = new List<Price>();

            foreach (object obj in lbPriceItems.Items)
            {
               if (obj != null && obj is Price)
                  price.Add((Price)obj);
            }

            FmMultySelectSKU selectSKU = new FmMultySelectSKU(price,
               new PriceListSelected(ShowSelectedPrice),
               new EndOfSelection(EndOfPriceSelected));
            selectSKU.Show();
         }));
      }

      private void EndOfPriceSelected()
      {
         btnEdit.Enabled = true;
      }

      private void ShowSelectedPrice(List<Price> list)
      {
         lbPriceItems.SuspendLayout();
         lbPriceItems.Items.Clear();

         try
         {
            lbPriceItems.Items.AddRange(list.ToArray());
         }
         finally
         {
            lbPriceItems.ResumeLayout();
         }

         lblCount.Text = lbPriceItems.Items.Count.ToString();
      }

      delegate void PriceListSelected(List<Price> list);
      delegate void EndOfSelection();

      /// <summary>
      /// Диалоговое окно для выбора SKU
      /// </summary>
      class FmMultySelectSKU : FmSelectSKU
      {
         private event PriceListSelected priceListSelected;
         private event EndOfSelection formClosed;

         private List<Price> currSelection;

         internal FmMultySelectSKU(List<Price> selected, 
            PriceListSelected priceListSelected, EndOfSelection formClosed)
         {
            colorFilter.Visible = false;
            tvArticles.CheckBoxes = true;
            currSelection = selected;   
            
            this.priceListSelected += priceListSelected;
            this.formClosed = formClosed;
            FormClosed += new FormClosedEventHandler(FmMultySelectSKU_FormClosed);
          }

         void FmMultySelectSKU_FormClosed(object sender, FormClosedEventArgs e)
         {
            if (formClosed != null)
               formClosed();
         }

         private void SetSelection(List<Price> selected)
         {
            tvArticles.SuspendLayout();
            tvArticles.AfterCheck -= new TreeViewEventHandler(tvArticles_AfterCheck);
            try
            {
               foreach (Price price in selected)
               {
                  CheckNodeInTree(tvArticles.Nodes, price);
               }
            }
            finally
            {
               tvArticles.AfterCheck += new TreeViewEventHandler(tvArticles_AfterCheck);
               tvArticles.ResumeLayout();
            }

         }

         private bool CheckNodeInTree(TreeNodeCollection nodes, Price data)
         {
            int check_node_count = 0;

            foreach (TreeNode node in nodes)
            {
               if (node.Nodes.Count > 0)
                  node.Checked = CheckNodeInTree(node.Nodes, data);

               if (node.Tag is Price && (node.Tag as Price).Equals(data))
                  node.Checked = true;

               if (node.Checked)
                  check_node_count++;
            }

            return check_node_count == nodes.Count;
               
         }
         
         /// <summary>
         /// При изменении статуса узла, 
         /// изменить и статус дочерних узлов/листьев
         /// </summary>
         /// <param name="sender"></param>
         /// <param name="e"></param>
         void tvArticles_AfterCheck(object sender, TreeViewEventArgs e)
         {
            ((TreeView)sender).SuspendLayout();

            try
            {
               if (e.Node == null || e.Node.Nodes == null)
                  return;

               foreach (TreeNode node in e.Node.Nodes)
               {
                  node.Checked = e.Node.Checked;
               }
            }
            finally
            {
               ((TreeView)sender).ResumeLayout();
            }
         }

         public List<Price> Selected
         {
            get
            {
               List<Price> result = new List<Price>();
               CollectCheckedNodes(tvArticles.Nodes, result);

               return result;
            }
         }

         private void CollectCheckedNodes(TreeNodeCollection nodes, List<Price> collect)
         {
            foreach (TreeNode node in nodes)
            {
               if (node.Nodes.Count > 0)
                  CollectCheckedNodes(node.Nodes, collect);

               if (node.Checked && node.Tag is Price)
                  collect.Add((Price)node.Tag);
            }
         }

         protected override void FmSelectSKU_FormClosing(object sender, FormClosingEventArgs e)
         {
            if (DialogResult == DialogResult.OK && priceListSelected != null)
               priceListSelected(Selected);
         }

         protected override void FillTreeView(TreeView treeView, 
            GRSoft.Network.DataSet<string, ManagerFolder> dsManagerFolder, 
            GRSoft.Network.DataSet<string, Price> dsPrice)
         {
            base.FillTreeView(treeView, dsManagerFolder, dsPrice);

            SetSelection(currSelection);
         }
      }

      class PlanItemEditMediator
      {
         private FmPlanItemEdit form;
         private FmPlanEditData planData;
         private bool createNewItem;

         public PlanItemEditMediator(FmPlanEditData planData, FmPlanItemEdit form,
            bool createNewItem)
         {
            this.form = form;
            this.planData = planData;
            this.createNewItem = createNewItem;

            form.btnEdit.Enabled = false;
            form.clear.Enabled = false;
            form.name.Enabled = false;
            form.lbPriceItems.Enabled = false;
         }

         public void fillControlFromObject()
         {
            for (int i = 0; i < form.cbName.Items.Count; i++)
            {
               if (form.cbName.Items[i].Equals(planItem))
               {
                  form.cbName.Items[i] = planItem;
                  form.cbName.SelectedIndex = i;
                  form.UpdateUnitsCBItems(planItem.GetUnits().ToArray());
                  OnChange(planItem);
                  break;
               }
            }

            for (int i = 0; i < form.cbUnit.Items.Count; i++)
            {
               if (form.cbUnit.Items[i].Equals(planItem.Unit))
               {
                  form.cbUnit.Items[i] = planItem.Unit;
                  form.cbUnit.SelectedIndex = i;
                  break;
               }
            }

            form.tbQuantity.Text = planItem.Value.ToString();

            if (planItem is ISKUPlanItem)
            {
               ISKUPlanItem skuPlan = planItem as ISKUPlanItem;

               form.ShowSelectedPrice(skuPlan.SKU);
            }

            form.name.Text = planItem.Text;
         }

         public void OnChange(IPlanItemType planItemType)
         {
            bool controlsForSKU = planItemType is ISKUPlanItem;

            form.btnEdit.Enabled = controlsForSKU;
            form.clear.Enabled = controlsForSKU;
            form.name.Enabled = controlsForSKU;
            form.lbPriceItems.Enabled = controlsForSKU;

            form.lbPriceItems.Items.Clear();
            form.tbQuantity.Text = string.Empty;
         }

         public bool fillPlanItem(IPlanItemType planItemType)
         {
            return checkPlanItem() && checkPlanUnit() && 
               setQuant(planItemType) && setSKU(planItemType);
         }

         private bool setSKU(IPlanItemType planItemType)
         {
            if (planItemType is ISKUPlanItem)
            {
               if (form.lbPriceItems.Items.Count <= 0)
               {
                  form.btnEdit.Focus();

                  const string TITLE_STR = "Ошибка";
                  const string MSG_STR = "Для плана SKU необходимо выбрать хотя бы один SKU.";

                  MessageBox.Show(MSG_STR, TITLE_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);

                  return false;
               }

               ISKUPlanItem skuPlan = planItemType as ISKUPlanItem;
               skuPlan.SKU.Clear();

               foreach (object obj in form.lbPriceItems.Items)
               {
                  if (obj is Price)
                     skuPlan.SKU.Add(obj as Price);
               }

               planItemType.Text = form.name.Text;
            }
            return true;
         }

         private bool checkPlanItem()
         {
            const string TITLE_STR = "Ошибка";
            bool result = true;

            if (planItem == null)
            {
               form.cbName.Focus();
               const string MSG_STR = "Укажите тип плана";

               MessageBox.Show(MSG_STR, TITLE_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);

               result = false;
            }
            else if (createNewItem && !planData.CheckForItemType(planItem.PlanItemCode))
            {
               form.cbName.Focus();

               string MSG_STR = String.Format("Пункт плана \"{0}\" уже был создан.", planItem.ToString());
               
               MessageBox.Show(MSG_STR, TITLE_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);

               result = false;
            }
            
            if (result == false)
               form.cbName.Focus();

            return result;
         }

         private bool checkPlanUnit()
         {
            if (planItem.Unit == null)
            {
               form.cbUnit.Focus();

               const string TITLE_STR = "Ошибка";
               const string MSG_STR = "Выберите единицы измерения";

               MessageBox.Show(MSG_STR, TITLE_STR, MessageBoxButtons.OK, MessageBoxIcon.Error);

               return false;
            }
            else
               return true;
         }

         private bool setQuant(IPlanItemType planItemType)
         {
            bool result = false;

            try
            {
               planItemType.Value = Double.Parse(form.tbQuantity.Text);

               result = true;
            }
            catch
            {
               result = false;

               form.tbQuantity.Focus();

               const string TITLE_STR = "Ошибка";
               const string MSG_STR = "Проверьте правильность ввода в поле количество!";

               MessageBox.Show(MSG_STR, TITLE_STR,MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

            return result;
         }
      }

      private void cbUnit_SelectionChangeCommitted(object sender, EventArgs e)
      {
         IUnitItemType selectedUnit = (IUnitItemType)((sender as ComboBox).SelectedItem);

         if (selectedUnit == null)
            return;

         planItem.Unit = selectedUnit;
      }

      private void FmPlanItemEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
            e.Cancel = !mediator.fillPlanItem(planItem);
      }

      private void FmPlanItemEdit_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void clear_Click(object sender, EventArgs e)
      {
         lbPriceItems.Items.Clear();
      }
   }
}
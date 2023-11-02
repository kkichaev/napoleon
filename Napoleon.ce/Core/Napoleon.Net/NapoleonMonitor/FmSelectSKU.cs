/*
 * Copyright (C), 2010, Гильдия разработчиков
 * 
 * Выбор объекта Price из списка с разбивкой на Folders и 
 * возможностью поиска
 * 
 * kki   26/09/2010   creating
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
   internal delegate void TSSelectColorHandler(Color clr, int index);

   public partial class FmSelectSKU : Form, ATCFilter
   {
      private SysColors colors = null;
      TreeSearch treeSearch;

      #region Public methods
      /// <summary>
      /// Функция доступа к форме
      /// </summary>
      /// <param name="owner">владелец</param>
      /// <param name="price">выбранный Price</param>
      /// <returns>DialogResult OK, CANCEL</returns>
      public static DialogResult SkuDialogQuery(IWin32Window owner, out Price price)
      {
         FmSelectSKU form = new FmSelectSKU();

         DialogResult result = form.ShowDialog(owner);
         price = form.SelectedPrice;

         return result;
      }

      public static List<Price> SelectItems(IWin32Window owner, List<Price> checkList, string userID)
      {
         return SelectItems(owner, checkList, userID, false);
      }

      public static List<Price> SelectItems(IWin32Window owner, List<Price> checkList, string userID, bool checkingFolder)
      {
         FmSelectSKU form = new FmSelectSKU(checkList, userID);
         form.checkingFolder = checkingFolder;

         if (form.ShowDialog(owner) == DialogResult.OK)
            return form.checkedPrice;

         return null;
      }

      private void AddChecked(List<Price> ret, TreeNodeCollection tnc)
      {
         foreach (TreeNode tn in tnc)
         {
            Price p = tn.Tag as Price;
            if (p != null && tn.Checked)
               ret.Add(p);
            if (tn.Nodes.Count > 0)
               AddChecked(ret, tn.Nodes);
         }
      }

      internal SysColors Colors
      {
         get { return colors; }
         set
         {
            colors = value;
            if (colors == null)
            {
               colorFilter.Visible = false;
               colorFilter.SelectColor -= new TSSelectColorHandler(colorFilter_SelectColor);
            }
            else
            {
               colorFilter.Visible = true;
               colorFilter.Colors = value;
               colorFilter.SelectColor += new TSSelectColorHandler(colorFilter_SelectColor);
            }
         }
      }

      void colorFilter_SelectColor(Color clr, int index)
      {
         if (index == 0)
            treeSearch.ClearFind();
         else
         {
            TreeNode[] nodes = new TreeNode[tvArticles.Nodes.Count];
            tvArticles.Nodes.CopyTo(nodes, 0);

            tvArticles.BeginUpdate();
            tvArticles.Nodes.Clear();
            int argb = clr.ToArgb();
            foreach (TreeNode fn in nodes)
               AddColorItem(tvArticles.Nodes, fn, argb);
            tvArticles.EndUpdate();
         }
      }

      void AddColorItem(TreeNodeCollection dest, TreeNode source, int color)
      {
         if (source.ForeColor.ToArgb() == color)
         {
            TreeNode dn = new TreeNode(source.Text);
            dn.Tag = source.Tag;
            dn.ForeColor = source.ForeColor;
            dest.Add(dn);
         }
         foreach (TreeNode tn in source.Nodes)
            AddColorItem(dest, tn, color);
      }
      #endregion

      #region Private methods

      /// <summary>
      /// Коструктор
      /// </summary>
      protected FmSelectSKU()
      {
         InitializeComponent();

         treeSearch = new TreeSearch(tvArticles, tstbFind.TextBox);
      }

      protected List<Price> checkedPrice = null;
      private string userID = null;
      protected FmSelectSKU(List<Price> checkList, string userID) : this()
      {
         tvArticles.CheckBoxes = true;
         checkedPrice = checkList;
         this.userID = userID;
      }

      //Выбранный прайс
      public Price SelectedPrice { get { return GetPrice(); } }

      //Получить выделенный прайс
      private Price GetPrice()
      {
         return tvArticles.SelectedNode == null ? null : tvArticles.SelectedNode.Tag as Price;
      }

      DataSet<string, ManagerFolder> dsManagerFolder;
      DataSet<string, Price> dsPrice;
      private void FetchDataSets()
      {
         if (userID == null)
         {
            dsManagerFolder = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
               new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
            dsManagerFolder.Filter = DataUtils.USERID_IS_NULL_STR;
         
            dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
               new DataSet<string, Price>(Price.OBJECT_NAME);
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         }
         else
         {
            dsManagerFolder = (DataSet<string, ManagerFolder>)DataModule.GetUserDataSet(userID, ManagerFolder.OBJECT_NAME, typeof(DataSet<string, ManagerFolder>));
            dsManagerFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userID), dsManagerFolder.Name);
            
            dsPrice = (DataSet<string, Price>)DataModule.GetUserDataSet(userID, Price.OBJECT_NAME, typeof(DataSet<string, Price>));
            dsPrice.Command = new ServerCommand(Commands.Impersonate(Commands.GET, userID), dsPrice.Name);
         }

         List<IDataSet> updList = new List<IDataSet>();

         if (dsManagerFolder.Count == 0)
            updList.Add(dsManagerFolder);

         if (dsPrice.Count == 0)
            updList.Add(dsPrice);

         if (updList.Count > 0)
         {
            DataModule.DataProcessed += DataLoaded;
            DataModule.OnDataResponceError += DataConnectionError;

            FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
                  updList, FmWait.ProgressIndicator));
         }
         else
         {
            FillTreeView(tvArticles,
               (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME),
               (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME));
         }
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         EndDataProcessed();
         MessageBox.Show(e.Msg);
      }

      //Очистить события выборки
      private void ClearRegisterDataModuleEvents()
      {
         DataModule.DataProcessed -= DataLoaded;
         DataModule.OnDataResponceError -= DataConnectionError;
      }

      //Выполнит действия по окончанию процесса выборки данных
      private void EndDataProcessed()
      {
         ClearRegisterDataModuleEvents();
         FmWait.CloseForm();
      }

      //Окончание выборки заполнить визуальные компоненты
      void DataLoaded(object sender, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         BeginInvoke(new EmptyParamHandler(delegate 
         {
            FillTreeView(tvArticles, dsManagerFolder, dsPrice);
            FmWait.CloseForm();
         }));
      }

      protected bool PriceChecked(Price p)
      {
         if (checkedPrice != null)
            foreach (Price cp in checkedPrice)
               if (cp.id.CompareTo(p.id) == 0)
                  return true;

         return false;
      }

      //Заполнить дерево
      protected virtual void FillTreeView(TreeView treeView, DataSet<string, ManagerFolder> dsManagerFolder, 
         DataSet<string, Price> dsPrice)
      {
         ArticlesTreeConstructorWithCondition a = new ArticlesTreeConstructorWithCondition(tvArticles, dsManagerFolder, dsPrice,this);
         a.MakeArticlesTree(0, 1, ((checkedPrice==null) ? (IsPriceChecked)null : PriceChecked));
      }

      //OK
      private void tsbOK_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.OK;
         Close();
      }

      //Отменить
      private void tsbCancel_Click(object sender, EventArgs e)
      {
         DialogResult = DialogResult.Cancel;
         Close();
      }

      //Не будем разрешать закрыть форму, если пользователь нажал "ОК",
      //но прайс не был выбран
      protected virtual void FmSelectSKU_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tvArticles.CheckBoxes)
            {
               if (checkedPrice == null)
                  checkedPrice = new List<Price>();
               else
                  checkedPrice.Clear();
               AddChecked(checkedPrice, tvArticles.Nodes);
            }
            else
            {
               const string ERROR_MSG = "Не выбран подходящий объект";
               const string ERROR_TITLE_MSG = "Ошибка";

               if (SelectedPrice == null)
               {
                  e.Cancel = true;
                  MessageBox.Show(this, ERROR_MSG, ERROR_TITLE_MSG, MessageBoxButtons.OK, MessageBoxIcon.Error);
               }
            }
         }
      }


      #endregion

      #region ATCFilter Members

      //Фильтр для отображения узлов в дереве
      //Отображаем узлы в которых есть "товар"
      public bool ApplyTreeNodeFilter(TreeNode treeNode)
      {
         if (treeNode.Tag is Price || treeNode.Nodes.Count > 0)
            return true;
         else
            return false;
      }

      #endregion

      private void FmSelectSKU_Load(object sender, EventArgs e)
      {
         FetchDataSets();
      }

      private void tsbClearSearch_Click(object sender, EventArgs e)
      {
         treeSearch.ClearFind();
      }

      void CheckNodes(TreeNodeCollection nodes, bool check)
      {
         foreach (TreeNode node in nodes)
         {
            node.Checked = check;
            if (node.Nodes.Count > 0)
               CheckNodes(node.Nodes, check);
         }
      }

      protected bool checkingFolder = false;
      private void tvArticles_AfterCheck(object sender, TreeViewEventArgs e)
      {
         if( checkingFolder )
            CheckNodes(e.Node.Nodes, e.Node.Checked);
      }
   }

   public class TSColorFilter : ToolStripDropDownButton
   {
      internal SysColors Colors
      {
         set
         {
            DropDownItems.Clear();

            Size sz = new Size(235, 22);
            ToolStripMenuItem m1 = new ToolStripMenuItem(ColorMenu.CreateImage(sz, Color.Black));
            m1.Checked = true;
            m1.Tag = Color.Black;
            DropDownItems.Add(m1);

            foreach (Color clr in value)
            {
               ToolStripMenuItem mi = new ToolStripMenuItem(ColorMenu.CreateImage(sz, clr));
               mi.Tag = clr;
               DropDownItems.Add(mi);
            }
         }
      }

      protected override void OnDropDownItemClicked(ToolStripItemClickedEventArgs e)
      {
         ToolStripMenuItem mi = (e.ClickedItem as ToolStripMenuItem);
         if (!mi.Checked)
         {
            foreach (ToolStripMenuItem ci in DropDownItems)
            {
               if (ci.Checked)
                  ci.Checked = false;
            }
            mi.Checked = true;
         }

         if (SelectColor != null)
            SelectColor((Color)mi.Tag, DropDownItems.IndexOf(mi));

         base.OnDropDownItemClicked(e);
      }

      internal event TSSelectColorHandler SelectColor;
   }
}
/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using GRSoft.Network;
using System.Collections.Generic;
using System.Windows.Forms;
using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   class FmPriceEx : FmPrice
   {
      DataSet<string, NoDiscountPrice> noDs = new DataSet<string, NoDiscountPrice>(NoDiscountPrice.OBJECT_NAME, false);
      DataSet<string, NoDiscountPrice> rmvDs = new DataSet<string, NoDiscountPrice>(NoDiscountPrice.OBJECT_NAME, false);
      DataGridViewCheckBoxColumn clmnDs = new DataGridViewCheckBoxColumn();
      
      public FmPriceEx() : base()
      {
         this.clmnDs.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnDs.HeaderText = "Нет скидки";
         this.clmnDs.Name = "clmnDs";
         this.clmnDs.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnDs.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         clmnDs.Width = 110;

         tgvPrice.Columns.Add(clmnDs);
         tgvPrice.CellMouseDown += new DataGridViewCellMouseEventHandler(tgvPrice_CellMouseDown);
      }

      void tgvPrice_CellMouseDown(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.ColumnIndex == clmnDs.Index)
         {
            DataGridViewRow r = tgvPrice.Rows[e.RowIndex];
            if (r.Tag is Price)
            {
               Price p = (Price)r.Tag;
               DataGridViewCell cell = r.Cells[clmnDs.Index];
               bool val = (bool)cell.Value;
               val = !val;
               cell.Value = val;
               ChangeDiscount(p, val);
            }
         }
      }

      private void ChangeDiscount(Price p, bool val)
      {
         NoDiscountPrice np = new NoDiscountPrice();
         np.id = p.id;
         if (val)
         {
            if (!noDs.ContainsKey(p.id))
            {
               noDs.Add(p.id, np);

               btnSave.Enabled = true;
            }
         }
         else
         {
            if (noDs.ContainsKey(p.id))
            {
               noDs.Remove(p.id);
               rmvDs.Add(p.id, np);
               btnSave.Enabled = true;
            }
         }
      }

      protected override void BeforeRefresh(List<IDataSet> updSet)
      {
         updSet.Add(noDs);
         rmvDs.Clear();
      }

      protected override void BeforeWrite(List<IDataSet> wrSet, List<IDataSet> rmvSet, List<ReplacedSet> rpcSet)
      {
         if (noDs.Count > 0)
            wrSet.Add(noDs);
         if (rmvDs.Count > 0)
            rmvSet.Add(rmvDs);
      }

      protected override TreeGridNode AddPriceNode(GRSoft.UILib.TreeGridNodeCollection parent, Price p)
      {
         TreeGridNode ret = base.AddPriceNode(parent, p);
         DataGridViewCell c = ret.Cells[clmnDs.Index];
         c.Value = noDs.ContainsKey(p.id);
         return ret;
      }
   }

   public class NoDiscountPrice : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "NoDiscountPrice";

      [KeyField]
      public string id;
   }
}

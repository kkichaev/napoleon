using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class OffTakeCoeffEditor : Form
   {
      DataSet<String, OffTakeCoeff> dsCoef = new DataSet<string, OffTakeCoeff>(OffTakeCoeff.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, Price> dsPrice;

      static readonly double OFFTAKE_COEF = 1.5;

      public OffTakeCoeffEditor()
      {
         InitializeComponent();
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ?? 
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);

      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();

         upd.Add(dsCoef);
         if (dsPrice.Count == 0)
         {
            upd.Add(dsPrice);
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         }

         if (dsFolders.Count == 0)
         {
            upd.Add(dsFolders);
            dsFolders.Filter = DataUtils.USERID_IS_NULL_STR;
         }

         DataModule.DataProcessed += new EventHandler((o, e) =>
         {
            DataModule.ClearEvents();
            FmWait.CloseForm();

            Invoke(new EmptyParamHandler(delegate { DoLoadData(); }));
         });
         DataModule.OnDataResponceError += new EventDataResponseError(FmWait.StdErrorHandler);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator));
      }

      virtual protected TreeGridNode AddPriceNode(TreeGridNodeCollection parent, Price p)
      {
         object coef = dsCoef.ContainsKey(p.id) ? (object)dsCoef[p.id].coef : null;
         TreeGridNode result = parent.Add(p.name, coef);
         result.Tag = p;
         return result;
      }

      virtual protected TreeGridNode AddFolderNode(TreeGridNodeCollection parent, ManagerFolder f)
      {
         TreeGridNode result = parent.Add(dsFolders[f.id].name, null);
         result.Tag = f;
         //result.Expand();
         return result;
      }

      private void FillGridRecursive(TreeNode node, TreeGridNodeCollection parent)
      {
         if (node.Tag is ManagerFolder)
         {
            TreeGridNode child = AddFolderNode(parent, (ManagerFolder)node.Tag);

            foreach (TreeNode n in node.Nodes)
               FillGridRecursive(n, child.Nodes);
         }
         else if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;
            AddPriceNode(parent, p).Tag = p;
         }
      }

      private void DoLoadData()
      {
         TreeView tmpTree = new TreeView();
         ArticlesTreeConstructor atc = new ArticlesTreeConstructor(tmpTree, dsFolders, dsPrice);
         atc.MakeArticlesTree();

         dgvPrice.SuspendLayout();
         dgvPrice.Nodes.Clear();
         dgvPrice.Rows.Clear();

         foreach (TreeNode n in tmpTree.Nodes)
            FillGridRecursive(n, dgvPrice.Nodes);

         dgvPrice.ResumeLayout();
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         rpl.Add(new ReplacedSet(dsCoef));

         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      private void dgvPrice_CellEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (Visible == false)
            return;
         if (e.ColumnIndex == clmnCoef.DisplayIndex)
         {
            TreeGridNode row = (TreeGridNode)dgvPrice.Rows[e.RowIndex];
            Price p = row.Tag as Price;
            if( p == null )
               return;

            double coef = OFFTAKE_COEF;
            if (dsCoef.ContainsKey(p.id))
               coef = dsCoef[p.id].coef;
            InputCoef ic = new InputCoef();
            ic.Coef = coef;
            ic.Location = new Point(Left + (Width - ic.Width) / 2, Top + (Height - ic.Height) / 2);
            if (ic.ShowDialog() == DialogResult.OK)
            {
               DataGridViewCell cell = row.Cells[e.ColumnIndex];

               coef = ic.Coef;
               if (coef == OFFTAKE_COEF || coef <= 0)
               {
                  dsCoef.Remove(p.id);
                  cell.Value = null;
               }
               else
               {
                  OffTakeCoeff ofc = new OffTakeCoeff();
                  ofc.id = p.id;
                  ofc.coef = coef;
                  dsCoef[p.id] = ofc;
                  cell.Value = coef;
               }
               dgvPrice.InvalidateCell(cell);
               tsbSave.Enabled = true;
            }
         }
      }
   }

   class OffTakeCoeff : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OffTakeCoeff";

      [KeyField]
      public String id = "";

      [Precision(2)]
      public double coef = 0;
   }
}

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
      DataSet<int, CommonConfig> dsConfig;
      DataSet<String, OffTakeCoeff> dsCoef = new DataSet<string, OffTakeCoeff>(OffTakeCoeff.OBJECT_NAME, false);
      DataSet<String, FolderOffTakeCoeff> dsFolderCoef = new DataSet<string, FolderOffTakeCoeff>(FolderOffTakeCoeff.OBJECT_NAME, false);
      DataSet<string, ManagerFolder> dsFolders;
      DataSet<string, Price> dsPrice;

      static readonly double OFFTAKE_COEF = 1.5;
      static readonly string OFFTAKE_KEY = "OFFTAKE_KEY";

      double defCoef = OFFTAKE_COEF;

      private const string COEF_TEXT = "Общий коеффициент: {0}";

      public OffTakeCoeffEditor()
      {
         InitializeComponent();

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsFolders = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ?? 
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ?? new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
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
         upd.Add(dsFolderCoef);

         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";
         upd.Add(dsConfig);

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
         object coef = dsFolderCoef.ContainsKey(f.id) ? (object)dsFolderCoef[f.id].coef : null;
         TreeGridNode result = parent.Add(dsFolders[f.id].name, coef);
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

        CommonConfig cc = ConfigUtils.GetCommonConfig(dsConfig, new ConfigKeyItems(OFFTAKE_KEY));

         if (cc != null)
         {
            try
            {
               defCoef = Double.Parse(cc.value);
            }
            catch (Exception)
            {

            }
         }

         FillControls();
      }

      private void FillControls()
      {
         btnCoeff.Text = string.Format(COEF_TEXT, defCoef);


         foreach (ManagerFolder f in dsFolders.Values)
            if (!dsFolderCoef.ContainsKey(f.id))
            {
               FolderOffTakeCoeff fo = new FolderOffTakeCoeff();
               fo.id = f.id;
               fo.coef = defCoef;
               dsFolderCoef.Add(f.id, fo);
            }


         foreach (Price p in dsPrice.Values)
         {
            if (!dsCoef.ContainsKey(p.id))
            {
               OffTakeCoeff ot = new OffTakeCoeff();
               ot.id = p.id;
               ot.coef = defCoef;
               dsCoef.Add(p.id, ot);
            }
         }

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
         CommonConfig cc = new CommonConfig();
         cc.key = OFFTAKE_KEY;
         cc.value = defCoef.ToString();

         DataSet<int, CommonConfig> ds = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
         ds.Add(1, cc);

         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(ds);
         wrSet.Add(dsCoef);
         wrSet.Add(dsFolderCoef);

         bool ret = DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection());
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
            ManagerFolder f = row.Tag as ManagerFolder;

            if (f != null)
            {
               double coef = OFFTAKE_COEF;
               if (dsFolderCoef.ContainsKey(f.id))
                  coef = dsFolderCoef[f.id].coef;

               InputCoef ic = new InputCoef();
               ic.Coef = coef;
               ic.Location = new Point(Left + (Width - ic.Width) / 2, Top + (Height - ic.Height) / 2);
               if (ic.ShowDialog() == DialogResult.OK)
               {
                  ChangeCoeff(row, ic.Coef, e.ColumnIndex);
                  dgvPrice.Invalidate();
                  tsbSave.Enabled = true;
               }
            }
            else
            {
               Price p = row.Tag as Price;
               if (p == null)
                  return;

               double coef = OFFTAKE_COEF;
               if (dsCoef.ContainsKey(p.id))
                  coef = dsCoef[p.id].coef;
               InputCoef ic = new InputCoef();
               ic.Coef = coef;
               ic.Location = new Point(Left + (Width - ic.Width) / 2, Top + (Height - ic.Height) / 2);

               if (ic.ShowDialog() == DialogResult.OK)
               {
                  coef = ic.Coef;
                  dsCoef[p.id].coef = coef;
                  DataGridViewCell cell = row.Cells[e.ColumnIndex];
                  cell.Value = coef;
                  dgvPrice.InvalidateCell(cell);
                  tsbSave.Enabled = true;
               }
            }
         }
      }

      private void ChangeCoeff(TreeGridNode node, double coef, int clmn)
      {
         foreach(TreeGridNode n in node.Nodes)
            ChangeCoeff(n,coef, clmn);

         if (node.Tag is Price)
         {
            Price p = (Price)node.Tag;

            if (dsCoef.ContainsKey(p.id))
               dsCoef[p.id].coef = coef;
         }
         else if (node.Tag is ManagerFolder)
         {
            ManagerFolder f = (ManagerFolder)node.Tag;

            if (dsFolderCoef.ContainsKey(f.id))
               dsFolderCoef[f.id].coef = coef;
         }

         DataGridViewCell cell = node.Cells[clmn];
         cell.Value = coef;
         //dgvPrice.InvalidateCell(cell);
      }

      private void tbCoeff_TextChanged(object sender, EventArgs e)
      {
         tsbSave.Enabled = true;
      }

      private void btnCoeff_Click(object sender, EventArgs e)
      {
         InputCoef ic = new InputCoef();
         ic.Coef = defCoef;
         ic.Location = new Point(Left + (Width - ic.Width) / 2, Top + (Height - ic.Height) / 2);

         if (ic.ShowDialog() == DialogResult.OK && ic.Coef != defCoef)
         {
            defCoef = ic.Coef;
            btnCoeff.Text = string.Format(COEF_TEXT, defCoef);
            tsbSave.Enabled = true;

            foreach (FolderOffTakeCoeff fo in dsFolderCoef.Values)
               fo.coef = defCoef;

            foreach (OffTakeCoeff fo in dsCoef.Values)
               fo.coef = defCoef;

            FillControls();
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

   class FolderOffTakeCoeff : OffTakeCoeff
   {
      public static new readonly string OBJECT_NAME = "FolderOffTakeCoeff";
   }
}

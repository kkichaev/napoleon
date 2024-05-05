using GRSoft.NapoleonManager.Properties;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.OleDb;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmMatrixDesignerEx : FmMatrixDesigner
   {
      static FmMatrixDesignerEx instance = null;

      public FmMatrixDesignerEx() 
      {
         ToolStripButton btn = new ToolStripButton();
         btn.Name = "btnLoad";
         btn.Image = Resources.excel;
         btn.Click += new System.EventHandler(btnLoad_Click);
         btn.ToolTipText = "Загрузить из Excel";
         toolStrip1.Items.Add(btn);
      }

      private void btnLoad_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();
         if (dlg.ShowDialog() == DialogResult.OK)
            LoadMatrix(dlg.FileName);
      }

      private void LoadMatrix(string file)
      {
         tvMatrix.BeginUpdate();
         Dictionary<string, TreeNode> mc = new Dictionary<string, TreeNode>(); // matrix cache
         Dictionary<string, Price> pc = new Dictionary<string, Price>(); // price cache

         foreach (TreeNode n in tvMatrix.Nodes)
            mc[n.Text] = n;

         foreach (Price p in dsPrice.Values)
            pc[p.name] = p;

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", file);
         var objConn = new OleDbConnection(connectionString);
         objConn.Open();
         var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

         if (dt == null)
         {
            return;
         }

         foreach (DataRow sh in dt.Rows)
         {
            string matrix = sh["TABLE_NAME"].ToString().Trim();
            var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + matrix + "]", connectionString);
            var ds = new DataSet();
            adapter.Fill(ds, matrix);

            DataTable data = ds.Tables[matrix];

            if (matrix.StartsWith("'"))
               matrix = matrix.Substring(1, matrix.Length - 3); // remove $ character
            else
               matrix = matrix.Substring(0, matrix.Length - 1); // remove $ character

            if (!mc.ContainsKey(matrix))
            {
               Matrix m = new Matrix();
               m.name = matrix;
               TreeNode n = new TreeNode();
               n.Text = matrix;
               n.Tag = m;
               tvMatrix.Nodes.Add(n);
               mc[matrix] = n;
               m.cdef = contractId;
            }

            foreach (DataRow row in data.Rows)
            {
               object[] r = row.ItemArray;
               string pn = r[0].ToString().Trim();

               if (pn.Length == 0)
                  break;

               if (!pc.ContainsKey(pn))
                  continue;

               Price p = pc[pn];

               MatrixItem mi = new MatrixItem();
               mi.id = p.id;
               mi.price = p;

               TreeNode mn = mc[matrix];
               Matrix m = mn.Tag as Matrix;
               
               bool cc = false;
               foreach (MatrixItem mii in m.items)
                  if (mii.id.Equals(p.id))
                  {
                     cc = true;
                     break;
                  }

               if (cc)
                  continue;

               m.items.Add(mi);
               TreeNode ppn = new TreeNode();
               ppn.Text = p.Name;
               ppn.Tag = mi;

               mn.Nodes.Add(ppn);
            }

         }

         tvMatrix.EndUpdate();
         tsbSave.Enabled = true;
      }

      string contractId = "";

      public static void Open(string contraciId)
      {
         if( instance == null )
         {
            instance = new FmMatrixDesignerEx();
            instance.contractId = contraciId;
            instance.Show();
         }
         else
         {
            instance.BringToFront();
            instance.RefreshData(true);
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData(true);
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void RefreshData(bool reload)
      {
         dsMatrix.Filter = "\"cdef\" = '" + contractId + "'";
         dsPrice.Filter = "\"cdef\" = '" + contractId + "'";

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         upd.Add(dsMatrix);

         FmWait.StdDataRefresh(this, upd, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         dsFolder.Clear();
         foreach (Price p in dsPrice.Data)
         {
            p.fid = p.group;
            if (dsFolder.ContainsKey(p.group) == false)
            {
               ManagerFolder mf = new ManagerFolder();
               mf.id = p.group;
               mf.name = p.group;
               mf.level = 0;
               dsFolder[mf.id] = mf;
            }
         }

         ControlsFillAfterLoaded();
      }

      protected override bool SaveData()
      {
         SimpleDataSet<Matrix> rmv = new SimpleDataSet<Matrix>(Matrix.OBJECT_NAME, false);
         DataSet<int, Matrix> curMatrix = (DataSet<int, Matrix>)GetMatrixDataSet();

         Dictionary<string, bool> cv = new Dictionary<string, bool>();
         foreach(Matrix m in curMatrix.Data)
         {
            m.cdef = contractId;
            cv.Add(m.name, true);
         }

         foreach(Matrix m in dsMatrix.Data)
         {
            m.cdef = contractId;
            if (!cv.ContainsKey(m.name))
               rmv.Add(m);
         }

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(curMatrix);

         List<IDataSet> rmvSet = new List<IDataSet>();
         rmvSet.Add(rmv);

         bool ret = DataModule.UpdateDataSet(upd, rmvSet, null, Config.GetConfig().GetConnection());
         if( ret )
            dsMatrix = curMatrix;
         return ret;
      }
   }
}

using ExcelLibrary.SpreadSheet;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceLoad : Form
   {
      public FmPriceLoad()
      {
         InitializeComponent();
      }

      private void btnOpen_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "Excel files|*.xls";
         if (openFileDialog1.ShowDialog() == DialogResult.OK)
         {
            tbPath.Text = openFileDialog1.FileName;
         }
      }

      private void bntLoad_Click(object sender, EventArgs e)
      {
         if (!File.Exists(tbPath.Text.Trim()))
         {
            MessageBox.Show(this, "Укажите путь к файлу Excel!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            tbPath.Focus();
            return;
         }


         Thread thread = new Thread(ExportData);
         thread.Start();
      }

      void CheckCostTypes(DBConnection conn)
      {
         SimpleDataSet<CommonConfig> cfg = new SimpleDataSet<CommonConfig>(CommonConfig.OBJECT_NAME, false);
         cfg.Filter = "userid is null or userid = ''";

         DataModule.RefreshGiveSets(conn, cfg, null).Join();
         bool finded = false;
         foreach (CommonConfig cc in cfg.Data)
         {
            if (cc.key == "ВидЦены")
            {
               finded = true;
               break;
            }
         }

         if (!finded)
         {
            CommonConfig cc = new CommonConfig();
            cc.key = "ВидЦены";
            cc.value = "Розница;Опт";
            SimpleDataSet<CommonConfig> cfgNew = new SimpleDataSet<CommonConfig>(CommonConfig.OBJECT_NAME, false);
            cfgNew.Add(cc);
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(cfgNew);
            DataModule.WriteDataSet(upd, conn);
         }
      }

      private void ExportData()
      {
         DBConnection conn = Config.GetConfig().GetConnection();
         CheckCostTypes(conn);

         string fileName = tbPath.Text.Trim();

         Workbook wb = Workbook.Load(fileName);
         Worksheet ws = wb.Worksheets[0];


         DataSet<string, Price> price = new DataSet<string, Price>(Price.OBJECT_NAME, false);
         SimpleDataSet<ManagerFolder> folders = new SimpleDataSet<ManagerFolder>(ManagerFolder.OBJECT_NAME, false);
         List<IDataSet> sets = new List<IDataSet>();
         sets.Add(price);
         sets.Add(folders);
         DataModule.RefreshGiveSets(conn, sets, null).Join();

         Dictionary<string, ManagerFolder> fdic = new Dictionary<string, ManagerFolder>();
         foreach(ManagerFolder mf in folders.Data)
         {
            fdic[mf.name] = mf;
         }

         for (int r = ws.Cells.FirstRowIndex + 1; r < ws.Cells.LastRowIndex; r++)
         {
            Row row = ws.Cells.Rows[r];
            string id = row.GetCell(0).StringValue.Trim();

            Price p;
            if(!price.TryGetValue(id, out p))
            {
               p = new Price();
               p.id = id;
               p.cost = new double[] { 0, 0 };
               price[id] = p;
            }

            p.inPack = 1;
            p.name = row.GetCell(2).StringValue.Trim();
            double val;
            if (Double.TryParse(row.GetCell(3).StringValue, out val))
               p.qty = val;

            string folder = row.GetCell(1).StringValue.Trim();
            ManagerFolder mf;
            if(!fdic.TryGetValue(folder, out mf))
            {
               mf = new ManagerFolder();
               mf.fid = Guid.NewGuid().ToString().Replace("-", "");
               mf.id = mf.fid;
               mf.name = folder;
               fdic[folder] = mf;
               folders.Add(mf);
            }
            p.fid = mf.fid;
            p.rem = 0;
            Price.CostItem c1 = new Price.CostItem();
            Price.CostItem c2 = new Price.CostItem();
            if (p.cost.Length > 0)
               c1.cost = p.cost[0];
            if(p.cost.Length > 1)
               c2.cost = p.cost[1];
            Double.TryParse(row.GetCell(4).StringValue, out c1.cost);
            Double.TryParse(row.GetCell(5).StringValue, out c2.cost);
            p.wrcost.Clear();
            p.wrcost.Add(c1);
            p.wrcost.Add(c2);
         }

         bool res = DataModule.UpdateDataSet(sets, null, null, conn);
         if(res == false)
         {
            Invoke(new InvokeDelegate(
               delegate {
                  MessageBox.Show("Ошибка при записи");
            }));
         }
         else
         {
            Invoke(new InvokeDelegate(
               delegate {
                  DialogResult = DialogResult.OK;
                  Close();
               }));
         }
      }
   }
}
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

using ExcelLibrary;
// using ExcelLibrary.SpreadSheet;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgLoad : Form
   {
      Agent agent;
      string fileName;
      FmOrgs owner;
      protected DataSet<string, Org> dsOrg;

      public FmOrgLoad()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
           new DataSet<string, Org>(Org.OBJECT_NAME);

      }

      public void SetAgent(Agent a, FmOrgs owner)
      {
         this.agent = a;
         this.owner = owner;

         Text = "Загрузка точек агента " + a.name;
      }

      private void FmOrgLoad_Load(object sender, EventArgs e)
      {
         //Manager mc = CurrentUser.user as Manager;

         //if (mc != null)
         //{
         //   List<Division> list = mc.AllDivisions;
         //   list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
         //   cbDivision.Items.AddRange(list.ToArray());
         //   SelectDivision(mc);
         //}
      }

      private void btnOpen_Click(object sender, EventArgs e)
      {
         if (openFileDialog1.ShowDialog() == DialogResult.OK)
         {
            tbPath.Text = openFileDialog1.FileName;
         }
      }

      private void bntLoad_Click(object sender, EventArgs e)
      {
         fileName = tbPath.Text.Trim();
         if (!File.Exists(fileName))
         {
            MessageBox.Show(this, "Укажите путь к файлу Excel!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            tbPath.Focus();
            return;
         }

         Thread thread = new Thread(ExportData);
         thread.Start();
      }

      private void ExportData()
      {
         Workbook wb = new Workbook();
		 wb.Open(fileName);
         SimpleDataSet<Org> ds = new SimpleDataSet<Org>(Org.OBJECT_NAME, false);
		foreach (Sheet sheet in wb.Sheets)
		{
			foreach (Row row in sheet.Rows)
			{
				Org o = new Org();
				o.name = row.Cell(0).Value;
				o.brand = row.Cell(1).Value;
				o.formatTT = row.Cell(2).Value;
				o.city = row.Cell(3).Value;
				o.address2 = row.Cell(4).Value;
				o.address = o.city + ", " + o.address2;

				if (o.IsValid)
				{
				   o.id = Guid.NewGuid().ToString().Replace("-", "");
				   o.userid = agent.id;
				   ds.Add(o);
				}
				
			}
			break;
		}

         // Workbook wb = Workbook.Load(fileName);
         // Worksheet ws = wb.Worksheets[0];

         // SimpleDataSet<Org> ds = new SimpleDataSet<Org>(Org.OBJECT_NAME, false);
         // for (int r = ws.Cells.FirstRowIndex + 1; r < ws.Cells.LastRowIndex; r++)
         // {
            // Row row = ws.Cells.Rows[r];
            // Org o = new Org();
            // o.name = row.GetCell(0).StringValue;
            // o.brand = row.GetCell(1).StringValue;
            // o.formatTT = row.GetCell(2).StringValue;
            // o.city = row.GetCell(3).StringValue;
            // o.address2 = row.GetCell(4).StringValue;
            // o.address = o.city + ", " + o.address2;

            // if (o.IsValid)
            // {
               // o.id = Guid.NewGuid().ToString().Replace("-", "");
               // o.userid = agent.id;
               // ds.Add(o);
            // }

         // }

         if (ds.Count > 0)
         {
            List<IDataSet> update = new List<IDataSet>();
            update.Add(ds);

            Config cfg = Config.GetConfig();

            if (DataModule.UpdateDataSet(update, null, null, cfg.GetConnection()))
               Invoke(new InvokeDelegate(
               delegate
               {
                  if (owner != null)
                  {
                     owner.RefreshData();
                  }
                  MessageBox.Show("Данные загружены успешно!", "Информация", MessageBoxButtons.OK,
                           MessageBoxIcon.Information);
               }));
            else
               Invoke(new InvokeDelegate(
               delegate
               {
                  MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                           MessageBoxIcon.Error);
               }));
         }
      }
   }
}

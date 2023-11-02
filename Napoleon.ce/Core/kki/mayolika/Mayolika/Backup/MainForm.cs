using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.OleDb;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Threading;

namespace Mayolika
{
   public partial class MainForm : Form
   {
      public MainForm()
      {
         InitializeComponent();
      }

      private void MainForm_Load(object sender, EventArgs e)
      {
         tbBasePath.Text =  Properties.Settings.Default.Base;
         dtpDate.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, 1);
         lblWait.Visible = false;
      }

      private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
      {
         string path = tbBasePath.Text.Trim();
         Properties.Settings.Default.Base = path;
         Properties.Settings.Default.Save();
      }

      private void btnBasePath_Click(object sender, EventArgs e)
      {
         if (openFileDialog1.ShowDialog() == DialogResult.OK)
            tbBasePath.Text = openFileDialog1.FileName;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Thread tr = new Thread(()=>{
            BeginInvoke((Action)(() => { btnExcel.Enabled = false; lblWait.Visible = true; }));
            ReportData data = new ReportData(tbBasePath.Text.Trim(), dtpDate.Value);
            Report excel = new Report(data);
            excel.Visible = true;
            BeginInvoke((Action)(() => { btnExcel.Enabled = true; lblWait.Visible = false; }));
         });
         tr.Start();
      }
   }

   class ItemData
   {
      public int day;
      public DateTime minval = DateTime.MinValue;
      public DateTime maxval = DateTime.MinValue;
   }

   class Item
   {
      public string name = string.Empty;
      public List<ItemData> data = new List<ItemData>();
   }

   class GroupItem
   {
      public List<Item> items = new List<Item>();
      public string name = string.Empty;

      public GroupItem()
      {
      }
   }

   class ReportData
   {
      public List<GroupItem> items = new List<GroupItem>();
      public int days;
      public int year;
      public int month;

      public ReportData(string path, DateTime date)
      {
         days = DateTime.DaysInMonth(date.Year, date.Month);
         year = date.Year;
         month = date.Month;

         string ps = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source = {0}", path);
         OleDbConnection conn = new OleDbConnection(ps);
         conn.Open();

         OleDbCommand cmdDept = new OleDbCommand("select deptid, deptname from dept order by deptname", conn);
         OleDbCommand cmdUser = new OleDbCommand("select name, userid from userinfo where deptid = ? order by name", conn);
         cmdUser.Parameters.Add("deptid", OleDbType.Integer);

         OleDbCommand cmdData = new OleDbCommand("select min(checktime), max(checktime) from checkinout where checktime >= ? and checktime < ? and userid = ?", conn);
         cmdData.Parameters.Add("startday", OleDbType.Date);
         cmdData.Parameters.Add("endday", OleDbType.Date);
         cmdData.Parameters.Add("userid", OleDbType.Integer);
         OleDbDataAdapter dataAdapter = new OleDbDataAdapter(cmdData);
         DataSet dsData = new DataSet();

         OleDbDataAdapter adapter = new OleDbDataAdapter(cmdDept);

         DataSet ds = new DataSet();
         adapter.Fill(ds);
         DataTable table = ds.Tables[0];

         foreach (DataRow row in table.Rows)
         {
            GroupItem gr = new GroupItem();
            gr.name = row.ItemArray[1].ToString();
            cmdUser.Parameters[0].Value = row.ItemArray[0];
            adapter = new OleDbDataAdapter(cmdUser);
            DataSet dsGroup = new DataSet();
            adapter.Fill(dsGroup);

            foreach (DataRow row2 in dsGroup.Tables[0].Rows)
            {
               Item item = new Item();
               item.name = row2.ItemArray[0].ToString();

               DateTime start = date;
               DateTime end = date.AddMonths(1);

               while (start < end)
               {
                  ItemData data = new ItemData();
                  data.day = start.Day;

                  cmdData.Parameters[0].Value = start;
                  start = start.AddDays(1);
                  cmdData.Parameters[1].Value = start;
                  cmdData.Parameters[2].Value = row2.ItemArray[1];


                  dsData.Clear();
                  dataAdapter.Fill(dsData);

                  if (dsData.Tables[0].Rows.Count > 0)
                  {
                     DataRow row3 = dsData.Tables[0].Rows[0];
                     try
                     {
                        data.minval = DateTime.Parse(row3.ItemArray[0].ToString());
                        data.maxval = DateTime.Parse(row3.ItemArray[1].ToString());
                     }
                     catch (Exception) { }
                  }

                  item.data.Add(data);
               }


               gr.items.Add(item);
            }

            items.Add(gr);
         }

         conn.Close();
      }
   }

   class Report : Excel
   {
      public Report(ReportData data)
      {
         FreezePanes("C2");
         SetRowHeight(1, 36);
         SetColumnWidth(1, 2.57);
         SetColumnWidth(2, 27.14);
         SetColumnWidth(3, 23.71);

         SetValue(1, 3, "график работы");
         SetCellBoldFont(1, 3, true);
         SetCellHorizontalAlign(1, 3, xlCenter);
         SetCellVerticalAlign(1, 3, xlCenter);

         const int DAY_CLMN = 4;
         const int COL_PER_DAY = 3;

         for (int i = DAY_CLMN; i < data.days * COL_PER_DAY + DAY_CLMN; i++)
            SetColumnWidth(i, 5.29);

         int d = 1;
         for (int i = DAY_CLMN; i < data.days * COL_PER_DAY + DAY_CLMN; i += COL_PER_DAY)
         {
            SetValue(1, i, d);
            MergeCells(1, i, 1, i + 2);
            SetCellHorizontalAlign(1, i, xlCenter);

            DateTime dt = new DateTime(data.year, data.month, d);

            if (dt.DayOfWeek == DayOfWeek.Saturday || dt.DayOfWeek == DayOfWeek.Sunday)
               SetBackColor(GetCell(1, i), Color.LightGreen);

            d++;
         }

         int add = DAY_CLMN + data.days * COL_PER_DAY;

         SetColumnWidth(add, 10.14);
         SetValue(1, add, "Часов по плану");
         SetAttr(add);
         add++;
         SetColumnWidth(add, 13);
         SetValue(1, add, "Часы по факту");
         SetAttr(add);
         add++;
         SetColumnWidth(add, 13);
         SetValue(1, add, "Переработка");
         MergeCells(1, add, 1, add + 2);
         SetAttr(add);
         SetColumnWidth(add + 1, 13);
         SetColumnWidth(add + 2, 13);
         add += 3;
         SetValue(1, add, "Рабочие дни");
         SetAttr(add);
         SetColumnWidth(add, 10.86);
         add++;
         SetValue(1, add, "Коммен.");
         SetAttr(add);
         SetColumnWidth(add, 8.57);
         add++;
         SetValue(1, add, "Часов в день");
         SetAttr(add);
         SetColumnWidth(add, 8.29);
         add++;
         SetValue(1, add, "Часов в сокр. пятн.");
         SetAttr(add);
         SetColumnWidth(add, 12.14);
         add++;

         Color bkg = Color.FromArgb(0x00ffff);
         SetValue(1, add, "5");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "6");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "12");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "13");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "19");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "20");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "26");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "27");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add++;
         SetValue(1, add, "ИТОГО(час)");
         SetAttr(add, bkg);
         SetColumnWidth(add, 12.14);
         add += 3;
         add++;
         SetValue(1, add, "Часы выходов по дням");

         int columns_cnt = DAY_CLMN + data.days * COL_PER_DAY  + data.days + 20;

         int row = 2;
         foreach(GroupItem group in data.items)
         {
            SetValue(row, 2, group.name);
            SetCellHorizontalAlign(row, 2, xlCenter);
            SetBackColor(GetRange(row, 1, row, columns_cnt), Color.FromArgb(0x00ffcc99));
            row++;

            int i = 1;
            const int SHIFT = 20;

            foreach (Item item in group.items)
            {
               SetCellHorizontalAlign(row, 2, xlLeft);
               SetValue(row, 1, i++);
               SetValue(row, 2, item.name);
               SetCellBoldFont(row, 3, true);
               SetValue(row, 3, "8.00-17.00");

               int col = DAY_CLMN;
               foreach (ItemData idata in item.data)
               {
                  SetValue(row, col, idata.minval == DateTime.MinValue ? "" : idata.minval.ToString());
                  SetProperty(GetCell(row, col), "NumberFormat", "ч:мм");
                  SetValue(row, col + 1, idata.maxval == DateTime.MinValue ? "" : idata.maxval.ToString());
                  SetProperty(GetCell(row, col + 1), "NumberFormat", "ч:мм");
                  SetValue(row, col + 2, "=RC[-1]-RC[-2]");
                  SetProperty(GetCell(row,col + 2), "NumberFormat", "ч:мм");
                  SetBackColor(GetCell(row, col + 2), Color.FromArgb(0x0099ccff));
                  col += COL_PER_DAY;
               }

               col++;

               SetValue(row, col, string.Format("=СУММ(RC[{0}]:RC[{1}])", SHIFT, SHIFT+data.days-1));
               SetProperty(GetCell(row, col), "NumberFormat", "0,00");
               col++;
               SetValue(row, col, "=RC[-1]-RC[-2]");
               col++;
               SetValue(row, col, "=ЕСЛИ(RC[-3]>0;(RC[-2]/RC[-3])*100-100;0)");
               col++;
               SetValue(row, col, "=RC[-2]/RC[3]");
               SetProperty(GetCell(row, col), "NumberFormat", "0,00");
               col++;
               SetValue(row, col, "21");
               col++;
               SetValue(row, col, "");
               col++;
               SetValue(row, col, "9");

               col++;
               SetValue(row, col, "2");
               
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 5 * COL_PER_DAY - 2, DAY_CLMN - col + 5 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 6 * COL_PER_DAY - 2, DAY_CLMN - col + 6 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 12 * COL_PER_DAY - 2, DAY_CLMN - col + 12 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 13 * COL_PER_DAY - 2, DAY_CLMN - col + 13 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 19 * COL_PER_DAY - 2, DAY_CLMN - col + 19 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 20 * COL_PER_DAY - 2, DAY_CLMN - col + 20 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 26 * COL_PER_DAY - 2, DAY_CLMN - col + 26 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, string.Format("=RC[{0}]-RC[{1}]", DAY_CLMN - col + 27 * COL_PER_DAY - 2, DAY_CLMN - col + 27 * COL_PER_DAY - 3));
               col++;
               SetValue(row, col, "=СУММ(RC[-8]:RC[-1])");
               col++;
               SetValue(row, col, "=RC[-17]-RC[1]");
               col++;
               SetValue(row, col, "=RC[1]*0,5");
               col++;
               SetValue(row, col, string.Format("=СЧЁТЕСЛИ(RC[1]:RC[{0}];\">0\")", data.days + 1));
               col++;

               for (int a = 1; a <= data.days; a++)
               {
                  SetValue(row, col, string.Format("=ЧАС(RC[{0}])+МИНУТЫ(RC[{0}])/60", DAY_CLMN - col + a * COL_PER_DAY - 1));
                  SetProperty(GetCell(row, col), "NumberFormat", "0,00");
                  col++;
               }

               row++;
            }

            if (group.items.Count > 1)
            {
               SetValue(row, DAY_CLMN + data.days * COL_PER_DAY, string.Format("=СУММ(R[-{0}]C:R[-1]C)", group.items.Count));
               SetProperty(GetCell(row, DAY_CLMN + data.days * COL_PER_DAY), "NumberFormat", "0,00");
               SetBackColor(GetCell(row, DAY_CLMN + data.days * COL_PER_DAY), Color.FromArgb(0x00c0c0c0));

               SetValue(row, DAY_CLMN + data.days * COL_PER_DAY + 1, string.Format("=СУММ(R[-{0}]C:R[-1]C)", group.items.Count));
               SetProperty(GetCell(row, DAY_CLMN + data.days * COL_PER_DAY + 1), "NumberFormat", "0,00");
               SetBackColor(GetCell(row, DAY_CLMN + data.days * COL_PER_DAY + 1), Color.FromArgb(0x00c0c0c0));
            }
     
            row++;
         }

         row--;
         SetBordersOnRange(1, 1, row, columns_cnt, xlContinuous);

         row += 3;

         SetBackColor(GetCell(row,2), Color.FromArgb(0x00c0c0c0));
         SetValue(row, 3, "больничный");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x0000ffff));
         SetValue(row, 3, "отпуск ежегодный");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00ffcc00));
         SetValue(row, 3, "отпуск без сохр. з/п");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00996666));
         SetValue(row, 3, "отпуск по ух.за реб.");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x000000ff));
         SetValue(row, 3, "увольнение");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x000099ff));
         SetValue(row, 3, "отсутств. с отработкой");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00ccffcc));
         SetValue(row, 3, "отсутств. без причины");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00ff6633));
         SetValue(row, 3, "командировка");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00808080));
         SetValue(row, 3, "мед.справка, без б/л");

         row++;

         SetBackColor(GetCell(row, 2), Color.FromArgb(0x00008000));
         SetValue(row, 3, "отпуск по беременности и родам");
      }

      private void SetAttr(int clmn)
      {
         SetAttr(clmn, Color.FromArgb(0x00a5ff));
      }

      private void SetAttr(int clmn, Color bkg)
      {
         SetCellHorizontalAlign(1, clmn, xlCenter);
         SetCellVerticalAlign(1, clmn, xlCenter);
         SetWrapeText(1, clmn, true);
         SetBackColor(GetCell(1, clmn), bkg);
      }
   }

}

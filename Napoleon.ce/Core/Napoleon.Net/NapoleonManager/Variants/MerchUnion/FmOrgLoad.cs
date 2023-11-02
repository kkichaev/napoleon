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
using ExcelLibrary.SpreadSheet;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgLoad : Form
   {
      Agent agent;
      string fileName;
      FmOrgs owner;
      List<string> orgLoaded = new List<string>();

      public FmOrgLoad()
      {
         InitializeComponent();
      }

      public void SetAgent(Agent a, FmOrgs owner)
      {
         this.agent = a;
         this.owner = owner;

         Text = "Загрузка точек агента " + a.name;

         orgLoaded.Clear();

         foreach (Org o in owner.dsOrg.Values)
            orgLoaded.Add(o.name.ToUpper() + o.address.ToUpper());
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
         try
         {
            Workbook wb = Workbook.Load(fileName);
            Worksheet ws = wb.Worksheets[0];

            SimpleDataSet<Org> ds = new SimpleDataSet<Org>(Org.OBJECT_NAME, false);
            for (int r = ws.Cells.FirstRowIndex+1; r <= ws.Cells.LastRowIndex; r++)
            {
               Row row = ws.Cells.Rows[r];
               Org o = new Org();
               o.name = row.GetCell(0).StringValue;
               o.address = row.GetCell(1).StringValue;

               if (!orgLoaded.Contains(o.name.ToUpper() + o.address.ToUpper()))
               {
                  o.id = Guid.NewGuid().ToString().Replace("-", "");
                  o.userid = agent.id;
                  ds.Add(o);
               }
            }

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
                        owner.btnRefresh.PerformClick();
                     }
                     MessageBox.Show("Данные загружены успешно!", "Информация", MessageBoxButtons.OK,
                              MessageBoxIcon.Information);

                     foreach (Org o in ds.Values)
                     {
                        string key = o.name.ToUpper() + o.address.ToUpper();

                        if (!orgLoaded.Contains(key))
                           orgLoaded.Add(key);
                     }

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
         catch(Exception e)
         {
            MessageBox.Show(e.Message, "Ошибка", MessageBoxButtons.OK,
                           MessageBoxIcon.Error);
         }
         
      }
   }
}

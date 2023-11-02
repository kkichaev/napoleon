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

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgLoad : Form
   {
      protected DataSet<string, Org> dsOrg;
      protected DataSet<int, DivisionOrg> dsDivisionOrg;

      public FmOrgLoad()
      {
         InitializeComponent();

         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
           new DataSet<string, Org>(Org.OBJECT_NAME);

         dsDivisionOrg = (DataSet<int, DivisionOrg>)DataModule.Get(DivisionOrg.OBJECT_NAME) ??
           new DataSet<int, DivisionOrg>(DivisionOrg.OBJECT_NAME);
      }

      private void FmOrgLoad_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            cbDivision.Items.AddRange(list.ToArray());
            SelectDivision(mc);
         }
      }

      private void SelectDivision(Manager mc)
      {
         int sel = -1;
         for (int i = 0; i < cbDivision.Items.Count; i++)
         {
            Division d = (Division)cbDivision.Items[i];

            if (d.id.Equals(mc.Division.id))
            {
               sel = i;
               break;
            }
         }

         cbDivision.SelectedIndex = sel;
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
         Division d = cbDivision.SelectedItem as Division;

         if (d == null)
         {
            MessageBox.Show(this, "Выберите подразделение!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            cbDivision.Focus();
            return;
         }

         if (!File.Exists(tbPath.Text.Trim()))
         {
            MessageBox.Show(this, "Укажите путь к файлу Excel!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            tbPath.Focus();
            return;
         }


         Thread thread = new Thread(ExportData);
         thread.Start(d);
      }

      private void ExportData(object obj)
      {
         Division div = (Division)obj;

         if (!dsDivisionOrg.ContainsKey(div.id))
            dsDivisionOrg[div.id] = new DivisionOrg()
            {
               id = div.id
            };

         DivisionOrg divOgr = dsDivisionOrg[div.id];

         List<string> divOrgCaches = new List<string>();

         foreach (DivisionOrg.DivisionOrgItem i in divOgr.items)
            divOrgCaches.Add(i.id);

         DataSet<string, Org> dsOrgLoad = new DataSet<string, Org>(Org.OBJECT_NAME);

         Dictionary<string, string> orgCahe = new Dictionary<string, string>();

         foreach (Org o in dsOrg.Data)
         {
            String key = o.name.ToUpper().Trim() + o.address.ToUpper().Trim();

            if (!orgCahe.ContainsKey(key))
               orgCahe[key] = o.id;
         }

         var connectionString = string.Format("Provider=Microsoft.Jet.OLEDB.4.0;Data Source={0}; Extended Properties=Excel 8.0;", tbPath.Text.Trim());
         var objConn = new OleDbConnection(connectionString);
         objConn.Open();
         var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

         bool divOrgUpdated = false;

         if (dt == null)
         {
            return;
         }

         foreach (DataRow sh in dt.Rows)
         {
            try
            {
               string group = sh["TABLE_NAME"].ToString().Trim();
               var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + group + "]", connectionString);
               var ds = new DataSet();
               adapter.Fill(ds, group);
               DataTable data = ds.Tables[group];

               Invoke(new InvokeDelegate(
                 delegate
                 {
                    progressBar1.Value = 0;
                    progressBar1.Maximum = data.Rows.Count;
                 }));

               foreach (DataRow row in data.Rows)
               {

                  Invoke(new InvokeDelegate(
                  delegate
                  {
                     progressBar1.PerformStep();
                  }));

                  object[] r = row.ItemArray;

                  if (r[0].ToString().Length == 0)
                     break;

                  Org o = new Org();

                  o.name = r[0].ToString().Trim();
                  o.address = r[1].ToString().Trim();
                  o.formatTT = r[2].ToString().Trim();

                  string key = o.name.ToUpper().Trim() + o.address.ToUpper();

                  if (!orgCahe.ContainsKey(key))
                  {
                     o.id = GRSoft.Network.DataObject.GenId();
                     dsOrgLoad.Add(o.id, o);
                  }
                  else
                     o.id = orgCahe[key];

                  if (!divOrgCaches.Contains(o.id))
                  {
                     divOgr.items.Add(new DivisionOrg.DivisionOrgItem()
                        {
                           id = o.id
                        });
                     divOrgCaches.Add(o.id);

                     if (!divOrgUpdated)
                        divOrgUpdated = true;
                  }
               }
            }
            catch (Exception e)
            {

            }
         }

         List<IDataSet> update = new List<IDataSet>();

         if (dsOrgLoad.Count > 0)
            update.Add(dsOrgLoad);

         if (divOrgUpdated && dsDivisionOrg.Count > 0)
            update.Add(dsDivisionOrg);

         if (update.Count > 0)
         {
            Config cfg = Config.GetConfig();

            if (DataModule.UpdateDataSet(update, null, null, cfg.GetConnection()))
               Invoke(new InvokeDelegate(
               delegate
               {
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
         }else
            Invoke(new InvokeDelegate(
               delegate
               {
                  MessageBox.Show("Данные уже загружены в систему, обновление не требуется.", "Информация", MessageBoxButtons.OK,
                     MessageBoxIcon.Information);
               }));
      }
   }
}

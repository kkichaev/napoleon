using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Data.Odbc;
using System.IO;
using System.Diagnostics;

namespace GRSoft.NapoleonManager
{
   public partial class FmMonitoringDBF : Form
   {
      public MonitoringDBFSetting setting = null;
      private DataSet<int, Monitoring> dsMonitoring;
      private DataSet<string, MonitoringItem> dsMonitoringItem;
      private DataSet<string, Org> dsOrg;
      private DataSet<string, Agent> dsAgent;

      public FmMonitoringDBF()
      {
         InitializeComponent();
         dsMonitoring = (DataSet<int, Monitoring>)DataModule.Get(Monitoring.OBJECT_NAME) ?? 
            new DataSet<int, Monitoring>(Monitoring.OBJECT_NAME);
         dsMonitoringItem = (DataSet<string, MonitoringItem>)DataModule.Get(MonitoringItem.OBJECT_NAME) ??
            new DataSet<string, MonitoringItem>(MonitoringItem.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ??
            new DataSet<string, Org>(Org.OBJECT_NAME);
         dsAgent = (DataSet<string, Agent>) DataModule.Get(Agent.OBJECT_NAME) ??
            new DataSet<string, Agent>(Agent.OBJECT_NAME);
      }

      private void FmMonitoringDBF_Load(object sender, EventArgs e)
      {
         setting = BaseFormSetting<MonitoringDBFSetting>.Load();
         tbPath.Text = setting.path;
         dtpBegin.Value = setting.begin;
         dtpEnd.Value = setting.end;
         tbTable.Text = setting.table;
         cbOpen.Checked = setting.open;
      }

      private void btnDBF_Click(object sender, EventArgs e)
      {
         if (tbTable.Text.Trim().Length > 0)
         {
            dsMonitoring.Filter = String.Format("{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy}')",
                  "created", dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1));

            List<IDataSet> updSets = new List<IDataSet>();
            updSets.Add(dsAgent);
            updSets.Add(dsOrg);
            updSets.Add(dsMonitoring);
            updSets.Add(dsMonitoringItem);
            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
            FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSets, FmWait.ProgressIndicator));
         }
         else
         {
            tbTable.Focus();
            MessageBox.Show("Имя таблицы не может быть пустым", "Ошибка", 
               MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }

      private void FmMonitoringDBF_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.path = tbPath.Text;
         setting.begin = dtpBegin.Value;
         setting.end = dtpEnd.Value;
         setting.table = tbTable.Text;
         setting.open = cbOpen.Checked;
         setting.Save();
      }

      private void tbnDirectory_Click(object sender, EventArgs e)
      {
         if (tbPath.Text.Length > 0)
            dialog.SelectedPath = tbPath.Text;

         if (dialog.ShowDialog() == DialogResult.OK)
            tbPath.Text = dialog.SelectedPath;
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         new Thread(new ParameterizedThreadStart(delegate(object o)
         {
            Invoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));
            try
            {
               string path = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
               string table = string.Empty;

               Invoke(new EmptyParamHandler(delegate()
               {
                  if(tbPath.Text.Length > 0)
                     path = tbPath.Text;

                  if(tbTable.Text.Length > 0)
                     table = tbTable.Text;
               }));

               using (OdbcConnection conn = new OdbcConnection())
               {
                  Invoke(new InvokeDelegate(delegate()
                  {
                     File.Delete(String.Format("{0}\\{1}.dbf", path, table));
                  }));

                  DBFWriter writer = new DBFWriter(String.Format("{0}\\{1}.dbf", path, table));
                  writer.Fields = new DBFField[]
                  {
                     new DBFField("CREATED", NativeDbType.Date),
                     new DBFField("LOGIN", NativeDbType.Char, 125),
                     new DBFField("ID", NativeDbType.Char, 125),
                     new DBFField("ORG", NativeDbType.Char, 250),
                     new DBFField("ADDR", NativeDbType.Char, 250),
                     new DBFField("MARKA", NativeDbType.Char, 250),
                     new DBFField("FACE", NativeDbType.Numeric, 8),
                     new DBFField("SKU", NativeDbType.Numeric, 8)
                  };

                  object[] data = new object[writer.Fields.Length];

                  foreach (Monitoring m in dsMonitoring.Data)
                  {
                     data[0] = m.created;
                     data[1] = m.agent == null ? string.Empty : m.agent.login;
                     data[2] = m.id;
                     data[3] = m.OrgName;
                     data[4] = m.OrgAddr;

                     if (m.items != null && m.items.Count > 0)
                        foreach (MonitoringDocItem mi in m.items)
                        {
                           if (dsMonitoringItem.ContainsKey(mi.id))
                           {
                              data[5] = dsMonitoringItem[mi.id].name;
                              data[6] = mi.face;
                              data[7] = mi.sku;

                              writer.WriteRecord(data);
                           }
                        }
                  }

                  writer.Dispose();

                  BeginInvoke(new EmptyParamHandler(delegate() {
                     if (cbOpen.Checked)
                     {
                        ProcessStartInfo psi = new ProcessStartInfo(String.Format("{0}\\{1}.dbf", path, table));
                        psi.UseShellExecute = true;
                        Process.Start(psi);
                     }
                     else
                        MessageBox.Show("Завершено успешно", "Сообщение", 
                           MessageBoxButtons.OK, MessageBoxIcon.Information);
                  }));
               }
            }
            catch (Exception e)
            {
               BeginInvoke(new EmptyParamHandler(delegate() {
                  MessageBox.Show(e.Message, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               }));
            }
            finally
            {
               Invoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
            }
         })).Start();
      }

   }

   [Serializable]
   public class MonitoringDBFSetting : BaseFormSetting<MonitoringDBFSetting>
   {
      public String path = "";
      public String table = "MON";
      public DateTime begin = DateTime.Now.AddMonths(-1);
      public DateTime end = DateTime.Now;
      public Boolean open = false;
   }
}

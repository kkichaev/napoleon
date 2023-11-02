using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Ads.Utils;
using System.Data.Odbc;
using System.Data.OleDb;
using GRSoft.Network;
using System.IO;
using System.Collections;
using System.Threading;

namespace GRSoft.Ads
{
   public partial class FmOrderDBF : Form
   {
      SettingFmOrderDBF setting = null;
      DsGPSPos dsGPSPos;

      public FmOrderDBF()
      {
         InitializeComponent();
         dsGPSPos = (DsGPSPos)DataModule.Get(GPSPos.OBJECT_NAME) ?? new DsGPSPos(true);
      }

      public static void ShowInstance()
      {
         FmOrderDBF fmOrderDbf = new FmOrderDBF();
         fmOrderDbf.Show();
      }

      private void FmOrderDBF_Load(object sender, EventArgs e)
      {
         setting = BaseFormSetting<SettingFmOrderDBF>.Load();
         tbPath.Text = setting.path;
         tbName.Text = setting.name;

         DsBrigade dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME);
         cbBrigade.Items.Clear();
         cbBrigade.Items.Add("Все");

         if (dsBrigade != null)
         {
            Brigade[] brigade = new Brigade[dsBrigade.Count];
            dsBrigade.Values.CopyTo(brigade,0);

            Array.Sort(brigade, new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2){return b1.Name.CompareTo(b2.Name);}));
            cbBrigade.Items.AddRange(brigade);
         }

         cbBrigade.SelectedIndex = 0;
      }

      private void FmOrderDBF_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.name = tbName.Text.Trim();
         setting.Save();
      }

      private void btnPath_Click(object sender, EventArgs e)
      {
         dialog.SelectedPath = setting.path;
         if (dialog.ShowDialog() == DialogResult.OK)
         {
            tbPath.Text = dialog.SelectedPath;
            setting.path = dialog.SelectedPath;
         }
      }

      private void btnStart_Click(object sender, EventArgs e)
      {
         new Thread(new ThreadStart(delegate()
         {
            Invoke(new InvokeDelegate(delegate() { FmWait.ShowForm(this, true); }));

            using (OdbcConnection conn = new OdbcConnection())
            {
               conn.ConnectionString = "Driver={Microsoft dBase Driver (*.dbf)};" +
                  "SourceType=DBF;Exclusive=No;" +
                  "Collate=Machine;NULL=NO;DELETED=NO;" +
                  "BACKGROUNDFETCH=NO;" +
                  "DBQ=" + tbPath.Text + ";" +
                  "FIL=dBase IV;";
               conn.Open();

               OdbcCommand command = conn.CreateCommand();

               Invoke(new InvokeDelegate(delegate() 
               {
                  File.Delete(String.Format("{0}\\{1}.dbf", getPath(), GetTableName())); 
               }));

               try
               {
                  command.CommandText = String.Format("CREATE TABLE [{0}] (BNAME Char(125), BID Char(125), ADDR Char(250),  JTNAME Char(150), " +
                     "JTID Char(32), BEGIN Char(25), END Char(25), DIST Number, CLIENT Char(250))",
                     GetTableName());
                  command.ExecuteNonQuery();
               
                  IDataSet dsOrder = new DsOrderRcv(false);
                  dsOrder.Filter = String.Format("((\"params\" & 524288) = 524288) " +
                     "and \"factbegin\" >= ToDate('{0:dd/MM/yyyy}') and \"factend\" <= ToDate('{1:dd/MM/yyyy}')",
                     dtpFrom.Value.Date, dtpTill.Value.Date.AddDays(1));

                  Brigade b = null;

                  Invoke(new InvokeDelegate(delegate()
                  {
                     b = cbBrigade.SelectedItem as Brigade;
                  }));

                  if (b != null)
                     dsOrder.Filter += " AND userid = '" + b.id + "'";

                  DsBrigade dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
                  DsJobType dsJobType = (DsJobType)DataModule.Get(JobType.OBJECT_NAME) ?? new DsJobType(true);
                  dsGPSPos.Filter = String.Format("date >= ToDate('{0:dd/MM/yyyy}') and date < ToDate('{1:dd/MM/yyyy}')",
                     dtpFrom.Value.Date, dtpTill.Value.Date);
                  List<IDataSet> list = new List<IDataSet>();
                  list.Add(dsJobType);
                  list.Add(dsBrigade);
                  list.Add(dsOrder);
                  list.Add(dsGPSPos);

                  DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
                  DataModule_OnDataResponceError);

                  DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
                        list, null).Join();

                  OdbcCommand insert = new OdbcCommand("INSERT INTO [ORD](BNAME, BID, ADDR, JTNAME, JTID, BEGIN, END, DIST," +
                     " CLIENT) VALUES(?,?,?,?,?,?,?,?,?)", conn);
                  const string DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
                  DateTime date = DateTime.MinValue;

                  List<OrderRcv> ordList = new List<OrderRcv>();

                  foreach (OrderRcv orcv in dsOrder.Data)
                     ordList.Add(orcv);

                  ordList.Sort(new Comparison<OrderRcv>(delegate(OrderRcv orcv1, OrderRcv orcv2)
                  {
                     int result = 0;

                     if (orcv1.brigade != null && orcv2.brigade != null)
                        result = orcv1.brigade.id.CompareTo(orcv2.brigade.id);

                     if (result == 0)
                        result = orcv1.factbegin.CompareTo(orcv2.factbegin);

                     return result;
                  }));

                  string userid = string.Empty;

               
                  foreach (OrderRcv ord in ordList)
                  {
                     insert.Parameters.Clear();
                     insert.Parameters.Add("@name", OdbcType.Char).Value = ord.BrigadeName;
                     insert.Parameters.Add("@id", OdbcType.Char).Value = ord.brigade != null ? ord.brigade.id : string.Empty;
                     insert.Parameters.Add("@addr", OdbcType.Char).Value = ord.Address;
                     insert.Parameters.Add("@tname", OdbcType.Char).Value = ord.brigade != null ?
                        ord.brigade.JobType != null ? ord.brigade.JobType.ToString() : string.Empty : string.Empty;
                     insert.Parameters.Add("@tid", OdbcType.Int).Value = ord.brigade != null ?
                        ord.brigade.JobType != null ? (int)ord.brigade.JobType.id : -1 : -1;
                     insert.Parameters.Add("@beg", OdbcType.Char).Value = ord.factbegin.ToString(DATE_TIME_FORMAT);
                     insert.Parameters.Add("@end", OdbcType.DateTime).Value = ord.factend.ToString(DATE_TIME_FORMAT);

                     if (ord.brigade != null && ord.brigade.id.Equals(userid))
                        insert.Parameters.Add("@dist", OdbcType.Double).Value = GetPathBetweenTwoDate(date, ord.factbegin, ord.brigade.id);
                     else
                        insert.Parameters.Add("@dist", OdbcType.Double).Value = 0.0;

                     insert.Parameters.Add("@client", OdbcType.Char).Value = ord.client != null ? ord.client.name : String.Empty;

                     insert.ExecuteNonQuery();

                     date = ord.planend;
                     userid = ord.brigade == null ? string.Empty : ord.brigade.id;
                  }

                  Invoke(new InvokeDelegate(delegate() { MessageBox.Show("Файл создан успешно!"); }));
               }
               catch (Exception exception) 
               {
                  Invoke(new InvokeDelegate(delegate() {MessageBox.Show("Ошибка:" + exception.Message);}));
               }

               conn.Close();
               Invoke(new InvokeDelegate(delegate() { FmWait.CloseForm(); }));
            }
         })).Start();
      }

      private string getPath()
      {
         return tbPath.Text.Trim().Length == 0 ? Application.StartupPath  : tbPath.Text;
      }

      private double GetPathBetweenTwoDate(DateTime t1, DateTime t2, String userid)
      {
         double result = 0.0;

         if (userid != null && userid.Trim().Length > 0 && dsGPSPos.Count > 0)
         {
            GPSPos[] gpsArray = new GPSPos[dsGPSPos.Count];
            dsGPSPos.Values.CopyTo(gpsArray, 0);

            List<GPSPos> filtered = new List<GPSPos>();

            for (int i = 0; i < gpsArray.Length; i++)
               if (gpsArray[i].brigade != null &&
                     gpsArray[i].brigade.id.Equals(userid) &&
                     gpsArray[i].date >= t1 && gpsArray[i].date <= t2)
               {
                  filtered.Add(gpsArray[i]);
                  dsGPSPos.Remove(gpsArray[i].date);
               }

            if (filtered.Count >= 2)
               for (int i = 0; i < filtered.Count - 1; i++)
                  result += Coordutils.Distance(filtered[i].latitude, filtered[i].longitude, filtered[i+1].latitude, filtered[i+1].longitude);
         }

         return result;
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private string GetTableName()
      {
         return tbName.Text.Trim().Length == 0 ? "ORD" : tbName.Text;
      }
   }

   [Serializable]
   class SettingFmOrderDBF : BaseFormSetting<SettingFmOrderDBF>
   {
      public string path = string.Empty;
      public string name = "ORD";
   } 
}

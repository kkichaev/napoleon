using GRSoft.Network;
using System;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripTextBox tsbBackSyncInterval;

      public MainFormEx()
      {

         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.tsbBackSyncInterval = new System.Windows.Forms.ToolStripTextBox();
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(171, 22);
         this.toolStripLabel2.Text = "Фоновая синхронизация (23:00 - 06:00)";
         // 
         // tsbBackSyncInterval
         // 
         this.tsbBackSyncInterval.Name = "tsbBackSyncInterval";
         this.tsbBackSyncInterval.Size = new System.Drawing.Size(50, 25);
         tsbBackSyncInterval.TextBoxTextAlign = HorizontalAlignment.Left;

         toolStrip1.Items.AddRange(new ToolStripItem[] {this.toolStripLabel2, this.tsbBackSyncInterval});
      }

      void tsbBackSyncInterval_TextChanged(object sender, System.EventArgs e)
      {
         IsDirty = true;
      }

      protected override bool SaveChanges()
      {
         string value = tsbBackSyncInterval.Text;
         if(value.Length == 0)
         {
            SimpleDataSet<ServerConfig> rmv = new SimpleDataSet<ServerConfig>(ServerConfig.OBJECT_NAME, false);
            ServerConfig sc = new ServerConfig();
            sc.userid = "";
            sc.key = "ФоноваяСинхронизация";
            rmv.Add(sc);
            userData.AddRemoveSet(rmv);
            return base.SaveChanges();
         }
         DateTime dt;
         if(!DateTime.TryParseExact(value, "H:mm", System.Globalization.CultureInfo.CurrentCulture, System.Globalization.DateTimeStyles.None,out dt))
         {
            MessageBox.Show("Ошибка в формате числа");
            return false;
         }
         if((dt.Hour > 6 || dt.Hour == 6 && dt.Minute > 0) && dt.Hour <= 22 )
         {
            MessageBox.Show("Введите время в диаппазоне 06:00 по 23:00");
            return false;
         }

         ServerConfig scret = null;
         foreach (ServerConfig sc in dsCommonConfig.Data)
            if (sc.userid == "" && sc.key == "ФоноваяСинхронизация")
            {
               scret = sc;
               break;
            }

         if( scret == null)
         {
            scret = new ServerConfig();
            scret.userid = "";
            scret.key = "ФоноваяСинхронизация";
            dsCommonConfig.Add(dsCommonConfig.Count + 1, scret);
         }


         scret.value = value;

         userData.AddWriteSet(dsCommonConfig);
         return base.SaveChanges();
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;
      }

      protected override void UpdateLoadedData()
      {
         string val = "";
         foreach (ServerConfig sc in dsCommonConfig.Data)
            if(sc.userid == "" && sc.key == "ФоноваяСинхронизация")
            {
               val = sc.value;
               break;
            }

         Invoke((UpdType)UpdateBackSync, new object[] { val });
         
      }

      void UpdateBackSync(string val)
      {
         tsbBackSyncInterval.TextChanged -= tsbBackSyncInterval_TextChanged;
         tsbBackSyncInterval.Text = val;
         tsbBackSyncInterval.TextChanged += tsbBackSyncInterval_TextChanged;
      }
      delegate void UpdType(string val);
   }
}
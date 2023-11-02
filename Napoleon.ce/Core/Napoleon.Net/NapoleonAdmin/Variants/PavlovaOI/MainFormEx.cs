using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataGridViewCheckBoxColumn clmnSellWithoutRest;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripTextBox tsbBackSyncInterval;

      public MainFormEx()
      {
         clmnSellWithoutRest = new DataGridViewCheckBoxColumn();
         clmnSellWithoutRest.DataPropertyName = "CanSellWithoutRest";
         clmnSellWithoutRest.HeaderText = "Может продавать в минус";
         clmnSellWithoutRest.Name = "clmnSellWithoutRest";
         clmnSellWithoutRest.Visible = false;
         clmnSellWithoutRest.Width = 90;
         usersView.Columns.Add(clmnSellWithoutRest);

         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.tsbBackSyncInterval = new System.Windows.Forms.ToolStripTextBox();
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(171, 22);
         this.toolStripLabel2.Text = "Фоновая синхронизация, мин";
         // 
         // tsbBackSyncInterval
         // 
         this.tsbBackSyncInterval.Name = "tsbBackSyncInterval";
         this.tsbBackSyncInterval.Size = new System.Drawing.Size(50, 25);
         tsbBackSyncInterval.TextBoxTextAlign = HorizontalAlignment.Right;
         tsbBackSyncInterval.TextChanged += tsbBackSyncInterval_TextChanged;

         toolStrip1.Items.AddRange(new ToolStripItem[] {this.toolStripLabel2, this.tsbBackSyncInterval});
      }

      void tsbBackSyncInterval_TextChanged(object sender, System.EventArgs e)
      {
         IsDirty = true;
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         if (usersView.CurrentCell.ColumnIndex == clmnSellWithoutRest.DisplayIndex)
            usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      protected override bool SaveChanges()
      {
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

         int val = 0;
         int.TryParse(tsbBackSyncInterval.Text, out val);
         scret.value = val.ToString();

         userData.AddWriteSet(dsCommonConfig);
         return base.SaveChanges();
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;

         if (clmnSellWithoutRest != null)
            clmnSellWithoutRest.Visible = agentView;
      }

      protected override void UpdateLoadedData()
      {
         int val = 0;
         foreach (ServerConfig sc in dsCommonConfig.Data)
            if(sc.userid == "" && sc.key == "ФоноваяСинхронизация")
            {
               int.TryParse(sc.value, out val);
               break;
            }

         Invoke((UpdType)UpdateBackSync, new object[] { val });
         
      }

      void UpdateBackSync(int val) { tsbBackSyncInterval.Text = val.ToString(); }
      delegate void UpdType(int val);
   }
}
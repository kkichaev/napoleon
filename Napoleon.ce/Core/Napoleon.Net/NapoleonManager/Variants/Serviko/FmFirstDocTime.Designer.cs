
namespace GRSoft.NapoleonManager
{
   partial class FmFirstDocTime
   {
      /// <summary>
      /// Required designer variable.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary>
      /// Clean up any resources being used.
      /// </summary>
      /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Windows Form Designer generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFirstDocTime));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.lblInfo = new System.Windows.Forms.ToolStripLabel();
         this.cbDivision = new System.Windows.Forms.ToolStripComboBox();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnMon = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnTue = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnWed = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnThu = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnFri = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnSat = new GRSoft.NapoleonManager.TimeColumn();
         this.clmnSun = new GRSoft.NapoleonManager.TimeColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.tsLabel = new System.Windows.Forms.ToolStripStatusLabel();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.lblInfo,
            this.cbDivision});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(996, 49);
         this.toolStrip1.TabIndex = 4;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 46);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 46);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // lblInfo
         // 
         this.lblInfo.Name = "lblInfo";
         this.lblInfo.Size = new System.Drawing.Size(0, 46);
         // 
         // cbDivision
         // 
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(180, 49);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnMon,
            this.clmnTue,
            this.clmnWed,
            this.clmnThu,
            this.clmnFri,
            this.clmnSat,
            this.clmnSun});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 49);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(996, 580);
         this.dgvItems.TabIndex = 5;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "ФИО";
         this.clmnAgent.Name = "clmnAgent";
         // 
         // clmnMon
         // 
         this.clmnMon.DataPropertyName = "Mon";
         this.clmnMon.HeaderText = "Пн";
         this.clmnMon.Name = "clmnMon";
         this.clmnMon.Width = 85;
         // 
         // clmnTue
         // 
         this.clmnTue.DataPropertyName = "Tue";
         this.clmnTue.HeaderText = "Вт";
         this.clmnTue.Name = "clmnTue";
         this.clmnTue.Width = 85;
         // 
         // clmnWed
         // 
         this.clmnWed.DataPropertyName = "Wed";
         this.clmnWed.HeaderText = "Ср";
         this.clmnWed.Name = "clmnWed";
         this.clmnWed.Width = 85;
         // 
         // clmnThu
         // 
         this.clmnThu.DataPropertyName = "Thu";
         this.clmnThu.HeaderText = "Чт";
         this.clmnThu.Name = "clmnThu";
         this.clmnThu.Width = 85;
         // 
         // clmnFri
         // 
         this.clmnFri.DataPropertyName = "Fri";
         this.clmnFri.HeaderText = "Пт";
         this.clmnFri.Name = "clmnFri";
         this.clmnFri.Width = 85;
         // 
         // clmnSat
         // 
         this.clmnSat.DataPropertyName = "Sat";
         this.clmnSat.HeaderText = "Сб";
         this.clmnSat.Name = "clmnSat";
         this.clmnSat.Width = 85;
         // 
         // clmnSun
         // 
         this.clmnSun.DataPropertyName = "Sun";
         this.clmnSun.HeaderText = "Вс";
         this.clmnSun.Name = "clmnSun";
         this.clmnSun.Width = 85;
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsLabel});
         this.statusStrip1.Location = new System.Drawing.Point(0, 607);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(996, 22);
         this.statusStrip1.TabIndex = 6;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // tsLabel
         // 
         this.tsLabel.Name = "tsLabel";
         this.tsLabel.Size = new System.Drawing.Size(0, 17);
         // 
         // FmFirstDocTime
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(996, 629);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2);
         this.Name = "FmFirstDocTime";
         this.Text = "Время первого документа";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      protected System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripLabel lblInfo;
      private System.Windows.Forms.ToolStripComboBox cbDivision;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private TimeColumn clmnMon;
      private TimeColumn clmnTue;
      private TimeColumn clmnWed;
      private TimeColumn clmnThu;
      private TimeColumn clmnFri;
      private TimeColumn clmnSat;
      private TimeColumn clmnSun;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel tsLabel;
   }
}
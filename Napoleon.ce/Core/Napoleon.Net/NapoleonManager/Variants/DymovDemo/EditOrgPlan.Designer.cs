namespace GRSoft.NapoleonManager
{
   partial class EditOrgPlan
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
         this.components = new System.ComponentModel.Container();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(EditOrgPlan));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFind = new System.Windows.Forms.ToolStripButton();
         this.tbPlan = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnApply = new System.Windows.Forms.ToolStripButton();
         this.btnPlanLoad = new System.Windows.Forms.ToolStripButton();
         this.grid = new System.Windows.Forms.DataGridView();
         this.clmnChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnMargin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStripDropDownButton1 = new System.Windows.Forms.ToolStripDropDownButton();
         this.tbSetCheck = new System.Windows.Forms.ToolStripMenuItem();
         this.tbResetCheck = new System.Windows.Forms.ToolStripMenuItem();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.timepicker = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnRefresh,
            this.toolStripSeparator2,
            this.toolStripSeparator1,
            this.tbFind,
            this.btnClearFind,
            this.tbPlan,
            this.toolStripLabel1,
            this.btnApply,
            this.btnPlanLoad});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(725, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(140, 0, 0, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(200, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // btnClearFind
         // 
         this.btnClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFind.Name = "btnClearFind";
         this.btnClearFind.Size = new System.Drawing.Size(23, 22);
         this.btnClearFind.Text = "Очистить поиск";
         this.btnClearFind.TextChanged += new System.EventHandler(this.btnClearFind_Click);
         // 
         // tbPlan
         // 
         this.tbPlan.Name = "tbPlan";
         this.tbPlan.Size = new System.Drawing.Size(100, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(18, 22);
         this.toolStripLabel1.Text = "кг";
         // 
         // btnApply
         // 
         this.btnApply.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnApply.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_apply;
         this.btnApply.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnApply.Name = "btnApply";
         this.btnApply.Size = new System.Drawing.Size(23, 22);
         this.btnApply.Text = "Назначить";
         this.btnApply.Click += new System.EventHandler(this.btnApply_Click);
         // 
         // btnPlanLoad
         // 
         this.btnPlanLoad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPlanLoad.Image = global::GRSoft.NapoleonManager.Properties.Resources.importpotorgl;
         this.btnPlanLoad.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPlanLoad.Name = "btnPlanLoad";
         this.btnPlanLoad.Size = new System.Drawing.Size(23, 22);
         this.btnPlanLoad.Text = "Ипорт плана из Excel";
         this.btnPlanLoad.Click += new System.EventHandler(this.btnPlanLoad_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnChecked,
            this.clmnName,
            this.clmnOrg,
            this.clmnMargin});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 25);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(725, 428);
         this.grid.TabIndex = 1;
         this.grid.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellEndEdit);
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         this.grid.CurrentCellDirtyStateChanged += new System.EventHandler(this.grid_CurrentCellDirtyStateChanged);
         this.grid.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.grid_DataError);
         // 
         // clmnChecked
         // 
         this.clmnChecked.DataPropertyName = "Checked";
         this.clmnChecked.HeaderText = "";
         this.clmnChecked.Name = "clmnChecked";
         this.clmnChecked.Width = 40;
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Название";
         this.clmnName.Name = "clmnName";
         // 
         // clmnOrg
         // 
         this.clmnOrg.DataPropertyName = "Owner";
         this.clmnOrg.HeaderText = "Юр.Лицо";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.Width = 200;
         // 
         // clmnMargin
         // 
         this.clmnMargin.DataPropertyName = "Plan";
         dataGridViewCellStyle1.Format = "N3";
         dataGridViewCellStyle1.NullValue = null;
         this.clmnMargin.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnMargin.FillWeight = 20F;
         this.clmnMargin.HeaderText = "План";
         this.clmnMargin.Name = "clmnMargin";
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripDropDownButton1});
         this.statusStrip1.Location = new System.Drawing.Point(0, 453);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(725, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStripDropDownButton1
         // 
         this.toolStripDropDownButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripDropDownButton1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbSetCheck,
            this.tbResetCheck});
         this.toolStripDropDownButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.toolStripDropDownButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripDropDownButton1.Name = "toolStripDropDownButton1";
         this.toolStripDropDownButton1.Size = new System.Drawing.Size(29, 20);
         this.toolStripDropDownButton1.Text = "toolStripDropDownButton1";
         // 
         // tbSetCheck
         // 
         this.tbSetCheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_apply;
         this.tbSetCheck.Name = "tbSetCheck";
         this.tbSetCheck.Size = new System.Drawing.Size(153, 22);
         this.tbSetCheck.Text = "Отметить";
         this.tbSetCheck.Click += new System.EventHandler(this.tbSetCheck_Click);
         // 
         // tbResetCheck
         // 
         this.tbResetCheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.tbResetCheck.Name = "tbResetCheck";
         this.tbResetCheck.Size = new System.Drawing.Size(153, 22);
         this.tbResetCheck.Text = "Снять отметку";
         this.tbResetCheck.Click += new System.EventHandler(this.tbResetCheck_Click);
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // timepicker
         // 
         this.timepicker.CustomFormat = "MMMM yyyy";
         this.timepicker.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.timepicker.Location = new System.Drawing.Point(66, 3);
         this.timepicker.Name = "timepicker";
         this.timepicker.ShowUpDown = true;
         this.timepicker.Size = new System.Drawing.Size(132, 20);
         this.timepicker.TabIndex = 3;
         // 
         // EditOrgPlan
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(725, 475);
         this.Controls.Add(this.timepicker);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "EditOrgPlan";
         this.Text = "Редактор планов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripDropDownButton toolStripDropDownButton1;
      private System.Windows.Forms.ToolStripMenuItem tbSetCheck;
      private System.Windows.Forms.ToolStripMenuItem tbResetCheck;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.DateTimePicker timepicker;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearFind;
      private System.Windows.Forms.ToolStripTextBox tbPlan;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton btnApply;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnChecked;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnMargin;
      private System.Windows.Forms.ToolStripButton btnPlanLoad;
   }
}
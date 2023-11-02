namespace GRSoft.NapoleonManager
{
   partial class FmTimeTracking
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle5 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle6 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTimeTracking));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tbCost = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.bntReport = new System.Windows.Forms.ToolStripButton();
         this.grid = new System.Windows.Forms.DataGridView();
         this.dateTimePicker = new System.Windows.Forms.DateTimePicker();
         this.btnRem = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnAdd,
            this.btnRem,
            this.btnSave,
            this.toolStripSeparator1,
            this.toolStripLabel1,
            this.tbCost,
            this.toolStripSeparator2,
            this.toolStripSeparator3,
            this.bntReport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(988, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
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
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить день";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(95, 22);
         this.toolStripLabel1.Text = "Стоимость ГСМ";
         // 
         // tbCost
         // 
         this.tbCost.Name = "tbCost";
         this.tbCost.Size = new System.Drawing.Size(100, 25);
         this.tbCost.Text = "0";
         this.tbCost.TextChanged += new System.EventHandler(this.tbCost_TextChanged);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // bntReport
         // 
         this.bntReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.bntReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.bntReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.bntReport.Name = "bntReport";
         this.bntReport.Size = new System.Drawing.Size(23, 22);
         this.bntReport.Text = "Отчет";
         this.bntReport.Click += new System.EventHandler(this.bntReport_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4,
            this.Column5});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 25);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(988, 421);
         this.grid.TabIndex = 1;
         this.grid.CellBeginEdit += new System.Windows.Forms.DataGridViewCellCancelEventHandler(this.grid_CellBeginEdit);
         this.grid.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellEndEdit);
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         // 
         // dateTimePicker
         // 
         this.dateTimePicker.CustomFormat = "MMMMyyyy";
         this.dateTimePicker.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dateTimePicker.Location = new System.Drawing.Point(343, 3);
         this.dateTimePicker.Name = "dateTimePicker";
         this.dateTimePicker.ShowUpDown = true;
         this.dateTimePicker.Size = new System.Drawing.Size(105, 20);
         this.dateTimePicker.TabIndex = 2;
         this.dateTimePicker.ValueChanged += new System.EventHandler(this.dateTimePicker_ValueChanged);
         // 
         // btnRem
         // 
         this.btnRem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRem.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.btnRem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRem.Name = "btnRem";
         this.btnRem.Size = new System.Drawing.Size(23, 22);
         this.btnRem.Text = "Удалить день";
         this.btnRem.Click += new System.EventHandler(this.btnRem_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Date";
         dataGridViewCellStyle4.NullValue = null;
         this.dataGridViewTextBoxColumn1.DefaultCellStyle = dataGridViewCellStyle4;
         this.dataGridViewTextBoxColumn1.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Agent";
         this.dataGridViewTextBoxColumn2.HeaderText = "ФИО";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Start";
         dataGridViewCellStyle5.NullValue = null;
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle5;
         this.dataGridViewTextBoxColumn3.HeaderText = "Начало";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Finish";
         dataGridViewCellStyle6.NullValue = null;
         this.dataGridViewTextBoxColumn4.DefaultCellStyle = dataGridViewCellStyle6;
         this.dataGridViewTextBoxColumn4.HeaderText = "Конец";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.DataPropertyName = "KM";
         this.dataGridViewTextBoxColumn5.HeaderText = "КМ";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Date";
         dataGridViewCellStyle1.NullValue = null;
         this.Column1.DefaultCellStyle = dataGridViewCellStyle1;
         this.Column1.HeaderText = "Дата";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Agent";
         this.Column2.HeaderText = "ФИО";
         this.Column2.Name = "Column2";
         this.Column2.ReadOnly = true;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Start";
         dataGridViewCellStyle2.Format = "t";
         dataGridViewCellStyle2.NullValue = "(##:##)";
         this.Column3.DefaultCellStyle = dataGridViewCellStyle2;
         this.Column3.HeaderText = "Начало";
         this.Column3.Name = "Column3";
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "Finish";
         dataGridViewCellStyle3.Format = "t";
         dataGridViewCellStyle3.NullValue = null;
         this.Column4.DefaultCellStyle = dataGridViewCellStyle3;
         this.Column4.HeaderText = "Конец";
         this.Column4.Name = "Column4";
         // 
         // Column5
         // 
         this.Column5.DataPropertyName = "KM";
         this.Column5.HeaderText = "КМ";
         this.Column5.Name = "Column5";
         // 
         // FmGSMEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(988, 446);
         this.Controls.Add(this.dateTimePicker);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGSMEdit";
         this.Text = "Учёт рабочего времени";
         this.Load += new System.EventHandler(this.FmGSMEdit_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox tbCost;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton bntReport;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DateTimePicker dateTimePicker;
      private System.Windows.Forms.ToolStripButton btnRem;
   }
}
using System;

namespace GRSoft.NapoleonManager
{
   partial class FmAgentPlan
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentPlan));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbDivision = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.tsbLoad = new System.Windows.Forms.ToolStripButton();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.tgvPrice = new GRSoft.UILib.TreeGridView();
         this.Column1 = new GRSoft.UILib.TreeGridColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.toolStripLabel1,
            this.cbDivision,
            this.toolStripLabel2,
            this.cbAgents,
            this.tsbLoad});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(831, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(122, 36);
         this.toolStripLabel1.Text = "Подразделение";
         // 
         // cbDivision
         // 
         this.cbDivision.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(150, 39);
         this.cbDivision.SelectedIndexChanged += new System.EventHandler(this.cbDivision_SelectedIndexChanged);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(50, 36);
         this.toolStripLabel2.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(150, 39);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // tsbLoad
         // 
         this.tsbLoad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbLoad.Image = global::GRSoft.NapoleonManager.Properties.Resources.time_shedule;
         this.tsbLoad.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbLoad.Name = "tsbLoad";
         this.tsbLoad.Size = new System.Drawing.Size(36, 36);
         this.tsbLoad.Text = "toolStripButton1";
         this.tsbLoad.ToolTipText = "Загрузка планов";
         this.tsbLoad.Visible = false;
         this.tsbLoad.Click += new System.EventHandler(this.tsbLoad_Click);
         // 
         // dtpDate
         // 
         this.dtpDate.CalendarFont = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpDate.CustomFormat = "MMM yyyy";
         this.dtpDate.Font = new System.Drawing.Font("Arial Narrow", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpDate.Location = new System.Drawing.Point(605, 7);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.ShowUpDown = true;
         this.dtpDate.Size = new System.Drawing.Size(101, 25);
         this.dtpDate.TabIndex = 2;
         this.dtpDate.ValueChanged += new System.EventHandler(this.dtpDate_ValueChanged);
         // 
         // tgvPrice
         // 
         this.tgvPrice.AllowUserToAddRows = false;
         this.tgvPrice.AllowUserToDeleteRows = false;
         this.tgvPrice.AllowUserToResizeRows = false;
         this.tgvPrice.ColumnHeadersHeight = 29;
         this.tgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3});
         this.tgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.tgvPrice.ImageList = null;
         this.tgvPrice.Location = new System.Drawing.Point(0, 39);
         this.tgvPrice.MultiSelect = false;
         this.tgvPrice.Name = "tgvPrice";
         this.tgvPrice.RowHeadersVisible = false;
         this.tgvPrice.RowHeadersWidth = 51;
         this.tgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvPrice.Size = new System.Drawing.Size(831, 546);
         this.tgvPrice.TabIndex = 5;
         this.tgvPrice.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.tgvPrice_CellEndEdit);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.DefaultNodeImage = null;
         this.Column1.HeaderText = "Папка";
         this.Column1.MinimumWidth = 6;
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         this.Column1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Order";
         dataGridViewCellStyle1.Format = "#,#.00;#,#.00; ";
         dataGridViewCellStyle1.NullValue = null;
         this.Column2.DefaultCellStyle = dataGridViewCellStyle1;
         this.Column2.HeaderText = "План заказов";
         this.Column2.MinimumWidth = 6;
         this.Column2.Name = "Column2";
         this.Column2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.Column2.Width = 150;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "AKB";
         dataGridViewCellStyle2.Format = "#,#;#,#;";
         this.Column3.DefaultCellStyle = dataGridViewCellStyle2;
         this.Column3.HeaderText = "План АКБ";
         this.Column3.MinimumWidth = 6;
         this.Column3.Name = "Column3";
         this.Column3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.Column3.Width = 150;
         // 
         // FmAgentPlan
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(831, 585);
         this.Controls.Add(this.tgvPrice);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmAgentPlan";
         this.Text = "План";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DateTimePicker dtpDate;
      protected GRSoft.UILib.TreeGridView tgvPrice;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbDivision;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripButton tsbLoad;
      private UILib.TreeGridColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
   }
}
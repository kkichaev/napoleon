namespace GRSoft.NapoleonManager
{
   partial class FmRouteHistory
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRouteHistory));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.tsbDay = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnWeekDay = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel2,
            this.tsbDay,
            this.toolStripLabel1,
            this.toolStripLabel3,
            this.tsbRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(639, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnWeekDay,
            this.clmnDate});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(639, 521);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellDoubleClick);
         this.dgvItems.KeyDown += new System.Windows.Forms.KeyEventHandler(this.dgvItems_KeyDown);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(34, 22);
         this.toolStripLabel2.Text = "День";
         // 
         // tsbDay
         // 
         this.tsbDay.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.tsbDay.Name = "tsbDay";
         this.tsbDay.Size = new System.Drawing.Size(150, 25);
         this.tsbDay.SelectedIndexChanged += new System.EventHandler(this.tsbDay_SelectedIndexChanged);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel3.Text = "по";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = ((System.Drawing.Image)(resources.GetObject("tsbRefresh.Image")));
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(385, 3);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(139, 20);
         this.dtpEnd.TabIndex = 6;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(213, 3);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(139, 20);
         this.dtpBegin.TabIndex = 5;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Day";
         this.dataGridViewTextBoxColumn1.HeaderText = "День недели";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Date";
         this.dataGridViewTextBoxColumn2.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // clmnWeekDay
         // 
         this.clmnWeekDay.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnWeekDay.DataPropertyName = "Day";
         this.clmnWeekDay.HeaderText = "День недели";
         this.clmnWeekDay.Name = "clmnWeekDay";
         this.clmnWeekDay.ReadOnly = true;
         // 
         // clmnDate
         // 
         this.clmnDate.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnDate.DataPropertyName = "Date";
         this.clmnDate.HeaderText = "Дата";
         this.clmnDate.Name = "clmnDate";
         this.clmnDate.ReadOnly = true;
         // 
         // FmRouteHistory
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(639, 546);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRouteHistory";
         this.Text = "История изменения маршрута";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripComboBox tsbDay;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnWeekDay;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmBNOper
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle5 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBNOper));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSync = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.cbDivision = new System.Windows.Forms.ToolStripComboBox();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSync,
            this.btnSave,
            this.toolStripSeparator1,
            this.cbDivision});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(800, 39);
         this.toolStrip1.TabIndex = 4;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSync
         // 
         this.btnSync.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSync.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnSync.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSync.Name = "btnSync";
         this.btnSync.Size = new System.Drawing.Size(36, 36);
         this.btnSync.Text = "Обновить";
         this.btnSync.Click += new System.EventHandler(this.btnSync_Click);
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // cbDivision
         // 
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(161, 39);
         this.cbDivision.Sorted = true;
         this.cbDivision.SelectedIndexChanged += new System.EventHandler(this.cbDivision_SelectedIndexChanged);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4,
            this.Column5,
            this.Column6,
            this.Column7,
            this.Column8});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 39);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(800, 411);
         this.grid.TabIndex = 5;
         this.grid.CellBeginEdit += new System.Windows.Forms.DataGridViewCellCancelEventHandler(this.grid_CellBeginEdit);
         this.grid.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellEndEdit);
         this.grid.CurrentCellDirtyStateChanged += new System.EventHandler(this.grid_CurrentCellDirtyStateChanged);
         this.grid.Scroll += new System.Windows.Forms.ScrollEventHandler(this.grid_Scroll);
         this.grid.KeyDown += new System.Windows.Forms.KeyEventHandler(this.grid_KeyDown);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Agent";
         this.Column1.HeaderText = "ФИО";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Mo";
         dataGridViewCellStyle1.Format = "t";
         dataGridViewCellStyle1.NullValue = null;
         this.Column2.DefaultCellStyle = dataGridViewCellStyle1;
         this.Column2.HeaderText = "ПН";
         this.Column2.Name = "Column2";
         this.Column2.Width = 50;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Tu";
         dataGridViewCellStyle2.Format = "t";
         dataGridViewCellStyle2.NullValue = null;
         this.Column3.DefaultCellStyle = dataGridViewCellStyle2;
         this.Column3.HeaderText = "ВТ";
         this.Column3.Name = "Column3";
         this.Column3.Width = 50;
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "We";
         dataGridViewCellStyle3.Format = "t";
         this.Column4.DefaultCellStyle = dataGridViewCellStyle3;
         this.Column4.HeaderText = "СР";
         this.Column4.Name = "Column4";
         this.Column4.Width = 50;
         // 
         // Column5
         // 
         this.Column5.DataPropertyName = "Th";
         dataGridViewCellStyle4.Format = "t";
         this.Column5.DefaultCellStyle = dataGridViewCellStyle4;
         this.Column5.HeaderText = "ЧТ";
         this.Column5.Name = "Column5";
         this.Column5.Width = 50;
         // 
         // Column6
         // 
         this.Column6.DataPropertyName = "Fr";
         dataGridViewCellStyle5.Format = "t";
         this.Column6.DefaultCellStyle = dataGridViewCellStyle5;
         this.Column6.HeaderText = "ПТ";
         this.Column6.Name = "Column6";
         this.Column6.Width = 50;
         // 
         // Column7
         // 
         this.Column7.DataPropertyName = "Sa";
         this.Column7.HeaderText = "СБ";
         this.Column7.Name = "Column7";
         this.Column7.Width = 50;
         // 
         // Column8
         // 
         this.Column8.DataPropertyName = "Su";
         this.Column8.HeaderText = "ВС";
         this.Column8.Name = "Column8";
         this.Column8.Width = 50;
         // 
         // FmBNOper
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(800, 450);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBNOper";
         this.Text = "Безналичные операции";
         this.Load += new System.EventHandler(this.FmBNOper_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnSync;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripComboBox cbDivision;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column8;
   }
}
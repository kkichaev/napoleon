namespace GRSoft.NapoleonManager
{
   partial class FmForsakeReturns
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmForsakeReturns));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnPrint = new System.Windows.Forms.ToolStripButton();
         this.cbAgent = new System.Windows.Forms.ToolStripComboBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvReturn = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column7 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.dgvItem = new System.Windows.Forms.DataGridView();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvReturn)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItem)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.btnPrint,
            this.cbAgent});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(980, 25);
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
         // btnPrint
         // 
         this.btnPrint.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPrint.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.btnPrint.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPrint.Name = "btnPrint";
         this.btnPrint.Size = new System.Drawing.Size(23, 22);
         this.btnPrint.Text = "Список контрагентов";
         this.btnPrint.Click += new System.EventHandler(this.btnPrint_Click);
         // 
         // cbAgent
         // 
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(121, 25);
         this.cbAgent.SelectedIndexChanged += new System.EventHandler(this.cbAgent_SelectedIndexChanged);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvReturn);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItem);
         this.splitContainer1.Size = new System.Drawing.Size(980, 582);
         this.splitContainer1.SplitterDistance = 333;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvReturn
         // 
         this.dgvReturn.AllowUserToAddRows = false;
         this.dgvReturn.AllowUserToDeleteRows = false;
         this.dgvReturn.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvReturn.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4,
            this.Column7});
         this.dgvReturn.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvReturn.Location = new System.Drawing.Point(0, 0);
         this.dgvReturn.Name = "dgvReturn";
         this.dgvReturn.RowHeadersVisible = false;
         this.dgvReturn.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvReturn.Size = new System.Drawing.Size(980, 333);
         this.dgvReturn.TabIndex = 0;
         this.dgvReturn.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvReturn_CellValueChanged);
         this.dgvReturn.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvReturn_RowEnter);
         this.dgvReturn.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvReturn_CellContentClick);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "OrgName";
         this.Column1.HeaderText = "Организация";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Created";
         this.Column2.HeaderText = "Дата создания";
         this.Column2.Name = "Column2";
         // 
         // Column3
         // 
         this.Column3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column3.DataPropertyName = "Sended";
         this.Column3.HeaderText = "Дата передачи";
         this.Column3.Name = "Column3";
         // 
         // Column4
         // 
         this.Column4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column4.DataPropertyName = "SSum";
         this.Column4.HeaderText = "Сумма";
         this.Column4.Name = "Column4";
         // 
         // Column7
         // 
         this.Column7.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column7.DataPropertyName = "Collect";
         this.Column7.HeaderText = "Забран";
         this.Column7.Name = "Column7";
         // 
         // dgvItem
         // 
         this.dgvItem.AllowUserToAddRows = false;
         this.dgvItem.AllowUserToDeleteRows = false;
         this.dgvItem.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItem.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column5,
            this.Column6});
         this.dgvItem.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItem.Location = new System.Drawing.Point(0, 0);
         this.dgvItem.Name = "dgvItem";
         this.dgvItem.RowHeadersVisible = false;
         this.dgvItem.Size = new System.Drawing.Size(980, 245);
         this.dgvItem.TabIndex = 0;
         // 
         // Column5
         // 
         this.Column5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column5.DataPropertyName = "Item";
         this.Column5.HeaderText = "Наименование";
         this.Column5.Name = "Column5";
         // 
         // Column6
         // 
         this.Column6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column6.DataPropertyName = "Qty";
         this.Column6.HeaderText = "Количество";
         this.Column6.Name = "Column6";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "OrgName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Организация";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Created";
         this.dataGridViewTextBoxColumn2.HeaderText = "Дата создания";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Sended";
         this.dataGridViewTextBoxColumn3.HeaderText = "Дата передачи";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Sum";
         this.dataGridViewTextBoxColumn4.HeaderText = "Сумма";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn6.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         // 
         // FmForsakeReturns
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(980, 607);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmForsakeReturns";
         this.Text = "Незабранные возвраты";
         this.Load += new System.EventHandler(this.FmForsakeReturns_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvReturn)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItem)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripComboBox cbAgent;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvReturn;
      private System.Windows.Forms.DataGridView dgvItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column7;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
      private System.Windows.Forms.ToolStripButton btnPrint;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmRegionParent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRegionParent));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvR2 = new System.Windows.Forms.DataGridView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddR2 = new System.Windows.Forms.ToolStripButton();
         this.btnEditR2 = new System.Windows.Forms.ToolStripButton();
         this.btnDelR2 = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.dgvR1 = new System.Windows.Forms.DataGridView();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddR1 = new System.Windows.Forms.ToolStripButton();
         this.btnEditR1 = new System.Windows.Forms.ToolStripButton();
         this.btnDelR1 = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvR1Code = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvR1Name = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvR2Code = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvR2Name = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvR2)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvR1)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 399);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(564, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvR2);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvR1);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(564, 399);
         this.splitContainer1.SplitterDistance = 282;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvR2
         // 
         this.dgvR2.AllowUserToAddRows = false;
         this.dgvR2.AllowUserToDeleteRows = false;
         this.dgvR2.AllowUserToResizeRows = false;
         this.dgvR2.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvR2.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvR1Code,
            this.dgvR1Name});
         this.dgvR2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvR2.Location = new System.Drawing.Point(0, 25);
         this.dgvR2.MultiSelect = false;
         this.dgvR2.Name = "dgvR2";
         this.dgvR2.RowHeadersVisible = false;
         this.dgvR2.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvR2.Size = new System.Drawing.Size(282, 374);
         this.dgvR2.TabIndex = 1;
         this.dgvR2.SelectionChanged += new System.EventHandler(this.dgvR2_SelectionChanged);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddR2,
            this.btnEditR2,
            this.btnDelR2,
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.toolStripLabel1});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(282, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddR2
         // 
         this.btnAddR2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddR2.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddR2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddR2.Name = "btnAddR2";
         this.btnAddR2.Size = new System.Drawing.Size(23, 22);
         this.btnAddR2.Text = "Добавить";
         this.btnAddR2.Click += new System.EventHandler(this.btnAddR2_Click);
         // 
         // btnEditR2
         // 
         this.btnEditR2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditR2.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEditR2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditR2.Name = "btnEditR2";
         this.btnEditR2.Size = new System.Drawing.Size(23, 22);
         this.btnEditR2.Text = "Изменить";
         this.btnEditR2.Click += new System.EventHandler(this.btnEditR2_Click);
         // 
         // btnDelR2
         // 
         this.btnDelR2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelR2.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelR2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelR2.Name = "btnDelR2";
         this.btnDelR2.Size = new System.Drawing.Size(23, 22);
         this.btnDelR2.Text = "Удалить";
         this.btnDelR2.Click += new System.EventHandler(this.btnDelR2_Click);
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(50, 22);
         this.toolStripLabel1.Text = "Область";
         // 
         // dgvR1
         // 
         this.dgvR1.AllowUserToAddRows = false;
         this.dgvR1.AllowUserToDeleteRows = false;
         this.dgvR1.AllowUserToResizeRows = false;
         this.dgvR1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvR1.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvR2Code,
            this.dgvR2Name});
         this.dgvR1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvR1.Location = new System.Drawing.Point(0, 25);
         this.dgvR1.MultiSelect = false;
         this.dgvR1.Name = "dgvR1";
         this.dgvR1.RowHeadersVisible = false;
         this.dgvR1.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvR1.Size = new System.Drawing.Size(278, 374);
         this.dgvR1.TabIndex = 1;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddR1,
            this.btnEditR1,
            this.btnDelR1,
            this.toolStripSeparator2,
            this.toolStripLabel2});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(278, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddR1
         // 
         this.btnAddR1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddR1.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddR1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddR1.Name = "btnAddR1";
         this.btnAddR1.Size = new System.Drawing.Size(23, 22);
         this.btnAddR1.Text = "Добавить";
         this.btnAddR1.Click += new System.EventHandler(this.btnAddR1_Click);
         // 
         // btnEditR1
         // 
         this.btnEditR1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditR1.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEditR1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditR1.Name = "btnEditR1";
         this.btnEditR1.Size = new System.Drawing.Size(23, 22);
         this.btnEditR1.Text = "Изменить";
         this.btnEditR1.Click += new System.EventHandler(this.btnEditR1_Click);
         // 
         // btnDelR1
         // 
         this.btnDelR1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelR1.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelR1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelR1.Name = "btnDelR1";
         this.btnDelR1.Size = new System.Drawing.Size(23, 22);
         this.btnDelR1.Text = "Удалить";
         this.btnDelR1.Click += new System.EventHandler(this.btnDelR1_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(37, 22);
         this.toolStripLabel2.Text = "Район";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Code";
         this.dataGridViewTextBoxColumn1.FillWeight = 50F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Код";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn2.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Code";
         this.dataGridViewTextBoxColumn3.FillWeight = 50F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Код";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn4.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dgvR1Code
         // 
         this.dgvR1Code.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvR1Code.DataPropertyName = "Code";
         this.dgvR1Code.FillWeight = 50F;
         this.dgvR1Code.HeaderText = "Код";
         this.dgvR1Code.Name = "dgvR1Code";
         // 
         // dgvR1Name
         // 
         this.dgvR1Name.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvR1Name.DataPropertyName = "Name";
         this.dgvR1Name.HeaderText = "Наименование";
         this.dgvR1Name.Name = "dgvR1Name";
         // 
         // dgvR2Code
         // 
         this.dgvR2Code.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvR2Code.DataPropertyName = "Code";
         this.dgvR2Code.FillWeight = 50F;
         this.dgvR2Code.HeaderText = "Код";
         this.dgvR2Code.Name = "dgvR2Code";
         // 
         // dgvR2Name
         // 
         this.dgvR2Name.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvR2Name.DataPropertyName = "Name";
         this.dgvR2Name.HeaderText = "Наименование";
         this.dgvR2Name.Name = "dgvR2Name";
         // 
         // FmRegionParent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(564, 421);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRegionParent";
         this.Text = "Область - район";
         this.Load += new System.EventHandler(this.FmRegionParent_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmRegionParent_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvR2)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvR1)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnAddR2;
      private System.Windows.Forms.ToolStripButton btnEditR2;
      private System.Windows.Forms.ToolStripButton btnDelR2;
      private System.Windows.Forms.ToolStripButton btnAddR1;
      private System.Windows.Forms.ToolStripButton btnEditR1;
      private System.Windows.Forms.ToolStripButton btnDelR1;
      private System.Windows.Forms.DataGridView dgvR2;
      private System.Windows.Forms.DataGridView dgvR1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvR1Code;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvR1Name;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvR2Code;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvR2Name;
   }
}
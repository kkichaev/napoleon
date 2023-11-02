namespace GRSoft.NapoleonManager
{
   partial class FmPartShelf
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPartShelf));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvContract = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPartShelf = new System.Windows.Forms.DataGridView();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContract)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPartShelf)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnRefresh,
            this.toolStripSeparator2});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(779, 25);
         this.toolStrip1.TabIndex = 1;
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
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Удалить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 3, 18, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(62, 0);
         this.dpv.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(370, 25);
         this.dpv.Start = new System.DateTime(2015, 3, 18, 0, 0, 0, 0);
         this.dpv.TabIndex = 3;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvContract);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvPartShelf);
         this.splitContainer1.Size = new System.Drawing.Size(779, 398);
         this.splitContainer1.SplitterDistance = 329;
         this.splitContainer1.TabIndex = 4;
         // 
         // dgvContract
         // 
         this.dgvContract.AllowUserToAddRows = false;
         this.dgvContract.AllowUserToResizeRows = false;
         this.dgvContract.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContract.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.dgvContract.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvContract.Location = new System.Drawing.Point(0, 0);
         this.dgvContract.Name = "dgvContract";
         this.dgvContract.RowHeadersVisible = false;
         this.dgvContract.Size = new System.Drawing.Size(329, 398);
         this.dgvContract.TabIndex = 1;
         this.dgvContract.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvContract_RowEnter);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Контракт";
         this.Column1.Name = "Column1";
         // 
         // dgvPartShelf
         // 
         this.dgvPartShelf.AllowUserToAddRows = false;
         this.dgvPartShelf.AllowUserToResizeRows = false;
         this.dgvPartShelf.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPartShelf.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column2,
            this.Column3});
         this.dgvPartShelf.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPartShelf.Location = new System.Drawing.Point(0, 0);
         this.dgvPartShelf.Name = "dgvPartShelf";
         this.dgvPartShelf.RowHeadersVisible = false;
         this.dgvPartShelf.Size = new System.Drawing.Size(446, 398);
         this.dgvPartShelf.TabIndex = 0;
         this.dgvPartShelf.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPartShelf_CellValueChanged);
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Name";
         this.Column2.HeaderText = "Сеть";
         this.Column2.Name = "Column2";
         // 
         // Column3
         // 
         this.Column3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column3.DataPropertyName = "Part";
         this.Column3.FillWeight = 20F;
         this.Column3.HeaderText = "Доля";
         this.Column3.Name = "Column3";
         // 
         // FmPartShelf
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(779, 423);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.dpv);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmPartShelf";
         this.Text = "Доля полки";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPartShelf_FormClosing);
         this.Load += new System.EventHandler(this.FmPartShelf_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvContract)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPartShelf)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private DatePeriodView dpv;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvContract;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridView dgvPartShelf;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
   }
}
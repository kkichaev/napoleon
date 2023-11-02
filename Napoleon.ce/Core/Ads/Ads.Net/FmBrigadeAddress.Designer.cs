namespace GRSoft.Ads
{
   partial class FmBrigadeAddress
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBrigadeAddress));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbBrigade = new System.Windows.Forms.ToolStripComboBox();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvAddress = new System.Windows.Forms.DataGridView();
         this.dgvAddressAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.wb = new System.Windows.Forms.WebBrowser();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAddress)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbBrigade,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.tbnAdd,
            this.btnDel,
            this.toolStripSeparator2,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(876, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbBrigade
         // 
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(121, 25);
         this.cbBrigade.SelectedIndexChanged += new System.EventHandler(this.cbBrigade_SelectedIndexChanged);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Все адреса";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbnAdd
         // 
         this.tbnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbnAdd.Image = ((System.Drawing.Image)(resources.GetObject("tbnAdd.Image")));
         this.tbnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbnAdd.Name = "tbnAdd";
         this.tbnAdd.Size = new System.Drawing.Size(23, 22);
         this.tbnAdd.Text = "Добавить";
         this.tbnAdd.Click += new System.EventHandler(this.tbnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 505);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(876, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvAddress);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.wb);
         this.splitContainer1.Size = new System.Drawing.Size(876, 480);
         this.splitContainer1.SplitterDistance = 367;
         this.splitContainer1.TabIndex = 2;
         // 
         // dgvAddress
         // 
         this.dgvAddress.AllowUserToAddRows = false;
         this.dgvAddress.AllowUserToDeleteRows = false;
         this.dgvAddress.AllowUserToResizeRows = false;
         this.dgvAddress.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAddress.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvAddressAddress});
         this.dgvAddress.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAddress.Location = new System.Drawing.Point(0, 0);
         this.dgvAddress.Name = "dgvAddress";
         this.dgvAddress.RowHeadersVisible = false;
         this.dgvAddress.Size = new System.Drawing.Size(367, 480);
         this.dgvAddress.TabIndex = 0;
         this.dgvAddress.DoubleClick += new System.EventHandler(this.dgvAddress_DoubleClick);
         // 
         // dgvAddressAddress
         // 
         this.dgvAddressAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvAddressAddress.DataPropertyName = "Address";
         this.dgvAddressAddress.HeaderText = "Адрес";
         this.dgvAddressAddress.Name = "dgvAddressAddress";
         // 
         // wb
         // 
         this.wb.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wb.Location = new System.Drawing.Point(0, 0);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.Size = new System.Drawing.Size(505, 480);
         this.wb.TabIndex = 0;
         // 
         // FmBrigadeAddress
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(876, 527);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBrigadeAddress";
         this.Text = "Адрес бригад";
         this.Load += new System.EventHandler(this.FmBrigadeAddress_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmBrigadeAddress_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAddress)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvAddress;
      private System.Windows.Forms.ToolStripComboBox cbBrigade;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton tbnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.WebBrowser wb;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvAddressAddress;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnSave;
   }
}
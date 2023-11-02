namespace GRSoft.NapoleonManager
{
   partial class FmContractPlanogram
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmContractPlanogram));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.lvPhotos = new System.Windows.Forms.ListView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFind = new System.Windows.Forms.ToolStripButton();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 39);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.lvPhotos);
         this.splitContainer1.Size = new System.Drawing.Size(823, 614);
         this.splitContainer1.SplitterDistance = 379;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnAddress});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(379, 614);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         // 
         // clmnName
         // 
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Название";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         // 
         // clmnAddress
         // 
         this.clmnAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAddress.DataPropertyName = "Address";
         this.clmnAddress.HeaderText = "Адрес";
         this.clmnAddress.Name = "clmnAddress";
         this.clmnAddress.ReadOnly = true;
         // 
         // dgvItems
         // 
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.Size = new System.Drawing.Size(440, 614);
         this.dgvItems.TabIndex = 1;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "HRef";
         this.Column1.HeaderText = "Ссылка";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Name";
         this.Column2.HeaderText = "Название";
         this.Column2.Name = "Column2";
         this.Column2.Width = 150;
         // 
         // lvPhotos
         // 
         this.lvPhotos.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvPhotos.HideSelection = false;
         this.lvPhotos.LabelEdit = true;
         this.lvPhotos.LargeImageList = this.imageList1;
         this.lvPhotos.Location = new System.Drawing.Point(0, 0);
         this.lvPhotos.Name = "lvPhotos";
         this.lvPhotos.Size = new System.Drawing.Size(440, 614);
         this.lvPhotos.TabIndex = 0;
         this.lvPhotos.UseCompatibleStateImageBehavior = false;
         this.lvPhotos.AfterLabelEdit += new System.Windows.Forms.LabelEditEventHandler(this.lvPhotos_AfterLabelEdit);
         // 
         // imageList1
         // 
         this.imageList1.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
         this.imageList1.ImageSize = new System.Drawing.Size(128, 128);
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnDel,
            this.toolStripButton1,
            this.tbFind,
            this.btnClearFind});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(823, 39);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "Добавить фото";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "Удалить фото";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.remnants_report;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(36, 36);
         this.toolStripButton1.Text = "Сохранить";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // tbFind
         // 
         this.tbFind.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(200, 39);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // btnClearFind
         // 
         this.btnClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFind.Name = "btnClearFind";
         this.btnClearFind.Size = new System.Drawing.Size(36, 36);
         this.btnClearFind.Text = "toolStripButton2";
         this.btnClearFind.ToolTipText = "Очистить поиск";
         this.btnClearFind.Click += new System.EventHandler(this.btnClearFind_Click);
         // 
         // timer1
         // 
         this.timer1.Interval = 400;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmContractPlanogram
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(823, 653);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmContractPlanogram";
         this.Text = "Планограммы магазинов";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAddress;
      private System.Windows.Forms.ListView lvPhotos;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearFind;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
   }
}
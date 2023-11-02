namespace GRSoft.NapoleonManager
{
   partial class FmGoodsValues
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGoodsValues));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbDel = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFind = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnOrgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnPriceName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnRedLine = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnGreenLine = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAdd,
            this.tsbDel,
            this.tsbSave,
            this.tbFind,
            this.btnClearFind});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(864, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(36, 36);
         this.tsbAdd.Text = "Добавить товар";
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbDel
         // 
         this.tsbDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsbDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDel.Name = "tsbDel";
         this.tsbDel.Size = new System.Drawing.Size(36, 36);
         this.tsbDel.Text = "Удалить товар";
         this.tsbDel.Click += new System.EventHandler(this.tsbDel_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Записать";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
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
         this.splitContainer1.Size = new System.Drawing.Size(864, 568);
         this.splitContainer1.SplitterDistance = 287;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrgName,
            this.clmnAddress});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(287, 568);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         // 
         // clmnOrgName
         // 
         this.clmnOrgName.DataPropertyName = "Name";
         this.clmnOrgName.HeaderText = "Наименование";
         this.clmnOrgName.Name = "clmnOrgName";
         this.clmnOrgName.ReadOnly = true;
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
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPriceName,
            this.clmnRedLine,
            this.clmnGreenLine});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(573, 568);
         this.dgvItems.TabIndex = 0;
         // 
         // clmnPriceName
         // 
         this.clmnPriceName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPriceName.DataPropertyName = "Name";
         this.clmnPriceName.HeaderText = "Товар";
         this.clmnPriceName.Name = "clmnPriceName";
         // 
         // clmnRedLine
         // 
         this.clmnRedLine.DataPropertyName = "RedLine";
         this.clmnRedLine.HeaderText = "Нижний порог";
         this.clmnRedLine.Name = "clmnRedLine";
         // 
         // clmnGreenLine
         // 
         this.clmnGreenLine.DataPropertyName = "GreenLine";
         this.clmnGreenLine.HeaderText = "Верхний порог";
         this.clmnGreenLine.Name = "clmnGreenLine";
         // 
         // timer1
         // 
         this.timer1.Interval = 400;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmGoodsValues
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(864, 607);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGoodsValues";
         this.Text = "Пороги товаров по магазинам";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrgName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPriceName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnRedLine;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnGreenLine;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbDel;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearFind;
      private System.Windows.Forms.Timer timer1;
   }
}
namespace GRSoft.Ads
{
   partial class FmPrice
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPrice));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tvFolders = new System.Windows.Forms.TreeView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddGroup = new System.Windows.Forms.ToolStripButton();
         this.btnEditGroup = new System.Windows.Forms.ToolStripButton();
         this.btnDelGroup = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.dgvPrice = new System.Windows.Forms.DataGridView();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddPrice = new System.Windows.Forms.ToolStripButton();
         this.btnEditPrice = new System.Windows.Forms.ToolStripButton();
         this.btnDelPrice = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvPriceName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPriceCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPriceQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.groupBox1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.groupBox2);
         this.splitContainer1.Size = new System.Drawing.Size(740, 513);
         this.splitContainer1.SplitterDistance = 246;
         this.splitContainer1.TabIndex = 0;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tvFolders);
         this.groupBox1.Controls.Add(this.toolStrip1);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(246, 513);
         this.groupBox1.TabIndex = 1;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Группы";
         // 
         // tvFolders
         // 
         this.tvFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvFolders.HideSelection = false;
         this.tvFolders.Location = new System.Drawing.Point(3, 41);
         this.tvFolders.Name = "tvFolders";
         this.tvFolders.Size = new System.Drawing.Size(240, 469);
         this.tvFolders.TabIndex = 1;
         this.tvFolders.MouseUp += new System.Windows.Forms.MouseEventHandler(this.tvFolders_MouseUp);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddGroup,
            this.btnEditGroup,
            this.btnDelGroup,
            this.btnRefresh,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(3, 16);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(240, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddGroup
         // 
         this.btnAddGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddGroup.Image = ((System.Drawing.Image)(resources.GetObject("btnAddGroup.Image")));
         this.btnAddGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddGroup.Name = "btnAddGroup";
         this.btnAddGroup.Size = new System.Drawing.Size(23, 22);
         this.btnAddGroup.Text = "Создать";
         this.btnAddGroup.Click += new System.EventHandler(this.btnAddFolder_Click);
         // 
         // btnEditGroup
         // 
         this.btnEditGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditGroup.Image = ((System.Drawing.Image)(resources.GetObject("btnEditGroup.Image")));
         this.btnEditGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditGroup.Name = "btnEditGroup";
         this.btnEditGroup.Size = new System.Drawing.Size(23, 22);
         this.btnEditGroup.Text = "Изменить";
         this.btnEditGroup.Click += new System.EventHandler(this.btnEditGroup_Click);
         // 
         // btnDelGroup
         // 
         this.btnDelGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelGroup.Image = ((System.Drawing.Image)(resources.GetObject("btnDelGroup.Image")));
         this.btnDelGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelGroup.Name = "btnDelGroup";
         this.btnDelGroup.Size = new System.Drawing.Size(23, 22);
         this.btnDelGroup.Text = "Удалить";
         this.btnDelGroup.Click += new System.EventHandler(this.btnDelGroup_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
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
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.dgvPrice);
         this.groupBox2.Controls.Add(this.toolStrip2);
         this.groupBox2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox2.Location = new System.Drawing.Point(0, 0);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(490, 513);
         this.groupBox2.TabIndex = 0;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Товары";
         // 
         // dgvPrice
         // 
         this.dgvPrice.AllowUserToAddRows = false;
         this.dgvPrice.AllowUserToDeleteRows = false;
         this.dgvPrice.AllowUserToResizeRows = false;
         this.dgvPrice.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvPriceName,
            this.dgvPriceCost,
            this.dgvPriceQty});
         this.dgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPrice.Location = new System.Drawing.Point(3, 41);
         this.dgvPrice.Name = "dgvPrice";
         this.dgvPrice.RowHeadersVisible = false;
         this.dgvPrice.Size = new System.Drawing.Size(484, 469);
         this.dgvPrice.TabIndex = 1;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddPrice,
            this.btnEditPrice,
            this.btnDelPrice});
         this.toolStrip2.Location = new System.Drawing.Point(3, 16);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(484, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddPrice
         // 
         this.btnAddPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddPrice.Image = ((System.Drawing.Image)(resources.GetObject("btnAddPrice.Image")));
         this.btnAddPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddPrice.Name = "btnAddPrice";
         this.btnAddPrice.Size = new System.Drawing.Size(23, 22);
         this.btnAddPrice.Text = "Создать";
         this.btnAddPrice.Click += new System.EventHandler(this.btnAddPrice_Click);
         // 
         // btnEditPrice
         // 
         this.btnEditPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditPrice.Image = ((System.Drawing.Image)(resources.GetObject("btnEditPrice.Image")));
         this.btnEditPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditPrice.Name = "btnEditPrice";
         this.btnEditPrice.Size = new System.Drawing.Size(23, 22);
         this.btnEditPrice.Text = "Изменить";
         this.btnEditPrice.Click += new System.EventHandler(this.btnEditPrice_Click);
         // 
         // btnDelPrice
         // 
         this.btnDelPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelPrice.Image = ((System.Drawing.Image)(resources.GetObject("btnDelPrice.Image")));
         this.btnDelPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelPrice.Name = "btnDelPrice";
         this.btnDelPrice.Size = new System.Drawing.Size(23, 22);
         this.btnDelPrice.Text = "Удалить";
         this.btnDelPrice.Click += new System.EventHandler(this.btnDelPrice_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 513);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(740, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvPriceName
         // 
         this.dgvPriceName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPriceName.DataPropertyName = "Name";
         this.dgvPriceName.FillWeight = 80F;
         this.dgvPriceName.HeaderText = "Наименование";
         this.dgvPriceName.Name = "dgvPriceName";
         // 
         // dgvPriceCost
         // 
         this.dgvPriceCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPriceCost.DataPropertyName = "Cost";
         this.dgvPriceCost.FillWeight = 20F;
         this.dgvPriceCost.HeaderText = "Цена";
         this.dgvPriceCost.Name = "dgvPriceCost";
         // 
         // dgvPriceQty
         // 
         this.dgvPriceQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPriceQty.DataPropertyName = "Qty";
         this.dgvPriceQty.FillWeight = 20F;
         this.dgvPriceQty.HeaderText = "Кол-во";
         this.dgvPriceQty.Name = "dgvPriceQty";
         this.dgvPriceQty.Visible = false;
         // 
         // FmPrice
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(740, 535);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPrice";
         this.Text = "Товары и услуги";
         this.Load += new System.EventHandler(this.FmPrice_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmPrice_FormClosed);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPrice_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.TreeView tvFolders;
      private System.Windows.Forms.DataGridView dgvPrice;
      private System.Windows.Forms.ToolStripButton btnAddGroup;
      private System.Windows.Forms.ToolStripButton btnEditGroup;
      private System.Windows.Forms.ToolStripButton btnDelGroup;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAddPrice;
      private System.Windows.Forms.ToolStripButton btnEditPrice;
      private System.Windows.Forms.ToolStripButton btnDelPrice;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPriceName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPriceCost;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPriceQty;
   }
}
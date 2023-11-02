namespace GRSoft.NapoleonManager
{
   partial class MinCostEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MinCostEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsbAddItem = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbClearFind = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(890, 600);
         this.splitContainer1.SplitterDistance = 373;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(373, 600);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "Org";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.ReadOnly = true;
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnCost});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(513, 575);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Item";
         this.clmnItem.HeaderText = "Товар";
         this.clmnItem.Name = "clmnItem";
         // 
         // clmnCost
         // 
         this.clmnCost.DataPropertyName = "Cost";
         this.clmnCost.HeaderText = "Мин.цена";
         this.clmnCost.Name = "clmnCost";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAddItem,
            this.tsbRemove});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(513, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsbAddItem
         // 
         this.tsbAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddItem.Name = "tsbAddItem";
         this.tsbAddItem.Size = new System.Drawing.Size(23, 22);
         this.tsbAddItem.Text = "Добавить товары";
         this.tsbAddItem.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(23, 22);
         this.tsbRemove.Text = "Удалить выделенные товары";
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbFind,
            this.tsbClearFind,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(890, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(200, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // tsbClearFind
         // 
         this.tsbClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsbClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbClearFind.Name = "tsbClearFind";
         this.tsbClearFind.Size = new System.Drawing.Size(23, 22);
         this.tsbClearFind.Text = "Очистить поиск";
         this.tsbClearFind.Click += new System.EventHandler(this.ClearFind);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // MinCostEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(890, 625);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "MinCostEditor";
         this.Text = "Минимальные цены на товар";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton tsbClearFind;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbAddItem;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCost;
      private System.Windows.Forms.Timer timer1;
   }
}
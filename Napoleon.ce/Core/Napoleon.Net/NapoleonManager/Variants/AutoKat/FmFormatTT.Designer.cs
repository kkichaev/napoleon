
namespace GRSoft.NapoleonManager
{
   partial class FmFormatTT
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFormatTT));
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.tsbUp = new System.Windows.Forms.ToolStripButton();
         this.tsbDn = new System.Windows.Forms.ToolStripButton();
         this.grid = new System.Windows.Forms.DataGridView();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddItem = new System.Windows.Forms.ToolStripButton();
         this.btnDelItem = new System.Windows.Forms.ToolStripButton();
         this.btUpItem = new System.Windows.Forms.ToolStripButton();
         this.btnDownItem = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Номенклатура";
         this.dataGridViewTextBoxColumn1.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.grid);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(411, 456);
         this.splitContainer1.SplitterDistance = 228;
         this.splitContainer1.TabIndex = 5;
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 39);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(2);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.RowHeadersWidth = 51;
         this.dgvItems.RowTemplate.Height = 24;
         this.dgvItems.Size = new System.Drawing.Size(411, 189);
         this.dgvItems.TabIndex = 6;
         this.dgvItems.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_RowEnter);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Формат клиента";
         this.clmnName.MinimumWidth = 6;
         this.clmnName.Name = "clmnName";
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSave,
            this.btnAdd,
            this.btnDel,
            this.tsbUp,
            this.tsbDn});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(411, 39);
         this.toolStrip1.TabIndex = 5;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "toolStripButton1";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click_1);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "Создать";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // tsbUp
         // 
         this.tsbUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.tsbUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUp.Name = "tsbUp";
         this.tsbUp.Size = new System.Drawing.Size(36, 36);
         this.tsbUp.Text = "Вверх";
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         // 
         // tsbDn
         // 
         this.tsbDn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDn.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.tsbDn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDn.Name = "tsbDn";
         this.tsbDn.Size = new System.Drawing.Size(36, 36);
         this.tsbDn.Text = "Вниз";
         this.tsbDn.Click += new System.EventHandler(this.tsbDn_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dataGridViewTextBoxColumn2});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 39);
         this.grid.Margin = new System.Windows.Forms.Padding(2);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.RowHeadersWidth = 51;
         this.grid.RowTemplate.Height = 24;
         this.grid.Size = new System.Drawing.Size(411, 185);
         this.grid.TabIndex = 8;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn2.HeaderText = "Тип точки";
         this.dataGridViewTextBoxColumn2.MinimumWidth = 6;
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // toolStrip2
         // 
         this.toolStrip2.AutoSize = false;
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddItem,
            this.btnDelItem,
            this.btUpItem,
            this.btnDownItem});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(411, 39);
         this.toolStrip2.TabIndex = 7;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddItem
         // 
         this.btnAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddItem.Name = "btnAddItem";
         this.btnAddItem.Size = new System.Drawing.Size(36, 36);
         this.btnAddItem.Text = "Создать";
         this.btnAddItem.Click += new System.EventHandler(this.btnAddItem_Click);
         // 
         // btnDelItem
         // 
         this.btnDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelItem.Name = "btnDelItem";
         this.btnDelItem.Size = new System.Drawing.Size(36, 36);
         this.btnDelItem.Text = "Удалить";
         this.btnDelItem.Click += new System.EventHandler(this.btnDelItem_Click);
         // 
         // btUpItem
         // 
         this.btUpItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btUpItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.btUpItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btUpItem.Name = "btUpItem";
         this.btUpItem.Size = new System.Drawing.Size(36, 36);
         this.btUpItem.Text = "Вверх";
         this.btUpItem.Click += new System.EventHandler(this.btUpItem_Click);
         // 
         // btnDownItem
         // 
         this.btnDownItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDownItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.btnDownItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDownItem.Name = "btnDownItem";
         this.btnDownItem.Size = new System.Drawing.Size(36, 36);
         this.btnDownItem.Text = "Вниз";
         this.btnDownItem.Click += new System.EventHandler(this.btnDownItem_Click);
         // 
         // FmFormatTT
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(411, 456);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2);
         this.Name = "FmFormatTT";
         this.Text = "Формат ТТ";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton tsbUp;
      private System.Windows.Forms.ToolStripButton tsbDn;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnAddItem;
      private System.Windows.Forms.ToolStripButton btnDelItem;
      private System.Windows.Forms.ToolStripButton btUpItem;
      private System.Windows.Forms.ToolStripButton btnDownItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
   }
}
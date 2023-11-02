namespace GRSoft.NapoleonManager
{
   partial class FmPriceEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceEdit));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvPrice = new GRSoft.UILib.TreeViewMS();
         this.menu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miTop = new System.Windows.Forms.ToolStripMenuItem();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnAddFolder = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.dgvPrice = new System.Windows.Forms.DataGridView();
         this.dgvPriceName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tbFilter = new System.Windows.Forms.ToolStripTextBox();
         this.btnFilter = new System.Windows.Forms.ToolStripButton();
         this.btnResetFilter = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.menu.SuspendLayout();
         this.toolStrip3.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 414);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(605, 22);
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
         this.splitContainer1.Panel1.Controls.Add(this.tvPrice);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip3);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvPrice);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(605, 414);
         this.splitContainer1.SplitterDistance = 303;
         this.splitContainer1.TabIndex = 2;
         // 
         // tvPrice
         // 
         this.tvPrice.AllowDrop = true;
         this.tvPrice.ContextMenuStrip = this.menu;
         this.tvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvPrice.HideSelection = false;
         this.tvPrice.ImageIndex = 0;
         this.tvPrice.ImageList = this.imageList1;
         this.tvPrice.Location = new System.Drawing.Point(0, 25);
         this.tvPrice.Name = "tvPrice";
         this.tvPrice.SelectedImageIndex = 0;
         this.tvPrice.SelectedNodes = ((System.Collections.Generic.List<System.Windows.Forms.TreeNode>)(resources.GetObject("tvPrice.SelectedNodes")));
         this.tvPrice.Size = new System.Drawing.Size(303, 389);
         this.tvPrice.TabIndex = 2;
         this.tvPrice.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvPrice_DragDrop);
         this.tvPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvPrice_MouseDown);
         this.tvPrice.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvPrice_DragEnter);
         this.tvPrice.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvPrice_ItemDrag);
         this.tvPrice.DragOver += new System.Windows.Forms.DragEventHandler(this.tvPrice_DragOver);
         // 
         // menu
         // 
         this.menu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miTop});
         this.menu.Name = "menu";
         this.menu.Size = new System.Drawing.Size(169, 26);
         this.menu.Opening += new System.ComponentModel.CancelEventHandler(this.menu_Opening);
         // 
         // miTop
         // 
         this.miTop.Name = "miTop";
         this.miTop.Size = new System.Drawing.Size(168, 22);
         this.miTop.Text = "Поднять наверх";
         this.miTop.Click += new System.EventHandler(this.miTop_Click);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnAddFolder,
            this.btnEdit,
            this.btnDel,
            this.btnSave,
            this.btnUp,
            this.btnDown});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(303, 25);
         this.toolStrip3.TabIndex = 3;
         this.toolStrip3.Text = "toolStrip3";
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
         // btnAddFolder
         // 
         this.btnAddFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddFolder.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddFolder.Name = "btnAddFolder";
         this.btnAddFolder.Size = new System.Drawing.Size(23, 22);
         this.btnAddFolder.Text = "Создать";
         this.btnAddFolder.Click += new System.EventHandler(this.btnAddFolder_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
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
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(23, 22);
         this.btnUp.Text = "Вверх";
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(23, 22);
         this.btnDown.Text = "Вниз";
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // dgvPrice
         // 
         this.dgvPrice.AllowUserToAddRows = false;
         this.dgvPrice.AllowUserToResizeRows = false;
         this.dgvPrice.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvPriceName});
         this.dgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPrice.Location = new System.Drawing.Point(0, 25);
         this.dgvPrice.Name = "dgvPrice";
         this.dgvPrice.RowHeadersVisible = false;
         this.dgvPrice.Size = new System.Drawing.Size(298, 389);
         this.dgvPrice.TabIndex = 0;
         this.dgvPrice.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.dgvPrice_MouseDoubleClick);
         // 
         // dgvPriceName
         // 
         this.dgvPriceName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPriceName.DataPropertyName = "Name";
         this.dgvPriceName.HeaderText = "Наименовние";
         this.dgvPriceName.Name = "dgvPriceName";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbFilter,
            this.btnFilter,
            this.btnResetFilter});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(298, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tbFilter
         // 
         this.tbFilter.Name = "tbFilter";
         this.tbFilter.Size = new System.Drawing.Size(100, 25);
         // 
         // btnFilter
         // 
         this.btnFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFilter.Image = global::GRSoft.NapoleonManager.Properties.Resources.maporg;
         this.btnFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFilter.Name = "btnFilter";
         this.btnFilter.Size = new System.Drawing.Size(23, 22);
         this.btnFilter.Text = "Применить фильтр";
         this.btnFilter.Click += new System.EventHandler(this.btnFilter_Click);
         // 
         // btnResetFilter
         // 
         this.btnResetFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnResetFilter.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnResetFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnResetFilter.Name = "btnResetFilter";
         this.btnResetFilter.Size = new System.Drawing.Size(23, 22);
         this.btnResetFilter.Text = "Очистить фильтр";
         this.btnResetFilter.Click += new System.EventHandler(this.btnResetFilter_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименовние";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmPriceEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(605, 436);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceEdit";
         this.Text = "Редактор номенклатурных групп";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPriceEdit_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.menu.ResumeLayout(false);
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripTextBox tbFilter;
      private System.Windows.Forms.ToolStripButton btnFilter;
      private System.Windows.Forms.DataGridView dgvPrice;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPriceName;
      private System.Windows.Forms.ToolStripButton btnResetFilter;
      private GRSoft.UILib.TreeViewMS tvPrice;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripButton btnAddFolder;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ContextMenuStrip menu;
      private System.Windows.Forms.ToolStripMenuItem miTop;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnDown;
   }
}
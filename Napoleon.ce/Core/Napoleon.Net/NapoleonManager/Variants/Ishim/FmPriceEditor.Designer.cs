namespace GRSoft.NapoleonManager
{
   partial class FmPriceEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvFolders = new System.Windows.Forms.TreeView();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.tsbHidden = new System.Windows.Forms.ToolStripMenuItem();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbAddFolder = new System.Windows.Forms.ToolStripButton();
         this.tsbDelFolder = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbExport = new System.Windows.Forms.ToolStripButton();
         this.tsbImport = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsbAddItem = new System.Windows.Forms.ToolStripButton();
         this.tsbDelItem = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbUpItem = new System.Windows.Forms.ToolStripButton();
         this.tsbDnItem = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.contextMenuStrip1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.tvFolders);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(600, 687);
         this.splitContainer1.SplitterDistance = 318;
         this.splitContainer1.TabIndex = 0;
         // 
         // tvFolders
         // 
         this.tvFolders.AllowDrop = true;
         this.tvFolders.ContextMenuStrip = this.contextMenuStrip1;
         this.tvFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvFolders.ImageIndex = 0;
         this.tvFolders.ImageList = this.imageList1;
         this.tvFolders.LabelEdit = true;
         this.tvFolders.Location = new System.Drawing.Point(0, 39);
         this.tvFolders.Name = "tvFolders";
         this.tvFolders.SelectedImageIndex = 0;
         this.tvFolders.Size = new System.Drawing.Size(600, 279);
         this.tvFolders.TabIndex = 1;
         this.tvFolders.AfterLabelEdit += new System.Windows.Forms.NodeLabelEditEventHandler(this.tvFolders_AfterLabelEdit);
         this.tvFolders.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.tvFolders_ItemDrag);
         this.tvFolders.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.tvFolders_AfterSelect);
         this.tvFolders.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvFolders_DragDrop);
         this.tvFolders.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvFolders_DragEnter);
         this.tvFolders.DragOver += new System.Windows.Forms.DragEventHandler(this.tvFolders_DragOver);
         this.tvFolders.MouseClick += new System.Windows.Forms.MouseEventHandler(this.tvFolders_MouseClick);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbHidden});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(157, 26);
         this.contextMenuStrip1.Opening += new System.ComponentModel.CancelEventHandler(this.contextMenuStrip1_Opening);
         // 
         // tsbHidden
         // 
         this.tsbHidden.Name = "tsbHidden";
         this.tsbHidden.Size = new System.Drawing.Size(156, 22);
         this.tsbHidden.Text = "Скрытая папка";
         this.tsbHidden.Click += new System.EventHandler(this.tsbHidden_Click);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "folder_open.ico");
         this.imageList1.Images.SetKeyName(2, "Generic_Document.ico");
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSave,
            this.tsbRefresh,
            this.toolStripSeparator2,
            this.tsbAddFolder,
            this.tsbDelFolder,
            this.toolStripSeparator3,
            this.tsbExport,
            this.tsbImport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(600, 39);
         this.toolStrip1.TabIndex = 0;
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
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(36, 36);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbAddFolder
         // 
         this.tsbAddFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddFolder.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAddFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddFolder.Name = "tsbAddFolder";
         this.tsbAddFolder.Size = new System.Drawing.Size(36, 36);
         this.tsbAddFolder.Text = "Добавить папку";
         this.tsbAddFolder.Click += new System.EventHandler(this.tsbAddFolder_Click);
         // 
         // tsbDelFolder
         // 
         this.tsbDelFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDelFolder.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsbDelFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDelFolder.Name = "tsbDelFolder";
         this.tsbDelFolder.Size = new System.Drawing.Size(36, 36);
         this.tsbDelFolder.Text = "Удалить папку";
         this.tsbDelFolder.Click += new System.EventHandler(this.tsbDelFolder_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbExport
         // 
         this.tsbExport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbExport.Image = global::GRSoft.NapoleonManager.Properties.Resources.download;
         this.tsbExport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbExport.Name = "tsbExport";
         this.tsbExport.Size = new System.Drawing.Size(36, 36);
         this.tsbExport.Text = "Выгрузить прайс";
         this.tsbExport.Click += new System.EventHandler(this.tsbExport_Click);
         // 
         // tsbImport
         // 
         this.tsbImport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbImport.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_top_5;
         this.tsbImport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbImport.Name = "tsbImport";
         this.tsbImport.Size = new System.Drawing.Size(36, 36);
         this.tsbImport.Text = "Загрзить прайс";
         this.tsbImport.Click += new System.EventHandler(this.tsbImport_Click);
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
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(600, 326);
         this.dgvItems.TabIndex = 1;
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Товар";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAddItem,
            this.tsbDelItem,
            this.toolStripSeparator1,
            this.tsbUpItem,
            this.tsbDnItem,
            this.toolStripButton1});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(600, 39);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsbAddItem
         // 
         this.tsbAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddItem.Name = "tsbAddItem";
         this.tsbAddItem.Size = new System.Drawing.Size(23, 22);
         this.tsbAddItem.Text = "Добавить товар";
         this.tsbAddItem.Click += new System.EventHandler(this.tsbAddItem_Click);
         // 
         // tsbDelItem
         // 
         this.tsbDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsbDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDelItem.Name = "tsbDelItem";
         this.tsbDelItem.Size = new System.Drawing.Size(36, 36);
         this.tsbDelItem.Text = "Удалить товар";
         this.tsbDelItem.Click += new System.EventHandler(this.tsbDelItem_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbUpItem
         // 
         this.tsbUpItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUpItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_41;
         this.tsbUpItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUpItem.Name = "tsbUpItem";
         this.tsbUpItem.Size = new System.Drawing.Size(23, 22);
         this.tsbUpItem.Text = "Вверх";
         this.tsbUpItem.Click += new System.EventHandler(this.tsbUpItem_Click);
         // 
         // tsbDnItem
         // 
         this.tsbDnItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDnItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.tsbDnItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDnItem.Name = "tsbDnItem";
         this.tsbDnItem.Size = new System.Drawing.Size(23, 22);
         this.tsbDnItem.Text = "Вниз";
         this.tsbDnItem.Click += new System.EventHandler(this.tsbDnItem_Click);
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.sorting;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton1.Text = "Сортировка номенклатуры";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmPriceEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(600, 687);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceEditor";
         this.Text = "Редактор прайс-листа";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.contextMenuStrip1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.TreeView tvFolders;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton tsbAddItem;
      private System.Windows.Forms.ToolStripButton tsbDelItem;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton tsbUpItem;
      private System.Windows.Forms.ToolStripButton tsbDnItem;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton tsbAddFolder;
      private System.Windows.Forms.ToolStripButton tsbDelFolder;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton tsbExport;
      private System.Windows.Forms.ToolStripButton tsbImport;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem tsbHidden;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
   }
}
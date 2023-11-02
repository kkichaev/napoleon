namespace GRSoft.NapoleonManager
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPrice));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnExpandAll = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.tsbNewPrice = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.pictures = new System.Windows.Forms.ImageList(this.components);
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tgvPrice = new GRSoft.UILib.TreeGridView();
         this.FPName = new GRSoft.UILib.TreeGridColumn();
         this.FPCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.FPQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.dgvItemsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.menuItems = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.mnuDelItem = new System.Windows.Forms.ToolStripMenuItem();
         this.lvPic = new System.Windows.Forms.ListView();
         this.menuPic = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.mnuDelPic = new System.Windows.Forms.ToolStripMenuItem();
         this.miSetting = new System.Windows.Forms.ToolStripMenuItem();
         this.statusStrip2 = new System.Windows.Forms.StatusStrip();
         this.label = new System.Windows.Forms.ToolStripStatusLabel();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.cbSizes = new System.Windows.Forms.ToolStripComboBox();
         this.dialog = new System.Windows.Forms.OpenFileDialog();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).BeginInit();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.menuItems.SuspendLayout();
         this.menuPic.SuspendLayout();
         this.statusStrip2.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.tbFind,
            this.btnDown,
            this.btnUp,
            this.toolStripSeparator2,
            this.btnExpandAll,
            this.btnSave,
            this.tsbNewPrice});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(645, 25);
         this.toolStrip1.TabIndex = 0;
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(200, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(10, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = ((System.Drawing.Image)(resources.GetObject("btnDown.Image")));
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(23, 22);
         this.btnDown.Text = "Искать вперед";
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = ((System.Drawing.Image)(resources.GetObject("btnUp.Image")));
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(23, 22);
         this.btnUp.Text = "Искать назад";
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnExpandAll
         // 
         this.btnExpandAll.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnExpandAll.Image = ((System.Drawing.Image)(resources.GetObject("btnExpandAll.Image")));
         this.btnExpandAll.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnExpandAll.Name = "btnExpandAll";
         this.btnExpandAll.Size = new System.Drawing.Size(23, 22);
         this.btnExpandAll.Text = "Развернуть";
         this.btnExpandAll.Visible = false;
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // tsbNewPrice
         // 
         this.tsbNewPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbNewPrice.Image = ((System.Drawing.Image)(resources.GetObject("tsbNewPrice.Image")));
         this.tsbNewPrice.ImageTransparentColor = System.Drawing.Color.White;
         this.tsbNewPrice.Name = "tsbNewPrice";
         this.tsbNewPrice.Size = new System.Drawing.Size(23, 22);
         this.tsbNewPrice.Text = "Вид прайса с фото товара";
         this.tsbNewPrice.Click += new System.EventHandler(this.tsbNewPrice_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 432);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(645, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // pictures
         // 
         this.pictures.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
         this.pictures.ImageSize = new System.Drawing.Size(115, 115);
         this.pictures.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tgvPrice);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Panel2.Controls.Add(this.statusStrip2);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(645, 407);
         this.splitContainer1.SplitterDistance = 360;
         this.splitContainer1.TabIndex = 4;
         // 
         // tgvPrice
         // 
         this.tgvPrice.AllowUserToAddRows = false;
         this.tgvPrice.AllowUserToDeleteRows = false;
         this.tgvPrice.AllowUserToResizeRows = false;
         this.tgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.FPName,
            this.FPCost,
            this.FPQty});
         this.tgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvPrice.ImageList = null;
         this.tgvPrice.Location = new System.Drawing.Point(0, 0);
         this.tgvPrice.MultiSelect = false;
         this.tgvPrice.Name = "tgvPrice";
         this.tgvPrice.RowHeadersVisible = false;
         this.tgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvPrice.Size = new System.Drawing.Size(360, 407);
         this.tgvPrice.TabIndex = 3;
         this.tgvPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseDown);
         this.tgvPrice.DoubleClick += new System.EventHandler(this.tgvPrice_DoubleClick);
         this.tgvPrice.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvPrice_CellFormatting);
         this.tgvPrice.MouseUp += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseUp);
         // 
         // FPName
         // 
         this.FPName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPName.DefaultNodeImage = null;
         this.FPName.FillWeight = 300F;
         this.FPName.HeaderText = "Папка/Наименование";
         this.FPName.Name = "FPName";
         this.FPName.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FPCost
         // 
         this.FPCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPCost.HeaderText = "Цена";
         this.FPCost.Name = "FPCost";
         this.FPCost.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPCost.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FPQty
         // 
         this.FPQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPQty.HeaderText = "Кол-во";
         this.FPQty.Name = "FPQty";
         this.FPQty.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPQty.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 25);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvItems);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.lvPic);
         this.splitContainer2.Size = new System.Drawing.Size(281, 360);
         this.splitContainer2.SplitterDistance = 180;
         this.splitContainer2.TabIndex = 5;
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.AllowUserToResizeRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvItemsName});
         this.dgvItems.ContextMenuStrip = this.menuItems;
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(281, 180);
         this.dgvItems.TabIndex = 4;
         this.dgvItems.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvItems_DragEnter);
         this.dgvItems.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvItems_DragDrop);
         // 
         // dgvItemsName
         // 
         this.dgvItemsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvItemsName.DataPropertyName = "Name";
         this.dgvItemsName.HeaderText = "Наименование";
         this.dgvItemsName.Name = "dgvItemsName";
         // 
         // menuItems
         // 
         this.menuItems.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuDelItem});
         this.menuItems.Name = "menuItems";
         this.menuItems.Size = new System.Drawing.Size(119, 26);
         // 
         // mnuDelItem
         // 
         this.mnuDelItem.Name = "mnuDelItem";
         this.mnuDelItem.Size = new System.Drawing.Size(118, 22);
         this.mnuDelItem.Text = "Удалить";
         this.mnuDelItem.Click += new System.EventHandler(this.mnuDelItem_Click);
         // 
         // lvPic
         // 
         this.lvPic.AllowDrop = true;
         this.lvPic.ContextMenuStrip = this.menuPic;
         this.lvPic.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvPic.HideSelection = false;
         this.lvPic.LargeImageList = this.pictures;
         this.lvPic.Location = new System.Drawing.Point(0, 0);
         this.lvPic.MultiSelect = false;
         this.lvPic.Name = "lvPic";
         this.lvPic.Size = new System.Drawing.Size(281, 176);
         this.lvPic.TabIndex = 3;
         this.lvPic.UseCompatibleStateImageBehavior = false;
         this.lvPic.SelectedIndexChanged += new System.EventHandler(this.lvPic_SelectedIndexChanged);
         this.lvPic.DoubleClick += new System.EventHandler(this.lvPic_DoubleClick);
         this.lvPic.DragDrop += new System.Windows.Forms.DragEventHandler(this.lvPic_DragDrop);
         this.lvPic.DragEnter += new System.Windows.Forms.DragEventHandler(this.lvPic_DragEnter);
         this.lvPic.DragOver += new System.Windows.Forms.DragEventHandler(this.lvPic_DragOver);
         // 
         // menuPic
         // 
         this.menuPic.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuDelPic,
            this.miSetting});
         this.menuPic.Name = "menuPic";
         this.menuPic.Size = new System.Drawing.Size(135, 48);
         // 
         // mnuDelPic
         // 
         this.mnuDelPic.Name = "mnuDelPic";
         this.mnuDelPic.Size = new System.Drawing.Size(134, 22);
         this.mnuDelPic.Text = "Удалить";
         this.mnuDelPic.Click += new System.EventHandler(this.mnuDelPic_Click);
         // 
         // miSetting
         // 
         this.miSetting.Name = "miSetting";
         this.miSetting.Size = new System.Drawing.Size(134, 22);
         this.miSetting.Text = "Настройки";
         this.miSetting.Click += new System.EventHandler(this.miSetting_Click);
         // 
         // statusStrip2
         // 
         this.statusStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.label});
         this.statusStrip2.Location = new System.Drawing.Point(0, 385);
         this.statusStrip2.Name = "statusStrip2";
         this.statusStrip2.Size = new System.Drawing.Size(281, 22);
         this.statusStrip2.TabIndex = 2;
         this.statusStrip2.Text = "statusStrip2";
         // 
         // label
         // 
         this.label.Name = "label";
         this.label.Size = new System.Drawing.Size(0, 17);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.cbSizes});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(281, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // cbSizes
         // 
         this.cbSizes.Name = "cbSizes";
         this.cbSizes.Size = new System.Drawing.Size(121, 25);
         // 
         // dialog
         // 
         this.dialog.Filter = "Все файлы|*.*|Изображения|*.jpg;*.png";
         this.dialog.FilterIndex = 2;
         this.dialog.Multiselect = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmPrice
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(645, 454);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPrice";
         this.Text = "Прайс";
         this.Load += new System.EventHandler(this.FmPrice_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).EndInit();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.menuItems.ResumeLayout(false);
         this.menuPic.ResumeLayout(false);
         this.statusStrip2.ResumeLayout(false);
         this.statusStrip2.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      protected System.Windows.Forms.ToolStripComboBox cbAgents;
      protected GRSoft.UILib.TreeGridView tgvPrice;
      private GRSoft.UILib.TreeGridColumn FPName;
      protected System.Windows.Forms.DataGridViewTextBoxColumn FPCost;
      protected System.Windows.Forms.DataGridViewTextBoxColumn FPQty;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnDown;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnExpandAll;
      private System.Windows.Forms.ImageList pictures;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      protected System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripComboBox cbSizes;
      private System.Windows.Forms.OpenFileDialog dialog;
      private System.Windows.Forms.StatusStrip statusStrip2;
      private System.Windows.Forms.ToolStripStatusLabel label;
      protected System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ListView lvPic;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.SplitContainer splitContainer2;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvItemsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ContextMenuStrip menuItems;
      private System.Windows.Forms.ContextMenuStrip menuPic;
      private System.Windows.Forms.ToolStripMenuItem mnuDelPic;
      private System.Windows.Forms.ToolStripMenuItem mnuDelItem;
      private System.Windows.Forms.ToolStripMenuItem miSetting;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton tsbNewPrice;
      
    
   }
}
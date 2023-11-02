namespace GRSoft.NapoleonManager
{
   partial class FmPricePhotoEx
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPricePhotoEx));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAddFolder = new System.Windows.Forms.ToolStripButton();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator4 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFind = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnPicMode = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.cbSizes = new System.Windows.Forms.ToolStripComboBox();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.tsbOldPrice = new System.Windows.Forms.ToolStripButton();
         this.btnLoadOldPrice = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.menuPic = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.mnuDelPic = new System.Windows.Forms.ToolStripMenuItem();
         this.mnuChangePic = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
         this.mnuSetting = new System.Windows.Forms.ToolStripMenuItem();
         this.dialog = new System.Windows.Forms.OpenFileDialog();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.menuFolder = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.msAddFolder = new System.Windows.Forms.ToolStripMenuItem();
         this.msAddItem = new System.Windows.Forms.ToolStripMenuItem();
         this.msDelFolder = new System.Windows.Forms.ToolStripMenuItem();
         this.tgvPrice = new GRSoft.UILib.TreeGridView();
         this.FPName = new GRSoft.UILib.TreeGridColumn();
         this.FPCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnInPack = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.FPQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPhoto = new System.Windows.Forms.DataGridViewImageColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.menuPic.SuspendLayout();
         this.menuFolder.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAddFolder,
            this.tsbAdd,
            this.tsbDel,
            this.toolStripSeparator4,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.tbFind,
            this.btnClearFind,
            this.toolStripSeparator3,
            this.btnPicMode,
            this.toolStripSeparator2,
            this.cbSizes,
            this.btnSave,
            this.tsbOldPrice,
            this.btnLoadOldPrice});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(836, 39);
         this.toolStrip1.TabIndex = 0;
         // 
         // tsbAddFolder
         // 
         this.tsbAddFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddFolder.Image = global::GRSoft.NapoleonManager.Properties.Resources.create_new_folder;
         this.tsbAddFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddFolder.Name = "tsbAddFolder";
         this.tsbAddFolder.Size = new System.Drawing.Size(36, 36);
         this.tsbAddFolder.Text = "Добавить папку";
         this.tsbAddFolder.Click += new System.EventHandler(this.tsbAddFolder_Click);
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
         this.tsbDel.Text = "Удалить";
         this.tsbDel.Click += new System.EventHandler(this.tsbDel_Click);
         // 
         // toolStripSeparator4
         // 
         this.toolStripSeparator4.Name = "toolStripSeparator4";
         this.toolStripSeparator4.Size = new System.Drawing.Size(6, 39);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 39);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // btnClearFind
         // 
         this.btnClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFind.Name = "btnClearFind";
         this.btnClearFind.Size = new System.Drawing.Size(36, 36);
         this.btnClearFind.Text = "Очистить поиск";
         this.btnClearFind.Click += new System.EventHandler(this.ClearFind);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 39);
         // 
         // btnPicMode
         // 
         this.btnPicMode.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPicMode.Image = global::GRSoft.NapoleonManager.Properties.Resources.htmlView;
         this.btnPicMode.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPicMode.Name = "btnPicMode";
         this.btnPicMode.Size = new System.Drawing.Size(36, 36);
         this.btnPicMode.Text = "Показать неназначенные/полный прайс";
         this.btnPicMode.Click += new System.EventHandler(this.ChangePictureMode);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // cbSizes
         // 
         this.cbSizes.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbSizes.Name = "cbSizes";
         this.cbSizes.Size = new System.Drawing.Size(121, 39);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // tsbOldPrice
         // 
         this.tsbOldPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOldPrice.Image = ((System.Drawing.Image)(resources.GetObject("tsbOldPrice.Image")));
         this.tsbOldPrice.ImageTransparentColor = System.Drawing.Color.White;
         this.tsbOldPrice.Name = "tsbOldPrice";
         this.tsbOldPrice.Size = new System.Drawing.Size(36, 36);
         this.tsbOldPrice.Text = "Прайс с перезентацией товара";
         this.tsbOldPrice.Visible = false;
         this.tsbOldPrice.Click += new System.EventHandler(this.tsbOldPrice_Click);
         // 
         // btnLoadOldPrice
         // 
         this.btnLoadOldPrice.Name = "btnLoadOldPrice";
         this.btnLoadOldPrice.Size = new System.Drawing.Size(23, 36);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 588);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(836, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // menuPic
         // 
         this.menuPic.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuDelPic,
            this.mnuChangePic,
            this.toolStripMenuItem1,
            this.mnuSetting});
         this.menuPic.Name = "menuPic";
         this.menuPic.Size = new System.Drawing.Size(135, 76);
         // 
         // mnuDelPic
         // 
         this.mnuDelPic.Name = "mnuDelPic";
         this.mnuDelPic.Size = new System.Drawing.Size(134, 22);
         this.mnuDelPic.Text = "Удалить";
         this.mnuDelPic.Click += new System.EventHandler(this.mnuDelPic_Click);
         // 
         // mnuChangePic
         // 
         this.mnuChangePic.Name = "mnuChangePic";
         this.mnuChangePic.Size = new System.Drawing.Size(134, 22);
         this.mnuChangePic.Text = "Изменить";
         this.mnuChangePic.Click += new System.EventHandler(this.mnuChangePic_Click);
         // 
         // toolStripMenuItem1
         // 
         this.toolStripMenuItem1.Name = "toolStripMenuItem1";
         this.toolStripMenuItem1.Size = new System.Drawing.Size(131, 6);
         this.toolStripMenuItem1.Visible = false;
         // 
         // mnuSetting
         // 
         this.mnuSetting.Name = "mnuSetting";
         this.mnuSetting.Size = new System.Drawing.Size(134, 22);
         this.mnuSetting.Text = "Настройки";
         this.mnuSetting.Visible = false;
         // 
         // dialog
         // 
         this.dialog.Filter = "Все файлы|*.*|Изображения|*.jpg;*.png";
         this.dialog.FilterIndex = 2;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // menuFolder
         // 
         this.menuFolder.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.msAddFolder,
            this.msAddItem,
            this.msDelFolder});
         this.menuFolder.Name = "menuFolder";
         this.menuFolder.Size = new System.Drawing.Size(162, 70);
         // 
         // msAddFolder
         // 
         this.msAddFolder.Name = "msAddFolder";
         this.msAddFolder.Size = new System.Drawing.Size(161, 22);
         this.msAddFolder.Text = "Добавить папку";
         this.msAddFolder.Click += new System.EventHandler(this.msAddFolder_Click);
         // 
         // msAddItem
         // 
         this.msAddItem.Name = "msAddItem";
         this.msAddItem.Size = new System.Drawing.Size(161, 22);
         this.msAddItem.Text = "Добавить товар";
         this.msAddItem.Click += new System.EventHandler(this.msAddItem_Click);
         // 
         // msDelFolder
         // 
         this.msDelFolder.Name = "msDelFolder";
         this.msDelFolder.Size = new System.Drawing.Size(161, 22);
         this.msDelFolder.Text = "Удалить папку";
         this.msDelFolder.Click += new System.EventHandler(this.msDelFolder_Click);
         // 
         // tgvPrice
         // 
         this.tgvPrice.AllowUserToAddRows = false;
         this.tgvPrice.AllowUserToDeleteRows = false;
         this.tgvPrice.AllowUserToResizeRows = false;
         this.tgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.FPName,
            this.FPCost,
            this.clmnInPack,
            this.FPQty,
            this.clmnPhoto});
         this.tgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnF2;
         this.tgvPrice.ImageList = null;
         this.tgvPrice.Location = new System.Drawing.Point(0, 39);
         this.tgvPrice.MultiSelect = false;
         this.tgvPrice.Name = "tgvPrice";
         this.tgvPrice.RowHeadersVisible = false;
         this.tgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvPrice.Size = new System.Drawing.Size(836, 549);
         this.tgvPrice.TabIndex = 3;
         this.tgvPrice.CellPainting += new System.Windows.Forms.DataGridViewCellPaintingEventHandler(this.tgvPrice_CellPainting);
         this.tgvPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseDown);
         this.tgvPrice.MouseUp += new System.Windows.Forms.MouseEventHandler(this.tgvPrice_MouseUp);
         // 
         // FPName
         // 
         this.FPName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPName.DataPropertyName = "Name";
         this.FPName.DefaultNodeImage = null;
         this.FPName.HeaderText = "Папка/Наименование";
         this.FPName.Name = "FPName";
         this.FPName.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FPCost
         // 
         this.FPCost.DataPropertyName = "Cost";
         this.FPCost.HeaderText = "Цена";
         this.FPCost.Name = "FPCost";
         this.FPCost.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPCost.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.FPCost.Width = 80;
         // 
         // clmnInPack
         // 
         this.clmnInPack.DataPropertyName = "InPack";
         this.clmnInPack.HeaderText = "В упаковке";
         this.clmnInPack.Name = "clmnInPack";
         this.clmnInPack.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.clmnInPack.Width = 80;
         // 
         // FPQty
         // 
         this.FPQty.DataPropertyName = "Qty";
         this.FPQty.HeaderText = "Кол-во";
         this.FPQty.Name = "FPQty";
         this.FPQty.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPQty.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.FPQty.Width = 80;
         // 
         // clmnPhoto
         // 
         this.clmnPhoto.HeaderText = "Фото";
         this.clmnPhoto.Name = "clmnPhoto";
         this.clmnPhoto.Width = 150;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmPricePhotoEx
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(836, 610);
         this.Controls.Add(this.tgvPrice);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPricePhotoEx";
         this.Text = "Прайс-лист";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.menuPic.ResumeLayout(false);
         this.menuFolder.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      protected GRSoft.UILib.TreeGridView tgvPrice;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearFind;
      private System.Windows.Forms.ToolStripButton btnPicMode;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.OpenFileDialog dialog;
      protected System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ContextMenuStrip menuPic;
      private System.Windows.Forms.ToolStripMenuItem mnuDelPic;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripMenuItem mnuChangePic;
      private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
      private System.Windows.Forms.ToolStripMenuItem mnuSetting;
      private System.Windows.Forms.ToolStripComboBox cbSizes;
      private System.Windows.Forms.ToolStripButton tsbOldPrice;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator4;
      private System.Windows.Forms.ToolStripButton tsbAddFolder;
      private System.Windows.Forms.ContextMenuStrip menuFolder;
      private System.Windows.Forms.ToolStripMenuItem msAddFolder;
      private System.Windows.Forms.ToolStripMenuItem msAddItem;
      private System.Windows.Forms.ToolStripMenuItem msDelFolder;
      private UILib.TreeGridColumn FPName;
      private System.Windows.Forms.DataGridViewTextBoxColumn FPCost;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnInPack;
      private System.Windows.Forms.DataGridViewTextBoxColumn FPQty;
      private System.Windows.Forms.DataGridViewImageColumn clmnPhoto;
      private System.Windows.Forms.ToolStripButton btnLoadOldPrice;
      
    
   }
}
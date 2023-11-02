namespace GRSoft.NapoleonManager
{
   partial class FmPrezentList
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPrezentList));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.numRow = new System.Windows.Forms.NumericUpDown();
         this.numCol = new System.Windows.Forms.NumericUpDown();
         this.grid = new System.Windows.Forms.DataGridView();
         this.gridContextMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.itDelete = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnLoad = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAddList = new System.Windows.Forms.ToolStripButton();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.lbList = new System.Windows.Forms.ListBox();
         this.listContextMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.itListDelete = new System.Windows.Forms.ToolStripMenuItem();
         this.btnPrice = new System.Windows.Forms.Button();
         this.label2 = new System.Windows.Forms.Label();
         this.lbPrice = new System.Windows.Forms.ListBox();
         this.priceContextMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.itPriceDelete = new System.Windows.Forms.ToolStripMenuItem();
         this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
         this.imageList = new System.Windows.Forms.ImageList(this.components);
         this.saveXmlDialog = new System.Windows.Forms.SaveFileDialog();
         this.openXmlDialog = new System.Windows.Forms.OpenFileDialog();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.numRow)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.numCol)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.gridContextMenu.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.listContextMenu.SuspendLayout();
         this.priceContextMenu.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.numRow);
         this.splitContainer1.Panel1.Controls.Add(this.numCol);
         this.splitContainer1.Panel1.Controls.Add(this.grid);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(692, 468);
         this.splitContainer1.SplitterDistance = 425;
         this.splitContainer1.TabIndex = 0;
         // 
         // numRow
         // 
         this.numRow.Enabled = false;
         this.numRow.Location = new System.Drawing.Point(250, 3);
         this.numRow.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.numRow.Name = "numRow";
         this.numRow.Size = new System.Drawing.Size(37, 20);
         this.numRow.TabIndex = 3;
         this.numRow.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.numRow.ValueChanged += new System.EventHandler(this.numRow_ValueChanged);
         // 
         // numCol
         // 
         this.numCol.Enabled = false;
         this.numCol.Location = new System.Drawing.Point(163, 3);
         this.numCol.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.numCol.Name = "numCol";
         this.numCol.Size = new System.Drawing.Size(37, 20);
         this.numCol.TabIndex = 2;
         this.numCol.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.numCol.ValueChanged += new System.EventHandler(this.numCol_ValueChanged);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToResizeColumns = false;
         this.grid.AllowUserToResizeRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.ColumnHeadersVisible = false;
         this.grid.ContextMenuStrip = this.gridContextMenu;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 25);
         this.grid.MultiSelect = false;
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(425, 443);
         this.grid.TabIndex = 1;
         this.grid.MouseDown += new System.Windows.Forms.MouseEventHandler(this.grid_MouseDown);
         this.grid.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellDoubleClick);
         this.grid.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellEnter);
         // 
         // gridContextMenu
         // 
         this.gridContextMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.itDelete});
         this.gridContextMenu.Name = "gridContextMenu";
         this.gridContextMenu.Size = new System.Drawing.Size(119, 26);
         // 
         // itDelete
         // 
         this.itDelete.Name = "itDelete";
         this.itDelete.Size = new System.Drawing.Size(118, 22);
         this.itDelete.Text = "Удалить";
         this.itDelete.Click += new System.EventHandler(this.itDelete_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnLoad,
            this.btnSave,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnDel,
            this.toolStripSeparator2,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.toolStripSeparator3,
            this.btnAddList});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(425, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnLoad
         // 
         this.btnLoad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnLoad.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnLoad.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnLoad.Name = "btnLoad";
         this.btnLoad.Size = new System.Drawing.Size(23, 22);
         this.btnLoad.Text = "Загрузить";
         this.btnLoad.Click += new System.EventHandler(this.btnLoad_Click);
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
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
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(54, 22);
         this.toolStripLabel1.Text = "колонки";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(45, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(45, 22);
         this.toolStripLabel2.Text = "строки";
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Margin = new System.Windows.Forms.Padding(40, 0, 0, 0);
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // btnAddList
         // 
         this.btnAddList.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddList.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.btnAddList.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddList.Name = "btnAddList";
         this.btnAddList.Size = new System.Drawing.Size(23, 22);
         this.btnAddList.Text = "Добавить лист";
         this.btnAddList.Click += new System.EventHandler(this.btnAddList_Click);
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.lbList);
         this.splitContainer2.Panel1.Margin = new System.Windows.Forms.Padding(0, 5, 5, 0);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.btnPrice);
         this.splitContainer2.Panel2.Controls.Add(this.label2);
         this.splitContainer2.Panel2.Controls.Add(this.lbPrice);
         this.splitContainer2.Size = new System.Drawing.Size(263, 468);
         this.splitContainer2.SplitterDistance = 165;
         this.splitContainer2.TabIndex = 0;
         // 
         // lbList
         // 
         this.lbList.ContextMenuStrip = this.listContextMenu;
         this.lbList.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbList.FormattingEnabled = true;
         this.lbList.ItemHeight = 14;
         this.lbList.Location = new System.Drawing.Point(0, 0);
         this.lbList.Name = "lbList";
         this.lbList.Size = new System.Drawing.Size(263, 158);
         this.lbList.TabIndex = 0;
         this.lbList.SelectedIndexChanged += new System.EventHandler(this.lbList_SelectedIndexChanged);
         this.lbList.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lbList_MouseDown);
         // 
         // listContextMenu
         // 
         this.listContextMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.itListDelete});
         this.listContextMenu.Name = "listContextMenu";
         this.listContextMenu.Size = new System.Drawing.Size(119, 26);
         this.listContextMenu.Opening += new System.ComponentModel.CancelEventHandler(this.listContextMenu_Opening);
         // 
         // itListDelete
         // 
         this.itListDelete.Name = "itListDelete";
         this.itListDelete.Size = new System.Drawing.Size(118, 22);
         this.itListDelete.Text = "Удалить";
         this.itListDelete.Click += new System.EventHandler(this.itListDelete_Click);
         // 
         // btnPrice
         // 
         this.btnPrice.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnPrice.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnPrice.Location = new System.Drawing.Point(216, 6);
         this.btnPrice.Name = "btnPrice";
         this.btnPrice.Size = new System.Drawing.Size(44, 28);
         this.btnPrice.TabIndex = 2;
         this.btnPrice.UseVisualStyleBackColor = true;
         this.btnPrice.Click += new System.EventHandler(this.btnPrice_Click);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(3, 13);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(37, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Товар";
         // 
         // lbPrice
         // 
         this.lbPrice.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.lbPrice.ContextMenuStrip = this.priceContextMenu;
         this.lbPrice.FormattingEnabled = true;
         this.lbPrice.ItemHeight = 14;
         this.lbPrice.Location = new System.Drawing.Point(0, 40);
         this.lbPrice.Name = "lbPrice";
         this.lbPrice.Size = new System.Drawing.Size(260, 256);
         this.lbPrice.TabIndex = 0;
         this.lbPrice.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lbPrice_MouseDown);
         // 
         // priceContextMenu
         // 
         this.priceContextMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.itPriceDelete});
         this.priceContextMenu.Name = "priceContextMenu";
         this.priceContextMenu.Size = new System.Drawing.Size(119, 26);
         // 
         // itPriceDelete
         // 
         this.itPriceDelete.Name = "itPriceDelete";
         this.itPriceDelete.Size = new System.Drawing.Size(118, 22);
         this.itPriceDelete.Text = "Удалить";
         this.itPriceDelete.Click += new System.EventHandler(this.itPriceDelete_Click);
         // 
         // imageList
         // 
         this.imageList.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList.ImageSize = new System.Drawing.Size(16, 16);
         this.imageList.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // saveXmlDialog
         // 
         this.saveXmlDialog.DefaultExt = "xml";
         this.saveXmlDialog.Filter = "xml|*.xml";
         // 
         // openXmlDialog
         // 
         this.openXmlDialog.Filter = "xml|*.xml";
         // 
         // FmPrezentList
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(692, 468);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPrezentList";
         this.Text = "Редактор презентаций";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPrezentList_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.numRow)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.numCol)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.gridContextMenu.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.listContextMenu.ResumeLayout(false);
         this.priceContextMenu.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.OpenFileDialog openFileDialog;
      private System.Windows.Forms.ImageList imageList;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.NumericUpDown numCol;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.NumericUpDown numRow;
      private System.Windows.Forms.ToolStripButton btnLoad;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ContextMenuStrip gridContextMenu;
      private System.Windows.Forms.ToolStripMenuItem itDelete;
      private System.Windows.Forms.SaveFileDialog saveXmlDialog;
      private System.Windows.Forms.OpenFileDialog openXmlDialog;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton btnAddList;
      private System.Windows.Forms.ContextMenuStrip listContextMenu;
      private System.Windows.Forms.ToolStripMenuItem itListDelete;
      private System.Windows.Forms.ContextMenuStrip priceContextMenu;
      private System.Windows.Forms.ToolStripMenuItem itPriceDelete;
      private System.Windows.Forms.ListBox lbList;
      private System.Windows.Forms.Button btnPrice;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ListBox lbPrice;
   }
}
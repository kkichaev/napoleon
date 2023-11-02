using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class OrgMatrixEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OrgMatrixEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.matrixTree = new System.Windows.Forms.TreeView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tscbMatrixMode = new System.Windows.Forms.ToolStripComboBox();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearFind = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbFindPrice = new System.Windows.Forms.ToolStripTextBox();
         this.tbCLearFindPrice = new System.Windows.Forms.ToolStripButton();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.tsmiRemoveMatrix = new System.Windows.Forms.ToolStripMenuItem();
         this.tsmiRemoveItem = new System.Windows.Forms.ToolStripMenuItem();
         this.priceTree = new GRSoft.UILib.TreeViewMS();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.contextMenuStrip1.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.matrixTree);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.priceTree);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(783, 581);
         this.splitContainer1.SplitterDistance = 432;
         this.splitContainer1.TabIndex = 0;
         // 
         // matrixTree
         // 
         this.matrixTree.AllowDrop = true;
         this.matrixTree.ContextMenuStrip = this.contextMenuStrip1;
         this.matrixTree.Dock = System.Windows.Forms.DockStyle.Fill;
         this.matrixTree.HideSelection = false;
         this.matrixTree.ImageIndex = 0;
         this.matrixTree.ImageList = this.imageList1;
         this.matrixTree.Location = new System.Drawing.Point(0, 25);
         this.matrixTree.Name = "matrixTree";
         this.matrixTree.SelectedImageIndex = 2;
         this.matrixTree.Size = new System.Drawing.Size(432, 556);
         this.matrixTree.TabIndex = 1;
         this.matrixTree.DragDrop += new System.Windows.Forms.DragEventHandler(this.matrixTree_DragDrop);
         this.matrixTree.MouseDown += new System.Windows.Forms.MouseEventHandler(this.matrixTree_MouseDown);
         this.matrixTree.DragEnter += new System.Windows.Forms.DragEventHandler(this.matrixTree_DragEnter);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         this.imageList1.Images.SetKeyName(2, "go-next-4.png");
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tscbMatrixMode,
            this.tsbRefresh,
            this.tsbSave,
            this.tbFind,
            this.btnClearFind});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(432, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tscbMatrixMode
         // 
         this.tscbMatrixMode.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.FileSystem;
         this.tscbMatrixMode.Items.AddRange(new object[] {
            "Категории",
            "Точки"});
         this.tscbMatrixMode.Name = "tscbMatrixMode";
         this.tscbMatrixMode.Size = new System.Drawing.Size(121, 25);
         this.tscbMatrixMode.SelectedIndexChanged += new System.EventHandler(this.tscbMatrixMode_SelectedIndexChanged);
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "tsbRefresh";
         this.tsbRefresh.ToolTipText = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
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
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         // 
         // btnClearFind
         // 
         this.btnClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearFind.Name = "btnClearFind";
         this.btnClearFind.Size = new System.Drawing.Size(23, 22);
         this.btnClearFind.Text = "Очистить поиск";
         this.btnClearFind.Click += new System.EventHandler(this.btnClearFind_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbFindPrice,
            this.tbCLearFindPrice});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(347, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbFindPrice
         // 
         this.tbFindPrice.Name = "tbFindPrice";
         this.tbFindPrice.Size = new System.Drawing.Size(100, 25);
         // 
         // tbCLearFindPrice
         // 
         this.tbCLearFindPrice.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbCLearFindPrice.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tbCLearFindPrice.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbCLearFindPrice.Name = "tbCLearFindPrice";
         this.tbCLearFindPrice.Size = new System.Drawing.Size(23, 22);
         this.tbCLearFindPrice.Text = "Очистить поиск";
         this.tbCLearFindPrice.Click += new System.EventHandler(this.tbClearFindPrice_Click);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmiRemoveMatrix,
            this.tsmiRemoveItem});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(204, 70);
         // 
         // tsmiRemoveMatrix
         // 
         this.tsmiRemoveMatrix.Name = "tsmiRemoveMatrix";
         this.tsmiRemoveMatrix.Size = new System.Drawing.Size(203, 22);
         this.tsmiRemoveMatrix.Text = "Удалить матрицу точки";
         this.tsmiRemoveMatrix.Click += new System.EventHandler(this.tsmiRemoveMatrix_Click);
         // 
         // tsmiRemoveItem
         // 
         this.tsmiRemoveItem.Name = "tsmiRemoveItem";
         this.tsmiRemoveItem.Size = new System.Drawing.Size(203, 22);
         this.tsmiRemoveItem.Text = "Удалить товар";
         this.tsmiRemoveItem.Click += new System.EventHandler(this.tsmiRemoveItem_Click);
         // 
         // priceTree
         // 
         this.priceTree.Dock = System.Windows.Forms.DockStyle.Fill;
         this.priceTree.ImageIndex = 0;
         this.priceTree.ImageList = this.imageList1;
         this.priceTree.Location = new System.Drawing.Point(0, 25);
         this.priceTree.Name = "priceTree";
         this.priceTree.SelectedImageIndex = 0;
         this.priceTree.SelectedNodes = ((System.Collections.Generic.List<System.Windows.Forms.TreeNode>)(resources.GetObject("priceTree.SelectedNodes")));
         this.priceTree.Size = new System.Drawing.Size(347, 556);
         this.priceTree.TabIndex = 1;
         this.priceTree.NodeMouseDoubleClick += new System.Windows.Forms.TreeNodeMouseClickEventHandler(this.priceTree_NodeMouseDoubleClick);
         this.priceTree.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.priceTree_ItemDrag);
         // 
         // OrgMatrixEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(783, 581);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OrgMatrixEditor";
         this.Text = "Редактор матриц ТТ";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.contextMenuStrip1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.TreeView matrixTree;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private TreeViewMS priceTree;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox tscbMatrixMode;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearFind;
      private System.Windows.Forms.ToolStripTextBox tbFindPrice;
      private System.Windows.Forms.ToolStripButton tbCLearFindPrice;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem tsmiRemoveMatrix;
      private System.Windows.Forms.ToolStripMenuItem tsmiRemoveItem;
   }
}
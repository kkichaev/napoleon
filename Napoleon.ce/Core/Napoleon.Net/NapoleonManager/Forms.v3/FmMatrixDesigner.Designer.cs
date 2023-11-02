using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class FmMatrixDesigner
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMatrixDesigner));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbMatrixName = new System.Windows.Forms.ToolStripTextBox();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbRename = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.tsbUp = new System.Windows.Forms.ToolStripButton();
         this.tsbDown = new System.Windows.Forms.ToolStripButton();
         this.tsbSort = new System.Windows.Forms.ToolStripButton();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tstbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbFind = new System.Windows.Forms.ToolStripButton();
         this.tsbFindBack = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvMatrix = new System.Windows.Forms.TreeView();
         this.tvPrice = new GRSoft.UILib.TreeViewMS();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.dgvPrice = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbMatrixName,
            this.tsbAdd,
            this.tsbRename,
            this.tsbRemove,
            this.tsbUp,
            this.tsbDown,
            this.tsbSort,
            this.tsbRefresh,
            this.tsbSave,
            this.toolStripSeparator1,
            this.tstbFind,
            this.tsbFind,
            this.tsbFindBack,
            this.toolStripSeparator2});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(736, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "Добавить";
         // 
         // tbMatrixName
         // 
         this.tbMatrixName.Name = "tbMatrixName";
         this.tbMatrixName.Size = new System.Drawing.Size(170, 25);
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = ((System.Drawing.Image)(resources.GetObject("tsbAdd.Image")));
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(23, 22);
         this.tsbAdd.Text = "Добавить матрицу";
         // 
         // tsbRename
         // 
         this.tsbRename.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRename.Image = ((System.Drawing.Image)(resources.GetObject("tsbRename.Image")));
         this.tsbRename.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRename.Name = "tsbRename";
         this.tsbRename.Size = new System.Drawing.Size(23, 22);
         this.tsbRename.Text = "Переименовать";
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = ((System.Drawing.Image)(resources.GetObject("tsbRemove.Image")));
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(23, 22);
         this.tsbRemove.Text = "Удалить";
         // 
         // tsbUp
         // 
         this.tsbUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUp.Image = ((System.Drawing.Image)(resources.GetObject("tsbUp.Image")));
         this.tsbUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUp.Name = "tsbUp";
         this.tsbUp.Size = new System.Drawing.Size(23, 22);
         this.tsbUp.Text = "Вверх";
         // 
         // tsbDown
         // 
         this.tsbDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDown.Image = ((System.Drawing.Image)(resources.GetObject("tsbDown.Image")));
         this.tsbDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDown.Name = "tsbDown";
         this.tsbDown.Size = new System.Drawing.Size(23, 22);
         this.tsbDown.Text = "Вниз";
         // 
         // tsbSort
         // 
         this.tsbSort.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSort.Image = ((System.Drawing.Image)(resources.GetObject("tsbSort.Image")));
         this.tsbSort.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSort.Name = "tsbSort";
         this.tsbSort.Size = new System.Drawing.Size(23, 22);
         this.tsbSort.Text = "Сортировка матрицы";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = ((System.Drawing.Image)(resources.GetObject("tsbRefresh.Image")));
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "Обновить";
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Image = ((System.Drawing.Image)(resources.GetObject("tsbSave.Image")));
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(0, 0, 4, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tstbFind
         // 
         this.tstbFind.Name = "tstbFind";
         this.tstbFind.Size = new System.Drawing.Size(150, 25);
         // 
         // tsbFind
         // 
         this.tsbFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbFind.Image = ((System.Drawing.Image)(resources.GetObject("tsbFind.Image")));
         this.tsbFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbFind.Name = "tsbFind";
         this.tsbFind.Size = new System.Drawing.Size(23, 22);
         this.tsbFind.Text = "Искать вперед";
         // 
         // tsbFindBack
         // 
         this.tsbFindBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbFindBack.Image = ((System.Drawing.Image)(resources.GetObject("tsbFindBack.Image")));
         this.tsbFindBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbFindBack.Name = "tsbFindBack";
         this.tsbFindBack.Size = new System.Drawing.Size(23, 22);
         this.tsbFindBack.Text = "Искать назад";
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.splitContainer1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(736, 519);
         this.panel1.TabIndex = 1;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(7, 8);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvMatrix);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tvPrice);
         this.splitContainer1.Panel2.Controls.Add(this.dgvPrice);
         this.splitContainer1.Size = new System.Drawing.Size(722, 503);
         this.splitContainer1.SplitterDistance = 343;
         this.splitContainer1.SplitterWidth = 7;
         this.splitContainer1.TabIndex = 0;
         // 
         // tvMatrix
         // 
         this.tvMatrix.AllowDrop = true;
         this.tvMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvMatrix.HideSelection = false;
         this.tvMatrix.Location = new System.Drawing.Point(0, 0);
         this.tvMatrix.Name = "tvMatrix";
         this.tvMatrix.Size = new System.Drawing.Size(343, 503);
         this.tvMatrix.TabIndex = 0;
         // 
         // tvPrice
         // 
         this.tvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvPrice.ImageIndex = 0;
         this.tvPrice.ImageList = this.imageList1;
         this.tvPrice.Location = new System.Drawing.Point(0, 0);
         this.tvPrice.Name = "tvPrice";
         this.tvPrice.SelectedImageIndex = 0;
         this.tvPrice.SelectedNodes = ((System.Collections.Generic.List<System.Windows.Forms.TreeNode>)(resources.GetObject("tvPrice.SelectedNodes")));
         this.tvPrice.Size = new System.Drawing.Size(372, 503);
         this.tvPrice.TabIndex = 1;
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         // 
         // dgvPrice
         // 
         this.dgvPrice.AllowUserToAddRows = false;
         this.dgvPrice.AllowUserToDeleteRows = false;
         this.dgvPrice.AllowUserToResizeColumns = false;
         this.dgvPrice.AllowUserToResizeRows = false;
         this.dgvPrice.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.dgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPrice.Location = new System.Drawing.Point(0, 0);
         this.dgvPrice.MultiSelect = false;
         this.dgvPrice.Name = "dgvPrice";
         this.dgvPrice.RowHeadersVisible = false;
         this.dgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvPrice.Size = new System.Drawing.Size(372, 503);
         this.dgvPrice.TabIndex = 0;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.HeaderText = "";
         this.Column1.Name = "Column1";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmMatrixDesigner
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(736, 544);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMatrixDesigner";
         this.Text = "Редактор матриц";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      protected System.Windows.Forms.TreeView tvMatrix;
      private System.Windows.Forms.DataGridView dgvPrice;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbRename;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.ToolStripButton tsbUp;
      private System.Windows.Forms.ToolStripButton tsbDown;
      protected System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      protected System.Windows.Forms.ToolStripButton tsbFind;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      protected System.Windows.Forms.ToolStripButton tsbFindBack;
      protected TreeViewMS tvPrice;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ToolStripButton tsbSort;
      protected System.Windows.Forms.ToolStripTextBox tbMatrixName;
      public System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      public System.Windows.Forms.ToolStripButton tsbSave;
      public System.Windows.Forms.ToolStripTextBox tstbFind;
   }
}
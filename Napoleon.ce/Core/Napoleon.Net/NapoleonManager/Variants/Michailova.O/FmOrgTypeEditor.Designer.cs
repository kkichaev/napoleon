using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class FmOrgTypeEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgTypeEditor));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbName = new System.Windows.Forms.ToolStripTextBox();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnRename = new System.Windows.Forms.ToolStripButton();
         this.btnRemove = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tstbFind = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnOrgTypeMatrix = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tree = new System.Windows.Forms.TreeView();
         this.grid = new System.Windows.Forms.DataGridView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbName,
            this.btnAdd,
            this.btnRename,
            this.btnRemove,
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.tstbFind,
            this.toolStripSeparator2,
            this.btnOrgTypeMatrix});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(736, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "Добавить";
         // 
         // tbName
         // 
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(170, 25);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить матрицу";
         this.btnAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // btnRename
         // 
         this.btnRename.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRename.Image = ((System.Drawing.Image)(resources.GetObject("btnRename.Image")));
         this.btnRename.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRename.Name = "btnRename";
         this.btnRename.Size = new System.Drawing.Size(23, 22);
         this.btnRename.Text = "Переименовать";
         this.btnRename.Click += new System.EventHandler(this.tsbRename_Click);
         // 
         // btnRemove
         // 
         this.btnRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRemove.Image = ((System.Drawing.Image)(resources.GetObject("btnRemove.Image")));
         this.btnRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRemove.Name = "btnRemove";
         this.btnRemove.Size = new System.Drawing.Size(23, 22);
         this.btnRemove.Text = "Удалить";
         this.btnRemove.Click += new System.EventHandler(this.tsbRemove_Click);
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
         this.tstbFind.TextChanged += new System.EventHandler(this.tstbFind_TextChanged);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnOrgTypeMatrix
         // 
         this.btnOrgTypeMatrix.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.btnOrgTypeMatrix.Image = ((System.Drawing.Image)(resources.GetObject("btnOrgTypeMatrix.Image")));
         this.btnOrgTypeMatrix.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrgTypeMatrix.Name = "btnOrgTypeMatrix";
         this.btnOrgTypeMatrix.Size = new System.Drawing.Size(107, 22);
         this.btnOrgTypeMatrix.Text = "Привязка матриц";
         this.btnOrgTypeMatrix.TextImageRelation = System.Windows.Forms.TextImageRelation.TextAboveImage;
         this.btnOrgTypeMatrix.Click += new System.EventHandler(this.btnOrgTypeMatrix_Click);
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
         this.splitContainer1.Panel1.Controls.Add(this.tree);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.grid);
         this.splitContainer1.Size = new System.Drawing.Size(722, 503);
         this.splitContainer1.SplitterDistance = 343;
         this.splitContainer1.SplitterWidth = 7;
         this.splitContainer1.TabIndex = 0;
         // 
         // tree
         // 
         this.tree.AllowDrop = true;
         this.tree.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tree.HideSelection = false;
         this.tree.Location = new System.Drawing.Point(0, 0);
         this.tree.Name = "tree";
         this.tree.Size = new System.Drawing.Size(343, 503);
         this.tree.TabIndex = 0;
         this.tree.DragDrop += new System.Windows.Forms.DragEventHandler(this.tree_DragDrop);
         this.tree.DragEnter += new System.Windows.Forms.DragEventHandler(this.tree_DragEnter);
         this.tree.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tree_MouseDown);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.AllowUserToResizeColumns = false;
         this.grid.AllowUserToResizeRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.MultiSelect = false;
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.grid.Size = new System.Drawing.Size(372, 503);
         this.grid.TabIndex = 0;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         this.grid.MouseDown += new System.Windows.Forms.MouseEventHandler(this.grid_MouseDown);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Организация";
         this.Column1.Name = "Column1";
         // 
         // FmOrgTypeEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(736, 544);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgTypeEditor";
         this.Text = "Редактор типов торговых точек";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrgTypeEditor_FormClosing);
         this.Load += new System.EventHandler(this.FmMatrixDesigner_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      protected System.Windows.Forms.TreeView tree;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnRename;
      private System.Windows.Forms.ToolStripButton btnRemove;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStripTextBox tstbFind;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ImageList imageList1;
      protected System.Windows.Forms.ToolStripTextBox tbName;
      public System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      public System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.ToolStripButton btnOrgTypeMatrix;
   }
}
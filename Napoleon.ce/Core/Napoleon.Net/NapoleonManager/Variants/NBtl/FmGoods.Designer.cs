namespace GRSoft.NapoleonManager
{
   partial class FmGoods
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGoods));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnAddGoods = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnMatrix = new System.Windows.Forms.ToolStripButton();
         this.tsbGoodsValues = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator4 = new System.Windows.Forms.ToolStripSeparator();
         this.btnLoad = new System.Windows.Forms.ToolStripButton();
         this.tree = new System.Windows.Forms.TreeView();
         this.menu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miSetMy = new System.Windows.Forms.ToolStripMenuItem();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1.SuspendLayout();
         this.menu.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnAddGoods,
            this.toolStripSeparator3,
            this.btnEdit,
            this.btnDel,
            this.toolStripSeparator2,
            this.btnMatrix,
            this.tsbGoodsValues,
            this.toolStripSeparator4,
            this.btnLoad});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(829, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
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
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.create_new_folder;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "Добавить группу";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnAddGoods
         // 
         this.btnAddGoods.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddGoods.Image = global::GRSoft.NapoleonManager.Properties.Resources.pnt_doc;
         this.btnAddGoods.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddGoods.Name = "btnAddGoods";
         this.btnAddGoods.Size = new System.Drawing.Size(36, 36);
         this.btnAddGoods.Text = "Добавить товар";
         this.btnAddGoods.Click += new System.EventHandler(this.btnAddGoods_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 39);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(36, 36);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // btnMatrix
         // 
         this.btnMatrix.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnMatrix.Image = global::GRSoft.NapoleonManager.Properties.Resources.kmenuedit;
         this.btnMatrix.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnMatrix.Name = "btnMatrix";
         this.btnMatrix.Size = new System.Drawing.Size(36, 36);
         this.btnMatrix.Text = "Матрицы";
         this.btnMatrix.Click += new System.EventHandler(this.btnMatrix_Click);
         // 
         // tsbGoodsValues
         // 
         this.tsbGoodsValues.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbGoodsValues.Image = global::GRSoft.NapoleonManager.Properties.Resources.plan_editor;
         this.tsbGoodsValues.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbGoodsValues.Name = "tsbGoodsValues";
         this.tsbGoodsValues.Size = new System.Drawing.Size(36, 36);
         this.tsbGoodsValues.Text = "Пороги наличия";
         this.tsbGoodsValues.Click += new System.EventHandler(this.tsbGoodsValues_Click);
         // 
         // toolStripSeparator4
         // 
         this.toolStripSeparator4.Name = "toolStripSeparator4";
         this.toolStripSeparator4.Size = new System.Drawing.Size(6, 39);
         // 
         // btnLoad
         // 
         this.btnLoad.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnLoad.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
         this.btnLoad.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnLoad.Name = "btnLoad";
         this.btnLoad.Size = new System.Drawing.Size(36, 36);
         this.btnLoad.Text = "Загрузить из Excel";
         this.btnLoad.Click += new System.EventHandler(this.btnLoad_Click);
         // 
         // tree
         // 
         this.tree.ContextMenuStrip = this.menu;
         this.tree.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tree.DrawMode = System.Windows.Forms.TreeViewDrawMode.OwnerDrawText;
         this.tree.HideSelection = false;
         this.tree.ImageIndex = 0;
         this.tree.ImageList = this.imageList1;
         this.tree.Location = new System.Drawing.Point(0, 39);
         this.tree.Name = "tree";
         this.tree.SelectedImageIndex = 0;
         this.tree.Size = new System.Drawing.Size(829, 512);
         this.tree.TabIndex = 1;
         this.tree.DrawNode += new System.Windows.Forms.DrawTreeNodeEventHandler(this.tree_DrawNode);
         this.tree.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tree_MouseDown);
         // 
         // menu
         // 
         this.menu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miSetMy});
         this.menu.Name = "menu";
         this.menu.Size = new System.Drawing.Size(135, 26);
         // 
         // miSetMy
         // 
         this.miSetMy.Name = "miSetMy";
         this.miSetMy.Size = new System.Drawing.Size(134, 22);
         this.miSetMy.Text = "Наш товар";
         this.miSetMy.Click += new System.EventHandler(this.miSetMy_Click);
         // 
         // imageList1
         // 
         this.imageList1.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList1.ImageSize = new System.Drawing.Size(16, 16);
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // FmGoods
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(829, 551);
         this.Controls.Add(this.tree);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGoods";
         this.Text = "Редактор товара";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.menu.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnAddGoods;
      private System.Windows.Forms.TreeView tree;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ImageList imageList1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton btnMatrix;
      private System.Windows.Forms.ToolStripButton tsbGoodsValues;
      private System.Windows.Forms.ContextMenuStrip menu;
      private System.Windows.Forms.ToolStripMenuItem miSetMy;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator4;
      private System.Windows.Forms.ToolStripButton btnLoad;
   }
}
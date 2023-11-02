namespace GRSoft.NapoleonManager
{
   partial class FmContractEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmContractEdit));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.tree = new System.Windows.Forms.TreeView();
         this.menu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miSetMy = new System.Windows.Forms.ToolStripMenuItem();
         this.picture = new System.Windows.Forms.PictureBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddImg = new System.Windows.Forms.ToolStripButton();
         this.btnDelImg = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.cbSizes = new System.Windows.Forms.ToolStripComboBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddGr = new System.Windows.Forms.ToolStripButton();
         this.btnEditGr = new System.Windows.Forms.ToolStripButton();
         this.btnDelGr = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnItems = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.menu.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.picture)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.btnCancel);
         this.splitContainer1.Panel1.Controls.Add(this.btnOK);
         this.splitContainer1.Panel1.Controls.Add(this.tbName);
         this.splitContainer1.Panel1.Controls.Add(this.label1);
         this.splitContainer1.Panel1.Controls.Add(this.dpv);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(721, 468);
         this.splitContainer1.SplitterDistance = 68;
         this.splitContainer1.TabIndex = 0;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(634, 32);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 4;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(553, 32);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 3;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(96, 6);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(613, 20);
         this.tbName.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(8, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Наименование";
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 3, 18, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(8, 32);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2015, 3, 18, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 25);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.tree);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.picture);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Size = new System.Drawing.Size(721, 371);
         this.splitContainer2.SplitterDistance = 435;
         this.splitContainer2.TabIndex = 4;
         // 
         // tree
         // 
         this.tree.ContextMenuStrip = this.menu;
         this.tree.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tree.DrawMode = System.Windows.Forms.TreeViewDrawMode.OwnerDrawText;
         this.tree.HideSelection = false;
         this.tree.Location = new System.Drawing.Point(0, 0);
         this.tree.Name = "tree";
         this.tree.Size = new System.Drawing.Size(435, 371);
         this.tree.TabIndex = 2;
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
         // picture
         // 
         this.picture.Dock = System.Windows.Forms.DockStyle.Fill;
         this.picture.Location = new System.Drawing.Point(0, 25);
         this.picture.Name = "picture";
         this.picture.Size = new System.Drawing.Size(282, 346);
         this.picture.TabIndex = 1;
         this.picture.TabStop = false;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddImg,
            this.btnDelImg,
            this.toolStripSeparator3,
            this.cbSizes});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(282, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddImg
         // 
         this.btnAddImg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddImg.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddImg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddImg.Name = "btnAddImg";
         this.btnAddImg.Size = new System.Drawing.Size(23, 22);
         this.btnAddImg.Text = "Добавить фото";
         this.btnAddImg.Click += new System.EventHandler(this.btnAddImg_Click);
         // 
         // btnDelImg
         // 
         this.btnDelImg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelImg.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelImg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelImg.Name = "btnDelImg";
         this.btnDelImg.Size = new System.Drawing.Size(23, 22);
         this.btnDelImg.Text = "Удалить фото";
         this.btnDelImg.Click += new System.EventHandler(this.btnDelImg_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // cbSizes
         // 
         this.cbSizes.Name = "cbSizes";
         this.cbSizes.Size = new System.Drawing.Size(121, 25);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddGr,
            this.btnEditGr,
            this.btnDelGr,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.toolStripSeparator2,
            this.btnItems});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(721, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddGr
         // 
         this.btnAddGr.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddGr.Image = global::GRSoft.NapoleonManager.Properties.Resources.group_add;
         this.btnAddGr.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddGr.Name = "btnAddGr";
         this.btnAddGr.Size = new System.Drawing.Size(23, 22);
         this.btnAddGr.Text = "Добавить группу";
         this.btnAddGr.Click += new System.EventHandler(this.btnAddGr_Click);
         // 
         // btnEditGr
         // 
         this.btnEditGr.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditGr.Image = global::GRSoft.NapoleonManager.Properties.Resources.group_edit;
         this.btnEditGr.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditGr.Name = "btnEditGr";
         this.btnEditGr.Size = new System.Drawing.Size(23, 22);
         this.btnEditGr.Text = "Изменить группу";
         this.btnEditGr.Click += new System.EventHandler(this.btnEditGr_Click);
         // 
         // btnDelGr
         // 
         this.btnDelGr.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelGr.Image = global::GRSoft.NapoleonManager.Properties.Resources.group_del;
         this.btnDelGr.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelGr.Name = "btnDelGr";
         this.btnDelGr.Size = new System.Drawing.Size(23, 22);
         this.btnDelGr.Text = "Удалить группу";
         this.btnDelGr.Click += new System.EventHandler(this.btnDelGr_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.pnt_doc;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить товар";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить товар";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.pnt_undoc;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить товар";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnItems
         // 
         this.btnItems.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnItems.Image = global::GRSoft.NapoleonManager.Properties.Resources.audit_doc;
         this.btnItems.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnItems.Name = "btnItems";
         this.btnItems.Size = new System.Drawing.Size(23, 22);
         this.btnItems.Text = "Планограммы по магазинам";
         this.btnItems.Click += new System.EventHandler(this.btnItems_Click);
         // 
         // FmContractEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(721, 468);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmContractEdit";
         this.Text = "Карточка контракта";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmContractEdit_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.menu.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.picture)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label1;
      private DatePeriodView dpv;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnAddGr;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.TreeView tree;
      private System.Windows.Forms.ToolStripButton btnEditGr;
      private System.Windows.Forms.ToolStripButton btnDelGr;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.PictureBox picture;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnAddImg;
      private System.Windows.Forms.ToolStripButton btnDelImg;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripComboBox cbSizes;
      private System.Windows.Forms.ContextMenuStrip menu;
      private System.Windows.Forms.ToolStripMenuItem miSetMy;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnItems;

   }
}
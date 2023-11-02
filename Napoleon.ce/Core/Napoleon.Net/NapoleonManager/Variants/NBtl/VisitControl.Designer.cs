using System.Windows.Forms;
namespace GRSoft.NapoleonManager
{
   partial class VisitControl : UserControl, DocControl
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         this.tbRemark = new System.Windows.Forms.TextBox();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.listView1 = new System.Windows.Forms.ListView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.listView2 = new System.Windows.Forms.ListView();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tbRemark
         // 
         this.tbRemark.Dock = System.Windows.Forms.DockStyle.Top;
         this.tbRemark.Location = new System.Drawing.Point(0, 0);
         this.tbRemark.Margin = new System.Windows.Forms.Padding(4);
         this.tbRemark.Name = "tbRemark";
         this.tbRemark.Size = new System.Drawing.Size(583, 22);
         this.tbRemark.TabIndex = 0;
         // 
         // imageList1
         // 
         this.imageList1.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList1.ImageSize = new System.Drawing.Size(96, 96);
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 22);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.listView1);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.listView2);
         this.splitContainer1.Size = new System.Drawing.Size(583, 493);
         this.splitContainer1.SplitterDistance = 255;
         this.splitContainer1.TabIndex = 1;
         // 
         // listView1
         // 
         this.listView1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listView1.HideSelection = false;
         this.listView1.LargeImageList = this.imageList1;
         this.listView1.Location = new System.Drawing.Point(0, 48);
         this.listView1.Name = "listView1";
         this.listView1.Size = new System.Drawing.Size(583, 207);
         this.listView1.TabIndex = 9;
         this.listView1.UseCompatibleStateImageBehavior = false;
         this.listView1.DragDrop += new System.Windows.Forms.DragEventHandler(this.listView1_DragDrop);
         this.listView1.DragEnter += new System.Windows.Forms.DragEventHandler(this.listView1_DragEnter);
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(583, 48);
         this.toolStrip1.TabIndex = 8;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 45);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAddPhoto_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 45);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // listView2
         // 
         this.listView2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listView2.HideSelection = false;
         this.listView2.LargeImageList = this.imageList1;
         this.listView2.Location = new System.Drawing.Point(0, 0);
         this.listView2.Name = "listView2";
         this.listView2.Size = new System.Drawing.Size(583, 234);
         this.listView2.TabIndex = 10;
         this.listView2.UseCompatibleStateImageBehavior = false;
         this.listView2.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.listView2_ItemDrag);
         // 
         // VisitControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.tbRemark);
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "VisitControl";
         this.Size = new System.Drawing.Size(583, 515);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox tbRemark;
      private ImageList imageList1;
      private SplitContainer splitContainer1;
      private ListView listView1;
      private ToolStrip toolStrip1;
      private ToolStripButton btnAdd;
      private ToolStripButton btnDel;
      private ListView listView2;
   }
}

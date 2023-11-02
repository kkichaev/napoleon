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
         this.tbRemark = new System.Windows.Forms.TextBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.checkedListBox1 = new System.Windows.Forms.CheckedListBox();
         this.listBox1 = new System.Windows.Forms.ListBox();
         this.webBrowser1 = new System.Windows.Forms.WebBrowser();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer2)).BeginInit();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tbRemark
         // 
         this.tbRemark.Dock = System.Windows.Forms.DockStyle.Top;
         this.tbRemark.Location = new System.Drawing.Point(0, 0);
         this.tbRemark.Name = "tbRemark";
         this.tbRemark.Size = new System.Drawing.Size(1080, 20);
         this.tbRemark.TabIndex = 0;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 20);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.webBrowser1);
         this.splitContainer1.Size = new System.Drawing.Size(1080, 618);
         this.splitContainer1.SplitterDistance = 360;
         this.splitContainer1.TabIndex = 1;
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
         this.splitContainer2.Panel1.Controls.Add(this.checkedListBox1);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.listBox1);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer2.Size = new System.Drawing.Size(360, 618);
         this.splitContainer2.SplitterDistance = 309;
         this.splitContainer2.TabIndex = 1;
         // 
         // checkedListBox1
         // 
         this.checkedListBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.checkedListBox1.FormattingEnabled = true;
         this.checkedListBox1.Location = new System.Drawing.Point(0, 0);
         this.checkedListBox1.Name = "checkedListBox1";
         this.checkedListBox1.Size = new System.Drawing.Size(360, 309);
         this.checkedListBox1.TabIndex = 1;
         // 
         // listBox1
         // 
         this.listBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listBox1.FormattingEnabled = true;
         this.listBox1.Location = new System.Drawing.Point(0, 39);
         this.listBox1.Name = "listBox1";
         this.listBox1.Size = new System.Drawing.Size(360, 266);
         this.listBox1.TabIndex = 4;
         // 
         // webBrowser1
         // 
         this.webBrowser1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.webBrowser1.Location = new System.Drawing.Point(0, 0);
         this.webBrowser1.MinimumSize = new System.Drawing.Size(20, 20);
         this.webBrowser1.Name = "webBrowser1";
         this.webBrowser1.Size = new System.Drawing.Size(716, 618);
         this.webBrowser1.TabIndex = 0;
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
         this.toolStrip1.Size = new System.Drawing.Size(360, 39);
         this.toolStrip1.TabIndex = 5;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "toolStripButton1";
         this.btnAdd.Click += new System.EventHandler(this.btnAddPhoto_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "toolStripButton2";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // VisitControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.tbRemark);
         this.Name = "VisitControl";
         this.Size = new System.Drawing.Size(1080, 638);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer2)).EndInit();
         this.splitContainer2.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox tbRemark;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.WebBrowser webBrowser1;
      private SplitContainer splitContainer2;
      private CheckedListBox checkedListBox1;
      private ListBox listBox1;
      private ToolStrip toolStrip1;
      private ToolStripButton btnAdd;
      private ToolStripButton btnDel;
   }
}

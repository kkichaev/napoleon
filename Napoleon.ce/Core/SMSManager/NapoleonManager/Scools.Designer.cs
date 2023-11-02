namespace GRSoft.NapoleonManager
{
   partial class Scools
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Scools));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.localityGrid = new System.Windows.Forms.DataGridView();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.scoolGrid = new System.Windows.Forms.DataGridView();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.classGrid = new System.Windows.Forms.DataGridView();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton3 = new System.Windows.Forms.ToolStripButton();
         this.addLocality = new System.Windows.Forms.ToolStripButton();
         this.delLocality = new System.Windows.Forms.ToolStripButton();
         this.addScool = new System.Windows.Forms.ToolStripButton();
         this.delScool = new System.Windows.Forms.ToolStripButton();
         this.addClass = new System.Windows.Forms.ToolStripButton();
         this.delClass = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.localityGrid)).BeginInit();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.scoolGrid)).BeginInit();
         this.toolStrip3.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.classGrid)).BeginInit();
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
         this.splitContainer1.Panel1.Controls.Add(this.localityGrid);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(777, 421);
         this.splitContainer1.SplitterDistance = 239;
         this.splitContainer1.TabIndex = 0;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addLocality,
            this.delLocality});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(239, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // localityGrid
         // 
         this.localityGrid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.localityGrid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.localityGrid.Location = new System.Drawing.Point(0, 25);
         this.localityGrid.Name = "localityGrid";
         this.localityGrid.Size = new System.Drawing.Size(239, 396);
         this.localityGrid.TabIndex = 0;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.scoolGrid);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.classGrid);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer2.Size = new System.Drawing.Size(534, 421);
         this.splitContainer2.SplitterDistance = 284;
         this.splitContainer2.TabIndex = 0;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addScool,
            this.delScool});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(284, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // scoolGrid
         // 
         this.scoolGrid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.scoolGrid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.scoolGrid.Location = new System.Drawing.Point(0, 25);
         this.scoolGrid.Name = "scoolGrid";
         this.scoolGrid.Size = new System.Drawing.Size(284, 396);
         this.scoolGrid.TabIndex = 0;
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addClass,
            this.delClass});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(246, 25);
         this.toolStrip3.TabIndex = 1;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // classGrid
         // 
         this.classGrid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.classGrid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.classGrid.Location = new System.Drawing.Point(0, 25);
         this.classGrid.Name = "classGrid";
         this.classGrid.Size = new System.Drawing.Size(246, 396);
         this.classGrid.TabIndex = 0;
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton1.Image")));
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton1.Text = "toolStripButton1";
         // 
         // toolStripButton2
         // 
         this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton2.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton2.Name = "toolStripButton2";
         this.toolStripButton2.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton2.Text = "toolStripButton2";
         // 
         // toolStripButton3
         // 
         this.toolStripButton3.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton3.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.toolStripButton3.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton3.Name = "toolStripButton3";
         this.toolStripButton3.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton3.Text = "toolStripButton3";
         // 
         // addLocality
         // 
         this.addLocality.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.addLocality.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.addLocality.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.addLocality.Name = "addLocality";
         this.addLocality.Size = new System.Drawing.Size(23, 22);
         this.addLocality.Text = "Новый населенный пункт";
         // 
         // delLocality
         // 
         this.delLocality.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.delLocality.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.delLocality.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.delLocality.Name = "delLocality";
         this.delLocality.Size = new System.Drawing.Size(23, 22);
         this.delLocality.Text = "Удалить населенный пункт";
         // 
         // addScool
         // 
         this.addScool.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.addScool.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.addScool.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.addScool.Name = "addScool";
         this.addScool.Size = new System.Drawing.Size(23, 22);
         this.addScool.Text = "Новая школа";
         // 
         // delScool
         // 
         this.delScool.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.delScool.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.delScool.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.delScool.Name = "delScool";
         this.delScool.Size = new System.Drawing.Size(23, 22);
         this.delScool.Text = "Удалить школу";
         // 
         // addClass
         // 
         this.addClass.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.addClass.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.addClass.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.addClass.Name = "addClass";
         this.addClass.Size = new System.Drawing.Size(23, 22);
         this.addClass.Text = "Новый класс";
         // 
         // delClass
         // 
         this.delClass.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.delClass.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.delClass.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.delClass.Name = "delClass";
         this.delClass.Size = new System.Drawing.Size(23, 22);
         this.delClass.Text = "Удалить класс";
         // 
         // Scools
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(777, 421);
         this.Controls.Add(this.splitContainer1);
         this.Name = "Scools";
         this.Text = "Школы";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.localityGrid)).EndInit();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.scoolGrid)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.classGrid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.DataGridView localityGrid;
      private System.Windows.Forms.DataGridView scoolGrid;
      private System.Windows.Forms.DataGridView classGrid;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripButton toolStripButton2;
      private System.Windows.Forms.ToolStripButton toolStripButton3;
      private System.Windows.Forms.ToolStripButton addLocality;
      private System.Windows.Forms.ToolStripButton delLocality;
      private System.Windows.Forms.ToolStripButton addScool;
      private System.Windows.Forms.ToolStripButton delScool;
      private System.Windows.Forms.ToolStripButton addClass;
      private System.Windows.Forms.ToolStripButton delClass;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmSelectSKU
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectSKU));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tstbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbClearSearch = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbCancel = new System.Windows.Forms.ToolStripButton();
         this.tsbOK = new System.Windows.Forms.ToolStripButton();
         this.colorFilter = new GRSoft.NapoleonManager.TSColorFilter();
         this.panel1 = new System.Windows.Forms.Panel();
         this.tvArticles = new System.Windows.Forms.TreeView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tstbFind,
            this.tsbClearSearch,
            this.toolStripSeparator1,
            this.tsbCancel,
            this.tsbOK,
            this.colorFilter});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(456, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tstbFind
         // 
         this.tstbFind.Name = "tstbFind";
         this.tstbFind.Size = new System.Drawing.Size(170, 25);
         // 
         // tsbClearSearch
         // 
         this.tsbClearSearch.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbClearSearch.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.edit_clear_4;
         this.tsbClearSearch.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbClearSearch.Margin = new System.Windows.Forms.Padding(10, 1, 0, 2);
         this.tsbClearSearch.Name = "tsbClearSearch";
         this.tsbClearSearch.Size = new System.Drawing.Size(23, 22);
         this.tsbClearSearch.Text = "Очистить поиск";
         this.tsbClearSearch.Click += new System.EventHandler(this.tsbClearSearch_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbCancel
         // 
         this.tsbCancel.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCancel.Image = ((System.Drawing.Image)(resources.GetObject("tsbCancel.Image")));
         this.tsbCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCancel.Margin = new System.Windows.Forms.Padding(0, 1, 7, 2);
         this.tsbCancel.Name = "tsbCancel";
         this.tsbCancel.Size = new System.Drawing.Size(23, 22);
         this.tsbCancel.Text = "Закрыть";
         this.tsbCancel.Click += new System.EventHandler(this.tsbCancel_Click);
         // 
         // tsbOK
         // 
         this.tsbOK.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbOK.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOK.Image = ((System.Drawing.Image)(resources.GetObject("tsbOK.Image")));
         this.tsbOK.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbOK.Name = "tsbOK";
         this.tsbOK.Size = new System.Drawing.Size(23, 22);
         this.tsbOK.Text = "ОК";
         this.tsbOK.Click += new System.EventHandler(this.tsbOK_Click);
         // 
         // colorFilter
         // 
         this.colorFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.colorFilter.Image = ((System.Drawing.Image)(resources.GetObject("colorFilter.Image")));
         this.colorFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.colorFilter.Name = "colorFilter";
         this.colorFilter.Size = new System.Drawing.Size(120, 22);
         this.colorFilter.Text = "Фильтр по цветам";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.tvArticles);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(456, 404);
         this.panel1.TabIndex = 1;
         // 
         // tvArticles
         // 
         this.tvArticles.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvArticles.HideSelection = false;
         this.tvArticles.ImageIndex = 0;
         this.tvArticles.ImageList = this.imageList1;
         this.tvArticles.Location = new System.Drawing.Point(7, 7);
         this.tvArticles.Name = "tvArticles";
         this.tvArticles.SelectedImageIndex = 0;
         this.tvArticles.Size = new System.Drawing.Size(442, 390);
         this.tvArticles.TabIndex = 0;
         this.tvArticles.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvArticles_AfterCheck);
         // 
         // imageList1
         // 
         this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         this.imageList1.Images.SetKeyName(0, "Folder_Back.ico");
         this.imageList1.Images.SetKeyName(1, "Generic_Document.ico");
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 429);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(456, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // FmSelectSKU
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(456, 451);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectSKU";
         this.Text = "Выберите SKU";
         this.Load += new System.EventHandler(this.FmSelectSKU_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSelectSKU_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.ToolStrip toolStrip1;
      protected System.Windows.Forms.Panel panel1;
      protected System.Windows.Forms.StatusStrip statusStrip1;
      protected System.Windows.Forms.ToolStripTextBox tstbFind;
      protected System.Windows.Forms.ToolStripButton tsbClearSearch;
      protected System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      protected System.Windows.Forms.ToolStripButton tsbOK;
      protected System.Windows.Forms.ToolStripButton tsbCancel;
      protected System.Windows.Forms.TreeView tvArticles;
      protected TSColorFilter colorFilter;
      private System.Windows.Forms.ImageList imageList1;
      //protected static System.Windows.Forms.TreeView tvArticles = new System.Windows.Forms.TreeView();
   }
}
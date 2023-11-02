namespace GRSoft.Ads
{
   partial class FmOrderMap
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrderMap));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.lvDetail = new System.Windows.Forms.ListView();
         this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader3 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader4 = new System.Windows.Forms.ColumnHeader();
         this.wb = new System.Windows.Forms.WebBrowser();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 352);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(586, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(586, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
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
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.lvDetail);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.wb);
         this.splitContainer1.Size = new System.Drawing.Size(586, 327);
         this.splitContainer1.SplitterDistance = 220;
         this.splitContainer1.TabIndex = 2;
         // 
         // lvDetail
         // 
         this.lvDetail.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2,
            this.columnHeader3,
            this.columnHeader4});
         this.lvDetail.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvDetail.GridLines = true;
         this.lvDetail.Location = new System.Drawing.Point(0, 0);
         this.lvDetail.Name = "lvDetail";
         this.lvDetail.OwnerDraw = true;
         this.lvDetail.Size = new System.Drawing.Size(220, 327);
         this.lvDetail.TabIndex = 0;
         this.lvDetail.UseCompatibleStateImageBehavior = false;
         this.lvDetail.View = System.Windows.Forms.View.Details;
         this.lvDetail.DrawColumnHeader += new System.Windows.Forms.DrawListViewColumnHeaderEventHandler(this.lvDetail_DrawColumnHeader);
         this.lvDetail.DrawSubItem += new System.Windows.Forms.DrawListViewSubItemEventHandler(this.lvDetail_DrawSubItem);
         // 
         // columnHeader1
         // 
         this.columnHeader1.Text = "Время";
         // 
         // columnHeader2
         // 
         this.columnHeader2.Text = "";
         // 
         // columnHeader3
         // 
         this.columnHeader3.Text = "";
         // 
         // columnHeader4
         // 
         this.columnHeader4.Text = "";
         // 
         // wb
         // 
         this.wb.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wb.Location = new System.Drawing.Point(0, 0);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.Size = new System.Drawing.Size(362, 327);
         this.wb.TabIndex = 0;
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(64, 1);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(143, 20);
         this.dtpDate.TabIndex = 3;
         // 
         // FmOrderMap
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(586, 374);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrderMap";
         this.Text = "Карта заказов";
         this.Load += new System.EventHandler(this.FmOrderMap_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.WebBrowser wb;
      private System.Windows.Forms.ListView lvDetail;
      private System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.ColumnHeader columnHeader1;
      private System.Windows.Forms.ColumnHeader columnHeader2;
      private System.Windows.Forms.ColumnHeader columnHeader3;
      private System.Windows.Forms.ColumnHeader columnHeader4;
   }
}
namespace GRSoft.Ads
{
   partial class FmCalendar
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCalendar));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnWeekDec = new System.Windows.Forms.ToolStripButton();
         this.btnWeekInc = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.lvCalendar = new System.Windows.Forms.ListView();
         this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader3 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader4 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader5 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader6 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader7 = new System.Windows.Forms.ColumnHeader();
         this.columnHeader8 = new System.Windows.Forms.ColumnHeader();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.cbBrigade = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnWeekDec,
            this.btnWeekInc,
            this.toolStripSeparator2,
            this.toolStripLabel1,
            this.cbBrigade});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(766, 25);
         this.toolStrip1.TabIndex = 0;
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnWeekDec
         // 
         this.btnWeekDec.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnWeekDec.Image = ((System.Drawing.Image)(resources.GetObject("btnWeekDec.Image")));
         this.btnWeekDec.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnWeekDec.Name = "btnWeekDec";
         this.btnWeekDec.Size = new System.Drawing.Size(23, 22);
         this.btnWeekDec.Text = "Предыдущая неделя.";
         this.btnWeekDec.Click += new System.EventHandler(this.btnWeekDec_Click);
         // 
         // btnWeekInc
         // 
         this.btnWeekInc.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnWeekInc.Image = ((System.Drawing.Image)(resources.GetObject("btnWeekInc.Image")));
         this.btnWeekInc.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnWeekInc.Name = "btnWeekInc";
         this.btnWeekInc.Size = new System.Drawing.Size(23, 22);
         this.btnWeekInc.Text = "Следующая неделя";
         this.btnWeekInc.Click += new System.EventHandler(this.btnWeekInc_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 504);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(766, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // lvCalendar
         // 
         this.lvCalendar.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2,
            this.columnHeader3,
            this.columnHeader4,
            this.columnHeader5,
            this.columnHeader6,
            this.columnHeader7,
            this.columnHeader8});
         this.lvCalendar.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvCalendar.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lvCalendar.GridLines = true;
         this.lvCalendar.Location = new System.Drawing.Point(0, 25);
         this.lvCalendar.Name = "lvCalendar";
         this.lvCalendar.OwnerDraw = true;
         this.lvCalendar.Size = new System.Drawing.Size(766, 479);
         this.lvCalendar.TabIndex = 2;
         this.lvCalendar.UseCompatibleStateImageBehavior = false;
         this.lvCalendar.View = System.Windows.Forms.View.Details;
         this.lvCalendar.DrawColumnHeader += new System.Windows.Forms.DrawListViewColumnHeaderEventHandler(this.lvCalendar_DrawColumnHeader);
         this.lvCalendar.MouseUp += new System.Windows.Forms.MouseEventHandler(this.lvCalendar_MouseUp);
         this.lvCalendar.DrawSubItem += new System.Windows.Forms.DrawListViewSubItemEventHandler(this.lvCalendar_DrawSubItem);
         // 
         // columnHeader1
         // 
         this.columnHeader1.Text = "Время";
         this.columnHeader1.Width = 118;
         // 
         // columnHeader2
         // 
         this.columnHeader2.Width = 90;
         // 
         // columnHeader3
         // 
         this.columnHeader3.Width = 90;
         // 
         // columnHeader4
         // 
         this.columnHeader4.Width = 90;
         // 
         // columnHeader5
         // 
         this.columnHeader5.Width = 90;
         // 
         // columnHeader6
         // 
         this.columnHeader6.Width = 90;
         // 
         // columnHeader7
         // 
         this.columnHeader7.Width = 90;
         // 
         // columnHeader8
         // 
         this.columnHeader8.Width = 90;
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // cbBrigade
         // 
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(121, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(63, 22);
         this.toolStripLabel1.Text = "назначить";
         // 
         // FmCalendar
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(766, 526);
         this.Controls.Add(this.lvCalendar);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmCalendar";
         this.Text = "Календарь";
         this.Load += new System.EventHandler(this.FmCalendar_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmCalendar_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ListView lvCalendar;
      private System.Windows.Forms.ColumnHeader columnHeader1;
      private System.Windows.Forms.ColumnHeader columnHeader2;
      private System.Windows.Forms.ColumnHeader columnHeader3;
      private System.Windows.Forms.ColumnHeader columnHeader4;
      private System.Windows.Forms.ColumnHeader columnHeader5;
      private System.Windows.Forms.ColumnHeader columnHeader6;
      private System.Windows.Forms.ColumnHeader columnHeader7;
      private System.Windows.Forms.ColumnHeader columnHeader8;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnWeekDec;
      private System.Windows.Forms.ToolStripButton btnWeekInc;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbBrigade;
   }
}
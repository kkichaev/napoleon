namespace GRSoft.NapoleonManager
{
   partial class FmRoute
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRoute));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAgent = new System.Windows.Forms.ToolStripButton();
         this.btnClass = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnDelete = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.lbAgents = new System.Windows.Forms.ListBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.label1 = new System.Windows.Forms.Label();
         this.tvRoute = new System.Windows.Forms.TreeView();
         this.panel2 = new System.Windows.Forms.Panel();
         this.label2 = new System.Windows.Forms.Label();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 370);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(597, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAgent,
            this.btnClass,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnDelete});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(597, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAgent
         // 
         this.btnAgent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgent.Image = ((System.Drawing.Image)(resources.GetObject("btnAgent.Image")));
         this.btnAgent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgent.Name = "btnAgent";
         this.btnAgent.Size = new System.Drawing.Size(23, 22);
         this.btnAgent.Text = "Агенты";
         this.btnAgent.Click += new System.EventHandler(this.btnAgent_Click);
         // 
         // btnClass
         // 
         this.btnClass.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClass.Image = ((System.Drawing.Image)(resources.GetObject("btnClass.Image")));
         this.btnClass.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClass.Name = "btnClass";
         this.btnClass.Size = new System.Drawing.Size(23, 22);
         this.btnClass.Text = "Классы";
         this.btnClass.Click += new System.EventHandler(this.btnClass_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnDelete
         // 
         this.btnDelete.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelete.Image = ((System.Drawing.Image)(resources.GetObject("btnDelete.Image")));
         this.btnDelete.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelete.Name = "btnDelete";
         this.btnDelete.Size = new System.Drawing.Size(23, 22);
         this.btnDelete.Text = "Удалить";
         this.btnDelete.Click += new System.EventHandler(this.btnDelete_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.lbAgents);
         this.splitContainer1.Panel1.Controls.Add(this.panel1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tvRoute);
         this.splitContainer1.Panel2.Controls.Add(this.panel2);
         this.splitContainer1.Size = new System.Drawing.Size(597, 345);
         this.splitContainer1.SplitterDistance = 275;
         this.splitContainer1.TabIndex = 2;
         // 
         // lbAgents
         // 
         this.lbAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbAgents.FormattingEnabled = true;
         this.lbAgents.Location = new System.Drawing.Point(0, 23);
         this.lbAgents.Name = "lbAgents";
         this.lbAgents.Size = new System.Drawing.Size(275, 316);
         this.lbAgents.Sorted = true;
         this.lbAgents.TabIndex = 0;
         this.lbAgents.SelectedIndexChanged += new System.EventHandler(this.lbAgents_SelectedIndexChanged);
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(275, 23);
         this.panel1.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(4, 2);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(44, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Агенты";
         // 
         // tvRoute
         // 
         this.tvRoute.AllowDrop = true;
         this.tvRoute.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvRoute.Location = new System.Drawing.Point(0, 23);
         this.tvRoute.Name = "tvRoute";
         this.tvRoute.Size = new System.Drawing.Size(318, 322);
         this.tvRoute.TabIndex = 0;
         this.tvRoute.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvRoute_DragDrop);
         this.tvRoute.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvRoute_DragEnter);
         this.tvRoute.DragOver += new System.Windows.Forms.DragEventHandler(this.tvRoute_DragOver);
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.label2);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(318, 23);
         this.panel2.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 3);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(52, 13);
         this.label2.TabIndex = 0;
         this.label2.Text = "Маршрут";
         // 
         // FmRoute
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(597, 392);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmRoute";
         this.Text = "Маршрут";
         this.Load += new System.EventHandler(this.FmRoute_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmRoute_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ListBox lbAgents;
      private System.Windows.Forms.TreeView tvRoute;
      private System.Windows.Forms.ToolStripButton btnAgent;
      private System.Windows.Forms.ToolStripButton btnClass;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnDelete;
   }
}
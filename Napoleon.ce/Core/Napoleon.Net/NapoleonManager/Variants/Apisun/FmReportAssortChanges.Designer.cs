namespace GRSoft.NapoleonManager
{
   partial class FmReportAssortChanges
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReportAssortChanges));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.dtpRange1Start = new System.Windows.Forms.DateTimePicker();
         this.dtpRange1Finish = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.dtpRange2Finish = new System.Windows.Forms.DateTimePicker();
         this.dtpRange2Start = new System.Windows.Forms.DateTimePicker();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.CheckedListBox();
         this.lbPrice = new System.Windows.Forms.ListBox();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miDel = new System.Windows.Forms.ToolStripMenuItem();
         this.btnReport = new System.Windows.Forms.Button();
         this.tvRegions = new System.Windows.Forms.TreeView();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnCheckAgent = new System.Windows.Forms.ToolStripButton();
         this.btnUncheckAgent = new System.Windows.Forms.ToolStripButton();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnCheckRegion = new System.Windows.Forms.ToolStripButton();
         this.btnUncheckRegion = new System.Windows.Forms.ToolStripButton();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.btnSKU = new System.Windows.Forms.ToolStripButton();
         this.groupBox1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.contextMenuStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.dtpRange1Start);
         this.groupBox1.Controls.Add(this.dtpRange1Finish);
         this.groupBox1.Controls.Add(this.label2);
         this.groupBox1.Controls.Add(this.label1);
         this.groupBox1.Location = new System.Drawing.Point(14, 10);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(233, 73);
         this.groupBox1.TabIndex = 0;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Период 1";
         // 
         // dtpRange1Start
         // 
         this.dtpRange1Start.Location = new System.Drawing.Point(67, 16);
         this.dtpRange1Start.Name = "dtpRange1Start";
         this.dtpRange1Start.Size = new System.Drawing.Size(160, 20);
         this.dtpRange1Start.TabIndex = 3;
         // 
         // dtpRange1Finish
         // 
         this.dtpRange1Finish.Location = new System.Drawing.Point(67, 42);
         this.dtpRange1Finish.Name = "dtpRange1Finish";
         this.dtpRange1Finish.Size = new System.Drawing.Size(160, 20);
         this.dtpRange1Finish.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 42);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(38, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Конец";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(44, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Начало";
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.dtpRange2Finish);
         this.groupBox2.Controls.Add(this.dtpRange2Start);
         this.groupBox2.Controls.Add(this.label4);
         this.groupBox2.Controls.Add(this.label3);
         this.groupBox2.Location = new System.Drawing.Point(262, 10);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(243, 73);
         this.groupBox2.TabIndex = 1;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Период 2";
         // 
         // dtpRange2Finish
         // 
         this.dtpRange2Finish.Location = new System.Drawing.Point(72, 48);
         this.dtpRange2Finish.Name = "dtpRange2Finish";
         this.dtpRange2Finish.Size = new System.Drawing.Size(160, 20);
         this.dtpRange2Finish.TabIndex = 3;
         // 
         // dtpRange2Start
         // 
         this.dtpRange2Start.Location = new System.Drawing.Point(72, 16);
         this.dtpRange2Start.Name = "dtpRange2Start";
         this.dtpRange2Start.Size = new System.Drawing.Size(160, 20);
         this.dtpRange2Start.TabIndex = 2;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(12, 48);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(38, 14);
         this.label4.TabIndex = 1;
         this.label4.Text = "Конец";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(6, 16);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(44, 14);
         this.label3.TabIndex = 0;
         this.label3.Text = "Начало";
         // 
         // cbAgents
         // 
         this.cbAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(0, 25);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(602, 229);
         this.cbAgents.TabIndex = 3;
         // 
         // lbPrice
         // 
         this.lbPrice.ContextMenuStrip = this.contextMenuStrip1;
         this.lbPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbPrice.FormattingEnabled = true;
         this.lbPrice.ItemHeight = 14;
         this.lbPrice.Location = new System.Drawing.Point(0, 25);
         this.lbPrice.Name = "lbPrice";
         this.lbPrice.Size = new System.Drawing.Size(646, 480);
         this.lbPrice.TabIndex = 7;
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miDel});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(119, 26);
         // 
         // miDel
         // 
         this.miDel.Name = "miDel";
         this.miDel.Size = new System.Drawing.Size(118, 22);
         this.miDel.Text = "Удалить";
         this.miDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(533, 16);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 8;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // tvRegions
         // 
         this.tvRegions.CheckBoxes = true;
         this.tvRegions.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvRegions.Location = new System.Drawing.Point(0, 25);
         this.tvRegions.Name = "tvRegions";
         this.tvRegions.Size = new System.Drawing.Size(602, 226);
         this.tvRegions.TabIndex = 9;
         this.tvRegions.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvRegions_AfterCheck);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.groupBox1);
         this.panel1.Controls.Add(this.groupBox2);
         this.panel1.Controls.Add(this.btnReport);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(1252, 91);
         this.panel1.TabIndex = 10;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 91);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.lbPrice);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer1.Size = new System.Drawing.Size(1252, 509);
         this.splitContainer1.SplitterDistance = 602;
         this.splitContainer1.TabIndex = 11;
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
         this.splitContainer2.Panel1.Controls.Add(this.cbAgents);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.tvRegions);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Size = new System.Drawing.Size(602, 509);
         this.splitContainer2.SplitterDistance = 254;
         this.splitContainer2.TabIndex = 10;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.btnCheckAgent,
            this.btnUncheckAgent});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(602, 25);
         this.toolStrip1.TabIndex = 4;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(47, 22);
         this.toolStripLabel1.Text = "Агенты";
         // 
         // btnCheckAgent
         // 
         this.btnCheckAgent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheckAgent.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnCheckAgent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheckAgent.Name = "btnCheckAgent";
         this.btnCheckAgent.Size = new System.Drawing.Size(23, 22);
         this.btnCheckAgent.Text = "Выбрать все";
         this.btnCheckAgent.Click += new System.EventHandler(this.btnCheckAgent_Click);
         // 
         // btnUncheckAgent
         // 
         this.btnUncheckAgent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUncheckAgent.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUncheckAgent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUncheckAgent.Name = "btnUncheckAgent";
         this.btnUncheckAgent.Size = new System.Drawing.Size(23, 22);
         this.btnUncheckAgent.Text = "Сбросить все";
         this.btnUncheckAgent.Click += new System.EventHandler(this.btnUncheckAgent_Click);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel2,
            this.btnCheckRegion,
            this.btnUncheckRegion});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(602, 25);
         this.toolStrip2.TabIndex = 10;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(50, 22);
         this.toolStripLabel2.Text = "Районы";
         // 
         // btnCheckRegion
         // 
         this.btnCheckRegion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheckRegion.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnCheckRegion.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheckRegion.Name = "btnCheckRegion";
         this.btnCheckRegion.Size = new System.Drawing.Size(23, 22);
         this.btnCheckRegion.Text = "Выбрать все";
         this.btnCheckRegion.Click += new System.EventHandler(this.btnCheckRegion_Click);
         // 
         // btnUncheckRegion
         // 
         this.btnUncheckRegion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUncheckRegion.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUncheckRegion.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUncheckRegion.Name = "btnUncheckRegion";
         this.btnUncheckRegion.Size = new System.Drawing.Size(23, 22);
         this.btnUncheckRegion.Text = "Сбросить все";
         this.btnUncheckRegion.Click += new System.EventHandler(this.btnUncheckRegion_Click);
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel3,
            this.btnSKU});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(646, 25);
         this.toolStrip3.TabIndex = 8;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(40, 22);
         this.toolStripLabel3.Text = "Товар";
         // 
         // btnSKU
         // 
         this.btnSKU.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSKU.Image = global::GRSoft.NapoleonManager.Properties.Resources.accessorieseditor;
         this.btnSKU.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSKU.Name = "btnSKU";
         this.btnSKU.Size = new System.Drawing.Size(23, 22);
         this.btnSKU.Text = "toolStripButton1";
         this.btnSKU.Click += new System.EventHandler(this.btnSKU_Click);
         // 
         // FmReportAssortChanges
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1252, 600);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportAssortChanges";
         this.Text = "Изменение ассортимента";
         this.Load += new System.EventHandler(this.FmReportAssortChanges_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.contextMenuStrip1.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpRange1Start;
      private System.Windows.Forms.DateTimePicker dtpRange1Finish;
      private System.Windows.Forms.DateTimePicker dtpRange2Finish;
      private System.Windows.Forms.DateTimePicker dtpRange2Start;
      private System.Windows.Forms.CheckedListBox cbAgents;
      private System.Windows.Forms.ListBox lbPrice;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.TreeView tvRegions;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripButton btnSKU;
      private System.Windows.Forms.ToolStripButton btnCheckAgent;
      private System.Windows.Forms.ToolStripButton btnUncheckAgent;
      private System.Windows.Forms.ToolStripButton btnCheckRegion;
      private System.Windows.Forms.ToolStripButton btnUncheckRegion;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miDel;
   }
}
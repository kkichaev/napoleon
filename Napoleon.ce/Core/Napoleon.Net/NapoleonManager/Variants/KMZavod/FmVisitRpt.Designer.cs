namespace GRSoft.NapoleonManager
{
   partial class FmVisitRpt
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVisitRpt));
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.button1 = new System.Windows.Forms.Button();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.panel3 = new System.Windows.Forms.Panel();
         this.lbDiv = new System.Windows.Forms.CheckedListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnDivOff = new System.Windows.Forms.ToolStripButton();
         this.btnDivOn = new System.Windows.Forms.ToolStripButton();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.panel4 = new System.Windows.Forms.Panel();
         this.lbAgent = new System.Windows.Forms.CheckedListBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnAgentOff = new System.Windows.Forms.ToolStripButton();
         this.btnAgentOn = new System.Windows.Forms.ToolStripButton();
         this.panel5 = new System.Windows.Forms.Panel();
         this.lbOrg = new System.Windows.Forms.CheckedListBox();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.btnRerfresh = new System.Windows.Forms.ToolStripButton();
         this.btnOrgOff = new System.Windows.Forms.ToolStripButton();
         this.btnOrgOn = new System.Windows.Forms.ToolStripButton();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.panel3.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.panel4.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.panel5.SuspendLayout();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(37, 7);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(200, 20);
         this.dtpStart.TabIndex = 0;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(37, 35);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(200, 20);
         this.dtpFinish.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "c";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 35);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "по";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dtpStart);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.dtpFinish);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(981, 61);
         this.panel1.TabIndex = 4;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.button1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 548);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(981, 46);
         this.panel2.TabIndex = 5;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(894, 11);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Отчет";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 61);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.panel3);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(981, 487);
         this.splitContainer1.SplitterDistance = 300;
         this.splitContainer1.TabIndex = 9;
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.lbDiv);
         this.panel3.Controls.Add(this.toolStrip1);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel3.Location = new System.Drawing.Point(0, 0);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(300, 487);
         this.panel3.TabIndex = 7;
         // 
         // lbDiv
         // 
         this.lbDiv.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbDiv.FormattingEnabled = true;
         this.lbDiv.Location = new System.Drawing.Point(0, 25);
         this.lbDiv.Name = "lbDiv";
         this.lbDiv.Size = new System.Drawing.Size(300, 462);
         this.lbDiv.TabIndex = 1;
         this.lbDiv.ItemCheck += new System.Windows.Forms.ItemCheckEventHandler(this.lbDiv_ItemCheck);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.btnDivOff,
            this.btnDivOn});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(300, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(92, 22);
         this.toolStripLabel1.Text = "Подразделения";
         // 
         // btnDivOff
         // 
         this.btnDivOff.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnDivOff.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDivOff.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnDivOff.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDivOff.Name = "btnDivOff";
         this.btnDivOff.Size = new System.Drawing.Size(23, 22);
         this.btnDivOff.Text = "Сбросить";
         this.btnDivOff.Click += new System.EventHandler(this.btnDivOff_Click);
         // 
         // btnDivOn
         // 
         this.btnDivOn.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnDivOn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDivOn.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnDivOn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDivOn.Name = "btnDivOn";
         this.btnDivOn.Size = new System.Drawing.Size(23, 22);
         this.btnDivOn.Text = "Отметить";
         this.btnDivOn.Click += new System.EventHandler(this.btnDivOn_Click);
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.panel4);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.panel5);
         this.splitContainer2.Size = new System.Drawing.Size(677, 487);
         this.splitContainer2.SplitterDistance = 331;
         this.splitContainer2.TabIndex = 0;
         // 
         // panel4
         // 
         this.panel4.Controls.Add(this.lbAgent);
         this.panel4.Controls.Add(this.toolStrip2);
         this.panel4.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel4.Location = new System.Drawing.Point(0, 0);
         this.panel4.Name = "panel4";
         this.panel4.Size = new System.Drawing.Size(331, 487);
         this.panel4.TabIndex = 8;
         // 
         // lbAgent
         // 
         this.lbAgent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbAgent.FormattingEnabled = true;
         this.lbAgent.Location = new System.Drawing.Point(0, 25);
         this.lbAgent.Name = "lbAgent";
         this.lbAgent.Size = new System.Drawing.Size(331, 462);
         this.lbAgent.TabIndex = 1;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel2,
            this.btnAgentOff,
            this.btnAgentOn});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(331, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(47, 22);
         this.toolStripLabel2.Text = "Агенты";
         // 
         // btnAgentOff
         // 
         this.btnAgentOff.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnAgentOff.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgentOff.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnAgentOff.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgentOff.Name = "btnAgentOff";
         this.btnAgentOff.Size = new System.Drawing.Size(23, 22);
         this.btnAgentOff.Text = "Сбросить";
         this.btnAgentOff.Click += new System.EventHandler(this.btnAgentOff_Click);
         // 
         // btnAgentOn
         // 
         this.btnAgentOn.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnAgentOn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgentOn.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnAgentOn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgentOn.Name = "btnAgentOn";
         this.btnAgentOn.Size = new System.Drawing.Size(23, 22);
         this.btnAgentOn.Text = "Отметить";
         this.btnAgentOn.Click += new System.EventHandler(this.btnAgentOn_Click);
         // 
         // panel5
         // 
         this.panel5.Controls.Add(this.lbOrg);
         this.panel5.Controls.Add(this.toolStrip3);
         this.panel5.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel5.Location = new System.Drawing.Point(0, 0);
         this.panel5.Name = "panel5";
         this.panel5.Size = new System.Drawing.Size(342, 487);
         this.panel5.TabIndex = 9;
         // 
         // lbOrg
         // 
         this.lbOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbOrg.FormattingEnabled = true;
         this.lbOrg.Location = new System.Drawing.Point(0, 25);
         this.lbOrg.Name = "lbOrg";
         this.lbOrg.Size = new System.Drawing.Size(342, 462);
         this.lbOrg.TabIndex = 1;
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel3,
            this.btnRerfresh,
            this.btnOrgOff,
            this.btnOrgOn});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(342, 25);
         this.toolStrip3.TabIndex = 0;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(78, 22);
         this.toolStripLabel3.Text = "Контрагенты";
         // 
         // btnRerfresh
         // 
         this.btnRerfresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRerfresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRerfresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRerfresh.Name = "btnRerfresh";
         this.btnRerfresh.Size = new System.Drawing.Size(23, 22);
         this.btnRerfresh.Text = "Получить список организаций";
         this.btnRerfresh.Click += new System.EventHandler(this.btnRerfresh_Click);
         // 
         // btnOrgOff
         // 
         this.btnOrgOff.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnOrgOff.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrgOff.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnOrgOff.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrgOff.Name = "btnOrgOff";
         this.btnOrgOff.Size = new System.Drawing.Size(23, 22);
         this.btnOrgOff.Text = "Сбросить";
         this.btnOrgOff.Click += new System.EventHandler(this.btnOrgOff_Click);
         // 
         // btnOrgOn
         // 
         this.btnOrgOn.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnOrgOn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrgOn.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnOrgOn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrgOn.Name = "btnOrgOn";
         this.btnOrgOn.Size = new System.Drawing.Size(23, 22);
         this.btnOrgOn.Text = "Отметить";
         this.btnOrgOn.Click += new System.EventHandler(this.btnOrgOn_Click);
         // 
         // FmVisitRpt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(981, 594);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVisitRpt";
         this.Text = "Отчет о посещениях Клиентов";
         this.Load += new System.EventHandler(this.FmVisitRpt_Load);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.panel3.ResumeLayout(false);
         this.panel3.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         this.panel4.ResumeLayout(false);
         this.panel4.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel5.ResumeLayout(false);
         this.panel5.PerformLayout();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.CheckedListBox lbDiv;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton btnDivOn;
      private System.Windows.Forms.ToolStripButton btnDivOff;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.Panel panel4;
      private System.Windows.Forms.CheckedListBox lbAgent;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripButton btnAgentOn;
      private System.Windows.Forms.ToolStripButton btnAgentOff;
      private System.Windows.Forms.Panel panel5;
      private System.Windows.Forms.CheckedListBox lbOrg;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripButton btnRerfresh;
      private System.Windows.Forms.ToolStripButton btnOrgOn;
      private System.Windows.Forms.ToolStripButton btnOrgOff;
   }
}
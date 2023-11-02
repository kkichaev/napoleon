namespace GRSoft.NapoleonManager
{
   partial class FmGroupTaskEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGroupTaskEdit));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.cbDiv = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbTask = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.agents = new System.Windows.Forms.CheckedListBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.label5 = new System.Windows.Forms.Label();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnCheckAllAgents = new System.Windows.Forms.ToolStripButton();
         this.btnUncheckAllAgents = new System.Windows.Forms.ToolStripButton();
         this.orgs = new System.Windows.Forms.CheckedListBox();
         this.panel2 = new System.Windows.Forms.Panel();
         this.label6 = new System.Windows.Forms.Label();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnCheckAllOrgs = new System.Windows.Forms.ToolStripButton();
         this.btnUncheckAllOrgs = new System.Windows.Forms.ToolStripButton();
         this.edFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnResetFind = new System.Windows.Forms.ToolStripButton();
         this.panel3 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.button2 = new System.Windows.Forms.Button();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.panel3.SuspendLayout();
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
         this.splitContainer1.Panel1.Controls.Add(this.cbDiv);
         this.splitContainer1.Panel1.Controls.Add(this.label4);
         this.splitContainer1.Panel1.Controls.Add(this.dtpFinish);
         this.splitContainer1.Panel1.Controls.Add(this.dtpStart);
         this.splitContainer1.Panel1.Controls.Add(this.label3);
         this.splitContainer1.Panel1.Controls.Add(this.label2);
         this.splitContainer1.Panel1.Controls.Add(this.tbTask);
         this.splitContainer1.Panel1.Controls.Add(this.label1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(847, 490);
         this.splitContainer1.SplitterDistance = 119;
         this.splitContainer1.TabIndex = 0;
         // 
         // cbDiv
         // 
         this.cbDiv.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDiv.FormattingEnabled = true;
         this.cbDiv.Location = new System.Drawing.Point(490, 83);
         this.cbDiv.Name = "cbDiv";
         this.cbDiv.Size = new System.Drawing.Size(345, 23);
         this.cbDiv.TabIndex = 7;
         this.cbDiv.SelectedIndexChanged += new System.EventHandler(this.cbDiv_SelectedIndexChanged);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(412, 87);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(72, 15);
         this.label4.TabIndex = 6;
         this.label4.Text = "Подразделение";
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(490, 51);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(200, 20);
         this.dtpFinish.TabIndex = 5;
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(490, 19);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(200, 20);
         this.dtpStart.TabIndex = 4;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(412, 53);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(32, 15);
         this.label3.TabIndex = 3;
         this.label3.Text = "Конец";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(412, 19);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(38, 15);
         this.label2.TabIndex = 2;
         this.label2.Text = "Начало";
         // 
         // tbTask
         // 
         this.tbTask.Location = new System.Drawing.Point(54, 12);
         this.tbTask.Multiline = true;
         this.tbTask.Name = "tbTask";
         this.tbTask.Size = new System.Drawing.Size(332, 94);
         this.tbTask.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(11, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 15);
         this.label1.TabIndex = 0;
         this.label1.Text = "Задача";
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.agents);
         this.splitContainer2.Panel1.Controls.Add(this.panel1);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip1);
         this.splitContainer2.Panel1.Padding = new System.Windows.Forms.Padding(5, 0, 0, 0);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.orgs);
         this.splitContainer2.Panel2.Controls.Add(this.panel2);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Panel2.Padding = new System.Windows.Forms.Padding(0, 0, 5, 0);
         this.splitContainer2.Size = new System.Drawing.Size(847, 367);
         this.splitContainer2.SplitterDistance = 399;
         this.splitContainer2.TabIndex = 0;
         // 
         // agents
         // 
         this.agents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.agents.FormattingEnabled = true;
         this.agents.Location = new System.Drawing.Point(5, 51);
         this.agents.Name = "agents";
         this.agents.Size = new System.Drawing.Size(394, 316);
         this.agents.TabIndex = 2;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.label5);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(5, 25);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(394, 26);
         this.panel1.TabIndex = 1;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(5, 5);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(37, 15);
         this.label5.TabIndex = 0;
         this.label5.Text = "Агенты";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnCheckAllAgents,
            this.btnUncheckAllAgents});
         this.toolStrip1.Location = new System.Drawing.Point(5, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(394, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnCheckAllAgents
         // 
         this.btnCheckAllAgents.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheckAllAgents.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnCheckAllAgents.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheckAllAgents.Name = "btnCheckAllAgents";
         this.btnCheckAllAgents.Size = new System.Drawing.Size(23, 22);
         this.btnCheckAllAgents.Text = "Выбрать всех";
         this.btnCheckAllAgents.Click += new System.EventHandler(this.btnCheckAllAgents_Click);
         // 
         // btnUncheckAllAgents
         // 
         this.btnUncheckAllAgents.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUncheckAllAgents.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUncheckAllAgents.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUncheckAllAgents.Name = "btnUncheckAllAgents";
         this.btnUncheckAllAgents.Size = new System.Drawing.Size(23, 22);
         this.btnUncheckAllAgents.Text = "Сбросить всех";
         this.btnUncheckAllAgents.Click += new System.EventHandler(this.btnUncheckAllAgents_Click);
         // 
         // orgs
         // 
         this.orgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.orgs.FormattingEnabled = true;
         this.orgs.Location = new System.Drawing.Point(0, 51);
         this.orgs.Name = "orgs";
         this.orgs.Size = new System.Drawing.Size(439, 316);
         this.orgs.TabIndex = 3;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.label6);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel2.Location = new System.Drawing.Point(0, 25);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(439, 26);
         this.panel2.TabIndex = 2;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(6, 5);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(61, 15);
         this.label6.TabIndex = 0;
         this.label6.Text = "Организация";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnCheckAllOrgs,
            this.btnUncheckAllOrgs,
            this.edFind,
            this.btnResetFind});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(439, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnCheckAllOrgs
         // 
         this.btnCheckAllOrgs.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheckAllOrgs.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnCheckAllOrgs.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheckAllOrgs.Name = "btnCheckAllOrgs";
         this.btnCheckAllOrgs.Size = new System.Drawing.Size(23, 22);
         this.btnCheckAllOrgs.Text = "Выбрать всех";
         this.btnCheckAllOrgs.Click += new System.EventHandler(this.btnCheckAllOrgs_Click);
         // 
         // btnUncheckAllOrgs
         // 
         this.btnUncheckAllOrgs.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUncheckAllOrgs.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUncheckAllOrgs.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUncheckAllOrgs.Name = "btnUncheckAllOrgs";
         this.btnUncheckAllOrgs.Size = new System.Drawing.Size(23, 22);
         this.btnUncheckAllOrgs.Text = "toolStripButton4";
         this.btnUncheckAllOrgs.Click += new System.EventHandler(this.btnUncheckAllOrgs_Click);
         // 
         // edFind
         // 
         this.edFind.Name = "edFind";
         this.edFind.Size = new System.Drawing.Size(100, 25);
         this.edFind.TextChanged += new System.EventHandler(this.edFind_TextChanged);
         // 
         // btnResetFind
         // 
         this.btnResetFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnResetFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnResetFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnResetFind.Name = "btnResetFind";
         this.btnResetFind.Size = new System.Drawing.Size(23, 22);
         this.btnResetFind.Text = "Очистить";
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.button2);
         this.panel3.Controls.Add(this.btnOK);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel3.Location = new System.Drawing.Point(0, 490);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(847, 39);
         this.panel3.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(666, 6);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // button2
         // 
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(760, 6);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 1;
         this.button2.Text = "Отмена";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // timer1
         // 
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmGroupTaskEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(847, 529);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel3);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmGroupTaskEdit";
         this.Text = "Общая задача";
         this.Load += new System.EventHandler(this.FmGroupTaskEdit_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.TextBox tbTask;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbDiv;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnCheckAllAgents;
      private System.Windows.Forms.ToolStripButton btnUncheckAllAgents;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnCheckAllOrgs;
      private System.Windows.Forms.ToolStripButton btnUncheckAllOrgs;
      private System.Windows.Forms.ToolStripTextBox edFind;
      private System.Windows.Forms.ToolStripButton btnResetFind;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.CheckedListBox agents;
      private System.Windows.Forms.CheckedListBox orgs;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Timer timer1;
   }
}
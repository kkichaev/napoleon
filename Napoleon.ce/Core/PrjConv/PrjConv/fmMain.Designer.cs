namespace PrjConv
{
   partial class fmMain
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(fmMain));
         this.cbTarget = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.btnBrowse = new System.Windows.Forms.Button();
         this.lbFileList = new System.Windows.Forms.ListBox();
         this.label3 = new System.Windows.Forms.Label();
         this.btnDoneConversion = new System.Windows.Forms.Button();
         this.lbLog = new System.Windows.Forms.ListBox();
         this.label4 = new System.Windows.Forms.Label();
         this.progressBar = new System.Windows.Forms.ProgressBar();
         this.cbClose = new System.Windows.Forms.CheckBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.panel4 = new System.Windows.Forms.Panel();
         this.panel3 = new System.Windows.Forms.Panel();
         this.panel6 = new System.Windows.Forms.Panel();
         this.panel5 = new System.Windows.Forms.Panel();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.panel4.SuspendLayout();
         this.panel3.SuspendLayout();
         this.panel6.SuspendLayout();
         this.panel5.SuspendLayout();
         this.SuspendLayout();
         // 
         // cbTarget
         // 
         this.cbTarget.FormattingEnabled = true;
         this.cbTarget.Items.AddRange(new object[] {
            "2005",
            "2008"});
         this.cbTarget.Location = new System.Drawing.Point(51, 6);
         this.cbTarget.Name = "cbTarget";
         this.cbTarget.Size = new System.Drawing.Size(121, 22);
         this.cbTarget.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(4, 11);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(32, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Цель";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(4, 42);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(43, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Проект";
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(50, 38);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(444, 20);
         this.tbPath.TabIndex = 3;
         // 
         // btnBrowse
         // 
         this.btnBrowse.Location = new System.Drawing.Point(6, 15);
         this.btnBrowse.Name = "btnBrowse";
         this.btnBrowse.Size = new System.Drawing.Size(75, 25);
         this.btnBrowse.TabIndex = 4;
         this.btnBrowse.Text = "Поиск...";
         this.btnBrowse.UseVisualStyleBackColor = true;
         this.btnBrowse.Click += new System.EventHandler(this.btnBrowse_Click);
         // 
         // lbFileList
         // 
         this.lbFileList.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbFileList.FormattingEnabled = true;
         this.lbFileList.ItemHeight = 14;
         this.lbFileList.Location = new System.Drawing.Point(7, 0);
         this.lbFileList.Name = "lbFileList";
         this.lbFileList.Size = new System.Drawing.Size(385, 340);
         this.lbFileList.TabIndex = 5;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(7, 6);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(149, 14);
         this.label3.TabIndex = 6;
         this.label3.Text = "Файлы для преобразования";
         // 
         // btnDoneConversion
         // 
         this.btnDoneConversion.Location = new System.Drawing.Point(87, 14);
         this.btnDoneConversion.Name = "btnDoneConversion";
         this.btnDoneConversion.Size = new System.Drawing.Size(75, 26);
         this.btnDoneConversion.TabIndex = 7;
         this.btnDoneConversion.Text = "Старт";
         this.btnDoneConversion.UseVisualStyleBackColor = true;
         this.btnDoneConversion.Click += new System.EventHandler(this.btnDoneConversion_Click);
         // 
         // lbLog
         // 
         this.lbLog.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbLog.FormattingEnabled = true;
         this.lbLog.ItemHeight = 14;
         this.lbLog.Location = new System.Drawing.Point(0, 0);
         this.lbLog.Name = "lbLog";
         this.lbLog.Size = new System.Drawing.Size(341, 340);
         this.lbLog.TabIndex = 8;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(0, 6);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(24, 14);
         this.label4.TabIndex = 9;
         this.label4.Text = "Лог";
         // 
         // progressBar
         // 
         this.progressBar.Dock = System.Windows.Forms.DockStyle.Fill;
         this.progressBar.Location = new System.Drawing.Point(7, 8);
         this.progressBar.Name = "progressBar";
         this.progressBar.Size = new System.Drawing.Size(730, 17);
         this.progressBar.TabIndex = 10;
         // 
         // cbClose
         // 
         this.cbClose.AutoSize = true;
         this.cbClose.Checked = true;
         this.cbClose.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbClose.Location = new System.Drawing.Point(205, 9);
         this.cbClose.Name = "cbClose";
         this.cbClose.Size = new System.Drawing.Size(190, 18);
         this.cbClose.TabIndex = 11;
         this.cbClose.Text = "Закрыть после преобразований";
         this.cbClose.UseVisualStyleBackColor = true;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.progressBar);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 441);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(744, 33);
         this.panel1.TabIndex = 12;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.groupBox1);
         this.panel2.Controls.Add(this.cbTarget);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Controls.Add(this.cbClose);
         this.panel2.Controls.Add(this.label2);
         this.panel2.Controls.Add(this.tbPath);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel2.Size = new System.Drawing.Size(744, 71);
         this.panel2.TabIndex = 13;
         // 
         // groupBox1
         // 
         this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.groupBox1.Controls.Add(this.btnDoneConversion);
         this.groupBox1.Controls.Add(this.btnBrowse);
         this.groupBox1.Location = new System.Drawing.Point(564, 13);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(169, 47);
         this.groupBox1.TabIndex = 12;
         this.groupBox1.TabStop = false;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 71);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.panel4);
         this.splitContainer1.Panel1.Controls.Add(this.panel3);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.panel6);
         this.splitContainer1.Panel2.Controls.Add(this.panel5);
         this.splitContainer1.Size = new System.Drawing.Size(744, 370);
         this.splitContainer1.SplitterDistance = 392;
         this.splitContainer1.TabIndex = 14;
         // 
         // panel4
         // 
         this.panel4.Controls.Add(this.lbFileList);
         this.panel4.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel4.Location = new System.Drawing.Point(0, 24);
         this.panel4.Name = "panel4";
         this.panel4.Padding = new System.Windows.Forms.Padding(7, 0, 0, 0);
         this.panel4.Size = new System.Drawing.Size(392, 346);
         this.panel4.TabIndex = 1;
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.label3);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel3.Location = new System.Drawing.Point(0, 0);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(392, 24);
         this.panel3.TabIndex = 0;
         // 
         // panel6
         // 
         this.panel6.Controls.Add(this.lbLog);
         this.panel6.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel6.Location = new System.Drawing.Point(0, 24);
         this.panel6.Name = "panel6";
         this.panel6.Padding = new System.Windows.Forms.Padding(0, 0, 7, 0);
         this.panel6.Size = new System.Drawing.Size(348, 346);
         this.panel6.TabIndex = 1;
         // 
         // panel5
         // 
         this.panel5.Controls.Add(this.label4);
         this.panel5.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel5.Location = new System.Drawing.Point(0, 0);
         this.panel5.Name = "panel5";
         this.panel5.Size = new System.Drawing.Size(348, 24);
         this.panel5.TabIndex = 0;
         // 
         // fmMain
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(744, 474);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "fmMain";
         this.Text = "Конвертер проектов 2005 <> 2008";
         this.Load += new System.EventHandler(this.fmMain_Load);
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.panel4.ResumeLayout(false);
         this.panel3.ResumeLayout(false);
         this.panel3.PerformLayout();
         this.panel6.ResumeLayout(false);
         this.panel5.ResumeLayout(false);
         this.panel5.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ComboBox cbTarget;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.Button btnBrowse;
      private System.Windows.Forms.ListBox lbFileList;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Button btnDoneConversion;
      private System.Windows.Forms.ListBox lbLog;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.ProgressBar progressBar;
      private System.Windows.Forms.CheckBox cbClose;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Panel panel4;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.Panel panel6;
      private System.Windows.Forms.Panel panel5;
      private System.Windows.Forms.GroupBox groupBox1;
   }
}


namespace GRSoft.NapoleonManager
{
   partial class FmQuestionReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmQuestionReport));
         this.label1 = new System.Windows.Forms.Label();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.btnRefresh = new System.Windows.Forms.Button();
         this.cbPhoto = new System.Windows.Forms.CheckBox();
         this.btnQuestRefresh = new System.Windows.Forms.Button();
         this.clbQuest = new System.Windows.Forms.CheckedListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.btnCheck = new System.Windows.Forms.ToolStripButton();
         this.btnUncheck = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.clbAgent = new System.Windows.Forms.CheckedListBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.lblAgent = new System.Windows.Forms.ToolStripLabel();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.gbQuestType = new System.Windows.Forms.GroupBox();
         this.rbVert = new System.Windows.Forms.RadioButton();
         this.rbHor = new System.Windows.Forms.RadioButton();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.gbQuestType.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(20, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(53, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Период с";
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(90, 12);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(124, 20);
         this.dtpFrom.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(219, 15);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "по";
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(245, 12);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(124, 20);
         this.dtpTill.TabIndex = 3;
         // 
         // btnRefresh
         // 
         this.btnRefresh.Location = new System.Drawing.Point(9, 8);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(112, 23);
         this.btnRefresh.TabIndex = 4;
         this.btnRefresh.Text = "Построить отчет";
         this.btnRefresh.UseVisualStyleBackColor = true;
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // cbPhoto
         // 
         this.cbPhoto.AutoSize = true;
         this.cbPhoto.Checked = true;
         this.cbPhoto.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPhoto.Location = new System.Drawing.Point(23, 48);
         this.cbPhoto.Name = "cbPhoto";
         this.cbPhoto.Size = new System.Drawing.Size(61, 18);
         this.cbPhoto.TabIndex = 5;
         this.cbPhoto.Text = "с фото";
         this.cbPhoto.UseVisualStyleBackColor = true;
         this.cbPhoto.Visible = false;
         // 
         // btnQuestRefresh
         // 
         this.btnQuestRefresh.Location = new System.Drawing.Point(90, 45);
         this.btnQuestRefresh.Name = "btnQuestRefresh";
         this.btnQuestRefresh.Size = new System.Drawing.Size(127, 23);
         this.btnQuestRefresh.TabIndex = 6;
         this.btnQuestRefresh.Text = "Получить анкеты";
         this.btnQuestRefresh.UseVisualStyleBackColor = true;
         this.btnQuestRefresh.Click += new System.EventHandler(this.btnQuestRefresh_Click);
         // 
         // clbQuest
         // 
         this.clbQuest.Dock = System.Windows.Forms.DockStyle.Fill;
         this.clbQuest.FormattingEnabled = true;
         this.clbQuest.Location = new System.Drawing.Point(5, 25);
         this.clbQuest.Name = "clbQuest";
         this.clbQuest.Size = new System.Drawing.Size(363, 295);
         this.clbQuest.TabIndex = 7;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.btnCheck,
            this.btnUncheck});
         this.toolStrip1.Location = new System.Drawing.Point(5, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(363, 25);
         this.toolStrip1.TabIndex = 8;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(48, 22);
         this.toolStripLabel1.Text = "Анкеты";
         // 
         // btnCheck
         // 
         this.btnCheck.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnCheck.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheck.Name = "btnCheck";
         this.btnCheck.Size = new System.Drawing.Size(23, 22);
         this.btnCheck.Tag = "true";
         this.btnCheck.Text = "Установить";
         this.btnCheck.Click += new System.EventHandler(this.setChecked);
         // 
         // btnUncheck
         // 
         this.btnUncheck.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUncheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnUncheck.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUncheck.Name = "btnUncheck";
         this.btnUncheck.Size = new System.Drawing.Size(23, 22);
         this.btnUncheck.Tag = "false";
         this.btnUncheck.Text = "Сбросить";
         this.btnUncheck.Click += new System.EventHandler(this.setChecked);
         // 
         // splitContainer1
         // 
         this.splitContainer1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 77);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.clbQuest);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         this.splitContainer1.Panel1.Padding = new System.Windows.Forms.Padding(5, 0, 0, 0);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.clbAgent);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(0, 0, 5, 0);
         this.splitContainer1.Size = new System.Drawing.Size(786, 324);
         this.splitContainer1.SplitterDistance = 372;
         this.splitContainer1.TabIndex = 10;
         // 
         // clbAgent
         // 
         this.clbAgent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.clbAgent.FormattingEnabled = true;
         this.clbAgent.Location = new System.Drawing.Point(0, 25);
         this.clbAgent.Name = "clbAgent";
         this.clbAgent.Size = new System.Drawing.Size(401, 295);
         this.clbAgent.TabIndex = 1;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.lblAgent,
            this.toolStripButton1,
            this.toolStripButton2});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(401, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // lblAgent
         // 
         this.lblAgent.Name = "lblAgent";
         this.lblAgent.Size = new System.Drawing.Size(47, 22);
         this.lblAgent.Text = "Агенты";
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton1.Tag = "true";
         this.toolStripButton1.Text = "Установить";
         this.toolStripButton1.Click += new System.EventHandler(this.setCheckedAgent);
         // 
         // toolStripButton2
         // 
         this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton2.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton2.Name = "toolStripButton2";
         this.toolStripButton2.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton2.Tag = "false";
         this.toolStripButton2.Text = "Сбросить";
         this.toolStripButton2.Click += new System.EventHandler(this.setCheckedAgent);
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.dtpFrom);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Controls.Add(this.btnQuestRefresh);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.cbPhoto);
         this.panel1.Controls.Add(this.dtpTill);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(786, 77);
         this.panel1.TabIndex = 11;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.gbQuestType);
         this.panel2.Controls.Add(this.btnRefresh);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 401);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(786, 41);
         this.panel2.TabIndex = 12;
         // 
         // gbQuestType
         // 
         this.gbQuestType.Controls.Add(this.rbVert);
         this.gbQuestType.Controls.Add(this.rbHor);
         this.gbQuestType.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.gbQuestType.Location = new System.Drawing.Point(136, 0);
         this.gbQuestType.Name = "gbQuestType";
         this.gbQuestType.Size = new System.Drawing.Size(282, 35);
         this.gbQuestType.TabIndex = 6;
         this.gbQuestType.TabStop = false;
         // 
         // rbVert
         // 
         this.rbVert.AutoSize = true;
         this.rbVert.Location = new System.Drawing.Point(121, 13);
         this.rbVert.Name = "rbVert";
         this.rbVert.Size = new System.Drawing.Size(99, 18);
         this.rbVert.TabIndex = 1;
         this.rbVert.Text = "Вертикальный";
         this.rbVert.UseVisualStyleBackColor = true;
         // 
         // rbHor
         // 
         this.rbHor.AutoSize = true;
         this.rbHor.Checked = true;
         this.rbHor.Location = new System.Drawing.Point(7, 13);
         this.rbHor.Name = "rbHor";
         this.rbHor.Size = new System.Drawing.Size(109, 18);
         this.rbHor.TabIndex = 0;
         this.rbHor.TabStop = true;
         this.rbHor.Text = "Горизонтальный";
         this.rbHor.UseVisualStyleBackColor = true;
         // 
         // FmQuestionReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(786, 442);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmQuestionReport";
         this.Text = "Отчет по анкетам";
         this.Load += new System.EventHandler(this.FmQuestionReport_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.gbQuestType.ResumeLayout(false);
         this.gbQuestType.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.Button btnRefresh;
      private System.Windows.Forms.CheckBox cbPhoto;
      private System.Windows.Forms.Button btnQuestRefresh;
      private System.Windows.Forms.CheckedListBox clbQuest;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton btnCheck;
      private System.Windows.Forms.ToolStripButton btnUncheck;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripButton toolStripButton2;
      protected System.Windows.Forms.ToolStripLabel lblAgent;
      public System.Windows.Forms.CheckedListBox clbAgent;
      public System.Windows.Forms.GroupBox gbQuestType;
      public System.Windows.Forms.RadioButton rbVert;
      public System.Windows.Forms.RadioButton rbHor;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmReportRemn
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReportRemn));
         this.button1 = new System.Windows.Forms.Button();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.rbOrg = new System.Windows.Forms.RadioButton();
         this.cbOrgs = new System.Windows.Forms.ComboBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(162, 163);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(41, 118);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(143, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(220, 118);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(143, 20);
         this.dtpFinish.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(16, 121);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 3;
         this.label1.Text = "c";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(195, 121);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 4;
         this.label2.Text = "по";
         // 
         // rbOrg
         // 
         this.rbOrg.AutoSize = true;
         this.rbOrg.Location = new System.Drawing.Point(7, 71);
         this.rbOrg.Name = "rbOrg";
         this.rbOrg.Size = new System.Drawing.Size(104, 18);
         this.rbOrg.TabIndex = 26;
         this.rbOrg.Text = "Торговая точка";
         this.rbOrg.UseVisualStyleBackColor = true;
         this.rbOrg.CheckedChanged += new System.EventHandler(this.rbOrg_CheckedChanged);
         // 
         // cbOrgs
         // 
         this.cbOrgs.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbOrgs.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbOrgs.Enabled = false;
         this.cbOrgs.FormattingEnabled = true;
         this.cbOrgs.Location = new System.Drawing.Point(131, 71);
         this.cbOrgs.Name = "cbOrgs";
         this.cbOrgs.Size = new System.Drawing.Size(214, 22);
         this.cbOrgs.TabIndex = 24;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.cbAgent);
         this.groupBox1.Controls.Add(this.rbAgent);
         this.groupBox1.Controls.Add(this.cbDivision);
         this.groupBox1.Controls.Add(this.rbDivision);
         this.groupBox1.Controls.Add(this.rbOrg);
         this.groupBox1.Controls.Add(this.cbOrgs);
         this.groupBox1.Location = new System.Drawing.Point(12, 3);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(351, 99);
         this.groupBox1.TabIndex = 27;
         this.groupBox1.TabStop = false;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Checked = true;
         this.rbDivision.Location = new System.Drawing.Point(6, 12);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 27;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbOrg_CheckedChanged);
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(131, 12);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(214, 22);
         this.cbDivision.TabIndex = 28;
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(6, 42);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(55, 18);
         this.rbAgent.TabIndex = 29;
         this.rbAgent.TabStop = true;
         this.rbAgent.Text = "Агент";
         this.rbAgent.UseVisualStyleBackColor = true;
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(131, 43);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(214, 22);
         this.cbAgent.TabIndex = 30;
         // 
         // FmReportRemn
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(438, 256);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.button1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportRemn";
         this.Text = "Отчет по посещениям и остаткам";
         this.Load += new System.EventHandler(this.FmReport_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.RadioButton rbOrg;
      private System.Windows.Forms.ComboBox cbOrgs;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmMerchRep
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMerchRep));
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnExcelReport = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.Enabled = false;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(127, 42);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(172, 22);
         this.cbAgents.TabIndex = 38;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(16, 42);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 37;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.rbAll_CheckedChanged);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Checked = true;
         this.rbDivision.Location = new System.Drawing.Point(16, 13);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 36;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbAll_CheckedChanged);
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(127, 102);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(167, 20);
         this.dtpFinish.TabIndex = 31;
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(127, 74);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(167, 20);
         this.dtpStart.TabIndex = 30;
         this.dtpStart.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(31, 79);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 14);
         this.label2.TabIndex = 29;
         this.label2.Text = "Дата";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(127, 13);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(172, 22);
         this.cbDivisions.TabIndex = 28;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnExcelReport);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 136);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(315, 48);
         this.panel1.TabIndex = 39;
         // 
         // btnExcelReport
         // 
         this.btnExcelReport.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnExcelReport.Location = new System.Drawing.Point(224, 14);
         this.btnExcelReport.Name = "btnExcelReport";
         this.btnExcelReport.Size = new System.Drawing.Size(75, 25);
         this.btnExcelReport.TabIndex = 36;
         this.btnExcelReport.Text = "Excel";
         this.btnExcelReport.UseVisualStyleBackColor = true;
         this.btnExcelReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmMerchRep
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(315, 184);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDivisions);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMerchRep";
         this.Text = "Отчет по мерчендайзингу";
         this.Load += new System.EventHandler(this.FmMerchRep_Load);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      protected System.Windows.Forms.DateTimePicker dtpFinish;
      protected System.Windows.Forms.DateTimePicker dtpStart;
      protected System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.Panel panel1;
      protected System.Windows.Forms.Button btnExcelReport;
   }
}
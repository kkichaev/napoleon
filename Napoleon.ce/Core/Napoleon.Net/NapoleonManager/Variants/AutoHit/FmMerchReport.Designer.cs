namespace GRSoft.NapoleonManager
{
   partial class FmMerchReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMerchReport));
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.cbPeriod = new System.Windows.Forms.CheckBox();
         this.btnExcelReport = new System.Windows.Forms.Button();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(123, 12);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(166, 21);
         this.cbDivisions.TabIndex = 0;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(27, 73);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Дата";
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(123, 69);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 3;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Enabled = false;
         this.dtpEnd.Location = new System.Drawing.Point(123, 95);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(167, 20);
         this.dtpEnd.TabIndex = 4;
         // 
         // cbPeriod
         // 
         this.cbPeriod.AutoSize = true;
         this.cbPeriod.Location = new System.Drawing.Point(12, 98);
         this.cbPeriod.Name = "cbPeriod";
         this.cbPeriod.Size = new System.Drawing.Size(77, 17);
         this.cbPeriod.TabIndex = 5;
         this.cbPeriod.Text = "за период";
         this.cbPeriod.UseVisualStyleBackColor = true;
         this.cbPeriod.CheckedChanged += new System.EventHandler(this.cbPeriod_CheckedChanged);
         // 
         // btnExcelReport
         // 
         this.btnExcelReport.Location = new System.Drawing.Point(214, 130);
         this.btnExcelReport.Name = "btnExcelReport";
         this.btnExcelReport.Size = new System.Drawing.Size(75, 23);
         this.btnExcelReport.TabIndex = 6;
         this.btnExcelReport.Text = "Excel";
         this.btnExcelReport.UseVisualStyleBackColor = true;
         this.btnExcelReport.Click += new System.EventHandler(this.btnExcelReport_Click);
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(12, 39);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(54, 17);
         this.rbAgents.TabIndex = 24;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.Click += new System.EventHandler(this.rbAgents_Click);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Checked = true;
         this.rbDivision.Location = new System.Drawing.Point(12, 12);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(105, 17);
         this.rbDivision.TabIndex = 23;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.Click += new System.EventHandler(this.rbDivision_Click);
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(123, 39);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(166, 21);
         this.cbAgents.TabIndex = 25;
         // 
         // FmMerchReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(321, 165);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.btnExcelReport);
         this.Controls.Add(this.cbPeriod);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDivisions);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMerchReport";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Отчет по мерчендайзингу";
         this.Load += new System.EventHandler(this.FmMerchReport_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmMerchReport_FormClosed);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.CheckBox cbPeriod;
      private System.Windows.Forms.Button btnExcelReport;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbAgents;
   }
}
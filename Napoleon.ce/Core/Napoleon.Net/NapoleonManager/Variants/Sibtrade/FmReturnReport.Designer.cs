namespace GRSoft.NapoleonManager
{
   partial class FmReturnReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReturnReport));
         this.dpvReport = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.btnExcelReport = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // dpvReport
         // 
         this.dpvReport.Finish = new System.DateTime(2015, 10, 30, 0, 0, 0, 0);
         this.dpvReport.Location = new System.Drawing.Point(12, 12);
         this.dpvReport.Name = "dpvReport";
         this.dpvReport.Size = new System.Drawing.Size(367, 27);
         this.dpvReport.Start = new System.DateTime(2015, 10, 30, 0, 0, 0, 0);
         this.dpvReport.TabIndex = 0;
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(134, 89);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(245, 21);
         this.cbAgents.TabIndex = 29;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Checked = true;
         this.rbAgents.Location = new System.Drawing.Point(23, 89);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(54, 17);
         this.rbAgents.TabIndex = 28;
         this.rbAgents.TabStop = true;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.Click += new System.EventHandler(this.rbAgents_Click);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(23, 62);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(105, 17);
         this.rbDivision.TabIndex = 27;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.Click += new System.EventHandler(this.rbDivision_Click);
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(134, 62);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(245, 21);
         this.cbDivisions.TabIndex = 26;
         // 
         // btnExcelReport
         // 
         this.btnExcelReport.Location = new System.Drawing.Point(167, 143);
         this.btnExcelReport.Name = "btnExcelReport";
         this.btnExcelReport.Size = new System.Drawing.Size(75, 23);
         this.btnExcelReport.TabIndex = 30;
         this.btnExcelReport.Text = "Excel";
         this.btnExcelReport.UseVisualStyleBackColor = true;
         this.btnExcelReport.Click += new System.EventHandler(this.btnExcelReport_Click);
         // 
         // FmReturnReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(418, 183);
         this.Controls.Add(this.btnExcelReport);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.dpvReport);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReturnReport";
         this.Text = "Отчет по возвратам";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpvReport;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbDivisions;
      protected System.Windows.Forms.Button btnExcelReport;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmAuditReportParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReportParams));
         this.btnOK = new System.Windows.Forms.Button();
         this.datePeriodView1 = new GRSoft.NapoleonManager.DatePeriodView();
         this.label1 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(145, 91);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 3;
         this.btnOK.Text = "Excel";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Finish = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(12, 58);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(367, 27);
         this.datePeriodView1.Start = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(16, 23);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 4;
         this.label1.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(63, 20);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(316, 22);
         this.cbAgents.TabIndex = 5;
         // 
         // FmReportParams
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(393, 148);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.datePeriodView1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportParams";
         this.Text = "Отчет по аудиту оборудования";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmAuditReportParams_FormClosing);
         this.Load += new System.EventHandler(this.FmAuditReportParams_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView datePeriodView1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbAgents;
   }
}
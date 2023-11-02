namespace GRSoft.NapoleonManager
{
   partial class FmDistrReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrReport));
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnReport = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpDailyReport = new System.Windows.Forms.DateTimePicker();
         this.cbPeriodReports = new System.Windows.Forms.CheckBox();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnReport);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 97);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(399, 44);
         this.panel2.TabIndex = 5;
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(304, 9);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 0;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 3, 25, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(12, 51);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 25);
         this.dpv.Start = new System.DateTime(2015, 3, 25, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 112);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(99, 14);
         this.label1.TabIndex = 6;
         this.label1.Text = "Дневной отчет за";
         this.label1.Visible = false;
         // 
         // dtpDailyReport
         // 
         this.dtpDailyReport.Location = new System.Drawing.Point(118, 109);
         this.dtpDailyReport.Name = "dtpDailyReport";
         this.dtpDailyReport.Size = new System.Drawing.Size(139, 20);
         this.dtpDailyReport.TabIndex = 7;
         this.dtpDailyReport.Visible = false;
         // 
         // cbPeriodReports
         // 
         this.cbPeriodReports.AutoSize = true;
         this.cbPeriodReports.Checked = true;
         this.cbPeriodReports.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPeriodReports.Location = new System.Drawing.Point(12, 26);
         this.cbPeriodReports.Name = "cbPeriodReports";
         this.cbPeriodReports.Size = new System.Drawing.Size(145, 18);
         this.cbPeriodReports.TabIndex = 8;
         this.cbPeriodReports.Text = "Периодические отчеты";
         this.cbPeriodReports.UseVisualStyleBackColor = true;
         this.cbPeriodReports.Visible = false;
         this.cbPeriodReports.CheckedChanged += new System.EventHandler(this.cbPeriodReports_CheckedChanged);
         // 
         // FmDistrReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(399, 141);
         this.Controls.Add(this.cbPeriodReports);
         this.Controls.Add(this.dtpDailyReport);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.dpv);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrReport";
         this.Text = "Отчет о дистрибуции";
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpDailyReport;
      private System.Windows.Forms.CheckBox cbPeriodReports;
   }
}
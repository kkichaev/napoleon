namespace GRSoft.Ads
{
   partial class FmGsmReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGsmReport));
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbBrigade = new System.Windows.Forms.ComboBox();
         this.Label1 = new System.Windows.Forms.Label();
         this.btnReport = new System.Windows.Forms.Button();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(20, 27);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(146, 20);
         this.dtpBegin.TabIndex = 0;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(20, 53);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(146, 20);
         this.dtpEnd.TabIndex = 1;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.dtpBegin);
         this.groupBox1.Controls.Add(this.dtpEnd);
         this.groupBox1.Location = new System.Drawing.Point(12, 12);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(200, 100);
         this.groupBox1.TabIndex = 2;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Период";
         // 
         // cbBrigade
         // 
         this.cbBrigade.FormattingEnabled = true;
         this.cbBrigade.Location = new System.Drawing.Point(239, 38);
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(191, 21);
         this.cbBrigade.TabIndex = 3;
         // 
         // Label1
         // 
         this.Label1.AutoSize = true;
         this.Label1.Location = new System.Drawing.Point(236, 12);
         this.Label1.Name = "Label1";
         this.Label1.Size = new System.Drawing.Size(49, 13);
         this.Label1.TabIndex = 4;
         this.Label1.Text = "Бригада";
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(303, 78);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 5;
         this.btnReport.Text = "HTML";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmGsmReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(473, 137);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.Label1);
         this.Controls.Add(this.cbBrigade);
         this.Controls.Add(this.groupBox1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGsmReport";
         this.Text = "Отчет по ГСМ";
         this.Load += new System.EventHandler(this.FmGsmReport_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmGsmReport_FormClosed);
         this.groupBox1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.ComboBox cbBrigade;
      private System.Windows.Forms.Label Label1;
      private System.Windows.Forms.Button btnReport;
   }
}
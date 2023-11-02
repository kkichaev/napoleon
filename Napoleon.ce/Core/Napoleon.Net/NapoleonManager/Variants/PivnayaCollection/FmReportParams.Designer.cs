namespace GRSoft.NapoleonManager
{
   partial class FmReportParams
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
         this.SuspendLayout();
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(145, 45);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 3;
         this.btnOK.Text = "Excel";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Finish = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(12, 12);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(367, 27);
         this.datePeriodView1.Start = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 2;
         // 
         // FmReportParams
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(393, 75);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.datePeriodView1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportParams";
         this.Text = "Отчет по возвратным кегам";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmVisitReportParams_FormClosing);
         this.Load += new System.EventHandler(this.FmVisitReportParams_Load);
         this.ResumeLayout(false);

      }

      #endregion

      private DatePeriodView datePeriodView1;
      private System.Windows.Forms.Button btnOK;
   }
}
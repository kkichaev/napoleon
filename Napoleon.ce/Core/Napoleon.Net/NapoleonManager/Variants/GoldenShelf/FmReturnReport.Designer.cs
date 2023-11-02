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
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.btnReport = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 4, 8, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(7, 12);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2015, 4, 8, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(144, 56);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 1;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmReturnReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(379, 86);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.dpv);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReturnReport";
         this.Text = "Отчет по возвратам";
         this.ResumeLayout(false);

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Button btnReport;
   }
}
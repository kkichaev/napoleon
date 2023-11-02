namespace GRSoft.NapoleonManager
{
   partial class FmStartWorkReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmStartWorkReport));
         this.label1 = new System.Windows.Forms.Label();
         this.date = new System.Windows.Forms.DateTimePicker();
         this.btnReport = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(17, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(33, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Дата";
         // 
         // date
         // 
         this.date.Location = new System.Drawing.Point(56, 12);
         this.date.Name = "date";
         this.date.Size = new System.Drawing.Size(138, 20);
         this.date.TabIndex = 1;
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(45, 52);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 2;
         this.btnReport.Text = "Отчет";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmStartWorkReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(393, 115);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.date);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmStartWorkReport";
         this.Text = "Нарушение регламента работы";
         this.Load += new System.EventHandler(this.FmStartWorkReport_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker date;
      private System.Windows.Forms.Button btnReport;
   }
}
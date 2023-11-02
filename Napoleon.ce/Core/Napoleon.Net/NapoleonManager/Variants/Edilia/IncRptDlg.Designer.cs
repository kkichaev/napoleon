namespace GRSoft.NapoleonManager
{
   partial class IncRptDlg
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(IncRptDlg));
         this.btnExcel = new System.Windows.Forms.Button();
         this.datePeriodView1 = new GRSoft.NapoleonManager.DatePeriodView();
         this.SuspendLayout();
         // 
         // btnExcel
         // 
         this.btnExcel.ImageAlign = System.Drawing.ContentAlignment.MiddleLeft;
         this.btnExcel.Location = new System.Drawing.Point(158, 45);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 1;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Finish = new System.DateTime(2015, 11, 26, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(12, 12);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(367, 27);
         this.datePeriodView1.Start = new System.DateTime(2015, 11, 26, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 0;
         // 
         // IncRptDlg
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(394, 73);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.datePeriodView1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "IncRptDlg";
         this.Text = "Отчет по поступлению денежных средств";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Button btnExcel;
      public DatePeriodView datePeriodView1;
   }
}
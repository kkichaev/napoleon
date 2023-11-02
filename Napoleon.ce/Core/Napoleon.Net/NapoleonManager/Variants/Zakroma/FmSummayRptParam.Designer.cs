namespace GRSoft.NapoleonManager
{
   partial class FmSummayRptParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSummayRptParam));
         this.label1 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.btnReport = new System.Windows.Forms.Button();
         this.listBox = new System.Windows.Forms.CheckedListBox();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(5, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(71, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Дата начала";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(100, 7);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(143, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(5, 38);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(89, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Дата окончания";
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(100, 36);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(143, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // btnReport
         // 
         this.btnReport.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnReport.Location = new System.Drawing.Point(86, 327);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 4;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         // 
         // listBox
         // 
         this.listBox.FormattingEnabled = true;
         this.listBox.Location = new System.Drawing.Point(8, 90);
         this.listBox.Name = "listBox";
         this.listBox.Size = new System.Drawing.Size(235, 229);
         this.listBox.TabIndex = 6;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(5, 68);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(87, 13);
         this.label3.TabIndex = 7;
         this.label3.Text = "Подразделение";
         // 
         // FmSummayRptParam
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(253, 354);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.listBox);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSummayRptParam";
         this.Text = "Итоговый отчет";
         this.Load += new System.EventHandler(this.FmSummayRptParam_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.CheckedListBox listBox;
      private System.Windows.Forms.Label label3;
   }
}
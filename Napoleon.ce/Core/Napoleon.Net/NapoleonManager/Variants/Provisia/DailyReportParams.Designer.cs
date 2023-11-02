namespace GRSoft.NapoleonManager
{
   partial class DailyReportParams
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
         this.label1 = new System.Windows.Forms.Label();
         this.dateFrom = new System.Windows.Forms.DateTimePicker();
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(41, 14);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(33, 13);
         this.label1.TabIndex = 12;
         this.label1.Text = "Дата";
         // 
         // dateFrom
         // 
         this.dateFrom.Location = new System.Drawing.Point(80, 12);
         this.dateFrom.Name = "dateFrom";
         this.dateFrom.Size = new System.Drawing.Size(144, 20);
         this.dateFrom.TabIndex = 0;
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(173, 50);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 9;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(92, 50);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 8;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // DailyReportParams
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.cancel;
         this.ClientSize = new System.Drawing.Size(260, 85);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dateFrom);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Name = "DailyReportParams";
         this.Text = "Параметры отчета";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dateFrom;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;

   }
}
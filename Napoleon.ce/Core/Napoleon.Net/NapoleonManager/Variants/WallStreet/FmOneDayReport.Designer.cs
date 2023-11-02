namespace GRSoft.NapoleonManager
{
   partial class FmOneDayReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOneDayReport));
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.btnExcel = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(12, 24);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(200, 22);
         this.cbAgent.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Агент";
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(12, 68);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(200, 20);
         this.dtpDate.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 53);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "Дата";
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(71, 107);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 4;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // FmOneDayReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(226, 135);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbAgent);
         this.Font = new System.Drawing.Font("Arial", 8.25F);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmOneDayReport";
         this.Text = "Дневной отчет";
         this.Load += new System.EventHandler(this.FmOneDayReport_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnExcel;
   }
}
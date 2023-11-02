namespace GRSoft.NapoleonManager
{
   partial class FmOrdVztRep
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrdVztRep));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.btnReport = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(33, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(16, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "с:";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(193, 9);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(22, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "по:";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(56, 6);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(137, 20);
         this.dtpStart.TabIndex = 2;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(221, 6);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(154, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 43);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(37, 14);
         this.label3.TabIndex = 4;
         this.label3.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(55, 40);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(320, 22);
         this.cbAgents.TabIndex = 5;
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(160, 87);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 6;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmOrdVztRep
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(400, 124);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrdVztRep";
         this.Text = "Отчёт о посещениях и заказах.";
         this.Load += new System.EventHandler(this.FmOrdVztRep_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Button btnReport;
   }
}
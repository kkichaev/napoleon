namespace GRSoft.NapoleonManager
{
   partial class FmFaceReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFaceReport));
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.btnAgentRpt = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(45, 6);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(129, 20);
         this.dtpStart.TabIndex = 0;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(45, 32);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(129, 20);
         this.dtpFinish.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 35);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "по";
         // 
         // btnAgentRpt
         // 
         this.btnAgentRpt.Location = new System.Drawing.Point(78, 79);
         this.btnAgentRpt.Name = "btnAgentRpt";
         this.btnAgentRpt.Size = new System.Drawing.Size(75, 23);
         this.btnAgentRpt.TabIndex = 4;
         this.btnAgentRpt.Text = "Excel";
         this.btnAgentRpt.UseVisualStyleBackColor = true;
         this.btnAgentRpt.Click += new System.EventHandler(this.btnAgentRpt_Click);
         // 
         // FmFaceReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(228, 114);
         this.Controls.Add(this.btnAgentRpt);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmFaceReport";
         this.Text = "Остатки - отчеты";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnAgentRpt;

   }
}
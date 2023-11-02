namespace GRSoft.NapoleonManager
{
   partial class FmGPSReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGPSReport));
         this.label1 = new System.Windows.Forms.Label();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.btnExcel = new System.Windows.Forms.Button();
         this.btnGpx = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(34, 17);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Агент";
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(75, 13);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(121, 22);
         this.cbAgent.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(18, 54);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(53, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Период с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 90);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(59, 14);
         this.label3.TabIndex = 3;
         this.label3.Text = "Период по";
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(75, 51);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(135, 20);
         this.dtpFrom.TabIndex = 4;
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(75, 87);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(135, 20);
         this.dtpTill.TabIndex = 5;
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(15, 123);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 0;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // btnGpx
         // 
         this.btnGpx.Location = new System.Drawing.Point(135, 123);
         this.btnGpx.Name = "btnGpx";
         this.btnGpx.Size = new System.Drawing.Size(75, 23);
         this.btnGpx.TabIndex = 6;
         this.btnGpx.Text = "GPX";
         this.btnGpx.UseVisualStyleBackColor = true;
         this.btnGpx.Click += new System.EventHandler(this.btnGpx_Click);
         // 
         // FmGPSReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(236, 158);
         this.Controls.Add(this.btnGpx);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dtpTill);
         this.Controls.Add(this.dtpFrom);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbAgent);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGPSReport";
         this.Text = "Трек";
         this.Load += new System.EventHandler(this.FmAgentGPSReport_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.Button btnGpx;
   }
}
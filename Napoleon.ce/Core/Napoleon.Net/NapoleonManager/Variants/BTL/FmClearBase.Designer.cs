namespace GRSoft.NapoleonManager
{
   partial class FmClearBase
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClearBase));
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.btnStart = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(24, 16);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(53, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Период с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(18, 54);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(59, 14);
         this.label3.TabIndex = 3;
         this.label3.Text = "Период по";
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(85, 12);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(136, 20);
         this.dtpFrom.TabIndex = 4;
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(85, 50);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(136, 20);
         this.dtpTill.TabIndex = 5;
         // 
         // btnStart
         // 
         this.btnStart.Location = new System.Drawing.Point(85, 87);
         this.btnStart.Name = "btnStart";
         this.btnStart.Size = new System.Drawing.Size(75, 23);
         this.btnStart.TabIndex = 6;
         this.btnStart.Text = "Выполнить";
         this.btnStart.UseVisualStyleBackColor = true;
         this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
         // 
         // FmClearBase
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(250, 120);
         this.Controls.Add(this.btnStart);
         this.Controls.Add(this.dtpTill);
         this.Controls.Add(this.dtpFrom);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClearBase";
         this.Text = "Удалить";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.Button btnStart;
   }
}
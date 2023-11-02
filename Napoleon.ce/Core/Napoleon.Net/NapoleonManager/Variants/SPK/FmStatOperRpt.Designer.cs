namespace GRSoft.NapoleonManager
{
   partial class FmStatOperRpt
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmStatOperRpt));
         this.btnExcel = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.SuspendLayout();
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(151, 59);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(100, 23);
         this.btnExcel.TabIndex = 5;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(11, 12);
         this.dpv.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(381, 31);
         this.dpv.Start = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.TabIndex = 4;
         // 
         // FmStatOperRpt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(403, 94);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dpv);
         this.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmStatOperRpt";
         this.Text = "Статистика по работе операторов";
         this.ResumeLayout(false);

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Button btnExcel;
   }
}
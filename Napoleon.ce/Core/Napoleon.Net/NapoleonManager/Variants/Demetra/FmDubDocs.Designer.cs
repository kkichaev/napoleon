namespace GRSoft.NapoleonManager
{
   partial class FmDubDocs
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDubDocs));
         this.btnStart = new System.Windows.Forms.Button();
         this.period = new GRSoft.NapoleonManager.DatePeriodView();
         this.SuspendLayout();
         // 
         // btnStart
         // 
         this.btnStart.Location = new System.Drawing.Point(165, 42);
         this.btnStart.Name = "btnStart";
         this.btnStart.Size = new System.Drawing.Size(75, 23);
         this.btnStart.TabIndex = 1;
         this.btnStart.Text = "Выгрузить";
         this.btnStart.UseVisualStyleBackColor = true;
         this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
         // 
         // period
         // 
         this.period.Finish = new System.DateTime(2015, 5, 18, 0, 0, 0, 0);
         this.period.Location = new System.Drawing.Point(12, 3);
         this.period.Name = "period";
         this.period.Size = new System.Drawing.Size(367, 27);
         this.period.Start = new System.DateTime(2015, 5, 18, 0, 0, 0, 0);
         this.period.TabIndex = 0;
         // 
         // FmDubDocs
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(400, 77);
         this.Controls.Add(this.btnStart);
         this.Controls.Add(this.period);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDubDocs";
         this.Text = "Выгрузка документов";
         this.ResumeLayout(false);

      }

      #endregion

      private DatePeriodView period;
      private System.Windows.Forms.Button btnStart;
   }
}
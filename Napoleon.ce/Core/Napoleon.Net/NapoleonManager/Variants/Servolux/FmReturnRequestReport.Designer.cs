namespace GRSoft.NapoleonManager
{
   partial class FmReturnRequestReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReturnRequestReport));
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2016, 8, 23, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(28, 22);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2016, 8, 23, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button1.Location = new System.Drawing.Point(176, 91);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 1;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmReturnRequestReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(442, 128);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.dpv);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReturnRequestReport";
         this.Text = "Отчет по возвратам";
         this.ResumeLayout(false);

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Button button1;
   }
}
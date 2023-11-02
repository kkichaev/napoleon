namespace GRSoft.NapoleonManager
{
   partial class FmMonitoringRep
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMonitoringRep));
         this.button1 = new System.Windows.Forms.Button();
         this.dtStart = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtEnd = new System.Windows.Forms.DateTimePicker();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(93, 116);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // dtStart
         // 
         this.dtStart.Location = new System.Drawing.Point(81, 32);
         this.dtStart.Name = "dtStart";
         this.dtStart.Size = new System.Drawing.Size(130, 20);
         this.dtStart.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(58, 36);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(52, 72);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 4;
         this.label2.Text = "по";
         // 
         // dtEnd
         // 
         this.dtEnd.Location = new System.Drawing.Point(81, 68);
         this.dtEnd.Name = "dtEnd";
         this.dtEnd.Size = new System.Drawing.Size(130, 20);
         this.dtEnd.TabIndex = 3;
         // 
         // FmMonitoringRep
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(268, 155);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtEnd);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtStart);
         this.Controls.Add(this.button1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMonitoringRep";
         this.Text = "Мониторинг цен";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.DateTimePicker dtStart;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtEnd;
   }
}
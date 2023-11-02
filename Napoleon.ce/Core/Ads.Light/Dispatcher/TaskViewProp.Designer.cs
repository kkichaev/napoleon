namespace GRSoft.Ads.Dispatcher
{
   partial class TaskViewProp
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
         this.cbAddress = new System.Windows.Forms.CheckBox();
         this.cbName = new System.Windows.Forms.CheckBox();
         this.cbText = new System.Windows.Forms.CheckBox();
         this.SuspendLayout();
         // 
         // cbAddress
         // 
         this.cbAddress.AutoSize = true;
         this.cbAddress.Location = new System.Drawing.Point(12, 12);
         this.cbAddress.Name = "cbAddress";
         this.cbAddress.Size = new System.Drawing.Size(58, 18);
         this.cbAddress.TabIndex = 0;
         this.cbAddress.Text = "Адрес";
         this.cbAddress.UseVisualStyleBackColor = true;
         // 
         // cbName
         // 
         this.cbName.AutoSize = true;
         this.cbName.Location = new System.Drawing.Point(12, 36);
         this.cbName.Name = "cbName";
         this.cbName.Size = new System.Drawing.Size(49, 18);
         this.cbName.TabIndex = 1;
         this.cbName.Text = "Имя";
         this.cbName.UseVisualStyleBackColor = true;
         // 
         // cbText
         // 
         this.cbText.AutoSize = true;
         this.cbText.Location = new System.Drawing.Point(12, 60);
         this.cbText.Name = "cbText";
         this.cbText.Size = new System.Drawing.Size(55, 18);
         this.cbText.TabIndex = 2;
         this.cbText.Text = "Текст";
         this.cbText.UseVisualStyleBackColor = true;
         // 
         // TaskViewProp
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(292, 286);
         this.Controls.Add(this.cbText);
         this.Controls.Add(this.cbName);
         this.Controls.Add(this.cbAddress);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Name = "TaskViewProp";
         this.Text = "TaskViewProp";
         this.Load += new System.EventHandler(this.TaskViewProp_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.TaskViewProp_FormClosed);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.CheckBox cbAddress;
      private System.Windows.Forms.CheckBox cbName;
      private System.Windows.Forms.CheckBox cbText;
   }
}
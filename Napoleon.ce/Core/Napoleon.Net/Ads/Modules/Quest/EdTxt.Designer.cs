namespace GRSoft.NapoleonManager
{
   partial class EdTxt
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.label1 = new System.Windows.Forms.Label();
         this.tbValue = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Dock = System.Windows.Forms.DockStyle.Top;
         this.label1.Location = new System.Drawing.Point(0, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(56, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Значение";
         // 
         // tbValue
         // 
         this.tbValue.BackColor = System.Drawing.Color.White;
         this.tbValue.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbValue.Location = new System.Drawing.Point(0, 14);
         this.tbValue.Multiline = true;
         this.tbValue.Name = "tbValue";
         this.tbValue.ReadOnly = true;
         this.tbValue.Size = new System.Drawing.Size(365, 148);
         this.tbValue.TabIndex = 1;
         this.tbValue.Text = "Текстовое значение, которое вводит пользователь.";
         // 
         // EdTxt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tbValue);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Name = "EdTxt";
         this.Size = new System.Drawing.Size(365, 162);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbValue;
   }
}

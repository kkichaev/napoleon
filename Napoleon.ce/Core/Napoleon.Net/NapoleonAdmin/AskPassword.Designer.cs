namespace GRSoft.NapoleonAdmin
{
   partial class AskPassword
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
         this.password = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // password
         // 
         this.password.Location = new System.Drawing.Point(12, 32);
         this.password.Name = "password";
         this.password.Size = new System.Drawing.Size(257, 20);
         this.password.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(175, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Введите пароль администратора";
         // 
         // ok
         // 
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(194, 64);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 2;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // AskPassword
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(284, 99);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.password);
         this.Name = "AskPassword";
         this.Text = "Пароль администратора";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox password;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button ok;
   }
}
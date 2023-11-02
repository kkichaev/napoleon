namespace GRSoft.Ads
{
   partial class Login
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
         this.label1 = new System.Windows.Forms.Label();
         this.user = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.password = new System.Windows.Forms.TextBox();
         this.rememberPwd = new System.Windows.Forms.CheckBox();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(32, 22);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(29, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Имя";
         // 
         // user
         // 
         this.user.Location = new System.Drawing.Point(67, 19);
         this.user.Name = "user";
         this.user.Size = new System.Drawing.Size(179, 20);
         this.user.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(16, 50);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(45, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Пароль";
         // 
         // password
         // 
         this.password.Location = new System.Drawing.Point(67, 47);
         this.password.Name = "password";
         this.password.Size = new System.Drawing.Size(179, 20);
         this.password.TabIndex = 3;
         // 
         // rememberPwd
         // 
         this.rememberPwd.AutoSize = true;
         this.rememberPwd.Location = new System.Drawing.Point(67, 75);
         this.rememberPwd.Name = "rememberPwd";
         this.rememberPwd.Size = new System.Drawing.Size(121, 17);
         this.rememberPwd.TabIndex = 4;
         this.rememberPwd.Text = "Запомнить пароль";
         this.rememberPwd.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(101, 106);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 5;
         this.ok.Text = "Войти";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // Login
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(277, 141);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.rememberPwd);
         this.Controls.Add(this.password);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.user);
         this.Controls.Add(this.label1);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
         this.Name = "Login";
         this.Text = "Введите имя и пароль";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox user;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox password;
      private System.Windows.Forms.CheckBox rememberPwd;
      private System.Windows.Forms.Button ok;
   }
}
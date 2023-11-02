namespace GRSoft.NapoleonManager
{
   partial class ChangePassword
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
         this.currPwd = new System.Windows.Forms.TextBox();
         this.newPwd = new System.Windows.Forms.TextBox();
         this.newPwdCheck = new System.Windows.Forms.TextBox();
         this.ok = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // currPwd
         // 
         this.currPwd.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.currPwd.Location = new System.Drawing.Point(109, 12);
         this.currPwd.Name = "currPwd";
         this.currPwd.PasswordChar = '*';
         this.currPwd.Size = new System.Drawing.Size(178, 20);
         this.currPwd.TabIndex = 0;
         // 
         // newPwd
         // 
         this.newPwd.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.newPwd.Location = new System.Drawing.Point(109, 43);
         this.newPwd.Name = "newPwd";
         this.newPwd.PasswordChar = '*';
         this.newPwd.Size = new System.Drawing.Size(178, 20);
         this.newPwd.TabIndex = 1;
         // 
         // newPwdCheck
         // 
         this.newPwdCheck.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.newPwdCheck.Location = new System.Drawing.Point(109, 73);
         this.newPwdCheck.Name = "newPwdCheck";
         this.newPwdCheck.PasswordChar = '*';
         this.newPwdCheck.Size = new System.Drawing.Size(178, 20);
         this.newPwdCheck.TabIndex = 2;
         // 
         // ok
         // 
         this.ok.Location = new System.Drawing.Point(112, 107);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 3;
         this.ok.Text = "Сохранить";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(91, 13);
         this.label1.TabIndex = 4;
         this.label1.Text = "Текущий пароль";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 46);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(80, 13);
         this.label2.TabIndex = 5;
         this.label2.Text = "Новый пароль";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 76);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(83, 13);
         this.label3.TabIndex = 6;
         this.label3.Text = "Повтор пароля";
         // 
         // ChangePassword
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(299, 142);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.newPwdCheck);
         this.Controls.Add(this.newPwd);
         this.Controls.Add(this.currPwd);
         this.Name = "ChangePassword";
         this.Text = "Сменить пароль";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox currPwd;
      private System.Windows.Forms.TextBox newPwd;
      private System.Windows.Forms.TextBox newPwdCheck;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
   }
}
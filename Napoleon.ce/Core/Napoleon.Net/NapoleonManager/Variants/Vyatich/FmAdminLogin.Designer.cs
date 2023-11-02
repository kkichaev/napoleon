namespace GRSoft.NapoleonManager
{
   partial class FmAdminLogin
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAdminLogin));
         this.ok = new System.Windows.Forms.Button();
         this.password = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.Location = new System.Drawing.Point(127, 68);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 8;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // password
         // 
         this.password.Location = new System.Drawing.Point(86, 25);
         this.password.Name = "password";
         this.password.PasswordChar = '*';
         this.password.Size = new System.Drawing.Size(201, 20);
         this.password.TabIndex = 7;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(35, 28);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(45, 13);
         this.label2.TabIndex = 6;
         this.label2.Text = "Пароль";
         // 
         // FmAdminLogin
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(328, 111);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.password);
         this.Controls.Add(this.label2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAdminLogin";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Пароль администратора";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.TextBox password;
      private System.Windows.Forms.Label label2;
   }
}
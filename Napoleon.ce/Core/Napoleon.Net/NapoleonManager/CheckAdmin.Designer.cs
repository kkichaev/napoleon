namespace GRSoft.NapoleonManager
{
   partial class CheckAdmin
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(CheckAdmin));
         this.label1 = new System.Windows.Forms.Label();
         this.password = new System.Windows.Forms.TextBox();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(18, 31);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(45, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Пароль";
         // 
         // password
         // 
         this.password.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.password.Location = new System.Drawing.Point(66, 29);
         this.password.Name = "password";
         this.password.Size = new System.Drawing.Size(252, 20);
         this.password.TabIndex = 1;
         this.password.UseSystemPasswordChar = true;
         this.password.KeyDown += new System.Windows.Forms.KeyEventHandler(this.password_KeyDown);
         // 
         // ok
         // 
         this.ok.Location = new System.Drawing.Point(130, 68);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 2;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // CheckAdmin
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(342, 103);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.password);
         this.Controls.Add(this.label1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "CheckAdmin";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Введите пароль";
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.CheckAdmin_KeyDown);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox password;
      private System.Windows.Forms.Button ok;
   }
}
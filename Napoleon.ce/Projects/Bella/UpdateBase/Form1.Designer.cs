namespace UpdateBase
{
   partial class Form1
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
         this.port = new System.Windows.Forms.TextBox();
         this.IP = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.login = new System.Windows.Forms.TextBox();
         this.password = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // port
         // 
         this.port.Location = new System.Drawing.Point(94, 58);
         this.port.Name = "port";
         this.port.Size = new System.Drawing.Size(81, 20);
         this.port.TabIndex = 9;
         // 
         // IP
         // 
         this.IP.Location = new System.Drawing.Point(94, 32);
         this.IP.Name = "IP";
         this.IP.Size = new System.Drawing.Size(187, 20);
         this.IP.TabIndex = 8;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(67, 35);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(17, 13);
         this.label1.TabIndex = 6;
         this.label1.Text = "IP";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(54, 61);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(30, 13);
         this.label2.TabIndex = 7;
         this.label2.Text = "порт";
         // 
         // login
         // 
         this.login.Location = new System.Drawing.Point(94, 97);
         this.login.Name = "login";
         this.login.Size = new System.Drawing.Size(187, 20);
         this.login.TabIndex = 10;
         // 
         // password
         // 
         this.password.Location = new System.Drawing.Point(94, 125);
         this.password.Name = "password";
         this.password.Size = new System.Drawing.Size(187, 20);
         this.password.TabIndex = 11;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(49, 100);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(36, 13);
         this.label3.TabIndex = 12;
         this.label3.Text = "логин";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(48, 128);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(43, 13);
         this.label4.TabIndex = 13;
         this.label4.Text = "пароль";
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(130, 172);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 14;
         this.button1.Text = "Обмен";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // Form1
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(357, 214);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.password);
         this.Controls.Add(this.login);
         this.Controls.Add(this.port);
         this.Controls.Add(this.IP);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.label2);
         this.Name = "Form1";
         this.Text = "Обмен по FTP";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox port;
      private System.Windows.Forms.TextBox IP;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox login;
      private System.Windows.Forms.TextBox password;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Button button1;
   }
}


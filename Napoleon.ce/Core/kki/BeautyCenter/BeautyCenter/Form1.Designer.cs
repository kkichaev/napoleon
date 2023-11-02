namespace BeautyCenter
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
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
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
         this.btnConnect = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.tbPassword = new System.Windows.Forms.TextBox();
         this.cbRoles = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // btnConnect
         // 
         this.btnConnect.Location = new System.Drawing.Point(251, 228);
         this.btnConnect.Name = "btnConnect";
         this.btnConnect.Size = new System.Drawing.Size(75, 23);
         this.btnConnect.TabIndex = 0;
         this.btnConnect.Text = "Войти";
         this.btnConnect.UseVisualStyleBackColor = true;
         this.btnConnect.Click += new System.EventHandler(this.button1_Click);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(271, 42);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(260, 15);
         this.label1.TabIndex = 1;
         this.label1.Text = "Добро пожаловать в систему салона красоты";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(33, 78);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(375, 15);
         this.label2.TabIndex = 2;
         this.label2.Text = "Для продолжения работы вам необходимо ввести логин и пароль";
         // 
         // tbLogin
         // 
         this.tbLogin.Location = new System.Drawing.Point(33, 161);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(293, 23);
         this.tbLogin.TabIndex = 3;
         // 
         // tbPassword
         // 
         this.tbPassword.Location = new System.Drawing.Point(33, 190);
         this.tbPassword.Name = "tbPassword";
         this.tbPassword.PasswordChar = '*';
         this.tbPassword.Size = new System.Drawing.Size(293, 23);
         this.tbPassword.TabIndex = 4;
         // 
         // cbRoles
         // 
         this.cbRoles.FormattingEnabled = true;
         this.cbRoles.Location = new System.Drawing.Point(335, 109);
         this.cbRoles.Name = "cbRoles";
         this.cbRoles.Size = new System.Drawing.Size(293, 23);
         this.cbRoles.TabIndex = 5;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(278, 112);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(48, 15);
         this.label3.TabIndex = 6;
         this.label3.Text = "Кто ты?";
         // 
         // Form1
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(800, 450);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.cbRoles);
         this.Controls.Add(this.tbPassword);
         this.Controls.Add(this.tbLogin);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnConnect);
         this.Name = "Form1";
         this.Text = "Form1";
         this.Load += new System.EventHandler(this.Form1_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

        }

        #endregion

        private Button btnConnect;
      private Label label1;
      private Label label2;
      private TextBox tbLogin;
      private TextBox tbPassword;
      private ComboBox cbRoles;
      private Label label3;
   }
}
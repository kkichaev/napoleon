namespace NapoleonCfg
{
   partial class FmMain
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMain));
         this.label1 = new System.Windows.Forms.Label();
         this.tbIPAdmin = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbPasswAdmin = new System.Windows.Forms.TextBox();
         this.btnConnect = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.tbPortAdmin = new System.Windows.Forms.TextBox();
         this.gpSettings = new System.Windows.Forms.GroupBox();
         this.btnRem = new System.Windows.Forms.Button();
         this.btnAdd = new System.Windows.Forms.Button();
         this.tbPort = new System.Windows.Forms.TextBox();
         this.tbIP = new System.Windows.Forms.TextBox();
         this.label8 = new System.Windows.Forms.Label();
         this.label7 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label16 = new System.Windows.Forms.Label();
         this.lbHistory = new System.Windows.Forms.ListBox();
         this.label15 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.tbPassword = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.proxyPort = new System.Windows.Forms.TextBox();
         this.proxyIP = new System.Windows.Forms.TextBox();
         this.label13 = new System.Windows.Forms.Label();
         this.label14 = new System.Windows.Forms.Label();
         this.proxyPassword = new System.Windows.Forms.TextBox();
         this.proxyLogin = new System.Windows.Forms.TextBox();
         this.label11 = new System.Windows.Forms.Label();
         this.label10 = new System.Windows.Forms.Label();
         this.gpSettings.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(10, 18);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(15, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "IP";
         // 
         // tbIPAdmin
         // 
         this.tbIPAdmin.Location = new System.Drawing.Point(63, 15);
         this.tbIPAdmin.Name = "tbIPAdmin";
         this.tbIPAdmin.Size = new System.Drawing.Size(189, 20);
         this.tbIPAdmin.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(10, 81);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(44, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Пароль";
         // 
         // tbPasswAdmin
         // 
         this.tbPasswAdmin.Location = new System.Drawing.Point(63, 75);
         this.tbPasswAdmin.Name = "tbPasswAdmin";
         this.tbPasswAdmin.PasswordChar = '*';
         this.tbPasswAdmin.Size = new System.Drawing.Size(189, 20);
         this.tbPasswAdmin.TabIndex = 3;
         // 
         // btnConnect
         // 
         this.btnConnect.Location = new System.Drawing.Point(12, 114);
         this.btnConnect.Name = "btnConnect";
         this.btnConnect.Size = new System.Drawing.Size(75, 23);
         this.btnConnect.TabIndex = 4;
         this.btnConnect.Text = "Проверить";
         this.btnConnect.UseVisualStyleBackColor = true;
         this.btnConnect.Click += new System.EventHandler(this.btnConnect_Click);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(10, 49);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(32, 14);
         this.label3.TabIndex = 5;
         this.label3.Text = "Порт";
         // 
         // tbPortAdmin
         // 
         this.tbPortAdmin.Location = new System.Drawing.Point(63, 46);
         this.tbPortAdmin.Name = "tbPortAdmin";
         this.tbPortAdmin.Size = new System.Drawing.Size(189, 20);
         this.tbPortAdmin.TabIndex = 6;
         // 
         // gpSettings
         // 
         this.gpSettings.Controls.Add(this.groupBox1);
         this.gpSettings.Controls.Add(this.btnRem);
         this.gpSettings.Controls.Add(this.btnAdd);
         this.gpSettings.Controls.Add(this.tbPort);
         this.gpSettings.Controls.Add(this.tbIP);
         this.gpSettings.Controls.Add(this.label8);
         this.gpSettings.Controls.Add(this.label7);
         this.gpSettings.Controls.Add(this.tbName);
         this.gpSettings.Controls.Add(this.label16);
         this.gpSettings.Controls.Add(this.lbHistory);
         this.gpSettings.Controls.Add(this.label15);
         this.gpSettings.Controls.Add(this.label4);
         this.gpSettings.Controls.Add(this.label5);
         this.gpSettings.Controls.Add(this.tbPassword);
         this.gpSettings.Controls.Add(this.tbLogin);
         this.gpSettings.Location = new System.Drawing.Point(12, 143);
         this.gpSettings.Name = "gpSettings";
         this.gpSettings.Size = new System.Drawing.Size(655, 316);
         this.gpSettings.TabIndex = 7;
         this.gpSettings.TabStop = false;
         this.gpSettings.Text = "Настройки Наполеон";
         // 
         // btnRem
         // 
         this.btnRem.Location = new System.Drawing.Point(264, 66);
         this.btnRem.Name = "btnRem";
         this.btnRem.Size = new System.Drawing.Size(75, 23);
         this.btnRem.TabIndex = 27;
         this.btnRem.Text = "<<<";
         this.btnRem.UseVisualStyleBackColor = true;
         this.btnRem.Click += new System.EventHandler(this.btnRem_Click);
         // 
         // btnAdd
         // 
         this.btnAdd.Location = new System.Drawing.Point(264, 36);
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(75, 23);
         this.btnAdd.TabIndex = 26;
         this.btnAdd.Text = ">>>";
         this.btnAdd.UseVisualStyleBackColor = true;
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // tbPort
         // 
         this.tbPort.Location = new System.Drawing.Point(51, 88);
         this.tbPort.Name = "tbPort";
         this.tbPort.Size = new System.Drawing.Size(190, 20);
         this.tbPort.TabIndex = 25;
         // 
         // tbIP
         // 
         this.tbIP.Location = new System.Drawing.Point(51, 62);
         this.tbIP.Name = "tbIP";
         this.tbIP.Size = new System.Drawing.Size(190, 20);
         this.tbIP.TabIndex = 24;
         // 
         // label8
         // 
         this.label8.AutoSize = true;
         this.label8.Location = new System.Drawing.Point(13, 88);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(32, 14);
         this.label8.TabIndex = 23;
         this.label8.Text = "Порт";
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(30, 62);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(15, 14);
         this.label7.TabIndex = 22;
         this.label7.Text = "IP";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(50, 36);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(190, 20);
         this.tbName.TabIndex = 20;
         // 
         // label16
         // 
         this.label16.AutoSize = true;
         this.label16.Location = new System.Drawing.Point(6, 16);
         this.label16.Name = "label16";
         this.label16.Size = new System.Drawing.Size(139, 14);
         this.label16.TabIndex = 19;
         this.label16.Text = "Наименование настройки";
         // 
         // lbHistory
         // 
         this.lbHistory.FormattingEnabled = true;
         this.lbHistory.ItemHeight = 14;
         this.lbHistory.Location = new System.Drawing.Point(360, 36);
         this.lbHistory.Name = "lbHistory";
         this.lbHistory.Size = new System.Drawing.Size(283, 270);
         this.lbHistory.TabIndex = 18;
         this.lbHistory.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lbHistory_MouseDown);
         this.lbHistory.KeyDown += new System.Windows.Forms.KeyEventHandler(this.lbHistory_KeyDown);
         // 
         // label15
         // 
         this.label15.AutoSize = true;
         this.label15.Location = new System.Drawing.Point(357, 16);
         this.label15.Name = "label15";
         this.label15.Size = new System.Drawing.Size(132, 14);
         this.label15.TabIndex = 17;
         this.label15.Text = "Сохраненные настройки";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(1, 144);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(44, 14);
         this.label4.TabIndex = 14;
         this.label4.Text = "Пароль";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(6, 117);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(36, 14);
         this.label5.TabIndex = 13;
         this.label5.Text = "Логин";
         // 
         // tbPassword
         // 
         this.tbPassword.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPassword.Location = new System.Drawing.Point(51, 141);
         this.tbPassword.Name = "tbPassword";
         this.tbPassword.PasswordChar = '*';
         this.tbPassword.Size = new System.Drawing.Size(189, 20);
         this.tbPassword.TabIndex = 12;
         // 
         // tbLogin
         // 
         this.tbLogin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbLogin.Location = new System.Drawing.Point(51, 114);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(190, 20);
         this.tbLogin.TabIndex = 11;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.proxyPort);
         this.groupBox1.Controls.Add(this.proxyIP);
         this.groupBox1.Controls.Add(this.label13);
         this.groupBox1.Controls.Add(this.label14);
         this.groupBox1.Controls.Add(this.proxyPassword);
         this.groupBox1.Controls.Add(this.proxyLogin);
         this.groupBox1.Controls.Add(this.label11);
         this.groupBox1.Controls.Add(this.label10);
         this.groupBox1.Location = new System.Drawing.Point(9, 172);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(331, 138);
         this.groupBox1.TabIndex = 28;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Прокси-сервер";
         // 
         // proxyPort
         // 
         this.proxyPort.Location = new System.Drawing.Point(58, 99);
         this.proxyPort.Name = "proxyPort";
         this.proxyPort.Size = new System.Drawing.Size(237, 20);
         this.proxyPort.TabIndex = 7;
         // 
         // proxyIP
         // 
         this.proxyIP.Location = new System.Drawing.Point(58, 72);
         this.proxyIP.Name = "proxyIP";
         this.proxyIP.Size = new System.Drawing.Size(237, 20);
         this.proxyIP.TabIndex = 6;
         // 
         // label13
         // 
         this.label13.AutoSize = true;
         this.label13.Location = new System.Drawing.Point(7, 100);
         this.label13.Name = "label13";
         this.label13.Size = new System.Drawing.Size(32, 14);
         this.label13.TabIndex = 5;
         this.label13.Text = "Порт";
         // 
         // label14
         // 
         this.label14.AutoSize = true;
         this.label14.Location = new System.Drawing.Point(7, 74);
         this.label14.Name = "label14";
         this.label14.Size = new System.Drawing.Size(15, 14);
         this.label14.TabIndex = 4;
         this.label14.Text = "IP";
         // 
         // proxyPassword
         // 
         this.proxyPassword.Location = new System.Drawing.Point(58, 46);
         this.proxyPassword.Name = "proxyPassword";
         this.proxyPassword.PasswordChar = '*';
         this.proxyPassword.Size = new System.Drawing.Size(237, 20);
         this.proxyPassword.TabIndex = 3;
         // 
         // proxyLogin
         // 
         this.proxyLogin.Location = new System.Drawing.Point(58, 19);
         this.proxyLogin.Name = "proxyLogin";
         this.proxyLogin.Size = new System.Drawing.Size(237, 20);
         this.proxyLogin.TabIndex = 2;
         // 
         // label11
         // 
         this.label11.AutoSize = true;
         this.label11.Location = new System.Drawing.Point(7, 47);
         this.label11.Name = "label11";
         this.label11.Size = new System.Drawing.Size(44, 14);
         this.label11.TabIndex = 1;
         this.label11.Text = "Пароль";
         // 
         // label10
         // 
         this.label10.AutoSize = true;
         this.label10.Location = new System.Drawing.Point(7, 21);
         this.label10.Name = "label10";
         this.label10.Size = new System.Drawing.Size(36, 14);
         this.label10.TabIndex = 0;
         this.label10.Text = "Логин";
         // 
         // FmMain
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(679, 466);
         this.Controls.Add(this.gpSettings);
         this.Controls.Add(this.tbPortAdmin);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.btnConnect);
         this.Controls.Add(this.tbPasswAdmin);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbIPAdmin);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMain";
         this.Text = "Настройка подключений Наполеон";
         this.gpSettings.ResumeLayout(false);
         this.gpSettings.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbIPAdmin;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPasswAdmin;
      private System.Windows.Forms.Button btnConnect;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbPortAdmin;
      private System.Windows.Forms.GroupBox gpSettings;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label16;
      private System.Windows.Forms.ListBox lbHistory;
      private System.Windows.Forms.Label label15;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox tbPassword;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox tbIP;
      private System.Windows.Forms.TextBox tbPort;
      private System.Windows.Forms.Button btnAdd;
      private System.Windows.Forms.Button btnRem;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.TextBox proxyPort;
      private System.Windows.Forms.TextBox proxyIP;
      private System.Windows.Forms.Label label13;
      private System.Windows.Forms.Label label14;
      private System.Windows.Forms.TextBox proxyPassword;
      private System.Windows.Forms.TextBox proxyLogin;
      private System.Windows.Forms.Label label11;
      private System.Windows.Forms.Label label10;
   }
}


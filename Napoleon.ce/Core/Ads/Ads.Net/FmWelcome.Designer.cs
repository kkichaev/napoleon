namespace GRSoft.Ads
{
   partial class FmWelcome
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmWelcome));
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel1 = new System.Windows.Forms.Panel();
         this.gbConnection = new System.Windows.Forms.GroupBox();
         this.cbRememberPassword = new System.Windows.Forms.CheckBox();
         this.btnConnect = new System.Windows.Forms.Button();
         this.tbPassw = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.tbPort = new System.Windows.Forms.TextBox();
         this.tbIP = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.textBox1 = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbLoginDisp = new System.Windows.Forms.TextBox();
         this.tbPasswDisp = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.label7 = new System.Windows.Forms.Label();
         this.panel2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.gbConnection.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 380);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(626, 46);
         this.panel2.TabIndex = 1;
         // 
         // btnCancel
         // 
         this.btnCancel.Location = new System.Drawing.Point(539, 11);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(458, 11);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "Закончить";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.groupBox1);
         this.panel1.Controls.Add(this.gbConnection);
         this.panel1.Controls.Add(this.textBox1);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(626, 380);
         this.panel1.TabIndex = 2;
         // 
         // gbConnection
         // 
         this.gbConnection.Controls.Add(this.cbRememberPassword);
         this.gbConnection.Controls.Add(this.btnConnect);
         this.gbConnection.Controls.Add(this.tbPassw);
         this.gbConnection.Controls.Add(this.tbLogin);
         this.gbConnection.Controls.Add(this.tbPort);
         this.gbConnection.Controls.Add(this.tbIP);
         this.gbConnection.Controls.Add(this.label5);
         this.gbConnection.Controls.Add(this.label4);
         this.gbConnection.Controls.Add(this.label3);
         this.gbConnection.Controls.Add(this.label2);
         this.gbConnection.Location = new System.Drawing.Point(12, 219);
         this.gbConnection.Name = "gbConnection";
         this.gbConnection.Size = new System.Drawing.Size(263, 154);
         this.gbConnection.TabIndex = 2;
         this.gbConnection.TabStop = false;
         this.gbConnection.Text = "Данные для подключения к серверу";
         // 
         // cbRememberPassword
         // 
         this.cbRememberPassword.AutoSize = true;
         this.cbRememberPassword.Checked = true;
         this.cbRememberPassword.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbRememberPassword.Location = new System.Drawing.Point(9, 116);
         this.cbRememberPassword.Name = "cbRememberPassword";
         this.cbRememberPassword.Size = new System.Drawing.Size(122, 18);
         this.cbRememberPassword.TabIndex = 9;
         this.cbRememberPassword.Text = "Запомнить пароль";
         this.cbRememberPassword.UseVisualStyleBackColor = true;
         // 
         // btnConnect
         // 
         this.btnConnect.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnConnect.Location = new System.Drawing.Point(151, 116);
         this.btnConnect.Name = "btnConnect";
         this.btnConnect.Size = new System.Drawing.Size(98, 23);
         this.btnConnect.TabIndex = 8;
         this.btnConnect.Text = "Подключиться";
         this.btnConnect.UseVisualStyleBackColor = true;
         this.btnConnect.Click += new System.EventHandler(this.btnConnect_Click);
         // 
         // tbPassw
         // 
         this.tbPassw.Location = new System.Drawing.Point(73, 90);
         this.tbPassw.Name = "tbPassw";
         this.tbPassw.PasswordChar = '*';
         this.tbPassw.Size = new System.Drawing.Size(176, 20);
         this.tbPassw.TabIndex = 7;
         // 
         // tbLogin
         // 
         this.tbLogin.Location = new System.Drawing.Point(73, 66);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(176, 20);
         this.tbLogin.TabIndex = 6;
         // 
         // tbPort
         // 
         this.tbPort.Location = new System.Drawing.Point(73, 42);
         this.tbPort.Name = "tbPort";
         this.tbPort.Size = new System.Drawing.Size(176, 20);
         this.tbPort.TabIndex = 5;
         // 
         // tbIP
         // 
         this.tbIP.Location = new System.Drawing.Point(73, 18);
         this.tbIP.Name = "tbIP";
         this.tbIP.Size = new System.Drawing.Size(176, 20);
         this.tbIP.TabIndex = 4;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(6, 90);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(44, 14);
         this.label5.TabIndex = 3;
         this.label5.Text = "Пароль";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(6, 66);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(36, 14);
         this.label4.TabIndex = 2;
         this.label4.Text = "Логин";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(6, 42);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(32, 14);
         this.label3.TabIndex = 1;
         this.label3.Text = "Порт";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 18);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(15, 14);
         this.label2.TabIndex = 0;
         this.label2.Text = "IP";
         // 
         // textBox1
         // 
         this.textBox1.Location = new System.Drawing.Point(12, 48);
         this.textBox1.Multiline = true;
         this.textBox1.Name = "textBox1";
         this.textBox1.Size = new System.Drawing.Size(604, 165);
         this.textBox1.TabIndex = 1;
         this.textBox1.Text = resources.GetString("textBox1.Text");
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Font = new System.Drawing.Font("Arial", 24F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label1.ForeColor = System.Drawing.Color.Blue;
         this.label1.Location = new System.Drawing.Point(202, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(222, 36);
         this.label1.TabIndex = 0;
         this.label1.Text = "ADS Welcome";
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.label7);
         this.groupBox1.Controls.Add(this.label6);
         this.groupBox1.Controls.Add(this.tbPasswDisp);
         this.groupBox1.Controls.Add(this.tbLoginDisp);
         this.groupBox1.Location = new System.Drawing.Point(288, 219);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(326, 154);
         this.groupBox1.TabIndex = 3;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Данный для вохода в диспетчер";
         // 
         // tbLoginDisp
         // 
         this.tbLoginDisp.Location = new System.Drawing.Point(58, 18);
         this.tbLoginDisp.Name = "tbLoginDisp";
         this.tbLoginDisp.Size = new System.Drawing.Size(176, 20);
         this.tbLoginDisp.TabIndex = 0;
         // 
         // tbPasswDisp
         // 
         this.tbPasswDisp.Location = new System.Drawing.Point(58, 42);
         this.tbPasswDisp.Name = "tbPasswDisp";
         this.tbPasswDisp.Size = new System.Drawing.Size(176, 20);
         this.tbPasswDisp.TabIndex = 1;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(8, 18);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(36, 14);
         this.label6.TabIndex = 2;
         this.label6.Text = "Логин";
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(8, 42);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(44, 14);
         this.label7.TabIndex = 3;
         this.label7.Text = "Пароль";
         // 
         // FmWelcome
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(626, 426);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmWelcome";
         this.Text = "ADSWelcome";
         this.panel2.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.gbConnection.ResumeLayout(false);
         this.gbConnection.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox textBox1;
      private System.Windows.Forms.GroupBox gbConnection;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPassw;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.TextBox tbPort;
      private System.Windows.Forms.TextBox tbIP;
      private System.Windows.Forms.Button btnConnect;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.CheckBox cbRememberPassword;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbPasswDisp;
      private System.Windows.Forms.TextBox tbLoginDisp;
   }
}
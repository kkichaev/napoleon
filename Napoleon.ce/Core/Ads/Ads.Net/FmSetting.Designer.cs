namespace GRSoft.Ads
{
   partial class FmSetting
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSetting));
         this.panel1 = new System.Windows.Forms.Panel();
         this.button2 = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.cbRememberPassword = new System.Windows.Forms.CheckBox();
         this.tbPassw = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.tbPort = new System.Windows.Forms.TextBox();
         this.tbIP = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbMapSource = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label6 = new System.Windows.Forms.Label();
         this.tbPrefix = new System.Windows.Forms.TextBox();
         this.label7 = new System.Windows.Forms.Label();
         this.tbOrderNumber = new System.Windows.Forms.TextBox();
         this.label8 = new System.Windows.Forms.Label();
         this.udRefreshTime = new System.Windows.Forms.NumericUpDown();
         this.label9 = new System.Windows.Forms.Label();
         this.label10 = new System.Windows.Forms.Label();
         this.udMissedOrderInterval = new System.Windows.Forms.NumericUpDown();
         this.cbAlert = new System.Windows.Forms.CheckBox();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.udRefreshTime)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.udMissedOrderInterval)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.button2);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 370);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(291, 40);
         this.panel1.TabIndex = 0;
         // 
         // button2
         // 
         this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(127, 8);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 1;
         this.button2.Text = "Отменить";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(208, 9);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // cbRememberPassword
         // 
         this.cbRememberPassword.AutoSize = true;
         this.cbRememberPassword.Checked = true;
         this.cbRememberPassword.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbRememberPassword.Location = new System.Drawing.Point(14, 107);
         this.cbRememberPassword.Name = "cbRememberPassword";
         this.cbRememberPassword.Size = new System.Drawing.Size(122, 18);
         this.cbRememberPassword.TabIndex = 18;
         this.cbRememberPassword.Text = "Запомнить пароль";
         this.cbRememberPassword.UseVisualStyleBackColor = true;
         // 
         // tbPassw
         // 
         this.tbPassw.Location = new System.Drawing.Point(81, 81);
         this.tbPassw.Name = "tbPassw";
         this.tbPassw.PasswordChar = '*';
         this.tbPassw.Size = new System.Drawing.Size(176, 20);
         this.tbPassw.TabIndex = 17;
         // 
         // tbLogin
         // 
         this.tbLogin.Location = new System.Drawing.Point(81, 57);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(176, 20);
         this.tbLogin.TabIndex = 16;
         // 
         // tbPort
         // 
         this.tbPort.Location = new System.Drawing.Point(81, 33);
         this.tbPort.Name = "tbPort";
         this.tbPort.Size = new System.Drawing.Size(176, 20);
         this.tbPort.TabIndex = 15;
         // 
         // tbIP
         // 
         this.tbIP.Location = new System.Drawing.Point(81, 9);
         this.tbIP.Name = "tbIP";
         this.tbIP.Size = new System.Drawing.Size(176, 20);
         this.tbIP.TabIndex = 14;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(14, 81);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(44, 14);
         this.label5.TabIndex = 13;
         this.label5.Text = "Пароль";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(14, 57);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(36, 14);
         this.label4.TabIndex = 12;
         this.label4.Text = "Логин";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(14, 33);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(32, 14);
         this.label3.TabIndex = 11;
         this.label3.Text = "Порт";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(14, 9);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(15, 14);
         this.label2.TabIndex = 10;
         this.label2.Text = "IP";
         // 
         // cbMapSource
         // 
         this.cbMapSource.FormattingEnabled = true;
         this.cbMapSource.Location = new System.Drawing.Point(81, 154);
         this.cbMapSource.Name = "cbMapSource";
         this.cbMapSource.Size = new System.Drawing.Size(176, 22);
         this.cbMapSource.TabIndex = 19;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(14, 137);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 20;
         this.label1.Text = "Источник карт";
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(14, 182);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(130, 14);
         this.label6.TabIndex = 21;
         this.label6.Text = "Префикс номера заявки";
         // 
         // tbPrefix
         // 
         this.tbPrefix.Location = new System.Drawing.Point(81, 200);
         this.tbPrefix.Name = "tbPrefix";
         this.tbPrefix.Size = new System.Drawing.Size(176, 20);
         this.tbPrefix.TabIndex = 22;
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(14, 221);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(78, 14);
         this.label7.TabIndex = 23;
         this.label7.Text = "Номер заявки";
         // 
         // tbOrderNumber
         // 
         this.tbOrderNumber.Location = new System.Drawing.Point(81, 239);
         this.tbOrderNumber.Name = "tbOrderNumber";
         this.tbOrderNumber.Size = new System.Drawing.Size(176, 20);
         this.tbOrderNumber.TabIndex = 24;
         // 
         // label8
         // 
         this.label8.AutoSize = true;
         this.label8.Location = new System.Drawing.Point(14, 269);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(167, 14);
         this.label8.TabIndex = 25;
         this.label8.Text = "Автообновление данных(мин.)";
         // 
         // udRefreshTime
         // 
         this.udRefreshTime.Location = new System.Drawing.Point(179, 269);
         this.udRefreshTime.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.udRefreshTime.Name = "udRefreshTime";
         this.udRefreshTime.Size = new System.Drawing.Size(78, 20);
         this.udRefreshTime.TabIndex = 26;
         this.udRefreshTime.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         // 
         // label9
         // 
         this.label9.AutoSize = true;
         this.label9.Location = new System.Drawing.Point(14, 303);
         this.label9.Name = "label9";
         this.label9.Size = new System.Drawing.Size(155, 14);
         this.label9.TabIndex = 27;
         this.label9.Text = "Интервал в мин. для подачи";
         // 
         // label10
         // 
         this.label10.AutoSize = true;
         this.label10.Location = new System.Drawing.Point(14, 321);
         this.label10.Name = "label10";
         this.label10.Size = new System.Drawing.Size(199, 14);
         this.label10.TabIndex = 28;
         this.label10.Text = "сигнала если заявка не выполняется";
         // 
         // udMissedOrderInterval
         // 
         this.udMissedOrderInterval.Increment = new decimal(new int[] {
            10,
            0,
            0,
            0});
         this.udMissedOrderInterval.Location = new System.Drawing.Point(180, 297);
         this.udMissedOrderInterval.Name = "udMissedOrderInterval";
         this.udMissedOrderInterval.Size = new System.Drawing.Size(77, 20);
         this.udMissedOrderInterval.TabIndex = 29;
         this.udMissedOrderInterval.Value = new decimal(new int[] {
            10,
            0,
            0,
            0});
         // 
         // cbAlert
         // 
         this.cbAlert.AutoSize = true;
         this.cbAlert.Location = new System.Drawing.Point(17, 347);
         this.cbAlert.Name = "cbAlert";
         this.cbAlert.Size = new System.Drawing.Size(152, 18);
         this.cbAlert.TabIndex = 30;
         this.cbAlert.Text = "Визуальное оповещение";
         this.cbAlert.UseVisualStyleBackColor = true;
         // 
         // FmSetting
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(291, 410);
         this.Controls.Add(this.cbAlert);
         this.Controls.Add(this.udMissedOrderInterval);
         this.Controls.Add(this.label10);
         this.Controls.Add(this.label9);
         this.Controls.Add(this.udRefreshTime);
         this.Controls.Add(this.label8);
         this.Controls.Add(this.tbOrderNumber);
         this.Controls.Add(this.label7);
         this.Controls.Add(this.tbPrefix);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbMapSource);
         this.Controls.Add(this.cbRememberPassword);
         this.Controls.Add(this.tbPassw);
         this.Controls.Add(this.tbLogin);
         this.Controls.Add(this.tbPort);
         this.Controls.Add(this.tbIP);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSetting";
         this.Text = "Настройки программы";
         this.Load += new System.EventHandler(this.FmSetting_Load);
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.udRefreshTime)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.udMissedOrderInterval)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.CheckBox cbRememberPassword;
      private System.Windows.Forms.TextBox tbPassw;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.TextBox tbPort;
      private System.Windows.Forms.TextBox tbIP;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbMapSource;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbPrefix;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox tbOrderNumber;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.NumericUpDown udRefreshTime;
      private System.Windows.Forms.Label label9;
      private System.Windows.Forms.Label label10;
      private System.Windows.Forms.NumericUpDown udMissedOrderInterval;
      private System.Windows.Forms.CheckBox cbAlert;
   }
}
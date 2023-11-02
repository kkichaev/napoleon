namespace GRSoft.NapoleonManager
{
   partial class FmConfig
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmConfig));
         this.tbConfig = new System.Windows.Forms.TabControl();
         this.tpUser = new System.Windows.Forms.TabPage();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label16 = new System.Windows.Forms.Label();
         this.lbHistory = new System.Windows.Forms.ListBox();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miDel = new System.Windows.Forms.ToolStripMenuItem();
         this.label15 = new System.Windows.Forms.Label();
         this.cbRememberPassword = new System.Windows.Forms.CheckBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.tbPassword = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.tpConnectionInfo = new System.Windows.Forms.TabPage();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.proxyDomen = new System.Windows.Forms.TextBox();
         this.label17 = new System.Windows.Forms.Label();
         this.proxyPort = new System.Windows.Forms.TextBox();
         this.proxyIP = new System.Windows.Forms.TextBox();
         this.label13 = new System.Windows.Forms.Label();
         this.label14 = new System.Windows.Forms.Label();
         this.proxyPassword = new System.Windows.Forms.TextBox();
         this.proxyLogin = new System.Windows.Forms.TextBox();
         this.label11 = new System.Windows.Forms.Label();
         this.label10 = new System.Windows.Forms.Label();
         this.tbPort = new System.Windows.Forms.TextBox();
         this.tbIP = new System.Windows.Forms.TextBox();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.tpMaps = new System.Windows.Forms.TabPage();
         this.cbMapSource = new System.Windows.Forms.ComboBox();
         this.label5 = new System.Windows.Forms.Label();
         this.tpAdd = new System.Windows.Forms.TabPage();
         this.cbScriptErrorsAllow = new System.Windows.Forms.CheckBox();
         this.cbOnlyInstance = new System.Windows.Forms.CheckBox();
         this.cbHighliteOrderMissed = new System.Windows.Forms.CheckBox();
         this.label12 = new System.Windows.Forms.Label();
         this.cbCultures = new System.Windows.Forms.ComboBox();
         this.cbFullOrgName = new System.Windows.Forms.CheckBox();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnExit = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOk = new System.Windows.Forms.Button();
         this.panel1 = new System.Windows.Forms.Panel();
         this.label8 = new System.Windows.Forms.Label();
         this.label9 = new System.Windows.Forms.Label();
         this.textBox1 = new System.Windows.Forms.TextBox();
         this.textBox2 = new System.Windows.Forms.TextBox();
         this.toolTip1 = new System.Windows.Forms.ToolTip(this.components);
         this.tbConfig.SuspendLayout();
         this.tpUser.SuspendLayout();
         this.contextMenuStrip1.SuspendLayout();
         this.tpConnectionInfo.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.tpMaps.SuspendLayout();
         this.tpAdd.SuspendLayout();
         this.panel2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tbConfig
         // 
         this.tbConfig.Controls.Add(this.tpUser);
         this.tbConfig.Controls.Add(this.tpConnectionInfo);
         this.tbConfig.Controls.Add(this.tpMaps);
         this.tbConfig.Controls.Add(this.tpAdd);
         this.tbConfig.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbConfig.Location = new System.Drawing.Point(7, 7);
         this.tbConfig.Name = "tbConfig";
         this.tbConfig.SelectedIndex = 0;
         this.tbConfig.Size = new System.Drawing.Size(372, 289);
         this.tbConfig.TabIndex = 0;
         // 
         // tpUser
         // 
         this.tpUser.Controls.Add(this.tbName);
         this.tpUser.Controls.Add(this.label16);
         this.tpUser.Controls.Add(this.lbHistory);
         this.tpUser.Controls.Add(this.label15);
         this.tpUser.Controls.Add(this.cbRememberPassword);
         this.tpUser.Controls.Add(this.label2);
         this.tpUser.Controls.Add(this.label1);
         this.tpUser.Controls.Add(this.tbPassword);
         this.tpUser.Controls.Add(this.tbLogin);
         this.tpUser.Location = new System.Drawing.Point(4, 22);
         this.tpUser.Name = "tpUser";
         this.tpUser.Padding = new System.Windows.Forms.Padding(3);
         this.tpUser.Size = new System.Drawing.Size(364, 263);
         this.tpUser.TabIndex = 0;
         this.tpUser.Text = "Пользователь";
         this.tpUser.UseVisualStyleBackColor = true;
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(57, 25);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(190, 20);
         this.tbName.TabIndex = 10;
         // 
         // label16
         // 
         this.label16.AutoSize = true;
         this.label16.Location = new System.Drawing.Point(7, 8);
         this.label16.Name = "label16";
         this.label16.Size = new System.Drawing.Size(139, 13);
         this.label16.TabIndex = 9;
         this.label16.Text = "Наименование настройки";
         // 
         // lbHistory
         // 
         this.lbHistory.ContextMenuStrip = this.contextMenuStrip1;
         this.lbHistory.FormattingEnabled = true;
         this.lbHistory.Location = new System.Drawing.Point(7, 139);
         this.lbHistory.Name = "lbHistory";
         this.lbHistory.Size = new System.Drawing.Size(337, 69);
         this.lbHistory.TabIndex = 8;
         this.lbHistory.KeyDown += new System.Windows.Forms.KeyEventHandler(this.lbHistory_KeyDown);
         this.lbHistory.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lbHistory_MouseDown);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miDel});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(119, 26);
         // 
         // miDel
         // 
         this.miDel.Name = "miDel";
         this.miDel.Size = new System.Drawing.Size(118, 22);
         this.miDel.Text = "Удалить";
         this.miDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // label15
         // 
         this.label15.AutoSize = true;
         this.label15.Location = new System.Drawing.Point(6, 123);
         this.label15.Name = "label15";
         this.label15.Size = new System.Drawing.Size(131, 13);
         this.label15.TabIndex = 7;
         this.label15.Text = "Сохраненные настройки";
         // 
         // cbRememberPassword
         // 
         this.cbRememberPassword.AutoSize = true;
         this.cbRememberPassword.Location = new System.Drawing.Point(9, 103);
         this.cbRememberPassword.Name = "cbRememberPassword";
         this.cbRememberPassword.Size = new System.Drawing.Size(121, 17);
         this.cbRememberPassword.TabIndex = 4;
         this.cbRememberPassword.Text = "Запомнить пароль";
         this.cbRememberPassword.UseVisualStyleBackColor = true;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 81);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(45, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "Пароль";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(7, 54);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(38, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Логин";
         // 
         // tbPassword
         // 
         this.tbPassword.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPassword.Location = new System.Drawing.Point(57, 79);
         this.tbPassword.Name = "tbPassword";
         this.tbPassword.PasswordChar = '*';
         this.tbPassword.Size = new System.Drawing.Size(190, 20);
         this.tbPassword.TabIndex = 1;
         // 
         // tbLogin
         // 
         this.tbLogin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbLogin.Location = new System.Drawing.Point(57, 52);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(190, 20);
         this.tbLogin.TabIndex = 0;
         // 
         // tpConnectionInfo
         // 
         this.tpConnectionInfo.Controls.Add(this.groupBox1);
         this.tpConnectionInfo.Controls.Add(this.tbPort);
         this.tpConnectionInfo.Controls.Add(this.tbIP);
         this.tpConnectionInfo.Controls.Add(this.label4);
         this.tpConnectionInfo.Controls.Add(this.label3);
         this.tpConnectionInfo.Location = new System.Drawing.Point(4, 22);
         this.tpConnectionInfo.Name = "tpConnectionInfo";
         this.tpConnectionInfo.Padding = new System.Windows.Forms.Padding(3);
         this.tpConnectionInfo.Size = new System.Drawing.Size(364, 263);
         this.tpConnectionInfo.TabIndex = 1;
         this.tpConnectionInfo.Text = "Соединение";
         this.tpConnectionInfo.UseVisualStyleBackColor = true;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.proxyDomen);
         this.groupBox1.Controls.Add(this.label17);
         this.groupBox1.Controls.Add(this.proxyPort);
         this.groupBox1.Controls.Add(this.proxyIP);
         this.groupBox1.Controls.Add(this.label13);
         this.groupBox1.Controls.Add(this.label14);
         this.groupBox1.Controls.Add(this.proxyPassword);
         this.groupBox1.Controls.Add(this.proxyLogin);
         this.groupBox1.Controls.Add(this.label11);
         this.groupBox1.Controls.Add(this.label10);
         this.groupBox1.Location = new System.Drawing.Point(8, 73);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(331, 167);
         this.groupBox1.TabIndex = 4;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Прокси-сервер";
         // 
         // proxyDomen
         // 
         this.proxyDomen.Location = new System.Drawing.Point(57, 74);
         this.proxyDomen.Name = "proxyDomen";
         this.proxyDomen.Size = new System.Drawing.Size(237, 20);
         this.proxyDomen.TabIndex = 9;
         // 
         // label17
         // 
         this.label17.AutoSize = true;
         this.label17.Location = new System.Drawing.Point(6, 75);
         this.label17.Name = "label17";
         this.label17.Size = new System.Drawing.Size(42, 13);
         this.label17.TabIndex = 8;
         this.label17.Text = "Домен";
         // 
         // proxyPort
         // 
         this.proxyPort.Location = new System.Drawing.Point(58, 129);
         this.proxyPort.Name = "proxyPort";
         this.proxyPort.Size = new System.Drawing.Size(237, 20);
         this.proxyPort.TabIndex = 7;
         // 
         // proxyIP
         // 
         this.proxyIP.Location = new System.Drawing.Point(58, 102);
         this.proxyIP.Name = "proxyIP";
         this.proxyIP.Size = new System.Drawing.Size(237, 20);
         this.proxyIP.TabIndex = 6;
         // 
         // label13
         // 
         this.label13.AutoSize = true;
         this.label13.Location = new System.Drawing.Point(7, 130);
         this.label13.Name = "label13";
         this.label13.Size = new System.Drawing.Size(32, 13);
         this.label13.TabIndex = 5;
         this.label13.Text = "Порт";
         // 
         // label14
         // 
         this.label14.AutoSize = true;
         this.label14.Location = new System.Drawing.Point(7, 104);
         this.label14.Name = "label14";
         this.label14.Size = new System.Drawing.Size(17, 13);
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
         this.label11.Size = new System.Drawing.Size(45, 13);
         this.label11.TabIndex = 1;
         this.label11.Text = "Пароль";
         // 
         // label10
         // 
         this.label10.AutoSize = true;
         this.label10.Location = new System.Drawing.Point(7, 21);
         this.label10.Name = "label10";
         this.label10.Size = new System.Drawing.Size(38, 13);
         this.label10.TabIndex = 0;
         this.label10.Text = "Логин";
         // 
         // tbPort
         // 
         this.tbPort.Location = new System.Drawing.Point(66, 41);
         this.tbPort.Name = "tbPort";
         this.tbPort.Size = new System.Drawing.Size(237, 20);
         this.tbPort.TabIndex = 3;
         // 
         // tbIP
         // 
         this.tbIP.Location = new System.Drawing.Point(66, 14);
         this.tbIP.Name = "tbIP";
         this.tbIP.Size = new System.Drawing.Size(237, 20);
         this.tbIP.TabIndex = 2;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(15, 42);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(32, 13);
         this.label4.TabIndex = 1;
         this.label4.Text = "Порт";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(15, 16);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(17, 13);
         this.label3.TabIndex = 0;
         this.label3.Text = "IP";
         // 
         // tpMaps
         // 
         this.tpMaps.Controls.Add(this.cbMapSource);
         this.tpMaps.Controls.Add(this.label5);
         this.tpMaps.Location = new System.Drawing.Point(4, 22);
         this.tpMaps.Name = "tpMaps";
         this.tpMaps.Padding = new System.Windows.Forms.Padding(3);
         this.tpMaps.Size = new System.Drawing.Size(364, 263);
         this.tpMaps.TabIndex = 2;
         this.tpMaps.Text = "Карты";
         this.tpMaps.UseVisualStyleBackColor = true;
         // 
         // cbMapSource
         // 
         this.cbMapSource.FormattingEnabled = true;
         this.cbMapSource.Location = new System.Drawing.Point(93, 18);
         this.cbMapSource.Name = "cbMapSource";
         this.cbMapSource.Size = new System.Drawing.Size(187, 21);
         this.cbMapSource.TabIndex = 1;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(6, 21);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(81, 13);
         this.label5.TabIndex = 0;
         this.label5.Text = "Источник карт";
         // 
         // tpAdd
         // 
         this.tpAdd.Controls.Add(this.cbScriptErrorsAllow);
         this.tpAdd.Controls.Add(this.cbOnlyInstance);
         this.tpAdd.Controls.Add(this.cbHighliteOrderMissed);
         this.tpAdd.Controls.Add(this.label12);
         this.tpAdd.Controls.Add(this.cbCultures);
         this.tpAdd.Controls.Add(this.cbFullOrgName);
         this.tpAdd.Location = new System.Drawing.Point(4, 22);
         this.tpAdd.Name = "tpAdd";
         this.tpAdd.Size = new System.Drawing.Size(364, 263);
         this.tpAdd.TabIndex = 4;
         this.tpAdd.Text = "Дополнительно";
         this.tpAdd.UseVisualStyleBackColor = true;
         // 
         // cbScriptErrorsAllow
         // 
         this.cbScriptErrorsAllow.AutoSize = true;
         this.cbScriptErrorsAllow.Location = new System.Drawing.Point(85, 121);
         this.cbScriptErrorsAllow.Name = "cbScriptErrorsAllow";
         this.cbScriptErrorsAllow.Size = new System.Drawing.Size(180, 17);
         this.cbScriptErrorsAllow.TabIndex = 5;
         this.cbScriptErrorsAllow.Text = "Показывать ошибки браузера";
         this.cbScriptErrorsAllow.UseVisualStyleBackColor = true;
         // 
         // cbOnlyInstance
         // 
         this.cbOnlyInstance.AutoSize = true;
         this.cbOnlyInstance.Location = new System.Drawing.Point(85, 98);
         this.cbOnlyInstance.Name = "cbOnlyInstance";
         this.cbOnlyInstance.Size = new System.Drawing.Size(255, 17);
         this.cbOnlyInstance.TabIndex = 4;
         this.cbOnlyInstance.Text = "Запускать одновременно только одну копию";
         this.cbOnlyInstance.UseVisualStyleBackColor = true;
         // 
         // cbHighliteOrderMissed
         // 
         this.cbHighliteOrderMissed.AutoSize = true;
         this.cbHighliteOrderMissed.Location = new System.Drawing.Point(85, 74);
         this.cbHighliteOrderMissed.Name = "cbHighliteOrderMissed";
         this.cbHighliteOrderMissed.Size = new System.Drawing.Size(266, 17);
         this.cbHighliteOrderMissed.TabIndex = 3;
         this.cbHighliteOrderMissed.Text = "Выделять цветом заявки не загруженные в 1С";
         this.cbHighliteOrderMissed.UseVisualStyleBackColor = true;
         // 
         // label12
         // 
         this.label12.AutoSize = true;
         this.label12.Location = new System.Drawing.Point(4, 16);
         this.label12.Name = "label12";
         this.label12.Size = new System.Drawing.Size(75, 13);
         this.label12.TabIndex = 2;
         this.label12.Text = "Локализация";
         // 
         // cbCultures
         // 
         this.cbCultures.FormattingEnabled = true;
         this.cbCultures.Location = new System.Drawing.Point(85, 13);
         this.cbCultures.Name = "cbCultures";
         this.cbCultures.Size = new System.Drawing.Size(244, 21);
         this.cbCultures.Sorted = true;
         this.cbCultures.TabIndex = 1;
         // 
         // cbFullOrgName
         // 
         this.cbFullOrgName.AutoSize = true;
         this.cbFullOrgName.Location = new System.Drawing.Point(85, 51);
         this.cbFullOrgName.Name = "cbFullOrgName";
         this.cbFullOrgName.Size = new System.Drawing.Size(155, 17);
         this.cbFullOrgName.TabIndex = 0;
         this.cbFullOrgName.Text = "Полное имя организации";
         this.toolTip1.SetToolTip(this.cbFullOrgName, "Отображать имя контрагента вместе с адресом.");
         this.cbFullOrgName.UseVisualStyleBackColor = true;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnExit);
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOk);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 303);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(386, 34);
         this.panel2.TabIndex = 1;
         // 
         // btnExit
         // 
         this.btnExit.Location = new System.Drawing.Point(7, 6);
         this.btnExit.Name = "btnExit";
         this.btnExit.Size = new System.Drawing.Size(75, 23);
         this.btnExit.TabIndex = 2;
         this.btnExit.Text = "Выход";
         this.btnExit.UseVisualStyleBackColor = true;
         this.btnExit.Click += new System.EventHandler(this.btnExit_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(304, 6);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // btnOk
         // 
         this.btnOk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOk.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOk.Location = new System.Drawing.Point(223, 6);
         this.btnOk.Name = "btnOk";
         this.btnOk.Size = new System.Drawing.Size(75, 23);
         this.btnOk.TabIndex = 0;
         this.btnOk.Text = "OK";
         this.btnOk.UseVisualStyleBackColor = true;
         this.btnOk.Click += new System.EventHandler(this.btnOk_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.tbConfig);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(386, 303);
         this.panel1.TabIndex = 2;
         // 
         // label8
         // 
         this.label8.AutoSize = true;
         this.label8.Location = new System.Drawing.Point(15, 42);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(32, 13);
         this.label8.TabIndex = 1;
         this.label8.Text = "Порт";
         // 
         // label9
         // 
         this.label9.AutoSize = true;
         this.label9.Location = new System.Drawing.Point(15, 16);
         this.label9.Name = "label9";
         this.label9.Size = new System.Drawing.Size(17, 13);
         this.label9.TabIndex = 0;
         this.label9.Text = "IP";
         // 
         // textBox1
         // 
         this.textBox1.Location = new System.Drawing.Point(66, 41);
         this.textBox1.Name = "textBox1";
         this.textBox1.Size = new System.Drawing.Size(237, 20);
         this.textBox1.TabIndex = 3;
         // 
         // textBox2
         // 
         this.textBox2.Location = new System.Drawing.Point(66, 14);
         this.textBox2.Name = "textBox2";
         this.textBox2.Size = new System.Drawing.Size(237, 20);
         this.textBox2.TabIndex = 2;
         // 
         // FmConfig
         // 
         this.AcceptButton = this.btnOk;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(386, 337);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmConfig";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "Настройки";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmConfig_FormClosing);
         this.tbConfig.ResumeLayout(false);
         this.tpUser.ResumeLayout(false);
         this.tpUser.PerformLayout();
         this.contextMenuStrip1.ResumeLayout(false);
         this.tpConnectionInfo.ResumeLayout(false);
         this.tpConnectionInfo.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.tpMaps.ResumeLayout(false);
         this.tpMaps.PerformLayout();
         this.tpAdd.ResumeLayout(false);
         this.tpAdd.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.TabPage tpUser;
      private System.Windows.Forms.TabPage tpConnectionInfo;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOk;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.TextBox tbPassword;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbPort;
      private System.Windows.Forms.TextBox tbIP;
      private System.Windows.Forms.CheckBox cbRememberPassword;
      private System.Windows.Forms.Button btnExit;
      private System.Windows.Forms.TabPage tpMaps;
      private System.Windows.Forms.ComboBox cbMapSource;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.TextBox proxyPassword;
      private System.Windows.Forms.TextBox proxyLogin;
      private System.Windows.Forms.Label label11;
      private System.Windows.Forms.Label label10;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.Label label9;
      private System.Windows.Forms.TextBox textBox1;
      private System.Windows.Forms.TextBox textBox2;
      private System.Windows.Forms.CheckBox cbFullOrgName;
      private System.Windows.Forms.ToolTip toolTip1;
      private System.Windows.Forms.Label label12;
      private System.Windows.Forms.ComboBox cbCultures;
      private System.Windows.Forms.TextBox proxyPort;
      private System.Windows.Forms.TextBox proxyIP;
      private System.Windows.Forms.Label label13;
      private System.Windows.Forms.Label label14;
      private System.Windows.Forms.CheckBox cbOnlyInstance;
      private System.Windows.Forms.Label label15;
      private System.Windows.Forms.ListBox lbHistory;
      private System.Windows.Forms.Label label16;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miDel;
      private System.Windows.Forms.TextBox proxyDomen;
      private System.Windows.Forms.Label label17;
      protected System.Windows.Forms.TabControl tbConfig;
      private System.Windows.Forms.CheckBox cbScriptErrorsAllow;
      public System.Windows.Forms.TabPage tpAdd;
      public System.Windows.Forms.CheckBox cbHighliteOrderMissed;
   }
}
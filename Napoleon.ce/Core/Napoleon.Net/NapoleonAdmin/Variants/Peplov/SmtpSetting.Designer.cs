namespace GRSoft.NapoleonAdmin
{
   partial class SmtpSetting
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SmtpSetting));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbFrom = new System.Windows.Forms.TextBox();
         this.label8 = new System.Windows.Forms.Label();
         this.tbPass = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.label7 = new System.Windows.Forms.Label();
         this.cbSSL = new System.Windows.Forms.CheckBox();
         this.tbPort = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.tbServer = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.tbHeader = new System.Windows.Forms.TextBox();
         this.label4 = new System.Windows.Forms.Label();
         this.tbBody = new System.Windows.Forms.TextBox();
         this.toolStrip1.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(434, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.Refresh;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tbFrom);
         this.groupBox1.Controls.Add(this.label8);
         this.groupBox1.Controls.Add(this.tbPass);
         this.groupBox1.Controls.Add(this.label1);
         this.groupBox1.Controls.Add(this.tbLogin);
         this.groupBox1.Controls.Add(this.label7);
         this.groupBox1.Controls.Add(this.cbSSL);
         this.groupBox1.Controls.Add(this.tbPort);
         this.groupBox1.Controls.Add(this.label6);
         this.groupBox1.Controls.Add(this.tbServer);
         this.groupBox1.Controls.Add(this.label5);
         this.groupBox1.Location = new System.Drawing.Point(6, 30);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(415, 189);
         this.groupBox1.TabIndex = 3;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Настройки почтового сервера";
         // 
         // tbFrom
         // 
         this.tbFrom.Location = new System.Drawing.Point(56, 64);
         this.tbFrom.Name = "tbFrom";
         this.tbFrom.Size = new System.Drawing.Size(313, 20);
         this.tbFrom.TabIndex = 10;
         // 
         // label8
         // 
         this.label8.AutoSize = true;
         this.label8.Location = new System.Drawing.Point(7, 67);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(27, 13);
         this.label8.TabIndex = 9;
         this.label8.Text = "from";
         // 
         // tbPass
         // 
         this.tbPass.Location = new System.Drawing.Point(56, 116);
         this.tbPass.Name = "tbPass";
         this.tbPass.Size = new System.Drawing.Size(313, 20);
         this.tbPass.TabIndex = 8;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 120);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(43, 13);
         this.label1.TabIndex = 7;
         this.label1.Text = "пароль";
         // 
         // tbLogin
         // 
         this.tbLogin.Location = new System.Drawing.Point(56, 89);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(313, 20);
         this.tbLogin.TabIndex = 6;
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(6, 93);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(36, 13);
         this.label7.TabIndex = 5;
         this.label7.Text = "логин";
         // 
         // cbSSL
         // 
         this.cbSSL.AutoSize = true;
         this.cbSSL.Location = new System.Drawing.Point(6, 144);
         this.cbSSL.Name = "cbSSL";
         this.cbSSL.Size = new System.Drawing.Size(46, 17);
         this.cbSSL.TabIndex = 4;
         this.cbSSL.Text = "SSL";
         this.cbSSL.UseVisualStyleBackColor = true;
         // 
         // tbPort
         // 
         this.tbPort.Location = new System.Drawing.Point(56, 40);
         this.tbPort.Name = "tbPort";
         this.tbPort.Size = new System.Drawing.Size(313, 20);
         this.tbPort.TabIndex = 3;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(6, 43);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(30, 13);
         this.label6.TabIndex = 2;
         this.label6.Text = "порт";
         // 
         // tbServer
         // 
         this.tbServer.Location = new System.Drawing.Point(56, 16);
         this.tbServer.Name = "tbServer";
         this.tbServer.Size = new System.Drawing.Size(313, 20);
         this.tbServer.TabIndex = 1;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(6, 19);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(43, 13);
         this.label5.TabIndex = 0;
         this.label5.Text = "сервер";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(9, 222);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(87, 13);
         this.label3.TabIndex = 6;
         this.label3.Text = "Тема рассылки";
         this.label3.Click += new System.EventHandler(this.label3_Click);
         // 
         // tbHeader
         // 
         this.tbHeader.Location = new System.Drawing.Point(6, 238);
         this.tbHeader.Multiline = true;
         this.tbHeader.Name = "tbHeader";
         this.tbHeader.Size = new System.Drawing.Size(415, 45);
         this.tbHeader.TabIndex = 7;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(9, 299);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(90, 13);
         this.label4.TabIndex = 8;
         this.label4.Text = "Текст рассылки";
         // 
         // tbBody
         // 
         this.tbBody.Location = new System.Drawing.Point(6, 315);
         this.tbBody.Multiline = true;
         this.tbBody.Name = "tbBody";
         this.tbBody.Size = new System.Drawing.Size(415, 55);
         this.tbBody.TabIndex = 9;
         // 
         // SmtpSetting
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tbBody);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.tbHeader);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.toolStrip1);
         this.Name = "SmtpSetting";
         this.Size = new System.Drawing.Size(434, 387);
         this.Load += new System.EventHandler(this.SmtpSetting_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbHeader;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbBody;
      private System.Windows.Forms.TextBox tbServer;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox tbPort;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.CheckBox cbSSL;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox tbPass;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.TextBox tbFrom;
      private System.Windows.Forms.Label label8;
   }
}

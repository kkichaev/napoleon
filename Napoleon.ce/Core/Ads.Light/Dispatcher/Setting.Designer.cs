namespace GRSoft.Ads.Dispatcher
{
   partial class Setting
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Setting));
         this.label1 = new System.Windows.Forms.Label();
         this.tbIP = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbPassw = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.numHourStart = new System.Windows.Forms.NumericUpDown();
         this.label4 = new System.Windows.Forms.Label();
         this.numHourEnd = new System.Windows.Forms.NumericUpDown();
         this.btnOK = new System.Windows.Forms.Button();
         this.cbRememberPassw = new System.Windows.Forms.CheckBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.label5 = new System.Windows.Forms.Label();
         this.cbCity = new System.Windows.Forms.ComboBox();
         this.label6 = new System.Windows.Forms.Label();
         this.cbMap = new System.Windows.Forms.ComboBox();
         ((System.ComponentModel.ISupportInitialize)(this.numHourStart)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.numHourEnd)).BeginInit();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(48, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "IP адрес";
         // 
         // tbIP
         // 
         this.tbIP.Location = new System.Drawing.Point(63, 14);
         this.tbIP.Name = "tbIP";
         this.tbIP.Size = new System.Drawing.Size(153, 20);
         this.tbIP.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 44);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(44, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Пароль";
         // 
         // tbPassw
         // 
         this.tbPassw.Location = new System.Drawing.Point(63, 41);
         this.tbPassw.Name = "tbPassw";
         this.tbPassw.PasswordChar = '*';
         this.tbPassw.Size = new System.Drawing.Size(153, 20);
         this.tbPassw.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 81);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(97, 14);
         this.label3.TabIndex = 4;
         this.label3.Text = "Шкала времени c";
         // 
         // numHourStart
         // 
         this.numHourStart.Location = new System.Drawing.Point(115, 79);
         this.numHourStart.Maximum = new decimal(new int[] {
            24,
            0,
            0,
            0});
         this.numHourStart.Name = "numHourStart";
         this.numHourStart.Size = new System.Drawing.Size(44, 20);
         this.numHourStart.TabIndex = 5;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(165, 81);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(19, 14);
         this.label4.TabIndex = 6;
         this.label4.Text = "по";
         // 
         // numHourEnd
         // 
         this.numHourEnd.Location = new System.Drawing.Point(190, 79);
         this.numHourEnd.Maximum = new decimal(new int[] {
            24,
            0,
            0,
            0});
         this.numHourEnd.Name = "numHourEnd";
         this.numHourEnd.Size = new System.Drawing.Size(44, 20);
         this.numHourEnd.TabIndex = 7;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(230, 193);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 8;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // cbRememberPassw
         // 
         this.cbRememberPassw.AutoSize = true;
         this.cbRememberPassw.Location = new System.Drawing.Point(222, 41);
         this.cbRememberPassw.Name = "cbRememberPassw";
         this.cbRememberPassw.Size = new System.Drawing.Size(81, 18);
         this.cbRememberPassw.TabIndex = 9;
         this.cbRememberPassw.Text = "Сохранять";
         this.cbRememberPassw.UseVisualStyleBackColor = true;
         // 
         // groupBox1
         // 
         this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.groupBox1.Controls.Add(this.cbMap);
         this.groupBox1.Controls.Add(this.label6);
         this.groupBox1.Controls.Add(this.cbCity);
         this.groupBox1.Controls.Add(this.label5);
         this.groupBox1.Location = new System.Drawing.Point(12, 105);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(293, 82);
         this.groupBox1.TabIndex = 10;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Основные данные";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(12, 26);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(37, 14);
         this.label5.TabIndex = 0;
         this.label5.Text = "Город";
         // 
         // cbCity
         // 
         this.cbCity.FormattingEnabled = true;
         this.cbCity.Location = new System.Drawing.Point(55, 23);
         this.cbCity.Name = "cbCity";
         this.cbCity.Size = new System.Drawing.Size(167, 22);
         this.cbCity.TabIndex = 1;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(12, 54);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(40, 14);
         this.label6.TabIndex = 2;
         this.label6.Text = "Карты";
         // 
         // cbMap
         // 
         this.cbMap.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbMap.FormattingEnabled = true;
         this.cbMap.Location = new System.Drawing.Point(55, 51);
         this.cbMap.Name = "cbMap";
         this.cbMap.Size = new System.Drawing.Size(167, 22);
         this.cbMap.TabIndex = 3;
         // 
         // Setting
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(314, 221);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.cbRememberPassw);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.numHourEnd);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.numHourStart);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.tbPassw);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbIP);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "Setting";
         this.Text = "Setting";
         this.Load += new System.EventHandler(this.Setting_Load);
         ((System.ComponentModel.ISupportInitialize)(this.numHourStart)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.numHourEnd)).EndInit();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbIP;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPassw;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.NumericUpDown numHourStart;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.NumericUpDown numHourEnd;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.CheckBox cbRememberPassw;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.ComboBox cbCity;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.ComboBox cbMap;
   }
}
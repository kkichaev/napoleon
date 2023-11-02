namespace GRSoft.Ads
{
   partial class FmBrigadeEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBrigadeEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnJobType = new System.Windows.Forms.Button();
         this.tbJobType = new System.Windows.Forms.TextBox();
         this.label4 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbPassword = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.tbPrefix = new System.Windows.Forms.TextBox();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 184);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(384, 40);
         this.panel1.TabIndex = 3;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(301, 10);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(217, 11);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
         this.panel2.Controls.Add(this.tbPrefix);
         this.panel2.Controls.Add(this.label5);
         this.panel2.Controls.Add(this.btnJobType);
         this.panel2.Controls.Add(this.tbJobType);
         this.panel2.Controls.Add(this.label4);
         this.panel2.Controls.Add(this.tbName);
         this.panel2.Controls.Add(this.tbPassword);
         this.panel2.Controls.Add(this.tbLogin);
         this.panel2.Controls.Add(this.label3);
         this.panel2.Controls.Add(this.label2);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(384, 184);
         this.panel2.TabIndex = 7;
         // 
         // btnJobType
         // 
         this.btnJobType.Image = ((System.Drawing.Image)(resources.GetObject("btnJobType.Image")));
         this.btnJobType.Location = new System.Drawing.Point(72, 116);
         this.btnJobType.Name = "btnJobType";
         this.btnJobType.Size = new System.Drawing.Size(32, 33);
         this.btnJobType.TabIndex = 15;
         this.btnJobType.UseVisualStyleBackColor = true;
         this.btnJobType.Click += new System.EventHandler(this.btnJobType_Click);
         // 
         // tbJobType
         // 
         this.tbJobType.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbJobType.BackColor = System.Drawing.SystemColors.Window;
         this.tbJobType.Location = new System.Drawing.Point(110, 123);
         this.tbJobType.Name = "tbJobType";
         this.tbJobType.ReadOnly = true;
         this.tbJobType.Size = new System.Drawing.Size(261, 20);
         this.tbJobType.TabIndex = 14;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(11, 126);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(58, 13);
         this.label4.TabIndex = 13;
         this.label4.Text = "Вид работ";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(98, 59);
         this.tbName.Multiline = true;
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(273, 51);
         this.tbName.TabIndex = 11;
         // 
         // tbPassword
         // 
         this.tbPassword.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPassword.Location = new System.Drawing.Point(98, 33);
         this.tbPassword.Name = "tbPassword";
         this.tbPassword.Size = new System.Drawing.Size(273, 20);
         this.tbPassword.TabIndex = 9;
         // 
         // tbLogin
         // 
         this.tbLogin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbLogin.Location = new System.Drawing.Point(98, 7);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(273, 20);
         this.tbLogin.TabIndex = 7;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(9, 14);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(38, 13);
         this.label3.TabIndex = 12;
         this.label3.Text = "Логин";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(8, 78);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(83, 13);
         this.label2.TabIndex = 10;
         this.label2.Text = "Наименование";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 40);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(45, 13);
         this.label1.TabIndex = 8;
         this.label1.Text = "Пароль";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(12, 164);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(53, 13);
         this.label5.TabIndex = 16;
         this.label5.Text = "Префикс";
         // 
         // tbPrefix
         // 
         this.tbPrefix.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPrefix.Location = new System.Drawing.Point(98, 156);
         this.tbPrefix.Name = "tbPrefix";
         this.tbPrefix.Size = new System.Drawing.Size(273, 20);
         this.tbPrefix.TabIndex = 17;
         // 
         // FmBrigadeEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(384, 224);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBrigadeEdit";
         this.Text = "FmBrigadeEdit";
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnJobType;
      private System.Windows.Forms.TextBox tbJobType;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbPassword;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbPrefix;
      private System.Windows.Forms.Label label5;
   }
}
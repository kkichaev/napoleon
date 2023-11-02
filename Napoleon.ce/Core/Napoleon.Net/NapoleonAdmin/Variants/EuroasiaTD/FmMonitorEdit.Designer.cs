namespace GRSoft.NapoleonAdmin
{
   partial class FmMonitorEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMonitorEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbPassword = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.tvFolders = new System.Windows.Forms.TreeView();
         this.lbScripts = new System.Windows.Forms.CheckedListBox();
         this.button1 = new System.Windows.Forms.Button();
         this.button2 = new System.Windows.Forms.Button();
         this.label4 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(102, 33);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(38, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Логин";
         // 
         // tbLogin
         // 
         this.tbLogin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbLogin.Location = new System.Drawing.Point(152, 30);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(299, 20);
         this.tbLogin.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(95, 64);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(45, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Пароль";
         // 
         // tbPassword
         // 
         this.tbPassword.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPassword.Location = new System.Drawing.Point(152, 61);
         this.tbPassword.Name = "tbPassword";
         this.tbPassword.Size = new System.Drawing.Size(299, 20);
         this.tbPassword.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(53, 95);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(87, 13);
         this.label3.TabIndex = 4;
         this.label3.Text = "Подразделение";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(155, 92);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(296, 21);
         this.cbDivisions.TabIndex = 5;
         // 
         // tvFolders
         // 
         this.tvFolders.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tvFolders.CheckBoxes = true;
         this.tvFolders.Location = new System.Drawing.Point(285, 148);
         this.tvFolders.Name = "tvFolders";
         this.tvFolders.Size = new System.Drawing.Size(256, 304);
         this.tvFolders.TabIndex = 7;
         this.tvFolders.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvFolders_AfterCheck);
         // 
         // lbScripts
         // 
         this.lbScripts.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left)));
         this.lbScripts.FormattingEnabled = true;
         this.lbScripts.IntegralHeight = false;
         this.lbScripts.Location = new System.Drawing.Point(12, 148);
         this.lbScripts.Name = "lbScripts";
         this.lbScripts.Size = new System.Drawing.Size(256, 304);
         this.lbScripts.Sorted = true;
         this.lbScripts.TabIndex = 8;
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button1.Location = new System.Drawing.Point(367, 470);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 9;
         this.button1.Text = "OK";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // button2
         // 
         this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button2.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button2.Location = new System.Drawing.Point(461, 470);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 10;
         this.button2.Text = "Отмена";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(12, 129);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(115, 13);
         this.label4.TabIndex = 11;
         this.label4.Text = "Доступные сценарии";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(285, 129);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(77, 13);
         this.label5.TabIndex = 12;
         this.label5.Text = "Папки товара";
         // 
         // FmMonitorEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.button2;
         this.ClientSize = new System.Drawing.Size(556, 505);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.button2);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.lbScripts);
         this.Controls.Add(this.tvFolders);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.tbPassword);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbLogin);
         this.Controls.Add(this.label1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMonitorEdit";
         this.Text = "Пользователь монитора";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPassword;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.TreeView tvFolders;
      private System.Windows.Forms.CheckedListBox lbScripts;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
   }
}
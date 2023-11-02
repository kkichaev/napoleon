namespace GRSoft.NapoleonManager
{
   partial class EditUser
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(EditUser));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel2 = new System.Windows.Forms.Panel();
         this.tbPassw = new System.Windows.Forms.TextBox();
         this.tbLogin = new System.Windows.Forms.TextBox();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 104);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(329, 49);
         this.panel1.TabIndex = 0;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(240, 12);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(146, 12);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.tbPassw);
         this.panel2.Controls.Add(this.tbLogin);
         this.panel2.Controls.Add(this.tbName);
         this.panel2.Controls.Add(this.label3);
         this.panel2.Controls.Add(this.label2);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(329, 104);
         this.panel2.TabIndex = 1;
         // 
         // tbPassw
         // 
         this.tbPassw.Location = new System.Drawing.Point(57, 66);
         this.tbPassw.Name = "tbPassw";
         this.tbPassw.Size = new System.Drawing.Size(258, 20);
         this.tbPassw.TabIndex = 5;
         // 
         // tbLogin
         // 
         this.tbLogin.Location = new System.Drawing.Point(56, 35);
         this.tbLogin.Name = "tbLogin";
         this.tbLogin.Size = new System.Drawing.Size(259, 20);
         this.tbLogin.TabIndex = 4;
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(57, 7);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(258, 20);
         this.tbName.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(4, 67);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(44, 14);
         this.label3.TabIndex = 2;
         this.label3.Text = "Пароль";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(3, 38);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(36, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Логин";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(3, 10);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(30, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Имя";
         // 
         // EditUser
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(329, 153);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "EditUser";
         this.Text = "Пользователь";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.EditUser_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbLogin;
      private System.Windows.Forms.TextBox tbPassw;
   }
}
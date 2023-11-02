namespace GRSoft.NapoleonManager
{
   partial class About
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(About));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnClose = new System.Windows.Forms.Button();
         this.lblMail = new System.Windows.Forms.LinkLabel();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.lblSite = new System.Windows.Forms.LinkLabel();
         this.lblVersion = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.btnClose);
         this.panel1.Controls.Add(this.lblMail);
         this.panel1.Controls.Add(this.label5);
         this.panel1.Controls.Add(this.label4);
         this.panel1.Controls.Add(this.lblSite);
         this.panel1.Controls.Add(this.lblVersion);
         this.panel1.Controls.Add(this.label3);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(292, 196);
         this.panel1.TabIndex = 0;
         // 
         // btnClose
         // 
         this.btnClose.Location = new System.Drawing.Point(100, 166);
         this.btnClose.Name = "btnClose";
         this.btnClose.Size = new System.Drawing.Size(75, 23);
         this.btnClose.TabIndex = 9;
         this.btnClose.Text = "Закрыть";
         this.btnClose.UseVisualStyleBackColor = true;
         this.btnClose.Click += new System.EventHandler(this.btnClose_Click);
         // 
         // lblMail
         // 
         this.lblMail.AutoSize = true;
         this.lblMail.Location = new System.Drawing.Point(54, 136);
         this.lblMail.Name = "lblMail";
         this.lblMail.Size = new System.Drawing.Size(81, 14);
         this.lblMail.TabIndex = 8;
         this.lblMail.TabStop = true;
         this.lblMail.Text = "info@grsoft.ru ";
         this.lblMail.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lblMail_LinkClicked);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(10, 136);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(41, 14);
         this.label5.TabIndex = 7;
         this.label5.Text = "Почта:";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(10, 106);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(35, 14);
         this.label4.TabIndex = 6;
         this.label4.Text = "Сайт:";
         // 
         // lblSite
         // 
         this.lblSite.AutoSize = true;
         this.lblSite.Location = new System.Drawing.Point(52, 106);
         this.lblSite.Name = "lblSite";
         this.lblSite.Size = new System.Drawing.Size(76, 14);
         this.lblSite.TabIndex = 5;
         this.lblSite.TabStop = true;
         this.lblSite.Text = "http://grsoft.ru";
         this.lblSite.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lblSite_LinkClicked);
         // 
         // lblVersion
         // 
         this.lblVersion.AutoSize = true;
         this.lblVersion.Location = new System.Drawing.Point(59, 76);
         this.lblVersion.Name = "lblVersion";
         this.lblVersion.Size = new System.Drawing.Size(54, 14);
         this.lblVersion.TabIndex = 3;
         this.lblVersion.Text = "lblVersion";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(10, 76);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(47, 14);
         this.label3.TabIndex = 2;
         this.label3.Text = "Версия:";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(10, 46);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(97, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "+7 (4852) 599-368";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(10, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(90, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "АДС \"Наполеон\"";
         // 
         // About
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(292, 196);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "About";
         this.ShowInTaskbar = false;
         this.Text = "О программе";
         this.Load += new System.EventHandler(this.About_Load);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label lblVersion;
      private System.Windows.Forms.LinkLabel lblSite;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.LinkLabel lblMail;
      private System.Windows.Forms.Button btnClose;
   }
}
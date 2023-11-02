namespace GRSoft.NapoleonManager
{
   partial class FmOrgEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.tbFormat = new System.Windows.Forms.TextBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.button1 = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(0, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(47, 15);
         this.label1.TabIndex = 0;
         this.label1.Text = "Название";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(0, 32);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 15);
         this.label2.TabIndex = 1;
         this.label2.Text = "Адрес";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(0, 58);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(39, 15);
         this.label3.TabIndex = 2;
         this.label3.Text = "Формат";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(53, 6);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(476, 20);
         this.tbName.TabIndex = 3;
         // 
         // tbAddress
         // 
         this.tbAddress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbAddress.Location = new System.Drawing.Point(53, 32);
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.Size = new System.Drawing.Size(476, 20);
         this.tbAddress.TabIndex = 4;
         // 
         // tbFormat
         // 
         this.tbFormat.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbFormat.Location = new System.Drawing.Point(53, 58);
         this.tbFormat.Name = "tbFormat";
         this.tbFormat.Size = new System.Drawing.Size(476, 20);
         this.tbFormat.TabIndex = 5;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.button1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 96);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(541, 48);
         this.panel1.TabIndex = 6;
         // 
         // button1
         // 
         this.button1.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button1.Location = new System.Drawing.Point(454, 13);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "ОК";
         this.button1.UseVisualStyleBackColor = true;
         // 
         // FmOrgEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(541, 144);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.tbFormat);
         this.Controls.Add(this.tbAddress);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmOrgEdit";
         this.Text = "Изменить клиента";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmOrgEdit_FormClosed);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.TextBox tbFormat;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button button1;
   }
}
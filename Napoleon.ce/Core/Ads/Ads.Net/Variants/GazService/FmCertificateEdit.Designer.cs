namespace GRSoft.Ads
{
   partial class FmCertificateEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCertificateEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.tbNumber = new System.Windows.Forms.TextBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOk = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(74, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Номер/Серия";
         // 
         // tbNumber
         // 
         this.tbNumber.Location = new System.Drawing.Point(16, 31);
         this.tbNumber.Name = "tbNumber";
         this.tbNumber.Size = new System.Drawing.Size(271, 20);
         this.tbNumber.TabIndex = 1;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOk);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 63);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(296, 35);
         this.panel1.TabIndex = 2;
         // 
         // btnOk
         // 
         this.btnOk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOk.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOk.Location = new System.Drawing.Point(217, 6);
         this.btnOk.Name = "btnOk";
         this.btnOk.Size = new System.Drawing.Size(75, 23);
         this.btnOk.TabIndex = 1;
         this.btnOk.Text = "OK";
         this.btnOk.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(136, 6);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // FmSertificateEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(296, 98);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.tbNumber);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSertificateEdit";
         this.Text = "Добавить";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSertificateEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbNumber;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOk;
      private System.Windows.Forms.Button btnCancel;
   }
}
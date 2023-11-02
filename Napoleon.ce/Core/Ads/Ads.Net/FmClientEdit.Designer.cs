namespace GRSoft.Ads
{
   partial class FmClientEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClientEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.btnKladr = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 96);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(377, 40);
         this.panel1.TabIndex = 1;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(206, 10);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(287, 10);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Наименование";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 51);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(39, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Адрес";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(101, 6);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(270, 20);
         this.tbName.TabIndex = 0;
         // 
         // tbAddress
         // 
         this.tbAddress.BackColor = System.Drawing.SystemColors.Window;
         this.tbAddress.Location = new System.Drawing.Point(101, 52);
         this.tbAddress.Multiline = true;
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.ReadOnly = true;
         this.tbAddress.Size = new System.Drawing.Size(270, 38);
         this.tbAddress.TabIndex = 1;
         // 
         // btnKladr
         // 
         this.btnKladr.Location = new System.Drawing.Point(12, 67);
         this.btnKladr.Name = "btnKladr";
         this.btnKladr.Size = new System.Drawing.Size(75, 23);
         this.btnKladr.TabIndex = 3;
         this.btnKladr.Text = "КЛАДР";
         this.btnKladr.UseVisualStyleBackColor = true;
         this.btnKladr.Click += new System.EventHandler(this.btnKladr_Click);
         // 
         // FmClientEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(377, 136);
         this.Controls.Add(this.btnKladr);
         this.Controls.Add(this.tbAddress);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClientEdit";
         this.Text = "FmClientEdit";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmClientEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.Button btnKladr;
   }
}
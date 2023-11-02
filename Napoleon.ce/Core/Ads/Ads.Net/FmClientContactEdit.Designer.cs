namespace GRSoft.Ads
{
   partial class FmClientContactEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClientContactEdit));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbPhone = new System.Windows.Forms.TextBox();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(30, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Имя";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 45);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(50, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Телефон";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 78);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(331, 43);
         this.panel1.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(244, 8);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 25);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(163, 8);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 25);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(62, 12);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(261, 20);
         this.tbName.TabIndex = 0;
         // 
         // tbPhone
         // 
         this.tbPhone.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPhone.Location = new System.Drawing.Point(63, 45);
         this.tbPhone.Name = "tbPhone";
         this.tbPhone.Size = new System.Drawing.Size(260, 20);
         this.tbPhone.TabIndex = 1;
         // 
         // FmClientContactEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(331, 121);
         this.Controls.Add(this.tbPhone);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClientContactEdit";
         this.Text = "FmClientContactEdit";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbPhone;
   }
}
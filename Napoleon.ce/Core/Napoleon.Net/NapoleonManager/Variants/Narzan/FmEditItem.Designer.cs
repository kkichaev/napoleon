namespace GRSoft.NapoleonManager
{
   partial class FmEditItem
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmEditItem));
         this.lblName = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.tbQty = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbFace = new System.Windows.Forms.TextBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // lblName
         // 
         this.lblName.AutoSize = true;
         this.lblName.Location = new System.Drawing.Point(10, 7);
         this.lblName.Name = "lblName";
         this.lblName.Size = new System.Drawing.Size(35, 14);
         this.lblName.TabIndex = 0;
         this.lblName.Text = "label1";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(10, 29);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(27, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Ост";
         // 
         // tbQty
         // 
         this.tbQty.Location = new System.Drawing.Point(10, 47);
         this.tbQty.Name = "tbQty";
         this.tbQty.Size = new System.Drawing.Size(100, 20);
         this.tbQty.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(165, 29);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "Фейс";
         // 
         // tbFace
         // 
         this.tbFace.Location = new System.Drawing.Point(168, 46);
         this.tbFace.Name = "tbFace";
         this.tbFace.Size = new System.Drawing.Size(112, 20);
         this.tbFace.TabIndex = 4;
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.tbFace);
         this.panel1.Controls.Add(this.lblName);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Controls.Add(this.tbQty);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(296, 126);
         this.panel1.TabIndex = 5;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 77);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(296, 49);
         this.panel2.TabIndex = 6;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(211, 12);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(129, 12);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // FmEditItem
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(296, 126);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmEditItem";
         this.Text = "Изменить";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmEditItem_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label lblName;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbQty;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbFace;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
   }
}
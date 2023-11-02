namespace GRSoft.Ads
{
   partial class FmJobsTypeEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmJobsTypeEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.pnlColor = new System.Windows.Forms.Panel();
         this.lblTip = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.colorDialog1 = new System.Windows.Forms.ColorDialog();
         this.panel1.SuspendLayout();
         this.pnlColor.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
         this.panel1.Controls.Add(this.pnlColor);
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(281, 101);
         this.panel1.TabIndex = 0;
         // 
         // pnlColor
         // 
         this.pnlColor.BackColor = System.Drawing.Color.Black;
         this.pnlColor.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.pnlColor.Controls.Add(this.lblTip);
         this.pnlColor.Location = new System.Drawing.Point(62, 64);
         this.pnlColor.Name = "pnlColor";
         this.pnlColor.Size = new System.Drawing.Size(213, 29);
         this.pnlColor.TabIndex = 3;
         this.pnlColor.Click += new System.EventHandler(this.pnlColor_Click);
         // 
         // lblTip
         // 
         this.lblTip.AutoSize = true;
         this.lblTip.ForeColor = System.Drawing.Color.White;
         this.lblTip.Location = new System.Drawing.Point(21, 6);
         this.lblTip.Name = "lblTip";
         this.lblTip.Size = new System.Drawing.Size(166, 13);
         this.lblTip.TabIndex = 0;
         this.lblTip.Text = "Щелкните, чтобы выбрать цвет";
         this.lblTip.Click += new System.EventHandler(this.pnlColor_Click);
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(13, 30);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(262, 20);
         this.tbName.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(16, 70);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(32, 13);
         this.label2.TabIndex = 1;
         this.label2.Text = "Цвет";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Наименование";
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 101);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(281, 40);
         this.panel2.TabIndex = 1;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(105, 8);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(195, 7);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // FmJobsTypeEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(281, 141);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmJobsTypeEdit";
         this.Text = "FmJobsTypeEdit";
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.pnlColor.ResumeLayout(false);
         this.pnlColor.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Panel pnlColor;
      private System.Windows.Forms.ColorDialog colorDialog1;
      private System.Windows.Forms.Label lblTip;
   }
}
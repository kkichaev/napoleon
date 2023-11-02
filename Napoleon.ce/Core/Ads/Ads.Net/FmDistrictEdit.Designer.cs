namespace GRSoft.Ads
{
   partial class FmDistrictEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrictEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbCode = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.bntCancel = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.tbCode);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(280, 103);
         this.panel1.TabIndex = 0;
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Left | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(105, 35);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(165, 20);
         this.tbName.TabIndex = 1;
         // 
         // tbCode
         // 
         this.tbCode.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Left | System.Windows.Forms.AnchorStyles.Right)));
         this.tbCode.Location = new System.Drawing.Point(105, 9);
         this.tbCode.Name = "tbCode";
         this.tbCode.Size = new System.Drawing.Size(165, 20);
         this.tbCode.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 35);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(83, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Наименование";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(26, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Код";
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Controls.Add(this.bntCancel);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 63);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(280, 40);
         this.panel2.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(195, 9);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // bntCancel
         // 
         this.bntCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.bntCancel.Location = new System.Drawing.Point(105, 9);
         this.bntCancel.Name = "bntCancel";
         this.bntCancel.Size = new System.Drawing.Size(75, 23);
         this.bntCancel.TabIndex = 0;
         this.bntCancel.Text = "Отменить";
         this.bntCancel.UseVisualStyleBackColor = true;
         // 
         // FmDistrictEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(280, 103);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrictEdit";
         this.Text = "Районы";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmDistrictEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbCode;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button bntCancel;
   }
}
namespace GRSoft.Ads
{
   partial class FmSMSTemplate
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSMSTemplate));
         this.tbText = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.lblCount = new System.Windows.Forms.Label();
         this.bntOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // tbText
         // 
         this.tbText.Location = new System.Drawing.Point(3, 2);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(415, 123);
         this.tbText.TabIndex = 0;
         this.tbText.TextChanged += new System.EventHandler(this.tbText_TextChanged);
         this.tbText.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbText_KeyDown);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(3, 139);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(98, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Осталось знаков:";
         // 
         // lblCount
         // 
         this.lblCount.AutoSize = true;
         this.lblCount.Location = new System.Drawing.Point(108, 139);
         this.lblCount.Name = "lblCount";
         this.lblCount.Size = new System.Drawing.Size(45, 13);
         this.lblCount.TabIndex = 2;
         this.lblCount.Text = "lblCount";
         // 
         // bntOK
         // 
         this.bntOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.bntOK.Location = new System.Drawing.Point(262, 134);
         this.bntOK.Name = "bntOK";
         this.bntOK.Size = new System.Drawing.Size(75, 23);
         this.bntOK.TabIndex = 3;
         this.bntOK.Text = "OK";
         this.bntOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(343, 134);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 4;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // FmSMSTemplate
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(422, 165);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.bntOK);
         this.Controls.Add(this.lblCount);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbText);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSMSTemplate";
         this.Text = "СМС сообщение";
         this.Load += new System.EventHandler(this.FmSMSTemplate_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSMSTemplate_FormClosing);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label lblCount;
      private System.Windows.Forms.Button bntOK;
      private System.Windows.Forms.Button btnCancel;
   }
}
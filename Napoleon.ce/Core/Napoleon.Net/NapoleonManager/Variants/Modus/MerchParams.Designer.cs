namespace GRSoft.NapoleonManager
{
   partial class MerchParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MerchParams));
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(32, 41);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 13);
         this.label3.TabIndex = 23;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 15);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 13);
         this.label2.TabIndex = 22;
         this.label2.Text = "Дата с";
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(57, 38);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(167, 20);
         this.dtpEnd.TabIndex = 21;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(57, 12);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 20;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(148, 76);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 25;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(67, 76);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 24;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // MerchParams
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(244, 111);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "MerchParams";
         this.Text = "Отчет мерчендайзинг";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.WorkTimeParams_FormClosing);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;
   }
}
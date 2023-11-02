namespace GRSoft.NapoleonManager
{
   partial class FmPriceActionEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceActionEdit));
         this.ok = new System.Windows.Forms.Button();
         this.text = new System.Windows.Forms.TextBox();
         this.startDate = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.endDate = new System.Windows.Forms.DateTimePicker();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(224, 224);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 1;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // text
         // 
         this.text.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.text.Location = new System.Drawing.Point(12, 59);
         this.text.Multiline = true;
         this.text.Name = "text";
         this.text.Size = new System.Drawing.Size(287, 159);
         this.text.TabIndex = 0;
         // 
         // startDate
         // 
         this.startDate.Location = new System.Drawing.Point(45, 7);
         this.startDate.Name = "startDate";
         this.startDate.Size = new System.Drawing.Size(145, 20);
         this.startDate.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(20, 10);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(14, 13);
         this.label1.TabIndex = 3;
         this.label1.Text = "С";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(20, 36);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(21, 13);
         this.label2.TabIndex = 5;
         this.label2.Text = "По";
         // 
         // endDate
         // 
         this.endDate.Location = new System.Drawing.Point(45, 33);
         this.endDate.Name = "endDate";
         this.endDate.Size = new System.Drawing.Size(145, 20);
         this.endDate.TabIndex = 4;
         // 
         // FmPriceActionEdit
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Inherit;
         this.ClientSize = new System.Drawing.Size(311, 255);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.endDate);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.startDate);
         this.Controls.Add(this.text);
         this.Controls.Add(this.ok);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceActionEdit";
         this.Text = "Описание акции";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.TextBox text;
      private System.Windows.Forms.DateTimePicker startDate;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker endDate;
   }
}
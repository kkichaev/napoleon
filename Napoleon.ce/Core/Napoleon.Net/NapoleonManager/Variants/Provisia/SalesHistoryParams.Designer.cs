namespace GRSoft.NapoleonManager
{
   partial class SalesHistoryParams
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
         this.ok = new System.Windows.Forms.Button();
         this.cancel = new System.Windows.Forms.Button();
         this.dateFrom = new System.Windows.Forms.DateTimePicker();
         this.dateTill = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.kagents = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(193, 142);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 0;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(274, 142);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 1;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // dateFrom
         // 
         this.dateFrom.Location = new System.Drawing.Point(82, 19);
         this.dateFrom.Name = "dateFrom";
         this.dateFrom.Size = new System.Drawing.Size(144, 20);
         this.dateFrom.TabIndex = 2;
         // 
         // dateTill
         // 
         this.dateTill.Location = new System.Drawing.Point(82, 46);
         this.dateTill.Name = "dateTill";
         this.dateTill.Size = new System.Drawing.Size(144, 20);
         this.dateTill.TabIndex = 3;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(22, 25);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(54, 13);
         this.label1.TabIndex = 4;
         this.label1.Text = "Период с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(57, 52);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 5;
         this.label2.Text = "по";
         // 
         // kagents
         // 
         this.kagents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.kagents.FormattingEnabled = true;
         this.kagents.Location = new System.Drawing.Point(82, 89);
         this.kagents.Name = "kagents";
         this.kagents.Size = new System.Drawing.Size(267, 21);
         this.kagents.TabIndex = 6;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(11, 92);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(65, 13);
         this.label3.TabIndex = 7;
         this.label3.Text = "Контрагент";
         // 
         // SalesHistoryParams
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.cancel;
         this.ClientSize = new System.Drawing.Size(361, 177);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.kagents);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dateTill);
         this.Controls.Add(this.dateFrom);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Name = "SalesHistoryParams";
         this.Text = "История продаж параметры";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.DateTimePicker dateFrom;
      private System.Windows.Forms.DateTimePicker dateTill;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox kagents;
      private System.Windows.Forms.Label label3;
   }
}
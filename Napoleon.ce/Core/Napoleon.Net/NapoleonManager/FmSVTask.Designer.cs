namespace GRSoft.NapoleonManager
{
   partial class FmSVTask
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSVTask));
         this.label1 = new System.Windows.Forms.Label();
         this.date = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.task = new System.Windows.Forms.TextBox();
         this.ok = new System.Windows.Forms.Button();
         this.label2 = new System.Windows.Forms.Label();
         this.category = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 11);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(33, 13);
         this.label1.TabIndex = 4;
         this.label1.Text = "Дата";
         // 
         // date
         // 
         this.date.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.date.Location = new System.Drawing.Point(12, 27);
         this.date.Name = "date";
         this.date.Size = new System.Drawing.Size(306, 20);
         this.date.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(13, 95);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(43, 13);
         this.label3.TabIndex = 10;
         this.label3.Text = "Задача";
         // 
         // task
         // 
         this.task.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.task.Location = new System.Drawing.Point(12, 111);
         this.task.Multiline = true;
         this.task.Name = "task";
         this.task.Size = new System.Drawing.Size(306, 76);
         this.task.TabIndex = 9;
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(243, 193);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 8;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(13, 54);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(60, 13);
         this.label2.TabIndex = 11;
         this.label2.Text = "Категория";
         // 
         // category
         // 
         this.category.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.category.FormattingEnabled = true;
         this.category.Location = new System.Drawing.Point(16, 71);
         this.category.Name = "category";
         this.category.Size = new System.Drawing.Size(302, 21);
         this.category.TabIndex = 12;
         // 
         // FmSVTask
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(330, 228);
         this.Controls.Add(this.category);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.task);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.date);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSVTask";
         this.Text = "Задача";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker date;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox task;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox category;
   }
}
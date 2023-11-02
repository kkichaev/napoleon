namespace GRSoft.NapoleonManager
{
   partial class FmTaskAdd
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskAdd));
         this.ok = new System.Windows.Forms.Button();
         this.date = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.orgTitle = new System.Windows.Forms.TextBox();
         this.selectOrg = new System.Windows.Forms.Button();
         this.task = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(230, 207);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 0;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // date
         // 
         this.date.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.date.Location = new System.Drawing.Point(15, 25);
         this.date.Name = "date";
         this.date.Size = new System.Drawing.Size(290, 20);
         this.date.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(33, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Дата";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 53);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(65, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "Контрагент";
         // 
         // orgTitle
         // 
         this.orgTitle.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.orgTitle.Enabled = false;
         this.orgTitle.Location = new System.Drawing.Point(15, 72);
         this.orgTitle.Name = "orgTitle";
         this.orgTitle.Size = new System.Drawing.Size(256, 20);
         this.orgTitle.TabIndex = 4;
         // 
         // selectOrg
         // 
         this.selectOrg.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.selectOrg.Location = new System.Drawing.Point(277, 69);
         this.selectOrg.Name = "selectOrg";
         this.selectOrg.Size = new System.Drawing.Size(28, 23);
         this.selectOrg.TabIndex = 5;
         this.selectOrg.Text = "...";
         this.selectOrg.UseVisualStyleBackColor = true;
         this.selectOrg.Click += new System.EventHandler(this.selectOrg_Click);
         // 
         // task
         // 
         this.task.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.task.Location = new System.Drawing.Point(15, 118);
         this.task.Multiline = true;
         this.task.Name = "task";
         this.task.Size = new System.Drawing.Size(290, 74);
         this.task.TabIndex = 6;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 102);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(43, 13);
         this.label3.TabIndex = 7;
         this.label3.Text = "Задача";
         // 
         // FmTaskAdd
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(317, 242);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.task);
         this.Controls.Add(this.selectOrg);
         this.Controls.Add(this.orgTitle);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.date);
         this.Controls.Add(this.ok);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTaskAdd";
         this.Text = "Новое задание";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.DateTimePicker date;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox orgTitle;
      private System.Windows.Forms.Button selectOrg;
      private System.Windows.Forms.TextBox task;
      private System.Windows.Forms.Label label3;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmAgentOrgTask
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentOrgTask));
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.orgName = new System.Windows.Forms.Label();
         this.task = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(12, 192);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 1;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(232, 192);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 2;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // orgName
         // 
         this.orgName.AutoSize = true;
         this.orgName.Location = new System.Drawing.Point(12, 9);
         this.orgName.Name = "orgName";
         this.orgName.Size = new System.Drawing.Size(35, 13);
         this.orgName.TabIndex = 2;
         this.orgName.Text = "label1";
         // 
         // task
         // 
         this.task.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.task.Location = new System.Drawing.Point(13, 38);
         this.task.Multiline = true;
         this.task.Name = "task";
         this.task.Size = new System.Drawing.Size(294, 137);
         this.task.TabIndex = 0;
         // 
         // FmAgentOrgTask
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.cancel;
         this.ClientSize = new System.Drawing.Size(319, 227);
         this.Controls.Add(this.task);
         this.Controls.Add(this.orgName);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.cancel);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAgentOrgTask";
         this.Text = "Введите задачу";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Label orgName;
      private System.Windows.Forms.TextBox task;
   }
}
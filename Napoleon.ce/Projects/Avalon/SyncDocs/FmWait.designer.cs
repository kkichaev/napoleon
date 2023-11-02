namespace SyncDocs
{
   partial class FmWait
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
         this.textLabel = new System.Windows.Forms.Label();
         this.btnCancel = new System.Windows.Forms.Button();
         this.progress = new System.Windows.Forms.ProgressBar();
         this.SuspendLayout();
         // 
         // textLabel
         // 
         this.textLabel.Location = new System.Drawing.Point(12, 9);
         this.textLabel.Name = "textLabel";
         this.textLabel.Size = new System.Drawing.Size(295, 13);
         this.textLabel.TabIndex = 0;
         this.textLabel.Text = "Подождите, идет обработка данных...";
         this.textLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
         this.textLabel.UseWaitCursor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.Location = new System.Drawing.Point(99, 77);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(113, 24);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         this.btnCancel.UseWaitCursor = true;
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // progress
         // 
         this.progress.Location = new System.Drawing.Point(12, 38);
         this.progress.Name = "progress";
         this.progress.Size = new System.Drawing.Size(295, 23);
         this.progress.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
         this.progress.TabIndex = 2;
         this.progress.UseWaitCursor = true;
         // 
         // FmWait
         // 
         this.AcceptButton = this.btnCancel;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(319, 113);
         this.ControlBox = false;
         this.Controls.Add(this.progress);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.textLabel);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
         this.Name = "FmWait";
         this.ShowIcon = false;
         this.ShowInTaskbar = false;
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
         this.Text = "fmWait";
         this.UseWaitCursor = true;
         this.ResumeLayout(false);

      }

      #endregion

      public System.Windows.Forms.Label textLabel;
      public System.Windows.Forms.Button btnCancel;
      public System.Windows.Forms.ProgressBar progress;
   }
}
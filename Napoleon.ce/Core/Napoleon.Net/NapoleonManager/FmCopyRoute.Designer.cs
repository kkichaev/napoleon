namespace GRSoft.NapoleonManager
{
   partial class FmCopyRoute
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCopyRoute));
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(134, 23);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(273, 21);
         this.cbAgents.Sorted = true;
         this.cbAgents.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.Location = new System.Drawing.Point(182, 67);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "Выполнить";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(25, 26);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(94, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Выберите агента";
         // 
         // FmCopyRoute
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(452, 111);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.cbAgents);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "FmCopyRoute";
         this.Text = "Копирование маршрута";
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.FmCopyRoute_KeyDown);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
   }
}
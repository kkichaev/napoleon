namespace GRSoft.NapoleonManager
{
   partial class EdBoolean
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.tbTrue = new System.Windows.Forms.TextBox();
         this.tbFalse = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // tbTrue
         // 
         this.tbTrue.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbTrue.Location = new System.Drawing.Point(54, 13);
         this.tbTrue.Name = "tbTrue";
         this.tbTrue.Size = new System.Drawing.Size(241, 20);
         this.tbTrue.TabIndex = 0;
         // 
         // tbFalse
         // 
         this.tbFalse.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbFalse.Location = new System.Drawing.Point(54, 51);
         this.tbFalse.Name = "tbFalse";
         this.tbFalse.Size = new System.Drawing.Size(241, 20);
         this.tbFalse.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(4, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(44, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Истина";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(4, 54);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(35, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "Ложь";
         // 
         // EdBoolean
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbFalse);
         this.Controls.Add(this.tbTrue);
         this.Name = "EdBoolean";
         this.Size = new System.Drawing.Size(302, 172);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox tbTrue;
      private System.Windows.Forms.TextBox tbFalse;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
   }
}

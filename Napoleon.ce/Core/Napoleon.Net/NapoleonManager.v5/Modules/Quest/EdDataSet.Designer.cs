namespace GRSoft.NapoleonManager
{
   partial class EdDataSet
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
         this.cbDataSet = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // cbDataSet
         // 
         this.cbDataSet.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDataSet.FormattingEnabled = true;
         this.cbDataSet.Items.AddRange(new object[] {
            "Организация",
            "Прайс"});
         this.cbDataSet.Location = new System.Drawing.Point(3, 16);
         this.cbDataSet.Name = "cbDataSet";
         this.cbDataSet.Size = new System.Drawing.Size(144, 21);
         this.cbDataSet.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(0, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(67, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Справочник";
         // 
         // EdDataSet
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbDataSet);
         this.Name = "EdDataSet";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbDataSet;
      private System.Windows.Forms.Label label1;

   }
}

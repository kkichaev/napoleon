namespace GRSoft.NapoleonManager
{
   partial class AddConfigData
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
         this.label1 = new System.Windows.Forms.Label();
         this.tbOrgCode = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(20, 23);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(140, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Код \"нового контрагента\"";
         // 
         // tbOrgCode
         // 
         this.tbOrgCode.Location = new System.Drawing.Point(166, 20);
         this.tbOrgCode.Name = "tbOrgCode";
         this.tbOrgCode.Size = new System.Drawing.Size(152, 20);
         this.tbOrgCode.TabIndex = 1;
         // 
         // AddConfigData
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tbOrgCode);
         this.Controls.Add(this.label1);
         this.Name = "AddConfigData";
         this.Size = new System.Drawing.Size(346, 62);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      public System.Windows.Forms.TextBox tbOrgCode;
   }
}

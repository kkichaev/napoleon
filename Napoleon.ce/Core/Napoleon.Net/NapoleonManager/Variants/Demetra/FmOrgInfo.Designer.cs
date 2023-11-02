namespace GRSoft.NapoleonManager
{
   partial class FmOrgInfo
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgInfo));
         this.lblName = new System.Windows.Forms.LinkLabel();
         this.lblAddress = new System.Windows.Forms.LinkLabel();
         this.SuspendLayout();
         // 
         // lblName
         // 
         this.lblName.AutoSize = true;
         this.lblName.Location = new System.Drawing.Point(12, 9);
         this.lblName.Name = "lblName";
         this.lblName.Size = new System.Drawing.Size(55, 13);
         this.lblName.TabIndex = 0;
         this.lblName.TabStop = true;
         this.lblName.Text = "linkLabel1";
         this.lblName.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lblName_LinkClicked);
         // 
         // lblAddress
         // 
         this.lblAddress.AutoSize = true;
         this.lblAddress.Location = new System.Drawing.Point(12, 44);
         this.lblAddress.Name = "lblAddress";
         this.lblAddress.Size = new System.Drawing.Size(55, 13);
         this.lblAddress.TabIndex = 1;
         this.lblAddress.TabStop = true;
         this.lblAddress.Text = "linkLabel2";
         this.lblAddress.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lblName_LinkClicked);
         // 
         // FmOrgInfo
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(599, 77);
         this.Controls.Add(this.lblAddress);
         this.Controls.Add(this.lblName);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgInfo";
         this.Text = "Контрагент";
         this.Load += new System.EventHandler(this.FmOrgInfo_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.LinkLabel lblName;
      private System.Windows.Forms.LinkLabel lblAddress;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmAddrShow
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAddrShow));
         this.wb = new System.Windows.Forms.WebBrowser();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // wb
         // 
         this.wb.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wb.Location = new System.Drawing.Point(7, 7);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.ScriptErrorsSuppressed = true;
         this.wb.Size = new System.Drawing.Size(676, 499);
         this.wb.TabIndex = 0;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.wb);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(690, 513);
         this.panel1.TabIndex = 1;
         // 
         // FmAddrShow
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(690, 513);
         this.Controls.Add(this.panel1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAddrShow";
         this.Text = "FmAddrShow";
         this.Shown += new System.EventHandler(this.FmAddrShow_Shown);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmAddrShow_FormClosed);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.WebBrowser wb;
      private System.Windows.Forms.Panel panel1;
   }
}
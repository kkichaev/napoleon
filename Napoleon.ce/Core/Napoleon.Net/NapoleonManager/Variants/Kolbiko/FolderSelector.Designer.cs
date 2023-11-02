namespace GRSoft.NapoleonManager
{
   partial class FolderSelector
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FolderSelector));
         this.treeFolders = new System.Windows.Forms.TreeView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnOK = new System.Windows.Forms.ToolStripButton();
         this.btnCancel = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // treeFolders
         // 
         this.treeFolders.CheckBoxes = true;
         this.treeFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.treeFolders.Location = new System.Drawing.Point(0, 25);
         this.treeFolders.Name = "treeFolders";
         this.treeFolders.Size = new System.Drawing.Size(349, 349);
         this.treeFolders.TabIndex = 0;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnOK,
            this.btnCancel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(349, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnOK
         // 
         this.btnOK.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOK.Image = ((System.Drawing.Image)(resources.GetObject("btnOK.Image")));
         this.btnOK.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(23, 22);
         this.btnOK.Text = "Сохранить";
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCancel.Image = ((System.Drawing.Image)(resources.GetObject("btnCancel.Image")));
         this.btnCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(23, 22);
         this.btnCancel.Text = "Отменить";
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // FolderSelector
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(349, 374);
         this.Controls.Add(this.treeFolders);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FolderSelector";
         this.Text = "Выберите папки";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TreeView treeFolders;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnOK;
      private System.Windows.Forms.ToolStripButton btnCancel;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmSelectDivision
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectDivision));
         this.treeView = new System.Windows.Forms.TreeView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbCancel = new System.Windows.Forms.ToolStripButton();
         this.tsbOK = new System.Windows.Forms.ToolStripButton();
         this.btnSelectAll = new System.Windows.Forms.ToolStripButton();
         this.btnReset = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // treeView
         // 
         this.treeView.CheckBoxes = true;
         this.treeView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.treeView.HideSelection = false;
         this.treeView.Location = new System.Drawing.Point(0, 25);
         this.treeView.Name = "treeView";
         this.treeView.Size = new System.Drawing.Size(458, 450);
         this.treeView.TabIndex = 5;
         this.treeView.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.treeView_AfterCheck);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbCancel,
            this.tsbOK,
            this.btnSelectAll,
            this.btnReset});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(458, 25);
         this.toolStrip1.TabIndex = 4;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbCancel
         // 
         this.tsbCancel.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCancel.Image = ((System.Drawing.Image)(resources.GetObject("tsbCancel.Image")));
         this.tsbCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCancel.Margin = new System.Windows.Forms.Padding(0, 1, 7, 2);
         this.tsbCancel.Name = "tsbCancel";
         this.tsbCancel.Size = new System.Drawing.Size(23, 22);
         this.tsbCancel.Text = "Закрыть";
         this.tsbCancel.Click += new System.EventHandler(this.tsbCancel_Click);
         // 
         // tsbOK
         // 
         this.tsbOK.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbOK.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOK.Image = ((System.Drawing.Image)(resources.GetObject("tsbOK.Image")));
         this.tsbOK.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbOK.Name = "tsbOK";
         this.tsbOK.Size = new System.Drawing.Size(23, 22);
         this.tsbOK.Text = "ОК";
         this.tsbOK.Click += new System.EventHandler(this.tsbOK_Click);
         // 
         // btnSelectAll
         // 
         this.btnSelectAll.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSelectAll.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.btnSelectAll.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSelectAll.Name = "btnSelectAll";
         this.btnSelectAll.Size = new System.Drawing.Size(23, 22);
         this.btnSelectAll.Text = "Пометить";
         this.btnSelectAll.Click += new System.EventHandler(this.btnSelectAll_Click_1);
         // 
         // btnReset
         // 
         this.btnReset.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReset.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnReset.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReset.Name = "btnReset";
         this.btnReset.Size = new System.Drawing.Size(23, 22);
         this.btnReset.Text = "Сбросить";
         this.btnReset.Click += new System.EventHandler(this.btnReset_Click_1);
         // 
         // FmSelectDivision
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(458, 475);
         this.Controls.Add(this.treeView);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectDivision";
         this.Text = "Выберите подразделение";
         this.Load += new System.EventHandler(this.FmSelectDivision_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TreeView treeView;
      private System.Windows.Forms.ToolStrip toolStrip1;
      protected System.Windows.Forms.ToolStripButton tsbCancel;
      protected System.Windows.Forms.ToolStripButton tsbOK;
      private System.Windows.Forms.ToolStripButton btnSelectAll;
      private System.Windows.Forms.ToolStripButton btnReset;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmSelectAgent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectAgent));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tbSearch = new System.Windows.Forms.ToolStripTextBox();
         this.btnClear = new System.Windows.Forms.ToolStripButton();
         this.tsbCancel = new System.Windows.Forms.ToolStripButton();
         this.tsbOK = new System.Windows.Forms.ToolStripButton();
         this.btnSelectAll = new System.Windows.Forms.ToolStripButton();
         this.btnReset = new System.Windows.Forms.ToolStripButton();
         this.treeView = new System.Windows.Forms.TreeView();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.tbSearch,
            this.btnClear,
            this.tsbCancel,
            this.tsbOK,
            this.btnSelectAll,
            this.btnReset});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(408, 25);
         this.toolStrip1.TabIndex = 4;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(40, 22);
         this.toolStripLabel1.Text = "поиск";
         // 
         // tbSearch
         // 
         this.tbSearch.Name = "tbSearch";
         this.tbSearch.Size = new System.Drawing.Size(170, 25);
         this.tbSearch.TextChanged += new System.EventHandler(this.tbSearch_TextChanged);
         // 
         // btnClear
         // 
         this.btnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(23, 22);
         this.btnClear.Text = "Очистить";
         this.btnClear.Click += new System.EventHandler(this.btnReset_Click);
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
         this.btnSelectAll.Click += new System.EventHandler(this.btnSelectAll_Click);
         // 
         // btnReset
         // 
         this.btnReset.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReset.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.btnReset.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReset.Name = "btnReset";
         this.btnReset.Size = new System.Drawing.Size(23, 22);
         this.btnReset.Text = "Сбросить";
         this.btnReset.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // treeView
         // 
         this.treeView.CheckBoxes = true;
         this.treeView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.treeView.HideSelection = false;
         this.treeView.Location = new System.Drawing.Point(0, 25);
         this.treeView.Name = "treeView";
         this.treeView.Size = new System.Drawing.Size(408, 467);
         this.treeView.TabIndex = 5;
         // 
         // FmSelectAgent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(408, 492);
         this.Controls.Add(this.treeView);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectAgent";
         this.Text = "Выберите агента";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox tbSearch;
      private System.Windows.Forms.ToolStripButton btnClear;
      protected System.Windows.Forms.ToolStripButton tsbCancel;
      protected System.Windows.Forms.ToolStripButton tsbOK;
      private System.Windows.Forms.ToolStripButton btnSelectAll;
      private System.Windows.Forms.ToolStripButton btnReset;
      private System.Windows.Forms.TreeView treeView;
   }
}
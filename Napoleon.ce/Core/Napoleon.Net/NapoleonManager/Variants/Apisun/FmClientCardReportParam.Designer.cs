namespace GRSoft.NapoleonManager
{
   partial class FmClientCardReportParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClientCardReportParam));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnExcel = new System.Windows.Forms.Button();
         this.treeView = new System.Windows.Forms.TreeView();
         this.btnClear = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripLabel1,
            this.tbFind,
            this.btnClear});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(556, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(40, 22);
         this.toolStripLabel1.Text = "поиск";
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(200, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnExcel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 464);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(556, 51);
         this.panel1.TabIndex = 1;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(469, 16);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 0;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // treeView
         // 
         this.treeView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.treeView.Location = new System.Drawing.Point(0, 25);
         this.treeView.Name = "treeView";
         this.treeView.Size = new System.Drawing.Size(556, 439);
         this.treeView.TabIndex = 2;
         // 
         // btnClear
         // 
         this.btnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(23, 22);
         this.btnClear.Text = "Очистить";
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // FmClientCardReportParam
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(556, 515);
         this.Controls.Add(this.treeView);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClientCardReportParam";
         this.Text = "Карта клиента";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmClientCardReportParam_FormClosing);
         this.Load += new System.EventHandler(this.FmClientCardReportParam_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.TreeView treeView;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnClear;
   }
}
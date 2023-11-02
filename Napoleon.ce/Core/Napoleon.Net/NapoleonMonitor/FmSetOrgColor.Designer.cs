namespace GRSoft.NapoleonManager
{
   partial class FmSetOrgColor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSetOrgColor));
         this.orgs = new System.Windows.Forms.DataGridView();
         this.name = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.colorFilter = new GRSoft.NapoleonManager.TSColorFilter();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindUp = new System.Windows.Forms.ToolStripButton();
         this.btnFindDown = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.orgs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // orgs
         // 
         this.orgs.AllowUserToAddRows = false;
         this.orgs.AllowUserToDeleteRows = false;
         this.orgs.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.orgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.orgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.name});
         this.orgs.Location = new System.Drawing.Point(0, 26);
         this.orgs.MultiSelect = false;
         this.orgs.Name = "orgs";
         this.orgs.ReadOnly = true;
         this.orgs.RowHeadersVisible = false;
         this.orgs.Size = new System.Drawing.Size(403, 352);
         this.orgs.TabIndex = 0;
         this.orgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.orgs_MouseDown);
         this.orgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.orgs_CellFormatting);
         // 
         // name
         // 
         this.name.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.name.DataPropertyName = "Name";
         this.name.HeaderText = "Название";
         this.name.Name = "name";
         this.name.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Название";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.colorFilter,
            this.tbFind,
            this.btnFindUp,
            this.btnFindDown});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(403, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // colorFilter
         // 
         this.colorFilter.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.colorFilter.Image = ((System.Drawing.Image)(resources.GetObject("colorFilter.Image")));
         this.colorFilter.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.colorFilter.Name = "colorFilter";
         this.colorFilter.Size = new System.Drawing.Size(112, 22);
         this.colorFilter.Text = "Фильтр по цветам";
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnFindUp
         // 
         this.btnFindUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindUp.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.go_up_search;
         this.btnFindUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindUp.Name = "btnFindUp";
         this.btnFindUp.Size = new System.Drawing.Size(23, 22);
         this.btnFindUp.Text = "Искать назад";
         this.btnFindUp.Click += new System.EventHandler(this.btnFindUp_Click);
         // 
         // btnFindDown
         // 
         this.btnFindDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindDown.Image = global::GRSoft.NapoleonMonitor.Properties.Resources.go_down_search;
         this.btnFindDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindDown.Name = "btnFindDown";
         this.btnFindDown.Size = new System.Drawing.Size(23, 22);
         this.btnFindDown.Text = "Искать вперед";
         this.btnFindDown.Click += new System.EventHandler(this.btnFindDown_Click);
         // 
         // FmSetOrgColor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(403, 378);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.orgs);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSetOrgColor";
         this.Text = "Список контрагентов";
         ((System.ComponentModel.ISupportInitialize)(this.orgs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView orgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn name;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private TSColorFilter colorFilter;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindUp;
      private System.Windows.Forms.ToolStripButton btnFindDown;
   }
}
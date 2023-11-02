namespace GRSoft.NapoleonManager
{
   partial class FmSunnySKUReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSunnySKUReport));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.tsFolder = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSKU1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSKU2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSKU3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSKU4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSKU5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.tsFolder,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(811, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(200, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // tsFolder
         // 
         this.tsFolder.IsLink = true;
         this.tsFolder.Name = "tsFolder";
         this.tsFolder.Size = new System.Drawing.Size(102, 22);
         this.tsFolder.Text = "Выберите группу";
         this.tsFolder.Click += new System.EventHandler(this.tsFolder_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnSKU1,
            this.clmnSKU2,
            this.clmnSKU3,
            this.clmnSKU4,
            this.clmnSKU5});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(811, 636);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellDoubleClick);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Наименование ТТ";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         // 
         // clmnSKU1
         // 
         this.clmnSKU1.DataPropertyName = "W1";
         this.clmnSKU1.HeaderText = "Продано 1 неделя";
         this.clmnSKU1.Name = "clmnSKU1";
         this.clmnSKU1.ReadOnly = true;
         // 
         // clmnSKU2
         // 
         this.clmnSKU2.DataPropertyName = "W2";
         this.clmnSKU2.HeaderText = "Продано 2 неделя";
         this.clmnSKU2.Name = "clmnSKU2";
         this.clmnSKU2.ReadOnly = true;
         // 
         // clmnSKU3
         // 
         this.clmnSKU3.DataPropertyName = "W3";
         this.clmnSKU3.HeaderText = "Продано 3 неделя";
         this.clmnSKU3.Name = "clmnSKU3";
         this.clmnSKU3.ReadOnly = true;
         // 
         // clmnSKU4
         // 
         this.clmnSKU4.DataPropertyName = "W4";
         this.clmnSKU4.HeaderText = "Продано 4 неделя";
         this.clmnSKU4.Name = "clmnSKU4";
         this.clmnSKU4.ReadOnly = true;
         // 
         // clmnSKU5
         // 
         this.clmnSKU5.DataPropertyName = "W5";
         this.clmnSKU5.HeaderText = "Продано 5 неделя";
         this.clmnSKU5.Name = "clmnSKU5";
         this.clmnSKU5.ReadOnly = true;
         // 
         // FmSunnySKUReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(811, 661);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSunnySKUReport";
         this.Text = "Отчет по SKU";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripLabel tsFolder;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSKU1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSKU2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSKU3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSKU4;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSKU5;
   }
}
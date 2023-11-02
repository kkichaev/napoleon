using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class OrgDiscountEditor
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OrgDiscountEditor));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbClearSearch = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnOrgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvFolders = new GRSoft.UILib.TreeGridView();
         this.clmnItemName = new GRSoft.UILib.TreeGridColumn();
         this.clmnDiscount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvFolders)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRefresh,
            this.tsbSave,
            this.tbFind,
            this.tsbClearSearch});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(821, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(150, 25);
         this.tbFind.ToolTipText = "Контрагент дял поиска";
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // tsbClearSearch
         // 
         this.tsbClearSearch.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbClearSearch.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsbClearSearch.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbClearSearch.Name = "tsbClearSearch";
         this.tsbClearSearch.Size = new System.Drawing.Size(23, 22);
         this.tsbClearSearch.Text = "Очистить поиск";
         this.tsbClearSearch.Click += new System.EventHandler(this.ClearFind);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvFolders);
         this.splitContainer1.Size = new System.Drawing.Size(821, 510);
         this.splitContainer1.SplitterDistance = 273;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrgName});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(273, 510);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         // 
         // clmnOrgName
         // 
         this.clmnOrgName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrgName.DataPropertyName = "Name";
         this.clmnOrgName.HeaderText = "Контрагент";
         this.clmnOrgName.Name = "clmnOrgName";
         this.clmnOrgName.ReadOnly = true;
         // 
         // dgvFolders
         // 
         this.dgvFolders.AllowUserToAddRows = false;
         this.dgvFolders.AllowUserToDeleteRows = false;
         this.dgvFolders.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvFolders.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItemName,
            this.clmnDiscount});
         this.dgvFolders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvFolders.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvFolders.ImageList = null;
         this.dgvFolders.Location = new System.Drawing.Point(0, 0);
         this.dgvFolders.Name = "dgvFolders";
         this.dgvFolders.RowHeadersVisible = false;
         this.dgvFolders.Size = new System.Drawing.Size(544, 510);
         this.dgvFolders.TabIndex = 0;
         // 
         // clmnItemName
         // 
         this.clmnItemName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItemName.DataPropertyName = "Name";
         this.clmnItemName.DefaultNodeImage = null;
         this.clmnItemName.HeaderText = "Папка товара";
         this.clmnItemName.Name = "clmnItemName";
         this.clmnItemName.ReadOnly = true;
         this.clmnItemName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnDiscount
         // 
         this.clmnDiscount.FillWeight = 50F;
         this.clmnDiscount.HeaderText = "Скидка";
         this.clmnDiscount.Name = "clmnDiscount";
         this.clmnDiscount.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // OrgDiscountEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(821, 535);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OrgDiscountEditor";
         this.Text = "Лимиты скидок для контрагентов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvFolders)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrgName;
      private TreeGridView dgvFolders;
      private TreeGridColumn clmnItemName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDiscount;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton tsbClearSearch;
      private System.Windows.Forms.Timer timer1;

   }
}
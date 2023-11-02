namespace GRSoft.NapoleonManager
{
   partial class FmDistribGroupEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistribGroupEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvGroups = new System.Windows.Forms.DataGridView();
         this.clmnGroup = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.tsbUp = new System.Windows.Forms.ToolStripButton();
         this.tbDn = new System.Windows.Forms.ToolStripButton();
         this.tsbAgentsSet = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddItem = new System.Windows.Forms.ToolStripButton();
         this.btnDelItem = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvGroups)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvGroups);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(784, 613);
         this.splitContainer1.SplitterDistance = 347;
         this.splitContainer1.TabIndex = 3;
         // 
         // dgvGroups
         // 
         this.dgvGroups.AllowUserToAddRows = false;
         this.dgvGroups.AllowUserToDeleteRows = false;
         this.dgvGroups.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvGroups.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnGroup});
         this.dgvGroups.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvGroups.Location = new System.Drawing.Point(0, 25);
         this.dgvGroups.Name = "dgvGroups";
         this.dgvGroups.RowHeadersVisible = false;
         this.dgvGroups.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvGroups.Size = new System.Drawing.Size(347, 588);
         this.dgvGroups.TabIndex = 4;
         this.dgvGroups.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvGroups_RowEnter);
         // 
         // clmnGroup
         // 
         this.clmnGroup.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnGroup.DataPropertyName = "Name";
         this.clmnGroup.HeaderText = "Группа";
         this.clmnGroup.Name = "clmnGroup";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnAdd,
            this.btnDel,
            this.btnSave,
            this.tsbUp,
            this.tbDn,
            this.tsbAgentsSet});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(347, 25);
         this.toolStrip1.TabIndex = 3;
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
         this.btnRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsbUp
         // 
         this.tsbUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.tsbUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUp.Name = "tsbUp";
         this.tsbUp.Size = new System.Drawing.Size(23, 22);
         this.tsbUp.Text = "Переместить вверх";
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         // 
         // tbDn
         // 
         this.tbDn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbDn.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.tbDn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbDn.Name = "tbDn";
         this.tbDn.Size = new System.Drawing.Size(23, 22);
         this.tbDn.Text = "Переместить вниз";
         this.tbDn.Click += new System.EventHandler(this.tbDn_Click);
         // 
         // tsbAgentsSet
         // 
         this.tsbAgentsSet.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAgentsSet.Image = global::GRSoft.NapoleonManager.Properties.Resources.ca_add;
         this.tsbAgentsSet.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAgentsSet.Name = "tsbAgentsSet";
         this.tsbAgentsSet.Size = new System.Drawing.Size(23, 22);
         this.tsbAgentsSet.Text = "Назначить агентам";
         this.tsbAgentsSet.Click += new System.EventHandler(this.tsbAgentsSet_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(433, 588);
         this.dgvItems.TabIndex = 4;
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Товары";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddItem,
            this.btnDelItem});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(433, 25);
         this.toolStrip2.TabIndex = 3;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddItem
         // 
         this.btnAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddItem.Name = "btnAddItem";
         this.btnAddItem.Size = new System.Drawing.Size(23, 22);
         this.btnAddItem.Text = "Добавить";
         this.btnAddItem.Click += new System.EventHandler(this.btnAddItem_Click);
         // 
         // btnDelItem
         // 
         this.btnDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelItem.Name = "btnDelItem";
         this.btnDelItem.Size = new System.Drawing.Size(23, 22);
         this.btnDelItem.Text = "Удалить";
         this.btnDelItem.Click += new System.EventHandler(this.btnDelItem_Click);
         // 
         // FmDistribGroupEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(784, 613);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistribGroupEditor";
         this.Text = "Редактор групп дистрибуции";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvGroups)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvGroups;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnAddItem;
      private System.Windows.Forms.ToolStripButton btnDelItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnGroup;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.ToolStripButton tsbUp;
      private System.Windows.Forms.ToolStripButton tbDn;
      private System.Windows.Forms.ToolStripButton tsbAgentsSet;
   }
}
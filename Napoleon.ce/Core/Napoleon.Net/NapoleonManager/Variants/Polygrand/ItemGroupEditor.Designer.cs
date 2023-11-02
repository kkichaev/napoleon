namespace GRSoft.NapoleonManager
{
   partial class ItemGroupEditor
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ItemGroupEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvGroups = new System.Windows.Forms.DataGridView();
         this.clmnGrouopName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbAddGroup = new System.Windows.Forms.ToolStripButton();
         this.tsbDelGroup = new System.Windows.Forms.ToolStripButton();
         this.tsbAgentSet = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnItemName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsbAddItem = new System.Windows.Forms.ToolStripButton();
         this.tsbDelItem = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.tsStatusText = new System.Windows.Forms.ToolStripStatusLabel();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvGroups)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
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
         this.splitContainer1.Panel2.Controls.Add(this.statusStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(557, 555);
         this.splitContainer1.SplitterDistance = 223;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvGroups
         // 
         this.dgvGroups.AllowUserToAddRows = false;
         this.dgvGroups.AllowUserToDeleteRows = false;
         this.dgvGroups.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvGroups.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnGrouopName});
         this.dgvGroups.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvGroups.Location = new System.Drawing.Point(0, 25);
         this.dgvGroups.Name = "dgvGroups";
         this.dgvGroups.RowHeadersVisible = false;
         this.dgvGroups.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvGroups.Size = new System.Drawing.Size(557, 198);
         this.dgvGroups.TabIndex = 1;
         this.dgvGroups.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvGroups_RowEnter);
         // 
         // clmnGrouopName
         // 
         this.clmnGrouopName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnGrouopName.DataPropertyName = "Name";
         this.clmnGrouopName.HeaderText = "Товарная группа";
         this.clmnGrouopName.Name = "clmnGrouopName";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRefresh,
            this.tsbSave,
            this.tsbAddGroup,
            this.tsbDelGroup,
            this.tsbAgentSet});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(557, 25);
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
         // 
         // tsbAddGroup
         // 
         this.tsbAddGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddGroup.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAddGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddGroup.Name = "tsbAddGroup";
         this.tsbAddGroup.Size = new System.Drawing.Size(23, 22);
         this.tsbAddGroup.Text = "Добавить";
         this.tsbAddGroup.Click += new System.EventHandler(this.tsbAddGroup_Click);
         // 
         // tsbDelGroup
         // 
         this.tsbDelGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDelGroup.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsbDelGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDelGroup.Name = "tsbDelGroup";
         this.tsbDelGroup.Size = new System.Drawing.Size(23, 22);
         this.tsbDelGroup.Text = "Удалить";
         this.tsbDelGroup.Click += new System.EventHandler(this.tsbDelGroup_Click);
         // 
         // tsbAgentSet
         // 
         this.tsbAgentSet.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAgentSet.Image = global::GRSoft.NapoleonManager.Properties.Resources.ca_add;
         this.tsbAgentSet.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAgentSet.Name = "tsbAgentSet";
         this.tsbAgentSet.Size = new System.Drawing.Size(23, 22);
         this.tsbAgentSet.Text = "Назначить группы агентам";
         this.tsbAgentSet.Click += new System.EventHandler(this.tsbAgentSet_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItemName,
            this.clmnQty});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(557, 281);
         this.dgvItems.TabIndex = 1;
         // 
         // clmnItemName
         // 
         this.clmnItemName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItemName.DataPropertyName = "Name";
         this.clmnItemName.HeaderText = "Товар";
         this.clmnItemName.Name = "clmnItemName";
         this.clmnItemName.ReadOnly = true;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "Qty";
         dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleRight;
         this.clmnQty.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnQty.HeaderText = "Количество";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAddItem,
            this.tsbDelItem});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(557, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsbAddItem
         // 
         this.tsbAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddItem.Name = "tsbAddItem";
         this.tsbAddItem.Size = new System.Drawing.Size(23, 22);
         this.tsbAddItem.Text = "Добавить";
         this.tsbAddItem.Click += new System.EventHandler(this.tsbAddItem_Click);
         // 
         // tsbDelItem
         // 
         this.tsbDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsbDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDelItem.Name = "tsbDelItem";
         this.tsbDelItem.Size = new System.Drawing.Size(23, 22);
         this.tsbDelItem.Text = "Удалить";
         this.tsbDelItem.Click += new System.EventHandler(this.tsbDelItem_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsStatusText});
         this.statusStrip1.Location = new System.Drawing.Point(0, 306);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(557, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // tsStatusText
         // 
         this.tsStatusText.Name = "tsStatusText";
         this.tsStatusText.Size = new System.Drawing.Size(0, 17);
         // 
         // ItemGroupEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(557, 555);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "ItemGroupEditor";
         this.Text = "Редактор товарных групп";
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
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvGroups;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnGrouopName;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbAddGroup;
      private System.Windows.Forms.ToolStripButton tsbDelGroup;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItemName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton tsbAddItem;
      private System.Windows.Forms.ToolStripButton tsbDelItem;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel tsStatusText;
      private System.Windows.Forms.ToolStripButton tsbAgentSet;

   }
}
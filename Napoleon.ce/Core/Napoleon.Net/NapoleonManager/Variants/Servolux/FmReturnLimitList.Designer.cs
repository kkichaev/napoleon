namespace GRSoft.NapoleonManager
{
   partial class FmReturnLimitList
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReturnLimitList));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbEdit = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnPriceType = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOverLimit = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnStart = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnEnd = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnLimitMoney = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnLimitWeight = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnRefresh,
            this.tsbAdd,
            this.tsbEdit,
            this.tsbRemove,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1276, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbAgents
         // 
         this.cbAgents.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(265, 39);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Margin = new System.Windows.Forms.Padding(5, 1, 0, 2);
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(21, 36);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(220, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(36, 36);
         this.toolStripLabel2.Text = "по";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(220, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(36, 36);
         this.tsbAdd.Text = "Добавить";
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbEdit
         // 
         this.tsbEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.tsbEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbEdit.Name = "tsbEdit";
         this.tsbEdit.Size = new System.Drawing.Size(36, 36);
         this.tsbEdit.Text = "Изменить";
         this.tsbEdit.Click += new System.EventHandler(this.tsbEdit_Click);
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(36, 36);
         this.tsbRemove.Text = "Удалить";
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // dtpBegin
         // 
         this.dtpBegin.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpBegin.Location = new System.Drawing.Point(307, 6);
         this.dtpBegin.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(207, 30);
         this.dtpBegin.TabIndex = 6;
         this.dtpBegin.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpEnd.Location = new System.Drawing.Point(560, 6);
         this.dtpEnd.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(207, 30);
         this.dtpEnd.TabIndex = 7;
         this.dtpEnd.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPriceType,
            this.clmnOverLimit,
            this.clmnStart,
            this.clmnEnd,
            this.clmnLimitMoney,
            this.clmnLimitWeight});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 39);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(1276, 565);
         this.dgvItems.TabIndex = 8;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
         // 
         // clmnPriceType
         // 
         this.clmnPriceType.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnPriceType.DataPropertyName = "Type";
         this.clmnPriceType.HeaderText = "Тип продукции";
         this.clmnPriceType.Name = "clmnPriceType";
         // 
         // clmnOverLimit
         // 
         this.clmnOverLimit.DataPropertyName = "CanOverLimit";
         this.clmnOverLimit.HeaderText = "Может превышать лимит";
         this.clmnOverLimit.Name = "clmnOverLimit";
         // 
         // clmnStart
         // 
         this.clmnStart.DataPropertyName = "Begin";
         this.clmnStart.HeaderText = "С";
         this.clmnStart.Name = "clmnStart";
         // 
         // clmnEnd
         // 
         this.clmnEnd.DataPropertyName = "End";
         this.clmnEnd.HeaderText = "По";
         this.clmnEnd.Name = "clmnEnd";
         // 
         // clmnLimitMoney
         // 
         this.clmnLimitMoney.DataPropertyName = "LimitSum";
         this.clmnLimitMoney.HeaderText = "Лимит, руб.";
         this.clmnLimitMoney.Name = "clmnLimitMoney";
         // 
         // clmnLimitWeight
         // 
         this.clmnLimitWeight.DataPropertyName = "LimitWeight";
         this.clmnLimitWeight.HeaderText = "Лимит, кг";
         this.clmnLimitWeight.Name = "clmnLimitWeight";
         // 
         // FmReturnLimitList
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1276, 604);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.Name = "FmReturnLimitList";
         this.Text = "Лимиты возвратов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel1;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel2;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbEdit;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPriceType;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnOverLimit;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnStart;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnEnd;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnLimitMoney;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnLimitWeight;
   }
}
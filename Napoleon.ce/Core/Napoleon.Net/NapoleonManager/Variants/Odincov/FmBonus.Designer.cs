namespace GRSoft.NapoleonManager
{
   partial class FmBonus
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBonus));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         this.dgvBonusItems = new System.Windows.Forms.DataGridView();
         this.lblFrom = new System.Windows.Forms.Label();
         this.dtFromDate = new System.Windows.Forms.DateTimePicker();
         this.lblTill = new System.Windows.Forms.Label();
         this.dtTillDate = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.cbBonusType = new System.Windows.Forms.ToolStripComboBox();
         this.panel = new System.Windows.Forms.Panel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnItemName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnItemQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBonusItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvBonusItems
         // 
         this.dgvBonusItems.AllowUserToAddRows = false;
         this.dgvBonusItems.AllowUserToDeleteRows = false;
         this.dgvBonusItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvBonusItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItemName,
            this.clmnItemQty});
         this.dgvBonusItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvBonusItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvBonusItems.Location = new System.Drawing.Point(0, 52);
         this.dgvBonusItems.Name = "dgvBonusItems";
         this.dgvBonusItems.RowHeadersVisible = false;
         this.dgvBonusItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvBonusItems.Size = new System.Drawing.Size(524, 324);
         this.dgvBonusItems.TabIndex = 0;
         // 
         // lblFrom
         // 
         this.lblFrom.AutoSize = true;
         this.lblFrom.Location = new System.Drawing.Point(211, 5);
         this.lblFrom.Name = "lblFrom";
         this.lblFrom.Size = new System.Drawing.Size(20, 13);
         this.lblFrom.TabIndex = 4;
         this.lblFrom.Text = "С: ";
         // 
         // dtFromDate
         // 
         this.dtFromDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtFromDate.Location = new System.Drawing.Point(231, 3);
         this.dtFromDate.Name = "dtFromDate";
         this.dtFromDate.Size = new System.Drawing.Size(108, 20);
         this.dtFromDate.TabIndex = 4;
         // 
         // lblTill
         // 
         this.lblTill.AutoSize = true;
         this.lblTill.Location = new System.Drawing.Point(346, 5);
         this.lblTill.Name = "lblTill";
         this.lblTill.Size = new System.Drawing.Size(27, 13);
         this.lblTill.TabIndex = 2;
         this.lblTill.Text = "По: ";
         // 
         // dtTillDate
         // 
         this.dtTillDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtTillDate.Location = new System.Drawing.Point(376, 3);
         this.dtTillDate.Name = "dtTillDate";
         this.dtTillDate.Size = new System.Drawing.Size(108, 20);
         this.dtTillDate.TabIndex = 4;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAdd,
            this.tsbRemove,
            this.tsbSave,
            this.cbBonusType});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(524, 25);
         this.toolStrip1.TabIndex = 5;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = ((System.Drawing.Image)(resources.GetObject("tsbAdd.Image")));
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(23, 22);
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = ((System.Drawing.Image)(resources.GetObject("tsbRemove.Image")));
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(23, 22);
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Margin = new System.Windows.Forms.Padding(2, 1, 0, 2);
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // cbBonusType
         // 
         this.cbBonusType.Items.AddRange(new object[] {
            "Товар",
            "Сумма заказа"});
         this.cbBonusType.Name = "cbBonusType";
         this.cbBonusType.Size = new System.Drawing.Size(121, 25);
         this.cbBonusType.SelectedIndexChanged += new System.EventHandler(this.cbBonusType_SelectedIndexChanged);
         // 
         // panel
         // 
         this.panel.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel.Location = new System.Drawing.Point(0, 25);
         this.panel.Name = "panel";
         this.panel.Size = new System.Drawing.Size(524, 27);
         this.panel.TabIndex = 6;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "ItemName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Width = 350;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Qty";
         dataGridViewCellStyle2.Format = "N0";
         dataGridViewCellStyle2.NullValue = "0";
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle2;
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Bonus";
         dataGridViewCellStyle3.Format = "N0";
         dataGridViewCellStyle3.NullValue = "0";
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle3;
         this.dataGridViewTextBoxColumn3.HeaderText = "Бонус";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // clmnItemName
         // 
         this.clmnItemName.DataPropertyName = "ItemName";
         this.clmnItemName.HeaderText = "Товар";
         this.clmnItemName.Name = "clmnItemName";
         this.clmnItemName.ReadOnly = true;
         this.clmnItemName.Width = 350;
         // 
         // clmnItemQty
         // 
         this.clmnItemQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItemQty.DataPropertyName = "Qty";
         dataGridViewCellStyle1.Format = "N0";
         dataGridViewCellStyle1.NullValue = "0";
         this.clmnItemQty.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnItemQty.HeaderText = "Количество";
         this.clmnItemQty.Name = "clmnItemQty";
         // 
         // FmBonus
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(524, 376);
         this.Controls.Add(this.dgvBonusItems);
         this.Controls.Add(this.panel);
         this.Controls.Add(this.lblFrom);
         this.Controls.Add(this.dtFromDate);
         this.Controls.Add(this.lblTill);
         this.Controls.Add(this.dtTillDate);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBonus";
         this.Text = "Акционные товары";
         ((System.ComponentModel.ISupportInitialize)(this.dgvBonusItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvBonusItems;
      private System.Windows.Forms.Label lblFrom;
      private System.Windows.Forms.DateTimePicker dtFromDate;
      private System.Windows.Forms.Label lblTill;
      private System.Windows.Forms.DateTimePicker dtTillDate;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.ToolStripComboBox cbBonusType;
      private System.Windows.Forms.Panel panel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItemName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItemQty;
      
      
   }
}
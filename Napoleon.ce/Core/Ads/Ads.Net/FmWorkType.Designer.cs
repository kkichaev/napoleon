namespace GRSoft.Ads
{
   partial class FmWorkType
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmWorkType));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvWorkType = new System.Windows.Forms.DataGridView();
         this.dgvWorkTypeId = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvWorkTypeName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvWorkType)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnEdit,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(496, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = ((System.Drawing.Image)(resources.GetObject("btnEdit.Image")));
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 283);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(496, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvWorkType
         // 
         this.dgvWorkType.AllowUserToAddRows = false;
         this.dgvWorkType.AllowUserToDeleteRows = false;
         this.dgvWorkType.AllowUserToResizeRows = false;
         this.dgvWorkType.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvWorkType.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvWorkTypeId,
            this.dgvWorkTypeName});
         this.dgvWorkType.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvWorkType.Location = new System.Drawing.Point(0, 25);
         this.dgvWorkType.Name = "dgvWorkType";
         this.dgvWorkType.RowHeadersVisible = false;
         this.dgvWorkType.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvWorkType.Size = new System.Drawing.Size(496, 258);
         this.dgvWorkType.TabIndex = 2;
         // 
         // dgvWorkTypeId
         // 
         this.dgvWorkTypeId.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvWorkTypeId.DataPropertyName = "Id";
         this.dgvWorkTypeId.FillWeight = 10F;
         this.dgvWorkTypeId.HeaderText = "Код";
         this.dgvWorkTypeId.Name = "dgvWorkTypeId";
         // 
         // dgvWorkTypeName
         // 
         this.dgvWorkTypeName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvWorkTypeName.DataPropertyName = "Name";
         this.dgvWorkTypeName.HeaderText = "Наименование";
         this.dgvWorkTypeName.Name = "dgvWorkTypeName";
         // 
         // FmWorkType
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(496, 305);
         this.Controls.Add(this.dgvWorkType);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmWorkType";
         this.Text = "Типы работ";
         this.Load += new System.EventHandler(this.FmWorkType_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvWorkType)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvWorkType;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvWorkTypeId;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvWorkTypeName;
   }
}
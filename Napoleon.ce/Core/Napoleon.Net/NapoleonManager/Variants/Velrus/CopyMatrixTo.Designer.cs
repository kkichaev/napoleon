namespace GRSoft.NapoleonManager
{
   partial class CopyMatrixTo
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(CopyMatrixTo));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbOK = new System.Windows.Forms.ToolStripButton();
         this.tsbCancel = new System.Windows.Forms.ToolStripButton();
         this.toolStripDropDownButton1 = new System.Windows.Forms.ToolStripDropDownButton();
         this.tsbSelectAllToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
         this.tsbUnselectAllToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbOK,
            this.tsbCancel,
            this.toolStripDropDownButton1});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(475, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbOK
         // 
         this.tsbOK.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbOK.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOK.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_apply;
         this.tsbOK.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbOK.Name = "tsbOK";
         this.tsbOK.Size = new System.Drawing.Size(23, 22);
         this.tsbOK.Text = "Сохранить";
         this.tsbOK.Click += new System.EventHandler(this.tsbOK_Click);
         // 
         // tsbCancel
         // 
         this.tsbCancel.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCancel.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.tsbCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCancel.Name = "tsbCancel";
         this.tsbCancel.Size = new System.Drawing.Size(23, 22);
         this.tsbCancel.Text = "Отмена";
         this.tsbCancel.Click += new System.EventHandler(this.tsbCancel_Click);
         // 
         // toolStripDropDownButton1
         // 
         this.toolStripDropDownButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripDropDownButton1.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSelectAllToolStripMenuItem,
            this.tsbUnselectAllToolStripMenuItem});
         this.toolStripDropDownButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.toolStripDropDownButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripDropDownButton1.Name = "toolStripDropDownButton1";
         this.toolStripDropDownButton1.Size = new System.Drawing.Size(29, 22);
         // 
         // tsbSelectAllToolStripMenuItem
         // 
         this.tsbSelectAllToolStripMenuItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.tsbSelectAllToolStripMenuItem.Name = "tsbSelectAllToolStripMenuItem";
         this.tsbSelectAllToolStripMenuItem.Size = new System.Drawing.Size(153, 22);
         this.tsbSelectAllToolStripMenuItem.Text = "Отметить все";
         this.tsbSelectAllToolStripMenuItem.Click += new System.EventHandler(this.tsbSelectAllToolStripMenuItem_Click);
         // 
         // tsbUnselectAllToolStripMenuItem
         // 
         this.tsbUnselectAllToolStripMenuItem.Name = "tsbUnselectAllToolStripMenuItem";
         this.tsbUnselectAllToolStripMenuItem.Size = new System.Drawing.Size(153, 22);
         this.tsbUnselectAllToolStripMenuItem.Text = "Снять отметку";
         this.tsbUnselectAllToolStripMenuItem.Click += new System.EventHandler(this.tsbUnselectAllToolStripMenuItem_Click);
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnChecked,
            this.clmnOrg});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 25);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(475, 372);
         this.dgvOrgs.TabIndex = 2;
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         this.dgvOrgs.CellValidating += new System.Windows.Forms.DataGridViewCellValidatingEventHandler(this.dgvOrgs_CellValidating);
         this.dgvOrgs.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvOrgs_CurrentCellDirtyStateChanged);
         // 
         // clmnChecked
         // 
         this.clmnChecked.DataPropertyName = "Checked";
         this.clmnChecked.HeaderText = "";
         this.clmnChecked.Name = "clmnChecked";
         this.clmnChecked.Width = 25;
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "Name";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnOrg.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // CopyMatrixTo
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(475, 397);
         this.Controls.Add(this.dgvOrgs);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "CopyMatrixTo";
         this.Text = "Копировть матрицу дистрибуции";
         this.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.CopyMatrixTo_KeyPress);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbOK;
      private System.Windows.Forms.ToolStripButton tsbCancel;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnChecked;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStripDropDownButton toolStripDropDownButton1;
      private System.Windows.Forms.ToolStripMenuItem tsbSelectAllToolStripMenuItem;
      private System.Windows.Forms.ToolStripMenuItem tsbUnselectAllToolStripMenuItem;
   }
}
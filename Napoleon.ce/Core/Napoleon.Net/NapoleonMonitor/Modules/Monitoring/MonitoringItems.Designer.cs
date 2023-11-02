namespace GRSoft.NapoleonManager
{
   partial class MonitoringItems
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MonitoringItems));
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOur = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.toolStrip = new System.Windows.Forms.ToolStrip();
         this.tbDel = new System.Windows.Forms.ToolStripButton();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnOur});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(284, 237);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CellBeginEdit += new System.Windows.Forms.DataGridViewCellCancelEventHandler(this.dgvItems_CellBeginEdit);
         this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
         this.dgvItems.RowsAdded += new System.Windows.Forms.DataGridViewRowsAddedEventHandler(this.dgvItems_RowsAdded);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Торговая марка";
         this.clmnName.Name = "clmnName";
         // 
         // clmnOur
         // 
         this.clmnOur.DataPropertyName = "IsOwn";
         this.clmnOur.HeaderText = "Наша";
         this.clmnOur.Name = "clmnOur";
         // 
         // toolStrip
         // 
         this.toolStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbDel,
            this.tbSave});
         this.toolStrip.Location = new System.Drawing.Point(0, 0);
         this.toolStrip.Name = "toolStrip";
         this.toolStrip.Size = new System.Drawing.Size(284, 25);
         this.toolStrip.TabIndex = 1;
         this.toolStrip.Text = "toolStrip1";
         // 
         // tbDel
         // 
         this.tbDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbDel.Image = ((System.Drawing.Image)(resources.GetObject("tbDel.Image")));
         this.tbDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbDel.Name = "tbDel";
         this.tbDel.Size = new System.Drawing.Size(23, 22);
         this.tbDel.Text = "Удалить элемент";
         this.tbDel.Click += new System.EventHandler(this.tbDel_Click);
         // 
         // tbSave
         // 
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Enabled = false;
         this.tbSave.Image = ((System.Drawing.Image)(resources.GetObject("tbSave.Image")));
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(23, 22);
         this.tbSave.Text = "Сохранить изменения";
         this.tbSave.Click += new System.EventHandler(this.tbSave_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Торговая марка";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // MonitoringItems
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(284, 262);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "MonitoringItems";
         this.Text = "Мониторинг";
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip.ResumeLayout(false);
         this.toolStrip.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStripButton tbDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnOur;
      protected System.Windows.Forms.ToolStrip toolStrip;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      protected System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.ToolStripButton tbSave;
   }
}
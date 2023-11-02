namespace GRSoft.NapoleonManager
{
   partial class FmSelectPrice
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectPrice));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbFind = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton3 = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnCheck = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.checkBox1 = new System.Windows.Forms.CheckBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbFind,
            this.toolStripButton1,
            this.toolStripButton2,
            this.toolStripButton3});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(579, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbFind
         // 
         this.tsbFind.Margin = new System.Windows.Forms.Padding(40, 0, 1, 0);
         this.tsbFind.Name = "tsbFind";
         this.tsbFind.Size = new System.Drawing.Size(200, 39);
         this.tsbFind.TextChanged += new System.EventHandler(this.toolStrip1_TextChanged);
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.clear_text;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(36, 36);
         this.toolStripButton1.Text = "Очистить поиск";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // toolStripButton2
         // 
         this.toolStripButton2.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton2.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton2.Name = "toolStripButton2";
         this.toolStripButton2.Size = new System.Drawing.Size(36, 36);
         this.toolStripButton2.Text = "Отмена";
         this.toolStripButton2.Click += new System.EventHandler(this.toolStripButton2_Click);
         // 
         // toolStripButton3
         // 
         this.toolStripButton3.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.toolStripButton3.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton3.Image = global::GRSoft.NapoleonManager.Properties.Resources.remnants_report;
         this.toolStripButton3.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton3.Name = "toolStripButton3";
         this.toolStripButton3.Size = new System.Drawing.Size(36, 36);
         this.toolStripButton3.Text = "OK";
         this.toolStripButton3.Click += new System.EventHandler(this.toolStripButton3_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnCheck,
            this.clmnItem});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 39);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(579, 416);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         // 
         // clmnCheck
         // 
         this.clmnCheck.DataPropertyName = "IsChecked";
         this.clmnCheck.FillWeight = 40F;
         this.clmnCheck.HeaderText = "";
         this.clmnCheck.Name = "clmnCheck";
         this.clmnCheck.Width = 40;
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Товар";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         this.clmnItem.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnItem.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // checkBox1
         // 
         this.checkBox1.AutoSize = true;
         this.checkBox1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.checkBox1.Location = new System.Drawing.Point(15, 10);
         this.checkBox1.Name = "checkBox1";
         this.checkBox1.Size = new System.Drawing.Size(15, 14);
         this.checkBox1.TabIndex = 2;
         this.checkBox1.UseVisualStyleBackColor = true;
         this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FmSelectPrice
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(579, 455);
         this.Controls.Add(this.checkBox1);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectPrice";
         this.Text = "Выберите товар";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripTextBox tsbFind;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripButton toolStripButton2;
      private System.Windows.Forms.ToolStripButton toolStripButton3;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.CheckBox checkBox1;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnCheck;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
   }
}
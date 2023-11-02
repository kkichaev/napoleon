using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class FmSkladBind
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSkladBind));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbClearFind = new System.Windows.Forms.ToolStripButton();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.tgvItems = new GRSoft.UILib.TreeGridView();
         this.clmnItem = new GRSoft.UILib.TreeGridColumn();
         this.clmnSklad = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRefresh,
            this.tsbSave,
            this.tsbFind,
            this.tsbClearFind});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(897, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(36, 36);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tsbFind
         // 
         this.tsbFind.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbFind.Name = "tsbFind";
         this.tsbFind.Size = new System.Drawing.Size(150, 39);
         this.tsbFind.Visible = false;
         this.tsbFind.TextChanged += new System.EventHandler(this.tsbFind_TextChanged);
         // 
         // tsbClearFind
         // 
         this.tsbClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsbClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbClearFind.Name = "tsbClearFind";
         this.tsbClearFind.Size = new System.Drawing.Size(36, 36);
         this.tsbClearFind.Text = "Очистить";
         this.tsbClearFind.Visible = false;
         this.tsbClearFind.Click += new System.EventHandler(this.tsbClearFind_Click);
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // tgvItems
         // 
         this.tgvItems.AllowUserToAddRows = false;
         this.tgvItems.AllowUserToDeleteRows = false;
         this.tgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.tgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnSklad});
         this.tgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.tgvItems.ImageList = null;
         this.tgvItems.Location = new System.Drawing.Point(0, 39);
         this.tgvItems.Name = "tgvItems";
         this.tgvItems.RowHeadersVisible = false;
         this.tgvItems.Size = new System.Drawing.Size(897, 512);
         this.tgvItems.TabIndex = 1;
         this.tgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.tgvItems_CurrentCellDirtyStateChanged);
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.DefaultNodeImage = null;
         this.clmnItem.HeaderText = "Товар";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         this.clmnItem.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnSklad
         // 
         this.clmnSklad.DataPropertyName = "Sklad";
         this.clmnSklad.HeaderText = "Склад";
         this.clmnSklad.Name = "clmnSklad";
         this.clmnSklad.Width = 400;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmSkladBind
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(897, 551);
         this.Controls.Add(this.tgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSkladBind";
         this.Text = "Привязка складов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private TreeGridView tgvItems;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripTextBox tsbFind;
      private System.Windows.Forms.ToolStripButton tsbClearFind;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private TreeGridColumn clmnItem;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnSklad;
   }
}
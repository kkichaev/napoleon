
namespace GRSoft.NapoleonManager
{
   partial class FmBrands
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBrands));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsAdd = new System.Windows.Forms.ToolStripButton();
         this.tsDel = new System.Windows.Forms.ToolStripButton();
         this.tsbUp = new System.Windows.Forms.ToolStripButton();
         this.tsbDn = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSave,
            this.tsAdd,
            this.tsDel,
            this.tsbUp,
            this.tsbDn});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(576, 48);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 45);
         this.tsbSave.Text = "toolStripButton1";
         this.tsbSave.ToolTipText = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click_1);
         // 
         // tsAdd
         // 
         this.tsAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsAdd.Name = "tsAdd";
         this.tsAdd.Size = new System.Drawing.Size(36, 45);
         this.tsAdd.Text = "Добавить";
         this.tsAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // tsDel
         // 
         this.tsDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsDel.Name = "tsDel";
         this.tsDel.Size = new System.Drawing.Size(36, 45);
         this.tsDel.Text = "Удалить";
         this.tsDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // tsbUp
         // 
         this.tsbUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.tsbUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUp.Name = "tsbUp";
         this.tsbUp.Size = new System.Drawing.Size(36, 45);
         this.tsbUp.Text = "toolStripButton2";
         this.tsbUp.ToolTipText = "Поднять вверх";
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         // 
         // tsbDn
         // 
         this.tsbDn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDn.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.tsbDn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDn.Name = "tsbDn";
         this.tsbDn.Size = new System.Drawing.Size(36, 45);
         this.tsbDn.Text = "toolStripButton3";
         this.tsbDn.ToolTipText = "Опустить вниз";
         this.tsbDn.Click += new System.EventHandler(this.tsbDn_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 48);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.RowHeadersWidth = 51;
         this.dgvItems.Size = new System.Drawing.Size(576, 570);
         this.dgvItems.TabIndex = 4;
         this.dgvItems.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellEndEdit);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Номенклатура";
         this.Column1.MinimumWidth = 6;
         this.Column1.Name = "Column1";
         // 
         // FmBrands
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(576, 618);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(3, 2, 3, 2);
         this.Name = "FmBrands";
         this.Text = "Бренд";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsAdd;
      private System.Windows.Forms.ToolStripButton tsDel;
      private System.Windows.Forms.ToolStripButton tsbUp;
      private System.Windows.Forms.ToolStripButton tsbDn;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
   }
}
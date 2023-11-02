using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class OffTakeCoeffEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OffTakeCoeffEditor));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.dgvPrice = new GRSoft.UILib.TreeGridView();
         this.clmnName = new GRSoft.UILib.TreeGridColumn();
         this.clmnCoef = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(505, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
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
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // dgvPrice
         // 
         this.dgvPrice.AllowUserToAddRows = false;
         this.dgvPrice.AllowUserToDeleteRows = false;
         this.dgvPrice.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnCoef});
         this.dgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.dgvPrice.ImageList = null;
         this.dgvPrice.Location = new System.Drawing.Point(0, 25);
         this.dgvPrice.Name = "dgvPrice";
         this.dgvPrice.ReadOnly = true;
         this.dgvPrice.RowHeadersVisible = false;
         this.dgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvPrice.Size = new System.Drawing.Size(505, 409);
         this.dgvPrice.TabIndex = 2;
         this.dgvPrice.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPrice_CellEnter);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DefaultNodeImage = null;
         this.clmnName.HeaderText = "Товар";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         this.clmnName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnCoef
         // 
         this.clmnCoef.HeaderText = "Коэфф.";
         this.clmnCoef.Name = "clmnCoef";
         this.clmnCoef.ReadOnly = true;
         this.clmnCoef.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // OffTakeCoeffEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(505, 434);
         this.Controls.Add(this.dgvPrice);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OffTakeCoeffEditor";
         this.Text = "Редктирование коэффициента предзаказа";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbSave;
      TreeGridView dgvPrice;
      private TreeGridColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCoef;
   }
}
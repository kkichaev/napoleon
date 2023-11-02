namespace GRSoft.NapoleonManager
{
   partial class OrderBundleOverview
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.dgvOrderItemsItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrderItemsItem,
            this.dgvOrderItemsQty,
            this.dgvOrderItemsCost});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(488, 185);
         this.dgvItems.TabIndex = 12;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         // 
         // dgvOrderItemsItem
         // 
         this.dgvOrderItemsItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsItem.DataPropertyName = "Item";
         this.dgvOrderItemsItem.FillWeight = 400F;
         this.dgvOrderItemsItem.HeaderText = "Наименование";
         this.dgvOrderItemsItem.Name = "dgvOrderItemsItem";
         // 
         // dgvOrderItemsQty
         // 
         this.dgvOrderItemsQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsQty.DataPropertyName = "Qty";
         this.dgvOrderItemsQty.HeaderText = "Количество";
         this.dgvOrderItemsQty.Name = "dgvOrderItemsQty";
         // 
         // dgvOrderItemsCost
         // 
         this.dgvOrderItemsCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsCost.DataPropertyName = "SCost";
         this.dgvOrderItemsCost.HeaderText = "Цена";
         this.dgvOrderItemsCost.Name = "dgvOrderItemsCost";
         // 
         // OrderBundleOverview
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Name = "OrderBundleOverview";
         this.Size = new System.Drawing.Size(488, 185);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      protected System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsItem;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsQty;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsCost;
   }
}

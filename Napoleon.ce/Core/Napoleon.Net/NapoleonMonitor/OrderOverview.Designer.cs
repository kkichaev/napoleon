namespace GRSoft.NapoleonManager
{
   partial class OrderOverview
   {
      /// <summary> 
      /// Требуется переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary> 
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором компонентов

      /// <summary> 
      /// Обязательный метод для поддержки конструктора - не изменяйте 
      /// содержимое данного метода при помощи редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.dgvOrderItemsItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrderItemsCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.orderRemark = new System.Windows.Forms.TextBox();
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
         this.dgvItems.Location = new System.Drawing.Point(0, 48);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(3, 4, 3, 4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(445, 211);
         this.dgvItems.TabIndex = 11;
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
         // orderRemark
         // 
         this.orderRemark.Dock = System.Windows.Forms.DockStyle.Top;
         this.orderRemark.Enabled = false;
         this.orderRemark.Location = new System.Drawing.Point(0, 0);
         this.orderRemark.Multiline = true;
         this.orderRemark.Name = "orderRemark";
         this.orderRemark.Size = new System.Drawing.Size(445, 48);
         this.orderRemark.TabIndex = 13;
         // 
         // OrderOverview
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.orderRemark);
         this.Name = "OrderOverview";
         this.Size = new System.Drawing.Size(445, 259);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsItem;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsQty;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsCost;
      private System.Windows.Forms.TextBox orderRemark;
   }
}

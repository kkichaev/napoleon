
namespace GRSoft.NapoleonManager
{
   partial class PurchaseView
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.orderRemark = new System.Windows.Forms.TextBox();
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
         this.dgvItems.Location = new System.Drawing.Point(0, 58);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.RowHeadersWidth = 51;
         this.dgvItems.Size = new System.Drawing.Size(593, 261);
         this.dgvItems.TabIndex = 11;
         // 
         // orderRemark
         // 
         this.orderRemark.Dock = System.Windows.Forms.DockStyle.Top;
         this.orderRemark.Enabled = false;
         this.orderRemark.Location = new System.Drawing.Point(0, 0);
         this.orderRemark.Margin = new System.Windows.Forms.Padding(4);
         this.orderRemark.Multiline = true;
         this.orderRemark.Name = "orderRemark";
         this.orderRemark.Size = new System.Drawing.Size(593, 58);
         this.orderRemark.TabIndex = 13;
         // 
         // dgvOrderItemsItem
         // 
         this.dgvOrderItemsItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsItem.DataPropertyName = "Name";
         this.dgvOrderItemsItem.FillWeight = 400F;
         this.dgvOrderItemsItem.HeaderText = "Наименование";
         this.dgvOrderItemsItem.MinimumWidth = 6;
         this.dgvOrderItemsItem.Name = "dgvOrderItemsItem";
         // 
         // dgvOrderItemsQty
         // 
         this.dgvOrderItemsQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsQty.DataPropertyName = "Weight";
         dataGridViewCellStyle1.Format = "N2";
         dataGridViewCellStyle1.NullValue = null;
         this.dgvOrderItemsQty.DefaultCellStyle = dataGridViewCellStyle1;
         this.dgvOrderItemsQty.HeaderText = "Вес";
         this.dgvOrderItemsQty.MinimumWidth = 6;
         this.dgvOrderItemsQty.Name = "dgvOrderItemsQty";
         // 
         // dgvOrderItemsCost
         // 
         this.dgvOrderItemsCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrderItemsCost.DataPropertyName = "Cost";
         this.dgvOrderItemsCost.HeaderText = "Цена";
         this.dgvOrderItemsCost.MinimumWidth = 6;
         this.dgvOrderItemsCost.Name = "dgvOrderItemsCost";
         // 
         // PurcharseView
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.orderRemark);
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "PurcharseView";
         this.Size = new System.Drawing.Size(593, 319);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.TextBox orderRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrderItemsCost;
   }
}

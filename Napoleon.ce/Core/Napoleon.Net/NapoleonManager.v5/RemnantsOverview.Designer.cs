namespace GRSoft.NapoleonManager
{
   partial class RemnantsOverview
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
         this.dgvRemnantsItemsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvRemnantsItemsQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.AllowUserToResizeRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvRemnantsItemsName,
            this.dgvRemnantsItemsQty});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(460, 268);
         this.dgvItems.TabIndex = 15;
         // 
         // dgvRemnantsItemsName
         // 
         this.dgvRemnantsItemsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvRemnantsItemsName.DataPropertyName = "Item";
         this.dgvRemnantsItemsName.FillWeight = 500F;
         this.dgvRemnantsItemsName.HeaderText = "Наименование";
         this.dgvRemnantsItemsName.Name = "dgvRemnantsItemsName";
         // 
         // dgvRemnantsItemsQty
         // 
         this.dgvRemnantsItemsQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvRemnantsItemsQty.DataPropertyName = "Qty";
         this.dgvRemnantsItemsQty.HeaderText = "Присутствие";
         this.dgvRemnantsItemsQty.Name = "dgvRemnantsItemsQty";
         // 
         // RemnantsOverview
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Name = "RemnantsOverview";
         this.Size = new System.Drawing.Size(460, 268);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      protected System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvRemnantsItemsName;
      protected System.Windows.Forms.DataGridViewTextBoxColumn dgvRemnantsItemsQty;
   }
}

namespace GRSoft.NapoleonManager
{
   partial class DistirbDetail
   {
      /// <summary> 
      /// Обязательная переменная конструктора.
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
      /// Требуемый метод для поддержки конструктора — не изменяйте 
      /// содержимое этого метода с помощью редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCost = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnCost});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.RowHeadersWidth = 51;
         this.dgvItems.Size = new System.Drawing.Size(691, 347);
         this.dgvItems.TabIndex = 1;
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Товар";
         this.clmnName.MinimumWidth = 6;
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         // 
         // clmnCost
         // 
         this.clmnCost.DataPropertyName = "Exists";
         this.clmnCost.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnCost.HeaderText = "Наличие";
         this.clmnCost.MinimumWidth = 6;
         this.clmnCost.Name = "clmnCost";
         this.clmnCost.ReadOnly = true;
         this.clmnCost.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnCost.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnCost.Width = 125;
         // 
         // DistirbDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Name = "DistirbDetail";
         this.Size = new System.Drawing.Size(691, 347);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnCost;
   }
}

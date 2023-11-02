namespace GRSoft.NapoleonManager
{
    partial class MerchDetail
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
            this.dgvItems = new System.Windows.Forms.DataGridView();
            this.dgvRemnantsItemsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvRemnantsItemsQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.clmnBestBefore = new System.Windows.Forms.DataGridViewTextBoxColumn();
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
            this.dgvRemnantsItemsQty,
            this.clmnBestBefore});
            this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvItems.Location = new System.Drawing.Point(0, 0);
            this.dgvItems.Name = "dgvItems";
            this.dgvItems.RowHeadersVisible = false;
            this.dgvItems.Size = new System.Drawing.Size(435, 313);
            this.dgvItems.TabIndex = 16;
            // 
            // dgvRemnantsItemsName
            // 
            this.dgvRemnantsItemsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.dgvRemnantsItemsName.DataPropertyName = "Name";
            this.dgvRemnantsItemsName.HeaderText = "Наименование";
            this.dgvRemnantsItemsName.Name = "dgvRemnantsItemsName";
            // 
            // dgvRemnantsItemsQty
            // 
            this.dgvRemnantsItemsQty.DataPropertyName = "Qty";
            this.dgvRemnantsItemsQty.HeaderText = "Количество";
            this.dgvRemnantsItemsQty.Name = "dgvRemnantsItemsQty";
            // 
            // clmnBestBefore
            // 
            this.clmnBestBefore.DataPropertyName = "BestBefore";
            this.clmnBestBefore.HeaderText = "Годен до";
            this.clmnBestBefore.Name = "clmnBestBefore";
            // 
            // MerchDetail
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.dgvItems);
            this.Name = "MerchDetail";
            this.Size = new System.Drawing.Size(435, 313);
            ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        protected System.Windows.Forms.DataGridView dgvItems;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvRemnantsItemsName;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvRemnantsItemsQty;
        private System.Windows.Forms.DataGridViewTextBoxColumn clmnBestBefore;
    }
}

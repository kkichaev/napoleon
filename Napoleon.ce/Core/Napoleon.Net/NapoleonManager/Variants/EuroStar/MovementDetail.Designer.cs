namespace GRSoft.NapoleonManager
{
   partial class MovementDetail
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
         this.labelSrcWh = new System.Windows.Forms.Label();
         this.labelDestWh = new System.Windows.Forms.Label();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnQty});
         this.dgvItems.Location = new System.Drawing.Point(0, 49);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(453, 223);
         this.dgvItems.TabIndex = 0;
         // 
         // labelSrcWh
         // 
         this.labelSrcWh.AutoSize = true;
         this.labelSrcWh.Location = new System.Drawing.Point(9, 9);
         this.labelSrcWh.Name = "labelSrcWh";
         this.labelSrcWh.Size = new System.Drawing.Size(35, 13);
         this.labelSrcWh.TabIndex = 1;
         this.labelSrcWh.Text = "label1";
         // 
         // labelDestWh
         // 
         this.labelDestWh.AutoSize = true;
         this.labelDestWh.Location = new System.Drawing.Point(9, 30);
         this.labelDestWh.Name = "labelDestWh";
         this.labelDestWh.Size = new System.Drawing.Size(35, 13);
         this.labelDestWh.TabIndex = 2;
         this.labelDestWh.Text = "label2";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Price";
         this.dataGridViewTextBoxColumn1.HeaderText = "Название";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn2.HeaderText = "Кол-во";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Price";
         this.clmnItem.HeaderText = "Название";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "Qty";
         this.clmnQty.HeaderText = "Кол-во";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.ReadOnly = true;
         // 
         // MovementDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.BackColor = System.Drawing.SystemColors.Control;
         this.Controls.Add(this.labelDestWh);
         this.Controls.Add(this.labelSrcWh);
         this.Controls.Add(this.dgvItems);
         this.Name = "MovementDetail";
         this.Size = new System.Drawing.Size(454, 275);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.Label labelSrcWh;
      private System.Windows.Forms.Label labelDestWh;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
   }
}

namespace GRSoft.NapoleonManager
{
   partial class RRItems
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
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSvQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
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
            this.clmnQty,
            this.clmnSvQty});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(465, 196);
         this.dgvItems.TabIndex = 0;
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Item";
         this.clmnName.HeaderText = "Товар";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "Qty";
         this.clmnQty.HeaderText = "Кол-во";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.ReadOnly = true;
         // 
         // clmnSvQty
         // 
         this.clmnSvQty.DataPropertyName = "SvQty";
         this.clmnSvQty.HeaderText = "Кол_во согласовано";
         this.clmnSvQty.Name = "clmnSvQty";
         this.clmnSvQty.ReadOnly = true;
         // 
         // RRItems
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Name = "RRItems";
         this.Size = new System.Drawing.Size(465, 196);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSvQty;
   }
}

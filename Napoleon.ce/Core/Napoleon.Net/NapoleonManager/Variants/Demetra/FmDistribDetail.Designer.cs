namespace GRSoft.NapoleonManager
{
   partial class FmDistribDetail
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
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnExist = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnExist});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(364, 251);
         this.dgvItems.TabIndex = 0;
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Группа";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.ReadOnly = true;
         // 
         // clmnExist
         // 
         this.clmnExist.DataPropertyName = "Exists";
         this.clmnExist.HeaderText = "";
         this.clmnExist.Name = "clmnExist";
         this.clmnExist.ReadOnly = true;
         this.clmnExist.Width = 50;
         // 
         // FmDistribDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Name = "FmDistribDetail";
         this.Size = new System.Drawing.Size(364, 251);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      public System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnExist;
   }
}

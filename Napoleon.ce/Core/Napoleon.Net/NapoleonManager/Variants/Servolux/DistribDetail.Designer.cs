namespace GRSoft.NapoleonManager
{
   partial class DistribDetail
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
         this.Column1 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.lbText = new System.Windows.Forms.Label();
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
            this.clmnName,
            this.Column1,
            this.Column2});
         this.dgvItems.Location = new System.Drawing.Point(0, 33);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(334, 191);
         this.dgvItems.TabIndex = 0;
         // 
         // Name
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Товар";
         this.clmnName.Name = "Name";
         this.clmnName.ReadOnly = true;
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "NoHave";
         this.Column1.HeaderText = "Нет";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         this.Column1.Width = 45;
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Exists";
         this.Column2.HeaderText = "Есть";
         this.Column2.Name = "Column2";
         this.Column2.ReadOnly = true;
         this.Column2.Width = 45;
         // 
         // lbText
         // 
         this.lbText.AutoSize = true;
         this.lbText.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbText.ForeColor = System.Drawing.Color.White;
         this.lbText.Location = new System.Drawing.Point(8, 9);
         this.lbText.Name = "lbText";
         this.lbText.Size = new System.Drawing.Size(42, 13);
         this.lbText.TabIndex = 1;
         this.lbText.Text = "lbText";
         // 
         // DistribDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.lbText);
         this.Controls.Add(this.dgvItems);
         this.Name = "DistribDetail";
         this.Size = new System.Drawing.Size(334, 224);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      public System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column1;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Column2;
      private System.Windows.Forms.Label lbText;
   }
}

namespace GRSoft.NapoleonManager
{
   partial class FmSunnySKUDetail
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

      #region Windows Form Designer generated code

      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSunnySKUDetail));
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnW1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnW2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnW3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnW4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnW5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnW1,
            this.clmnW2,
            this.clmnW3,
            this.clmnW4,
            this.clmnW5});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(631, 417);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         // 
         // clmnW1
         // 
         this.clmnW1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnW1.DataPropertyName = "W1";
         this.clmnW1.HeaderText = "Column1";
         this.clmnW1.Name = "clmnW1";
         this.clmnW1.ReadOnly = true;
         // 
         // clmnW2
         // 
         this.clmnW2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnW2.DataPropertyName = "W2";
         this.clmnW2.HeaderText = "Column1";
         this.clmnW2.Name = "clmnW2";
         this.clmnW2.ReadOnly = true;
         // 
         // clmnW3
         // 
         this.clmnW3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnW3.DataPropertyName = "W3";
         this.clmnW3.HeaderText = "Column1";
         this.clmnW3.Name = "clmnW3";
         this.clmnW3.ReadOnly = true;
         // 
         // clmnW4
         // 
         this.clmnW4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnW4.DataPropertyName = "W4";
         this.clmnW4.HeaderText = "Column1";
         this.clmnW4.Name = "clmnW4";
         this.clmnW4.ReadOnly = true;
         // 
         // clmnW5
         // 
         this.clmnW5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnW5.DataPropertyName = "W5";
         this.clmnW5.HeaderText = "Column1";
         this.clmnW5.Name = "clmnW5";
         this.clmnW5.ReadOnly = true;
         // 
         // FmSunnySKUDetail
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(631, 417);
         this.Controls.Add(this.dgvItems);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSunnySKUDetail";
         this.Text = "Ассортимент";
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnW1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnW2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnW3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnW4;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnW5;
   }
}
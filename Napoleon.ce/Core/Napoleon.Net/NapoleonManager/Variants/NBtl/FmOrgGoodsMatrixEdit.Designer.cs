namespace GRSoft.NapoleonManager
{
   partial class FmOrgGoodsMatrixEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgGoodsMatrixEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 417);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(373, 50);
         this.panel1.TabIndex = 0;
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(373, 417);
         this.grid.TabIndex = 1;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Матрица";
         this.Column1.Name = "Column1";
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(203, 12);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(284, 12);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // FmOrgGoodsMatrixEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(373, 467);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgGoodsMatrixEdit";
         this.Text = "Выберите матрицу";
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
   }
}
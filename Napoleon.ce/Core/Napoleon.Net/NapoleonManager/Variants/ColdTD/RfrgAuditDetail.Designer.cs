namespace GRSoft.NapoleonManager
{
   partial class RfrgAuditDetail
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
         this.labelExcl = new System.Windows.Forms.Label();
         this.clmnDocID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFactID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnRFID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnModel = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDescr = new System.Windows.Forms.DataGridViewTextBoxColumn();
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
            this.clmnDocID,
            this.clmnFactID,
            this.clmnRFID,
            this.clmnModel,
            this.clmnDescr});
         this.dgvItems.Location = new System.Drawing.Point(3, 29);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(521, 199);
         this.dgvItems.TabIndex = 0;
         // 
         // labelExcl
         // 
         this.labelExcl.AutoSize = true;
         this.labelExcl.Location = new System.Drawing.Point(5, 7);
         this.labelExcl.Name = "labelExcl";
         this.labelExcl.Size = new System.Drawing.Size(35, 13);
         this.labelExcl.TabIndex = 1;
         this.labelExcl.Text = "label1";
         // 
         // clmnDocID
         // 
         this.clmnDocID.DataPropertyName = "DocID";
         this.clmnDocID.HeaderText = "Учетные данные";
         this.clmnDocID.Name = "clmnDocID";
         this.clmnDocID.ReadOnly = true;
         // 
         // clmnFactID
         // 
         this.clmnFactID.DataPropertyName = "FactID";
         this.clmnFactID.HeaderText = "Факт.данные";
         this.clmnFactID.Name = "clmnFactID";
         this.clmnFactID.ReadOnly = true;
         // 
         // clmnRFID
         // 
         this.clmnRFID.DataPropertyName = "RFID";
         this.clmnRFID.HeaderText = "RFID";
         this.clmnRFID.Name = "clmnRFID";
         this.clmnRFID.ReadOnly = true;
         // 
         // clmnModel
         // 
         this.clmnModel.DataPropertyName = "Model";
         this.clmnModel.HeaderText = "Модель";
         this.clmnModel.Name = "clmnModel";
         this.clmnModel.ReadOnly = true;
         // 
         // clmnDescr
         // 
         this.clmnDescr.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnDescr.DataPropertyName = "Descr";
         this.clmnDescr.HeaderText = "Описание";
         this.clmnDescr.Name = "clmnDescr";
         this.clmnDescr.ReadOnly = true;
         // 
         // RfrgAuditDetail
         // 
         this.Controls.Add(this.labelExcl);
         this.Controls.Add(this.dgvItems);
         this.Name = "RfrgAuditDetail";
         this.Size = new System.Drawing.Size(527, 231);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.Label labelExcl;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDocID;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFactID;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnRFID;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnModel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDescr;

   }
}

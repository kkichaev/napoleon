namespace GRSoft.Ads
{
   partial class FmClientFind
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClientFind));
         this.dgvContact = new System.Windows.Forms.DataGridView();
         this.dgvContactName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).BeginInit();
         this.SuspendLayout();
         // 
         // dgvContact
         // 
         this.dgvContact.AllowUserToAddRows = false;
         this.dgvContact.AllowUserToDeleteRows = false;
         this.dgvContact.AllowUserToResizeRows = false;
         this.dgvContact.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContact.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvContactName,
            this.dgvContactAddress});
         this.dgvContact.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvContact.Location = new System.Drawing.Point(0, 0);
         this.dgvContact.MultiSelect = false;
         this.dgvContact.Name = "dgvContact";
         this.dgvContact.ReadOnly = true;
         this.dgvContact.RowHeadersVisible = false;
         this.dgvContact.Size = new System.Drawing.Size(399, 351);
         this.dgvContact.TabIndex = 0;
         this.dgvContact.DoubleClick += new System.EventHandler(this.dgvContact_DoubleClick);
         // 
         // dgvContactName
         // 
         this.dgvContactName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactName.DataPropertyName = "Name";
         this.dgvContactName.FillWeight = 71.06599F;
         this.dgvContactName.HeaderText = "Имя";
         this.dgvContactName.Name = "dgvContactName";
         this.dgvContactName.ReadOnly = true;
         // 
         // dgvContactAddress
         // 
         this.dgvContactAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactAddress.DataPropertyName = "Address";
         this.dgvContactAddress.FillWeight = 128.934F;
         this.dgvContactAddress.HeaderText = "Адрес";
         this.dgvContactAddress.Name = "dgvContactAddress";
         this.dgvContactAddress.ReadOnly = true;
         // 
         // FmClientFind
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(399, 351);
         this.Controls.Add(this.dgvContact);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClientFind";
         this.Text = "Поиск контакта";
         this.Load += new System.EventHandler(this.FmClientFind_Load);
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvContact;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactAddress;
   }
}
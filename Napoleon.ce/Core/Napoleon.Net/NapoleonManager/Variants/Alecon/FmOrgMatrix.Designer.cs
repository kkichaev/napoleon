namespace GRSoft.NapoleonManager
{
   partial class FmOrgMatrix
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgMatrix));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.dgvOrg = new System.Windows.Forms.DataGridView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgMatrix = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 290);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(553, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.cbAgents});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(553, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(121, 25);
         // 
         // dgvOrg
         // 
         this.dgvOrg.AllowUserToAddRows = false;
         this.dgvOrg.AllowUserToResizeRows = false;
         this.dgvOrg.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrg.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrgName,
            this.dgvOrgMatrix});
         this.dgvOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrg.Location = new System.Drawing.Point(0, 25);
         this.dgvOrg.Name = "dgvOrg";
         this.dgvOrg.RowHeadersVisible = false;
         this.dgvOrg.Size = new System.Drawing.Size(553, 265);
         this.dgvOrg.TabIndex = 2;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dgvOrgName
         // 
         this.dgvOrgName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgName.DataPropertyName = "Name";
         this.dgvOrgName.HeaderText = "Наименование";
         this.dgvOrgName.Name = "dgvOrgName";
         // 
         // dgvOrgMatrix
         // 
         this.dgvOrgMatrix.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgMatrix.DataPropertyName = "Matrix";
         this.dgvOrgMatrix.FillWeight = 30F;
         this.dgvOrgMatrix.HeaderText = "Матрица";
         this.dgvOrgMatrix.Name = "dgvOrgMatrix";
         // 
         // FmOrgMatrix
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(553, 312);
         this.Controls.Add(this.dgvOrg);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgMatrix";
         this.Text = "Редактор ассортиментных матриц";
         this.Load += new System.EventHandler(this.FmOrgMatrix_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrg)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvOrg;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgName;
      private System.Windows.Forms.DataGridViewComboBoxColumn dgvOrgMatrix;
   }
}
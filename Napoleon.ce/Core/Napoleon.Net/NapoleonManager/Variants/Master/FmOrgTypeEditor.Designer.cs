namespace GRSoft.NapoleonManager
{
   partial class FmOrgTypeEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgTypeEditor));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnOrgType = new System.Windows.Forms.ToolStripButton();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnTypes = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.btnOrgType});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(657, 25);
         this.toolStrip1.TabIndex = 0;
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
         // btnOrgType
         // 
         this.btnOrgType.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrgType.Image = global::GRSoft.NapoleonManager.Properties.Resources.bonus_doc;
         this.btnOrgType.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrgType.Name = "btnOrgType";
         this.btnOrgType.Size = new System.Drawing.Size(23, 22);
         this.btnOrgType.Text = "Типы ТТ";
         this.btnOrgType.Click += new System.EventHandler(this.btnOrgType_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.AllowUserToResizeRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.clmnTypes});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 25);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(657, 393);
         this.grid.TabIndex = 1;
         this.grid.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellEndEdit);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Наименоваие";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Address";
         this.Column2.HeaderText = "Адрес";
         this.Column2.Name = "Column2";
         this.Column2.ReadOnly = true;
         // 
         // clmnTypes
         // 
         this.clmnTypes.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnTypes.DataPropertyName = "Type";
         this.clmnTypes.HeaderText = "Тип";
         this.clmnTypes.Name = "clmnTypes";
         // 
         // FmOrgTypeEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(657, 418);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgTypeEditor";
         this.Text = "Редактор типов торговых точек";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrgTypeEditor_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnOrgType;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnTypes;
   }
}
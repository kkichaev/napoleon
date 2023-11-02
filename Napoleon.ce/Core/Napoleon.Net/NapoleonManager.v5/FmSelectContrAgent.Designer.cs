namespace GRSoft.NapoleonManager
{
   partial class FmSelectContrAgent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectContrAgent));
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.dgvOrgsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgsAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tstbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tcbFilter = new System.Windows.Forms.ToolStripComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvOrgs);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(524, 500);
         this.panel1.TabIndex = 0;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrgsName,
            this.dgvOrgsAddress});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(7, 8);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(510, 484);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_CellDoubleClick);
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         this.dgvOrgs.KeyDown += new System.Windows.Forms.KeyEventHandler(this.dgvOrgs_KeyDown);
         this.dgvOrgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseDown);
         this.dgvOrgs.MouseMove += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseMove);
         // 
         // dgvOrgsName
         // 
         this.dgvOrgsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsName.DataPropertyName = "Name";
         this.dgvOrgsName.HeaderText = "Контрагент";
         this.dgvOrgsName.Name = "dgvOrgsName";
         this.dgvOrgsName.ReadOnly = true;
         // 
         // dgvOrgsAddress
         // 
         this.dgvOrgsAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsAddress.DataPropertyName = "Address";
         this.dgvOrgsAddress.HeaderText = "Адрес";
         this.dgvOrgsAddress.Name = "dgvOrgsAddress";
         this.dgvOrgsAddress.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tstbFind,
            this.tcbFilter});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(524, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tstbFind
         // 
         this.tstbFind.Name = "tstbFind";
         this.tstbFind.Size = new System.Drawing.Size(175, 25);
         this.tstbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tstbFind_KeyDown);
         // 
         // tcbFilter
         // 
         this.tcbFilter.Items.AddRange(new object[] {
            "Все",
            "Выбранные",
            "Не выбранные"});
         this.tcbFilter.Name = "tcbFilter";
         this.tcbFilter.Size = new System.Drawing.Size(150, 25);
         this.tcbFilter.Visible = false;
         this.tcbFilter.SelectedIndexChanged += new System.EventHandler(this.tcbFilter_SelectedIndexChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.FillWeight = 25F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Color";
         this.dataGridViewTextBoxColumn2.FillWeight = 30F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Цвет(R,G,B)";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Address";
         this.dataGridViewTextBoxColumn3.FillWeight = 25F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Адрес";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // FmSelectContrAgent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(524, 525);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectContrAgent";
         this.Text = "Выберите контрагента";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmSelectContrAgent_FormClosed);
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripTextBox tstbFind;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      protected System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.ToolStripComboBox tcbFilter;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsAddress;
      protected System.Windows.Forms.ToolStrip toolStrip1;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmOrgProp
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgProp));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgent = new System.Windows.Forms.ToolStripComboBox();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel5 = new System.Windows.Forms.ToolStripLabel();
         this.cbMatrix = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel6 = new System.Windows.Forms.ToolStripLabel();
         this.cbScript = new System.Windows.Forms.ToolStripComboBox();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.edOrg = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.edMatrix = new System.Windows.Forms.ToolStripTextBox();
         this.toolStripLabel4 = new System.Windows.Forms.ToolStripLabel();
         this.edScript = new System.Windows.Forms.ToolStripTextBox();
         this.btnClear = new System.Windows.Forms.ToolStripButton();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgMatrix = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.dgvOrgScript = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.cbAgent,
            this.btnRefresh,
            this.btnSave,
            this.toolStripLabel5,
            this.cbMatrix,
            this.toolStripLabel6,
            this.cbScript});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(841, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(50, 36);
         this.toolStripLabel1.Text = "Агент";
         // 
         // cbAgent
         // 
         this.cbAgent.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(210, 39);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "toolStripButton1";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripLabel5
         // 
         this.toolStripLabel5.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel5.Name = "toolStripLabel5";
         this.toolStripLabel5.Size = new System.Drawing.Size(75, 36);
         this.toolStripLabel5.Text = "Матрица";
         // 
         // cbMatrix
         // 
         this.cbMatrix.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbMatrix.Name = "cbMatrix";
         this.cbMatrix.Size = new System.Drawing.Size(121, 39);
         this.cbMatrix.SelectedIndexChanged += new System.EventHandler(this.ComboBox_SelectedIndexChanged);
         // 
         // toolStripLabel6
         // 
         this.toolStripLabel6.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel6.Name = "toolStripLabel6";
         this.toolStripLabel6.Size = new System.Drawing.Size(82, 36);
         this.toolStripLabel6.Text = "Сценарий";
         // 
         // cbScript
         // 
         this.cbScript.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbScript.Name = "cbScript";
         this.cbScript.Size = new System.Drawing.Size(121, 39);
         this.cbScript.SelectedIndexChanged += new System.EventHandler(this.ComboBox_SelectedIndexChanged);
         // 
         // toolStrip2
         // 
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel2,
            this.edOrg,
            this.toolStripLabel3,
            this.edMatrix,
            this.toolStripLabel4,
            this.edScript,
            this.btnClear});
         this.toolStrip2.Location = new System.Drawing.Point(0, 39);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(841, 39);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(62, 36);
         this.toolStripLabel2.Text = "фильтр";
         // 
         // edOrg
         // 
         this.edOrg.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.edOrg.Name = "edOrg";
         this.edOrg.Size = new System.Drawing.Size(100, 39);
         this.edOrg.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(19, 36);
         this.toolStripLabel3.Text = "и";
         // 
         // edMatrix
         // 
         this.edMatrix.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.edMatrix.Name = "edMatrix";
         this.edMatrix.Size = new System.Drawing.Size(100, 39);
         this.edMatrix.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // toolStripLabel4
         // 
         this.toolStripLabel4.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel4.Name = "toolStripLabel4";
         this.toolStripLabel4.Size = new System.Drawing.Size(19, 36);
         this.toolStripLabel4.Text = "и";
         // 
         // edScript
         // 
         this.edScript.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.edScript.Name = "edScript";
         this.edScript.Size = new System.Drawing.Size(100, 39);
         this.edScript.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnClear
         // 
         this.btnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(36, 36);
         this.btnClear.Text = "Очистить";
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.dgvOrgMatrix,
            this.dgvOrgScript});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 78);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(841, 528);
         this.grid.TabIndex = 2;
         this.grid.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_CellValueChanged);
         this.grid.CurrentCellDirtyStateChanged += new System.EventHandler(this.grid_CurrentCellDirtyStateChanged);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.FillWeight = 50F;
         this.Column1.HeaderText = "Организация";
         this.Column1.Name = "Column1";
         // 
         // dgvOrgMatrix
         // 
         this.dgvOrgMatrix.DataPropertyName = "Matrix";
         this.dgvOrgMatrix.HeaderText = "Матрица";
         this.dgvOrgMatrix.Name = "dgvOrgMatrix";
         // 
         // dgvOrgScript
         // 
         this.dgvOrgScript.DataPropertyName = "Script";
         this.dgvOrgScript.HeaderText = "Сценарий";
         this.dgvOrgScript.Name = "dgvOrgScript";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.FillWeight = 50F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Организация";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.FillWeight = 25F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Матрица";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.FillWeight = 25F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Сценарий";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // timer1
         // 
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmOrgProp
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(841, 606);
         this.Controls.Add(this.grid);
         this.Controls.Add(this.toolStrip2);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgProp";
         this.Text = "Привязка матриц";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrgProp_FormClosing);
         this.Load += new System.EventHandler(this.FmOrgProp_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbAgent;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripTextBox edOrg;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripTextBox edMatrix;
      private System.Windows.Forms.ToolStripLabel toolStripLabel4;
      private System.Windows.Forms.ToolStripTextBox edScript;
      private System.Windows.Forms.ToolStripButton btnClear;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewComboBoxColumn dgvOrgMatrix;
      private System.Windows.Forms.DataGridViewComboBoxColumn dgvOrgScript;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel5;
      private System.Windows.Forms.ToolStripComboBox cbMatrix;
      private System.Windows.Forms.ToolStripLabel toolStripLabel6;
      private System.Windows.Forms.ToolStripComboBox cbScript;
   }
}
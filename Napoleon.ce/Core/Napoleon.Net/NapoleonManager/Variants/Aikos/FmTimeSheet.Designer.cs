namespace GRSoft.NapoleonManager
{
    partial class FmTimeSheet
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTimeSheet));
            this.tsbConfig = new System.Windows.Forms.ToolStrip();
            this.btnRefresh = new System.Windows.Forms.ToolStripButton();
            this.tsbSave = new System.Windows.Forms.ToolStripButton();
            this.btnTabel = new System.Windows.Forms.ToolStripButton();
            this.dtpDate = new System.Windows.Forms.DateTimePicker();
            this.dgvItems = new System.Windows.Forms.DataGridView();
            this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.clmnDay1 = new System.Windows.Forms.DataGridViewComboBoxColumn();
            this.tsbConfig.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
            this.SuspendLayout();
            // 
            // tsbConfig
            // 
            this.tsbConfig.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.tsbConfig.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.tsbSave,
            this.btnTabel});
            this.tsbConfig.Location = new System.Drawing.Point(0, 0);
            this.tsbConfig.Name = "tsbConfig";
            this.tsbConfig.Size = new System.Drawing.Size(1244, 39);
            this.tsbConfig.TabIndex = 11;
            // 
            // btnRefresh
            // 
            this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
            this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnRefresh.Margin = new System.Windows.Forms.Padding(170, 1, 0, 2);
            this.btnRefresh.Name = "btnRefresh";
            this.btnRefresh.Size = new System.Drawing.Size(36, 36);
            this.btnRefresh.Text = "toolStripButton1";
            this.btnRefresh.ToolTipText = "Обновить";
            // 
            // tsbSave
            // 
            this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.tsbSave.Enabled = false;
            this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
            this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.tsbSave.Name = "tsbSave";
            this.tsbSave.Size = new System.Drawing.Size(36, 36);
            this.tsbSave.Text = "Сохранить";
            this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
            // 
            // btnTabel
            // 
            this.btnTabel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.btnTabel.Image = global::GRSoft.NapoleonManager.Properties.Resources.excel;
            this.btnTabel.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnTabel.Name = "btnTabel";
            this.btnTabel.Size = new System.Drawing.Size(36, 36);
            this.btnTabel.Text = "Табель учета рабочего времени";
            this.btnTabel.Click += new System.EventHandler(this.btnTabel_Click);
            // 
            // dtpDate
            // 
            this.dtpDate.CustomFormat = "MMMM yyyy";
            this.dtpDate.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
            this.dtpDate.Location = new System.Drawing.Point(11, 5);
            this.dtpDate.Name = "dtpDate";
            this.dtpDate.Size = new System.Drawing.Size(159, 26);
            this.dtpDate.TabIndex = 12;
            this.dtpDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
            this.dtpDate.ValueChanged += new System.EventHandler(this.dtpDate_ValueChanged);
            // 
            // dgvItems
            // 
            this.dgvItems.AllowUserToAddRows = false;
            this.dgvItems.AllowUserToDeleteRows = false;
            this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnDay1});
            this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
            this.dgvItems.Location = new System.Drawing.Point(0, 39);
            this.dgvItems.Name = "dgvItems";
            this.dgvItems.RowHeadersVisible = false;
            this.dgvItems.Size = new System.Drawing.Size(1244, 679);
            this.dgvItems.TabIndex = 13;
            this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
            this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
            // 
            // clmnAgent
            // 
            this.clmnAgent.DataPropertyName = "Name";
            this.clmnAgent.Frozen = true;
            this.clmnAgent.HeaderText = "ФИО";
            this.clmnAgent.Name = "clmnAgent";
            this.clmnAgent.Width = 300;
            // 
            // clmnDay1
            // 
            this.clmnDay1.DataPropertyName = "Day1";
            this.clmnDay1.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
            this.clmnDay1.FillWeight = 50F;
            this.clmnDay1.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.clmnDay1.HeaderText = "1";
            this.clmnDay1.Name = "clmnDay1";
            this.clmnDay1.Width = 50;
            // 
            // FmTimeSheet
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1244, 718);
            this.Controls.Add(this.dgvItems);
            this.Controls.Add(this.dtpDate);
            this.Controls.Add(this.tsbConfig);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "FmTimeSheet";
            this.Text = "Табель учета рабочего времени";
            this.tsbConfig.ResumeLayout(false);
            this.tsbConfig.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.ToolStrip tsbConfig;
        protected System.Windows.Forms.ToolStripButton btnRefresh;
        protected System.Windows.Forms.DateTimePicker dtpDate;
        private System.Windows.Forms.ToolStripButton tsbSave;
        private System.Windows.Forms.DataGridView dgvItems;
        private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
        private System.Windows.Forms.DataGridViewComboBoxColumn clmnDay1;
        private System.Windows.Forms.ToolStripButton btnTabel;
    }
}
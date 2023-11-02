namespace GRSoft.NapoleonManager
{
   partial class FmRegionRoute
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRegionRoute));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindDown = new System.Windows.Forms.ToolStripButton();
         this.btnFindUp = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvRoute = new System.Windows.Forms.TreeView();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tbRoute = new System.Windows.Forms.ToolStripTextBox();
         this.btnAddDay = new System.Windows.Forms.ToolStripButton();
         this.btnEditDay = new System.Windows.Forms.ToolStripButton();
         this.btnDelDay = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.cbRegionR2 = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.cbRegionR1 = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.dgvRegion = new System.Windows.Forms.DataGridView();
         this.dgvRegionName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnAddReg = new System.Windows.Forms.ToolStripButton();
         this.btnEditReg = new System.Windows.Forms.ToolStripButton();
         this.btnDelReg = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRegionR1R2 = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRegion)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.btnSave,
            this.btnRefresh,
            this.toolStripSeparator2,
            this.tbFind,
            this.btnFindDown,
            this.btnFindUp});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(658, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(121, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnFindDown
         // 
         this.btnFindDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindDown.Image = ((System.Drawing.Image)(resources.GetObject("btnFindDown.Image")));
         this.btnFindDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindDown.Name = "btnFindDown";
         this.btnFindDown.Size = new System.Drawing.Size(23, 22);
         this.btnFindDown.Text = "Искать вперед";
         this.btnFindDown.Click += new System.EventHandler(this.btnFindDown_Click);
         // 
         // btnFindUp
         // 
         this.btnFindUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindUp.Image = ((System.Drawing.Image)(resources.GetObject("btnFindUp.Image")));
         this.btnFindUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindUp.Name = "btnFindUp";
         this.btnFindUp.Size = new System.Drawing.Size(23, 22);
         this.btnFindUp.Text = "Искать назад";
         this.btnFindUp.Click += new System.EventHandler(this.btnFindUp_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 434);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(658, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvRoute);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvRegion);
         this.splitContainer1.Panel2.Controls.Add(this.panel1);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer1.Size = new System.Drawing.Size(658, 409);
         this.splitContainer1.SplitterDistance = 285;
         this.splitContainer1.TabIndex = 2;
         // 
         // tvRoute
         // 
         this.tvRoute.AllowDrop = true;
         this.tvRoute.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvRoute.HideSelection = false;
         this.tvRoute.Location = new System.Drawing.Point(0, 25);
         this.tvRoute.Name = "tvRoute";
         this.tvRoute.Size = new System.Drawing.Size(285, 384);
         this.tvRoute.TabIndex = 1;
         this.tvRoute.MouseUp += new System.Windows.Forms.MouseEventHandler(this.tvRoute_MouseUp);
         this.tvRoute.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvRoute_DragDrop);
         this.tvRoute.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvRoute_MouseDown);
         this.tvRoute.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvRoute_DragEnter);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbRoute,
            this.btnAddDay,
            this.btnEditDay,
            this.btnDelDay,
            this.toolStripSeparator1,
            this.btnUp,
            this.btnDown});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(285, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tbRoute
         // 
         this.tbRoute.Name = "tbRoute";
         this.tbRoute.Size = new System.Drawing.Size(160, 25);
         // 
         // btnAddDay
         // 
         this.btnAddDay.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddDay.Image = ((System.Drawing.Image)(resources.GetObject("btnAddDay.Image")));
         this.btnAddDay.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddDay.Name = "btnAddDay";
         this.btnAddDay.Size = new System.Drawing.Size(23, 22);
         this.btnAddDay.Text = "Добавить";
         this.btnAddDay.Click += new System.EventHandler(this.btnAddDay_Click);
         // 
         // btnEditDay
         // 
         this.btnEditDay.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditDay.Image = ((System.Drawing.Image)(resources.GetObject("btnEditDay.Image")));
         this.btnEditDay.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditDay.Name = "btnEditDay";
         this.btnEditDay.Size = new System.Drawing.Size(23, 22);
         this.btnEditDay.Text = "Изменить";
         this.btnEditDay.Click += new System.EventHandler(this.btnEditDay_Click);
         // 
         // btnDelDay
         // 
         this.btnDelDay.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelDay.Image = ((System.Drawing.Image)(resources.GetObject("btnDelDay.Image")));
         this.btnDelDay.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelDay.Name = "btnDelDay";
         this.btnDelDay.Size = new System.Drawing.Size(23, 22);
         this.btnDelDay.Text = "Удалить";
         this.btnDelDay.Click += new System.EventHandler(this.btnDelDay_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = ((System.Drawing.Image)(resources.GetObject("btnUp.Image")));
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(23, 20);
         this.btnUp.Text = "Переместить вверх";
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = ((System.Drawing.Image)(resources.GetObject("btnDown.Image")));
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(23, 20);
         this.btnDown.Text = "Переместить вниз";
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.cbRegionR2);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.cbRegionR1);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 343);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(369, 66);
         this.panel1.TabIndex = 2;
         // 
         // cbRegionR2
         // 
         this.cbRegionR2.FormattingEnabled = true;
         this.cbRegionR2.Location = new System.Drawing.Point(69, 35);
         this.cbRegionR2.Name = "cbRegionR2";
         this.cbRegionR2.Size = new System.Drawing.Size(172, 22);
         this.cbRegionR2.TabIndex = 3;
         this.cbRegionR2.SelectionChangeCommitted += new System.EventHandler(this.cbRegionR2_SelectedIndexChanged);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 38);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(51, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Область";
         // 
         // cbRegionR1
         // 
         this.cbRegionR1.FormattingEnabled = true;
         this.cbRegionR1.Location = new System.Drawing.Point(69, 7);
         this.cbRegionR1.Name = "cbRegionR1";
         this.cbRegionR1.Size = new System.Drawing.Size(172, 22);
         this.cbRegionR1.TabIndex = 1;
         this.cbRegionR1.SelectionChangeCommitted += new System.EventHandler(this.cbRegionR1_SelectedIndexChanged);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(7, 10);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Район";
         // 
         // dgvRegion
         // 
         this.dgvRegion.AllowUserToAddRows = false;
         this.dgvRegion.AllowUserToDeleteRows = false;
         this.dgvRegion.AllowUserToResizeRows = false;
         this.dgvRegion.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvRegion.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvRegionName});
         this.dgvRegion.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvRegion.Location = new System.Drawing.Point(0, 25);
         this.dgvRegion.Name = "dgvRegion";
         this.dgvRegion.RowHeadersVisible = false;
         this.dgvRegion.Size = new System.Drawing.Size(369, 318);
         this.dgvRegion.TabIndex = 1;
         this.dgvRegion.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvRegion_MouseDown);
         // 
         // dgvRegionName
         // 
         this.dgvRegionName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvRegionName.DataPropertyName = "Name";
         this.dgvRegionName.HeaderText = "Н/П";
         this.dgvRegionName.Name = "dgvRegionName";
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddReg,
            this.btnEditReg,
            this.btnDelReg,
            this.toolStripSeparator3,
            this.btnRegionR1R2});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(369, 25);
         this.toolStrip3.TabIndex = 0;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // btnAddReg
         // 
         this.btnAddReg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddReg.Image = ((System.Drawing.Image)(resources.GetObject("btnAddReg.Image")));
         this.btnAddReg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddReg.Name = "btnAddReg";
         this.btnAddReg.Size = new System.Drawing.Size(23, 22);
         this.btnAddReg.Text = "Добавить";
         this.btnAddReg.Click += new System.EventHandler(this.btnAddReg_Click);
         // 
         // btnEditReg
         // 
         this.btnEditReg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditReg.Image = ((System.Drawing.Image)(resources.GetObject("btnEditReg.Image")));
         this.btnEditReg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditReg.Name = "btnEditReg";
         this.btnEditReg.Size = new System.Drawing.Size(23, 22);
         this.btnEditReg.Text = "Изменить";
         this.btnEditReg.Click += new System.EventHandler(this.btnEditReg_Click);
         // 
         // btnDelReg
         // 
         this.btnDelReg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelReg.Image = ((System.Drawing.Image)(resources.GetObject("btnDelReg.Image")));
         this.btnDelReg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelReg.Name = "btnDelReg";
         this.btnDelReg.Size = new System.Drawing.Size(23, 22);
         this.btnDelReg.Text = "Удалить";
         this.btnDelReg.Click += new System.EventHandler(this.btnDelReg_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRegionR1R2
         // 
         this.btnRegionR1R2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRegionR1R2.Image = global::GRSoft.NapoleonManager.Properties.Resources.software_update_current;
         this.btnRegionR1R2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRegionR1R2.Name = "btnRegionR1R2";
         this.btnRegionR1R2.Size = new System.Drawing.Size(23, 22);
         this.btnRegionR1R2.Text = "Область и регион";
         this.btnRegionR1R2.Click += new System.EventHandler(this.btnRegionR1R2_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.FillWeight = 81.47208F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmRegionRoute
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(658, 456);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRegionRoute";
         this.Text = "Редактирование маршрута";
         this.Load += new System.EventHandler(this.FmRegionRoute_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmRegionRoute_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRegion)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.TreeView tvRoute;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripTextBox tbRoute;
      private System.Windows.Forms.ToolStripButton btnAddDay;
      private System.Windows.Forms.ToolStripButton btnDelDay;
      private System.Windows.Forms.ToolStripButton btnEditDay;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAddReg;
      private System.Windows.Forms.ToolStripButton btnDelReg;
      private System.Windows.Forms.ToolStripButton btnEditReg;
      private System.Windows.Forms.DataGridView dgvRegion;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnDown;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindDown;
      private System.Windows.Forms.ToolStripButton btnFindUp;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton btnRegionR1R2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ComboBox cbRegionR2;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbRegionR1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvRegionName;
   }
}
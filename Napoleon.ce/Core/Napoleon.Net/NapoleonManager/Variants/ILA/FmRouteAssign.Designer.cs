namespace GRSoft.NapoleonManager
{
   partial class FmRouteAssign
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRouteAssign));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnPos = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.olOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgsDay = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgW1 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.dgvOrgW2 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.dgvOrgW3 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.dgvOrgW4 = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.wb = new System.Windows.Forms.WebBrowser();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel5 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.cbSelectDay = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbShowMap = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbAddOrg = new System.Windows.Forms.ToolStripButton();
         this.tsbDelete = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator4 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbUp = new System.Windows.Forms.ToolStripButton();
         this.tsbDown = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.cbWeek = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel4 = new System.Windows.Forms.ToolStripLabel();
         this.btnReport = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dtpRouteStart = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(7, 7);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvOrgs);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.wb);
         this.splitContainer1.Size = new System.Drawing.Size(1336, 627);
         this.splitContainer1.SplitterDistance = 671;
         this.splitContainer1.SplitterWidth = 7;
         this.splitContainer1.TabIndex = 14;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowDrop = true;
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.AllowUserToResizeRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnPos,
            this.olOrg,
            this.dgvOrgsDay,
            this.dgvOrgW1,
            this.dgvOrgW2,
            this.dgvOrgW3,
            this.dgvOrgW4});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(671, 627);
         this.dgvOrgs.TabIndex = 1;
         // 
         // clmnPos
         // 
         this.clmnPos.DataPropertyName = "Pos";
         this.clmnPos.HeaderText = "№";
         this.clmnPos.Name = "clmnPos";
         this.clmnPos.Width = 27;
         // 
         // olOrg
         // 
         this.olOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.olOrg.DataPropertyName = "OrgName";
         this.olOrg.FillWeight = 600F;
         this.olOrg.HeaderText = "Контрагент";
         this.olOrg.Name = "olOrg";
         this.olOrg.ReadOnly = true;
         this.olOrg.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.olOrg.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dgvOrgsDay
         // 
         this.dgvOrgsDay.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsDay.DataPropertyName = "Day";
         dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         this.dgvOrgsDay.DefaultCellStyle = dataGridViewCellStyle1;
         this.dgvOrgsDay.FillWeight = 200F;
         this.dgvOrgsDay.HeaderText = "День";
         this.dgvOrgsDay.Name = "dgvOrgsDay";
         this.dgvOrgsDay.ReadOnly = true;
         // 
         // dgvOrgW1
         // 
         this.dgvOrgW1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgW1.DataPropertyName = "W1";
         this.dgvOrgW1.FillWeight = 200F;
         this.dgvOrgW1.HeaderText = "Неделя1";
         this.dgvOrgW1.Name = "dgvOrgW1";
         // 
         // dgvOrgW2
         // 
         this.dgvOrgW2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgW2.DataPropertyName = "W2";
         this.dgvOrgW2.FillWeight = 200F;
         this.dgvOrgW2.HeaderText = "Неделя2";
         this.dgvOrgW2.Name = "dgvOrgW2";
         // 
         // dgvOrgW3
         // 
         this.dgvOrgW3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgW3.DataPropertyName = "W3";
         this.dgvOrgW3.FillWeight = 200F;
         this.dgvOrgW3.HeaderText = "Неделя3";
         this.dgvOrgW3.Name = "dgvOrgW3";
         // 
         // dgvOrgW4
         // 
         this.dgvOrgW4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgW4.DataPropertyName = "W4";
         this.dgvOrgW4.FillWeight = 200F;
         this.dgvOrgW4.HeaderText = "Неделя4";
         this.dgvOrgW4.Name = "dgvOrgW4";
         // 
         // wb
         // 
         this.wb.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wb.Location = new System.Drawing.Point(0, 0);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.ScriptErrorsSuppressed = true;
         this.wb.Size = new System.Drawing.Size(658, 627);
         this.wb.TabIndex = 0;
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel5,
            this.cbAgents,
            this.toolStripLabel2,
            this.cbSelectDay,
            this.toolStripSeparator2,
            this.tsbShowMap,
            this.toolStripSeparator3,
            this.tsbSave,
            this.tsbAddOrg,
            this.tsbDelete,
            this.toolStripSeparator4,
            this.tsbUp,
            this.tsbDown,
            this.toolStripLabel3,
            this.cbWeek,
            this.toolStripLabel4,
            this.btnReport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1350, 39);
         this.toolStrip1.TabIndex = 15;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel5
         // 
         this.toolStripLabel5.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel5.Name = "toolStripLabel5";
         this.toolStripLabel5.Size = new System.Drawing.Size(49, 36);
         this.toolStripLabel5.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FlatStyle = System.Windows.Forms.FlatStyle.Standard;
         this.cbAgents.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(200, 39);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(45, 36);
         this.toolStripLabel2.Text = "День";
         // 
         // cbSelectDay
         // 
         this.cbSelectDay.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbSelectDay.FlatStyle = System.Windows.Forms.FlatStyle.Standard;
         this.cbSelectDay.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbSelectDay.Name = "cbSelectDay";
         this.cbSelectDay.Size = new System.Drawing.Size(180, 39);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Margin = new System.Windows.Forms.Padding(1, 0, 0, 0);
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbShowMap
         // 
         this.tsbShowMap.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbShowMap.Image = global::GRSoft.NapoleonManager.Properties.Resources.route2;
         this.tsbShowMap.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbShowMap.Margin = new System.Windows.Forms.Padding(2, 1, 0, 2);
         this.tsbShowMap.Name = "tsbShowMap";
         this.tsbShowMap.Size = new System.Drawing.Size(36, 36);
         this.tsbShowMap.Text = "Показать на карте";
         this.tsbShowMap.Click += new System.EventHandler(this.tsbShowMap_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tsbAddOrg
         // 
         this.tsbAddOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAddOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.ca_add;
         this.tsbAddOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAddOrg.Name = "tsbAddOrg";
         this.tsbAddOrg.Size = new System.Drawing.Size(36, 36);
         this.tsbAddOrg.Text = "Добавить контрагента";
         this.tsbAddOrg.Click += new System.EventHandler(this.tsbAddOrg_Click);
         // 
         // tsbDelete
         // 
         this.tsbDelete.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDelete.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.tsbDelete.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDelete.Name = "tsbDelete";
         this.tsbDelete.Size = new System.Drawing.Size(36, 36);
         this.tsbDelete.Text = "Удалить";
         this.tsbDelete.Click += new System.EventHandler(this.tsbDelete_Click);
         // 
         // toolStripSeparator4
         // 
         this.toolStripSeparator4.Name = "toolStripSeparator4";
         this.toolStripSeparator4.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbUp
         // 
         this.tsbUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.tsbUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbUp.Name = "tsbUp";
         this.tsbUp.Size = new System.Drawing.Size(36, 36);
         this.tsbUp.Text = "Вверх";
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         // 
         // tsbDown
         // 
         this.tsbDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.tsbDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDown.Name = "tsbDown";
         this.tsbDown.Size = new System.Drawing.Size(36, 36);
         this.tsbDown.Text = "Вниз";
         this.tsbDown.Click += new System.EventHandler(this.tsbUp_Click);
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(62, 36);
         this.toolStripLabel3.Text = "Неделя";
         // 
         // cbWeek
         // 
         this.cbWeek.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbWeek.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbWeek.Items.AddRange(new object[] {
            "Первая ",
            "Вторая",
            "Третья",
            "Четвертая"});
         this.cbWeek.Name = "cbWeek";
         this.cbWeek.Size = new System.Drawing.Size(121, 39);
         // 
         // toolStripLabel4
         // 
         this.toolStripLabel4.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel4.Name = "toolStripLabel4";
         this.toolStripLabel4.Size = new System.Drawing.Size(97, 36);
         this.toolStripLabel4.Text = "Старт цикла";
         // 
         // btnReport
         // 
         this.btnReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         this.btnReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReport.Margin = new System.Windows.Forms.Padding(165, 1, 0, 2);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(36, 36);
         this.btnReport.Text = "Маршрутный лист";
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.splitContainer1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 39);
         this.panel1.Margin = new System.Windows.Forms.Padding(0);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(1350, 641);
         this.panel1.TabIndex = 16;
         // 
         // dtpRouteStart
         // 
         this.dtpRouteStart.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpRouteStart.Location = new System.Drawing.Point(1008, 6);
         this.dtpRouteStart.Name = "dtpRouteStart";
         this.dtpRouteStart.Size = new System.Drawing.Size(160, 26);
         this.dtpRouteStart.TabIndex = 17;
         this.dtpRouteStart.ValueChanged += new System.EventHandler(this.dtpRouteStart_ValueChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Pos";
         this.dataGridViewTextBoxColumn1.FillWeight = 200F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Value.Name";
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle2;
         this.dataGridViewTextBoxColumn2.FillWeight = 600F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Column1";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         this.dataGridViewTextBoxColumn2.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.DisplayedCells;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Day";
         dataGridViewCellStyle3.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle3;
         this.dataGridViewTextBoxColumn3.FillWeight = 200F;
         this.dataGridViewTextBoxColumn3.HeaderText = "День";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn4.HeaderText = "Название";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // FmRouteAssign
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1350, 680);
         this.Controls.Add(this.dtpRouteStart);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRouteAssign";
         this.Text = "Редактирование шаблона маршрута";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripButton tsbDelete;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator4;
      private System.Windows.Forms.ToolStripButton tsbUp;
      private System.Windows.Forms.ToolStripButton tsbDown;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      private System.Windows.Forms.ToolStripComboBox cbWeek;
      private System.Windows.Forms.ToolStripLabel toolStripLabel4;
      private System.Windows.Forms.DateTimePicker dtpRouteStart;
      protected System.Windows.Forms.WebBrowser wb;
      protected System.Windows.Forms.ToolStripButton tsbShowMap;
      protected System.Windows.Forms.SplitContainer splitContainer1;
      protected System.Windows.Forms.ToolStripButton tsbAddOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ToolStripComboBox cbSelectDay;
      private System.Windows.Forms.ToolStripLabel toolStripLabel5;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      protected System.Windows.Forms.ToolStripButton btnReport;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPos;
      private System.Windows.Forms.DataGridViewTextBoxColumn olOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsDay;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvOrgW1;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvOrgW2;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvOrgW3;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvOrgW4;
   }
}
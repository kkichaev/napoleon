using GRSoft.NapoleonManager.Utils;
namespace GRSoft.NapoleonManager
{
   partial class DailyAgentPlans
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(DailyAgentPlans));
         this.dgvPlans = new System.Windows.Forms.DataGridView();
         this.dtWorkDate = new System.Windows.Forms.DateTimePicker();
         this.cbSVOnly = new System.Windows.Forms.CheckBox();
         this.tsSVAlert = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tsbDoChcangeQty = new System.Windows.Forms.ToolStripButton();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.tsFirms = new System.Windows.Forms.ToolStripComboBox();
         this.tsFolders = new System.Windows.Forms.ToolStripComboBox();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbChangePlan = new System.Windows.Forms.ToolStripButton();
         this.tsbSend = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbInfo = new System.Windows.Forms.ToolStripLabel();
         this.tsbChangeSVQty = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsDisableFirms = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tsDivisions = new GRSoft.NapoleonManager.Utils.CheckedComboBox();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnState = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).BeginInit();
         this.tsSVAlert.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvPlans
         // 
         this.dgvPlans.AllowUserToAddRows = false;
         this.dgvPlans.AllowUserToDeleteRows = false;
         this.dgvPlans.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPlans.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnState,
            this.clmnQty});
         dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleLeft;
         dataGridViewCellStyle1.BackColor = System.Drawing.SystemColors.Window;
         dataGridViewCellStyle1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         dataGridViewCellStyle1.ForeColor = System.Drawing.SystemColors.ControlText;
         dataGridViewCellStyle1.SelectionBackColor = System.Drawing.SystemColors.Highlight;
         dataGridViewCellStyle1.SelectionForeColor = System.Drawing.Color.Red;
         dataGridViewCellStyle1.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
         this.dgvPlans.DefaultCellStyle = dataGridViewCellStyle1;
         this.dgvPlans.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlans.Location = new System.Drawing.Point(0, 75);
         this.dgvPlans.Margin = new System.Windows.Forms.Padding(4);
         this.dgvPlans.MultiSelect = false;
         this.dgvPlans.Name = "dgvPlans";
         this.dgvPlans.ReadOnly = true;
         this.dgvPlans.RowHeadersVisible = false;
         this.dgvPlans.Size = new System.Drawing.Size(1645, 737);
         this.dgvPlans.TabIndex = 1;
         this.dgvPlans.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPlans_CellClick);
         this.dgvPlans.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPlans_CellDoubleClick);
         this.dgvPlans.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvPlans_CellEnter);
         this.dgvPlans.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvPlans_CellFormatting);
         // 
         // dtWorkDate
         // 
         this.dtWorkDate.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtWorkDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtWorkDate.Location = new System.Drawing.Point(162, 7);
         this.dtWorkDate.Margin = new System.Windows.Forms.Padding(4);
         this.dtWorkDate.Name = "dtWorkDate";
         this.dtWorkDate.Size = new System.Drawing.Size(170, 30);
         this.dtWorkDate.TabIndex = 3;
         this.dtWorkDate.ValueChanged += new System.EventHandler(this.dtWorkDate_ValueChanged);
         // 
         // cbSVOnly
         // 
         this.cbSVOnly.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbSVOnly.AutoSize = true;
         this.cbSVOnly.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbSVOnly.Location = new System.Drawing.Point(760, 9);
         this.cbSVOnly.Margin = new System.Windows.Forms.Padding(4);
         this.cbSVOnly.Name = "cbSVOnly";
         this.cbSVOnly.Size = new System.Drawing.Size(235, 29);
         this.cbSVOnly.TabIndex = 4;
         this.cbSVOnly.Text = "Только супервайзеры";
         this.cbSVOnly.UseVisualStyleBackColor = true;
         this.cbSVOnly.Click += new System.EventHandler(this.RefreshAgents);
         // 
         // tsSVAlert
         // 
         this.tsSVAlert.BackColor = System.Drawing.SystemColors.Info;
         this.tsSVAlert.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.tsSVAlert.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.tsbDoChcangeQty});
         this.tsSVAlert.Location = new System.Drawing.Point(0, 31);
         this.tsSVAlert.Name = "tsSVAlert";
         this.tsSVAlert.Size = new System.Drawing.Size(1645, 31);
         this.tsSVAlert.TabIndex = 5;
         this.tsSVAlert.Text = "toolStrip2";
         this.tsSVAlert.Visible = false;
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 9F, System.Drawing.FontStyle.Bold);
         this.toolStripLabel1.ForeColor = System.Drawing.Color.Red;
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(302, 28);
         this.toolStripLabel1.Text = "Необходимо перераспределить лимиты";
         // 
         // tsbDoChcangeQty
         // 
         this.tsbDoChcangeQty.Image = global::GRSoft.NapoleonManager.Properties.Resources.qty2report;
         this.tsbDoChcangeQty.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDoChcangeQty.Name = "tsbDoChcangeQty";
         this.tsbDoChcangeQty.Size = new System.Drawing.Size(209, 28);
         this.tsbDoChcangeQty.Text = "Изменить планы агентов";
         this.tsbDoChcangeQty.Click += new System.EventHandler(this.tsbDoChcangeQty_Click);
         // 
         // toolStrip2
         // 
         this.toolStrip2.ImageScalingSize = new System.Drawing.Size(36, 36);
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsFirms,
            this.tsFolders});
         this.toolStrip2.Location = new System.Drawing.Point(0, 39);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(1645, 36);
         this.toolStrip2.TabIndex = 6;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // tsFirms
         // 
         this.tsFirms.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsFirms.Name = "tsFirms";
         this.tsFirms.Size = new System.Drawing.Size(265, 36);
         this.tsFirms.ToolTipText = "Выбор фабрики";
         this.tsFirms.SelectedIndexChanged += new System.EventHandler(this.tsFirms_SelectedIndexChanged);
         // 
         // tsFolders
         // 
         this.tsFolders.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsFolders.Margin = new System.Windows.Forms.Padding(250, 0, 1, 0);
         this.tsFolders.Name = "tsFolders";
         this.tsFolders.Size = new System.Drawing.Size(345, 36);
         this.tsFolders.ToolTipText = "Выбор папки";
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(36, 36);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.ToolTipText = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsbChangePlan
         // 
         this.tsbChangePlan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbChangePlan.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.tsbChangePlan.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbChangePlan.Name = "tsbChangePlan";
         this.tsbChangePlan.Size = new System.Drawing.Size(36, 36);
         this.tsbChangePlan.Text = "Изменить план";
         this.tsbChangePlan.Click += new System.EventHandler(this.tsbChangePlan_Click);
         // 
         // tsbSend
         // 
         this.tsbSend.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbSend.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbSend.Image = global::GRSoft.NapoleonManager.Properties.Resources.abiword_3;
         this.tsbSend.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSend.Name = "tsbSend";
         this.tsbSend.Size = new System.Drawing.Size(146, 36);
         this.tsbSend.Text = "Отправить";
         this.tsbSend.Visible = false;
         this.tsbSend.Click += new System.EventHandler(this.tsbSend_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Margin = new System.Windows.Forms.Padding(2, 1, 0, 2);
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(36, 36);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // tsbInfo
         // 
         this.tsbInfo.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbInfo.Margin = new System.Windows.Forms.Padding(190, 1, 0, 2);
         this.tsbInfo.Name = "tsbInfo";
         this.tsbInfo.Size = new System.Drawing.Size(73, 36);
         this.tsbInfo.Text = "tsbInfo";
         this.tsbInfo.Visible = false;
         // 
         // tsbChangeSVQty
         // 
         this.tsbChangeSVQty.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbChangeSVQty.Enabled = false;
         this.tsbChangeSVQty.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbChangeSVQty.Image = global::GRSoft.NapoleonManager.Properties.Resources.pnt_doc;
         this.tsbChangeSVQty.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbChangeSVQty.Name = "tsbChangeSVQty";
         this.tsbChangeSVQty.Size = new System.Drawing.Size(161, 36);
         this.tsbChangeSVQty.Text = "Передать SV";
         this.tsbChangeSVQty.Visible = false;
         this.tsbChangeSVQty.Click += new System.EventHandler(this.tsbChangeSVQty_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbRefresh,
            this.tsbChangePlan,
            this.tsbSend,
            this.tsbSave,
            this.tsbInfo,
            this.tsbChangeSVQty,
            this.tsDisableFirms});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1645, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsDisableFirms
         // 
         this.tsDisableFirms.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsDisableFirms.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsDisableFirms.Image = global::GRSoft.NapoleonManager.Properties.Resources.stop;
         this.tsDisableFirms.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsDisableFirms.Name = "tsDisableFirms";
         this.tsDisableFirms.Size = new System.Drawing.Size(335, 36);
         this.tsDisableFirms.Text = "Запрет редактирования заявок";
         this.tsDisableFirms.Click += new System.EventHandler(this.tsDisableFirms_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Width = 200;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "State";
         this.dataGridViewTextBoxColumn2.HeaderText = "Состояние";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "TotalPlan";
         this.dataGridViewTextBoxColumn3.HeaderText = "План";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         this.dataGridViewTextBoxColumn3.Width = 60;
         // 
         // tsDivisions
         // 
         this.tsDivisions.CheckOnClick = true;
         this.tsDivisions.DrawMode = System.Windows.Forms.DrawMode.OwnerDrawVariable;
         this.tsDivisions.DropDownHeight = 1;
         this.tsDivisions.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.tsDivisions.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsDivisions.IntegralHeight = false;
         this.tsDivisions.Location = new System.Drawing.Point(298, 48);
         this.tsDivisions.Margin = new System.Windows.Forms.Padding(4);
         this.tsDivisions.Name = "tsDivisions";
         this.tsDivisions.Size = new System.Drawing.Size(293, 31);
         this.tsDivisions.TabIndex = 0;
         this.tsDivisions.ValueSeparator = ", ";
         // 
         // clmnName
         // 
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Наименование";
         this.clmnName.Name = "clmnName";
         this.clmnName.ReadOnly = true;
         this.clmnName.Width = 200;
         // 
         // clmnState
         // 
         this.clmnState.DataPropertyName = "State";
         this.clmnState.HeaderText = "Состояние";
         this.clmnState.Name = "clmnState";
         this.clmnState.ReadOnly = true;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "TotalPlan";
         this.clmnQty.HeaderText = "План";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.ReadOnly = true;
         this.clmnQty.Width = 60;
         // 
         // DailyAgentPlans
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1645, 812);
         this.Controls.Add(this.tsDivisions);
         this.Controls.Add(this.cbSVOnly);
         this.Controls.Add(this.dtWorkDate);
         this.Controls.Add(this.dgvPlans);
         this.Controls.Add(this.toolStrip2);
         this.Controls.Add(this.tsSVAlert);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "DailyAgentPlans";
         this.Text = "Центр управления продажами";
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.DailyAgentPlans_KeyDown);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlans)).EndInit();
         this.tsSVAlert.ResumeLayout(false);
         this.tsSVAlert.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvPlans;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnState;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
      private System.Windows.Forms.DateTimePicker dtWorkDate;
      private System.Windows.Forms.CheckBox cbSVOnly;
      private System.Windows.Forms.ToolStrip tsSVAlert;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripButton tsbDoChcangeQty;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripComboBox tsFirms;
      private CheckedComboBox tsDivisions;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.ToolStripButton tsbChangePlan;
      private System.Windows.Forms.ToolStripButton tsbSend;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.ToolStripLabel tsbInfo;
      private System.Windows.Forms.ToolStripButton tsbChangeSVQty;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox tsFolders;
      private System.Windows.Forms.ToolStripButton tsDisableFirms;
   }
}
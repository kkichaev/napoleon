using GRSoft.UILib;
namespace GRSoft.NapoleonManager
{
   partial class FmDailyRouteEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDailyRouteEditor));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.AgentName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.OrgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvRoute = new GRSoft.UILib.TreeGridView();
         this.tnAgent = new GRSoft.UILib.TreeGridColumn();
         this.cmsRote = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.tsmDel = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.tsRefresh = new System.Windows.Forms.ToolStripButton();
         this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
         this.dtpBeginDate = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tsbMakeHtml = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRoute)).BeginInit();
         this.cmsRote.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvRoute);
         this.splitContainer1.Size = new System.Drawing.Size(677, 429);
         this.splitContainer1.SplitterDistance = 306;
         this.splitContainer1.TabIndex = 0;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvAgents);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.dgvOrgs);
         this.splitContainer2.Size = new System.Drawing.Size(306, 429);
         this.splitContainer2.SplitterDistance = 240;
         this.splitContainer2.TabIndex = 0;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.AgentName});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 0);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.ReadOnly = true;
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(306, 240);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvAgents_RowEnter);
         // 
         // AgentName
         // 
         this.AgentName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.AgentName.DataPropertyName = "Name";
         this.AgentName.HeaderText = "Агент";
         this.AgentName.Name = "AgentName";
         this.AgentName.ReadOnly = true;
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.OrgName});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.ReadOnly = true;
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(306, 185);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseDown);
         this.dgvOrgs.MouseMove += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseMove);
         // 
         // OrgName
         // 
         this.OrgName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.OrgName.DataPropertyName = "Name";
         this.OrgName.HeaderText = "Контрагенты";
         this.OrgName.Name = "OrgName";
         this.OrgName.ReadOnly = true;
         // 
         // dgvRoute
         // 
         this.dgvRoute.AllowDrop = true;
         this.dgvRoute.AllowUserToAddRows = false;
         this.dgvRoute.AllowUserToDeleteRows = false;
         this.dgvRoute.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvRoute.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.tnAgent});
         this.dgvRoute.ContextMenuStrip = this.cmsRote;
         this.dgvRoute.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvRoute.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.dgvRoute.ImageList = null;
         this.dgvRoute.Location = new System.Drawing.Point(0, 0);
         this.dgvRoute.Name = "dgvRoute";
         this.dgvRoute.ReadOnly = true;
         this.dgvRoute.RowHeadersVisible = false;
         this.dgvRoute.Size = new System.Drawing.Size(367, 429);
         this.dgvRoute.TabIndex = 0;
         this.dgvRoute.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvRoute_MouseDown);
         this.dgvRoute.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvRoute_CellFormatting);
         this.dgvRoute.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvRoute_DragEnter);
         this.dgvRoute.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvRoute_DragDrop);
         // 
         // tnAgent
         // 
         this.tnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tnAgent.DefaultNodeImage = null;
         this.tnAgent.HeaderText = "Маршрут";
         this.tnAgent.Name = "tnAgent";
         this.tnAgent.ReadOnly = true;
         this.tnAgent.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // cmsRote
         // 
         this.cmsRote.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmDel});
         this.cmsRote.Name = "cmsRote";
         this.cmsRote.Size = new System.Drawing.Size(119, 26);
         // 
         // tsmDel
         // 
         this.tsmDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsmDel.Name = "tsmDel";
         this.tsmDel.Size = new System.Drawing.Size(118, 22);
         this.tsmDel.Text = "Удалить";
         this.tsmDel.Click += new System.EventHandler(this.tsmDel_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsSave,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.tsRefresh,
            this.tsbMakeHtml});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(677, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsSave
         // 
         this.tsSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsSave.Enabled = false;
         this.tsSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsSave.Name = "tsSave";
         this.tsSave.Size = new System.Drawing.Size(23, 22);
         this.tsSave.Text = "Сохранить";
         this.tsSave.Click += new System.EventHandler(this.tsSave_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // tsRefresh
         // 
         this.tsRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsRefresh.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.tsRefresh.Name = "tsRefresh";
         this.tsRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsRefresh.Text = "Обновить";
         this.tsRefresh.Click += new System.EventHandler(this.tsRefresh_Click);
         // 
         // dtpEndDate
         // 
         this.dtpEndDate.Location = new System.Drawing.Point(218, 2);
         this.dtpEndDate.Name = "dtpEndDate";
         this.dtpEndDate.Size = new System.Drawing.Size(144, 20);
         this.dtpEndDate.TabIndex = 7;
         this.dtpEndDate.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // dtpBeginDate
         // 
         this.dtpBeginDate.Location = new System.Drawing.Point(48, 2);
         this.dtpBeginDate.Name = "dtpBeginDate";
         this.dtpBeginDate.Size = new System.Drawing.Size(144, 20);
         this.dtpBeginDate.TabIndex = 6;
         this.dtpBeginDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Контрагенты";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // tsbMakeHtml
         // 
         this.tsbMakeHtml.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMakeHtml.Image = ((System.Drawing.Image)(resources.GetObject("tsbMakeHtml.Image")));
         this.tsbMakeHtml.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMakeHtml.Name = "tsbMakeHtml";
         this.tsbMakeHtml.Size = new System.Drawing.Size(23, 22);
         this.tsbMakeHtml.Text = "Составить отчет";
         this.tsbMakeHtml.Click += new System.EventHandler(this.tsbMakeHtml_Click);
         // 
         // FmDailyRouteEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(677, 454);
         this.Controls.Add(this.dtpEndDate);
         this.Controls.Add(this.dtpBeginDate);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDailyRouteEditor";
         this.Text = "Дополнительный маршрут";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRoute)).EndInit();
         this.cmsRote.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private TreeGridView dgvRoute;
      private System.Windows.Forms.DataGridViewTextBoxColumn AgentName;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DateTimePicker dtpEndDate;
      private System.Windows.Forms.DateTimePicker dtpBeginDate;
      private System.Windows.Forms.ToolStripButton tsSave;
      private System.Windows.Forms.ToolStripButton tsRefresh;
      private System.Windows.Forms.ContextMenuStrip cmsRote;
      private System.Windows.Forms.ToolStripMenuItem tsmDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private TreeGridColumn tnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn OrgName;
      public System.Windows.Forms.ToolStripButton tsbMakeHtml;
   }
}
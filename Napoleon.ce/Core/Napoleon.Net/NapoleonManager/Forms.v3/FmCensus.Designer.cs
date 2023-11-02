namespace GRSoft.NapoleonManager
{
   partial class FmCensus
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCensus));
         this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
         this.dtpBeginDate = new System.Windows.Forms.DateTimePicker();
         this.splitContainer3 = new System.Windows.Forms.SplitContainer();
         this.splitContainer4 = new System.Windows.Forms.SplitContainer();
         this.splitContainer5 = new System.Windows.Forms.SplitContainer();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnDelOrg = new System.Windows.Forms.ToolStripButton();
         this.tbAddOrg = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.tbFindOrg = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindOrgDown = new System.Windows.Forms.ToolStripButton();
         this.btnFindOrgUp = new System.Windows.Forms.ToolStripButton();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddTask = new System.Windows.Forms.ToolStripButton();
         this.orgAddress = new System.Windows.Forms.LinkLabel();
         this.label2 = new System.Windows.Forms.Label();
         this.orgName = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrgCount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnTaskCount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDoneCount = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrgTask = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrgDone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvTask = new GRSoft.UILib.TreeGridView();
         this.clmnTask = new GRSoft.UILib.TreeGridColumn();
         this.clmnTaskTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tsbSelectRange = new System.Windows.Forms.ToolStripSplitButton();
         this.tsmiToday = new System.Windows.Forms.ToolStripMenuItem();
         this.tsmiRange = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbConfig = new System.Windows.Forms.ToolStrip();
         this.splitContainer3.Panel1.SuspendLayout();
         this.splitContainer3.Panel2.SuspendLayout();
         this.splitContainer3.SuspendLayout();
         this.splitContainer4.Panel1.SuspendLayout();
         this.splitContainer4.Panel2.SuspendLayout();
         this.splitContainer4.SuspendLayout();
         this.splitContainer5.Panel1.SuspendLayout();
         this.splitContainer5.Panel2.SuspendLayout();
         this.splitContainer5.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).BeginInit();
         this.tsbConfig.SuspendLayout();
         this.SuspendLayout();
         // 
         // dtpEndDate
         // 
         this.dtpEndDate.Location = new System.Drawing.Point(230, 2);
         this.dtpEndDate.Name = "dtpEndDate";
         this.dtpEndDate.Size = new System.Drawing.Size(144, 20);
         this.dtpEndDate.TabIndex = 12;
         this.dtpEndDate.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // dtpBeginDate
         // 
         this.dtpBeginDate.Location = new System.Drawing.Point(58, 3);
         this.dtpBeginDate.Name = "dtpBeginDate";
         this.dtpBeginDate.Size = new System.Drawing.Size(144, 20);
         this.dtpBeginDate.TabIndex = 11;
         this.dtpBeginDate.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // splitContainer3
         // 
         this.splitContainer3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer3.Location = new System.Drawing.Point(0, 0);
         this.splitContainer3.Name = "splitContainer3";
         this.splitContainer3.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer3.Panel1
         // 
         this.splitContainer3.Panel1.Controls.Add(this.splitContainer4);
         // 
         // splitContainer3.Panel2
         // 
         this.splitContainer3.Panel2.Controls.Add(this.orgAddress);
         this.splitContainer3.Panel2.Controls.Add(this.label2);
         this.splitContainer3.Panel2.Controls.Add(this.orgName);
         this.splitContainer3.Panel2.Controls.Add(this.label1);
         this.splitContainer3.Size = new System.Drawing.Size(856, 449);
         this.splitContainer3.SplitterDistance = 400;
         this.splitContainer3.TabIndex = 13;
         // 
         // splitContainer4
         // 
         this.splitContainer4.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer4.Location = new System.Drawing.Point(0, 0);
         this.splitContainer4.Name = "splitContainer4";
         // 
         // splitContainer4.Panel1
         // 
         this.splitContainer4.Panel1.Controls.Add(this.splitContainer5);
         // 
         // splitContainer4.Panel2
         // 
         this.splitContainer4.Panel2.Controls.Add(this.dgvTask);
         this.splitContainer4.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer4.Size = new System.Drawing.Size(856, 400);
         this.splitContainer4.SplitterDistance = 464;
         this.splitContainer4.TabIndex = 0;
         // 
         // splitContainer5
         // 
         this.splitContainer5.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer5.Location = new System.Drawing.Point(0, 0);
         this.splitContainer5.Name = "splitContainer5";
         this.splitContainer5.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer5.Panel1
         // 
         this.splitContainer5.Panel1.Controls.Add(this.dgvAgents);
         this.splitContainer5.Panel1.Controls.Add(this.tsbConfig);
         // 
         // splitContainer5.Panel2
         // 
         this.splitContainer5.Panel2.Controls.Add(this.dgvOrgs);
         this.splitContainer5.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer5.Size = new System.Drawing.Size(464, 400);
         this.splitContainer5.SplitterDistance = 205;
         this.splitContainer5.TabIndex = 0;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowDrop = true;
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.AllowUserToResizeRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnOrgCount,
            this.clmnTaskCount,
            this.clmnDoneCount});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(0, 25);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvAgents.Size = new System.Drawing.Size(464, 180);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvAgents_RowEnter);
         this.dgvAgents.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvAgents_DragDrop);
         this.dgvAgents.DragOver += new System.Windows.Forms.DragEventHandler(this.dgvAgents_DragOver);
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.AllowUserToResizeRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg,
            this.clmnOrgTask,
            this.clmnOrgDone});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 25);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvOrgs.Size = new System.Drawing.Size(464, 166);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvOrgs_ColumnHeaderMouseClick);
         this.dgvOrgs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_RowEnter);
         this.dgvOrgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseDown);
         this.dgvOrgs.MouseMove += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseMove);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnDelOrg,
            this.tbAddOrg,
            this.btnEdit,
            this.tbFindOrg,
            this.btnFindOrgDown,
            this.btnFindOrgUp});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(464, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnDelOrg
         // 
         this.btnDelOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.btnDelOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelOrg.Name = "btnDelOrg";
         this.btnDelOrg.Size = new System.Drawing.Size(23, 22);
         this.btnDelOrg.Text = "Удалить организацию";
         this.btnDelOrg.Click += new System.EventHandler(this.btnDelOrg_Click);
         // 
         // tbAddOrg
         // 
         this.tbAddOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbAddOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tbAddOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbAddOrg.Name = "tbAddOrg";
         this.tbAddOrg.Size = new System.Drawing.Size(23, 22);
         this.tbAddOrg.Text = "Добавить точку";
         this.tbAddOrg.Click += new System.EventHandler(this.tbAddOrg_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // tbFindOrg
         // 
         this.tbFindOrg.Name = "tbFindOrg";
         this.tbFindOrg.Size = new System.Drawing.Size(100, 25);
         this.tbFindOrg.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFindOrg_KeyDown);
         // 
         // btnFindOrgDown
         // 
         this.btnFindOrgDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindOrgDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.btnFindOrgDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindOrgDown.Name = "btnFindOrgDown";
         this.btnFindOrgDown.Size = new System.Drawing.Size(23, 22);
         this.btnFindOrgDown.Text = "Искать вперед";
         this.btnFindOrgDown.Click += new System.EventHandler(this.btnFindOrgDown_Click);
         // 
         // btnFindOrgUp
         // 
         this.btnFindOrgUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindOrgUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.btnFindOrgUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindOrgUp.Name = "btnFindOrgUp";
         this.btnFindOrgUp.Size = new System.Drawing.Size(23, 22);
         this.btnFindOrgUp.Text = "Искать назад";
         this.btnFindOrgUp.Click += new System.EventHandler(this.btnFindOrgUp_Click);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddTask});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(388, 25);
         this.toolStrip2.TabIndex = 2;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddTask
         // 
         this.btnAddTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddTask.Image = ((System.Drawing.Image)(resources.GetObject("btnAddTask.Image")));
         this.btnAddTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddTask.Name = "btnAddTask";
         this.btnAddTask.Size = new System.Drawing.Size(23, 22);
         this.btnAddTask.Text = "Добавить задачу";
         this.btnAddTask.Click += new System.EventHandler(this.btnAddTask_Click);
         // 
         // orgAddress
         // 
         this.orgAddress.AutoSize = true;
         this.orgAddress.Location = new System.Drawing.Point(78, 25);
         this.orgAddress.Name = "orgAddress";
         this.orgAddress.Size = new System.Drawing.Size(0, 13);
         this.orgAddress.TabIndex = 3;
         this.orgAddress.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.orgAddress_LinkClicked);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(34, 25);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(38, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Адрес";
         // 
         // orgName
         // 
         this.orgName.AutoSize = true;
         this.orgName.Location = new System.Drawing.Point(78, 7);
         this.orgName.Name = "orgName";
         this.orgName.Size = new System.Drawing.Size(0, 13);
         this.orgName.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(15, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(57, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Название";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "AgentName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "OrgCount";
         this.dataGridViewTextBoxColumn2.HeaderText = "Точек";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.Width = 50;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "TaskCount";
         this.dataGridViewTextBoxColumn3.HeaderText = "Задач";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.Width = 50;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "DoneCount";
         this.dataGridViewTextBoxColumn4.HeaderText = "Выполнено";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.Width = 70;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Org";
         this.dataGridViewTextBoxColumn5.HeaderText = "Точки";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.DataPropertyName = "TaskCount";
         this.dataGridViewTextBoxColumn6.HeaderText = "Задач";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.DataPropertyName = "DoneCount";
         this.dataGridViewTextBoxColumn7.HeaderText = "Выполнено";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "AgentName";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         // 
         // clmnOrgCount
         // 
         this.clmnOrgCount.DataPropertyName = "OrgCount";
         this.clmnOrgCount.HeaderText = "Точек";
         this.clmnOrgCount.Name = "clmnOrgCount";
         this.clmnOrgCount.Width = 50;
         // 
         // clmnTaskCount
         // 
         this.clmnTaskCount.DataPropertyName = "TaskCount";
         this.clmnTaskCount.HeaderText = "Задач";
         this.clmnTaskCount.Name = "clmnTaskCount";
         this.clmnTaskCount.Width = 50;
         // 
         // clmnDoneCount
         // 
         this.clmnDoneCount.DataPropertyName = "DoneCount";
         this.clmnDoneCount.HeaderText = "Выполнено";
         this.clmnDoneCount.Name = "clmnDoneCount";
         this.clmnDoneCount.Width = 70;
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "OrgName";
         this.clmnOrg.HeaderText = "Точки";
         this.clmnOrg.Name = "clmnOrg";
         // 
         // clmnOrgTask
         // 
         this.clmnOrgTask.DataPropertyName = "TaskCount";
         this.clmnOrgTask.HeaderText = "Задач";
         this.clmnOrgTask.Name = "clmnOrgTask";
         // 
         // clmnOrgDone
         // 
         this.clmnOrgDone.DataPropertyName = "DoneCount";
         this.clmnOrgDone.HeaderText = "Выполнено";
         this.clmnOrgDone.Name = "clmnOrgDone";
         // 
         // dgvTask
         // 
         this.dgvTask.AllowUserToAddRows = false;
         this.dgvTask.AllowUserToDeleteRows = false;
         this.dgvTask.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvTask.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnTask,
            this.clmnTaskTime});
         this.dgvTask.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvTask.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.dgvTask.ImageList = null;
         this.dgvTask.Location = new System.Drawing.Point(0, 25);
         this.dgvTask.Name = "dgvTask";
         this.dgvTask.RowHeadersVisible = false;
         this.dgvTask.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvTask.Size = new System.Drawing.Size(388, 375);
         this.dgvTask.TabIndex = 0;
         this.dgvTask.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvTask_CellFormatting);
         // 
         // clmnTask
         // 
         this.clmnTask.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnTask.DataPropertyName = "Task";
         this.clmnTask.DefaultNodeImage = null;
         this.clmnTask.HeaderText = "Задачи";
         this.clmnTask.Name = "clmnTask";
         this.clmnTask.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnTaskTime
         // 
         this.clmnTaskTime.HeaderText = "Дата";
         this.clmnTaskTime.Name = "clmnTaskTime";
         this.clmnTaskTime.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tsbSelectRange
         // 
         this.tsbSelectRange.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSelectRange.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmiToday,
            this.tsmiRange});
         this.tsbSelectRange.Image = ((System.Drawing.Image)(resources.GetObject("tsbSelectRange.Image")));
         this.tsbSelectRange.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSelectRange.Name = "tsbSelectRange";
         this.tsbSelectRange.Size = new System.Drawing.Size(32, 22);
         this.tsbSelectRange.Text = "toolStripSplitButton1";
         this.tsbSelectRange.ToolTipText = "За сегодня";
         this.tsbSelectRange.ButtonClick += new System.EventHandler(this.tsbSelectRange_Click);
         // 
         // tsmiToday
         // 
         this.tsmiToday.Checked = true;
         this.tsmiToday.CheckState = System.Windows.Forms.CheckState.Checked;
         this.tsmiToday.Image = ((System.Drawing.Image)(resources.GetObject("tsmiToday.Image")));
         this.tsmiToday.Name = "tsmiToday";
         this.tsmiToday.Size = new System.Drawing.Size(152, 22);
         this.tsmiToday.Text = "За сегодня";
         this.tsmiToday.Click += new System.EventHandler(this.tsmiToday_Click);
         // 
         // tsmiRange
         // 
         this.tsmiRange.Image = ((System.Drawing.Image)(resources.GetObject("tsmiRange.Image")));
         this.tsmiRange.Name = "tsmiRange";
         this.tsmiRange.Size = new System.Drawing.Size(152, 22);
         this.tsmiRange.Text = "За интервал";
         this.tsmiRange.Click += new System.EventHandler(this.tsmiRange_Click);
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
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(160, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // tsbConfig
         // 
         this.tsbConfig.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbSelectRange,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnRefresh});
         this.tsbConfig.Location = new System.Drawing.Point(0, 0);
         this.tsbConfig.Name = "tsbConfig";
         this.tsbConfig.Size = new System.Drawing.Size(464, 25);
         this.tsbConfig.TabIndex = 15;
         // 
         // FmCensus
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(856, 449);
         this.Controls.Add(this.dtpBeginDate);
         this.Controls.Add(this.dtpEndDate);
         this.Controls.Add(this.splitContainer3);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmCensus";
         this.Text = "Census";
         this.splitContainer3.Panel1.ResumeLayout(false);
         this.splitContainer3.Panel2.ResumeLayout(false);
         this.splitContainer3.Panel2.PerformLayout();
         this.splitContainer3.ResumeLayout(false);
         this.splitContainer4.Panel1.ResumeLayout(false);
         this.splitContainer4.Panel2.ResumeLayout(false);
         this.splitContainer4.Panel2.PerformLayout();
         this.splitContainer4.ResumeLayout(false);
         this.splitContainer5.Panel1.ResumeLayout(false);
         this.splitContainer5.Panel1.PerformLayout();
         this.splitContainer5.Panel2.ResumeLayout(false);
         this.splitContainer5.Panel2.PerformLayout();
         this.splitContainer5.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).EndInit();
         this.tsbConfig.ResumeLayout(false);
         this.tsbConfig.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      protected GRSoft.UILib.TreeGridColumn clmnTask;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnTaskTime;
      private System.Windows.Forms.SplitContainer splitContainer5;
      protected GRSoft.UILib.TreeGridView dgvTask;
      protected System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnOrgCount;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnTaskCount;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnDoneCount;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnOrgTask;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnOrgDone;
      protected System.Windows.Forms.LinkLabel orgAddress;
      protected System.Windows.Forms.Label orgName;
      protected System.Windows.Forms.ToolStripButton tbAddOrg;
      public System.Windows.Forms.SplitContainer splitContainer4;
      protected System.Windows.Forms.DataGridView dgvOrgs;
      protected System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
      protected System.Windows.Forms.DateTimePicker dtpEndDate;
      protected System.Windows.Forms.DateTimePicker dtpBeginDate;
      private System.Windows.Forms.ToolStripTextBox tbFindOrg;
      private System.Windows.Forms.ToolStripButton btnFindOrgDown;
      private System.Windows.Forms.ToolStripButton btnFindOrgUp;
      protected System.Windows.Forms.ToolStripButton btnAddTask;
      protected System.Windows.Forms.SplitContainer splitContainer3;
      protected System.Windows.Forms.Label label2;
      protected System.Windows.Forms.Label label1;
      protected System.Windows.Forms.ToolStripButton btnDelOrg;
      public System.Windows.Forms.ToolStrip tsbConfig;
      protected System.Windows.Forms.ToolStripSplitButton tsbSelectRange;
      protected System.Windows.Forms.ToolStripMenuItem tsmiToday;
      protected System.Windows.Forms.ToolStripMenuItem tsmiRange;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel1;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel2;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
   }
}
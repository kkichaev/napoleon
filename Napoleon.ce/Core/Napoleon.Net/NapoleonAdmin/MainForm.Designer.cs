namespace GRSoft.NapoleonAdmin
{
   partial class MainForm
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
         this.tabControl1 = new System.Windows.Forms.TabControl();
         this.users = new System.Windows.Forms.TabPage();
         this.usersView = new System.Windows.Forms.DataGridView();
         this.clmnId = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.user = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.login = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.password = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.ProgID = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.activity = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.progVersion = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCheckPwd = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.registred = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.tracking = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.userUpdate = new System.Windows.Forms.ToolStripButton();
         this.userChangesSave = new System.Windows.Forms.ToolStripButton();
         this.cbUserType = new System.Windows.Forms.ToolStripComboBox();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tbPresentFolder = new System.Windows.Forms.ToolStripTextBox();
         this.btnFolder = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.tsFind = new System.Windows.Forms.ToolStripTextBox();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.licenseStatusText = new System.Windows.Forms.ToolStripStatusLabel();
         this.rmvScheduler = new System.Windows.Forms.TabPage();
         this.rmvScheduler1 = new GRSoft.NapoleonAdmin.RmvScheduler();
         this.userActivity = new System.Windows.Forms.TabPage();
         this.dgvActivity = new System.Windows.Forms.DataGridView();
         this.clmnExclMgr = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnManagerActivity = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmManagerIP = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnManagerDuration = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.statusStrip2 = new System.Windows.Forms.StatusStrip();
         this.userActivityTotals = new System.Windows.Forms.ToolStripStatusLabel();
         this.toolStrip4 = new System.Windows.Forms.ToolStrip();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.update = new System.Windows.Forms.TabPage();
         this.label4 = new System.Windows.Forms.Label();
         this.sendUpdate = new System.Windows.Forms.Button();
         this.browseFile = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.uploadFileName = new System.Windows.Forms.TextBox();
         this.settings = new System.Windows.Forms.TabPage();
         this.label12 = new System.Windows.Forms.Label();
         this.lbHistory = new System.Windows.Forms.ListBox();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miDel = new System.Windows.Forms.ToolStripMenuItem();
         this.label11 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.test = new System.Windows.Forms.Button();
         this.savePwd = new System.Windows.Forms.CheckBox();
         this.label7 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.log = new System.Windows.Forms.TextBox();
         this.pwd = new System.Windows.Forms.TextBox();
         this.save = new System.Windows.Forms.Button();
         this.port = new System.Windows.Forms.TextBox();
         this.ip = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.gps = new System.Windows.Forms.TabPage();
         this.btnSaveGPSSetting = new System.Windows.Forms.Button();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.btnSPGSetAllDays = new System.Windows.Forms.Button();
         this.btnUnsetGpsSetting = new System.Windows.Forms.Button();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label10 = new System.Windows.Forms.Label();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.label9 = new System.Windows.Forms.Label();
         this.cbD6 = new System.Windows.Forms.CheckBox();
         this.cbD5 = new System.Windows.Forms.CheckBox();
         this.cbD4 = new System.Windows.Forms.CheckBox();
         this.cbD7 = new System.Windows.Forms.CheckBox();
         this.cbD3 = new System.Windows.Forms.CheckBox();
         this.cbD2 = new System.Windows.Forms.CheckBox();
         this.cbD1 = new System.Windows.Forms.CheckBox();
         this.syncinfo = new System.Windows.Forms.TabPage();
         this.dtpDateSyncInfo = new System.Windows.Forms.DateTimePicker();
         this.dgvSyncInfo = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnRefreshSyncInfo = new System.Windows.Forms.ToolStripButton();
         this.cbAgentSyncInfo = new System.Windows.Forms.ToolStripComboBox();
         this.version = new System.Windows.Forms.Label();
         this.label8 = new System.Windows.Forms.Label();
         this.folderBrowserDialog1 = new System.Windows.Forms.FolderBrowserDialog();
         this.tabControl1.SuspendLayout();
         this.users.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.usersView)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         this.rmvScheduler.SuspendLayout();
         this.userActivity.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvActivity)).BeginInit();
         this.statusStrip2.SuspendLayout();
         this.toolStrip4.SuspendLayout();
         this.update.SuspendLayout();
         this.settings.SuspendLayout();
         this.contextMenuStrip1.SuspendLayout();
         this.gps.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.syncinfo.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSyncInfo)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.SuspendLayout();
         // 
         // tabControl1
         // 
         this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tabControl1.Controls.Add(this.users);
         this.tabControl1.Controls.Add(this.rmvScheduler);
         this.tabControl1.Controls.Add(this.userActivity);
         this.tabControl1.Controls.Add(this.update);
         this.tabControl1.Controls.Add(this.settings);
         this.tabControl1.Controls.Add(this.gps);
         this.tabControl1.Controls.Add(this.syncinfo);
         this.tabControl1.Location = new System.Drawing.Point(0, 40);
         this.tabControl1.Multiline = true;
         this.tabControl1.Name = "tabControl1";
         this.tabControl1.SelectedIndex = 0;
         this.tabControl1.Size = new System.Drawing.Size(1246, 529);
         this.tabControl1.TabIndex = 2;
         this.tabControl1.Selecting += new System.Windows.Forms.TabControlCancelEventHandler(this.tabControl1_Selecting);
         // 
         // users
         // 
         this.users.Controls.Add(this.usersView);
         this.users.Controls.Add(this.toolStrip1);
         this.users.Controls.Add(this.statusStrip1);
         this.users.Location = new System.Drawing.Point(4, 22);
         this.users.Name = "users";
         this.users.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.users.Size = new System.Drawing.Size(1238, 503);
         this.users.TabIndex = 1;
         this.users.Text = "Пользователи";
         this.users.UseVisualStyleBackColor = true;
         // 
         // usersView
         // 
         this.usersView.AllowUserToAddRows = false;
         this.usersView.AllowUserToDeleteRows = false;
         this.usersView.AllowUserToOrderColumns = true;
         this.usersView.AllowUserToResizeRows = false;
         this.usersView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.usersView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnId,
            this.user,
            this.login,
            this.password,
            this.ProgID,
            this.activity,
            this.progVersion,
            this.clmnCheckPwd,
            this.registred,
            this.tracking});
         this.usersView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.usersView.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.usersView.Location = new System.Drawing.Point(3, 30);
         this.usersView.MultiSelect = false;
         this.usersView.Name = "usersView";
         this.usersView.RowHeadersVisible = false;
         this.usersView.RowHeadersWidth = 51;
         this.usersView.Size = new System.Drawing.Size(1232, 448);
         this.usersView.TabIndex = 0;
         this.usersView.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.usersView_CellEnter);
         this.usersView.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.usersView_CellFormatting);
         this.usersView.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.usersView_ColumnHeaderMouseClick);
         this.usersView.CurrentCellDirtyStateChanged += new System.EventHandler(this.usersView_CurrentCellDirtyStateChanged);
         this.usersView.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.usersView_DataError);
         // 
         // clmnId
         // 
         this.clmnId.DataPropertyName = "Id";
         this.clmnId.FillWeight = 35.94316F;
         this.clmnId.HeaderText = "ID";
         this.clmnId.MinimumWidth = 6;
         this.clmnId.Name = "clmnId";
         this.clmnId.ReadOnly = true;
         this.clmnId.Width = 125;
         // 
         // user
         // 
         this.user.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.user.DataPropertyName = "Name";
         this.user.FillWeight = 71.88631F;
         this.user.HeaderText = "Пользователь";
         this.user.MinimumWidth = 6;
         this.user.Name = "user";
         this.user.ReadOnly = true;
         this.user.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // login
         // 
         this.login.DataPropertyName = "Login";
         this.login.FillWeight = 35.94316F;
         this.login.HeaderText = "Логин";
         this.login.MinimumWidth = 6;
         this.login.Name = "login";
         this.login.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.login.Width = 150;
         // 
         // password
         // 
         this.password.DataPropertyName = "Passw";
         this.password.FillWeight = 35.94316F;
         this.password.HeaderText = "Пароль";
         this.password.MinimumWidth = 6;
         this.password.Name = "password";
         this.password.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.password.Width = 150;
         // 
         // ProgID
         // 
         this.ProgID.DataPropertyName = "ProgID";
         this.ProgID.HeaderText = "IMEI";
         this.ProgID.MinimumWidth = 6;
         this.ProgID.Name = "ProgID";
         this.ProgID.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.ProgID.Visible = false;
         this.ProgID.Width = 125;
         // 
         // activity
         // 
         this.activity.DataPropertyName = "LastAccess";
         this.activity.FillWeight = 50.32042F;
         this.activity.HeaderText = "Посл.доступ";
         this.activity.MinimumWidth = 6;
         this.activity.Name = "activity";
         this.activity.ReadOnly = true;
         this.activity.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.activity.Width = 150;
         // 
         // progVersion
         // 
         this.progVersion.DataPropertyName = "Version";
         this.progVersion.FillWeight = 35.94316F;
         this.progVersion.HeaderText = "Версия";
         this.progVersion.MinimumWidth = 6;
         this.progVersion.Name = "progVersion";
         this.progVersion.ReadOnly = true;
         this.progVersion.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.progVersion.Width = 150;
         // 
         // clmnCheckPwd
         // 
         this.clmnCheckPwd.DataPropertyName = "DisablePwdChg";
         this.clmnCheckPwd.HeaderText = "Запрет ред.пароля";
         this.clmnCheckPwd.MinimumWidth = 6;
         this.clmnCheckPwd.Name = "clmnCheckPwd";
         this.clmnCheckPwd.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.clmnCheckPwd.Visible = false;
         this.clmnCheckPwd.Width = 60;
         // 
         // registred
         // 
         this.registred.DataPropertyName = "License";
         this.registred.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.registred.FillWeight = 594.0206F;
         this.registred.HeaderText = "Лицензия";
         this.registred.Items.AddRange(new object[] {
            "нет",
            "Pre-Selling",
            "Van-Selling"});
         this.registred.MinimumWidth = 6;
         this.registred.Name = "registred";
         this.registred.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.registred.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.registred.Width = 250;
         // 
         // tracking
         // 
         this.tracking.DataPropertyName = "Tracking";
         this.tracking.HeaderText = "Слежение";
         this.tracking.MinimumWidth = 6;
         this.tracking.Name = "tracking";
         this.tracking.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.tracking.Width = 60;
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.userUpdate,
            this.userChangesSave,
            this.cbUserType,
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.toolStripLabel1,
            this.tbPresentFolder,
            this.btnFolder,
            this.toolStripLabel2,
            this.tsFind});
         this.toolStrip1.Location = new System.Drawing.Point(3, 3);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1232, 27);
         this.toolStrip1.TabIndex = 1;
         // 
         // userUpdate
         // 
         this.userUpdate.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.userUpdate.Image = ((System.Drawing.Image)(resources.GetObject("userUpdate.Image")));
         this.userUpdate.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.userUpdate.Name = "userUpdate";
         this.userUpdate.Size = new System.Drawing.Size(24, 24);
         this.userUpdate.Text = "Обновить";
         this.userUpdate.Click += new System.EventHandler(this.userUpdate_Click);
         // 
         // userChangesSave
         // 
         this.userChangesSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.userChangesSave.Enabled = false;
         this.userChangesSave.Image = ((System.Drawing.Image)(resources.GetObject("userChangesSave.Image")));
         this.userChangesSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.userChangesSave.Name = "userChangesSave";
         this.userChangesSave.Size = new System.Drawing.Size(24, 24);
         this.userChangesSave.Text = "Сохранить изменения";
         this.userChangesSave.Click += new System.EventHandler(this.userChangesSave_Click);
         // 
         // cbUserType
         // 
         this.cbUserType.Items.AddRange(new object[] {
            "Агенты",
            "Менеджеры"});
         this.cbUserType.Name = "cbUserType";
         this.cbUserType.Size = new System.Drawing.Size(121, 27);
         this.cbUserType.SelectedIndexChanged += new System.EventHandler(this.cbUserType_SelectedIndexChanged);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(24, 24);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(24, 24);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.dialog_cancel;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(24, 24);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(76, 24);
         this.toolStripLabel1.Text = "презентация";
         // 
         // tbPresentFolder
         // 
         this.tbPresentFolder.Name = "tbPresentFolder";
         this.tbPresentFolder.ReadOnly = true;
         this.tbPresentFolder.Size = new System.Drawing.Size(350, 27);
         this.tbPresentFolder.TextChanged += new System.EventHandler(this.tbPresentFolder_TextChanged);
         // 
         // btnFolder
         // 
         this.btnFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFolder.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.folder_drag_accept;
         this.btnFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFolder.Name = "btnFolder";
         this.btnFolder.RightToLeft = System.Windows.Forms.RightToLeft.No;
         this.btnFolder.Size = new System.Drawing.Size(24, 24);
         this.btnFolder.Text = "Назначить папку с презентацией";
         this.btnFolder.Click += new System.EventHandler(this.btnSelectPresentFolder_Click);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(42, 24);
         this.toolStripLabel2.Text = "Поиск";
         // 
         // tsFind
         // 
         this.tsFind.Name = "tsFind";
         this.tsFind.Size = new System.Drawing.Size(151, 27);
         this.tsFind.TextChanged += new System.EventHandler(this.tsFind_TextChanged);
         // 
         // statusStrip1
         // 
         this.statusStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.licenseStatusText});
         this.statusStrip1.Location = new System.Drawing.Point(3, 478);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(1232, 22);
         this.statusStrip1.TabIndex = 2;
         // 
         // licenseStatusText
         // 
         this.licenseStatusText.Name = "licenseStatusText";
         this.licenseStatusText.Size = new System.Drawing.Size(0, 17);
         // 
         // rmvScheduler
         // 
         this.rmvScheduler.Controls.Add(this.rmvScheduler1);
         this.rmvScheduler.Location = new System.Drawing.Point(4, 22);
         this.rmvScheduler.Name = "rmvScheduler";
         this.rmvScheduler.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.rmvScheduler.Size = new System.Drawing.Size(1238, 503);
         this.rmvScheduler.TabIndex = 3;
         this.rmvScheduler.Text = "Очистка фото";
         this.rmvScheduler.UseVisualStyleBackColor = true;
         // 
         // rmvScheduler1
         // 
         this.rmvScheduler1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.rmvScheduler1.Location = new System.Drawing.Point(3, 3);
         this.rmvScheduler1.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.rmvScheduler1.Name = "rmvScheduler1";
         this.rmvScheduler1.Size = new System.Drawing.Size(1232, 497);
         this.rmvScheduler1.TabIndex = 0;
         // 
         // userActivity
         // 
         this.userActivity.Controls.Add(this.dgvActivity);
         this.userActivity.Controls.Add(this.statusStrip2);
         this.userActivity.Controls.Add(this.toolStrip4);
         this.userActivity.Location = new System.Drawing.Point(4, 22);
         this.userActivity.Name = "userActivity";
         this.userActivity.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.userActivity.Size = new System.Drawing.Size(1238, 503);
         this.userActivity.TabIndex = 6;
         this.userActivity.Text = "Активность менеджеров";
         this.userActivity.UseVisualStyleBackColor = true;
         // 
         // dgvActivity
         // 
         this.dgvActivity.AllowUserToAddRows = false;
         this.dgvActivity.AllowUserToDeleteRows = false;
         this.dgvActivity.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvActivity.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnExclMgr,
            this.clmnManagerActivity,
            this.clmManagerIP,
            this.clmnManagerDuration});
         this.dgvActivity.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvActivity.Location = new System.Drawing.Point(3, 30);
         this.dgvActivity.Name = "dgvActivity";
         this.dgvActivity.ReadOnly = true;
         this.dgvActivity.RowHeadersVisible = false;
         this.dgvActivity.RowHeadersWidth = 51;
         this.dgvActivity.Size = new System.Drawing.Size(1232, 448);
         this.dgvActivity.TabIndex = 2;
         // 
         // clmnExclMgr
         // 
         this.clmnExclMgr.DataPropertyName = "IsExclusive";
         this.clmnExclMgr.HeaderText = "Экскл.";
         this.clmnExclMgr.MinimumWidth = 6;
         this.clmnExclMgr.Name = "clmnExclMgr";
         this.clmnExclMgr.ReadOnly = true;
         this.clmnExclMgr.Width = 70;
         // 
         // clmnManagerActivity
         // 
         this.clmnManagerActivity.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnManagerActivity.DataPropertyName = "Manager";
         this.clmnManagerActivity.HeaderText = "Менеджер";
         this.clmnManagerActivity.MinimumWidth = 6;
         this.clmnManagerActivity.Name = "clmnManagerActivity";
         this.clmnManagerActivity.ReadOnly = true;
         // 
         // clmManagerIP
         // 
         this.clmManagerIP.DataPropertyName = "IP";
         this.clmManagerIP.HeaderText = "IP";
         this.clmManagerIP.MinimumWidth = 6;
         this.clmManagerIP.Name = "clmManagerIP";
         this.clmManagerIP.ReadOnly = true;
         this.clmManagerIP.Width = 150;
         // 
         // clmnManagerDuration
         // 
         this.clmnManagerDuration.DataPropertyName = "Duration";
         this.clmnManagerDuration.HeaderText = "Продолжительность";
         this.clmnManagerDuration.MinimumWidth = 6;
         this.clmnManagerDuration.Name = "clmnManagerDuration";
         this.clmnManagerDuration.ReadOnly = true;
         this.clmnManagerDuration.Width = 180;
         // 
         // statusStrip2
         // 
         this.statusStrip2.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.statusStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.userActivityTotals});
         this.statusStrip2.Location = new System.Drawing.Point(3, 478);
         this.statusStrip2.Name = "statusStrip2";
         this.statusStrip2.Size = new System.Drawing.Size(1232, 22);
         this.statusStrip2.TabIndex = 1;
         this.statusStrip2.Text = "statusStrip2";
         // 
         // userActivityTotals
         // 
         this.userActivityTotals.Name = "userActivityTotals";
         this.userActivityTotals.Size = new System.Drawing.Size(0, 17);
         // 
         // toolStrip4
         // 
         this.toolStrip4.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.toolStrip4.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton1});
         this.toolStrip4.Location = new System.Drawing.Point(3, 3);
         this.toolStrip4.Name = "toolStrip4";
         this.toolStrip4.Size = new System.Drawing.Size(1232, 27);
         this.toolStrip4.TabIndex = 0;
         this.toolStrip4.Text = "toolStrip4";
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.Refresh;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(24, 24);
         this.toolStripButton1.Text = "Обновить";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // update
         // 
         this.update.AllowDrop = true;
         this.update.Controls.Add(this.label4);
         this.update.Controls.Add(this.sendUpdate);
         this.update.Controls.Add(this.browseFile);
         this.update.Controls.Add(this.label3);
         this.update.Controls.Add(this.uploadFileName);
         this.update.Location = new System.Drawing.Point(4, 22);
         this.update.Name = "update";
         this.update.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.update.Size = new System.Drawing.Size(1238, 503);
         this.update.TabIndex = 2;
         this.update.Text = "Обновления";
         this.update.UseVisualStyleBackColor = true;
         this.update.DragDrop += new System.Windows.Forms.DragEventHandler(this.update_DragDrop);
         this.update.DragEnter += new System.Windows.Forms.DragEventHandler(this.update_DragEnter);
         // 
         // label4
         // 
         this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label4.Location = new System.Drawing.Point(128, 22);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(330, 47);
         this.label4.TabIndex = 12;
         this.label4.Text = "Загрузка обновлений на сервер:\r\nвыберите файл и нажмите кнопку \"Отправить\".";
         // 
         // sendUpdate
         // 
         this.sendUpdate.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.sendUpdate.Location = new System.Drawing.Point(225, 274);
         this.sendUpdate.Name = "sendUpdate";
         this.sendUpdate.Size = new System.Drawing.Size(75, 23);
         this.sendUpdate.TabIndex = 11;
         this.sendUpdate.Text = "Отправить";
         this.sendUpdate.UseVisualStyleBackColor = true;
         this.sendUpdate.Click += new System.EventHandler(this.sendUpdate_Click);
         // 
         // browseFile
         // 
         this.browseFile.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.browseFile.Location = new System.Drawing.Point(464, 123);
         this.browseFile.Name = "browseFile";
         this.browseFile.Size = new System.Drawing.Size(22, 23);
         this.browseFile.TabIndex = 10;
         this.browseFile.Text = ">";
         this.browseFile.UseVisualStyleBackColor = true;
         this.browseFile.Click += new System.EventHandler(this.browseFile_Click);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(26, 128);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(99, 13);
         this.label3.TabIndex = 9;
         this.label3.Text = "Файл обновлений";
         // 
         // uploadFileName
         // 
         this.uploadFileName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.uploadFileName.Location = new System.Drawing.Point(131, 125);
         this.uploadFileName.Name = "uploadFileName";
         this.uploadFileName.Size = new System.Drawing.Size(327, 20);
         this.uploadFileName.TabIndex = 8;
         this.uploadFileName.DragDrop += new System.Windows.Forms.DragEventHandler(this.update_DragDrop);
         this.uploadFileName.DragEnter += new System.Windows.Forms.DragEventHandler(this.update_DragEnter);
         // 
         // settings
         // 
         this.settings.Controls.Add(this.label12);
         this.settings.Controls.Add(this.lbHistory);
         this.settings.Controls.Add(this.label11);
         this.settings.Controls.Add(this.tbName);
         this.settings.Controls.Add(this.label6);
         this.settings.Controls.Add(this.test);
         this.settings.Controls.Add(this.savePwd);
         this.settings.Controls.Add(this.label7);
         this.settings.Controls.Add(this.label5);
         this.settings.Controls.Add(this.log);
         this.settings.Controls.Add(this.pwd);
         this.settings.Controls.Add(this.save);
         this.settings.Controls.Add(this.port);
         this.settings.Controls.Add(this.ip);
         this.settings.Controls.Add(this.label2);
         this.settings.Controls.Add(this.label1);
         this.settings.Location = new System.Drawing.Point(4, 22);
         this.settings.Name = "settings";
         this.settings.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.settings.Size = new System.Drawing.Size(1238, 503);
         this.settings.TabIndex = 0;
         this.settings.Text = "Настройки";
         this.settings.UseVisualStyleBackColor = true;
         // 
         // label12
         // 
         this.label12.AutoSize = true;
         this.label12.Location = new System.Drawing.Point(397, 83);
         this.label12.Name = "label12";
         this.label12.Size = new System.Drawing.Size(131, 13);
         this.label12.TabIndex = 17;
         this.label12.Text = "Сохраненные настройки";
         // 
         // lbHistory
         // 
         this.lbHistory.ContextMenuStrip = this.contextMenuStrip1;
         this.lbHistory.FormattingEnabled = true;
         this.lbHistory.Location = new System.Drawing.Point(397, 109);
         this.lbHistory.Name = "lbHistory";
         this.lbHistory.Size = new System.Drawing.Size(279, 108);
         this.lbHistory.TabIndex = 16;
         this.lbHistory.KeyDown += new System.Windows.Forms.KeyEventHandler(this.lbHistory_KeyDown);
         this.lbHistory.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lbHistory_MouseDown);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miDel});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(119, 26);
         // 
         // miDel
         // 
         this.miDel.Name = "miDel";
         this.miDel.Size = new System.Drawing.Size(118, 22);
         this.miDel.Text = "Удалить";
         this.miDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // label11
         // 
         this.label11.AutoSize = true;
         this.label11.Location = new System.Drawing.Point(194, 86);
         this.label11.Name = "label11";
         this.label11.Size = new System.Drawing.Size(29, 13);
         this.label11.TabIndex = 15;
         this.label11.Text = "Имя";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(229, 83);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(121, 20);
         this.tbName.TabIndex = 14;
         // 
         // label6
         // 
         this.label6.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label6.Location = new System.Drawing.Point(106, 22);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(416, 62);
         this.label6.TabIndex = 13;
         this.label6.Text = "Укажите IP и порт сервера,\r\nвведите пароль администратора.\r\nИспользуйте кнопку \"Т" +
    "ест\" для проверки связи с сервером.";
         // 
         // test
         // 
         this.test.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.test.Location = new System.Drawing.Point(158, 312);
         this.test.Name = "test";
         this.test.Size = new System.Drawing.Size(75, 23);
         this.test.TabIndex = 10;
         this.test.Text = "Тест";
         this.test.UseVisualStyleBackColor = true;
         this.test.Click += new System.EventHandler(this.test_Click);
         // 
         // savePwd
         // 
         this.savePwd.AutoSize = true;
         this.savePwd.Location = new System.Drawing.Point(229, 236);
         this.savePwd.Name = "savePwd";
         this.savePwd.Size = new System.Drawing.Size(121, 17);
         this.savePwd.TabIndex = 9;
         this.savePwd.Text = "Запомнить пароль";
         this.savePwd.UseVisualStyleBackColor = true;
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(185, 173);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(38, 13);
         this.label7.TabIndex = 8;
         this.label7.Text = "Логин";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(178, 203);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(45, 13);
         this.label5.TabIndex = 8;
         this.label5.Text = "Пароль";
         // 
         // log
         // 
         this.log.Location = new System.Drawing.Point(229, 170);
         this.log.Name = "log";
         this.log.Size = new System.Drawing.Size(121, 20);
         this.log.TabIndex = 7;
         // 
         // pwd
         // 
         this.pwd.Location = new System.Drawing.Point(229, 200);
         this.pwd.Name = "pwd";
         this.pwd.Size = new System.Drawing.Size(121, 20);
         this.pwd.TabIndex = 7;
         this.pwd.UseSystemPasswordChar = true;
         // 
         // save
         // 
         this.save.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.save.Location = new System.Drawing.Point(310, 312);
         this.save.Name = "save";
         this.save.Size = new System.Drawing.Size(75, 23);
         this.save.TabIndex = 4;
         this.save.Text = "Сохранить";
         this.save.UseVisualStyleBackColor = true;
         this.save.Click += new System.EventHandler(this.save_Click);
         // 
         // port
         // 
         this.port.Location = new System.Drawing.Point(229, 140);
         this.port.Name = "port";
         this.port.Size = new System.Drawing.Size(121, 20);
         this.port.TabIndex = 3;
         // 
         // ip
         // 
         this.ip.Location = new System.Drawing.Point(229, 110);
         this.ip.Name = "ip";
         this.ip.Size = new System.Drawing.Size(121, 20);
         this.ip.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(191, 140);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(32, 13);
         this.label2.TabIndex = 1;
         this.label2.Text = "Порт";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(173, 113);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(50, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "IP адрес";
         // 
         // gps
         // 
         this.gps.Controls.Add(this.btnSaveGPSSetting);
         this.gps.Controls.Add(this.groupBox1);
         this.gps.Location = new System.Drawing.Point(4, 22);
         this.gps.Name = "gps";
         this.gps.Padding = new System.Windows.Forms.Padding(3, 3, 3, 3);
         this.gps.Size = new System.Drawing.Size(1238, 503);
         this.gps.TabIndex = 4;
         this.gps.Text = "GPS Настройки";
         this.gps.UseVisualStyleBackColor = true;
         // 
         // btnSaveGPSSetting
         // 
         this.btnSaveGPSSetting.Location = new System.Drawing.Point(239, 113);
         this.btnSaveGPSSetting.Name = "btnSaveGPSSetting";
         this.btnSaveGPSSetting.Size = new System.Drawing.Size(75, 23);
         this.btnSaveGPSSetting.TabIndex = 1;
         this.btnSaveGPSSetting.Text = "Сохранить";
         this.btnSaveGPSSetting.UseVisualStyleBackColor = true;
         this.btnSaveGPSSetting.Click += new System.EventHandler(this.btnSaveGPSSetting_Click);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.btnSPGSetAllDays);
         this.groupBox1.Controls.Add(this.btnUnsetGpsSetting);
         this.groupBox1.Controls.Add(this.dtpEnd);
         this.groupBox1.Controls.Add(this.label10);
         this.groupBox1.Controls.Add(this.dtpBegin);
         this.groupBox1.Controls.Add(this.label9);
         this.groupBox1.Controls.Add(this.cbD6);
         this.groupBox1.Controls.Add(this.cbD5);
         this.groupBox1.Controls.Add(this.cbD4);
         this.groupBox1.Controls.Add(this.cbD7);
         this.groupBox1.Controls.Add(this.cbD3);
         this.groupBox1.Controls.Add(this.cbD2);
         this.groupBox1.Controls.Add(this.cbD1);
         this.groupBox1.Location = new System.Drawing.Point(8, 6);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(309, 101);
         this.groupBox1.TabIndex = 0;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Включать снятие GPS координат в период";
         // 
         // btnSPGSetAllDays
         // 
         this.btnSPGSetAllDays.Location = new System.Drawing.Point(7, 72);
         this.btnSPGSetAllDays.Name = "btnSPGSetAllDays";
         this.btnSPGSetAllDays.Size = new System.Drawing.Size(75, 23);
         this.btnSPGSetAllDays.TabIndex = 12;
         this.btnSPGSetAllDays.Text = "Все дни";
         this.btnSPGSetAllDays.UseVisualStyleBackColor = true;
         this.btnSPGSetAllDays.Click += new System.EventHandler(this.btnSPGSetAllDays_Click);
         // 
         // btnUnsetGpsSetting
         // 
         this.btnUnsetGpsSetting.Location = new System.Drawing.Point(91, 72);
         this.btnUnsetGpsSetting.Name = "btnUnsetGpsSetting";
         this.btnUnsetGpsSetting.Size = new System.Drawing.Size(75, 23);
         this.btnUnsetGpsSetting.TabIndex = 11;
         this.btnUnsetGpsSetting.Text = "Очистить";
         this.btnUnsetGpsSetting.UseVisualStyleBackColor = true;
         this.btnUnsetGpsSetting.Click += new System.EventHandler(this.btnUnsetGpsSetting_Click);
         // 
         // dtpEnd
         // 
         this.dtpEnd.CustomFormat = "HH:mm";
         this.dtpEnd.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpEnd.Location = new System.Drawing.Point(148, 45);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.ShowUpDown = true;
         this.dtpEnd.Size = new System.Drawing.Size(57, 20);
         this.dtpEnd.TabIndex = 10;
         this.dtpEnd.Value = new System.DateTime(2012, 11, 14, 18, 0, 0, 0);
         // 
         // label10
         // 
         this.label10.AutoSize = true;
         this.label10.Location = new System.Drawing.Point(122, 49);
         this.label10.Name = "label10";
         this.label10.Size = new System.Drawing.Size(19, 13);
         this.label10.TabIndex = 9;
         this.label10.Text = "по";
         // 
         // dtpBegin
         // 
         this.dtpBegin.CustomFormat = "HH:mm";
         this.dtpBegin.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpBegin.Location = new System.Drawing.Point(58, 45);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.ShowUpDown = true;
         this.dtpBegin.Size = new System.Drawing.Size(57, 20);
         this.dtpBegin.TabIndex = 8;
         this.dtpBegin.Value = new System.DateTime(2012, 11, 14, 9, 0, 0, 0);
         // 
         // label9
         // 
         this.label9.AutoSize = true;
         this.label9.Location = new System.Drawing.Point(4, 49);
         this.label9.Name = "label9";
         this.label9.Size = new System.Drawing.Size(48, 13);
         this.label9.TabIndex = 7;
         this.label9.Text = "время с";
         // 
         // cbD6
         // 
         this.cbD6.AutoSize = true;
         this.cbD6.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbD6.ForeColor = System.Drawing.Color.Red;
         this.cbD6.Location = new System.Drawing.Point(223, 20);
         this.cbD6.Name = "cbD6";
         this.cbD6.Size = new System.Drawing.Size(41, 17);
         this.cbD6.TabIndex = 6;
         this.cbD6.Tag = "6";
         this.cbD6.Text = "Сб";
         this.cbD6.UseVisualStyleBackColor = true;
         // 
         // cbD5
         // 
         this.cbD5.AutoSize = true;
         this.cbD5.Location = new System.Drawing.Point(179, 20);
         this.cbD5.Name = "cbD5";
         this.cbD5.Size = new System.Drawing.Size(39, 17);
         this.cbD5.TabIndex = 5;
         this.cbD5.Tag = "5";
         this.cbD5.Text = "Пт";
         this.cbD5.UseVisualStyleBackColor = true;
         // 
         // cbD4
         // 
         this.cbD4.AutoSize = true;
         this.cbD4.Location = new System.Drawing.Point(135, 20);
         this.cbD4.Name = "cbD4";
         this.cbD4.Size = new System.Drawing.Size(39, 17);
         this.cbD4.TabIndex = 4;
         this.cbD4.Tag = "4";
         this.cbD4.Text = "Чт";
         this.cbD4.UseVisualStyleBackColor = true;
         // 
         // cbD7
         // 
         this.cbD7.AutoSize = true;
         this.cbD7.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbD7.ForeColor = System.Drawing.Color.Red;
         this.cbD7.Location = new System.Drawing.Point(267, 20);
         this.cbD7.Name = "cbD7";
         this.cbD7.Size = new System.Drawing.Size(41, 17);
         this.cbD7.TabIndex = 3;
         this.cbD7.Tag = "7";
         this.cbD7.Text = "Вс";
         this.cbD7.UseVisualStyleBackColor = true;
         // 
         // cbD3
         // 
         this.cbD3.AutoSize = true;
         this.cbD3.Location = new System.Drawing.Point(91, 20);
         this.cbD3.Name = "cbD3";
         this.cbD3.Size = new System.Drawing.Size(39, 17);
         this.cbD3.TabIndex = 2;
         this.cbD3.Tag = "3";
         this.cbD3.Text = "Ср";
         this.cbD3.UseVisualStyleBackColor = true;
         // 
         // cbD2
         // 
         this.cbD2.AutoSize = true;
         this.cbD2.Location = new System.Drawing.Point(48, 20);
         this.cbD2.Name = "cbD2";
         this.cbD2.Size = new System.Drawing.Size(38, 17);
         this.cbD2.TabIndex = 1;
         this.cbD2.Tag = "2";
         this.cbD2.Text = "Вт";
         this.cbD2.UseVisualStyleBackColor = true;
         // 
         // cbD1
         // 
         this.cbD1.AutoSize = true;
         this.cbD1.Location = new System.Drawing.Point(3, 20);
         this.cbD1.Name = "cbD1";
         this.cbD1.Size = new System.Drawing.Size(40, 17);
         this.cbD1.TabIndex = 0;
         this.cbD1.Tag = "1";
         this.cbD1.Text = "Пн";
         this.cbD1.UseVisualStyleBackColor = true;
         // 
         // syncinfo
         // 
         this.syncinfo.Controls.Add(this.dtpDateSyncInfo);
         this.syncinfo.Controls.Add(this.dgvSyncInfo);
         this.syncinfo.Controls.Add(this.toolStrip3);
         this.syncinfo.Location = new System.Drawing.Point(4, 22);
         this.syncinfo.Name = "syncinfo";
         this.syncinfo.Size = new System.Drawing.Size(1238, 503);
         this.syncinfo.TabIndex = 5;
         this.syncinfo.Text = "Лог синхронизации";
         this.syncinfo.UseVisualStyleBackColor = true;
         // 
         // dtpDateSyncInfo
         // 
         this.dtpDateSyncInfo.Location = new System.Drawing.Point(187, 2);
         this.dtpDateSyncInfo.Name = "dtpDateSyncInfo";
         this.dtpDateSyncInfo.Size = new System.Drawing.Size(127, 20);
         this.dtpDateSyncInfo.TabIndex = 2;
         // 
         // dgvSyncInfo
         // 
         this.dgvSyncInfo.AllowUserToAddRows = false;
         this.dgvSyncInfo.AllowUserToDeleteRows = false;
         this.dgvSyncInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSyncInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column3,
            this.Column2});
         this.dgvSyncInfo.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvSyncInfo.Location = new System.Drawing.Point(0, 27);
         this.dgvSyncInfo.Name = "dgvSyncInfo";
         this.dgvSyncInfo.RowHeadersVisible = false;
         this.dgvSyncInfo.RowHeadersWidth = 51;
         this.dgvSyncInfo.Size = new System.Drawing.Size(1238, 476);
         this.dgvSyncInfo.TabIndex = 1;
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Date";
         this.Column1.HeaderText = "Дата";
         this.Column1.MinimumWidth = 6;
         this.Column1.Name = "Column1";
         this.Column1.Width = 125;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Agent";
         this.Column3.HeaderText = "Агент";
         this.Column3.MinimumWidth = 6;
         this.Column3.Name = "Column3";
         this.Column3.Width = 125;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Info";
         this.Column2.HeaderText = "Синхронизация";
         this.Column2.MinimumWidth = 6;
         this.Column2.Name = "Column2";
         // 
         // toolStrip3
         // 
         this.toolStrip3.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefreshSyncInfo,
            this.cbAgentSyncInfo});
         this.toolStrip3.Location = new System.Drawing.Point(0, 0);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(1238, 27);
         this.toolStrip3.TabIndex = 0;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // btnRefreshSyncInfo
         // 
         this.btnRefreshSyncInfo.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefreshSyncInfo.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.Refresh;
         this.btnRefreshSyncInfo.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefreshSyncInfo.Name = "btnRefreshSyncInfo";
         this.btnRefreshSyncInfo.Size = new System.Drawing.Size(24, 24);
         this.btnRefreshSyncInfo.Text = "Обновить";
         this.btnRefreshSyncInfo.Click += new System.EventHandler(this.btnRefreshSyncInfo_Click);
         // 
         // cbAgentSyncInfo
         // 
         this.cbAgentSyncInfo.Items.AddRange(new object[] {
            "Все"});
         this.cbAgentSyncInfo.Name = "cbAgentSyncInfo";
         this.cbAgentSyncInfo.Size = new System.Drawing.Size(150, 27);
         this.cbAgentSyncInfo.SelectedIndexChanged += new System.EventHandler(this.cbAgentSyncInfo_SelectedIndexChanged);
         // 
         // version
         // 
         this.version.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.version.Location = new System.Drawing.Point(317, 2);
         this.version.Name = "version";
         this.version.Size = new System.Drawing.Size(917, 35);
         this.version.TabIndex = 3;
         this.version.Text = "version";
         this.version.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
         // 
         // label8
         // 
         this.label8.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label8.Location = new System.Drawing.Point(81, 2);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(230, 35);
         this.label8.TabIndex = 4;
         this.label8.Text = "Наполеон - Администратор";
         this.label8.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
         // 
         // MainForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1246, 570);
         this.Controls.Add(this.label8);
         this.Controls.Add(this.version);
         this.Controls.Add(this.tabControl1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "MainForm";
         this.Text = "Наполеон - Администратор";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.MainForm_KeyDown);
         this.tabControl1.ResumeLayout(false);
         this.users.ResumeLayout(false);
         this.users.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.usersView)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.rmvScheduler.ResumeLayout(false);
         this.userActivity.ResumeLayout(false);
         this.userActivity.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvActivity)).EndInit();
         this.statusStrip2.ResumeLayout(false);
         this.statusStrip2.PerformLayout();
         this.toolStrip4.ResumeLayout(false);
         this.toolStrip4.PerformLayout();
         this.update.ResumeLayout(false);
         this.update.PerformLayout();
         this.settings.ResumeLayout(false);
         this.settings.PerformLayout();
         this.contextMenuStrip1.ResumeLayout(false);
         this.gps.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.syncinfo.ResumeLayout(false);
         this.syncinfo.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSyncInfo)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.TabPage settings;
      private System.Windows.Forms.TextBox port;
      private System.Windows.Forms.TextBox ip;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button save;
      private System.Windows.Forms.TabPage users;
      private System.Windows.Forms.StatusStrip statusStrip1;
      public System.Windows.Forms.DataGridView usersView;
      protected System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripStatusLabel licenseStatusText;
      protected System.Windows.Forms.ToolStripButton userChangesSave;
      private System.Windows.Forms.Button browseFile;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox uploadFileName;
      private System.Windows.Forms.Button sendUpdate;
       private System.Windows.Forms.TabPage rmvScheduler;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox pwd;
      private System.Windows.Forms.CheckBox savePwd;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.Button test;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox log;
      protected System.Windows.Forms.Label version;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.TabPage gps;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.CheckBox cbD6;
      private System.Windows.Forms.CheckBox cbD5;
      private System.Windows.Forms.CheckBox cbD4;
      private System.Windows.Forms.CheckBox cbD7;
      private System.Windows.Forms.CheckBox cbD3;
      private System.Windows.Forms.CheckBox cbD2;
      private System.Windows.Forms.CheckBox cbD1;
      private System.Windows.Forms.Label label9;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Label label10;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Button btnSaveGPSSetting;
      private System.Windows.Forms.Button btnUnsetGpsSetting;
      private System.Windows.Forms.Button btnSPGSetAllDays;
      protected System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.Label label11;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label12;
      private System.Windows.Forms.ListBox lbHistory;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miDel;
      private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox tbPresentFolder;
      private System.Windows.Forms.ToolStripButton btnFolder;
      protected System.Windows.Forms.ToolStripComboBox cbUserType;
      protected System.Windows.Forms.ToolStripButton btnAdd;
      protected System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.TabPage syncinfo;
      protected System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.ToolStripButton btnRefreshSyncInfo;
      private System.Windows.Forms.ToolStripComboBox cbAgentSyncInfo;
      protected System.Windows.Forms.DateTimePicker dtpDateSyncInfo;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.ToolStrip toolStrip4;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.StatusStrip statusStrip2;
      private System.Windows.Forms.ToolStripStatusLabel userActivityTotals;
      private System.Windows.Forms.DataGridView dgvActivity;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnExclMgr;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnManagerActivity;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmManagerIP;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnManagerDuration;
      protected System.Windows.Forms.TabControl tabControl1;
      protected System.Windows.Forms.TabPage userActivity;
      public System.Windows.Forms.DataGridView dgvSyncInfo;
      public System.Windows.Forms.TabPage update;
      public System.Windows.Forms.ToolStripButton userUpdate;
      public System.Windows.Forms.DataGridViewTextBoxColumn clmnId;
      public System.Windows.Forms.DataGridViewTextBoxColumn user;
      public System.Windows.Forms.DataGridViewTextBoxColumn login;
      public System.Windows.Forms.DataGridViewTextBoxColumn password;
      public System.Windows.Forms.DataGridViewTextBoxColumn ProgID;
      public System.Windows.Forms.DataGridViewTextBoxColumn activity;
      public System.Windows.Forms.DataGridViewTextBoxColumn progVersion;
      public System.Windows.Forms.DataGridViewCheckBoxColumn clmnCheckPwd;
      public  System.Windows.Forms.DataGridViewComboBoxColumn registred;
      public System.Windows.Forms.DataGridViewCheckBoxColumn tracking;
      private RmvScheduler rmvScheduler1;
      private System.Windows.Forms.ToolStripTextBox tsFind;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
   }
}
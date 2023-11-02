namespace GRSoft.NapoleonManager
{
   partial class FmMakeShedule
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMakeShedule));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator6 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbExport = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tsbAddress = new System.Windows.Forms.ToolStripTextBox();
         this.btnAddressClear = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.tsAgent = new System.Windows.Forms.ToolStripLabel();
         this.tsbAgent = new System.Windows.Forms.ToolStripTextBox();
         this.btnAgentClear = new System.Windows.Forms.ToolStripButton();
         this.tsAgentAdd = new System.Windows.Forms.ToolStripLabel();
         this.tsbAgentAdd = new System.Windows.Forms.ToolStripTextBox();
         this.btnAgentAddClear = new System.Windows.Forms.ToolStripButton();
         this.tsDisp = new System.Windows.Forms.ToolStripLabel();
         this.tsbDisp = new System.Windows.Forms.ToolStripTextBox();
         this.btnDispClear = new System.Windows.Forms.ToolStripButton();
         this.tsMerch = new System.Windows.Forms.ToolStripLabel();
         this.tsbMerch = new System.Windows.Forms.ToolStripTextBox();
         this.btnMerchClear = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator4 = new System.Windows.Forms.ToolStripSeparator();
         this.tsMerchAdd = new System.Windows.Forms.ToolStripLabel();
         this.tsbMerchAdd = new System.Windows.Forms.ToolStripTextBox();
         this.btnMerchAddClear = new System.Windows.Forms.ToolStripButton();
         this.tsbNoRoute = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgentAdd = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnMonday = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnTue = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnWed = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnThursday = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnFriday = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnSaturday = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnSunday = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnCicle = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnDCode = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnMRCode = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnMrAdd = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator6,
            this.tsbExport,
            this.toolStripSeparator1,
            this.toolStripLabel1,
            this.tsbAddress,
            this.btnAddressClear,
            this.toolStripSeparator2,
            this.tsAgent,
            this.tsbAgent,
            this.btnAgentClear,
            this.tsAgentAdd,
            this.tsbAgentAdd,
            this.btnAgentAddClear,
            this.tsDisp,
            this.tsbDisp,
            this.btnDispClear,
            this.tsMerch,
            this.tsbMerch,
            this.btnMerchClear,
            this.toolStripSeparator4,
            this.tsMerchAdd,
            this.tsbMerchAdd,
            this.btnMerchAddClear,
            this.tsbNoRoute});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1352, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
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
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator6
         // 
         this.toolStripSeparator6.Name = "toolStripSeparator6";
         this.toolStripSeparator6.Size = new System.Drawing.Size(6, 39);
         // 
         // tsbExport
         // 
         this.tsbExport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbExport.Image = global::GRSoft.NapoleonManager.Properties.Resources.abiword_3;
         this.tsbExport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbExport.Name = "tsbExport";
         this.tsbExport.Size = new System.Drawing.Size(36, 36);
         this.tsbExport.Text = "Создать маршруты агентов";
         this.tsbExport.Click += new System.EventHandler(this.tsbExport_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(177, 36);
         this.toolStripLabel1.Text = "Контрагент, адрес";
         // 
         // tsbAddress
         // 
         this.tsbAddress.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbAddress.Name = "tsbAddress";
         this.tsbAddress.Size = new System.Drawing.Size(225, 39);
         this.tsbAddress.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnAddressClear
         // 
         this.btnAddressClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddressClear.Image = ((System.Drawing.Image)(resources.GetObject("btnAddressClear.Image")));
         this.btnAddressClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddressClear.Name = "btnAddressClear";
         this.btnAddressClear.Size = new System.Drawing.Size(36, 36);
         this.btnAddressClear.Text = "Очистить";
         this.btnAddressClear.Click += new System.EventHandler(this.DoClear);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // tsAgent
         // 
         this.tsAgent.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsAgent.Name = "tsAgent";
         this.tsAgent.Size = new System.Drawing.Size(36, 36);
         this.tsAgent.Text = "ТП";
         // 
         // tsbAgent
         // 
         this.tsbAgent.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbAgent.Name = "tsbAgent";
         this.tsbAgent.Size = new System.Drawing.Size(79, 39);
         this.tsbAgent.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnAgentClear
         // 
         this.btnAgentClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgentClear.Image = ((System.Drawing.Image)(resources.GetObject("btnAgentClear.Image")));
         this.btnAgentClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgentClear.Name = "btnAgentClear";
         this.btnAgentClear.Size = new System.Drawing.Size(36, 36);
         this.btnAgentClear.Text = "Очистить";
         this.btnAgentClear.Click += new System.EventHandler(this.DoClear);
         // 
         // tsAgentAdd
         // 
         this.tsAgentAdd.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsAgentAdd.Name = "tsAgentAdd";
         this.tsAgentAdd.Size = new System.Drawing.Size(76, 36);
         this.tsAgentAdd.Text = "ТП доп";
         this.tsAgentAdd.Visible = false;
         // 
         // tsbAgentAdd
         // 
         this.tsbAgentAdd.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbAgentAdd.Name = "tsbAgentAdd";
         this.tsbAgentAdd.Size = new System.Drawing.Size(79, 39);
         this.tsbAgentAdd.Visible = false;
         this.tsbAgentAdd.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnAgentAddClear
         // 
         this.btnAgentAddClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgentAddClear.Image = ((System.Drawing.Image)(resources.GetObject("btnAgentAddClear.Image")));
         this.btnAgentAddClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgentAddClear.Name = "btnAgentAddClear";
         this.btnAgentAddClear.Size = new System.Drawing.Size(36, 36);
         this.btnAgentAddClear.Text = "Очистить";
         this.btnAgentAddClear.Visible = false;
         this.btnAgentAddClear.Click += new System.EventHandler(this.DoClear);
         // 
         // tsDisp
         // 
         this.tsDisp.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsDisp.Name = "tsDisp";
         this.tsDisp.Size = new System.Drawing.Size(26, 36);
         this.tsDisp.Text = "Д";
         // 
         // tsbDisp
         // 
         this.tsbDisp.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbDisp.Name = "tsbDisp";
         this.tsbDisp.Size = new System.Drawing.Size(79, 39);
         this.tsbDisp.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnDispClear
         // 
         this.btnDispClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDispClear.Image = ((System.Drawing.Image)(resources.GetObject("btnDispClear.Image")));
         this.btnDispClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDispClear.Name = "btnDispClear";
         this.btnDispClear.Size = new System.Drawing.Size(36, 36);
         this.btnDispClear.Text = "Очистить";
         this.btnDispClear.Click += new System.EventHandler(this.DoClear);
         // 
         // tsMerch
         // 
         this.tsMerch.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsMerch.Name = "tsMerch";
         this.tsMerch.Size = new System.Drawing.Size(82, 36);
         this.tsMerch.Text = "Код Мр";
         // 
         // tsbMerch
         // 
         this.tsbMerch.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbMerch.Name = "tsbMerch";
         this.tsbMerch.Size = new System.Drawing.Size(79, 39);
         this.tsbMerch.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnMerchClear
         // 
         this.btnMerchClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnMerchClear.Image = ((System.Drawing.Image)(resources.GetObject("btnMerchClear.Image")));
         this.btnMerchClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnMerchClear.Name = "btnMerchClear";
         this.btnMerchClear.Size = new System.Drawing.Size(36, 36);
         this.btnMerchClear.Text = "Очистить";
         this.btnMerchClear.Click += new System.EventHandler(this.DoClear);
         // 
         // toolStripSeparator4
         // 
         this.toolStripSeparator4.Name = "toolStripSeparator4";
         this.toolStripSeparator4.Size = new System.Drawing.Size(6, 39);
         // 
         // tsMerchAdd
         // 
         this.tsMerchAdd.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsMerchAdd.Name = "tsMerchAdd";
         this.tsMerchAdd.Size = new System.Drawing.Size(122, 28);
         this.tsMerchAdd.Text = "Код доп Мр";
         this.tsMerchAdd.Visible = false;
         // 
         // tsbMerchAdd
         // 
         this.tsbMerchAdd.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbMerchAdd.Name = "tsbMerchAdd";
         this.tsbMerchAdd.Size = new System.Drawing.Size(79, 34);
         this.tsbMerchAdd.Visible = false;
         this.tsbMerchAdd.TextChanged += new System.EventHandler(this.TextChange);
         // 
         // btnMerchAddClear
         // 
         this.btnMerchAddClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnMerchAddClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.clear_text;
         this.btnMerchAddClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnMerchAddClear.Name = "btnMerchAddClear";
         this.btnMerchAddClear.Size = new System.Drawing.Size(36, 36);
         this.btnMerchAddClear.Text = "Очистить";
         this.btnMerchAddClear.Visible = false;
         this.btnMerchAddClear.Click += new System.EventHandler(this.DoClear);
         // 
         // tsbNoRoute
         // 
         this.tsbNoRoute.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.tsbNoRoute.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.tsbNoRoute.Image = ((System.Drawing.Image)(resources.GetObject("tsbNoRoute.Image")));
         this.tsbNoRoute.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbNoRoute.Name = "tsbNoRoute";
         this.tsbNoRoute.Size = new System.Drawing.Size(143, 32);
         this.tsbNoRoute.Text = "Без маршрута";
         this.tsbNoRoute.Click += new System.EventHandler(this.tsbNoRoute_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg,
            this.clmnAddress,
            this.clmnAgent,
            this.clmnAgentAdd,
            this.clmnMonday,
            this.clmnTue,
            this.clmnWed,
            this.clmnThursday,
            this.clmnFriday,
            this.clmnSaturday,
            this.clmnSunday,
            this.clmnCicle,
            this.clmnDCode,
            this.clmnMRCode,
            this.clmnMrAdd});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 39);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(1352, 799);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
         this.dgvItems.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvItems_DataError);
         // 
         // clmnOrg
         // 
         this.clmnOrg.DataPropertyName = "Name";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.Width = 120;
         // 
         // clmnAddress
         // 
         this.clmnAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAddress.DataPropertyName = "Address";
         this.clmnAddress.HeaderText = "Адрес доставки";
         this.clmnAddress.Name = "clmnAddress";
         // 
         // clmnAgent
         // 
         this.clmnAgent.DataPropertyName = "Agent";
         this.clmnAgent.HeaderText = "Код ТП основной";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         // 
         // clmnAgentAdd
         // 
         this.clmnAgentAdd.DataPropertyName = "AgentAdd";
         this.clmnAgentAdd.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnAgentAdd.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnAgentAdd.HeaderText = "Код ТП доп.";
         this.clmnAgentAdd.Name = "clmnAgentAdd";
         this.clmnAgentAdd.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnAgentAdd.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnAgentAdd.Visible = false;
         this.clmnAgentAdd.Width = 60;
         // 
         // clmnMonday
         // 
         this.clmnMonday.DataPropertyName = "Mon";
         this.clmnMonday.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnMonday.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnMonday.HeaderText = "пн";
         this.clmnMonday.Name = "clmnMonday";
         this.clmnMonday.Width = 55;
         // 
         // clmnTue
         // 
         this.clmnTue.DataPropertyName = "Tue";
         this.clmnTue.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnTue.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnTue.HeaderText = "вт";
         this.clmnTue.Name = "clmnTue";
         this.clmnTue.Width = 55;
         // 
         // clmnWed
         // 
         this.clmnWed.DataPropertyName = "Wed";
         this.clmnWed.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnWed.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnWed.HeaderText = "ср";
         this.clmnWed.Name = "clmnWed";
         this.clmnWed.Width = 55;
         // 
         // clmnThursday
         // 
         this.clmnThursday.DataPropertyName = "Thu";
         this.clmnThursday.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnThursday.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnThursday.HeaderText = "чт";
         this.clmnThursday.Name = "clmnThursday";
         this.clmnThursday.Width = 55;
         // 
         // clmnFriday
         // 
         this.clmnFriday.DataPropertyName = "Fri";
         this.clmnFriday.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnFriday.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnFriday.HeaderText = "пт";
         this.clmnFriday.Name = "clmnFriday";
         this.clmnFriday.Width = 55;
         // 
         // clmnSaturday
         // 
         this.clmnSaturday.DataPropertyName = "Sat";
         this.clmnSaturday.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnSaturday.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnSaturday.HeaderText = "сб";
         this.clmnSaturday.Name = "clmnSaturday";
         this.clmnSaturday.Width = 55;
         // 
         // clmnSunday
         // 
         this.clmnSunday.DataPropertyName = "Sun";
         this.clmnSunday.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnSunday.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnSunday.HeaderText = "вс";
         this.clmnSunday.Name = "clmnSunday";
         this.clmnSunday.Width = 55;
         // 
         // clmnCicle
         // 
         this.clmnCicle.DataPropertyName = "Cicle";
         this.clmnCicle.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnCicle.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnCicle.HeaderText = "Цикличность";
         this.clmnCicle.Name = "clmnCicle";
         this.clmnCicle.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnCicle.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         // 
         // clmnDCode
         // 
         this.clmnDCode.DataPropertyName = "DCode";
         this.clmnDCode.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnDCode.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnDCode.HeaderText = "Код Д";
         this.clmnDCode.Name = "clmnDCode";
         this.clmnDCode.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnDCode.Width = 60;
         // 
         // clmnMRCode
         // 
         this.clmnMRCode.DataPropertyName = "MRCode";
         this.clmnMRCode.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnMRCode.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnMRCode.HeaderText = "Код МР";
         this.clmnMRCode.Name = "clmnMRCode";
         this.clmnMRCode.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnMRCode.Width = 60;
         // 
         // clmnMrAdd
         // 
         this.clmnMrAdd.DataPropertyName = "MRAdd";
         this.clmnMrAdd.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.clmnMrAdd.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
         this.clmnMrAdd.HeaderText = "Код доп МР";
         this.clmnMrAdd.Name = "clmnMrAdd";
         this.clmnMrAdd.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnMrAdd.Visible = false;
         this.clmnMrAdd.Width = 60;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmMakeShedule
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1352, 838);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "FmMakeShedule";
         this.Text = "Распределение маршрутов агентов";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox tsbAddress;
      private System.Windows.Forms.ToolStripButton btnAddressClear;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator6;
      private System.Windows.Forms.ToolStripButton tsbExport;
      private System.Windows.Forms.Timer timer1;
      protected System.Windows.Forms.ToolStrip toolStrip1;
      protected System.Windows.Forms.ToolStripLabel tsAgentAdd;
      protected System.Windows.Forms.ToolStripLabel tsDisp;
      protected System.Windows.Forms.ToolStripLabel tsAgent;
      protected System.Windows.Forms.ToolStripLabel tsMerch;
      protected System.Windows.Forms.ToolStripTextBox tsbMerch;
      protected System.Windows.Forms.ToolStripButton btnMerchClear;
      protected System.Windows.Forms.ToolStripSeparator toolStripSeparator4;
      protected System.Windows.Forms.ToolStripLabel tsMerchAdd;
      protected System.Windows.Forms.ToolStripTextBox tsbMerchAdd;
      protected System.Windows.Forms.ToolStripButton btnMerchAddClear;
      protected System.Windows.Forms.ToolStripTextBox tsbDisp;
      protected System.Windows.Forms.ToolStripButton btnDispClear;
      protected System.Windows.Forms.ToolStripTextBox tsbAgent;
      protected System.Windows.Forms.ToolStripButton btnAgentClear;
      protected System.Windows.Forms.ToolStripTextBox tsbAgentAdd;
      protected System.Windows.Forms.ToolStripButton btnAgentAddClear;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnAddress;
      protected System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnAgentAdd;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnMonday;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnTue;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnWed;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnThursday;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnFriday;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnSaturday;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnSunday;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnCicle;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnDCode;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnMRCode;
      protected System.Windows.Forms.DataGridViewComboBoxColumn clmnMrAdd;
      private System.Windows.Forms.ToolStripButton tsbNoRoute;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmReturnRequestList
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
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle5 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle6 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle7 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle8 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle9 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle10 = new System.Windows.Forms.DataGridViewCellStyle();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReturnRequestList));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer3 = new System.Windows.Forms.SplitContainer();
         this.dgvDocs = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFactory = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAccepted = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.lvPhoto = new System.Windows.Forms.ListView();
         this.imPhoto = new System.Windows.Forms.ImageList(this.components);
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.tsbPrint = new System.Windows.Forms.ToolStripButton();
         this.tsbExport = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnMfrDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDoc = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDocDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgentQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCause = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnSvQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnSvCause = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.clmnRemark = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn9 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn10 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn11 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn12 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer3.Panel1.SuspendLayout();
         this.splitContainer3.Panel2.SuspendLayout();
         this.splitContainer3.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDocs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Margin = new System.Windows.Forms.Padding(4);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer3);
         this.splitContainer1.Panel1.Controls.Add(this.dtpBegin);
         this.splitContainer1.Panel1.Controls.Add(this.dtpEnd);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvItems);
         this.splitContainer1.Size = new System.Drawing.Size(1588, 855);
         this.splitContainer1.SplitterDistance = 344;
         this.splitContainer1.SplitterWidth = 5;
         this.splitContainer1.TabIndex = 0;
         // 
         // splitContainer3
         // 
         this.splitContainer3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer3.Location = new System.Drawing.Point(0, 39);
         this.splitContainer3.Margin = new System.Windows.Forms.Padding(4);
         this.splitContainer3.Name = "splitContainer3";
         // 
         // splitContainer3.Panel1
         // 
         this.splitContainer3.Panel1.Controls.Add(this.dgvDocs);
         // 
         // splitContainer3.Panel2
         // 
         this.splitContainer3.Panel2.Controls.Add(this.lvPhoto);
         this.splitContainer3.Size = new System.Drawing.Size(1588, 305);
         this.splitContainer3.SplitterDistance = 1054;
         this.splitContainer3.SplitterWidth = 5;
         this.splitContainer3.TabIndex = 10;
         // 
         // dgvDocs
         // 
         this.dgvDocs.AllowUserToAddRows = false;
         this.dgvDocs.AllowUserToDeleteRows = false;
         this.dgvDocs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvDocs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnFactory,
            this.clmnOrg,
            this.clmnAddress,
            this.clmnDate,
            this.clmnAccepted});
         this.dgvDocs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvDocs.Location = new System.Drawing.Point(0, 0);
         this.dgvDocs.Margin = new System.Windows.Forms.Padding(4);
         this.dgvDocs.Name = "dgvDocs";
         this.dgvDocs.RowHeadersVisible = false;
         this.dgvDocs.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvDocs.Size = new System.Drawing.Size(1054, 305);
         this.dgvDocs.TabIndex = 1;
         this.dgvDocs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvDocs_CellFormatting);
         this.dgvDocs.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvDocs_RowEnter);
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         this.clmnAgent.DataPropertyName = "AgentName";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.Width = 150;
         // 
         // clmnFactory
         // 
         this.clmnFactory.DataPropertyName = "FactoryName";
         this.clmnFactory.HeaderText = "Фабрика";
         this.clmnFactory.Name = "clmnFactory";
         this.clmnFactory.Width = 150;
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "OrgName";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.ReadOnly = true;
         // 
         // clmnAddress
         // 
         this.clmnAddress.DataPropertyName = "Address";
         this.clmnAddress.HeaderText = "Адрес";
         this.clmnAddress.Name = "clmnAddress";
         this.clmnAddress.Width = 150;
         // 
         // clmnDate
         // 
         this.clmnDate.DataPropertyName = "Created";
         dataGridViewCellStyle1.Format = "g";
         dataGridViewCellStyle1.NullValue = null;
         this.clmnDate.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnDate.HeaderText = "Дата";
         this.clmnDate.Name = "clmnDate";
         this.clmnDate.ReadOnly = true;
         // 
         // clmnAccepted
         // 
         this.clmnAccepted.DataPropertyName = "Handled";
         dataGridViewCellStyle2.NullValue = null;
         this.clmnAccepted.DefaultCellStyle = dataGridViewCellStyle2;
         this.clmnAccepted.HeaderText = "Обработан";
         this.clmnAccepted.Name = "clmnAccepted";
         this.clmnAccepted.ReadOnly = true;
         // 
         // lvPhoto
         // 
         this.lvPhoto.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvPhoto.HideSelection = false;
         this.lvPhoto.LargeImageList = this.imPhoto;
         this.lvPhoto.Location = new System.Drawing.Point(0, 0);
         this.lvPhoto.Margin = new System.Windows.Forms.Padding(4);
         this.lvPhoto.Name = "lvPhoto";
         this.lvPhoto.Size = new System.Drawing.Size(529, 305);
         this.lvPhoto.TabIndex = 1;
         this.lvPhoto.UseCompatibleStateImageBehavior = false;
         this.lvPhoto.DoubleClick += new System.EventHandler(this.lvPhoto_DoubleClick_1);
         // 
         // imPhoto
         // 
         this.imPhoto.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imPhoto.ImageSize = new System.Drawing.Size(115, 115);
         this.imPhoto.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpBegin.Location = new System.Drawing.Point(307, 6);
         this.dtpBegin.Margin = new System.Windows.Forms.Padding(4);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(208, 30);
         this.dtpBegin.TabIndex = 8;
         this.dtpBegin.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.dtpEnd.Location = new System.Drawing.Point(569, 6);
         this.dtpEnd.Margin = new System.Windows.Forms.Padding(4);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(206, 30);
         this.dtpEnd.TabIndex = 9;
         this.dtpEnd.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnRefresh,
            this.tsbSave,
            this.tsbPrint,
            this.tsbExport});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1588, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbAgents
         // 
         this.cbAgents.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(265, 39);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel1.Margin = new System.Windows.Forms.Padding(5, 1, 0, 2);
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(21, 36);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Segoe UI", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(230, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(36, 36);
         this.toolStripLabel2.Text = "по";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(220, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
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
         // tsbPrint
         // 
         this.tsbPrint.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbPrint.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_print;
         this.tsbPrint.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbPrint.Name = "tsbPrint";
         this.tsbPrint.Size = new System.Drawing.Size(36, 36);
         this.tsbPrint.Text = "Отчет по возвратам";
         this.tsbPrint.Click += new System.EventHandler(this.tsbPrint_Click);
         // 
         // tsbExport
         // 
         this.tsbExport.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbExport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbExport.Enabled = false;
         this.tsbExport.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_export;
         this.tsbExport.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbExport.Name = "tsbExport";
         this.tsbExport.Size = new System.Drawing.Size(36, 36);
         this.tsbExport.Text = "Экспортиовать документы";
         this.tsbExport.Click += new System.EventHandler(this.tsbExport_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnChecked,
            this.clmItem,
            this.clmnMfrDate,
            this.clmnDoc,
            this.clmnDocDate,
            this.clmnAgentQty,
            this.clmnCause,
            this.clmnSvQty,
            this.clmnSvCause,
            this.clmnRemark});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 0);
         this.dgvItems.Margin = new System.Windows.Forms.Padding(4);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(1588, 506);
         this.dgvItems.TabIndex = 0;
         this.dgvItems.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvItems_CurrentCellDirtyStateChanged);
         this.dgvItems.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvItems_DataError);
         // 
         // clmnChecked
         // 
         this.clmnChecked.DataPropertyName = "IsAccepted";
         this.clmnChecked.HeaderText = "";
         this.clmnChecked.Name = "clmnChecked";
         this.clmnChecked.Width = 35;
         // 
         // clmItem
         // 
         this.clmItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmItem.DataPropertyName = "Item";
         dataGridViewCellStyle3.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
         this.clmItem.DefaultCellStyle = dataGridViewCellStyle3;
         this.clmItem.HeaderText = "Товар";
         this.clmItem.Name = "clmItem";
         // 
         // clmnMfrDate
         // 
         this.clmnMfrDate.DataPropertyName = "MfrDate";
         this.clmnMfrDate.HeaderText = "Дата изготовления";
         this.clmnMfrDate.Name = "clmnMfrDate";
         // 
         // clmnDoc
         // 
         this.clmnDoc.DataPropertyName = "DocNumber";
         this.clmnDoc.HeaderText = "№ накладной";
         this.clmnDoc.Name = "clmnDoc";
         // 
         // clmnDocDate
         // 
         this.clmnDocDate.DataPropertyName = "DocDate";
         dataGridViewCellStyle4.Format = "d";
         dataGridViewCellStyle4.NullValue = null;
         this.clmnDocDate.DefaultCellStyle = dataGridViewCellStyle4;
         this.clmnDocDate.HeaderText = "Дата накладной";
         this.clmnDocDate.Name = "clmnDocDate";
         // 
         // clmnAgentQty
         // 
         this.clmnAgentQty.DataPropertyName = "Qty";
         this.clmnAgentQty.HeaderText = "Кол-во";
         this.clmnAgentQty.Name = "clmnAgentQty";
         // 
         // clmnCause
         // 
         this.clmnCause.DataPropertyName = "Cause";
         this.clmnCause.HeaderText = "Причина";
         this.clmnCause.Name = "clmnCause";
         this.clmnCause.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnCause.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         // 
         // clmnSvQty
         // 
         this.clmnSvQty.DataPropertyName = "QtySV";
         this.clmnSvQty.HeaderText = "Кол-во СВ";
         this.clmnSvQty.Name = "clmnSvQty";
         // 
         // clmnSvCause
         // 
         this.clmnSvCause.DataPropertyName = "CauseSV";
         this.clmnSvCause.HeaderText = "Причина СВ";
         this.clmnSvCause.Name = "clmnSvCause";
         this.clmnSvCause.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnSvCause.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         // 
         // clmnRemark
         // 
         this.clmnRemark.DataPropertyName = "Remark";
         this.clmnRemark.HeaderText = "Примечание";
         this.clmnRemark.Name = "clmnRemark";
         this.clmnRemark.Width = 150;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "OrgName";
         this.dataGridViewTextBoxColumn1.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Created";
         dataGridViewCellStyle5.Format = "g";
         dataGridViewCellStyle5.NullValue = null;
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle5;
         this.dataGridViewTextBoxColumn2.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Handled";
         dataGridViewCellStyle6.NullValue = null;
         this.dataGridViewTextBoxColumn3.DefaultCellStyle = dataGridViewCellStyle6;
         this.dataGridViewTextBoxColumn3.HeaderText = "Обработан";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Name";
         dataGridViewCellStyle7.NullValue = null;
         this.dataGridViewTextBoxColumn4.DefaultCellStyle = dataGridViewCellStyle7;
         this.dataGridViewTextBoxColumn4.HeaderText = "Товар";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Qty";
         dataGridViewCellStyle8.NullValue = null;
         this.dataGridViewTextBoxColumn5.DefaultCellStyle = dataGridViewCellStyle8;
         this.dataGridViewTextBoxColumn5.HeaderText = "Кол-во";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn6.DataPropertyName = "QtySV";
         dataGridViewCellStyle9.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn6.DefaultCellStyle = dataGridViewCellStyle9;
         this.dataGridViewTextBoxColumn6.HeaderText = "Кол-во СВ";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         // 
         // dataGridViewTextBoxColumn7
         // 
         this.dataGridViewTextBoxColumn7.DataPropertyName = "QtySV";
         this.dataGridViewTextBoxColumn7.HeaderText = "Кол-во СВ";
         this.dataGridViewTextBoxColumn7.Name = "dataGridViewTextBoxColumn7";
         // 
         // dataGridViewTextBoxColumn8
         // 
         this.dataGridViewTextBoxColumn8.DataPropertyName = "DocNumber";
         this.dataGridViewTextBoxColumn8.HeaderText = "№ накладной";
         this.dataGridViewTextBoxColumn8.Name = "dataGridViewTextBoxColumn8";
         // 
         // dataGridViewTextBoxColumn9
         // 
         this.dataGridViewTextBoxColumn9.DataPropertyName = "DocDate";
         dataGridViewCellStyle10.Format = "d";
         dataGridViewCellStyle10.NullValue = null;
         this.dataGridViewTextBoxColumn9.DefaultCellStyle = dataGridViewCellStyle10;
         this.dataGridViewTextBoxColumn9.HeaderText = "Дата накладной";
         this.dataGridViewTextBoxColumn9.Name = "dataGridViewTextBoxColumn9";
         // 
         // dataGridViewTextBoxColumn10
         // 
         this.dataGridViewTextBoxColumn10.DataPropertyName = "Qty";
         this.dataGridViewTextBoxColumn10.HeaderText = "Кол-во";
         this.dataGridViewTextBoxColumn10.Name = "dataGridViewTextBoxColumn10";
         // 
         // dataGridViewTextBoxColumn11
         // 
         this.dataGridViewTextBoxColumn11.DataPropertyName = "QtySV";
         this.dataGridViewTextBoxColumn11.HeaderText = "Кол-во СВ";
         this.dataGridViewTextBoxColumn11.Name = "dataGridViewTextBoxColumn11";
         // 
         // dataGridViewTextBoxColumn12
         // 
         this.dataGridViewTextBoxColumn12.HeaderText = "Примечание";
         this.dataGridViewTextBoxColumn12.Name = "dataGridViewTextBoxColumn12";
         this.dataGridViewTextBoxColumn12.Width = 150;
         // 
         // FmReturnRequestList
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1588, 855);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "FmReturnRequestList";
         this.Text = "Список возвратов";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer3.Panel1.ResumeLayout(false);
         this.splitContainer3.Panel2.ResumeLayout(false);
         this.splitContainer3.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvDocs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvDocs;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel1;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel2;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
      private System.Windows.Forms.ImageList imPhoto;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn7;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.SplitContainer splitContainer3;
      private System.Windows.Forms.ListView lvPhoto;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnChecked;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnMfrDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDoc;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDocDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgentQty;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnCause;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSvQty;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnSvCause;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn8;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn9;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn10;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn11;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn12;
      private System.Windows.Forms.ToolStripButton tsbPrint;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFactory;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAccepted;
      private System.Windows.Forms.ToolStripButton tsbExport;

   }
}
namespace GRSoft.Ads
{
   partial class FmClient
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClient));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddClient = new System.Windows.Forms.ToolStripButton();
         this.btnEditClient = new System.Windows.Forms.ToolStripButton();
         this.btnDelClient = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnSearchBack = new System.Windows.Forms.ToolStripButton();
         this.btnSearchForward = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvClient = new System.Windows.Forms.DataGridView();
         this.dgvClientName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvClientAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvContact = new System.Windows.Forms.DataGridView();
         this.dgvContactName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddContact = new System.Windows.Forms.ToolStripButton();
         this.btnEditContact = new System.Windows.Forms.ToolStripButton();
         this.btnDelContact = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFindContact = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindContactPrev = new System.Windows.Forms.ToolStripButton();
         this.btnFindContactNext = new System.Windows.Forms.ToolStripButton();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvClient)).BeginInit();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddClient,
            this.btnEditClient,
            this.btnDelClient,
            this.btnRefresh,
            this.toolStripSeparator3,
            this.tbFind,
            this.btnSearchBack,
            this.btnSearchForward});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(823, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddClient
         // 
         this.btnAddClient.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddClient.Image = ((System.Drawing.Image)(resources.GetObject("btnAddClient.Image")));
         this.btnAddClient.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddClient.Name = "btnAddClient";
         this.btnAddClient.Size = new System.Drawing.Size(23, 22);
         this.btnAddClient.Text = "Добавить";
         this.btnAddClient.Click += new System.EventHandler(this.btnAddClient_Click);
         // 
         // btnEditClient
         // 
         this.btnEditClient.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditClient.Image = ((System.Drawing.Image)(resources.GetObject("btnEditClient.Image")));
         this.btnEditClient.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditClient.Name = "btnEditClient";
         this.btnEditClient.Size = new System.Drawing.Size(23, 22);
         this.btnEditClient.Text = "Изменить";
         this.btnEditClient.Click += new System.EventHandler(this.btnEditClient_Click);
         // 
         // btnDelClient
         // 
         this.btnDelClient.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelClient.Image = ((System.Drawing.Image)(resources.GetObject("btnDelClient.Image")));
         this.btnDelClient.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelClient.Name = "btnDelClient";
         this.btnDelClient.Size = new System.Drawing.Size(23, 22);
         this.btnDelClient.Text = "Удалить";
         this.btnDelClient.Click += new System.EventHandler(this.btnDelClient_Click);
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
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.ToolTipText = "Введите строку для поиска";
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnSearchBack
         // 
         this.btnSearchBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchBack.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchBack.Image")));
         this.btnSearchBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchBack.Name = "btnSearchBack";
         this.btnSearchBack.Size = new System.Drawing.Size(23, 22);
         this.btnSearchBack.Text = "Искать назад";
         this.btnSearchBack.Click += new System.EventHandler(this.btnSearchBack_Click);
         // 
         // btnSearchForward
         // 
         this.btnSearchForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchForward.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchForward.Image")));
         this.btnSearchForward.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchForward.Name = "btnSearchForward";
         this.btnSearchForward.Size = new System.Drawing.Size(23, 22);
         this.btnSearchForward.Text = " Искать вперед";
         this.btnSearchForward.Click += new System.EventHandler(this.btnSearchForward_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvClient);
         this.splitContainer1.Panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.panel1);
         this.splitContainer1.Panel2.Controls.Add(this.statusStrip1);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(823, 504);
         this.splitContainer1.SplitterDistance = 252;
         this.splitContainer1.TabIndex = 3;
         // 
         // dgvClient
         // 
         this.dgvClient.AllowUserToAddRows = false;
         this.dgvClient.AllowUserToDeleteRows = false;
         this.dgvClient.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvClient.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvClientName,
            this.dgvClientAddress});
         this.dgvClient.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvClient.Location = new System.Drawing.Point(7, 8);
         this.dgvClient.Name = "dgvClient";
         this.dgvClient.RowHeadersVisible = false;
         this.dgvClient.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvClient.Size = new System.Drawing.Size(809, 236);
         this.dgvClient.TabIndex = 0;
         this.dgvClient.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvClient_ColumnHeaderMouseClick);
         this.dgvClient.DoubleClick += new System.EventHandler(this.dgvClient_DoubleClick);
         this.dgvClient.SelectionChanged += new System.EventHandler(this.dgvClient_SelectionChanged);
         // 
         // dgvClientName
         // 
         this.dgvClientName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvClientName.DataPropertyName = "Name";
         this.dgvClientName.FillWeight = 45F;
         this.dgvClientName.HeaderText = "Наименование";
         this.dgvClientName.Name = "dgvClientName";
         // 
         // dgvClientAddress
         // 
         this.dgvClientAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvClientAddress.DataPropertyName = "Address";
         this.dgvClientAddress.HeaderText = "Адрес";
         this.dgvClientAddress.Name = "dgvClientAddress";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvContact);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(823, 201);
         this.panel1.TabIndex = 2;
         // 
         // dgvContact
         // 
         this.dgvContact.AllowUserToAddRows = false;
         this.dgvContact.AllowUserToDeleteRows = false;
         this.dgvContact.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContact.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvContactName,
            this.dgvContactPhone});
         this.dgvContact.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvContact.Location = new System.Drawing.Point(7, 8);
         this.dgvContact.Name = "dgvContact";
         this.dgvContact.RowHeadersVisible = false;
         this.dgvContact.Size = new System.Drawing.Size(809, 185);
         this.dgvContact.TabIndex = 0;
         this.dgvContact.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvContact_ColumnHeaderMouseClick);
         // 
         // dgvContactName
         // 
         this.dgvContactName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactName.DataPropertyName = "Name";
         this.dgvContactName.HeaderText = "Имя";
         this.dgvContactName.Name = "dgvContactName";
         // 
         // dgvContactPhone
         // 
         this.dgvContactPhone.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactPhone.DataPropertyName = "Phone";
         this.dgvContactPhone.HeaderText = "Телефон";
         this.dgvContactPhone.Name = "dgvContactPhone";
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 226);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(823, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddContact,
            this.btnEditContact,
            this.btnDelContact,
            this.toolStripSeparator1,
            this.tbFindContact,
            this.btnFindContactPrev,
            this.btnFindContactNext});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(823, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddContact
         // 
         this.btnAddContact.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddContact.Image = ((System.Drawing.Image)(resources.GetObject("btnAddContact.Image")));
         this.btnAddContact.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddContact.Name = "btnAddContact";
         this.btnAddContact.Size = new System.Drawing.Size(23, 22);
         this.btnAddContact.Text = "Добавить";
         this.btnAddContact.Click += new System.EventHandler(this.btnAddContact_Click);
         // 
         // btnEditContact
         // 
         this.btnEditContact.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditContact.Image = ((System.Drawing.Image)(resources.GetObject("btnEditContact.Image")));
         this.btnEditContact.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditContact.Name = "btnEditContact";
         this.btnEditContact.Size = new System.Drawing.Size(23, 22);
         this.btnEditContact.Text = "Изменить";
         this.btnEditContact.Click += new System.EventHandler(this.btnEditContact_Click);
         // 
         // btnDelContact
         // 
         this.btnDelContact.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelContact.Image = ((System.Drawing.Image)(resources.GetObject("btnDelContact.Image")));
         this.btnDelContact.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelContact.Name = "btnDelContact";
         this.btnDelContact.Size = new System.Drawing.Size(23, 22);
         this.btnDelContact.Text = "Удалить";
         this.btnDelContact.Click += new System.EventHandler(this.btnDelContact_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFindContact
         // 
         this.tbFindContact.Name = "tbFindContact";
         this.tbFindContact.Size = new System.Drawing.Size(100, 25);
         this.tbFindContact.ToolTipText = "Введите строку для поиска";
         this.tbFindContact.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFindContact_KeyDown);
         // 
         // btnFindContactPrev
         // 
         this.btnFindContactPrev.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindContactPrev.Image = ((System.Drawing.Image)(resources.GetObject("btnFindContactPrev.Image")));
         this.btnFindContactPrev.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindContactPrev.Name = "btnFindContactPrev";
         this.btnFindContactPrev.Size = new System.Drawing.Size(23, 22);
         this.btnFindContactPrev.Text = "Искать назад";
         this.btnFindContactPrev.Click += new System.EventHandler(this.btnFindContactPrev_Click);
         // 
         // btnFindContactNext
         // 
         this.btnFindContactNext.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindContactNext.Image = ((System.Drawing.Image)(resources.GetObject("btnFindContactNext.Image")));
         this.btnFindContactNext.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindContactNext.Name = "btnFindContactNext";
         this.btnFindContactNext.Size = new System.Drawing.Size(23, 22);
         this.btnFindContactNext.Text = " Искать вперед";
         this.btnFindContactNext.Click += new System.EventHandler(this.btnFindContactNext_Click);
         // 
         // FmClient
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(823, 529);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClient";
         this.Text = "Клиенты";
         this.Load += new System.EventHandler(this.FmClient_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmClient_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvClient)).EndInit();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvClient;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvContact;
      private System.Windows.Forms.ToolStripButton btnAddClient;
      private System.Windows.Forms.ToolStripButton btnEditClient;
      private System.Windows.Forms.ToolStripButton btnDelClient;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAddContact;
      private System.Windows.Forms.ToolStripButton btnEditContact;
      private System.Windows.Forms.ToolStripButton btnDelContact;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvClientName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvClientAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactPhone;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnSearchBack;
      private System.Windows.Forms.ToolStripButton btnSearchForward;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripTextBox tbFindContact;
      private System.Windows.Forms.ToolStripButton btnFindContactPrev;
      private System.Windows.Forms.ToolStripButton btnFindContactNext;

   }
}
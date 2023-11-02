namespace GRSoft.NapoleonManager
{
   partial class FmOrg
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrg));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddRegion = new System.Windows.Forms.ToolStripButton();
         this.btnAddOrg = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnOrgType = new System.Windows.Forms.ToolStripButton();
         this.btnDealer = new System.Windows.Forms.ToolStripButton();
         this.btnAgentOrg = new System.Windows.Forms.ToolStripButton();
         this.btnConvert = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindDown = new System.Windows.Forms.ToolStripButton();
         this.btnFindUp = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.tgvOrg = new GRSoft.UILib.TreeGridView();
         this.Column1 = new GRSoft.UILib.TreeGridColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new GRSoft.UILib.TreeGridColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column8 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvOrg)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddRegion,
            this.btnAddOrg,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.btnOrgType,
            this.btnDealer,
            this.btnAgentOrg,
            this.btnConvert,
            this.tbFind,
            this.btnFindDown,
            this.btnFindUp});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(573, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddRegion
         // 
         this.btnAddRegion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddRegion.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddRegion.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddRegion.Name = "btnAddRegion";
         this.btnAddRegion.Size = new System.Drawing.Size(23, 22);
         this.btnAddRegion.Text = "Добавить район";
         this.btnAddRegion.Click += new System.EventHandler(this.btnAddRegion_Click);
         // 
         // btnAddOrg
         // 
         this.btnAddOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.ca_add;
         this.btnAddOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddOrg.Name = "btnAddOrg";
         this.btnAddOrg.Size = new System.Drawing.Size(23, 22);
         this.btnAddOrg.Text = "Добавить организацию";
         this.btnAddOrg.Click += new System.EventHandler(this.btnAddOrg_Click);
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
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnOrgType
         // 
         this.btnOrgType.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnOrgType.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         this.btnOrgType.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnOrgType.Name = "btnOrgType";
         this.btnOrgType.Size = new System.Drawing.Size(23, 22);
         this.btnOrgType.Text = "Виды торговых точек";
         this.btnOrgType.Click += new System.EventHandler(this.btnOrgType_Click);
         // 
         // btnDealer
         // 
         this.btnDealer.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDealer.Image = global::GRSoft.NapoleonManager.Properties.Resources.visit_doc;
         this.btnDealer.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDealer.Name = "btnDealer";
         this.btnDealer.Size = new System.Drawing.Size(23, 22);
         this.btnDealer.Text = "Оптовики";
         this.btnDealer.Click += new System.EventHandler(this.btnDealer_Click);
         // 
         // btnAgentOrg
         // 
         this.btnAgentOrg.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAgentOrg.Image = global::GRSoft.NapoleonManager.Properties.Resources.abiword_3;
         this.btnAgentOrg.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAgentOrg.Name = "btnAgentOrg";
         this.btnAgentOrg.Size = new System.Drawing.Size(23, 22);
         this.btnAgentOrg.Text = "Организации контрагента";
         this.btnAgentOrg.Click += new System.EventHandler(this.btnAgentOrg_Click);
         // 
         // btnConvert
         // 
         this.btnConvert.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnConvert.Image = ((System.Drawing.Image)(resources.GetObject("btnConvert.Image")));
         this.btnConvert.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnConvert.Name = "btnConvert";
         this.btnConvert.Size = new System.Drawing.Size(23, 22);
         this.btnConvert.Text = "toolStripButton1";
         this.btnConvert.Click += new System.EventHandler(this.btnConvert_Click);
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
         this.btnFindDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.btnFindDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindDown.Name = "btnFindDown";
         this.btnFindDown.Size = new System.Drawing.Size(23, 22);
         this.btnFindDown.Text = "Искать вперед";
         this.btnFindDown.Click += new System.EventHandler(this.btnFindDown_Click);
         // 
         // btnFindUp
         // 
         this.btnFindUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.btnFindUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindUp.Name = "btnFindUp";
         this.btnFindUp.Size = new System.Drawing.Size(23, 22);
         this.btnFindUp.Text = "Искать назад";
         this.btnFindUp.Click += new System.EventHandler(this.btnFindUp_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 299);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(573, 22);
         this.statusStrip1.TabIndex = 4;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // tgvOrg
         // 
         this.tgvOrg.AllowUserToAddRows = false;
         this.tgvOrg.AllowUserToDeleteRows = false;
         this.tgvOrg.AllowUserToResizeRows = false;
         this.tgvOrg.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column5,
            this.Column6,
            this.Column7,
            this.Column8});
         this.tgvOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvOrg.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvOrg.ImageList = null;
         this.tgvOrg.Location = new System.Drawing.Point(0, 25);
         this.tgvOrg.Name = "tgvOrg";
         this.tgvOrg.RowHeadersVisible = false;
         this.tgvOrg.Size = new System.Drawing.Size(573, 274);
         this.tgvOrg.TabIndex = 3;
         this.tgvOrg.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvOrg_CellFormatting);
         this.tgvOrg.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.tgvOrg_ColumnHeaderMouseClick);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DefaultNodeImage = null;
         this.Column1.HeaderText = "Район/Название";
         this.Column1.Name = "Column1";
         this.Column1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.HeaderText = "Адрес";
         this.Column2.Name = "Column2";
         this.Column2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column3
         // 
         this.Column3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column3.HeaderText = "Директор";
         this.Column3.Name = "Column3";
         this.Column3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column4
         // 
         this.Column4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column4.HeaderText = "Тел. директора";
         this.Column4.Name = "Column4";
         this.Column4.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column5
         // 
         this.Column5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column5.DefaultNodeImage = null;
         this.Column5.HeaderText = "Район/Организация";
         this.Column5.Name = "Column5";
         this.Column5.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column6
         // 
         this.Column6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column6.HeaderText = "Адрес";
         this.Column6.Name = "Column6";
         this.Column6.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.Column6.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column7
         // 
         this.Column7.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column7.HeaderText = "ЛПР";
         this.Column7.Name = "Column7";
         this.Column7.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.Column7.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column8
         // 
         this.Column8.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column8.HeaderText = "Телефон";
         this.Column8.Name = "Column8";
         this.Column8.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.Column8.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FmOrg
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(573, 321);
         this.Controls.Add(this.tgvOrg);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrg";
         this.Text = "Организации";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrg_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvOrg)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private GRSoft.UILib.TreeGridColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private GRSoft.UILib.TreeGridView tgvOrg;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnAddRegion;
      private System.Windows.Forms.ToolStripButton btnAddOrg;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnOrgType;
      private System.Windows.Forms.ToolStripButton btnDealer;
      private System.Windows.Forms.ToolStripButton btnAgentOrg;
      private System.Windows.Forms.ToolStripButton btnConvert;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindDown;
      private System.Windows.Forms.ToolStripButton btnFindUp;
      private UILib.TreeGridColumn Column5;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column8;
   }
}
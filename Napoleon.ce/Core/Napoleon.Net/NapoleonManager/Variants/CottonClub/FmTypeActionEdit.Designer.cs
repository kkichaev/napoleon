namespace GRSoft.NapoleonManager
{
   partial class FmTypeActionEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTypeActionEdit));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.lbTypes = new System.Windows.Forms.ListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnAddType = new System.Windows.Forms.ToolStripButton();
         this.btnDelType = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnCheck = new System.Windows.Forms.ToolStripButton();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.dgvQuestItems = new System.Windows.Forms.DataGridView();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddItem = new System.Windows.Forms.ToolStripButton();
         this.btnEditItem = new System.Windows.Forms.ToolStripButton();
         this.btnDelItem = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Quest = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Type = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestItems)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 385);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(626, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.lbTypes);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tbName);
         this.splitContainer1.Panel2.Controls.Add(this.label1);
         this.splitContainer1.Panel2.Controls.Add(this.dgvQuestItems);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(626, 385);
         this.splitContainer1.SplitterDistance = 208;
         this.splitContainer1.TabIndex = 1;
         // 
         // lbTypes
         // 
         this.lbTypes.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbTypes.FormattingEnabled = true;
         this.lbTypes.ItemHeight = 14;
         this.lbTypes.Location = new System.Drawing.Point(0, 25);
         this.lbTypes.Name = "lbTypes";
         this.lbTypes.Size = new System.Drawing.Size(208, 354);
         this.lbTypes.Sorted = true;
         this.lbTypes.TabIndex = 1;
         this.lbTypes.SelectedIndexChanged += new System.EventHandler(this.lbTypes_SelectedIndexChanged);
         this.lbTypes.DoubleClick += new System.EventHandler(this.lbTypes_DoubleClick);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnAddType,
            this.btnDelType,
            this.btnSave,
            this.btnCheck});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(208, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
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
         // btnAddType
         // 
         this.btnAddType.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddType.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddType.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddType.Name = "btnAddType";
         this.btnAddType.Size = new System.Drawing.Size(23, 22);
         this.btnAddType.Text = "Создать";
         this.btnAddType.Click += new System.EventHandler(this.btnAddType_Click);
         // 
         // btnDelType
         // 
         this.btnDelType.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelType.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelType.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelType.Name = "btnDelType";
         this.btnDelType.Size = new System.Drawing.Size(23, 22);
         this.btnDelType.Text = "Удалить";
         this.btnDelType.Click += new System.EventHandler(this.btnDelType_Click);
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
         // btnCheck
         // 
         this.btnCheck.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.actchk;
         this.btnCheck.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCheck.Name = "btnCheck";
         this.btnCheck.Size = new System.Drawing.Size(23, 22);
         this.btnCheck.Text = "Проверить акции";
         this.btnCheck.Click += new System.EventHandler(this.btnCheck_Click);
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(92, 33);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(310, 20);
         this.tbName.TabIndex = 3;
         this.tbName.TextChanged += new System.EventHandler(this.tbName_TextChanged);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(3, 36);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "Наименование";
         // 
         // dgvQuestItems
         // 
         this.dgvQuestItems.AllowUserToAddRows = false;
         this.dgvQuestItems.AllowUserToResizeRows = false;
         this.dgvQuestItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvQuestItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Quest,
            this.Type});
         this.dgvQuestItems.Location = new System.Drawing.Point(0, 59);
         this.dgvQuestItems.Name = "dgvQuestItems";
         this.dgvQuestItems.RowHeadersVisible = false;
         this.dgvQuestItems.Size = new System.Drawing.Size(414, 326);
         this.dgvQuestItems.TabIndex = 1;
         this.dgvQuestItems.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvQuestItems_CellDoubleClick);
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddItem,
            this.btnEditItem,
            this.btnDelItem,
            this.toolStripSeparator1,
            this.btnUp,
            this.btnDown});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(414, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddItem
         // 
         this.btnAddItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAddItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddItem.Name = "btnAddItem";
         this.btnAddItem.Size = new System.Drawing.Size(23, 22);
         this.btnAddItem.Text = "Создать";
         this.btnAddItem.Click += new System.EventHandler(this.btnAddItem_Click);
         // 
         // btnEditItem
         // 
         this.btnEditItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEditItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditItem.Name = "btnEditItem";
         this.btnEditItem.Size = new System.Drawing.Size(23, 22);
         this.btnEditItem.Text = "Изменить";
         this.btnEditItem.Click += new System.EventHandler(this.btnEditItem_Click);
         // 
         // btnDelItem
         // 
         this.btnDelItem.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelItem.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnDelItem.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelItem.Name = "btnDelItem";
         this.btnDelItem.Size = new System.Drawing.Size(23, 22);
         this.btnDelItem.Text = "Удалить";
         this.btnDelItem.Click += new System.EventHandler(this.btnDelItem_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(23, 22);
         this.btnUp.Text = "Вверх";
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(23, 22);
         this.btnDown.Text = "Вниз";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Number";
         this.dataGridViewTextBoxColumn1.FillWeight = 30F;
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Text";
         this.dataGridViewTextBoxColumn2.FillWeight = 50F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Вопрос";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // Quest
         // 
         this.Quest.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Quest.DataPropertyName = "Text";
         this.Quest.HeaderText = "Вопрос";
         this.Quest.Name = "Quest";
         // 
         // Type
         // 
         this.Type.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Type.DataPropertyName = "TypeStr";
         this.Type.FillWeight = 50F;
         this.Type.HeaderText = "Тип";
         this.Type.Name = "Type";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "TypeStr";
         this.dataGridViewTextBoxColumn3.FillWeight = 50F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Тип";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // FmTypeActionEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(626, 407);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTypeActionEdit";
         this.Text = "Редактор типов акций";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmTypeActionEdit_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestItems)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ListBox lbTypes;
      private System.Windows.Forms.DataGridView dgvQuestItems;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnAddType;
      private System.Windows.Forms.ToolStripButton btnDelType;
      private System.Windows.Forms.ToolStripButton btnAddItem;
      private System.Windows.Forms.ToolStripButton btnEditItem;
      private System.Windows.Forms.ToolStripButton btnDelItem;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnDown;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.DataGridViewTextBoxColumn Quest;
      private System.Windows.Forms.DataGridViewTextBoxColumn Type;
      private System.Windows.Forms.ToolStripButton btnCheck;

   }
}
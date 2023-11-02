namespace GRSoft.NapoleonManager
{
   partial class FmContragents
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmContragents));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnLoadOldOrgs = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tbClearFind = new System.Windows.Forms.ToolStripButton();
         this.btnCopy = new System.Windows.Forms.ToolStripButton();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAddres = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFIO = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.toolStripSeparator1,
            this.tsbAdd,
            this.tsbDel,
            this.toolStripSeparator2,
            this.btnLoadOldOrgs,
            this.tbFind,
            this.tbClearFind,
            this.btnCopy});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(828, 25);
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
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
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
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(23, 22);
         this.tsbAdd.Text = "Добавить товар";
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbDel
         // 
         this.tsbDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsbDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDel.Name = "tsbDel";
         this.tsbDel.Size = new System.Drawing.Size(23, 22);
         this.tsbDel.Text = "Удалить";
         this.tsbDel.Visible = false;
         this.tsbDel.Click += new System.EventHandler(this.tsbDel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnLoadOldOrgs
         // 
         this.btnLoadOldOrgs.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnLoadOldOrgs.Image = ((System.Drawing.Image)(resources.GetObject("btnLoadOldOrgs.Image")));
         this.btnLoadOldOrgs.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnLoadOldOrgs.Name = "btnLoadOldOrgs";
         this.btnLoadOldOrgs.Size = new System.Drawing.Size(23, 22);
         this.btnLoadOldOrgs.Text = "Загрузить старых контрагентов";
         this.btnLoadOldOrgs.Visible = false;
         this.btnLoadOldOrgs.Click += new System.EventHandler(this.btnLoadOldOrgs_Click);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(150, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // tbClearFind
         // 
         this.tbClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tbClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbClearFind.Name = "tbClearFind";
         this.tbClearFind.Size = new System.Drawing.Size(23, 22);
         this.tbClearFind.Text = "Очистить поиск";
         this.tbClearFind.Click += new System.EventHandler(this.tbClearFind_Click);
         // 
         // btnCopy
         // 
         this.btnCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCopy.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.btnCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCopy.Name = "btnCopy";
         this.btnCopy.Size = new System.Drawing.Size(23, 22);
         this.btnCopy.Text = "Скопировать в буффер обмена";
         this.btnCopy.Click += new System.EventHandler(this.btnCopy_Click);
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnAddres,
            this.clmnFIO,
            this.clmnPhone,
            this.clmnAgent});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 25);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(828, 476);
         this.dgvOrgs.TabIndex = 1;
         this.dgvOrgs.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvOrgs_DataError);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.HeaderText = "Название";
         this.clmnName.Name = "clmnName";
         // 
         // clmnAddres
         // 
         this.clmnAddres.DataPropertyName = "Address";
         this.clmnAddres.HeaderText = "Адрес";
         this.clmnAddres.Name = "clmnAddres";
         this.clmnAddres.Width = 200;
         // 
         // clmnFIO
         // 
         this.clmnFIO.DataPropertyName = "FIO";
         this.clmnFIO.HeaderText = "Ф.И.О.";
         this.clmnFIO.Name = "clmnFIO";
         this.clmnFIO.Width = 200;
         // 
         // clmnPhone
         // 
         this.clmnPhone.DataPropertyName = "Phone";
         this.clmnPhone.HeaderText = "Телефон";
         this.clmnPhone.Name = "clmnPhone";
         // 
         // clmnAgent
         // 
         this.clmnAgent.DataPropertyName = "Agent";
         this.clmnAgent.HeaderText = "ТП";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.Width = 150;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Название";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Address";
         this.dataGridViewTextBoxColumn2.HeaderText = "Адрес";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.Width = 200;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.DataPropertyName = "FIO";
         this.dataGridViewTextBoxColumn3.HeaderText = "Ф.И.О.";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.Width = 200;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Phone";
         this.dataGridViewTextBoxColumn4.HeaderText = "Телефон";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // FmContragents
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(828, 501);
         this.Controls.Add(this.dgvOrgs);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmContragents";
         this.Text = "Контрагенты";
         this.Load += new System.EventHandler(this.FmContragents_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAddres;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFIO;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPhone;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnAgent;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnLoadOldOrgs;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton tbClearFind;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.ToolStripButton btnCopy;
   }
}
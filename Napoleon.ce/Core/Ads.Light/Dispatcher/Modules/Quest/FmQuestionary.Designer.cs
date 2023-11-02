namespace GRSoft.Ads.Dispatcher
{
   partial class FmQuestionary
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmQuestionary));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnCopy = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnHtmlView = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvQuestion = new System.Windows.Forms.DataGridView();
         this.dgvQuestionNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestionName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestionDescript = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestionFrom = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestionTill = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestion)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnEdit,
            this.btnCopy,
            this.btnDel,
            this.toolStripSeparator2,
            this.btnUp,
            this.btnDown,
            this.toolStripSeparator3,
            this.btnSave,
            this.btnHtmlView});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(639, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = " HTML";
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
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Enabled = false;
         this.btnEdit.Image = ((System.Drawing.Image)(resources.GetObject("btnEdit.Image")));
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnCopy
         // 
         this.btnCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCopy.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.copy;
         this.btnCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCopy.Name = "btnCopy";
         this.btnCopy.Size = new System.Drawing.Size(23, 22);
         this.btnCopy.Text = "Копировать";
         this.btnCopy.Click += new System.EventHandler(this.btnCopy_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.go_up_4;
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(23, 22);
         this.btnUp.Text = "Вверх";
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.go_down_4;
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(23, 22);
         this.btnDown.Text = "Вниз";
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnHtmlView
         // 
         this.btnHtmlView.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnHtmlView.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.htmlView;
         this.btnHtmlView.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnHtmlView.Name = "btnHtmlView";
         this.btnHtmlView.Size = new System.Drawing.Size(23, 22);
         this.btnHtmlView.Text = "Просмотр анкеты в HTML";
         this.btnHtmlView.Click += new System.EventHandler(this.btnHtmlView_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 330);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(639, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvQuestion
         // 
         this.dgvQuestion.AllowUserToAddRows = false;
         this.dgvQuestion.AllowUserToDeleteRows = false;
         this.dgvQuestion.AllowUserToResizeRows = false;
         this.dgvQuestion.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvQuestion.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvQuestionNumber,
            this.dgvQuestionName,
            this.dgvQuestionDescript,
            this.dgvQuestionFrom,
            this.dgvQuestionTill});
         this.dgvQuestion.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvQuestion.Location = new System.Drawing.Point(0, 25);
         this.dgvQuestion.Name = "dgvQuestion";
         this.dgvQuestion.RowHeadersVisible = false;
         this.dgvQuestion.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvQuestion.Size = new System.Drawing.Size(639, 305);
         this.dgvQuestion.TabIndex = 2;
         this.dgvQuestion.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvQuestion_RowEnter);
         // 
         // dgvQuestionNumber
         // 
         this.dgvQuestionNumber.DataPropertyName = "Number";
         this.dgvQuestionNumber.HeaderText = "№";
         this.dgvQuestionNumber.Name = "dgvQuestionNumber";
         this.dgvQuestionNumber.Width = 30;
         // 
         // dgvQuestionName
         // 
         this.dgvQuestionName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestionName.DataPropertyName = "Name";
         this.dgvQuestionName.FillWeight = 30F;
         this.dgvQuestionName.HeaderText = "Наименование";
         this.dgvQuestionName.Name = "dgvQuestionName";
         // 
         // dgvQuestionDescript
         // 
         this.dgvQuestionDescript.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestionDescript.DataPropertyName = "Text";
         this.dgvQuestionDescript.FillWeight = 70F;
         this.dgvQuestionDescript.HeaderText = "Описание";
         this.dgvQuestionDescript.Name = "dgvQuestionDescript";
         // 
         // dgvQuestionFrom
         // 
         this.dgvQuestionFrom.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestionFrom.DataPropertyName = "From";
         this.dgvQuestionFrom.FillWeight = 20F;
         this.dgvQuestionFrom.HeaderText = "начало";
         this.dgvQuestionFrom.Name = "dgvQuestionFrom";
         this.dgvQuestionFrom.Visible = false;
         // 
         // dgvQuestionTill
         // 
         this.dgvQuestionTill.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestionTill.DataPropertyName = "Till";
         this.dgvQuestionTill.FillWeight = 20F;
         this.dgvQuestionTill.HeaderText = "окончание";
         this.dgvQuestionTill.Name = "dgvQuestionTill";
         this.dgvQuestionTill.Visible = false;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "From";
         this.dataGridViewTextBoxColumn2.FillWeight = 20F;
         this.dataGridViewTextBoxColumn2.HeaderText = "начало";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Till";
         this.dataGridViewTextBoxColumn3.FillWeight = 20F;
         this.dataGridViewTextBoxColumn3.HeaderText = "окончание";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "From";
         this.dataGridViewTextBoxColumn4.FillWeight = 20F;
         this.dataGridViewTextBoxColumn4.HeaderText = "начало";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.Visible = false;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.DataPropertyName = "Till";
         this.dataGridViewTextBoxColumn5.FillWeight = 20F;
         this.dataGridViewTextBoxColumn5.HeaderText = "окончание";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         this.dataGridViewTextBoxColumn5.Visible = false;
         // 
         // FmQuestionary
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(639, 352);
         this.Controls.Add(this.dgvQuestion);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmQuestionary";
         this.Text = "Анкеты";
         this.Load += new System.EventHandler(this.FmQuestionary_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmQuestionary_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestion)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvQuestion;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnDown;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestionNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestionName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestionDescript;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestionFrom;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestionTill;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.ToolStripButton btnCopy;
      private System.Windows.Forms.ToolStripButton btnHtmlView;
   }
}
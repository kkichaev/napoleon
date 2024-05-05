namespace GRSoft.NapoleonManager
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
         this.tsLabel = new System.Windows.Forms.ToolStripStatusLabel();
         this.toolStrip1.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestion)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.Font = new System.Drawing.Font("Arial", 12F);
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
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
         this.toolStrip1.Size = new System.Drawing.Size(639, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = " HTML";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(36, 36);
         this.btnRefresh.Text = "Обновить";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "Добавить";
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(36, 36);
         this.btnEdit.Text = "Изменить";
         // 
         // btnCopy
         // 
         this.btnCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnCopy.Image = global::GRSoft.NapoleonManager.Properties.Resources.copy;
         this.btnCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnCopy.Name = "btnCopy";
         this.btnCopy.Size = new System.Drawing.Size(36, 36);
         this.btnCopy.Text = "Копировать";
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "Удалить";
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 39);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(36, 36);
         this.btnUp.Text = "Вверх";
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(36, 36);
         this.btnDown.Text = "Вниз";
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 39);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 36);
         this.btnSave.Text = "Сохранить";
         // 
         // btnHtmlView
         // 
         this.btnHtmlView.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnHtmlView.Image = global::GRSoft.NapoleonManager.Properties.Resources.htmlView;
         this.btnHtmlView.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnHtmlView.Name = "btnHtmlView";
         this.btnHtmlView.Size = new System.Drawing.Size(36, 36);
         this.btnHtmlView.Text = "Просмотр анкеты в HTML";
         this.btnHtmlView.Visible = false;
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsLabel});
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
         this.dgvQuestion.Location = new System.Drawing.Point(0, 39);
         this.dgvQuestion.Name = "dgvQuestion";
         this.dgvQuestion.RowHeadersVisible = false;
         this.dgvQuestion.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvQuestion.Size = new System.Drawing.Size(639, 291);
         this.dgvQuestion.TabIndex = 2;
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
         // tsLabel
         // 
         this.tsLabel.Name = "tsLabel";
         this.tsLabel.Size = new System.Drawing.Size(0, 17);
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
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
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
      private System.Windows.Forms.ToolStripStatusLabel tsLabel;
   }
}
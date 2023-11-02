namespace GRSoft.NapoleonManager
{
   partial class FmQuestEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmQuestEdit));
         this.dgvQuestItem = new System.Windows.Forms.DataGridView();
         this.dgvQuestItemNum = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestItemTheme = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestItemText = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvQuestItemType = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.gbDict = new System.Windows.Forms.GroupBox();
         this.btnDelProd = new System.Windows.Forms.Button();
         this.btnDelCat = new System.Windows.Forms.Button();
         this.btnCat = new System.Windows.Forms.Button();
         this.btnProd = new System.Windows.Forms.Button();
         this.cbProd = new System.Windows.Forms.ComboBox();
         this.lblProd = new System.Windows.Forms.Label();
         this.cbCat = new System.Windows.Forms.ComboBox();
         this.lblCat = new System.Windows.Forms.Label();
         this.gbPeriod = new System.Windows.Forms.GroupBox();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.cbUsePeriod = new System.Windows.Forms.CheckBox();
         this.tbText = new System.Windows.Forms.TextBox();
         this.label4 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.panel3 = new System.Windows.Forms.Panel();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnUp = new System.Windows.Forms.ToolStripButton();
         this.btnDown = new System.Windows.Forms.ToolStripButton();
         this.btnAttach = new System.Windows.Forms.ToolStripButton();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestItem)).BeginInit();
         this.panel1.SuspendLayout();
         this.gbDict.SuspendLayout();
         this.gbPeriod.SuspendLayout();
         this.panel3.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvQuestItem
         // 
         this.dgvQuestItem.AllowUserToAddRows = false;
         this.dgvQuestItem.AllowUserToDeleteRows = false;
         this.dgvQuestItem.AllowUserToResizeRows = false;
         this.dgvQuestItem.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvQuestItem.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvQuestItemNum,
            this.dgvQuestItemTheme,
            this.dgvQuestItemText,
            this.dgvQuestItemType});
         this.dgvQuestItem.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvQuestItem.Location = new System.Drawing.Point(5, 39);
         this.dgvQuestItem.MultiSelect = false;
         this.dgvQuestItem.Name = "dgvQuestItem";
         this.dgvQuestItem.RowHeadersVisible = false;
         this.dgvQuestItem.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvQuestItem.Size = new System.Drawing.Size(733, 245);
         this.dgvQuestItem.TabIndex = 8;
         // 
         // dgvQuestItemNum
         // 
         this.dgvQuestItemNum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestItemNum.DataPropertyName = "Number";
         this.dgvQuestItemNum.FillWeight = 10F;
         this.dgvQuestItemNum.HeaderText = "№";
         this.dgvQuestItemNum.Name = "dgvQuestItemNum";
         // 
         // dgvQuestItemTheme
         // 
         this.dgvQuestItemTheme.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestItemTheme.DataPropertyName = "Id";
         this.dgvQuestItemTheme.HeaderText = "Тема";
         this.dgvQuestItemTheme.Name = "dgvQuestItemTheme";
         // 
         // dgvQuestItemText
         // 
         this.dgvQuestItemText.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestItemText.DataPropertyName = "Text";
         this.dgvQuestItemText.HeaderText = "Вопрос";
         this.dgvQuestItemText.Name = "dgvQuestItemText";
         // 
         // dgvQuestItemType
         // 
         this.dgvQuestItemType.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvQuestItemType.DataPropertyName = "TypeStr";
         this.dgvQuestItemType.FillWeight = 30F;
         this.dgvQuestItemType.HeaderText = "Тип";
         this.dgvQuestItemType.Name = "dgvQuestItemType";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.gbDict);
         this.panel1.Controls.Add(this.gbPeriod);
         this.panel1.Controls.Add(this.cbUsePeriod);
         this.panel1.Controls.Add(this.tbText);
         this.panel1.Controls.Add(this.label4);
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(747, 141);
         this.panel1.TabIndex = 9;
         // 
         // gbDict
         // 
         this.gbDict.Controls.Add(this.btnDelProd);
         this.gbDict.Controls.Add(this.btnDelCat);
         this.gbDict.Controls.Add(this.btnCat);
         this.gbDict.Controls.Add(this.btnProd);
         this.gbDict.Controls.Add(this.cbProd);
         this.gbDict.Controls.Add(this.lblProd);
         this.gbDict.Controls.Add(this.cbCat);
         this.gbDict.Controls.Add(this.lblCat);
         this.gbDict.Location = new System.Drawing.Point(551, 7);
         this.gbDict.Name = "gbDict";
         this.gbDict.Size = new System.Drawing.Size(191, 125);
         this.gbDict.TabIndex = 19;
         this.gbDict.TabStop = false;
         this.gbDict.Text = "Справочники";
         // 
         // btnDelProd
         // 
         this.btnDelProd.Location = new System.Drawing.Point(126, 65);
         this.btnDelProd.Name = "btnDelProd";
         this.btnDelProd.Size = new System.Drawing.Size(26, 23);
         this.btnDelProd.TabIndex = 7;
         this.btnDelProd.Text = "X";
         this.btnDelProd.UseVisualStyleBackColor = true;
         this.btnDelProd.Click += new System.EventHandler(this.btnDelProd_Click);
         // 
         // btnDelCat
         // 
         this.btnDelCat.Location = new System.Drawing.Point(126, 9);
         this.btnDelCat.Name = "btnDelCat";
         this.btnDelCat.Size = new System.Drawing.Size(26, 23);
         this.btnDelCat.TabIndex = 6;
         this.btnDelCat.Text = "X";
         this.btnDelCat.UseVisualStyleBackColor = true;
         this.btnDelCat.Click += new System.EventHandler(this.btnDelCat_Click);
         // 
         // btnCat
         // 
         this.btnCat.Location = new System.Drawing.Point(158, 9);
         this.btnCat.Name = "btnCat";
         this.btnCat.Size = new System.Drawing.Size(26, 23);
         this.btnCat.TabIndex = 5;
         this.btnCat.Text = "...";
         this.btnCat.UseVisualStyleBackColor = true;
         this.btnCat.Click += new System.EventHandler(this.btnCat_Click);
         // 
         // btnProd
         // 
         this.btnProd.Location = new System.Drawing.Point(158, 65);
         this.btnProd.Name = "btnProd";
         this.btnProd.Size = new System.Drawing.Size(26, 23);
         this.btnProd.TabIndex = 4;
         this.btnProd.Text = "...";
         this.btnProd.UseVisualStyleBackColor = true;
         this.btnProd.Click += new System.EventHandler(this.btnProd_Click);
         // 
         // cbProd
         // 
         this.cbProd.FormattingEnabled = true;
         this.cbProd.Location = new System.Drawing.Point(10, 91);
         this.cbProd.Name = "cbProd";
         this.cbProd.Size = new System.Drawing.Size(174, 22);
         this.cbProd.TabIndex = 3;
         // 
         // lblProd
         // 
         this.lblProd.AutoSize = true;
         this.lblProd.Location = new System.Drawing.Point(7, 74);
         this.lblProd.Name = "lblProd";
         this.lblProd.Size = new System.Drawing.Size(86, 14);
         this.lblProd.TabIndex = 2;
         this.lblProd.Text = "Прозиводитель";
         // 
         // cbCat
         // 
         this.cbCat.FormattingEnabled = true;
         this.cbCat.Location = new System.Drawing.Point(10, 38);
         this.cbCat.Name = "cbCat";
         this.cbCat.Size = new System.Drawing.Size(174, 22);
         this.cbCat.TabIndex = 1;
         // 
         // lblCat
         // 
         this.lblCat.AutoSize = true;
         this.lblCat.Location = new System.Drawing.Point(7, 20);
         this.lblCat.Name = "lblCat";
         this.lblCat.Size = new System.Drawing.Size(60, 14);
         this.lblCat.TabIndex = 0;
         this.lblCat.Text = "Категории";
         // 
         // gbPeriod
         // 
         this.gbPeriod.Controls.Add(this.dtpTill);
         this.gbPeriod.Controls.Add(this.label2);
         this.gbPeriod.Controls.Add(this.label3);
         this.gbPeriod.Controls.Add(this.dtpFrom);
         this.gbPeriod.Location = new System.Drawing.Point(341, 35);
         this.gbPeriod.Name = "gbPeriod";
         this.gbPeriod.Size = new System.Drawing.Size(200, 100);
         this.gbPeriod.TabIndex = 18;
         this.gbPeriod.TabStop = false;
         this.gbPeriod.Text = "Период";
         this.gbPeriod.Visible = false;
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(37, 65);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(132, 20);
         this.dtpTill.TabIndex = 13;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(14, 29);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(13, 14);
         this.label2.TabIndex = 10;
         this.label2.Text = "c";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(14, 68);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 11;
         this.label3.Text = "по";
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(35, 27);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(132, 20);
         this.dtpFrom.TabIndex = 12;
         // 
         // cbUsePeriod
         // 
         this.cbUsePeriod.AutoSize = true;
         this.cbUsePeriod.Location = new System.Drawing.Point(341, 7);
         this.cbUsePeriod.Name = "cbUsePeriod";
         this.cbUsePeriod.Size = new System.Drawing.Size(102, 18);
         this.cbUsePeriod.TabIndex = 17;
         this.cbUsePeriod.Text = "Задать период";
         this.cbUsePeriod.UseVisualStyleBackColor = true;
         this.cbUsePeriod.Visible = false;
         this.cbUsePeriod.CheckedChanged += new System.EventHandler(this.cbPeriod_CheckedChanged);
         // 
         // tbText
         // 
         this.tbText.Location = new System.Drawing.Point(6, 53);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(311, 80);
         this.tbText.TabIndex = 15;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(6, 36);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(57, 14);
         this.label4.TabIndex = 14;
         this.label4.Text = "Описание";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(70, 5);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(246, 20);
         this.tbName.TabIndex = 9;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(56, 14);
         this.label1.TabIndex = 8;
         this.label1.Text = "Название";
         // 
         // panel3
         // 
         this.panel3.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel3.Controls.Add(this.dgvQuestItem);
         this.panel3.Controls.Add(this.toolStrip1);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel3.Location = new System.Drawing.Point(0, 141);
         this.panel3.Name = "panel3";
         this.panel3.Padding = new System.Windows.Forms.Padding(5, 0, 5, 0);
         this.panel3.Size = new System.Drawing.Size(747, 288);
         this.panel3.TabIndex = 16;
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.toolStripSeparator1,
            this.btnUp,
            this.btnDown,
            this.btnAttach});
         this.toolStrip1.Location = new System.Drawing.Point(5, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(733, 39);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(36, 36);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(36, 36);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(36, 36);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 39);
         // 
         // btnUp
         // 
         this.btnUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.btnUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(36, 36);
         this.btnUp.Text = "Вверх";
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // btnDown
         // 
         this.btnDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.btnDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(36, 36);
         this.btnDown.Text = "Вниз";
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // btnAttach
         // 
         this.btnAttach.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAttach.Image = global::GRSoft.NapoleonManager.Properties.Resources.attachment;
         this.btnAttach.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAttach.Name = "btnAttach";
         this.btnAttach.Size = new System.Drawing.Size(36, 36);
         this.btnAttach.Text = "Вложение";
         this.btnAttach.Visible = false;
         this.btnAttach.Click += new System.EventHandler(this.btnAttach_Click);
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 429);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(747, 40);
         this.panel2.TabIndex = 10;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(571, 10);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(660, 10);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Number";
         this.dataGridViewTextBoxColumn1.FillWeight = 10F;
         this.dataGridViewTextBoxColumn1.HeaderText = "№";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Text";
         this.dataGridViewTextBoxColumn2.HeaderText = "Текст";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "TypeStr";
         this.dataGridViewTextBoxColumn3.FillWeight = 30F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Тип";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "TypeStr";
         this.dataGridViewTextBoxColumn4.FillWeight = 30F;
         this.dataGridViewTextBoxColumn4.HeaderText = "Тип";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // FmQuestEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(747, 469);
         this.Controls.Add(this.panel3);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmQuestEdit";
         this.Text = "FmQuestEdit";
         ((System.ComponentModel.ISupportInitialize)(this.dgvQuestItem)).EndInit();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.gbDict.ResumeLayout(false);
         this.gbDict.PerformLayout();
         this.gbPeriod.ResumeLayout(false);
         this.gbPeriod.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvQuestItem;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.CheckBox cbUsePeriod;
      private System.Windows.Forms.GroupBox gbPeriod;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestItemNum;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestItemTheme;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestItemText;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvQuestItemType;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnUp;
      private System.Windows.Forms.ToolStripButton btnDown;
      private System.Windows.Forms.GroupBox gbDict;
      private System.Windows.Forms.ComboBox cbCat;
      private System.Windows.Forms.Label lblCat;
      private System.Windows.Forms.Label lblProd;
      private System.Windows.Forms.ComboBox cbProd;
      private System.Windows.Forms.Button btnProd;
      private System.Windows.Forms.Button btnCat;
      private System.Windows.Forms.Button btnDelProd;
      private System.Windows.Forms.Button btnDelCat;
      private System.Windows.Forms.ToolStripButton btnAttach;
   }
}
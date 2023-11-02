namespace GRSoft.NapoleonManager
{
   partial class FmPlanEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPlanEdit));
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.panel2 = new System.Windows.Forms.Panel();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvPlanItems = new System.Windows.Forms.DataGridView();
         this.dgvPlanItemsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPlanItemsUnit = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPlanItemsQuant = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPlanItemsText = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.panel3 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOk = new System.Windows.Forms.Button();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel2.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlanItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.panel3.SuspendLayout();
         this.SuspendLayout();
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(191, 2);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(135, 20);
         this.dtpTill.TabIndex = 5;
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(26, 2);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(130, 20);
         this.dtpFrom.TabIndex = 4;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.panel1);
         this.panel2.Controls.Add(this.dtpTill);
         this.panel2.Controls.Add(this.dtpFrom);
         this.panel2.Controls.Add(this.toolStrip1);
         this.panel2.Controls.Add(this.panel3);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(691, 476);
         this.panel2.TabIndex = 2;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvPlanItems);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(691, 418);
         this.panel1.TabIndex = 7;
         // 
         // dgvPlanItems
         // 
         this.dgvPlanItems.AllowUserToAddRows = false;
         this.dgvPlanItems.AllowUserToDeleteRows = false;
         this.dgvPlanItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPlanItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvPlanItemsName,
            this.dgvPlanItemsUnit,
            this.dgvPlanItemsQuant,
            this.dgvPlanItemsText});
         this.dgvPlanItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvPlanItems.Location = new System.Drawing.Point(7, 7);
         this.dgvPlanItems.MultiSelect = false;
         this.dgvPlanItems.Name = "dgvPlanItems";
         this.dgvPlanItems.ReadOnly = true;
         this.dgvPlanItems.RowHeadersVisible = false;
         this.dgvPlanItems.Size = new System.Drawing.Size(677, 404);
         this.dgvPlanItems.TabIndex = 6;
         // 
         // dgvPlanItemsName
         // 
         this.dgvPlanItemsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPlanItemsName.DataPropertyName = "PlanName";
         this.dgvPlanItemsName.FillWeight = 200F;
         this.dgvPlanItemsName.HeaderText = "Наименование";
         this.dgvPlanItemsName.Name = "dgvPlanItemsName";
         this.dgvPlanItemsName.ReadOnly = true;
         // 
         // dgvPlanItemsUnit
         // 
         this.dgvPlanItemsUnit.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPlanItemsUnit.DataPropertyName = "UnitName";
         this.dgvPlanItemsUnit.FillWeight = 70F;
         this.dgvPlanItemsUnit.HeaderText = "Ед. изм.";
         this.dgvPlanItemsUnit.Name = "dgvPlanItemsUnit";
         this.dgvPlanItemsUnit.ReadOnly = true;
         // 
         // dgvPlanItemsQuant
         // 
         this.dgvPlanItemsQuant.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPlanItemsQuant.DataPropertyName = "Value";
         this.dgvPlanItemsQuant.FillWeight = 70F;
         this.dgvPlanItemsQuant.HeaderText = "Кол-во";
         this.dgvPlanItemsQuant.Name = "dgvPlanItemsQuant";
         this.dgvPlanItemsQuant.ReadOnly = true;
         // 
         // dgvPlanItemsText
         // 
         this.dgvPlanItemsText.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPlanItemsText.DataPropertyName = "Text";
         this.dgvPlanItemsText.FillWeight = 150F;
         this.dgvPlanItemsText.HeaderText = "Текст";
         this.dgvPlanItemsText.Name = "dgvPlanItemsText";
         this.dgvPlanItemsText.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnEdit,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(691, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(12, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(145, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(19, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(150, 0, 0, 0);
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
         this.btnEdit.Image = ((System.Drawing.Image)(resources.GetObject("btnEdit.Image")));
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
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
         // panel3
         // 
         this.panel3.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel3.Controls.Add(this.btnCancel);
         this.panel3.Controls.Add(this.btnOk);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel3.Location = new System.Drawing.Point(0, 443);
         this.panel3.Name = "panel3";
         this.panel3.Padding = new System.Windows.Forms.Padding(7);
         this.panel3.Size = new System.Drawing.Size(691, 33);
         this.panel3.TabIndex = 2;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(604, 4);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOk
         // 
         this.btnOk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOk.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOk.Location = new System.Drawing.Point(523, 4);
         this.btnOk.Name = "btnOk";
         this.btnOk.Size = new System.Drawing.Size(75, 23);
         this.btnOk.TabIndex = 0;
         this.btnOk.Text = "ОК";
         this.btnOk.UseVisualStyleBackColor = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Unit.Name";
         this.dataGridViewTextBoxColumn1.FillWeight = 50F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         this.dataGridViewTextBoxColumn1.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "UnitName";
         this.dataGridViewTextBoxColumn2.FillWeight = 50F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Количество";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         this.dataGridViewTextBoxColumn2.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.dataGridViewTextBoxColumn2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "Quant";
         this.dataGridViewTextBoxColumn3.FillWeight = 20F;
         this.dataGridViewTextBoxColumn3.HeaderText = "Содержание";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         this.dataGridViewTextBoxColumn3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Text";
         this.dataGridViewTextBoxColumn4.FillWeight = 150F;
         this.dataGridViewTextBoxColumn4.HeaderText = "Содержание";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // FmPlanEdit
         // 
         this.AcceptButton = this.btnOk;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(691, 476);
         this.ControlBox = false;
         this.Controls.Add(this.panel2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPlanEdit";
         this.Text = "FmPlanEdit";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPlanEdit_FormClosing);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPlanItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOk;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridView dgvPlanItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPlanItemsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPlanItemsUnit;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPlanItemsQuant;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPlanItemsText;
   }
}
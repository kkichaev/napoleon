namespace GRSoft.NapoleonManager
{
   partial class FmSchool
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSchool));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbLocality = new System.Windows.Forms.ToolStripComboBox();
         this.btnLocality = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvSchool = new System.Windows.Forms.DataGridView();
         this.dgvSchoolNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvSchoolAdress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchool)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbLocality,
            this.btnLocality,
            this.toolStripSeparator1,
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(661, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbLocality
         // 
         this.cbLocality.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbLocality.Name = "cbLocality";
         this.cbLocality.Size = new System.Drawing.Size(121, 25);
         this.cbLocality.SelectedIndexChanged += new System.EventHandler(this.cbLocality_SelectedIndexChanged);
         // 
         // btnLocality
         // 
         this.btnLocality.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnLocality.Image = ((System.Drawing.Image)(resources.GetObject("btnLocality.Image")));
         this.btnLocality.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnLocality.Name = "btnLocality";
         this.btnLocality.Size = new System.Drawing.Size(23, 22);
         this.btnLocality.Text = "Город";
         this.btnLocality.Click += new System.EventHandler(this.btnLocality_Click);
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
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 338);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(661, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvSchool);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(661, 313);
         this.panel1.TabIndex = 2;
         // 
         // dgvSchool
         // 
         this.dgvSchool.AllowUserToAddRows = false;
         this.dgvSchool.AllowUserToDeleteRows = false;
         this.dgvSchool.AllowUserToResizeRows = false;
         this.dgvSchool.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSchool.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvSchoolNumber,
            this.dgvSchoolAdress});
         this.dgvSchool.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvSchool.Location = new System.Drawing.Point(7, 7);
         this.dgvSchool.Name = "dgvSchool";
         this.dgvSchool.ReadOnly = true;
         this.dgvSchool.RowHeadersVisible = false;
         this.dgvSchool.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvSchool.Size = new System.Drawing.Size(647, 299);
         this.dgvSchool.TabIndex = 0;
         this.dgvSchool.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.dgvSchool_MouseDoubleClick);
         // 
         // dgvSchoolNumber
         // 
         this.dgvSchoolNumber.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSchoolNumber.FillWeight = 10F;
         this.dgvSchoolNumber.HeaderText = "Номер";
         this.dgvSchoolNumber.Name = "dgvSchoolNumber";
         this.dgvSchoolNumber.ReadOnly = true;
         // 
         // dgvSchoolAdress
         // 
         this.dgvSchoolAdress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvSchoolAdress.FillWeight = 90F;
         this.dgvSchoolAdress.HeaderText = "Адрес";
         this.dgvSchoolAdress.Name = "dgvSchoolAdress";
         this.dgvSchoolAdress.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.FillWeight = 10F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Номер";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.FillWeight = 90F;
         this.dataGridViewTextBoxColumn2.HeaderText = "Адрес";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // FmSchool
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(661, 360);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Name = "FmSchool";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Школа";
         this.Load += new System.EventHandler(this.FmSchool_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSchool_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchool)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvSchool;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSchoolNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvSchoolAdress;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripComboBox cbLocality;
      private System.Windows.Forms.ToolStripButton btnLocality;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.ToolStripButton btnRefresh;
   }
}
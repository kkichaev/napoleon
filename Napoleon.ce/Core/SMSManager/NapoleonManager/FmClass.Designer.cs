namespace GRSoft.NapoleonManager
{
   partial class FmClass
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClass));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbLocality = new System.Windows.Forms.ToolStripComboBox();
         this.btnLocality = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.cbSchool = new System.Windows.Forms.ToolStripComboBox();
         this.btnShool = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvClass = new System.Windows.Forms.DataGridView();
         this.dgvClassNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvClassContacts = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvClass)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 392);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(571, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbLocality,
            this.btnLocality,
            this.toolStripSeparator1,
            this.cbSchool,
            this.btnShool,
            this.toolStripSeparator2,
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(571, 25);
         this.toolStrip1.TabIndex = 1;
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
         // cbSchool
         // 
         this.cbSchool.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbSchool.Name = "cbSchool";
         this.cbSchool.Size = new System.Drawing.Size(121, 25);
         this.cbSchool.SelectedIndexChanged += new System.EventHandler(this.cbShool_SelectedIndexChanged);
         // 
         // btnShool
         // 
         this.btnShool.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnShool.Image = ((System.Drawing.Image)(resources.GetObject("btnShool.Image")));
         this.btnShool.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnShool.Name = "btnShool";
         this.btnShool.Size = new System.Drawing.Size(23, 22);
         this.btnShool.Text = "Шкла";
         this.btnShool.Click += new System.EventHandler(this.btnShool_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
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
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvClass);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(571, 367);
         this.panel1.TabIndex = 2;
         // 
         // dgvClass
         // 
         this.dgvClass.AllowUserToAddRows = false;
         this.dgvClass.AllowUserToDeleteRows = false;
         this.dgvClass.AllowUserToResizeRows = false;
         this.dgvClass.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvClass.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvClassNumber,
            this.dgvClassContacts});
         this.dgvClass.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvClass.Location = new System.Drawing.Point(7, 7);
         this.dgvClass.Name = "dgvClass";
         this.dgvClass.ReadOnly = true;
         this.dgvClass.RowHeadersVisible = false;
         this.dgvClass.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvClass.Size = new System.Drawing.Size(557, 353);
         this.dgvClass.TabIndex = 0;
         this.dgvClass.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvClass_MouseDown);
         // 
         // dgvClassNumber
         // 
         this.dgvClassNumber.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvClassNumber.FillWeight = 30F;
         this.dgvClassNumber.HeaderText = "Номер";
         this.dgvClassNumber.Name = "dgvClassNumber";
         this.dgvClassNumber.ReadOnly = true;
         // 
         // dgvClassContacts
         // 
         this.dgvClassContacts.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvClassContacts.HeaderText = "Контакты";
         this.dgvClassContacts.Name = "dgvClassContacts";
         this.dgvClassContacts.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.FillWeight = 30F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Номер";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Контакты";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // FmClass
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(571, 414);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmClass";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Классы";
         this.Load += new System.EventHandler(this.FmClass_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmClass_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvClass)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvClass;
      private System.Windows.Forms.ToolStripComboBox cbLocality;
      private System.Windows.Forms.ToolStripButton btnLocality;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripComboBox cbSchool;
      private System.Windows.Forms.ToolStripButton btnShool;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvClassNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvClassContacts;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
   }
}
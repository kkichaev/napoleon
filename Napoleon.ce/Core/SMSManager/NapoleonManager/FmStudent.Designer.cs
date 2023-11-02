namespace GRSoft.NapoleonManager
{
   partial class FmStudent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmStudent));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbLocality = new System.Windows.Forms.ToolStripComboBox();
         this.btnLocality = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.cbSchool = new System.Windows.Forms.ToolStripComboBox();
         this.btnSchool = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.cbClass = new System.Windows.Forms.ToolStripComboBox();
         this.btnClass = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvStudent = new System.Windows.Forms.DataGridView();
         this.dgvStudentName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 441);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(590, 22);
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
            this.btnSchool,
            this.toolStripSeparator2,
            this.cbClass,
            this.btnClass,
            this.toolStripSeparator3,
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(590, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "Добавить";
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
         this.cbSchool.SelectedIndexChanged += new System.EventHandler(this.cbSchool_SelectedIndexChanged);
         // 
         // btnSchool
         // 
         this.btnSchool.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSchool.Image = ((System.Drawing.Image)(resources.GetObject("btnSchool.Image")));
         this.btnSchool.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSchool.Name = "btnSchool";
         this.btnSchool.Size = new System.Drawing.Size(23, 22);
         this.btnSchool.Text = "Школа";
         this.btnSchool.Click += new System.EventHandler(this.btnSchool_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // cbClass
         // 
         this.cbClass.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbClass.Name = "cbClass";
         this.cbClass.Size = new System.Drawing.Size(121, 25);
         this.cbClass.SelectedIndexChanged += new System.EventHandler(this.cbClass_SelectedIndexChanged);
         // 
         // btnClass
         // 
         this.btnClass.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClass.Image = ((System.Drawing.Image)(resources.GetObject("btnClass.Image")));
         this.btnClass.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClass.Name = "btnClass";
         this.btnClass.Size = new System.Drawing.Size(23, 22);
         this.btnClass.Text = "Класс";
         this.btnClass.Click += new System.EventHandler(this.btnClass_Click);
         // 
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
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
         this.panel1.Controls.Add(this.dgvStudent);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(590, 416);
         this.panel1.TabIndex = 2;
         // 
         // dgvStudent
         // 
         this.dgvStudent.AllowUserToAddRows = false;
         this.dgvStudent.AllowUserToDeleteRows = false;
         this.dgvStudent.AllowUserToResizeRows = false;
         this.dgvStudent.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvStudent.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvStudentName});
         this.dgvStudent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvStudent.Location = new System.Drawing.Point(7, 7);
         this.dgvStudent.Name = "dgvStudent";
         this.dgvStudent.ReadOnly = true;
         this.dgvStudent.RowHeadersVisible = false;
         this.dgvStudent.Size = new System.Drawing.Size(576, 402);
         this.dgvStudent.TabIndex = 0;
         this.dgvStudent.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvStudent_MouseDown);
         // 
         // dgvStudentName
         // 
         this.dgvStudentName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStudentName.HeaderText = "Имя";
         this.dgvStudentName.Name = "dgvStudentName";
         this.dgvStudentName.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // FmStudent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(590, 463);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmStudent";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Ученики";
         this.Load += new System.EventHandler(this.FmStudent_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmStudent_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvStudent;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStudentName;
      private System.Windows.Forms.ToolStripComboBox cbLocality;
      private System.Windows.Forms.ToolStripButton btnLocality;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripComboBox cbSchool;
      private System.Windows.Forms.ToolStripButton btnSchool;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.ToolStripComboBox cbClass;
      private System.Windows.Forms.ToolStripButton btnClass;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
   }
}
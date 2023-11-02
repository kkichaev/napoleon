namespace GRSoft.NapoleonManager
{
   partial class FmDogovor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDogovor));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvDogovor = new System.Windows.Forms.DataGridView();
         this.dgvDogovorNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDogovorBegin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDogovorEnd = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvDogovorType = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.label1 = new System.Windows.Forms.Label();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.dgvStudent = new System.Windows.Forms.DataGridView();
         this.dgvStudentName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnAddStudent = new System.Windows.Forms.ToolStripButton();
         this.btnDelStudent = new System.Windows.Forms.ToolStripButton();
         this.panel2 = new System.Windows.Forms.Panel();
         this.label2 = new System.Windows.Forms.Label();
         this.dgvParent = new System.Windows.Forms.DataGridView();
         this.gvdParentName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip3 = new System.Windows.Forms.ToolStrip();
         this.btnAddParent = new System.Windows.Forms.ToolStripButton();
         this.btnDelParent = new System.Windows.Forms.ToolStripButton();
         this.panel3 = new System.Windows.Forms.Panel();
         this.label3 = new System.Windows.Forms.Label();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddDogovor = new System.Windows.Forms.ToolStripButton();
         this.btnEditDogovor = new System.Windows.Forms.ToolStripButton();
         this.btnDeleteDogovor = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel4 = new System.Windows.Forms.Panel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvDogovor)).BeginInit();
         this.panel1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvParent)).BeginInit();
         this.toolStrip3.SuspendLayout();
         this.panel3.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.panel4.SuspendLayout();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 438);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(863, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(7, 7);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvDogovor);
         this.splitContainer1.Panel1.Controls.Add(this.panel1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(849, 399);
         this.splitContainer1.SplitterDistance = 515;
         this.splitContainer1.TabIndex = 1;
         // 
         // dgvDogovor
         // 
         this.dgvDogovor.AllowUserToAddRows = false;
         this.dgvDogovor.AllowUserToResizeRows = false;
         this.dgvDogovor.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvDogovor.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvDogovorNumber,
            this.dgvDogovorBegin,
            this.dgvDogovorEnd,
            this.dgvDogovorType});
         this.dgvDogovor.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvDogovor.Location = new System.Drawing.Point(0, 26);
         this.dgvDogovor.Name = "dgvDogovor";
         this.dgvDogovor.ReadOnly = true;
         this.dgvDogovor.RowHeadersVisible = false;
         this.dgvDogovor.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvDogovor.Size = new System.Drawing.Size(515, 373);
         this.dgvDogovor.TabIndex = 1;
         this.dgvDogovor.DoubleClick += new System.EventHandler(this.dgvDogovor_DoubleClick);
         this.dgvDogovor.SelectionChanged += new System.EventHandler(this.dgvDogovor_SelectionChanged);
         // 
         // dgvDogovorNumber
         // 
         this.dgvDogovorNumber.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDogovorNumber.HeaderText = "Номер";
         this.dgvDogovorNumber.Name = "dgvDogovorNumber";
         this.dgvDogovorNumber.ReadOnly = true;
         // 
         // dgvDogovorBegin
         // 
         this.dgvDogovorBegin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDogovorBegin.HeaderText = "Начало";
         this.dgvDogovorBegin.Name = "dgvDogovorBegin";
         this.dgvDogovorBegin.ReadOnly = true;
         // 
         // dgvDogovorEnd
         // 
         this.dgvDogovorEnd.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDogovorEnd.HeaderText = "Окончание";
         this.dgvDogovorEnd.Name = "dgvDogovorEnd";
         this.dgvDogovorEnd.ReadOnly = true;
         // 
         // dgvDogovorType
         // 
         this.dgvDogovorType.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvDogovorType.HeaderText = "Тип";
         this.dgvDogovorType.Name = "dgvDogovorType";
         this.dgvDogovorType.ReadOnly = true;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(515, 26);
         this.panel1.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(3, 5);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(59, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Договоры";
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         this.splitContainer2.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.dgvStudent);
         this.splitContainer2.Panel1.Controls.Add(this.toolStrip2);
         this.splitContainer2.Panel1.Controls.Add(this.panel2);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.dgvParent);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip3);
         this.splitContainer2.Panel2.Controls.Add(this.panel3);
         this.splitContainer2.Size = new System.Drawing.Size(330, 399);
         this.splitContainer2.SplitterDistance = 187;
         this.splitContainer2.TabIndex = 0;
         // 
         // dgvStudent
         // 
         this.dgvStudent.AllowDrop = true;
         this.dgvStudent.AllowUserToAddRows = false;
         this.dgvStudent.AllowUserToDeleteRows = false;
         this.dgvStudent.AllowUserToResizeRows = false;
         this.dgvStudent.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvStudent.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvStudentName});
         this.dgvStudent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvStudent.Location = new System.Drawing.Point(0, 51);
         this.dgvStudent.Name = "dgvStudent";
         this.dgvStudent.ReadOnly = true;
         this.dgvStudent.RowHeadersVisible = false;
         this.dgvStudent.Size = new System.Drawing.Size(330, 136);
         this.dgvStudent.TabIndex = 2;
         this.dgvStudent.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvStudents_DragEnter);
         this.dgvStudent.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvStudents_DragDrop);
         // 
         // dgvStudentName
         // 
         this.dgvStudentName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStudentName.HeaderText = "Имя";
         this.dgvStudentName.Name = "dgvStudentName";
         this.dgvStudentName.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddStudent,
            this.btnDelStudent});
         this.toolStrip2.Location = new System.Drawing.Point(0, 26);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(330, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnAddStudent
         // 
         this.btnAddStudent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddStudent.Image = ((System.Drawing.Image)(resources.GetObject("btnAddStudent.Image")));
         this.btnAddStudent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddStudent.Name = "btnAddStudent";
         this.btnAddStudent.Size = new System.Drawing.Size(23, 22);
         this.btnAddStudent.Text = "Добавиь";
         this.btnAddStudent.Click += new System.EventHandler(this.btnStudent_Click);
         // 
         // btnDelStudent
         // 
         this.btnDelStudent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelStudent.Image = ((System.Drawing.Image)(resources.GetObject("btnDelStudent.Image")));
         this.btnDelStudent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelStudent.Name = "btnDelStudent";
         this.btnDelStudent.Size = new System.Drawing.Size(23, 22);
         this.btnDelStudent.Text = "Удалить";
         this.btnDelStudent.Click += new System.EventHandler(this.btnDelStudent_Click);
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.label2);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(330, 26);
         this.panel2.TabIndex = 0;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(3, 5);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(50, 13);
         this.label2.TabIndex = 0;
         this.label2.Text = "Ученики";
         // 
         // dgvParent
         // 
         this.dgvParent.AllowDrop = true;
         this.dgvParent.AllowUserToAddRows = false;
         this.dgvParent.AllowUserToResizeRows = false;
         this.dgvParent.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvParent.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.gvdParentName});
         this.dgvParent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvParent.Location = new System.Drawing.Point(0, 51);
         this.dgvParent.Name = "dgvParent";
         this.dgvParent.ReadOnly = true;
         this.dgvParent.RowHeadersVisible = false;
         this.dgvParent.Size = new System.Drawing.Size(330, 157);
         this.dgvParent.TabIndex = 2;
         this.dgvParent.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvParents_DragEnter);
         this.dgvParent.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvParents_DragDrop);
         // 
         // gvdParentName
         // 
         this.gvdParentName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.gvdParentName.HeaderText = "Имя";
         this.gvdParentName.Name = "gvdParentName";
         this.gvdParentName.ReadOnly = true;
         // 
         // toolStrip3
         // 
         this.toolStrip3.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddParent,
            this.btnDelParent});
         this.toolStrip3.Location = new System.Drawing.Point(0, 26);
         this.toolStrip3.Name = "toolStrip3";
         this.toolStrip3.Size = new System.Drawing.Size(330, 25);
         this.toolStrip3.TabIndex = 1;
         this.toolStrip3.Text = "toolStrip3";
         // 
         // btnAddParent
         // 
         this.btnAddParent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddParent.Image = ((System.Drawing.Image)(resources.GetObject("btnAddParent.Image")));
         this.btnAddParent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddParent.Name = "btnAddParent";
         this.btnAddParent.Size = new System.Drawing.Size(23, 22);
         this.btnAddParent.Text = "Добавить";
         this.btnAddParent.Click += new System.EventHandler(this.btnParent_Click);
         // 
         // btnDelParent
         // 
         this.btnDelParent.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelParent.Image = ((System.Drawing.Image)(resources.GetObject("btnDelParent.Image")));
         this.btnDelParent.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelParent.Name = "btnDelParent";
         this.btnDelParent.Size = new System.Drawing.Size(23, 22);
         this.btnDelParent.Text = "Удалить";
         this.btnDelParent.Click += new System.EventHandler(this.btnDelParent_Click);
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.label3);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel3.Location = new System.Drawing.Point(0, 0);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(330, 26);
         this.panel3.TabIndex = 0;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(2, 6);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(55, 13);
         this.label3.TabIndex = 0;
         this.label3.Text = "Родители";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddDogovor,
            this.btnEditDogovor,
            this.btnDeleteDogovor,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(863, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddDogovor
         // 
         this.btnAddDogovor.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddDogovor.Image = ((System.Drawing.Image)(resources.GetObject("btnAddDogovor.Image")));
         this.btnAddDogovor.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddDogovor.Name = "btnAddDogovor";
         this.btnAddDogovor.Size = new System.Drawing.Size(23, 22);
         this.btnAddDogovor.Text = "Создать";
         this.btnAddDogovor.Click += new System.EventHandler(this.btnAddDogovor_Click);
         // 
         // btnEditDogovor
         // 
         this.btnEditDogovor.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditDogovor.Image = ((System.Drawing.Image)(resources.GetObject("btnEditDogovor.Image")));
         this.btnEditDogovor.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditDogovor.Name = "btnEditDogovor";
         this.btnEditDogovor.Size = new System.Drawing.Size(23, 22);
         this.btnEditDogovor.Text = "Изменить";
         this.btnEditDogovor.Click += new System.EventHandler(this.btnEditDogovor_Click);
         // 
         // btnDeleteDogovor
         // 
         this.btnDeleteDogovor.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDeleteDogovor.Image = ((System.Drawing.Image)(resources.GetObject("btnDeleteDogovor.Image")));
         this.btnDeleteDogovor.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDeleteDogovor.Name = "btnDeleteDogovor";
         this.btnDeleteDogovor.Size = new System.Drawing.Size(23, 22);
         this.btnDeleteDogovor.Text = "Удалить";
         this.btnDeleteDogovor.Click += new System.EventHandler(this.btnDeleteDogovor_Click);
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
         // panel4
         // 
         this.panel4.Controls.Add(this.splitContainer1);
         this.panel4.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel4.Location = new System.Drawing.Point(0, 25);
         this.panel4.Name = "panel4";
         this.panel4.Padding = new System.Windows.Forms.Padding(7);
         this.panel4.Size = new System.Drawing.Size(863, 413);
         this.panel4.TabIndex = 3;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Номер";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Начало";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.HeaderText = "Окончание";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         this.dataGridViewTextBoxColumn3.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.HeaderText = "Тип";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         this.dataGridViewTextBoxColumn4.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn5
         // 
         this.dataGridViewTextBoxColumn5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn5.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn5.Name = "dataGridViewTextBoxColumn5";
         // 
         // dataGridViewTextBoxColumn6
         // 
         this.dataGridViewTextBoxColumn6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn6.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn6.Name = "dataGridViewTextBoxColumn6";
         // 
         // FmDogovor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(863, 460);
         this.Controls.Add(this.panel4);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmDogovor";
         this.Text = "Договор";
         this.Load += new System.EventHandler(this.FmDogovor_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmDogovor_FormClosed);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvDogovor)).EndInit();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvParent)).EndInit();
         this.toolStrip3.ResumeLayout(false);
         this.toolStrip3.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.panel3.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel4.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvDogovor;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.ToolStrip toolStrip3;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.DataGridView dgvStudent;
      private System.Windows.Forms.DataGridView dgvParent;
      private System.Windows.Forms.Panel panel4;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDogovorNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDogovorBegin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDogovorEnd;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvDogovorType;
      private System.Windows.Forms.ToolStripButton btnAddDogovor;
      private System.Windows.Forms.ToolStripButton btnEditDogovor;
      private System.Windows.Forms.ToolStripButton btnDeleteDogovor;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.ToolStripButton btnAddStudent;
      private System.Windows.Forms.ToolStripButton btnAddParent;
      private System.Windows.Forms.ToolStripButton btnDelStudent;
      private System.Windows.Forms.ToolStripButton btnDelParent;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStudentName;
      private System.Windows.Forms.DataGridViewTextBoxColumn gvdParentName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn5;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn6;
   }
}
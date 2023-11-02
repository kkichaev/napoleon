namespace GRSoft.Ads
{
   partial class FmProfession
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmProfession));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnSearchBack = new System.Windows.Forms.ToolStripButton();
         this.btnSearchForward = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.dgvProfession = new System.Windows.Forms.DataGridView();
         this.dgvProfessionCode = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dvgProfessionName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvProfession)).BeginInit();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh,
            this.toolStripSeparator3,
            this.tbFind,
            this.btnSearchBack,
            this.btnSearchForward});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(439, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
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
         // toolStripSeparator3
         // 
         this.toolStripSeparator3.Name = "toolStripSeparator3";
         this.toolStripSeparator3.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.ToolTipText = "Введите строку для поиска";
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnSearchBack
         // 
         this.btnSearchBack.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchBack.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchBack.Image")));
         this.btnSearchBack.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchBack.Name = "btnSearchBack";
         this.btnSearchBack.Size = new System.Drawing.Size(23, 22);
         this.btnSearchBack.Text = "Искать назад";
         this.btnSearchBack.Click += new System.EventHandler(this.btnSearchBack_Click);
         // 
         // btnSearchForward
         // 
         this.btnSearchForward.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSearchForward.Image = ((System.Drawing.Image)(resources.GetObject("btnSearchForward.Image")));
         this.btnSearchForward.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSearchForward.Name = "btnSearchForward";
         this.btnSearchForward.Size = new System.Drawing.Size(23, 22);
         this.btnSearchForward.Text = " Искать вперед";
         this.btnSearchForward.Click += new System.EventHandler(this.btnSearchForward_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 264);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(439, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvProfession
         // 
         this.dgvProfession.AllowUserToAddRows = false;
         this.dgvProfession.AllowUserToDeleteRows = false;
         this.dgvProfession.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvProfession.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvProfessionCode,
            this.dvgProfessionName});
         this.dgvProfession.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvProfession.Location = new System.Drawing.Point(7, 7);
         this.dgvProfession.Name = "dgvProfession";
         this.dgvProfession.RowHeadersVisible = false;
         this.dgvProfession.Size = new System.Drawing.Size(425, 225);
         this.dgvProfession.TabIndex = 2;
         this.dgvProfession.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvProfession_ColumnHeaderMouseClick);
         // 
         // dgvProfessionCode
         // 
         this.dgvProfessionCode.DataPropertyName = "Id";
         this.dgvProfessionCode.HeaderText = "Код";
         this.dgvProfessionCode.Name = "dgvProfessionCode";
         // 
         // dvgProfessionName
         // 
         this.dvgProfessionName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dvgProfessionName.DataPropertyName = "Name";
         this.dvgProfessionName.HeaderText = "Наименование";
         this.dvgProfessionName.Name = "dvgProfessionName";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvProfession);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(439, 239);
         this.panel1.TabIndex = 3;
         // 
         // FmProfession
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(439, 286);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmProfession";
         this.Text = "Профессии";
         this.Load += new System.EventHandler(this.FmProfession_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmProfession_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvProfession)).EndInit();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvProfession;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvProfessionCode;
      private System.Windows.Forms.DataGridViewTextBoxColumn dvgProfessionName;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnSearchBack;
      private System.Windows.Forms.ToolStripButton btnSearchForward;
   }
}
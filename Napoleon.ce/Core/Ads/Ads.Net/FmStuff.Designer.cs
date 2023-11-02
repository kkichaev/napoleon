namespace GRSoft.Ads
{
   partial class FmStuff
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmStuff));
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
         this.dgvStuff = new System.Windows.Forms.DataGridView();
         this.dgvStuffFIO = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvStuffPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvStuffAddress = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvStuffProfession = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvStuffRank = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1 = new System.Windows.Forms.Panel();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStuff)).BeginInit();
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
         this.toolStrip1.Size = new System.Drawing.Size(600, 25);
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
         this.btnRefresh.Text = "Удалить";
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
         this.statusStrip1.Location = new System.Drawing.Point(0, 423);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(600, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // dgvStuff
         // 
         this.dgvStuff.AllowUserToAddRows = false;
         this.dgvStuff.AllowUserToDeleteRows = false;
         this.dgvStuff.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvStuff.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvStuffFIO,
            this.dgvStuffPhone,
            this.dgvStuffAddress,
            this.dgvStuffProfession,
            this.dgvStuffRank});
         this.dgvStuff.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvStuff.Location = new System.Drawing.Point(7, 7);
         this.dgvStuff.Name = "dgvStuff";
         this.dgvStuff.RowHeadersVisible = false;
         this.dgvStuff.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvStuff.Size = new System.Drawing.Size(586, 384);
         this.dgvStuff.TabIndex = 2;
         this.dgvStuff.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvStuff_ColumnHeaderMouseClick);
         this.dgvStuff.DoubleClick += new System.EventHandler(this.dgvStuff_DoubleClick);
         // 
         // dgvStuffFIO
         // 
         this.dgvStuffFIO.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffFIO.DataPropertyName = "FIO";
         this.dgvStuffFIO.HeaderText = "ФИО";
         this.dgvStuffFIO.Name = "dgvStuffFIO";
         // 
         // dgvStuffPhone
         // 
         this.dgvStuffPhone.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffPhone.DataPropertyName = "Phone";
         this.dgvStuffPhone.HeaderText = "Телефон";
         this.dgvStuffPhone.Name = "dgvStuffPhone";
         // 
         // dgvStuffAddress
         // 
         this.dgvStuffAddress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffAddress.DataPropertyName = "Address";
         this.dgvStuffAddress.HeaderText = "Адрес";
         this.dgvStuffAddress.Name = "dgvStuffAddress";
         // 
         // dgvStuffProfession
         // 
         this.dgvStuffProfession.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffProfession.DataPropertyName = "Profession";
         this.dgvStuffProfession.HeaderText = "Профессия";
         this.dgvStuffProfession.Name = "dgvStuffProfession";
         // 
         // dgvStuffRank
         // 
         this.dgvStuffRank.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStuffRank.DataPropertyName = "Rank";
         this.dgvStuffRank.HeaderText = "Категория";
         this.dgvStuffRank.Name = "dgvStuffRank";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvStuff);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(600, 398);
         this.panel1.TabIndex = 3;
         // 
         // FmStuff
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(600, 445);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmStuff";
         this.Text = "Рабочие";
         this.Load += new System.EventHandler(this.FmStuff_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmStuff_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStuff)).EndInit();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.DataGridView dgvStuff;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffFIO;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffPhone;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffAddress;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffProfession;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStuffRank;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnSearchBack;
      private System.Windows.Forms.ToolStripButton btnSearchForward;
   }
}
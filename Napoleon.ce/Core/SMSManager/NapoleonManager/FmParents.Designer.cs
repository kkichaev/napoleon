namespace GRSoft.NapoleonManager
{
   partial class FmParents
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmParents));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dgvParents = new System.Windows.Forms.DataGridView();
         this.dgvParentsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvParentsPhones = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvParents)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 424);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(677, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDel,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(677, 25);
         this.toolStrip1.TabIndex = 1;
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
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvParents);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(677, 399);
         this.panel1.TabIndex = 2;
         // 
         // dgvParents
         // 
         this.dgvParents.AllowUserToAddRows = false;
         this.dgvParents.AllowUserToResizeRows = false;
         this.dgvParents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvParents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvParentsName,
            this.dgvParentsPhones});
         this.dgvParents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvParents.Location = new System.Drawing.Point(7, 7);
         this.dgvParents.Name = "dgvParents";
         this.dgvParents.ReadOnly = true;
         this.dgvParents.RowHeadersVisible = false;
         this.dgvParents.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvParents.Size = new System.Drawing.Size(663, 385);
         this.dgvParents.TabIndex = 0;
         this.dgvParents.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvParents_MouseDown);
         // 
         // dgvParentsName
         // 
         this.dgvParentsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvParentsName.HeaderText = "Имя";
         this.dgvParentsName.Name = "dgvParentsName";
         this.dgvParentsName.ReadOnly = true;
         // 
         // dgvParentsPhones
         // 
         this.dgvParentsPhones.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvParentsPhones.HeaderText = "Телефоны";
         this.dgvParentsPhones.Name = "dgvParentsPhones";
         this.dgvParentsPhones.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Телефоны";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // FmParents
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(677, 446);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmParents";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Родители";
         this.Load += new System.EventHandler(this.FmParents_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmParents_FormClosed);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvParents)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView dgvParents;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvParentsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvParentsPhones;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
   }
}
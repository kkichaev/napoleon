namespace GRSoft.NapoleonManager
{
   partial class FmAgent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgent));
         this.panel1 = new System.Windows.Forms.Panel();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDelete = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgentsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgentsLogin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgentsPassw = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvAgentsPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dgvAgents);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(672, 398);
         this.panel1.TabIndex = 0;
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 423);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(672, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDelete,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(672, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvAgentsName,
            this.dgvAgentsLogin,
            this.dgvAgentsPassw,
            this.dgvAgentsPhone});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.Location = new System.Drawing.Point(7, 7);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvAgents.Size = new System.Drawing.Size(658, 384);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvAgents_CellMouseDoubleClick);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Создать";
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
         // btnDelete
         // 
         this.btnDelete.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelete.Image = ((System.Drawing.Image)(resources.GetObject("btnDelete.Image")));
         this.btnDelete.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelete.Name = "btnDelete";
         this.btnDelete.Size = new System.Drawing.Size(23, 22);
         this.btnDelete.Text = "Удалить";
         this.btnDelete.Click += new System.EventHandler(this.btnDelete_Click);
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
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Логин";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.HeaderText = "Пароль";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn4.HeaderText = "Телефон";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // dgvAgentsName
         // 
         this.dgvAgentsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvAgentsName.HeaderText = "Имя";
         this.dgvAgentsName.Name = "dgvAgentsName";
         // 
         // dgvAgentsLogin
         // 
         this.dgvAgentsLogin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvAgentsLogin.HeaderText = "Логин";
         this.dgvAgentsLogin.Name = "dgvAgentsLogin";
         // 
         // dgvAgentsPassw
         // 
         this.dgvAgentsPassw.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvAgentsPassw.HeaderText = "Пароль";
         this.dgvAgentsPassw.Name = "dgvAgentsPassw";
         // 
         // dgvAgentsPhone
         // 
         this.dgvAgentsPhone.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvAgentsPhone.HeaderText = "Телефон";
         this.dgvAgentsPhone.Name = "dgvAgentsPhone";
         // 
         // FmAgent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(672, 445);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Name = "FmAgent";
         this.Text = "Агенты";
         this.Load += new System.EventHandler(this.FmAgent_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmAgent_FormClosed);
         this.panel1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvAgentsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvAgentsLogin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvAgentsPassw;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvAgentsPhone;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDelete;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
   }
}
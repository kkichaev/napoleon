namespace GRSoft.NapoleonManager
{
   partial class DivisionChief
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(DivisionChief));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnNew = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.dgvManagers = new System.Windows.Forms.DataGridView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.login = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.password = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvManagers)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnNew,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(374, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnNew
         // 
         this.btnNew.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnNew.Image = ((System.Drawing.Image)(resources.GetObject("btnNew.Image")));
         this.btnNew.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnNew.Name = "btnNew";
         this.btnNew.Size = new System.Drawing.Size(23, 22);
         this.btnNew.Text = "Добавить";
         this.btnNew.Click += new System.EventHandler(this.btnNew_Click);
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
         // dgvManagers
         // 
         this.dgvManagers.AllowUserToAddRows = false;
         this.dgvManagers.AllowUserToDeleteRows = false;
         this.dgvManagers.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvManagers.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.login,
            this.password,
            this.Column1});
         this.dgvManagers.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvManagers.Location = new System.Drawing.Point(0, 25);
         this.dgvManagers.Name = "dgvManagers";
         this.dgvManagers.RowHeadersVisible = false;
         this.dgvManagers.Size = new System.Drawing.Size(374, 237);
         this.dgvManagers.TabIndex = 1;
         this.dgvManagers.CellValidating += new System.Windows.Forms.DataGridViewCellValidatingEventHandler(this.dgvManagers_CellValidating);
         this.dgvManagers.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvManagers_CellValueChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Login";
         this.dataGridViewTextBoxColumn1.HeaderText = "Логин";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Password";
         this.dataGridViewTextBoxColumn2.HeaderText = "Пароль";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.Width = 200;
         // 
         // login
         // 
         this.login.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.login.DataPropertyName = "Login";
         this.login.HeaderText = "Логин";
         this.login.Name = "login";
         // 
         // password
         // 
         this.password.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.password.DataPropertyName = "Password";
         this.password.HeaderText = "Пароль";
         this.password.Name = "password";
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Имя";
         this.Column1.Name = "Column1";
         // 
         // DivisionChief
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(374, 262);
         this.Controls.Add(this.dgvManagers);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "DivisionChief";
         this.Text = "Руководители подразделения";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvManagers)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnNew;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.DataGridView dgvManagers;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn login;
      private System.Windows.Forms.DataGridViewTextBoxColumn password;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
   }
}
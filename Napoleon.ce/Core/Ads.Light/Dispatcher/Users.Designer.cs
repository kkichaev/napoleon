namespace GRSoft.Ads.Dispatcher
{
   partial class Users
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
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.label = new System.Windows.Forms.ToolStripStatusLabel();
         this.dgvUsers = new System.Windows.Forms.DataGridView();
         this.dgvUsersName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvUsersLogin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvUsersPassw = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvUsersLicence = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvUsers)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.btnAdd,
            this.btnEdit,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(561, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.expand;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.document_sign;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.dialog_cancel;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.label});
         this.statusStrip1.Location = new System.Drawing.Point(0, 341);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(561, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // label
         // 
         this.label.Name = "label";
         this.label.Size = new System.Drawing.Size(109, 17);
         this.label.Text = "toolStripStatusLabel1";
         // 
         // dgvUsers
         // 
         this.dgvUsers.AllowUserToAddRows = false;
         this.dgvUsers.AllowUserToDeleteRows = false;
         this.dgvUsers.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvUsers.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvUsersName,
            this.dgvUsersLogin,
            this.dgvUsersPassw,
            this.dgvUsersLicence});
         this.dgvUsers.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvUsers.Location = new System.Drawing.Point(0, 25);
         this.dgvUsers.Name = "dgvUsers";
         this.dgvUsers.RowHeadersVisible = false;
         this.dgvUsers.Size = new System.Drawing.Size(561, 316);
         this.dgvUsers.TabIndex = 2;
         this.dgvUsers.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvUsers_CellContentClick);
         // 
         // dgvUsersName
         // 
         this.dgvUsersName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvUsersName.DataPropertyName = "Name";
         this.dgvUsersName.HeaderText = "Имя";
         this.dgvUsersName.Name = "dgvUsersName";
         // 
         // dgvUsersLogin
         // 
         this.dgvUsersLogin.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvUsersLogin.DataPropertyName = "Login";
         this.dgvUsersLogin.HeaderText = "Логин";
         this.dgvUsersLogin.Name = "dgvUsersLogin";
         // 
         // dgvUsersPassw
         // 
         this.dgvUsersPassw.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvUsersPassw.DataPropertyName = "Pwd";
         this.dgvUsersPassw.HeaderText = "Пароль";
         this.dgvUsersPassw.Name = "dgvUsersPassw";
         // 
         // dgvUsersLicence
         // 
         this.dgvUsersLicence.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvUsersLicence.DataPropertyName = "License";
         this.dgvUsersLicence.FalseValue = "false";
         this.dgvUsersLicence.HeaderText = "Лицензия";
         this.dgvUsersLicence.Name = "dgvUsersLicence";
         this.dgvUsersLicence.TrueValue = "true";
         // 
         // Users
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(561, 363);
         this.Controls.Add(this.dgvUsers);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Name = "Users";
         this.Text = "Пользователи";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Users_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvUsers)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.DataGridView dgvUsers;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvUsersName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvUsersLogin;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvUsersPassw;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvUsersLicence;
      private System.Windows.Forms.ToolStripStatusLabel label;
   }
}
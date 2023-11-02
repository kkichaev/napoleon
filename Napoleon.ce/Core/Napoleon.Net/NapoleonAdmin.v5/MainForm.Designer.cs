namespace GRSoft.NapoleonAdmin
{
   partial class MainForm
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
         this.userMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.bindUser = new System.Windows.Forms.ToolStripMenuItem();
         this.unlinkUser = new System.Windows.Forms.ToolStripMenuItem();
         this.folderBrowserDialog1 = new System.Windows.Forms.FolderBrowserDialog();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.userUpdate = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.serverCode = new System.Windows.Forms.ToolStripTextBox();
         this.version = new System.Windows.Forms.ToolStripLabel();
         this.usersView = new System.Windows.Forms.DataGridView();
         this.cbUsers = new System.Windows.Forms.ComboBox();
         this.clmnId = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.user = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnLink = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.activity = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.progVersion = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.userMenu.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.usersView)).BeginInit();
         this.SuspendLayout();
         // 
         // userMenu
         // 
         this.userMenu.ImageScalingSize = new System.Drawing.Size(20, 20);
         this.userMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.bindUser,
            this.unlinkUser});
         this.userMenu.Name = "contextMenuStrip1";
         this.userMenu.Size = new System.Drawing.Size(265, 52);
         // 
         // bindUser
         // 
         this.bindUser.Name = "bindUser";
         this.bindUser.Size = new System.Drawing.Size(264, 24);
         this.bindUser.Text = "Подключить пользователя";
         this.bindUser.Click += new System.EventHandler(this.bindUser_Click);
         // 
         // unlinkUser
         // 
         this.unlinkUser.Name = "unlinkUser";
         this.unlinkUser.Size = new System.Drawing.Size(264, 24);
         this.unlinkUser.Text = "Отключить пользователя";
         this.unlinkUser.Click += new System.EventHandler(this.unlinkUser_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.userUpdate,
            this.toolStripLabel1,
            this.serverCode,
            this.version});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1497, 39);
         this.toolStrip1.TabIndex = 5;
         // 
         // userUpdate
         // 
         this.userUpdate.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.userUpdate.Image = global::GRSoft.NapoleonAdmin.Properties.Resources.refresh;
         this.userUpdate.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.userUpdate.Name = "userUpdate";
         this.userUpdate.Size = new System.Drawing.Size(36, 36);
         this.userUpdate.Text = "Обновить";
         this.userUpdate.Click += new System.EventHandler(this.userUpdate_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(95, 36);
         this.toolStripLabel1.Text = "Код проекта";
         // 
         // serverCode
         // 
         this.serverCode.Name = "serverCode";
         this.serverCode.Size = new System.Drawing.Size(332, 39);
         // 
         // version
         // 
         this.version.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.version.Name = "version";
         this.version.Size = new System.Drawing.Size(56, 36);
         this.version.Text = "version";
         // 
         // usersView
         // 
         this.usersView.AllowUserToAddRows = false;
         this.usersView.AllowUserToDeleteRows = false;
         this.usersView.AllowUserToOrderColumns = true;
         this.usersView.AllowUserToResizeRows = false;
         this.usersView.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.usersView.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnId,
            this.user,
            this.clmnLink,
            this.activity,
            this.progVersion});
         this.usersView.ContextMenuStrip = this.userMenu;
         this.usersView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.usersView.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.usersView.Location = new System.Drawing.Point(0, 39);
         this.usersView.Margin = new System.Windows.Forms.Padding(4);
         this.usersView.MultiSelect = false;
         this.usersView.Name = "usersView";
         this.usersView.RowHeadersVisible = false;
         this.usersView.RowHeadersWidth = 51;
         this.usersView.Size = new System.Drawing.Size(1497, 654);
         this.usersView.TabIndex = 6;
         this.usersView.MouseDown += new System.Windows.Forms.MouseEventHandler(this.usersView_MouseDown);
         // 
         // cbUsers
         // 
         this.cbUsers.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.cbUsers.FormattingEnabled = true;
         this.cbUsers.Items.AddRange(new object[] {
            "Агенты",
            "Менеджеры"});
         this.cbUsers.Location = new System.Drawing.Point(627, 9);
         this.cbUsers.Margin = new System.Windows.Forms.Padding(4);
         this.cbUsers.Name = "cbUsers";
         this.cbUsers.Size = new System.Drawing.Size(225, 28);
         this.cbUsers.TabIndex = 7;
         this.cbUsers.SelectedIndexChanged += new System.EventHandler(this.cbUsers_SelectedIndexChanged);
         // 
         // clmnId
         // 
         this.clmnId.DataPropertyName = "Id";
         this.clmnId.HeaderText = "ID";
         this.clmnId.MinimumWidth = 6;
         this.clmnId.Name = "clmnId";
         this.clmnId.ReadOnly = true;
         this.clmnId.Width = 200;
         // 
         // user
         // 
         this.user.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.user.DataPropertyName = "Name";
         this.user.HeaderText = "Пользователь";
         this.user.MinimumWidth = 6;
         this.user.Name = "user";
         this.user.ReadOnly = true;
         this.user.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         // 
         // clmnLink
         // 
         this.clmnLink.DataPropertyName = "Status";
         this.clmnLink.HeaderText = "Статус";
         this.clmnLink.MinimumWidth = 6;
         this.clmnLink.Name = "clmnLink";
         this.clmnLink.Width = 200;
         // 
         // activity
         // 
         this.activity.DataPropertyName = "LastAccess";
         this.activity.FillWeight = 50.32042F;
         this.activity.HeaderText = "Посл.доступ";
         this.activity.MinimumWidth = 6;
         this.activity.Name = "activity";
         this.activity.ReadOnly = true;
         this.activity.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.activity.Width = 150;
         // 
         // progVersion
         // 
         this.progVersion.DataPropertyName = "Version";
         this.progVersion.FillWeight = 35.94316F;
         this.progVersion.HeaderText = "Версия";
         this.progVersion.MinimumWidth = 6;
         this.progVersion.Name = "progVersion";
         this.progVersion.ReadOnly = true;
         this.progVersion.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Programmatic;
         this.progVersion.Width = 150;
         // 
         // MainForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1497, 693);
         this.Controls.Add(this.cbUsers);
         this.Controls.Add(this.usersView);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Margin = new System.Windows.Forms.Padding(4);
         this.Name = "MainForm";
         this.Text = "Наполеон - Администратор";
         this.userMenu.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.usersView)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion
      private System.Windows.Forms.ContextMenuStrip userMenu;
      private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog1;
      protected System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton userUpdate;
      public System.Windows.Forms.DataGridView usersView;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripTextBox serverCode;
      private System.Windows.Forms.ToolStripLabel version;
      private System.Windows.Forms.ComboBox cbUsers;
      private System.Windows.Forms.ToolStripMenuItem bindUser;
      private System.Windows.Forms.ToolStripMenuItem unlinkUser;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnId;
      private System.Windows.Forms.DataGridViewTextBoxColumn user;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnLink;
      private System.Windows.Forms.DataGridViewTextBoxColumn activity;
      private System.Windows.Forms.DataGridViewTextBoxColumn progVersion;
   }
}
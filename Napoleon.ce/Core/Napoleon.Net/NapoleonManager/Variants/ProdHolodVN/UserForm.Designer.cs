namespace GRSoft.NapoleonManager
{
   partial class UserForm
   {
      /// <summary> 
      /// Требуется переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary> 
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором компонентов

      /// <summary> 
      /// Обязательный метод для поддержки конструктора - не изменяйте 
      /// содержимое данного метода при помощи редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.Windows.Forms.TreeNode treeNode1 = new System.Windows.Forms.TreeNode("Понедельник");
         System.Windows.Forms.TreeNode treeNode2 = new System.Windows.Forms.TreeNode("Вторник");
         System.Windows.Forms.TreeNode treeNode3 = new System.Windows.Forms.TreeNode("Среда");
         System.Windows.Forms.TreeNode treeNode4 = new System.Windows.Forms.TreeNode("Четверг");
         System.Windows.Forms.TreeNode treeNode5 = new System.Windows.Forms.TreeNode("Пятница");
         System.Windows.Forms.TreeNode treeNode6 = new System.Windows.Forms.TreeNode("Суббота");
         System.Windows.Forms.TreeNode treeNode7 = new System.Windows.Forms.TreeNode("Воскресенье");
         this.label1 = new System.Windows.Forms.Label();
         this.name = new System.Windows.Forms.TextBox();
         this.userDetails = new System.Windows.Forms.TabControl();
         this.udOrgs = new System.Windows.Forms.TabPage();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tvDayTasks = new System.Windows.Forms.TreeView();
         this.cmsDayTask = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miDelete = new System.Windows.Forms.ToolStripMenuItem();
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.dgvOrgsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.udFolders = new System.Windows.Forms.TabPage();
         this.wbArticlesMessage = new System.Windows.Forms.WebBrowser();
         this.tvAccessibleArticles = new System.Windows.Forms.TreeView();
         this.udMatrix = new System.Windows.Forms.TabPage();
         this.tvAgentMatrix = new System.Windows.Forms.TreeView();
         this.panel1 = new System.Windows.Forms.Panel();
         this.tbPhone = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.udDogovors = new System.Windows.Forms.TabPage();
         this.tvDogovors = new System.Windows.Forms.TreeView();
         this.userDetails.SuspendLayout();
         this.udOrgs.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.cmsDayTask.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.udFolders.SuspendLayout();
         this.udMatrix.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.udDogovors.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(8, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(32, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "ФИО";
         // 
         // name
         // 
         this.name.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.name.Location = new System.Drawing.Point(64, 5);
         this.name.Name = "name";
         this.name.Size = new System.Drawing.Size(346, 20);
         this.name.TabIndex = 1;
         // 
         // userDetails
         // 
         this.userDetails.Controls.Add(this.udOrgs);
         this.userDetails.Controls.Add(this.udFolders);
         this.userDetails.Controls.Add(this.udMatrix);
         this.userDetails.Controls.Add(this.udDogovors);
         this.userDetails.Dock = System.Windows.Forms.DockStyle.Fill;
         this.userDetails.Location = new System.Drawing.Point(0, 0);
         this.userDetails.Name = "userDetails";
         this.userDetails.SelectedIndex = 0;
         this.userDetails.Size = new System.Drawing.Size(474, 306);
         this.userDetails.TabIndex = 2;
         // 
         // udOrgs
         // 
         this.udOrgs.Controls.Add(this.splitContainer1);
         this.udOrgs.Location = new System.Drawing.Point(4, 23);
         this.udOrgs.Name = "udOrgs";
         this.udOrgs.Padding = new System.Windows.Forms.Padding(3);
         this.udOrgs.Size = new System.Drawing.Size(466, 279);
         this.udOrgs.TabIndex = 0;
         this.udOrgs.Text = "Контрагенты";
         this.udOrgs.UseVisualStyleBackColor = true;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(3, 3);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvDayTasks);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvOrgs);
         this.splitContainer1.Size = new System.Drawing.Size(460, 273);
         this.splitContainer1.SplitterDistance = 224;
         this.splitContainer1.TabIndex = 0;
         // 
         // tvDayTasks
         // 
         this.tvDayTasks.AllowDrop = true;
         this.tvDayTasks.ContextMenuStrip = this.cmsDayTask;
         this.tvDayTasks.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvDayTasks.HideSelection = false;
         this.tvDayTasks.Location = new System.Drawing.Point(0, 0);
         this.tvDayTasks.Name = "tvDayTasks";
         treeNode1.BackColor = System.Drawing.Color.White;
         treeNode1.ForeColor = System.Drawing.Color.Blue;
         treeNode1.Name = "Node0";
         treeNode1.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode1.Text = "Понедельник";
         treeNode2.ForeColor = System.Drawing.Color.Blue;
         treeNode2.Name = "Node1";
         treeNode2.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode2.Text = "Вторник";
         treeNode3.ForeColor = System.Drawing.Color.Blue;
         treeNode3.Name = "Node2";
         treeNode3.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode3.Text = "Среда";
         treeNode4.ForeColor = System.Drawing.Color.Blue;
         treeNode4.Name = "Node3";
         treeNode4.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode4.Text = "Четверг";
         treeNode5.ForeColor = System.Drawing.Color.Blue;
         treeNode5.Name = "Node4";
         treeNode5.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode5.Text = "Пятница";
         treeNode6.ForeColor = System.Drawing.Color.Blue;
         treeNode6.Name = "Node5";
         treeNode6.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode6.Text = "Суббота";
         treeNode7.ForeColor = System.Drawing.Color.Blue;
         treeNode7.Name = "Node6";
         treeNode7.NodeFont = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         treeNode7.Text = "Воскресенье";
         this.tvDayTasks.Nodes.AddRange(new System.Windows.Forms.TreeNode[] {
            treeNode1,
            treeNode2,
            treeNode3,
            treeNode4,
            treeNode5,
            treeNode6,
            treeNode7});
         this.tvDayTasks.Size = new System.Drawing.Size(224, 273);
         this.tvDayTasks.TabIndex = 0;
         this.tvDayTasks.DragDrop += new System.Windows.Forms.DragEventHandler(this.tvDayTasks_DragDrop);
         this.tvDayTasks.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvDayTasks_MouseDown);
         this.tvDayTasks.DragEnter += new System.Windows.Forms.DragEventHandler(this.tvDayTasks_DragEnter);
         this.tvDayTasks.DragOver += new System.Windows.Forms.DragEventHandler(this.tvDayTasks_DragOver);
         // 
         // cmsDayTask
         // 
         this.cmsDayTask.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miDelete});
         this.cmsDayTask.Name = "cmsDayTask";
         this.cmsDayTask.Size = new System.Drawing.Size(119, 26);
         this.cmsDayTask.Opening += new System.ComponentModel.CancelEventHandler(this.cmsDayTask_Opening);
         // 
         // miDelete
         // 
         this.miDelete.Name = "miDelete";
         this.miDelete.Size = new System.Drawing.Size(118, 22);
         this.miDelete.Text = "Удалить";
         this.miDelete.Click += new System.EventHandler(this.miDelete_Click);
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvOrgsName});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 0);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(232, 273);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseDown);
         this.dgvOrgs.DoubleClick += new System.EventHandler(this.dgvOrgs_DoubleClick);
         // 
         // dgvOrgsName
         // 
         this.dgvOrgsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvOrgsName.HeaderText = "";
         this.dgvOrgsName.Name = "dgvOrgsName";
         this.dgvOrgsName.ToolTipText = "перенесите желаемого контрагента в нужный день недели";
         // 
         // udFolders
         // 
         this.udFolders.Controls.Add(this.wbArticlesMessage);
         this.udFolders.Controls.Add(this.tvAccessibleArticles);
         this.udFolders.Location = new System.Drawing.Point(4, 23);
         this.udFolders.Name = "udFolders";
         this.udFolders.Padding = new System.Windows.Forms.Padding(3);
         this.udFolders.Size = new System.Drawing.Size(466, 279);
         this.udFolders.TabIndex = 1;
         this.udFolders.Text = "Доступный товар";
         this.udFolders.UseVisualStyleBackColor = true;
         // 
         // wbArticlesMessage
         // 
         this.wbArticlesMessage.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wbArticlesMessage.Location = new System.Drawing.Point(3, 3);
         this.wbArticlesMessage.MinimumSize = new System.Drawing.Size(20, 20);
         this.wbArticlesMessage.Name = "wbArticlesMessage";
         this.wbArticlesMessage.Size = new System.Drawing.Size(460, 273);
         this.wbArticlesMessage.TabIndex = 1;
         // 
         // tvAccessibleArticles
         // 
         this.tvAccessibleArticles.CheckBoxes = true;
         this.tvAccessibleArticles.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvAccessibleArticles.Location = new System.Drawing.Point(3, 3);
         this.tvAccessibleArticles.Name = "tvAccessibleArticles";
         this.tvAccessibleArticles.Size = new System.Drawing.Size(460, 273);
         this.tvAccessibleArticles.TabIndex = 0;
         this.tvAccessibleArticles.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAccessibleArticles_AfterCheck);
         this.tvAccessibleArticles.BeforeCheck += new System.Windows.Forms.TreeViewCancelEventHandler(this.tvAccessibleArticles_BeforeCheck);
         // 
         // udMatrix
         // 
         this.udMatrix.Controls.Add(this.tvAgentMatrix);
         this.udMatrix.Location = new System.Drawing.Point(4, 23);
         this.udMatrix.Name = "udMatrix";
         this.udMatrix.Size = new System.Drawing.Size(466, 279);
         this.udMatrix.TabIndex = 2;
         this.udMatrix.Text = "Матрицы";
         this.udMatrix.UseVisualStyleBackColor = true;
         // 
         // tvAgentMatrix
         // 
         this.tvAgentMatrix.CheckBoxes = true;
         this.tvAgentMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvAgentMatrix.Location = new System.Drawing.Point(0, 0);
         this.tvAgentMatrix.Name = "tvAgentMatrix";
         this.tvAgentMatrix.Size = new System.Drawing.Size(466, 279);
         this.tvAgentMatrix.TabIndex = 0;
         this.tvAgentMatrix.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAgentMatrix_AfterCheck);
         this.tvAgentMatrix.BeforeCheck += new System.Windows.Forms.TreeViewCancelEventHandler(this.tvAgentMatrix_BeforeCheck);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.tbPhone);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Controls.Add(this.name);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(474, 64);
         this.panel1.TabIndex = 3;
         // 
         // tbPhone
         // 
         this.tbPhone.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPhone.Location = new System.Drawing.Point(64, 31);
         this.tbPhone.Name = "tbPhone";
         this.tbPhone.Size = new System.Drawing.Size(345, 20);
         this.tbPhone.TabIndex = 3;
         this.tbPhone.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.tbPhone_KeyPress);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(8, 33);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(50, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Телефон";
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.userDetails);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 64);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(474, 306);
         this.panel2.TabIndex = 4;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Column1";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ToolTipText = "перенесите желаемого контрагента в нужный день недели";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // udDogovors
         // 
         this.udDogovors.Controls.Add(this.tvDogovors);
         this.udDogovors.Location = new System.Drawing.Point(4, 23);
         this.udDogovors.Name = "udDogovors";
         this.udDogovors.Padding = new System.Windows.Forms.Padding(3);
         this.udDogovors.Size = new System.Drawing.Size(466, 279);
         this.udDogovors.TabIndex = 3;
         this.udDogovors.Text = "Договора";
         this.udDogovors.UseVisualStyleBackColor = true;
         // 
         // tvDogovors
         // 
         this.tvDogovors.CheckBoxes = true;
         this.tvDogovors.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvDogovors.Location = new System.Drawing.Point(3, 3);
         this.tvDogovors.Name = "tvDogovors";
         this.tvDogovors.Size = new System.Drawing.Size(460, 273);
         this.tvDogovors.TabIndex = 1;
         // 
         // UserForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Name = "UserForm";
         this.Size = new System.Drawing.Size(474, 370);
         this.userDetails.ResumeLayout(false);
         this.udOrgs.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.cmsDayTask.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.udFolders.ResumeLayout(false);
         this.udMatrix.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.udDogovors.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox name;
      private System.Windows.Forms.TabControl userDetails;
      private System.Windows.Forms.TabPage udOrgs;
      private System.Windows.Forms.TabPage udFolders;
      private System.Windows.Forms.TreeView tvAccessibleArticles;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.TreeView tvDayTasks;
      private System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.ContextMenuStrip cmsDayTask;
      private System.Windows.Forms.ToolStripMenuItem miDelete;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvOrgsName;
      private System.Windows.Forms.TabPage udMatrix;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.TreeView tvAgentMatrix;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.TextBox tbPhone;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.WebBrowser wbArticlesMessage;
      private System.Windows.Forms.TabPage udDogovors;
      private System.Windows.Forms.TreeView tvDogovors;
   }
}

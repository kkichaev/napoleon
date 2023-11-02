namespace GRSoft.NapoleonManager
{
   partial class DivisionForm
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
         this.tabControl1 = new System.Windows.Forms.TabControl();
         this.childUsers = new System.Windows.Forms.TabPage();
         this.childUserList = new System.Windows.Forms.DataGridView();
         this.culName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tracking = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.childDivisions = new System.Windows.Forms.TabPage();
         this.childDivisionList = new System.Windows.Forms.DataGridView();
         this.cdlName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.childArticles = new System.Windows.Forms.TabPage();
         this.tvAccessibleArticles = new System.Windows.Forms.TreeView();
         this.setCheif = new System.Windows.Forms.Button();
         this.description = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.name = new System.Windows.Forms.TextBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tabControl1.SuspendLayout();
         this.childUsers.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.childUserList)).BeginInit();
         this.childDivisions.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.childDivisionList)).BeginInit();
         this.childArticles.SuspendLayout();
         this.SuspendLayout();
         // 
         // tabControl1
         // 
         this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tabControl1.Controls.Add(this.childUsers);
         this.tabControl1.Controls.Add(this.childDivisions);
         this.tabControl1.Controls.Add(this.childArticles);
         this.tabControl1.Location = new System.Drawing.Point(2, 123);
         this.tabControl1.Name = "tabControl1";
         this.tabControl1.SelectedIndex = 0;
         this.tabControl1.ShowToolTips = true;
         this.tabControl1.Size = new System.Drawing.Size(341, 272);
         this.tabControl1.TabIndex = 15;
         // 
         // childUsers
         // 
         this.childUsers.Controls.Add(this.childUserList);
         this.childUsers.Location = new System.Drawing.Point(4, 22);
         this.childUsers.Name = "childUsers";
         this.childUsers.Padding = new System.Windows.Forms.Padding(3);
         this.childUsers.Size = new System.Drawing.Size(333, 246);
         this.childUsers.TabIndex = 0;
         this.childUsers.Text = "Подчиненные";
         this.childUsers.UseVisualStyleBackColor = true;
         // 
         // childUserList
         // 
         this.childUserList.AllowDrop = true;
         this.childUserList.AllowUserToAddRows = false;
         this.childUserList.AllowUserToDeleteRows = false;
         this.childUserList.AllowUserToResizeRows = false;
         this.childUserList.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.childUserList.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.culName,
            this.tracking});
         this.childUserList.Dock = System.Windows.Forms.DockStyle.Fill;
         this.childUserList.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.childUserList.Location = new System.Drawing.Point(3, 3);
         this.childUserList.Name = "childUserList";
         this.childUserList.RowHeadersVisible = false;
         this.childUserList.Size = new System.Drawing.Size(327, 240);
         this.childUserList.TabIndex = 0;
         this.childUserList.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.OnRowEnter);
         this.childUserList.DragOver += new System.Windows.Forms.DragEventHandler(this.OnDragOver);
         this.childUserList.RowLeave += new System.Windows.Forms.DataGridViewCellEventHandler(this.OnRowLeave);
         this.childUserList.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.childUserList_CellMouseDoubleClick);
         this.childUserList.DragEnter += new System.Windows.Forms.DragEventHandler(this.OnDragEnter);
         this.childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(this.childUserList_CurrentCellDirtyStateChanged);
         this.childUserList.DragDrop += new System.Windows.Forms.DragEventHandler(this.OnDragDrop);
         // 
         // culName
         // 
         this.culName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.culName.DataPropertyName = "AgentName";
         this.culName.HeaderText = "ФИО";
         this.culName.Name = "culName";
         this.culName.ReadOnly = true;
         // 
         // tracking
         // 
         this.tracking.DataPropertyName = "Tracking";
         this.tracking.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.ComboBox;
         this.tracking.HeaderText = "Слежение";
         this.tracking.Items.AddRange(new object[] {
            "нет",
            "GSM",
            "торг.точки GPS",
            "маршрут GPS"});
         this.tracking.Name = "tracking";
         // 
         // childDivisions
         // 
         this.childDivisions.Controls.Add(this.childDivisionList);
         this.childDivisions.Location = new System.Drawing.Point(4, 22);
         this.childDivisions.Name = "childDivisions";
         this.childDivisions.Padding = new System.Windows.Forms.Padding(3);
         this.childDivisions.Size = new System.Drawing.Size(333, 246);
         this.childDivisions.TabIndex = 1;
         this.childDivisions.Text = "Подразделения";
         this.childDivisions.UseVisualStyleBackColor = true;
         // 
         // childDivisionList
         // 
         this.childDivisionList.AllowDrop = true;
         this.childDivisionList.AllowUserToAddRows = false;
         this.childDivisionList.AllowUserToDeleteRows = false;
         this.childDivisionList.AllowUserToResizeRows = false;
         this.childDivisionList.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.childDivisionList.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.cdlName});
         this.childDivisionList.Dock = System.Windows.Forms.DockStyle.Fill;
         this.childDivisionList.Location = new System.Drawing.Point(3, 3);
         this.childDivisionList.Name = "childDivisionList";
         this.childDivisionList.ReadOnly = true;
         this.childDivisionList.RowHeadersVisible = false;
         this.childDivisionList.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.childDivisionList.Size = new System.Drawing.Size(327, 240);
         this.childDivisionList.TabIndex = 0;
         this.childDivisionList.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.OnRowEnter);
         this.childDivisionList.DragOver += new System.Windows.Forms.DragEventHandler(this.OnDragOver);
         this.childDivisionList.RowLeave += new System.Windows.Forms.DataGridViewCellEventHandler(this.OnRowLeave);
         this.childDivisionList.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.childDivisionList_CellMouseDoubleClick);
         this.childDivisionList.DragEnter += new System.Windows.Forms.DragEventHandler(this.OnDragEnter);
         this.childDivisionList.DragDrop += new System.Windows.Forms.DragEventHandler(this.OnDragDrop);
         // 
         // cdlName
         // 
         this.cdlName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.cdlName.DataPropertyName = "DivisionName";
         this.cdlName.HeaderText = "Название";
         this.cdlName.Name = "cdlName";
         this.cdlName.ReadOnly = true;
         // 
         // childArticles
         // 
         this.childArticles.Controls.Add(this.tvAccessibleArticles);
         this.childArticles.Location = new System.Drawing.Point(4, 22);
         this.childArticles.Name = "childArticles";
         this.childArticles.Size = new System.Drawing.Size(333, 246);
         this.childArticles.TabIndex = 2;
         this.childArticles.Text = "Доступный товар";
         this.childArticles.UseVisualStyleBackColor = true;
         // 
         // tvAccessibleArticles
         // 
         this.tvAccessibleArticles.CheckBoxes = true;
         this.tvAccessibleArticles.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvAccessibleArticles.Location = new System.Drawing.Point(0, 0);
         this.tvAccessibleArticles.Name = "tvAccessibleArticles";
         this.tvAccessibleArticles.Size = new System.Drawing.Size(333, 246);
         this.tvAccessibleArticles.TabIndex = 1;
         this.tvAccessibleArticles.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAccessibleArticles_AfterCheck);
         // 
         // setCheif
         // 
         this.setCheif.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.setCheif.Location = new System.Drawing.Point(93, 95);
         this.setCheif.Name = "setCheif";
         this.setCheif.Size = new System.Drawing.Size(250, 23);
         this.setCheif.TabIndex = 14;
         this.setCheif.Text = "Назначить руководителя";
         this.setCheif.UseVisualStyleBackColor = true;
         this.setCheif.Click += new System.EventHandler(this.setCheif_Click);
         // 
         // description
         // 
         this.description.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.description.Location = new System.Drawing.Point(93, 36);
         this.description.Multiline = true;
         this.description.Name = "description";
         this.description.Size = new System.Drawing.Size(250, 55);
         this.description.TabIndex = 11;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(30, 39);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(57, 13);
         this.label2.TabIndex = 10;
         this.label2.Text = "Описание";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(30, 12);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(57, 13);
         this.label1.TabIndex = 9;
         this.label1.Text = "Название";
         // 
         // name
         // 
         this.name.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.name.Location = new System.Drawing.Point(93, 9);
         this.name.Name = "name";
         this.name.Size = new System.Drawing.Size(250, 20);
         this.name.TabIndex = 8;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "AgentName";
         this.dataGridViewTextBoxColumn1.HeaderText = "ФИО";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "DivisionName";
         this.dataGridViewTextBoxColumn2.HeaderText = "Название";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // DivisionForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.tabControl1);
         this.Controls.Add(this.setCheif);
         this.Controls.Add(this.description);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.name);
         this.Name = "DivisionForm";
         this.Size = new System.Drawing.Size(360, 395);
         this.Load += new System.EventHandler(this.DivisionForm_Load);
         this.tabControl1.ResumeLayout(false);
         this.childUsers.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.childUserList)).EndInit();
         this.childDivisions.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.childDivisionList)).EndInit();
         this.childArticles.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TabPage childUsers;
      protected System.Windows.Forms.DataGridView childUserList;
      private System.Windows.Forms.TabPage childDivisions;
      private System.Windows.Forms.DataGridView childDivisionList;
      private System.Windows.Forms.Button setCheif;
      private System.Windows.Forms.TextBox description;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox name;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn cdlName;
      private System.Windows.Forms.DataGridViewTextBoxColumn culName;
      private System.Windows.Forms.DataGridViewComboBoxColumn tracking;
      private System.Windows.Forms.TabPage childArticles;
      public System.Windows.Forms.TreeView tvAccessibleArticles;
      protected System.Windows.Forms.TabControl tabControl1;
   }
}

namespace GRSoft.NapoleonManager
{
   partial class FmTask
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTask));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvTask = new System.Windows.Forms.DataGridView();
         this.clmDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnTask = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDo = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.task = new System.Windows.Forms.TextBox();
         this.doing = new System.Windows.Forms.TextBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvTask);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(653, 393);
         this.splitContainer1.SplitterDistance = 215;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvTask
         // 
         this.dgvTask.AllowUserToAddRows = false;
         this.dgvTask.AllowUserToDeleteRows = false;
         this.dgvTask.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvTask.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmDate,
            this.clmnOrg,
            this.clmnTask,
            this.clmnDo});
         this.dgvTask.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvTask.Location = new System.Drawing.Point(0, 25);
         this.dgvTask.Name = "dgvTask";
         this.dgvTask.RowHeadersVisible = false;
         this.dgvTask.Size = new System.Drawing.Size(653, 190);
         this.dgvTask.TabIndex = 1;
         this.dgvTask.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvTask_CellFormatting);
         this.dgvTask.SelectionChanged += new System.EventHandler(this.dgvTask_SelectionChanged);
         // 
         // clmDate
         // 
         this.clmDate.DataPropertyName = "StrDate";
         this.clmDate.HeaderText = "Дата";
         this.clmDate.Name = "clmDate";
         // 
         // clmnOrg
         // 
         this.clmnOrg.DataPropertyName = "StrOrg";
         this.clmnOrg.HeaderText = "Контрагнет";
         this.clmnOrg.Name = "clmnOrg";
         // 
         // clmnTask
         // 
         this.clmnTask.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnTask.DataPropertyName = "StrTask";
         this.clmnTask.HeaderText = "Задача";
         this.clmnTask.Name = "clmnTask";
         // 
         // clmnDo
         // 
         this.clmnDo.DataPropertyName = "Do";
         this.clmnDo.HeaderText = "Выполнение";
         this.clmnDo.Name = "clmnDo";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnAdd,
            this.btnDel,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(653, 25);
         this.toolStrip1.TabIndex = 2;
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
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.task);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.doing);
         this.splitContainer2.Size = new System.Drawing.Size(653, 174);
         this.splitContainer2.SplitterDistance = 316;
         this.splitContainer2.TabIndex = 0;
         // 
         // task
         // 
         this.task.Dock = System.Windows.Forms.DockStyle.Fill;
         this.task.Location = new System.Drawing.Point(0, 0);
         this.task.Multiline = true;
         this.task.Name = "task";
         this.task.Size = new System.Drawing.Size(316, 174);
         this.task.TabIndex = 0;
         this.task.Leave += new System.EventHandler(this.task_Leave);
         this.task.Enter += new System.EventHandler(this.task_Enter);
         // 
         // doing
         // 
         this.doing.Dock = System.Windows.Forms.DockStyle.Fill;
         this.doing.Enabled = false;
         this.doing.Location = new System.Drawing.Point(0, 0);
         this.doing.Multiline = true;
         this.doing.Name = "doing";
         this.doing.Size = new System.Drawing.Size(333, 174);
         this.doing.TabIndex = 0;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "StrDate";
         this.dataGridViewTextBoxColumn1.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.DataPropertyName = "StrOrg";
         this.dataGridViewTextBoxColumn2.HeaderText = "Контрагнет";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.DataPropertyName = "StrTask";
         this.dataGridViewTextBoxColumn3.HeaderText = "Задача";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // dataGridViewTextBoxColumn4
         // 
         this.dataGridViewTextBoxColumn4.DataPropertyName = "Do";
         this.dataGridViewTextBoxColumn4.HeaderText = "Выполнение";
         this.dataGridViewTextBoxColumn4.Name = "dataGridViewTextBoxColumn4";
         // 
         // FmTask
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(653, 393);
         this.Controls.Add(this.splitContainer1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTask";
         this.Text = "Задачи";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel1.PerformLayout();
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvTask;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnTask;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDo;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn4;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.TextBox task;
      private System.Windows.Forms.TextBox doing;
      private System.Windows.Forms.ToolStripButton btnRefresh;

   }
}
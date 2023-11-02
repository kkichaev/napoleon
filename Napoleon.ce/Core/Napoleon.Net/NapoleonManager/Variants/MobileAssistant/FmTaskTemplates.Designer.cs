namespace GRSoft.NapoleonManager
{
   partial class FmTaskTemplates
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskTemplates));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAddTask = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnTaks = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddTask,
            this.toolStripSeparator1,
            this.toolStripLabel1});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(495, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAddTask
         // 
         this.btnAddTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAddTask.Image = global::GRSoft.NapoleonManager.Properties.Resources.accessorieseditor;
         this.btnAddTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAddTask.Name = "btnAddTask";
         this.btnAddTask.Size = new System.Drawing.Size(23, 22);
         this.btnAddTask.Text = "Добавить задачу";
         this.btnAddTask.Click += new System.EventHandler(this.btnAddTask_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(120, 22);
         this.toolStripLabel1.Text = "Назначить задачу на";
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnTaks});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(495, 364);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvItems_CellDoubleClick);
         // 
         // clmnTaks
         // 
         this.clmnTaks.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnTaks.DataPropertyName = "Text";
         this.clmnTaks.HeaderText = "Задача";
         this.clmnTaks.Name = "clmnTaks";
         this.clmnTaks.ReadOnly = true;
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(163, 3);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(139, 20);
         this.dtpDate.TabIndex = 2;
         // 
         // FmTaskTemplates
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(495, 389);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTaskTemplates";
         this.Text = "Список задач";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.ToolStripButton btnAddTask;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnTaks;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.DateTimePicker dtpDate;
   }
}
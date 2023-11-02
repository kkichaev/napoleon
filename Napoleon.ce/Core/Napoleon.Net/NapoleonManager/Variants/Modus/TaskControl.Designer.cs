namespace GRSoft.NapoleonManager
{
   partial class TaskControl
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnTask = new System.Windows.Forms.Button();
         this.grid = new System.Windows.Forms.DataGridView();
         this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
         this.miEdit = new System.Windows.Forms.ToolStripMenuItem();
         this.miDel = new System.Windows.Forms.ToolStripMenuItem();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.contextMenuStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnTask);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(381, 36);
         this.panel1.TabIndex = 0;
         // 
         // btnTask
         // 
         this.btnTask.Location = new System.Drawing.Point(5, 6);
         this.btnTask.Name = "btnTask";
         this.btnTask.Size = new System.Drawing.Size(75, 23);
         this.btnTask.TabIndex = 0;
         this.btnTask.Text = "Задача";
         this.btnTask.UseVisualStyleBackColor = true;
         this.btnTask.Click += new System.EventHandler(this.btnTask_Click);
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2});
         this.grid.ContextMenuStrip = this.contextMenuStrip1;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 36);
         this.grid.Name = "grid";
         this.grid.RowHeadersVisible = false;
         this.grid.Size = new System.Drawing.Size(381, 185);
         this.grid.TabIndex = 1;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         this.grid.CellMouseDoubleClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.grid_CellMouseDoubleClick);
         // 
         // contextMenuStrip1
         // 
         this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miEdit,
            this.miDel});
         this.contextMenuStrip1.Name = "contextMenuStrip1";
         this.contextMenuStrip1.Size = new System.Drawing.Size(155, 48);
         // 
         // miEdit
         // 
         this.miEdit.Name = "miEdit";
         this.miEdit.Size = new System.Drawing.Size(154, 22);
         this.miEdit.Text = "Редактировать";
         this.miEdit.Click += new System.EventHandler(this.miEdit_Click);
         // 
         // miDel
         // 
         this.miDel.Name = "miDel";
         this.miDel.Size = new System.Drawing.Size(154, 22);
         this.miDel.Text = "Удалить";
         this.miDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Date";
         this.dataGridViewTextBoxColumn1.HeaderText = "Дата";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Text";
         this.dataGridViewTextBoxColumn2.HeaderText = "Текст";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Created";
         dataGridViewCellStyle1.Format = "d";
         dataGridViewCellStyle1.NullValue = null;
         this.Column1.DefaultCellStyle = dataGridViewCellStyle1;
         this.Column1.HeaderText = "Дата";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column2.DataPropertyName = "Text";
         this.Column2.HeaderText = "Текст";
         this.Column2.Name = "Column2";
         // 
         // TaskControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Controls.Add(this.panel1);
         this.Name = "TaskControl";
         this.Size = new System.Drawing.Size(381, 221);
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.contextMenuStrip1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.DataGridView grid;
      private System.Windows.Forms.Button btnTask;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miEdit;
      private System.Windows.Forms.ToolStripMenuItem miDel;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
   }
}

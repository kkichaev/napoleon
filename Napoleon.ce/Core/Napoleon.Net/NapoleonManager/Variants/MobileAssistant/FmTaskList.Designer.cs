namespace GRSoft.NapoleonManager
{
   partial class FmTaskList
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskList));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.btnTask = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnCheck = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnActive = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.cbSelAgents = new System.Windows.Forms.CheckBox();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripLabel1,
            this.toolStripLabel2,
            this.btnTask});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(909, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(25, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.ToolTipText = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(13, 22);
         this.toolStripLabel1.Text = "с";
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(150, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(21, 22);
         this.toolStripLabel2.Text = "по";
         // 
         // btnTask
         // 
         this.btnTask.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnTask.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_calendar_timeline;
         this.btnTask.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnTask.Margin = new System.Windows.Forms.Padding(155, 1, 0, 2);
         this.btnTask.Name = "btnTask";
         this.btnTask.Size = new System.Drawing.Size(23, 22);
         this.btnTask.Text = "Добавить задачи";
         this.btnTask.Click += new System.EventHandler(this.btnTask_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnCheck,
            this.clmnAgent,
            this.clmnDone,
            this.clmnActive});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvItems.Size = new System.Drawing.Size(909, 461);
         this.dgvItems.TabIndex = 1;
         this.dgvItems.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvItems_CellFormatting);
         // 
         // clmnCheck
         // 
         this.clmnCheck.DataPropertyName = "Checked";
         this.clmnCheck.HeaderText = "";
         this.clmnCheck.Name = "clmnCheck";
         this.clmnCheck.Width = 40;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnAgent.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnDone
         // 
         this.clmnDone.DataPropertyName = "Done";
         this.clmnDone.HeaderText = "Выполнено";
         this.clmnDone.Name = "clmnDone";
         this.clmnDone.Width = 120;
         // 
         // clmnActive
         // 
         this.clmnActive.DataPropertyName = "Active";
         this.clmnActive.HeaderText = "Не выполнено";
         this.clmnActive.Name = "clmnActive";
         this.clmnActive.Width = 120;
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(73, 2);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(141, 20);
         this.dtpStart.TabIndex = 6;
         this.dtpStart.Value = new System.DateTime(2010, 4, 22, 0, 0, 0, 0);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(249, 2);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(141, 20);
         this.dtpEnd.TabIndex = 7;
         this.dtpEnd.Value = new System.DateTime(2010, 5, 11, 0, 0, 0, 0);
         // 
         // cbSelAgents
         // 
         this.cbSelAgents.AutoSize = true;
         this.cbSelAgents.Location = new System.Drawing.Point(13, 5);
         this.cbSelAgents.Name = "cbSelAgents";
         this.cbSelAgents.Size = new System.Drawing.Size(15, 14);
         this.cbSelAgents.TabIndex = 8;
         this.cbSelAgents.UseVisualStyleBackColor = true;
         this.cbSelAgents.CheckedChanged += new System.EventHandler(this.cbSelAgents_CheckedChanged);
         // 
         // FmTaskList
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(909, 486);
         this.Controls.Add(this.cbSelAgents);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.KeyPreview = true;
         this.Name = "FmTaskList";
         this.Text = "Выполнение задач";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvItems;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel1;
      protected System.Windows.Forms.ToolStripLabel toolStripLabel2;
      protected System.Windows.Forms.DateTimePicker dtpStart;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnTask;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnCheck;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDone;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnActive;
      private System.Windows.Forms.CheckBox cbSelAgents;
   }
}
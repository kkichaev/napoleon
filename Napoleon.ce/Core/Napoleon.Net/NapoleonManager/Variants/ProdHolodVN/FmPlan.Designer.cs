namespace GRSoft.NapoleonManager
{
   partial class FmPlan
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPlan));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.tsAdd = new System.Windows.Forms.ToolStripButton();
         this.tsEdit = new System.Windows.Forms.ToolStripButton();
         this.tsDelete = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.tgvPlan = new GRSoft.UILib.TreeGridView();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvPlanName = new GRSoft.UILib.TreeGridColumn();
         this.tgvPlanUnit = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvPlanPlan = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvPLanFakt = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvPlanProgress = new System.Windows.Forms.DataGridViewImageColumn();
         this.tgvPlanInstance = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPlan)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 367);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(645, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.cbAgents,
            this.toolStripSeparator1,
            this.tsbRefresh,
            this.tsAdd,
            this.tsEdit,
            this.tsDelete});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(645, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(37, 22);
         this.toolStripLabel1.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.FlatStyle = System.Windows.Forms.FlatStyle.System;
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(170, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Margin = new System.Windows.Forms.Padding(10, 0, 10, 0);
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = ((System.Drawing.Image)(resources.GetObject("tsbRefresh.Image")));
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Margin = new System.Windows.Forms.Padding(7, 1, 0, 2);
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // tsAdd
         // 
         this.tsAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsAdd.Image = ((System.Drawing.Image)(resources.GetObject("tsAdd.Image")));
         this.tsAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsAdd.Name = "tsAdd";
         this.tsAdd.Size = new System.Drawing.Size(23, 22);
         this.tsAdd.Text = "Новый план";
         this.tsAdd.Click += new System.EventHandler(this.tsAdd_Click);
         // 
         // tsEdit
         // 
         this.tsEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsEdit.Image = ((System.Drawing.Image)(resources.GetObject("tsEdit.Image")));
         this.tsEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsEdit.Name = "tsEdit";
         this.tsEdit.Size = new System.Drawing.Size(23, 22);
         this.tsEdit.Text = "Изменить";
         this.tsEdit.Click += new System.EventHandler(this.tsEdit_Click);
         // 
         // tsDelete
         // 
         this.tsDelete.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsDelete.Image = ((System.Drawing.Image)(resources.GetObject("tsDelete.Image")));
         this.tsDelete.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsDelete.Name = "tsDelete";
         this.tsDelete.Size = new System.Drawing.Size(23, 22);
         this.tsDelete.Text = "Удалить";
         this.tsDelete.Click += new System.EventHandler(this.tsDelete_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.tgvPlan);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.panel1.Size = new System.Drawing.Size(645, 342);
         this.panel1.TabIndex = 2;
         // 
         // tgvPlan
         // 
         this.tgvPlan.AllowUserToAddRows = false;
         this.tgvPlan.AllowUserToDeleteRows = false;
         this.tgvPlan.AllowUserToResizeRows = false;
         this.tgvPlan.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.tgvPlanName,
            this.tgvPlanUnit,
            this.tgvPlanPlan,
            this.tgvPLanFakt,
            this.tgvPlanProgress,
            this.tgvPlanInstance});
         this.tgvPlan.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPlan.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvPlan.ImageList = null;
         this.tgvPlan.Location = new System.Drawing.Point(7, 8);
         this.tgvPlan.MultiSelect = false;
         this.tgvPlan.Name = "tgvPlan";
         this.tgvPlan.RowHeadersVisible = false;
         this.tgvPlan.Size = new System.Drawing.Size(631, 326);
         this.tgvPlan.TabIndex = 0;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.FillWeight = 200F;
         this.dataGridViewTextBoxColumn1.HeaderText = "Название";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle2;
         this.dataGridViewTextBoxColumn2.HeaderText = "Ед. изм.";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.HeaderText = "План";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // tgvPlanName
         // 
         this.tgvPlanName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvPlanName.DefaultNodeImage = null;
         this.tgvPlanName.FillWeight = 200F;
         this.tgvPlanName.HeaderText = "Название";
         this.tgvPlanName.Name = "tgvPlanName";
         this.tgvPlanName.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.tgvPlanName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvPlanUnit
         // 
         this.tgvPlanUnit.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvPlanUnit.FillWeight = 30F;
         this.tgvPlanUnit.HeaderText = "Ед. изм.";
         this.tgvPlanUnit.Name = "tgvPlanUnit";
         this.tgvPlanUnit.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvPlanPlan
         // 
         this.tgvPlanPlan.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvPlanPlan.FillWeight = 30F;
         this.tgvPlanPlan.HeaderText = "План";
         this.tgvPlanPlan.Name = "tgvPlanPlan";
         this.tgvPlanPlan.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvPLanFakt
         // 
         this.tgvPLanFakt.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvPLanFakt.FillWeight = 30F;
         this.tgvPLanFakt.HeaderText = "Факт";
         this.tgvPLanFakt.Name = "tgvPLanFakt";
         this.tgvPLanFakt.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // tgvPlanProgress
         // 
         this.tgvPlanProgress.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.tgvPlanProgress.FillWeight = 90F;
         this.tgvPlanProgress.HeaderText = "Прогресс";
         this.tgvPlanProgress.Name = "tgvPlanProgress";
         // 
         // tgvPlanInstance
         // 
         this.tgvPlanInstance.HeaderText = "Plan";
         this.tgvPlanInstance.Name = "tgvPlanInstance";
         this.tgvPlanInstance.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.tgvPlanInstance.Visible = false;
         // 
         // FmPlan
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(645, 389);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPlan";
         this.Text = "Планы:";
         this.Load += new System.EventHandler(this.FmPlan_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.tgvPlan)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
      private GRSoft.UILib.TreeGridView tgvPlan;
      private System.Windows.Forms.ToolStripButton tsAdd;
      private System.Windows.Forms.ToolStripButton tsEdit;
      private System.Windows.Forms.ToolStripButton tsDelete;
      private GRSoft.UILib.TreeGridColumn tgvPlanName;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvPlanUnit;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvPlanPlan;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvPLanFakt;
      private System.Windows.Forms.DataGridViewImageColumn tgvPlanProgress;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvPlanInstance;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class OrderAnalize
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(OrderAnalize));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnAgents = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvOrders = new System.Windows.Forms.DataGridView();
         this.clmnOrg = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbRepType = new System.Windows.Forms.ToolStripComboBox();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrders)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.dgvAgents);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvOrders);
         this.splitContainer1.Size = new System.Drawing.Size(916, 575);
         this.splitContainer1.SplitterDistance = 305;
         this.splitContainer1.TabIndex = 0;
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnChecked,
            this.clmnAgents});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvAgents.Location = new System.Drawing.Point(0, 0);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.Size = new System.Drawing.Size(305, 575);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvAgents_CurrentCellDirtyStateChanged);
         // 
         // clmnChecked
         // 
         this.clmnChecked.DataPropertyName = "Checked";
         this.clmnChecked.HeaderText = "";
         this.clmnChecked.Name = "clmnChecked";
         this.clmnChecked.Width = 45;
         // 
         // clmnAgents
         // 
         this.clmnAgents.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgents.DataPropertyName = "Name";
         this.clmnAgents.HeaderText = "Агенты";
         this.clmnAgents.Name = "clmnAgents";
         // 
         // dgvOrders
         // 
         this.dgvOrders.AllowUserToAddRows = false;
         this.dgvOrders.AllowUserToDeleteRows = false;
         this.dgvOrders.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrders.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnOrg});
         this.dgvOrders.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrders.Location = new System.Drawing.Point(0, 0);
         this.dgvOrders.Name = "dgvOrders";
         this.dgvOrders.ReadOnly = true;
         this.dgvOrders.RowHeadersVisible = false;
         this.dgvOrders.Size = new System.Drawing.Size(607, 575);
         this.dgvOrders.TabIndex = 0;
         this.dgvOrders.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrders_CellFormatting);
         // 
         // clmnOrg
         // 
         this.clmnOrg.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnOrg.DataPropertyName = "Name";
         this.clmnOrg.HeaderText = "Контрагент";
         this.clmnOrg.Name = "clmnOrg";
         this.clmnOrg.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbRepType});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(916, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbRepType
         // 
         this.cbRepType.Items.AddRange(new object[] {
            "Активный ассортимент",
            "Кооличество заказов"});
         this.cbRepType.Name = "cbRepType";
         this.cbRepType.Size = new System.Drawing.Size(200, 25);
         this.cbRepType.SelectedIndexChanged += new System.EventHandler(this.toolStripComboBox1_SelectedIndexChanged);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Агенты";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Контрагент";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // OrderAnalize
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(916, 600);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "OrderAnalize";
         this.Text = "Анализ заявок";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrders)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox cbRepType;
      private System.Windows.Forms.DataGridView dgvOrders;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnChecked;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnOrg;
   }
}
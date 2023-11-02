namespace GRSoft.NapoleonManager
{
   partial class FmRouteApproval
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRouteApproval));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.lbAgents = new System.Windows.Forms.ListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnApply = new System.Windows.Forms.ToolStripButton();
         this.btnReject = new System.Windows.Forms.ToolStripButton();
         this.dgvRoute = new System.Windows.Forms.DataGridView();
         this.name = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.mon = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Tue = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Wed = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Thu = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Fri = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Sat = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.Sun = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStripStatusLabel1 = new System.Windows.Forms.ToolStripStatusLabel();
         this.lbDate = new System.Windows.Forms.ToolStripStatusLabel();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRoute)).BeginInit();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.lbAgents);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvRoute);
         this.splitContainer1.Panel2.Controls.Add(this.statusStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(706, 419);
         this.splitContainer1.SplitterDistance = 235;
         this.splitContainer1.SplitterWidth = 3;
         this.splitContainer1.TabIndex = 0;
         // 
         // lbAgents
         // 
         this.lbAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbAgents.FormattingEnabled = true;
         this.lbAgents.ItemHeight = 15;
         this.lbAgents.Location = new System.Drawing.Point(0, 25);
         this.lbAgents.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.lbAgents.Name = "lbAgents";
         this.lbAgents.Size = new System.Drawing.Size(235, 394);
         this.lbAgents.TabIndex = 0;
         this.lbAgents.SelectedIndexChanged += new System.EventHandler(this.lbAgents_SelectedIndexChanged);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnApply,
            this.btnReject});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(235, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnApply
         // 
         this.btnApply.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnApply.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_apply;
         this.btnApply.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnApply.Name = "btnApply";
         this.btnApply.Size = new System.Drawing.Size(23, 22);
         this.btnApply.Text = "Утвердить";
         this.btnApply.Click += new System.EventHandler(this.btnApply_Click);
         // 
         // btnReject
         // 
         this.btnReject.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnReject.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.btnReject.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnReject.Name = "btnReject";
         this.btnReject.Size = new System.Drawing.Size(23, 22);
         this.btnReject.Text = "Отклонить";
         this.btnReject.Click += new System.EventHandler(this.btnReject_Click);
         // 
         // dgvRoute
         // 
         this.dgvRoute.AllowUserToAddRows = false;
         this.dgvRoute.AllowUserToDeleteRows = false;
         this.dgvRoute.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvRoute.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.name,
            this.mon,
            this.Tue,
            this.Wed,
            this.Thu,
            this.Fri,
            this.Sat,
            this.Sun});
         this.dgvRoute.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvRoute.Location = new System.Drawing.Point(0, 0);
         this.dgvRoute.Name = "dgvRoute";
         this.dgvRoute.ReadOnly = true;
         this.dgvRoute.RowHeadersVisible = false;
         this.dgvRoute.Size = new System.Drawing.Size(468, 397);
         this.dgvRoute.TabIndex = 0;
         // 
         // name
         // 
         this.name.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.name.HeaderText = "Организация";
         this.name.Name = "name";
         this.name.ReadOnly = true;
         // 
         // mon
         // 
         this.mon.HeaderText = "Пн";
         this.mon.Name = "mon";
         this.mon.ReadOnly = true;
         this.mon.Width = 25;
         // 
         // Tue
         // 
         this.Tue.HeaderText = "Вт";
         this.Tue.Name = "Tue";
         this.Tue.ReadOnly = true;
         this.Tue.Width = 25;
         // 
         // Wed
         // 
         this.Wed.HeaderText = "Ср";
         this.Wed.Name = "Wed";
         this.Wed.ReadOnly = true;
         this.Wed.Width = 25;
         // 
         // Thu
         // 
         this.Thu.HeaderText = "Чт";
         this.Thu.Name = "Thu";
         this.Thu.ReadOnly = true;
         this.Thu.Width = 25;
         // 
         // Fri
         // 
         this.Fri.HeaderText = "Пт";
         this.Fri.Name = "Fri";
         this.Fri.ReadOnly = true;
         this.Fri.Width = 25;
         // 
         // Sat
         // 
         this.Sat.HeaderText = "Сб";
         this.Sat.Name = "Sat";
         this.Sat.ReadOnly = true;
         this.Sat.Width = 25;
         // 
         // Sun
         // 
         this.Sun.HeaderText = "Вс";
         this.Sun.Name = "Sun";
         this.Sun.ReadOnly = true;
         this.Sun.Width = 25;
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel1,
            this.lbDate});
         this.statusStrip1.Location = new System.Drawing.Point(0, 397);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(468, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStripStatusLabel1
         // 
         this.toolStripStatusLabel1.Name = "toolStripStatusLabel1";
         this.toolStripStatusLabel1.Size = new System.Drawing.Size(165, 17);
         this.toolStripStatusLabel1.Text = "Дата последнего изменения:";
         // 
         // lbDate
         // 
         this.lbDate.Name = "lbDate";
         this.lbDate.Size = new System.Drawing.Size(31, 17);
         this.lbDate.Text = "Date";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Организация";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // FmRouteApproval
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(706, 419);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmRouteApproval";
         this.Text = "Утверждение маршрута";
         this.Load += new System.EventHandler(this.FmRouteApproval_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvRoute)).EndInit();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ListBox lbAgents;
      private System.Windows.Forms.ToolStripButton btnApply;
      private System.Windows.Forms.ToolStripButton btnReject;
      private System.Windows.Forms.DataGridView dgvRoute;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn name;
      private System.Windows.Forms.DataGridViewCheckBoxColumn mon;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Tue;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Wed;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Thu;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Fri;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Sat;
      private System.Windows.Forms.DataGridViewCheckBoxColumn Sun;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel lbDate;
      private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel1;
   }
}
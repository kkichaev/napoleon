namespace GRSoft.Ads.Dispatcher
{
   partial class FmTaskReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskReport));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.dgvTask = new System.Windows.Forms.DataGridView();
         this.tgvColumnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dateTimeField2 = new GRSoft.Ads.Dispatcher.DateTimeField();
         this.dateTimeField1 = new GRSoft.Ads.Dispatcher.DateTimeField();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 353);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(826, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbAgents,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(826, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(121, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // dgvTask
         // 
         this.dgvTask.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvTask.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.tgvColumnAgent});
         this.dgvTask.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvTask.Location = new System.Drawing.Point(0, 25);
         this.dgvTask.Name = "dgvTask";
         this.dgvTask.Size = new System.Drawing.Size(826, 328);
         this.dgvTask.TabIndex = 2;
         // 
         // tgvColumnAgent
         // 
         this.tgvColumnAgent.HeaderText = "Column1";
         this.tgvColumnAgent.Name = "tgvColumnAgent";
         // 
         // dateTimeField2
         // 
         this.dateTimeField2.Location = new System.Drawing.Point(161, 2);
         this.dateTimeField2.Mode = GRSoft.Ads.Dispatcher.DateTimeField.TMode.finish;
         this.dateTimeField2.Name = "dateTimeField2";
         this.dateTimeField2.Size = new System.Drawing.Size(140, 20);
         this.dateTimeField2.TabIndex = 6;
         this.dateTimeField2.Text = "dateTimeField2";
         // 
         // dateTimeField1
         // 
         this.dateTimeField1.Location = new System.Drawing.Point(0, 0);
         this.dateTimeField1.Mode = GRSoft.Ads.Dispatcher.DateTimeField.TMode.start;
         this.dateTimeField1.Name = "dateTimeField1";
         this.dateTimeField1.Size = new System.Drawing.Size(0, 0);
         this.dateTimeField1.TabIndex = 5;
         // 
         // FmTaskReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(826, 375);
         this.Controls.Add(this.dateTimeField2);
         this.Controls.Add(this.dateTimeField1);
         this.Controls.Add(this.dgvTask);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTaskReport";
         this.Text = "Отчет по задачам";
         this.Load += new System.EventHandler(this.FmTaskReport_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvTask)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvTask;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.DataGridViewTextBoxColumn tgvColumnAgent;
      private DateTimeField dateTimeField1;
      private DateTimeField dateTimeField2;
   }
}
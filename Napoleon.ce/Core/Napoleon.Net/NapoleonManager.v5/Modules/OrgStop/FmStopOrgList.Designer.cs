namespace GRSoft.NapoleonManager
{
   partial class FmStopOrgList
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmStopOrgList));
         this.dgvOrgs = new System.Windows.Forms.DataGridView();
         this.clmnStop = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbSave = new System.Windows.Forms.ToolStripButton();
         this.tsSearch = new System.Windows.Forms.ToolStripTextBox();
         this.tsClearSearch = new System.Windows.Forms.ToolStripButton();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvOrgs
         // 
         this.dgvOrgs.AllowUserToAddRows = false;
         this.dgvOrgs.AllowUserToDeleteRows = false;
         this.dgvOrgs.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvOrgs.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnStop,
            this.clmnName});
         this.dgvOrgs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvOrgs.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvOrgs.Location = new System.Drawing.Point(0, 25);
         this.dgvOrgs.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.dgvOrgs.Name = "dgvOrgs";
         this.dgvOrgs.RowHeadersVisible = false;
         this.dgvOrgs.Size = new System.Drawing.Size(384, 362);
         this.dgvOrgs.TabIndex = 0;
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         this.dgvOrgs.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.dgvOrgs_ColumnHeaderMouseClick);
         this.dgvOrgs.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvOrgs_CurrentCellDirtyStateChanged_1);
         // 
         // clmnStop
         // 
         this.clmnStop.DataPropertyName = "Stopped";
         this.clmnStop.FillWeight = 40F;
         this.clmnStop.HeaderText = "";
         this.clmnStop.Name = "clmnStop";
         this.clmnStop.Width = 40;
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.FillWeight = 500F;
         this.clmnName.HeaderText = "Наименование";
         this.clmnName.Name = "clmnName";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbSave,
            this.tsSearch,
            this.tsClearSearch});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(384, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbSave
         // 
         this.tbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tbSave.Enabled = false;
         this.tbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbSave.Name = "tbSave";
         this.tbSave.Size = new System.Drawing.Size(23, 22);
         this.tbSave.Text = "toolStripButton1";
         this.tbSave.Click += new System.EventHandler(this.tbSave_Click);
         // 
         // tsSearch
         // 
         this.tsSearch.Name = "tsSearch";
         this.tsSearch.Size = new System.Drawing.Size(126, 25);
         this.tsSearch.TextChanged += new System.EventHandler(this.tsSearch_TextChanged);
         // 
         // tsClearSearch
         // 
         this.tsClearSearch.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsClearSearch.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsClearSearch.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsClearSearch.Name = "tsClearSearch";
         this.tsClearSearch.Size = new System.Drawing.Size(23, 22);
         this.tsClearSearch.Text = "toolStripButton1";
         this.tsClearSearch.Click += new System.EventHandler(this.tsClearSearch_Click);
         // 
         // timer1
         // 
         this.timer1.Interval = 200;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmStopOrgList
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(384, 387);
         this.Controls.Add(this.dgvOrgs);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmStopOrgList";
         this.Text = "Стоп-лист";
         ((System.ComponentModel.ISupportInitialize)(this.dgvOrgs)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.DataGridView dgvOrgs;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tbSave;
      private System.Windows.Forms.ToolStripTextBox tsSearch;
      private System.Windows.Forms.ToolStripButton tsClearSearch;
      public System.Windows.Forms.DataGridViewCheckBoxColumn clmnStop;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName;
      private System.Windows.Forms.Timer timer1;
   }
}
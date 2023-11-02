namespace GRSoft.NapoleonManager
{
   partial class FmAgentPlan
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentPlan));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnPrint = new System.Windows.Forms.ToolStripButton();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.tgvPrice = new GRSoft.UILib.TreeGridView();
         this.Column1 = new GRSoft.UILib.TreeGridColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.btnRefresh,
            this.btnPrint});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(621, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Enabled = false;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
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
         // btnPrint
         // 
         this.btnPrint.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPrint.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_print;
         this.btnPrint.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPrint.Name = "btnPrint";
         this.btnPrint.Size = new System.Drawing.Size(23, 22);
         this.btnPrint.Text = "toolStripButton1";
         this.btnPrint.Visible = false;
         // 
         // dtpDate
         // 
         this.dtpDate.CustomFormat = "MMM yyyy";
         this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpDate.Location = new System.Drawing.Point(212, 2);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.ShowUpDown = true;
         this.dtpDate.Size = new System.Drawing.Size(91, 20);
         this.dtpDate.TabIndex = 2;
         // 
         // cbAgent
         // 
         this.cbAgent.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(80, 1);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(121, 23);
         this.cbAgent.TabIndex = 6;
         this.cbAgent.SelectionChangeCommitted += new System.EventHandler(this.cbAgent_SelectionChangeCommitted);
         // 
         // tgvPrice
         // 
         this.tgvPrice.AllowUserToAddRows = false;
         this.tgvPrice.AllowUserToDeleteRows = false;
         this.tgvPrice.AllowUserToResizeRows = false;
         this.tgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3,
            this.Column4});
         this.tgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.tgvPrice.ImageList = null;
         this.tgvPrice.Location = new System.Drawing.Point(0, 25);
         this.tgvPrice.MultiSelect = false;
         this.tgvPrice.Name = "tgvPrice";
         this.tgvPrice.RowHeadersVisible = false;
         this.tgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvPrice.Size = new System.Drawing.Size(621, 500);
         this.tgvPrice.TabIndex = 5;
         this.tgvPrice.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.tgvPrice_CellEndEdit);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DefaultNodeImage = null;
         this.Column1.HeaderText = "Папка/Наименование";
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         this.Column1.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // Column2
         // 
         this.Column2.HeaderText = "План";
         this.Column2.Name = "Column2";
         this.Column2.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.Column2.Width = 60;
         // 
         // Column3
         // 
         this.Column3.HeaderText = "Факт";
         this.Column3.Name = "Column3";
         this.Column3.ReadOnly = true;
         this.Column3.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.Column3.Width = 60;
         // 
         // Column4
         // 
         this.Column4.HeaderText = "%";
         this.Column4.Name = "Column4";
         this.Column4.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         this.Column4.Width = 60;
         // 
         // FmAgentPlan
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(5F, 15F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(621, 525);
         this.Controls.Add(this.cbAgent);
         this.Controls.Add(this.tgvPrice);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial Narrow", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmAgentPlan";
         this.Text = "План";
         this.Load += new System.EventHandler(this.FmAgentPlan_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmAgentPlan_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DateTimePicker dtpDate;
      protected GRSoft.UILib.TreeGridView tgvPrice;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.ToolStripButton btnPrint;
      private GRSoft.UILib.TreeGridColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmFacing
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFacing));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.grid = new System.Windows.Forms.DataGridView();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column6 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column7 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.detail = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.listView = new System.Windows.Forms.ListView();
         this.imageList = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.btnLoadPhoto = new System.Windows.Forms.ToolStripButton();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbLoadVisit = new System.Windows.Forms.CheckBox();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.grid)).BeginInit();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.detail)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(831, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "toolStripButton2";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 505);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(831, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.grid);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.splitContainer2);
         this.splitContainer1.Size = new System.Drawing.Size(831, 480);
         this.splitContainer1.SplitterDistance = 244;
         this.splitContainer1.TabIndex = 2;
         // 
         // grid
         // 
         this.grid.AllowUserToAddRows = false;
         this.grid.AllowUserToDeleteRows = false;
         this.grid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.grid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column4,
            this.Column5,
            this.Column6,
            this.Column7});
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.Location = new System.Drawing.Point(0, 0);
         this.grid.Name = "grid";
         this.grid.Size = new System.Drawing.Size(831, 244);
         this.grid.TabIndex = 0;
         this.grid.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.grid_CellFormatting);
         this.grid.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.grid_RowEnter);
         // 
         // Column4
         // 
         this.Column4.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column4.DataPropertyName = "OrgName";
         this.Column4.HeaderText = "Контрагент";
         this.Column4.Name = "Column4";
         // 
         // Column5
         // 
         this.Column5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column5.DataPropertyName = "AgentName";
         this.Column5.HeaderText = "ТП";
         this.Column5.Name = "Column5";
         // 
         // Column6
         // 
         this.Column6.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column6.DataPropertyName = "Created";
         this.Column6.HeaderText = "Дата создания";
         this.Column6.Name = "Column6";
         // 
         // Column7
         // 
         this.Column7.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column7.DataPropertyName = "Sended";
         this.Column7.HeaderText = "Дата передачи";
         this.Column7.Name = "Column7";
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 0);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.detail);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.listView);
         this.splitContainer2.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer2.Size = new System.Drawing.Size(831, 232);
         this.splitContainer2.SplitterDistance = 397;
         this.splitContainer2.TabIndex = 0;
         // 
         // detail
         // 
         this.detail.AllowUserToAddRows = false;
         this.detail.AllowUserToDeleteRows = false;
         this.detail.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.detail.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column2,
            this.Column3});
         this.detail.Dock = System.Windows.Forms.DockStyle.Fill;
         this.detail.Location = new System.Drawing.Point(0, 0);
         this.detail.Name = "detail";
         this.detail.Size = new System.Drawing.Size(397, 232);
         this.detail.TabIndex = 0;
         this.detail.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.detail_CellValueChanged);
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Name";
         this.Column1.HeaderText = "Наименование";
         this.Column1.Name = "Column1";
         // 
         // Column2
         // 
         this.Column2.DataPropertyName = "Qty1";
         this.Column2.HeaderText = "Кол ТП";
         this.Column2.Name = "Column2";
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "Qty2";
         this.Column3.HeaderText = "Кол Аудитор";
         this.Column3.Name = "Column3";
         // 
         // listView
         // 
         this.listView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listView.LargeImageList = this.imageList;
         this.listView.Location = new System.Drawing.Point(0, 25);
         this.listView.Name = "listView";
         this.listView.Size = new System.Drawing.Size(430, 207);
         this.listView.TabIndex = 0;
         this.listView.UseCompatibleStateImageBehavior = false;
         this.listView.DoubleClick += new System.EventHandler(this.listView_DoubleClick);
         // 
         // imageList
         // 
         this.imageList.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
         this.imageList.ImageSize = new System.Drawing.Size(115, 115);
         this.imageList.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnLoadPhoto});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(430, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // btnLoadPhoto
         // 
         this.btnLoadPhoto.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnLoadPhoto.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnLoadPhoto.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnLoadPhoto.Name = "btnLoadPhoto";
         this.btnLoadPhoto.Size = new System.Drawing.Size(23, 22);
         this.btnLoadPhoto.Text = "Получить фотографии";
         this.btnLoadPhoto.Click += new System.EventHandler(this.btnLoadPhoto_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2016, 2, 9, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(86, 0);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 25);
         this.dpv.Start = new System.DateTime(2016, 2, 9, 0, 0, 0, 0);
         this.dpv.TabIndex = 3;
         // 
         // cbLoadVisit
         // 
         this.cbLoadVisit.AutoSize = true;
         this.cbLoadVisit.Checked = true;
         this.cbLoadVisit.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLoadVisit.Location = new System.Drawing.Point(459, 4);
         this.cbLoadVisit.Name = "cbLoadVisit";
         this.cbLoadVisit.Size = new System.Drawing.Size(110, 18);
         this.cbLoadVisit.TabIndex = 4;
         this.cbLoadVisit.Text = "Загружать фото";
         this.cbLoadVisit.UseVisualStyleBackColor = true;
         // 
         // FmFacing
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(831, 527);
         this.Controls.Add(this.cbLoadVisit);
         this.Controls.Add(this.dpv);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmFacing";
         this.Text = "Фэйсинг";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmFacing_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.grid)).EndInit();
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.Panel2.PerformLayout();
         this.splitContainer2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.detail)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.DataGridView grid;
      private DatePeriodView dpv;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridView detail;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column6;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column7;
      private System.Windows.Forms.CheckBox cbLoadVisit;
      private System.Windows.Forms.ListView listView;
      private System.Windows.Forms.ImageList imageList;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton btnLoadPhoto;
   }
}
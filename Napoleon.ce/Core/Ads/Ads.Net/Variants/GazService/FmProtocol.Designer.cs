namespace GRSoft.Ads
{
   partial class FmProtocol
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmProtocol));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.lbBrigades = new System.Windows.Forms.ListBox();
         this.dgvProtocol = new System.Windows.Forms.DataGridView();
         this.dgvProtocolName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvProtocolAssigned = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvProtocol)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnDel,
            this.toolStripSeparator1,
            this.btnRefresh,
            this.btnSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(492, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = ((System.Drawing.Image)(resources.GetObject("btnAdd.Image")));
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = ((System.Drawing.Image)(resources.GetObject("btnDel.Image")));
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = ((System.Drawing.Image)(resources.GetObject("btnSave.Image")));
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "btnSave";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.lbBrigades);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dgvProtocol);
         this.splitContainer1.Size = new System.Drawing.Size(492, 347);
         this.splitContainer1.SplitterDistance = 206;
         this.splitContainer1.TabIndex = 1;
         // 
         // lbBrigades
         // 
         this.lbBrigades.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbBrigades.FormattingEnabled = true;
         this.lbBrigades.ItemHeight = 14;
         this.lbBrigades.Location = new System.Drawing.Point(0, 0);
         this.lbBrigades.Name = "lbBrigades";
         this.lbBrigades.Size = new System.Drawing.Size(206, 340);
         this.lbBrigades.TabIndex = 0;
         this.lbBrigades.SelectedIndexChanged += new System.EventHandler(this.lbBrigades_SelectedIndexChanged);
         this.lbBrigades.Format += new System.Windows.Forms.ListControlConvertEventHandler(this.lbBrigades_Format);
         // 
         // dgvProtocol
         // 
         this.dgvProtocol.AllowUserToAddRows = false;
         this.dgvProtocol.AllowUserToDeleteRows = false;
         this.dgvProtocol.AllowUserToResizeRows = false;
         this.dgvProtocol.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvProtocol.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvProtocolName,
            this.dgvProtocolAssigned});
         this.dgvProtocol.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvProtocol.Location = new System.Drawing.Point(0, 0);
         this.dgvProtocol.MultiSelect = false;
         this.dgvProtocol.Name = "dgvProtocol";
         this.dgvProtocol.RowHeadersVisible = false;
         this.dgvProtocol.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvProtocol.Size = new System.Drawing.Size(282, 347);
         this.dgvProtocol.TabIndex = 0;
         // 
         // dgvProtocolName
         // 
         this.dgvProtocolName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvProtocolName.DataPropertyName = "Number";
         this.dgvProtocolName.HeaderText = "Серия/Номер";
         this.dgvProtocolName.Name = "dgvProtocolName";
         // 
         // dgvProtocolAssigned
         // 
         this.dgvProtocolAssigned.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvProtocolAssigned.DataPropertyName = "AssignedStr";
         this.dgvProtocolAssigned.HeaderText = "Дата";
         this.dgvProtocolAssigned.Name = "dgvProtocolAssigned";
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 350);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(492, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // FmProtocol
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(492, 372);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmProtocol";
         this.Text = "Протоколы";
         this.Load += new System.EventHandler(this.FmSertificate_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvProtocol)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridView dgvProtocol;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ListBox lbBrigades;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvProtocolName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvProtocolAssigned;
   }
}
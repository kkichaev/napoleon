namespace GRSoft.NapoleonManager
{
   partial class FmBonuses
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmBonuses));
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
         System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.tsbRefresh = new System.Windows.Forms.ToolStripButton();
         this.dgvBonuses = new System.Windows.Forms.DataGridView();
         this.lblFrom = new System.Windows.Forms.Label();
         this.dtFromDate = new System.Windows.Forms.DateTimePicker();
         this.lblTill = new System.Windows.Forms.Label();
         this.dtTillDate = new System.Windows.Forms.DateTimePicker();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnBonusFrom = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnBonusTill = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBonuses)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAdd,
            this.tsbRemove,
            this.tsbRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(492, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = ((System.Drawing.Image)(resources.GetObject("tsbAdd.Image")));
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(23, 22);
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = ((System.Drawing.Image)(resources.GetObject("tsbRemove.Image")));
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(23, 22);
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // tsbRefresh
         // 
         this.tsbRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.tsbRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRefresh.Name = "tsbRefresh";
         this.tsbRefresh.Size = new System.Drawing.Size(23, 22);
         this.tsbRefresh.Text = "Обновить";
         this.tsbRefresh.ToolTipText = "Обновить";
         this.tsbRefresh.Click += new System.EventHandler(this.tsbRefresh_Click);
         // 
         // dgvBonuses
         // 
         this.dgvBonuses.AllowUserToAddRows = false;
         this.dgvBonuses.AllowUserToDeleteRows = false;
         this.dgvBonuses.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvBonuses.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.clmnBonusFrom,
            this.clmnBonusTill});
         this.dgvBonuses.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvBonuses.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvBonuses.Location = new System.Drawing.Point(0, 25);
         this.dgvBonuses.Name = "dgvBonuses";
         this.dgvBonuses.RowHeadersVisible = false;
         this.dgvBonuses.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvBonuses.Size = new System.Drawing.Size(492, 348);
         this.dgvBonuses.TabIndex = 0;
         this.dgvBonuses.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvBonuses_CellDoubleClick);
         // 
         // lblFrom
         // 
         this.lblFrom.AutoSize = true;
         this.lblFrom.Location = new System.Drawing.Point(110, 5);
         this.lblFrom.Name = "lblFrom";
         this.lblFrom.Size = new System.Drawing.Size(20, 13);
         this.lblFrom.TabIndex = 4;
         this.lblFrom.Text = "С: ";
         // 
         // dtFromDate
         // 
         this.dtFromDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtFromDate.Location = new System.Drawing.Point(130, 3);
         this.dtFromDate.Name = "dtFromDate";
         this.dtFromDate.Size = new System.Drawing.Size(108, 20);
         this.dtFromDate.TabIndex = 4;
         // 
         // lblTill
         // 
         this.lblTill.AutoSize = true;
         this.lblTill.Location = new System.Drawing.Point(245, 5);
         this.lblTill.Name = "lblTill";
         this.lblTill.Size = new System.Drawing.Size(27, 13);
         this.lblTill.TabIndex = 2;
         this.lblTill.Text = "По: ";
         // 
         // dtTillDate
         // 
         this.dtTillDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
         this.dtTillDate.Location = new System.Drawing.Point(275, 3);
         this.dtTillDate.Name = "dtTillDate";
         this.dtTillDate.Size = new System.Drawing.Size(108, 20);
         this.dtTillDate.TabIndex = 4;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "From";
         dataGridViewCellStyle3.Format = "d";
         dataGridViewCellStyle3.NullValue = null;
         this.dataGridViewTextBoxColumn1.DefaultCellStyle = dataGridViewCellStyle3;
         this.dataGridViewTextBoxColumn1.HeaderText = "Дата начала";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         this.dataGridViewTextBoxColumn1.ReadOnly = true;
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.DataPropertyName = "Till";
         dataGridViewCellStyle4.Format = "d";
         this.dataGridViewTextBoxColumn2.DefaultCellStyle = dataGridViewCellStyle4;
         this.dataGridViewTextBoxColumn2.HeaderText = "Дата окончания";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         this.dataGridViewTextBoxColumn2.ReadOnly = true;
         // 
         // Column1
         // 
         this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column1.DataPropertyName = "Caption";
         this.Column1.HeaderText = "Акция";
         this.Column1.Name = "Column1";
         // 
         // clmnBonusFrom
         // 
         this.clmnBonusFrom.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnBonusFrom.DataPropertyName = "Start";
         dataGridViewCellStyle1.Format = "d";
         dataGridViewCellStyle1.NullValue = null;
         this.clmnBonusFrom.DefaultCellStyle = dataGridViewCellStyle1;
         this.clmnBonusFrom.FillWeight = 50F;
         this.clmnBonusFrom.HeaderText = "Дата начала";
         this.clmnBonusFrom.Name = "clmnBonusFrom";
         this.clmnBonusFrom.ReadOnly = true;
         // 
         // clmnBonusTill
         // 
         this.clmnBonusTill.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnBonusTill.DataPropertyName = "Till";
         dataGridViewCellStyle2.Format = "d";
         this.clmnBonusTill.DefaultCellStyle = dataGridViewCellStyle2;
         this.clmnBonusTill.FillWeight = 50F;
         this.clmnBonusTill.HeaderText = "Дата окончания";
         this.clmnBonusTill.Name = "clmnBonusTill";
         this.clmnBonusTill.ReadOnly = true;
         // 
         // FmBonuses
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(492, 373);
         this.Controls.Add(this.lblFrom);
         this.Controls.Add(this.dtFromDate);
         this.Controls.Add(this.lblTill);
         this.Controls.Add(this.dtTillDate);
         this.Controls.Add(this.dgvBonuses);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmBonuses";
         this.Text = "Акции";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvBonuses)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvBonuses;
      private System.Windows.Forms.ToolStripButton tsbRefresh;
      private System.Windows.Forms.Label lblFrom;
      private System.Windows.Forms.DateTimePicker dtFromDate;
      private System.Windows.Forms.Label lblTill;
      private System.Windows.Forms.DateTimePicker dtTillDate;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnBonusFrom;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnBonusTill;
      
      
   }
}
namespace GRSoft.Ads
{
   partial class FmCounter
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCounter));
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.bntAdd = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.dgvCounter = new System.Windows.Forms.DataGridView();
         this.dgvCounterName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvCounter)).BeginInit();
         this.SuspendLayout();
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 286);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(404, 22);
         this.statusStrip1.TabIndex = 0;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.bntAdd,
            this.btnDel,
            this.toolStripSeparator1,
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(404, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // bntAdd
         // 
         this.bntAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.bntAdd.Image = ((System.Drawing.Image)(resources.GetObject("bntAdd.Image")));
         this.bntAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.bntAdd.Name = "bntAdd";
         this.bntAdd.Size = new System.Drawing.Size(23, 22);
         this.bntAdd.Text = "Добавить";
         this.bntAdd.Click += new System.EventHandler(this.bntAdd_Click);
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
         // dgvCounter
         // 
         this.dgvCounter.AllowUserToAddRows = false;
         this.dgvCounter.AllowUserToDeleteRows = false;
         this.dgvCounter.AllowUserToResizeRows = false;
         this.dgvCounter.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvCounter.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvCounterName});
         this.dgvCounter.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvCounter.Location = new System.Drawing.Point(0, 25);
         this.dgvCounter.MultiSelect = false;
         this.dgvCounter.Name = "dgvCounter";
         this.dgvCounter.RowHeadersVisible = false;
         this.dgvCounter.Size = new System.Drawing.Size(404, 261);
         this.dgvCounter.TabIndex = 2;
         // 
         // dgvCounterName
         // 
         this.dgvCounterName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvCounterName.DataPropertyName = "Name";
         this.dgvCounterName.HeaderText = "Наименование";
         this.dgvCounterName.Name = "dgvCounterName";
         // 
         // FmCounter
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(404, 308);
         this.Controls.Add(this.dgvCounter);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmCounter";
         this.Text = "Счетчики";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvCounter)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvCounter;
      private System.Windows.Forms.ToolStripButton bntAdd;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvCounterName;
   }
}
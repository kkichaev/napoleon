namespace GRSoft.NapoleonManager
{
   partial class FmPriceViewAH
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceViewAH));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbPriceType = new System.Windows.Forms.ToolStripComboBox();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnClearSearch = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.FPName = new GRSoft.UILib.TreeGridColumn();
         this.FPCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.FPQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tgvPrice = new GRSoft.UILib.TreeGridView();
         this.clmnItem = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCost = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnQty = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbPriceType,
            this.btnRefresh,
            this.toolStripSeparator1,
            this.tbFind,
            this.btnClearSearch,
            this.toolStripSeparator2});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(645, 25);
         this.toolStrip1.TabIndex = 0;
         // 
         // cbPriceType
         // 
         this.cbPriceType.Name = "cbPriceType";
         this.cbPriceType.Size = new System.Drawing.Size(121, 25);
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = ((System.Drawing.Image)(resources.GetObject("btnRefresh.Image")));
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(10, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.TextChanged += new System.EventHandler(this.tbFind_TextChanged);
         // 
         // btnClearSearch
         // 
         this.btnClearSearch.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnClearSearch.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnClearSearch.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnClearSearch.Name = "btnClearSearch";
         this.btnClearSearch.Size = new System.Drawing.Size(23, 22);
         this.btnClearSearch.Text = "Искать вперед";
         this.btnClearSearch.Click += new System.EventHandler(this.btnClearSearch_Click);
         // 
         // toolStripSeparator2
         // 
         this.toolStripSeparator2.Name = "toolStripSeparator2";
         this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 432);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(645, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // FPName
         // 
         this.FPName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPName.DefaultNodeImage = null;
         this.FPName.FillWeight = 300F;
         this.FPName.HeaderText = "Папка/Наименование";
         this.FPName.Name = "FPName";
         this.FPName.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FPCost
         // 
         this.FPCost.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPCost.HeaderText = "Цена";
         this.FPCost.Name = "FPCost";
         this.FPCost.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPCost.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // FPQty
         // 
         this.FPQty.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.FPQty.HeaderText = "Кол-во";
         this.FPQty.Name = "FPQty";
         this.FPQty.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.FPQty.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.DataPropertyName = "Name";
         this.dataGridViewTextBoxColumn1.HeaderText = "Наименование";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // tgvPrice
         // 
         this.tgvPrice.AllowUserToAddRows = false;
         this.tgvPrice.AllowUserToDeleteRows = false;
         this.tgvPrice.AllowUserToResizeRows = false;
         this.tgvPrice.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnItem,
            this.clmnCost,
            this.clmnQty});
         this.tgvPrice.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvPrice.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvPrice.ImageList = null;
         this.tgvPrice.Location = new System.Drawing.Point(0, 25);
         this.tgvPrice.MultiSelect = false;
         this.tgvPrice.Name = "tgvPrice";
         this.tgvPrice.RowHeadersVisible = false;
         this.tgvPrice.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvPrice.Size = new System.Drawing.Size(645, 407);
         this.tgvPrice.TabIndex = 4;
         // 
         // clmnItem
         // 
         this.clmnItem.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnItem.DataPropertyName = "Name";
         this.clmnItem.HeaderText = "Папка/Товар";
         this.clmnItem.Name = "clmnItem";
         this.clmnItem.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnCost
         // 
         this.clmnCost.DataPropertyName = "Cost";
         this.clmnCost.HeaderText = "Цена";
         this.clmnCost.Name = "clmnCost";
         this.clmnCost.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnQty
         // 
         this.clmnQty.DataPropertyName = "Qty";
         this.clmnQty.HeaderText = "Кол-во";
         this.clmnQty.Name = "clmnQty";
         this.clmnQty.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // FmPriceViewAH
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(645, 454);
         this.Controls.Add(this.tgvPrice);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceViewAH";
         this.Text = "Прайс";
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvPrice)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      protected System.Windows.Forms.ToolStripComboBox cbPriceType;
      private GRSoft.UILib.TreeGridColumn FPName;
      protected System.Windows.Forms.DataGridViewTextBoxColumn FPCost;
      protected System.Windows.Forms.DataGridViewTextBoxColumn FPQty;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnClearSearch;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      protected System.Windows.Forms.ToolStripButton btnRefresh;
      protected GRSoft.UILib.TreeGridView tgvPrice;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnItem;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCost;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnQty;
      private System.Windows.Forms.Timer timer1;
      
    
   }
}
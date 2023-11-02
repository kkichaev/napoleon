namespace GRSoft.NapoleonManager
{
   partial class FmCauseEditor
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCauseEditor));
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnCause = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbDel = new System.Windows.Forms.ToolStripButton();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnCause});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(409, 254);
         this.dgvItems.TabIndex = 2;
         // 
         // clmnCause
         // 
         this.clmnCause.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnCause.DataPropertyName = "Name";
         this.clmnCause.HeaderText = "Причина";
         this.clmnCause.Name = "clmnCause";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAdd,
            this.tsbDel,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(409, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(23, 22);
         this.tsbAdd.Text = "Добавить";
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbDel
         // 
         this.tsbDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbDel.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.tsbDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbDel.Name = "tsbDel";
         this.tsbDel.Size = new System.Drawing.Size(23, 22);
         this.tsbDel.Text = "Удалить";
         this.tsbDel.Click += new System.EventHandler(this.tsbDel_Click);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Enabled = false;
         this.tsbSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // FmCauseEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(409, 279);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmCauseEditor";
         this.Text = "Причины для отсутствия";
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCause;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbDel;
      private System.Windows.Forms.ToolStripButton tsbSave;
   }
}
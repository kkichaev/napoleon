namespace GRSoft.NapoleonManager
{
   partial class FmSCActionEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSCActionEdit));
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnName2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.toolStripLabel3 = new System.Windows.Forms.ToolStripLabel();
         this.toolStrip2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.SuspendLayout();
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton1,
            this.toolStripButton2,
            this.toolStripLabel3});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(422, 25);
         this.toolStrip2.TabIndex = 1;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton1.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton1.Text = "Добавить";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // toolStripButton2
         // 
         this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton2.Image = global::GRSoft.NapoleonManager.Properties.Resources.dialog_cancel;
         this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton2.Name = "toolStripButton2";
         this.toolStripButton2.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton2.Text = "Удалить";
         this.toolStripButton2.Click += new System.EventHandler(this.toolStripButton2_Click);
         // 
         // dgvItems
         // 
         this.dgvItems.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName2});
         this.dgvItems.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(420, 359);
         this.dgvItems.TabIndex = 2;
         // 
         // clmnName2
         // 
         this.clmnName2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName2.DataPropertyName = "Name";
         this.clmnName2.HeaderText = "Акции";
         this.clmnName2.Name = "clmnName2";
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.Location = new System.Drawing.Point(249, 392);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 5;
         this.btnOK.Text = "Сохранить";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.Location = new System.Drawing.Point(335, 392);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 4;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(130, 3);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(119, 20);
         this.dtpStart.TabIndex = 7;
         // 
         // toolStripLabel3
         // 
         this.toolStripLabel3.Name = "toolStripLabel3";
         this.toolStripLabel3.Size = new System.Drawing.Size(71, 22);
         this.toolStripLabel3.Text = "Действует с";
         // 
         // FmSCActionEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(422, 423);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSCActionEdit";
         this.Text = "Акции \"Сторчек\"";
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripButton toolStripButton2;
      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnName2;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.ToolStripLabel toolStripLabel3;
      protected System.Windows.Forms.DateTimePicker dtpStart;
   }
}
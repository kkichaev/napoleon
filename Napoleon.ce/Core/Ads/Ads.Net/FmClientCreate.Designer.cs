namespace GRSoft.Ads
{
   partial class FmClientCreate
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmClientCreate));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCopy = new System.Windows.Forms.Button();
         this.btnClear = new System.Windows.Forms.Button();
         this.btnFind = new System.Windows.Forms.Button();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.btnKladr = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.dgvContact = new System.Windows.Forms.DataGridView();
         this.dgvContactName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnAdd = new System.Windows.Forms.ToolStripButton();
         this.btnEdit = new System.Windows.Forms.ToolStripButton();
         this.btnDel = new System.Windows.Forms.ToolStripButton();
         this.panel3 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.panel3.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnCopy);
         this.panel1.Controls.Add(this.btnClear);
         this.panel1.Controls.Add(this.btnFind);
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.tbAddress);
         this.panel1.Controls.Add(this.btnKladr);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(478, 111);
         this.panel1.TabIndex = 0;
         // 
         // btnCopy
         // 
         this.btnCopy.Image = global::GRSoft.Ads.Properties.Resources.arrow_down_double_3;
         this.btnCopy.Location = new System.Drawing.Point(249, 54);
         this.btnCopy.Name = "btnCopy";
         this.btnCopy.Size = new System.Drawing.Size(40, 27);
         this.btnCopy.TabIndex = 7;
         this.btnCopy.UseVisualStyleBackColor = true;
         this.btnCopy.Click += new System.EventHandler(this.btnCopy_Click);
         // 
         // btnClear
         // 
         this.btnClear.Image = global::GRSoft.Ads.Properties.Resources.edit_clear_4;
         this.btnClear.Location = new System.Drawing.Point(418, 76);
         this.btnClear.Name = "btnClear";
         this.btnClear.Size = new System.Drawing.Size(33, 31);
         this.btnClear.TabIndex = 6;
         this.btnClear.UseVisualStyleBackColor = true;
         this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
         // 
         // btnFind
         // 
         this.btnFind.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnFind.Image = global::GRSoft.Ads.Properties.Resources.monitor_doc;
         this.btnFind.Location = new System.Drawing.Point(421, 11);
         this.btnFind.Name = "btnFind";
         this.btnFind.Size = new System.Drawing.Size(33, 32);
         this.btnFind.TabIndex = 5;
         this.btnFind.UseVisualStyleBackColor = true;
         this.btnFind.Click += new System.EventHandler(this.btnFind_Click);
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(109, 85);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(295, 20);
         this.tbName.TabIndex = 4;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(16, 83);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(83, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "Наименование";
         // 
         // tbAddress
         // 
         this.tbAddress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbAddress.BackColor = System.Drawing.Color.White;
         this.tbAddress.Location = new System.Drawing.Point(106, 11);
         this.tbAddress.Multiline = true;
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.ReadOnly = true;
         this.tbAddress.Size = new System.Drawing.Size(295, 39);
         this.tbAddress.TabIndex = 2;
         // 
         // btnKladr
         // 
         this.btnKladr.Location = new System.Drawing.Point(16, 27);
         this.btnKladr.Name = "btnKladr";
         this.btnKladr.Size = new System.Drawing.Size(75, 23);
         this.btnKladr.TabIndex = 1;
         this.btnKladr.Text = "КЛАДР";
         this.btnKladr.UseVisualStyleBackColor = true;
         this.btnKladr.Click += new System.EventHandler(this.btnKladr_Click);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(39, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Адрес";
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.dgvContact);
         this.panel2.Controls.Add(this.toolStrip1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 111);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(478, 139);
         this.panel2.TabIndex = 1;
         // 
         // dgvContact
         // 
         this.dgvContact.AllowUserToAddRows = false;
         this.dgvContact.AllowUserToDeleteRows = false;
         this.dgvContact.AllowUserToResizeRows = false;
         this.dgvContact.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContact.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvContactName,
            this.dgvContactPhone});
         this.dgvContact.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvContact.Location = new System.Drawing.Point(0, 25);
         this.dgvContact.Name = "dgvContact";
         this.dgvContact.RowHeadersVisible = false;
         this.dgvContact.Size = new System.Drawing.Size(478, 114);
         this.dgvContact.TabIndex = 1;
         // 
         // dgvContactName
         // 
         this.dgvContactName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactName.DataPropertyName = "Name";
         this.dgvContactName.HeaderText = "Имя";
         this.dgvContactName.Name = "dgvContactName";
         // 
         // dgvContactPhone
         // 
         this.dgvContactPhone.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactPhone.DataPropertyName = "Phone";
         this.dgvContactPhone.HeaderText = "Телефон";
         this.dgvContactPhone.Name = "dgvContactPhone";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAdd,
            this.btnEdit,
            this.btnDel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(478, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnAdd
         // 
         this.btnAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnAdd.Image = global::GRSoft.Ads.Properties.Resources.add;
         this.btnAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(23, 22);
         this.btnAdd.Text = "Добавить";
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnEdit
         // 
         this.btnEdit.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEdit.Image = global::GRSoft.Ads.Properties.Resources.edit;
         this.btnEdit.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEdit.Name = "btnEdit";
         this.btnEdit.Size = new System.Drawing.Size(23, 22);
         this.btnEdit.Text = "Изменить";
         this.btnEdit.Click += new System.EventHandler(this.btnEdit_Click);
         // 
         // btnDel
         // 
         this.btnDel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDel.Image = global::GRSoft.Ads.Properties.Resources.delete;
         this.btnDel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(23, 22);
         this.btnDel.Text = "Удалить";
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.btnCancel);
         this.panel3.Controls.Add(this.btnOK);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel3.Location = new System.Drawing.Point(0, 250);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(478, 46);
         this.panel3.TabIndex = 2;
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(391, 11);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(310, 11);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // FmClientCreate
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(478, 296);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel3);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmClientCreate";
         this.Text = "Клиент";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmClient2_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContact)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnKladr;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnFind;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.DataGridView dgvContact;
      private System.Windows.Forms.ToolStripButton btnAdd;
      private System.Windows.Forms.ToolStripButton btnEdit;
      private System.Windows.Forms.ToolStripButton btnDel;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnClear;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactPhone;
      private System.Windows.Forms.Button btnCopy;
   }
}
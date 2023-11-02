namespace GRSoft.Ads
{
   partial class FmKladr
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmKladr));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.tvKladr = new System.Windows.Forms.TreeView();
         this.groupBox3 = new System.Windows.Forms.GroupBox();
         this.panel3 = new System.Windows.Forms.Panel();
         this.dgvStreet = new System.Windows.Forms.DataGridView();
         this.dgvStreetName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvStreetSocr = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel5 = new System.Windows.Forms.Panel();
         this.tbFind = new System.Windows.Forms.TextBox();
         this.panel4 = new System.Windows.Forms.Panel();
         this.tbNumberHome = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.groupBox3.SuspendLayout();
         this.panel3.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvStreet)).BeginInit();
         this.panel5.SuspendLayout();
         this.panel4.SuspendLayout();
         this.panel2.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 442);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(636, 43);
         this.panel1.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(552, 11);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 25);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(471, 11);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 25);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.groupBox2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.groupBox3);
         this.splitContainer1.Size = new System.Drawing.Size(636, 334);
         this.splitContainer1.SplitterDistance = 212;
         this.splitContainer1.TabIndex = 1;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.tvKladr);
         this.groupBox2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox2.Location = new System.Drawing.Point(0, 0);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(212, 334);
         this.groupBox2.TabIndex = 1;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Субъекты РФ";
         // 
         // tvKladr
         // 
         this.tvKladr.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvKladr.HideSelection = false;
         this.tvKladr.Location = new System.Drawing.Point(3, 16);
         this.tvKladr.Name = "tvKladr";
         this.tvKladr.Size = new System.Drawing.Size(206, 315);
         this.tvKladr.TabIndex = 0;
         this.tvKladr.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvKladr_MouseDown);
         // 
         // groupBox3
         // 
         this.groupBox3.Controls.Add(this.panel3);
         this.groupBox3.Controls.Add(this.panel4);
         this.groupBox3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox3.Location = new System.Drawing.Point(0, 0);
         this.groupBox3.Name = "groupBox3";
         this.groupBox3.Size = new System.Drawing.Size(420, 334);
         this.groupBox3.TabIndex = 0;
         this.groupBox3.TabStop = false;
         this.groupBox3.Text = "Улица, дом";
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.dgvStreet);
         this.panel3.Controls.Add(this.panel5);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel3.Location = new System.Drawing.Point(3, 16);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(414, 278);
         this.panel3.TabIndex = 0;
         // 
         // dgvStreet
         // 
         this.dgvStreet.AllowUserToAddRows = false;
         this.dgvStreet.AllowUserToDeleteRows = false;
         this.dgvStreet.AllowUserToResizeRows = false;
         this.dgvStreet.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvStreet.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvStreetName,
            this.dgvStreetSocr});
         this.dgvStreet.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvStreet.Location = new System.Drawing.Point(0, 38);
         this.dgvStreet.MultiSelect = false;
         this.dgvStreet.Name = "dgvStreet";
         this.dgvStreet.RowHeadersVisible = false;
         this.dgvStreet.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvStreet.Size = new System.Drawing.Size(414, 240);
         this.dgvStreet.TabIndex = 0;
         this.dgvStreet.SelectionChanged += new System.EventHandler(this.dgvStreet_SelectionChanged);
         // 
         // dgvStreetName
         // 
         this.dgvStreetName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStreetName.DataPropertyName = "Name";
         this.dgvStreetName.FillWeight = 70F;
         this.dgvStreetName.HeaderText = "Наименование";
         this.dgvStreetName.Name = "dgvStreetName";
         // 
         // dgvStreetSocr
         // 
         this.dgvStreetSocr.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvStreetSocr.DataPropertyName = "Socr";
         this.dgvStreetSocr.FillWeight = 20F;
         this.dgvStreetSocr.HeaderText = "Сокр.";
         this.dgvStreetSocr.Name = "dgvStreetSocr";
         // 
         // panel5
         // 
         this.panel5.Controls.Add(this.tbFind);
         this.panel5.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel5.Location = new System.Drawing.Point(0, 0);
         this.panel5.Name = "panel5";
         this.panel5.Size = new System.Drawing.Size(414, 38);
         this.panel5.TabIndex = 1;
         // 
         // tbFind
         // 
         this.tbFind.Location = new System.Drawing.Point(4, 6);
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(407, 20);
         this.tbFind.TabIndex = 0;
         this.tbFind.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.tbFind_KeyPress);
         // 
         // panel4
         // 
         this.panel4.Controls.Add(this.tbNumberHome);
         this.panel4.Controls.Add(this.label1);
         this.panel4.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel4.Location = new System.Drawing.Point(3, 294);
         this.panel4.Name = "panel4";
         this.panel4.Size = new System.Drawing.Size(414, 37);
         this.panel4.TabIndex = 1;
         // 
         // tbNumberHome
         // 
         this.tbNumberHome.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbNumberHome.Location = new System.Drawing.Point(58, 6);
         this.tbNumberHome.Name = "tbNumberHome";
         this.tbNumberHome.Size = new System.Drawing.Size(350, 20);
         this.tbNumberHome.TabIndex = 1;
         this.tbNumberHome.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.tbNumberHome_KeyPress);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 11);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(29, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Дом";
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.groupBox1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 334);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(636, 108);
         this.panel2.TabIndex = 2;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.tbAddress);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(636, 108);
         this.groupBox1.TabIndex = 0;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Адрес объект";
         // 
         // tbAddress
         // 
         this.tbAddress.BackColor = System.Drawing.SystemColors.Window;
         this.tbAddress.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbAddress.Location = new System.Drawing.Point(3, 16);
         this.tbAddress.Multiline = true;
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.ReadOnly = true;
         this.tbAddress.Size = new System.Drawing.Size(630, 89);
         this.tbAddress.TabIndex = 0;
         // 
         // FmKladr
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(636, 485);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmKladr";
         this.Text = "КЛАДР";
         this.Load += new System.EventHandler(this.FmKladr_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmKladr_FormClosed);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmKladr_FormClosing);
         this.panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.groupBox2.ResumeLayout(false);
         this.groupBox3.ResumeLayout(false);
         this.panel3.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvStreet)).EndInit();
         this.panel5.ResumeLayout(false);
         this.panel5.PerformLayout();
         this.panel4.ResumeLayout(false);
         this.panel4.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.TreeView tvKladr;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.GroupBox groupBox3;
      private System.Windows.Forms.Panel panel4;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.TextBox tbNumberHome;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DataGridView dgvStreet;
      private System.Windows.Forms.Panel panel5;
      private System.Windows.Forms.TextBox tbFind;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStreetName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvStreetSocr;
   }
}
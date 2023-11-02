namespace GRSoft.NapoleonManager
{
   partial class FmSchoolEdit
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
         this.panel1 = new System.Windows.Forms.Panel();
         this.label3 = new System.Windows.Forms.Label();
         this.dgvContacts = new System.Windows.Forms.DataGridView();
         this.dgvContactsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactsPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactsRemarks = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.tbNumber = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContacts)).BeginInit();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.label3);
         this.panel1.Controls.Add(this.dgvContacts);
         this.panel1.Controls.Add(this.tbAddress);
         this.panel1.Controls.Add(this.tbNumber);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Padding = new System.Windows.Forms.Padding(7);
         this.panel1.Size = new System.Drawing.Size(663, 345);
         this.panel1.TabIndex = 0;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(9, 127);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(56, 13);
         this.label3.TabIndex = 5;
         this.label3.Text = "Контакты";
         // 
         // dgvContacts
         // 
         this.dgvContacts.AllowUserToResizeRows = false;
         this.dgvContacts.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvContacts.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContacts.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvContactsName,
            this.dgvContactsPhone,
            this.dgvContactsRemarks});
         this.dgvContacts.Location = new System.Drawing.Point(9, 146);
         this.dgvContacts.Name = "dgvContacts";
         this.dgvContacts.Size = new System.Drawing.Size(646, 191);
         this.dgvContacts.TabIndex = 4;
         // 
         // dgvContactsName
         // 
         this.dgvContactsName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactsName.HeaderText = "Имя";
         this.dgvContactsName.Name = "dgvContactsName";
         // 
         // dgvContactsPhone
         // 
         this.dgvContactsPhone.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactsPhone.HeaderText = "Телефон";
         this.dgvContactsPhone.Name = "dgvContactsPhone";
         // 
         // dgvContactsRemarks
         // 
         this.dgvContactsRemarks.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvContactsRemarks.HeaderText = "Заметки";
         this.dgvContactsRemarks.Name = "dgvContactsRemarks";
         // 
         // tbAddress
         // 
         this.tbAddress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbAddress.Location = new System.Drawing.Point(56, 41);
         this.tbAddress.Multiline = true;
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.Size = new System.Drawing.Size(598, 81);
         this.tbAddress.TabIndex = 3;
         // 
         // tbNumber
         // 
         this.tbNumber.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbNumber.Location = new System.Drawing.Point(56, 15);
         this.tbNumber.Name = "tbNumber";
         this.tbNumber.Size = new System.Drawing.Size(301, 20);
         this.tbNumber.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 44);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(38, 13);
         this.label2.TabIndex = 1;
         this.label2.Text = "Адрес";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 18);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(41, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Номер";
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 345);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(663, 40);
         this.panel2.TabIndex = 1;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(485, 8);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(579, 8);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Имя";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Телефон";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // dataGridViewTextBoxColumn3
         // 
         this.dataGridViewTextBoxColumn3.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn3.HeaderText = "Заметки";
         this.dataGridViewTextBoxColumn3.Name = "dataGridViewTextBoxColumn3";
         // 
         // FmSchoolEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(663, 385);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Name = "FmSchoolEdit";
         this.Text = "Школа (редактирование)";
         this.Activated += new System.EventHandler(this.FmSchoolEdit_Activated);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmSchoolEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContacts)).EndInit();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.TextBox tbNumber;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DataGridView dgvContacts;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsPhone;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsRemarks;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
   }
}
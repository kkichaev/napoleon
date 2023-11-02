namespace GRSoft.NapoleonManager
{
   partial class FmClassEdit
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
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.lblLocality = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.lblSchool = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.lblAddress = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.lblContacts = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.dgvContacts = new System.Windows.Forms.DataGridView();
         this.dgvContactsName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactsPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvContactsRemarks = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.label6 = new System.Windows.Forms.Label();
         this.tbClass = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContacts)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 335);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(589, 39);
         this.panel1.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(505, 7);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(419, 7);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(40, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Город:";
         // 
         // lblLocality
         // 
         this.lblLocality.AutoSize = true;
         this.lblLocality.Location = new System.Drawing.Point(77, 9);
         this.lblLocality.Name = "lblLocality";
         this.lblLocality.Size = new System.Drawing.Size(51, 13);
         this.lblLocality.TabIndex = 2;
         this.lblLocality.Text = "lbLocality";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 28);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(43, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "Школа:";
         // 
         // lblSchool
         // 
         this.lblSchool.AutoSize = true;
         this.lblSchool.Location = new System.Drawing.Point(77, 28);
         this.lblSchool.Name = "lblSchool";
         this.lblSchool.Size = new System.Drawing.Size(50, 13);
         this.lblSchool.TabIndex = 4;
         this.lblSchool.Text = "lblSchool";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 47);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(41, 13);
         this.label3.TabIndex = 5;
         this.label3.Text = "Адрес:";
         // 
         // lblAddress
         // 
         this.lblAddress.AutoSize = true;
         this.lblAddress.Location = new System.Drawing.Point(77, 47);
         this.lblAddress.Name = "lblAddress";
         this.lblAddress.Size = new System.Drawing.Size(55, 13);
         this.lblAddress.TabIndex = 6;
         this.lblAddress.Text = "lblAddress";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(12, 66);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(59, 13);
         this.label4.TabIndex = 7;
         this.label4.Text = "Контакты:";
         // 
         // lblContacts
         // 
         this.lblContacts.AutoSize = true;
         this.lblContacts.Location = new System.Drawing.Point(77, 66);
         this.lblContacts.Name = "lblContacts";
         this.lblContacts.Size = new System.Drawing.Size(59, 13);
         this.lblContacts.TabIndex = 8;
         this.lblContacts.Text = "lblContacts";
         // 
         // panel2
         // 
         this.panel2.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.dgvContacts);
         this.panel2.Controls.Add(this.label6);
         this.panel2.Controls.Add(this.tbClass);
         this.panel2.Controls.Add(this.label5);
         this.panel2.Location = new System.Drawing.Point(0, 82);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(589, 253);
         this.panel2.TabIndex = 9;
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
         this.dgvContacts.Location = new System.Drawing.Point(6, 64);
         this.dgvContacts.Name = "dgvContacts";
         this.dgvContacts.Size = new System.Drawing.Size(575, 181);
         this.dgvContacts.TabIndex = 5;
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
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(10, 48);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(56, 13);
         this.label6.TabIndex = 2;
         this.label6.Text = "Контакты";
         // 
         // tbClass
         // 
         this.tbClass.Location = new System.Drawing.Point(54, 10);
         this.tbClass.Name = "tbClass";
         this.tbClass.Size = new System.Drawing.Size(164, 20);
         this.tbClass.TabIndex = 1;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(10, 17);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(38, 13);
         this.label5.TabIndex = 0;
         this.label5.Text = "Класс";
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
         // FmClassEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(589, 374);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.lblContacts);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.lblAddress);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.lblSchool);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.lblLocality);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel1);
         this.Name = "FmClassEdit";
         this.Text = "Классы (редактирование)";
         this.Activated += new System.EventHandler(this.FmClassEdit_Activated);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmClassEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContacts)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label lblLocality;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label lblSchool;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label lblAddress;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label lblContacts;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.TextBox tbClass;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.DataGridView dgvContacts;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsName;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsPhone;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvContactsRemarks;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn3;
   }
}
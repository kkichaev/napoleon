namespace GRSoft.NapoleonManager
{
   partial class FmParentsEdit
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
         this.tbName = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.dgvPhones = new System.Windows.Forms.DataGridView();
         this.dgvPhoneNumber = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvPhoneRemark = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dataGridViewTextBoxColumn2 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.panel2 = new System.Windows.Forms.Panel();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvPhones)).BeginInit();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 364);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(691, 39);
         this.panel1.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(607, 6);
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
         this.btnCancel.Location = new System.Drawing.Point(521, 6);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 8);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(29, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Имя";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(6, 24);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(524, 20);
         this.tbName.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 58);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(56, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "Контакты";
         // 
         // dgvPhones
         // 
         this.dgvPhones.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvPhones.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvPhones.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvPhoneNumber,
            this.dgvPhoneRemark});
         this.dgvPhones.Location = new System.Drawing.Point(6, 74);
         this.dgvPhones.Name = "dgvPhones";
         this.dgvPhones.Size = new System.Drawing.Size(675, 280);
         this.dgvPhones.TabIndex = 4;
         // 
         // dgvPhoneNumber
         // 
         this.dgvPhoneNumber.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPhoneNumber.HeaderText = "Телефон";
         this.dgvPhoneNumber.Name = "dgvPhoneNumber";
         // 
         // dgvPhoneRemark
         // 
         this.dgvPhoneRemark.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvPhoneRemark.HeaderText = "Заметки";
         this.dgvPhoneRemark.Name = "dgvPhoneRemark";
         // 
         // dataGridViewTextBoxColumn1
         // 
         this.dataGridViewTextBoxColumn1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn1.HeaderText = "Телефон";
         this.dataGridViewTextBoxColumn1.Name = "dataGridViewTextBoxColumn1";
         // 
         // dataGridViewTextBoxColumn2
         // 
         this.dataGridViewTextBoxColumn2.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dataGridViewTextBoxColumn2.HeaderText = "Заметки";
         this.dataGridViewTextBoxColumn2.Name = "dataGridViewTextBoxColumn2";
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.dgvPhones);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Controls.Add(this.label2);
         this.panel2.Controls.Add(this.tbName);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(691, 364);
         this.panel2.TabIndex = 5;
         // 
         // FmParentsEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(691, 403);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Name = "FmParentsEdit";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Родители (редактирование)";
         this.Activated += new System.EventHandler(this.FmParentsEdit_Activated);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmParentsEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvPhones)).EndInit();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DataGridView dgvPhones;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPhoneNumber;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvPhoneRemark;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn1;
      private System.Windows.Forms.DataGridViewTextBoxColumn dataGridViewTextBoxColumn2;
      private System.Windows.Forms.Panel panel2;
   }
}
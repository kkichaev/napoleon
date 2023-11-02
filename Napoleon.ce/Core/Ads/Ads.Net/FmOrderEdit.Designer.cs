namespace GRSoft.Ads
{
   partial class FmOrderEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrderEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.btnBrigade = new System.Windows.Forms.Button();
         this.btnBrigadeClear = new System.Windows.Forms.Button();
         this.tbBrigade = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbClient = new System.Windows.Forms.TextBox();
         this.btnClient = new System.Windows.Forms.Button();
         this.btnClientClear = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.tbText = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.cbBegin = new System.Windows.Forms.ComboBox();
         this.cbEnd = new System.Windows.Forms.ComboBox();
         this.btnKladr = new System.Windows.Forms.Button();
         this.label7 = new System.Windows.Forms.Label();
         this.tbNumber = new System.Windows.Forms.TextBox();
         this.gbReject = new System.Windows.Forms.GroupBox();
         this.tbReject = new System.Windows.Forms.TextBox();
         this.btnMap = new System.Windows.Forms.Button();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.dgvWorkType = new System.Windows.Forms.DataGridView();
         this.dgvWorkTypeName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.dgvWorkTypeChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.panel1.SuspendLayout();
         this.gbReject.SuspendLayout();
         this.groupBox1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvWorkType)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 354);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(756, 40);
         this.panel1.TabIndex = 11;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = System.Windows.Forms.AnchorStyles.Right;
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(588, 9);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = System.Windows.Forms.AnchorStyles.Right;
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(669, 9);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(11, 43);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(49, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "Бригада";
         // 
         // btnBrigade
         // 
         this.btnBrigade.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnBrigade.Image = ((System.Drawing.Image)(resources.GetObject("btnBrigade.Image")));
         this.btnBrigade.Location = new System.Drawing.Point(407, 34);
         this.btnBrigade.Name = "btnBrigade";
         this.btnBrigade.Size = new System.Drawing.Size(30, 30);
         this.btnBrigade.TabIndex = 7;
         this.btnBrigade.UseVisualStyleBackColor = true;
         this.btnBrigade.Click += new System.EventHandler(this.btnBrigade_Click);
         // 
         // btnBrigadeClear
         // 
         this.btnBrigadeClear.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnBrigadeClear.Image = ((System.Drawing.Image)(resources.GetObject("btnBrigadeClear.Image")));
         this.btnBrigadeClear.Location = new System.Drawing.Point(443, 34);
         this.btnBrigadeClear.Name = "btnBrigadeClear";
         this.btnBrigadeClear.Size = new System.Drawing.Size(30, 30);
         this.btnBrigadeClear.TabIndex = 8;
         this.btnBrigadeClear.UseVisualStyleBackColor = true;
         this.btnBrigadeClear.Click += new System.EventHandler(this.btnBrigadeClear_Click);
         // 
         // tbBrigade
         // 
         this.tbBrigade.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbBrigade.BackColor = System.Drawing.SystemColors.Window;
         this.tbBrigade.Location = new System.Drawing.Point(74, 40);
         this.tbBrigade.Name = "tbBrigade";
         this.tbBrigade.ReadOnly = true;
         this.tbBrigade.Size = new System.Drawing.Size(327, 20);
         this.tbBrigade.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(11, 83);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(43, 13);
         this.label2.TabIndex = 5;
         this.label2.Text = "Клиент";
         // 
         // tbClient
         // 
         this.tbClient.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbClient.BackColor = System.Drawing.SystemColors.Window;
         this.tbClient.Location = new System.Drawing.Point(74, 80);
         this.tbClient.Name = "tbClient";
         this.tbClient.ReadOnly = true;
         this.tbClient.Size = new System.Drawing.Size(327, 20);
         this.tbClient.TabIndex = 2;
         // 
         // btnClient
         // 
         this.btnClient.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnClient.Image = ((System.Drawing.Image)(resources.GetObject("btnClient.Image")));
         this.btnClient.Location = new System.Drawing.Point(407, 74);
         this.btnClient.Name = "btnClient";
         this.btnClient.Size = new System.Drawing.Size(30, 30);
         this.btnClient.TabIndex = 9;
         this.btnClient.UseVisualStyleBackColor = true;
         this.btnClient.Click += new System.EventHandler(this.btnClient_Click);
         // 
         // btnClientClear
         // 
         this.btnClientClear.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnClientClear.Image = ((System.Drawing.Image)(resources.GetObject("btnClientClear.Image")));
         this.btnClientClear.Location = new System.Drawing.Point(443, 73);
         this.btnClientClear.Name = "btnClientClear";
         this.btnClientClear.Size = new System.Drawing.Size(30, 30);
         this.btnClientClear.TabIndex = 10;
         this.btnClientClear.UseVisualStyleBackColor = true;
         this.btnClientClear.Click += new System.EventHandler(this.btnClientClear_Click);
         // 
         // label3
         // 
         this.label3.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(11, 121);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(95, 13);
         this.label3.TabIndex = 9;
         this.label3.Text = "Начало работы, ч";
         // 
         // label4
         // 
         this.label4.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(11, 152);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(89, 13);
         this.label4.TabIndex = 11;
         this.label4.Text = "Конец работы, ч";
         // 
         // label5
         // 
         this.label5.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(11, 192);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(70, 13);
         this.label5.TabIndex = 13;
         this.label5.Text = "Содержание";
         // 
         // tbText
         // 
         this.tbText.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbText.Location = new System.Drawing.Point(103, 192);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(384, 72);
         this.tbText.TabIndex = 5;
         // 
         // label6
         // 
         this.label6.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(11, 271);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(38, 13);
         this.label6.TabIndex = 15;
         this.label6.Text = "Адрес";
         // 
         // tbAddress
         // 
         this.tbAddress.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbAddress.BackColor = System.Drawing.SystemColors.Window;
         this.tbAddress.Location = new System.Drawing.Point(103, 271);
         this.tbAddress.Multiline = true;
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.ReadOnly = true;
         this.tbAddress.Size = new System.Drawing.Size(384, 77);
         this.tbAddress.TabIndex = 6;
         // 
         // cbBegin
         // 
         this.cbBegin.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.cbBegin.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbBegin.FormattingEnabled = true;
         this.cbBegin.Location = new System.Drawing.Point(115, 118);
         this.cbBegin.Name = "cbBegin";
         this.cbBegin.Size = new System.Drawing.Size(61, 21);
         this.cbBegin.TabIndex = 3;
         // 
         // cbEnd
         // 
         this.cbEnd.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.cbEnd.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbEnd.FormattingEnabled = true;
         this.cbEnd.Location = new System.Drawing.Point(115, 149);
         this.cbEnd.Name = "cbEnd";
         this.cbEnd.Size = new System.Drawing.Size(61, 21);
         this.cbEnd.TabIndex = 4;
         // 
         // btnKladr
         // 
         this.btnKladr.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnKladr.Location = new System.Drawing.Point(11, 298);
         this.btnKladr.Name = "btnKladr";
         this.btnKladr.Size = new System.Drawing.Size(75, 23);
         this.btnKladr.TabIndex = 16;
         this.btnKladr.Text = "КЛАДР";
         this.btnKladr.UseVisualStyleBackColor = true;
         this.btnKladr.Click += new System.EventHandler(this.btnKladr_Click);
         // 
         // label7
         // 
         this.label7.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(11, 13);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(80, 13);
         this.label7.TabIndex = 17;
         this.label7.Text = "Номер заявки";
         // 
         // tbNumber
         // 
         this.tbNumber.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbNumber.Location = new System.Drawing.Point(97, 10);
         this.tbNumber.Name = "tbNumber";
         this.tbNumber.ReadOnly = true;
         this.tbNumber.Size = new System.Drawing.Size(129, 20);
         this.tbNumber.TabIndex = 18;
         // 
         // gbReject
         // 
         this.gbReject.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.gbReject.Controls.Add(this.tbReject);
         this.gbReject.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.gbReject.ForeColor = System.Drawing.Color.Red;
         this.gbReject.Location = new System.Drawing.Point(191, 106);
         this.gbReject.Name = "gbReject";
         this.gbReject.Size = new System.Drawing.Size(297, 80);
         this.gbReject.TabIndex = 19;
         this.gbReject.TabStop = false;
         this.gbReject.Text = "Причина отказа";
         // 
         // tbReject
         // 
         this.tbReject.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.tbReject.Location = new System.Drawing.Point(6, 20);
         this.tbReject.Multiline = true;
         this.tbReject.Name = "tbReject";
         this.tbReject.ReadOnly = true;
         this.tbReject.Size = new System.Drawing.Size(285, 54);
         this.tbReject.TabIndex = 0;
         // 
         // btnMap
         // 
         this.btnMap.Anchor = System.Windows.Forms.AnchorStyles.Left;
         this.btnMap.Location = new System.Drawing.Point(408, 7);
         this.btnMap.Name = "btnMap";
         this.btnMap.Size = new System.Drawing.Size(66, 23);
         this.btnMap.TabIndex = 20;
         this.btnMap.Text = "Карта";
         this.btnMap.UseVisualStyleBackColor = true;
         this.btnMap.Click += new System.EventHandler(this.btnMap_Click);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.dgvWorkType);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Right;
         this.groupBox1.Location = new System.Drawing.Point(494, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(262, 354);
         this.groupBox1.TabIndex = 21;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Типы работ";
         // 
         // dgvWorkType
         // 
         this.dgvWorkType.AllowUserToAddRows = false;
         this.dgvWorkType.AllowUserToDeleteRows = false;
         this.dgvWorkType.AllowUserToResizeRows = false;
         this.dgvWorkType.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvWorkType.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvWorkTypeName,
            this.dgvWorkTypeChecked});
         this.dgvWorkType.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvWorkType.Location = new System.Drawing.Point(3, 16);
         this.dgvWorkType.Name = "dgvWorkType";
         this.dgvWorkType.RowHeadersVisible = false;
         this.dgvWorkType.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvWorkType.Size = new System.Drawing.Size(256, 335);
         this.dgvWorkType.TabIndex = 0;
         this.dgvWorkType.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvWorkType_CellContentClick);
         // 
         // dgvWorkTypeName
         // 
         this.dgvWorkTypeName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvWorkTypeName.DataPropertyName = "Name";
         this.dgvWorkTypeName.HeaderText = "Имя";
         this.dgvWorkTypeName.Name = "dgvWorkTypeName";
         // 
         // dgvWorkTypeChecked
         // 
         this.dgvWorkTypeChecked.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.dgvWorkTypeChecked.FalseValue = "0";
         this.dgvWorkTypeChecked.FillWeight = 20F;
         this.dgvWorkTypeChecked.HeaderText = "Вкл.";
         this.dgvWorkTypeChecked.Name = "dgvWorkTypeChecked";
         this.dgvWorkTypeChecked.TrueValue = "1";
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(232, 10);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(169, 20);
         this.dtpDate.TabIndex = 22;
         this.dtpDate.ValueChanged += new System.EventHandler(this.dtpDate_ValueChanged);
         // 
         // FmOrderEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(756, 394);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.btnMap);
         this.Controls.Add(this.gbReject);
         this.Controls.Add(this.tbNumber);
         this.Controls.Add(this.label7);
         this.Controls.Add(this.btnKladr);
         this.Controls.Add(this.cbEnd);
         this.Controls.Add(this.cbBegin);
         this.Controls.Add(this.tbAddress);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.tbText);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.btnClientClear);
         this.Controls.Add(this.btnClient);
         this.Controls.Add(this.tbClient);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbBrigade);
         this.Controls.Add(this.btnBrigadeClear);
         this.Controls.Add(this.btnBrigade);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrderEdit";
         this.Text = "FmOrderEdit";
         this.Load += new System.EventHandler(this.FmOrderEdit_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrderEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.gbReject.ResumeLayout(false);
         this.gbReject.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.dgvWorkType)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button btnBrigade;
      private System.Windows.Forms.Button btnBrigadeClear;
      private System.Windows.Forms.TextBox tbBrigade;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbClient;
      private System.Windows.Forms.Button btnClient;
      private System.Windows.Forms.Button btnClientClear;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.ComboBox cbBegin;
      private System.Windows.Forms.ComboBox cbEnd;
      private System.Windows.Forms.Button btnKladr;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox tbNumber;
      private System.Windows.Forms.GroupBox gbReject;
      private System.Windows.Forms.TextBox tbReject;
      private System.Windows.Forms.Button btnMap;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.DataGridView dgvWorkType;
      private System.Windows.Forms.DataGridViewTextBoxColumn dgvWorkTypeName;
      private System.Windows.Forms.DataGridViewCheckBoxColumn dgvWorkTypeChecked;
      private System.Windows.Forms.DateTimePicker dtpDate;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReport));
         this.button1 = new System.Windows.Forms.Button();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbAddress = new System.Windows.Forms.CheckBox();
         this.cbName = new System.Windows.Forms.CheckBox();
         this.cbData = new System.Windows.Forms.CheckBox();
         this.cbPhone = new System.Windows.Forms.CheckBox();
         this.cbOrgType = new System.Windows.Forms.CheckBox();
         this.cbDealer = new System.Windows.Forms.CheckBox();
         this.cbLicense = new System.Windows.Forms.CheckBox();
         this.cbCheif = new System.Windows.Forms.CheckBox();
         this.cbContact = new System.Windows.Forms.CheckBox();
         this.cbPrice = new System.Windows.Forms.CheckBox();
         this.groupBox = new System.Windows.Forms.GroupBox();
         this.cbVisitCnt = new System.Windows.Forms.CheckBox();
         this.cbAvgTraff = new System.Windows.Forms.CheckBox();
         this.cbSkucnt = new System.Windows.Forms.CheckBox();
         this.cbResult = new System.Windows.Forms.CheckBox();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.tcParams = new System.Windows.Forms.TabControl();
         this.tabPage1 = new System.Windows.Forms.TabPage();
         this.tabPage2 = new System.Windows.Forms.TabPage();
         this.lbOrg = new System.Windows.Forms.CheckedListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.cbEmail = new System.Windows.Forms.CheckBox();
         this.groupBox.SuspendLayout();
         this.tcParams.SuspendLayout();
         this.tabPage1.SuspendLayout();
         this.tabPage2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(161, 255);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(41, 13);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(143, 20);
         this.dtpBegin.TabIndex = 1;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(220, 13);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(143, 20);
         this.dtpEnd.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(16, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 3;
         this.label1.Text = "c";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(195, 16);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 4;
         this.label2.Text = "по";
         // 
         // cbAddress
         // 
         this.cbAddress.AutoSize = true;
         this.cbAddress.Checked = true;
         this.cbAddress.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbAddress.Location = new System.Drawing.Point(20, 26);
         this.cbAddress.Name = "cbAddress";
         this.cbAddress.Size = new System.Drawing.Size(58, 18);
         this.cbAddress.TabIndex = 5;
         this.cbAddress.Tag = "1";
         this.cbAddress.Text = "Адрес";
         this.cbAddress.UseVisualStyleBackColor = true;
         // 
         // cbName
         // 
         this.cbName.AutoSize = true;
         this.cbName.Checked = true;
         this.cbName.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbName.Location = new System.Drawing.Point(20, 51);
         this.cbName.Name = "cbName";
         this.cbName.Size = new System.Drawing.Size(102, 18);
         this.cbName.TabIndex = 6;
         this.cbName.Tag = "2";
         this.cbName.Text = "Наименование";
         this.cbName.UseVisualStyleBackColor = true;
         // 
         // cbData
         // 
         this.cbData.AutoSize = true;
         this.cbData.Checked = true;
         this.cbData.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbData.Location = new System.Drawing.Point(20, 101);
         this.cbData.Name = "cbData";
         this.cbData.Size = new System.Drawing.Size(52, 18);
         this.cbData.TabIndex = 7;
         this.cbData.Tag = "4";
         this.cbData.Text = "Дата";
         this.cbData.UseVisualStyleBackColor = true;
         // 
         // cbPhone
         // 
         this.cbPhone.AutoSize = true;
         this.cbPhone.Checked = true;
         this.cbPhone.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPhone.Location = new System.Drawing.Point(20, 125);
         this.cbPhone.Name = "cbPhone";
         this.cbPhone.Size = new System.Drawing.Size(69, 18);
         this.cbPhone.TabIndex = 8;
         this.cbPhone.Tag = "5";
         this.cbPhone.Text = "Телефон";
         this.cbPhone.UseVisualStyleBackColor = true;
         // 
         // cbOrgType
         // 
         this.cbOrgType.AutoSize = true;
         this.cbOrgType.Checked = true;
         this.cbOrgType.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbOrgType.Location = new System.Drawing.Point(20, 149);
         this.cbOrgType.Name = "cbOrgType";
         this.cbOrgType.Size = new System.Drawing.Size(60, 18);
         this.cbOrgType.TabIndex = 9;
         this.cbOrgType.Tag = "6";
         this.cbOrgType.Text = "Вид ТТ";
         this.cbOrgType.UseVisualStyleBackColor = true;
         // 
         // cbDealer
         // 
         this.cbDealer.AutoSize = true;
         this.cbDealer.Checked = true;
         this.cbDealer.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbDealer.Location = new System.Drawing.Point(169, 26);
         this.cbDealer.Name = "cbDealer";
         this.cbDealer.Size = new System.Drawing.Size(70, 18);
         this.cbDealer.TabIndex = 10;
         this.cbDealer.Tag = "7";
         this.cbDealer.Text = "Оптовик";
         this.cbDealer.UseVisualStyleBackColor = true;
         // 
         // cbLicense
         // 
         this.cbLicense.AutoSize = true;
         this.cbLicense.Checked = true;
         this.cbLicense.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLicense.Location = new System.Drawing.Point(168, 51);
         this.cbLicense.Name = "cbLicense";
         this.cbLicense.Size = new System.Drawing.Size(74, 18);
         this.cbLicense.TabIndex = 11;
         this.cbLicense.Tag = "8";
         this.cbLicense.Text = "Лицензия";
         this.cbLicense.UseVisualStyleBackColor = true;
         // 
         // cbCheif
         // 
         this.cbCheif.AutoSize = true;
         this.cbCheif.Checked = true;
         this.cbCheif.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbCheif.Location = new System.Drawing.Point(168, 76);
         this.cbCheif.Name = "cbCheif";
         this.cbCheif.Size = new System.Drawing.Size(75, 18);
         this.cbCheif.TabIndex = 12;
         this.cbCheif.Tag = "9";
         this.cbCheif.Text = "Директор";
         this.cbCheif.UseVisualStyleBackColor = true;
         // 
         // cbContact
         // 
         this.cbContact.AutoSize = true;
         this.cbContact.Checked = true;
         this.cbContact.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbContact.Location = new System.Drawing.Point(168, 101);
         this.cbContact.Name = "cbContact";
         this.cbContact.Size = new System.Drawing.Size(113, 18);
         this.cbContact.TabIndex = 13;
         this.cbContact.Tag = "10";
         this.cbContact.Text = "Контактное лицо";
         this.cbContact.UseVisualStyleBackColor = true;
         // 
         // cbPrice
         // 
         this.cbPrice.AutoSize = true;
         this.cbPrice.Checked = true;
         this.cbPrice.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPrice.Location = new System.Drawing.Point(315, 49);
         this.cbPrice.Name = "cbPrice";
         this.cbPrice.Size = new System.Drawing.Size(84, 18);
         this.cbPrice.TabIndex = 14;
         this.cbPrice.Tag = "Price";
         this.cbPrice.Text = "Прайс лист";
         this.cbPrice.UseVisualStyleBackColor = true;
         // 
         // groupBox
         // 
         this.groupBox.Controls.Add(this.cbEmail);
         this.groupBox.Controls.Add(this.cbVisitCnt);
         this.groupBox.Controls.Add(this.cbAvgTraff);
         this.groupBox.Controls.Add(this.cbSkucnt);
         this.groupBox.Controls.Add(this.cbResult);
         this.groupBox.Controls.Add(this.cbContact);
         this.groupBox.Controls.Add(this.cbPrice);
         this.groupBox.Controls.Add(this.cbAddress);
         this.groupBox.Controls.Add(this.cbName);
         this.groupBox.Controls.Add(this.cbCheif);
         this.groupBox.Controls.Add(this.cbData);
         this.groupBox.Controls.Add(this.cbLicense);
         this.groupBox.Controls.Add(this.cbPhone);
         this.groupBox.Controls.Add(this.cbDealer);
         this.groupBox.Controls.Add(this.cbOrgType);
         this.groupBox.Location = new System.Drawing.Point(20, 42);
         this.groupBox.Name = "groupBox";
         this.groupBox.Size = new System.Drawing.Size(415, 207);
         this.groupBox.TabIndex = 15;
         this.groupBox.TabStop = false;
         this.groupBox.Text = "Колонки";
         // 
         // cbVisitCnt
         // 
         this.cbVisitCnt.AutoSize = true;
         this.cbVisitCnt.Checked = true;
         this.cbVisitCnt.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbVisitCnt.Location = new System.Drawing.Point(20, 75);
         this.cbVisitCnt.Name = "cbVisitCnt";
         this.cbVisitCnt.Size = new System.Drawing.Size(121, 18);
         this.cbVisitCnt.TabIndex = 17;
         this.cbVisitCnt.Tag = "3";
         this.cbVisitCnt.Text = "Кол-во посещений";
         this.cbVisitCnt.UseVisualStyleBackColor = true;
         // 
         // cbAvgTraff
         // 
         this.cbAvgTraff.AutoSize = true;
         this.cbAvgTraff.Checked = true;
         this.cbAvgTraff.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbAvgTraff.Location = new System.Drawing.Point(168, 125);
         this.cbAvgTraff.Name = "cbAvgTraff";
         this.cbAvgTraff.Size = new System.Drawing.Size(146, 18);
         this.cbAvgTraff.TabIndex = 16;
         this.cbAvgTraff.Tag = "11";
         this.cbAvgTraff.Text = "Средняя проходимость";
         this.cbAvgTraff.UseVisualStyleBackColor = true;
         // 
         // cbSkucnt
         // 
         this.cbSkucnt.AutoSize = true;
         this.cbSkucnt.Checked = true;
         this.cbSkucnt.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbSkucnt.Location = new System.Drawing.Point(167, 173);
         this.cbSkucnt.Name = "cbSkucnt";
         this.cbSkucnt.Size = new System.Drawing.Size(121, 18);
         this.cbSkucnt.TabIndex = 15;
         this.cbSkucnt.Tag = "14";
         this.cbSkucnt.Text = "Общее кол-во SKU";
         this.cbSkucnt.UseVisualStyleBackColor = true;
         // 
         // cbResult
         // 
         this.cbResult.AutoSize = true;
         this.cbResult.Checked = true;
         this.cbResult.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbResult.Location = new System.Drawing.Point(168, 149);
         this.cbResult.Name = "cbResult";
         this.cbResult.Size = new System.Drawing.Size(147, 18);
         this.cbResult.TabIndex = 15;
         this.cbResult.Tag = "12";
         this.cbResult.Text = "Результат переговоров";
         this.cbResult.UseVisualStyleBackColor = true;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Checked = true;
         this.rbAgents.Location = new System.Drawing.Point(16, 44);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 26;
         this.rbAgents.TabStop = true;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.Click += new System.EventHandler(this.rbAgents_Click);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(16, 17);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 25;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.Click += new System.EventHandler(this.rbDivision_Click);
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(124, 43);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(254, 22);
         this.cbAgents.TabIndex = 24;
         // 
         // cbDivisions
         // 
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(124, 16);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(254, 22);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 23;
         // 
         // tcParams
         // 
         this.tcParams.Controls.Add(this.tabPage1);
         this.tcParams.Controls.Add(this.tabPage2);
         this.tcParams.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tcParams.Location = new System.Drawing.Point(0, 0);
         this.tcParams.Name = "tcParams";
         this.tcParams.SelectedIndex = 0;
         this.tcParams.Size = new System.Drawing.Size(400, 516);
         this.tcParams.TabIndex = 30;
         // 
         // tabPage1
         // 
         this.tabPage1.Controls.Add(this.cbDivisions);
         this.tabPage1.Controls.Add(this.rbAgents);
         this.tabPage1.Controls.Add(this.cbAgents);
         this.tabPage1.Controls.Add(this.rbDivision);
         this.tabPage1.Location = new System.Drawing.Point(4, 23);
         this.tabPage1.Name = "tabPage1";
         this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
         this.tabPage1.Size = new System.Drawing.Size(392, 489);
         this.tabPage1.TabIndex = 0;
         this.tabPage1.Text = "Торговый";
         this.tabPage1.UseVisualStyleBackColor = true;
         // 
         // tabPage2
         // 
         this.tabPage2.Controls.Add(this.lbOrg);
         this.tabPage2.Controls.Add(this.toolStrip1);
         this.tabPage2.Location = new System.Drawing.Point(4, 22);
         this.tabPage2.Name = "tabPage2";
         this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
         this.tabPage2.Size = new System.Drawing.Size(392, 490);
         this.tabPage2.TabIndex = 1;
         this.tabPage2.Text = "Организация";
         this.tabPage2.UseVisualStyleBackColor = true;
         // 
         // lbOrg
         // 
         this.lbOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbOrg.FormattingEnabled = true;
         this.lbOrg.Location = new System.Drawing.Point(3, 28);
         this.lbOrg.Name = "lbOrg";
         this.lbOrg.Size = new System.Drawing.Size(386, 459);
         this.lbOrg.TabIndex = 30;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(3, 3);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(386, 25);
         this.toolStrip1.TabIndex = 31;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "toolStripButton1";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.groupBox);
         this.splitContainer1.Panel1.Controls.Add(this.button1);
         this.splitContainer1.Panel1.Controls.Add(this.label2);
         this.splitContainer1.Panel1.Controls.Add(this.dtpBegin);
         this.splitContainer1.Panel1.Controls.Add(this.label1);
         this.splitContainer1.Panel1.Controls.Add(this.dtpEnd);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tcParams);
         this.splitContainer1.Size = new System.Drawing.Size(848, 516);
         this.splitContainer1.SplitterDistance = 444;
         this.splitContainer1.TabIndex = 31;
         // 
         // cbEmail
         // 
         this.cbEmail.AutoSize = true;
         this.cbEmail.Checked = true;
         this.cbEmail.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbEmail.Location = new System.Drawing.Point(315, 25);
         this.cbEmail.Name = "cbEmail";
         this.cbEmail.Size = new System.Drawing.Size(50, 18);
         this.cbEmail.TabIndex = 18;
         this.cbEmail.Tag = "15";
         this.cbEmail.Text = "email";
         this.cbEmail.UseVisualStyleBackColor = true;
         // 
         // FmReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(848, 516);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReport";
         this.Text = "Отчет";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmReport_FormClosed);
         this.Load += new System.EventHandler(this.FmReport_Load);
         this.groupBox.ResumeLayout(false);
         this.groupBox.PerformLayout();
         this.tcParams.ResumeLayout(false);
         this.tabPage1.ResumeLayout(false);
         this.tabPage1.PerformLayout();
         this.tabPage2.ResumeLayout(false);
         this.tabPage2.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.CheckBox cbAddress;
      private System.Windows.Forms.CheckBox cbName;
      private System.Windows.Forms.CheckBox cbData;
      private System.Windows.Forms.CheckBox cbPhone;
      private System.Windows.Forms.CheckBox cbOrgType;
      private System.Windows.Forms.CheckBox cbDealer;
      private System.Windows.Forms.CheckBox cbLicense;
      private System.Windows.Forms.CheckBox cbCheif;
      private System.Windows.Forms.CheckBox cbContact;
      private System.Windows.Forms.CheckBox cbPrice;
      private System.Windows.Forms.GroupBox groupBox;
      private System.Windows.Forms.CheckBox cbResult;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.CheckBox cbAvgTraff;
      private System.Windows.Forms.CheckBox cbVisitCnt;
      private System.Windows.Forms.TabControl tcParams;
      private System.Windows.Forms.TabPage tabPage1;
      private System.Windows.Forms.TabPage tabPage2;
      private System.Windows.Forms.CheckedListBox lbOrg;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.CheckBox cbSkucnt;
      private System.Windows.Forms.CheckBox cbEmail;
   }
}
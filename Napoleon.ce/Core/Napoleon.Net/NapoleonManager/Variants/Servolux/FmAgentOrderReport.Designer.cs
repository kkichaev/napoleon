namespace GRSoft.NapoleonManager
{
   partial class FmAgentOrderReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentOrderReport));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.label4 = new System.Windows.Forms.Label();
         this.cbGroupBy = new System.Windows.Forms.ComboBox();
         this.cbTotal = new System.Windows.Forms.CheckBox();
         this.cbPackets = new System.Windows.Forms.CheckBox();
         this.cbPiece = new System.Windows.Forms.CheckBox();
         this.btnExcelReport = new System.Windows.Forms.Button();
         this.cbPeriod = new System.Windows.Forms.CheckBox();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.rbByCreatedFld = new System.Windows.Forms.RadioButton();
         this.rbByDateFld = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbFirms = new System.Windows.Forms.ComboBox();
         this.cbThermal = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.groupBox1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.label4);
         this.groupBox1.Controls.Add(this.cbGroupBy);
         this.groupBox1.Controls.Add(this.cbTotal);
         this.groupBox1.Controls.Add(this.cbPackets);
         this.groupBox1.Controls.Add(this.cbPiece);
         this.groupBox1.Location = new System.Drawing.Point(21, 251);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(297, 88);
         this.groupBox1.TabIndex = 33;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Данные";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(24, 55);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(92, 13);
         this.label4.TabIndex = 4;
         this.label4.Text = "Группировать по";
         // 
         // cbGroupBy
         // 
         this.cbGroupBy.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbGroupBy.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbGroupBy.FormattingEnabled = true;
         this.cbGroupBy.Items.AddRange(new object[] {
            "Контрагентам",
            "Торговым агентам"});
         this.cbGroupBy.Location = new System.Drawing.Point(122, 52);
         this.cbGroupBy.Name = "cbGroupBy";
         this.cbGroupBy.Size = new System.Drawing.Size(156, 21);
         this.cbGroupBy.TabIndex = 3;
         // 
         // cbTotal
         // 
         this.cbTotal.AutoSize = true;
         this.cbTotal.Location = new System.Drawing.Point(181, 20);
         this.cbTotal.Name = "cbTotal";
         this.cbTotal.Size = new System.Drawing.Size(94, 17);
         this.cbTotal.TabIndex = 2;
         this.cbTotal.Text = "Только итоги";
         this.cbTotal.UseVisualStyleBackColor = true;
         // 
         // cbPackets
         // 
         this.cbPackets.AutoSize = true;
         this.cbPackets.Checked = true;
         this.cbPackets.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPackets.Location = new System.Drawing.Point(96, 20);
         this.cbPackets.Name = "cbPackets";
         this.cbPackets.Size = new System.Drawing.Size(76, 17);
         this.cbPackets.TabIndex = 1;
         this.cbPackets.Text = "Упаковки";
         this.cbPackets.UseVisualStyleBackColor = true;
         // 
         // cbPiece
         // 
         this.cbPiece.AutoSize = true;
         this.cbPiece.Checked = true;
         this.cbPiece.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPiece.Location = new System.Drawing.Point(27, 20);
         this.cbPiece.Name = "cbPiece";
         this.cbPiece.Size = new System.Drawing.Size(57, 17);
         this.cbPiece.TabIndex = 0;
         this.cbPiece.Text = "Штуки";
         this.cbPiece.UseVisualStyleBackColor = true;
         // 
         // btnExcelReport
         // 
         this.btnExcelReport.Location = new System.Drawing.Point(118, 359);
         this.btnExcelReport.Name = "btnExcelReport";
         this.btnExcelReport.Size = new System.Drawing.Size(75, 23);
         this.btnExcelReport.TabIndex = 32;
         this.btnExcelReport.Text = "Excel";
         this.btnExcelReport.UseVisualStyleBackColor = true;
         this.btnExcelReport.Click += new System.EventHandler(this.btnExcelReport_Click);
         // 
         // cbPeriod
         // 
         this.cbPeriod.AutoSize = true;
         this.cbPeriod.Location = new System.Drawing.Point(21, 172);
         this.cbPeriod.Name = "cbPeriod";
         this.cbPeriod.Size = new System.Drawing.Size(77, 17);
         this.cbPeriod.TabIndex = 31;
         this.cbPeriod.Text = "за период";
         this.cbPeriod.UseVisualStyleBackColor = true;
         this.cbPeriod.CheckedChanged += new System.EventHandler(this.cbPeriod_CheckedChanged);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpEnd.Enabled = false;
         this.dtpEnd.Location = new System.Drawing.Point(132, 169);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(167, 20);
         this.dtpEnd.TabIndex = 30;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpBegin.Location = new System.Drawing.Point(132, 143);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 29;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(36, 147);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 13);
         this.label2.TabIndex = 28;
         this.label2.Text = "Дата";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(132, 23);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(167, 21);
         this.cbDivisions.TabIndex = 27;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.rbByCreatedFld);
         this.groupBox2.Controls.Add(this.rbByDateFld);
         this.groupBox2.Location = new System.Drawing.Point(21, 200);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(297, 45);
         this.groupBox2.TabIndex = 38;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Выборка заявок по дате";
         // 
         // rbByCreatedFld
         // 
         this.rbByCreatedFld.AutoSize = true;
         this.rbByCreatedFld.Location = new System.Drawing.Point(132, 20);
         this.rbByCreatedFld.Name = "rbByCreatedFld";
         this.rbByCreatedFld.Size = new System.Drawing.Size(73, 17);
         this.rbByCreatedFld.TabIndex = 1;
         this.rbByCreatedFld.Text = "создания";
         this.rbByCreatedFld.UseVisualStyleBackColor = true;
         // 
         // rbByDateFld
         // 
         this.rbByDateFld.AutoSize = true;
         this.rbByDateFld.Checked = true;
         this.rbByDateFld.Location = new System.Drawing.Point(27, 20);
         this.rbByDateFld.Name = "rbByDateFld";
         this.rbByDateFld.Size = new System.Drawing.Size(70, 17);
         this.rbByDateFld.TabIndex = 0;
         this.rbByDateFld.TabStop = true;
         this.rbByDateFld.Text = "отгрузки";
         this.rbByDateFld.UseVisualStyleBackColor = true;
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.Enabled = false;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(132, 50);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(167, 21);
         this.cbAgents.TabIndex = 37;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(21, 50);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(54, 17);
         this.rbAgents.TabIndex = 36;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.Click += new System.EventHandler(this.rbAgents_Click);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Checked = true;
         this.rbDivision.Location = new System.Drawing.Point(21, 23);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(105, 17);
         this.rbDivision.TabIndex = 35;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.Click += new System.EventHandler(this.rbDivision_Click);
         // 
         // cbFirms
         // 
         this.cbFirms.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFirms.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbFirms.FormattingEnabled = true;
         this.cbFirms.Location = new System.Drawing.Point(132, 82);
         this.cbFirms.Name = "cbFirms";
         this.cbFirms.Size = new System.Drawing.Size(164, 21);
         this.cbFirms.TabIndex = 39;
         // 
         // cbThermal
         // 
         this.cbThermal.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbThermal.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbThermal.FormattingEnabled = true;
         this.cbThermal.Items.AddRange(new object[] {
            "<Все>",
            "Охл",
            "Зам"});
         this.cbThermal.Location = new System.Drawing.Point(132, 109);
         this.cbThermal.Name = "cbThermal";
         this.cbThermal.Size = new System.Drawing.Size(164, 21);
         this.cbThermal.TabIndex = 40;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(36, 85);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(54, 13);
         this.label1.TabIndex = 41;
         this.label1.Text = "Фабрика";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(36, 112);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(61, 13);
         this.label3.TabIndex = 42;
         this.label3.Text = "Состояние";
         // 
         // FmAgentOrderReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(333, 394);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbThermal);
         this.Controls.Add(this.cbFirms);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.btnExcelReport);
         this.Controls.Add(this.cbPeriod);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAgentOrderReport";
         this.Text = "Отчет по заявкам";
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      protected System.Windows.Forms.GroupBox groupBox1;
      protected System.Windows.Forms.CheckBox cbPackets;
      protected System.Windows.Forms.CheckBox cbPiece;
      protected System.Windows.Forms.Button btnExcelReport;
      protected System.Windows.Forms.CheckBox cbPeriod;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbDivisions;
      protected System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.RadioButton rbByCreatedFld;
      private System.Windows.Forms.RadioButton rbByDateFld;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.CheckBox cbTotal;
      private System.Windows.Forms.ComboBox cbFirms;
      private System.Windows.Forms.ComboBox cbThermal;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.ComboBox cbGroupBy;
   }
}
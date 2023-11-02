
namespace GRSoft.NapoleonAdmin
{
   partial class RmvScheduler
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(RmvScheduler));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.btnSchSave = new System.Windows.Forms.Button();
         this.cbMonths = new System.Windows.Forms.ComboBox();
         this.label14 = new System.Windows.Forms.Label();
         this.cbMon = new System.Windows.Forms.CheckBox();
         this.dtpSchedule = new System.Windows.Forms.DateTimePicker();
         this.cbTue = new System.Windows.Forms.CheckBox();
         this.label13 = new System.Windows.Forms.Label();
         this.cbWed = new System.Windows.Forms.CheckBox();
         this.cbSun = new System.Windows.Forms.CheckBox();
         this.cbThr = new System.Windows.Forms.CheckBox();
         this.cbSat = new System.Windows.Forms.CheckBox();
         this.cbFri = new System.Windows.Forms.CheckBox();
         this.dtpLogEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpLogStart = new System.Windows.Forms.DateTimePicker();
         this.dgvSchLog = new System.Windows.Forms.DataGridView();
         this.Column4 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column5 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripButton2 = new System.Windows.Forms.ToolStripButton();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchLog)).BeginInit();
         this.toolStrip2.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.groupBox2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.dtpLogEnd);
         this.splitContainer1.Panel2.Controls.Add(this.dtpLogStart);
         this.splitContainer1.Panel2.Controls.Add(this.dgvSchLog);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip2);
         this.splitContainer1.Size = new System.Drawing.Size(776, 559);
         this.splitContainer1.SplitterDistance = 371;
         this.splitContainer1.TabIndex = 15;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.btnSchSave);
         this.groupBox2.Controls.Add(this.cbMonths);
         this.groupBox2.Controls.Add(this.label14);
         this.groupBox2.Controls.Add(this.cbMon);
         this.groupBox2.Controls.Add(this.dtpSchedule);
         this.groupBox2.Controls.Add(this.cbTue);
         this.groupBox2.Controls.Add(this.label13);
         this.groupBox2.Controls.Add(this.cbWed);
         this.groupBox2.Controls.Add(this.cbSun);
         this.groupBox2.Controls.Add(this.cbThr);
         this.groupBox2.Controls.Add(this.cbSat);
         this.groupBox2.Controls.Add(this.cbFri);
         this.groupBox2.Location = new System.Drawing.Point(15, 20);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(342, 214);
         this.groupBox2.TabIndex = 14;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Параметры запуска";
         // 
         // btnSchSave
         // 
         this.btnSchSave.Location = new System.Drawing.Point(119, 172);
         this.btnSchSave.Name = "btnSchSave";
         this.btnSchSave.Size = new System.Drawing.Size(113, 23);
         this.btnSchSave.TabIndex = 15;
         this.btnSchSave.Text = "Сохранить";
         this.btnSchSave.UseVisualStyleBackColor = true;
         this.btnSchSave.Click += new System.EventHandler(this.btnSchSave_Click);
         // 
         // cbMonths
         // 
         this.cbMonths.FormattingEnabled = true;
         this.cbMonths.Location = new System.Drawing.Point(16, 129);
         this.cbMonths.Name = "cbMonths";
         this.cbMonths.Size = new System.Drawing.Size(144, 21);
         this.cbMonths.TabIndex = 14;
         // 
         // label14
         // 
         this.label14.AutoSize = true;
         this.label14.Location = new System.Drawing.Point(16, 113);
         this.label14.Name = "label14";
         this.label14.Size = new System.Drawing.Size(144, 13);
         this.label14.TabIndex = 13;
         this.label14.Text = "Оставлять последние, мес";
         // 
         // cbMon
         // 
         this.cbMon.AutoSize = true;
         this.cbMon.Location = new System.Drawing.Point(19, 29);
         this.cbMon.Margin = new System.Windows.Forms.Padding(2);
         this.cbMon.Name = "cbMon";
         this.cbMon.Size = new System.Drawing.Size(43, 17);
         this.cbMon.TabIndex = 4;
         this.cbMon.Text = "Пн.";
         this.cbMon.UseVisualStyleBackColor = true;
         // 
         // dtpSchedule
         // 
         this.dtpSchedule.CustomFormat = "HH:mm";
         this.dtpSchedule.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpSchedule.ImeMode = System.Windows.Forms.ImeMode.NoControl;
         this.dtpSchedule.Location = new System.Drawing.Point(16, 77);
         this.dtpSchedule.Margin = new System.Windows.Forms.Padding(2);
         this.dtpSchedule.Name = "dtpSchedule";
         this.dtpSchedule.ShowUpDown = true;
         this.dtpSchedule.Size = new System.Drawing.Size(144, 20);
         this.dtpSchedule.TabIndex = 12;
         // 
         // cbTue
         // 
         this.cbTue.AutoSize = true;
         this.cbTue.Location = new System.Drawing.Point(67, 29);
         this.cbTue.Margin = new System.Windows.Forms.Padding(2);
         this.cbTue.Name = "cbTue";
         this.cbTue.Size = new System.Drawing.Size(41, 17);
         this.cbTue.TabIndex = 5;
         this.cbTue.Text = "Вт.";
         this.cbTue.UseVisualStyleBackColor = true;
         // 
         // label13
         // 
         this.label13.AutoSize = true;
         this.label13.Location = new System.Drawing.Point(16, 62);
         this.label13.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
         this.label13.Name = "label13";
         this.label13.Size = new System.Drawing.Size(84, 13);
         this.label13.TabIndex = 11;
         this.label13.Text = "Время запуска";
         // 
         // cbWed
         // 
         this.cbWed.AutoSize = true;
         this.cbWed.Location = new System.Drawing.Point(113, 29);
         this.cbWed.Margin = new System.Windows.Forms.Padding(2);
         this.cbWed.Name = "cbWed";
         this.cbWed.Size = new System.Drawing.Size(39, 17);
         this.cbWed.TabIndex = 6;
         this.cbWed.Text = "Ср";
         this.cbWed.UseVisualStyleBackColor = true;
         // 
         // cbSun
         // 
         this.cbSun.AutoSize = true;
         this.cbSun.Location = new System.Drawing.Point(289, 29);
         this.cbSun.Margin = new System.Windows.Forms.Padding(2);
         this.cbSun.Name = "cbSun";
         this.cbSun.Size = new System.Drawing.Size(39, 17);
         this.cbSun.TabIndex = 10;
         this.cbSun.Text = "Вс";
         this.cbSun.UseVisualStyleBackColor = true;
         // 
         // cbThr
         // 
         this.cbThr.AutoSize = true;
         this.cbThr.Location = new System.Drawing.Point(157, 29);
         this.cbThr.Margin = new System.Windows.Forms.Padding(2);
         this.cbThr.Name = "cbThr";
         this.cbThr.Size = new System.Drawing.Size(39, 17);
         this.cbThr.TabIndex = 7;
         this.cbThr.Text = "Чт";
         this.cbThr.UseVisualStyleBackColor = true;
         // 
         // cbSat
         // 
         this.cbSat.AutoSize = true;
         this.cbSat.Location = new System.Drawing.Point(245, 29);
         this.cbSat.Margin = new System.Windows.Forms.Padding(2);
         this.cbSat.Name = "cbSat";
         this.cbSat.Size = new System.Drawing.Size(39, 17);
         this.cbSat.TabIndex = 9;
         this.cbSat.Text = "Сб";
         this.cbSat.UseVisualStyleBackColor = true;
         // 
         // cbFri
         // 
         this.cbFri.AutoSize = true;
         this.cbFri.Location = new System.Drawing.Point(201, 29);
         this.cbFri.Margin = new System.Windows.Forms.Padding(2);
         this.cbFri.Name = "cbFri";
         this.cbFri.Size = new System.Drawing.Size(39, 17);
         this.cbFri.TabIndex = 8;
         this.cbFri.Text = "Пт";
         this.cbFri.UseVisualStyleBackColor = true;
         // 
         // dtpLogEnd
         // 
         this.dtpLogEnd.Location = new System.Drawing.Point(190, 2);
         this.dtpLogEnd.Name = "dtpLogEnd";
         this.dtpLogEnd.Size = new System.Drawing.Size(127, 20);
         this.dtpLogEnd.TabIndex = 4;
         // 
         // dtpLogStart
         // 
         this.dtpLogStart.Location = new System.Drawing.Point(47, 1);
         this.dtpLogStart.Name = "dtpLogStart";
         this.dtpLogStart.Size = new System.Drawing.Size(127, 20);
         this.dtpLogStart.TabIndex = 3;
         // 
         // dgvSchLog
         // 
         this.dgvSchLog.AllowUserToAddRows = false;
         this.dgvSchLog.AllowUserToDeleteRows = false;
         this.dgvSchLog.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvSchLog.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column4,
            this.Column5});
         this.dgvSchLog.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvSchLog.Location = new System.Drawing.Point(0, 25);
         this.dgvSchLog.Name = "dgvSchLog";
         this.dgvSchLog.ReadOnly = true;
         this.dgvSchLog.RowHeadersVisible = false;
         this.dgvSchLog.Size = new System.Drawing.Size(401, 534);
         this.dgvSchLog.TabIndex = 1;
         // 
         // Column4
         // 
         this.Column4.DataPropertyName = "Date";
         this.Column4.HeaderText = "Дата";
         this.Column4.Name = "Column4";
         this.Column4.ReadOnly = true;
         // 
         // Column5
         // 
         this.Column5.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.Column5.DataPropertyName = "Info";
         this.Column5.HeaderText = "Сообщение";
         this.Column5.Name = "Column5";
         this.Column5.ReadOnly = true;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton2,
            this.toolStripLabel2});
         this.toolStrip2.Location = new System.Drawing.Point(0, 0);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(401, 25);
         this.toolStrip2.TabIndex = 0;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripButton2
         // 
         this.toolStripButton2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButton2.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton2.Image")));
         this.toolStripButton2.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton2.Name = "toolStripButton2";
         this.toolStripButton2.Size = new System.Drawing.Size(23, 22);
         this.toolStripButton2.Text = "Обновить";
         this.toolStripButton2.Click += new System.EventHandler(this.toolStripButton2_Click);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Font = new System.Drawing.Font("Segoe UI", 9F, System.Drawing.FontStyle.Bold);
         this.toolStripLabel2.Margin = new System.Windows.Forms.Padding(330, 1, 0, 2);
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(97, 15);
         this.toolStripLabel2.Text = "Журнал работы";
         // 
         // RmvScheduler
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Name = "RmvScheduler";
         this.Size = new System.Drawing.Size(776, 559);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvSchLog)).EndInit();
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.Button btnSchSave;
      private System.Windows.Forms.ComboBox cbMonths;
      private System.Windows.Forms.Label label14;
      private System.Windows.Forms.CheckBox cbMon;
      private System.Windows.Forms.DateTimePicker dtpSchedule;
      private System.Windows.Forms.CheckBox cbTue;
      private System.Windows.Forms.Label label13;
      private System.Windows.Forms.CheckBox cbWed;
      private System.Windows.Forms.CheckBox cbSun;
      private System.Windows.Forms.CheckBox cbThr;
      private System.Windows.Forms.CheckBox cbSat;
      private System.Windows.Forms.CheckBox cbFri;
      private System.Windows.Forms.DateTimePicker dtpLogEnd;
      private System.Windows.Forms.DateTimePicker dtpLogStart;
      private System.Windows.Forms.DataGridView dgvSchLog;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column4;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column5;
      private System.Windows.Forms.ToolStrip toolStrip2;
      public System.Windows.Forms.ToolStripButton toolStripButton2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
   }
}

namespace GRSoft.NapoleonManager{
   partial class FmAuditReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAuditReport));
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.button1 = new System.Windows.Forms.Button();
         this.groupBox3 = new System.Windows.Forms.GroupBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.dtpTimeEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpTimeStart = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.groupBox3.SuspendLayout();
         this.SuspendLayout();
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(115, 38);
         this.cbDivision.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(225, 22);
         this.cbDivision.TabIndex = 4;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(22, 17);
         this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(33, 14);
         this.label1.TabIndex = 5;
         this.label1.Text = "Дата";
         // 
         // dtpDate
         // 
         this.dtpDate.Location = new System.Drawing.Point(115, 12);
         this.dtpDate.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.Size = new System.Drawing.Size(152, 20);
         this.dtpDate.TabIndex = 0;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(153, 162);
         this.button1.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(90, 21);
         this.button1.TabIndex = 9;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // groupBox3
         // 
         this.groupBox3.Controls.Add(this.label3);
         this.groupBox3.Controls.Add(this.label4);
         this.groupBox3.Controls.Add(this.dtpTimeEnd);
         this.groupBox3.Controls.Add(this.dtpTimeStart);
         this.groupBox3.Location = new System.Drawing.Point(25, 66);
         this.groupBox3.Name = "groupBox3";
         this.groupBox3.Size = new System.Drawing.Size(315, 79);
         this.groupBox3.TabIndex = 10;
         this.groupBox3.TabStop = false;
         this.groupBox3.Text = "Рабочее время";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(10, 48);
         this.label3.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 8;
         this.label3.Text = "по";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(10, 24);
         this.label4.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(13, 14);
         this.label4.TabIndex = 7;
         this.label4.Text = "с";
         // 
         // dtpTimeEnd
         // 
         this.dtpTimeEnd.CustomFormat = "HH:mm";
         this.dtpTimeEnd.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeEnd.Location = new System.Drawing.Point(38, 44);
         this.dtpTimeEnd.Name = "dtpTimeEnd";
         this.dtpTimeEnd.ShowUpDown = true;
         this.dtpTimeEnd.Size = new System.Drawing.Size(62, 20);
         this.dtpTimeEnd.TabIndex = 1;
         this.dtpTimeEnd.Value = new System.DateTime(2019, 6, 26, 18, 0, 0, 0);
         // 
         // dtpTimeStart
         // 
         this.dtpTimeStart.CustomFormat = "HH:mm";
         this.dtpTimeStart.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeStart.Location = new System.Drawing.Point(38, 18);
         this.dtpTimeStart.Name = "dtpTimeStart";
         this.dtpTimeStart.ShowUpDown = true;
         this.dtpTimeStart.Size = new System.Drawing.Size(62, 20);
         this.dtpTimeStart.TabIndex = 0;
         this.dtpTimeStart.Value = new System.DateTime(2019, 6, 26, 9, 0, 0, 0);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(22, 41);
         this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(85, 14);
         this.label2.TabIndex = 11;
         this.label2.Text = "Подразделение";
         // 
         // FmAuditReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(371, 205);
         this.Controls.Add(this.cbDivision);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.groupBox3);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.dtpDate);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 4, 2, 4);
         this.Name = "FmAuditReport";
         this.Text = "Отчет по непосещенным точкам ";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmAuditReport_FormClosed);
         this.Load += new System.EventHandler(this.FmWorkReport_Load);
         this.groupBox3.ResumeLayout(false);
         this.groupBox3.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.Label label1;
      protected System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.GroupBox groupBox3;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpTimeEnd;
      private System.Windows.Forms.DateTimePicker dtpTimeStart;
      private System.Windows.Forms.Label label2;
   }
}
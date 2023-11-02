namespace GRSoft.NapoleonManager
{
   partial class FmGPSReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmGPSReport));
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpTimeEnd = new System.Windows.Forms.DateTimePicker();
         this.cbTime = new System.Windows.Forms.CheckBox();
         this.dtpTimeStart = new System.Windows.Forms.DateTimePicker();
         this.btnReport = new System.Windows.Forms.Button();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(30, 23);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(146, 20);
         this.dtpBegin.TabIndex = 0;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(30, 49);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(146, 20);
         this.dtpEnd.TabIndex = 1;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.label5);
         this.groupBox1.Controls.Add(this.label4);
         this.groupBox1.Controls.Add(this.label2);
         this.groupBox1.Controls.Add(this.label1);
         this.groupBox1.Controls.Add(this.dtpTimeEnd);
         this.groupBox1.Controls.Add(this.cbTime);
         this.groupBox1.Controls.Add(this.dtpTimeStart);
         this.groupBox1.Controls.Add(this.dtpBegin);
         this.groupBox1.Controls.Add(this.dtpEnd);
         this.groupBox1.Location = new System.Drawing.Point(67, 12);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(200, 156);
         this.groupBox1.TabIndex = 2;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Период";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(21, 132);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(19, 13);
         this.label5.TabIndex = 9;
         this.label5.Text = "по";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(21, 104);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(13, 13);
         this.label4.TabIndex = 8;
         this.label4.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 51);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 6;
         this.label2.Text = "по";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(7, 26);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 5;
         this.label1.Text = "с";
         // 
         // dtpTimeEnd
         // 
         this.dtpTimeEnd.CustomFormat = "HH:mm";
         this.dtpTimeEnd.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeEnd.Location = new System.Drawing.Point(46, 129);
         this.dtpTimeEnd.Name = "dtpTimeEnd";
         this.dtpTimeEnd.ShowUpDown = true;
         this.dtpTimeEnd.Size = new System.Drawing.Size(83, 20);
         this.dtpTimeEnd.TabIndex = 4;
         this.dtpTimeEnd.Value = new System.DateTime(2012, 8, 22, 20, 0, 0, 0);
         // 
         // cbTime
         // 
         this.cbTime.AutoSize = true;
         this.cbTime.Location = new System.Drawing.Point(7, 80);
         this.cbTime.Name = "cbTime";
         this.cbTime.Size = new System.Drawing.Size(58, 17);
         this.cbTime.TabIndex = 3;
         this.cbTime.Text = "время";
         this.cbTime.UseVisualStyleBackColor = true;
         this.cbTime.CheckedChanged += new System.EventHandler(this.cbTime_CheckedChanged);
         // 
         // dtpTimeStart
         // 
         this.dtpTimeStart.CustomFormat = "HH:mm";
         this.dtpTimeStart.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeStart.Location = new System.Drawing.Point(46, 102);
         this.dtpTimeStart.Name = "dtpTimeStart";
         this.dtpTimeStart.ShowUpDown = true;
         this.dtpTimeStart.Size = new System.Drawing.Size(83, 20);
         this.dtpTimeStart.TabIndex = 2;
         this.dtpTimeStart.Value = new System.DateTime(2012, 8, 22, 8, 0, 0, 0);
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(121, 174);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 5;
         this.btnReport.Text = "HTML";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmGPSReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(340, 208);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.groupBox1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmGPSReport";
         this.Text = "Отчет по километражу";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmGsmReport_FormClosed);
         this.Load += new System.EventHandler(this.FmGsmReport_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.CheckBox cbTime;
      private System.Windows.Forms.DateTimePicker dtpTimeStart;
      private System.Windows.Forms.DateTimePicker dtpTimeEnd;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      protected System.Windows.Forms.Button btnReport;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
   }
}
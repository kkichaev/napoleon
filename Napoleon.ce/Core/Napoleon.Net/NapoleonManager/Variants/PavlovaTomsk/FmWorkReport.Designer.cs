namespace GRSoft.NapoleonManager{
   partial class FmWorkReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmWorkReport));
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.label5 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpTimeEnd = new System.Windows.Forms.DateTimePicker();
         this.cbTime = new System.Windows.Forms.CheckBox();
         this.dtpTimeStart = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.button1 = new System.Windows.Forms.Button();
         this.groupBox2.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.cbDivision);
         this.groupBox2.Location = new System.Drawing.Point(263, 12);
         this.groupBox2.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Padding = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.groupBox2.Size = new System.Drawing.Size(240, 145);
         this.groupBox2.TabIndex = 8;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Данные по подразделению";
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(7, 23);
         this.cbDivision.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(225, 22);
         this.cbDivision.TabIndex = 4;
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
         this.groupBox1.Location = new System.Drawing.Point(14, 11);
         this.groupBox1.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Padding = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.groupBox1.Size = new System.Drawing.Size(240, 146);
         this.groupBox1.TabIndex = 7;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Период";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(25, 123);
         this.label5.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(19, 14);
         this.label5.TabIndex = 9;
         this.label5.Text = "по";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(25, 97);
         this.label4.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(13, 14);
         this.label4.TabIndex = 8;
         this.label4.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(8, 48);
         this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 6;
         this.label2.Text = "по";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(8, 24);
         this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 5;
         this.label1.Text = "с";
         // 
         // dtpTimeEnd
         // 
         this.dtpTimeEnd.CustomFormat = "HH:mm";
         this.dtpTimeEnd.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeEnd.Location = new System.Drawing.Point(55, 120);
         this.dtpTimeEnd.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.dtpTimeEnd.Name = "dtpTimeEnd";
         this.dtpTimeEnd.ShowUpDown = true;
         this.dtpTimeEnd.Size = new System.Drawing.Size(99, 20);
         this.dtpTimeEnd.TabIndex = 4;
         this.dtpTimeEnd.Value = new System.DateTime(2012, 8, 22, 20, 0, 0, 0);
         // 
         // cbTime
         // 
         this.cbTime.AutoSize = true;
         this.cbTime.Location = new System.Drawing.Point(8, 75);
         this.cbTime.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.cbTime.Name = "cbTime";
         this.cbTime.Size = new System.Drawing.Size(59, 18);
         this.cbTime.TabIndex = 3;
         this.cbTime.Text = "время";
         this.cbTime.UseVisualStyleBackColor = true;
         // 
         // dtpTimeStart
         // 
         this.dtpTimeStart.CustomFormat = "HH:mm";
         this.dtpTimeStart.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpTimeStart.Location = new System.Drawing.Point(55, 95);
         this.dtpTimeStart.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.dtpTimeStart.Name = "dtpTimeStart";
         this.dtpTimeStart.ShowUpDown = true;
         this.dtpTimeStart.Size = new System.Drawing.Size(99, 20);
         this.dtpTimeStart.TabIndex = 2;
         this.dtpTimeStart.Value = new System.DateTime(2012, 8, 22, 8, 0, 0, 0);
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(36, 21);
         this.dtpBegin.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(174, 20);
         this.dtpBegin.TabIndex = 0;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(36, 46);
         this.dtpEnd.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(174, 20);
         this.dtpEnd.TabIndex = 1;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(202, 162);
         this.button1.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(90, 21);
         this.button1.TabIndex = 9;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmWorkReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(516, 193);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.groupBox1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 4, 2, 4);
         this.Name = "FmWorkReport";
         this.Text = "Отчет о работе";
         this.Load += new System.EventHandler(this.FmWorkReport_Load);
         this.groupBox2.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpTimeEnd;
      private System.Windows.Forms.CheckBox cbTime;
      private System.Windows.Forms.DateTimePicker dtpTimeStart;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Button button1;
   }
}
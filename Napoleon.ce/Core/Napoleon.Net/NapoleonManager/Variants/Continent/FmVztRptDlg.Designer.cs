namespace GRSoft.NapoleonManager
{
   partial class FmVztRptDlg
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVztRptDlg));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbAgent = new System.Windows.Forms.CheckedListBox();
         this.btnExcel = new System.Windows.Forms.Button();
         this.datePeriodView1 = new GRSoft.NapoleonManager.DatePeriodView();
         this.panel1 = new System.Windows.Forms.Panel();
         this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.groupBox1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.cbAgent);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.Location = new System.Drawing.Point(0, 57);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(475, 348);
         this.groupBox1.TabIndex = 1;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Агенты";
         // 
         // cbAgent
         // 
         this.cbAgent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(3, 16);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(469, 329);
         this.cbAgent.TabIndex = 0;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(390, 2);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 2;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Dock = System.Windows.Forms.DockStyle.Top;
         this.datePeriodView1.Finish = new System.DateTime(2015, 12, 3, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(0, 0);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(475, 27);
         this.datePeriodView1.Start = new System.DateTime(2015, 12, 3, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 0;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.dateTimePicker1);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 27);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(475, 30);
         this.panel1.TabIndex = 3;
         // 
         // dateTimePicker1
         // 
         this.dateTimePicker1.CustomFormat = "HH:mm";
         this.dateTimePicker1.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dateTimePicker1.Location = new System.Drawing.Point(126, 5);
         this.dateTimePicker1.Name = "dateTimePicker1";
         this.dateTimePicker1.ShowUpDown = true;
         this.dateTimePicker1.Size = new System.Drawing.Size(77, 20);
         this.dateTimePicker1.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(6, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(117, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Начало рабочего дня:";
         // 
         // FmVztPrtDlg
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(475, 405);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.datePeriodView1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVztPrtDlg";
         this.Text = "Отчет по посещениям";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmVztPrtDlg_FormClosing);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmVztPrtDlg_FormClosed);
         this.Load += new System.EventHandler(this.FmVztPrtDlg_Load);
         this.groupBox1.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.CheckedListBox cbAgent;
      public DatePeriodView datePeriodView1;
      public System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Label label1;
      public System.Windows.Forms.DateTimePicker dateTimePicker1;
   }
}
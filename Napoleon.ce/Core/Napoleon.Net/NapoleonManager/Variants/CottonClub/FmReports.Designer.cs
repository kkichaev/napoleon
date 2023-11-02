namespace GRSoft.NapoleonManager
{
   partial class FmReports
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReports));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.cbCities = new System.Windows.Forms.CheckBox();
         this.cbRetailer = new System.Windows.Forms.CheckBox();
         this.lbReports = new System.Windows.Forms.ListBox();
         this.btnRefresh = new System.Windows.Forms.Button();
         this.btnReport = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 10);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 42);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(53, 5);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(149, 20);
         this.dtpStart.TabIndex = 2;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(53, 38);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(149, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // cbCities
         // 
         this.cbCities.AutoSize = true;
         this.cbCities.Location = new System.Drawing.Point(235, 6);
         this.cbCities.Name = "cbCities";
         this.cbCities.Size = new System.Drawing.Size(55, 18);
         this.cbCities.TabIndex = 5;
         this.cbCities.Text = "Город";
         this.cbCities.UseVisualStyleBackColor = true;
         // 
         // cbRetailer
         // 
         this.cbRetailer.AutoSize = true;
         this.cbRetailer.Location = new System.Drawing.Point(235, 38);
         this.cbRetailer.Name = "cbRetailer";
         this.cbRetailer.Size = new System.Drawing.Size(99, 18);
         this.cbRetailer.TabIndex = 6;
         this.cbRetailer.Text = "Торговая сеть";
         this.cbRetailer.UseVisualStyleBackColor = true;
         // 
         // lbReports
         // 
         this.lbReports.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.lbReports.FormattingEnabled = true;
         this.lbReports.ItemHeight = 14;
         this.lbReports.Location = new System.Drawing.Point(15, 92);
         this.lbReports.Name = "lbReports";
         this.lbReports.Size = new System.Drawing.Size(349, 298);
         this.lbReports.Sorted = true;
         this.lbReports.TabIndex = 9;
         // 
         // btnRefresh
         // 
         this.btnRefresh.Location = new System.Drawing.Point(15, 64);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(122, 23);
         this.btnRefresh.TabIndex = 10;
         this.btnRefresh.Text = "Получить список";
         this.btnRefresh.UseVisualStyleBackColor = true;
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(143, 64);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(122, 23);
         this.btnReport.TabIndex = 11;
         this.btnReport.Text = "Отчет";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // FmReports
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(379, 400);
         this.Controls.Add(this.btnReport);
         this.Controls.Add(this.btnRefresh);
         this.Controls.Add(this.lbReports);
         this.Controls.Add(this.cbRetailer);
         this.Controls.Add(this.cbCities);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReports";
         this.Text = "Отчеты";
         this.Load += new System.EventHandler(this.FmReports_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.CheckBox cbCities;
      private System.Windows.Forms.CheckBox cbRetailer;
      private System.Windows.Forms.ListBox lbReports;
      private System.Windows.Forms.Button btnRefresh;
      private System.Windows.Forms.Button btnReport;
   }
}
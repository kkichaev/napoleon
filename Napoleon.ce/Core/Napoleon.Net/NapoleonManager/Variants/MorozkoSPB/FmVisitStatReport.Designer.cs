namespace GRSoft.NapoleonManager
{
   partial class FmVisitStatReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVisitStatReport));
         this.dpvDate = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbCompanies = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbOrg = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.button1 = new System.Windows.Forms.Button();
         this.cbInterval = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // dpvDate
         // 
         this.dpvDate.Finish = new System.DateTime(2017, 6, 24, 0, 0, 0, 0);
         this.dpvDate.Location = new System.Drawing.Point(19, 17);
         this.dpvDate.Name = "dpvDate";
         this.dpvDate.Size = new System.Drawing.Size(367, 27);
         this.dpvDate.Start = new System.DateTime(2017, 6, 24, 0, 0, 0, 0);
         this.dpvDate.TabIndex = 0;
         // 
         // cbCompanies
         // 
         this.cbCompanies.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbCompanies.FormattingEnabled = true;
         this.cbCompanies.Location = new System.Drawing.Point(122, 101);
         this.cbCompanies.Name = "cbCompanies";
         this.cbCompanies.Size = new System.Drawing.Size(259, 21);
         this.cbCompanies.Sorted = true;
         this.cbCompanies.TabIndex = 1;
         this.cbCompanies.SelectedIndexChanged += new System.EventHandler(this.cbCompanies_SelectedIndexChanged);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(80, 104);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(31, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Сеть";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(25, 139);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(86, 13);
         this.label2.TabIndex = 4;
         this.label2.Text = "Торговая точка";
         // 
         // cbOrg
         // 
         this.cbOrg.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbOrg.FormattingEnabled = true;
         this.cbOrg.Location = new System.Drawing.Point(122, 136);
         this.cbOrg.Name = "cbOrg";
         this.cbOrg.Size = new System.Drawing.Size(259, 21);
         this.cbOrg.Sorted = true;
         this.cbOrg.TabIndex = 3;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(24, 177);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(87, 13);
         this.label3.TabIndex = 5;
         this.label3.Text = "Подразделение";
         // 
         // cbDivision
         // 
         this.cbDivision.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(122, 174);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(259, 21);
         this.cbDivision.TabIndex = 6;
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.button1.Location = new System.Drawing.Point(161, 231);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 7;
         this.button1.Text = "Отчет";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // cbInterval
         // 
         this.cbInterval.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbInterval.FormattingEnabled = true;
         this.cbInterval.Items.AddRange(new object[] {
            "Неделя",
            "Месяц"});
         this.cbInterval.Location = new System.Drawing.Point(122, 63);
         this.cbInterval.Name = "cbInterval";
         this.cbInterval.Size = new System.Drawing.Size(259, 21);
         this.cbInterval.TabIndex = 8;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(19, 66);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(92, 13);
         this.label4.TabIndex = 9;
         this.label4.Text = "Интервал отчета";
         // 
         // FmVisitStatReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(412, 277);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.cbInterval);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.cbDivision);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbOrg);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbCompanies);
         this.Controls.Add(this.dpvDate);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVisitStatReport";
         this.Text = "Статистика визитов";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpvDate;
      private System.Windows.Forms.ComboBox cbCompanies;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbOrg;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.ComboBox cbInterval;
      private System.Windows.Forms.Label label4;
   }
}
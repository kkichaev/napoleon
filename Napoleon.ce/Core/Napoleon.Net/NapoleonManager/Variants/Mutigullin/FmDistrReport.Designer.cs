namespace GRSoft.NapoleonManager
{
   partial class FmDistrReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrReport));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.label1 = new System.Windows.Forms.Label();
         this.cbNetOrg = new System.Windows.Forms.ComboBox();
         this.button1 = new System.Windows.Forms.Button();
         this.datePeriodView1 = new GRSoft.NapoleonManager.DatePeriodView();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.cbDivision);
         this.groupBox1.Controls.Add(this.cbAgent);
         this.groupBox1.Controls.Add(this.rbDivision);
         this.groupBox1.Controls.Add(this.rbAgent);
         this.groupBox1.Location = new System.Drawing.Point(13, 49);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(388, 97);
         this.groupBox1.TabIndex = 0;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Выберите параметр";
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(125, 55);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(241, 22);
         this.cbDivision.TabIndex = 3;
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(125, 24);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(241, 22);
         this.cbAgent.TabIndex = 2;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(8, 53);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 1;
         this.rbDivision.TabStop = true;
         this.rbDivision.Tag = "cbDivision";
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(8, 25);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(55, 18);
         this.rbAgent.TabIndex = 0;
         this.rbAgent.TabStop = true;
         this.rbAgent.Tag = "cbAgent";
         this.rbAgent.Text = "Агент";
         this.rbAgent.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(100, 158);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(32, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "Сеть";
         // 
         // cbNetOrg
         // 
         this.cbNetOrg.FormattingEnabled = true;
         this.cbNetOrg.Location = new System.Drawing.Point(138, 155);
         this.cbNetOrg.Name = "cbNetOrg";
         this.cbNetOrg.Size = new System.Drawing.Size(242, 22);
         this.cbNetOrg.TabIndex = 3;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(163, 192);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 4;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Finish = new System.DateTime(2017, 6, 6, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(13, 12);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(367, 27);
         this.datePeriodView1.Start = new System.DateTime(2017, 6, 6, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 1;
         // 
         // FmDistrReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(413, 226);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.cbNetOrg);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.datePeriodView1);
         this.Controls.Add(this.groupBox1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrReport";
         this.Text = "Отчёт о дистрибуции";
         this.Load += new System.EventHandler(this.FmDistrReport_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.ComboBox cbAgent;
      private DatePeriodView datePeriodView1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbNetOrg;
      private System.Windows.Forms.Button button1;
   }
}
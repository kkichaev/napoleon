namespace GRSoft.NapoleonManager
{
   partial class FmRpt
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRpt));
         this.label1 = new System.Windows.Forms.Label();
         this.btnExcel = new System.Windows.Forms.Button();
         this.period = new GRSoft.NapoleonManager.DatePeriodView();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.groupBox2.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 14);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(44, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Период";
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(157, 233);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 2;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // period
         // 
         this.period.Finish = new System.DateTime(2015, 1, 28, 0, 0, 0, 0);
         this.period.Location = new System.Drawing.Point(12, 31);
         this.period.Name = "period";
         this.period.Size = new System.Drawing.Size(446, 31);
         this.period.Start = new System.DateTime(2015, 1, 28, 0, 0, 0, 0);
         this.period.TabIndex = 0;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.rbAll);
         this.groupBox2.Controls.Add(this.cbDivision);
         this.groupBox2.Controls.Add(this.rbDivision);
         this.groupBox2.Controls.Add(this.rbAgent);
         this.groupBox2.Controls.Add(this.cbAgent);
         this.groupBox2.Location = new System.Drawing.Point(12, 68);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(220, 150);
         this.groupBox2.TabIndex = 8;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Данные по";
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Location = new System.Drawing.Point(12, 20);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 5;
         this.rbAll.TabStop = true;
         this.rbAll.Text = "все";
         this.rbAll.UseVisualStyleBackColor = true;
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(35, 118);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(154, 22);
         this.cbDivision.TabIndex = 4;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(12, 95);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(104, 18);
         this.rbDivision.TabIndex = 1;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "подразделению";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbDivision_CheckedChanged);
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(12, 44);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(84, 18);
         this.rbAgent.TabIndex = 0;
         this.rbAgent.TabStop = true;
         this.rbAgent.Text = "сотруднику";
         this.rbAgent.UseVisualStyleBackColor = true;
         this.rbAgent.CheckedChanged += new System.EventHandler(this.rbAgent_CheckedChanged);
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(35, 67);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(154, 22);
         this.cbAgent.TabIndex = 3;
         // 
         // FmRpt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(455, 288);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.period);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRpt";
         this.Text = "Отчет";
         this.Load += new System.EventHandler(this.FmRpt_Load);
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView period;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.RadioButton rbAll;
   }
}
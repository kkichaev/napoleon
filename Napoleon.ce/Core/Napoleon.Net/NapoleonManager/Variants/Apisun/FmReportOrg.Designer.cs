namespace GRSoft.NapoleonManager
{
   partial class FmReportOrg
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReportOrg));
         this.button1 = new System.Windows.Forms.Button();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(143, 143);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.rbAll);
         this.groupBox1.Controls.Add(this.cbAgent);
         this.groupBox1.Controls.Add(this.rbAgent);
         this.groupBox1.Controls.Add(this.cbDivision);
         this.groupBox1.Controls.Add(this.rbDivision);
         this.groupBox1.Location = new System.Drawing.Point(12, 12);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(351, 110);
         this.groupBox1.TabIndex = 27;
         this.groupBox1.TabStop = false;
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(131, 75);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(214, 22);
         this.cbAgent.TabIndex = 30;
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(6, 74);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(55, 18);
         this.rbAgent.TabIndex = 29;
         this.rbAgent.Text = "Агент";
         this.rbAgent.UseVisualStyleBackColor = true;
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(131, 44);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(214, 22);
         this.cbDivision.TabIndex = 28;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(6, 44);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 27;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Checked = true;
         this.rbAll.Location = new System.Drawing.Point(7, 20);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 31;
         this.rbAll.TabStop = true;
         this.rbAll.Text = "Все";
         this.rbAll.UseVisualStyleBackColor = true;
         // 
         // FmReportOrg
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(376, 184);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.button1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportOrg";
         this.Text = "Список организаций";
         this.Load += new System.EventHandler(this.FmReport_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.RadioButton rbAll;
   }
}
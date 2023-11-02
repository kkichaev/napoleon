namespace GRSoft.NapoleonManager
{
   partial class FmReportPresent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmReportPresent));
         this.button1 = new System.Windows.Forms.Button();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(162, 231);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 0;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(41, 186);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(143, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(220, 186);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(143, 20);
         this.dtpFinish.TabIndex = 2;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(16, 189);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 3;
         this.label1.Text = "c";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(195, 189);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 4;
         this.label2.Text = "по";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(19, 134);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(123, 14);
         this.label3.TabIndex = 5;
         this.label3.Text = "Наименование товара";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(22, 151);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(341, 20);
         this.tbName.TabIndex = 6;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(12, 71);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 30;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.rbDivision_CheckedChanged);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(12, 44);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 29;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbDivision_CheckedChanged);
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(120, 70);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(215, 22);
         this.cbAgents.TabIndex = 28;
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(120, 43);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(215, 22);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 27;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.rbAll);
         this.groupBox1.Controls.Add(this.cbDivisions);
         this.groupBox1.Controls.Add(this.rbAgents);
         this.groupBox1.Controls.Add(this.rbDivision);
         this.groupBox1.Controls.Add(this.cbAgents);
         this.groupBox1.Location = new System.Drawing.Point(19, 12);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(341, 109);
         this.groupBox1.TabIndex = 31;
         this.groupBox1.TabStop = false;
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Checked = true;
         this.rbAll.Location = new System.Drawing.Point(12, 20);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 31;
         this.rbAll.TabStop = true;
         this.rbAll.Text = "Все";
         this.rbAll.UseVisualStyleBackColor = true;
         // 
         // FmReportPresent
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(385, 262);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.button1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmReportPresent";
         this.Text = "Отчет по присутсвию товара";
         this.Load += new System.EventHandler(this.FmReportPresent_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbAll;
   }
}
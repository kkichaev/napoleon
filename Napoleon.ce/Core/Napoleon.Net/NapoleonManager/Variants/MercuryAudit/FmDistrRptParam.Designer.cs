namespace GRSoft.NapoleonManager
{
   partial class FmDistrRptParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrRptParam));
         this.label1 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.btnExcel = new System.Windows.Forms.Button();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(39, 104);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "с";
         // 
         // dtpStart
         // 
         this.dtpStart.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpStart.Location = new System.Drawing.Point(81, 97);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(219, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(39, 128);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "по";
         // 
         // dtpFinish
         // 
         this.dtpFinish.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpFinish.Location = new System.Drawing.Point(81, 125);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(219, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(225, 189);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 4;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(10, 64);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 26;
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.rbAll_CheckedChanged);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(10, 33);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 25;
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbAll_CheckedChanged);
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(119, 60);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(183, 22);
         this.cbAgents.TabIndex = 24;
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(119, 33);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(181, 22);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 23;
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Checked = true;
         this.rbAll.Location = new System.Drawing.Point(10, 5);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 27;
         this.rbAll.TabStop = true;
         this.rbAll.Text = "Все";
         this.rbAll.UseVisualStyleBackColor = true;
         this.rbAll.CheckedChanged += new System.EventHandler(this.rbAll_CheckedChanged);
         // 
         // FmDistrRptParam
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(312, 218);
         this.Controls.Add(this.rbAll);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrRptParam";
         this.Text = "Отчёт по дистрибуции";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      public System.Windows.Forms.Button btnExcel;
      public System.Windows.Forms.RadioButton rbAgents;
      public System.Windows.Forms.RadioButton rbDivision;
      public System.Windows.Forms.RadioButton rbAll;
      public System.Windows.Forms.ComboBox cbAgents;
      public System.Windows.Forms.ComboBox cbDivisions;
      public System.Windows.Forms.DateTimePicker dtpStart;
      public System.Windows.Forms.DateTimePicker dtpFinish;
   }
}
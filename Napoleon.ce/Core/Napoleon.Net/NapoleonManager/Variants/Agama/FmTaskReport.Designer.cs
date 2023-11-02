namespace GRSoft.NapoleonManager
{
   partial class FmTaskReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmTaskReport));
         this.label1 = new System.Windows.Forms.Label();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.btnExcel = new System.Windows.Forms.Button();
         this.lbAgent = new System.Windows.Forms.CheckedListBox();
         this.btnSelect = new System.Windows.Forms.Button();
         this.btnUnselect = new System.Windows.Forms.Button();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Агент";
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.dtpFinish);
         this.groupBox1.Controls.Add(this.dtpStart);
         this.groupBox1.Controls.Add(this.label3);
         this.groupBox1.Controls.Add(this.label2);
         this.groupBox1.Location = new System.Drawing.Point(260, 9);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(267, 99);
         this.groupBox1.TabIndex = 2;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Период";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(6, 18);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(13, 14);
         this.label2.TabIndex = 0;
         this.label2.Text = "с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(6, 62);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 1;
         this.label3.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(31, 19);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(140, 20);
         this.dtpStart.TabIndex = 2;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(31, 56);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(140, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(356, 183);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 4;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // lbAgent
         // 
         this.lbAgent.FormattingEnabled = true;
         this.lbAgent.Location = new System.Drawing.Point(12, 26);
         this.lbAgent.Name = "lbAgent";
         this.lbAgent.Size = new System.Drawing.Size(242, 229);
         this.lbAgent.TabIndex = 5;
         // 
         // btnSelect
         // 
         this.btnSelect.Location = new System.Drawing.Point(12, 262);
         this.btnSelect.Name = "btnSelect";
         this.btnSelect.Size = new System.Drawing.Size(90, 23);
         this.btnSelect.TabIndex = 6;
         this.btnSelect.Text = "Выбрать все";
         this.btnSelect.UseVisualStyleBackColor = true;
         this.btnSelect.Click += new System.EventHandler(this.btnSelect_Click);
         // 
         // btnUnselect
         // 
         this.btnUnselect.Location = new System.Drawing.Point(179, 261);
         this.btnUnselect.Name = "btnUnselect";
         this.btnUnselect.Size = new System.Drawing.Size(75, 23);
         this.btnUnselect.TabIndex = 7;
         this.btnUnselect.Text = "Сбросить";
         this.btnUnselect.UseVisualStyleBackColor = true;
         this.btnUnselect.Click += new System.EventHandler(this.btnUnselect_Click);
         // 
         // FmTaskReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(539, 297);
         this.Controls.Add(this.btnUnselect);
         this.Controls.Add(this.btnSelect);
         this.Controls.Add(this.lbAgent);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmTaskReport";
         this.Text = "Отчет по задачам";
         this.Load += new System.EventHandler(this.FmTaskReport_Load);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.CheckedListBox lbAgent;
      private System.Windows.Forms.Button btnSelect;
      private System.Windows.Forms.Button btnUnselect;
   }
}
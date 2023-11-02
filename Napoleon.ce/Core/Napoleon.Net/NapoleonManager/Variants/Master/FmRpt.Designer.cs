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
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.clbAgent = new System.Windows.Forms.CheckedListBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.cbCheck = new System.Windows.Forms.ToolStripButton();
         this.cbUncheck = new System.Windows.Forms.ToolStripButton();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnExcel = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(39, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Месяц";
         // 
         // dtpDate
         // 
         this.dtpDate.CustomFormat = "MMMM";
         this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpDate.Location = new System.Drawing.Point(57, 8);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.ShowUpDown = true;
         this.dtpDate.Size = new System.Drawing.Size(200, 20);
         this.dtpDate.TabIndex = 1;
         // 
         // clbAgent
         // 
         this.clbAgent.Dock = System.Windows.Forms.DockStyle.Fill;
         this.clbAgent.FormattingEnabled = true;
         this.clbAgent.Location = new System.Drawing.Point(0, 25);
         this.clbAgent.Name = "clbAgent";
         this.clbAgent.Size = new System.Drawing.Size(413, 392);
         this.clbAgent.TabIndex = 2;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.clbAgent);
         this.panel1.Controls.Add(this.toolStrip1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 34);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(413, 417);
         this.panel1.TabIndex = 5;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cbCheck,
            this.cbUncheck});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(413, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // cbCheck
         // 
         this.cbCheck.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.cbCheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.checkbox;
         this.cbCheck.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.cbCheck.Name = "cbCheck";
         this.cbCheck.Size = new System.Drawing.Size(23, 22);
         this.cbCheck.Tag = "true";
         this.cbCheck.Text = "Выбрать все";
         this.cbCheck.Click += new System.EventHandler(this.CheckUnCheck);
         // 
         // cbUncheck
         // 
         this.cbUncheck.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.cbUncheck.Image = global::GRSoft.NapoleonManager.Properties.Resources.uncheckbox;
         this.cbUncheck.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.cbUncheck.Name = "cbUncheck";
         this.cbUncheck.Size = new System.Drawing.Size(23, 22);
         this.cbUncheck.Tag = "false";
         this.cbUncheck.Text = "Сбросить все";
         this.cbUncheck.Click += new System.EventHandler(this.CheckUnCheck);
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnExcel);
         this.panel2.Controls.Add(this.dtpDate);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(413, 34);
         this.panel2.TabIndex = 6;
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(313, 5);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 2;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // FmRpt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(413, 451);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRpt";
         this.Text = "Отчет результативность посещений";
         this.Load += new System.EventHandler(this.FmRpt_Load);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.CheckedListBox clbAgent;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton cbCheck;
      private System.Windows.Forms.ToolStripButton cbUncheck;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnExcel;
   }
}
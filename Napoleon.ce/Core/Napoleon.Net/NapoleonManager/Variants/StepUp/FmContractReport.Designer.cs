namespace GRSoft.NapoleonManager
{
   partial class FmContractReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmContractReport));
         this.label1 = new System.Windows.Forms.Label();
         this.lbContract = new System.Windows.Forms.ListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnReport = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Dock = System.Windows.Forms.DockStyle.Top;
         this.label1.Location = new System.Drawing.Point(0, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(63, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Контракты";
         // 
         // lbContract
         // 
         this.lbContract.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbContract.FormattingEnabled = true;
         this.lbContract.ItemHeight = 14;
         this.lbContract.Location = new System.Drawing.Point(0, 14);
         this.lbContract.Name = "lbContract";
         this.lbContract.Size = new System.Drawing.Size(509, 328);
         this.lbContract.TabIndex = 2;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(509, 25);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.lbContract);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(509, 342);
         this.panel1.TabIndex = 4;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnReport);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 324);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(509, 43);
         this.panel2.TabIndex = 5;
         // 
         // btnReport
         // 
         this.btnReport.Location = new System.Drawing.Point(424, 10);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 0;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 3, 25, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(40, 0);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 25);
         this.dpv.Start = new System.DateTime(2015, 3, 25, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // FmContractReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(509, 367);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.dpv);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmContractReport";
         this.Text = "Отчет о контрактах";
         this.Load += new System.EventHandler(this.FmContractReport_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ListBox lbContract;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnReport;
   }
}
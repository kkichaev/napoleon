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
         this.lbContract = new System.Windows.Forms.ListBox();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.toolStrip2 = new System.Windows.Forms.ToolStrip();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.panel3 = new System.Windows.Forms.Panel();
         this.btnFolder = new System.Windows.Forms.Button();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnReport = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbPhoto = new System.Windows.Forms.CheckBox();
         this.toolStrip1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.toolStrip2.SuspendLayout();
         this.panel3.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // lbContract
         // 
         this.lbContract.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbContract.FormattingEnabled = true;
         this.lbContract.ItemHeight = 14;
         this.lbContract.Location = new System.Drawing.Point(0, 57);
         this.lbContract.Name = "lbContract";
         this.lbContract.Size = new System.Drawing.Size(509, 285);
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
         this.panel1.Controls.Add(this.cbPhoto);
         this.panel1.Controls.Add(this.lbContract);
         this.panel1.Controls.Add(this.toolStrip2);
         this.panel1.Controls.Add(this.panel3);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 25);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(509, 342);
         this.panel1.TabIndex = 4;
         // 
         // toolStrip2
         // 
         this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1});
         this.toolStrip2.Location = new System.Drawing.Point(0, 32);
         this.toolStrip2.Name = "toolStrip2";
         this.toolStrip2.Size = new System.Drawing.Size(509, 25);
         this.toolStrip2.TabIndex = 4;
         this.toolStrip2.Text = "toolStrip2";
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(66, 22);
         this.toolStripLabel1.Text = "Контракты";
         // 
         // panel3
         // 
         this.panel3.Controls.Add(this.btnFolder);
         this.panel3.Controls.Add(this.tbPath);
         this.panel3.Controls.Add(this.label2);
         this.panel3.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel3.Location = new System.Drawing.Point(0, 0);
         this.panel3.Name = "panel3";
         this.panel3.Size = new System.Drawing.Size(509, 32);
         this.panel3.TabIndex = 3;
         // 
         // btnFolder
         // 
         this.btnFolder.Location = new System.Drawing.Point(434, 4);
         this.btnFolder.Name = "btnFolder";
         this.btnFolder.Size = new System.Drawing.Size(63, 23);
         this.btnFolder.TabIndex = 2;
         this.btnFolder.Text = "...";
         this.btnFolder.UseVisualStyleBackColor = true;
         this.btnFolder.Click += new System.EventHandler(this.btnFolder_Click);
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(48, 6);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(375, 20);
         this.tbPath.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 10);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(37, 14);
         this.label2.TabIndex = 0;
         this.label2.Text = "Папка";
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
         // cbPhoto
         // 
         this.cbPhoto.AutoSize = true;
         this.cbPhoto.Checked = true;
         this.cbPhoto.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPhoto.Location = new System.Drawing.Point(96, 36);
         this.cbPhoto.Name = "cbPhoto";
         this.cbPhoto.Size = new System.Drawing.Size(62, 18);
         this.cbPhoto.TabIndex = 5;
         this.cbPhoto.Text = "С фото";
         this.cbPhoto.UseVisualStyleBackColor = true;
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
         this.toolStrip2.ResumeLayout(false);
         this.toolStrip2.PerformLayout();
         this.panel3.ResumeLayout(false);
         this.panel3.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.ListBox lbContract;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.Panel panel3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.Button btnFolder;
      private System.Windows.Forms.ToolStrip toolStrip2;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.CheckBox cbPhoto;
   }
}
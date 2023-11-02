using System.Windows.Forms;
namespace GRSoft.NapoleonManager
{
   partial class FmScriptTimeRptParam3 : Form
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmScriptTimeRptParam3));
         this.label1 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.btnExcel = new System.Windows.Forms.Button();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.cbContract = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.label4 = new System.Windows.Forms.Label();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(11, 68);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "с";
         // 
         // dtpStart
         // 
         this.dtpStart.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpStart.Location = new System.Drawing.Point(53, 61);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(431, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(11, 92);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "по";
         // 
         // dtpFinish
         // 
         this.dtpFinish.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpFinish.Location = new System.Drawing.Point(53, 89);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(431, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(229, 163);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 4;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(105, 28);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(379, 22);
         this.cbAgents.TabIndex = 24;
         // 
         // cbContract
         // 
         this.cbContract.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbContract.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbContract.FormattingEnabled = true;
         this.cbContract.Location = new System.Drawing.Point(77, 123);
         this.cbContract.Name = "cbContract";
         this.cbContract.Size = new System.Drawing.Size(407, 22);
         this.cbContract.TabIndex = 25;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(11, 123);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(55, 14);
         this.label3.TabIndex = 26;
         this.label3.Text = "Контракт";
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(524, 25);
         this.toolStrip1.TabIndex = 27;
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
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(11, 33);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(80, 14);
         this.label4.TabIndex = 28;
         this.label4.Text = "Пользователь";
         // 
         // FmScriptTimeRptParam3
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(524, 199);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.cbContract);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmScriptTimeRptParam3";
         this.Text = "Времени пребывания в точке";
         this.Load += new System.EventHandler(this.FmScriptTimeRptParam3_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      public System.Windows.Forms.Button btnExcel;
      public System.Windows.Forms.ComboBox cbAgents;
      public System.Windows.Forms.DateTimePicker dtpStart;
      public ComboBox cbContract;
      private Label label3;
      public System.Windows.Forms.DateTimePicker dtpFinish;
      private ToolStrip toolStrip1;
      private ToolStripButton btnRefresh;
      private Label label4;
   }
}
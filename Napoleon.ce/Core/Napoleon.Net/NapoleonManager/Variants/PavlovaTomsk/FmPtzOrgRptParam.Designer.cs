namespace GRSoft.NapoleonManager
{
   partial class FmPtzOrgRptParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPtzOrgRptParam));
         this.btnExcel = new System.Windows.Forms.Button();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(89, 89);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 25);
         this.btnExcel.TabIndex = 29;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpFinish.Location = new System.Drawing.Point(53, 42);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(210, 20);
         this.dtpFinish.TabIndex = 28;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(11, 45);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 27;
         this.label2.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dtpStart.Location = new System.Drawing.Point(53, 12);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(210, 20);
         this.dtpStart.TabIndex = 26;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(11, 19);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 25;
         this.label1.Text = "с";
         // 
         // FmPtzOrgRptParam
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(275, 124);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPtzOrgRptParam";
         this.Text = "Отчет Ценсус";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      public System.Windows.Forms.Button btnExcel;
      public System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label2;
      public System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label1;
   }
}
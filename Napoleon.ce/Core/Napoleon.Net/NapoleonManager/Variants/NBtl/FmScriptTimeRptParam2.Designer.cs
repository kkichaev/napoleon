namespace GRSoft.NapoleonManager
{
   partial class FmScriptTimeRptParam2
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmScriptTimeRptParam2));
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.btnExcel = new System.Windows.Forms.Button();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // cbDivision
         // 
         this.cbDivision.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(12, 12);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(260, 21);
         this.cbDivision.TabIndex = 30;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(87, 121);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 29;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(51, 77);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(219, 20);
         this.dtpFinish.TabIndex = 28;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 80);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 27;
         this.label2.Text = "по";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(51, 49);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(219, 20);
         this.dtpStart.TabIndex = 26;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 56);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 25;
         this.label1.Text = "с";
         // 
         // FmScriptTimeRptParam2
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(286, 158);
         this.Controls.Add(this.cbDivision);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.label1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmScriptTimeRptParam2";
         this.Text = "Временя пребывания в точке 2";
         this.Load += new System.EventHandler(this.FmScriptTimeRptParam2_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      public System.Windows.Forms.ComboBox cbDivision;
      public System.Windows.Forms.Button btnExcel;
      public System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label2;
      public System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label1;
   }
}
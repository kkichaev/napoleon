namespace GRSoft.NapoleonManager
{
   partial class FmVizitReportParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVizitReportParams));
         this.label1 = new System.Windows.Forms.Label();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.btnOK = new System.Windows.Forms.Button();
         this.datePeriodView1 = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbPhoto = new System.Windows.Forms.CheckBox();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Агент";
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(55, 6);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(187, 22);
         this.cbAgent.TabIndex = 1;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(152, 67);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 3;
         this.btnOK.Text = "Excel";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // datePeriodView1
         // 
         this.datePeriodView1.Finish = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.Location = new System.Drawing.Point(10, 34);
         this.datePeriodView1.Name = "datePeriodView1";
         this.datePeriodView1.Size = new System.Drawing.Size(367, 27);
         this.datePeriodView1.Start = new System.DateTime(2016, 2, 2, 0, 0, 0, 0);
         this.datePeriodView1.TabIndex = 2;
         // 
         // cbPhoto
         // 
         this.cbPhoto.AutoSize = true;
         this.cbPhoto.Checked = true;
         this.cbPhoto.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPhoto.Location = new System.Drawing.Point(248, 8);
         this.cbPhoto.Name = "cbPhoto";
         this.cbPhoto.Size = new System.Drawing.Size(61, 18);
         this.cbPhoto.TabIndex = 4;
         this.cbPhoto.Text = "с фото";
         this.cbPhoto.UseVisualStyleBackColor = true;
         // 
         // FmVizitReportParams
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(413, 100);
         this.Controls.Add(this.cbPhoto);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.datePeriodView1);
         this.Controls.Add(this.cbAgent);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVizitReportParams";
         this.Text = "Отчет о визитах";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmVisitReportParams_FormClosing);
         this.Load += new System.EventHandler(this.FmVisitReportParams_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbAgent;
      private DatePeriodView datePeriodView1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.CheckBox cbPhoto;
   }
}
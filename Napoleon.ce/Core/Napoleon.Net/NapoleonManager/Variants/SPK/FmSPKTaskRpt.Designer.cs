namespace GRSoft.NapoleonManager
{
   partial class FmSPKTaskRpt
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSPKTaskRpt));
         this.btnExcel = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(143, 99);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(108, 23);
         this.btnExcel.TabIndex = 5;
         this.btnExcel.Text = "Построить отчет";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(11, 12);
         this.dpv.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(378, 31);
         this.dpv.Start = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.TabIndex = 4;
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(52, 58);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(319, 21);
         this.cbAgents.TabIndex = 6;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(11, 61);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(36, 13);
         this.label1.TabIndex = 7;
         this.label1.Text = "Агент";
         // 
         // FmSPKTaskRpt
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(395, 143);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.dpv);
         this.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmSPKTaskRpt";
         this.Text = "Отчет по задачам";
         this.Load += new System.EventHandler(this.FmSPKTaskRpt_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Label label1;
   }
}
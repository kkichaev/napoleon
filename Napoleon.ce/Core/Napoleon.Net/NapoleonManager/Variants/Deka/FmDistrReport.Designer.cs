namespace GRSoft.NapoleonManager
{
   partial class FmDistrReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistrReport));
         this.dpReport = new GRSoft.NapoleonManager.DatePeriodView();
         this.lbItems = new System.Windows.Forms.CheckedListBox();
         this.label1 = new System.Windows.Forms.Label();
         this.cbSelectAll = new System.Windows.Forms.CheckBox();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // dpReport
         // 
         this.dpReport.Finish = new System.DateTime(2017, 2, 22, 0, 0, 0, 0);
         this.dpReport.Location = new System.Drawing.Point(12, 23);
         this.dpReport.Name = "dpReport";
         this.dpReport.Size = new System.Drawing.Size(367, 27);
         this.dpReport.Start = new System.DateTime(2017, 2, 22, 0, 0, 0, 0);
         this.dpReport.TabIndex = 0;
         // 
         // lbItems
         // 
         this.lbItems.CheckOnClick = true;
         this.lbItems.FormattingEnabled = true;
         this.lbItems.Location = new System.Drawing.Point(12, 84);
         this.lbItems.Name = "lbItems";
         this.lbItems.Size = new System.Drawing.Size(367, 169);
         this.lbItems.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 68);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(81, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Дистрибуторы";
         // 
         // cbSelectAll
         // 
         this.cbSelectAll.AutoSize = true;
         this.cbSelectAll.Location = new System.Drawing.Point(15, 259);
         this.cbSelectAll.Name = "cbSelectAll";
         this.cbSelectAll.Size = new System.Drawing.Size(102, 17);
         this.cbSelectAll.TabIndex = 3;
         this.cbSelectAll.Text = "Выделить всех";
         this.cbSelectAll.UseVisualStyleBackColor = true;
         this.cbSelectAll.CheckedChanged += new System.EventHandler(this.cbSelectAll_CheckedChanged);
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(147, 294);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 4;
         this.button1.Text = "Отчет";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmDistrReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(396, 338);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.cbSelectAll);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.lbItems);
         this.Controls.Add(this.dpReport);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistrReport";
         this.Text = "Отчет для дистрибютора";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpReport;
      private System.Windows.Forms.CheckedListBox lbItems;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.CheckBox cbSelectAll;
      private System.Windows.Forms.Button button1;
   }
}
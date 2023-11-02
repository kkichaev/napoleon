namespace GRSoft.NapoleonManager
{
   partial class FmAvalonSalesReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAvalonSalesReport));
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.button1 = new System.Windows.Forms.Button();
         this.cbOrg = new System.Windows.Forms.CheckBox();
         this.cbPrice = new System.Windows.Forms.CheckBox();
         this.cbDetail = new System.Windows.Forms.CheckBox();
         this.SuspendLayout();
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(132, 12);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(149, 20);
         this.dtpStart.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(107, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(103, 46);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "по";
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(132, 43);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(149, 20);
         this.dtpEnd.TabIndex = 2;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(165, 226);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 4;
         this.button1.Text = "Отчет";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // cbOrg
         // 
         this.cbOrg.AutoSize = true;
         this.cbOrg.Checked = true;
         this.cbOrg.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbOrg.Location = new System.Drawing.Point(132, 95);
         this.cbOrg.Name = "cbOrg";
         this.cbOrg.Size = new System.Drawing.Size(142, 17);
         this.cbOrg.TabIndex = 5;
         this.cbOrg.Text = "Краткий по автоматам";
         this.cbOrg.UseVisualStyleBackColor = true;
         // 
         // cbPrice
         // 
         this.cbPrice.AutoSize = true;
         this.cbPrice.Checked = true;
         this.cbPrice.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPrice.Location = new System.Drawing.Point(132, 118);
         this.cbPrice.Name = "cbPrice";
         this.cbPrice.Size = new System.Drawing.Size(129, 17);
         this.cbPrice.TabIndex = 6;
         this.cbPrice.Text = "Краткий по товарам";
         this.cbPrice.UseVisualStyleBackColor = true;
         // 
         // cbDetail
         // 
         this.cbDetail.AutoSize = true;
         this.cbDetail.Checked = true;
         this.cbDetail.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbDetail.Location = new System.Drawing.Point(132, 141);
         this.cbDetail.Name = "cbDetail";
         this.cbDetail.Size = new System.Drawing.Size(93, 17);
         this.cbDetail.TabIndex = 7;
         this.cbDetail.Text = "Развернутый";
         this.cbDetail.UseVisualStyleBackColor = true;
         // 
         // FmAvalonSalesReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(415, 261);
         this.Controls.Add(this.cbDetail);
         this.Controls.Add(this.cbPrice);
         this.Controls.Add(this.cbOrg);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpStart);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAvalonSalesReport";
         this.Text = "Отчет по продажам";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.CheckBox cbOrg;
      private System.Windows.Forms.CheckBox cbPrice;
      private System.Windows.Forms.CheckBox cbDetail;
   }
}
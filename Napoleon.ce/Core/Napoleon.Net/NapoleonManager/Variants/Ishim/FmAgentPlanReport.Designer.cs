namespace GRSoft.NapoleonManager
{
   partial class FmAgentPlanReport
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
         this.dtBegin = new System.Windows.Forms.DateTimePicker();
         this.dtEnd = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.ok = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.cbType = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // dtBegin
         // 
         this.dtBegin.CustomFormat = "MMMM yyyy";
         this.dtBegin.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtBegin.Location = new System.Drawing.Point(93, 21);
         this.dtBegin.Name = "dtBegin";
         this.dtBegin.ShowUpDown = true;
         this.dtBegin.Size = new System.Drawing.Size(142, 20);
         this.dtBegin.TabIndex = 0;
         // 
         // dtEnd
         // 
         this.dtEnd.CustomFormat = "MMMM yyyy";
         this.dtEnd.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtEnd.Location = new System.Drawing.Point(93, 47);
         this.dtEnd.Name = "dtEnd";
         this.dtEnd.ShowUpDown = true;
         this.dtEnd.Size = new System.Drawing.Size(142, 20);
         this.dtEnd.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(69, 25);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(67, 51);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "по";
         // 
         // ok
         // 
         this.ok.Location = new System.Drawing.Point(110, 112);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 4;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(24, 76);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(62, 13);
         this.label3.TabIndex = 5;
         this.label3.Text = "Тип отчета";
         // 
         // cbType
         // 
         this.cbType.FormattingEnabled = true;
         this.cbType.Items.AddRange(new object[] {
            "HTML",
            "CSV"});
         this.cbType.Location = new System.Drawing.Point(93, 73);
         this.cbType.Name = "cbType";
         this.cbType.Size = new System.Drawing.Size(142, 21);
         this.cbType.TabIndex = 6;
         // 
         // FmAgentPlanReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(302, 164);
         this.Controls.Add(this.cbType);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtEnd);
         this.Controls.Add(this.dtBegin);
         this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
         this.Name = "FmAgentPlanReport";
         this.Text = "Параметры отчета";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtBegin;
      private System.Windows.Forms.DateTimePicker dtEnd;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbType;
   }
}
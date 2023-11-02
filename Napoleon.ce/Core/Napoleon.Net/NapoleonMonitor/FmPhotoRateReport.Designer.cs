namespace GRSoft.NapoleonManager
{
   partial class FmPhotoRateReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPhotoRateReport));
         this.button1 = new System.Windows.Forms.Button();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Anchor = System.Windows.Forms.AnchorStyles.None;
         this.button1.Location = new System.Drawing.Point(156, 104);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 25);
         this.button1.TabIndex = 0;
         this.button1.Text = "ОК";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(63, 68);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(167, 20);
         this.dtpEnd.TabIndex = 6;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(63, 40);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 5;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(15, 43);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 14);
         this.label2.TabIndex = 9;
         this.label2.Text = "Дата с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(38, 71);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 10;
         this.label3.Text = "по";
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(63, 11);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(167, 22);
         this.cbAgents.TabIndex = 11;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(22, 14);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 12;
         this.label1.Text = "Агент";
         // 
         // FmPhotoRateReport
         // 
         this.AcceptButton = this.button1;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(243, 141);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.button1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPhotoRateReport";
         this.Text = "Параметры отчета";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Label label1;
   }
}
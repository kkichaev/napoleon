namespace GRSoft.NapoleonManager
{
   partial class FmRemnReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmRemnReport));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbDiv = new System.Windows.Forms.ComboBox();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(21, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(85, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Подразделение";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(21, 46);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(44, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Период";
         // 
         // cbDiv
         // 
         this.cbDiv.FormattingEnabled = true;
         this.cbDiv.Location = new System.Drawing.Point(112, 12);
         this.cbDiv.Name = "cbDiv";
         this.cbDiv.Size = new System.Drawing.Size(249, 22);
         this.cbDiv.TabIndex = 2;
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(112, 40);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(200, 20);
         this.dtpStart.TabIndex = 3;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(112, 66);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(200, 20);
         this.dtpFinish.TabIndex = 4;
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(112, 106);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(160, 23);
         this.button1.TabIndex = 5;
         this.button1.Text = "Построить отчет";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmRemnReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(458, 143);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.dtpFinish);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.cbDiv);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmRemnReport";
         this.Text = "Отчет по присутсвию";
         this.Load += new System.EventHandler(this.FmRemnReport_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbDiv;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Button button1;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmAuditParam
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAuditParam));
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.cbMatrix = new System.Windows.Forms.ComboBox();
         this.btnExcel = new System.Windows.Forms.Button();
         this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 7);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Агент";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 110);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(51, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Матрица";
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(12, 27);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(248, 22);
         this.cbAgents.TabIndex = 2;
         // 
         // cbMatrix
         // 
         this.cbMatrix.FormattingEnabled = true;
         this.cbMatrix.Location = new System.Drawing.Point(12, 127);
         this.cbMatrix.Name = "cbMatrix";
         this.cbMatrix.Size = new System.Drawing.Size(248, 22);
         this.cbMatrix.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(144, 172);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 4;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // dateTimePicker1
         // 
         this.dateTimePicker1.Location = new System.Drawing.Point(12, 78);
         this.dateTimePicker1.Name = "dateTimePicker1";
         this.dateTimePicker1.Size = new System.Drawing.Size(248, 20);
         this.dateTimePicker1.TabIndex = 6;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 61);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(33, 14);
         this.label3.TabIndex = 5;
         this.label3.Text = "Дата";
         // 
         // FmAuditParam
         // 
         this.AcceptButton = this.btnExcel;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(344, 211);
         this.Controls.Add(this.dateTimePicker1);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.cbMatrix);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAuditParam";
         this.Text = "Аудит дистрибьюции";
         this.Load += new System.EventHandler(this.FmAuditParam_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.ComboBox cbMatrix;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.DateTimePicker dateTimePicker1;
      private System.Windows.Forms.Label label3;
   }
}
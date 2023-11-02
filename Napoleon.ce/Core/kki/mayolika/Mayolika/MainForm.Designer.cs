namespace Mayolika
{
   partial class MainForm
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
         this.label1 = new System.Windows.Forms.Label();
         this.tbBasePath = new System.Windows.Forms.TextBox();
         this.btnBasePath = new System.Windows.Forms.Button();
         this.btnExcel = new System.Windows.Forms.Button();
         this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpDate = new System.Windows.Forms.DateTimePicker();
         this.lblWait = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 19);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(32, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "База";
         // 
         // tbBasePath
         // 
         this.tbBasePath.Location = new System.Drawing.Point(58, 19);
         this.tbBasePath.Name = "tbBasePath";
         this.tbBasePath.Size = new System.Drawing.Size(341, 20);
         this.tbBasePath.TabIndex = 1;
         // 
         // btnBasePath
         // 
         this.btnBasePath.Location = new System.Drawing.Point(418, 17);
         this.btnBasePath.Name = "btnBasePath";
         this.btnBasePath.Size = new System.Drawing.Size(51, 21);
         this.btnBasePath.TabIndex = 2;
         this.btnBasePath.Text = "...";
         this.btnBasePath.UseVisualStyleBackColor = true;
         this.btnBasePath.Click += new System.EventHandler(this.btnBasePath_Click);
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(15, 127);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 5;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // openFileDialog1
         // 
         this.openFileDialog1.FileName = "openFileDialog1";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 64);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(33, 13);
         this.label2.TabIndex = 4;
         this.label2.Text = "Дата";
         // 
         // dtpDate
         // 
         this.dtpDate.CustomFormat = "MMMM yyyyy";
         this.dtpDate.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dtpDate.Location = new System.Drawing.Point(58, 58);
         this.dtpDate.Name = "dtpDate";
         this.dtpDate.ShowUpDown = true;
         this.dtpDate.Size = new System.Drawing.Size(200, 20);
         this.dtpDate.TabIndex = 6;
         // 
         // lblWait
         // 
         this.lblWait.AutoSize = true;
         this.lblWait.Font = new System.Drawing.Font("Arial", 15.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lblWait.ForeColor = System.Drawing.Color.Red;
         this.lblWait.Location = new System.Drawing.Point(2, 88);
         this.lblWait.Name = "lblWait";
         this.lblWait.Size = new System.Drawing.Size(484, 24);
         this.lblWait.TabIndex = 7;
         this.lblWait.Text = "Пожалуйста, подождите, формирую табель...";
         // 
         // MainForm
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(477, 165);
         this.Controls.Add(this.lblWait);
         this.Controls.Add(this.dtpDate);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.btnBasePath);
         this.Controls.Add(this.tbBasePath);
         this.Controls.Add(this.label1);
         this.Name = "MainForm";
         this.Text = "Табель рабочего времени";
         this.Load += new System.EventHandler(this.MainForm_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.MainForm_FormClosed);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbBasePath;
      private System.Windows.Forms.Button btnBasePath;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.OpenFileDialog openFileDialog1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpDate;
      private System.Windows.Forms.Label lblWait;

   }
}


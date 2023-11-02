namespace GRSoft.NapoleonManager
{
   partial class FmMonitoringDBF
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMonitoringDBF));
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.btnDBF = new System.Windows.Forms.Button();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.tbnDirectory = new System.Windows.Forms.Button();
         this.dialog = new System.Windows.Forms.FolderBrowserDialog();
         this.label4 = new System.Windows.Forms.Label();
         this.tbTable = new System.Windows.Forms.TextBox();
         this.cbOpen = new System.Windows.Forms.CheckBox();
         this.SuspendLayout();
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(62, 9);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(139, 20);
         this.dtpBegin.TabIndex = 0;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(62, 38);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(139, 20);
         this.dtpEnd.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 15);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 14);
         this.label1.TabIndex = 2;
         this.label1.Text = "с";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 44);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "по";
         // 
         // btnDBF
         // 
         this.btnDBF.Location = new System.Drawing.Point(140, 158);
         this.btnDBF.Name = "btnDBF";
         this.btnDBF.Size = new System.Drawing.Size(75, 23);
         this.btnDBF.TabIndex = 4;
         this.btnDBF.Text = "DBF";
         this.btnDBF.UseVisualStyleBackColor = true;
         this.btnDBF.Click += new System.EventHandler(this.btnDBF_Click);
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(62, 64);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(243, 20);
         this.tbPath.TabIndex = 5;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(12, 70);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(36, 14);
         this.label3.TabIndex = 6;
         this.label3.Text = "папка";
         // 
         // tbnDirectory
         // 
         this.tbnDirectory.Location = new System.Drawing.Point(311, 65);
         this.tbnDirectory.Name = "tbnDirectory";
         this.tbnDirectory.Size = new System.Drawing.Size(37, 20);
         this.tbnDirectory.TabIndex = 7;
         this.tbnDirectory.Text = "...";
         this.tbnDirectory.UseVisualStyleBackColor = true;
         this.tbnDirectory.Click += new System.EventHandler(this.tbnDirectory_Click);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(12, 99);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(49, 14);
         this.label4.TabIndex = 8;
         this.label4.Text = "таблица";
         // 
         // tbTable
         // 
         this.tbTable.Location = new System.Drawing.Point(62, 93);
         this.tbTable.Name = "tbTable";
         this.tbTable.Size = new System.Drawing.Size(100, 20);
         this.tbTable.TabIndex = 9;
         // 
         // cbOpen
         // 
         this.cbOpen.AutoSize = true;
         this.cbOpen.Location = new System.Drawing.Point(62, 119);
         this.cbOpen.Name = "cbOpen";
         this.cbOpen.Size = new System.Drawing.Size(183, 18);
         this.cbOpen.TabIndex = 10;
         this.cbOpen.Text = "Открыть файл после создания";
         this.cbOpen.UseVisualStyleBackColor = true;
         // 
         // FmMonitoringDBF
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(355, 193);
         this.Controls.Add(this.cbOpen);
         this.Controls.Add(this.tbTable);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.tbnDirectory);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.tbPath);
         this.Controls.Add(this.btnDBF);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMonitoringDBF";
         this.Text = "Выгрузка в DBF мониторинг";
         this.Load += new System.EventHandler(this.FmMonitoringDBF_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmMonitoringDBF_FormClosed);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnDBF;
      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Button tbnDirectory;
      private System.Windows.Forms.FolderBrowserDialog dialog;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbTable;
      private System.Windows.Forms.CheckBox cbOpen;
   }
}
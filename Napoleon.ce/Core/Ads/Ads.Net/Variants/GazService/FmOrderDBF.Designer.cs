namespace GRSoft.Ads
{
   partial class FmOrderDBF
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrderDBF));
         this.label1 = new System.Windows.Forms.Label();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.btnPath = new System.Windows.Forms.Button();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpFrom = new System.Windows.Forms.DateTimePicker();
         this.dtpTill = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.btnStart = new System.Windows.Forms.Button();
         this.dialog = new System.Windows.Forms.FolderBrowserDialog();
         this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
         this.label4 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.cbBrigade = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 16);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(32, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Путь";
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(72, 13);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(244, 20);
         this.tbPath.TabIndex = 1;
         // 
         // btnPath
         // 
         this.btnPath.Location = new System.Drawing.Point(326, 12);
         this.btnPath.Name = "btnPath";
         this.btnPath.Size = new System.Drawing.Size(39, 23);
         this.btnPath.TabIndex = 2;
         this.btnPath.Text = "...";
         this.btnPath.UseVisualStyleBackColor = true;
         this.btnPath.Click += new System.EventHandler(this.btnPath_Click);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(13, 69);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(53, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "Период с";
         // 
         // dtpFrom
         // 
         this.dtpFrom.Location = new System.Drawing.Point(72, 66);
         this.dtpFrom.Name = "dtpFrom";
         this.dtpFrom.Size = new System.Drawing.Size(132, 20);
         this.dtpFrom.TabIndex = 4;
         // 
         // dtpTill
         // 
         this.dtpTill.Location = new System.Drawing.Point(235, 66);
         this.dtpTill.Name = "dtpTill";
         this.dtpTill.Size = new System.Drawing.Size(130, 20);
         this.dtpTill.TabIndex = 5;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(210, 69);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 6;
         this.label3.Text = "по";
         // 
         // btnStart
         // 
         this.btnStart.Location = new System.Drawing.Point(154, 131);
         this.btnStart.Name = "btnStart";
         this.btnStart.Size = new System.Drawing.Size(75, 23);
         this.btnStart.TabIndex = 7;
         this.btnStart.Text = "Выгрузка";
         this.btnStart.UseVisualStyleBackColor = true;
         this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
         // 
         // openFileDialog
         // 
         this.openFileDialog.FileName = "openFileDialog1";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(13, 42);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(33, 14);
         this.label4.TabIndex = 8;
         this.label4.Text = "Файл";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(72, 42);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(132, 20);
         this.tbName.TabIndex = 9;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(13, 99);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(48, 14);
         this.label5.TabIndex = 10;
         this.label5.Text = "Бригада";
         // 
         // cbBrigade
         // 
         this.cbBrigade.FormattingEnabled = true;
         this.cbBrigade.Location = new System.Drawing.Point(72, 96);
         this.cbBrigade.Name = "cbBrigade";
         this.cbBrigade.Size = new System.Drawing.Size(157, 22);
         this.cbBrigade.TabIndex = 11;
         // 
         // FmOrderDBF
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(377, 168);
         this.Controls.Add(this.cbBrigade);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.btnStart);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.dtpTill);
         this.Controls.Add(this.dtpFrom);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.btnPath);
         this.Controls.Add(this.tbPath);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrderDBF";
         this.Text = "Выгрузка заявок в DBF";
         this.Load += new System.EventHandler(this.FmOrderDBF_Load);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmOrderDBF_FormClosed);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.Button btnPath;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpFrom;
      private System.Windows.Forms.DateTimePicker dtpTill;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Button btnStart;
      private System.Windows.Forms.FolderBrowserDialog dialog;
      private System.Windows.Forms.OpenFileDialog openFileDialog;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.ComboBox cbBrigade;
   }
}
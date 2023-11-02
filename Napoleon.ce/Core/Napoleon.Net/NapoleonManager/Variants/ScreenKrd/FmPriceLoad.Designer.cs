namespace GRSoft.NapoleonManager
{
   partial class FmPriceLoad
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceLoad));
         this.label2 = new System.Windows.Forms.Label();
         this.panel1 = new System.Windows.Forms.Panel();
         this.bntLoad = new System.Windows.Forms.Button();
         this.progressBar1 = new System.Windows.Forms.ProgressBar();
         this.btnOpen = new System.Windows.Forms.Button();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 11);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(81, 13);
         this.label2.TabIndex = 10;
         this.label2.Text = "Путь до файла";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.bntLoad);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 78);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(502, 46);
         this.panel1.TabIndex = 7;
         // 
         // bntLoad
         // 
         this.bntLoad.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.bntLoad.Location = new System.Drawing.Point(369, 11);
         this.bntLoad.Name = "bntLoad";
         this.bntLoad.Size = new System.Drawing.Size(121, 23);
         this.bntLoad.TabIndex = 1;
         this.bntLoad.Text = "Загрузить";
         this.bntLoad.UseVisualStyleBackColor = true;
         this.bntLoad.Click += new System.EventHandler(this.bntLoad_Click);
         // 
         // progressBar1
         // 
         this.progressBar1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.progressBar1.Location = new System.Drawing.Point(97, 37);
         this.progressBar1.Name = "progressBar1";
         this.progressBar1.Size = new System.Drawing.Size(331, 23);
         this.progressBar1.TabIndex = 13;
         // 
         // btnOpen
         // 
         this.btnOpen.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOpen.Location = new System.Drawing.Point(433, 9);
         this.btnOpen.Name = "btnOpen";
         this.btnOpen.Size = new System.Drawing.Size(57, 23);
         this.btnOpen.TabIndex = 12;
         this.btnOpen.Text = "...";
         this.btnOpen.UseVisualStyleBackColor = true;
         this.btnOpen.Click += new System.EventHandler(this.btnOpen_Click);
         // 
         // tbPath
         // 
         this.tbPath.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbPath.Location = new System.Drawing.Point(97, 10);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(329, 20);
         this.tbPath.TabIndex = 11;
         // 
         // openFileDialog1
         // 
         this.openFileDialog1.FileName = "openFileDialog1";
         this.openFileDialog1.Filter = "Excel files|*.xls";
         // 
         // FmPriceLoad
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(502, 124);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.progressBar1);
         this.Controls.Add(this.btnOpen);
         this.Controls.Add(this.tbPath);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceLoad";
         this.Text = "Загрузка товаров из Excel";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button bntLoad;
      private System.Windows.Forms.ProgressBar progressBar1;
      private System.Windows.Forms.Button btnOpen;
      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.OpenFileDialog openFileDialog1;
   }
}
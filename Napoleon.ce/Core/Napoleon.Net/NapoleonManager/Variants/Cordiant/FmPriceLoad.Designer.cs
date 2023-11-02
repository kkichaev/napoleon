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
         this.panel1 = new System.Windows.Forms.Panel();
         this.bntLoad = new System.Windows.Forms.Button();
         this.progressBar1 = new System.Windows.Forms.ProgressBar();
         this.btnOpen = new System.Windows.Forms.Button();
         this.tbPath = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.bntLoad);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 82);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(507, 46);
         this.panel1.TabIndex = 7;
         // 
         // bntLoad
         // 
         this.bntLoad.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.bntLoad.Location = new System.Drawing.Point(374, 11);
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
         this.progressBar1.Location = new System.Drawing.Point(100, 44);
         this.progressBar1.Name = "progressBar1";
         this.progressBar1.Size = new System.Drawing.Size(379, 23);
         this.progressBar1.TabIndex = 13;
         // 
         // btnOpen
         // 
         this.btnOpen.Location = new System.Drawing.Point(422, 14);
         this.btnOpen.Name = "btnOpen";
         this.btnOpen.Size = new System.Drawing.Size(57, 23);
         this.btnOpen.TabIndex = 12;
         this.btnOpen.Text = "...";
         this.btnOpen.UseVisualStyleBackColor = true;
         this.btnOpen.Click += new System.EventHandler(this.btnOpen_Click);
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(100, 15);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(310, 20);
         this.tbPath.TabIndex = 11;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(12, 21);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(81, 13);
         this.label2.TabIndex = 10;
         this.label2.Text = "Путь до файла";
         // 
         // openFileDialog1
         // 
         this.openFileDialog1.FileName = "openFileDialog1";
         this.openFileDialog1.Filter = "Excel files|*.xls;*.xlsx";
         // 
         // FmPriceLoad
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(507, 128);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.progressBar1);
         this.Controls.Add(this.btnOpen);
         this.Controls.Add(this.tbPath);
         this.Controls.Add(this.label2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceLoad";
         this.Text = "Загрузка товара";
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button bntLoad;
        private System.Windows.Forms.ProgressBar progressBar1;
        private System.Windows.Forms.Button btnOpen;
        private System.Windows.Forms.TextBox tbPath;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.OpenFileDialog openFileDialog1;
    }
}
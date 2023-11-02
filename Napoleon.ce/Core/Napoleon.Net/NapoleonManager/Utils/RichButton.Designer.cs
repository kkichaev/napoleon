namespace GRSoft.NapoleonManager.Utils
{
   partial class RichButton
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.pictureBox1 = new System.Windows.Forms.PictureBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // pictureBox1
         // 
         this.pictureBox1.Image = global::GRSoft.NapoleonManager.Properties.Resources.result_report;
         this.pictureBox1.Location = new System.Drawing.Point(3, 0);
         this.pictureBox1.Name = "pictureBox1";
         this.pictureBox1.Size = new System.Drawing.Size(28, 28);
         this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
         this.pictureBox1.TabIndex = 0;
         this.pictureBox1.TabStop = false;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label1.Location = new System.Drawing.Point(38, 4);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(35, 13);
         this.label1.TabIndex = 1;
         this.label1.Text = "label1";
         // 
         // label2
         // 
         this.label2.Anchor = System.Windows.Forms.AnchorStyles.None;
         this.label2.AutoSize = true;
         this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label2.ForeColor = System.Drawing.SystemColors.ButtonShadow;
         this.label2.Location = new System.Drawing.Point(2, 4);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(35, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "label2";
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.pictureBox1);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(1, 1);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(238, 64);
         this.panel1.TabIndex = 3;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.label2);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(1, 45);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(238, 20);
         this.panel2.TabIndex = 4;
         // 
         // RichButton
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Name = "RichButton";
         this.Padding = new System.Windows.Forms.Padding(1);
         this.Size = new System.Drawing.Size(240, 66);
         ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.PictureBox pictureBox1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
   }
}

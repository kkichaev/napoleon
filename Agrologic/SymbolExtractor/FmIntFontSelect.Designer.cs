
namespace SymbolsExtractor
{
   partial class FmIntFontSelect
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
         this.button1 = new System.Windows.Forms.Button();
         this.button2 = new System.Windows.Forms.Button();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.cbFonts = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.numericUpDown1 = new System.Windows.Forms.NumericUpDown();
         this.button3 = new System.Windows.Forms.Button();
         this.label5 = new System.Windows.Forms.Label();
         ((System.ComponentModel.ISupportInitialize)(this.numericUpDown1)).BeginInit();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(46, 31);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(46, 17);
         this.label1.TabIndex = 0;
         this.label1.Text = "label1";
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(49, 69);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(164, 39);
         this.button1.TabIndex = 1;
         this.button1.Text = "Open languages.txt";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // button2
         // 
         this.button2.Location = new System.Drawing.Point(49, 125);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(164, 39);
         this.button2.TabIndex = 2;
         this.button2.Text = "Select help folder";
         this.button2.UseVisualStyleBackColor = true;
         this.button2.Click += new System.EventHandler(this.button2_Click);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(49, 193);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(36, 17);
         this.label2.TabIndex = 3;
         this.label2.Text = "Font";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(49, 232);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(67, 17);
         this.label3.TabIndex = 4;
         this.label3.Text = "Font Size";
         // 
         // cbFonts
         // 
         this.cbFonts.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFonts.FormattingEnabled = true;
         this.cbFonts.Location = new System.Drawing.Point(132, 190);
         this.cbFonts.Name = "cbFonts";
         this.cbFonts.Size = new System.Drawing.Size(516, 24);
         this.cbFonts.Sorted = true;
         this.cbFonts.TabIndex = 5;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(228, 136);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(94, 17);
         this.label4.TabIndex = 6;
         this.label4.Text = "<helpl folder>";
         // 
         // numericUpDown1
         // 
         this.numericUpDown1.Location = new System.Drawing.Point(132, 230);
         this.numericUpDown1.Name = "numericUpDown1";
         this.numericUpDown1.Size = new System.Drawing.Size(98, 22);
         this.numericUpDown1.TabIndex = 7;
         this.numericUpDown1.Value = new decimal(new int[] {
            15,
            0,
            0,
            0});
         // 
         // button3
         // 
         this.button3.Location = new System.Drawing.Point(264, 306);
         this.button3.Name = "button3";
         this.button3.Size = new System.Drawing.Size(163, 38);
         this.button3.TabIndex = 8;
         this.button3.Text = "Generate";
         this.button3.UseVisualStyleBackColor = true;
         this.button3.Click += new System.EventHandler(this.button3_Click);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(228, 80);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(0, 17);
         this.label5.TabIndex = 9;
         // 
         // FmIntFontSelect
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(708, 401);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.button3);
         this.Controls.Add(this.numericUpDown1);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.cbFonts);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.button2);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.label1);
         this.Name = "FmIntFontSelect";
         this.Text = "Internal font select";
         ((System.ComponentModel.ISupportInitialize)(this.numericUpDown1)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbFonts;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.NumericUpDown numericUpDown1;
      private System.Windows.Forms.Button button3;
      private System.Windows.Forms.Label label5;
   }
}
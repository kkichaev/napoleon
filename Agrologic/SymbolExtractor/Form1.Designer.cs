namespace SymbolsExtractor
{
   partial class Form1
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
         this.button1 = new System.Windows.Forms.Button();
         this.lbInputFile = new System.Windows.Forms.Label();
         this.button2 = new System.Windows.Forms.Button();
         this.lbFontFolder = new System.Windows.Forms.Label();
         this.dgvFonts = new System.Windows.Forms.DataGridView();
         this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.Column3 = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnFIleName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.button3 = new System.Windows.Forms.Button();
         this.lbOutFolder = new System.Windows.Forms.Label();
         this.cbFormat = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.cbCompress = new System.Windows.Forms.CheckBox();
         this.tbFlags = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.cbMakeBat = new System.Windows.Forms.CheckBox();
         this.button4 = new System.Windows.Forms.Button();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.tbPrefix = new System.Windows.Forms.TextBox();
         this.tbSuffix = new System.Windows.Forms.TextBox();
         this.lbProgName = new System.Windows.Forms.Label();
         this.button5 = new System.Windows.Forms.Button();
         this.label5 = new System.Windows.Forms.Label();
         this.nmBPP = new System.Windows.Forms.NumericUpDown();
         this.label6 = new System.Windows.Forms.Label();
         this.tbCoef = new System.Windows.Forms.TextBox();
         this.button6 = new System.Windows.Forms.Button();
         this.button7 = new System.Windows.Forms.Button();
         ((System.ComponentModel.ISupportInitialize)(this.dgvFonts)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.nmBPP)).BeginInit();
         this.SuspendLayout();
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(36, 33);
         this.button1.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(165, 38);
         this.button1.TabIndex = 0;
         this.button1.Text = "Open source file";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // lbInputFile
         // 
         this.lbInputFile.AutoSize = true;
         this.lbInputFile.Location = new System.Drawing.Point(212, 44);
         this.lbInputFile.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.lbInputFile.Name = "lbInputFile";
         this.lbInputFile.Size = new System.Drawing.Size(128, 17);
         this.lbInputFile.TabIndex = 1;
         this.lbInputFile.Text = "<source file name>";
         // 
         // button2
         // 
         this.button2.Location = new System.Drawing.Point(36, 86);
         this.button2.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(165, 38);
         this.button2.TabIndex = 2;
         this.button2.Text = "Font folder";
         this.button2.UseVisualStyleBackColor = true;
         this.button2.Click += new System.EventHandler(this.button2_Click);
         // 
         // lbFontFolder
         // 
         this.lbFontFolder.AutoSize = true;
         this.lbFontFolder.Location = new System.Drawing.Point(212, 97);
         this.lbFontFolder.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.lbFontFolder.Name = "lbFontFolder";
         this.lbFontFolder.Size = new System.Drawing.Size(88, 17);
         this.lbFontFolder.TabIndex = 3;
         this.lbFontFolder.Text = "<font folder>";
         // 
         // dgvFonts
         // 
         this.dgvFonts.AllowUserToAddRows = false;
         this.dgvFonts.AllowUserToDeleteRows = false;
         this.dgvFonts.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvFonts.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvFonts.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Column1,
            this.Column3,
            this.clmnFIleName});
         this.dgvFonts.Location = new System.Drawing.Point(36, 458);
         this.dgvFonts.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.dgvFonts.Name = "dgvFonts";
         this.dgvFonts.RowHeadersVisible = false;
         this.dgvFonts.RowHeadersWidth = 51;
         this.dgvFonts.Size = new System.Drawing.Size(1024, 246);
         this.dgvFonts.TabIndex = 4;
         this.dgvFonts.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvFonts_CellDoubleClick);
         // 
         // Column1
         // 
         this.Column1.DataPropertyName = "Font";
         this.Column1.FillWeight = 200F;
         this.Column1.HeaderText = "Font";
         this.Column1.MinimumWidth = 6;
         this.Column1.Name = "Column1";
         this.Column1.ReadOnly = true;
         this.Column1.Width = 200;
         // 
         // Column3
         // 
         this.Column3.DataPropertyName = "SymbolCount";
         this.Column3.HeaderText = "Symbols";
         this.Column3.MinimumWidth = 6;
         this.Column3.Name = "Column3";
         this.Column3.Width = 125;
         // 
         // clmnFIleName
         // 
         this.clmnFIleName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnFIleName.DataPropertyName = "FileName";
         this.clmnFIleName.HeaderText = "File name";
         this.clmnFIleName.MinimumWidth = 6;
         this.clmnFIleName.Name = "clmnFIleName";
         this.clmnFIleName.ReadOnly = true;
         // 
         // button3
         // 
         this.button3.Location = new System.Drawing.Point(36, 144);
         this.button3.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button3.Name = "button3";
         this.button3.Size = new System.Drawing.Size(165, 38);
         this.button3.TabIndex = 5;
         this.button3.Text = "Output folder";
         this.button3.UseVisualStyleBackColor = true;
         this.button3.Click += new System.EventHandler(this.button3_Click);
         // 
         // lbOutFolder
         // 
         this.lbOutFolder.AutoSize = true;
         this.lbOutFolder.Location = new System.Drawing.Point(212, 155);
         this.lbOutFolder.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.lbOutFolder.Name = "lbOutFolder";
         this.lbOutFolder.Size = new System.Drawing.Size(84, 17);
         this.lbOutFolder.TabIndex = 6;
         this.lbOutFolder.Text = "<out folder>";
         // 
         // cbFormat
         // 
         this.cbFormat.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbFormat.FormattingEnabled = true;
         this.cbFormat.Items.AddRange(new object[] {
            "dump",
            "bin",
            "lvgl"});
         this.cbFormat.Location = new System.Drawing.Point(827, 46);
         this.cbFormat.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.cbFormat.Name = "cbFormat";
         this.cbFormat.Size = new System.Drawing.Size(219, 24);
         this.cbFormat.TabIndex = 7;
         // 
         // label1
         // 
         this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(715, 49);
         this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(95, 17);
         this.label1.TabIndex = 8;
         this.label1.Text = "Output format";
         // 
         // cbCompress
         // 
         this.cbCompress.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbCompress.AutoSize = true;
         this.cbCompress.Location = new System.Drawing.Point(831, 81);
         this.cbCompress.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.cbCompress.Name = "cbCompress";
         this.cbCompress.Size = new System.Drawing.Size(91, 21);
         this.cbCompress.TabIndex = 9;
         this.cbCompress.Text = "compress";
         this.cbCompress.UseVisualStyleBackColor = true;
         // 
         // tbFlags
         // 
         this.tbFlags.Location = new System.Drawing.Point(36, 358);
         this.tbFlags.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.tbFlags.Multiline = true;
         this.tbFlags.Name = "tbFlags";
         this.tbFlags.Size = new System.Drawing.Size(1023, 74);
         this.tbFlags.TabIndex = 10;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(32, 334);
         this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(108, 17);
         this.label2.TabIndex = 11;
         this.label2.Text = "Additional flags:";
         // 
         // cbMakeBat
         // 
         this.cbMakeBat.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.cbMakeBat.AutoSize = true;
         this.cbMakeBat.Location = new System.Drawing.Point(834, 110);
         this.cbMakeBat.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.cbMakeBat.Name = "cbMakeBat";
         this.cbMakeBat.Size = new System.Drawing.Size(128, 21);
         this.cbMakeBat.TabIndex = 12;
         this.cbMakeBat.Text = "Make .BAT files";
         this.cbMakeBat.UseVisualStyleBackColor = true;
         // 
         // button4
         // 
         this.button4.Location = new System.Drawing.Point(303, 312);
         this.button4.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button4.Name = "button4";
         this.button4.Size = new System.Drawing.Size(165, 38);
         this.button4.TabIndex = 13;
         this.button4.Text = "Run!";
         this.button4.UseVisualStyleBackColor = true;
         this.button4.Click += new System.EventHandler(this.button4_Click);
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(736, 144);
         this.label3.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(73, 17);
         this.label3.TabIndex = 14;
         this.label3.Text = "Out preffix";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(716, 182);
         this.label4.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(92, 17);
         this.label4.TabIndex = 15;
         this.label4.Text = "Out extention";
         // 
         // tbPrefix
         // 
         this.tbPrefix.Location = new System.Drawing.Point(828, 140);
         this.tbPrefix.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.tbPrefix.Name = "tbPrefix";
         this.tbPrefix.Size = new System.Drawing.Size(132, 22);
         this.tbPrefix.TabIndex = 16;
         // 
         // tbSuffix
         // 
         this.tbSuffix.Location = new System.Drawing.Point(827, 178);
         this.tbSuffix.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.tbSuffix.Name = "tbSuffix";
         this.tbSuffix.Size = new System.Drawing.Size(132, 22);
         this.tbSuffix.TabIndex = 17;
         // 
         // lbProgName
         // 
         this.lbProgName.AutoSize = true;
         this.lbProgName.Location = new System.Drawing.Point(212, 212);
         this.lbProgName.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.lbProgName.Name = "lbProgName";
         this.lbProgName.Size = new System.Drawing.Size(104, 17);
         this.lbProgName.TabIndex = 19;
         this.lbProgName.Text = "<lv_font_conv>";
         // 
         // button5
         // 
         this.button5.Location = new System.Drawing.Point(36, 201);
         this.button5.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button5.Name = "button5";
         this.button5.Size = new System.Drawing.Size(165, 38);
         this.button5.TabIndex = 18;
         this.button5.Text = "Font conv program";
         this.button5.UseVisualStyleBackColor = true;
         this.button5.Click += new System.EventHandler(this.button5_Click);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(776, 222);
         this.label5.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(32, 17);
         this.label5.TabIndex = 20;
         this.label5.Text = "bpp";
         // 
         // nmBPP
         // 
         this.nmBPP.Location = new System.Drawing.Point(827, 217);
         this.nmBPP.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.nmBPP.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.nmBPP.Name = "nmBPP";
         this.nmBPP.Size = new System.Drawing.Size(67, 22);
         this.nmBPP.TabIndex = 21;
         this.nmBPP.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(740, 258);
         this.label6.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(67, 17);
         this.label6.TabIndex = 22;
         this.label6.Text = "Font coef";
         // 
         // tbCoef
         // 
         this.tbCoef.Location = new System.Drawing.Point(828, 255);
         this.tbCoef.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.tbCoef.Name = "tbCoef";
         this.tbCoef.Size = new System.Drawing.Size(107, 22);
         this.tbCoef.TabIndex = 23;
         // 
         // button6
         // 
         this.button6.Location = new System.Drawing.Point(36, 257);
         this.button6.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.button6.Name = "button6";
         this.button6.Size = new System.Drawing.Size(165, 52);
         this.button6.TabIndex = 24;
         this.button6.Text = "Additional fonts and symbols";
         this.button6.UseVisualStyleBackColor = true;
         this.button6.Click += new System.EventHandler(this.button6_Click);
         // 
         // button7
         // 
         this.button7.Location = new System.Drawing.Point(508, 312);
         this.button7.Margin = new System.Windows.Forms.Padding(4);
         this.button7.Name = "button7";
         this.button7.Size = new System.Drawing.Size(165, 38);
         this.button7.TabIndex = 25;
         this.button7.Text = "Internal font select";
         this.button7.UseVisualStyleBackColor = true;
         this.button7.Click += new System.EventHandler(this.button7_Click);
         // 
         // Form1
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1099, 737);
         this.Controls.Add(this.button7);
         this.Controls.Add(this.button6);
         this.Controls.Add(this.tbCoef);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.nmBPP);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.lbProgName);
         this.Controls.Add(this.button5);
         this.Controls.Add(this.tbSuffix);
         this.Controls.Add(this.tbPrefix);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.button4);
         this.Controls.Add(this.cbMakeBat);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbFlags);
         this.Controls.Add(this.cbCompress);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbFormat);
         this.Controls.Add(this.lbOutFolder);
         this.Controls.Add(this.button3);
         this.Controls.Add(this.dgvFonts);
         this.Controls.Add(this.lbFontFolder);
         this.Controls.Add(this.button2);
         this.Controls.Add(this.lbInputFile);
         this.Controls.Add(this.button1);
         this.Margin = new System.Windows.Forms.Padding(4, 4, 4, 4);
         this.Name = "Form1";
         this.Text = "Form1";
         ((System.ComponentModel.ISupportInitialize)(this.dgvFonts)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.nmBPP)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Label lbInputFile;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.Label lbFontFolder;
      private System.Windows.Forms.DataGridView dgvFonts;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
      private System.Windows.Forms.DataGridViewTextBoxColumn Column3;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnFIleName;
      private System.Windows.Forms.Button button3;
      private System.Windows.Forms.Label lbOutFolder;
      private System.Windows.Forms.ComboBox cbFormat;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.CheckBox cbCompress;
      private System.Windows.Forms.TextBox tbFlags;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.CheckBox cbMakeBat;
      private System.Windows.Forms.Button button4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbPrefix;
      private System.Windows.Forms.TextBox tbSuffix;
      private System.Windows.Forms.Label lbProgName;
      private System.Windows.Forms.Button button5;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.NumericUpDown nmBPP;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbCoef;
      private System.Windows.Forms.Button button6;
      private System.Windows.Forms.Button button7;
   }
}


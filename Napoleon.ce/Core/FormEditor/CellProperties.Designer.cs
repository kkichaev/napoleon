namespace NFormEditor
{
   partial class CellProperties
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
         this.ok = new System.Windows.Forms.Button();
         this.cancel = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.colSpan = new System.Windows.Forms.NumericUpDown();
         this.rowSpan = new System.Windows.Forms.NumericUpDown();
         this.text = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.align = new System.Windows.Forms.ComboBox();
         this.borders = new System.Windows.Forms.CheckedListBox();
         this.label4 = new System.Windows.Forms.Label();
         ((System.ComponentModel.ISupportInitialize)(this.colSpan)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.rowSpan)).BeginInit();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(13, 254);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 4;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(217, 254);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 5;
         this.cancel.Text = "Cancel";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(22, 14);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(47, 13);
         this.label1.TabIndex = 3;
         this.label1.Text = "ColSpan";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(15, 41);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(54, 13);
         this.label2.TabIndex = 5;
         this.label2.Text = "RowSpan";
         // 
         // colSpan
         // 
         this.colSpan.Location = new System.Drawing.Point(75, 12);
         this.colSpan.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.colSpan.Name = "colSpan";
         this.colSpan.Size = new System.Drawing.Size(54, 20);
         this.colSpan.TabIndex = 1;
         this.colSpan.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         // 
         // rowSpan
         // 
         this.rowSpan.Location = new System.Drawing.Point(75, 39);
         this.rowSpan.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
         this.rowSpan.Name = "rowSpan";
         this.rowSpan.Size = new System.Drawing.Size(54, 20);
         this.rowSpan.TabIndex = 3;
         this.rowSpan.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
         // 
         // text
         // 
         this.text.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                     | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.text.Location = new System.Drawing.Point(12, 105);
         this.text.Multiline = true;
         this.text.Name = "text";
         this.text.Size = new System.Drawing.Size(280, 143);
         this.text.TabIndex = 0;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(136, 18);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(30, 13);
         this.label3.TabIndex = 6;
         this.label3.Text = "Align";
         // 
         // align
         // 
         this.align.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.align.FormattingEnabled = true;
         this.align.Items.AddRange(new object[] {
            "TopLeft",
            "TopCenter",
            "TopRight",
            "MiddleLeft",
            "MiddleCenter",
            "MiddleRight",
            "BottomLeft",
            "BottomCenter",
            "BottomRight"});
         this.align.Location = new System.Drawing.Point(173, 14);
         this.align.Name = "align";
         this.align.Size = new System.Drawing.Size(119, 21);
         this.align.TabIndex = 2;
         // 
         // borders
         // 
         this.borders.CheckOnClick = true;
         this.borders.ColumnWidth = 100;
         this.borders.FormattingEnabled = true;
         this.borders.Items.AddRange(new object[] {
            "Left",
            "Right",
            "Top",
            "Bottom"});
         this.borders.Location = new System.Drawing.Point(75, 65);
         this.borders.MultiColumn = true;
         this.borders.Name = "borders";
         this.borders.Size = new System.Drawing.Size(217, 34);
         this.borders.TabIndex = 7;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(22, 65);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(43, 13);
         this.label4.TabIndex = 8;
         this.label4.Text = "Borders";
         // 
         // CellProperties
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.ok;
         this.ClientSize = new System.Drawing.Size(304, 289);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.borders);
         this.Controls.Add(this.align);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.text);
         this.Controls.Add(this.rowSpan);
         this.Controls.Add(this.colSpan);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Name = "CellProperties";
         this.Text = "Cell";
         ((System.ComponentModel.ISupportInitialize)(this.colSpan)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.rowSpan)).EndInit();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.NumericUpDown colSpan;
      private System.Windows.Forms.NumericUpDown rowSpan;
      private System.Windows.Forms.TextBox text;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox align;
      private System.Windows.Forms.CheckedListBox borders;
      private System.Windows.Forms.Label label4;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmPriceSetting
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPriceSetting));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.panel2 = new System.Windows.Forms.Panel();
         this.gpPicSize = new System.Windows.Forms.GroupBox();
         this.upSizeY = new System.Windows.Forms.NumericUpDown();
         this.upSizeX = new System.Windows.Forms.NumericUpDown();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.gpPicSize.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.upSizeY)).BeginInit();
         ((System.ComponentModel.ISupportInitialize)(this.upSizeX)).BeginInit();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 115);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(223, 44);
         this.panel1.TabIndex = 0;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(48, 11);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(139, 12);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.gpPicSize);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(223, 115);
         this.panel2.TabIndex = 1;
         // 
         // gpPicSize
         // 
         this.gpPicSize.Controls.Add(this.upSizeY);
         this.gpPicSize.Controls.Add(this.upSizeX);
         this.gpPicSize.Controls.Add(this.label2);
         this.gpPicSize.Controls.Add(this.label1);
         this.gpPicSize.Location = new System.Drawing.Point(13, 13);
         this.gpPicSize.Name = "gpPicSize";
         this.gpPicSize.Size = new System.Drawing.Size(155, 92);
         this.gpPicSize.TabIndex = 0;
         this.gpPicSize.TabStop = false;
         this.gpPicSize.Text = "Размер миниатюр";
         // 
         // upSizeY
         // 
         this.upSizeY.Increment = new decimal(new int[] {
            5,
            0,
            0,
            0});
         this.upSizeY.Location = new System.Drawing.Point(42, 45);
         this.upSizeY.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
         this.upSizeY.Name = "upSizeY";
         this.upSizeY.Size = new System.Drawing.Size(81, 20);
         this.upSizeY.TabIndex = 3;
         this.upSizeY.Value = new decimal(new int[] {
            115,
            0,
            0,
            0});
         // 
         // upSizeX
         // 
         this.upSizeX.Increment = new decimal(new int[] {
            5,
            0,
            0,
            0});
         this.upSizeX.Location = new System.Drawing.Point(42, 19);
         this.upSizeX.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
         this.upSizeX.Name = "upSizeX";
         this.upSizeX.Size = new System.Drawing.Size(81, 20);
         this.upSizeX.TabIndex = 2;
         this.upSizeX.Value = new decimal(new int[] {
            115,
            0,
            0,
            0});
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 48);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(15, 14);
         this.label2.TabIndex = 1;
         this.label2.Text = "Y";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 22);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(14, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "X";
         // 
         // FmPriceSetting
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(223, 159);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPriceSetting";
         this.Text = "Настройка";
         this.Load += new System.EventHandler(this.FmPriceSetting_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmPriceSetting_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.gpPicSize.ResumeLayout(false);
         this.gpPicSize.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.upSizeY)).EndInit();
         ((System.ComponentModel.ISupportInitialize)(this.upSizeX)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.GroupBox gpPicSize;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.NumericUpDown upSizeX;
      private System.Windows.Forms.NumericUpDown upSizeY;
   }
}
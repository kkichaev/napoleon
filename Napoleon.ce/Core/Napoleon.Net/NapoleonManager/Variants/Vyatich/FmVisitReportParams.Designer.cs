namespace GRSoft.NapoleonManager
{
   partial class FmVisitReportParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmVisitReportParams));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.rbOrg = new System.Windows.Forms.RadioButton();
         this.rbCreated = new System.Windows.Forms.RadioButton();
         this.panel1 = new System.Windows.Forms.Panel();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.precision = new System.Windows.Forms.NumericUpDown();
         this.groupBox1.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.precision)).BeginInit();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.rbOrg);
         this.groupBox1.Controls.Add(this.rbCreated);
         this.groupBox1.Location = new System.Drawing.Point(12, 47);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(196, 67);
         this.groupBox1.TabIndex = 4;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "сортировать по";
         // 
         // rbOrg
         // 
         this.rbOrg.AutoSize = true;
         this.rbOrg.Location = new System.Drawing.Point(6, 43);
         this.rbOrg.Name = "rbOrg";
         this.rbOrg.Size = new System.Drawing.Size(89, 18);
         this.rbOrg.TabIndex = 1;
         this.rbOrg.Text = "имя клиента";
         this.rbOrg.UseVisualStyleBackColor = true;
         // 
         // rbCreated
         // 
         this.rbCreated.AutoSize = true;
         this.rbCreated.Checked = true;
         this.rbCreated.Location = new System.Drawing.Point(6, 19);
         this.rbCreated.Name = "rbCreated";
         this.rbCreated.Size = new System.Drawing.Size(117, 18);
         this.rbCreated.TabIndex = 0;
         this.rbCreated.TabStop = true;
         this.rbCreated.Text = "время посещения";
         this.rbCreated.UseVisualStyleBackColor = true;
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.precision);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Controls.Add(this.groupBox1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(302, 156);
         this.panel1.TabIndex = 5;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.btnOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 120);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(302, 36);
         this.panel2.TabIndex = 6;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(137, 7);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(218, 7);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 0;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(14, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(58, 14);
         this.label1.TabIndex = 5;
         this.label1.Text = "точность:";
         // 
         // precision
         // 
         this.precision.Increment = new decimal(new int[] {
            100,
            0,
            0,
            0});
         this.precision.Location = new System.Drawing.Point(78, 10);
         this.precision.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
         this.precision.Name = "precision";
         this.precision.Size = new System.Drawing.Size(57, 20);
         this.precision.TabIndex = 6;
         this.precision.Value = new decimal(new int[] {
            300,
            0,
            0,
            0});
         // 
         // FmVisitReportParams
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(302, 156);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmVisitReportParams";
         this.Text = "Отчет по работе торговых представителей";
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.precision)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbOrg;
      private System.Windows.Forms.RadioButton rbCreated;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.NumericUpDown precision;
   }
}
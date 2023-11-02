namespace GRSoft.NapoleonManager
{
   partial class FmEditAgentLimit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmEditAgentLimit));
         this.dpvDates = new GRSoft.NapoleonManager.DatePeriodView();
         this.tbLimitWeight = new System.Windows.Forms.TextBox();
         this.tbLimitSum = new System.Windows.Forms.TextBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.rbLimitSum = new System.Windows.Forms.RadioButton();
         this.rbLimitWeight = new System.Windows.Forms.RadioButton();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.cbPriceType = new System.Windows.Forms.ComboBox();
         this.cbCanOverLimit = new System.Windows.Forms.CheckBox();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dpvDates
         // 
         this.dpvDates.Finish = new System.DateTime(2016, 8, 17, 0, 0, 0, 0);
         this.dpvDates.Location = new System.Drawing.Point(22, 23);
         this.dpvDates.Name = "dpvDates";
         this.dpvDates.Size = new System.Drawing.Size(367, 27);
         this.dpvDates.Start = new System.DateTime(2016, 8, 17, 0, 0, 0, 0);
         this.dpvDates.TabIndex = 1;
         // 
         // tbLimitWeight
         // 
         this.tbLimitWeight.Location = new System.Drawing.Point(66, 30);
         this.tbLimitWeight.Name = "tbLimitWeight";
         this.tbLimitWeight.Size = new System.Drawing.Size(100, 20);
         this.tbLimitWeight.TabIndex = 2;
         // 
         // tbLimitSum
         // 
         this.tbLimitSum.Location = new System.Drawing.Point(66, 60);
         this.tbLimitSum.Name = "tbLimitSum";
         this.tbLimitSum.Size = new System.Drawing.Size(100, 20);
         this.tbLimitSum.TabIndex = 3;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.rbLimitSum);
         this.groupBox1.Controls.Add(this.rbLimitWeight);
         this.groupBox1.Controls.Add(this.tbLimitWeight);
         this.groupBox1.Controls.Add(this.tbLimitSum);
         this.groupBox1.Location = new System.Drawing.Point(28, 121);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(361, 104);
         this.groupBox1.TabIndex = 6;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Лимит";
         // 
         // rbLimitSum
         // 
         this.rbLimitSum.AutoSize = true;
         this.rbLimitSum.Location = new System.Drawing.Point(175, 62);
         this.rbLimitSum.Name = "rbLimitSum";
         this.rbLimitSum.Size = new System.Drawing.Size(68, 17);
         this.rbLimitSum.TabIndex = 7;
         this.rbLimitSum.TabStop = true;
         this.rbLimitSum.Text = "в рублях";
         this.rbLimitSum.UseVisualStyleBackColor = true;
         this.rbLimitSum.CheckedChanged += new System.EventHandler(this.rbLimitSum_CheckedChanged);
         // 
         // rbLimitWeight
         // 
         this.rbLimitWeight.AutoSize = true;
         this.rbLimitWeight.Location = new System.Drawing.Point(175, 31);
         this.rbLimitWeight.Name = "rbLimitWeight";
         this.rbLimitWeight.Size = new System.Drawing.Size(102, 17);
         this.rbLimitWeight.TabIndex = 6;
         this.rbLimitWeight.TabStop = true;
         this.rbLimitWeight.Text = "в килограммах";
         this.rbLimitWeight.UseVisualStyleBackColor = true;
         this.rbLimitWeight.CheckedChanged += new System.EventHandler(this.rbLimitWeight_CheckedChanged);
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(28, 292);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 7;
         this.btnCancel.Text = "Отмена";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(314, 292);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 8;
         this.btnOK.Text = "Сохранить";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(28, 65);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(82, 13);
         this.label1.TabIndex = 9;
         this.label1.Text = "Тип продукции";
         // 
         // cbPriceType
         // 
         this.cbPriceType.FormattingEnabled = true;
         this.cbPriceType.Location = new System.Drawing.Point(31, 85);
         this.cbPriceType.Name = "cbPriceType";
         this.cbPriceType.Size = new System.Drawing.Size(358, 21);
         this.cbPriceType.TabIndex = 10;
         // 
         // cbCanOverLimit
         // 
         this.cbCanOverLimit.AutoSize = true;
         this.cbCanOverLimit.Location = new System.Drawing.Point(28, 240);
         this.cbCanOverLimit.Name = "cbCanOverLimit";
         this.cbCanOverLimit.Size = new System.Drawing.Size(155, 17);
         this.cbCanOverLimit.TabIndex = 11;
         this.cbCanOverLimit.Text = "Можно превышать лимит";
         this.cbCanOverLimit.UseVisualStyleBackColor = true;
         // 
         // FmEditAgentLimit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(407, 327);
         this.Controls.Add(this.cbCanOverLimit);
         this.Controls.Add(this.cbPriceType);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.dpvDates);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmEditAgentLimit";
         this.Text = "Редактирование лимита";
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpvDates;
      private System.Windows.Forms.TextBox tbLimitWeight;
      private System.Windows.Forms.TextBox tbLimitSum;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton rbLimitSum;
      private System.Windows.Forms.RadioButton rbLimitWeight;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbPriceType;
      private System.Windows.Forms.CheckBox cbCanOverLimit;
   }
}
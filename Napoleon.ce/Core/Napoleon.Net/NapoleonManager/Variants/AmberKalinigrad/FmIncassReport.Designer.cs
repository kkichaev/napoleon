namespace GRSoft.NapoleonManager
{
   partial class FmIncassReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmIncassReport));
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.SuspendLayout();
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Checked = true;
         this.rbAgents.Location = new System.Drawing.Point(10, 63);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 34;
         this.rbAgents.TabStop = true;
         this.rbAgents.Tag = "1";
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.RadioButtonSelect);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(10, 36);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 33;
         this.rbDivision.Tag = "0";
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.RadioButtonSelect);
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(118, 62);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(178, 22);
         this.cbAgents.TabIndex = 32;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(93, 118);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 31;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(70, 92);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 14);
         this.label2.TabIndex = 30;
         this.label2.Text = "Дата с";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(118, 35);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(178, 22);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 29;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(118, 115);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(167, 20);
         this.dtpEnd.TabIndex = 28;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(118, 89);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 27;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(221, 211);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 24;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(140, 211);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 23;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Location = new System.Drawing.Point(10, 12);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 35;
         this.rbAll.TabStop = true;
         this.rbAll.Tag = "-1";
         this.rbAll.Text = "Все";
         this.rbAll.UseVisualStyleBackColor = true;
         // 
         // FmIncassReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(317, 246);
         this.Controls.Add(this.rbAll);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Margin = new System.Windows.Forms.Padding(2, 3, 2, 3);
         this.Name = "FmIncassReport";
         this.Text = "Отчет по инкассациям";
         this.Load += new System.EventHandler(this.FmIncassReport_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.RadioButton rbAll;
   }
}
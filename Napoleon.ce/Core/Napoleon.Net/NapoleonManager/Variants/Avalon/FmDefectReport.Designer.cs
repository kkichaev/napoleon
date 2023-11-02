namespace GRSoft.NapoleonManager
{
   partial class FmDefectReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDefectReport));
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
         this.cbPics = new System.Windows.Forms.CheckBox();
         this.SuspendLayout();
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Location = new System.Drawing.Point(7, 38);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(55, 18);
         this.rbAgents.TabIndex = 34;
         this.rbAgents.Tag = "cbAgents";
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.rb_CheckedChanged);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Checked = true;
         this.rbDivision.Location = new System.Drawing.Point(7, 11);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(103, 18);
         this.rbDivision.TabIndex = 33;
         this.rbDivision.TabStop = true;
         this.rbDivision.Tag = "cbDivisions";
         this.rbDivision.Text = "Подразделение";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rb_CheckedChanged);
         // 
         // cbAgents
         // 
         this.cbAgents.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbAgents.Enabled = false;
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(117, 33);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(182, 22);
         this.cbAgents.TabIndex = 32;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(90, 93);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 14);
         this.label3.TabIndex = 31;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(67, 67);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 14);
         this.label2.TabIndex = 30;
         this.label2.Text = "Дата с";
         // 
         // cbDivisions
         // 
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(117, 6);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(182, 22);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 29;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(115, 90);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(184, 20);
         this.dtpEnd.TabIndex = 28;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(115, 64);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(184, 20);
         this.dtpBegin.TabIndex = 27;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // cancel
         // 
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(226, 140);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 24;
         this.cancel.Text = "Отмена";
         this.cancel.UseVisualStyleBackColor = true;
         // 
         // ok
         // 
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(145, 140);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 23;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // cbPics
         // 
         this.cbPics.AutoSize = true;
         this.cbPics.Checked = true;
         this.cbPics.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbPics.Location = new System.Drawing.Point(7, 115);
         this.cbPics.Name = "cbPics";
         this.cbPics.Size = new System.Drawing.Size(88, 18);
         this.cbPics.TabIndex = 35;
         this.cbPics.Text = "Фотографии";
         this.cbPics.UseVisualStyleBackColor = true;
         // 
         // FmDefectReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(341, 182);
         this.Controls.Add(this.cbPics);
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
         this.Name = "FmDefectReport";
         this.Text = "Рапорт о неисправностях";
         this.Load += new System.EventHandler(this.FmDefectReport_Load);
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
      private System.Windows.Forms.CheckBox cbPics;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmDaliyPlanReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDaliyPlanReport));
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Location = new System.Drawing.Point(30, 29);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 17);
         this.rbAll.TabIndex = 42;
         this.rbAll.TabStop = true;
         this.rbAll.Tag = "-1";
         this.rbAll.Text = "Все";
         this.rbAll.UseVisualStyleBackColor = true;
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Checked = true;
         this.rbAgents.Location = new System.Drawing.Point(30, 80);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(54, 17);
         this.rbAgents.TabIndex = 41;
         this.rbAgents.TabStop = true;
         this.rbAgents.Tag = "1";
         this.rbAgents.Text = "Агент";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.RadioButtonSelect);
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(30, 53);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(105, 17);
         this.rbDivision.TabIndex = 40;
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
         this.cbAgents.Location = new System.Drawing.Point(138, 79);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(252, 21);
         this.cbAgents.Sorted = true;
         this.cbAgents.TabIndex = 39;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(90, 109);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 13);
         this.label2.TabIndex = 38;
         this.label2.Text = "Дата с";
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(138, 52);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(252, 21);
         this.cbDivisions.Sorted = true;
         this.cbDivisions.TabIndex = 37;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(138, 106);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(167, 20);
         this.dtpBegin.TabIndex = 36;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(172, 163);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 43;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmDaliyPlanReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(429, 208);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.rbAll);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.dtpBegin);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDaliyPlanReport";
         this.Text = "Отчет по плану на день";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.RadioButton rbAll;
      private System.Windows.Forms.RadioButton rbAgents;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.ComboBox cbDivisions;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Button button1;
   }
}
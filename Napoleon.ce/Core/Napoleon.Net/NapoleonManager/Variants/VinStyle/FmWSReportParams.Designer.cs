namespace GRSoft.NapoleonManager
{
   partial class FmWSReportParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmWSReportParams));
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.button1 = new System.Windows.Forms.Button();
         this.rbDivisions = new System.Windows.Forms.RadioButton();
         this.rbAgents = new System.Windows.Forms.RadioButton();
         this.cbDivisions = new System.Windows.Forms.ComboBox();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.SuspendLayout();
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2018, 7, 25, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(27, 21);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2018, 7, 25, 0, 0, 0, 0);
         this.dpv.TabIndex = 0;
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button1.Location = new System.Drawing.Point(169, 193);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 1;
         this.button1.Text = "Excel";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // rbDivisions
         // 
         this.rbDivisions.AutoSize = true;
         this.rbDivisions.Location = new System.Drawing.Point(33, 84);
         this.rbDivisions.Name = "rbDivisions";
         this.rbDivisions.Size = new System.Drawing.Size(105, 17);
         this.rbDivisions.TabIndex = 2;
         this.rbDivisions.Text = "Подразделения";
         this.rbDivisions.UseVisualStyleBackColor = true;
         this.rbDivisions.CheckedChanged += new System.EventHandler(this.rbDivisions_CheckedChanged);
         // 
         // rbAgents
         // 
         this.rbAgents.AutoSize = true;
         this.rbAgents.Checked = true;
         this.rbAgents.Location = new System.Drawing.Point(33, 120);
         this.rbAgents.Name = "rbAgents";
         this.rbAgents.Size = new System.Drawing.Size(62, 17);
         this.rbAgents.TabIndex = 3;
         this.rbAgents.TabStop = true;
         this.rbAgents.Text = "Агенты";
         this.rbAgents.UseVisualStyleBackColor = true;
         this.rbAgents.CheckedChanged += new System.EventHandler(this.rbAgents_CheckedChanged);
         // 
         // cbDivisions
         // 
         this.cbDivisions.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivisions.Enabled = false;
         this.cbDivisions.FormattingEnabled = true;
         this.cbDivisions.Location = new System.Drawing.Point(144, 83);
         this.cbDivisions.Name = "cbDivisions";
         this.cbDivisions.Size = new System.Drawing.Size(250, 21);
         this.cbDivisions.TabIndex = 4;
         // 
         // cbAgents
         // 
         this.cbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(144, 119);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(250, 21);
         this.cbAgents.TabIndex = 5;
         // 
         // FmWSReportParams
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(425, 239);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.cbDivisions);
         this.Controls.Add(this.rbAgents);
         this.Controls.Add(this.rbDivisions);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.dpv);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmWSReportParams";
         this.Text = "Параметры отчета";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private DatePeriodView dpv;
      private System.Windows.Forms.Button button1;
      public System.Windows.Forms.RadioButton rbDivisions;
      public System.Windows.Forms.RadioButton rbAgents;
      public System.Windows.Forms.ComboBox cbDivisions;
      public System.Windows.Forms.ComboBox cbAgents;
   }
}
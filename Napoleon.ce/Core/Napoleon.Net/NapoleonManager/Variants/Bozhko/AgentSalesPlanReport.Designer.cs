namespace GRSoft.NapoleonManager
{
   partial class AgentSalesPlanReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AgentSalesPlanReport));
         this.btnReport = new System.Windows.Forms.Button();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.tbAgents = new System.Windows.Forms.TextBox();
         this.tbItems = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.btnAgents = new System.Windows.Forms.Button();
         this.btnItems = new System.Windows.Forms.Button();
         this.btnClearItems = new System.Windows.Forms.Button();
         this.btnClearAgents = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // btnReport
         // 
         this.btnReport.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.btnReport.Location = new System.Drawing.Point(217, 131);
         this.btnReport.Name = "btnReport";
         this.btnReport.Size = new System.Drawing.Size(75, 23);
         this.btnReport.TabIndex = 0;
         this.btnReport.Text = "Excel";
         this.btnReport.UseVisualStyleBackColor = true;
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(72, 12);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(141, 20);
         this.dtpStart.TabIndex = 1;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(72, 38);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(140, 20);
         this.dtpEnd.TabIndex = 2;
         // 
         // tbAgents
         // 
         this.tbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbAgents.Enabled = false;
         this.tbAgents.Location = new System.Drawing.Point(72, 67);
         this.tbAgents.Name = "tbAgents";
         this.tbAgents.Size = new System.Drawing.Size(148, 20);
         this.tbAgents.TabIndex = 3;
         // 
         // tbItems
         // 
         this.tbItems.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                     | System.Windows.Forms.AnchorStyles.Right)));
         this.tbItems.Enabled = false;
         this.tbItems.Location = new System.Drawing.Point(72, 96);
         this.tbItems.Name = "tbItems";
         this.tbItems.Size = new System.Drawing.Size(148, 20);
         this.tbItems.TabIndex = 4;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(48, 44);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(19, 13);
         this.label1.TabIndex = 6;
         this.label1.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(53, 18);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(13, 13);
         this.label2.TabIndex = 7;
         this.label2.Text = "с";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(22, 70);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(44, 13);
         this.label3.TabIndex = 8;
         this.label3.Text = "Агенты";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(28, 99);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(38, 13);
         this.label4.TabIndex = 9;
         this.label4.Text = "Товар";
         // 
         // btnAgents
         // 
         this.btnAgents.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnAgents.Location = new System.Drawing.Point(226, 65);
         this.btnAgents.Name = "btnAgents";
         this.btnAgents.Size = new System.Drawing.Size(30, 23);
         this.btnAgents.TabIndex = 10;
         this.btnAgents.Text = "...";
         this.btnAgents.UseVisualStyleBackColor = true;
         this.btnAgents.Click += new System.EventHandler(this.btnAgents_Click);
         // 
         // btnItems
         // 
         this.btnItems.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnItems.Location = new System.Drawing.Point(226, 94);
         this.btnItems.Name = "btnItems";
         this.btnItems.Size = new System.Drawing.Size(30, 23);
         this.btnItems.TabIndex = 11;
         this.btnItems.Text = "...";
         this.btnItems.UseVisualStyleBackColor = true;
         this.btnItems.Click += new System.EventHandler(this.btnItems_Click);
         // 
         // btnClearItems
         // 
         this.btnClearItems.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnClearItems.Location = new System.Drawing.Point(262, 94);
         this.btnClearItems.Name = "btnClearItems";
         this.btnClearItems.Size = new System.Drawing.Size(30, 23);
         this.btnClearItems.TabIndex = 13;
         this.btnClearItems.Text = "X";
         this.btnClearItems.UseVisualStyleBackColor = true;
         this.btnClearItems.Click += new System.EventHandler(this.btnClearItems_Click);
         // 
         // btnClearAgents
         // 
         this.btnClearAgents.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnClearAgents.Location = new System.Drawing.Point(262, 65);
         this.btnClearAgents.Name = "btnClearAgents";
         this.btnClearAgents.Size = new System.Drawing.Size(30, 23);
         this.btnClearAgents.TabIndex = 12;
         this.btnClearAgents.Text = "X";
         this.btnClearAgents.UseVisualStyleBackColor = true;
         this.btnClearAgents.Click += new System.EventHandler(this.btnClearAgents_Click);
         // 
         // AgentSalesPlanReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(304, 166);
         this.Controls.Add(this.btnClearItems);
         this.Controls.Add(this.btnClearAgents);
         this.Controls.Add(this.btnItems);
         this.Controls.Add(this.btnAgents);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbItems);
         this.Controls.Add(this.tbAgents);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpStart);
         this.Controls.Add(this.btnReport);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "AgentSalesPlanReport";
         this.Text = "Параметры отчета";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.TextBox tbAgents;
      private System.Windows.Forms.TextBox tbItems;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Button btnAgents;
      private System.Windows.Forms.Button btnItems;
      private System.Windows.Forms.Button btnClearItems;
      private System.Windows.Forms.Button btnClearAgents;
   }
}
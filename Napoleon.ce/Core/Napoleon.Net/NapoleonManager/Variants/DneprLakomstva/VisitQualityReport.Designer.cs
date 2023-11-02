namespace GRSoft.NapoleonManager
{
   partial class VisitQualityReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(VisitQualityReport));
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.cbDay = new System.Windows.Forms.ComboBox();
         this.tbItem1 = new System.Windows.Forms.TextBox();
         this.btnSetItem1 = new System.Windows.Forms.Button();
         this.label4 = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.btnSetItem2 = new System.Windows.Forms.Button();
         this.tbItem2 = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.btnSetItem3 = new System.Windows.Forms.Button();
         this.tbItem3 = new System.Windows.Forms.TextBox();
         this.btnExcel = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // cbDivision
         // 
         this.cbDivision.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbDivision.Enabled = false;
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(145, 59);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(214, 21);
         this.cbDivision.TabIndex = 8;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(36, 59);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(105, 17);
         this.rbDivision.TabIndex = 6;
         this.rbDivision.Text = "подразделению";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbDivision_CheckedChanged);
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Checked = true;
         this.rbAgent.Location = new System.Drawing.Point(37, 34);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(97, 17);
         this.rbAgent.TabIndex = 5;
         this.rbAgent.TabStop = true;
         this.rbAgent.Text = "по сотруднику";
         this.rbAgent.UseVisualStyleBackColor = true;
         this.rbAgent.CheckedChanged += new System.EventHandler(this.rbAgent_CheckedChanged);
         // 
         // cbAgent
         // 
         this.cbAgent.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(145, 32);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(214, 21);
         this.cbAgent.TabIndex = 7;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(122, 128);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(19, 13);
         this.label2.TabIndex = 12;
         this.label2.Text = "по";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(122, 103);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(13, 13);
         this.label1.TabIndex = 11;
         this.label1.Text = "с";
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(145, 100);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(146, 20);
         this.dtpBegin.TabIndex = 9;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(145, 126);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(146, 20);
         this.dtpEnd.TabIndex = 10;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(65, 159);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(70, 13);
         this.label3.TabIndex = 13;
         this.label3.Text = "день недели";
         // 
         // cbDay
         // 
         this.cbDay.FormattingEnabled = true;
         this.cbDay.Location = new System.Drawing.Point(145, 157);
         this.cbDay.Name = "cbDay";
         this.cbDay.Size = new System.Drawing.Size(214, 21);
         this.cbDay.TabIndex = 14;
         // 
         // tbItem1
         // 
         this.tbItem1.Location = new System.Drawing.Point(145, 204);
         this.tbItem1.Name = "tbItem1";
         this.tbItem1.Size = new System.Drawing.Size(214, 20);
         this.tbItem1.TabIndex = 15;
         // 
         // btnSetItem1
         // 
         this.btnSetItem1.Location = new System.Drawing.Point(362, 202);
         this.btnSetItem1.Name = "btnSetItem1";
         this.btnSetItem1.Size = new System.Drawing.Size(31, 23);
         this.btnSetItem1.TabIndex = 16;
         this.btnSetItem1.Text = ">";
         this.btnSetItem1.UseVisualStyleBackColor = true;
         this.btnSetItem1.Click += new System.EventHandler(this.btnSetItem1_Click);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(44, 207);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(88, 13);
         this.label4.TabIndex = 17;
         this.label4.Text = "номенклатура 1";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(44, 233);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(88, 13);
         this.label5.TabIndex = 20;
         this.label5.Text = "номенклатура 2";
         // 
         // btnSetItem2
         // 
         this.btnSetItem2.Location = new System.Drawing.Point(362, 228);
         this.btnSetItem2.Name = "btnSetItem2";
         this.btnSetItem2.Size = new System.Drawing.Size(31, 23);
         this.btnSetItem2.TabIndex = 19;
         this.btnSetItem2.Text = ">";
         this.btnSetItem2.UseVisualStyleBackColor = true;
         this.btnSetItem2.Click += new System.EventHandler(this.btnSetItem2_Click);
         // 
         // tbItem2
         // 
         this.tbItem2.Location = new System.Drawing.Point(145, 230);
         this.tbItem2.Name = "tbItem2";
         this.tbItem2.Size = new System.Drawing.Size(214, 20);
         this.tbItem2.TabIndex = 18;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(44, 259);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(88, 13);
         this.label6.TabIndex = 23;
         this.label6.Text = "номенклатура 3";
         // 
         // btnSetItem3
         // 
         this.btnSetItem3.Location = new System.Drawing.Point(362, 254);
         this.btnSetItem3.Name = "btnSetItem3";
         this.btnSetItem3.Size = new System.Drawing.Size(31, 23);
         this.btnSetItem3.TabIndex = 22;
         this.btnSetItem3.Text = ">";
         this.btnSetItem3.UseVisualStyleBackColor = true;
         this.btnSetItem3.Click += new System.EventHandler(this.btnSetItem3_Click);
         // 
         // tbItem3
         // 
         this.tbItem3.Location = new System.Drawing.Point(145, 256);
         this.tbItem3.Name = "tbItem3";
         this.tbItem3.Size = new System.Drawing.Size(214, 20);
         this.tbItem3.TabIndex = 21;
         // 
         // btnExcel
         // 
         this.btnExcel.Location = new System.Drawing.Point(162, 328);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 24;
         this.btnExcel.Text = "Отчет";
         this.btnExcel.UseVisualStyleBackColor = true;
         this.btnExcel.Click += new System.EventHandler(this.btnExcel_Click);
         // 
         // VisitQualityReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(432, 375);
         this.Controls.Add(this.btnExcel);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.btnSetItem3);
         this.Controls.Add(this.tbItem3);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.btnSetItem2);
         this.Controls.Add(this.tbItem2);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.btnSetItem1);
         this.Controls.Add(this.tbItem1);
         this.Controls.Add(this.cbDay);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.cbDivision);
         this.Controls.Add(this.rbDivision);
         this.Controls.Add(this.rbAgent);
         this.Controls.Add(this.cbAgent);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "VisitQualityReport";
         this.Text = "Реализация посещений";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label1;
      protected System.Windows.Forms.DateTimePicker dtpBegin;
      protected System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbDay;
      private System.Windows.Forms.TextBox tbItem1;
      private System.Windows.Forms.Button btnSetItem1;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Button btnSetItem2;
      private System.Windows.Forms.TextBox tbItem2;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.Button btnSetItem3;
      private System.Windows.Forms.TextBox tbItem3;
      private System.Windows.Forms.Button btnExcel;
   }
}
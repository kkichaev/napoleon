namespace GRSoft.NapoleonManager
{
   partial class FmFridgeRptParams
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmFridgeRptParams));
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.rbAll = new System.Windows.Forms.RadioButton();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.btnExcel = new System.Windows.Forms.Button();
         this.gbReport = new System.Windows.Forms.GroupBox();
         this.rbDoc = new System.Windows.Forms.RadioButton();
         this.rbPrez = new System.Windows.Forms.RadioButton();
         this.groupBox2.SuspendLayout();
         this.gbReport.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.rbAll);
         this.groupBox2.Controls.Add(this.cbDivision);
         this.groupBox2.Controls.Add(this.rbDivision);
         this.groupBox2.Controls.Add(this.rbAgent);
         this.groupBox2.Controls.Add(this.cbAgent);
         this.groupBox2.Location = new System.Drawing.Point(12, 12);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(200, 165);
         this.groupBox2.TabIndex = 9;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Данные по";
         // 
         // rbAll
         // 
         this.rbAll.AutoSize = true;
         this.rbAll.Checked = true;
         this.rbAll.Location = new System.Drawing.Point(17, 25);
         this.rbAll.Name = "rbAll";
         this.rbAll.Size = new System.Drawing.Size(44, 18);
         this.rbAll.TabIndex = 5;
         this.rbAll.TabStop = true;
         this.rbAll.Text = "все";
         this.rbAll.UseVisualStyleBackColor = true;
         this.rbAll.CheckedChanged += new System.EventHandler(this.rb_CheckedChanged);
         // 
         // cbDivision
         // 
         this.cbDivision.Enabled = false;
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(40, 128);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(154, 22);
         this.cbDivision.TabIndex = 4;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(17, 105);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(104, 18);
         this.rbDivision.TabIndex = 1;
         this.rbDivision.Text = "подразделению";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rb_CheckedChanged);
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(17, 53);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(84, 18);
         this.rbAgent.TabIndex = 0;
         this.rbAgent.Text = "сотруднику";
         this.rbAgent.UseVisualStyleBackColor = true;
         this.rbAgent.CheckedChanged += new System.EventHandler(this.rb_CheckedChanged);
         // 
         // cbAgent
         // 
         this.cbAgent.Enabled = false;
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(40, 76);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(154, 22);
         this.cbAgent.TabIndex = 3;
         // 
         // btnExcel
         // 
         this.btnExcel.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnExcel.Location = new System.Drawing.Point(12, 183);
         this.btnExcel.Name = "btnExcel";
         this.btnExcel.Size = new System.Drawing.Size(75, 23);
         this.btnExcel.TabIndex = 8;
         this.btnExcel.Text = "Excel";
         this.btnExcel.UseVisualStyleBackColor = true;
         // 
         // gbReport
         // 
         this.gbReport.Controls.Add(this.rbDoc);
         this.gbReport.Controls.Add(this.rbPrez);
         this.gbReport.Location = new System.Drawing.Point(218, 12);
         this.gbReport.Name = "gbReport";
         this.gbReport.Size = new System.Drawing.Size(218, 165);
         this.gbReport.TabIndex = 10;
         this.gbReport.TabStop = false;
         this.gbReport.Text = "Отчет";
         // 
         // rbDoc
         // 
         this.rbDoc.AutoSize = true;
         this.rbDoc.Location = new System.Drawing.Point(26, 53);
         this.rbDoc.Name = "rbDoc";
         this.rbDoc.Size = new System.Drawing.Size(121, 18);
         this.rbDoc.TabIndex = 1;
         this.rbDoc.Tag = "fridge_doc_rpt";
         this.rbDoc.Text = "по документам ХО";
         this.rbDoc.UseVisualStyleBackColor = true;
         // 
         // rbPrez
         // 
         this.rbPrez.AutoSize = true;
         this.rbPrez.Checked = true;
         this.rbPrez.Location = new System.Drawing.Point(26, 25);
         this.rbPrez.Name = "rbPrez";
         this.rbPrez.Size = new System.Drawing.Size(127, 18);
         this.rbPrez.TabIndex = 0;
         this.rbPrez.TabStop = true;
         this.rbPrez.Tag = "fridge_prez_rpt";
         this.rbPrez.Text = "по присутствию ХО";
         this.rbPrez.UseVisualStyleBackColor = true;
         // 
         // FmFridgeRptParams
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(446, 217);
         this.Controls.Add(this.gbReport);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.btnExcel);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmFridgeRptParams";
         this.Text = "Холодильное оборудование";
         this.Load += new System.EventHandler(this.FmFridgeRptParams_Load);
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.gbReport.ResumeLayout(false);
         this.gbReport.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.RadioButton rbAll;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.Button btnExcel;
      private System.Windows.Forms.GroupBox gbReport;
      private System.Windows.Forms.RadioButton rbDoc;
      private System.Windows.Forms.RadioButton rbPrez;
   }
}
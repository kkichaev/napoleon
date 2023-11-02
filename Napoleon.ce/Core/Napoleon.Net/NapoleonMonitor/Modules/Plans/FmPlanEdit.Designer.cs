namespace GRSoft.NapoleonManager
{
   partial class FmPlanEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPlanEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.label1 = new System.Windows.Forms.Label();
         this.cbAgents = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label4 = new System.Windows.Forms.Label();
         this.tbText = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.tbPlan = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 300);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(452, 40);
         this.panel1.TabIndex = 0;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(276, 9);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(363, 9);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(9, 40);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Агент";
         // 
         // cbAgents
         // 
         this.cbAgents.FormattingEnabled = true;
         this.cbAgents.Location = new System.Drawing.Point(66, 40);
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(121, 22);
         this.cbAgents.TabIndex = 2;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(9, 74);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(44, 14);
         this.label2.TabIndex = 3;
         this.label2.Text = "Начало";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(9, 109);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(38, 14);
         this.label3.TabIndex = 4;
         this.label3.Text = "Конец";
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(67, 74);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(148, 20);
         this.dtpBegin.TabIndex = 5;
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(67, 109);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(148, 20);
         this.dtpEnd.TabIndex = 6;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(9, 150);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(36, 14);
         this.label4.TabIndex = 7;
         this.label4.Text = "Текст";
         // 
         // tbText
         // 
         this.tbText.Location = new System.Drawing.Point(67, 150);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(371, 59);
         this.tbText.TabIndex = 8;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(9, 226);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(43, 14);
         this.label5.TabIndex = 9;
         this.label5.Text = "Кол-во";
         // 
         // tbPlan
         // 
         this.tbPlan.Location = new System.Drawing.Point(69, 226);
         this.tbPlan.Name = "tbPlan";
         this.tbPlan.Size = new System.Drawing.Size(100, 20);
         this.tbPlan.TabIndex = 10;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(10, 10);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(83, 14);
         this.label6.TabIndex = 11;
         this.label6.Text = "Наименование";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(99, 9);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(241, 20);
         this.tbName.TabIndex = 12;
         // 
         // FmPlanEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(452, 340);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.tbPlan);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.tbText);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbAgents);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPlanEdit";
         this.Text = "FmPlanEdit";
         this.Load += new System.EventHandler(this.FmPlanEdit_Load);
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbAgents;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox tbPlan;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbName;
   }
}
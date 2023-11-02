namespace GRSoft.Ads.Dispatcher
{
   partial class FmQuestItemEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmQuestItemEdit));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tbShortQuest = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.btnCancel = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.cbType = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbText = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.cbNecessary = new System.Windows.Forms.CheckBox();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.cbNecessary);
         this.splitContainer1.Panel1.Controls.Add(this.tbShortQuest);
         this.splitContainer1.Panel1.Controls.Add(this.label3);
         this.splitContainer1.Panel1.Controls.Add(this.btnCancel);
         this.splitContainer1.Panel1.Controls.Add(this.btnOK);
         this.splitContainer1.Panel1.Controls.Add(this.cbType);
         this.splitContainer1.Panel1.Controls.Add(this.label2);
         this.splitContainer1.Panel1.Controls.Add(this.tbText);
         this.splitContainer1.Panel1.Controls.Add(this.label1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(7, 8, 7, 8);
         this.splitContainer1.Size = new System.Drawing.Size(571, 292);
         this.splitContainer1.SplitterDistance = 297;
         this.splitContainer1.TabIndex = 0;
         // 
         // tbShortQuest
         // 
         this.tbShortQuest.Location = new System.Drawing.Point(7, 26);
         this.tbShortQuest.Name = "tbShortQuest";
         this.tbShortQuest.Size = new System.Drawing.Size(265, 20);
         this.tbShortQuest.TabIndex = 7;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(7, 8);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(79, 14);
         this.label3.TabIndex = 6;
         this.label3.Text = "Тема вопроса";
         // 
         // btnCancel
         // 
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(39, 259);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 25);
         this.btnCancel.TabIndex = 5;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(130, 259);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 25);
         this.btnOK.TabIndex = 4;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // cbType
         // 
         this.cbType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
         this.cbType.FormattingEnabled = true;
         this.cbType.Location = new System.Drawing.Point(7, 176);
         this.cbType.Name = "cbType";
         this.cbType.Size = new System.Drawing.Size(121, 22);
         this.cbType.TabIndex = 3;
         this.cbType.SelectedIndexChanged += new System.EventHandler(this.cbType_SelectedIndexChanged);
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 159);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(65, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Тип ответа";
         // 
         // tbText
         // 
         this.tbText.Location = new System.Drawing.Point(7, 71);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(269, 79);
         this.tbText.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(7, 54);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(44, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Вопрос";
         // 
         // cbNecessary
         // 
         this.cbNecessary.AutoSize = true;
         this.cbNecessary.Location = new System.Drawing.Point(7, 215);
         this.cbNecessary.Name = "cbNecessary";
         this.cbNecessary.Size = new System.Drawing.Size(135, 18);
         this.cbNecessary.TabIndex = 8;
         this.cbNecessary.Text = "Обязательный ответ";
         this.cbNecessary.UseVisualStyleBackColor = true;
         // 
         // FmQuestItemEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(571, 292);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmQuestItemEdit";
         this.Text = "Редактирование вопроса";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmQuestItemEdit_FormClosing);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.ComboBox cbType;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Label label3;
      public System.Windows.Forms.TextBox tbShortQuest;
      private System.Windows.Forms.CheckBox cbNecessary;
   }
}
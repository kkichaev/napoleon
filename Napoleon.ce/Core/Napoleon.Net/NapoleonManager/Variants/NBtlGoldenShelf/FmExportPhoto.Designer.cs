namespace GRSoft.NapoleonManager
{
   partial class FmExportPhoto
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmExportPhoto));
         this.tbPath = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.btnFolder = new System.Windows.Forms.Button();
         this.btnStart = new System.Windows.Forms.Button();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbLevel3Vaule = new System.Windows.Forms.ComboBox();
         this.cbLevel2Vaule = new System.Windows.Forms.ComboBox();
         this.cbLevel1Vaule = new System.Windows.Forms.ComboBox();
         this.cbLevel3 = new System.Windows.Forms.CheckBox();
         this.cbLevel2 = new System.Windows.Forms.CheckBox();
         this.cbLevel1 = new System.Windows.Forms.CheckBox();
         this.groupBox2.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tbPath
         // 
         this.tbPath.Location = new System.Drawing.Point(61, 6);
         this.tbPath.Name = "tbPath";
         this.tbPath.Size = new System.Drawing.Size(323, 20);
         this.tbPath.TabIndex = 0;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(18, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(37, 14);
         this.label1.TabIndex = 1;
         this.label1.Text = "Папка";
         // 
         // btnFolder
         // 
         this.btnFolder.Location = new System.Drawing.Point(405, 3);
         this.btnFolder.Name = "btnFolder";
         this.btnFolder.Size = new System.Drawing.Size(75, 23);
         this.btnFolder.TabIndex = 2;
         this.btnFolder.Text = "...";
         this.btnFolder.UseVisualStyleBackColor = true;
         this.btnFolder.Click += new System.EventHandler(this.btnFolder_Click);
         // 
         // btnStart
         // 
         this.btnStart.Location = new System.Drawing.Point(18, 235);
         this.btnStart.Name = "btnStart";
         this.btnStart.Size = new System.Drawing.Size(75, 23);
         this.btnStart.TabIndex = 4;
         this.btnStart.Text = "Выгрузить";
         this.btnStart.UseVisualStyleBackColor = true;
         this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
         // 
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(18, 32);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.TabIndex = 3;
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.cbDivision);
         this.groupBox2.Controls.Add(this.rbDivision);
         this.groupBox2.Controls.Add(this.rbAgent);
         this.groupBox2.Controls.Add(this.cbAgent);
         this.groupBox2.Location = new System.Drawing.Point(18, 65);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(200, 155);
         this.groupBox2.TabIndex = 7;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Данные по";
         // 
         // cbDivision
         // 
         this.cbDivision.FormattingEnabled = true;
         this.cbDivision.Location = new System.Drawing.Point(40, 112);
         this.cbDivision.Name = "cbDivision";
         this.cbDivision.Size = new System.Drawing.Size(154, 22);
         this.cbDivision.TabIndex = 4;
         // 
         // rbDivision
         // 
         this.rbDivision.AutoSize = true;
         this.rbDivision.Location = new System.Drawing.Point(17, 89);
         this.rbDivision.Name = "rbDivision";
         this.rbDivision.Size = new System.Drawing.Size(104, 18);
         this.rbDivision.TabIndex = 1;
         this.rbDivision.TabStop = true;
         this.rbDivision.Text = "подразделению";
         this.rbDivision.UseVisualStyleBackColor = true;
         this.rbDivision.CheckedChanged += new System.EventHandler(this.rbDivision_CheckedChanged);
         // 
         // rbAgent
         // 
         this.rbAgent.AutoSize = true;
         this.rbAgent.Location = new System.Drawing.Point(17, 26);
         this.rbAgent.Name = "rbAgent";
         this.rbAgent.Size = new System.Drawing.Size(84, 18);
         this.rbAgent.TabIndex = 0;
         this.rbAgent.TabStop = true;
         this.rbAgent.Text = "сотруднику";
         this.rbAgent.UseVisualStyleBackColor = true;
         this.rbAgent.CheckedChanged += new System.EventHandler(this.rbAgent_CheckedChanged);
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(40, 49);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(154, 22);
         this.cbAgent.TabIndex = 3;
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.cbLevel3Vaule);
         this.groupBox1.Controls.Add(this.cbLevel2Vaule);
         this.groupBox1.Controls.Add(this.cbLevel1Vaule);
         this.groupBox1.Controls.Add(this.cbLevel3);
         this.groupBox1.Controls.Add(this.cbLevel2);
         this.groupBox1.Controls.Add(this.cbLevel1);
         this.groupBox1.Location = new System.Drawing.Point(241, 65);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(308, 155);
         this.groupBox1.TabIndex = 8;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Структура папок";
         // 
         // cbLevel3Vaule
         // 
         this.cbLevel3Vaule.FormattingEnabled = true;
         this.cbLevel3Vaule.Items.AddRange(new object[] {
            "Контракт",
            "Фамилия сотрудника",
            "Название торговой сети"});
         this.cbLevel3Vaule.Location = new System.Drawing.Point(102, 81);
         this.cbLevel3Vaule.Name = "cbLevel3Vaule";
         this.cbLevel3Vaule.Size = new System.Drawing.Size(183, 22);
         this.cbLevel3Vaule.TabIndex = 5;
         // 
         // cbLevel2Vaule
         // 
         this.cbLevel2Vaule.FormattingEnabled = true;
         this.cbLevel2Vaule.Items.AddRange(new object[] {
            "Контракт",
            "Фамилия сотрудника",
            "Название торговой сети"});
         this.cbLevel2Vaule.Location = new System.Drawing.Point(102, 51);
         this.cbLevel2Vaule.Name = "cbLevel2Vaule";
         this.cbLevel2Vaule.Size = new System.Drawing.Size(183, 22);
         this.cbLevel2Vaule.TabIndex = 4;
         // 
         // cbLevel1Vaule
         // 
         this.cbLevel1Vaule.FormattingEnabled = true;
         this.cbLevel1Vaule.Items.AddRange(new object[] {
            "Контракт",
            "Фамилия сотрудника",
            "Название торговой сети"});
         this.cbLevel1Vaule.Location = new System.Drawing.Point(102, 24);
         this.cbLevel1Vaule.Name = "cbLevel1Vaule";
         this.cbLevel1Vaule.Size = new System.Drawing.Size(183, 22);
         this.cbLevel1Vaule.TabIndex = 3;
         // 
         // cbLevel3
         // 
         this.cbLevel3.AutoSize = true;
         this.cbLevel3.Checked = true;
         this.cbLevel3.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLevel3.Location = new System.Drawing.Point(6, 83);
         this.cbLevel3.Name = "cbLevel3";
         this.cbLevel3.Size = new System.Drawing.Size(79, 18);
         this.cbLevel3.TabIndex = 2;
         this.cbLevel3.Text = "Уровень 3";
         this.cbLevel3.UseVisualStyleBackColor = true;
         // 
         // cbLevel2
         // 
         this.cbLevel2.AutoSize = true;
         this.cbLevel2.Checked = true;
         this.cbLevel2.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLevel2.Location = new System.Drawing.Point(6, 53);
         this.cbLevel2.Name = "cbLevel2";
         this.cbLevel2.Size = new System.Drawing.Size(79, 18);
         this.cbLevel2.TabIndex = 1;
         this.cbLevel2.Text = "Уровень 2";
         this.cbLevel2.UseVisualStyleBackColor = true;
         // 
         // cbLevel1
         // 
         this.cbLevel1.AutoSize = true;
         this.cbLevel1.Checked = true;
         this.cbLevel1.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLevel1.Location = new System.Drawing.Point(6, 26);
         this.cbLevel1.Name = "cbLevel1";
         this.cbLevel1.Size = new System.Drawing.Size(79, 18);
         this.cbLevel1.TabIndex = 0;
         this.cbLevel1.Text = "Уровень 1";
         this.cbLevel1.UseVisualStyleBackColor = true;
         // 
         // FmExportPhoto
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(561, 274);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.btnStart);
         this.Controls.Add(this.dpv);
         this.Controls.Add(this.btnFolder);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbPath);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmExportPhoto";
         this.Text = "Выгрузка фотографий";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmExportPhoto_FormClosed);
         this.Load += new System.EventHandler(this.FmExportPhoto_Load);
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox tbPath;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button btnFolder;
      private DatePeriodView dpv;
      private System.Windows.Forms.Button btnStart;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.ComboBox cbDivision;
      private System.Windows.Forms.RadioButton rbDivision;
      private System.Windows.Forms.RadioButton rbAgent;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.ComboBox cbLevel3Vaule;
      private System.Windows.Forms.ComboBox cbLevel2Vaule;
      private System.Windows.Forms.ComboBox cbLevel1Vaule;
      private System.Windows.Forms.CheckBox cbLevel3;
      private System.Windows.Forms.CheckBox cbLevel2;
      private System.Windows.Forms.CheckBox cbLevel1;
   }
}
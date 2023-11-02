namespace GRSoft.NapoleonManager
{
   partial class FmExportPhotoNBTL
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmExportPhotoNBTL));
         this.tbPath = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.btnFolder = new System.Windows.Forms.Button();
         this.btnStart = new System.Windows.Forms.Button();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.btnContract = new System.Windows.Forms.Button();
         this.tbContract = new System.Windows.Forms.TextBox();
         this.rbContract = new System.Windows.Forms.RadioButton();
         this.btnSelOrg = new System.Windows.Forms.Button();
         this.tbOrg = new System.Windows.Forms.TextBox();
         this.rbOrg = new System.Windows.Forms.RadioButton();
         this.cbDivision = new System.Windows.Forms.ComboBox();
         this.rbDivision = new System.Windows.Forms.RadioButton();
         this.rbAgent = new System.Windows.Forms.RadioButton();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.cbLevel4Value = new System.Windows.Forms.ComboBox();
         this.cbLevel4 = new System.Windows.Forms.CheckBox();
         this.cbLevel3Value = new System.Windows.Forms.ComboBox();
         this.cbLevel2Value = new System.Windows.Forms.ComboBox();
         this.cbLevel1Value = new System.Windows.Forms.ComboBox();
         this.cbLevel3 = new System.Windows.Forms.CheckBox();
         this.cbLevel2 = new System.Windows.Forms.CheckBox();
         this.cbLevel1 = new System.Windows.Forms.CheckBox();
         this.dpv = new GRSoft.NapoleonManager.DatePeriodView();
         this.lblInfo = new System.Windows.Forms.Label();
         this.cbNotDrawText = new System.Windows.Forms.CheckBox();
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
         this.btnStart.Location = new System.Drawing.Point(18, 403);
         this.btnStart.Name = "btnStart";
         this.btnStart.Size = new System.Drawing.Size(75, 23);
         this.btnStart.TabIndex = 4;
         this.btnStart.Text = "Выгрузить";
         this.btnStart.UseVisualStyleBackColor = true;
         this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.btnContract);
         this.groupBox2.Controls.Add(this.tbContract);
         this.groupBox2.Controls.Add(this.rbContract);
         this.groupBox2.Controls.Add(this.btnSelOrg);
         this.groupBox2.Controls.Add(this.tbOrg);
         this.groupBox2.Controls.Add(this.rbOrg);
         this.groupBox2.Controls.Add(this.cbDivision);
         this.groupBox2.Controls.Add(this.rbDivision);
         this.groupBox2.Controls.Add(this.rbAgent);
         this.groupBox2.Controls.Add(this.cbAgent);
         this.groupBox2.Location = new System.Drawing.Point(18, 65);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(338, 292);
         this.groupBox2.TabIndex = 7;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Данные по";
         // 
         // btnContract
         // 
         this.btnContract.Location = new System.Drawing.Point(280, 234);
         this.btnContract.Name = "btnContract";
         this.btnContract.Size = new System.Drawing.Size(41, 23);
         this.btnContract.TabIndex = 10;
         this.btnContract.Text = "...";
         this.btnContract.UseVisualStyleBackColor = true;
         this.btnContract.Click += new System.EventHandler(this.btnContract_Click);
         // 
         // tbContract
         // 
         this.tbContract.Location = new System.Drawing.Point(40, 234);
         this.tbContract.Name = "tbContract";
         this.tbContract.Size = new System.Drawing.Size(234, 20);
         this.tbContract.TabIndex = 9;
         // 
         // rbContract
         // 
         this.rbContract.AutoSize = true;
         this.rbContract.Location = new System.Drawing.Point(17, 210);
         this.rbContract.Name = "rbContract";
         this.rbContract.Size = new System.Drawing.Size(93, 18);
         this.rbContract.TabIndex = 8;
         this.rbContract.TabStop = true;
         this.rbContract.Text = "По контракту";
         this.rbContract.UseVisualStyleBackColor = true;
         // 
         // btnSelOrg
         // 
         this.btnSelOrg.Location = new System.Drawing.Point(280, 177);
         this.btnSelOrg.Name = "btnSelOrg";
         this.btnSelOrg.Size = new System.Drawing.Size(41, 23);
         this.btnSelOrg.TabIndex = 7;
         this.btnSelOrg.Text = "...";
         this.btnSelOrg.UseVisualStyleBackColor = true;
         this.btnSelOrg.Click += new System.EventHandler(this.btnSelOrg_Click);
         // 
         // tbOrg
         // 
         this.tbOrg.Location = new System.Drawing.Point(40, 177);
         this.tbOrg.Name = "tbOrg";
         this.tbOrg.Size = new System.Drawing.Size(234, 20);
         this.tbOrg.TabIndex = 6;
         // 
         // rbOrg
         // 
         this.rbOrg.AutoSize = true;
         this.rbOrg.Location = new System.Drawing.Point(17, 153);
         this.rbOrg.Name = "rbOrg";
         this.rbOrg.Size = new System.Drawing.Size(72, 18);
         this.rbOrg.TabIndex = 5;
         this.rbOrg.TabStop = true;
         this.rbOrg.Text = "магазину";
         this.rbOrg.UseVisualStyleBackColor = true;
         this.rbOrg.CheckedChanged += new System.EventHandler(this.rbOrg_CheckedChanged);
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
         this.groupBox1.Controls.Add(this.cbLevel4Value);
         this.groupBox1.Controls.Add(this.cbLevel4);
         this.groupBox1.Controls.Add(this.cbLevel3Value);
         this.groupBox1.Controls.Add(this.cbLevel2Value);
         this.groupBox1.Controls.Add(this.cbLevel1Value);
         this.groupBox1.Controls.Add(this.cbLevel3);
         this.groupBox1.Controls.Add(this.cbLevel2);
         this.groupBox1.Controls.Add(this.cbLevel1);
         this.groupBox1.Location = new System.Drawing.Point(362, 72);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(308, 155);
         this.groupBox1.TabIndex = 8;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Структура папок";
         // 
         // cbLevel4Value
         // 
         this.cbLevel4Value.FormattingEnabled = true;
         this.cbLevel4Value.Location = new System.Drawing.Point(102, 110);
         this.cbLevel4Value.Name = "cbLevel4Value";
         this.cbLevel4Value.Size = new System.Drawing.Size(183, 22);
         this.cbLevel4Value.TabIndex = 7;
         // 
         // cbLevel4
         // 
         this.cbLevel4.AutoSize = true;
         this.cbLevel4.Checked = true;
         this.cbLevel4.CheckState = System.Windows.Forms.CheckState.Checked;
         this.cbLevel4.Location = new System.Drawing.Point(6, 112);
         this.cbLevel4.Name = "cbLevel4";
         this.cbLevel4.Size = new System.Drawing.Size(79, 18);
         this.cbLevel4.TabIndex = 6;
         this.cbLevel4.Text = "Уровень 4";
         this.cbLevel4.UseVisualStyleBackColor = true;
         // 
         // cbLevel3Value
         // 
         this.cbLevel3Value.FormattingEnabled = true;
         this.cbLevel3Value.Location = new System.Drawing.Point(102, 81);
         this.cbLevel3Value.Name = "cbLevel3Value";
         this.cbLevel3Value.Size = new System.Drawing.Size(183, 22);
         this.cbLevel3Value.TabIndex = 5;
         // 
         // cbLevel2Value
         // 
         this.cbLevel2Value.FormattingEnabled = true;
         this.cbLevel2Value.Location = new System.Drawing.Point(102, 51);
         this.cbLevel2Value.Name = "cbLevel2Value";
         this.cbLevel2Value.Size = new System.Drawing.Size(183, 22);
         this.cbLevel2Value.TabIndex = 4;
         // 
         // cbLevel1Value
         // 
         this.cbLevel1Value.FormattingEnabled = true;
         this.cbLevel1Value.Location = new System.Drawing.Point(102, 24);
         this.cbLevel1Value.Name = "cbLevel1Value";
         this.cbLevel1Value.Size = new System.Drawing.Size(183, 22);
         this.cbLevel1Value.TabIndex = 3;
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
         // dpv
         // 
         this.dpv.Finish = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.Location = new System.Drawing.Point(18, 32);
         this.dpv.Name = "dpv";
         this.dpv.Size = new System.Drawing.Size(367, 27);
         this.dpv.Start = new System.DateTime(2015, 1, 12, 0, 0, 0, 0);
         this.dpv.TabIndex = 3;
         // 
         // lblInfo
         // 
         this.lblInfo.AutoSize = true;
         this.lblInfo.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lblInfo.ForeColor = System.Drawing.Color.Firebrick;
         this.lblInfo.Location = new System.Drawing.Point(17, 370);
         this.lblInfo.Name = "lblInfo";
         this.lblInfo.Size = new System.Drawing.Size(54, 19);
         this.lblInfo.TabIndex = 9;
         this.lblInfo.Text = "label2";
         // 
         // cbNotDrawText
         // 
         this.cbNotDrawText.AutoSize = true;
         this.cbNotDrawText.Location = new System.Drawing.Point(362, 242);
         this.cbNotDrawText.Name = "cbNotDrawText";
         this.cbNotDrawText.Size = new System.Drawing.Size(138, 18);
         this.cbNotDrawText.TabIndex = 10;
         this.cbNotDrawText.Text = "Выгрузка без подписи";
         this.cbNotDrawText.UseVisualStyleBackColor = true;
         // 
         // FmExportPhotoNBTL
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(693, 438);
         this.Controls.Add(this.cbNotDrawText);
         this.Controls.Add(this.lblInfo);
         this.Controls.Add(this.groupBox1);
         this.Controls.Add(this.groupBox2);
         this.Controls.Add(this.btnStart);
         this.Controls.Add(this.dpv);
         this.Controls.Add(this.btnFolder);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.tbPath);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmExportPhotoNBTL";
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
      private System.Windows.Forms.ComboBox cbLevel3Value;
      private System.Windows.Forms.ComboBox cbLevel2Value;
      private System.Windows.Forms.ComboBox cbLevel1Value;
      private System.Windows.Forms.CheckBox cbLevel3;
      private System.Windows.Forms.CheckBox cbLevel2;
      private System.Windows.Forms.CheckBox cbLevel1;
      private System.Windows.Forms.RadioButton rbOrg;
      private System.Windows.Forms.Button btnSelOrg;
      private System.Windows.Forms.TextBox tbOrg;
      private System.Windows.Forms.ComboBox cbLevel4Value;
      private System.Windows.Forms.CheckBox cbLevel4;
      private System.Windows.Forms.Button btnContract;
      private System.Windows.Forms.TextBox tbContract;
      private System.Windows.Forms.RadioButton rbContract;
      private System.Windows.Forms.Label lblInfo;
      private System.Windows.Forms.CheckBox cbNotDrawText;
   }
}
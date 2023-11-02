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
         this.dgvContracts = new System.Windows.Forms.DataGridView();
         this.btnRefresh = new System.Windows.Forms.Button();
         this.clmnCheck = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnContract = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.groupBox2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvContracts)).BeginInit();
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
         this.btnFolder.Location = new System.Drawing.Point(398, 4);
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
         // dgvContracts
         // 
         this.dgvContracts.AllowUserToAddRows = false;
         this.dgvContracts.AllowUserToDeleteRows = false;
         this.dgvContracts.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.dgvContracts.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvContracts.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnCheck,
            this.clmnContract});
         this.dgvContracts.Location = new System.Drawing.Point(240, 65);
         this.dgvContracts.Name = "dgvContracts";
         this.dgvContracts.RowHeadersVisible = false;
         this.dgvContracts.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvContracts.Size = new System.Drawing.Size(309, 197);
         this.dgvContracts.TabIndex = 8;
         // 
         // btnRefresh
         // 
         this.btnRefresh.BackgroundImage = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Zoom;
         this.btnRefresh.Location = new System.Drawing.Point(397, 31);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(29, 25);
         this.btnRefresh.TabIndex = 9;
         this.btnRefresh.UseVisualStyleBackColor = true;
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // clmnCheck
         // 
         this.clmnCheck.DataPropertyName = "Checked";
         this.clmnCheck.HeaderText = "";
         this.clmnCheck.Name = "clmnCheck";
         this.clmnCheck.Width = 40;
         // 
         // clmnContract
         // 
         this.clmnContract.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnContract.DataPropertyName = "Contract";
         this.clmnContract.HeaderText = "Контракт";
         this.clmnContract.Name = "clmnContract";
         // 
         // FmExportPhoto
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(561, 274);
         this.Controls.Add(this.btnRefresh);
         this.Controls.Add(this.dgvContracts);
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
         ((System.ComponentModel.ISupportInitialize)(this.dgvContracts)).EndInit();
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
      private System.Windows.Forms.DataGridView dgvContracts;
      private System.Windows.Forms.Button btnRefresh;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnCheck;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnContract;
   }
}
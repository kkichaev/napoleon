namespace SyncDocs
{
   partial class Form1
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.portDydo = new System.Windows.Forms.TextBox();
         this.IPDydo = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.portAvalon = new System.Windows.Forms.TextBox();
         this.IPAvalon = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.dtpStart = new System.Windows.Forms.DateTimePicker();
         this.dtpFinish = new System.Windows.Forms.DateTimePicker();
         this.label5 = new System.Windows.Forms.Label();
         this.label6 = new System.Windows.Forms.Label();
         this.button1 = new System.Windows.Forms.Button();
         this.panel1 = new System.Windows.Forms.Panel();
         this.button2 = new System.Windows.Forms.Button();
         this.panel2 = new System.Windows.Forms.Panel();
         this.dgvData = new System.Windows.Forms.DataGridView();
         this.clmnSrcObject = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDestObject = new System.Windows.Forms.DataGridViewComboBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbType = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.tsbViewType = new System.Windows.Forms.ToolStripComboBox();
         this.tsbSave = new System.Windows.Forms.ToolStripButton();
         this.button3 = new System.Windows.Forms.Button();
         this.groupBox1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvData)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.portDydo);
         this.groupBox1.Controls.Add(this.IPDydo);
         this.groupBox1.Controls.Add(this.label1);
         this.groupBox1.Controls.Add(this.label2);
         this.groupBox1.Location = new System.Drawing.Point(21, 27);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(314, 80);
         this.groupBox1.TabIndex = 0;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Дайдо";
         // 
         // portDydo
         // 
         this.portDydo.Location = new System.Drawing.Point(87, 46);
         this.portDydo.Name = "portDydo";
         this.portDydo.Size = new System.Drawing.Size(81, 20);
         this.portDydo.TabIndex = 5;
         // 
         // IPDydo
         // 
         this.IPDydo.Location = new System.Drawing.Point(87, 20);
         this.IPDydo.Name = "IPDydo";
         this.IPDydo.Size = new System.Drawing.Size(187, 20);
         this.IPDydo.TabIndex = 4;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(60, 23);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(17, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "IP";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(47, 49);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(30, 13);
         this.label2.TabIndex = 3;
         this.label2.Text = "порт";
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.portAvalon);
         this.groupBox2.Controls.Add(this.IPAvalon);
         this.groupBox2.Controls.Add(this.label3);
         this.groupBox2.Controls.Add(this.label4);
         this.groupBox2.Location = new System.Drawing.Point(367, 27);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(327, 80);
         this.groupBox2.TabIndex = 1;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Авалон";
         // 
         // portAvalon
         // 
         this.portAvalon.Location = new System.Drawing.Point(87, 46);
         this.portAvalon.Name = "portAvalon";
         this.portAvalon.Size = new System.Drawing.Size(81, 20);
         this.portAvalon.TabIndex = 9;
         // 
         // IPAvalon
         // 
         this.IPAvalon.Location = new System.Drawing.Point(87, 20);
         this.IPAvalon.Name = "IPAvalon";
         this.IPAvalon.Size = new System.Drawing.Size(187, 20);
         this.IPAvalon.TabIndex = 8;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(60, 23);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(17, 13);
         this.label3.TabIndex = 6;
         this.label3.Text = "IP";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(47, 49);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(30, 13);
         this.label4.TabIndex = 7;
         this.label4.Text = "порт";
         // 
         // dtpStart
         // 
         this.dtpStart.Location = new System.Drawing.Point(177, 121);
         this.dtpStart.Name = "dtpStart";
         this.dtpStart.Size = new System.Drawing.Size(141, 20);
         this.dtpStart.TabIndex = 2;
         // 
         // dtpFinish
         // 
         this.dtpFinish.Location = new System.Drawing.Point(355, 121);
         this.dtpFinish.Name = "dtpFinish";
         this.dtpFinish.Size = new System.Drawing.Size(141, 20);
         this.dtpFinish.TabIndex = 3;
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(39, 125);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(131, 13);
         this.label5.TabIndex = 4;
         this.label5.Text = "Выгружать документы с";
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(328, 125);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(19, 13);
         this.label6.TabIndex = 5;
         this.label6.Text = "по";
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(542, 117);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(113, 28);
         this.button1.TabIndex = 6;
         this.button1.Text = "Получить данные";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // panel1
         // 
         this.panel1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.panel1.Controls.Add(this.button3);
         this.panel1.Controls.Add(this.button2);
         this.panel1.Controls.Add(this.button1);
         this.panel1.Controls.Add(this.groupBox1);
         this.panel1.Controls.Add(this.label6);
         this.panel1.Controls.Add(this.groupBox2);
         this.panel1.Controls.Add(this.label5);
         this.panel1.Controls.Add(this.dtpStart);
         this.panel1.Controls.Add(this.dtpFinish);
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(809, 162);
         this.panel1.TabIndex = 7;
         // 
         // button2
         // 
         this.button2.Location = new System.Drawing.Point(709, 47);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(79, 47);
         this.button2.TabIndex = 7;
         this.button2.Text = "Экспорт";
         this.button2.UseVisualStyleBackColor = true;
         this.button2.Click += new System.EventHandler(this.button2_Click);
         // 
         // panel2
         // 
         this.panel2.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.panel2.Controls.Add(this.dgvData);
         this.panel2.Controls.Add(this.toolStrip1);
         this.panel2.Location = new System.Drawing.Point(0, 164);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(809, 460);
         this.panel2.TabIndex = 8;
         // 
         // dgvData
         // 
         this.dgvData.AllowUserToAddRows = false;
         this.dgvData.AllowUserToDeleteRows = false;
         this.dgvData.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvData.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnSrcObject,
            this.clmnDestObject});
         this.dgvData.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvData.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvData.Location = new System.Drawing.Point(0, 25);
         this.dgvData.Name = "dgvData";
         this.dgvData.RowHeadersVisible = false;
         this.dgvData.Size = new System.Drawing.Size(809, 435);
         this.dgvData.TabIndex = 1;
         this.dgvData.CellEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvData_CellEnter);
         this.dgvData.DataError += new System.Windows.Forms.DataGridViewDataErrorEventHandler(this.dgvData_DataError);
         this.dgvData.EditingControlShowing += new System.Windows.Forms.DataGridViewEditingControlShowingEventHandler(this.dgvData_EditingControlShowing);
         // 
         // clmnSrcObject
         // 
         this.clmnSrcObject.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnSrcObject.DataPropertyName = "SrcObject";
         this.clmnSrcObject.HeaderText = "Данные из Дайдо";
         this.clmnSrcObject.Name = "clmnSrcObject";
         // 
         // clmnDestObject
         // 
         this.clmnDestObject.DataPropertyName = "DestObject";
         this.clmnDestObject.DisplayStyle = System.Windows.Forms.DataGridViewComboBoxDisplayStyle.Nothing;
         this.clmnDestObject.HeaderText = "Даные Авалон";
         this.clmnDestObject.Name = "clmnDestObject";
         this.clmnDestObject.Resizable = System.Windows.Forms.DataGridViewTriState.True;
         this.clmnDestObject.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
         this.clmnDestObject.Width = 380;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbType,
            this.toolStripLabel1,
            this.tsbViewType,
            this.tsbSave});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(809, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbType
         // 
         this.tsbType.Items.AddRange(new object[] {
            "Агенты",
            "Автоматы",
            "Товары"});
         this.tsbType.Name = "tsbType";
         this.tsbType.Size = new System.Drawing.Size(150, 25);
         this.tsbType.SelectedIndexChanged += new System.EventHandler(this.tsbType_SelectedIndexChanged);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Margin = new System.Windows.Forms.Padding(5, 1, 0, 2);
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(114, 22);
         this.toolStripLabel1.Text = "показывать данные";
         // 
         // tsbViewType
         // 
         this.tsbViewType.Items.AddRange(new object[] {
            "Все",
            "Необходимые для загрузки полученных документов"});
         this.tsbViewType.Name = "tsbViewType";
         this.tsbViewType.Size = new System.Drawing.Size(330, 25);
         this.tsbViewType.SelectedIndexChanged += new System.EventHandler(this.tsbViewType_SelectedIndexChanged);
         // 
         // tsbSave
         // 
         this.tsbSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbSave.Image = ((System.Drawing.Image)(resources.GetObject("tsbSave.Image")));
         this.tsbSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbSave.Name = "tsbSave";
         this.tsbSave.Size = new System.Drawing.Size(23, 22);
         this.tsbSave.Text = "Сохранить";
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         // 
         // button3
         // 
         this.button3.Location = new System.Drawing.Point(675, 117);
         this.button3.Name = "button3";
         this.button3.Size = new System.Drawing.Size(113, 28);
         this.button3.TabIndex = 8;
         this.button3.Text = "Документы";
         this.button3.UseVisualStyleBackColor = true;
         this.button3.Click += new System.EventHandler(this.button3_Click);
         // 
         // Form1
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(810, 625);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "Form1";
         this.Text = "Настройки синхронизации";
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.groupBox2.ResumeLayout(false);
         this.groupBox2.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.dgvData)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox IPDydo;
      private System.Windows.Forms.TextBox portDydo;
      private System.Windows.Forms.TextBox portAvalon;
      private System.Windows.Forms.TextBox IPAvalon;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpStart;
      private System.Windows.Forms.DateTimePicker dtpFinish;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.DataGridView dgvData;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripComboBox tsbType;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox tsbViewType;
      private System.Windows.Forms.Button button2;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnSrcObject;
      private System.Windows.Forms.DataGridViewComboBoxColumn clmnDestObject;
      private System.Windows.Forms.ToolStripButton tsbSave;
      private System.Windows.Forms.Button button3;
   }
}


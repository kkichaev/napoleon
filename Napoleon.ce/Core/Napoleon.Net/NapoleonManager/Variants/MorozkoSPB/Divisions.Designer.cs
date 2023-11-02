namespace GRSoft.NapoleonManager
{
   partial class Divisions
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
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Divisions));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.tgvDivisions = new GRSoft.UILib.TreeGridView();
         this.clmnName = new GRSoft.UILib.TreeGridColumn();
         this.clmnCode = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbFind = new System.Windows.Forms.ToolStripTextBox();
         this.tsbClearFind = new System.Windows.Forms.ToolStripButton();
         this.images = new System.Windows.Forms.ImageList(this.components);
         this.tb = new System.Windows.Forms.ToolStrip();
         this.newButton = new System.Windows.Forms.ToolStripDropDownButton();
         this.miAddDivision = new System.Windows.Forms.ToolStripMenuItem();
         this.miAddAgent = new System.Windows.Forms.ToolStripMenuItem();
         this.delButton = new System.Windows.Forms.ToolStripButton();
         this.saveButton = new System.Windows.Forms.ToolStripButton();
         this.tsbMatrixDesigner = new System.Windows.Forms.ToolStripButton();
         this.setColor = new System.Windows.Forms.ToolStripSplitButton();
         this.orgsSetColor = new System.Windows.Forms.ToolStripMenuItem();
         this.priceSetColor = new System.Windows.Forms.ToolStripMenuItem();
         this.tsEditColor = new System.Windows.Forms.ToolStripMenuItem();
         this.tbSep1 = new System.Windows.Forms.ToolStripSeparator();
         this.tbCoef = new System.Windows.Forms.ToolStripButton();
         this.tbSep2 = new System.Windows.Forms.ToolStripSeparator();
         this.btnScriptDesigner = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.connectStatus = new System.Windows.Forms.ToolStripStatusLabel();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.tgvDivisions)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.tb.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tgvDivisions);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         this.splitContainer1.Panel1.Padding = new System.Windows.Forms.Padding(7, 7, 0, 7);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(0, 0, 0, 5);
         this.splitContainer1.Size = new System.Drawing.Size(965, 541);
         this.splitContainer1.SplitterDistance = 487;
         this.splitContainer1.TabIndex = 0;
         // 
         // tgvDivisions
         // 
         this.tgvDivisions.AllowUserToAddRows = false;
         this.tgvDivisions.AllowUserToDeleteRows = false;
         this.tgvDivisions.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnName,
            this.clmnCode});
         this.tgvDivisions.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tgvDivisions.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
         this.tgvDivisions.ImageList = null;
         this.tgvDivisions.Location = new System.Drawing.Point(7, 7);
         this.tgvDivisions.Name = "tgvDivisions";
         this.tgvDivisions.RowHeadersVisible = false;
         this.tgvDivisions.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.tgvDivisions.Size = new System.Drawing.Size(480, 502);
         this.tgvDivisions.TabIndex = 0;
         this.tgvDivisions.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.tgvDivisions_CellFormatting);
         this.tgvDivisions.ColumnHeaderMouseClick += new System.Windows.Forms.DataGridViewCellMouseEventHandler(this.tgvDivisions_ColumnHeaderMouseClick);
         this.tgvDivisions.RowEnter += new System.Windows.Forms.DataGridViewCellEventHandler(this.tgvDivisions_RowEnter);
         // 
         // clmnName
         // 
         this.clmnName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnName.DataPropertyName = "Name";
         this.clmnName.DefaultNodeImage = null;
         this.clmnName.HeaderText = "Наименование";
         this.clmnName.Name = "clmnName";
         this.clmnName.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // clmnCode
         // 
         this.clmnCode.DataPropertyName = "ID";
         this.clmnCode.HeaderText = "Код";
         this.clmnCode.Name = "clmnCode";
         this.clmnCode.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbFind,
            this.tsbClearFind});
         this.toolStrip1.Location = new System.Drawing.Point(7, 509);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(480, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbFind
         // 
         this.tsbFind.Name = "tsbFind";
         this.tsbFind.Size = new System.Drawing.Size(150, 25);
         this.tsbFind.TextChanged += new System.EventHandler(this.tsbFind_TextChanged);
         // 
         // tsbClearFind
         // 
         this.tsbClearFind.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbClearFind.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.tsbClearFind.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbClearFind.Name = "tsbClearFind";
         this.tsbClearFind.Size = new System.Drawing.Size(23, 22);
         this.tsbClearFind.Text = "toolStripButton1";
         this.tsbClearFind.Click += new System.EventHandler(this.tsbClearFind_Click);
         // 
         // images
         // 
         this.images.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("images.ImageStream")));
         this.images.TransparentColor = System.Drawing.Color.Transparent;
         this.images.Images.SetKeyName(0, "Folder_Back.ico");
         this.images.Images.SetKeyName(1, "folder_open.ico");
         this.images.Images.SetKeyName(2, "user.ico");
         this.images.Images.SetKeyName(3, "Users.bmp");
         // 
         // tb
         // 
         this.tb.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.newButton,
            this.delButton,
            this.saveButton,
            this.tsbMatrixDesigner,
            this.setColor,
            this.tbSep1,
            this.tbCoef,
            this.tbSep2,
            this.btnScriptDesigner});
         this.tb.Location = new System.Drawing.Point(0, 0);
         this.tb.Name = "tb";
         this.tb.Size = new System.Drawing.Size(965, 25);
         this.tb.TabIndex = 1;
         this.tb.Text = "Коэф. автозаказа";
         // 
         // newButton
         // 
         this.newButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.newButton.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miAddDivision,
            this.miAddAgent});
         this.newButton.Image = ((System.Drawing.Image)(resources.GetObject("newButton.Image")));
         this.newButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.newButton.Name = "newButton";
         this.newButton.Size = new System.Drawing.Size(29, 22);
         this.newButton.Text = "Создать";
         this.newButton.DropDownOpening += new System.EventHandler(this.newButton_DropDownOpening);
         // 
         // miAddDivision
         // 
         this.miAddDivision.Name = "miAddDivision";
         this.miAddDivision.Size = new System.Drawing.Size(212, 22);
         this.miAddDivision.Text = "Добавить подразделение";
         this.miAddDivision.Click += new System.EventHandler(this.miAddDivision_Click);
         // 
         // miAddAgent
         // 
         this.miAddAgent.Name = "miAddAgent";
         this.miAddAgent.Size = new System.Drawing.Size(212, 22);
         this.miAddAgent.Text = "Добавить агента";
         this.miAddAgent.Click += new System.EventHandler(this.miAddAgent_Click);
         // 
         // delButton
         // 
         this.delButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.delButton.Image = ((System.Drawing.Image)(resources.GetObject("delButton.Image")));
         this.delButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.delButton.Name = "delButton";
         this.delButton.Size = new System.Drawing.Size(23, 22);
         this.delButton.Text = "Удалить";
         this.delButton.Click += new System.EventHandler(this.delButton_Click);
         // 
         // saveButton
         // 
         this.saveButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.saveButton.Enabled = false;
         this.saveButton.Image = ((System.Drawing.Image)(resources.GetObject("saveButton.Image")));
         this.saveButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.saveButton.Name = "saveButton";
         this.saveButton.Size = new System.Drawing.Size(23, 22);
         this.saveButton.Text = "Сохранить";
         this.saveButton.Click += new System.EventHandler(this.saveButton_Click);
         // 
         // tsbMatrixDesigner
         // 
         this.tsbMatrixDesigner.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMatrixDesigner.Image = ((System.Drawing.Image)(resources.GetObject("tsbMatrixDesigner.Image")));
         this.tsbMatrixDesigner.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMatrixDesigner.Name = "tsbMatrixDesigner";
         this.tsbMatrixDesigner.Size = new System.Drawing.Size(23, 22);
         this.tsbMatrixDesigner.Text = "Редактор матриц";
         this.tsbMatrixDesigner.Click += new System.EventHandler(this.tsbMatrixDesigner_Click);
         // 
         // setColor
         // 
         this.setColor.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.setColor.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.orgsSetColor,
            this.priceSetColor,
            this.tsEditColor});
         this.setColor.Image = ((System.Drawing.Image)(resources.GetObject("setColor.Image")));
         this.setColor.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.setColor.Name = "setColor";
         this.setColor.Size = new System.Drawing.Size(108, 22);
         this.setColor.Text = "Назначить цвет";
         // 
         // orgsSetColor
         // 
         this.orgsSetColor.Name = "orgsSetColor";
         this.orgsSetColor.Size = new System.Drawing.Size(164, 22);
         this.orgsSetColor.Text = "Контрагенты";
         this.orgsSetColor.Click += new System.EventHandler(this.orgsSetColor_Click);
         // 
         // priceSetColor
         // 
         this.priceSetColor.Name = "priceSetColor";
         this.priceSetColor.Size = new System.Drawing.Size(164, 22);
         this.priceSetColor.Text = "Прайс-лист";
         this.priceSetColor.Click += new System.EventHandler(this.priceSetColor_Click);
         // 
         // tsEditColor
         // 
         this.tsEditColor.Name = "tsEditColor";
         this.tsEditColor.Size = new System.Drawing.Size(164, 22);
         this.tsEditColor.Text = "Редактор цветов";
         this.tsEditColor.Click += new System.EventHandler(this.tsEditColor_Click);
         // 
         // tbSep1
         // 
         this.tbSep1.Name = "tbSep1";
         this.tbSep1.Size = new System.Drawing.Size(6, 25);
         // 
         // tbCoef
         // 
         this.tbCoef.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.tbCoef.Image = ((System.Drawing.Image)(resources.GetObject("tbCoef.Image")));
         this.tbCoef.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbCoef.Name = "tbCoef";
         this.tbCoef.Size = new System.Drawing.Size(101, 22);
         this.tbCoef.Text = "Коэф.автозаказа";
         this.tbCoef.Click += new System.EventHandler(this.toolStripLabel1_Click);
         // 
         // tbSep2
         // 
         this.tbSep2.Name = "tbSep2";
         this.tbSep2.Size = new System.Drawing.Size(6, 25);
         // 
         // btnScriptDesigner
         // 
         this.btnScriptDesigner.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnScriptDesigner.Image = ((System.Drawing.Image)(resources.GetObject("btnScriptDesigner.Image")));
         this.btnScriptDesigner.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnScriptDesigner.Name = "btnScriptDesigner";
         this.btnScriptDesigner.Size = new System.Drawing.Size(23, 22);
         this.btnScriptDesigner.Text = "Редактор сценариев";
         this.btnScriptDesigner.Visible = false;
         this.btnScriptDesigner.Click += new System.EventHandler(this.btnScriptDesigner_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.connectStatus});
         this.statusStrip1.Location = new System.Drawing.Point(0, 566);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(965, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // connectStatus
         // 
         this.connectStatus.Name = "connectStatus";
         this.connectStatus.Size = new System.Drawing.Size(0, 17);
         // 
         // timer1
         // 
         this.timer1.Interval = 500;
         this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
         // 
         // Divisions
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(965, 588);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.tb);
         this.Controls.Add(this.statusStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "Divisions";
         this.Text = "Управление командой";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Divisions_FormClosing);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.Divisions_FormClosed);
         this.Load += new System.EventHandler(this.Divisions_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.tgvDivisions)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.tb.ResumeLayout(false);
         this.tb.PerformLayout();
         this.statusStrip1.ResumeLayout(false);
         this.statusStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      public System.Windows.Forms.ToolStrip tb;
      private System.Windows.Forms.ToolStripButton delButton;
      private System.Windows.Forms.ToolStripButton saveButton;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.ToolStripStatusLabel connectStatus;
      private System.Windows.Forms.ToolStripDropDownButton newButton;
      private System.Windows.Forms.ToolStripMenuItem miAddDivision;
      private System.Windows.Forms.ToolStripMenuItem miAddAgent;
      public System.Windows.Forms.ImageList images;
      public System.Windows.Forms.ToolStripButton tsbMatrixDesigner;
      private System.Windows.Forms.ToolStripSeparator tbSep1;
      private System.Windows.Forms.ToolStripButton tbCoef;
      private System.Windows.Forms.ToolStripSeparator tbSep2;
      protected System.Windows.Forms.ToolStripSplitButton setColor;
      private System.Windows.Forms.ToolStripMenuItem orgsSetColor;
      private System.Windows.Forms.ToolStripMenuItem priceSetColor;
      private System.Windows.Forms.ToolStripMenuItem tsEditColor;
      private System.Windows.Forms.ToolStripButton btnScriptDesigner;
      private UILib.TreeGridView tgvDivisions;
      private UILib.TreeGridColumn clmnName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCode;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.Timer timer1;
      private System.Windows.Forms.ToolStripTextBox tsbFind;
      private System.Windows.Forms.ToolStripButton tsbClearFind;

   }
}
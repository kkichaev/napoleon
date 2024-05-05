namespace GRSoft.NapoleonManager
{
   partial class FmScriptEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmScriptEdit));
         this.Label1 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.lvDocs = new System.Windows.Forms.ListView();
         this.imageList1 = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.lblInfo = new System.Windows.Forms.ToolStripLabel();
         this.groupBox2 = new System.Windows.Forms.GroupBox();
         this.lvDocsAvail = new System.Windows.Forms.ListView();
         this.btnAdd = new System.Windows.Forms.Button();
         this.btnDel = new System.Windows.Forms.Button();
         this.btnUp = new System.Windows.Forms.Button();
         this.btnDown = new System.Windows.Forms.Button();
         this.panel1 = new System.Windows.Forms.Panel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.panel2 = new System.Windows.Forms.Panel();
         this.cbOrgType = new System.Windows.Forms.ToolStripComboBox();
         this.groupBox1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.groupBox2.SuspendLayout();
         this.panel1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // Label1
         // 
         this.Label1.AutoSize = true;
         this.Label1.Location = new System.Drawing.Point(7, 6);
         this.Label1.Name = "Label1";
         this.Label1.Size = new System.Drawing.Size(83, 14);
         this.Label1.TabIndex = 0;
         this.Label1.Text = "Наименование";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(96, 3);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(630, 20);
         this.tbName.TabIndex = 1;
         this.tbName.TextChanged += new System.EventHandler(this.tbName_TextChanged);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.lvDocs);
         this.groupBox1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox1.ForeColor = System.Drawing.Color.Blue;
         this.groupBox1.Location = new System.Drawing.Point(0, 0);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(281, 393);
         this.groupBox1.TabIndex = 2;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "В сценарии";
         // 
         // lvDocs
         // 
         this.lvDocs.CheckBoxes = true;
         this.lvDocs.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvDocs.HideSelection = false;
         this.lvDocs.LabelEdit = true;
         this.lvDocs.LargeImageList = this.imageList1;
         this.lvDocs.Location = new System.Drawing.Point(3, 16);
         this.lvDocs.Name = "lvDocs";
         this.lvDocs.Size = new System.Drawing.Size(275, 374);
         this.lvDocs.SmallImageList = this.imageList1;
         this.lvDocs.TabIndex = 0;
         this.lvDocs.UseCompatibleStateImageBehavior = false;
         this.lvDocs.View = System.Windows.Forms.View.List;
         this.lvDocs.ItemChecked += new System.Windows.Forms.ItemCheckedEventHandler(this.lvDocs_ItemChecked);
         // 
         // imageList1
         // 
         this.imageList1.ColorDepth = System.Windows.Forms.ColorDepth.Depth8Bit;
         this.imageList1.ImageSize = new System.Drawing.Size(16, 16);
         this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
         // 
         // toolStrip1
         // 
         this.toolStrip1.AutoSize = false;
         this.toolStrip1.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSave,
            this.lblInfo,
            this.cbOrgType});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(617, 49);
         this.toolStrip1.TabIndex = 3;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(36, 46);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // lblInfo
         // 
         this.lblInfo.Name = "lblInfo";
         this.lblInfo.Size = new System.Drawing.Size(0, 46);
         // 
         // groupBox2
         // 
         this.groupBox2.Controls.Add(this.lvDocsAvail);
         this.groupBox2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.groupBox2.ForeColor = System.Drawing.Color.Blue;
         this.groupBox2.Location = new System.Drawing.Point(0, 0);
         this.groupBox2.Name = "groupBox2";
         this.groupBox2.Size = new System.Drawing.Size(300, 393);
         this.groupBox2.TabIndex = 4;
         this.groupBox2.TabStop = false;
         this.groupBox2.Text = "Доступные документы";
         // 
         // lvDocsAvail
         // 
         this.lvDocsAvail.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lvDocsAvail.HideSelection = false;
         this.lvDocsAvail.LargeImageList = this.imageList1;
         this.lvDocsAvail.Location = new System.Drawing.Point(3, 16);
         this.lvDocsAvail.Name = "lvDocsAvail";
         this.lvDocsAvail.Size = new System.Drawing.Size(294, 374);
         this.lvDocsAvail.SmallImageList = this.imageList1;
         this.lvDocsAvail.TabIndex = 0;
         this.lvDocsAvail.UseCompatibleStateImageBehavior = false;
         this.lvDocsAvail.View = System.Windows.Forms.View.List;
         // 
         // btnAdd
         // 
         this.btnAdd.Location = new System.Drawing.Point(3, 36);
         this.btnAdd.Name = "btnAdd";
         this.btnAdd.Size = new System.Drawing.Size(28, 23);
         this.btnAdd.TabIndex = 5;
         this.btnAdd.Text = "<<";
         this.btnAdd.UseVisualStyleBackColor = true;
         this.btnAdd.Click += new System.EventHandler(this.btnAdd_Click);
         // 
         // btnDel
         // 
         this.btnDel.Location = new System.Drawing.Point(3, 65);
         this.btnDel.Name = "btnDel";
         this.btnDel.Size = new System.Drawing.Size(28, 23);
         this.btnDel.TabIndex = 6;
         this.btnDel.Text = ">>";
         this.btnDel.UseVisualStyleBackColor = true;
         this.btnDel.Click += new System.EventHandler(this.btnDel_Click);
         // 
         // btnUp
         // 
         this.btnUp.Image = ((System.Drawing.Image)(resources.GetObject("btnUp.Image")));
         this.btnUp.Location = new System.Drawing.Point(3, 118);
         this.btnUp.Name = "btnUp";
         this.btnUp.Size = new System.Drawing.Size(28, 29);
         this.btnUp.TabIndex = 7;
         this.btnUp.UseVisualStyleBackColor = true;
         this.btnUp.Click += new System.EventHandler(this.btnUp_Click);
         // 
         // btnDown
         // 
         this.btnDown.Image = ((System.Drawing.Image)(resources.GetObject("btnDown.Image")));
         this.btnDown.Location = new System.Drawing.Point(2, 153);
         this.btnDown.Name = "btnDown";
         this.btnDown.Size = new System.Drawing.Size(28, 29);
         this.btnDown.TabIndex = 8;
         this.btnDown.UseVisualStyleBackColor = true;
         this.btnDown.Click += new System.EventHandler(this.btnDown_Click);
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.Label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 49);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(617, 36);
         this.panel1.TabIndex = 9;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 85);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.groupBox1);
         this.splitContainer1.Panel1.Controls.Add(this.panel2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.groupBox2);
         this.splitContainer1.Size = new System.Drawing.Size(617, 393);
         this.splitContainer1.SplitterDistance = 313;
         this.splitContainer1.TabIndex = 10;
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.btnAdd);
         this.panel2.Controls.Add(this.btnDel);
         this.panel2.Controls.Add(this.btnDown);
         this.panel2.Controls.Add(this.btnUp);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Right;
         this.panel2.Location = new System.Drawing.Point(281, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(32, 393);
         this.panel2.TabIndex = 0;
         // 
         // cbOrgType
         // 
         this.cbOrgType.Font = new System.Drawing.Font("Segoe UI", 11F);
         this.cbOrgType.Items.AddRange(new object[] {
            "сценария для обычных точек",
            "сценарий для потеницальных точек",
            "сценария для всех типов точек"});
         this.cbOrgType.Name = "cbOrgType";
         this.cbOrgType.Size = new System.Drawing.Size(300, 49);
         // 
         // FmScriptEdit
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(617, 478);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmScriptEdit";
         this.Text = "Сценарий";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmScriptEdit_FormClosing);
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmScriptEdit_FormClosed);
         this.Load += new System.EventHandler(this.FmScriptEdit_Load);
         this.groupBox1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.groupBox2.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Label Label1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.GroupBox groupBox2;
      private System.Windows.Forms.Button btnDel;
      protected System.Windows.Forms.ListView lvDocs;
      private System.Windows.Forms.Button btnUp;
      private System.Windows.Forms.Button btnDown;
      public System.Windows.Forms.ImageList imageList1;
      public System.Windows.Forms.ListView lvDocsAvail;
      protected System.Windows.Forms.Button btnAdd;
      protected System.Windows.Forms.TextBox tbName;
      protected System.Windows.Forms.Panel panel1;
      protected System.Windows.Forms.ToolStripButton btnSave;
      protected System.Windows.Forms.SplitContainer splitContainer1;
      protected System.Windows.Forms.Panel panel2;
      protected System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.ToolStripLabel lblInfo;
      private System.Windows.Forms.ToolStripComboBox cbOrgType;
   }
}
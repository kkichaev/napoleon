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
         this.tvDivisions = new System.Windows.Forms.TreeView();
         this.images = new System.Windows.Forms.ImageList(this.components);
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindClear = new System.Windows.Forms.ToolStripButton();
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
         this.tsbOrgRadiusDocs = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.connectStatus = new System.Windows.Forms.ToolStripStatusLabel();
         this.timer1 = new System.Windows.Forms.Timer(this.components);
         this.btnPriceRemnants = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.tb.SuspendLayout();
         this.statusStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 39);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvDivisions);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         this.splitContainer1.Panel1.Padding = new System.Windows.Forms.Padding(7, 7, 0, 7);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(0, 0, 0, 5);
         this.splitContainer1.Size = new System.Drawing.Size(666, 369);
         this.splitContainer1.SplitterDistance = 222;
         this.splitContainer1.TabIndex = 0;
         // 
         // tvDivisions
         // 
         this.tvDivisions.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvDivisions.ImageIndex = 0;
         this.tvDivisions.ImageList = this.images;
         this.tvDivisions.Location = new System.Drawing.Point(7, 7);
         this.tvDivisions.Name = "tvDivisions";
         this.tvDivisions.SelectedImageIndex = 0;
         this.tvDivisions.Size = new System.Drawing.Size(215, 330);
         this.tvDivisions.TabIndex = 0;
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
         // toolStrip1
         // 
         this.toolStrip1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tbFind,
            this.btnFindClear});
         this.toolStrip1.Location = new System.Drawing.Point(7, 337);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(215, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         // 
         // btnFindClear
         // 
         this.btnFindClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindClear.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_clear_4;
         this.btnFindClear.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindClear.Name = "btnFindClear";
         this.btnFindClear.Size = new System.Drawing.Size(23, 22);
         this.btnFindClear.Text = "Очистить поиск";
         // 
         // tb
         // 
         this.tb.AutoSize = false;
         this.tb.Font = new System.Drawing.Font("Arial", 12F);
         this.tb.ImageScalingSize = new System.Drawing.Size(32, 32);
         this.tb.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.newButton,
            this.delButton,
            this.saveButton,
            this.tsbMatrixDesigner,
            this.setColor,
            this.tbSep1,
            this.tbCoef,
            this.tbSep2,
            this.btnScriptDesigner,
            this.tsbOrgRadiusDocs,
            this.btnPriceRemnants});
         this.tb.Location = new System.Drawing.Point(0, 0);
         this.tb.Name = "tb";
         this.tb.Size = new System.Drawing.Size(666, 39);
         this.tb.TabIndex = 1;
         this.tb.Text = "Коэф. автозаказа";
         // 
         // newButton
         // 
         this.newButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.newButton.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miAddDivision,
            this.miAddAgent});
         this.newButton.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.newButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.newButton.Name = "newButton";
         this.newButton.Size = new System.Drawing.Size(45, 36);
         this.newButton.Text = "Создать";
         // 
         // miAddDivision
         // 
         this.miAddDivision.Name = "miAddDivision";
         this.miAddDivision.Size = new System.Drawing.Size(261, 22);
         this.miAddDivision.Text = "Добавить подразделение";
         // 
         // miAddAgent
         // 
         this.miAddAgent.Name = "miAddAgent";
         this.miAddAgent.Size = new System.Drawing.Size(261, 22);
         this.miAddAgent.Text = "Добавить агента";
         // 
         // delButton
         // 
         this.delButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.delButton.Image = global::GRSoft.NapoleonManager.Properties.Resources.delete;
         this.delButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.delButton.Name = "delButton";
         this.delButton.Size = new System.Drawing.Size(36, 36);
         this.delButton.Text = "Удалить";
         // 
         // saveButton
         // 
         this.saveButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.saveButton.Enabled = false;
         this.saveButton.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save1;
         this.saveButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.saveButton.Name = "saveButton";
         this.saveButton.Size = new System.Drawing.Size(36, 36);
         this.saveButton.Text = "Сохранить";
         // 
         // tsbMatrixDesigner
         // 
         this.tsbMatrixDesigner.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMatrixDesigner.Image = global::GRSoft.NapoleonManager.Properties.Resources.kmenuedit;
         this.tsbMatrixDesigner.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMatrixDesigner.Name = "tsbMatrixDesigner";
         this.tsbMatrixDesigner.Size = new System.Drawing.Size(36, 36);
         this.tsbMatrixDesigner.Text = "Редактор матриц";
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
         this.setColor.Size = new System.Drawing.Size(137, 36);
         this.setColor.Text = "Назначить цвет";
         // 
         // orgsSetColor
         // 
         this.orgsSetColor.Name = "orgsSetColor";
         this.orgsSetColor.Size = new System.Drawing.Size(200, 22);
         this.orgsSetColor.Text = "Контрагенты";
         // 
         // priceSetColor
         // 
         this.priceSetColor.Name = "priceSetColor";
         this.priceSetColor.Size = new System.Drawing.Size(200, 22);
         this.priceSetColor.Text = "Прайс-лист";
         // 
         // tsEditColor
         // 
         this.tsEditColor.Name = "tsEditColor";
         this.tsEditColor.Size = new System.Drawing.Size(200, 22);
         this.tsEditColor.Text = "Редактор цветов";
         // 
         // tbSep1
         // 
         this.tbSep1.Name = "tbSep1";
         this.tbSep1.Size = new System.Drawing.Size(6, 39);
         // 
         // tbCoef
         // 
         this.tbCoef.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.tbCoef.Image = ((System.Drawing.Image)(resources.GetObject("tbCoef.Image")));
         this.tbCoef.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tbCoef.Name = "tbCoef";
         this.tbCoef.Size = new System.Drawing.Size(137, 36);
         this.tbCoef.Text = "Коэф.автозаказа";
         // 
         // tbSep2
         // 
         this.tbSep2.Name = "tbSep2";
         this.tbSep2.Size = new System.Drawing.Size(6, 39);
         // 
         // btnScriptDesigner
         // 
         this.btnScriptDesigner.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnScriptDesigner.Image = global::GRSoft.NapoleonManager.Properties.Resources.script_doc;
         this.btnScriptDesigner.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnScriptDesigner.Name = "btnScriptDesigner";
         this.btnScriptDesigner.Size = new System.Drawing.Size(36, 36);
         this.btnScriptDesigner.Text = "Редактор сценариев";
         this.btnScriptDesigner.Visible = false;
         // 
         // tsbOrgRadiusDocs
         // 
         this.tsbOrgRadiusDocs.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOrgRadiusDocs.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_top_5;
         this.tsbOrgRadiusDocs.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbOrgRadiusDocs.Name = "tsbOrgRadiusDocs";
         this.tsbOrgRadiusDocs.Size = new System.Drawing.Size(36, 36);
         this.tsbOrgRadiusDocs.Text = "Документы для контроля координат";
         this.tsbOrgRadiusDocs.Visible = false;
         // 
         // statusStrip1
         // 
         this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.connectStatus});
         this.statusStrip1.Location = new System.Drawing.Point(0, 408);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(666, 22);
         this.statusStrip1.TabIndex = 2;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // connectStatus
         // 
         this.connectStatus.Name = "connectStatus";
         this.connectStatus.Size = new System.Drawing.Size(0, 17);
         // 
         // btnPriceRemnants
         // 
         this.btnPriceRemnants.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnPriceRemnants.Image = ((System.Drawing.Image)(resources.GetObject("btnPriceRemnants.Image")));
         this.btnPriceRemnants.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnPriceRemnants.Name = "btnPriceRemnants";
         this.btnPriceRemnants.Size = new System.Drawing.Size(36, 36);
         this.btnPriceRemnants.Text = "Редактор презентации";
         this.btnPriceRemnants.Click += new System.EventHandler(this.btnPriceRemnants_Click);
         // 
         // Divisions
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(666, 430);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.tb);
         this.Controls.Add(this.statusStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "Divisions";
         this.Text = "Управление командой";
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
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
      private System.Windows.Forms.TreeView tvDivisions;
      public System.Windows.Forms.ToolStrip tb;
      private System.Windows.Forms.ToolStripButton delButton;
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
      private System.Windows.Forms.ToolStripMenuItem orgsSetColor;
      private System.Windows.Forms.ToolStripMenuItem priceSetColor;
      private System.Windows.Forms.ToolStripMenuItem tsEditColor;
      private System.Windows.Forms.ToolStripButton btnScriptDesigner;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindClear;
      protected System.Windows.Forms.Timer timer1;
      protected System.Windows.Forms.ToolStripButton tsbOrgRadiusDocs;
      public System.Windows.Forms.ToolStripButton saveButton;
      public System.Windows.Forms.ToolStripSplitButton setColor;
      public System.Windows.Forms.ToolStripButton btnPriceRemnants;
   }
}
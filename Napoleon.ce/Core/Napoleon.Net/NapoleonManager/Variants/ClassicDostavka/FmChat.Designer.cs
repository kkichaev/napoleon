namespace GRSoft.NapoleonManager
{
   partial class FmChat
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmChat));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.lbGroup = new System.Windows.Forms.ListBox();
         this.wb = new System.Windows.Forms.WebBrowser();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnNewGroup = new System.Windows.Forms.ToolStripButton();
         this.btnDelGroup = new System.Windows.Forms.ToolStripButton();
         this.btnEditGroup = new System.Windows.Forms.ToolStripButton();
         this.btnSetting = new System.Windows.Forms.ToolStripButton();
         this.panel2 = new System.Windows.Forms.Panel();
         this.tbText = new System.Windows.Forms.TextBox();
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnSend = new System.Windows.Forms.Button();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         this.splitContainer1.Panel1.Controls.Add(this.toolStrip1);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.panel2);
         this.splitContainer1.Panel2.Controls.Add(this.panel1);
         this.splitContainer1.Size = new System.Drawing.Size(753, 555);
         this.splitContainer1.SplitterDistance = 372;
         this.splitContainer1.TabIndex = 0;
         // 
         // splitContainer2
         // 
         this.splitContainer2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer2.Location = new System.Drawing.Point(0, 25);
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.lbGroup);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.Controls.Add(this.wb);
         this.splitContainer2.Size = new System.Drawing.Size(753, 347);
         this.splitContainer2.SplitterDistance = 215;
         this.splitContainer2.TabIndex = 1;
         // 
         // lbGroup
         // 
         this.lbGroup.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lbGroup.FormattingEnabled = true;
         this.lbGroup.ItemHeight = 14;
         this.lbGroup.Location = new System.Drawing.Point(0, 0);
         this.lbGroup.Name = "lbGroup";
         this.lbGroup.Size = new System.Drawing.Size(215, 347);
         this.lbGroup.TabIndex = 0;
         this.lbGroup.SelectedIndexChanged += new System.EventHandler(this.lbGroup_SelectedIndexChanged);
         // 
         // wb
         // 
         this.wb.Dock = System.Windows.Forms.DockStyle.Fill;
         this.wb.Location = new System.Drawing.Point(0, 0);
         this.wb.MinimumSize = new System.Drawing.Size(20, 20);
         this.wb.Name = "wb";
         this.wb.Size = new System.Drawing.Size(534, 347);
         this.wb.TabIndex = 0;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnNewGroup,
            this.btnDelGroup,
            this.btnEditGroup,
            this.btnSetting});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(753, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnNewGroup
         // 
         this.btnNewGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnNewGroup.Image = global::GRSoft.NapoleonManager.Properties.Resources.add;
         this.btnNewGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnNewGroup.Name = "btnNewGroup";
         this.btnNewGroup.Size = new System.Drawing.Size(23, 22);
         this.btnNewGroup.Text = "Новая группа";
         this.btnNewGroup.Click += new System.EventHandler(this.btnNewGroup_Click);
         // 
         // btnDelGroup
         // 
         this.btnDelGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnDelGroup.Image = global::GRSoft.NapoleonManager.Properties.Resources.del;
         this.btnDelGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnDelGroup.Name = "btnDelGroup";
         this.btnDelGroup.Size = new System.Drawing.Size(23, 22);
         this.btnDelGroup.Text = "Удалить группу";
         this.btnDelGroup.Click += new System.EventHandler(this.btnDelGroup_Click);
         // 
         // btnEditGroup
         // 
         this.btnEditGroup.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnEditGroup.Image = global::GRSoft.NapoleonManager.Properties.Resources.document_sign;
         this.btnEditGroup.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnEditGroup.Name = "btnEditGroup";
         this.btnEditGroup.Size = new System.Drawing.Size(23, 22);
         this.btnEditGroup.Text = "Изменить группу";
         this.btnEditGroup.Click += new System.EventHandler(this.btnEditGroup_Click);
         // 
         // btnSetting
         // 
         this.btnSetting.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.btnSetting.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSetting.Image = global::GRSoft.NapoleonManager.Properties.Resources.preferences;
         this.btnSetting.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSetting.Name = "btnSetting";
         this.btnSetting.Size = new System.Drawing.Size(23, 22);
         this.btnSetting.Text = "Настройки";
         this.btnSetting.Click += new System.EventHandler(this.btnSetting_Click);
         // 
         // panel2
         // 
         this.panel2.Controls.Add(this.tbText);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(611, 179);
         this.panel2.TabIndex = 1;
         // 
         // tbText
         // 
         this.tbText.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbText.Location = new System.Drawing.Point(0, 0);
         this.tbText.Multiline = true;
         this.tbText.Name = "tbText";
         this.tbText.Size = new System.Drawing.Size(611, 179);
         this.tbText.TabIndex = 0;
         this.tbText.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbText_KeyDown);
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnSend);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Right;
         this.panel1.Location = new System.Drawing.Point(611, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(142, 179);
         this.panel1.TabIndex = 0;
         // 
         // btnSend
         // 
         this.btnSend.Location = new System.Drawing.Point(26, 68);
         this.btnSend.Name = "btnSend";
         this.btnSend.Size = new System.Drawing.Size(89, 49);
         this.btnSend.TabIndex = 0;
         this.btnSend.Text = "Отправить";
         this.btnSend.UseVisualStyleBackColor = true;
         this.btnSend.Click += new System.EventHandler(this.btnSend_Click);
         // 
         // FmChat
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(753, 555);
         this.Controls.Add(this.splitContainer1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmChat";
         this.Text = "Чат";
         this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FmChat_FormClosed);
         this.Load += new System.EventHandler(this.FmChat_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.panel1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.TextBox tbText;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnSend;
      private System.Windows.Forms.WebBrowser wb;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnNewGroup;
      private System.Windows.Forms.ToolStripButton btnEditGroup;
      private System.Windows.Forms.ListBox lbGroup;
      private System.Windows.Forms.ToolStripButton btnDelGroup;
      private System.Windows.Forms.ToolStripButton btnSetting;
   }
}
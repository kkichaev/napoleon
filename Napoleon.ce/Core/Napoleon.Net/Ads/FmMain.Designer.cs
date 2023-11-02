namespace GRSoft.NapoleonManager
{
   partial class FmMain
   {
      /// <summary>
      /// Требуется переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary>
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором форм Windows

      /// <summary>
      /// Обязательный метод для поддержки конструктора - не изменяйте
      /// содержимое данного метода при помощи редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMain));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.splitContainer3 = new System.Windows.Forms.SplitContainer();
         this.calendar = new System.Windows.Forms.MonthCalendar();
         this.tvUsers = new System.Windows.Forms.TreeView();
         this.ilUsers = new System.Windows.Forms.ImageList(this.components);
         this.btnReport = new System.Windows.Forms.Button();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btJournal = new System.Windows.Forms.ToolStripButton();
         this.btnUserLocation_ = new System.Windows.Forms.ToolStripButton();
         this.menuStrip = new System.Windows.Forms.MenuStrip();
         this.miFile = new System.Windows.Forms.ToolStripMenuItem();
         this.miSetting = new System.Windows.Forms.ToolStripMenuItem();
         this.miUsers = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
         this.miClose = new System.Windows.Forms.ToolStripMenuItem();
         this.miEdit = new System.Windows.Forms.ToolStripMenuItem();
         this.miCut = new System.Windows.Forms.ToolStripMenuItem();
         this.miCopy = new System.Windows.Forms.ToolStripMenuItem();
         this.miPast = new System.Windows.Forms.ToolStripMenuItem();
         this.отчетыToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
         this.miOrder = new System.Windows.Forms.ToolStripMenuItem();
         this.miJournal = new System.Windows.Forms.ToolStripMenuItem();
         this.miDistance = new System.Windows.Forms.ToolStripMenuItem();
         this.miRoute = new System.Windows.Forms.ToolStripMenuItem();
         this.miHelp = new System.Windows.Forms.ToolStripMenuItem();
         this.miAbout = new System.Windows.Forms.ToolStripMenuItem();
         this.miWiki = new System.Windows.Forms.ToolStripMenuItem();
         this.timeGrid = new GRSoft.NapoleonManager.TimeGridControl();
         this.itemContextMenuStrip = new GRSoft.NapoleonManager.TaskContextMenuStrip();
         this.cmiDel = new System.Windows.Forms.ToolStripMenuItem();
         this.cmiCut = new System.Windows.Forms.ToolStripMenuItem();
         this.cmiCopy = new System.Windows.Forms.ToolStripMenuItem();
         this.cmiPast = new System.Windows.Forms.ToolStripMenuItem();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.splitContainer3.Panel1.SuspendLayout();
         this.splitContainer3.Panel2.SuspendLayout();
         this.splitContainer3.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.menuStrip.SuspendLayout();
         this.itemContextMenuStrip.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         resources.ApplyResources(this.splitContainer1, "splitContainer1");
         this.splitContainer1.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.splitContainer2);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.timeGrid);
         // 
         // splitContainer2
         // 
         this.splitContainer2.BackColor = System.Drawing.SystemColors.Control;
         this.splitContainer2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         resources.ApplyResources(this.splitContainer2, "splitContainer2");
         this.splitContainer2.Name = "splitContainer2";
         // 
         // splitContainer2.Panel1
         // 
         this.splitContainer2.Panel1.Controls.Add(this.splitContainer3);
         // 
         // splitContainer2.Panel2
         // 
         this.splitContainer2.Panel2.BackColor = System.Drawing.SystemColors.Window;
         this.splitContainer2.Panel2.Controls.Add(this.btnReport);
         this.splitContainer2.Panel2Collapsed = true;
         // 
         // splitContainer3
         // 
         resources.ApplyResources(this.splitContainer3, "splitContainer3");
         this.splitContainer3.Name = "splitContainer3";
         // 
         // splitContainer3.Panel1
         // 
         this.splitContainer3.Panel1.BackColor = System.Drawing.SystemColors.Window;
         this.splitContainer3.Panel1.Controls.Add(this.calendar);
         // 
         // splitContainer3.Panel2
         // 
         this.splitContainer3.Panel2.Controls.Add(this.tvUsers);
         // 
         // calendar
         // 
         resources.ApplyResources(this.calendar, "calendar");
         this.calendar.Name = "calendar";
         // 
         // tvUsers
         // 
         resources.ApplyResources(this.tvUsers, "tvUsers");
         this.tvUsers.HideSelection = false;
         this.tvUsers.ImageList = this.ilUsers;
         this.tvUsers.Name = "tvUsers";
         this.tvUsers.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.tvUsers_AfterSelect);
         this.tvUsers.MouseDown += new System.Windows.Forms.MouseEventHandler(this.tvUsers_MouseDown);
         // 
         // ilUsers
         // 
         this.ilUsers.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("ilUsers.ImageStream")));
         this.ilUsers.TransparentColor = System.Drawing.Color.Transparent;
         this.ilUsers.Images.SetKeyName(0, "family.png");
         this.ilUsers.Images.SetKeyName(1, "edit-user.png");
         // 
         // btnReport
         // 
         this.btnReport.BackColor = System.Drawing.Color.WhiteSmoke;
         resources.ApplyResources(this.btnReport, "btnReport");
         this.btnReport.Image = global::GRSoft.NapoleonManager.Properties.Resources.kworldclock;
         this.btnReport.Name = "btnReport";
         this.btnReport.UseVisualStyleBackColor = false;
         this.btnReport.Click += new System.EventHandler(this.btnMode_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.toolStripSeparator1,
            this.btJournal,
            this.btnUserLocation_});
         resources.ApplyResources(this.toolStrip1, "toolStrip1");
         this.toolStrip1.Name = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         resources.ApplyResources(this.btnRefresh, "btnRefresh");
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         resources.ApplyResources(this.toolStripSeparator1, "toolStripSeparator1");
         // 
         // btJournal
         // 
         this.btJournal.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btJournal.Image = global::GRSoft.NapoleonManager.Properties.Resources.kmenuedit;
         resources.ApplyResources(this.btJournal, "btJournal");
         this.btJournal.Name = "btJournal";
         this.btJournal.Click += new System.EventHandler(this.btJournal_Click);
         // 
         // btnUserLocation_
         // 
         this.btnUserLocation_.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUserLocation_.Image = global::GRSoft.NapoleonManager.Properties.Resources.edit_find_user;
         resources.ApplyResources(this.btnUserLocation_, "btnUserLocation_");
         this.btnUserLocation_.Name = "btnUserLocation_";
         this.btnUserLocation_.Click += new System.EventHandler(this.btnUserLocation_Click);
         // 
         // menuStrip
         // 
         this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miFile,
            this.miEdit,
            this.отчетыToolStripMenuItem,
            this.miHelp});
         resources.ApplyResources(this.menuStrip, "menuStrip");
         this.menuStrip.Name = "menuStrip";
         // 
         // miFile
         // 
         this.miFile.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miSetting,
            this.miUsers,
            this.toolStripMenuItem1,
            this.miClose});
         this.miFile.Name = "miFile";
         resources.ApplyResources(this.miFile, "miFile");
         // 
         // miSetting
         // 
         this.miSetting.Name = "miSetting";
         resources.ApplyResources(this.miSetting, "miSetting");
         this.miSetting.Click += new System.EventHandler(this.miSetting_Click);
         // 
         // miUsers
         // 
         this.miUsers.Name = "miUsers";
         resources.ApplyResources(this.miUsers, "miUsers");
         this.miUsers.Click += new System.EventHandler(this.miUsers_Click);
         // 
         // toolStripMenuItem1
         // 
         this.toolStripMenuItem1.Name = "toolStripMenuItem1";
         resources.ApplyResources(this.toolStripMenuItem1, "toolStripMenuItem1");
         // 
         // miClose
         // 
         this.miClose.Name = "miClose";
         resources.ApplyResources(this.miClose, "miClose");
         this.miClose.Click += new System.EventHandler(this.miClose_Click);
         // 
         // miEdit
         // 
         this.miEdit.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miCut,
            this.miCopy,
            this.miPast});
         this.miEdit.Name = "miEdit";
         resources.ApplyResources(this.miEdit, "miEdit");
         this.miEdit.DropDownOpening += new System.EventHandler(this.miEdit_DropDownOpening);
         // 
         // miCut
         // 
         this.miCut.Name = "miCut";
         resources.ApplyResources(this.miCut, "miCut");
         this.miCut.Click += new System.EventHandler(this.miCut_Click);
         // 
         // miCopy
         // 
         this.miCopy.Name = "miCopy";
         resources.ApplyResources(this.miCopy, "miCopy");
         this.miCopy.Click += new System.EventHandler(this.miCopy_Click);
         // 
         // miPast
         // 
         this.miPast.Name = "miPast";
         resources.ApplyResources(this.miPast, "miPast");
         this.miPast.Click += new System.EventHandler(this.miPast_Click);
         // 
         // отчетыToolStripMenuItem
         // 
         this.отчетыToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miOrder,
            this.miJournal,
            this.miDistance,
            this.miRoute});
         this.отчетыToolStripMenuItem.Name = "отчетыToolStripMenuItem";
         resources.ApplyResources(this.отчетыToolStripMenuItem, "отчетыToolStripMenuItem");
         // 
         // miOrder
         // 
         this.miOrder.Name = "miOrder";
         resources.ApplyResources(this.miOrder, "miOrder");
         this.miOrder.Click += new System.EventHandler(this.miOrder_Click);
         // 
         // miJournal
         // 
         this.miJournal.Name = "miJournal";
         resources.ApplyResources(this.miJournal, "miJournal");
         this.miJournal.Click += new System.EventHandler(this.miJournal_Click);
         // 
         // miDistance
         // 
         this.miDistance.Name = "miDistance";
         resources.ApplyResources(this.miDistance, "miDistance");
         this.miDistance.Click += new System.EventHandler(this.miDistance_Click);
         // 
         // miRoute
         // 
         this.miRoute.Name = "miRoute";
         resources.ApplyResources(this.miRoute, "miRoute");
         this.miRoute.Click += new System.EventHandler(this.miRoute_Click);
         // 
         // miHelp
         // 
         this.miHelp.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miAbout,
            this.miWiki});
         this.miHelp.Name = "miHelp";
         resources.ApplyResources(this.miHelp, "miHelp");
         // 
         // miAbout
         // 
         this.miAbout.Name = "miAbout";
         resources.ApplyResources(this.miAbout, "miAbout");
         this.miAbout.Click += new System.EventHandler(this.miAbout_Click);
         // 
         // miWiki
         // 
         this.miWiki.Name = "miWiki";
         resources.ApplyResources(this.miWiki, "miWiki");
         this.miWiki.Click += new System.EventHandler(this.miWiki_Click);
         // 
         // timeGrid
         // 
         resources.ApplyResources(this.timeGrid, "timeGrid");
         this.timeGrid.ItemContextMenuStrip = this.itemContextMenuStrip;
         this.timeGrid.Name = "timeGrid";
         // 
         // itemContextMenuStrip
         // 
         this.itemContextMenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.cmiDel,
            this.cmiCut,
            this.cmiCopy,
            this.cmiPast});
         this.itemContextMenuStrip.Name = "taskContextMenuStrip1";
         resources.ApplyResources(this.itemContextMenuStrip, "itemContextMenuStrip");
         this.itemContextMenuStrip.Task = null;
         this.itemContextMenuStrip.Opening += new System.ComponentModel.CancelEventHandler(this.itemContextMenuStrip_Opening);
         // 
         // cmiDel
         // 
         this.cmiDel.Name = "cmiDel";
         resources.ApplyResources(this.cmiDel, "cmiDel");
         this.cmiDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // cmiCut
         // 
         this.cmiCut.Name = "cmiCut";
         resources.ApplyResources(this.cmiCut, "cmiCut");
         this.cmiCut.Click += new System.EventHandler(this.miCut_Click);
         // 
         // cmiCopy
         // 
         this.cmiCopy.Name = "cmiCopy";
         resources.ApplyResources(this.cmiCopy, "cmiCopy");
         this.cmiCopy.Click += new System.EventHandler(this.miCopy_Click);
         // 
         // cmiPast
         // 
         this.cmiPast.Name = "cmiPast";
         resources.ApplyResources(this.cmiPast, "cmiPast");
         this.cmiPast.Click += new System.EventHandler(this.miPast_Click);
         // 
         // FmMain
         // 
         resources.ApplyResources(this, "$this");
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.menuStrip);
         this.MainMenuStrip = this.menuStrip;
         this.Name = "FmMain";
         this.Load += new System.EventHandler(this.Main_Load);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.ResumeLayout(false);
         this.splitContainer2.Panel1.ResumeLayout(false);
         this.splitContainer2.Panel2.ResumeLayout(false);
         this.splitContainer2.ResumeLayout(false);
         this.splitContainer3.Panel1.ResumeLayout(false);
         this.splitContainer3.Panel2.ResumeLayout(false);
         this.splitContainer3.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.menuStrip.ResumeLayout(false);
         this.menuStrip.PerformLayout();
         this.itemContextMenuStrip.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.MenuStrip menuStrip;
      private System.Windows.Forms.ToolStripMenuItem miFile;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.SplitContainer splitContainer3;
      private System.Windows.Forms.MonthCalendar calendar;
      private System.Windows.Forms.ToolStripMenuItem miEdit;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.ImageList ilUsers;
      private System.Windows.Forms.ToolStripMenuItem miSetting;
      private System.Windows.Forms.ToolStripMenuItem miClose;
      private GRSoft.NapoleonManager.TaskContextMenuStrip itemContextMenuStrip;
      private System.Windows.Forms.ToolStripMenuItem cmiDel;
      private System.Windows.Forms.ToolStripMenuItem miHelp;
      private System.Windows.Forms.ToolStripMenuItem miAbout;
      private System.Windows.Forms.ToolStripMenuItem miWiki;
      private System.Windows.Forms.ToolStripMenuItem miCut;
      private System.Windows.Forms.ToolStripMenuItem miCopy;
      private System.Windows.Forms.ToolStripMenuItem miPast;
      private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
      private TimeGridControl timeGrid;
      private System.Windows.Forms.ToolStripButton btJournal;
      private System.Windows.Forms.ToolStripMenuItem отчетыToolStripMenuItem;
      private System.Windows.Forms.ToolStripMenuItem miOrder;
      private System.Windows.Forms.ToolStripMenuItem miJournal;
      private System.Windows.Forms.ToolStripMenuItem miDistance;
      private System.Windows.Forms.ToolStripMenuItem miRoute;
      private System.Windows.Forms.TreeView tvUsers;
      private System.Windows.Forms.ToolStripMenuItem miUsers;
      private System.Windows.Forms.ToolStripButton btnUserLocation_;
      private System.Windows.Forms.ToolStripMenuItem cmiCut;
      private System.Windows.Forms.ToolStripMenuItem cmiCopy;
      private System.Windows.Forms.ToolStripMenuItem cmiPast;
   }
}


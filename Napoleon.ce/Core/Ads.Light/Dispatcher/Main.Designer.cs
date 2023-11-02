namespace GRSoft.Ads.Dispatcher
{
   partial class Main
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Main));
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.splitContainer2 = new System.Windows.Forms.SplitContainer();
         this.splitContainer3 = new System.Windows.Forms.SplitContainer();
         this.calendar = new System.Windows.Forms.MonthCalendar();
         this.tvUsers = new System.Windows.Forms.TreeView();
         this.ilUsers = new System.Windows.Forms.ImageList(this.components);
         this.btnReport = new System.Windows.Forms.Button();
         this.btnTask = new System.Windows.Forms.Button();
         this.timeGrid = new GRSoft.Ads.Dispatcher.TimeGrid();
         this.taskContextMenuStrip1 = new GRSoft.Ads.Dispatcher.TaskContextMenuStrip();
         this.miDel = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnUser = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.btnQuest = new System.Windows.Forms.ToolStripButton();
         this.btnTaskReport = new System.Windows.Forms.ToolStripButton();
         this.menuStrip = new System.Windows.Forms.MenuStrip();
         this.miFile = new System.Windows.Forms.ToolStripMenuItem();
         this.miSetting = new System.Windows.Forms.ToolStripMenuItem();
         this.miClose = new System.Windows.Forms.ToolStripMenuItem();
         this.miEdit = new System.Windows.Forms.ToolStripMenuItem();
         this.miCut = new System.Windows.Forms.ToolStripMenuItem();
         this.miCopy = new System.Windows.Forms.ToolStripMenuItem();
         this.miPast = new System.Windows.Forms.ToolStripMenuItem();
         this.miVid = new System.Windows.Forms.ToolStripMenuItem();
         this.miTask = new System.Windows.Forms.ToolStripMenuItem();
         this.miHelp = new System.Windows.Forms.ToolStripMenuItem();
         this.miAbout = new System.Windows.Forms.ToolStripMenuItem();
         this.miWiki = new System.Windows.Forms.ToolStripMenuItem();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.splitContainer2.Panel1.SuspendLayout();
         this.splitContainer2.Panel2.SuspendLayout();
         this.splitContainer2.SuspendLayout();
         this.splitContainer3.Panel1.SuspendLayout();
         this.splitContainer3.Panel2.SuspendLayout();
         this.splitContainer3.SuspendLayout();
         this.taskContextMenuStrip1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.menuStrip.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         resources.ApplyResources(this.splitContainer1, "splitContainer1");
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
         this.splitContainer2.Panel2.Controls.Add(this.btnTask);
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
         this.btnReport.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.kworldclock;
         this.btnReport.Name = "btnReport";
         this.btnReport.UseVisualStyleBackColor = false;
         this.btnReport.Click += new System.EventHandler(this.btnMode_Click);
         // 
         // btnTask
         // 
         this.btnTask.BackColor = System.Drawing.Color.WhiteSmoke;
         resources.ApplyResources(this.btnTask, "btnTask");
         this.btnTask.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.kmenuedit;
         this.btnTask.Name = "btnTask";
         this.btnTask.UseVisualStyleBackColor = false;
         this.btnTask.Click += new System.EventHandler(this.btnMode_Click);
         // 
         // timeGrid
         // 
         resources.ApplyResources(this.timeGrid, "timeGrid");
         this.timeGrid.BackColor = System.Drawing.Color.WhiteSmoke;
         this.timeGrid.BackLostColor = System.Drawing.Color.DarkGray;
         this.timeGrid.DrawedDate = new System.DateTime(2013, 7, 18, 0, 0, 0, 0);
         this.timeGrid.GridColor = System.Drawing.Color.LightSteelBlue;
         this.timeGrid.HourEnd = ((short)(20));
         this.timeGrid.HourStart = ((short)(7));
         this.timeGrid.MinimumSize = new System.Drawing.Size(36, 420);
         this.timeGrid.Name = "timeGrid";
         this.timeGrid.TaskContextMenuStrip = this.taskContextMenuStrip1;
         this.timeGrid.ViewProperty = null;
         this.timeGrid.GridDblClicked += new GRSoft.Ads.Dispatcher.GridEventHandler(this.timeGrid_GridDblClicked);
         // 
         // taskContextMenuStrip1
         // 
         this.taskContextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miDel});
         this.taskContextMenuStrip1.Name = "taskContextMenuStrip1";
         resources.ApplyResources(this.taskContextMenuStrip1, "taskContextMenuStrip1");
         this.taskContextMenuStrip1.Task = null;
         // 
         // miDel
         // 
         this.miDel.Name = "miDel";
         resources.ApplyResources(this.miDel, "miDel");
         this.miDel.Click += new System.EventHandler(this.miDel_Click);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnUser,
            this.toolStripSeparator1,
            this.btnQuest,
            this.btnTaskReport});
         resources.ApplyResources(this.toolStrip1, "toolStrip1");
         this.toolStrip1.Name = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.view_refresh_6;
         resources.ApplyResources(this.btnRefresh, "btnRefresh");
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnUser
         // 
         this.btnUser.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnUser.Image = global::GRSoft.Ads.Dispatcher.Properties.Resources.family;
         resources.ApplyResources(this.btnUser, "btnUser");
         this.btnUser.Name = "btnUser";
         this.btnUser.Click += new System.EventHandler(this.btnUser_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         resources.ApplyResources(this.toolStripSeparator1, "toolStripSeparator1");
         // 
         // btnQuest
         // 
         this.btnQuest.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         resources.ApplyResources(this.btnQuest, "btnQuest");
         this.btnQuest.Name = "btnQuest";
         this.btnQuest.Click += new System.EventHandler(this.btnQuest_Click);
         // 
         // btnTaskReport
         // 
         this.btnTaskReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         resources.ApplyResources(this.btnTaskReport, "btnTaskReport");
         this.btnTaskReport.Name = "btnTaskReport";
         this.btnTaskReport.Click += new System.EventHandler(this.btnTaskReport_Click);
         // 
         // menuStrip
         // 
         this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miFile,
            this.miEdit,
            this.miVid,
            this.miHelp});
         resources.ApplyResources(this.menuStrip, "menuStrip");
         this.menuStrip.Name = "menuStrip";
         // 
         // miFile
         // 
         this.miFile.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miSetting,
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
         // miVid
         // 
         this.miVid.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miTask});
         this.miVid.Name = "miVid";
         resources.ApplyResources(this.miVid, "miVid");
         // 
         // miTask
         // 
         this.miTask.Name = "miTask";
         resources.ApplyResources(this.miTask, "miTask");
         this.miTask.Click += new System.EventHandler(this.miTask_Click);
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
         // Main
         // 
         resources.ApplyResources(this, "$this");
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.menuStrip);
         this.MainMenuStrip = this.menuStrip;
         this.Name = "Main";
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
         this.taskContextMenuStrip1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.menuStrip.ResumeLayout(false);
         this.menuStrip.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private TimeGrid timeGrid;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripButton btnUser;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.MenuStrip menuStrip;
      private System.Windows.Forms.ToolStripMenuItem miFile;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.SplitContainer splitContainer2;
      private System.Windows.Forms.SplitContainer splitContainer3;
      private System.Windows.Forms.MonthCalendar calendar;
      private System.Windows.Forms.TreeView tvUsers;
      private System.Windows.Forms.ToolStripMenuItem miEdit;
      private System.Windows.Forms.Button btnTask;
      private System.Windows.Forms.Button btnReport;
      private System.Windows.Forms.ImageList ilUsers;
      private System.Windows.Forms.ToolStripMenuItem miSetting;
      private System.Windows.Forms.ToolStripMenuItem miClose;
      private GRSoft.Ads.Dispatcher.TaskContextMenuStrip taskContextMenuStrip1;
      private System.Windows.Forms.ToolStripMenuItem miDel;
      private System.Windows.Forms.ToolStripMenuItem miHelp;
      private System.Windows.Forms.ToolStripMenuItem miAbout;
      private System.Windows.Forms.ToolStripMenuItem miWiki;
      private System.Windows.Forms.ToolStripMenuItem miVid;
      private System.Windows.Forms.ToolStripMenuItem miTask;
      private System.Windows.Forms.ToolStripMenuItem miCut;
      private System.Windows.Forms.ToolStripMenuItem miCopy;
      private System.Windows.Forms.ToolStripMenuItem miPast;
      private System.Windows.Forms.ToolStripButton btnQuest;
      private System.Windows.Forms.ToolStripButton btnTaskReport;
   }
}


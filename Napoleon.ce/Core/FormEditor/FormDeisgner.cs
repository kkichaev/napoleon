/*  GREATIS FORM DESIGNER FOR .NET           */
/*  Copyright (C) 2004-2007 Greatis Software */
/*  http://www.greatis.com/dotnet/formdes/   */
/*  http://www.greatis.com/bteam.html        */

using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.Drawing.Design;
using System.ComponentModel.Design;
using System.Xml;
using System.IO;
using Greatis.FormDesigner;
using System.Text;

namespace NFormEditor
{
   /// <summary>
   /// Summary description for Form1.
   /// </summary>
   public class FormDeisgner : System.Windows.Forms.Form
   {

      private Greatis.FormDesigner.Designer designer1;
      private ToolStripPanel BottomToolStripPanel;
      private ToolStripPanel TopToolStripPanel;
      private ToolStripPanel RightToolStripPanel;
      private ToolStripPanel LeftToolStripPanel;
      private MenuStrip menuStrip1;
      private ToolStripContainer toolStripContainer1;
      private PropertyGrid propertyGrid;
      private Splitter splitter1;
      private Greatis.FormDesigner.ToolboxControl toolboxControl;
      private Splitter splitter2;
      private Panel panelDesign;
      private ToolStripMenuItem miOpen;
      private ToolStripMenuItem miSave;
      private ToolStripComboBox miScale;
      private Greatis.FormDesigner.Treasury treasury1;
      private ToolStripMenuItem miRemove;
      private ToolStripMenuItem shiftToolStripMenuItem;
      private string formName;
      private ToolStripButton toolStripButtonMenuBarAlignBottom;
      private ToolStripButton toolStripButtonMenuBarAlignCenterVertical;
      private ToolStripButton toolStripButtonMenuBarAlignTop;
      private ToolStripSeparator toolStripSeparator9;
      private ToolStripButton toolStripButtonMenuBarAlignLeft;
      private ToolStripButton toolStripButtonMenuBarAlignMiddle;
      private ToolStripButton toolStripButtonMenuBarAlignRight;
      private ToolStripButton toolStripSpaceHorizButton;
      private ToolStripButton toolStripSpaceVertButton;
      private ToolStripSeparator toolStripSeparator10;
      private ToolStripButton toolStripButtonMenuBarSameWidth;
      private ToolStripButton toolStripButtonMenuBarSameHeight;
      private ToolStripButton toolStripButtonMenuBarSameBoth;
      private ToolStripMenuItem tsmHeader;

      public static FormDeisgner instance = null;
      bool headerView = false;

      public FormDeisgner()
      {
         //
         // Required for Windows Form Designer support
         //
         InitializeComponent();

         //
         // TODO: Add any constructor code after InitializeComponent call
         //
         Init();
         instance = this;
      }

      /// <summary>
      /// Clean up any resources being used.
      /// </summary>
      protected override void Dispose(bool disposing)
      {
         base.Dispose(disposing);
      }

      #region Windows Form Designer generated code
      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FormDeisgner));
         this.designer1 = new Greatis.FormDesigner.Designer();
         this.treasury1 = new Greatis.FormDesigner.Treasury();
         this.BottomToolStripPanel = new System.Windows.Forms.ToolStripPanel();
         this.TopToolStripPanel = new System.Windows.Forms.ToolStripPanel();
         this.RightToolStripPanel = new System.Windows.Forms.ToolStripPanel();
         this.LeftToolStripPanel = new System.Windows.Forms.ToolStripPanel();
         this.menuStrip1 = new System.Windows.Forms.MenuStrip();
         this.miOpen = new System.Windows.Forms.ToolStripMenuItem();
         this.miSave = new System.Windows.Forms.ToolStripMenuItem();
         this.miScale = new System.Windows.Forms.ToolStripComboBox();
         this.miRemove = new System.Windows.Forms.ToolStripMenuItem();
         this.shiftToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
         this.toolStripButtonMenuBarAlignBottom = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarAlignCenterVertical = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarAlignTop = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator9 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripButtonMenuBarAlignLeft = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarAlignMiddle = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarAlignRight = new System.Windows.Forms.ToolStripButton();
         this.toolStripSpaceHorizButton = new System.Windows.Forms.ToolStripButton();
         this.toolStripSpaceVertButton = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator10 = new System.Windows.Forms.ToolStripSeparator();
         this.toolStripButtonMenuBarSameWidth = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarSameHeight = new System.Windows.Forms.ToolStripButton();
         this.toolStripButtonMenuBarSameBoth = new System.Windows.Forms.ToolStripButton();
         this.toolStripContainer1 = new System.Windows.Forms.ToolStripContainer();
         this.panelDesign = new System.Windows.Forms.Panel();
         this.splitter2 = new System.Windows.Forms.Splitter();
         this.splitter1 = new System.Windows.Forms.Splitter();
         this.toolboxControl = new Greatis.FormDesigner.ToolboxControl();
         this.propertyGrid = new System.Windows.Forms.PropertyGrid();
         this.tsmHeader = new System.Windows.Forms.ToolStripMenuItem();
         this.menuStrip1.SuspendLayout();
         this.toolStripContainer1.ContentPanel.SuspendLayout();
         this.toolStripContainer1.TopToolStripPanel.SuspendLayout();
         this.toolStripContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // designer1
         // 
         this.designer1.Active = false;
         this.designer1.DesignContainer = null;
         this.designer1.DesignedComponents = null;
         this.designer1.DesignedForm = null;
         this.designer1.FormTreasury = this.treasury1;
         this.designer1.GridSize = new System.Drawing.Size(8, 8);
         this.designer1.LoadMode = Greatis.FormDesigner.LoadModes.Duplicate;
         this.designer1.LogFile = null;
         this.designer1.ShowErrorMessage = true;
         this.designer1.ShowGrid = false;
         this.designer1.SnapToGrid = false;
         this.designer1.UseNativeSurfaceClass = true;
         this.designer1.UseSmartTags = false;
         // 
         // treasury1
         // 
         this.treasury1.Components = null;
         this.treasury1.DesignerHost = null;
         this.treasury1.LoadMode = Greatis.FormDesigner.LoadModes.Duplicate;
         this.treasury1.LogFile = null;
         this.treasury1.ShowErrorMessage = true;
         // 
         // BottomToolStripPanel
         // 
         this.BottomToolStripPanel.Location = new System.Drawing.Point(0, 0);
         this.BottomToolStripPanel.Name = "BottomToolStripPanel";
         this.BottomToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
         this.BottomToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
         this.BottomToolStripPanel.Size = new System.Drawing.Size(0, 0);
         // 
         // TopToolStripPanel
         // 
         this.TopToolStripPanel.Location = new System.Drawing.Point(0, 0);
         this.TopToolStripPanel.Name = "TopToolStripPanel";
         this.TopToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
         this.TopToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
         this.TopToolStripPanel.Size = new System.Drawing.Size(0, 0);
         // 
         // RightToolStripPanel
         // 
         this.RightToolStripPanel.Location = new System.Drawing.Point(0, 0);
         this.RightToolStripPanel.Name = "RightToolStripPanel";
         this.RightToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
         this.RightToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
         this.RightToolStripPanel.Size = new System.Drawing.Size(0, 0);
         // 
         // LeftToolStripPanel
         // 
         this.LeftToolStripPanel.Location = new System.Drawing.Point(0, 0);
         this.LeftToolStripPanel.Name = "LeftToolStripPanel";
         this.LeftToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
         this.LeftToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
         this.LeftToolStripPanel.Size = new System.Drawing.Size(0, 0);
         // 
         // menuStrip1
         // 
         this.menuStrip1.Dock = System.Windows.Forms.DockStyle.None;
         this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miOpen,
            this.miSave,
            this.miScale,
            this.miRemove,
            this.shiftToolStripMenuItem,
            this.toolStripButtonMenuBarAlignBottom,
            this.toolStripButtonMenuBarAlignCenterVertical,
            this.toolStripButtonMenuBarAlignTop,
            this.toolStripSeparator9,
            this.toolStripButtonMenuBarAlignLeft,
            this.toolStripButtonMenuBarAlignMiddle,
            this.toolStripButtonMenuBarAlignRight,
            this.toolStripSpaceHorizButton,
            this.toolStripSpaceVertButton,
            this.toolStripSeparator10,
            this.toolStripButtonMenuBarSameWidth,
            this.toolStripButtonMenuBarSameHeight,
            this.toolStripButtonMenuBarSameBoth,
            this.tsmHeader});
         this.menuStrip1.Location = new System.Drawing.Point(0, 0);
         this.menuStrip1.Name = "menuStrip1";
         this.menuStrip1.Size = new System.Drawing.Size(1093, 27);
         this.menuStrip1.TabIndex = 0;
         this.menuStrip1.Text = "menuStrip1";
         // 
         // miOpen
         // 
         this.miOpen.Image = ((System.Drawing.Image)(resources.GetObject("miOpen.Image")));
         this.miOpen.ImageTransparentColor = System.Drawing.Color.Fuchsia;
         this.miOpen.Name = "miOpen";
         this.miOpen.Size = new System.Drawing.Size(64, 23);
         this.miOpen.Text = "Open";
         this.miOpen.Click += new System.EventHandler(this.OpenPaper);
         // 
         // miSave
         // 
         this.miSave.Image = ((System.Drawing.Image)(resources.GetObject("miSave.Image")));
         this.miSave.ImageTransparentColor = System.Drawing.Color.Fuchsia;
         this.miSave.Name = "miSave";
         this.miSave.Size = new System.Drawing.Size(59, 23);
         this.miSave.Text = "Save";
         this.miSave.Click += new System.EventHandler(this.SavePaper);
         // 
         // miScale
         // 
         this.miScale.Items.AddRange(new object[] {
            "100 %",
            "80 %",
            "50 %",
            "40 %",
            "30 %",
            "20 %",
            "10 %"});
         this.miScale.Name = "miScale";
         this.miScale.Size = new System.Drawing.Size(100, 23);
         this.miScale.SelectedIndexChanged += new System.EventHandler(this.ScaleChanged);
         // 
         // miRemove
         // 
         this.miRemove.Name = "miRemove";
         this.miRemove.Size = new System.Drawing.Size(62, 23);
         this.miRemove.Text = "Remove";
         this.miRemove.Click += new System.EventHandler(this.RemoveSelected);
         // 
         // shiftToolStripMenuItem
         // 
         this.shiftToolStripMenuItem.Name = "shiftToolStripMenuItem";
         this.shiftToolStripMenuItem.Size = new System.Drawing.Size(43, 23);
         this.shiftToolStripMenuItem.Text = "Shift";
         this.shiftToolStripMenuItem.Click += new System.EventHandler(this.shiftToolStripMenuItem_Click);
         // 
         // toolStripButtonMenuBarAlignBottom
         // 
         this.toolStripButtonMenuBarAlignBottom.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignBottom.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignBottom.Image")));
         this.toolStripButtonMenuBarAlignBottom.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignBottom.Name = "toolStripButtonMenuBarAlignBottom";
         this.toolStripButtonMenuBarAlignBottom.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignBottom.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignBottom.ToolTipText = "Align Buttom";
         this.toolStripButtonMenuBarAlignBottom.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignBottom_Click);
         // 
         // toolStripButtonMenuBarAlignCenterVertical
         // 
         this.toolStripButtonMenuBarAlignCenterVertical.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignCenterVertical.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignCenterVertical.Image")));
         this.toolStripButtonMenuBarAlignCenterVertical.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignCenterVertical.Name = "toolStripButtonMenuBarAlignCenterVertical";
         this.toolStripButtonMenuBarAlignCenterVertical.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignCenterVertical.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignCenterVertical.ToolTipText = "Align Middle";
         this.toolStripButtonMenuBarAlignCenterVertical.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignCenterVertical_Click);
         // 
         // toolStripButtonMenuBarAlignTop
         // 
         this.toolStripButtonMenuBarAlignTop.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignTop.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignTop.Image")));
         this.toolStripButtonMenuBarAlignTop.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignTop.Name = "toolStripButtonMenuBarAlignTop";
         this.toolStripButtonMenuBarAlignTop.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignTop.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignTop.ToolTipText = "Align Top";
         this.toolStripButtonMenuBarAlignTop.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignTop_Click);
         // 
         // toolStripSeparator9
         // 
         this.toolStripSeparator9.Name = "toolStripSeparator9";
         this.toolStripSeparator9.Size = new System.Drawing.Size(6, 23);
         // 
         // toolStripButtonMenuBarAlignLeft
         // 
         this.toolStripButtonMenuBarAlignLeft.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignLeft.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignLeft.Image")));
         this.toolStripButtonMenuBarAlignLeft.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignLeft.Name = "toolStripButtonMenuBarAlignLeft";
         this.toolStripButtonMenuBarAlignLeft.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignLeft.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignLeft.ToolTipText = "Align Left";
         this.toolStripButtonMenuBarAlignLeft.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignLeft_Click);
         // 
         // toolStripButtonMenuBarAlignMiddle
         // 
         this.toolStripButtonMenuBarAlignMiddle.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignMiddle.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignMiddle.Image")));
         this.toolStripButtonMenuBarAlignMiddle.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignMiddle.Name = "toolStripButtonMenuBarAlignMiddle";
         this.toolStripButtonMenuBarAlignMiddle.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignMiddle.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignMiddle.ToolTipText = "Align Middle";
         this.toolStripButtonMenuBarAlignMiddle.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignMiddle_Click);
         // 
         // toolStripButtonMenuBarAlignRight
         // 
         this.toolStripButtonMenuBarAlignRight.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarAlignRight.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarAlignRight.Image")));
         this.toolStripButtonMenuBarAlignRight.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarAlignRight.Name = "toolStripButtonMenuBarAlignRight";
         this.toolStripButtonMenuBarAlignRight.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarAlignRight.Text = "toolStripButton1";
         this.toolStripButtonMenuBarAlignRight.ToolTipText = "Align Right";
         this.toolStripButtonMenuBarAlignRight.Click += new System.EventHandler(this.toolStripButtonMenuBarAlignRight_Click);
         // 
         // toolStripSpaceHorizButton
         // 
         this.toolStripSpaceHorizButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripSpaceHorizButton.Image = ((System.Drawing.Image)(resources.GetObject("toolStripSpaceHorizButton.Image")));
         this.toolStripSpaceHorizButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripSpaceHorizButton.Name = "toolStripSpaceHorizButton";
         this.toolStripSpaceHorizButton.Size = new System.Drawing.Size(23, 20);
         this.toolStripSpaceHorizButton.Text = "toolStripSpaceHorizontalyButton";
         this.toolStripSpaceHorizButton.ToolTipText = "Space evenly horizontally ";
         this.toolStripSpaceHorizButton.Click += new System.EventHandler(this.toolStripSpaceHorizButton_Click);
         // 
         // toolStripSpaceVertButton
         // 
         this.toolStripSpaceVertButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripSpaceVertButton.Image = ((System.Drawing.Image)(resources.GetObject("toolStripSpaceVertButton.Image")));
         this.toolStripSpaceVertButton.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripSpaceVertButton.Name = "toolStripSpaceVertButton";
         this.toolStripSpaceVertButton.Size = new System.Drawing.Size(23, 20);
         this.toolStripSpaceVertButton.Text = "toolStripSpaceVerticallyButton";
         this.toolStripSpaceVertButton.ToolTipText = "Space evenly vertically";
         this.toolStripSpaceVertButton.Click += new System.EventHandler(this.toolStripSpaceVertButton_Click);
         // 
         // toolStripSeparator10
         // 
         this.toolStripSeparator10.Name = "toolStripSeparator10";
         this.toolStripSeparator10.Size = new System.Drawing.Size(6, 23);
         // 
         // toolStripButtonMenuBarSameWidth
         // 
         this.toolStripButtonMenuBarSameWidth.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarSameWidth.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarSameWidth.Image")));
         this.toolStripButtonMenuBarSameWidth.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarSameWidth.Name = "toolStripButtonMenuBarSameWidth";
         this.toolStripButtonMenuBarSameWidth.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarSameWidth.Text = "toolStripButton1";
         this.toolStripButtonMenuBarSameWidth.ToolTipText = "Same Width";
         this.toolStripButtonMenuBarSameWidth.Click += new System.EventHandler(this.toolStripButtonMenuBarSameWidth_Click);
         // 
         // toolStripButtonMenuBarSameHeight
         // 
         this.toolStripButtonMenuBarSameHeight.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarSameHeight.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarSameHeight.Image")));
         this.toolStripButtonMenuBarSameHeight.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarSameHeight.Name = "toolStripButtonMenuBarSameHeight";
         this.toolStripButtonMenuBarSameHeight.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarSameHeight.Text = "toolStripButton1";
         this.toolStripButtonMenuBarSameHeight.ToolTipText = "Same Height";
         this.toolStripButtonMenuBarSameHeight.Click += new System.EventHandler(this.toolStripButtonMenuBarSameHeight_Click);
         // 
         // toolStripButtonMenuBarSameBoth
         // 
         this.toolStripButtonMenuBarSameBoth.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.toolStripButtonMenuBarSameBoth.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButtonMenuBarSameBoth.Image")));
         this.toolStripButtonMenuBarSameBoth.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButtonMenuBarSameBoth.Name = "toolStripButtonMenuBarSameBoth";
         this.toolStripButtonMenuBarSameBoth.Size = new System.Drawing.Size(23, 20);
         this.toolStripButtonMenuBarSameBoth.Text = "toolStripButton1";
         this.toolStripButtonMenuBarSameBoth.ToolTipText = "Same Both";
         this.toolStripButtonMenuBarSameBoth.Click += new System.EventHandler(this.toolStripButtonMenuBarSameBoth_Click);
         // 
         // toolStripContainer1
         // 
         // 
         // toolStripContainer1.ContentPanel
         // 
         this.toolStripContainer1.ContentPanel.Controls.Add(this.panelDesign);
         this.toolStripContainer1.ContentPanel.Controls.Add(this.splitter2);
         this.toolStripContainer1.ContentPanel.Controls.Add(this.splitter1);
         this.toolStripContainer1.ContentPanel.Controls.Add(this.toolboxControl);
         this.toolStripContainer1.ContentPanel.Controls.Add(this.propertyGrid);
         this.toolStripContainer1.ContentPanel.Size = new System.Drawing.Size(1093, 637);
         this.toolStripContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.toolStripContainer1.Location = new System.Drawing.Point(0, 0);
         this.toolStripContainer1.Name = "toolStripContainer1";
         this.toolStripContainer1.Size = new System.Drawing.Size(1093, 664);
         this.toolStripContainer1.TabIndex = 1;
         this.toolStripContainer1.Text = "toolStripContainer1";
         // 
         // toolStripContainer1.TopToolStripPanel
         // 
         this.toolStripContainer1.TopToolStripPanel.Controls.Add(this.menuStrip1);
         // 
         // panelDesign
         // 
         this.panelDesign.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panelDesign.Location = new System.Drawing.Point(139, 0);
         this.panelDesign.Name = "panelDesign";
         this.panelDesign.Size = new System.Drawing.Size(751, 637);
         this.panelDesign.TabIndex = 4;
         // 
         // splitter2
         // 
         this.splitter2.Dock = System.Windows.Forms.DockStyle.Right;
         this.splitter2.Location = new System.Drawing.Point(890, 0);
         this.splitter2.Name = "splitter2";
         this.splitter2.Size = new System.Drawing.Size(3, 637);
         this.splitter2.TabIndex = 3;
         this.splitter2.TabStop = false;
         // 
         // splitter1
         // 
         this.splitter1.Location = new System.Drawing.Point(136, 0);
         this.splitter1.Name = "splitter1";
         this.splitter1.Size = new System.Drawing.Size(3, 637);
         this.splitter1.TabIndex = 2;
         this.splitter1.TabStop = false;
         // 
         // toolboxControl
         // 
         this.toolboxControl.AutoValidate = System.Windows.Forms.AutoValidate.Disable;
         this.toolboxControl.Designer = null;
         this.toolboxControl.Dock = System.Windows.Forms.DockStyle.Left;
         this.toolboxControl.Font = new System.Drawing.Font("Tahoma", 8F);
         this.toolboxControl.Location = new System.Drawing.Point(0, 0);
         this.toolboxControl.Name = "toolboxControl";
         this.toolboxControl.SelectedCategory = null;
         this.toolboxControl.SelectedItem = null;
         this.toolboxControl.Size = new System.Drawing.Size(136, 637);
         this.toolboxControl.TabIndex = 1;
         // 
         // propertyGrid
         // 
         this.propertyGrid.Dock = System.Windows.Forms.DockStyle.Right;
         this.propertyGrid.Location = new System.Drawing.Point(893, 0);
         this.propertyGrid.Name = "propertyGrid";
         this.propertyGrid.Size = new System.Drawing.Size(200, 637);
         this.propertyGrid.TabIndex = 0;
         // 
         // tsmHeader
         // 
         this.tsmHeader.Name = "tsmHeader";
         this.tsmHeader.Size = new System.Drawing.Size(84, 23);
         this.tsmHeader.Text = "Колонтитул";
         this.tsmHeader.Click += new System.EventHandler(this.tsmHeader_Click);
         // 
         // FormDeisgner
         // 
         this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
         this.ClientSize = new System.Drawing.Size(1093, 664);
         this.Controls.Add(this.toolStripContainer1);
         this.Font = new System.Drawing.Font("Tahoma", 8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.KeyPreview = true;
         this.MainMenuStrip = this.menuStrip1;
         this.Name = "FormDeisgner";
         this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
         this.Text = "Napoleon Form Editor";
         this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.DesignerDemo_KeyDown);
         this.menuStrip1.ResumeLayout(false);
         this.menuStrip1.PerformLayout();
         this.toolStripContainer1.ContentPanel.ResumeLayout(false);
         this.toolStripContainer1.TopToolStripPanel.ResumeLayout(false);
         this.toolStripContainer1.TopToolStripPanel.PerformLayout();
         this.toolStripContainer1.ResumeLayout(false);
         this.toolStripContainer1.PerformLayout();
         this.ResumeLayout(false);

      }
      #endregion

      public string FormName { get { return formName; } }

      private void Init()
      {
         this.WindowState = FormWindowState.Maximized;

         InitToolbox();

         IDesignEvents de = (IDesignEvents)designer1.DesignerHost.GetService(typeof(IDesignEvents));
         de.FilterProperties += new FilterEventHandler(FilterProperties);

         treasury1.DrillDown += new DrillDownHandler(DrillDown);

         designer1.DesignerHost.AddService(typeof(FormDeisgner), this);

         miScale.SelectedIndex = 0;
         NewDesignedForm();
         StartDesign();
      }

      //private XmlDocument SavePageToXmlDoc()
      //{
      //   PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
      //   Control parent = root.Parent;
      //   //Control dummy = new Control();

      //   parent.SuspendLayout();
      //   root.Hide();

      //   float prevScale = Program.ScaleFactor;
      //   Program.ScaleFactor = 1.0F;
      //   root.Scaling();

      //   StringWriter wr = new UnicodeStringWriter();
      //   XmlWriter xwriter = new XmlTextWriter(wr);
      //   using (Greatis.FormDesigner.IWriter writer = new PageWriter(xwriter))
      //   {
      //      treasury1.Store(new IComponent[1] { designer1.DesignerHost.RootComponent as Control }, writer);
      //   }

      //   Program.ScaleFactor = prevScale;
      //   root.Scaling();
      //   root.Show();

      //   ISelectionService iss = (ISelectionService)designer1.DesignerHost.GetService(typeof(ISelectionService));
      //   iss.SetSelectedComponents(new Component[] { root });

      //   parent.ResumeLayout(false);

      //   XmlDocument xd = new XmlDocument();
      //   xd.LoadXml(wr.ToString());

      //   return xd;
      //}

      void LoadFromStream(MemoryStream ms, MemoryStream header)
      {
         ms.Seek(0, SeekOrigin.Begin);

         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         ISelectionService iss = (ISelectionService)designer1.DesignerHost.GetService(typeof(ISelectionService));
         iss.SetSelectedComponents(new Component[] { null });

         IContainer container = (IContainer)designer1.DesignerHost.GetService(typeof(IContainer));
         foreach (Control ctrl in root.Controls)
         {
            container.Remove(ctrl);
            ctrl.Dispose();
         }

         if( header != null )
            root.Header = header;
         root.BeforeLoading();

         Control parent = root.Parent;

         float scale = Program.ScaleFactor;
         Program.ScaleFactor = 1.0F;

         root.Hide();

         if (ms.Length > 0)
         {
            XmlTextReader xreader = new XmlTextReader(ms);
            using (Greatis.FormDesigner.IReader reader = new PageReader(xreader))
            {
               treasury1.Load(root, reader);
            }
         }

         Program.ScaleFactor = scale;
         root.Scaling();

         root.Show();

         designer1.ClearDirty();
         iss.SetSelectedComponents(new Component[] { root });
      }

      private void LoadPage(XmlNode node, MemoryStream header)
      {
         MemoryStream ms = new MemoryStream();
         if( node != null )
         {
            MemoryStream str = new MemoryStream();
            XmlWriter wr = new XmlTextWriter(str, Encoding.Unicode);
            node.WriteTo(wr);
            wr.Flush();

            str.Seek(0, SeekOrigin.Begin);
            str.WriteTo(ms);
            wr.Close();
         }

         LoadFromStream(ms, header);
         ms.Close();
      }

      bool DrillDown(IComponent control)
      {
         if (control is Row || control is Panel) return true;
         return false;
      }

      void FilterProperties(IComponent component, ref FilterEventArgs args)
      {
         args.caching = true;

         args.data.Remove("DataBindings");
         args.data.Remove("Margin");
         args.data.Remove("TabIndex");
         args.data.Remove("ImeMode");

         if (component is Line || component is Row)
            args.data.Remove("Text");
      }

      private void InitToolbox()
      {
         //toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(TextBox)), "General");
         toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(NFormEditor.Label)), "General");
         toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(Line)), "General");
         toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(Table)), "General");
         toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(Row)), "General");
         toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(Picture)), "General");
         //toolboxControl.AddItem(new System.Drawing.Design.ToolboxItem(typeof(DataGridView)), "General");
      }

      private void Design()
      {
         if (designer1.Active == false)
            StartDesign();
         else
            EndDesign();
      }

      private void StartDesign()
      {
         designer1.DesignContainer = this.panelDesign;

         IDesignEvents dev = (IDesignEvents)designer1.DesignerHost.GetService(typeof(IDesignEvents));
         if (dev != null)
            dev.FilterProperties += new FilterEventHandler(ComponentsFilterProperties);

         designer1.UseSmartTags = true;

         propertyGrid.Site = new PropertyGridSite(designer1.DesignerHost as IServiceProvider);

         if (toolboxControl.Designer == null)
            toolboxControl.Designer = designer1;

         ISelectionService ss = (ISelectionService)designer1.DesignerHost.GetService(typeof(ISelectionService));
         ss.SelectionChanged += new EventHandler(SelectionChanged);

         IComponentChangeService iccs = (IComponentChangeService)
            designer1.DesignerHost.GetService(typeof(IComponentChangeService));
         iccs.ComponentAdded += new ComponentEventHandler(ComponentAdded);
         iccs.ComponentChanged += new ComponentChangedEventHandler(ComponentChanged);
         iccs.ComponentRemoved += new ComponentEventHandler(ComponentRemoved);

         designer1.KeyDown += new KeyEventHandler(designedForm_KeyDown);

         designer1.Active = true;
      }

      void ComponentsFilterProperties(IComponent component, ref FilterEventArgs args)
      {
         PrintForm pf = component as PrintForm;
         if (pf != null)
         {
            pf.FilterProperties(ref args.data);
            return;
         }
      }

      private void EndDesign()
      {
         IDesignEvents dev = (IDesignEvents)designer1.DesignerHost.GetService(typeof(IDesignEvents));
         if (dev != null)
            dev.FilterProperties -= new FilterEventHandler(ComponentsFilterProperties);

         ISelectionService ss = (ISelectionService)designer1.DesignerHost.GetService(typeof(ISelectionService));
         ss.SelectionChanged -= new EventHandler(SelectionChanged);

         designer1.KeyDown -= new KeyEventHandler(designedForm_KeyDown);

         CheckDesignedForm();

         designer1.Active = false;

         if (designer1.DesignContainer != null)
            designer1.DesignContainer.Hide();
         designer1.DesignContainer = null;

         if (DesignEnded != null)
            DesignEnded(this, new EventArgs());
      }

      internal event EventHandler DesignEnded;

      private void NewDesignedForm()
      {
         if (designer1.DesignedForm == null)
         {
            // create new form

            Control designedForm = new PrintForm();
            designedForm.Location = new Point(0, 0);

            designedForm.KeyDown += new KeyEventHandler(designedForm_KeyDown);

           // assign designed form to designer
            designer1.DesignedForm = designedForm;
         }
      }

      void designedForm_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Delete)
            designer1.DeleteSelected();
         if (e.Shift == true && e.KeyCode == Keys.Insert)
            designer1.PasteControlsFromClipboard();
         if (e.Control == true && e.KeyCode == Keys.Insert)
            designer1.CopyControlsToClipboard();
      }

      /// <summary>
      /// Save designed Form
      /// </summary>
      private void SaveDesignedForm()
      {
         if (formName == null || formName.Length == 0)
         {
            SaveFileDialog saveFileName = new SaveFileDialog();
            saveFileName.Filter = "XML Form (*.xml)|*.xml";
            saveFileName.FilterIndex = 0;
            saveFileName.RestoreDirectory = true;

            if (saveFileName.ShowDialog() != DialogResult.OK)
               return;
            formName = saveFileName.FileName;

            SaveForm();
         }
         else
            SaveForm();
      }

      void SaveFormData(Greatis.FormDesigner.IWriter writer)
      {
         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         Control parent = root.Parent;
         ISelectionService iss = (ISelectionService)designer1.DesignerHost.GetService(typeof(ISelectionService));

         iss.SetSelectedComponents(null);
         parent.SuspendLayout();

         float prevScale = Program.ScaleFactor;
         Program.ScaleFactor = 1.0F;
         if (prevScale != Program.ScaleFactor)
            root.Scaling();

         treasury1.Store(new IComponent[1] { root }, writer);

         if (prevScale != Program.ScaleFactor)
         {
            Program.ScaleFactor = prevScale;
            root.Scaling();
         }

         iss.SetSelectedComponents(new Component[] { root });

         parent.ResumeLayout(false);
         designer1.ClearDirty();
      }

      private void SaveToFile(string fileName)
      {
         if(headerView)
         {
            headerView = !headerView;
            UpdateHeaderView();
         }

         XmlTextWriter wf = new XmlTextWriter(fileName, Encoding.Unicode);
         wf.Formatting = Formatting.Indented;
         wf.Indentation = 3;

         string header = null;
         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         MemoryStream memHeader = root.Header;
         if( memHeader != null && memHeader.Length > 0)
         {
            memHeader.Seek(0, SeekOrigin.Begin);
            XmlDocument xd = new XmlDocument();
            xd.Load(memHeader);
            
            XmlNodeList list = xd.DocumentElement.GetElementsByTagName("Album");
            if (list.Count > 0)
               xd.DocumentElement.RemoveChild(list[0]);

            header = "<Header>" + xd.DocumentElement.InnerXml + "</Header>";
            
         }
         using (Greatis.FormDesigner.IWriter writer = new PageWriter(wf, header))
         {
            SaveFormData(writer);
         }
      }

      /// <summary>
      /// use formName for save form
      /// </summary>
      private void SaveForm()
      {
         if (formName == null || formName.Length == 0)
            return;

         SaveToFile(formName);
      }

      private void OpenDesignedForm()
      {
         OpenFileDialog openFileName = new OpenFileDialog();

         openFileName.Filter = "XML files (*.xml)|*.xml";
         openFileName.FilterIndex = 0;
         openFileName.RestoreDirectory = true;

         if (openFileName.ShowDialog() == DialogResult.OK)
         {
            formName = openFileName.FileName;

            XmlDocument doc = new XmlDocument();

            doc.Load(formName);

            MemoryStream header = new MemoryStream();
            XmlNodeList list = doc.DocumentElement.GetElementsByTagName("Header");
            if (list.Count > 0)
            {
               XmlWriter wr = new XmlTextWriter(header, Encoding.Unicode);
               XmlAttribute tv = doc.CreateAttribute("TreasuryVersion");
               tv.Value = "1";
               list[0].Attributes.Append(tv);
               list[0].WriteTo(wr);
               wr.Flush();
               doc.DocumentElement.RemoveChild(list[0]);
            }

            LoadPage(doc.DocumentElement, header);
         }
      }

      private void CheckDesignedForm()
      {
         if (designer1.IsDirty == true)
         {
            if (MessageBox.Show("Do you want save the designed form?", "Question",
               MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
               SaveDesignedForm();
            }
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         if (designer1.Active)
            EndDesign();
         base.OnClosing(e);
         instance = null;
      }

      private void SelectionChanged(object sender, EventArgs e)
      {
         // disable/enable menu & toolbox align buttons
         int selectionCount = ((ISelectionService)sender).SelectionCount;
         bool enable = (selectionCount < 2) ? false : true;

         if (selectionCount == 0)
            propertyGrid.SelectedObject = designer1.DesignedForm;
         else
         {
            object[] selected = new object[selectionCount];
            ((ISelectionService)sender).GetSelectedComponents().CopyTo(selected, 0);
            propertyGrid.SelectedObjects = selected;
         }
      }

      private void ComponentAdded(object sender, ComponentEventArgs e)
      {
      }

      private void ComponentChanged(object sender, ComponentChangedEventArgs e)
      {
      }

      private void ComponentRemoved(object sender, ComponentEventArgs e)
      {
      }

      float GetScale()
      {
         string v = (string)miScale.SelectedItem;
         if (v == null)
            return 0.1F;

         int i = v.IndexOf('%');
         float scale = float.Parse(v.Substring(0, i));
         scale /= 100;
         return scale;
      }

      private void ScaleChanged(object sender, EventArgs e)
      {
         bool saveDirty = designer1.IsDirty;
         PrintForm ctrl = designer1.DesignerHost.RootComponent as PrintForm;
         if (ctrl != null)
         {
            Program.ScaleFactor = GetScale();
            ctrl.Scaling();

            if (!saveDirty)
               designer1.ClearDirty();
         }
         /*
         if (ctrl != null)
         {
            //designer1.DesignedForm = null;
            designer1.Active = false;

            prevScale.Height = 1 / prevScale.Height;
            prevScale.Width = 1 / prevScale.Width;
            ctrl.Scale(prevScale);

            prevScale = GetScale();
            ctrl.Scale(prevScale);
            //designer1.DesignedForm = ctrl;
            designer1.Active = true;
         }
          * */
      }

      private void SavePaper(object sender, EventArgs e)
      {
         SaveDesignedForm();
      }

      private void OpenPaper(object sender, EventArgs e)
      {
         OpenDesignedForm();
      }

      private void RemoveSelected(object sender, EventArgs e)
      {
         designer1.DeleteSelected();
      }

      void CopyControlsToClipboard()
      {
         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         Control parent = root.Parent;
         //Control dummy = new Control();

         parent.SuspendLayout();
         root.Hide();

         float prevScale = Program.ScaleFactor;
         Program.ScaleFactor = 1.0F;
         root.Scaling();
         
         designer1.CopyControlsToClipboard();

         Program.ScaleFactor = prevScale;
         root.Scaling();
         root.Show();
      }

      void PasteControls()
      {
         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         Control parent = root.Parent;
         //Control dummy = new Control();

         parent.SuspendLayout();
         root.Hide();

         float prevScale = Program.ScaleFactor;
         Program.ScaleFactor = 1.0F;
         root.Scaling();

         designer1.PasteControlsFromClipboard();

         Program.ScaleFactor = prevScale;
         root.Scaling();
         root.Show();
      }

      private void DesignerDemo_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.S && (e.Modifiers & Keys.Control) != 0 )
         {
            SaveForm();
            return;
         }

         if (e.KeyCode == Keys.Delete && e.Modifiers == 0)
            designer1.DeleteSelected();
         if (e.Shift == true && e.KeyCode == Keys.Insert)
            PasteControls();
         if (e.Control == true && e.KeyCode == Keys.Insert)
            CopyControlsToClipboard();
      }

      private void shiftToolStripMenuItem_Click(object sender, EventArgs e)
      {
         InputNumber inum = new InputNumber(10);
         if( inum.ShowDialog() == DialogResult.OK )
         {
            int shift = inum.Value;

            PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
            Control parent = root.Parent;
            //Control dummy = new Control();

            parent.SuspendLayout();
            root.Hide();

            float prevScale = Program.ScaleFactor;
            Program.ScaleFactor = 1.0F;
            root.Scaling();

            foreach (Control c in root.Controls)
            {
               PropertyDescriptorCollection pdc = TypeDescriptor.GetProperties(c);
               PropertyDescriptor pd = pdc["Left"];
               if (pd != null)
               {
                  int left = (int)pd.GetValue(c);
                  left += shift;
                  pd.SetValue(c, left);
               }
            }

            Program.ScaleFactor = prevScale;
            root.Scaling();
            root.Show();
         }
      }

      private void toolStripButtonMenuBarAlignBottom_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Bottom);
      }

      private void toolStripButtonMenuBarAlignCenterVertical_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Center);
      }

      private void toolStripButtonMenuBarAlignTop_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Top);
      }

      private void toolStripButtonMenuBarAlignMiddle_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Middle);
      }

      private void toolStripButtonMenuBarAlignRight_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Right);
      }

      private void toolStripSpaceHorizButton_Click(object sender, EventArgs e)
      {
         IMenuCommandService mcs = (IMenuCommandService)designer1.DesignerHost.GetService(typeof(IMenuCommandService));
         mcs.GlobalInvoke(StandardCommands.HorizSpaceMakeEqual);
      }

      private void toolStripSpaceVertButton_Click(object sender, EventArgs e)
      {
         IMenuCommandService mcs = (IMenuCommandService)designer1.DesignerHost.GetService(typeof(IMenuCommandService));
         mcs.GlobalInvoke(StandardCommands.VertSpaceMakeEqual);
      }

      private void toolStripButtonMenuBarSameWidth_Click(object sender, EventArgs e)
      {
         designer1.MakeSameSize(Greatis.FormDesigner.ResizeType.SameWidth);
      }

      private void toolStripButtonMenuBarSameHeight_Click(object sender, EventArgs e)
      {
         designer1.MakeSameSize(Greatis.FormDesigner.ResizeType.SameHeight);
      }

      private void toolStripButtonMenuBarSameBoth_Click(object sender, EventArgs e)
      {
         designer1.MakeSameSize(Greatis.FormDesigner.ResizeType.SameWidth | ResizeType.SameHeight);
      }

      private void toolStripButtonMenuBarAlignLeft_Click(object sender, EventArgs e)
      {
         designer1.Align(Greatis.FormDesigner.AlignType.Left);
      }

      private void tsmHeader_Click(object sender, EventArgs e)
      {
         headerView = !headerView;
         UpdateHeaderView();
      }

      void UpdateHeaderView()
      {
         tsmHeader.Text = headerView ? "Осн.форма" : "Колонтитул";

         PrintForm root = designer1.DesignerHost.RootComponent as PrintForm;
         Program.ScaleFactor = 1.0F;
         root.Scaling();
         miScale.SelectedIndex = 0;

         MemoryStream ms = new MemoryStream();

         MemoryStream str = new MemoryStream();
         XmlTextWriter wr = new XmlTextWriter(str, Encoding.Unicode);
         Greatis.FormDesigner.IWriter writer = new PageWriter(wr, null);
         SaveFormData(writer);
         str.Seek(0, SeekOrigin.Begin);
         str.WriteTo(ms);
         writer.Dispose();

         if( headerView )
         {
            root.MainForm = ms;
            ms = root.Header;
         }
         else
         {
            root.Header = ms;
            ms = root.MainForm;
         }

         if (ms == null)
            ms = new MemoryStream();
         LoadFromStream(ms, null);
      }
   }

   /// A nearly empty implementation of ISite, this class merely passes on
   /// service requests to the host.
   internal class PropertyGridSite : System.ComponentModel.ISite
   {
      private IServiceProvider sp;

      public PropertyGridSite(IServiceProvider sp)
      {
         this.sp = sp;
      }

      #region Implementation of ISite

      public System.ComponentModel.IComponent Component
      {
         get { return null; }
      }

      public System.ComponentModel.IContainer Container
      {
         get { return null; }
      }

      public bool DesignMode
      {
         get { return false; }
      }

      public string Name
      {
         get { return null; }
         set { }
      }

      #endregion

      #region Implementation of IServiceProvider

      public object GetService(Type serviceType)
      {
         if (sp != null)
            return sp.GetService(serviceType);

         return null;
      }

      #endregion
   }
}

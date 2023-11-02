namespace GRSoft.Ads
{
   partial class FmPlanning
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmPlanning));
         this.tabControl = new System.Windows.Forms.TabControl();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindNext = new System.Windows.Forms.ToolStripButton();
         this.btnFindPrev = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.datePickerCtrl1 = new GRSoft.Ads.DatePickerCtrl();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // tabControl
         // 
         this.tabControl.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tabControl.Location = new System.Drawing.Point(0, 25);
         this.tabControl.Name = "tabControl";
         this.tabControl.SelectedIndex = 0;
         this.tabControl.Size = new System.Drawing.Size(727, 438);
         this.tabControl.TabIndex = 1;
         this.tabControl.Selected += new System.Windows.Forms.TabControlEventHandler(this.tabControl_Selected);
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.tbFind,
            this.btnFindNext,
            this.btnFindPrev});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(727, 25);
         this.toolStrip1.TabIndex = 2;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.Ads.Properties.Resources.refresh;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.ToolTipText = "Введите строку для поиска";
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnFindNext
         // 
         this.btnFindNext.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindNext.Image = global::GRSoft.Ads.Properties.Resources.go_down_search;
         this.btnFindNext.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindNext.Name = "btnFindNext";
         this.btnFindNext.Size = new System.Drawing.Size(23, 22);
         this.btnFindNext.Text = "Искать вперед";
         this.btnFindNext.Click += new System.EventHandler(this.btnFindNext_Click);
         // 
         // btnFindPrev
         // 
         this.btnFindPrev.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindPrev.Image = global::GRSoft.Ads.Properties.Resources.go_up_search;
         this.btnFindPrev.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindPrev.Name = "btnFindPrev";
         this.btnFindPrev.Size = new System.Drawing.Size(23, 22);
         this.btnFindPrev.Text = "Искать назад";
         this.btnFindPrev.Click += new System.EventHandler(this.btnFindPrev_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 463);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(727, 22);
         this.statusStrip1.TabIndex = 3;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // datePickerCtrl1
         // 
         this.datePickerCtrl1.BackColor = System.Drawing.SystemColors.Control;
         this.datePickerCtrl1.Date = new System.DateTime(2013, 1, 29, 15, 34, 8, 156);
         this.datePickerCtrl1.Location = new System.Drawing.Point(242, -1);
         this.datePickerCtrl1.Name = "datePickerCtrl1";
         this.datePickerCtrl1.Size = new System.Drawing.Size(220, 26);
         this.datePickerCtrl1.TabIndex = 4;
         // 
         // FmPlanning
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(727, 485);
         this.Controls.Add(this.datePickerCtrl1);
         this.Controls.Add(this.tabControl);
         this.Controls.Add(this.toolStrip1);
         this.Controls.Add(this.statusStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmPlanning";
         this.Text = "Форма планирования";
         this.Load += new System.EventHandler(this.FmPlanning_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion


      private System.Windows.Forms.TabControl tabControl;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private DatePickerCtrl datePickerCtrl1;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindNext;
      private System.Windows.Forms.ToolStripButton btnFindPrev;
   }
}
namespace GRSoft.NapoleonManager
{
   partial class FmAgentOrg
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentOrg));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.btnRefresh = new System.Windows.Forms.ToolStripButton();
         this.btnSave = new System.Windows.Forms.ToolStripButton();
         this.tbFind = new System.Windows.Forms.ToolStripTextBox();
         this.btnFindDown = new System.Windows.Forms.ToolStripButton();
         this.btnFindUp = new System.Windows.Forms.ToolStripButton();
         this.statusStrip1 = new System.Windows.Forms.StatusStrip();
         this.tvOrg = new System.Windows.Forms.TreeView();
         this.cbAgent = new System.Windows.Forms.ComboBox();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnRefresh,
            this.btnSave,
            this.tbFind,
            this.btnFindDown,
            this.btnFindUp});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(526, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // btnRefresh
         // 
         this.btnRefresh.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnRefresh.Image = global::GRSoft.NapoleonManager.Properties.Resources.view_refresh_6;
         this.btnRefresh.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnRefresh.Margin = new System.Windows.Forms.Padding(140, 1, 0, 2);
         this.btnRefresh.Name = "btnRefresh";
         this.btnRefresh.Size = new System.Drawing.Size(23, 22);
         this.btnRefresh.Text = "Обновить";
         this.btnRefresh.Click += new System.EventHandler(this.btnRefresh_Click);
         // 
         // btnSave
         // 
         this.btnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnSave.Image = global::GRSoft.NapoleonManager.Properties.Resources.Save;
         this.btnSave.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnSave.Name = "btnSave";
         this.btnSave.Size = new System.Drawing.Size(23, 22);
         this.btnSave.Text = "Сохранить";
         this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
         // 
         // tbFind
         // 
         this.tbFind.Name = "tbFind";
         this.tbFind.Size = new System.Drawing.Size(100, 25);
         this.tbFind.Visible = false;
         this.tbFind.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbFind_KeyDown);
         // 
         // btnFindDown
         // 
         this.btnFindDown.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindDown.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_search;
         this.btnFindDown.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindDown.Name = "btnFindDown";
         this.btnFindDown.Size = new System.Drawing.Size(23, 22);
         this.btnFindDown.Text = "Искать вперед";
         this.btnFindDown.Visible = false;
         this.btnFindDown.Click += new System.EventHandler(this.btnFindDown_Click);
         // 
         // btnFindUp
         // 
         this.btnFindUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.btnFindUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_search;
         this.btnFindUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.btnFindUp.Name = "btnFindUp";
         this.btnFindUp.Size = new System.Drawing.Size(23, 22);
         this.btnFindUp.Text = "Искать назад";
         this.btnFindUp.Visible = false;
         this.btnFindUp.Click += new System.EventHandler(this.btnFindUp_Click);
         // 
         // statusStrip1
         // 
         this.statusStrip1.Location = new System.Drawing.Point(0, 386);
         this.statusStrip1.Name = "statusStrip1";
         this.statusStrip1.Size = new System.Drawing.Size(526, 22);
         this.statusStrip1.TabIndex = 1;
         this.statusStrip1.Text = "statusStrip1";
         // 
         // tvOrg
         // 
         this.tvOrg.CheckBoxes = true;
         this.tvOrg.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvOrg.DrawMode = System.Windows.Forms.TreeViewDrawMode.OwnerDrawText;
         this.tvOrg.HideSelection = false;
         this.tvOrg.Location = new System.Drawing.Point(0, 25);
         this.tvOrg.Name = "tvOrg";
         this.tvOrg.Size = new System.Drawing.Size(526, 361);
         this.tvOrg.TabIndex = 2;
         this.tvOrg.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvOrg_AfterCheck);
         this.tvOrg.DrawNode += new System.Windows.Forms.DrawTreeNodeEventHandler(this.tvOrg_DrawNode);
         // 
         // cbAgent
         // 
         this.cbAgent.FormattingEnabled = true;
         this.cbAgent.Location = new System.Drawing.Point(10, 2);
         this.cbAgent.Name = "cbAgent";
         this.cbAgent.Size = new System.Drawing.Size(136, 22);
         this.cbAgent.TabIndex = 3;
         // 
         // FmAgentOrg
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(526, 408);
         this.Controls.Add(this.cbAgent);
         this.Controls.Add(this.tvOrg);
         this.Controls.Add(this.statusStrip1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmAgentOrg";
         this.Text = "Контрагенты";
         this.Load += new System.EventHandler(this.FmAgentOrg_Load);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmAgentOrg_FormClosing);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.StatusStrip statusStrip1;
      private System.Windows.Forms.TreeView tvOrg;
      private System.Windows.Forms.ToolStripButton btnRefresh;
      private System.Windows.Forms.ComboBox cbAgent;
      private System.Windows.Forms.ToolStripButton btnSave;
      private System.Windows.Forms.ToolStripTextBox tbFind;
      private System.Windows.Forms.ToolStripButton btnFindDown;
      private System.Windows.Forms.ToolStripButton btnFindUp;
   }
}
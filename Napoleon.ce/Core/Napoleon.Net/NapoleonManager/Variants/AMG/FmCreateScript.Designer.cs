namespace GRSoft.NapoleonManager
{
   partial class FmCreateScript
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmCreateScript));
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.toolStripButton1 = new System.Windows.Forms.ToolStripButton();
         this.cbAgents = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
         this.cbOrg = new System.Windows.Forms.ToolStripComboBox();
         this.toolStripLabel2 = new System.Windows.Forms.ToolStripLabel();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.listView = new System.Windows.Forms.ListView();
         this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
         this.toolStrip1.SuspendLayout();
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).BeginInit();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripButton1,
            this.cbAgents,
            this.toolStripLabel1,
            this.cbOrg,
            this.toolStripLabel2});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(1046, 25);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // toolStripButton1
         // 
         this.toolStripButton1.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.toolStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         this.toolStripButton1.Image = ((System.Drawing.Image)(resources.GetObject("toolStripButton1.Image")));
         this.toolStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.toolStripButton1.Name = "toolStripButton1";
         this.toolStripButton1.Size = new System.Drawing.Size(109, 22);
         this.toolStripButton1.Text = "Создать документ";
         this.toolStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
         // 
         // cbAgents
         // 
         this.cbAgents.Name = "cbAgents";
         this.cbAgents.Size = new System.Drawing.Size(170, 25);
         this.cbAgents.SelectedIndexChanged += new System.EventHandler(this.cbAgents_SelectedIndexChanged);
         // 
         // toolStripLabel1
         // 
         this.toolStripLabel1.Name = "toolStripLabel1";
         this.toolStripLabel1.Size = new System.Drawing.Size(77, 22);
         this.toolStripLabel1.Text = "организация";
         // 
         // cbOrg
         // 
         this.cbOrg.Name = "cbOrg";
         this.cbOrg.Size = new System.Drawing.Size(350, 25);
         // 
         // toolStripLabel2
         // 
         this.toolStripLabel2.Name = "toolStripLabel2";
         this.toolStripLabel2.Size = new System.Drawing.Size(72, 22);
         this.toolStripLabel2.Text = "Дата время:";
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 25);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.listView);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(10);
         this.splitContainer1.Size = new System.Drawing.Size(1046, 639);
         this.splitContainer1.SplitterDistance = 348;
         this.splitContainer1.TabIndex = 1;
         // 
         // listView
         // 
         this.listView.Dock = System.Windows.Forms.DockStyle.Fill;
         this.listView.HideSelection = false;
         this.listView.Location = new System.Drawing.Point(0, 0);
         this.listView.Name = "listView";
         this.listView.Size = new System.Drawing.Size(348, 639);
         this.listView.TabIndex = 0;
         this.listView.UseCompatibleStateImageBehavior = false;
         this.listView.View = System.Windows.Forms.View.List;
         this.listView.SelectedIndexChanged += new System.EventHandler(this.listView_SelectedIndexChanged);
         // 
         // dateTimePicker1
         // 
         this.dateTimePicker1.CustomFormat = "dd.MM.yyyy HH:mm";
         this.dateTimePicker1.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
         this.dateTimePicker1.Location = new System.Drawing.Point(684, 3);
         this.dateTimePicker1.Name = "dateTimePicker1";
         this.dateTimePicker1.Size = new System.Drawing.Size(129, 20);
         this.dateTimePicker1.TabIndex = 2;
         // 
         // FmCreateScript
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(1046, 664);
         this.Controls.Add(this.dateTimePicker1);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.toolStrip1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmCreateScript";
         this.Text = "Создать на основании";
         this.Load += new System.EventHandler(this.FmCreateScript_Load);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         ((System.ComponentModel.ISupportInitialize)(this.splitContainer1)).EndInit();
         this.splitContainer1.ResumeLayout(false);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ListView listView;
      private System.Windows.Forms.ToolStripButton toolStripButton1;
      private System.Windows.Forms.ToolStripComboBox cbAgents;
      private System.Windows.Forms.ToolStripLabel toolStripLabel1;
      private System.Windows.Forms.ToolStripComboBox cbOrg;
      private System.Windows.Forms.ToolStripLabel toolStripLabel2;
      private System.Windows.Forms.DateTimePicker dateTimePicker1;
   }
}
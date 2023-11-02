namespace GRSoft.NapoleonManager
{
   partial class MatrixOrderEditor
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbAdd = new System.Windows.Forms.ToolStripButton();
         this.tsbRemove = new System.Windows.Forms.ToolStripButton();
         this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
         this.tsbMoveUp = new System.Windows.Forms.ToolStripButton();
         this.tsbMoveDn = new System.Windows.Forms.ToolStripButton();
         this.tvAgentMatrix = new System.Windows.Forms.TreeView();
         this.lvOrderedMatrix = new System.Windows.Forms.ListView();
         this.clmnMarixName = new System.Windows.Forms.ColumnHeader();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 0);
         this.splitContainer1.Name = "splitContainer1";
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tvAgentMatrix);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.lvOrderedMatrix);
         this.splitContainer1.Panel2.Controls.Add(this.toolStrip1);
         this.splitContainer1.Size = new System.Drawing.Size(570, 466);
         this.splitContainer1.SplitterDistance = 269;
         this.splitContainer1.TabIndex = 0;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Dock = System.Windows.Forms.DockStyle.Left;
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbAdd,
            this.tsbRemove,
            this.toolStripSeparator1,
            this.tsbMoveUp,
            this.tsbMoveDn});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(24, 466);
         this.toolStrip1.TabIndex = 0;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbAdd
         // 
         this.tsbAdd.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbAdd.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_next_4;
         this.tsbAdd.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbAdd.Name = "tsbAdd";
         this.tsbAdd.Size = new System.Drawing.Size(29, 20);
         this.tsbAdd.Text = "Добавить матрицу";
         this.tsbAdd.Click += new System.EventHandler(this.tsbAdd_Click);
         // 
         // tsbRemove
         // 
         this.tsbRemove.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbRemove.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_previous_4;
         this.tsbRemove.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbRemove.Name = "tsbRemove";
         this.tsbRemove.Size = new System.Drawing.Size(29, 20);
         this.tsbRemove.Text = "Убрать матрицу";
         this.tsbRemove.Click += new System.EventHandler(this.tsbRemove_Click);
         // 
         // toolStripSeparator1
         // 
         this.toolStripSeparator1.Name = "toolStripSeparator1";
         this.toolStripSeparator1.Size = new System.Drawing.Size(21, 6);
         // 
         // tsbMoveUp
         // 
         this.tsbMoveUp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMoveUp.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_up_4;
         this.tsbMoveUp.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMoveUp.Name = "tsbMoveUp";
         this.tsbMoveUp.Size = new System.Drawing.Size(29, 20);
         this.tsbMoveUp.Text = "Переместить вверх";
         this.tsbMoveUp.Click += new System.EventHandler(this.tsbMoveUp_Click);
         // 
         // tsbMoveDn
         // 
         this.tsbMoveDn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbMoveDn.Image = global::GRSoft.NapoleonManager.Properties.Resources.go_down_4;
         this.tsbMoveDn.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbMoveDn.Name = "tsbMoveDn";
         this.tsbMoveDn.Size = new System.Drawing.Size(29, 20);
         this.tsbMoveDn.Text = "Переместить вниз";
         this.tsbMoveDn.Click += new System.EventHandler(this.tsbMoveDn_Click);
         // 
         // tvAgentMatrix
         // 
         this.tvAgentMatrix.CheckBoxes = true;
         this.tvAgentMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tvAgentMatrix.Location = new System.Drawing.Point(0, 0);
         this.tvAgentMatrix.Name = "tvAgentMatrix";
         this.tvAgentMatrix.Size = new System.Drawing.Size(269, 466);
         this.tvAgentMatrix.TabIndex = 0;
         this.tvAgentMatrix.NodeMouseDoubleClick += new System.Windows.Forms.TreeNodeMouseClickEventHandler(this.tvAgentMatrix_NodeMouseDoubleClick);
         this.tvAgentMatrix.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.tvAgentMatrix_AfterSelect);
         // 
         // lvOrderedMatrix
         // 
         this.lvOrderedMatrix.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.clmnMarixName});
         this.lvOrderedMatrix.Dock = System.Windows.Forms.DockStyle.Fill;
         this.lvOrderedMatrix.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.Nonclickable;
         this.lvOrderedMatrix.HideSelection = false;
         this.lvOrderedMatrix.Location = new System.Drawing.Point(24, 0);
         this.lvOrderedMatrix.MultiSelect = false;
         this.lvOrderedMatrix.Name = "lvOrderedMatrix";
         this.lvOrderedMatrix.Size = new System.Drawing.Size(273, 466);
         this.lvOrderedMatrix.TabIndex = 1;
         this.lvOrderedMatrix.UseCompatibleStateImageBehavior = false;
         this.lvOrderedMatrix.View = System.Windows.Forms.View.Details;
         this.lvOrderedMatrix.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.lvOrderedMatrix_MouseDoubleClick);
         this.lvOrderedMatrix.SelectedIndexChanged += new System.EventHandler(this.lvOrderedMatrix_SelectedIndexChanged);
         // 
         // clmnMarixName
         // 
         this.clmnMarixName.Text = "Матрица прайса";
         this.clmnMarixName.Width = 249;
         // 
         // MatrixOrderEditor
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.splitContainer1);
         this.Name = "MatrixOrderEditor";
         this.Size = new System.Drawing.Size(570, 466);
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbAdd;
      private System.Windows.Forms.ToolStripButton tsbRemove;
      private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
      private System.Windows.Forms.ToolStripButton tsbMoveUp;
      private System.Windows.Forms.ToolStripButton tsbMoveDn;
      private System.Windows.Forms.ColumnHeader clmnMarixName;
      public System.Windows.Forms.TreeView tvAgentMatrix;
      public System.Windows.Forms.ListView lvOrderedMatrix;
   }
}

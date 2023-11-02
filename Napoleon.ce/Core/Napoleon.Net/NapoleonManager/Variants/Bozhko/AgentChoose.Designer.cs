namespace GRSoft.NapoleonManager
{
   partial class AgentChoose
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AgentChoose));
         this.dgvAgents = new System.Windows.Forms.DataGridView();
         this.clmnCheck = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbOK = new System.Windows.Forms.ToolStripButton();
         this.tsbCancel = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvAgents
         // 
         this.dgvAgents.AllowUserToAddRows = false;
         this.dgvAgents.AllowUserToDeleteRows = false;
         this.dgvAgents.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvAgents.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnCheck,
            this.clmnAgent});
         this.dgvAgents.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvAgents.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
         this.dgvAgents.Location = new System.Drawing.Point(0, 25);
         this.dgvAgents.Name = "dgvAgents";
         this.dgvAgents.RowHeadersVisible = false;
         this.dgvAgents.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.dgvAgents.Size = new System.Drawing.Size(346, 367);
         this.dgvAgents.TabIndex = 0;
         this.dgvAgents.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvAgents_CurrentCellDirtyStateChanged);
         // 
         // clmnCheck
         // 
         this.clmnCheck.DataPropertyName = "Check";
         this.clmnCheck.HeaderText = "";
         this.clmnCheck.Name = "clmnCheck";
         this.clmnCheck.Width = 30;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "Name";
         this.clmnAgent.HeaderText = "Агент";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbOK,
            this.tsbCancel});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(346, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbOK
         // 
         this.tsbOK.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbOK.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbOK.Image = ((System.Drawing.Image)(resources.GetObject("tsbOK.Image")));
         this.tsbOK.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbOK.Name = "tsbOK";
         this.tsbOK.Size = new System.Drawing.Size(23, 22);
         this.tsbOK.Text = "ОК";
         this.tsbOK.Click += new System.EventHandler(this.tsbOK_Click);
         // 
         // tsbCancel
         // 
         this.tsbCancel.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
         this.tsbCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCancel.Image = ((System.Drawing.Image)(resources.GetObject("tsbCancel.Image")));
         this.tsbCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCancel.Margin = new System.Windows.Forms.Padding(0, 1, 7, 2);
         this.tsbCancel.Name = "tsbCancel";
         this.tsbCancel.Size = new System.Drawing.Size(23, 22);
         this.tsbCancel.Text = "Закрыть";
         this.tsbCancel.Click += new System.EventHandler(this.tsbCancel_Click);
         // 
         // AgentChoose
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(346, 392);
         this.Controls.Add(this.dgvAgents);
         this.Controls.Add(this.toolStrip1);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "AgentChoose";
         this.Text = "Выберите агентов";
         ((System.ComponentModel.ISupportInitialize)(this.dgvAgents)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvAgents;
      private System.Windows.Forms.ToolStrip toolStrip1;
      protected System.Windows.Forms.ToolStripButton tsbOK;
      protected System.Windows.Forms.ToolStripButton tsbCancel;
      private System.Windows.Forms.DataGridViewCheckBoxColumn clmnCheck;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;

   }
}
namespace GRSoft.NapoleonAdmin
{
   partial class FmAgentActivity
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmAgentActivity));
         this.dgvItems = new System.Windows.Forms.DataGridView();
         this.clmnAgent = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnLogin = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnDate = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnPhone = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnIMEI = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.toolStrip1 = new System.Windows.Forms.ToolStrip();
         this.tsbCopy = new System.Windows.Forms.ToolStripButton();
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).BeginInit();
         this.toolStrip1.SuspendLayout();
         this.SuspendLayout();
         // 
         // dgvItems
         // 
         this.dgvItems.AllowUserToAddRows = false;
         this.dgvItems.AllowUserToDeleteRows = false;
         this.dgvItems.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.clmnAgent,
            this.clmnLogin,
            this.clmnDate,
            this.clmnTime,
            this.clmnPhone,
            this.clmnIMEI});
         this.dgvItems.Dock = System.Windows.Forms.DockStyle.Fill;
         this.dgvItems.Location = new System.Drawing.Point(0, 25);
         this.dgvItems.Name = "dgvItems";
         this.dgvItems.ReadOnly = true;
         this.dgvItems.RowHeadersVisible = false;
         this.dgvItems.Size = new System.Drawing.Size(694, 376);
         this.dgvItems.TabIndex = 0;
         // 
         // clmnAgent
         // 
         this.clmnAgent.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.clmnAgent.DataPropertyName = "UserID";
         this.clmnAgent.HeaderText = "ID Агента";
         this.clmnAgent.Name = "clmnAgent";
         this.clmnAgent.ReadOnly = true;
         // 
         // clmnLogin
         // 
         this.clmnLogin.DataPropertyName = "Login";
         this.clmnLogin.HeaderText = "Логин";
         this.clmnLogin.Name = "clmnLogin";
         this.clmnLogin.ReadOnly = true;
         // 
         // clmnDate
         // 
         this.clmnDate.DataPropertyName = "Date";
         this.clmnDate.HeaderText = "Дата";
         this.clmnDate.Name = "clmnDate";
         this.clmnDate.ReadOnly = true;
         // 
         // clmnTime
         // 
         this.clmnTime.DataPropertyName = "Time";
         this.clmnTime.HeaderText = "Время";
         this.clmnTime.Name = "clmnTime";
         this.clmnTime.ReadOnly = true;
         // 
         // clmnPhone
         // 
         this.clmnPhone.DataPropertyName = "Phone";
         this.clmnPhone.HeaderText = "Модель телефона";
         this.clmnPhone.Name = "clmnPhone";
         this.clmnPhone.ReadOnly = true;
         // 
         // clmnIMEI
         // 
         this.clmnIMEI.DataPropertyName = "IMEI";
         this.clmnIMEI.HeaderText = "IMEI";
         this.clmnIMEI.Name = "clmnIMEI";
         this.clmnIMEI.ReadOnly = true;
         // 
         // toolStrip1
         // 
         this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsbCopy});
         this.toolStrip1.Location = new System.Drawing.Point(0, 0);
         this.toolStrip1.Name = "toolStrip1";
         this.toolStrip1.Size = new System.Drawing.Size(694, 25);
         this.toolStrip1.TabIndex = 1;
         this.toolStrip1.Text = "toolStrip1";
         // 
         // tsbCopy
         // 
         this.tsbCopy.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         this.tsbCopy.Image = ((System.Drawing.Image)(resources.GetObject("tsbCopy.Image")));
         this.tsbCopy.ImageTransparentColor = System.Drawing.Color.Magenta;
         this.tsbCopy.Name = "tsbCopy";
         this.tsbCopy.Size = new System.Drawing.Size(23, 22);
         this.tsbCopy.Text = "Копировать в буфер";
         this.tsbCopy.Click += new System.EventHandler(this.tsbCopy_Click);
         // 
         // FmAgentActivity
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.dgvItems);
         this.Controls.Add(this.toolStrip1);
         this.Name = "FmAgentActivity";
         this.Size = new System.Drawing.Size(694, 401);
         ((System.ComponentModel.ISupportInitialize)(this.dgvItems)).EndInit();
         this.toolStrip1.ResumeLayout(false);
         this.toolStrip1.PerformLayout();
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.DataGridView dgvItems;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnAgent;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnLogin;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnDate;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnTime;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnPhone;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnIMEI;
      private System.Windows.Forms.ToolStrip toolStrip1;
      private System.Windows.Forms.ToolStripButton tsbCopy;
   }
}

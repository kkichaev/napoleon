namespace GRSoft.NapoleonManager
{
   partial class SelectAgents
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SelectAgents));
         this.agentsList = new System.Windows.Forms.DataGridView();
         this.alChecked = new System.Windows.Forms.DataGridViewCheckBoxColumn();
         this.alName = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.clmnCode = new System.Windows.Forms.DataGridViewTextBoxColumn();
         this.ok = new System.Windows.Forms.Button();
         ((System.ComponentModel.ISupportInitialize)(this.agentsList)).BeginInit();
         this.SuspendLayout();
         // 
         // agentsList
         // 
         this.agentsList.AllowUserToAddRows = false;
         this.agentsList.AllowUserToDeleteRows = false;
         this.agentsList.AllowUserToResizeRows = false;
         this.agentsList.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.agentsList.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
         this.agentsList.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.alChecked,
            this.alName,
            this.clmnCode});
         this.agentsList.Location = new System.Drawing.Point(2, 0);
         this.agentsList.MultiSelect = false;
         this.agentsList.Name = "agentsList";
         this.agentsList.RowHeadersVisible = false;
         this.agentsList.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
         this.agentsList.Size = new System.Drawing.Size(374, 303);
         this.agentsList.TabIndex = 0;
         this.agentsList.CellDoubleClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.agentsList_CellDoubleClick);
         // 
         // alChecked
         // 
         this.alChecked.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.alChecked.HeaderText = "";
         this.alChecked.Name = "alChecked";
         // 
         // alName
         // 
         this.alName.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         this.alName.DataPropertyName = "Name";
         this.alName.FillWeight = 300F;
         this.alName.HeaderText = "ФИО";
         this.alName.Name = "alName";
         // 
         // clmnCode
         // 
         this.clmnCode.DataPropertyName = "ID";
         this.clmnCode.HeaderText = "Код";
         this.clmnCode.Name = "clmnCode";
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(287, 309);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 1;
         this.ok.Text = "Выбрать";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // SelectAgents
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(374, 344);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.agentsList);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "SelectAgents";
         this.Text = "Выберите агента";
         ((System.ComponentModel.ISupportInitialize)(this.agentsList)).EndInit();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.DataGridView agentsList;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.DataGridViewCheckBoxColumn alChecked;
      private System.Windows.Forms.DataGridViewTextBoxColumn alName;
      private System.Windows.Forms.DataGridViewTextBoxColumn clmnCode;
   }
}
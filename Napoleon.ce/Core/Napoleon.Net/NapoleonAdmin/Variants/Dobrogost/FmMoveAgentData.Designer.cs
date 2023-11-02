namespace GRSoft.NapoleonAdmin
{
   partial class FmMoveAgentData
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMoveAgentData));
         this.srcAgentLabel = new System.Windows.Forms.Label();
         this.lbAgents = new System.Windows.Forms.ListBox();
         this.label1 = new System.Windows.Forms.Label();
         this.cbDocs = new System.Windows.Forms.CheckBox();
         this.cbRoute = new System.Windows.Forms.CheckBox();
         this.cbMoveType = new System.Windows.Forms.ComboBox();
         this.label2 = new System.Windows.Forms.Label();
         this.button1 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // srcAgentLabel
         // 
         this.srcAgentLabel.AutoSize = true;
         this.srcAgentLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.srcAgentLabel.Location = new System.Drawing.Point(12, 11);
         this.srcAgentLabel.Name = "srcAgentLabel";
         this.srcAgentLabel.Size = new System.Drawing.Size(143, 18);
         this.srcAgentLabel.TabIndex = 0;
         this.srcAgentLabel.Text = "Перенести с агента";
         // 
         // lbAgents
         // 
         this.lbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lbAgents.Font = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbAgents.FormattingEnabled = true;
         this.lbAgents.IntegralHeight = false;
         this.lbAgents.ItemHeight = 18;
         this.lbAgents.Location = new System.Drawing.Point(12, 173);
         this.lbAgents.Name = "lbAgents";
         this.lbAgents.Size = new System.Drawing.Size(413, 172);
         this.lbAgents.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(12, 154);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(164, 13);
         this.label1.TabIndex = 2;
         this.label1.Text = "Укажите агента для переноса:";
         // 
         // cbDocs
         // 
         this.cbDocs.AutoSize = true;
         this.cbDocs.Location = new System.Drawing.Point(91, 107);
         this.cbDocs.Name = "cbDocs";
         this.cbDocs.Size = new System.Drawing.Size(85, 17);
         this.cbDocs.TabIndex = 9;
         this.cbDocs.Text = "Документы";
         this.cbDocs.UseVisualStyleBackColor = true;
         // 
         // cbRoute
         // 
         this.cbRoute.AutoSize = true;
         this.cbRoute.Location = new System.Drawing.Point(91, 85);
         this.cbRoute.Name = "cbRoute";
         this.cbRoute.Size = new System.Drawing.Size(71, 17);
         this.cbRoute.TabIndex = 8;
         this.cbRoute.Text = "Маршрут";
         this.cbRoute.UseVisualStyleBackColor = true;
         // 
         // cbMoveType
         // 
         this.cbMoveType.FormattingEnabled = true;
         this.cbMoveType.Items.AddRange(new object[] {
            "Переносить",
            "Копировать"});
         this.cbMoveType.Location = new System.Drawing.Point(193, 55);
         this.cbMoveType.Name = "cbMoveType";
         this.cbMoveType.Size = new System.Drawing.Size(132, 21);
         this.cbMoveType.TabIndex = 14;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(89, 58);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(98, 13);
         this.label2.TabIndex = 13;
         this.label2.Text = "Способ переноса:";
         // 
         // button1
         // 
         this.button1.Location = new System.Drawing.Point(176, 369);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 15;
         this.button1.Text = "OK";
         this.button1.UseVisualStyleBackColor = true;
         this.button1.Click += new System.EventHandler(this.button1_Click);
         // 
         // FmMoveAgentData
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(437, 404);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.cbMoveType);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.cbDocs);
         this.Controls.Add(this.cbRoute);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.lbAgents);
         this.Controls.Add(this.srcAgentLabel);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMoveAgentData";
         this.Text = "Перенести данные агента";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label srcAgentLabel;
      private System.Windows.Forms.ListBox lbAgents;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.CheckBox cbDocs;
      private System.Windows.Forms.CheckBox cbRoute;
      private System.Windows.Forms.ComboBox cbMoveType;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Button button1;
   }
}
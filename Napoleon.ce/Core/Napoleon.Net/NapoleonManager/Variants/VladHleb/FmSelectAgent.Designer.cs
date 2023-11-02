namespace GRSoft.NapoleonManager
{
   partial class FmSelectAgent
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmSelectAgent));
         this.lbAgents = new System.Windows.Forms.ListBox();
         this.button1 = new System.Windows.Forms.Button();
         this.button2 = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // lbAgents
         // 
         this.lbAgents.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.lbAgents.FormattingEnabled = true;
         this.lbAgents.Location = new System.Drawing.Point(1, 0);
         this.lbAgents.Name = "lbAgents";
         this.lbAgents.Size = new System.Drawing.Size(282, 251);
         this.lbAgents.TabIndex = 0;
         this.lbAgents.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.lbAgents_MouseDoubleClick);
         // 
         // button1
         // 
         this.button1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.button1.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.button1.Location = new System.Drawing.Point(12, 269);
         this.button1.Name = "button1";
         this.button1.Size = new System.Drawing.Size(75, 23);
         this.button1.TabIndex = 1;
         this.button1.Text = "Отмена";
         this.button1.UseVisualStyleBackColor = true;
         // 
         // button2
         // 
         this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.button2.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.button2.Location = new System.Drawing.Point(197, 269);
         this.button2.Name = "button2";
         this.button2.Size = new System.Drawing.Size(75, 23);
         this.button2.TabIndex = 2;
         this.button2.Text = "OK";
         this.button2.UseVisualStyleBackColor = true;
         // 
         // FmSelectAgent
         // 
         this.AcceptButton = this.button2;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.button1;
         this.ClientSize = new System.Drawing.Size(284, 306);
         this.Controls.Add(this.button2);
         this.Controls.Add(this.button1);
         this.Controls.Add(this.lbAgents);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmSelectAgent";
         this.Text = "Выберите агента";
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.ListBox lbAgents;
      private System.Windows.Forms.Button button1;
      private System.Windows.Forms.Button button2;
   }
}
namespace GRSoft.Ads
{
   partial class FmMessage
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmMessage));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnHistory = new System.Windows.Forms.Button();
         this.btnSend = new System.Windows.Forms.Button();
         this.lbCharsRemain = new System.Windows.Forms.Label();
         this.lbAgent = new System.Windows.Forms.Label();
         this.label1 = new System.Windows.Forms.Label();
         this.tbMessage = new System.Windows.Forms.TextBox();
         this.tbHistory = new System.Windows.Forms.TextBox();
         this.splitContainer1 = new System.Windows.Forms.SplitContainer();
         this.panel1.SuspendLayout();
         this.splitContainer1.Panel1.SuspendLayout();
         this.splitContainer1.Panel2.SuspendLayout();
         this.splitContainer1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnHistory);
         this.panel1.Controls.Add(this.btnSend);
         this.panel1.Controls.Add(this.lbCharsRemain);
         this.panel1.Controls.Add(this.lbAgent);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(609, 35);
         this.panel1.TabIndex = 0;
         // 
         // btnHistory
         // 
         this.btnHistory.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnHistory.Location = new System.Drawing.Point(522, 6);
         this.btnHistory.Name = "btnHistory";
         this.btnHistory.Size = new System.Drawing.Size(75, 23);
         this.btnHistory.TabIndex = 4;
         this.btnHistory.Text = "История";
         this.btnHistory.UseVisualStyleBackColor = true;
         this.btnHistory.Click += new System.EventHandler(this.btnHistory_Click);
         // 
         // btnSend
         // 
         this.btnSend.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnSend.Location = new System.Drawing.Point(436, 6);
         this.btnSend.Name = "btnSend";
         this.btnSend.Size = new System.Drawing.Size(75, 23);
         this.btnSend.TabIndex = 3;
         this.btnSend.Text = "Отправить";
         this.btnSend.UseVisualStyleBackColor = true;
         this.btnSend.Click += new System.EventHandler(this.btnSend_Click);
         // 
         // lbCharsRemain
         // 
         this.lbCharsRemain.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.lbCharsRemain.AutoSize = true;
         this.lbCharsRemain.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbCharsRemain.Location = new System.Drawing.Point(390, 9);
         this.lbCharsRemain.Name = "lbCharsRemain";
         this.lbCharsRemain.Size = new System.Drawing.Size(35, 18);
         this.lbCharsRemain.TabIndex = 2;
         this.lbCharsRemain.Text = "200";
         // 
         // lbAgent
         // 
         this.lbAgent.AutoSize = true;
         this.lbAgent.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.lbAgent.Location = new System.Drawing.Point(127, 9);
         this.lbAgent.Name = "lbAgent";
         this.lbAgent.Size = new System.Drawing.Size(61, 18);
         this.lbAgent.TabIndex = 1;
         this.lbAgent.Text = "lbAgent";
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Font = new System.Drawing.Font("Arial", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label1.Location = new System.Drawing.Point(4, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(126, 18);
         this.label1.TabIndex = 0;
         this.label1.Text = "Сообщение для:";
         // 
         // tbMessage
         // 
         this.tbMessage.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbMessage.Location = new System.Drawing.Point(7, 0);
         this.tbMessage.Multiline = true;
         this.tbMessage.Name = "tbMessage";
         this.tbMessage.ScrollBars = System.Windows.Forms.ScrollBars.Both;
         this.tbMessage.Size = new System.Drawing.Size(595, 163);
         this.tbMessage.TabIndex = 0;
         this.tbMessage.TextChanged += new System.EventHandler(this.tbMessage_TextChanged);
         this.tbMessage.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbMessage_KeyDown);
         // 
         // tbHistory
         // 
         this.tbHistory.BackColor = System.Drawing.SystemColors.InactiveBorder;
         this.tbHistory.Dock = System.Windows.Forms.DockStyle.Fill;
         this.tbHistory.Location = new System.Drawing.Point(7, 0);
         this.tbHistory.Multiline = true;
         this.tbHistory.Name = "tbHistory";
         this.tbHistory.ReadOnly = true;
         this.tbHistory.ScrollBars = System.Windows.Forms.ScrollBars.Both;
         this.tbHistory.Size = new System.Drawing.Size(595, 99);
         this.tbHistory.TabIndex = 0;
         // 
         // splitContainer1
         // 
         this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.splitContainer1.Location = new System.Drawing.Point(0, 35);
         this.splitContainer1.Name = "splitContainer1";
         this.splitContainer1.Orientation = System.Windows.Forms.Orientation.Horizontal;
         // 
         // splitContainer1.Panel1
         // 
         this.splitContainer1.Panel1.Controls.Add(this.tbMessage);
         this.splitContainer1.Panel1.Padding = new System.Windows.Forms.Padding(7, 0, 7, 0);
         // 
         // splitContainer1.Panel2
         // 
         this.splitContainer1.Panel2.Controls.Add(this.tbHistory);
         this.splitContainer1.Panel2.Padding = new System.Windows.Forms.Padding(7, 0, 7, 7);
         this.splitContainer1.Size = new System.Drawing.Size(609, 276);
         this.splitContainer1.SplitterDistance = 163;
         this.splitContainer1.SplitterWidth = 7;
         this.splitContainer1.TabIndex = 1;
         // 
         // FmMessage
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(609, 311);
         this.Controls.Add(this.splitContainer1);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmMessage";
         this.Text = "Сообщение";
         this.Activated += new System.EventHandler(this.FmMessage_Activated);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.splitContainer1.Panel1.ResumeLayout(false);
         this.splitContainer1.Panel1.PerformLayout();
         this.splitContainer1.Panel2.ResumeLayout(false);
         this.splitContainer1.Panel2.PerformLayout();
         this.splitContainer1.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Label lbAgent;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbMessage;
      private System.Windows.Forms.Label lbCharsRemain;
      private System.Windows.Forms.Button btnSend;
      private System.Windows.Forms.TextBox tbHistory;
      private System.Windows.Forms.SplitContainer splitContainer1;
      private System.Windows.Forms.Button btnHistory;
   }
}
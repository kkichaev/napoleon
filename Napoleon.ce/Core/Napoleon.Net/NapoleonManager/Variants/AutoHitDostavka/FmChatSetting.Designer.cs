namespace GRSoft.NapoleonManager
{
   partial class FmChatSetting
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmChatSetting));
         this.label1 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.cbIncSnd = new System.Windows.Forms.ComboBox();
         this.cbOutSnd = new System.Windows.Forms.ComboBox();
         this.btnPlayNewMsg = new System.Windows.Forms.Button();
         this.btnPlaySendMsg = new System.Windows.Forms.Button();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(5, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(73, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Имя в чатах";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(91, 6);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(266, 20);
         this.tbName.TabIndex = 1;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(5, 38);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(76, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Уведомления";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(5, 62);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(98, 14);
         this.label3.TabIndex = 3;
         this.label3.Text = "Новое сообщение";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(5, 86);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(141, 14);
         this.label4.TabIndex = 4;
         this.label4.Text = "Отправленное сообщение";
         // 
         // cbIncSnd
         // 
         this.cbIncSnd.FormattingEnabled = true;
         this.cbIncSnd.Location = new System.Drawing.Point(165, 54);
         this.cbIncSnd.Name = "cbIncSnd";
         this.cbIncSnd.Size = new System.Drawing.Size(192, 22);
         this.cbIncSnd.TabIndex = 5;
         // 
         // cbOutSnd
         // 
         this.cbOutSnd.FormattingEnabled = true;
         this.cbOutSnd.Location = new System.Drawing.Point(165, 83);
         this.cbOutSnd.Name = "cbOutSnd";
         this.cbOutSnd.Size = new System.Drawing.Size(192, 22);
         this.cbOutSnd.TabIndex = 6;
         // 
         // btnPlayNewMsg
         // 
         this.btnPlayNewMsg.Location = new System.Drawing.Point(390, 54);
         this.btnPlayNewMsg.Name = "btnPlayNewMsg";
         this.btnPlayNewMsg.Size = new System.Drawing.Size(75, 23);
         this.btnPlayNewMsg.TabIndex = 7;
         this.btnPlayNewMsg.Text = "button1";
         this.btnPlayNewMsg.UseVisualStyleBackColor = true;
         // 
         // btnPlaySendMsg
         // 
         this.btnPlaySendMsg.Location = new System.Drawing.Point(390, 83);
         this.btnPlaySendMsg.Name = "btnPlaySendMsg";
         this.btnPlaySendMsg.Size = new System.Drawing.Size(75, 23);
         this.btnPlaySendMsg.TabIndex = 8;
         this.btnPlaySendMsg.Text = "button2";
         this.btnPlaySendMsg.UseVisualStyleBackColor = true;
         // 
         // btnOK
         // 
         this.btnOK.Location = new System.Drawing.Point(309, 246);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 9;
         this.btnOK.Text = "ОК";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.Location = new System.Drawing.Point(390, 246);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 10;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // FmChatSetting
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(472, 277);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.btnPlaySendMsg);
         this.Controls.Add(this.btnPlayNewMsg);
         this.Controls.Add(this.cbOutSnd);
         this.Controls.Add(this.cbIncSnd);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmChatSetting";
         this.Text = "Настройки";
         this.Load += new System.EventHandler(this.FmChatSetting_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.ComboBox cbIncSnd;
      private System.Windows.Forms.ComboBox cbOutSnd;
      private System.Windows.Forms.Button btnPlayNewMsg;
      private System.Windows.Forms.Button btnPlaySendMsg;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
   }
}
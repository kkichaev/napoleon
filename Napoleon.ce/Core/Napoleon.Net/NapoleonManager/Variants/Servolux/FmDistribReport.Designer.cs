namespace GRSoft.NapoleonManager
{
   partial class FmDistribReport
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmDistribReport));
         this.label3 = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.cancel = new System.Windows.Forms.Button();
         this.ok = new System.Windows.Forms.Button();
         this.cbPriceType = new System.Windows.Forms.ComboBox();
         this.label1 = new System.Windows.Forms.Label();
         this.cbThState = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.cbOrg = new System.Windows.Forms.ComboBox();
         this.label5 = new System.Windows.Forms.Label();
         this.cbAddress = new System.Windows.Forms.ComboBox();
         this.label6 = new System.Windows.Forms.Label();
         this.lbAgent = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(239, 47);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(19, 13);
         this.label3.TabIndex = 27;
         this.label3.Text = "по";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(25, 46);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(42, 13);
         this.label2.TabIndex = 26;
         this.label2.Text = "Дата с";
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(264, 44);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(151, 20);
         this.dtpEnd.TabIndex = 25;
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(73, 43);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(151, 20);
         this.dtpBegin.TabIndex = 24;
         this.dtpBegin.Value = new System.DateTime(2009, 11, 1, 0, 0, 0, 0);
         // 
         // cancel
         // 
         this.cancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
         this.cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.cancel.Location = new System.Drawing.Point(373, 221);
         this.cancel.Name = "cancel";
         this.cancel.Size = new System.Drawing.Size(75, 23);
         this.cancel.TabIndex = 29;
         this.cancel.Text = "Закрыть";
         this.cancel.UseVisualStyleBackColor = true;
         this.cancel.Click += new System.EventHandler(this.cancel_Click);
         // 
         // ok
         // 
         this.ok.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(12, 221);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 28;
         this.ok.Text = "Excel";
         this.ok.UseVisualStyleBackColor = true;
         this.ok.Click += new System.EventHandler(this.ok_Click);
         // 
         // cbPriceType
         // 
         this.cbPriceType.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbPriceType.FormattingEnabled = true;
         this.cbPriceType.Location = new System.Drawing.Point(158, 89);
         this.cbPriceType.Name = "cbPriceType";
         this.cbPriceType.Size = new System.Drawing.Size(222, 21);
         this.cbPriceType.TabIndex = 30;
         this.cbPriceType.SelectedIndexChanged += new System.EventHandler(this.cbPriceType_SelectedIndexChanged);
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(87, 92);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(57, 13);
         this.label1.TabIndex = 31;
         this.label1.Text = "Вид груза";
         // 
         // cbThState
         // 
         this.cbThState.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbThState.FormattingEnabled = true;
         this.cbThState.Location = new System.Drawing.Point(158, 119);
         this.cbThState.Name = "cbThState";
         this.cbThState.Size = new System.Drawing.Size(222, 21);
         this.cbThState.TabIndex = 32;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(54, 122);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(90, 13);
         this.label4.TabIndex = 33;
         this.label4.Text = "Терм.состояние";
         // 
         // cbOrg
         // 
         this.cbOrg.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbOrg.FormattingEnabled = true;
         this.cbOrg.Location = new System.Drawing.Point(158, 147);
         this.cbOrg.Name = "cbOrg";
         this.cbOrg.Size = new System.Drawing.Size(222, 21);
         this.cbOrg.TabIndex = 34;
         this.cbOrg.SelectedIndexChanged += new System.EventHandler(this.cbOrg_SelectedIndexChanged);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(79, 151);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(65, 13);
         this.label5.TabIndex = 35;
         this.label5.Text = "Контрагент";
         // 
         // cbAddress
         // 
         this.cbAddress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbAddress.FormattingEnabled = true;
         this.cbAddress.Location = new System.Drawing.Point(158, 175);
         this.cbAddress.Name = "cbAddress";
         this.cbAddress.Size = new System.Drawing.Size(222, 21);
         this.cbAddress.TabIndex = 36;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(48, 178);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(96, 13);
         this.label6.TabIndex = 37;
         this.label6.Text = "Торговый объект";
         // 
         // lbAgent
         // 
         this.lbAgent.AutoSize = true;
         this.lbAgent.Location = new System.Drawing.Point(32, 9);
         this.lbAgent.Name = "lbAgent";
         this.lbAgent.Size = new System.Drawing.Size(35, 13);
         this.lbAgent.TabIndex = 38;
         this.lbAgent.Text = "label7";
         // 
         // FmDistribReport
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(460, 256);
         this.Controls.Add(this.lbAgent);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.cbAddress);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.cbOrg);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.cbThState);
         this.Controls.Add(this.label1);
         this.Controls.Add(this.cbPriceType);
         this.Controls.Add(this.cancel);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.dtpEnd);
         this.Controls.Add(this.dtpBegin);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmDistribReport";
         this.Text = "Отчет по дистрибуции";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Button cancel;
      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.ComboBox cbPriceType;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.ComboBox cbThState;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.ComboBox cbOrg;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.ComboBox cbAddress;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.Label lbAgent;
   }
}
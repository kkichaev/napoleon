namespace GRSoft.NapoleonManager
{
   partial class FmOrgEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgEdit));
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnAddCity = new System.Windows.Forms.Button();
         this.btnAddSlsnet = new System.Windows.Forms.Button();
         this.cbCity = new System.Windows.Forms.ComboBox();
         this.cbSlsnet = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.bntOK = new System.Windows.Forms.Button();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.btnAddCity);
         this.panel1.Controls.Add(this.btnAddSlsnet);
         this.panel1.Controls.Add(this.cbCity);
         this.panel1.Controls.Add(this.cbSlsnet);
         this.panel1.Controls.Add(this.label4);
         this.panel1.Controls.Add(this.label3);
         this.panel1.Controls.Add(this.tbAddress);
         this.panel1.Controls.Add(this.label2);
         this.panel1.Controls.Add(this.tbName);
         this.panel1.Controls.Add(this.label1);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(432, 193);
         this.panel1.TabIndex = 0;
         // 
         // btnAddCity
         // 
         this.btnAddCity.Location = new System.Drawing.Point(278, 81);
         this.btnAddCity.Name = "btnAddCity";
         this.btnAddCity.Size = new System.Drawing.Size(44, 23);
         this.btnAddCity.TabIndex = 9;
         this.btnAddCity.Text = "...";
         this.btnAddCity.UseVisualStyleBackColor = true;
         this.btnAddCity.Click += new System.EventHandler(this.btnAddCity_Click);
         // 
         // btnAddSlsnet
         // 
         this.btnAddSlsnet.Location = new System.Drawing.Point(278, 46);
         this.btnAddSlsnet.Name = "btnAddSlsnet";
         this.btnAddSlsnet.Size = new System.Drawing.Size(44, 23);
         this.btnAddSlsnet.TabIndex = 8;
         this.btnAddSlsnet.Text = "...";
         this.btnAddSlsnet.UseVisualStyleBackColor = true;
         this.btnAddSlsnet.Click += new System.EventHandler(this.btnAddSlsnet_Click);
         // 
         // cbCity
         // 
         this.cbCity.FormattingEnabled = true;
         this.cbCity.Location = new System.Drawing.Point(104, 81);
         this.cbCity.Name = "cbCity";
         this.cbCity.Size = new System.Drawing.Size(160, 22);
         this.cbCity.TabIndex = 7;
         // 
         // cbSlsnet
         // 
         this.cbSlsnet.FormattingEnabled = true;
         this.cbSlsnet.Location = new System.Drawing.Point(104, 46);
         this.cbSlsnet.Name = "cbSlsnet";
         this.cbSlsnet.Size = new System.Drawing.Size(160, 22);
         this.cbSlsnet.TabIndex = 6;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(64, 49);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(32, 14);
         this.label4.TabIndex = 5;
         this.label4.Text = "Сеть";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(60, 84);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(36, 14);
         this.label3.TabIndex = 4;
         this.label3.Text = "Город";
         // 
         // tbAddress
         // 
         this.tbAddress.Location = new System.Drawing.Point(104, 110);
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.Size = new System.Drawing.Size(311, 20);
         this.tbAddress.TabIndex = 3;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(57, 116);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(39, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Адрес";
         // 
         // tbName
         // 
         this.tbName.Location = new System.Drawing.Point(104, 15);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(311, 20);
         this.tbName.TabIndex = 1;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(13, 18);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Наименование";
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.bntOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 143);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(432, 50);
         this.panel2.TabIndex = 1;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(350, 13);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 1;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // bntOK
         // 
         this.bntOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.bntOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.bntOK.Location = new System.Drawing.Point(269, 13);
         this.bntOK.Name = "bntOK";
         this.bntOK.Size = new System.Drawing.Size(75, 23);
         this.bntOK.TabIndex = 0;
         this.bntOK.Text = "ОК";
         this.bntOK.UseVisualStyleBackColor = true;
         // 
         // FmOrgEdit
         // 
         this.AcceptButton = this.bntOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(432, 193);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgEdit";
         this.Text = "Карточка контрагента";
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmOrgEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.panel2.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button bntOK;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ComboBox cbSlsnet;
      private System.Windows.Forms.ComboBox cbCity;
      private System.Windows.Forms.Button btnAddSlsnet;
      private System.Windows.Forms.Button btnAddCity;
   }
}
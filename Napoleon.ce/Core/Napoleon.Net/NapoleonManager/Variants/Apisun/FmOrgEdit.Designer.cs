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
         this.label1 = new System.Windows.Forms.Label();
         this.tbName = new System.Windows.Forms.TextBox();
         this.tbAddress = new System.Windows.Forms.TextBox();
         this.label2 = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.cbType = new System.Windows.Forms.ComboBox();
         this.btnType = new System.Windows.Forms.Button();
         this.btnDealer = new System.Windows.Forms.Button();
         this.label5 = new System.Windows.Forms.Label();
         this.cbLicense = new System.Windows.Forms.CheckBox();
         this.tbCheif = new System.Windows.Forms.TextBox();
         this.label6 = new System.Windows.Forms.Label();
         this.tbContact = new System.Windows.Forms.TextBox();
         this.label7 = new System.Windows.Forms.Label();
         this.tbCheifPhone = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.tbContactPhone = new System.Windows.Forms.TextBox();
         this.label8 = new System.Windows.Forms.Label();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.lbDealers = new System.Windows.Forms.ListBox();
         this.label9 = new System.Windows.Forms.Label();
         this.tbAgvTraffic = new System.Windows.Forms.TextBox();
         this.bntDelDealer = new System.Windows.Forms.Button();
         this.tbEmail = new System.Windows.Forms.TextBox();
         this.label11 = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(4, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(83, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Наименование";
         // 
         // tbName
         // 
         this.tbName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbName.Location = new System.Drawing.Point(102, 7);
         this.tbName.Name = "tbName";
         this.tbName.Size = new System.Drawing.Size(512, 20);
         this.tbName.TabIndex = 1;
         // 
         // tbAddress
         // 
         this.tbAddress.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbAddress.Location = new System.Drawing.Point(101, 41);
         this.tbAddress.Name = "tbAddress";
         this.tbAddress.Size = new System.Drawing.Size(513, 20);
         this.tbAddress.TabIndex = 3;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(4, 44);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(39, 14);
         this.label2.TabIndex = 2;
         this.label2.Text = "Адрес";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(4, 79);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(58, 14);
         this.label4.TabIndex = 6;
         this.label4.Text = "Вид точки";
         // 
         // cbType
         // 
         this.cbType.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbType.FormattingEnabled = true;
         this.cbType.Location = new System.Drawing.Point(102, 76);
         this.cbType.Name = "cbType";
         this.cbType.Size = new System.Drawing.Size(435, 22);
         this.cbType.TabIndex = 7;
         // 
         // btnType
         // 
         this.btnType.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnType.Location = new System.Drawing.Point(543, 75);
         this.btnType.Name = "btnType";
         this.btnType.Size = new System.Drawing.Size(75, 23);
         this.btnType.TabIndex = 8;
         this.btnType.Text = "...";
         this.btnType.UseVisualStyleBackColor = true;
         this.btnType.Click += new System.EventHandler(this.btnType_Click);
         // 
         // btnDealer
         // 
         this.btnDealer.Location = new System.Drawing.Point(382, 115);
         this.btnDealer.Name = "btnDealer";
         this.btnDealer.Size = new System.Drawing.Size(75, 23);
         this.btnDealer.TabIndex = 11;
         this.btnDealer.Text = "...";
         this.btnDealer.UseVisualStyleBackColor = true;
         this.btnDealer.Click += new System.EventHandler(this.btnDealer_Click);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(4, 115);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(57, 14);
         this.label5.TabIndex = 9;
         this.label5.Text = "Оптовики";
         // 
         // cbLicense
         // 
         this.cbLicense.AutoSize = true;
         this.cbLicense.Location = new System.Drawing.Point(107, 227);
         this.cbLicense.Name = "cbLicense";
         this.cbLicense.Size = new System.Drawing.Size(73, 18);
         this.cbLicense.TabIndex = 13;
         this.cbLicense.Text = "Лизензия";
         this.cbLicense.UseVisualStyleBackColor = true;
         // 
         // tbCheif
         // 
         this.tbCheif.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbCheif.Location = new System.Drawing.Point(108, 323);
         this.tbCheif.Name = "tbCheif";
         this.tbCheif.Size = new System.Drawing.Size(266, 20);
         this.tbCheif.TabIndex = 15;
         // 
         // label6
         // 
         this.label6.AutoSize = true;
         this.label6.Location = new System.Drawing.Point(4, 323);
         this.label6.Name = "label6";
         this.label6.Size = new System.Drawing.Size(27, 14);
         this.label6.TabIndex = 14;
         this.label6.Text = "ЛПР";
         // 
         // tbContact
         // 
         this.tbContact.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.tbContact.Location = new System.Drawing.Point(108, 361);
         this.tbContact.Name = "tbContact";
         this.tbContact.Size = new System.Drawing.Size(266, 20);
         this.tbContact.TabIndex = 17;
         // 
         // label7
         // 
         this.label7.AutoSize = true;
         this.label7.Location = new System.Drawing.Point(4, 361);
         this.label7.Name = "label7";
         this.label7.Size = new System.Drawing.Size(94, 14);
         this.label7.TabIndex = 16;
         this.label7.Text = "Контактное лицо";
         // 
         // tbCheifPhone
         // 
         this.tbCheifPhone.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.tbCheifPhone.Location = new System.Drawing.Point(447, 323);
         this.tbCheifPhone.Name = "tbCheifPhone";
         this.tbCheifPhone.Size = new System.Drawing.Size(174, 20);
         this.tbCheifPhone.TabIndex = 19;
         // 
         // label3
         // 
         this.label3.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(394, 326);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(50, 14);
         this.label3.TabIndex = 18;
         this.label3.Text = "Телефон";
         // 
         // tbContactPhone
         // 
         this.tbContactPhone.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.tbContactPhone.Location = new System.Drawing.Point(447, 361);
         this.tbContactPhone.Name = "tbContactPhone";
         this.tbContactPhone.Size = new System.Drawing.Size(174, 20);
         this.tbContactPhone.TabIndex = 21;
         // 
         // label8
         // 
         this.label8.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.label8.AutoSize = true;
         this.label8.Location = new System.Drawing.Point(394, 364);
         this.label8.Name = "label8";
         this.label8.Size = new System.Drawing.Size(50, 14);
         this.label8.TabIndex = 20;
         this.label8.Text = "Телефон";
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(444, 402);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 22;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(543, 402);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 23;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // lbDealers
         // 
         this.lbDealers.AllowDrop = true;
         this.lbDealers.FormattingEnabled = true;
         this.lbDealers.ItemHeight = 14;
         this.lbDealers.Location = new System.Drawing.Point(101, 112);
         this.lbDealers.Name = "lbDealers";
         this.lbDealers.Size = new System.Drawing.Size(273, 102);
         this.lbDealers.TabIndex = 24;
         this.lbDealers.DragDrop += new System.Windows.Forms.DragEventHandler(this.lbDealers_DragDrop);
         this.lbDealers.DragEnter += new System.Windows.Forms.DragEventHandler(this.lbDealers_DragEnter);
         // 
         // label9
         // 
         this.label9.AutoSize = true;
         this.label9.Location = new System.Drawing.Point(4, 263);
         this.label9.Name = "label9";
         this.label9.Size = new System.Drawing.Size(127, 14);
         this.label9.TabIndex = 25;
         this.label9.Text = "Средняя проходимость";
         // 
         // tbAgvTraffic
         // 
         this.tbAgvTraffic.Location = new System.Drawing.Point(138, 263);
         this.tbAgvTraffic.Name = "tbAgvTraffic";
         this.tbAgvTraffic.Size = new System.Drawing.Size(304, 20);
         this.tbAgvTraffic.TabIndex = 26;
         // 
         // bntDelDealer
         // 
         this.bntDelDealer.Location = new System.Drawing.Point(382, 152);
         this.bntDelDealer.Name = "bntDelDealer";
         this.bntDelDealer.Size = new System.Drawing.Size(75, 23);
         this.bntDelDealer.TabIndex = 27;
         this.bntDelDealer.Text = ">>>";
         this.bntDelDealer.UseVisualStyleBackColor = true;
         this.bntDelDealer.Click += new System.EventHandler(this.bntDelDealer_Click);
         // 
         // tbEmail
         // 
         this.tbEmail.Location = new System.Drawing.Point(107, 294);
         this.tbEmail.Name = "tbEmail";
         this.tbEmail.Size = new System.Drawing.Size(240, 20);
         this.tbEmail.TabIndex = 30;
         // 
         // label11
         // 
         this.label11.AutoSize = true;
         this.label11.Location = new System.Drawing.Point(6, 297);
         this.label11.Name = "label11";
         this.label11.Size = new System.Drawing.Size(31, 14);
         this.label11.TabIndex = 31;
         this.label11.Text = "email";
         // 
         // FmOrgEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 14F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(626, 437);
         this.Controls.Add(this.label11);
         this.Controls.Add(this.tbEmail);
         this.Controls.Add(this.bntDelDealer);
         this.Controls.Add(this.tbAgvTraffic);
         this.Controls.Add(this.label9);
         this.Controls.Add(this.lbDealers);
         this.Controls.Add(this.btnCancel);
         this.Controls.Add(this.btnOK);
         this.Controls.Add(this.tbContactPhone);
         this.Controls.Add(this.label8);
         this.Controls.Add(this.tbCheifPhone);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.tbContact);
         this.Controls.Add(this.label7);
         this.Controls.Add(this.tbCheif);
         this.Controls.Add(this.label6);
         this.Controls.Add(this.cbLicense);
         this.Controls.Add(this.btnDealer);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.btnType);
         this.Controls.Add(this.cbType);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.tbAddress);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.tbName);
         this.Controls.Add(this.label1);
         this.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgEdit";
         this.Text = "Карточка организации";
         this.Load += new System.EventHandler(this.FmOrgEdit_Load);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox tbName;
      private System.Windows.Forms.TextBox tbAddress;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.ComboBox cbType;
      private System.Windows.Forms.Button btnType;
      private System.Windows.Forms.Button btnDealer;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.CheckBox cbLicense;
      private System.Windows.Forms.TextBox tbCheif;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.TextBox tbContact;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.TextBox tbCheifPhone;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.TextBox tbContactPhone;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.ListBox lbDealers;
      private System.Windows.Forms.Label label9;
      private System.Windows.Forms.TextBox tbAgvTraffic;
      private System.Windows.Forms.Button bntDelDealer;
      private System.Windows.Forms.TextBox tbEmail;
      private System.Windows.Forms.Label label11;
   }
}
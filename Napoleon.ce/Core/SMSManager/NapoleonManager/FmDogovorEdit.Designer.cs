namespace GRSoft.NapoleonManager
{
   partial class FmDogovorEdit
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
         this.panel1 = new System.Windows.Forms.Panel();
         this.btnOK = new System.Windows.Forms.Button();
         this.btnCancel = new System.Windows.Forms.Button();
         this.panel2 = new System.Windows.Forms.Panel();
         this.cbType = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.dtpEnd = new System.Windows.Forms.DateTimePicker();
         this.label3 = new System.Windows.Forms.Label();
         this.dtpBegin = new System.Windows.Forms.DateTimePicker();
         this.label2 = new System.Windows.Forms.Label();
         this.tbNumber = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.panel1.SuspendLayout();
         this.panel2.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel1
         // 
         this.panel1.Controls.Add(this.btnOK);
         this.panel1.Controls.Add(this.btnCancel);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel1.Location = new System.Drawing.Point(0, 143);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(360, 42);
         this.panel1.TabIndex = 0;
         // 
         // btnOK
         // 
         this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.btnOK.Location = new System.Drawing.Point(277, 8);
         this.btnOK.Name = "btnOK";
         this.btnOK.Size = new System.Drawing.Size(75, 23);
         this.btnOK.TabIndex = 1;
         this.btnOK.Text = "OK";
         this.btnOK.UseVisualStyleBackColor = true;
         this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(185, 9);
         this.btnCancel.Name = "btnCancel";
         this.btnCancel.Size = new System.Drawing.Size(75, 23);
         this.btnCancel.TabIndex = 0;
         this.btnCancel.Text = "Отменить";
         this.btnCancel.UseVisualStyleBackColor = true;
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.cbType);
         this.panel2.Controls.Add(this.label4);
         this.panel2.Controls.Add(this.dtpEnd);
         this.panel2.Controls.Add(this.label3);
         this.panel2.Controls.Add(this.dtpBegin);
         this.panel2.Controls.Add(this.label2);
         this.panel2.Controls.Add(this.tbNumber);
         this.panel2.Controls.Add(this.label1);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel2.Location = new System.Drawing.Point(0, 0);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(360, 143);
         this.panel2.TabIndex = 9;
         // 
         // cbType
         // 
         this.cbType.FormattingEnabled = true;
         this.cbType.Location = new System.Drawing.Point(87, 106);
         this.cbType.Name = "cbType";
         this.cbType.Size = new System.Drawing.Size(200, 21);
         this.cbType.TabIndex = 16;
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(7, 108);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(26, 13);
         this.label4.TabIndex = 15;
         this.label4.Text = "Тип";
         // 
         // dtpEnd
         // 
         this.dtpEnd.Location = new System.Drawing.Point(87, 69);
         this.dtpEnd.Name = "dtpEnd";
         this.dtpEnd.Size = new System.Drawing.Size(200, 20);
         this.dtpEnd.TabIndex = 14;
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(7, 75);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(62, 13);
         this.label3.TabIndex = 13;
         this.label3.Text = "Окончание";
         // 
         // dtpBegin
         // 
         this.dtpBegin.Location = new System.Drawing.Point(87, 37);
         this.dtpBegin.Name = "dtpBegin";
         this.dtpBegin.Size = new System.Drawing.Size(200, 20);
         this.dtpBegin.TabIndex = 12;
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(7, 42);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(44, 13);
         this.label2.TabIndex = 11;
         this.label2.Text = "Начало";
         // 
         // tbNumber
         // 
         this.tbNumber.Location = new System.Drawing.Point(87, 6);
         this.tbNumber.Name = "tbNumber";
         this.tbNumber.Size = new System.Drawing.Size(200, 20);
         this.tbNumber.TabIndex = 10;
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(7, 9);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(41, 13);
         this.label1.TabIndex = 9;
         this.label1.Text = "Номер";
         // 
         // FmDogovorEdit
         // 
         this.AcceptButton = this.btnOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(360, 185);
         this.Controls.Add(this.panel2);
         this.Controls.Add(this.panel1);
         this.Name = "FmDogovorEdit";
         this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
         this.Text = "Договор (редактирование)";
         this.Activated += new System.EventHandler(this.FmDogovorEdit_Activated);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FmDogovorEdit_FormClosing);
         this.panel1.ResumeLayout(false);
         this.panel2.ResumeLayout(false);
         this.panel2.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.Button btnOK;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.ComboBox cbType;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.DateTimePicker dtpEnd;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.DateTimePicker dtpBegin;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.TextBox tbNumber;
      private System.Windows.Forms.Label label1;
   }
}
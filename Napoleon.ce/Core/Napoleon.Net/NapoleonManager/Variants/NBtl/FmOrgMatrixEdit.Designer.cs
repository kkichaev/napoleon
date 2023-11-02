namespace GRSoft.NapoleonManager
{
   partial class FmOrgMatrixEdit
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
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FmOrgMatrixEdit));
         this.panel2 = new System.Windows.Forms.Panel();
         this.btnCancel = new System.Windows.Forms.Button();
         this.bntOK = new System.Windows.Forms.Button();
         this.panel1 = new System.Windows.Forms.Panel();
         this.cbMatrix = new System.Windows.Forms.ComboBox();
         this.cbContract = new System.Windows.Forms.ComboBox();
         this.label4 = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.panel2.SuspendLayout();
         this.panel1.SuspendLayout();
         this.SuspendLayout();
         // 
         // panel2
         // 
         this.panel2.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel2.Controls.Add(this.btnCancel);
         this.panel2.Controls.Add(this.bntOK);
         this.panel2.Dock = System.Windows.Forms.DockStyle.Bottom;
         this.panel2.Location = new System.Drawing.Point(0, 108);
         this.panel2.Name = "panel2";
         this.panel2.Size = new System.Drawing.Size(463, 50);
         this.panel2.TabIndex = 2;
         // 
         // btnCancel
         // 
         this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
         this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
         this.btnCancel.Location = new System.Drawing.Point(381, 13);
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
         this.bntOK.Location = new System.Drawing.Point(300, 13);
         this.bntOK.Name = "bntOK";
         this.bntOK.Size = new System.Drawing.Size(75, 23);
         this.bntOK.TabIndex = 0;
         this.bntOK.Text = "ОК";
         this.bntOK.UseVisualStyleBackColor = true;
         // 
         // panel1
         // 
         this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
         this.panel1.Controls.Add(this.cbMatrix);
         this.panel1.Controls.Add(this.cbContract);
         this.panel1.Controls.Add(this.label4);
         this.panel1.Controls.Add(this.label3);
         this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
         this.panel1.Location = new System.Drawing.Point(0, 0);
         this.panel1.Name = "panel1";
         this.panel1.Size = new System.Drawing.Size(463, 108);
         this.panel1.TabIndex = 3;
         // 
         // cbMatrix
         // 
         this.cbMatrix.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbMatrix.FormattingEnabled = true;
         this.cbMatrix.Location = new System.Drawing.Point(82, 57);
         this.cbMatrix.Name = "cbMatrix";
         this.cbMatrix.Size = new System.Drawing.Size(328, 21);
         this.cbMatrix.TabIndex = 7;
         // 
         // cbContract
         // 
         this.cbContract.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
         this.cbContract.FormattingEnabled = true;
         this.cbContract.Location = new System.Drawing.Point(82, 22);
         this.cbContract.Name = "cbContract";
         this.cbContract.Size = new System.Drawing.Size(328, 21);
         this.cbContract.TabIndex = 6;
         this.cbContract.SelectedIndexChanged += new System.EventHandler(this.cbContract_SelectedIndexChanged);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(23, 25);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(54, 13);
         this.label4.TabIndex = 5;
         this.label4.Text = "Контракт";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(26, 60);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(51, 13);
         this.label3.TabIndex = 4;
         this.label3.Text = "Матрица";
         // 
         // FmOrgMatrixEdit
         // 
         this.AcceptButton = this.bntOK;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.CancelButton = this.btnCancel;
         this.ClientSize = new System.Drawing.Size(463, 158);
         this.Controls.Add(this.panel1);
         this.Controls.Add(this.panel2);
         this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
         this.Name = "FmOrgMatrixEdit";
         this.Text = "Матрица для точки";
         this.panel2.ResumeLayout(false);
         this.panel1.ResumeLayout(false);
         this.panel1.PerformLayout();
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.Panel panel2;
      private System.Windows.Forms.Button btnCancel;
      private System.Windows.Forms.Button bntOK;
      private System.Windows.Forms.Panel panel1;
      private System.Windows.Forms.ComboBox cbMatrix;
      private System.Windows.Forms.ComboBox cbContract;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label3;
   }
}
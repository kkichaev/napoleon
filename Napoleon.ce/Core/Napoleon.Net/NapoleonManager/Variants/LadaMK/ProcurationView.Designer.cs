namespace GRSoft.NapoleonManager
{
   partial class ProcurationView
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.label1 = new System.Windows.Forms.Label();
         this.lblRoute = new System.Windows.Forms.Label();
         this.label2 = new System.Windows.Forms.Label();
         this.lblQty = new System.Windows.Forms.Label();
         this.label3 = new System.Windows.Forms.Label();
         this.lblRemark = new System.Windows.Forms.Label();
         this.label4 = new System.Windows.Forms.Label();
         this.lblFIO = new System.Windows.Forms.Label();
         this.label5 = new System.Windows.Forms.Label();
         this.lblProcurationBy = new System.Windows.Forms.Label();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
         this.label1.Location = new System.Drawing.Point(15, 13);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(53, 14);
         this.label1.TabIndex = 0;
         this.label1.Text = "Маршрут";
         // 
         // lblRoute
         // 
         this.lblRoute.AutoSize = true;
         this.lblRoute.Location = new System.Drawing.Point(121, 13);
         this.lblRoute.Name = "lblRoute";
         this.lblRoute.Size = new System.Drawing.Size(46, 13);
         this.lblRoute.TabIndex = 1;
         this.lblRoute.Text = "lblRoute";
         // 
         // label2
         // 
         this.label2.AutoSize = true;
         this.label2.Location = new System.Drawing.Point(15, 36);
         this.label2.Name = "label2";
         this.label2.Size = new System.Drawing.Size(66, 13);
         this.label2.TabIndex = 2;
         this.label2.Text = "Количество";
         // 
         // lblQty
         // 
         this.lblQty.AutoSize = true;
         this.lblQty.Location = new System.Drawing.Point(121, 35);
         this.lblQty.Name = "lblQty";
         this.lblQty.Size = new System.Drawing.Size(33, 13);
         this.lblQty.TabIndex = 3;
         this.lblQty.Text = "lblQty";
         // 
         // label3
         // 
         this.label3.AutoSize = true;
         this.label3.Location = new System.Drawing.Point(15, 58);
         this.label3.Name = "label3";
         this.label3.Size = new System.Drawing.Size(51, 13);
         this.label3.TabIndex = 4;
         this.label3.Text = "Заметка";
         // 
         // lblRemark
         // 
         this.lblRemark.AutoSize = true;
         this.lblRemark.Location = new System.Drawing.Point(121, 58);
         this.lblRemark.Name = "lblRemark";
         this.lblRemark.Size = new System.Drawing.Size(54, 13);
         this.lblRemark.TabIndex = 5;
         this.lblRemark.Text = "lblRemark";
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(15, 80);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(34, 13);
         this.label4.TabIndex = 6;
         this.label4.Text = "ФИО";
         // 
         // lblFIO
         // 
         this.lblFIO.AutoSize = true;
         this.lblFIO.Location = new System.Drawing.Point(121, 80);
         this.lblFIO.Name = "lblFIO";
         this.lblFIO.Size = new System.Drawing.Size(34, 13);
         this.lblFIO.TabIndex = 7;
         this.lblFIO.Text = "lblFIO";
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(15, 102);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(96, 13);
         this.label5.TabIndex = 8;
         this.label5.Text = "Доверенность на";
         // 
         // lblProcurationBy
         // 
         this.lblProcurationBy.AutoSize = true;
         this.lblProcurationBy.Location = new System.Drawing.Point(121, 102);
         this.lblProcurationBy.Name = "lblProcurationBy";
         this.lblProcurationBy.Size = new System.Drawing.Size(83, 13);
         this.lblProcurationBy.TabIndex = 9;
         this.lblProcurationBy.Text = "lblProcurationBy";
         // 
         // ProcurationView
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.AutoSize = true;
         this.BackColor = System.Drawing.Color.Gainsboro;
         this.Controls.Add(this.lblProcurationBy);
         this.Controls.Add(this.label5);
         this.Controls.Add(this.lblFIO);
         this.Controls.Add(this.label4);
         this.Controls.Add(this.lblRemark);
         this.Controls.Add(this.label3);
         this.Controls.Add(this.lblQty);
         this.Controls.Add(this.label2);
         this.Controls.Add(this.lblRoute);
         this.Controls.Add(this.label1);
         this.Name = "ProcurationView";
         this.Size = new System.Drawing.Size(571, 352);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.Label label5;
      public System.Windows.Forms.Label lblRoute;
      public System.Windows.Forms.Label lblQty;
      public System.Windows.Forms.Label lblRemark;
      public System.Windows.Forms.Label lblFIO;
      public System.Windows.Forms.Label lblProcurationBy;
   }
}

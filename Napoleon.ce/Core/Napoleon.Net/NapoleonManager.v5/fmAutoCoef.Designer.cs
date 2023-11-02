namespace GRSoft.NapoleonManager
{
   partial class FmAutoCoef
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
         this.label1 = new System.Windows.Forms.Label();
         this.value = new System.Windows.Forms.TextBox();
         this.ok = new System.Windows.Forms.Button();
         this.SuspendLayout();
         // 
         // label1
         // 
         this.label1.AutoSize = true;
         this.label1.Location = new System.Drawing.Point(19, 35);
         this.label1.Name = "label1";
         this.label1.Size = new System.Drawing.Size(139, 13);
         this.label1.TabIndex = 0;
         this.label1.Text = "Коэффициент автозаказа";
         // 
         // value
         // 
         this.value.Location = new System.Drawing.Point(173, 32);
         this.value.Name = "value";
         this.value.Size = new System.Drawing.Size(75, 20);
         this.value.TabIndex = 1;
         // 
         // ok
         // 
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(173, 69);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 2;
         this.ok.Text = "ОК";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // fmAutoCoef
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(272, 104);
         this.Controls.Add(this.ok);
         this.Controls.Add(this.value);
         this.Controls.Add(this.label1);
         this.Name = "fmAutoCoef";
         this.Text = "Введите значение";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.TextBox value;
      private System.Windows.Forms.Button ok;
   }
}
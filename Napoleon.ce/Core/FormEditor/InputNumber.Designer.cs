namespace NFormEditor
{
   partial class InputNumber
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
         this.ok = new System.Windows.Forms.Button();
         this.value = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // ok
         // 
         this.ok.DialogResult = System.Windows.Forms.DialogResult.OK;
         this.ok.Location = new System.Drawing.Point(110, 53);
         this.ok.Name = "ok";
         this.ok.Size = new System.Drawing.Size(75, 23);
         this.ok.TabIndex = 0;
         this.ok.Text = "OK";
         this.ok.UseVisualStyleBackColor = true;
         // 
         // value
         // 
         this.value.Location = new System.Drawing.Point(23, 13);
         this.value.Name = "value";
         this.value.Size = new System.Drawing.Size(162, 20);
         this.value.TabIndex = 1;
         // 
         // InputNumber
         // 
         this.AcceptButton = this.ok;
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.ClientSize = new System.Drawing.Size(207, 88);
         this.Controls.Add(this.value);
         this.Controls.Add(this.ok);
         this.Name = "InputNumber";
         this.Text = "Введите число";
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.Button ok;
      private System.Windows.Forms.TextBox value;
   }
}
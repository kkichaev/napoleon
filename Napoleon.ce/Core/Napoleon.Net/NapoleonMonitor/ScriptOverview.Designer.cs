namespace GRSoft.NapoleonManager
{
   partial class ScriptOverview
   {
      /// <summary> 
      /// Требуется переменная конструктора.
      /// </summary>
      private System.ComponentModel.IContainer components = null;

      /// <summary> 
      /// Освободить все используемые ресурсы.
      /// </summary>
      /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
      protected override void Dispose(bool disposing)
      {
         if (disposing && (components != null))
         {
            components.Dispose();
         }
         base.Dispose(disposing);
      }

      #region Код, автоматически созданный конструктором компонентов

      /// <summary> 
      /// Обязательный метод для поддержки конструктора - не изменяйте 
      /// содержимое данного метода при помощи редактора кода.
      /// </summary>
      private void InitializeComponent()
      {
         this.text = new System.Windows.Forms.TextBox();
         this.SuspendLayout();
         // 
         // text
         // 
         this.text.Dock = System.Windows.Forms.DockStyle.Fill;
         this.text.Location = new System.Drawing.Point(0, 0);
         this.text.Multiline = true;
         this.text.Name = "text";
         this.text.Size = new System.Drawing.Size(171, 112);
         this.text.TabIndex = 0;
         // 
         // ScriptOverview
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.text);
         this.Name = "ScriptOverview";
         this.Size = new System.Drawing.Size(171, 112);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      #endregion

      private System.Windows.Forms.TextBox text;
   }
}

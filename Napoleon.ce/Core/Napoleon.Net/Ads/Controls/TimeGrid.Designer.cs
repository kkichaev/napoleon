using System;
namespace GRSoft.NapoleonManager
{
   partial class TimeGrid
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
         this.components = new System.ComponentModel.Container();
         this.timeLine = new GRSoft.NapoleonManager.TimeLine();
         this.toolTip = new System.Windows.Forms.ToolTip(this.components);
         this.SuspendLayout();
         // 
         // timeLine
         // 
         this.timeLine.BackColor = System.Drawing.SystemColors.GradientInactiveCaption;
         this.timeLine.Dock = System.Windows.Forms.DockStyle.Left;
         this.timeLine.ForeColor = System.Drawing.SystemColors.HotTrack;
         this.timeLine.Finish = 24;
         this.timeLine.Start = 0;
         this.timeLine.Location = new System.Drawing.Point(0, 0);
         this.timeLine.Name = "timeLine";
         this.timeLine.Size = new System.Drawing.Size(36, 500);
         this.timeLine.TabIndex = 1;
         this.timeLine.TabStop = false;
         // 
         // TimeGrid
         // 
         this.BackColor = System.Drawing.Color.White;
         this.Controls.Add(this.timeLine);
         this.Name = "TimeGrid";
         this.Size = new System.Drawing.Size(620, 500);
         this.toolTip.SetToolTip(this, "show");
         this.ResumeLayout(false);

      }

      #endregion

      private TimeLine timeLine;
      private HeaderControl header;
      private System.Windows.Forms.ToolTip toolTip;
   }
}

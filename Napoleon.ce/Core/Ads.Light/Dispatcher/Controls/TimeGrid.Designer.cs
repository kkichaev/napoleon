namespace GRSoft.Ads.Dispatcher
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
         this.timeLine = new GRSoft.Ads.Dispatcher.TimeLine();
         this.header = new GRSoft.Ads.Dispatcher.HeaderControl();
         this.toolTip = new System.Windows.Forms.ToolTip(this.components);
         this.SuspendLayout();
         // 
         // timeLine
         // 
         this.timeLine.BackColor = System.Drawing.SystemColors.GradientInactiveCaption;
         this.timeLine.Dock = System.Windows.Forms.DockStyle.Left;
         this.timeLine.ForeColor = System.Drawing.SystemColors.HotTrack;
         this.timeLine.HourEnd = ((short)(19));
         this.timeLine.HourStart = ((short)(7));
         this.timeLine.Location = new System.Drawing.Point(0, 23);
         this.timeLine.MinimumSize = new System.Drawing.Size(36, 420);
         this.timeLine.Name = "timeLine";
         this.timeLine.Size = new System.Drawing.Size(36, 420);
         this.timeLine.Step = 30;
         this.timeLine.TabIndex = 1;
         this.timeLine.TabStop = false;
         // 
         // header
         // 
         this.header.Dock = System.Windows.Forms.DockStyle.Top;
         this.header.Location = new System.Drawing.Point(0, 0);
         this.header.Name = "header";
         this.header.Size = new System.Drawing.Size(264, 23);
         this.header.TabIndex = 0;
         this.header.TabStop = false;
         this.header.Text = "headerControl1";
         // 
         // TimeGrid
         // 
         this.BackColor = System.Drawing.Color.White;
         this.Controls.Add(this.timeLine);
         this.Controls.Add(this.header);
         this.Name = "TimeGrid";
         this.Size = new System.Drawing.Size(264, 266);
         this.toolTip.SetToolTip(this, "show");
         this.ResumeLayout(false);

      }

      #endregion

      private TimeLine timeLine;
      private HeaderControl header;
      private System.Windows.Forms.ToolTip toolTip;
   }
}

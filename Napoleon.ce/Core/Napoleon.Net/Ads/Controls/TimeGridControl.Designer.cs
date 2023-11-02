namespace GRSoft.NapoleonManager
{
   partial class TimeGridControl
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
         this.grid = new GRSoft.NapoleonManager.TimeGrid();
         this.header = new GRSoft.NapoleonManager.HeaderControl();
         this.SuspendLayout();
         // 
         // grid
         // 
         this.grid.AutoScroll = true;
         this.grid.AutoScrollMargin = new System.Drawing.Size(10, 10);
         this.grid.AutoScrollMinSize = new System.Drawing.Size(36, 420);
         this.grid.BackColor = System.Drawing.Color.White;
         this.grid.BackLostColor = System.Drawing.Color.DarkGray;
         this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
         this.grid.GridColor = System.Drawing.Color.Black;
         this.grid.Header = this.header;
         this.grid.Finish = 0;
         this.grid.Start = 24;
         this.grid.Location = new System.Drawing.Point(0, 23);
         this.grid.Name = "grid";
         this.grid.Size = new System.Drawing.Size(632, 681);
         this.grid.TabIndex = 1;
         this.grid.ItemContextMenuStrip = null;
         this.grid.ViewProperty = null;
         // 
         // header
         // 
         this.header.Dock = System.Windows.Forms.DockStyle.Top;
         this.header.Location = new System.Drawing.Point(0, 0);
         this.header.Name = "header";
         this.header.Size = new System.Drawing.Size(632, 23);
         this.header.TabIndex = 0;
         this.header.TabStop = false;
         this.header.Text = "headerControl1";
         // 
         // TimeGridControl
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.Controls.Add(this.grid);
         this.Controls.Add(this.header);
         this.Name = "TimeGridControl";
         this.Size = new System.Drawing.Size(632, 704);
         this.ResumeLayout(false);

      }

      #endregion

      private HeaderControl header;
      public TimeGrid grid;
   }
}

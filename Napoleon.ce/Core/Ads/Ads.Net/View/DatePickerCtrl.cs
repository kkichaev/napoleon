using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using System.Drawing.Drawing2D;

namespace GRSoft.Ads
{
   public class DatePickerCtrl : System.Windows.Forms.UserControl
   {
      private System.Windows.Forms.Button btnNextDay;
      private System.Windows.Forms.Button btnPrevDay;
      private System.Windows.Forms.DateTimePicker dtpWorkDay;
      public delegate void DayChangeHandler(DateTime date);
      public event DayChangeHandler OnDayChanged;

      private void InitializeComponent()
      {
         this.btnNextDay = new System.Windows.Forms.Button();
         this.btnPrevDay = new System.Windows.Forms.Button();
         this.dtpWorkDay = new System.Windows.Forms.DateTimePicker();
         this.SuspendLayout();
         // 
         // btnNextDay
         // 
         this.btnNextDay.Location = new System.Drawing.Point(189, 3);
         this.btnNextDay.Name = "btnNextDay";
         this.btnNextDay.Size = new System.Drawing.Size(30, 21);
         this.btnNextDay.TabIndex = 9;
         this.btnNextDay.Text = ">>";
         this.btnNextDay.UseVisualStyleBackColor = true;
         this.btnNextDay.Click += new System.EventHandler(this.btnNextDay_Click);
         // 
         // btnPrevDay
         // 
         this.btnPrevDay.Location = new System.Drawing.Point(0, 3);
         this.btnPrevDay.Name = "btnPrevDay";
         this.btnPrevDay.Size = new System.Drawing.Size(30, 21);
         this.btnPrevDay.TabIndex = 8;
         this.btnPrevDay.Text = "<<";
         this.btnPrevDay.UseVisualStyleBackColor = true;
         this.btnPrevDay.Click += new System.EventHandler(this.btnPrevDay_Click);
         // 
         // dtpWorkDay
         // 
         this.dtpWorkDay.Location = new System.Drawing.Point(37, 3);
         this.dtpWorkDay.Name = "dtpWorkDay";
         this.dtpWorkDay.Size = new System.Drawing.Size(146, 20);
         this.dtpWorkDay.TabIndex = 7;
         this.dtpWorkDay.ValueChanged += new System.EventHandler(this.dtpWorkDay_ValueChanged);
         // 
         // DatePickerCtrl
         // 
         this.BackColor = System.Drawing.SystemColors.Control;
         this.Controls.Add(this.btnNextDay);
         this.Controls.Add(this.btnPrevDay);
         this.Controls.Add(this.dtpWorkDay);
         this.Name = "DatePickerCtrl";
         this.Size = new System.Drawing.Size(220, 27);
         this.ResumeLayout(false);

      }

      public DatePickerCtrl()
      {
         InitializeComponent();
      }

      public DateTime Date
      {
         get
         {
            return dtpWorkDay.Value;
         }
         set 
         {
            dtpWorkDay.Value = value;
         }
      }

      private void btnPrevDay_Click(object sender, EventArgs e)
      {
         dtpWorkDay.Value = dtpWorkDay.Value.AddDays(-1);
      }

      private void btnNextDay_Click(object sender, EventArgs e)
      {
         dtpWorkDay.Value = dtpWorkDay.Value.AddDays(1);
      }

      private void FireDateChanged()
      {
         if (OnDayChanged != null)
            OnDayChanged(dtpWorkDay.Value);
      }

      protected override void OnPaint(PaintEventArgs e)
      {
         base.OnPaint(e);
         Rectangle rect = new Rectangle(0, 0, Width, Height);
         
         LinearGradientBrush brd = new LinearGradientBrush(rect,
                    ProfessionalColors.ToolStripGradientBegin,
                    ProfessionalColors.ToolStripGradientEnd, 
                    LinearGradientMode.Vertical);
         float[] relativeIntensities = { 0.0f, 0.6f, 1.0f };
         float[] relativePositions = { 0.0f, 0.8f, 1.0f };
         Blend blend = new Blend();
         blend.Factors = relativeIntensities;
         blend.Positions = relativePositions;
         brd.Blend = blend;

         e.Graphics.FillRectangle(brd, 0, 0, rect.Width, rect.Height);
      }

      private void dtpWorkDay_ValueChanged(object sender, EventArgs e)
      {
         if (OnDayChanged != null)
            OnDayChanged(dtpWorkDay.Value);
      }
   }
}

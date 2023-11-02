using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.ComponentModel.Design;
using System.Drawing;
using System.ComponentModel;

namespace NFormEditor
{
   class Scaler
   {
      private Control scaledControl;
      private bool scaling = false;

      private Size size;
      private Point location;

      public Scaler(Control control)
      {
         scaledControl = control;
      }

      public void Sited()
      {
         size.Width = (int)(scaledControl.Width / Program.ScaleFactor);
         size.Height = (int)(scaledControl.Height / Program.ScaleFactor);

         location.X = (int)(scaledControl.Left / Program.ScaleFactor);
         location.Y = (int)(scaledControl.Top / Program.ScaleFactor);

         ISite site = scaledControl.Site;
         if (site == null) return;

         IComponentChangeService ccs = (IComponentChangeService)site.GetService(typeof(IComponentChangeService));
         if( ccs != null )
            ccs.ComponentChanged += new ComponentChangedEventHandler(ComponentChanged);
      }

      public void UnSited()
      {
         ISite site = scaledControl.Site;
         if (site == null) return;

         IComponentChangeService ccs = (IComponentChangeService)site.GetService(typeof(IComponentChangeService));
         if (ccs != null)
            ccs.ComponentChanged -= new ComponentChangedEventHandler(ComponentChanged);
      }

      public bool IsScaling
      {
         get { return scaling; }
      }

      void ComponentChanged(object sender, ComponentChangedEventArgs e)
      {
         if (scaling || e.Component != scaledControl) return;

         float scale = Program.ScaleFactor;
         if (scale == 0.0F) return;

         location.X = (int)(scaledControl.Location.X / scale);
         location.Y = (int)(scaledControl.Location.Y / scale);

         size.Width = (int)(scaledControl.Size.Width / scale);
         size.Height = (int)(scaledControl.Size.Height / scale);
      }

      private void SayChanged()
      {
         ISite site = scaledControl.Site;
         if (site == null) return;

         IComponentChangeService ccs = (IComponentChangeService)site.GetService(typeof(IComponentChangeService));
         if (ccs != null)
         {
            ccs.OnComponentChanging(scaledControl, null);
            ccs.OnComponentChanged(scaledControl, null, null, scaledControl.Size);
         }
      }

      public void DoScaling(float factor)
      {
         scaling = true;

         if (Scaling != null)
            Scaling(this, new EventArgs());

         scaledControl.Size = new System.Drawing.Size((int)(size.Width * factor), (int)(size.Height * factor));
         scaledControl.Location = new System.Drawing.Point((int)(location.X * factor), (int)(location.Y * factor));

         if (scaledControl is IFontSize)
         {
            scaledControl.Font = new Font(scaledControl.Font.FontFamily,
               ((IFontSize)scaledControl).FontSize * factor * 300.0F / 96);
         }

         SayChanged();

         scaling = false;
      }

      public Size Size
      {
         get { return size; }
      }

      public Point Location
      {
         get { return location; }
      }

      public event EventHandler Scaling;
   }
}

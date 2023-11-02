using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.ComponentModel.Design;
using System.ComponentModel;
using System.Windows.Forms.Design;
using System.Collections;
using System.Drawing;
using System.IO;

namespace NFormEditor
{
   interface IScalable
   {
      void Scaling(float factor);

      Size RealSize
      {
         get;
      }

      Point RealLocation
      {
         get;
      }
   }

   interface IFontSize
   {
      int FontSize
      {
         get;
         set;
      }
   }

   class PrintForm : Panel
   {
      private bool album = false;
      private Size realSize;
      int leftMargin = 0;

      public PrintForm()
      {
         realSize = new System.Drawing.Size(2400, 3320);

         Size = realSize;

         BackColor = SystemColors.Window;
      }

      public void BeforeLoading()
      {
         leftMargin = 0;
         Controls.Clear();
      }

      public MemoryStream MainForm
      {
         get;
         set;
      }

      public MemoryStream Header
      {
         get;
         set;
      }

      public void Scaling()
      {
         float scale = Program.ScaleFactor;

         System.Drawing.Size oldValue = Size;

         Size = new System.Drawing.Size((int)(realSize.Width * scale), (int)(realSize.Height * scale));
         SaySizeChanged(oldValue);

         foreach (Control child in Controls)
         {
            IScalable scalable = child as IScalable;
            if (scalable != null)
               scalable.Scaling(scale);
         }
      }

      private void SaySizeChanged(Size oldValue)
      {
         if (Site != null)
         {
            IComponentChangeService ccs = (IComponentChangeService)Site.GetService(typeof(IComponentChangeService));
            if (ccs != null)
            {
               PropertyDescriptorCollection pdc = TypeDescriptor.GetProperties(this);
               PropertyDescriptor pd = pdc.Find("Size", false);

               ccs.OnComponentChanging(this, pd);
               ccs.OnComponentChanged(this, pd, oldValue, Size);
            }
         }
      }

      [Category("Свойства")]
      public bool Album
      {
         get { return album; }
         set
         {
            float scale = Program.ScaleFactor;

            album = value;
            System.Drawing.Size oldValue = Size;

            if (album)
               realSize = new System.Drawing.Size(3320, 2400);
            else
               realSize = new System.Drawing.Size(2400, 3320);

            Size = new System.Drawing.Size((int)(realSize.Width * scale), (int)(realSize.Height * scale));

            SaySizeChanged(oldValue);
         }
      }

      static List<string> visibleProps = new List<String>(new String[] { "Album", "LeftMargin" });
      public static bool IsWritable(string propname)
      {
         return visibleProps.Contains(propname) || propname == "Size";
      }

      public static bool CanView(string propname)
      {
         return visibleProps.Contains(propname) || propname == "SecondPage";
      }


      [Category("Свойства")]
      public int LeftMargin { get { return leftMargin; } set { leftMargin = value; } }

      public void FilterProperties(ref System.Collections.IDictionary properties)
      {
         DictionaryEntry[] entiries = new DictionaryEntry[properties.Count];
         properties.CopyTo(entiries, 0);

         foreach (DictionaryEntry de in entiries)
         {
            PropertyDescriptor pd = de.Value as PropertyDescriptor;
            if (pd.IsBrowsable == false || pd.IsReadOnly) continue;

            if (!CanView(pd.Name))
               properties.Remove(de.Key);
         }
      }
   }
}

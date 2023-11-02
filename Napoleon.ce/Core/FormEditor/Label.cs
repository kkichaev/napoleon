using System.Windows.Forms;
using System.Windows.Forms.Design;
using System.Drawing;
using System.ComponentModel;
using System.Windows.Forms.Design.Behavior;
using System.Collections;
using System.ComponentModel.Design;

namespace NFormEditor
{
   [Designer(typeof(LabelDesigner))]
   class Label : System.Windows.Forms.Label, IScalable, IFontSize
   {
      private Scaler scaler;
      private int fontSize = 6;

      public Label()
      {
         scaler = new Scaler(this);

         Font = new Font("Microsoft Sans Serif", fontSize * 300.0F / 96);
         BackColor = Color.Transparent;
         base.TextAlign = ContentAlignment.MiddleCenter;
      }

      [DefaultValue(null)]
      public override ContentAlignment TextAlign
      {
         get
         {
            return base.TextAlign;
         }
         set
         {
            base.TextAlign = value;
         }
      }

      public override ISite Site
      {
         get
         {
            return base.Site;
         }
         set
         {
            if (base.Site != null)
               scaler.UnSited();

            base.Site = value;

            if (value != null)
               scaler.Sited();
         }
      }

      protected override Size DefaultSize
      {
         get
         {
            Size sz = base.DefaultSize;
            sz.Height = 40;
            return sz;
         }
      }

      #region IScalable Members

      public void Scaling(float factor)
      {
         scaler.DoScaling(factor);
      }

      public Size RealSize
      {
         get { return scaler.Size; }
      }

      public Point RealLocation
      {
         get { return scaler.Location; }
      }

      #endregion

      #region IFontSize Members

      public int FontSize
      {
         get { return fontSize; }
         set
         {
            fontSize = value;
            Font = new Font(Font.FontFamily, fontSize * Program.ScaleFactor * 300.0F / 96);
         }
      }

      #endregion
   }

   class LabelDesigner : ControlDesigner
   {
      protected override void  PreFilterProperties(System.Collections.IDictionary properties)
      {
         base.PreFilterProperties(properties);

         DictionaryEntry[] entiries = new DictionaryEntry[properties.Count];
         properties.CopyTo(entiries, 0);

         foreach (DictionaryEntry de in entiries)
         {
            PropertyDescriptor pd = de.Value as PropertyDescriptor;
            if (pd.IsBrowsable == false || pd.IsReadOnly) continue;

            if (pd.Name != "Text" && pd.Name != "Location" && pd.Name != "Size" && pd.Name != "TextAlign" && pd.Name != "FontSize" )
               properties.Remove(de.Key);
         }
     }
   }
}

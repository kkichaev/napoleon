using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class BandItem
   {
      private DateTime start;
      private DateTime finish;
      private DateTime created;
      private object stored;
      private Color color = Color.LightBlue;

      public DateTime Start { get { return start; } set { start = value; } }
      public DateTime Finish { get { return finish; } set { finish = value; } }
      public object Stored { get { return stored; } set { stored = value; } }
      public Color Color { get { return color; } set { color = value; } }
      public DateTime Created { get { return created; } set { created = value; } }
   }
}

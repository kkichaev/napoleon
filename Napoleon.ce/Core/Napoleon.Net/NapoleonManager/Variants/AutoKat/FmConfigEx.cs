using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   internal class FmConfigEx : FmConfig
   {
      public FmConfigEx() {
         cbHighliteOrderMissed.Text = "Выделять документы не загруженные в 1С";
      }
   }
}

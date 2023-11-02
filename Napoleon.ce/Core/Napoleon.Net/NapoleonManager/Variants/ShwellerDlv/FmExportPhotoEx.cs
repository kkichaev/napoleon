using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmExportPhotoEx : FmExportPhoto
   {
      public FmExportPhotoEx()
      {
         dsVisit.Name = DVisit.OBJECT_NAME;
      }
   }
}

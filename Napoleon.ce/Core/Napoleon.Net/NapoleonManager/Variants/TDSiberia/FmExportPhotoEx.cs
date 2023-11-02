using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmExportPhotoEx : FmExportPhoto
   {
      protected override string getPhotoText(BaseDocument doc)
      {
         return base.getPhotoText(doc) + "\n" + doc.OrgName;
      }
   }
}

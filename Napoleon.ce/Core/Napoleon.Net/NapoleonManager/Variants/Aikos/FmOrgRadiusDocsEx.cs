using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
    class FmOrgRadiusDocsEx : FmOrgRadiusDocs
    {
        public override string[] AvailDocs
        {
            get { return new string[] { "Visit" }; }
        }
    }
}

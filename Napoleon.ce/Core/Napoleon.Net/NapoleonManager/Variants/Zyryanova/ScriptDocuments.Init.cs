using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
    partial class ScriptDocuments
    {
        static void InitDocuments()
        {
            docs = new List<ScriptDocument>();
            docs.Add(new OrderDoc());
            docs.Add(new IncassDoc());

            docs.Add(new VisitDoc());
            docs.Add(new MerchDocument());
            docs.Add(new ScanLocationDoc());
        }
    }

    class MerchDocument : ScriptDocument
    {
        public MerchDocument() : base("Merch", "Мерчендайзинг", Resources.remnants_doc)
        {

        }
    }
}

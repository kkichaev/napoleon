using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class Distrib : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DistrDoc";

      public int outFaces = 0;
      public int theirFaces = 0;
   }

   class OrgAssortimentMatrix : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgAssortimentMatrix";

      [KeyField]
      public string category = "";
      [KeyField]
      public string id = "";

      public List<MatrixItem> items = new List<MatrixItem>();
   }

   class OrgCategory : DataObject
   {
      public static readonly string OBJECT_NAME = "Category";
      
      [KeyField]
      public string id = "";
      public string name = "";
   }
}

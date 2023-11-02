using GRSoft.NapoleonManager.Utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmMatrixDesignerEx : FmMatrixDesigner
   {
      protected override string GetPriceName(Price p)
      {
         return "(" + p.id + ") " + p.name;
      }

      //protected override string GetMatrixPriceName(MatrixItem item)
      //{
      //   return GetPriceName(item.price);
      //}

      //protected override void MakePriceTree(System.Windows.Forms.TreeView tv, Network.DataSet<string, ManagerFolder> folders, Network.DataSet<string, Price> price)
      //{
      //   ArticlesTreeConstructor t = new ArticlesTreeConstructor(tv, folders, price);
      //   t.GetPriceName = GetPriceName;
      //   t.MakeArticlesTree(0, 1);
      //}
   }
}

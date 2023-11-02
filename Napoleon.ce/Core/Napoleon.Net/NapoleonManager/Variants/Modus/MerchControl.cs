using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class MerchControl : UserControl, DataObjectViewer
   {
      public MerchControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         if (dataObject != null)
         {
            MerchEnd me = dataObject as MerchEnd;

            if (me != null)
            {
               List<MerchEndItem> data = new List<MerchEndItem>();
               data.AddRange(me.items);
               data.Sort((lhs, rhs) => { return lhs.Item.CompareTo(rhs.Item); });
               grid.DataSource = data;
            }
         }
      }
   }

   public partial class MerchEndItem
   {
      public double Start { get { return start; } }
      public double Finish { get { return finish; } }
      public string Item { get { return item.Name;  } }
   }
}

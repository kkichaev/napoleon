using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DistribOverview : UserControl, DataObjectViewer
   {
      public DistribOverview()
      {
         InitializeComponent();
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {

         OrgDistribution o = dataObject as OrgDistribution;
         DataSet<string, DistributionMatrix> dsDistribMatrix = (DataSet<string, DistributionMatrix>)
            DataModule.Get(DistributionMatrix.OBJECT_NAME);

         if (o != null && dsDistribMatrix != null)
         {
            DistributionMatrix distrMatrix = null;

            if (dsDistribMatrix.ContainsKey(o.id))
               distrMatrix = dsDistribMatrix[o.id];

            List<DistribItemEx> loi = new List<DistribItemEx>();
            List<String> orgDistr = new List<string>();

            foreach (OrgDistribution.DistribItem item in o.items)
               orgDistr.Add(item.id);

            if (distrMatrix != null && distrMatrix.items != null)
               foreach (DistributionMatrix.Item i in distrMatrix.items)
                  loi.Add(new DistribItemEx(i, orgDistr.Contains(i.id)));

            loi.Sort(new Comparison<DistribItemEx>(delegate(DistribItemEx lhs, DistribItemEx rhs)
            {
               int result = 0;
               result = lhs.present.CompareTo(rhs.present) * -1;

               if (result == 0)
                  result = lhs.Item.CompareTo(rhs.Item);

               return result;
            }));

            dgvItems.DataSource = loi;
         }
      }
   }
}

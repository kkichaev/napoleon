using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<Delivery> dsDelivery = new SimpleDataSet<Delivery>(Delivery.OBJECT_NAME, true);
      DataGridViewTextBoxColumn clmnPDZ = new DataGridViewTextBoxColumn();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         clmnPDZ.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnPDZ.DataPropertyName = "PDZ";
         clmnPDZ.HeaderText = "ПДЗ";
         clmnPDZ.Name = "pdz";
         clmnPDZ.DefaultCellStyle.Font = new Font(dgvDetail.Font, FontStyle.Bold);
         dgvDetail.Columns.Add(clmnPDZ);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsDelivery.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsDelivery.Name);
         updSets.Add(dsDelivery);
      }
   }

   public partial class OrderDetailRepresentation : ODRComapartor
   {
      private bool inited = false;
      public double pdz = 0.0;

      public string PDZ 
      {
         get 
         {
            if (!inited)
            {
               if (dataObject is Order)
               {
                  SimpleDataSet<Delivery> dsDelivery = (SimpleDataSet<Delivery>)DataModule.Get(Delivery.OBJECT_NAME);
                  DateTime now = DateTime.Now;
                  Order order = dataObject as Order;

                  if (order != null)
                  {
                     foreach (Delivery d in dsDelivery.Values)
                     {
                        if (d.id.Equals(order.id))
                           if (d.payDate < now)
                              pdz += d.sumD;
                     }
                  }
               }
            }

            if (!inited)
               inited = true;

            return pdz > 0 ? pdz.ToString("C", Config.GetCultureInfo()) : string.Empty; 
         } 
      }
   }
}

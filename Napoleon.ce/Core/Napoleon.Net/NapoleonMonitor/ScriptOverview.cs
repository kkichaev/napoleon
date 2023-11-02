using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.Globalization;

namespace GRSoft.NapoleonManager
{
   public partial class ScriptOverview : UserControl, DataObjectViewer
   {
      public ScriptOverview()
      {
         InitializeComponent();
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
#if SCRIPT_DOC
         ScriptDoc sd = dataObject as ScriptDoc;
         if( sd != null )
         {
            StringBuilder sb = new StringBuilder();
            TimeSpan ts = new TimeSpan(sd.End.Ticks);
            ts -= new TimeSpan(sd.Start.Ticks);

            sb.AppendFormat("Время визита:\t{0} - {1} ({2} мин)",
               sd.Start.ToShortTimeString(),
               sd.End.ToShortTimeString(),
               (int)ts.TotalMinutes);

            List<GRSoft.Network.DataObject> visits = sd.GetDocumentsOfType(Visit.OBJECT_NAME);
            sb.AppendLine();
            for (int i = 0, j = 0; i < visits.Count; ++i)
            {
                Visit vzt = visits[i] as Visit;
                if (vzt != null)
                {
                    sb.AppendLine();
                    sb.AppendFormat("Примечание {0}:\t{1}", ++j, vzt.remark);
                }
            }

            List<GRSoft.Network.DataObject> incasses = sd.GetDocumentsOfType(Incass.OBJECT_NAME);
            sb.AppendLine();
            for (int i = 0, j = 0; i < incasses.Count; ++i)
            {
                Incass id = incasses[i] as Incass;
                if (id != null)
                {
                    //sb.AppendLine();
                    sb.AppendFormat("Инкассация {0}:\t{1}", ++j, id.sum.ToString("C", Config.GetCultureInfo()));
                }
            }

            List<GRSoft.Network.DataObject> orders = sd.GetDocumentsOfType(Order.OBJECT_NAME);
            sb.AppendLine();
            for (int i = 0, j = 0; i < orders.Count; ++i)
            {
                Order o = orders[i] as Order;
                if (o != null)
                {
                    //sb.AppendLine();
                    sb.AppendFormat("Заявка {0}:\t{1}", ++j, ((o == null) ? 0.0 : o.Sum()).ToString("C", Config.GetCultureInfo()));
                    if (o != null && o.remark.Length > 0)
                        sb.AppendFormat(" ({0})", o.remark);
                    sb.AppendLine();
                }
            }

            text.Text = sb.ToString();
         }
#endif
      }

   }
}
